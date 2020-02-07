package com.caiyu.lib_photo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caiyu.lib_base.imageloader.ImageLoader;
import com.caiyu.lib_photo.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.samluys.jutils.ScreenUtils;
import com.samluys.jutils.Utils;

import java.util.ArrayList;
import java.util.List;

public class FilePhotoSeeSelectedRVAdapter extends RecyclerView.Adapter<FilePhotoSeeSelectedRVAdapter.ViewHolder> {


    private OnItemClickListener onItemClickListener;

    private Context mContext;
    private List<String> infos;
    private String nowLookPath;
    private int screenWidth;
    private int screenHeight;

    public FilePhotoSeeSelectedRVAdapter(Context mContext, List<String> info) {
        this.mContext = mContext;
        if (info != null) {
            this.infos = info;
        } else {
            this.infos = new ArrayList<>();
        }

        screenWidth = ScreenUtils.getScreenWidth(Utils.getContext());
        screenHeight = ScreenUtils.getScreenHeight(Utils.getContext());
    }

    /**
     * 设置当前正在查看的path
     * @param nowLookPath
     */
    public void setNowLookPath(String nowLookPath) {
        this.nowLookPath = nowLookPath;
        notifyDataSetChanged();
    }

    public void setInfos(List<String> info) {
        if (info != null && this.infos != null) {
            this.infos = info;
            notifyDataSetChanged();
        }
    }

    public void addInfos(List<String> info) {
        if (info != null && this.infos != null) {
            this.infos.addAll(info);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FilePhotoSeeSelectedRVAdapter.ViewHolder viewHolder = new FilePhotoSeeSelectedRVAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.filephotoseeselected_layout, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (infos.get(position).equals(nowLookPath)) {
            holder.view_biankuang.setVisibility(View.VISIBLE);
        } else {
            holder.view_biankuang.setVisibility(View.GONE);
        }


        ImageLoader.loadResize(holder.simpleDraweeView, infos.get(position), screenWidth, screenHeight);
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
        return infos == null ? 0 : infos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView simpleDraweeView;
        private View view_biankuang;

        public ViewHolder(View itemView) {
            super(itemView);
            int screenWidth = ScreenUtils.getScreenWidth(Utils.getContext());
            simpleDraweeView = itemView.findViewById(R.id.simpleDraweeView);
            view_biankuang = itemView.findViewById(R.id.view_biankuang);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) simpleDraweeView.getLayoutParams();
            int width = (int) (screenWidth * 0.144);
            params.width = width;
            params.height = width;
            simpleDraweeView.setLayoutParams(params);
            view_biankuang.setLayoutParams(params);
        }
    }

    public void setOnItemClickListener(FilePhotoSeeSelectedRVAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
