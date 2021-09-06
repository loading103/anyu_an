package com.yunbao.main.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.SPUtils;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.SmallProgramTitleActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.VideoClassBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.main.R;
import com.yunbao.main.adapter.MainHomeVideoAdapter;
import com.yunbao.main.adapter.MainHomeVideoClassAdapter;
import com.yunbao.video.activity.VideoDetailActivity;
import com.yunbao.common.bean.VideoBean;
import com.yunbao.video.event.VideoDeleteEvent;
import com.yunbao.video.event.VideoScrollPageEvent;
import com.yunbao.video.event.VideoViewNumAddEvent;
import com.yunbao.video.http.VideoHttpConsts;
import com.yunbao.video.http.VideoHttpUtil;
import com.yunbao.video.interfaces.VideoScrollDataHelper;
import com.yunbao.video.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by cxf on 2018/9/22.
 * 首页视频（影藏下载）
 */

public class MainHomeVideoViewHolder2 extends AbsMainHomeChildViewHolder implements OnItemClickListener<VideoBean> {

    private CommonRefreshView mRefreshView;
    private MainHomeVideoAdapter mAdapter;
    private VideoScrollDataHelper mVideoScrollDataHelper;
    private RecyclerView mClassRecyclerViewTop;
    private View mShadow;
    private View mBtnDismiss;
    private RecyclerView mClassRecyclerViewDialog;
    private ObjectAnimator mShowAnimator;
    private ObjectAnimator mHideAnimator;
    private List<VideoClassBean> list;
    private String classid="all";
    private MainHomeVideoClassAdapter topAdapter;
    private LinearLayout llClose;
    private RelativeLayout flRoot;
    private TextView titleView;
    private FrameLayout ll_down;
    private MainDownLoadViewHolder mainDownLoadViewHolder;
    public MainHomeVideoViewHolder2(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_video;
    }

