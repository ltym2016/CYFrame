package com.caiyu.cyframe.list;

import android.app.Application;

import androidx.annotation.NonNull;

import com.caiyu.cyframe.R;
import com.caiyu.lib_base.base.BaseBindingItem;
import com.caiyu.lib_base.base.BaseViewModel;
import com.caiyu.lib_base.callback.BindingAction;
import com.caiyu.lib_base.callback.BindingCommand;
import com.samluys.jutils.NetworkUtils;
import com.samluys.jutils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class BlackListViewModel extends BaseViewModel {

    private int mPage = 1;
    private List<BlackListEntity> mList;

    public BlackListViewModel(@NonNull Application application) {
        super(application);
        titleName.set("黑名单");

        mList = new ArrayList<>();
    }

    public BindingCommand onLoadMoreCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (NetworkUtils.isConnected()) {
                mPage++;
                getData();
            } else {
                ToastUtils.showLong(R.string.no_net_remind);
            }
        }
    });

    public List<BaseBindingItem> getData() {

        List<BaseBindingItem> mList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            if (i % 5 ==0) {
                mList.add(new BlackList2Entity());
            } else {
                mList.add(new BlackListEntity());
            }

        }

        return mList;
    }

    public List<BlackListEntity> getList() {
        return mList;
    }

    public void setPage(int mPage) {
        this.mPage = mPage;
    }

    public int getPage() {
        return mPage;
    }



}