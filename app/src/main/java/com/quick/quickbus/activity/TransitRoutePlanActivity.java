/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.quick.quickbus.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.quick.quickbus.R;
import com.quick.quickbus.adapter.RouteLineListAdapter;
import com.quick.quickbus.util.TransitRouteOverlay;
import com.quick.quickbus.util.Utils;

import java.util.List;

/**
 * 此demo用来展示如何进行公交路线搜索并在地图使用RouteOverlay、TransitOverlay绘制
 * 同时展示如何进行节点浏览并弹出泡泡
 */
public class TransitRoutePlanActivity extends AppCompatActivity
        implements BaiduMap.OnMapClickListener,
        OnGetRoutePlanResultListener, View.OnClickListener {

    public LatLng location;
    public MyLocationData locData;
    private RouteLine mRouteLine = null;
    private BroadcastReceiver mLocationDataReceiver;
    private MapView mMapView = null;    // 地图View
    private BaiduMap mBaidumap = null;
    private RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    private boolean isFirstLoc = true;
    private AutoCompleteTextView mStrartNodeView;
    private AutoCompleteTextView mEndNodeView;
    private View mBottomOverviewCard = null;
    private TextView mETAText = null;
    private List<? extends RouteLine> mRouteLines;
    private ListView mRouteListView = null;
    private RouteLineListAdapter mRouteLineListAdapter = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transit_route);

        location = getIntent().getParcelableExtra("location");

        registerLocationReceiver(this);
        // 初始化地图
        initView();
        initMapView();

        mBaidumap = mMapView.getMap();
        setMapStatus();
        mBaidumap.setViewPadding(20, 0, 0, 20);

        MyLocationConfiguration config = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null);
        mBaidumap.setMyLocationConfiguration(config);
        mBaidumap.setMyLocationEnabled(true);
        mBaidumap.setCompassEnable(true); // 设置指南针是否显示
