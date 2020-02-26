package com.caiyu.lib_base.http;

import com.caiyu.lib_base.constants.Constants;
import com.samluys.jutils.SPUtils;
import com.samluys.jutils.log.LogUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
