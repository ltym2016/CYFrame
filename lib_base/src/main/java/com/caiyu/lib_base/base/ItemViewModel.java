package com.caiyu.lib_base.base;

import androidx.annotation.NonNull;

/**
 * @author luys
 * @describe
 * @date 2019-12-05
 * @email samluys@foxmail.com
 */
public class ItemViewModel<VM extends BaseViewModel> {
    protected transient VM viewModel;

    public ItemViewModel(@NonNull VM viewModel) {
        this.viewModel = viewModel;
    }
}
