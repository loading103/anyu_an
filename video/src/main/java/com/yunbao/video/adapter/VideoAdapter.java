package com.yunbao.video.adapter;


import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.video.R;
import com.yunbao.common.bean.VideoBean;

public class VideoAdapter extends BaseQuickAdapter<VideoBean, BaseViewHolder> {
    public VideoAdapter() {
        super(R.layout.item_video_like);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoBean item) {
        helper.setText(R.id.tv_title,item.getTitle())
                .setText(R.id.tv_num,item.getViewNum());

        RequestOptions myOptions = new RequestOptions().placeholder(R.mipmap.icon_video_default).
                error(R.mipmap.icon_video_default).transform(new MultiTransformation<>(new CenterCrop(),
                new RoundedCorners(8)));
        Glide.with(getContext()).load(item.getThumb()+"?width=400").apply(myOptions).
//                placeholder(R.mipmap.img_default_pic2).
//                error(R.mipmap.img_default_pic2).
                into((ImageView) helper.getView(R.id.iv_content));
        if(item.getVip_limit()>0){
            helper.setVisible(R.id.iv_vip,true);
        }else {
            helper.setVisible(R.id.iv_vip,false);
        }
    }
}
