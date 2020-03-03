package com.caiyu.cyframe.list;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.caiyu.cyframe.BR;
import com.caiyu.cyframe.R;
import com.caiyu.cyframe.databinding.FragmentBlackListBinding;
import com.caiyu.lib_base.base.BaseBindingAdapter;
import com.caiyu.lib_base.base.BaseBindingItem;
import com.caiyu.lib_base.base.BaseFragment;
import com.caiyu.lib_base.base.OnViewClickListener;
import com.samluys.jutils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luys
 * @describe 屏蔽名单
 * @date 2019-11-20
 * @email samluys@foxmail.com
 */
public class BlackListFragment extends BaseFragment<FragmentBlackListBinding, BlackListViewModel> {

    private BaseBindingAdapter mAdapter;
    private List<BaseBindingItem> mList;

    public static BlackListFragment newInstance() {
        return new BlackListFragment();
    }


    @Override
    public void initParam() {
        super.initParam();
    }

    @Override
    public int initContentView() {
        return R.layout.fragment_black_list;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
//        mHelper.showLoading();
        mList = new ArrayList<>();
        mList.addAll(viewModel.getData());
        mAdapter = new BaseBindingAdapter(new OnViewClickListener() {
            @Override
            public void onViewClick(View view, BaseBindingItem item) {
                int index = mList.indexOf(item);
                if (item instanceof BlackListEntity) {
                    BlackListEntity entity = (BlackListEntity) item;
                    ToastUtils.showLong("position:"+index);
                }
            }
        });
        mAdapter.setList(mList);
        binding.rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvContent.setAdapter(mAdapter);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onLoadRetry() {
        super.onLoadRetry();
        mList.clear();
        binding.srfRefresh.setEnableLoadMore(true);
        viewModel.setPage(1);
        viewModel.getData();
    }
}
