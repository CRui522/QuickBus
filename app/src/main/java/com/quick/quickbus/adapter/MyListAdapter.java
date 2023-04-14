package com.quick.quickbus.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.quick.quickbus.model.BusStop;

import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private int lastHeight;
    private boolean isExpanded;
    private List<BusStop> myDataList;
    private OnItemClickListener listener;

    public MyListAdapter(List<BusStop> myDataList) {
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        for (String subItemText : myData.getSubItems()) {
            TextView subItemTextView = new TextView(subItemList.getContext());
            subItemTextView.setText(subItemText);
            subItemList.addView(subItemTextView);
        }

        ImageView expandCollapseIcon = viewHolder.expandCollapseIcon;
        Drawable icon;
        isExpanded = myData.isExpanded();
        icon = ContextCompat.getDrawable(expandCollapseIcon.getContext(), isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
        subItemList.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        addressTextView.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
        expandCollapseIcon.setImageDrawable(icon);

        expandCollapseIcon.setOnClickListener(view -> {
            isExpanded = !myData.isExpanded();
            myData.setExpanded(isExpanded);
            lastHeight = getHeight(viewHolder.itemView);
            Log.i("lastHeight", String.valueOf(lastHeight));
            notifyItemChanged(position);

            // 最后一个item滑动
            if (listener != null && isExpanded && position == getItemCount() - 1) {
                viewHolder.itemView.post(() -> {
                    listener.onItemClick(lastHeight / 2);
                });
            }
        });
    }


    public int getHeight(View itemView) {
        itemView.measure(
                View.MeasureSpec.makeMeasureSpec(itemView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return itemView.getMeasuredHeight();
    }

    @Override
    public int getItemCount() {
        if (myDataList == null) {
            return 0;
        }
        return myDataList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<BusStop> stationList) {
        //if (myDataList == null) return;
        myDataList = stationList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView distance;
        public TextView addressTextView;
        public ImageView expandCollapseIcon;
        public LinearLayout subItemList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.tv_station_name);
            distance = itemView.findViewById(R.id.tv_distance);
            addressTextView = itemView.findViewById(R.id.parent_name_text_view);
            expandCollapseIcon = itemView.findViewById(R.id.parent_arrow_image_view);
            subItemList = itemView.findViewById(R.id.sub_item_list);
        }
    }
}
