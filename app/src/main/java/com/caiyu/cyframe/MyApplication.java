package com.caiyu.cyframe;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.caiyu.lib_base.http.HttpUtils;
import com.samluys.jutils.Utils;
import com.samluys.jutils.log.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

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
                .timeout(15)
                .addHeaderInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        // 这里可以添加请求头
                        Request.Builder builder = chain.request().newBuilder();
                        builder.addHeader("clientid", "3");
                        Request mRequest = builder.build();
                        return chain.proceed(mRequest);
                    }
                })
                .getParamsInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                // 打印所有的请求参数
                if (Utils.isDebug()) {
                    Map<String, String> paramsMap = HttpUtils.getRequestParams(chain);
                    StringBuffer sbDebug = new StringBuffer();
                    for(Map.Entry<String, String> entry : paramsMap.entrySet()){
                        sbDebug.append(entry.getKey()+"=" + entry.getValue()).append("\n");
                    }
                    LogUtils.d("***请求参数*** ",sbDebug.toString());
                }
                return chain.proceed(chain.request());
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
