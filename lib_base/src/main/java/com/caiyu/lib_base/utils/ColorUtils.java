package com.caiyu.lib_base.utils;

import android.graphics.Color;

/**
 * @author luys
 * @describe
 * @date 2019-11-20
 * @email samluys@foxmail.com
 */
public class ColorUtils {

    /**
     * 获取带透明度的颜色
     *
     * @param color 不带透明度的颜色
     * @param alpha 透明度 0-1.0f
     * @return
     */
    public static int getAlphaColor(int color, float alpha) {
        int mAlpha = (int) (255 * alpha);
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        return Color.argb(mAlpha, red, green, blue);
    }
}
