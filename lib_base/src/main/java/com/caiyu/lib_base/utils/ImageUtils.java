package com.caiyu.lib_base.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.caiyu.lib_base.utils.log.LogUtils;

import java.io.IOException;

/**
 * @author luys
 * @describe
 * @date 2019-05-25
 * @email samluys@foxmail.com
 */
public class ImageUtils {

    public static class Config {

        public static final int MAX_SIZE = 1280;//普通图片最大宽高

        public static final int DEFAULT_OPTIONS = 80;//默认压缩质量压缩比

        public static final int LONG_IMAGE_RATIO = 3;//长图，宽图比例基准

        public static final int MAX_LONG_IMG_HEIGHT = 10000;//长图长度上限

        public static final int MAX_LONG_IMG_WIDTH = 2048;//宽图宽度上限

    }

    /**
     * 将图片压缩为指定宽高的Bitmap
     *
     * @param imagePath
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap getScaledBitmap(String imagePath, int maxWidth, int maxHeight) {
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap = null;
        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;

        // Then compute the dimensions we would ideally like to decode to.
        int desiredWidth = getResizedDimension(maxWidth, maxHeight,
                actualWidth, actualHeight);
        int desiredHeight = getResizedDimension(maxHeight, maxWidth,
                actualHeight, actualWidth);

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false;
        // TODO(ficus): Do we need this or is it okay since API 8 doesn't support it?
        // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
        decodeOptions.inSampleSize =
                findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
        Bitmap tempBitmap = BitmapFactory.decodeFile(imagePath, decodeOptions);
        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                tempBitmap.getHeight() > desiredHeight)) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap,
                    desiredWidth, desiredHeight, true);
            tempBitmap.recycle();
        } else {
            bitmap = tempBitmap;
        }

        return bitmap;
    }

    public static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                          int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     *
     * @param actualWidth   Actual width of the bitmap
     * @param actualHeight  Actual height of the bitmap
     * @param desiredWidth  Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     */
    // Visible for testing.
    public static int findBestSampleSize(
            int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param imagePath 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String imagePath) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(imagePath);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    /**
     * @param inputFile     原路径
     * @param outputDir     输出路径
     * @param options       图片的压缩比列 1-100
     * @param customMaxSize 自定义图片的最大宽高限制，如果为0 则使用默认的
     * @return 压缩后的图片地址
     */
    public static String compressImage(String inputFile, String outputDir, int options, int customMaxSize) {

        FileUtils.createNoMediaFile(outputDir);

        if (inputFile.startsWith("file://")) {
            inputFile = inputFile.replace("file://", "");
        }
        if (inputFile.startsWith("file:/")) {
            inputFile = inputFile.replace("file:/", "");
        }
        //如果是Gif不进行压缩
        if (BitmapUtils.getImageMineType(inputFile) == BitmapUtils.ImageType.TYPE_GIF) {
            return inputFile;
        }

        int targetOptions;
        if (options <= 0 || options > 100) {
            targetOptions = Config.DEFAULT_OPTIONS;
        } else {
            targetOptions = options;
        }


        BitmapUtils.Size size = BitmapUtils.getImageSize(inputFile);

        float originWidth = (float) size.getWidth();//原始宽
        float originHeight = (float) size.getHeight();//原始高

        float targetWidth;//最终宽
        float targetHeight;//最终高

        if (originWidth > originHeight) {
            if (originWidth / originHeight > Config.LONG_IMAGE_RATIO) {//超过限制倍数认为是宽图
                if (originWidth > Config.MAX_LONG_IMG_WIDTH) {//超过最长宽限制，需要压缩宽高
                    targetWidth = Config.MAX_LONG_IMG_WIDTH;
                    float ratio = originWidth / Config.MAX_LONG_IMG_WIDTH;
                    targetHeight = (originHeight / ratio);
                } else {
                    targetWidth = originWidth;//默认原图宽高
                    targetHeight = originHeight;
                }
            } else {
                if (originWidth <= getMaxSize(customMaxSize)) {//不超过普通图片最长宽限制，原图大小
                    targetWidth = originWidth;
                    targetHeight = originHeight;
                } else {//超过限制，等比缩放到宽为限制值
                    float ratio = originWidth / getMaxSize(customMaxSize);
                    targetWidth = getMaxSize(customMaxSize);
                    targetHeight = (originHeight / ratio);
                }
            }
        } else {
            if (originHeight / originWidth > Config.LONG_IMAGE_RATIO) {//超过限制倍数认为是长图
                if (originHeight > Config.MAX_LONG_IMG_HEIGHT) {//超过最大长度限制
                    targetHeight = Config.MAX_LONG_IMG_HEIGHT;
                    float ratio = originHeight / Config.MAX_LONG_IMG_HEIGHT;
                    targetWidth = (originWidth / ratio);
                } else {
                    targetWidth = originWidth;//默认原图宽高
                    targetHeight = originHeight;
                }
            } else {
                if (originHeight <= getMaxSize(customMaxSize)) {//不超过普通图片最长高度限制，原图大小
                    targetWidth = originWidth;
                    targetHeight = originHeight;
                } else {
                    float ratio = originHeight / getMaxSize(customMaxSize);//超过限制，等比缩放高到限制值
                    targetHeight = getMaxSize(customMaxSize);
                    targetWidth = (originWidth / ratio);
                }
            }
        }
        return BitmapUtils.compressImage(inputFile, outputDir, targetWidth, targetHeight, targetOptions);
    }

