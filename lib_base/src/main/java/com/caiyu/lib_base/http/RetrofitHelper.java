package com.caiyu.lib_base.http;

import com.caiyu.lib_base.constants.Constants;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * @author luys
 * @describe
 * @date 2019/4/17
 * @email ltym_lys@126.com
 */
public class RetrofitHelper extends RetrofitClientManager {


    private static <T> T getRetrofitApi(String url, Class<T> clazz) {
        return new RetrofitHelper().getRetrofit(url, clazz);
    }

    public static <T> T getApiService(Class<T> clazz) {
        return getRetrofitApi(HttpConfig.getInstance().host, clazz);
    }

    public static <T> T getUploadService(Class<T> clazz, String host) {
        return getRetrofitApi(host, clazz);
    }


    public static <T> Disposable subscript(Observable<T> observable, Consumer<T> action) {
        return subscript(observable, new Consumer<Disposable>() {
            @Override
            public void accept(Disposable t) throws Exception {

            }
        }, action, new ExceptionHandle(), new Action() {
            @Override
            public void run() throws Exception {

            }
        });
    }

    public static <T> Disposable subscript(Observable<T> observable, Consumer<T> action, Consumer<Throwable> e) {
        return subscript(observable, new Consumer<Disposable>() {
            @Override
            public void accept(Disposable t) throws Exception {

            }
        }, action, e, new Action() {
            @Override
            public void run() throws Exception {

            }
        });
    }

    public static <T> Disposable subscript(Observable<T> observable, Consumer<Disposable> start, Consumer<T> action, Action complete) {
        return subscript(observable, start, action, new ExceptionHandle(), complete);
    }

    public static <T> Disposable subscript(Observable<T> observable,
                                           Consumer<Disposable> start,
                                           Consumer<T> action,
                                           Consumer<Throwable> e,
                                           Action complete) {
        return observable
                .compose(RxUtils.<T>iOThreadScheduler())
                .doOnSubscribe(start)
                .subscribe(action, e, complete);
    }
}
