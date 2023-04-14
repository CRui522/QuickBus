package com.quick.quickbus.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.quick.quickbus.adapter.OnGetPoiSearchResultAdapter;
import com.quick.quickbus.listener.NearSearchResultListener;
import com.quick.quickbus.model.BusStop;

import java.util.ArrayList;
import java.util.List;

public class NearbyBusStations {
    private final Context mContext;
    private PoiSearch mPoiSearch;
    private final LatLng mLocation;
    private final List<BusLineResult.BusStation> mStations = new ArrayList<>();

    public NearbyBusStations(Context context, LatLng location) {
        this.mContext = context;
        this.mLocation = location;
    }

    public void search(NearSearchResultListener listener, String keyWord) {
        // 设置搜索监听器
        // 创建 PoiSearch 对象
        mPoiSearch = PoiSearch.newInstance();

        // 设置搜索监听器
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultAdapter() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult == null || poiResult.error != PoiResult.ERRORNO.NO_ERROR) {
                    // 搜索失败
                    Toast.makeText(mContext, "搜索失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 获取搜索结果列表
                List<PoiInfo> pois = poiResult.getAllPoi();
                List<BusStop> busList = new ArrayList<>();
                StringBuilder uids = new StringBuilder();

                for (PoiInfo poi : pois) {
                    // 获取公交站点信息
                    String name = poi.name; // 公交站点名称
                    String uid = poi.uid;
                    String address = poi.address; // 所有线路名称
                    LatLng location = poi.location; // 公交站点位置
                    double lat = location.latitude;
                    double lng = location.longitude;
                    int distance = calculateDistance(lat, lng);
                    BusStop busStop = new BusStop(name, uid, distance, address);
                    busList.add(busStop);
                    uids.append(",").append(uid);
                    // 输出公交站点信息
                    Log.d("NearbyBusStations", "name: " + name + ", uid: " + uid + ", address: " + address + ", distance: " + distance + "m");
                }

                listener.onDataReceived(busList);
            }
        });
        poiSearch(keyWord);
    }

    public void poiSearch(String keyWord) {
        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .location(mLocation) // 搜索中心点位置
                .radius(1200) // 搜索半径，单位：米
                .keyword(keyWord) // 搜索关键词
                .pageNum(0) // 分页页码，从0开始
                .pageCapacity(10)// 分页容量，最大值为50
                .scope(2));
    }

    public List<BusLineResult.BusStation> getStations() {
        return mStations;
    }


    // 计算地球上两点之间的距离，单位为千米
    @SuppressLint("DefaultLocale")
    public int calculateDistance(double lat, double lng) {
        //DistanceUtil.getDistance();
        double latitude = mLocation.latitude;
        double longitude = mLocation.longitude;

        double earthRadius = 6371; // 地球半径，单位为千米
        double dLat = Math.toRadians(lat - latitude);
        double dLng = Math.toRadians(lng - longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(lat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c * 1000;
        return (int) distance;
    }
}

