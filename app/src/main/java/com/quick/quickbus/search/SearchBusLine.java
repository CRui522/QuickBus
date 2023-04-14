package com.quick.quickbus.search;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.core.SearchResult;
import com.google.gson.Gson;

import java.util.List;

public class SearchBusLine {

    private final String uid;
    private final Context mContext;
    private final String city;

    public SearchBusLine(Context mContext, String uid, String city) {
        this.city = city;
        this.mContext = mContext;
        this.uid = uid;
    }

    public void search() {
        // 创建公交线路查询实例
        BusLineSearch busLineSearch = BusLineSearch.newInstance();

        // 设置公交线路查询监听器
        busLineSearch.setOnGetBusLineSearchResultListener(busLineResult -> {
            Log.i("stations", uid + city);
            if (busLineResult == null || busLineResult.error != SearchResult.ERRORNO.NO_ERROR) {
                // 查询失败处理
                Toast.makeText(mContext, "搜索失败", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: 在此处处理获取到的公交路线信息
            List<BusLineResult.BusStation> stations = busLineResult.getStations();
            Log.i("stations", new Gson().toJson(stations));
        });
        busLineSearch.searchBusLine(new BusLineSearchOption()
                .uid(uid)
                .city(city));

    }
}
