package com.caiyu.cyframe.api;

import com.caiyu.lib_base.http.BaseResponse;

import io.reactivex.Observable;
import retrofit2.http.POST;

/**
 * @author luys
 * @describe
 * @date 2020-02-10
 * @email samluys@foxmail.com
 */
public interface ApiService {

    /**
     * 首页Tab
     *
     * @return 数据
     */
    @POST("circles/indexTag")
    Observable<BaseResponse> tabs();

    /**
     * 初始化配置 app默认配置文件
     *
     * @return 数据
     */
    @POST("common/appInit")
    Observable<BaseResponse> appInit();

}
