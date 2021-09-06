package com.yunbao.live.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.common.Constants;
import com.yunbao.live.R;
import com.yunbao.live.bean.LiveContactBean;

public class LiveContactAdapter extends BaseQuickAdapter<LiveContactBean, BaseViewHolder> {

    public LiveContactAdapter() {
        super(R.layout.item_live_contact);
    }
    @Override
    protected void convert(final BaseViewHolder helper, LiveContactBean item) {
        String t="其他";
        switch (item.getType()){
            case Constants.WX:
                t="微信";
                break;
            case Constants.QQ:
                t="QQ";
                break;
            case Constants.TL:
                t="特聊";
                break;
            case Constants.TG:
                t="Telegram";
                break;
            case Constants.FB:
                t="Facebook";
                break;
        }
        if(TextUtils.isEmpty(item.getContent())){
            helper.setText(R.id.tv_content,t+"：--");
            helper.setGone(R.id.tv_copy,true)
                    .setGone(R.id.tv_open,true);
        }else {
            helper.setText(R.id.tv_content,t+"："+item.getContent());
            helper.setVisible(R.id.tv_copy,true)
                    .setVisible(R.id.tv_open,true);
        }
    }
}