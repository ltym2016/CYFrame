package com.caiyu.lib_base.base;

import com.caiyu.lib_base.R;

/**
 * @author luys
 * @describe
 * @date 2019-05-23
 * @email samluys@foxmail.com
 */
public class NoMoreDataEntity implements BaseBindingItem {
    @Override
    public int getViewType() {
        return R.layout.item_bottom;
    }

    @Override
    public boolean isFooter() {
        return true;
    }

    @Override
    public boolean isHeader() {
        return false;
    }


}
