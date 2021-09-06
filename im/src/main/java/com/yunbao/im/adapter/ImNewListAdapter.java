package com.yunbao.im.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.greendao.entity.SocketListBean;
import com.yunbao.im.R;
import com.yunbao.im.utils.IMTimeData;


public class ImNewListAdapter extends BaseQuickAdapter<SocketListBean, BaseViewHolder> {

    public ImNewListAdapter() {
        super(R.layout.item_im_list_anchor);
    }

    @Override
    protected void convert(BaseViewHolder helper, SocketListBean item) {
        ImgLoader.display(getContext(),item.getAvatar(), (ImageView) helper.getView(R.id.avatar));
        helper.setText(R.id.name,item.getNickname());
        helper.setText(R.id.msg,item.getContent());
        helper.setText(R.id.time,IMTimeData.stampToTime(item.getTime(),null));
        if(item.getUnReadCount()==1){
            helper.findView(R.id.red_point).setVisibility(View.VISIBLE);
        }else {
            helper.findView(R.id.red_point).setVisibility(View.INVISIBLE);
        }
    }

}
