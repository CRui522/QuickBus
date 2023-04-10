package com.quick.quickbus.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PerformanceHintManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.baidu.mapapi.model.LatLng;
import com.quick.quickbus.NearbyBusStations;
import com.quick.quickbus.PermissionsManager;
import com.quick.quickbus.R;
import com.quick.quickbus.adapter.MyListAdapter;
import com.quick.quickbus.listener.NearSearchResultListener;
import com.quick.quickbus.model.BusStop;

import java.util.List;


public class MainActivity extends AppCompatActivity {


    private List<BusStop> busStop;
    private LatLng location;
    private MyListAdapter myListAdapter;
    private RecyclerView recyclerView;
    private Handler mHandler = new Handler();
    private LocationClient mLocationClient;
    private NearSearchResultListener nearSearchResultListener;
    private MyLocationListener myLocationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        // 检查定位权限是否已经被授权
        new PermissionsManager(this, 1).checkAndRequestPermissions();
        LocationClient.setAgreePrivacy(true);

        searchNearStations();
        Log.i("abc", "123");
        
    }

    public void searchNearStations() {
        runOnUiThread(() -> {
            try {
                initLocation();
                nearSearchResultListener = data -> {
                    busStop = data;
                    Log.i("abc", String.valueOf(data));
                    myListAdapter.updateData(busStop);
                };
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
/*        mHandler.postDelayed(() -> {

        }, 1000);*/
    }

    public void initView() {
        recyclerView = findViewById(R.id.routeRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myListAdapter = new MyListAdapter(busStop);
        recyclerView.setAdapter(myListAdapter);
    }

    public void initLocation() throws Exception {
        // 初始化LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());

        // 创建MyLocationListener对象
        myLocationListener = new MyLocationListener();

        // 注册监听函数
        mLocationClient.registerLocationListener(myLocationListener);
        if (mLocationClient != null) {

            // 配置定位参数
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // 设置定位模式为高精度
            // 设置是否需要地址信息
            option.setIsNeedAddress(true);
            option.setIsNeedLocationPoiList(true);
            //option.setIgnoreKillProcess(false);
            option.setCoorType("bd09ll"); // 设置返回的定位结果坐标系
            option.setScanSpan(1000); // 设置定位间隔时间
            mLocationClient.setLocOption(option);
            // 启动定位
            mLocationClient.start();
        }
    }

    class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                // 定位失败
                return;
            }
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();
            location = new LatLng(latitude, longitude);

            NearbyBusStations nearbyBusStations = new NearbyBusStations(getApplicationContext(), location);
            nearbyBusStations.search(nearSearchResultListener);

            // 打印定位结果
            Log.d("MainActivity", "latitude: " + latitude + ", longitude: " + longitude);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 停止定位
        mLocationClient.stop();

        // 注销监听函数
        mLocationClient.unRegisterLocationListener(myLocationListener);
    }
}