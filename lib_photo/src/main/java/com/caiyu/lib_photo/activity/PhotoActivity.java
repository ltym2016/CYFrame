package com.caiyu.lib_photo.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

import com.caiyu.lib_base.constants.Constants;
import com.caiyu.lib_base.permission.PermissionManager;
import com.caiyu.lib_base.permission.PermissionRequestListener;
import com.caiyu.lib_photo.ListImageDirPopWindow;
import com.caiyu.lib_photo.R;
import com.caiyu.lib_photo.adapter.PhotoAdapter;
import com.caiyu.lib_photo.entity.FileEntity;
import com.caiyu.lib_photo.entity.FolderBean;
import com.samluys.jutils.FileUtils;
import com.samluys.jutils.ToastUtils;
import com.samluys.jutils.Utils;
import com.samluys.jutils.log.LogUtils;
import com.samluys.statusbar.StatusBarUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author luys
 * @describe 相册页面
 * @date 2019-11-22
 * @email samluys@foxmail.com
 */
public class PhotoActivity extends AppCompatActivity {


    private static final int DATA_LOADED = 0X110;
    public static final int CHANGE_NUM = 5678;

    private static final int SELECTPHOTO = 527;
    // 调系统相机
    private static final int REQUEST_CODE_TAKE_PICTURE = 1001;

    private static final int MSG_FINISH = 666;

