package com.yunbao.video.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.video.R;

public class VideoMainAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public VideoMainAdapter() {
        super(R.layout.item_live_video_main);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {

    }
}