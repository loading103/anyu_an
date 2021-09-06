package com.yunbao.main.adapter;


import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.common.greendao.entity.ShopRightDbBean;
import com.yunbao.main.R;

public class ShopRightChildAdapter extends BaseQuickAdapter<ShopRightDbBean, BaseViewHolder> {
    public ShopRightChildAdapter() {
        super(R.layout.item_shop_right);
    }

    @Override
    protected void convert(final BaseViewHolder helper, ShopRightDbBean item) {
        helper.setText(R.id.tv_name,item.getName());
        if(item.getPic()!=null){
            final ImageView lottie=(ImageView) helper.getView(R.id.animation_view);
            lottie.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(item.getPic())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            lottie.setVisibility(View.VISIBLE);
                            helper.getView(R.id.iv_top).setVisibility(View.INVISIBLE);
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            lottie.setVisibility(View.GONE);
                            helper.getView(R.id.iv_top).setVisibility(View.VISIBLE);
                            return false;
                        }
                    }).into((ImageView) helper.getView(R.id.iv_top));
        }
    }
}
