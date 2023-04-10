package com.quick.quickbus.listener;

import com.quick.quickbus.model.BusStop;

import java.util.List;


public interface NearSearchResultListener {
    void onDataReceived(List<BusStop> data);
}
