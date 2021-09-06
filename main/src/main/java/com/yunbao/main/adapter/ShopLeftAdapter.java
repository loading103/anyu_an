package com.yunbao.main.adapter;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.main.R;
import com.yunbao.common.bean.ShopLeftBean;

public class ShopLeftAdapter extends BaseQuickAdapter<ShopLeftBean, BaseViewHolder> {
    public ShopLeftAdapter() {
        super(R.layout.item_shop_left);
    }
    @Override
    protected void convert(BaseViewHolder helper, ShopLeftBean item) {
        if(!TextUtils.isEmpty(item.getName())){
            String name="";
            if(item.getName().length()<=4){
                name=item.getName();
            }else {
                name=item.getName().substring(0,4)+"...";
            }
            helper.setText(R.id.tv_name,name);
        }
        if(item.isChoosed()){
            helper.getView(R.id.iv_line).setVisibility(View.VISIBLE);
            ((TextView)helper.getView(R.id.tv_name)).setTextColor(getContext().getResources().getColor(R.color.color_222222));
            helper.getView(R.id.ll_root).setBackgroundColor(getContext().getResources().getColor(R.color.white));
            ((TextView)helper.getView(R.id.tv_name)).setTextSize(15);
            ((TextView)helper.getView(R.id.tv_name)).setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            ((TextView)helper.getView(R.id.tv_name)) .setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }else {
            helper.getView(R.id.iv_line).setVisibility(View.INVISIBLE);
            ((TextView)helper.getView(R.id.tv_name)).setTextColor(getContext().getResources().getColor(R.color.color_666666));
            ((TextView)helper.getView(R.id.tv_name)).setTextSize(14);
            helper.getView(R.id.ll_root).setBackgroundColor(getContext().getResources().getColor(R.color.color_F3F3F3));
            ((TextView)helper.getView(R.id.tv_name)).setTextColor(getContext().getResources().getColor(R.color.color_333333));
            ((TextView)helper.getView(R.id.tv_name)) .setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }
}