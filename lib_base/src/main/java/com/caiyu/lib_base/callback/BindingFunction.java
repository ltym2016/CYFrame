package com.caiyu.lib_base.callback;

/**
 * @author luys
 * @describe 无参数有返回值的Action
 * @date 2019/4/27
 * @email ltym_lys@126.com
 */
public interface BindingFunction<T> {
    T call();
}
