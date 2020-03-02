package com.caiyu.cyframe.login;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.caiyu.lib_base.base.BaseViewModel;
import com.caiyu.lib_base.callback.BindingAction;
import com.caiyu.lib_base.callback.BindingCommand;
import com.samluys.jutils.ToastUtils;

/**
 * @author luys
 * @describe
 * @date 2019-11-15
 * @email samluys@foxmail.com
 */
public class LoginViewModel extends BaseViewModel {
    public ObservableField<String> phone;
    public ObservableField<String> code;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        phone = new ObservableField<>();
        code = new ObservableField<>();
    }


    public BindingCommand loginCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (TextUtils.isEmpty(phone.get())) {
                ToastUtils.showLong("请输入手机号码");
                return;
            }

            if (TextUtils.isEmpty(code.get())) {
                ToastUtils.showLong("请随便输入验证码");
                return;
            }
            ToastUtils.showLong("登录成功");
        }
    });


    /**
     * QQ登录
     */
    public BindingCommand qqLoginCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ToastUtils.showLong("QQ登录");
        }
    });

    /**
     * 发送验证码
     */
    public BindingCommand sendCodeCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (TextUtils.isEmpty(phone.get())) {
                ToastUtils.showLong("请输入手机号码");
                return;
            }
            ToastUtils.showLong("发送验证码成功");
        }
    });

    /**
     * 微信登录
     */
    public BindingCommand wechatLoginCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ToastUtils.showLong("微信登录");
        }
    });

}
