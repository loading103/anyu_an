package com.yunbao.main.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SizeUtils;
import com.youth.banner.adapter.BannerAdapter;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.bean.BannerBean;
import com.yunbao.common.utils.IMImageLoadUtil;
import com.yunbao.main.R;

import java.util.List;

/**
 * Created by Administrator on 2020/5/6.
 * Describe:
 */
public class ImageAdapter extends BannerAdapter<BannerBean, ImageAdapter.BannerViewHolder> {

    private final Context mContext;

    public ImageAdapter(List<BannerBean> mDatas, Context mContext) {
        //设置数据，也可以调用banner提供的方法,或者自己在adapter中实现
        super(mDatas);
        this.mContext=mContext;
    }

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return new BannerViewHolder(imageView);
    }


    @Override
    public void onBindView(BannerViewHolder holder, BannerBean data, int position, int size) {

////        holder.imageView.setImageResource(data.getImageUrl());
//        Glide.with(mContext).load(data.getImageUrl()).
//                skipMemoryCache(false).into((ImageView) holder.imageView);
        IMImageLoadUtil.CommonImageRoundLoad3(CommonAppContext.sInstance,data.getImageUrl(),holder.imageView, R.mipmap.banner_defualt);
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public BannerViewHolder(@NonNull ImageView view) {
            super(view);
            this.imageView = view;
        }
    }
}
