package com.caiyu.lib_base.base;

import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;

import com.caiyu.lib_base.BR;
import com.caiyu.lib_base.R;
import com.caiyu.lib_base.databinding.FragmentBasePagerBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

/**
 * @author luys
 * @describe
 * @date 2019/4/25
 * @email samluys@foxmail.com
 */
public abstract class BasePagerFragment extends BaseFragment<FragmentBasePagerBinding, BaseViewModel> {

    private List<Fragment> mFragments;
    private List<String> titlePager;
    private int tab=0;
    protected abstract List<Fragment> pagerFragment();

    protected abstract List<String> pagerTitleString();

    @Override
    public int initContentView() {
        return R.layout.fragment_base_pager;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        init();

        mFragments = pagerFragment();
        titlePager = pagerTitleString();
        viewModel.titleName = setTitleName();
        //设置Adapter
        BaseFragmentPagerAdapter pagerAdapter = new BaseFragmentPagerAdapter(getChildFragmentManager(), mFragments, titlePager);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.tabs.setupWithViewPager(binding.viewPager);
        binding.viewPager.setCurrentItem(getCurrentTab());
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabs));
    }

    protected abstract void init();
    protected  void setCurrentTab(int tab){
        this.tab=tab;
    }

    protected  int getCurrentTab(){
        return tab;
    }

    protected abstract ObservableField<String> setTitleName();
}
