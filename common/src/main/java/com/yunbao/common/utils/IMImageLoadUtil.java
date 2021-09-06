package com.yunbao.common.utils;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.yunbao.common.R;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class IMImageLoadUtil {
    /**
     * 加载普通类型的图片centerCrop
     */
    public static void CommonImageLoad(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(imageView);
    }

    public static void CommonImageLoadCp(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.mipmap.icon_video_default)
                .error(R.mipmap.icon_video_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .centerCrop()
                .into(imageView);
    }
    /**
     * 加载圆形图片
     */
    public static void CommonImageCircleLoad(Context context, String url, ImageView imageView) {
        RequestOptions requestOptions = RequestOptions.circleCropTransform();
        Glide.with(context)
                .load(url)
                .placeholder(R.mipmap.im_icon_stub_loading_circle)
                .error(R.mipmap.im_icon_stub)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .apply(requestOptions)
                .into(imageView);
    }
    /**
     * 加载圆形图片
     */
    public static void CommonImageCircleLoadXh(Context context, String url, ImageView imageView) {
        RequestOptions requestOptions = RequestOptions.circleCropTransform();
        Glide.with(context)
                .load(url)
                .placeholder(R.mipmap.im_icon_stub_loading_circle)
                .error(R.mipmap.im_icon_stub)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(bitmapTransform(new BlurTransformation(23,10)))
                .dontAnimate()
                .apply(requestOptions)
                .into(imageView);
    }
    /**
     * 加载圆形图片
     */
    public static void CommonImageCircle(Context context, int id, ImageView imageView) {
        RequestOptions requestOptions = RequestOptions.circleCropTransform();
        Glide.with(context)
                .load(id)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .apply(requestOptions)
                .into(imageView);
    }
    /**
     * 加载圆形图片(带边框)
     */
    public static void CommonImageLineCircleLoad(Context context, int id, ImageView imageView) {
        Glide.with(context)
                .load(id)
                .apply(new RequestOptions().bitmapTransform(new CircleCrop())
                        .transform(new IMGlideCircleWithBorder(context, 2,context.getResources().getColor(R.color.colorPrimary))))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(imageView);
    }


    public static void CommonImageLineCircleLoad1(Context context, int id, ImageView imageView) {
        Glide.with(context)
                .load(id)
                .apply(new RequestOptions().bitmapTransform(new CircleCrop())
                        .transform(new IMGlideCircleWithBorder(context, 1,context.getResources().getColor(R.color.color_ABABAB))))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(imageView);
    }
    public static void CommonImageLineCircleLoad1(Context context, String id, ImageView imageView) {
        Glide.with(context)
                .load(id)
                .apply(new RequestOptions().bitmapTransform(new CircleCrop())
                        .transform(new IMGlideCircleWithBorder(context, 1,context.getResources().getColor(R.color.color_ABABAB))))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(imageView);
    }
    /**
     *圆角图片
     */
    public static void CommonImageRoundLoad(Context context, String url, ImageView imageView) {
        RoundedCornersTransformation transform = new RoundedCornersTransformation(IMDensityUtil.dip2px(context,5),0);
        RequestOptions options = new RequestOptions().placeholder(R.mipmap.im_icon_stub).error(R.mipmap.im_icon_stub).transform(transform);
        Glide.with(context)
                .asBitmap()
                .load(url)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(options)
                .dontAnimate()
                .into(imageView);
    }

    /**
     *圆角图片 占位图
     */
    public static void CommonImageRoundLoad3(Context context, String url, ImageView imageView,int placeholder) {
        RoundedCornersTransformation transform = new RoundedCornersTransformation(IMDensityUtil.dip2px(context,10),0);
        RequestOptions options = new RequestOptions().placeholder(placeholder).error(placeholder).transform(transform);
        Glide.with(context)
                .asBitmap()
                .load(url)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(options)
                .dontAnimate()
                .into(imageView);
    }

    /**
     *圆角图片
     */
    public static void CommonImageRoundLoad1(Context context, String url, ImageView imageView) {
        RequestOptions options = new RequestOptions().placeholder(R.mipmap.banner_place).error(R.mipmap.banner_place);
        Glide.with(context)
                .asBitmap()
                .load(url)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(options)
                .dontAnimate()
                .into(imageView);
    }
    /**
     *gif
     */
    public static void CommonGifLoadCp(Context context, int res, ImageView imageView) {
        Glide.with(context).load(res).listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (resource instanceof GifDrawable) {
                    //加载一次
                    ((GifDrawable)resource).setLoopCount(-1);
                }
                return false;
            }

        }).into(imageView);
    }
}
