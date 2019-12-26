package com.caiyu.lib_photo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.caiyu.lib_base.utils.ImageUtils;
import com.caiyu.lib_base.utils.log.LogUtils;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.kareluo.intensify.image.IntensifyImage;
import me.kareluo.intensify.image.IntensifyImageView;

/**
 * Created by 24706 on 2017/3/8.
 */

public class LongImageHelper {

    private ExecutorService mExecutor;

    /**
     * 判断图片是否为gif
     *
     * @param path
     * @return
     */
    public boolean isGif(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        String type = options.outMimeType;
        if (type.contains("gif")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isGif(InputStream inputStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        String type = options.outMimeType;
        if (type.contains("gif")) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 加载长图，区分本地图片和远程图片
     *
     * @param context
     * @param intensifyImage
     * @param url
     * @param photoLoadingView
     * @param mOnFileReadyListener
     */
    public void loadImage(final Context context, final IntensifyImageView intensifyImage, final String url, PhotoLoadingView photoLoadingView, PhotoImageView.OnFileReadyListener mOnFileReadyListener) {
        String targetUrl = url.replace("file://", "");
        if (targetUrl.startsWith("/storage/") || targetUrl.startsWith("/data")) {
            loadLocalLongImage(context, intensifyImage, targetUrl, photoLoadingView);
            if (mOnFileReadyListener != null) {
                mOnFileReadyListener.onFileReady(new File(targetUrl), targetUrl);
            }
        } else {
            loadRemoteLongImage(context, intensifyImage, targetUrl, photoLoadingView, mOnFileReadyListener);
        }
    }

    /**
     * 加载本地长图
     *
     * @param context
     * @param intensifyImage
     * @param path
     * @param photoLoadingView
     */
    private void loadLocalLongImage(final Context context, final IntensifyImageView intensifyImage, final String path, final PhotoLoadingView photoLoadingView) {
        LogUtils.d("加载本地长图");
        if (isGif(path)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                int orientation = ImageUtils.getExifOrientation(path);
                if (orientation != 0) {
                    final Bitmap targetBitmap = ImageUtils.rotateBitmapByDegree(
                            BitmapFactory.decodeFile(path, getAutoBitmapOption(path)), orientation);
                    intensifyImage.setScaleType(IntensifyImage.ScaleType.FIT_AUTO);
                    intensifyImage.setMaximumScale(getMaxScale(targetBitmap.getWidth(), targetBitmap.getHeight(),
                            context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels));
                    intensifyImage.setMinimumScale(getMinScale(targetBitmap.getWidth(), targetBitmap.getHeight(),
                            context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels));
                    final InputStream inputStream = Bitmap2InputStream(targetBitmap);
                    intensifyImage.post(new Runnable() {
                        @Override
                        public void run() {
                            intensifyImage.setImage(inputStream);
                            photoLoadingView.dismiss();
                        }
                    });
                } else {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);
                    intensifyImage.setScaleType(IntensifyImage.ScaleType.FIT_AUTO);
                    intensifyImage.setMaximumScale(getMaxScale(options.outWidth, options.outHeight, context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels));
                    intensifyImage.setMinimumScale(getMinScale(options.outWidth, options.outHeight, context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels));
                    intensifyImage.post(new Runnable() {
                        @Override
                        public void run() {
                            intensifyImage.setImage(path);
                            photoLoadingView.dismiss();
                        }
                    });
                }
            }
        }).start();
    }


