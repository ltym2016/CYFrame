package com.caiyu.lib_photo.view;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.caiyu.lib_base.constants.Constants;
import com.caiyu.lib_base.utils.log.LogUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

import me.kareluo.intensify.image.IntensifyImage;
import me.kareluo.intensify.image.IntensifyImageView;

/**
 * 一句话功能简述
 * 功能详细描述
 *
 * @author 杨晨 on 2017/4/2 18:42
 * @e-mail 247067345@qq.com
 * @see [相关类/方法](可选)
 */

public class PhotoImageView extends RelativeLayout {
    private static final int DEFAULT_LONG_IMAGE_RATIO = 4;
    private int longImageRatio;
    private LongImageHelper longImageHelper;
    private PhotoLoadingView photoLoadingView;
    private OnTapListener mOnTapListener;
    private OnFileReadyListener mOnFileReadyListener;
    private OnImageLongClickListener mLongClickListener;

    public PhotoImageView(Context context) {
        this(context, null);
    }

    public PhotoImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        photoLoadingView = new PhotoLoadingView(getContext());
    }

    public void loadImage(String url) {
        final Uri uri;
        if (url.startsWith(Constants.FILE_PREX)) {
            url = url.replace(Constants.FILE_PREX, "");
        }
        LogUtils.d("imagePath===>" + url);
        if (url.startsWith("/storage/") || url.startsWith("/data")) {
            uri = Uri.parse(Constants.FILE_PREX + getContext().getPackageName() + "/" + url);
        } else {
            uri = Uri.parse(url);
        }
        final PhotoDraweeView photoDraweeView = new PhotoDraweeView(getContext());
//
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels))
                .build();
        final String finalUrl = url;
        photoDraweeView.setController(Fresco.newDraweeControllerBuilder()
                .setOldController(photoDraweeView.getController())
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        LogUtils.d("animatable---->" + animatable);
                        Log.d("image", "width====>" + imageInfo.getWidth() + "height====>" + imageInfo.getHeight());
                        photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                        if (mOnFileReadyListener != null) {
                            if (finalUrl.startsWith("/storage/") || finalUrl.startsWith("/data")) {
                                mOnFileReadyListener.onFileReady(new File(finalUrl), finalUrl);
                            } else {
                                mOnFileReadyListener.onFileReady(LongImageHelper.getImageFile(getContext(), uri), finalUrl);
                            }
                        }
                        photoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
                            @Override
                            public void onPhotoTap(View view, float x, float y) {
                                if (mOnTapListener != null) {
                                    mOnTapListener.onTap();
                                }
                            }
                        });
                        photoDraweeView.setOnViewTapListener(new OnViewTapListener() {
                            @Override
                            public void onViewTap(View view, float x, float y) {
                                if (mOnTapListener != null) {
                                    mOnTapListener.onTap();
                                }
                            }
                        });
                        photoDraweeView.setOnLongClickListener(new OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                if (mLongClickListener != null) {
                                    mLongClickListener.onLongClick();
                                }
                                return false;
                            }
                        });
                        if (LongImageHelper.isLongImage(imageInfo.getWidth(), imageInfo.getHeight(), DEFAULT_LONG_IMAGE_RATIO)
                                && animatable == null) {
                            removeAllViews();
                            addLongImageView(finalUrl, photoLoadingView, mOnTapListener, mOnFileReadyListener, mLongClickListener);
                            addView(photoLoadingView);
                        } else {
                            photoLoadingView.dismiss();
                        }
                    }
                })
                .build());
        removeAllViews();
        addView(photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(photoLoadingView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        photoLoadingView.show();
    }

    private void addLongImageView(String url, PhotoLoadingView photoLoadingView, final OnTapListener onTapListener, OnFileReadyListener mOnFileReadyListener, final OnImageLongClickListener mLongClickListener) {
        IntensifyImageView intensifyImageView = new IntensifyImageView(getContext());
        intensifyImageView.setOnSingleTapListener(new IntensifyImage.OnSingleTapListener() {
            @Override
            public void onSingleTap(boolean inside) {
                if (onTapListener != null) {
                    onTapListener.onTap();
                }
            }
        });
        intensifyImageView.setOnLongPressListener(new IntensifyImage.OnLongPressListener() {
            @Override
            public void onLongPress(boolean inside) {
                if (mLongClickListener != null) {
                    mLongClickListener.onLongClick();
                }
            }
        });
        if (longImageHelper == null) {
            longImageHelper = new LongImageHelper();
        }
        longImageHelper.loadImage(getContext(), intensifyImageView, url, photoLoadingView, mOnFileReadyListener);
        addView(intensifyImageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public interface OnTapListener {
        void onTap();
    }

    public void setOnTapListener(OnTapListener onTapListener) {
        this.mOnTapListener = onTapListener;
    }

    public interface OnFileReadyListener {
        void onFileReady(File file, String url);
    }

    public void setOnFileReadyListener(OnFileReadyListener onFileReadyListener) {
        this.mOnFileReadyListener = onFileReadyListener;
    }

    public interface OnImageLongClickListener {
        void onLongClick();
    }

    public void setOnImageLongClickListener(OnImageLongClickListener longClickListener) {
        this.mLongClickListener = longClickListener;
    }
}
