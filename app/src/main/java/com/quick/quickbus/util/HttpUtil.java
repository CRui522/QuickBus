package com.quick.quickbus.util;

import android.util.Log;

import com.quick.quickbus.model.ResponseData;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {
    private static final OkHttpClient client = new OkHttpClient();

    public static ResponseData run(String url, String method, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
        Request request = new Request.Builder()
                .url(url)
                .method(method, builder.build())
                .build();
        try (Response response = client.newCall(request).execute()) {
            int code = response.code();
            String data = response.body().string();
            return new ResponseData(code, data);
        } catch (IOException e) {
            Log.i("error", e.toString());
            return null;
        }
    }
}