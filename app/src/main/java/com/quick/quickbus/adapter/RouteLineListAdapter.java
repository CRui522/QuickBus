/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.quick.quickbus.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.core.VehicleInfo;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.quick.quickbus.R;
import com.quick.quickbus.ui.TagFlowLayout;
import com.quick.quickbus.ui.TagInfo;

import java.util.ArrayList;
import java.util.List;

public class RouteLineListAdapter extends BaseAdapter {

    private final List<? extends RouteLine> mRouteLines;
    private final LayoutInflater mLayoutInflater;
    private final Type mType;
    private final Context mContext;

    public RouteLineListAdapter(Context context, List<? extends RouteLine> routeLines, Type type) {
        this.mRouteLines = routeLines;
        mLayoutInflater = LayoutInflater.from(context);
        mType = type;
        mContext = context;
    }

    @Override
    public int getCount() {
        if (mRouteLines == null) {
            return 0;
        }
        return mRouteLines.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mRouteLines == null) {
            return null;
        }
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.transit_route_item, null);
            holder = new ViewHolder();
            holder.time = convertView.findViewById(R.id.total_time);
            holder.distance = convertView.findViewById(R.id.total_distance);
            holder.itemTagFlow = convertView.findViewById(R.id.step_flow);
            holder.itemLineStopsCount =
                    convertView.findViewById(R.id.itemLineStopsCount);
            holder.itemStartNodeName = convertView.findViewById(R.id.itemStartNodeName);
            holder.itemDivide = convertView.findViewById(R.id.divider_line_stop);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (mType) {
            case TRANSIT_ROUTE:
                updateETAInfo((TransitRouteLine) (mRouteLines.get(position)), holder);
                updateFlowStepName((TransitRouteLine) (mRouteLines.get(position)), holder);
                updateStepInfo((TransitRouteLine) (mRouteLines.get(position)), holder);
                break;
            default:
                break;
        }

        return convertView;
    }

    private void updateETAInfo(TransitRouteLine routeLine, ViewHolder holder) {
        int time = routeLine.getDuration();
        if (time / 3600 == 0) {
            holder.time.setText(time / 60 + "分钟");
        } else {
            holder.time.setText(time / 3600 + "小时" + (time % 3600) / 60 + "分");
        }

        int distance = routeLine.getDistance();
        if (distance / 1000 == 0) {
            holder.distance.setText(distance + "米");
        } else {
            String dis = String.format("%.1f", distance / 1000f);
            holder.distance.setText(dis + "公里");
        }
    }

    private void updateFlowStepName(TransitRouteLine routeLine, ViewHolder holder) {
        if (mContext == null || routeLine == null) {
            return;
        }
        List<TagInfo> tagInfos = new ArrayList<>();
        List<TransitRouteLine.TransitStep> transitSteps = routeLine.getAllStep();
        for (TransitRouteLine.TransitStep transitStep : transitSteps) {
            TagInfo tagInfo = new TagInfo();
            tagInfo.setTagType(transitStep.getStepType().ordinal());
            VehicleInfo vehicleInfo = transitStep.getVehicleInfo();
            // 只有公交的step才有VehicleInfo的值
            if (vehicleInfo != null) {
                tagInfo.setTagDesc(vehicleInfo.getTitle());
                tagInfos.add(tagInfo);
            }

        }
        holder.itemTagFlow.setAdapter(new TransitListItemFlowLayoutAdapter(mContext, tagInfos));
    }

    private void updateStepInfo(TransitRouteLine routeLine, ViewHolder holder) {
        if (mContext == null || routeLine == null) {
            return;
        }
        int stopsNum = 0;
        String startName = "";
        List<TransitRouteLine.TransitStep> transitSteps = routeLine.getAllStep();
        for (int i = 0; i < transitSteps.size(); ++i) {
            VehicleInfo vehicleInfo = transitSteps.get(i).getVehicleInfo();
            if (vehicleInfo != null) {
                stopsNum += transitSteps.get(i).getVehicleInfo().getPassStationNum();
                // 只取第一个有效的公交站点信息
                if (TextUtils.isEmpty(startName)) {
                    RouteNode startNode = transitSteps.get(i).getEntrance();
                    if (startNode != null) {
                        startName = startNode.getTitle();
                    }
                }
            }
        }
        holder.itemLineStopsCount.setText(stopsNum + "站");
        if (!TextUtils.isEmpty(startName)) {
            holder.itemDivide.setVisibility(View.VISIBLE);
            holder.itemStartNodeName.setText(startName + "上车");
        } else {
            holder.itemDivide.setVisibility(View.GONE);
        }

    }

    public enum Type {
        TRANSIT_ROUTE, // 公交
        WALKING_ROUTE, // 步行
        BIKING_ROUTE // 骑行

    }

    private static class ViewHolder {
        // left
        private TextView time;
        private TextView distance;

        // right
        private TagFlowLayout itemTagFlow;
        private TextView itemLineStopsCount;
        private TextView itemStartNodeName;
        private View itemDivide;
    }

}
