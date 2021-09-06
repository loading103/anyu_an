package com.yunbao.main.views;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.blankj.utilcode.util.SizeUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.main.R;
import com.yunbao.common.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wolf on 2020/05/25
 * MainActivity 直播
 */

public class MainLiveViewHolder extends AbsMainHomeParentViewHolder3 {

    private MainLiveLiveViewHolder mLiveViewHolder;
    private List<LiveClassBean> list;
    private MainHomeFollowViewHolder mFollowViewHolder;
    private AppBarLayout appBarLayout;


    public MainLiveViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_live;
    }


    @Override
    public void init() {
        super.init();
        appBarLayout=(AppBarLayout) findViewById(R.id.appBarLayout);
        initScreen();

        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
            if (configBean != null) {
                list = configBean.getLiveClass();
                if (list != null && list.size() > 0) {
                    LiveClassBean bean3 = new LiveClassBean();
                    bean3.setName("推荐");
                    bean3.setChecked(true);
                    if(!list.get(0).getName().equals("推荐")){
                        list.add(0,bean3);
                    }

                    LiveClassBean bean2 = new LiveClassBean();
                    bean2.setName("关注");
                    bean2.setChecked(false);
                    if(!list.get(0).getName().equals("关注")){
                        list.add(0,bean2);
                    }
                }
                showTitle();
//                loadData();
//                if(list.size()>1){
//                    setCurrentPage(1);
//                }
            }
    };

    public void loadData(String id){
        for (int i = 0; i < list.size(); i++) {
            if(id.equals(list.get(i).getId()+"")){
                setCurrentPage(i);
            }
        }
    }

    /**
     * 挖孔屏适配
     */
    private void initScreen() {
        if(AppUtil.CutOutSafeHeight>0){
            CoordinatorLayout.LayoutParams linearParams =(CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            linearParams.height = SizeUtils.dp2px(SizeUtils.px2dp(AppUtil.CutOutSafeHeight)+40);
            appBarLayout.setLayoutParams(linearParams);
        }
    }

    public void loadDataByPosition(int position){
        setCurrentPage(position);
    }

    @Override
    public void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        AbsMainHomeChildViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }

                if(position==0){
                    mFollowViewHolder = new MainHomeFollowViewHolder(mContext, parent);
                    vh = mFollowViewHolder;
                }else {
                    mLiveViewHolder = new MainLiveLiveViewHolder(mContext, parent,position==1?-1:list.get(position).getId());
                    vh = mLiveViewHolder;
                }

                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
            }
        }
        if (vh != null) {
            vh.loadData();
        }
    }

    @Override
    protected int getPageCount() {
        return list.size();
    }

    @Override
    protected String[] getTitles() {
        List<String> strList = new ArrayList();
//        strList.add("推荐");
        for (LiveClassBean bean : list) {
            strList.add(bean.getName());
        }
        return (String[]) strList.toArray(new String[strList.size()]);
    }

    public void setCurrentPosition(String id) {
        if (mLiveViewHolder != null) {
            mLiveViewHolder.setCurrentPosition(id);
//            mLiveViewHolder.setCurrentPosition(id);
        }
    }

}
