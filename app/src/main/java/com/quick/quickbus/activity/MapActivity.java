package com.quick.quickbus.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.quick.quickbus.R;


public class MapActivity extends AppCompatActivity {

    public MyLocationData locData;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private BroadcastReceiver mLocationDataReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_busstop);

        registerLocationReceiver(this);
        // 初始化地图控件
        initView();

        mBaiduMap = mMapView.getMap();

        mBaiduMap.setOnMarkerClickListener(marker -> {
            Log.i("marker", String.valueOf(marker.isInfoWindowEnabled()));
            return true;
        });
        MyLocationConfiguration config = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null);
        mBaiduMap.setMyLocationConfiguration(config);
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setCompassEnable(true); // 设置指南针是否显示
        mBaiduMap.showMapPoi(true);

        // 获取传递的地点uid值
        LatLng latLng = getIntent().getParcelableExtra("latLng");
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng).zoom(18.0f);
        // 将地图定位到该点
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
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
                    mBaiduMap.setMyLocationData(locData);

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

    public void initView() {
        mMapView = (MapView) findViewById(R.id.mapview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterLocationReceiver(this);
        mBaiduMap.clear();
        mMapView.onDestroy();
    }
}

