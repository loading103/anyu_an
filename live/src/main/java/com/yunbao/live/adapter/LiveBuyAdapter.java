package com.yunbao.live.adapter;


import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.live.R;

public class LiveBuyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public LiveBuyAdapter() {
        super(R.layout.item_live_buy);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_number,item);
    }
}