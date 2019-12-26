package com.caiyu.lib_photo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.caiyu.lib_base.imageloader.ImageLoader;
import com.caiyu.lib_base.utils.ApplicationUtils;
import com.caiyu.lib_base.utils.ScreenUtils;
import com.caiyu.lib_photo.R;
import com.caiyu.lib_photo.entity.SelectImageEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class PreviewPhotoRVAdapter extends RecyclerView.Adapter<PreviewPhotoRVAdapter.ViewHolder> {


    private OnItemClickListener onItemClickListener;

    private Context mContext;
    private List<SelectImageEntity> selectImageEntities;
    private int selectPosition;
    private int screenWidth;
    private int screenHeight;

    public PreviewPhotoRVAdapter(Context mContext, List<SelectImageEntity> selectImageEntities) {
        this.mContext = mContext;
        if (selectImageEntities != null) {
            this.selectImageEntities = selectImageEntities;
        } else {
            this.selectImageEntities = new ArrayList<>();
        }

        screenWidth = ScreenUtils.getScreenWidth(ApplicationUtils.getApp());
        screenHeight = ScreenUtils.getScreenHeight(ApplicationUtils.getApp());
    }

    public void setInfos(List<SelectImageEntity> infos) {
        if (infos != null) {
            this.selectImageEntities = infos;
            notifyDataSetChanged();
        }
    }

    public void setNowPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PreviewPhotoRVAdapter.ViewHolder viewHolder = new PreviewPhotoRVAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_previewphoto_rv_adapter, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String path = "" + selectImageEntities.get(position).getPath();
        if (selectPosition == position) {
            holder.view_biankuang.setVisibility(View.VISIBLE);
        } else {
            holder.view_biankuang.setVisibility(View.GONE);
        }
        if (!selectImageEntities.get(position).isChoose()) {
            // 图片未选中，显示蒙层
            holder.view_top.setVisibility(View.VISIBLE);
            holder.view_top.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_previewphoto_item));
        } else {
            holder.view_top.setVisibility(View.GONE);
        }
        ImageLoader.loadResize(holder.simpleDraweeView, path, screenWidth, screenHeight);
        holder.simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectImageEntities == null ? 0 : selectImageEntities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView simpleDraweeView;
        private View view_top;
        private View view_biankuang;

        public ViewHolder(View itemView) {
            super(itemView);
            simpleDraweeView = itemView.findViewById(R.id.simpleDraweeView);
            view_top = itemView.findViewById(R.id.view_top);
            view_biankuang = itemView.findViewById(R.id.view_biankuang);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) simpleDraweeView.getLayoutParams();
            int width = (int) (ScreenUtils.getScreenWidth(ApplicationUtils.getApp()) * 0.144);
            params.width = width;
            params.height = width;
            simpleDraweeView.setLayoutParams(params);
            view_top.setLayoutParams(params);
            view_biankuang.setLayoutParams(params);
        }
    }

    public void setOnItemClickListener(PreviewPhotoRVAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
