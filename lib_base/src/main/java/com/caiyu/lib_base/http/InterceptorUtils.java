package com.caiyu.lib_base.http;

import com.caiyu.lib_base.constants.Constants;
import com.samluys.jutils.SPUtils;
import com.samluys.jutils.log.LogUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author luys
 * @describe 拦截器
 * @date 2018/4/23
 * @email samluys@foxmail.com
 */
public class InterceptorUtils {

    /**
     * 头部信息
     *
     * @return
     */
    public static Interceptor headerInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                String channelid = (String) SPUtils.getInstance().get(Constants.SP_CHANNEL_ID, "0");
                String subchannelid = (String) SPUtils.getInstance().get(Constants.SP_SUB_CHANNEL_ID, "0");

                Request mRequest = chain.request().newBuilder()
//                        .header("channelid", channelid)// 渠道号
//                        .header("version", ApplicationUtils.getApp().getResources().getString(R.string.versionName))
//                        .header("subchannelid",subchannelid)
//                        .header("interfaceversion",ApplicationUtils.getApp().getResources().getString(R.string.interfaceCode))
//                        .header("deviceid", DeviceUtils.getDeviceId())// 设备ID
//                        .header("appid", ApplicationUtils.getApp().getResources().getString(R.string.app_id))// 每次安装的code
//                        .header("os", "1")// 1 是Android 2 是IOS
//                        .header("timestamp", System.currentTimeMillis()/1000+"")
//                        .header("Token", UserInfoUtils.getInstance().getToken())
                        .header("clientid", "3")
                        .build();
                return chain.proceed(mRequest);
            }
        };
    }

    /**
     * 日志拦截器
     *
     * @return
     */
    public static Interceptor logInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                okhttp3.Response response = chain.proceed(chain.request());
                okhttp3.MediaType mediaType = response.body().contentType();
                String content = response.body().string();
                LogUtils.e("request url====>" + request.url());
                LogUtils.json("response result",content);
                return response.newBuilder()
                        .body(okhttp3.ResponseBody.create(mediaType, content))
                        .build();
            }
        };
    }

}
