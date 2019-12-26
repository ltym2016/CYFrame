package com.caiyu.lib_base.base;

/**
 * @author luys
 * @describe
 * @date 2019/4/17
 * @email ltym_lys@126.com
 */
public interface IBaseView {
    /**
     * 初始化界面传递参数
     */
    void initParam();
    /**
     * 初始化数据
     */
    void initData();

    /**
     * 初始化界面观察者的监听
     */
    void initViewObservable();
}
