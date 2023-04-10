package com.quick.quickbus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.quick.quickbus.listener.NearSearchResultListener;
import com.quick.quickbus.model.BusStop;

import java.util.ArrayList;
import java.util.List;

public class NearbyBusStations {
    private Context mContext;
    private PoiSearch mPoiSearch;
    private LatLng mLocation;
    private List<BusLineResult.BusStation> mStations = new ArrayList<>();

    public NearbyBusStations(Context context, LatLng location) {
        mContext = context;
        mLocation = location;
    }

    public void search(NearSearchResultListener listener) {
        // 设置搜索监听器
        // 创建 PoiSearch 对象
        mPoiSearch = PoiSearch.newInstance();

        // 设置搜索监听器
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                Log.i("abc", "456");
                if (poiResult == null || poiResult.error != PoiResult.ERRORNO.NO_ERROR) {
                    // 搜索失败
                    Toast.makeText(mContext, "搜索失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 获取搜索结果列表
                List<PoiInfo> pois = poiResult.getAllPoi();

                List<BusStop> busList = new ArrayList<>();
                for (PoiInfo poi : pois) {
                    // 获取公交站点信息
                    String name = poi.name; // 公交站点名称
                    String uid = poi.uid;
                    String address = poi.address;
                    LatLng location = poi.location; // 公交站点位置
                    double lat = location.latitude;
                    double lng = location.longitude;
                    int distance = calculateDistance(lat, lng);
                    BusStop busStop = new BusStop(name, uid, distance, address);
                    busList.add(busStop);
                    // 输出公交站点信息
                    Log.d("NearbyBusStations", "name: " + name + ", uid: " + uid + ", address: " + address + ", distance: " + distance + "m");
                }
                listener.onDataReceived(busList);
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            }
        });

        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .location(mLocation) // 搜索中心点位置
                .radius(1500) // 搜索半径，单位：米
                .keyword("公交车站") // 搜索关键词
                .pageNum(0) // 分页页码，从0开始
                .pageCapacity(10));// 分页容量，最大值为50
    }

    public List<BusLineResult.BusStation> getStations() {
        return mStations;
    }

    // 计算地球上两点之间的距离，单位为千米
    @SuppressLint("DefaultLocale")
    public int calculateDistance(double lat, double lng) {
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

