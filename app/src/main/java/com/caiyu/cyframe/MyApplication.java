package com.caiyu.cyframe;

import android.app.Application;

import com.caiyu.lib_base.http.HttpUtils;
import com.samluys.jutils.Utils;
import com.samluys.jutils.log.LogUtils;

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

        Utils.init(this);

        LogUtils.newBuilder()
                .debug(Utils.isDebug())
                .tag("CIRCLE_DIMENSION_LOG")
                .build();

        HashMap<String,String> headers = new HashMap<>();
        headers.put("clientid", "3");
        HttpUtils.init(Utils.getStringFromConfig(R.string.host))
                .cacheName("http_cache")
                .isDebug(true)
                .timeout(15)
                .headers(headers);
    }
}
