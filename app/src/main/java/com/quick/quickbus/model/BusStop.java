package com.quick.quickbus.model;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.List;

public class BusStop {
    private final String name;
    private final LatLng latLng;
    private final String uid;
    private int distance;
    private final String address;
    private final String city;
    private final List<BusLine> busLines = new ArrayList<>();
    private boolean isExpanded;

    public BusStop(PoiInfo poi) {
        // 获取公交站点信息
        this.name = poi.name; // 公交站点名称
        this.uid = poi.uid;
        this.address = poi.address; // 所有线路名称
        this.latLng = poi.location; // 公交站点位置
        this.city = poi.city;
    }

    // 计算地球上两点之间的距离，单位为千米
    public void setDistance(LatLng myLocation) {
        distance = (int) DistanceUtil.getDistance(myLocation, latLng);
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public List<BusLine> getBusLines() {
        return busLines;
    }

    public void setBusLines(BusLine busLines) {
        this.busLines.add(busLines);
    }

    public int getDistance() {
        return distance;
    }

    public String getAddress() {
        return address;
    }

    public void setExpanded(boolean expanded) {
        this.isExpanded = expanded;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public String getUid() {
        return uid;
    }

    public String[] getLineNames() {
        return address.split(";");
    }
}

