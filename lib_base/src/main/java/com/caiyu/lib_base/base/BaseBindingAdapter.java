package com.caiyu.lib_base.base;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luys
 * @describe
 * @date 2019/4/19
 * @email ltym_lys@126.com
 */
public class BaseBindingAdapter extends RecyclerView.Adapter<BaseBindingAdapter.BindViewHolder> {

    private List<BaseBindingItem> mList = new ArrayList<>();

    private OnItemClickListener onItemClickListener;
    private OnViewClickListener onViewClickListener;

    public BaseBindingAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public BaseBindingAdapter(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    public BaseBindingAdapter() {

    }

    @NonNull
    @Override
    public BindViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                viewType, parent, false);

        return new BindViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindViewHolder holder, final int position) {
        holder.bindData(mList.get(position), onItemClickListener, onViewClickListener, position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getViewType();
    }

    public List<BaseBindingItem> getList() {
        return mList;
    }

    public void setList(List<BaseBindingItem> mList) {
        this.mList = mList;
    }

    static class BindViewHolder extends RecyclerView.ViewHolder {

        ViewDataBinding binding;

        public BindViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(BaseBindingItem item,
                             OnItemClickListener onItemClickListener,
                             OnViewClickListener onViewClickListener,
                             int position) {
            binding.setVariable(com.caiyu.lib_base.BR.item, item);
            binding.setVariable(com.caiyu.lib_base.BR.position, position);
            binding.setVariable(com.caiyu.lib_base.BR.itemClick, onItemClickListener);
            binding.setVariable(com.caiyu.lib_base.BR.viewClick, onViewClickListener);
            binding.executePendingBindings();
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (mList.size() > 0 && mList.size() > position) {
                        BaseBindingItem item = mList.get(position);

                        return item.isFooter() ? gridLayoutManager.getSpanCount() : 1;
                    } else if (position == 0) {
                        BaseBindingItem item = mList.get(0);
                        return item.isHeader() ? gridLayoutManager.getSpanCount() : 1;
                    } else {
                        return 1;
                    }
                }
            });
        }
    }
}
