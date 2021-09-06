package com.yunbao.live.adapter;

import android.widget.ImageView;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.bean.LiveMoreBean;

import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;

public class LiveMoreGameAdapter extends BaseQuickAdapter<LiveMoreBean.SlideBeanX.SlideBean, BaseViewHolder> {
    public LiveMoreGameAdapter() {
        super(R.layout.item_live_more_game);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveMoreBean.SlideBeanX.SlideBean item) {
        helper.setText(R.id.tv_content,item.getSlide_name());
        Glide.with(getContext())
                .load(item.getSlide_pic())
                .apply(RequestOptions.bitmapTransform(new CropCircleWithBorderTransformation(SizeUtils.dp2px(1.5f)
                        , getContext().getResources().getColor(R.color.color_FE41F4))))
                .into((ImageView) helper.getView(R.id.iv_content));
    }
}