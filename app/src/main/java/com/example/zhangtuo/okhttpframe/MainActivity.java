package com.example.zhangtuo.okhttpframe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.get_cache).setOnClickListener(this);
        findViewById(R.id.post).setOnClickListener(this);
        findViewById(R.id.get_service_no_cache).setOnClickListener(this);
        findViewById(R.id.get_official_cache).setOnClickListener(this);

        tvContent = (TextView) findViewById(R.id.content);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.get_cache:
                getRequest();
                break;
            case R.id.post:
                postRequest();
                break;
            case R.id.get_service_no_cache:
                getNoCacheRequest();
                break;
            case R.id.get_official_cache:
                getOfficialCacheRequest();
                break;
            default:
                break;
        }
    }

    /**
     * 如果项目中不同的模块需要进行不同的缓存配置，设置不同的缓存时间
     *
     * okhttp中建议用CacheControl这个类来进行缓存策略的制定
     */
    private void getOfficialCacheRequest() {
        File cacheFile = new File(getExternalCacheDir().toString(),"cache");
        int cacheSize = 10 * 1024 * 1024;
        final Cache cache = new Cache(cacheFile,cacheSize);

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient.Builder()
                        .cache(cache)
                        .build();
                //设置缓存时间为60秒
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(60, TimeUnit.SECONDS)
                        .build();
                Request request = new Request.Builder()
                        .url("http://blog.csdn.net/briblue")
                        .cacheControl(cacheControl)
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 服务端没配置缓存，okhttp重写响应头进行缓存
     */
    private void getNoCacheRequest() {
        File file = new File(getExternalCacheDir().toString(), "cache");
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(file, cacheSize);

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new CacheInterceptor())
                .cache(cache)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvContent.setText(e.toString());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("tag", "response body  :" + response.body().string());
                Log.i("tag", "response cache  :" + response.cacheResponse());
                Log.i("tag", "response netWork  :" + response.networkResponse());
            }
        });

    }

    private void postRequest() {
        MediaType MEDIA_TYPE_MARKDOWN
                = MediaType.parse("text/x-markdown; charset=utf-8");
        String postBody = ""
                + "Releases\n"
                + "--------\n"
                + "\n"
                + " * _1.0_ May 6, 2013\n"
                + " * _1.1_ June 15, 2013\n"
                + " * _1.2_ August 11, 2013\n";

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody))
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tvContent.setText(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    public void getRequest() {
        //创建缓存--服务端支持缓存
        File file = new File(getExternalCacheDir().toString(), "cache");
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(file, cacheSize);

        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .cache(cache)
                .build();

        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvContent.setText(e.toString());
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                Log.i("tag", "response body  :" + response.body().string());
                Log.i("tag", "response cache  :" + response.cacheResponse());
                Log.i("tag", "response netWork  :" + response.networkResponse());
//                if (response != null) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                tvContent.setText(response.body().string());
//                                Log.i("tag","response body  :"+ response.body().string());
//                                Log.i("tag","response cache  :"+ response.cacheResponse());
//                                Log.i("tag","response netWork  :"+ response.networkResponse());
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
            }
        });
    }
}
