package com.quick.quickbus.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.quick.quickbus.R;
import com.quick.quickbus.model.BusLine;
import com.quick.quickbus.model.BusStop;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private boolean isExpanded;
    private List<BusStop> myDataList;
    private OnItemClickListener listener;
    private final Context mContext;
    RecyclerView recyclerView;

    public MyListAdapter(Context mContext, List<BusStop> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        recyclerView = (RecyclerView) parent;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View myDataView = inflater.inflate(R.layout.item_bus_stop, parent, false);
        return new ViewHolder(myDataView);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        BusStop myData = myDataList.get(position);

        TextView nameTextView = viewHolder.nameTextView;
        nameTextView.setText(myData.getName());
        TextView distance = viewHolder.distance;
        distance.setText(myData.getDistance() + "米");
        TextView addressTextView = viewHolder.addressTextView;
        addressTextView.setText(myData.getAddress());

        //子列表
        LinearLayout subItemList = viewHolder.subItemList;
        subItemList.removeAllViews();
        for (BusLine busLine : myData.getBusLines()) {
            // 动态添加子布局
            View subView = LayoutInflater.from(mContext).inflate(R.layout.item_bus_line, subItemList, false);
            TextView lineName = subView.findViewById(R.id.line_name);
            TextView lineTextView = subView.findViewById(R.id.line);
            TextView lineStartTime = subView.findViewById(R.id.line_start_time);
            TextView lineEndTime = subView.findViewById(R.id.line_end_time);

            lineName.setText(busLine.getName());
            lineTextView.setText(busLine.getLine());
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
            String startTime = format.format(busLine.getStartTime());
            String endTime = format.format(busLine.getEndTime());
            lineStartTime.setText("发车：" + startTime);
            lineEndTime.setText("结束：" + endTime);
            subItemList.addView(subView);
        }

        LinearLayout routesView = viewHolder.routesView;
        ImageView expandCollapseIcon = viewHolder.expandCollapseIcon;
        TextView parentArrowTextView = viewHolder.parentArrowTextView;

        Drawable icon;
        isExpanded = myData.isExpanded();
        icon = ContextCompat.getDrawable(expandCollapseIcon.getContext(), isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
        subItemList.setVisibility(isExpanded ? View.VISIBLE : View.GONE);   //展开后的子布局
        addressTextView.setVisibility(isExpanded ? View.GONE : View.VISIBLE);   //收起状态下的路线描述
        expandCollapseIcon.setImageDrawable(icon);
        parentArrowTextView.setText(isExpanded ? "收起" : "展开");

        viewHolder.busStation.setOnClickListener(view -> listener.onItemClick(position));

        routesView.setOnClickListener(view -> {
            isExpanded = !myData.isExpanded();
            myData.setExpanded(isExpanded);
            if (isExpanded) listener.onExpend(position);
            int lastHeight = getHeight(viewHolder.itemView);    //获取展开前的item布局高度
            notifyItemChanged(position);
            // 最后一个item滑动
            scroll(position, lastHeight);
        });
    }

    public void scroll(int position, int lastHeight) {
        if (listener != null && isExpanded && position == getItemCount() - 1) {
            recyclerView.post(() -> {
                // 滑动到展开的 item
                recyclerView.smoothScrollBy(0, lastHeight / 2);
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<BusStop> stationList) {
        myDataList = stationList;
        notifyDataSetChanged();
    }

    public void updateItem(int position, BusLine lines) {
        BusStop busStop = myDataList.get(position);
        if (!busStop.getBusLines().contains(lines)) {
            busStop.setBusLines(lines);
        }
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        if (myDataList == null) {
            return 0;
        }
        return myDataList.size();
    }

    public int getHeight(View itemView) {
        itemView.measure(
                View.MeasureSpec.makeMeasureSpec(itemView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return itemView.getMeasuredHeight();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onExpend(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View busStation;
        public TextView nameTextView;
        public TextView distance;
        public TextView addressTextView;
        public ImageView expandCollapseIcon;
        public LinearLayout subItemList;
        public LinearLayout routesView;
        public TextView parentArrowTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            busStation = itemView.findViewById(R.id.bus_station);
            routesView = itemView.findViewById(R.id.routes_view);
            parentArrowTextView = itemView.findViewById(R.id.parent_arrow_text_view);
            nameTextView = itemView.findViewById(R.id.tv_station_name);
            distance = itemView.findViewById(R.id.tv_distance);
            addressTextView = itemView.findViewById(R.id.parent_name_text_view);
            expandCollapseIcon = itemView.findViewById(R.id.parent_arrow_image_view);
            subItemList = itemView.findViewById(R.id.sub_item_list);
        }
    }
}
