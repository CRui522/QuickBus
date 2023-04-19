package com.quick.quickbus.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
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
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.quick.quickbus.MySharedPreferences;
import com.quick.quickbus.PermissionUtils;
import com.quick.quickbus.R;
import com.quick.quickbus.adapter.MyListAdapter;
import com.quick.quickbus.listener.NearSearchResultListener;
import com.quick.quickbus.model.BusLine;
import com.quick.quickbus.model.BusStop;
import com.quick.quickbus.search.NearbyBusStations;
import com.quick.quickbus.search.SearchBusLine;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String city;
    public static String addressText;
    public static String addressName;

    private List<BusStop> busStop;
    private TextView address;
    private final Handler mHandler = new Handler();
    public LatLng location;
    private MyListAdapter myListAdapter;
    private RecyclerView recyclerView;
    private Button search;
    private LocationClient mLocationClient;
    private NearSearchResultListener nearSearchResultListener;
    private MyLocationListener myLocationListener;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionUtils.requestPermissions(this);
        LocationClient.setAgreePrivacy(true);

        initView();

        search.setOnClickListener(view -> {
            Intent intent = new Intent(this, TransitRoutePlanActivity.class);
            intent.putExtra("location", location);
            this.startActivity(intent);
        });

        // 添加刷新监听器
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        myListAdapter.setOnItemClickListener(new MyListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                BusStop stop = busStop.get(position);
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                intent.putExtra("latLng", stop.getLatLng());
                startActivity(intent);
            }

            @Override
            public void onExpend(int position) {
                BusStop stop = busStop.get(position);
                searchBusLineInCity(stop, position);

            }
        });
    }

    public void searchNearStations() {
        nearSearchResultListener = data -> {
            showLoadingIndicator(false);
            if (data == null) {
                Log.i("hihihi", "1");
                Toast.makeText(this, "下拉刷新数据", Toast.LENGTH_SHORT).show();
                return;
            }

            busStop = (List<BusStop>) data;
            myListAdapter.updateData(busStop);

            address.setText(addressName);
            address.setSelected(true);
        };

        mHandler.postDelayed(() -> {
            if (location != null) {
                NearbyBusStations nearbyBusStations = new NearbyBusStations(getApplicationContext(), location);
                nearbyBusStations.setListener(nearSearchResultListener);
                nearbyBusStations.nearbySearch("公交车站");
            }
        }, 500);
    }

    public void searchBusLineInCity(BusStop stop, int position) {
        nearSearchResultListener = data -> {
            if (data == null) {
                return;
            }
            List<BusLine> lines = (List<BusLine>) data;

            SearchBusLine searchBusLine = new SearchBusLine(this, myListAdapter, position);
            searchBusLine.searchBusLine(lines);

            //myListAdapter.updateItem(position, lines.get(0));
        };
        NearbyBusStations nearbyBusStations = new NearbyBusStations(this, stop.getLatLng());
        nearbyBusStations.setListener(nearSearchResultListener);
        for (String lineName : stop.getLineNames()) {
            nearbyBusStations.citySearch(stop.getCity(), lineName);
        }
    }

    // 显示或隐藏加载指示器
    private void showLoadingIndicator(boolean isLoading) {
        swipeRefreshLayout.setRefreshing(isLoading);
    }

    private void refreshData() {
        searchNearStations();
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
            option.setIsNeedAddress(true);// 设置是否需要地址信息
            option.setIsNeedLocationPoiList(true); // 可选，是否需要周边POI信息，默认为不需要，即参数为false
            option.setNeedDeviceDirect(true); // 可选，设置是否需要设备方向结果
            //option.setIgnoreKillProcess(false);

            option.setLocationNotify(true); // 可选，默认false，设置是否当卫星定位有效时按照1S1次频率输出卫星定位结果
            option.setOpenGnss(true); // 打开gps
            option.setCoorType("bd09ll"); // 设置返回的定位结果坐标系
            option.setScanSpan(1000); // 设置定位间隔时间
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!PermissionUtils.hasPermissions(this, requestCode)) {
            finish();
        }

        try {
            initLocation();
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                int login = new MySharedPreferences(getApplicationContext(), "auth").getInt("login", 0);
                if (login == 0) {
                    intent.setClass(this, LoginActivity.class);
                    this.startActivity(intent);
                } else {
                    Toast.makeText(this, "已登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_item_about:
                intent.setClass(this, InfoActivity.class);
                this.startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initView() {
        search = findViewById(R.id.searchButton);
        address = findViewById(R.id.address);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = findViewById(R.id.routeRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myListAdapter = new MyListAdapter(this, busStop);
        recyclerView.setAdapter(myListAdapter);
    }

    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                // 定位失败
                Toast.makeText(MainActivity.this, "获取定位失败", Toast.LENGTH_SHORT).show();
                return;
            }
            Poi poi = bdLocation.getPoiList().get(0);
            String poiName = poi.getName();    //获取POI名称
//            String poiTags = poi.getTags();    //获取POI类型
            addressText = poi.getAddr();    //获取POI地址 //获取周边POI信息
//            Log.d("poi", "poiName: " + poiName + ", poiTags: " + poiTags + "poiAddr: " + poiAddr);
            city = bdLocation.getCity();
            addressName = poiName;
//            address.setMovementMethod(new ScrollingMovementMethod());
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();
            location = new LatLng(latitude, longitude);
            float radius = bdLocation.getRadius();
            float direction = bdLocation.getDirection();
            // 发送广播，通知其他组件定位信息更新
            sendLocationBroadcast(radius, direction, latitude, longitude);

            // 打印定位结果
            Log.d("location", "latitude: " + latitude + ", longitude: " + longitude);
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