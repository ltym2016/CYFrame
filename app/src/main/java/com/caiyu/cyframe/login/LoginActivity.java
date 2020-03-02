package com.caiyu.cyframe.login;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;


import com.caiyu.cyframe.BR;
import com.caiyu.cyframe.R;
import com.caiyu.cyframe.databinding.ActivityLoginBinding;
import com.caiyu.lib_base.base.BaseActivity;
import com.samluys.jutils.ToastUtils;
import com.samluys.jutils.Utils;
import com.samluys.jutils.log.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;


/**
 * @author luys
 * @describe 登录页
 * @date 2019-11-15
 * @email samluys@foxmail.com
 */
public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> {

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
    }

    @Override
    protected void setOrientation() {
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