    private int mPhotoNum;
    private boolean mIsShowVideo = false;//是否可以选择本地视频
    private boolean mIsShowVideoOnly = false;//是否只显示本地视频
    private boolean mIsShowTakePhoto = true;//是否显示拍照，默认显示
    private boolean mIsShowGif = true;
    private long maxVideoSize = 0;//视频的大小限制，默认0 不限制
    private String mCurrentPhotoPath;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_FINISH) {
                mProgressDialog.dismiss();
                ToastUtils.showLong("请检查是否拥有读取SD卡权限");
                finish();
            }
        }
    };

    private Toolbar toolbar;
    private Button btn_commit;
    private RelativeLayout rl_finish;
    private TextView tv_yulan;
    private TextView tv_title;

    private ListImageDirPopWindow popwindow;
    public static List<String> imagePathInPhone = new ArrayList<>();

    private GridView mGridView;
    private List<String> mImgs = new ArrayList<>();
    protected PhotoAdapter adapter;


    private RelativeLayout mBottomLy;
    private TextView mDirname;

    private File mCurrentDir;
    private int mMaxCount;

    private List<FolderBean> mFolderBeans = new ArrayList<>();

    private List<FileEntity> allImageFile = new ArrayList<>();
    private ArrayList<FileEntity> allVideoFile = new ArrayList<>();
    private List<FileEntity> allFile = new ArrayList<>();
    public static List<String> selectImages = new ArrayList<>();// 当前选择的图片
    private List<String> mSelectedImages;// 之前已经选择的图片
    private int mSelectedImageSize;
    private boolean mIsCanRepeatSelect;

    private int allpicSize = 0;

    private ProgressDialog mProgressDialog;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DATA_LOADED:
                    //绑定数据到View中
                    data2View();
                    initPopwindow();
                    setCommitText();
                    mProgressDialog.dismiss();
                    break;
                case CHANGE_NUM:
                    setCommitText(msg.arg1);
                    break;
                case 1567:
                    getCameraPermission();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo);
        StatusBarUtils.StatusBarIconDark(this);
        initView();
        initEvent();
        initDatas();
    }

    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        btn_commit = findViewById(R.id.btn_commit);
        rl_finish = findViewById(R.id.rl_finish);
        tv_yulan = findViewById(R.id.tv_yulan);
        tv_title = findViewById(R.id.tv_title);

        toolbar.setContentInsetsAbsolute(0, 0);
        mGridView = findViewById(R.id.id_gridView);
        mBottomLy = findViewById(R.id.bottom_ly);
        mDirname = (Button) findViewById(R.id.id_dir_name);

        mPhotoNum = getIntent().getIntExtra("PHOTO_NUM", -1);
        mIsShowGif = getIntent().getBooleanExtra("SHOW_GIF", true);
        mIsShowVideo = getIntent().getBooleanExtra("SHOW_VIDEO", false);
        mIsShowVideoOnly = getIntent().getBooleanExtra("SHOW_VIDEO_ONLY", false);
        mSelectedImages = getIntent().getStringArrayListExtra("SELECTED_IMAGES");
        mSelectedImageSize = getIntent().getIntExtra("SELECTED_IMAGES_SIZE", 0);
        mIsShowTakePhoto = getIntent().getBooleanExtra("SHOW_TAKE_PHOTO", true);
        mIsCanRepeatSelect = getIntent().getBooleanExtra("IS_CAN_REPEAT_SELECT", true);
        selectImages.clear();

        if (mSelectedImages != null) {
            selectImages.addAll(mSelectedImages);
        } else {
            mSelectedImages = new ArrayList<>();
        }

        if (mPhotoNum == -1) {
            mPhotoNum = 9;
        }

        if (mIsShowVideo) {
            tv_title.setText("图片和视频");
        } else {
            tv_title.setText("图片");
        }
    }

    private void initPopwindow() {

        popwindow = new ListImageDirPopWindow(this, mFolderBeans);
        popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        popwindow.setOnDirSelectedListener(new ListImageDirPopWindow.OnDirSelectedListener() {
            @Override
            public void onSelected(FolderBean folderBean) {
                if (adapter.getmSelectPath() != null) {


                    List<String> mImgPaths = adapter.getmSelectPath();//当前以及选择的图片
                    if (folderBean.getName().equals("所有图片")) {
                        adapter = new PhotoAdapter(PhotoActivity.this, allFile,
                                "allimgs", mImgPaths,
                                mHandler, PhotoActivity.this,
                                mPhotoNum,
                                mIsShowTakePhoto,
                                mIsCanRepeatSelect,
                                mSelectedImageSize);
                        adapter.setAllImage(allImageFile);
                    } else if (folderBean.getName().equals("所有视频")) {
                        adapter = new PhotoAdapter(PhotoActivity.this, allVideoFile,
                                "allimgs",
                                mImgPaths,
                                mHandler,
                                PhotoActivity.this,
                                mPhotoNum,
                                mIsShowTakePhoto,
                                mIsCanRepeatSelect,
                                mSelectedImageSize);
                    } else {
                        mCurrentDir = new File(folderBean.getDir());
                        mImgs = Arrays.asList(mCurrentDir.list(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String filename) {
                                String lowercaseName = filename.toLowerCase();//大写转小写
                                if (lowercaseName.endsWith(".jpg")
                                        || lowercaseName.endsWith(".jpeg")
                                        || lowercaseName.endsWith("png")
                                        || lowercaseName.endsWith(".gif")) {
                                    return true;
                                }
                                return false;
                            }
                        }));
                        Collections.reverse(mImgs);
                        List<FileEntity> folderImages = new ArrayList<FileEntity>();
                        for (String path : mImgs) {
                            FileEntity fileEntity = new FileEntity();
                            fileEntity.setPath(path);
                            fileEntity.setType(FileEntity.IMAGE);
                            folderImages.add(fileEntity);
                        }

                        adapter = new PhotoAdapter(PhotoActivity.this, folderImages,
                                mCurrentDir.getAbsolutePath(), mImgPaths, mHandler,
                                PhotoActivity.this,
                                mPhotoNum,
                                mIsShowTakePhoto,
                                mIsCanRepeatSelect,
                                mSelectedImageSize);
                    }
                    mGridView.setAdapter(adapter);
                    mDirname.setText("" + folderBean.getName());
                }
                popwindow.dismiss();

            }
        });
    }

    /**
     * 绑定数据到view
     */
    private void data2View() {
        if ((allImageFile.isEmpty() || allpicSize == 0) && allVideoFile.isEmpty()) {
            ToastUtils.showLong("未扫描到任何图片");
        }
        if (!allImageFile.isEmpty()) {
            mFolderBeans.get(0).setFirstImgPath(allImageFile.get(0).getPath());
            mFolderBeans.get(0).setVideo(false);
        } else if (!allVideoFile.isEmpty()) {
            mFolderBeans.get(0).setVideo(true);
            mFolderBeans.get(0).setCount(allVideoFile.size());
        }

        List<String> mImgPaths = new ArrayList<>();
        for (String path : mSelectedImages) {
            if (path != null) {
                String url = null;
                if (!path.startsWith(Constants.FILE_PREX) && (!path.startsWith("http://") && !path.startsWith("https://"))) {
                    url = Constants.FILE_PREX + path;
                } else {
                    url = path;
                }
                mImgPaths.add(url);
            }
        }

        adapter = new PhotoAdapter(this, allFile, "allimgs",
                mImgPaths, mHandler, PhotoActivity.this,
                mPhotoNum, mIsShowTakePhoto,
                mIsCanRepeatSelect,
                mSelectedImageSize);
        adapter.setAllImage(allImageFile);
        mGridView.setAdapter(adapter);
        mDirname.setText("" + mFolderBeans.get(0).getName());
    }

    /**
     * 设置顶部选择图片的数量
     *
     * @param num
     */
    private void setCommitText(int num) {
        LogUtils.e("setCommitText", "num==>" + num);
        if (num > 0) {
            tv_yulan.setEnabled(true);
            btn_commit.setEnabled(true);
            btn_commit.setText("完成(" + (num+mSelectedImageSize) + "/" + mPhotoNum + ")");
        } else {
            btn_commit.setEnabled(false);
            tv_yulan.setEnabled(false);
            btn_commit.setText("完成");
        }

    }

    private void setCommitText() {
//        int size = 0;
//        try {
//            size = adapter.getmSelectPath().size();
//        } catch (Exception e) {
//            size = 0;
//        }
        if (mSelectedImageSize > 0) {
            btn_commit.setEnabled(true);
            tv_yulan.setEnabled(true);
            btn_commit.setText("完成(" + mSelectedImageSize + "/" + mPhotoNum + ")");
        } else {
            btn_commit.setEnabled(false);
            btn_commit.setText("完成");
            tv_yulan.setEnabled(false);
        }
    }


    /**
     * 利用ContentProvider扫描手机中所有图片(视频)
     */
    private void initDatas() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "当前存储卡不可用!", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
        new Thread() {
            @Override
            public void run() {
                if (mIsShowVideoOnly) {
                    loadAllVideoData();
                } else {
                    loadAllImageData();
                    //通知handler扫描图片完成
                    if (mIsShowVideo) {
                        loadAllVideoData();
                    }
                }
                mHandler.sendEmptyMessage(DATA_LOADED);
            }
        }.start();
    }


    private void initEvent() {
        rl_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mDirname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popwindow.setAnimationStyle(R.style.dir_popupwindow_anim);
                popwindow.showAsDropDown(mBottomLy, 0, 0);
            }
        });
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitAndFinish();
            }
        });
        tv_yulan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isFastDoubleClick()) {
                    return;
                }
                Intent intent = new Intent(PhotoActivity.this, PreviewPhotoActivity.class);
                intent.putExtra("list", (Serializable) adapter.getmSelectPath());
                intent.putExtra("max_size", mPhotoNum);
                intent.putExtra("select_size", mSelectedImageSize);
                startActivityForResult(intent, SELECTPHOTO);
            }
        });
    }

    private void commit() {
        if (mIsCanRepeatSelect) {
            getSelectImages().clear();
            getSelectImages().addAll(adapter.getmSelectPath());
        } else {
            for (int i = 0; i < adapter.getmSelectPath().size(); i++) {
                if (!getSelectImages().contains(adapter.getmSelectPath().get(i))) {
                    getSelectImages().add(adapter.getmSelectPath().get(i));
                }
            }

            for (int i = getSelectImages().size() - 1; i >= 0; i--) {
                if (!adapter.getmSelectPath().contains(getSelectImages().get(i))) {
                    getSelectImages().remove(i);
                }
            }
        }
    }

    private void commitAndFinish() {

        commit();
        PhotoActivity.this.setResult(RESULT_OK);

        PhotoActivity.this.finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == SELECTPHOTO) {//预览图片后返回
                try {
                    LogUtils.e("onActivityResult", "SELECTPHOTO");
                    List<String> infos = (List<String>) data.getSerializableExtra("list");
                    if (infos != null) {
                        int size = infos.size();
                        LogUtils.e("SELECTPHOTO", "size==>" + size);
                        adapter.setmSelectPath(infos);
                        setCommitText(size);
                        boolean shouldCommit = data.getBooleanExtra("should_commit", true);
                        if (shouldCommit) {
                            commitAndFinish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 888) {
                LogUtils.e("onActivityResult", "888");
                boolean close = data.getBooleanExtra("close", false);
                List<String> paths = data.getStringArrayListExtra("simage");
                LogUtils.e("888", "close==>" + close);
                if (close) {
                    adapter.setmSelectPath(paths);
                    commitAndFinish();
                } else {
                    setCommitText(paths.size());
                    adapter.setmSelectPath(paths);
                }
            } else if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
                LogUtils.e("mCurrentPhotoPath : " + mCurrentPhotoPath);

                Intent intent = new Intent();
                intent.putExtra("take_photo", mCurrentPhotoPath);
                setResult(RESULT_OK, intent);
                finish();
            }
    }

    /**
     * 从本地获取所有的图片
     */
    private void loadAllImageData() {
        Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = PhotoActivity.this.getContentResolver();
        Cursor cursor = null;
        try {
            String[] queryType = new String[0];

            if (mIsShowGif) {
                queryType = new String[]{"image/jpeg", "image/png", "image/gif"};
            } else {
                queryType = new String[]{"image/jpeg", "image/png"};
            }
            cursor = cr.query(mImgUri, null,
                    MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                    queryType,
                    MediaStore.Images.Media.DATE_TAKEN);
        } catch (SecurityException e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                    ToastUtils.showLong("请检查是否拥有读取SD卡权限");
                    finish();
                }
            });
            return;
        }
        Set<String> mDirPath = new HashSet<String>();
        FolderBean allFolderBean = new FolderBean();//所有图片的bean
        allFolderBean.setName("所有图片");
        if (cursor == null) {
            finish();
            return;
        }
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            long dateTaken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
            File parentFile = new File(path).getParentFile();
            File file = new File(path);
            if (TextUtils.isEmpty(path) || parentFile == null || file.length() <= 0) {
                continue;
            }
            File tempFile = new File(path);
            if (!tempFile.exists()) {
                continue;
            }
            String lowercasePath = path.toLowerCase();
            if (lowercasePath.endsWith(".jpg")
                    || lowercasePath.endsWith(".jpeg")
                    || lowercasePath.endsWith(".png")
                    || lowercasePath.endsWith(".gif")) {
                FileEntity fileEntity = new FileEntity();
                fileEntity.setPath(path);
                fileEntity.setDateTaken(dateTaken);
                fileEntity.setType(FileEntity.IMAGE);
                allImageFile.add(fileEntity);
            }
            final String dirPath = parentFile.getAbsolutePath();
            FolderBean folderBean = null;
            if (mDirPath.contains(dirPath)) {
                continue;
            } else {
                mDirPath.add(dirPath);
                folderBean = new FolderBean();
                folderBean.setDir(dirPath);
                folderBean.setFirstImgPath(path);
                folderBean.setVideo(false);
            }

            if (parentFile.list() == null) {
                continue;
            }
            int picSize = parentFile.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    String lowercaseName = filename.toLowerCase();//大写转小写
                    if (lowercaseName.endsWith(".jpg")
                            || lowercaseName.endsWith(".jpeg")
                            || lowercaseName.endsWith(".png")
                            || lowercaseName.endsWith(".gif")) {
                        return true;
                    }
                    return false;
                }
            }).length;
            allpicSize = allpicSize + picSize;
            folderBean.setCount(picSize);
            mFolderBeans.add(0, folderBean);

            if (picSize > mMaxCount) {
                mMaxCount = picSize;
                LogUtils.e("picSize", "picSize==>" + picSize + "==" + parentFile.list().length);
                mCurrentDir = parentFile;
            }

        }
        cursor.close();
        Collections.reverse(allImageFile);
        allFile.addAll(allImageFile);
        allFolderBean.setAllFile(allImageFile);
        allFolderBean.setCount(allpicSize);
        mFolderBeans.add(0, allFolderBean);
    }

    /**
     * 获取所有视频数据
     */
    private void loadAllVideoData() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = null;
        try {
            if (maxVideoSize <= 0) {
                cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DATE_TAKEN);
            } else {
                cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Video.Media.SIZE + "<" + maxVideoSize, null, MediaStore.Video.Media.DATE_TAKEN);
            }
        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    ToastUtils.showLong("请检查是否拥有读取SD卡权限");
                    finish();
                }
            });
            return;
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                long dateTaken = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN));
                long videoId = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                if (TextUtils.isEmpty(path) || new File(path).length() <= 0) {
                    continue;
                }
                LogUtils.d("video path====>" + path);
                LogUtils.d("video size====>" + size + " video path====>" + path);
                FileEntity fileEntity = new FileEntity();
                fileEntity.setPath(path);
                fileEntity.setDateTaken(dateTaken);
                fileEntity.setType(FileEntity.VIDEO);
                fileEntity.setVideoId(videoId);
                fileEntity.setDuration(duration);
                allVideoFile.add(fileEntity);
            }
            cursor.close();
        }
        if (allVideoFile.size() <= 0) {
            return;
        }
        Collections.reverse(allVideoFile);
        FolderBean videoFolder = new FolderBean();
        videoFolder.setName("所有视频");
        videoFolder.setAllFile(allVideoFile);
        videoFolder.setVideo(true);
        videoFolder.setCount(allVideoFile.size());
        allFile.addAll(allVideoFile);
        Collections.sort(allFile, new Comparator<FileEntity>() {
            @Override
            public int compare(FileEntity o1, FileEntity o2) {
                long dateTaken1 = o1.getDateTaken();
                long dateTaken2 = o2.getDateTaken();
                return -Long.valueOf(dateTaken1).compareTo(dateTaken2);
            }
        });
        if (mFolderBeans.isEmpty()) {
            mFolderBeans.add(0, videoFolder);
        } else {
            mFolderBeans.add(1, videoFolder);
        }
    }


    private void getCameraPermission() {
        PermissionManager.getInstance()
                .requestPermission(
                        new PermissionRequestListener() {
                            @Override
                            public void onGranted() {
                                takePhoto();
                            }

                            @Override
                            public void onDenied(List<String> data) {

                            }
                        },
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE

                );
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                String authority = Utils.getContext().getPackageName() + ".fileprovider";
                mCurrentPhotoPath = photoFile.getAbsolutePath();
                Uri currentPhotoUri = FileProvider.getUriForFile(this,
                        authority, photoFile);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    List<ResolveInfo> resInfoList = this.getPackageManager()
                            .queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, currentPhotoUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }
                startActivityForResult(captureIntent, REQUEST_CODE_TAKE_PICTURE);
            }
        }
    }


    private File createImageFile() {

        String tempPath = FileUtils.getLocalCacheFilePath(Environment.DIRECTORY_PICTURES+
                File.separator + Constants.TEMP);
        File file = new File(tempPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        File tempFile = new File(tempPath + System.currentTimeMillis() + ".jpg");
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }

        return tempFile;
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (adapter != null) {
            adapter.closeCache();
        }
    }

    public static List<String> getImagePathInPhone() {
        return imagePathInPhone;
    }

    public static void setImagePathInPhone(List<String> imagePathInPhone) {
        PhotoActivity.imagePathInPhone = imagePathInPhone;
    }

    public static List<String> getSelectImages() {
        return selectImages;
    }
}
