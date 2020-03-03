package com.caiyu.cyframe;

import android.app.Application;

import androidx.annotation.NonNull;

import com.caiyu.cyframe.list.BlackListFragment;
import com.caiyu.cyframe.login.LoginActivity;
import com.caiyu.lib_base.base.BaseViewModel;
import com.caiyu.lib_base.callback.BindingAction;
import com.caiyu.lib_base.callback.BindingCommand;

/**
 * @author luys
 * @describe
 * @date 2020/3/1
 * @email samluys@foxmail.com
 */
public class MainViewModel extends BaseViewModel {
    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public BindingCommand jumpLoginCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startActivity(LoginActivity.class);
        }
    });

    public BindingCommand jumpListCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startContainerActivity(BlackListFragment.class.getCanonicalName());
        }
    });
}
