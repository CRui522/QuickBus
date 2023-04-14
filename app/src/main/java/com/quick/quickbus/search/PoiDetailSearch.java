package com.quick.quickbus.search;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.google.gson.Gson;
import com.quick.quickbus.adapter.OnGetPoiSearchResultAdapter;

import java.util.List;

public class PoiDetailSearch {

    private final String uids;
    private final Context context;
    private PoiSearch mPoiSearch;

    public PoiDetailSearch(Context context, String uids) {
        this.uids = uids;
        this.context = context;
    }

    public void search() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultAdapter() {
            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
                super.onGetPoiDetailResult(poiDetailSearchResult);
                if (poiDetailSearchResult == null || poiDetailSearchResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 查询失败处理
                    Toast.makeText(context, "搜索失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<PoiDetailInfo> pois = poiDetailSearchResult.getPoiDetailInfoList();
                Log.i("detail", new Gson().toJson(pois));
            }
        });
        mPoiSearch.searchPoiDetail(new PoiDetailSearchOption()
                .poiUids(uids));
    }

}
