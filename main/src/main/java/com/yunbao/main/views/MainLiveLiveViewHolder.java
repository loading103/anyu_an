package com.yunbao.main.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.youth.banner.Banner;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.BannerBean;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.L;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.live.utils.LiveStorge;
import com.yunbao.main.R;
import com.yunbao.main.activity.LoginNewActivity;
import com.yunbao.main.adapter.MainLiveLiveAdapter;
import com.yunbao.main.adapter.TabAdapter;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 直播 直播
 */

public class MainLiveLiveViewHolder extends AbsMainHomeChildViewHolder implements OnItemClickListener<LiveBean>{

    private RecyclerView mClassRecyclerViewDialog;
    private View mShadow;
    private View mBtnDismiss;
    private CommonRefreshView mRefreshView;
    private RecyclerView mClassRecyclerViewTop;
    private RecyclerView mClassRecyclerViewTap;
    private MainLiveLiveAdapter mAdapter;
    private ObjectAnimator mShowAnimator;
    private ObjectAnimator mHideAnimator;  
    private Banner mBanner;
    private boolean mBannerShowed;
    private List<BannerBean> mBannerList;
    private TabAdapter tabAdapter;
    private boolean isAll;//推荐主播
    private int mClassId;
    private List<LiveClassBean> datas = new ArrayList<>();
    public MainLiveLiveViewHolder(Context context, ViewGroup parentView, int mClassId) {
        super(context, parentView,mClassId);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_live_live;
    }

    @Override
    protected void processArguments(Object... args) {
        mClassId=(int)args[0];
        if(mClassId==-1){
            isAll=true;
        }
    }

    @Override
    public void init() {
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        mRefreshView.setLayoutManager(gridLayoutManager);
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mAdapter = new MainLiveLiveAdapter(mContext);
        mAdapter.setOnItemClickListener(MainLiveLiveViewHolder.this);
        if (tabAdapter != null && tabAdapter.getData()!=null && tabAdapter.getData().size() > 0) {
            ((LiveClassBean) tabAdapter.getData().get(0)).setChecked(true);
            tabAdapter.notifyDataSetChanged();
        }
        mRefreshView.setRecyclerViewAdapter(mAdapter);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveBean>() {
            @Override
            public RefreshAdapter<LiveBean> getAdapter() {
                return null;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if(isAll){
                    MainHttpUtil.getLiveRecommend(p, callback);
                }else {
                    MainHttpUtil.getClassLive(mClassId, p, callback);
                }
            }

            @Override
            public List<LiveBean> processData(String[] info) {

                L.e("WOLF5",Arrays.toString(info));
                if(isAll){
                    return JSON.parseArray(Arrays.toString(info), LiveBean.class);
                }else {
                    return JSON.parseArray(Arrays.toString(info), LiveBean.class);
                }
            }

            @Override
            public void onRefreshSuccess(List<LiveBean> list, int count) {
                if(isAll){
                    LiveStorge.getInstance().put(Constants.LIVE_HOME, list);
                }else {
                    LiveStorge.getInstance().put(Constants.LIVE_CLASS_PREFIX + mClassId, list);
                }
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<LiveBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }


    @Override
    public void onItemClick(LiveBean bean, int position) {
        if(CommonAppConfig.getInstance().isVisitorLogin()){
            LoginNewActivity.forwardVisitor(mContext);
            return;
        }
        if(isAll){
            watchLive(bean, Constants.LIVE_HOME, position);
        }else {
            watchLive(bean, Constants.LIVE_CLASS_PREFIX + mClassId, position);
        }
    }

    @Override
    public void loadData() {
        if (mRefreshView != null) {
            Log.e("loadData----------","loadData111");
            mRefreshView.initData();
        }
    }

    @Override
    public void release() {
        MainHttpUtil.cancel(MainHttpConsts.GET_HOT);
        if (mHideAnimator != null) {
            mHideAnimator.cancel();
        }
        if (mShowAnimator != null) {
            mShowAnimator.cancel();
        }
        mShowAnimator = null;
        mHideAnimator = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }


    public void setCurrentPosition(String id) {
        if (tabAdapter != null && tabAdapter.getData()!=null && tabAdapter.getData().size() > 0) {
            for (int i = 0; i < tabAdapter.getData().size(); i++) {
                if(tabAdapter.getData().get(i).getId()==Integer.parseInt(id)){
                    ((LiveClassBean) tabAdapter.getData().get(i)).setChecked(true);
                    if (i == 0) {
                        isAll = true;
                    } else {
                        isAll = false;

                        mClassId = ((LiveClassBean) tabAdapter.getData().get(i)).getId();
                    }
                    mClassRecyclerViewTap.scrollToPosition(i);
                    mRefreshView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRefreshView.initData();
                        }
                    },200);
                }else {
                    ((LiveClassBean) tabAdapter.getData().get(i)).setChecked(false);
                }
            }
            tabAdapter.notifyDataSetChanged();
        }
    }

}
