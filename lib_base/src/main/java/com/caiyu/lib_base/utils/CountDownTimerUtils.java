package com.caiyu.lib_base.utils;

import android.graphics.Color;
import android.os.CountDownTimer;

import com.caiyu.lib_base.view.UIButton;

/**
 * @author :liyang
 * @date :2019-04-23:下午 2:34
 * 说明：
 */
public class CountDownTimerUtils extends CountDownTimer {
    private UIButton mUIButton;

    /**
     * @param uiButton          The TextView
     *
     *
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receiver
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTimerUtils(UIButton uiButton, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mUIButton = uiButton;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mUIButton.setClickable(false); //设置不可点击
        mUIButton.setText(millisUntilFinished / 1000 + "秒后重发");  //设置倒计时时间
        mUIButton.setTextColor(Color.parseColor("#999999")); //设置按钮为灰色，这时是不能点击的
        mUIButton.setQfSolidColor(Color.parseColor("#E3E3E3"));

//        /**
//         * 超链接 URLSpan
//         * 文字背景颜色 BackgroundColorSpan
//         * 文字颜色 ForegroundColorSpan
//         * 字体大小 AbsoluteSizeSpan
//         * 粗体、斜体 StyleSpan
//         * 删除线 StrikethroughSpan
//         * 下划线 UnderlineSpan
//         * 图片 ImageSpan
//         * http://blog.csdn.net/ah200614435/article/details/7914459
//         */
//        SpannableString spannableString = new SpannableString(mUIButton.getText().toString());  //获取按钮上的文字
//        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
//        /**
//         * public void setSpan(Object what, int start, int end, int flags) {
//         * 主要是start跟end，start是起始位置,无论中英文，都算一个。
//         * 从0开始计算起。end是结束位置，所以处理的文字，包含开始位置，但不包含结束位置。
//         */
//        spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//将倒计时的时间设置为红色
//        mUIButton.setText(spannableString);
    }

    @Override
    public void onFinish() {
        mUIButton.setText("重新获取验证码");
        mUIButton.setClickable(true);//重新获得点击
        mUIButton.setTextColor(Color.parseColor("#FFFFFF"));  //还原背景色
        mUIButton.setQfSolidColor(Color.parseColor("#FF8054"));
    }
}
