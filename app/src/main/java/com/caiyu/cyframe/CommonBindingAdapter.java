package com.caiyu.cyframe;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import com.caiyu.lib_base.bindingadapter.ViewBindingAdapter;
import com.caiyu.lib_base.callback.BindingCommand;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

/**
 * @author luys
 * @describe
 * @date 2019-11-18
 * @email samluys@foxmail.com
 */
public class CommonBindingAdapter extends ViewBindingAdapter {

    @BindingAdapter({"onRefreshCommand"})
    public static void onRefreshCommand(SmartRefreshLayout smartRefreshLayout, final BindingCommand onRefreshCommand) {
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (onRefreshCommand != null) {
                    onRefreshCommand.execute();
                }
            }
        });
    }

    @BindingAdapter({"onLoadMoreCommand"})
    public static void onLoadMoreCommand(SmartRefreshLayout smartRefreshLayout, final BindingCommand onLoadMoreCommand) {
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (onLoadMoreCommand != null) {
                    onLoadMoreCommand.execute();
                }
            }
        });
    }


}
