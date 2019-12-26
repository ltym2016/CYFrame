package com.caiyu.lib_photo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.caiyu.lib_photo.R;
import com.caiyu.lib_photo.view.PhotoImageView;
import com.facebook.drawee.drawable.AutoRotateDrawable;

import java.util.List;


public class FilePhotoSeeSelectedAdapter extends PagerAdapter {

    private Context mContext;
    private Activity activity;
    private List<String> infos;
    private String dirpath;

    Drawable ddefault_drawable;
    Drawable load_drawable;
    Drawable background_drawable;

    private OnItemClickListener onItemClickListener;

    public FilePhotoSeeSelectedAdapter(Context context, Activity activity, List<String> infos, String dirpath) {
        this.mContext = context;
        this.activity = activity;
        this.dirpath = dirpath;
        this.infos = infos;
        ddefault_drawable = ContextCompat.getDrawable(mContext, R.drawable.preview_default);
        load_drawable = new AutoRotateDrawable(ContextCompat.getDrawable(mContext, R.drawable.loading_01), 1000);
        background_drawable = ContextCompat.getDrawable(mContext, R.color.black);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        String filePath = "";
        if (dirpath.equals("allimgs")) {
            filePath = "file://" + infos.get(position);
        } else {
            filePath = "file://" + dirpath + "/" + infos.get(position);
        }
        PhotoImageView photoImageView = new PhotoImageView(mContext);
        photoImageView.loadImage(filePath);
        photoImageView.setOnTapListener(new PhotoImageView.OnTapListener() {
            @Override
            public void onTap() {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
        container.addView(photoImageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoImageView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
