package com.yunbao.common.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yunbao.common.R;

import java.io.File;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;

/**
 * Created by cxf on 2017/8/9.
 */

public class ImgLoader {
    private static final boolean SKIP_MEMORY_CACHE = false;

    private static BlurTransformation sBlurTransformation;
    private static CropCircleTransformation cropCircleTransformation;

    static {
        sBlurTransformation= new BlurTransformation(25);
        cropCircleTransformation= new CropCircleTransformation();
    }


    public static void display(Context context, String url, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(url).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void displayCircle(Context context, String url, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(url).
                skipMemoryCache(SKIP_MEMORY_CACHE).
                apply(RequestOptions.bitmapTransform(cropCircleTransformation)).
                into(imageView);
    }

    public static void displayCircleWithPlaceError(Context context, String url, ImageView imageView,int placeholderRes, int errorRes) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(url).
                skipMemoryCache(SKIP_MEMORY_CACHE).
                apply(RequestOptions.bitmapTransform(cropCircleTransformation)).
                placeholder(placeholderRes).
                error(errorRes).
                into(imageView);
    }

    public static void displayWithError(Context context, String url, ImageView imageView, int errorRes) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(url).error(errorRes).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void displayWithPlaceError(Context context, String url, ImageView imageView,int placeholderRes, int errorRes) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(url).placeholder(placeholderRes).error(errorRes).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void displayAvatar(Context context, String url, ImageView imageView) {
        if (context == null) {
            return;
        }
        displayWithError(context, url, imageView, R.mipmap.img_head_deafult);
    }

    public static void display(Context context, File file, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(file).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void display(Context context, int res, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(res).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    /**
     * 显示视频封面缩略图
     */
    public static void displayVideoThumb(Context context, String videoPath, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(Uri.fromFile(new File(videoPath))).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void displayDrawable(Context context, String url, final DrawableCallback callback) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(url).skipMemoryCache(SKIP_MEMORY_CACHE).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if (callback != null) {
                    callback.onLoadSuccess(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                if (callback != null) {
                    callback.onLoadFailed();
                }
            }

        });
    }

    public static void clear(Context context, ImageView imageView) {
        Glide.with(context).clear(imageView);
    }


    /**
     * 显示模糊的毛玻璃图片
     */
    public static void displayBlur(Context context, String url, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(url)
                .skipMemoryCache(SKIP_MEMORY_CACHE)
                .apply(RequestOptions.bitmapTransform(sBlurTransformation))
                .into(imageView);
    }

    /**
     * 显示模糊的毛玻璃图片
     */
    public static void displayBlur(Context context, String url, ImageView imageView,int placeholder) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(url)
                .skipMemoryCache(SKIP_MEMORY_CACHE)
                .apply(RequestOptions.bitmapTransform(sBlurTransformation))
                .placeholder(placeholder)
                .error(placeholder)
                .into(imageView);
    }

    /**
     * 显示模糊的毛玻璃图片
     */
    public static void displayBlur(Context context, int url, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(url)
                .skipMemoryCache(SKIP_MEMORY_CACHE)
                .apply(RequestOptions.bitmapTransform(sBlurTransformation))
                .into(imageView);
    }

    public interface DrawableCallback {
        void onLoadSuccess(Drawable drawable);

        void onLoadFailed();
    }


}
