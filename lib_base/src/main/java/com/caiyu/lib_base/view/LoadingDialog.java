package com.caiyu.lib_base.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.caiyu.lib_base.R;
import com.samluys.statusbar.StatusBarUtils;


/**
 * @author luys
 * @describe LoadingDialog
 * @date 2019-07-18
 * @email samluys@foxmail.com
 */
public class LoadingDialog extends Dialog {

    private Context mContext;
    private ViewGroup contentWrap;
    private TextView tipView;

    public LoadingDialog(@NonNull Context context) {
        this(context, R.style.loadingDialog);
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setCanceledOnTouchOutside(false);
        mContext = context;
        initDialogWidth();
    }

    private void initDialogWidth() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wmLp = window.getAttributes();
            wmLp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setAttributes(wmLp);
        }

        setContentView(R.layout.loading_dialog_layout);
        contentWrap = findViewById(R.id.contentWrap);
        CustomLoadingView loadingView = new CustomLoadingView(mContext);
        loadingView.setColor(Color.WHITE);
        loadingView.setSize(StatusBarUtils.dp2px(mContext, 32));
        LinearLayout.LayoutParams loadingViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingView.setLayoutParams(loadingViewLP);
        contentWrap.addView(loadingView);
    }

    public void showDialog(String mTipWord) {
        if (mTipWord != null && mTipWord.length() > 0) {
            if (tipView == null) {
                tipView = new TextView(mContext);
                tipView.setTag(1);
            }

            LinearLayout.LayoutParams tipViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tipViewLP.topMargin = StatusBarUtils.dp2px(mContext, 12);
            tipView.setLayoutParams(tipViewLP);

            tipView.setEllipsize(TextUtils.TruncateAt.END);
            tipView.setGravity(Gravity.CENTER);
            tipView.setMaxLines(2);
            tipView.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
            tipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            tipView.setText(mTipWord);

            if (contentWrap != null && contentWrap.findViewWithTag(1) == null) {
                contentWrap.addView(tipView);
            }

            show();
        }
    }

    /**
     * 生成默认的 {@link LoadingDialog}
     * <p>
     * 提供了一个图标和一行文字的样式, 其中图标有几种类型可选。
     * </p>
     */
    public static class Builder {

        private Context mContext;

        private CharSequence mTipWord;

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * 设置显示的文案
         */
        public Builder setTipWord(CharSequence tipWord) {
            mTipWord = tipWord;
            return this;
        }

        public LoadingDialog create(){
            return create(true);
        }

        /**
         * 创建 Dialog, 但没有弹出来, 如果要弹出来, 请调用返回值的 {@link Dialog#show()} 方法
         *
         * @param cancelable 按系统返回键是否可以取消
         * @return 创建的 Dialog
         */
        public LoadingDialog create(boolean cancelable) {
            LoadingDialog dialog = new LoadingDialog(mContext);
            dialog.setCancelable(cancelable);
            dialog.setContentView(R.layout.loading_dialog_layout);
            ViewGroup contentWrap = (ViewGroup) dialog.findViewById(R.id.contentWrap);

            CustomLoadingView loadingView = new CustomLoadingView(mContext);
            loadingView.setColor(Color.WHITE);
            loadingView.setSize(StatusBarUtils.dp2px(mContext, 32));
            LinearLayout.LayoutParams loadingViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            loadingView.setLayoutParams(loadingViewLP);
            contentWrap.addView(loadingView);


            if (mTipWord != null && mTipWord.length() > 0) {
                TextView tipView = new TextView(mContext);
                LinearLayout.LayoutParams tipViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tipViewLP.topMargin = StatusBarUtils.dp2px(mContext, 12);
                tipView.setLayoutParams(tipViewLP);

                tipView.setEllipsize(TextUtils.TruncateAt.END);
                tipView.setGravity(Gravity.CENTER);
                tipView.setMaxLines(2);
                tipView.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
                tipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tipView.setText(mTipWord);

                contentWrap.addView(tipView);
            }
            return dialog;
        }

    }
}
