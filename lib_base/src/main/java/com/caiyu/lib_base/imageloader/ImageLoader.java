package com.caiyu.lib_base.imageloader;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class ImageLoader {

    /**
     * 直接加载图片,不做任何限制
     *
     * @param simpleDraweeView SimpleDraweeView
     * @param url              url地址
     */
    public static void load(SimpleDraweeView simpleDraweeView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        simpleDraweeView.setImageURI(Uri.parse("" + url));
    }


    /**
     * 加载图片-可能出现大图，需要利用ResizeOptions处理，否则会报错 Bitmap too large to be uploaded into a texture
     * 如果是Gif，则默认获取Gif的第一帧显示，并且不会播放
     *
     * @param simpleDraweeView SimpleDraweeView
     * @param url              url地址
     * @param reWidth          限制的宽
     * @param reHeight         限制的高
     */
    public static void loadResize(SimpleDraweeView simpleDraweeView, String url, int reWidth, int reHeight) {
        ImageDecodeOptions options = ImageDecodeOptions.newBuilder().setForceStaticImage(true).setDecodePreviewFrame(true).build();
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse("" + url))
                .setResizeOptions(new ResizeOptions(reWidth, reHeight))
                .setImageDecodeOptions(options);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequestBuilder.build())
                .setOldController(simpleDraweeView.getController())
                .setAutoPlayAnimations(false)
                .build();
        simpleDraweeView.setController(controller);
    }

    /**
     * 加载本地图片
     *
     * @param context          Context
     * @param simpleDraweeView SimpleDraweeView
     * @param id
     */
    public static void loadLocalImage(Context context, SimpleDraweeView simpleDraweeView, int id) {
        simpleDraweeView.setImageURI(Uri.parse("res://" + context.getPackageName() + "/" + id));
    }

}
