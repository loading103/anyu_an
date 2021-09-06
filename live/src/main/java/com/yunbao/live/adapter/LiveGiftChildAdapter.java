package com.yunbao.live.adapter;


import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.live.R;
import com.yunbao.live.bean.LiveReadyBean;

/**
 * Created by Wolf on 2020/04/25.
 * Describe:更多
 */
public class LiveGiftChildAdapter extends BaseQuickAdapter<LiveReadyBean.GoodsBean, BaseViewHolder> {
    public LiveGiftChildAdapter() {
        super(R.layout.item_section_content_child);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveReadyBean.GoodsBean item) {
        helper.setText(R.id.tv_content,item.getName());
        ImgLoader.displayCircleWithPlaceError(getContext(),item.getThumb(), (ImageView) helper.getView(R.id.iv_content),R.mipmap.icon_app_bg,
                R.mipmap.icon_app_bg);
        LinearLayout llRoot =helper.getView(R.id.ll_root);
        if(item.isCheck()){
            llRoot.setBackgroundResource(R.drawable.shap_gift);
        }else {
            llRoot.setBackground(null);
        }
    }
}
