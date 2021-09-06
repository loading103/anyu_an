package com.yunbao.live.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.yunbao.live.R;
import com.yunbao.live.adapter.CagegoryViewPagerAdapter;
import com.yunbao.live.adapter.RecycleViewPagerdapter;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.live.bean.ShopBean;

import java.util.ArrayList;
import java.util.List;


public class ShopPageViewPager extends LinearLayout implements ViewPager.OnPageChangeListener {
    private  int   lastpositon;
    private  int   pageSize=10;
    private ViewPager  viewPager;
    private LinearLayout mllcontain;
    private ViewPager viewpager;


    public ShopPageViewPager(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public ShopPageViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    private void initView(Context context) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_viewpager_title, this);
        viewpager = view.findViewById(R.id.view_pager);
        mllcontain = view.findViewById(R.id.llcontain);

    }

    public void setData( Context context,List<LiveReadyBean.GoodsBean> data ){
        if(data==null){
            return;
        }
        initData(context,data);
    }

    private void initData(Context context,List<LiveReadyBean.GoodsBean> data ) {
        //一共的页数等于 总数/每页数量，并取整。
        int pageCount = (int)Math.ceil(data.size() * 1.0 / pageSize);
        List<View> viewList = new ArrayList();
        for (int i = 0; i <pageCount ; i++) { //每个页面都是inflate出一个新实例
            View view1 = LayoutInflater.from(context).inflate(R.layout.item_home_entrance_vp,viewpager,false);
            RecyclerView recyclerView = view1.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new GridLayoutManager(context, pageSize/2));


            RecycleViewPagerdapter adapter = new RecycleViewPagerdapter(context, i, pageSize,data);
            recyclerView.setAdapter(adapter);
            adapter.setData(data);

            viewList.add(recyclerView);

            View view= LayoutInflater.from(context).inflate(R.layout.item_view_dot, viewpager, false);
            ImageView iv_dot = view.findViewById(R.id.iv_dot);
            if(i==0){
                iv_dot.setEnabled(true);
            }else {
                iv_dot.setEnabled(false);
            }
            mllcontain.addView(view);
        }
        CagegoryViewPagerAdapter  mAdapter =new CagegoryViewPagerAdapter(viewList);
        viewpager.setAdapter(mAdapter);
        viewpager.addOnPageChangeListener(this);
    }

    public void setpageSize( int pageSize){
        this.pageSize=pageSize;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mllcontain.getChildAt(position).findViewById(R.id.iv_dot).setEnabled(true);
        mllcontain.getChildAt(lastpositon).findViewById(R.id.iv_dot).setEnabled(false);
        lastpositon=position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
