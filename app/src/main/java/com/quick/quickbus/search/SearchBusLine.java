package com.quick.quickbus.search;

import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.core.SearchResult;
import com.quick.quickbus.adapter.MyListAdapter;
import com.quick.quickbus.model.BusLine;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SearchBusLine {
    private final BusLineSearch mBusLineSearch;
    private List<BusLine> busLines;

    // 构造函数
    public SearchBusLine(Context context, MyListAdapter myListAdapter, int position) {
        mBusLineSearch = BusLineSearch.newInstance();
        // 查询结果回调函数
        mBusLineSearch.setOnGetBusLineSearchResultListener(result -> {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // 查询失败
                Toast.makeText(context, "搜索失败", Toast.LENGTH_SHORT).show();
                return;
            }
            // 处理查询结果
            Date startTime = result.getStartTime();
            Date endTime = result.getEndTime();
            String uid = result.getUid();
            List<BusLineResult.BusStation> stations = result.getStations();
            List<BusLineResult.BusStep> steps = result.getSteps();
            for (BusLine line : busLines) {
                if (Objects.equals(line.getUid(), uid)) {
                    line.setStartTime(startTime);
                    line.setEndTime(endTime);
                    line.setBusStationList(stations);
                    line.setBusStepList(steps);
                    myListAdapter.updateItem(position, line);
                }
            }
            //listener.onDataReceived(busLine);
        });
    }

    // 查询指定城市和公交线路uid的公交线路信息
    public void searchBusLine(List<BusLine> lines) {
        busLines = lines;
        for (BusLine line : lines) {
            mBusLineSearch.searchBusLine(new BusLineSearchOption()
                    .city(line.getCity())
                    .uid(line.getUid()));
        }
    }

}
