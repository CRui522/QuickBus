package com.quick.quickbus;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

public class PermissionUtils {
    private static final int REQUEST_CODE_LOCATION = 100;
    private static final int REQUEST_CODE_STORAGE = 101;

    public static void requestPermissions(Activity activity) {
        if (activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showPermissionDialog(activity);
        } else {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_LOCATION + REQUEST_CODE_STORAGE);
        }
    }

    public static boolean hasPermissions(Activity activity, int... requestCodes) {
        if (requestCodes.length == 0) return true;
        for (int requestCode : new int[]{100, 101}) {
            if (!hasPermission(activity, requestCode)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasPermission(Activity activity, int requestCode) {
        String permission = "";
        switch (requestCode) {
            case REQUEST_CODE_LOCATION:
                permission = Manifest.permission.ACCESS_FINE_LOCATION;
                break;
            case REQUEST_CODE_STORAGE:
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                break;
        }
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isLocationPermissionGranted(Activity activity) {
        return hasPermission(activity, REQUEST_CODE_LOCATION);
    }

    public static boolean isStoragePermissionGranted(Activity activity) {
        return hasPermission(activity, REQUEST_CODE_STORAGE);
    }

    public static void showPermissionDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("权限请求");
        builder.setMessage("这些权限是使用该功能所必需的。请允许相关权限");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_LOCATION + REQUEST_CODE_STORAGE);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}