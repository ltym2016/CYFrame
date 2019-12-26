package com.caiyu.lib_base.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * Created by Administrator on 2016/6/14 0014.
 */
public class TintUtils {

    public static Drawable getTintDrawable(Drawable drawable, @ColorInt int color) {
        Drawable.ConstantState state = drawable.getConstantState();
        Drawable drawable1 = DrawableCompat.wrap(state == null ? drawable : state.newDrawable()).mutate();
        drawable1.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        DrawableCompat.setTint(drawable1, color);
        return drawable1;
    }

    public static Drawable getTintDrawable(Context context, int id, int color) {
        Drawable originDrawable = ContextCompat.getDrawable(context, id);
        if (originDrawable != null) {
            return getTintDrawable(originDrawable, color);
        }
        return null;
    }
}
