package com.quick.quickbus;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsManager {

    private Activity mActivity;
    private int mRequestCode;
    private OnPermissionsCallback mCallback;

    public interface OnPermissionsCallback {
        void onPermissionsGranted();
        void onPermissionsDenied();
    }

    public PermissionsManager(Activity activity, int requestCode) {
        mActivity = activity;
        mRequestCode = requestCode;
        //mCallback = callback;
    }

    public void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //mCallback.onPermissionsGranted();
        } else {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    mRequestCode);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == mRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mCallback.onPermissionsGranted();
            } else {
                mCallback.onPermissionsDenied();
            }
        }
    }
}

