package com.quick.quickbus;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

public class BusApplication extends Application {
    public static String host = "http://192.168.0.108:3000";

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.setAgreePrivacy(this, true);
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        // 默认本地个性化地图初始化方法
        SDKInitializer.initialize(this);

        // 自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        // 包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
}
