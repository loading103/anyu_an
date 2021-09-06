package com.yunbao.main.adapter;


import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.main.R;
import com.yunbao.main.bean.TabBean;

/**
 * Created by Wolf on 2020/04/22.
 * Describe:
 */
public class TabAdapter extends BaseQuickAdapter<LiveClassBean, BaseViewHolder> {
    public TabAdapter() {
        super(R.layout.item_tab);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveClassBean item) {
        TextView textView=helper.getView(R.id.tv_content);
        helper.setText(R.id.tv_content,item.getName());
        if(item.isChecked()){
            textView.setBackground(getContext().getDrawable(R.drawable.shap_tab_choose));
//            textView.setTC(getContext().getResources().getDrawable(com.yunbao.common.R.drawable.shape_color_normal));
            setTextViewStyles(textView);
            textView.invalidate();
        }else {
            textView.setBackground(getContext().getDrawable(R.drawable.shap_tab_unchoose));
            textView.getPaint().setShader(null);
            textView.invalidate();
            textView.setTextColor(getContext().getResources().getColor(R.color.color_A6A6A6));
        }

    }

    /**

     * 设置textview 的颜色渐变

     * @param text

     */

    public void setTextViewStyles(TextView text){
        LinearGradient mLinearGradient =new LinearGradient(text.getMeasuredWidth(), text.getMeasuredHeight(), 0, 0,
                getContext().getResources().getColor(com.yunbao.live.R.color.color_gradient_dark),
                getContext().getResources().getColor(com.yunbao.live.R.color.color_gradient_light), Shader.TileMode.CLAMP);
        text.getPaint().setShader(mLinearGradient);
        text.invalidate();
    }

}