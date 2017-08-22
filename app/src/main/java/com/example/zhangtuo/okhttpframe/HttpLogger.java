package com.example.zhangtuo.okhttpframe;

import android.util.Log;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by zhangtuo on 2017/8/22.
 */

public class HttpLogger implements HttpLoggingInterceptor.Logger {
    @Override
    public void log(String message) {
        Log.d("HttpLogInfo", message);
    }
}