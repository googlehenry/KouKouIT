package com.koukou.it.util;

import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    private static final String TAG = "HttpUtil";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void sendOkHttpGetRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static void uploadImage(String url, File imageFile, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        File imageCompressed=ImageUtil.compressImage(imageFile);
        RequestBody image = RequestBody.create(MediaType.parse("image/jpeg"), imageCompressed);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image.jpg", image)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpPostRequest(String json, String address, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, json);
        Log.d(TAG, "sendOkHttpPostRequest:address : " + address);
        Request request = new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
}
