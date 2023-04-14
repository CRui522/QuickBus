package com.quick.quickbus.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.quick.quickbus.PermissionUtils;
import com.quick.quickbus.R;
import com.quick.quickbus.adapter.MyListAdapter;
import com.quick.quickbus.listener.MylocationDataListener;
import com.quick.quickbus.listener.NearSearchResultListener;
import com.quick.quickbus.model.BusStop;
import com.quick.quickbus.search.NearbyBusStations;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String city;
    private List<BusStop> busStop;
    private final Handler mHandler = new Handler();
    public MyLocationData locData;
    public LatLng location;
    private MyListAdapter myListAdapter;
    public MylocationDataListener mylocationDataListener;
    private RecyclerView recyclerView;
    private Button search;
    private LocationClient mLocationClient;
    private NearSearchResultListener nearSearchResultListener;
    private MyLocationListener myLocationListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NearbyBusStations nearbyBusStations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        PermissionUtils.requestPermissions(this);
        LocationClient.setAgreePrivacy(true);

        search.setOnClickListener(view -> {
            Intent intent;
            intent = new Intent(this, TransitRoutePlanActivity.class);
            this.startActivity(intent);
        });


        // 添加刷新监听器
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        //searchNearStations();
        myListAdapter.setOnItemClickListener(expandedHeight -> {
            // 滑动到展开的 item
            recyclerView.smoothScrollBy(0, expandedHeight);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!PermissionUtils.hasPermissions(this, requestCode)) {
            finish();
        }
        // 在用户进入界面时显示下拉刷新动画

        swipeRefreshLayout.post(() -> {
            showLoadingIndicator(true);
            searchNearStations();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent();
        // 检查哪个菜单项被点击
        switch (item.getItemId()) {
            case R.id.menu_item_personal:
                intent.setClass(this, LoginActivity.class);
                break;
            case R.id.menu_item_about:
                intent.setClass(this, InfoActivity.class);
                break;
        }
        this.startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    public void searchNearStations() {
        try {
            initLocation();
            search();
            nearSearchResultListener = data -> {
                busStop = data;
                myListAdapter.updateData(busStop);
                Log.i("data", new Gson().toJson(data));
                showLoadingIndicator(false);
                Toast.makeText(this, "数据获取成功", Toast.LENGTH_SHORT).show();
/*                    List<String> busLines = new ArrayList<>();
                    for (BusStop busStop : data) {
                        String mAddress = busStop.getAddress();
                        boolean aBoolean = busLines.contains(mAddress);
                        if (!aBoolean) busLines.add(mAddress);
                    }
                    String busLine = String.join("$", busLines);*/
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 显示或隐藏加载指示器
    private void showLoadingIndicator(boolean isLoading) {
        swipeRefreshLayout.setRefreshing(isLoading);
    }

    private void refreshData() {
        searchNearStations();
    }

    public void initView() {
        search = findViewById(R.id.searchButton);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
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
            option.setOpenGnss(true); // 打开gps
            option.setCoorType("bd09ll"); // 设置返回的定位结果坐标系
            option.setScanSpan(3000); // 设置定位间隔时间
            mLocationClient.setLocOption(option);
            // 启动定位
            mLocationClient.start();
        }
    }

    // 发送广播
    private void sendLocationBroadcast(float radius, float direction, double latitude, double longitude) {
        Intent intent = new Intent("com.example.LOCATION_UPDATED");
        intent.putExtra("radius", radius);
        intent.putExtra("direction", direction);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void search() {
        mHandler.postDelayed(() -> {
            nearbyBusStations = new NearbyBusStations(getApplicationContext(), location);
            nearbyBusStations.search(nearSearchResultListener, "公交车站");
        }, 500);
    }

    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                // 定位失败
                Toast.makeText(MainActivity.this, "获取定位失败", Toast.LENGTH_SHORT).show();
                return;
            }
            city = bdLocation.getCity();
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();
            location = new LatLng(latitude, longitude);

            // 发送广播，通知其他组件定位信息更新
            sendLocationBroadcast(bdLocation.getRadius(), bdLocation.getDirection(), latitude, longitude);

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