package com.caiyu.lib_base.http;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author luys
 * @describe 接口返回结果拦截处理，防止返回的data数据类型不一致导致报错或者崩溃
 * @date 2019-10-23
 * @email samluys@foxmail.com
 */
public class ResponseInterceptor extends ResponseBodyInterceptor {

    @Override
    Response intercept(@NotNull Response response, String url, String body) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(body);
            String data = jsonObject.getString("data");
            // 这里对接口可能返回的data类型，无数据的情况做统一处理
            if (TextUtils.isEmpty(data) || "[]".equals(data) || "{}".equals(data)) {
                jsonObject.put("data", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (response.body() != null) {
            MediaType contentType = response.body().contentType();
            ResponseBody responseBody = ResponseBody.create(contentType, jsonObject.toString());
            return response.newBuilder().body(responseBody).build();
        }

        return response;
    }
}
