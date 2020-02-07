package com.caiyu.lib_base.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caiyu.lib_base.R;
import com.samluys.jutils.NetworkUtils;
import com.samluys.jutils.log.LogUtils;


/**
 * @author luys
 * @describe
 * @date 2019/3/28
 * @email samluys@foxmail.com
 */
public class LoadingView {

    /**
     * 加载中
     */
    public static final int STATUS_LOADING = 1;
    /**
     * 加载成功
     */
    public static final int STATUS_LOAD_SUCCESS = 2;
    /**
     * 加载失败
     */
    public static final int STATUS_LOAD_FAILED = 3;
    /**
     * 加载为空
     */
    public static final int STATUS_EMPTY_DATA = 4;

    /**
     * 没有网路
     */
    public static final int STATUS_NO_NET = 5;

    private static volatile LoadingView instance;

    private LoadingView() {

    }

    public static LoadingView getInstance() {
        if (instance == null) {
            synchronized (LoadingView.class) {
                if (instance == null) {
                    instance = new LoadingView();
                }
            }
        }
        return instance;
    }

    public LoadingHelper wrap(Activity activity) {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View loadingView = LayoutInflater.from(activity).inflate(R.layout.layout_loading, null);
        return new LoadingHelper(activity, loadingView, viewGroup);
    }

