package com.quick.quickbus.search;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.google.gson.Gson;
import com.quick.quickbus.adapter.OnGetPoiSearchResultAdapter;

import java.util.List;

public class SearchInCity {
    private final String keyName;
    private final Context mContext;
    private final String city;
    private PoiSearch mPoiSearch;

    public SearchInCity(Context mContext, String city, String keyName) {
        this.mContext = mContext;
        this.keyName = keyName;
        this.city = city;
    }

    public void search() {
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

                Log.i("city", new Gson().toJson(pois));
/*                List<BusStop> busList = new ArrayList<>();
                StringBuilder uids = new StringBuilder("");
                for (PoiInfo poi : pois) {
                    // 获取公交站点信息
                    String name = poi.name; // 公交站点名称
                    String uid = poi.uid;
                    String address = poi.address;
                    LatLng location = poi.location; // 公交站点位置

                    Log.d("NearbyBusStations", "name: " + name + ", uid: " + uid + ", address: " + address);
                }*/

            }
        });

        mPoiSearch.searchInCity(new PoiCitySearchOption()
                .tag("公交线路")
                .keyword(keyName)
                .city(city)
                .cityLimit(true));
    }

}