    @Override
    public void init() {
        mShadow = findViewById(R.id.shadow);
        mBtnDismiss = findViewById(R.id.btn_dismiss);
        flRoot = (RelativeLayout) findViewById(R.id.fl_root);
        titleView = (TextView) findViewById(R.id.titleView);
        ll_down= (FrameLayout) findViewById(R.id.ll_down);
        //添加开播前设置控件
        mainDownLoadViewHolder = new MainDownLoadViewHolder(mContext, ll_down);
        mainDownLoadViewHolder.addToParent();
        mainDownLoadViewHolder.subscribeActivityLifeCycle();

        mBtnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClick()) {
                    if (mShowAnimator != null) {
                        mShowAnimator.cancel();
                    }
                    if (mHideAnimator != null) {
                        mHideAnimator.start();
                    }
                }
            }
        });

        flRoot.setBackground(mContext.getResources().getDrawable(R.drawable.main_title));
        titleView.setText("视频");

        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live_video);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2;
                }
                return 1;
            }
        });
        mRefreshView.setLayoutManager(gridLayoutManager);
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);

        mAdapter = new MainHomeVideoAdapter(mContext);
        mAdapter.setOnItemClickListener(MainHomeVideoViewHolder2.this);
        mRefreshView.setRecyclerViewAdapter(mAdapter);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<VideoBean>() {
            @Override
            public RefreshAdapter<VideoBean> getAdapter() {
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                L.e("开始请求："+classid);
                mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live_video);
                mRefreshView.hideEmpty();
                VideoHttpUtil.getHomeVideoList(classid,"",p, callback);
            }

            @Override
            public List<VideoBean> processData(String[] info) {
                L.e("成功："+Arrays.toString(info));
                return JSON.parseArray(Arrays.toString(info), VideoBean.class);
            }

            @Override
            public void onRefreshSuccess(List<VideoBean> list, int listCount) {
                if(classid!=null&&classid.equals("all")){
                    VideoStorge.getInstance().put(Constants.VIDEO_HOME, list);
                }
            }

            @Override
            public void onRefreshFailure() {
                if(mAdapter.getItemCount()<=1){
                    mRefreshView.setEmptyLayoutId(R.layout.view_no_net_live_video);
                    mRefreshView.showEmpty();
                    mRefreshView.getEmptyView().findViewById(R.id.tv_try).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mRefreshView.initData();
                        }
                    });
                }
            }

            @Override

            public void onLoadMoreSuccess(List<VideoBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        EventBus.getDefault().register(this);

        View headView = mAdapter.getHeadView();
        if (headView != null) {
            mClassRecyclerViewTop = headView.findViewById(R.id.classRecyclerView_top);
            mClassRecyclerViewTop.setHasFixedSize(true);
            mClassRecyclerViewTop.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        }
        llClose = (LinearLayout) findViewById(R.id.ll_close);
        mClassRecyclerViewDialog = (RecyclerView) findViewById(R.id.classRecyclerView_dialog);
        mClassRecyclerViewDialog.setHasFixedSize(true);
        mClassRecyclerViewDialog.setLayoutManager(new LinearLayoutManager(mContext,  LinearLayoutManager.HORIZONTAL, false));


        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            list = configBean.getVideoclass();
            if (list != null && list.size() > 0) {
//                VideoClassBean bean1 = new VideoClassBean();
//                bean1.setAll(false);
//                bean1.setName("下载");
//                bean1.setChecked(false);
                VideoClassBean bean3 = new VideoClassBean();
                bean3.setAll(true);
                bean3.setName("推荐");
                bean3.setChecked(true);
                if(!list.get(0).getName().equals("推荐")){
                    list.add(0,bean3);
                }
                for (int i = 0; i < list.size(); i++) {
                    if(i==0){
                        list.get(i).setChecked(true);
                    }else {
                        list.get(i).setChecked(false);
                    }
                }
            }
            initTopAdapter(list);
        }
    }

    /**
     * 顶部列表
     * @param list
     */
    private void initTopAdapter(final List<VideoClassBean> list) {
        final List<VideoClassBean> targetList = new ArrayList<>();
        targetList.addAll(list);
        for (int i = 0; i < targetList.size(); i++) {
            if(i==0){
                targetList.get(i).setChecked(true);
            }else {
                targetList.get(i).setChecked(false);
            }
        }
        topAdapter = new MainHomeVideoClassAdapter(mContext, targetList, false);
        topAdapter.setOnItemClickListener(new OnItemClickListener<VideoClassBean>() {
            @Override
            public void onItemClick(VideoClassBean bean, int position) {
                if (!canClick()) {
                    return;
                }
                if (position==0 ) {//全部分类
                    classid="all";
                    mRefreshView.initData();
                } else {
                    classid=bean.getId();
                    mRefreshView.initData();
                }

                for (int i = 0; i < topAdapter.getmList().size(); i++) {
                    if(position==i){
                        topAdapter.getmList().get(i).setChecked(true);
                    }else {
                        topAdapter.getmList().get(i).setChecked(false);
                    }
                }
                topAdapter.notifyDataSetChanged();
            }
        });
        if (mClassRecyclerViewTop != null) {
            mClassRecyclerViewTop.setAdapter(topAdapter);
        }
    }

    /**
     * 是否显示title
     * @param isShow
     */
    public void isShowTitle(boolean isShow){
        flRoot.setVisibility(isShow?View.VISIBLE:View.GONE);
    }

    /**
     * 初始化弹窗动画
     */
    private void initAnim() {
        final int height = mClassRecyclerViewDialog.getHeight();
        mClassRecyclerViewDialog.setTranslationY(-height);
        mShowAnimator = ObjectAnimator.ofFloat(mClassRecyclerViewDialog, "translationY", 0);
        mShowAnimator.setDuration(200);
        mHideAnimator = ObjectAnimator.ofFloat(mClassRecyclerViewDialog, "translationY", -height);
        mHideAnimator.setDuration(200);
        TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();
        mShowAnimator.setInterpolator(interpolator);
        mHideAnimator.setInterpolator(interpolator);
        ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float rate = 1 + ((float) animation.getAnimatedValue() / height);
                mShadow.setAlpha(rate);
            }
        };
        mShowAnimator.addUpdateListener(updateListener);
        mHideAnimator.addUpdateListener(updateListener);
        mHideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mBtnDismiss != null && mBtnDismiss.getVisibility() == View.VISIBLE) {
                    mBtnDismiss.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * 显示分类列表弹窗
     */
    private void showClassListDialog() {
        if (mBtnDismiss != null && mBtnDismiss.getVisibility() != View.VISIBLE) {
            mBtnDismiss.setVisibility(View.VISIBLE);
        }
        if (mShowAnimator != null) {
            mShowAnimator.start();
        }
    }

    @Override
    public void loadData() {
        if (!isFirstLoadData()) {
            return;
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoScrollPageEvent(VideoScrollPageEvent e) {
        if (Constants.VIDEO_HOME.equals(e.getKey()) && mRefreshView != null) {
            mRefreshView.setPageCount(e.getPage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoDeleteEvent(VideoDeleteEvent e) {
        if (mAdapter != null) {
            mAdapter.deleteVideo(e.getVideoId());
            if (mAdapter.getItemCount() == 0 && mRefreshView != null) {
                mRefreshView.showEmpty();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoViewNumAddEvent(VideoViewNumAddEvent e) {
        List<VideoBean> lists = mAdapter.getList();
        for (int i = 0; i <lists.size(); i++) {
            if(e.getVideoId().equals(lists.get(i).getId())){
                lists.get(i).setViewNum(Integer.parseInt(lists.get(i).getViewNum())+1+"");
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemClick(VideoBean bean, int position) {
        int vip = SPUtils.getInstance().getInt(Constants.USER_VIP);
        if(vip<bean.getVip_limit()){
            showLimitDialog(mContext,bean.getVip_limit(),position);
        }else {
            toVideoDetails(position);
        }
    }

    /**
     * 跳转视频详情
     * @param position
     */
    private void toVideoDetails(int position) {
        int page = 1;
        if (mRefreshView != null) {
            page = mRefreshView.getPageCount();
        }
        if (mVideoScrollDataHelper == null) {
            mVideoScrollDataHelper = new VideoScrollDataHelper() {

                @Override
                public void loadData(int p, HttpCallback callback) {
                    VideoHttpUtil.getHomeVideoList(classid,"",p, callback);
                }
            };
        }
        VideoStorge.getInstance().putDataHelper(Constants.VIDEO_HOME, mVideoScrollDataHelper);

        Intent intent = new Intent(CommonAppContext.sInstance, VideoDetailActivity.class);
        intent.putExtra(Constants.VIDEO_POSITION, position);
        intent.putExtra(Constants.VIDEO_KEY,  Constants.VIDEO_HOME);
        intent.putExtra(Constants.VIDEO_PAGE, page);
        mContext.startActivity(intent);
    }

    /**
     * 限制观看dialog
     */
    public void showLimitDialog(final Context context, int vip, final int position) {
        DialogUitl.Builder builder = new DialogUitl.Builder(context);
        builder.setTitle("温馨提示")
                .setContent("亲\uD83D\uDE0A！观看该视频需要达到VIP"+vip+"\n点击获取开通VIP！")
                .setConfrimString("获取VIP")
                .setCancelString("取消")
                .setCancelable(false)
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        new SmallProgramTitleActivity().toActivity(context, CommonAppConfig.getInstance().getConfig().getVideo_limit_rule(),"normal",true);
                    }
                })
                .build()
                .show();
    }

    @Override
    public void release() {
        VideoHttpUtil.cancel(VideoHttpConsts.GET_HOME_VIDEO_LIST);
        EventBus.getDefault().unregister(this);
        mVideoScrollDataHelper = null;


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
}