    public LoadingHelper wrap(ViewGroup viewGroup) {
        View loadingView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading, viewGroup, false);
        return new LoadingHelper(viewGroup.getContext(), loadingView, viewGroup);
    }


    public class LoadingHelper {

        private Context mContext;
        private View mLoadingView;
        private ViewGroup mViewGroup;
        private int mCurState;
        private int mToolbarHeight;
        private Runnable mRetryTask;

        public LoadingHelper(Context context, View loadingView, ViewGroup viewGroup) {
            this.mContext = context;
            this.mLoadingView = loadingView;
            this.mViewGroup = viewGroup;
        }

        public LoadingHelper withRetry(Runnable task) {
            mRetryTask = task;
            return this;
        }

        public void setBackground(int colorId) {
            mLoadingView.setBackgroundColor(mContext.getResources().getColor(colorId));
        }

        /**
         * ================显示加载中=================
         */
        public void showLoading() {
            showLoadingStatus(STATUS_LOADING, null, 0);
        }

        public void showLoading(String msg) {
            showLoadingStatus(STATUS_LOADING, msg, 0);
        }

        public void showLoading(int imageId) {
            showLoadingStatus(STATUS_LOADING, null, imageId);
        }

        public void showLoading(String msg, int imageId) {
            showLoadingStatus(STATUS_LOADING, msg, imageId);
        }


        /**
         * ================加载成功=================
         */
        public void showLoadingSuccess() {
            showLoadingStatus(STATUS_LOAD_SUCCESS, null, 0);
        }


        /**
         * ================加载失败=================
         */
        public void showLoadingFail() {
            showLoadingStatus(STATUS_LOAD_FAILED, null, 0);
        }

        public void showLoadingFail(String msg) {
            showLoadingStatus(STATUS_LOAD_FAILED, msg, 0);
        }

        public void showLoadingFail(int imageId) {
            showLoadingStatus(STATUS_LOAD_FAILED, null, imageId);
        }

        public void showLoadingFail(String msg, int imageId) {
            showLoadingStatus(STATUS_LOAD_FAILED, msg, imageId);
        }


        /**
         * ================空数据=================
         */
        public void showEmpty(String msg, int imageId) {
            showLoadingStatus(STATUS_EMPTY_DATA, msg, imageId);
        }

        public void showEmpty(int imageId) {
            showLoadingStatus(STATUS_EMPTY_DATA, null, imageId);
        }

        public void showEmpty(String msg) {
            showLoadingStatus(STATUS_EMPTY_DATA, msg, 0);
        }



        public void showEmpty() {
            String noData = mContext.getResources().getString(R.string.no_data);
            showLoadingStatus(STATUS_EMPTY_DATA, noData, 0);
        }

        /**
         * ================空数据=================
         */
        public void showNoNet(String msg, int imageId) {
            showLoadingStatus(STATUS_NO_NET, msg, imageId);
        }

        public void showNoNet(int imageId) {
            showLoadingStatus(STATUS_NO_NET, null, imageId);
        }

        public void showNoNet(String msg) {
            showLoadingStatus(STATUS_NO_NET, msg, 0);
        }

        public void showNoNet() {
            showLoadingStatus(STATUS_NO_NET, null, 0);
        }


        private void showLoadingStatus(int status, String msg, int imageId) {

            if (!NetworkUtils.isConnected()) {
                LogUtils.e("网络不可用");
                status = STATUS_NO_NET;
            }

            if (mCurState == status) {
                return;
            }
            mCurState = status;

            try {

                ImageView iv_loading = mLoadingView.findViewById(R.id.iv_loading);

                // 加载动画
                Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                        mContext, R.anim.load_progress_animation);
                // 使用ImageView显示动画
                LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
                hyperspaceJumpAnimation.setInterpolator(lin);
                iv_loading.startAnimation(hyperspaceJumpAnimation);

                LinearLayout ll_loading_fail = mLoadingView.findViewById(R.id.ll_loading_fail);
                Button btn_tryAgain = mLoadingView.findViewById(R.id.btn_tryAgain);
                TextView tv_text = mLoadingView.findViewById(R.id.tv_text);
                ImageView iv_status_icon = mLoadingView.findViewById(R.id.iv_status_icon);
                LinearLayout ll_empty = mLoadingView.findViewById(R.id.ll_empty);
                TextView tv_empty_text = mLoadingView.findViewById(R.id.tv_empty_text);
                ImageView iv_empty = mLoadingView.findViewById(R.id.iv_empty);
                Button btn_refresh=mLoadingView.findViewById(R.id.btn_refresh);

                // 设置图片
                mViewGroup.removeView(mLoadingView);
                mLoadingView.setClickable(true);
                mViewGroup.addView(mLoadingView);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mLoadingView.getLayoutParams();
                if (lp != null) {
                    lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    lp.topMargin = mToolbarHeight;
                }

                if (mViewGroup.indexOfChild(mLoadingView) != mViewGroup.getChildCount() - 1) {
                    mLoadingView.bringToFront();
                }

                if (status == STATUS_LOAD_SUCCESS) {
                    mLoadingView.setVisibility(View.GONE);
                } else {
                    mLoadingView.setVisibility(View.VISIBLE);
                }

                if (status == STATUS_LOAD_FAILED) {
                    // 服务器异常
                    iv_loading.setVisibility(View.GONE);
                    ll_loading_fail.setVisibility(View.VISIBLE);

                    if (!TextUtils.isEmpty(msg)) {
                        tv_text.setText(setSpanText(mContext.getResources().getString(R.string.loading_fail_msg, msg)));
                    } else {
                        tv_text.setText(setSpanText(mContext.getResources().getString(R.string.loading_fail)));
                    }


                    iv_status_icon.setImageResource(R.drawable.ic_loading_fail);
                    btn_tryAgain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mRetryTask != null) {
                                mRetryTask.run();
                            }
                        }
                    });
                } else if (status == STATUS_NO_NET) {
                    // 网络异常
                    iv_loading.setVisibility(View.GONE);
                    ll_loading_fail.setVisibility(View.VISIBLE);
                    tv_text.setText(setSpanText(mContext.getResources().getString(R.string.no_net)));
                    iv_status_icon.setImageResource(R.drawable.ic_no_net);
                    btn_tryAgain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mRetryTask != null) {
                                mRetryTask.run();
                            }
                        }
                    });
                } else if (status == STATUS_EMPTY_DATA) {
                    iv_loading.setVisibility(View.GONE);
                    ll_loading_fail.setVisibility(View.GONE);
                    ll_empty.setVisibility(View.VISIBLE);
                    iv_empty.setVisibility(View.VISIBLE);
                    if (imageId != 0) {
                        iv_empty.setImageResource(imageId);
                    }

                    tv_empty_text.setText(setSpanText(msg));
                    btn_refresh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mRetryTask != null) {
                                mRetryTask.run();
                            }
                        }
                    });
                } else {
                    ll_loading_fail.setVisibility(View.GONE);
                    ll_empty.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Context getContext() {
            return mContext;
        }

        public LoadingHelper toobarHeight(int height) {
            mToolbarHeight = height;
            return this;
        }

        private SpannableString setSpanText(String text) {

            if (TextUtils.isEmpty(text)) {
                return new SpannableString("");
            } else if (!text.contains("\n")) {
                return new SpannableString(text);
            }

            SpannableString spannableString = new SpannableString(text);
            int endIndex = text.indexOf("\n");
            // 颜色#333333
            ForegroundColorSpan userNameColor = new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_333333));
            spannableString.setSpan(userNameColor, 0, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 粗体
            StyleSpan span = new StyleSpan(Typeface.BOLD);
            spannableString.setSpan(span, 0, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            return spannableString;
        }
    }
}
