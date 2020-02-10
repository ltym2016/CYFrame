package com.caiyu.lib_base.http;

import android.text.TextUtils;


import com.caiyu.lib_base.R;
import com.samluys.jutils.MD5Utils;
import com.samluys.jutils.Utils;
import com.samluys.jutils.log.LogUtils;

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
import okhttp3.Response;
import okio.Buffer;

/**
 * @author luys
 * @describe
 * @date 2019-07-02
 * @email samluys@foxmail.com
 */
public class AddParameterInterceptor implements Interceptor {

    @Override
    public Response intercept(@NotNull Chain chain) {
        try {
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
                                Headers headerparam = part.headers();
                                if (!TextUtils.isEmpty(normalParamValue) && headerparam != null) {
                                    for (String name : headerparam.names()) {
                                        String headerContent = headerparam.get(name);
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

            // 对请求到的参数排序
            List<Map.Entry<String,String>> list = new ArrayList<Map.Entry<String,String>>(signParams.entrySet());
            Collections.sort(list,new Comparator<Map.Entry<String,String>>() {
                //升序排序
                public int compare(Map.Entry<String, String> o1,
                                   Map.Entry<String, String> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }

            });

            // 拼接字符串
            StringBuffer sb = new StringBuffer();
            StringBuffer sbDebug = new StringBuffer();
            for(Map.Entry<String,String> mapping:list){
                sb.append(mapping.getKey()+"=" + mapping.getValue()).append("&");
                sbDebug.append(mapping.getKey()+"=" + mapping.getValue()).append("\n");
            }
            LogUtils.d("***请求参数***",sbDebug.toString());

            if (HttpConfig.getInstance().isSign) {
//                sb.append("key="+ Utils.getStringFromConfig(R.string.md5_key));
//                // MD5加密并转成大写
//                String md5String = MD5Utils.encryptMD5ToString(sb.toString()).toUpperCase();
//                // 将加密后的参数赋值给sign加到请求参数里
//                Request mRequest = chain.request().newBuilder()
//                        .addHeader("sign", md5String)
//                        .build();
//                return chain.proceed(mRequest);
            } else {
                return chain.proceed(chain.request());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getParamContent(RequestBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readUtf8();
    }
}
