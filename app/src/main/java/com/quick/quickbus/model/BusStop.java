package com.quick.quickbus.model;

import java.util.List;

public class BusStop {
    private String name;
    private String uid;
    private int distance;
    private String address;
    private List<String> busRoutes;
    private boolean isExpanded;

    public BusStop(String name, String uid, int distance, String address) {
        this.name = name;
        this.uid = uid;
        this.distance = distance;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public int getDistance() {
        return distance;
    }

    public List<String> getBusRoutes() {
        return busRoutes;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setBusRoutes(List<String> busRoutes) {
        this.busRoutes = busRoutes;
    }

    public String[] getSubItems() {
        return address.split(";");
    }
}

