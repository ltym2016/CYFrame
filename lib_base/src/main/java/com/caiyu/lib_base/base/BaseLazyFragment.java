package com.caiyu.lib_base.base;


import androidx.databinding.ViewDataBinding;

/**
 * @author luys
 * @describe
 * @date 2019-12-13
 * @email samluys@foxmail.com
 */
public abstract class BaseLazyFragment<V extends ViewDataBinding, VM extends BaseViewModel>
        extends BaseFragment<V, VM> {



    private boolean isPrepared;
    /**
     * 第一次onResume中的调用onUserVisible避免操作与onFirstUserVisible操作重复
     */
    private boolean isFirstResume = true;
    protected boolean isFirstVisible = true;
    private boolean isFirstInvisible = true;

    @Override
    public void initData() {
        super.initData();
        initPrepare();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            onUserVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserInvisible();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFirstResume=true;
        isFirstVisible=true;
        isPrepared=false;
        isFirstInvisible=true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            } else {
                onUserVisible();
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisible();
            } else {
                onUserInvisible();
            }
        }
    }

    public synchronized void initPrepare() {
        if (isPrepared) {
            if (getActivity()!=null) {
                onFirstUserVisible();
            }
        } else {
            isPrepared = true;
        }
    }

    /**
     * 第一次fragment可见（进行初始化工作）
     */
    public abstract void onFirstUserVisible() ;

    /**
     * fragment可见（切换回来或者onResume）
     */
    public void onUserVisible() {

    }

    /**
     * 第一次fragment不可见（不建议在此处理事件）
     */
    public void onFirstUserInvisible() {

    }

    /**
     * fragment不可见（切换掉或者onPause）
     */
    public void onUserInvisible() {

    }
}
