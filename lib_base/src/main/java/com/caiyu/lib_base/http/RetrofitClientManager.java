package com.caiyu.lib_base.http;


import com.samluys.jutils.Utils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author luys
 * @describe
 * @date 2019/4/15
 * @email samluys@foxmail.com
 */
public class RetrofitClientManager {

    private static OkHttpClient mOkHttpClient;

    static {
        initOkHttpClient();
    }

    private static void initOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (RetrofitClientManager.class) {
                if (mOkHttpClient == null) {
                    //设置Http缓存
                    Cache cache = new Cache(new File(Utils.getContext()
                            .getExternalCacheDir(), HttpConfig.getInstance().cacheName),
                            HttpConfig.getInstance().cacheSize);

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            .cache(cache)
                            .retryOnConnectionFailure(true)
                            .connectTimeout(HttpConfig.getInstance().timeout, TimeUnit.SECONDS)
                            .writeTimeout(HttpConfig.getInstance().timeout, TimeUnit.SECONDS)
                            .readTimeout(HttpConfig.getInstance().timeout, TimeUnit.SECONDS);

                    if (HttpConfig.getInstance().addHeaderInterceptor != null) {
                        // 添加请求头
                        builder.addInterceptor(HttpConfig.getInstance().addHeaderInterceptor);
                    }

                    if (HttpConfig.getInstance().getParamsInterceptor != null) {
                        // 获取请求参数的拦截器
                        builder.addInterceptor(HttpConfig.getInstance().getParamsInterceptor);
                    }

                    if (HttpConfig.getInstance().isDebug) {
                        // 设置log格式
                        builder.addInterceptor(InterceptorUtils.logInterceptor());
                    }
                    builder.addInterceptor(new ResponseInterceptor());
                    mOkHttpClient = builder.build();
                }
            }
        }
    }

    protected <T> T getRetrofit(String url, Class<T> clazz) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(url)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //  Gson 转换器
                .addConverterFactory(GsonConverterFactory.create());
        return builder.build().create(clazz);
    }
}
