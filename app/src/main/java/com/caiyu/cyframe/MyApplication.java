package com.caiyu.cyframe;

import android.app.Application;

import com.caiyu.lib_base.http.HttpConfig;
import com.caiyu.lib_base.http.HttpUtils;
import com.samluys.jutils.Utils;
import com.samluys.jutils.log.LogUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

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

        Utils.init(this);

        LogUtils.newBuilder()
                .debug(Utils.isDebug())
                .tag("CIRCLE_DIMENSION_LOG")
                .build();

        HttpUtils.init(Utils.getStringFromConfig(R.string.host))
                .cacheName("http_cache")
                .isDebug(true)
                .timeout(15);
    }
}
