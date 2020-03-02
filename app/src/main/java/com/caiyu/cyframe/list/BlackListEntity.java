package com.caiyu.cyframe.list;

import com.caiyu.cyframe.R;
import com.caiyu.lib_base.base.BaseBindingItem;
import com.caiyu.lib_base.callback.BindingAction;
import com.caiyu.lib_base.callback.BindingCommand;

/**
 * @author luys
 * @describe 我的关注
 * @date 2019-11-20
 * @email samluys@foxmail.com
 */
public class BlackListEntity implements BaseBindingItem {


    /**
     * uid : 10000
     * avatar : http://c.hiphotos.baidu.com/image/pic/item/d1a20cf431adcbef74082900a3af2edda3cc9ff3.jpg
     * nickname : 甜圈10000
     * level_num : Lv1
     * gender : 1
     * introduction : 大家好我是小明
     */

    private int uid;
    private String avatar;
    private String nickname;
    private String level_num;
    private int gender;
    private String introduction;



    public BindingCommand personHomeCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {

        }
    });

    @Override
    public int getViewType() {
        return R.layout.item_black_list;
    }

    @Override
    public boolean isFooter() {
        return false;
    }

    @Override
    public boolean isHeader() {
        return false;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLevel_num() {
        return level_num;
    }

    public void setLevel_num(String level_num) {
        this.level_num = level_num;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
