/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.quick.quickbus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.quick.quickbus.R;

import java.util.List;

public class RouteLineDetailAdapter extends BaseAdapter {

    private final List<? extends RouteStep> mRouteSteps;
    private final LayoutInflater mLayoutInflater;
    private final int mType;

    public RouteLineDetailAdapter(Context context, List<? extends RouteStep> routeSteps, int type) {
        this.mRouteSteps = routeSteps;
        mLayoutInflater = LayoutInflater.from(context);
        mType = type;
    }

    @Override
    public int getCount() {
        return mRouteSteps.size();
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
        if (mRouteSteps == null) {
            return null;
        }
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.routeplan_detail_item, null);
            holder = new ViewHolder();
            holder.iv_tips = convertView.findViewById(R.id.route_detail_iv_tips);
            holder.tv_desc = convertView.findViewById(R.id.route_detail_tv_desc);
            holder.devide_line = convertView.findViewById(R.id.devide_line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            holder.iv_tips.setImageResource(R.drawable.route_detail_start);
            holder.tv_desc.setText("我的位置");
            holder.devide_line.setVisibility(View.VISIBLE);
            return convertView;
        }

        if (position == mRouteSteps.size() - 1) {
            holder.iv_tips.setImageResource(R.drawable.route_detail_end);
            holder.tv_desc.setText("终点");
            holder.devide_line.setVisibility(View.GONE);
            return convertView;
        } else {
            holder.devide_line.setVisibility(View.VISIBLE);
        }

        // 公交
        updateTransitItem(position, holder);

        return convertView;
    }

    private void updateTransitItem(int position, ViewHolder holder) {
        if (mRouteSteps == null || position >= mRouteSteps.size()) {
            return;
        }

        TransitRouteLine.TransitStep.TransitRouteStepType type =
                ((TransitRouteLine.TransitStep) mRouteSteps.get(position)).getStepType();
        switch (type) {
            case BUSLINE:
                holder.iv_tips.setImageResource(R.drawable.icon_busline);
                break;
            default:
                break;
        }
        holder.tv_desc.setText(
                ((TransitRouteLine.TransitStep) mRouteSteps.get(position)).getInstructions());

    }

    private static class ViewHolder {
        private ImageView iv_tips;
        private TextView tv_desc;
        private View devide_line;
    }

}
