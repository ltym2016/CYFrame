package com.caiyu.lib_base.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 11/2/2015.
 */
public class BitmapUtils {


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Write the given bitmap into the given file. JPEG is used as the compression format with
     * quality set
     * to 100.
     *
     * @param bm   The bitmap.
     * @param file The file to write the bitmap into.
     */
    public static void writeBitmapToFile(Bitmap bm, File file, int quality) throws IOException {

        FileOutputStream fos = new FileOutputStream(file);
        bm.compress(Bitmap.CompressFormat.JPEG, quality, fos);
        fos.flush();
        fos.close();
        if (bm != null) {
            bm.recycle();
            bm = null;
        }
    }

    public static Bitmap addPadding(@NonNull Bitmap bmp) {

        int biggerParam = Math.max(bmp.getWidth(), bmp.getHeight());
        Bitmap bitmap = Bitmap.createBitmap(biggerParam, biggerParam, bmp.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        int top = bmp.getHeight() > bmp.getWidth() ? 0 : (bmp.getWidth() - bmp.getHeight()) / 2;
        int left = bmp.getWidth() > bmp.getHeight() ? 0 : (bmp.getHeight() - bmp.getWidth()) / 2;

        canvas.drawBitmap(bmp, left, top, null);
        return bitmap;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Drawable getAssetImage(Context context, String filename) throws IOException {
        AssetManager assets = context.getResources().getAssets();
        InputStream buffer = new BufferedInputStream((assets.open("drawable/" + filename + ".png")));
        Bitmap bitmap = BitmapFactory.decodeStream(buffer);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    /**
     * 屏幕截屏
     *
     * @param view 需要截屏的view
     * @return
     */
    public static Bitmap ScreenShots(View view) {
        if (view == null) return null;
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return bitmap;
    }

    /**
     * 从sd卡中读取Bitmap
     *
     * @param path
     * @return
     */
    public static Bitmap getBitmapFromSDCard(String path) {
        File file = new File(path);
        if (file.exists()) {
            return BitmapFactory.decodeFile(path);
        } else {
            return null;
        }
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio, int width, int height) {
        Log.e("BitmapUtils", "scaleBitmap==>ratio==>" + ratio + "==width==>" + width + "==height==>" + height);
        if (origin == null || width <= 0 || height <= 0) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, true);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    /**
     * 保存小视屏编辑时预览的小图到sd卡
     *
     * @param bitmap   bitmap
     * @param dirpath  保存图片的路径
     * @param fileName 保存图片的名字
     * @return
     */
    public static Boolean saveSViewoBitmapToSdCard(Bitmap bitmap, String dirpath, String fileName) {
        if (bitmap == null) {
            return false;
        }
        File dir_file = new File(dirpath);
        if (!dir_file.exists()) {
            dir_file.mkdirs();
        }
        File file = new File(dir_file, fileName);//谷歌推荐这种写法
        try {
            FileOutputStream fout = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fout);
            fout.flush();
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);

        return bm1;
    }

    public static BitmapFactory.Options getBitmapSize(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        return opts;
    }


    private final static int MaxWidth = 1280;


    /**
     * 是否是Gif图片
     *
     * @param imagePath
     * @return
     */
    private static boolean isGifImage(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        String type = options.outMimeType;
        Log.d("image type -> ", "" + type);
        if (TextUtils.isEmpty(type)) {
            type = "未能识别的图片";
        } else {
            type = type.substring(6, type.length());
        }
        if (TextUtils.equals(type.toLowerCase(), "gif")) {
            return true;
        }
        return false;
    }

    /**
     * 获取图片的旋转角度
     *
     * @param filePath
     * @return
     */
    public static int getExifProemtation(String filePath) {
        int degree = 0;
        try {
            ExifInterface exif = null;
            exif = new ExifInterface(filePath);
            if (exif != null) {
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                if (orientation != -1) {
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            degree = 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            degree = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            degree = 270;
                            break;
                    }
                }
            }
        } catch (IOException ex) {
            return 0;
        }
        return degree;
    }

    /**
     * 压缩图片尺寸
     *
     * @param bitmap       图片bitmap
     * @param targetWidth  缩放后的图片宽
     * @param targetHeight 缩放后的图片高
     * @param rotate       图片旋转角度
     * @return
     */
    private static Bitmap compressBySize(Bitmap bitmap, int targetWidth, int targetHeight, int rotate) {
        if (bitmap == null) {
            return null;
        }
        // 得到图片的宽度、高度；
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        // 缩放图片的尺寸
        float scaleWidth = targetWidth * 1f / bitmapWidth;
        float scaleHeight = targetHeight * 1f / bitmapHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postRotate(rotate);
        // 产生缩放后的Bitmap对象
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
//        if (bitmap != null && !bitmap.isRecycled()) {
//            bitmap.recycle();
//        }
        return resizeBitmap;
    }

    /**
     * 主要的压缩方法
     *
     * @param inputFile
     * @param outputDir
     * @param targetWidth
     * @param targetHeight
     * @param options
     * @return
     */
    public static String compressImage(String inputFile, String outputDir, float targetWidth, float targetHeight, int options) {
        Bitmap bitmap = getOriginBitmap(inputFile, targetWidth, targetHeight);
        String outputFile;
        File file = new File(outputDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (getImageMineType(inputFile) == ImageType.TYPE_PNG) {
            outputFile = outputDir + System.currentTimeMillis() + "comp.png";
        } else {
            outputFile = outputDir + System.currentTimeMillis() + "comp.jpg";
        }
        boolean success = compressImageToFile(executeMatrix(inputFile, bitmap, targetWidth, targetHeight), new File(outputFile), getImageMineType(inputFile), options);
        if (success) {
            return outputFile;
        } else {
            return inputFile;
        }
    }

    /**
     * 根据图片大小设置采样率，减少内存占用
     *
     * @param inputFile
     * @return
     */
    private static Bitmap getOriginBitmap(String inputFile, float targetWidth, float targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Size size = getImageSize(inputFile);
        int singlePxSize;
        switch (options.inPreferredConfig) {
            case ARGB_4444:
                singlePxSize = 2;
                break;
            case ARGB_8888:
                singlePxSize = 4;
                break;
            case ALPHA_8:
                singlePxSize = 1;
                break;
            case RGB_565:
                singlePxSize = 2;
                break;
            case HARDWARE:
            case RGBA_F16:
                singlePxSize = 2;
                break;
            default:
                singlePxSize = 2;
        }
        float imageSize = size.getWidth() * size.getHeight() * singlePxSize / 1024 / 1024;

        float allowSize = Runtime.getRuntime().maxMemory() / 1024 / 1024 / 2;

        int sizeLimitSampleSize = (int) (Math.sqrt(imageSize / allowSize));

        int sampleSize = (int) (size.getWidth() > size.getHeight() ? size.getWidth() / targetWidth : size.getHeight() / targetHeight);
        if (sampleSize <= 0) {
            sampleSize = 1;
        }
        options.inSampleSize = sampleSize > sizeLimitSampleSize ? sampleSize : sizeLimitSampleSize;
        return BitmapFactory.decodeFile(inputFile, options);
    }

    /**
     * 对bitmap进行旋转和宽高变化
     *
     * @param inputFile
     * @param bitmap
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    private static Bitmap executeMatrix(String inputFile, Bitmap bitmap, float targetWidth, float targetHeight) {
        if (bitmap == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        ExifInterface exifReader = null;
        int orientation = ExifInterface.ORIENTATION_NORMAL;
        try {
            exifReader = new ExifInterface(inputFile);
            orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;

                default: // ExifInterface.ORIENTATION_NORMAL
                    // Do nothing. The original image is fine.
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int width;
        int height;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            width = bitmap.getHeight();
            height = bitmap.getWidth();
        } else {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }
        if (width > targetWidth && height > targetHeight) {
            matrix.postScale(targetWidth / width, targetHeight / height);
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 压缩并保存到文件中
     *
     * @param bmp
     * @param file
     * @param type
     * @param options
     * @return
     */
    private static boolean compressImageToFile(Bitmap bmp, File file, int type, int options) {
        if (bmp == null) {
            return false;
        }
        // 0-100 100为不压缩
        boolean success = false;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        if (type == ImageType.TYPE_PNG) {
            bmp.compress(Bitmap.CompressFormat.PNG, options, baos);
        } else {
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            bmp.recycle();
        }
        return success;
    }


    /**
     * 获取图片宽高
     *
     * @return
     */
    public static Size getImageSize(Context context, int resId) {
        Size size = new Size();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(context.getResources(), resId, options);

        size.setWidth(options.outWidth);
        size.setHeight(options.outHeight);

        return size;
    }

    /**
     * 获取图片宽高
     *
     * @param inputFile
     * @return
     */
    public static Size getImageSize(String inputFile) {
        Size size = new Size();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(inputFile, options);

        size.setWidth(options.outWidth);
        size.setHeight(options.outHeight);

        try {
            ExifInterface exifReader = null;
            exifReader = new ExifInterface(inputFile);
            int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    size.setWidth(options.outHeight);
                    size.setHeight(options.outWidth);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    size.setWidth(options.outHeight);
                    size.setHeight(options.outWidth);
                    break;
                default: // ExifInterface.ORIENTATION_NORMAL
                    // Do nothing. The original image is fine.
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return size;
    }


    /**
     * 获取图片类型 gif,png,jpeg
     *
     * @param path
     * @return
     */
    public static int getImageMineType(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        if (options.outMimeType != null && options.outMimeType.contains("gif")) {
            return ImageType.TYPE_GIF;
        } else if (options.outMimeType != null && options.outMimeType.contains("png")) {
            return ImageType.TYPE_PNG;
        } else {
            return ImageType.TYPE_JPEG;
        }
    }


    /**
     * 将View转换成Bitmap
     *
     * @param context
     * @param layoutId
     * @param width
     * @param height
     * @return
     */
    private Bitmap getViewBitmap(Context context, int layoutId, int width, int height) {
        View view = LayoutInflater.from(context).inflate(layoutId, null, false);
        return getViewBitmap(view, width, height);
    }

    /**
     * 将View转换成Bitmap
     *
     * @param view
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getViewBitmap(View view, int width, int height) {
        Bitmap bitmap;

        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        view.measure(measuredWidth, measuredHeight);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static Bitmap createFullWindowBitmap(int screenWidth, int screenHeight, Bitmap bitmap) {
        int targetWidth;
        int targetHeight;

        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();

        if ((float) screenHeight / screenWidth > (float) oldHeight / oldWidth) {
            targetHeight = oldHeight;
            targetWidth = (int) (oldHeight * ((float) screenWidth / screenHeight));
        } else {
            targetWidth = oldWidth;
            targetHeight = (int) (oldWidth * ((float) screenHeight / screenWidth));
        }
        return Bitmap.createBitmap(bitmap, (int) ((float) (oldWidth - targetWidth) / 2),
                (int) ((float) (oldHeight - targetHeight) / 2),
                targetWidth, targetHeight);
    }

    public static class Size {
        int width;
        int height;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public static class ImageType {
        public static final int TYPE_GIF = 1;
        public static final int TYPE_JPEG = 2;
        public static final int TYPE_PNG = 3;
    }


}
