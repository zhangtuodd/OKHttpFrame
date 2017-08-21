package com.example.zhangtuo.okhttpframe;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhangtuo on 2017/8/21.
 * <p>
 * 使用interceptor重写Response的头信息，支持okhttp缓存
 */

public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        Response response1 = response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                //cache for 1mins
                .header("Cache-Control", "max-age=" + 60  *1)
                .build();
        return response1;
    }
}