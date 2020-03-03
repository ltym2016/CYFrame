package com.caiyu.cyframe.list;

import com.caiyu.cyframe.R;
import com.caiyu.lib_base.base.BaseBindingItem;
import com.caiyu.lib_base.callback.BindingAction;
import com.caiyu.lib_base.callback.BindingCommand;
import com.samluys.jutils.ToastUtils;

/**
 * @author luys
 * @describe 我的关注
 * @date 2019-11-20
 * @email samluys@foxmail.com
 */
public class BlackList2Entity implements BaseBindingItem {

    @Override
    public int getViewType() {
        return R.layout.item_black_list2;
    }

    @Override
    public boolean isFooter() {
        return false;
    }

    @Override
    public boolean isHeader() {
        return false;
    }

}
