package com.yunbao.live.adapter;


import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.live.R;

/**
 * Created by Wolf on 2020/04/22.
 * Describe:
 */
//public class MoreAdapter extends BaseQuickAdapter<LiveClassBean, BaseViewHolder> {
//    public MoreAdapter() {
//        super(R.layout.item_tab);
//    }
//
//    @Override
//    protected void convert(BaseViewHolder helper, LiveClassBean item) {
//        TextView textView=helper.getView(R.id.tv_content);
//        helper.setText(R.id.tv_content,item.getName());
//        if(item.isChecked()){
//            textView.setBackground(mContext.getDrawable(R.drawable.shap_tab_choose));
//            textView.setTextColor(mContext.getResources().getColor(R.color.color_8B2AFB));
//        }else {
//            textView.setBackground(mContext.getDrawable(R.drawable.shap_tab_unchoose));
//            textView.setTextColor(mContext.getResources().getColor(R.color.color_A6A6A6));
//        }
//
//    }
//}