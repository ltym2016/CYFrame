package com.caiyu.lib_photo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.caiyu.lib_base.constants.Constants;
import com.caiyu.lib_base.imageloader.ImageLoader;
import com.caiyu.lib_base.utils.ToastUtils;
import com.caiyu.lib_base.utils.Utils;
import com.caiyu.lib_base.utils.log.LogUtils;
import com.caiyu.lib_photo.R;
import com.caiyu.lib_photo.activity.FilePhotoSeeSelectedActivity;
import com.caiyu.lib_photo.activity.PhotoActivity;
import com.caiyu.lib_photo.entity.FileEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.cache.DiskLruCache;


public class PhotoAdapter extends BaseAdapter {
    private List<String> mSelectPath = new ArrayList<>();
    private Context mContext;
    private String mDitPath;
    private List<FileEntity> mFileEntitys;
    private List<FileEntity> mImgFileEntitys;
    private LayoutInflater mInflater;
    private Handler handler;
    private int maxNum;
    private Activity activity;
    private boolean isShowTakePhoto = true;
    private DiskLruCache mVideoCoverCache;
    private boolean isCanRepeatSelect;
    private int itemWidth;
    private int mSelectSize;
    // 当前正在选择
    private List<String> mChoosingPath = new ArrayList<>();

    public PhotoAdapter(Context context, List<FileEntity> mDatas, String dirpath,
                        List<String> mSelectPath, Handler handler, Activity activity,
                        int maxNum,  boolean showTakePhoto, boolean isCanRepeatSelect, int selectSize) {
        this.mContext = context;
        this.handler = handler;
        this.mDitPath = dirpath;
        this.mFileEntitys = mDatas;
        this.maxNum = maxNum;
        this.activity = activity;
        this.isShowTakePhoto = showTakePhoto;
        this.isCanRepeatSelect = isCanRepeatSelect;
        this.mSelectSize = selectSize;
        if (mSelectPath != null) {
            this.mSelectPath = mSelectPath;
        }
//        initVideoCoverCache();
        mInflater = LayoutInflater.from(context);
        itemWidth = context.getResources().getDisplayMetrics().widthPixels / 4 - 1;
//        Utils.createNoMediaFile(AppConfig.VIDEO_COVER_CACHE_FOLDER);
    }

//    private void initVideoCoverCache() {
//        File file = new File(AppConfig.VIDEO_COVER_CACHE_FOLDER);
//        FileUtils.createSDCardDir(AppConfig.VIDEO_COVER_CACHE_FOLDER);
//
//        try {
//            mVideoCoverCache = DiskLruCache.open(file, Utils.getAppVersionCode(mContext), 1, 10 * 1024 * 1024);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public int getCount() {
        if (isShowTakePhoto) {
            return mFileEntitys.size() + 1;
        } else {
            return mFileEntitys.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mFileEntitys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_photo_activity, parent, false);
            holder = new ViewHolder();
            holder.item_image = convertView.findViewById(R.id.item_image);
            holder.mSelect = convertView.findViewById(R.id.id_item_select);
            holder.item_take_photo_image = convertView.findViewById(R.id.item_take_photo_image);
            holder.imvVideoMark = convertView.findViewById(R.id.imv_video_mark);
            holder.tvVideoDuration = convertView.findViewById(R.id.tv_video_duration);
            convertView.setLayoutParams(new GridView.LayoutParams(itemWidth, itemWidth));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (isShowTakePhoto && position == 0) {//拍照
            try {
                holder.mSelect.setVisibility(View.GONE);
                holder.item_image.setVisibility(View.GONE);
                holder.item_take_photo_image.setVisibility(View.VISIBLE);
                holder.imvVideoMark.setVisibility(View.GONE);
                holder.tvVideoDuration.setVisibility(View.GONE);
                holder.item_take_photo_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isFastDoubleClick()) {
                            return;
                        }
                        handler.sendEmptyMessage(1567);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                final FileEntity fileEntity;
                if (isShowTakePhoto) {
                    fileEntity = mFileEntitys.get(position - 1);
                } else {
                    fileEntity = mFileEntitys.get(position);
                }
                if (fileEntity.getType() == FileEntity.IMAGE) {
                    holder.item_take_photo_image.setVisibility(View.GONE);
                    holder.item_image.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.imvVideoMark.setVisibility(View.GONE);
                    holder.tvVideoDuration.setVisibility(View.GONE);

                    holder.mSelect.setImageResource(R.drawable.picture_unselected);
                    holder.item_image.setColorFilter(null);
                    String filePath = null;
                    if (mDitPath.equals("allimgs") || mDitPath.equals("allVideos")) {
                        filePath = Constants.FILE_PREX + fileEntity.getPath();
                    } else {
                        filePath = Constants.FILE_PREX + mDitPath + "/" + fileEntity.getPath();
                    }
                    ImageLoader.loadResize(holder.item_image, filePath, itemWidth, itemWidth);
                    final String finalFilePath = filePath;
                    holder.mSelect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int size = mSelectPath.size() + mSelectSize;
                            //已经被选择
                            if (mSelectPath.contains(finalFilePath)) {

                                if (mChoosingPath.contains(finalFilePath)) {
                                    mChoosingPath.remove(finalFilePath);
                                }

                                mSelectPath.remove(finalFilePath);
                                holder.item_image.setColorFilter(null);
                                holder.mSelect.setImageResource(R.drawable.picture_unselected);
                                Message msg = new Message();
                                msg.what = PhotoActivity.CHANGE_NUM;
                                msg.arg1 = mSelectPath.size();
                                handler.sendMessage(msg);
                            } else {//未被选择
                                if (size >= maxNum) {
                                    ToastUtils.showLong("最多选择" + maxNum + "张");

                                } else {
                                    mSelectPath.add(finalFilePath);
                                    mChoosingPath.add(finalFilePath);
                                    holder.item_image.setColorFilter(Color.parseColor("#77000000"));
                                    holder.mSelect.setImageResource(R.drawable.pictures_selected);
                                    Message msg = new Message();
                                    msg.what = PhotoActivity.CHANGE_NUM;
                                    msg.arg1 = mSelectPath.size();
                                    handler.sendMessage(msg);
                                }
                            }
                        }
                    });

                    if (mSelectPath.contains(filePath)) {
                        LogUtils.e("filePath", "存在==》" + filePath);
                        holder.item_image.setColorFilter(Color.parseColor("#77000000"));
                        holder.mSelect.setImageResource(R.drawable.pictures_selected);
                    }

                    holder.item_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(mContext, FilePhotoSeeSelectedActivity.class);
                            if (mDitPath.equals("allimgs")) {
                                intent.putExtra("position", mImgFileEntitys.indexOf(fileEntity));
                                LogUtils.d("click file path====>" + fileEntity.getPath());
                            } else {
                                if (isShowTakePhoto) {
                                    intent.putExtra("position", position - 1);
                                } else {
                                    intent.putExtra("position", position);
                                }
                            }
                            intent.putExtra("max_size", maxNum);
                            intent.putExtra("select_size", mSelectSize);
                            intent.putExtra("dirpath", "" + mDitPath);
                            intent.putExtra("selectimage", (Serializable) mSelectPath);
                            PhotoActivity.setImagePathInPhone(getImagePath(mFileEntitys));//这里这么写是防止图片数量过多，导致intent传递的数据量过大导致奔溃。
                            activity.startActivityForResult(intent, 888);
                        }
                    });
                } else if (fileEntity.getType() == FileEntity.VIDEO) {
                    holder.item_take_photo_image.setVisibility(View.GONE);
                    holder.item_image.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    holder.imvVideoMark.setVisibility(View.VISIBLE);
                    holder.tvVideoDuration.setVisibility(View.VISIBLE);



                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }


    private List<String> getImagePath(List<FileEntity> mFilePaths) {
        List<String> images = new ArrayList<>();
        for (FileEntity entity : mFilePaths) {
            if (entity.getType() == FileEntity.IMAGE) {
                images.add(entity.getPath());
            }
        }
        return images;
    }

    public List<String> getmSelectPath() {
        if (mSelectPath == null) {
            return null;
        }
        return mSelectPath;
    }

    public void setmSelectPath(List<String> mSelectPath) {
        if (mSelectPath != null) {
            this.mSelectPath = mSelectPath;
        } else {
            this.mSelectPath.clear();
        }
        this.notifyDataSetChanged();
    }

    public void setAllImage(List<FileEntity> allImageFile) {
        this.mImgFileEntitys = allImageFile;
    }

    private class ViewHolder {
        //        ImageView mImg;
        SimpleDraweeView item_image;
        ImageButton mSelect;
        SimpleDraweeView item_take_photo_image;
        ImageView imvVideoMark;
        TextView tvVideoDuration;
    }


    /**
     * 关闭缓存
     */
    public void closeCache() {
        if (mVideoCoverCache != null) {
            try {
                mVideoCoverCache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
