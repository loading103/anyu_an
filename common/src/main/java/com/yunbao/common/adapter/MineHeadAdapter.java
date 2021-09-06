package com.yunbao.common.adapter;


import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.common.R;
import com.yunbao.common.bean.HeaderBean;
import com.yunbao.common.utils.IMImageLoadUtil;

import java.util.List;

public class MineHeadAdapter extends BaseQuickAdapter<HeaderBean, BaseViewHolder> {
    public MineHeadAdapter(List<HeaderBean> datas) {
        super(R.layout.item_header,datas);
    }

    @Override
    protected void convert(BaseViewHolder helper, HeaderBean item) {
        if(item.isCheck()){
            IMImageLoadUtil.CommonImageLineCircleLoad(getContext(),item.getId(), (ImageView) helper.getView(R.id.iv_content));
        }else {
            IMImageLoadUtil.CommonImageCircle(getContext(),item.getId(), (ImageView) helper.getView(R.id.iv_content));
        }
    }

}