package com.quick.quickbus.search;

import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.quick.quickbus.adapter.OnGetPoiSearchResultAdapter;
import com.quick.quickbus.listener.NearSearchResultListener;
import com.quick.quickbus.model.BusLine;
import com.quick.quickbus.model.BusStop;

import java.util.ArrayList;
import java.util.List;

public class NearbyBusStations {
    private final Context mContext;
    private PoiSearch mPoiSearch;
    private final LatLng mLocation;
    public String type;
//    private final List<BusLineResult.BusStation> mStations = new ArrayList<>();

    public NearbyBusStations(Context context, LatLng location) {
        this.mContext = context;
        this.mLocation = location;
    }

    public void setListener(NearSearchResultListener listener) {
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
                    listener.onDataReceived(null);
                    return;
                }
                // 获取搜索结果列表
                List<PoiInfo> pois = poiResult.getAllPoi();
                List<Object> list = new ArrayList<>();
                for (PoiInfo poi : pois) {
                    if (type.equals("nearby")) {
                        BusStop busStop = new BusStop(poi);
                        busStop.setDistance(mLocation);
                        list.add(busStop);

                    } else if (type.equals("city")) {
                        BusLine busLine = new BusLine(poi);
                        list.add(busLine);
                    }
                }
                listener.onDataReceived(list);
            }
        });
//        poiSearch(keyWord);
    }

    public void nearbySearch(String keyWord) {
        if (mPoiSearch == null) return;
        type = "nearby";
        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .location(mLocation) // 搜索中心点位置
                .radius(1200) // 搜索半径，单位：米
                .keyword(keyWord)
                .pageNum(0) // 分页页码，从0开始
                .pageCapacity(20));// 分页容量，最大值为50
    }

    public void citySearch(String city, String keyword) {
        if (mPoiSearch == null) return;
        type = "city";
        mPoiSearch.searchInCity(new PoiCitySearchOption()
                .city(city)
                .pageCapacity(1)
                .keyword(keyword));
    }
}

