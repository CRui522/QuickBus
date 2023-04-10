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
import androidx.recyclerview.widget.RecyclerView;

import com.quick.quickbus.R;
import com.quick.quickbus.model.BusStop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private List<BusStop> myDataList;

    public MyListAdapter(List<BusStop> myDataList) {
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View myDataView = inflater.inflate(R.layout.item_bus_stop, parent, false);

        ViewHolder viewHolder = new ViewHolder(myDataView);
        return viewHolder;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        BusStop myData = myDataList.get(position);

        TextView nameTextView = viewHolder.nameTextView;
        nameTextView.setText(myData.getName());

        TextView addressTextView = viewHolder.addressTextView;
        addressTextView.setText(myData.getAddress());

        LinearLayout subItemList = viewHolder.subItemList;
        subItemList.removeAllViews();
        for (String subItemText : myData.getSubItems()) {
            TextView subItemTextView = new TextView(subItemList.getContext());
            subItemTextView.setText(subItemText);
            subItemList.addView(subItemTextView);
        }

        ImageView expandCollapseIcon = viewHolder.expandCollapseIcon;
        Drawable icon = null;
        if (myData.isExpanded()) {
            icon = expandCollapseIcon.getContext().getDrawable(R.drawable.ic_arrow_up);
            subItemList.setVisibility(View.VISIBLE);
        } else {
            icon = expandCollapseIcon.getContext().getDrawable(R.drawable.ic_arrow_down);
            subItemList.setVisibility(View.GONE);
        }
        expandCollapseIcon.setImageDrawable(icon);

        expandCollapseIcon.setOnClickListener(view -> {
            boolean isExpanded = myData.isExpanded();
            myData.setExpanded(!isExpanded);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<BusStop> stationList) {
        Log.i("MyList", String.valueOf(stationList));
        myDataList = stationList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView addressTextView;
        public ImageView expandCollapseIcon;
        public LinearLayout subItemList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.tv_station_name);
            addressTextView = (TextView) itemView.findViewById(R.id.parent_name_text_view);
            expandCollapseIcon = (ImageView) itemView.findViewById(R.id.parent_arrow_image_view);
            subItemList = (LinearLayout) itemView.findViewById(R.id.sub_item_list);
        }
    }
}
