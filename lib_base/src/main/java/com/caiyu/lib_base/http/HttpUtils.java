package com.caiyu.lib_base.http;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * @author luys
 * @describe
 * @date 2020-02-10
 * @email samluys@foxmail.com
 */
public class HttpUtils {


    public static Builder init(String host) {
        return new Builder(host);
    }

    public static class Builder {

        private final HttpConfig mHttpConfig;

        private Builder(String host) {
            mHttpConfig = HttpConfig.getInstance();
            mHttpConfig.host = host;
        }


        /**
         * 接口域名
         * @param uploadHost
         * @return
         */
        public Builder uploadHost(String uploadHost) {
            mHttpConfig.uploadHost = uploadHost;
            return this;
        }


        /**
         * 缓存存放的文件名称
         * @param cacheName
         * @return
         */
        public Builder cacheName(String cacheName) {
            mHttpConfig.cacheName = cacheName;
            return this;
        }

        /**
         * 缓存存放的文件最大size
         * @param cacheSize
         * @return
         */
        public Builder cacheSize(int cacheSize) {
            mHttpConfig.cacheSize = cacheSize;
            return this;
        }

        /**
         * 请求超时
         * @param timeout
         * @return
         */
        public Builder timeout(int timeout) {
            mHttpConfig.timeout = timeout;
            return this;
        }

        /**
         * 是否为debug模式
         * @param isDebug
         * @return
         */
        public Builder isDebug(boolean isDebug) {
            mHttpConfig.isDebug = isDebug;
            return this;
        }

        /**
         * 添加头文件请求拦截器
         * @param interceptor
         * @return
         */
        public Builder addHeaderInterceptor(Interceptor interceptor) {
            mHttpConfig.addHeaderInterceptor = interceptor;
            return this;
        }

        /**
         * 获取请求参数的拦截器
         * @param interceptor
         * @return
         */
        public Builder getParamsInterceptor(Interceptor interceptor) {
            mHttpConfig.getParamsInterceptor = interceptor;
            return this;
        }
    }

    /**
     * 在拦截器中获取接口请求的所有参数
     * @param chain
     * @return
     */
    public static Map<String, String> getRequestParams(@NotNull Interceptor.Chain chain) {

        Request oldRequest = chain.request();

        // 获取接口请求的所有参数
        Map<String, String> signParams = new HashMap<>();
        // 头文件的字段
        Headers headers = oldRequest.headers();
        for (int i = 0; i < headers.size(); i++) {
            signParams.put(headers.name(i), headers.value(i));
        }
        // body里面的字段
        if (oldRequest.method().equals("POST")) {
            if (oldRequest.body() instanceof FormBody) {
                FormBody formBody = (FormBody) oldRequest.body();
                for (int i = 0; i < formBody.size(); i++) {
                    signParams.put(formBody.encodedName(i), formBody.encodedValue(i));
                }
            } else if (oldRequest.body() instanceof MultipartBody) {
                MultipartBody requestBody = (MultipartBody) oldRequest.body();
                MultipartBody.Builder multipartBodybuilder = new MultipartBody.Builder();
                for (int i = 0; i < requestBody.size(); i++) {
                    MultipartBody.Part part = requestBody.part(i);
                    multipartBodybuilder.addPart(part);
                    MediaType mediaType = part.body().contentType();
                    if (mediaType != null) {
                        String normalParamKey;
                        String normalParamValue;
                        try {
                            normalParamValue = getParamContent(requestBody.part(i).body());
                            Headers headerParam = part.headers();
                            if (!TextUtils.isEmpty(normalParamValue) && headerParam != null) {
                                for (String name : headerParam.names()) {
                                    String headerContent = headerParam.get(name);
                                    if (!TextUtils.isEmpty(headerContent)) {
                                        String[] normalParamKeyContainer = headerContent.split("name=\"");
                                        if (normalParamKeyContainer.length == 2) {
                                            normalParamKey = normalParamKeyContainer[1].split("\"")[0];
                                            signParams.put(normalParamKey, normalParamValue);
                                            break;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return signParams;
    }

    private static String getParamContent(RequestBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readUtf8();
    }
}
