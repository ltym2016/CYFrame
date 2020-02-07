package com.caiyu.lib_base.http;

import android.accounts.NetworkErrorException;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonSyntaxException;
import com.samluys.jutils.ToastUtils;
import com.samluys.jutils.log.LogUtils;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import retrofit2.HttpException;

/**
 * @author luys
 * @describe
 * @date 2019-07-30
 * @email samluys@foxmail.com
 */
public class ExceptionManger {

    public static int handleException (Throwable e) {

        int errCode = 0;

        try {
            String errMsg="";
            if (e instanceof ConnectException
                    || e instanceof TimeoutException
                    || e instanceof NetworkErrorException
                    || e instanceof UnknownHostException) {
                errMsg = "网络连接异常";
                errCode = 1;
            } else if (e instanceof JsonSyntaxException
                    || e instanceof NullPointerException) {
                errMsg = "数据解析异常";
            } else {
                if (e instanceof HttpException) {
                    HttpException httpException = (HttpException)e;

                    if (httpException.code() == 500) {
                        errMsg = "服务器异常 code : " + 500;
                        errCode = 2;
                        LogUtils.e(httpException.code() + "=====" + httpException.response().errorBody().string());
                    } else {
                        String content = httpException.response().errorBody().string();
                        BaseResponse response = JSON.parseObject(content, BaseResponse.class);
                        switch (response.getCode()) {
                            case 401:// 401 代表需要跳转到登录接口
                                errCode = 401;
                                break;
                        }
                    }
                } else {
                    errMsg = "加载数据失败，请稍后再试";
                    e.printStackTrace();
                }
            }
            if (!TextUtils.isEmpty(errMsg)) {
                ToastUtils.showShort(errMsg);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            LogUtils.e("加载数据失败，请稍后再试");
            ToastUtils.showShort("加载数据失败，请稍后再试");
        }

        return errCode;
    }
}
