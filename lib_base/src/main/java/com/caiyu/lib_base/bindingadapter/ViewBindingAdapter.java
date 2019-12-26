package com.caiyu.lib_base.bindingadapter;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.caiyu.lib_base.callback.BindingCommand;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * @author luys
 * @describe 系统View的BindingAdapter的调用汇总
 * @date 2019/4/27
 * @email samluys@foxmail.com
 */
public class ViewBindingAdapter {

    // 防重复点击间隔(秒)
    public static final int CLICK_INTERVAL = 1;

    /**
     * 普通View 的点击事件
     *
     * @param view         普票view （Button，TextView...）
     * @param clickCommand 绑定的命令
     */
    @SuppressLint("CheckResult")
    @BindingAdapter(value = {"onClickCommand"})
    public static void onClickCommand(View view,
                                      final BindingCommand clickCommand) {

        RxView.clicks(view)
                .throttleFirst(CLICK_INTERVAL, TimeUnit.SECONDS)//1秒钟内只允许点击1次
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object unit) throws Exception {
                        if (clickCommand != null) {
                            clickCommand.execute();
                        }
                    }
                });
    }

    /**
     * 普通View 的点击事件
     *
     * @param view         普票view （Button，TextView...）
     * @param clickCommand 绑定的命令
     */
    @BindingAdapter(value = {"onClickScaleCommand"})
    public static void onClickScaleCommand(ImageView view,
                                           final BindingCommand clickCommand) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://收缩到0.8(正常值是1)，速度100
                        v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                        v.animate().scaleX(1).scaleY(1).setDuration(100).start();
                        if (clickCommand != null) {
                            clickCommand.execute();
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 动态设置字体颜色
     *
     * @param textView
     * @param colorId
     */
    @BindingAdapter({"txtColor"})
    public static void setTxtColor(TextView textView, int colorId) {
        if (colorId > 0) {
            textView.setTextColor(textView.getContext().getResources().getColor(colorId));
        }
    }

    /**
     * 图片动态加载本地资源
     *
     * @param imageView
     * @param resId
     */
    @BindingAdapter({"android:src"})
    public static void setImageRes(ImageView imageView, int resId) {
        if (resId > 0) {
            imageView.setImageResource(resId);
        }
    }

}
