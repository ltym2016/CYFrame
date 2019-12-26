package com.caiyu.lib_base.imageloader;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.youth.banner.loader.ImageLoader;

/**
 * @author luys
 * @describe Banner 图片加载器
 * @date 2019-11-19
 * @email samluys@foxmail.com
 */
public class BannerImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Uri uri = Uri.parse((String) path);
        imageView.setImageURI(uri);
    }
    @Override
    public ImageView createImageView(Context context) {
        //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
        SimpleDraweeView simpleDraweeView = new SimpleDraweeView(context);
        RoundingParams roundingParams = new RoundingParams();
        roundingParams.setCornersRadius(10);//这里是设置你希望的圆角的值
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder.build();
        hierarchy.setRoundingParams(roundingParams);
        simpleDraweeView.setHierarchy(hierarchy);//一定要先设置Hierarchy，再去加载图片，否则会加载不出来图片
        return simpleDraweeView;
    }
}
