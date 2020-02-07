package com.caiyu.lib_base.http;

import io.reactivex.functions.Consumer;

/**
 * @author luys
 * @describe
 * @date 2019/4/15
 * @email ltym_lys@126.com
 */
public class ExceptionHandle implements Consumer<Throwable> {

    public ExceptionHandle() {

    }

    @Override
    public void accept(Throwable e) {
        ExceptionManger.handleException(e);
    }
}
