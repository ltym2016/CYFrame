package com.caiyu.lib_base.http;


import com.caiyu.lib_base.constants.Constants;
import com.caiyu.lib_base.utils.Utils;

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

    protected static OkHttpClient mOkHttpClient;

    static {
        initOkHttpClient();
    }

    private static void initOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (RetrofitClientManager.class) {
                if (mOkHttpClient == null) {
                    //设置Http缓存
                    Cache cache = new Cache(new File(Utils.getContext()
                            .getExternalCacheDir(), "HttpCache"), 1024 * 1024 * 100);

                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .retryOnConnectionFailure(true)
                            .connectTimeout(Constants.HTTP_TIME, TimeUnit.SECONDS)
                            .writeTimeout(Constants.HTTP_TIME, TimeUnit.SECONDS)
                            .readTimeout(Constants.HTTP_TIME, TimeUnit.SECONDS)
                            .addInterceptor(InterceptorUtils.headerInterceptor())
                            .addInterceptor(InterceptorUtils.logInterceptor())
//                            .addInterceptor(new AddParameterInterceptor())
                            .addInterceptor(new ResponseInterceptor())
                            .build();
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
