package com.caiyu.lib_photo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.caiyu.lib_base.constants.Constants;
import com.caiyu.lib_base.utils.ApplicationUtils;
import com.caiyu.lib_base.utils.ScreenUtils;
import com.caiyu.lib_photo.R;
import com.caiyu.lib_photo.entity.SelectImageEntity;
import com.caiyu.lib_photo.view.OnPhotoTapListener;
import com.caiyu.lib_photo.view.PhotoDraweeView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.AutoRotateDrawable;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;


public class PreviewPhotoAdapter extends PagerAdapter {

    private Context mContext;
    private List<SelectImageEntity> mSelectImageEntites;

    Drawable ddefault_drawable;
    Drawable load_drawable;
    Drawable background_drawable;
    private int screenWidth;
    private int screenHeight;

    private OnItemClickListener onItemClickListener;


    public PreviewPhotoAdapter(Context context, Activity activity, List<SelectImageEntity> mSelectImageEntites) {
        this.mContext = context;
        this.mSelectImageEntites = mSelectImageEntites;
        ddefault_drawable = ContextCompat.getDrawable(mContext, R.drawable.preview_default);
        load_drawable = new AutoRotateDrawable(ContextCompat.getDrawable(mContext, R.drawable.loading_01), 1000);
        background_drawable = ContextCompat.getDrawable(mContext, R.color.black);
        screenWidth = ScreenUtils.getScreenWidth(ApplicationUtils.getApp());
        screenHeight = ScreenUtils.getScreenHeight(ApplicationUtils.getApp());
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mSelectImageEntites.size();
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
        final PhotoDraweeView photoDraweeView = new PhotoDraweeView(container.getContext());
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(Constants.FILE_PREX + mSelectImageEntites.get(position).getPath()))
                .setResizeOptions(new ResizeOptions(screenWidth, screenHeight))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(photoDraweeView.getController())
                .setAutoPlayAnimations(true)
                .setImageRequest(request)
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if (imageInfo == null) {
                            return;
                        }
                        photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                    }
                })
                .build();
        photoDraweeView.setController(controller);
        photoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

        try {
            container.addView(photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoDraweeView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
