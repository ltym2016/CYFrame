package com.caiyu.cyframe.list


import com.caiyu.cyframe.R
import com.caiyu.cyframe.BR
import com.caiyu.cyframe.databinding.FragmentListBinding
import com.caiyu.lib_base.base.BaseFragment

class ListFragment : BaseFragment<FragmentListBinding, ListViewModel>() {


    override fun initContentView(): Int {
        return R.layout.fragment_list
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }


}
