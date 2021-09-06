package com.yunbao.live.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.common.bean.LiveBean;

public class LiveMoreFooterAdapter extends BaseQuickAdapter<LiveBean, BaseViewHolder> {
    public LiveMoreFooterAdapter() {
        super(R.layout.item_live_recommend);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveBean item) {
        ImgLoader.displayWithPlaceError(getContext(),item.getThumb(), (ImageView) helper.getView(R.id.cover),R.mipmap.img_default_pic2,
                R.mipmap.img_default_pic2);
        helper.setText(R.id.name,item.getUserNiceName())
                .setText(R.id.tv_goods,item.getName())
                .setText(R.id.title,TextUtils.isEmpty(item.getTitle())? WordUtil.getString(R.string.live_normal):item.getTitle());

        if(TextUtils.isEmpty(item.getName())){
            helper.getView(R.id.tv_goods).setVisibility(View.INVISIBLE);
        }else {
            helper.getView(R.id.tv_goods).setVisibility(View.VISIBLE);
        }

        if(TextUtils.isEmpty(item.getLive_tag())){
            helper.setGone(R.id.tv_playback,true);
        }else {
            helper.setGone(R.id.tv_playback,false);
            helper.setText(R.id.tv_playback,item.getLive_tag());
        }

        ImageView ivFirst=helper.getView(R.id.iv_first);
        TextView tvPlayback=helper.getView(R.id.tv_playback);
        LinearLayout llRight=helper.getView(R.id.ll_right);

        if(item.getIsvideo().equals("1")){
            ivFirst.setVisibility(View.GONE);
            tvPlayback.setVisibility(View.GONE);
            llRight.setVisibility(View.GONE);
//            if(TextUtils.isEmpty(item.getLive_tag())){
//                tvPlayback.setVisibility(View.GONE);
//                llRight.setVisibility(View.GONE);
//            }else {
//                tvPlayback.setVisibility(View.VISIBLE);
//                llRight.setVisibility(View.VISIBLE);
//                tvPlayback.setText(item.getLive_tag());
//            }
        }else {
            ivFirst.setVisibility(View.VISIBLE);
            tvPlayback.setVisibility(View.VISIBLE);
            llRight.setVisibility(View.VISIBLE);
            if(TextUtils.isEmpty(item.getLive_tag())){
                if(item.getIsvideo().equals("2")){
                    ivFirst.setVisibility(View.GONE);
                    tvPlayback.setVisibility(View.GONE);
                    llRight.setVisibility(View.GONE);
                }else {
                    tvPlayback.setText("直播中");
                    llRight.setBackgroundResource(R.mipmap.icon_live_item_right);
                }
            }else {
                tvPlayback.setText(item.getLive_tag());
                if((!TextUtils.isEmpty(item.getLive_tag()))&&item.getLive_tag().length()==4){
                    llRight.setBackgroundResource(R.mipmap.icon_right_bottom4);
                }else if((!TextUtils.isEmpty(item.getLive_tag()))&&item.getLive_tag().length()==5){
                    llRight.setBackgroundResource(R.mipmap.icon_right_bottom5);
                }else {
                    llRight.setBackgroundResource(R.mipmap.icon_live_item_right);
                }
            }
        }
    }
}