    public static int getFormatOptions(String rate) {
        int options;
        try {
            float targetRate = Float.parseFloat(rate);
            options = (int) (100 * targetRate);
        } catch (Exception e) {
            options = Config.DEFAULT_OPTIONS;
        }
        return options;
    }

    public static String ImageType(String localPhotoPath) {
        if (localPhotoPath.startsWith("file://")) {
            localPhotoPath = localPhotoPath.replace("file://", "");
        }
        if (localPhotoPath.startsWith("file:/")) {
            localPhotoPath = localPhotoPath.replace("file:/", "");
        }
        switch (BitmapUtils.getImageMineType(localPhotoPath)) {
            case BitmapUtils.ImageType.TYPE_PNG:
                return ".png";
            default:
                return ".jpg";
        }
    }

    private static int getMaxSize(int customMaxSize) {
        if (customMaxSize > 0) {
            return customMaxSize;
        }
        return Config.MAX_SIZE;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurBitmap(Context context, Bitmap image, float blurRadius, int outWidth, int outHeight) {
        // 将缩小后的图片做为预渲染的图片
        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, outWidth, outHeight, false);
        // 创建一张渲染后的输出图片
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        // 创建RenderScript内核对象
        RenderScript rs = RenderScript.create(context);
        // 创建一个模糊效果的RenderScript的工具对象
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        // 设置渲染的模糊程度, 25f是最大模糊度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            blurScript.setRadius(blurRadius);
        }
        // 设置blurScript对象的输入内存
        blurScript.setInput(tmpIn);
        // 将输出数据保存到输出内存中
        blurScript.forEach(tmpOut);
        // 将数据填充到Allocation中
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    public static Drawable getTintDrawable(Drawable drawable, @ColorInt int color) {
        Drawable.ConstantState state = drawable.getConstantState();
        Drawable drawable1 = DrawableCompat.wrap(state == null ? drawable : state.newDrawable()).mutate();
        drawable1.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        DrawableCompat.setTint(drawable1, color);
        return drawable1;
    }

    public static Drawable getTintDrawable(Context context, int id, int color) {
        Drawable originDrawable = ContextCompat.getDrawable(context, id);
        if (originDrawable != null) {
            return getTintDrawable(originDrawable, color);
        }
        return null;
    }

    /**
     * 获取旋转的角度
     *
     * @param filepath
     * @return
     */
    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            LogUtils.d("luys", "cannot read exif" + ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
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
        return degree;
    }
}
