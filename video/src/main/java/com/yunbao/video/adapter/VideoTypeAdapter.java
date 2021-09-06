package com.yunbao.video.adapter;


import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.common.bean.VideoClassBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.video.R;

public class VideoTypeAdapter extends BaseQuickAdapter<VideoClassBean, BaseViewHolder> {
    public VideoTypeAdapter() {
        super(R.layout.item_root_node);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoClassBean item) {
        ImgLoader.displayWithPlaceError(getContext(),item.getThumb(), (ImageView) helper.getView(R.id.thumb),R.mipmap.img_default_pic2,
                R.mipmap.img_default_pic2);
        helper.setText(R.id.name,item.getName())
                .setText(R.id.des,item.getDes());
    }
}