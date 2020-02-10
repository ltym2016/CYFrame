package com.caiyu.cyframe;

import android.app.Application;

import com.caiyu.lib_base.http.HttpUtils;

import java.util.HashMap;

/**
 * @author luys
 * @describe
 * @date 2020-02-10
 * @email samluys@foxmail.com
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        HashMap<String,String> headers = new HashMap<>();
        headers.put("Token", "");
        HttpUtils.init()
                .cacheName("http_cache")
                .isDebug(true)
                .timeout(15)
                .headers(headers);
    }
}
