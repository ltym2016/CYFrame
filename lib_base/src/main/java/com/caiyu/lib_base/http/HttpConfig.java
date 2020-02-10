package com.caiyu.lib_base.http;

import java.util.HashMap;

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
    public HashMap<String, String> headerHashMap;
    public boolean isDebug = false;
    public boolean isSign;// 是否开启MD5签名
    public String host;
    public String uploadHost;

    private HttpConfig() {

    }

    public static HttpConfig getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static final class InstanceHolder {
        private static final HttpConfig INSTANCE = new HttpConfig();
    }

}
