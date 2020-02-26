package com.caiyu.lib_base.http;

import java.util.HashMap;

import okhttp3.Interceptor;

/**
 * @author luys
 * @describe 选项配置
 * @date 2020-01-17
 * @email samluys@foxmail.com
 */
public class HttpConfig {

    public String cacheName = "HttpCache";
    public int cacheSize = 10 * 1024 * 1024;
    public int timeout = 15;
    public boolean isDebug = false;
    public String host;// 常用请求地址
    public String uploadHost;// 其他请求地址
    public Interceptor addHeaderInterceptor;// 添加请求头
    public Interceptor getParamsInterceptor;// 获取请求参数用来签名等操作

    private HttpConfig() {

    }

    public static HttpConfig getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static final class InstanceHolder {
        private static final HttpConfig INSTANCE = new HttpConfig();
    }

}
