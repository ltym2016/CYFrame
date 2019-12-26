package com.caiyu.lib_photo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.caiyu.lib_base.constants.Constants;
import com.caiyu.lib_base.utils.StringUtils;
import com.caiyu.lib_photo.entity.FileEntity;
import com.caiyu.lib_photo.entity.FolderBean;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by wangjing on 2015/10/18 0018.
 */
public class ListImageDirPopWindow extends PopupWindow {

    private int mWidth;
    private int mHeight;
    private View mConvertView;

    private ListView mListView;
    private List<FolderBean> mDatas;

    public interface OnDirSelectedListener {
        void onSelected(FolderBean folderBean);
    }

    public OnDirSelectedListener mListener;

    public void setOnDirSelectedListener(OnDirSelectedListener mListener) {
        this.mListener = mListener;
    }

    public ListImageDirPopWindow(Context context, List<FolderBean> datas) {
        calWidthAndHeight(context);
        mConvertView = LayoutInflater.from(context).inflate(R.layout.popwindow_photo, null);
        this.mDatas = datas;
        setContentView(mConvertView);
        setWidth(mWidth);
        setHeight(mHeight);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#77000000"));
        setBackgroundDrawable(colorDrawable);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        initViews(context);
        initEvent();
    }


    private void initViews(Context context) {

        mListView = (ListView) mConvertView.findViewById(R.id.id_list_dir);
        mListView.setAdapter(new ListDirAdapter(context, mDatas));
    }

    private void initEvent() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (mListener != null && mDatas != null) {
                        mListener.onSelected(mDatas.get(position));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 计算popwindow的宽度和高度
     *
     * @param context
     */
    private void calWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        mWidth = displayMetrics.widthPixels;
        mHeight = (int) (displayMetrics.heightPixels * 0.7);
    }


    private class ListDirAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        private List<FolderBean> mDatas;

        public ListDirAdapter(Context context, List<FolderBean> objects) {
            mInflater = LayoutInflater.from(context);
            this.mDatas = objects;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.popwindow_photo_item, parent, false);
                holder.mImg = (SimpleDraweeView) convertView.findViewById(R.id.id_dir_item_image);
                holder.mDirName = (TextView) convertView.findViewById(R.id.id_item_name);
                holder.mDirCount = (TextView) convertView.findViewById(R.id.id_item_count);
                holder.imvVideo = (ImageView) convertView.findViewById(R.id.imv_video);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            FolderBean bean = mDatas.get(position);
            //重置
            holder.mImg.setImageResource(R.drawable.pictures_no);
            if (bean.getName().equals("所有视频")) {
                holder.imvVideo.setVisibility(View.VISIBLE);
                GetVideoFrameTask newTask = new GetVideoFrameTask(holder.mImg);
                newTask.execute(bean.getAllFile().get(0));
            } else {
                if (bean.isVideo()) {//如果只有视频没有图片，用所有视频的第一个作为封面
                    for (int i = 0; i < mDatas.size(); i++) {
                        if (mDatas.get(i).getName().equals("所有视频")) {
                            holder.imvVideo.setVisibility(View.VISIBLE);
                            GetVideoFrameTask newTask = new GetVideoFrameTask(holder.mImg);
                            newTask.execute(mDatas.get(i).getAllFile().get(0));
                            break;
                        }
                    }
                } else {
                    holder.imvVideo.setVisibility(View.GONE);
                    Imageloader.getmInstance().loadImage(bean.getFirstImgPath(), holder.mImg);
                }
            }
            holder.mDirName.setText(bean.getName() + "");
            holder.mDirCount.setText("" + bean.getCount());
            return convertView;

        }

        private class ViewHolder {
            SimpleDraweeView mImg;
            TextView mDirName;
            TextView mDirCount;
            ImageView imvVideo;
        }
    }

    class GetVideoFrameTask extends AsyncTask<FileEntity, Integer, String> {

        private SimpleDraweeView simpleDraweeView;

        public GetVideoFrameTask(SimpleDraweeView imageView) {
            this.simpleDraweeView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            simpleDraweeView.setImageURI((new Uri.Builder()).scheme("res").path(String.valueOf(R.drawable.pictures_no)).build());
        }

        @Override
        protected String doInBackground(FileEntity... params) {
//            String thumbPath = FileUtils.getVideoCoverPath(params[0].getPath());
//            if (!FileUtils.fileIsExists(thumbPath)) {
//                FileUtils.saveBitmap(VideoUtil.getVideoThumbnail(ApplicationUtils.getApp().getContentResolver(), params[0].getVideoId()), thumbPath);
//            }
            return "";
        }

        @Override
        protected void onPostExecute(String path) {
            if (!StringUtils.isEmpty(path)) {
                simpleDraweeView.setImageURI(Uri.parse(Constants.FILE_PREX + path));
            }
        }
    }
}
