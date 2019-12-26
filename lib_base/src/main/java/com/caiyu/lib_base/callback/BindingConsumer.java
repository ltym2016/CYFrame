package com.caiyu.lib_base.callback;

/**
 * @author luys
 * @describe 一个参数的Action
 * @date 2019/4/27
 * @email ltym_lys@126.com
 */
public interface BindingConsumer<T> {
    void call(T t);
}
