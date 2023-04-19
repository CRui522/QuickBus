package com.quick.quickbus.model;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.core.PoiInfo;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusLine {

    private final LatLng latLng;
    private final String city;
    private final String uid;
    private String name;
    private String line;
    private Date startTime;
    private Date endTime;
    private List<BusLineResult.BusStation> busStationList;
    private List<BusLineResult.BusStep> busStepList;

    public BusLine(PoiInfo poi) {

        // 定义正则表达式
        Pattern pattern = Pattern.compile("^(.*?)\\((.*?)\\)$");
        // 匹配字符串
        Matcher matcher = pattern.matcher(poi.name);

        if (matcher.matches()) {
            // 获取匹配到的信息
            String busNumber = matcher.group(1);
            String bracketsContent = matcher.group(2);
            this.name = busNumber;
            this.line = bracketsContent;
        }
        this.latLng = poi.location;
        this.city = poi.city;
        this.uid = poi.uid;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getName() {
        return name;
    }

    public String getLine() {
        return line;
    }

    public String getCity() {
        return city;
    }

    public String getUid() {
        return uid;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setBusStationList(List<BusLineResult.BusStation> busStationList) {
        this.busStationList = busStationList;
    }

    public void setBusStepList(List<BusLineResult.BusStep> busStepList) {
        this.busStepList = busStepList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusLine busLine = (BusLine) o;
        return Objects.equals(uid, busLine.uid);
    }

}
