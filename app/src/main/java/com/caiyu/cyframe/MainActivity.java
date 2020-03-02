package com.caiyu.cyframe;

import com.caiyu.cyframe.databinding.ActivityMainBinding;
import com.caiyu.lib_base.base.BaseActivity;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {

    @Override
    protected void setOrientation() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }
}