    /**
     * 加载网络长图
     *
     * @param context
     * @param intensifyImage
     * @param url
     * @param photoLoadingView
     * @param mOnFileReadyListener
     */
    private void loadRemoteLongImage(final Context context, final IntensifyImageView intensifyImage, final String url, final PhotoLoadingView photoLoadingView, final PhotoImageView.OnFileReadyListener mOnFileReadyListener) {
        if (mExecutor == null) {
            mExecutor = Executors.newCachedThreadPool();
        }
        DataSubscriber dataSubscriber = new BaseDataSubscriber<CloseableReference<CloseableBitmap>>() {
            @Override
            public void onNewResultImpl(
                    DataSource<CloseableReference<CloseableBitmap>> dataSource) {
                if (!dataSource.isFinished()) {
                    return;
                }
                CloseableReference<CloseableBitmap> imageReference = dataSource.getResult();
                if (imageReference != null) {
                    final CloseableReference<CloseableBitmap> closeableReference = imageReference.clone();
                    try {
                        CloseableBitmap closeableBitmap = closeableReference.get();
                        final Bitmap bitmap = closeableBitmap.getUnderlyingBitmap();
                        if (bitmap != null && !bitmap.isRecycled()) {
                            FileBinaryResource resource = (FileBinaryResource) Fresco.getImagePipelineFactory().getMainFileCache().getResource(new SimpleCacheKey(url));
                            if (resource != null) {
                                final File file = resource.getFile();
                                if (file.exists()) {
                                    if (mOnFileReadyListener != null) {
                                        mOnFileReadyListener.onFileReady(file, url);
                                    }
                                    int orientation = ImageUtils.getExifOrientation(file.getPath());
                                    LogUtils.d("旋转角度为===>" + orientation);
                                    if (orientation != 0) {
                                        final Bitmap targetBitmap = ImageUtils.rotateBitmapByDegree(
                                                BitmapFactory.decodeFile(file.getPath(), getAutoBitmapOption(file.getPath())), orientation);
                                        intensifyImage.setScaleType(IntensifyImage.ScaleType.FIT_AUTO);
                                        intensifyImage.setMaximumScale(getMaxScale(targetBitmap.getWidth(), targetBitmap.getHeight(),
                                                context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels));
                                        intensifyImage.setMinimumScale(getMinScale(targetBitmap.getWidth(), targetBitmap.getHeight(),
                                                context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels));
                                        final InputStream inputStream = Bitmap2InputStream(targetBitmap);
                                        intensifyImage.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                intensifyImage.setImage(inputStream);
                                                photoLoadingView.dismiss();
                                            }
                                        });
                                    } else {
                                        intensifyImage.setScaleType(IntensifyImage.ScaleType.FIT_AUTO);
                                        intensifyImage.setMaximumScale(getMaxScale(bitmap.getWidth(), bitmap.getHeight(), context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels));
                                        intensifyImage.setMinimumScale(getMinScale(bitmap.getWidth(), bitmap.getHeight(), context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels));
                                        intensifyImage.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                intensifyImage.setImage(file.getPath());
                                                photoLoadingView.dismiss();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    } finally {
                        imageReference.close();
                        closeableReference.close();
                    }
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                Throwable throwable = dataSource.getFailureCause();
                // handle failure
            }
        };
        getBitmap(context, Uri.parse(url), dataSubscriber);
    }

    private float getMinScale(int width, int height, int widthPixels, int heightPixels) {
        if (height > width) {
            if (width < widthPixels) {
                return 0.5f;
            } else {
                return (float) widthPixels / (float) width / 2;
            }
        } else {
            if (width < widthPixels) {
                return 1.0f;
            } else {
                return (float) widthPixels * (float) height / (float) width / heightPixels;
            }
        }
    }

    private float getMaxScale(int bmWidth, int bmHeight, int parentWidth, int parentHeight) {
        if (bmHeight > bmWidth) {
            if (bmHeight >= parentHeight) {
                return (float) parentWidth * 3 / (float) bmWidth;
            } else {
                return (float) parentWidth * ((float) parentHeight / (float) bmHeight) * 3 / bmWidth;
            }
        } else {
            return (float) parentHeight / (float) bmHeight;
        }
    }

    /**
     * @param context
     * @param uri
     * @param dataSubscriber
     */
    public void getBitmap(Context context, Uri uri, DataSubscriber dataSubscriber) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        ImageRequest request = builder.build();
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(request, context);
        dataSource.subscribe(dataSubscriber, mExecutor);
    }


    // 将Bitmap转换成InputStream
    public InputStream Bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }


    /**
     * 判断是否超过了最大允许的图片内存占用量，来决定压缩比
     *
     * @param path
     * @return
     */
    public static BitmapFactory.Options getAutoBitmapOption(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float width = options.outWidth;
        float height = options.outHeight;

        options.inJustDecodeBounds = false;
        float imageSize = width * height * 2 / 1024 / 1024;
        float allowSize = Runtime.getRuntime().maxMemory() / 1024 / 1024 / 4;
        if (imageSize > allowSize) {
            options.inSampleSize = (int) (imageSize / allowSize + 1.5f);
        } else {
            options.inSampleSize = 1;
        }
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        return options;
    }


    /**
     * 自定义图片压缩比
     *
     * @param inSampleSize
     * @return
     */
    private BitmapFactory.Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        return options;
    }

    public static File getImageFile(Context context, Uri uri) {
        ImageRequest imageRequest = ImageRequest.fromUri(uri);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance()
                .getEncodedCacheKey(imageRequest, context);
        BinaryResource resource = ImagePipelineFactory.getInstance()
                .getMainFileCache().getResource(cacheKey);
        if (((FileBinaryResource) resource) != null) {
            return ((FileBinaryResource) resource).getFile();
        } else {
            return null;
        }
    }


    public static boolean isLongImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return isLongImage(options.outWidth, options.outHeight, 4);
    }

    /**
     * 判断是否为长图 或 宽图
     *
     * @param width
     * @param height
     * @param ratio
     * @return
     */
    public static boolean isLongImage(int width, int height, int ratio) {
        if ((height / width > ratio)) {
            return true;
        } else if (width / height > ratio) {
            return true;
        } else {
            return false;
        }
    }


}