//        mBaidumap.setCompassPosition(new Point(1500,500));

        // 地图点击事件处理
        mBaidumap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

    }

    private void initMapView() {
        mMapView.showZoomControls(true); // 设置是否显示比例尺控件
        mMapView.showScaleControl(true); // 设置是否显示缩放控件
    }

    private void initRouteOverViewData() {
        mRouteLineListAdapter =
                new RouteLineListAdapter(TransitRoutePlanActivity.this.getApplicationContext(),
                        mRouteLines,
                        RouteLineListAdapter.Type.TRANSIT_ROUTE);
        mRouteListView.setAdapter(mRouteLineListAdapter);
    }

    private void initView() {
        mMapView = findViewById(R.id.map);
        // 路线概览卡片
        mBottomOverviewCard = findViewById(R.id.rooter_search);
        mETAText = findViewById(R.id.eta_text);
        Button detailButton = findViewById(R.id.route_detail_button);
        detailButton.setOnClickListener(this);

        // 起终点输入
        mStrartNodeView = findViewById(R.id.route_search_input_start_text);
        mEndNodeView = findViewById(R.id.route_search_input_end_text);

        // 路线列表
        mRouteListView = findViewById(R.id.route_result_listview);
        mRouteListView.setOnItemClickListener((parent, view, position, id) -> {
            if (mRouteLines == null) {
                return;
            }
            mRouteListView.setVisibility(View.GONE);
            mBottomOverviewCard.setVisibility(View.VISIBLE);

            mRouteLine = mRouteLines.get(position);

            // 路线概览
            updateRouteOverViewCard(mRouteLine);

            // 路线绘制
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            overlay.setData((TransitRouteLine) mRouteLine);
            overlay.addToMap();
            overlay.zoomToSpanPaddingBounds(100, 500, 100, 500);
        });

    }

    public void setMapStatus() {
        if (isFirstLoc) {
            isFirstLoc = false;
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(location).zoom(18.0f);
            mBaidumap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    public void registerLocationReceiver(Context context) {
        // 创建广播接收器
        mLocationDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.example.LOCATION_UPDATED")) {
                    // 获取最新的定位信息
                    double latitude = intent.getDoubleExtra("latitude", 0);
                    float direction = intent.getFloatExtra("direction", 0);
                    float radius = intent.getFloatExtra("radius", 0);
                    double longitude = intent.getDoubleExtra("longitude", 0);

                    // 在这里处理定位信息
                    locData = new MyLocationData.Builder()
                            .accuracy(radius) // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(direction).latitude(latitude).longitude(longitude).build();
                    mBaidumap.setMyLocationData(locData);

                }
            }
        };

        // 注册广播接收器
        LocalBroadcastManager.getInstance(context).registerReceiver(mLocationDataReceiver,
                new IntentFilter("com.example.LOCATION_UPDATED"));
    }

    public void unregisterLocationReceiver(Context context) {
        // 取消注册广播接收器
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mLocationDataReceiver);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateRouteOverViewCard(RouteLine routeLine) {
        String totalTime = "";
        String totalDistance = "";
        int time = routeLine.getDuration();
        if (time / 3600 == 0) {
            totalTime = time / 60 + "分钟";
        } else {
            totalTime = time / 3600 + "小时" + (time % 3600) / 60 + "分钟";
        }

        int distance = routeLine.getDistance();
        if (distance / 1000 == 0) {
            totalDistance = distance + "米";
        } else {
            totalDistance = String.format("%.1f", distance / 1000f) + "公里";
        }

        mETAText.setText(totalTime + " " + totalDistance);
    }

    /**
     * 发起路线规划搜索示例
     */
    public void searchButtonProcess(View v) {
        Utils.hideKeyBoard(TransitRoutePlanActivity.this);
        if (mRouteLines != null) {
            // 重置路线数据
            mRouteLines.clear();
            mRouteLines = null;
        }

        if (mBottomOverviewCard.getVisibility() == View.VISIBLE) {
            mBottomOverviewCard.setVisibility(View.GONE);
        }

        // 清除之前的覆盖物
        mBaidumap.clear();
        // 设置起终点信息 起点参数
        PlanNode startNode = PlanNode.withCityNameAndPlaceName(MainActivity.city,
                mStrartNodeView.getText().toString().trim());
        // 终点参数
        PlanNode endNode = PlanNode.withCityNameAndPlaceName(MainActivity.city,
                mEndNodeView.getText().toString().trim());
        // 创建换乘路线规划Option
        TransitRoutePlanOption transitRoutePlanOption =
                new TransitRoutePlanOption().from(startNode).to(endNode).city(MainActivity.city);
        // 发起换乘路线规划
        mSearch.transitSearch(transitRoutePlanOption);
    }


    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        if (result != null && result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            Toast.makeText(TransitRoutePlanActivity.this,
                    "起终点或途经点地址有岐义，通过result.getSuggestAddrInfo()接口获取建议查询信息",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(TransitRoutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mRouteListView.setVisibility(View.VISIBLE);
            if (mRouteLines != null) {
                mRouteLines.clear();
            }
            mRouteLines = result.getRouteLines();

//            Log.d("routeline", new Gson().toJson(result.getRouteLines()));
            initRouteOverViewData();
            mRouteLineListAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult result) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {

    }

    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
        Utils.hideKeyBoard(this);
    }

    @Override
    public void onMapPoiClick(MapPoi poi) {

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.route_detail_button:
                Intent intent = new Intent(TransitRoutePlanActivity.this.getApplicationContext(),
                        RouteDetailActivity.class);
                intent.putExtra("route_plan_result", mRouteLine);
                intent.putExtra("route_plan_type", 1);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放检索对象
        if (mSearch != null) {
            mSearch.destroy();
        }
        mBaidumap.clear();
        mMapView.onDestroy();
        mBaidumap.setMyLocationEnabled(false);
        unregisterLocationReceiver(this);
    }

    private static class MyTransitRouteOverlay extends TransitRouteOverlay {

        private MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_end);
        }
    }
}
