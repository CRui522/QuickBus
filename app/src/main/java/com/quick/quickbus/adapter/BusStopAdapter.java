package com.quick.quickbus.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quick.quickbus.R;
import com.quick.quickbus.model.BusStop;

import java.util.List;

public class BusStopAdapter extends RecyclerView.Adapter<BusStopAdapter.BusStopViewHolder> {

    private List<BusStop> busStopList;

    public BusStopAdapter(List<BusStop> busStopList) {
        this.busStopList = busStopList;
    }

    @NonNull
    @Override
    public BusStopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bus_stop, parent, false);
        return new BusStopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusStopViewHolder holder, int position) {
        BusStop busStop = busStopList.get(position);
        holder.nameTextView.setText(busStop.getName());
        holder.distanceTextView.setText(String.valueOf(busStop.getDistance()) + "m");
        holder.busRoutesTextView.setText(TextUtils.join(", ", busStop.getBusRoutes()));
    }

    @Override
    public int getItemCount() {
        return busStopList.size();
    }

    public static class BusStopViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView distanceTextView;
        public TextView busRoutesTextView;

        public BusStopViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_station_name);
            distanceTextView = itemView.findViewById(R.id.tv_distance);

        }
    }
}
