package com.yunbao.main.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.utils.TextUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.main.R;
import com.yunbao.common.bean.ShopTopBean;

import java.util.List;

import jp.wasabeef.glide.transformations.GrayscaleTransformation;

public class ViewShopTop extends RelativeLayout {

    private ImageView ivTitle;
    private TextView tvName;
    private LinearLayout ll_root;
    private LinearLayout mlinear;
    private FrameLayout flBg;
    private List<ShopTopBean> data;
    public ViewShopTop(Context context) {
        super(context);
        initView();
    }

    public ViewShopTop(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ViewShopTop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view= LayoutInflater.from(getContext()).inflate(R.layout.item_main_shop,this);
        mlinear=view.findViewById(R.id.ll_root);
    }

    public void  setData(List<ShopTopBean> data){
        if(data==null || data.size()==0){
            return;
        }
        this.data=data;
        if(mlinear!=null){
            mlinear.removeAllViews();
        }
        for (int i = 0; i < data.size(); i++) {
            View view= LayoutInflater.from(getContext()).inflate(R.layout.item_main_shop_top,null);
            ivTitle = view.findViewById(R.id.iv);
            tvName = view.findViewById(R.id.tv);
            flBg = view.findViewById(R.id.fl_bg);

            ll_root = view.findViewById(R.id.ll_root);
            if(!TextUtils.isEmpty(data.get(i).getName())){
                String name="";
                if(data.get(i).getName().length()<=4){
                    name=data.get(i).getName();
                }else {
                    name=data.get(i).getName().substring(0,4)+"...";
                }
                tvName.setText(name);
            }
            if(data.get(i).getPic()!=null){
                ImgLoader.display(getContext(),data.get(i).getPic(), ivTitle);
            }
            final int finalI = i;
            ll_root.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                  setOnClick(finalI);
                }
            });
            setChecked(data.get(i));
            mlinear.addView(view);
        }
    }

    private void setChecked(ShopTopBean bean) {
        if(bean.isChoosed()){
            flBg.setBackgroundResource(R.mipmap.shop_top_bg);
            tvName.setTextColor(getContext().getResources().getColor(R.color.white));
            if(bean.getPic()!=null){
                ImgLoader.display(getContext(),bean.getPic(), ivTitle);
            }
        }else {
            flBg.setBackgroundResource(R.drawable.shape_item_unshop);
            tvName.setTextColor(getContext().getResources().getColor(R.color.color_AAAAAA));
                Glide.with(getContext()).load(bean.getPic())
                        .apply(RequestOptions.bitmapTransform(new GrayscaleTransformation()))
                        .into(ivTitle);
        }
    }

    private void setOnClick(int finalI) {
        for (int i = 0; i < data.size(); i++) {
            if(finalI==i){
                data.get(i).setChoosed(true);
            }else {
                data.get(i).setChoosed(false);
            }
            setData(data);
        }
        if(topItemClickListener!=null){
            topItemClickListener.setOnItemClickListener(data.get(finalI),finalI);
        }
    }



    public interface TopItemClickListener {
        void setOnItemClickListener(ShopTopBean bean, int position);
    }
    private  TopItemClickListener topItemClickListener;

    public void setTopItemClickListener(TopItemClickListener topItemClickListener) {
        this.topItemClickListener = topItemClickListener;
    }
}
