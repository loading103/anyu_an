package com.yunbao.main.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.youth.banner.Banner;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.transformer.ScaleInTransformer;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.AdvertBean;
import com.yunbao.common.bean.BannerBean;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.bean.NoticeBean;
import com.yunbao.common.bean.ShopRightBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.event.OnSetChangeEvent;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.live.utils.LiveStorge;
import com.yunbao.main.R;
import com.yunbao.main.activity.LoginNewActivity;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.main.adapter.ImageAdapter;
import com.yunbao.main.adapter.MainHomeLiveAdapter2;
import com.yunbao.main.adapter.MainHomeLiveClassAdapter;
import com.yunbao.main.adapter.MainHomeLiveTopGameAdapter;
import com.yunbao.main.adapter.TabAdapter;
import com.yunbao.main.custom.ScrrollTextView;
import com.yunbao.main.event.LiveEndEvent;
import com.yunbao.main.event.MoreGameEvent;
import com.yunbao.main.event.MoreGameRefreshEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 首页 直播
 */

public class MainHomeLiveViewHolder extends AbsMainHomeChildViewHolder implements OnItemClickListener<LiveBean> {

    private RecyclerView mClassRecyclerViewDialog;
    private View mShadow;
    private View mBtnDismiss;
    private CommonRefreshView mRefreshView;
    private RecyclerView mClassRecyclerViewTop;
    private RecyclerView mClassRecyclerViewTap;
    private RecyclerView mClassRecyclerViewTopGame;
    private MainHomeLiveAdapter2 mAdapter;
    private ObjectAnimator mShowAnimator;
    private ObjectAnimator mHideAnimator;
    private Banner mBanner;
    private boolean mBannerShowed;
    private List<BannerBean> mBannerList;
    private TabAdapter tabAdapter;
    private boolean isAll = true;
    private int mClassId;
    private List<LiveClassBean> datas = new ArrayList<>();
    private MainHomeLiveClassAdapter topAdapter;
    private MainHomeLiveTopGameAdapter gameAdapter;

    private ScrrollTextView tvMarquee;
    private LinearLayout llNotice;
    private NoticeBean mNotice;
    private View topView;
    private View viewNo;
    private ImageView ivClose;
    private LinearLayout llClose;
    private LinearLayout llGame;
    private boolean isFirst;
    private ImageView ivBg;
    private TextView iv_more;
    private TextView iv_more_game;
    private List<LiveClassBean> gameList;
    private JSONObject obj;
    private boolean isFirstTop = true;

    public MainHomeLiveViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_live;
    }

    @Override
    public void init() {
        isFirst = true;
        mShadow = findViewById(R.id.shadow);
        mBtnDismiss = findViewById(R.id.btn_dismiss);
        mBtnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                flag_more=false;
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
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live);
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
        mAdapter = new MainHomeLiveAdapter2(mContext);
        mAdapter.setOnItemClickListener(MainHomeLiveViewHolder.this);
        if (tabAdapter != null && tabAdapter.getData() != null && tabAdapter.getData().size() > 0) {
            ((LiveClassBean) tabAdapter.getData().get(0)).setChecked(true);
            tabAdapter.notifyDataSetChanged();
        }
        mRefreshView.setRecyclerViewAdapter(mAdapter);
//        mRefreshView.getHeader().setBackgroundColor(mContext.getResources().getColor(R.color.color_e53de1));
//        mRefreshView.getSmartRefreshLayout().setBackgroundColor(mContext.getResources().getColor(R.color.color_e53de1));
//        mRefreshView.getFooter().setBackgroundColor(mContext.getResources().getColor(R.color.white));
//        mRefreshView.getHeader().setAccentColor(mContext.getResources().getColor(R.color.white));
        SpUtil.getInstance().setBooleanValue(SpUtil.REFRESH_TOP, true); //点击头部item,记录不刷新
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveBean>() {
            @Override
            public RefreshAdapter<LiveBean> getAdapter() {
                return null;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if (isAll) {
                    MainHttpUtil.getHot(p, callback);
                } else {
                    MainHttpUtil.getClassLive(mClassId, p, callback);
                }
                //顶部数据，只在它初始化和下拉刷新的时候刷新 p==1是下拉刷新
                boolean booleanValue = SpUtil.getInstance().getBooleanValue(SpUtil.REFRESH_TOP);
                if (booleanValue) {
                    getTopClass();
                    SpUtil.getInstance().setBooleanValue(SpUtil.REFRESH_TOP, false);
                }
            }

            @Override
            public List<LiveBean> processData(String[] info) {
                if (isAll) {
                    obj = JSON.parseObject(info[0]);
                    SpUtil.getInstance().setStringValue(SpUtil.FAST_HOME_SLIDE, obj.getString("slide"));
                    SpUtil.getInstance().setStringValue(SpUtil.FAST_HOME_LIST, obj.getString("list"));
                    mBannerList = JSON.parseArray(obj.getString("slide"), BannerBean.class);
                    mNotice = JSON.parseObject(obj.getString("notice"), NoticeBean.class);
                    //下拉刷新精选游戏
                    refreshGameData(obj);
                    return JSON.parseArray(obj.getString("list"), LiveBean.class);
                } else {
                    return JSON.parseArray(Arrays.toString(info), LiveBean.class);
                }
            }

            @Override
            public void onRefreshSuccess(List<LiveBean> list, int count) {
                if (isAll) {
                    LiveStorge.getInstance().put(Constants.LIVE_HOME, list);
                    showBanner();
                    showNotice();
                } else {
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

        View headView = mAdapter.getHeadView();
        if (headView != null) {
            mClassRecyclerViewTop = headView.findViewById(R.id.classRecyclerView_top);
            iv_more = headView.findViewById(R.id.iv_more);
            isMoreGone();
            iv_more.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isMoreGone();
                }
            }, 3000);
            mClassRecyclerViewTop.setHasFixedSize(true);
            mClassRecyclerViewTop.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            //初始化精选游戏
            initGameChoose(headView);
            mClassRecyclerViewTap = headView.findViewById(R.id.classRecyclerView_tab);
            mClassRecyclerViewTap.setHasFixedSize(true);
            mClassRecyclerViewTap.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

            headView.findViewById(R.id.rl_hot).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //热门直播
//                    LiveHotActivity.forward(mContext,"热门直播");
                    if(CommonAppConfig.getInstance().isVisitorLogin()){
                        LoginNewActivity.forwardVisitor(mContext);
                        return;
                    }
                    if (((MainActivity) mContext).isMoreGone()) {
                        return;
                    }
                    EventBus.getDefault().post(new OnSetChangeEvent("-1", mContext));
                }
            });
            topView = headView.findViewById(R.id.iv_top);
            mBanner = headView.findViewById(R.id.banner);
            viewNo = headView.findViewById(R.id.view_no);
            ivBg = headView.findViewById(R.id.iv_bg);
            mBanner.addBannerLifecycleObserver((MainActivity) mContext);
        }
        llClose = (LinearLayout) findViewById(R.id.ll_close);
        mClassRecyclerViewDialog = (RecyclerView) findViewById(R.id.classRecyclerView_dialog);
        mClassRecyclerViewDialog.setHasFixedSize(true);
        mClassRecyclerViewDialog.setLayoutManager(new GridLayoutManager(mContext, 6, GridLayoutManager.VERTICAL, false));

        if (((MainActivity) mContext).getListClass() != null) {
            initTopAdapter(((MainActivity) mContext).getListClass());
        } else {
            getTopClass();
        }

        tabAdapter = new TabAdapter();
        tabAdapter.setOnItemClickListener(new com.chad.library.adapter.base.listener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if(CommonAppConfig.getInstance().isVisitorLogin()){
                    LoginNewActivity.forwardVisitor(mContext);
                    return;
                }
                for (int i = 0; i < adapter.getData().size(); i++) {
                    if (i == position) {
                        ((LiveClassBean) adapter.getData().get(i)).setChecked(true);
                        if (position == 0) {
                            isAll = true;
                        } else {
                            isAll = false;
                            mClassId = ((LiveClassBean) adapter.getData().get(i)).getId();
                        }
                        //TODO 刷新列表
                        mRefreshView.initData();
                    } else {
                        ((LiveClassBean) adapter.getData().get(i)).setChecked(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        if (mClassRecyclerViewTap != null) {
            mClassRecyclerViewTap.setAdapter(tabAdapter);
        }

        if (tabAdapter != null && tabAdapter.getData().size() == 0) {
            ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
            if (configBean != null) {
                List<LiveClassBean> list = configBean.getLiveClass();
                if (list != null && list.size() > 0) {
//                    LiveClassBean bean3 = new LiveClassBean();
//                    bean3.setAll(true);
//                    bean3.setName("热门");
//                    bean3.setChecked(true);
//                    if(!list.get(0).getName().equals("热门")){
//                        list.add(0,bean3);
//                    }
                    for (int i = 0; i < list.size(); i++) {
                        if (i == 0) {
                            list.get(i).setChecked(true);
                        } else {
                            list.get(i).setChecked(false);
                        }
                    }
                    datas = list;
                    tabAdapter.setNewData(list);
                }
            }
        }

        tvMarquee = headView.findViewById(R.id.tv_marquee);
        llNotice = headView.findViewById(R.id.ll_notice);

        if (!TextUtils.isEmpty(CommonAppConfig.getInstance().getHotList())) {
            List<AdvertBean> advertBean = JSON.parseArray(CommonAppConfig.getInstance().getHotList(), AdvertBean.class);
            if (advertBean == null || advertBean.size() == 0) {
                return;
            }
            List<LiveBean> beans = advertBean.get(0).getList();
            mBannerList = advertBean.get(0).getSlide();
            mNotice = advertBean.get(0).getNotice();
            LiveStorge.getInstance().put(Constants.LIVE_HOME, beans);
            if (beans == null) {
                return;
            }
            mAdapter.insertList(beans);
            showBanner();
            showNotice();
        } else {
            //加载缓存数据
            getStorgeData();
        }
        EventBus.getDefault().register(this);
    }


    /**
     * 初始化精选游戏
     */
    public void initGameChoose(View headView) {
        mClassRecyclerViewTopGame = headView.findViewById(R.id.classRecyclerView_game);
        iv_more_game = headView.findViewById(R.id.iv_game_more);
        llGame = headView.findViewById(R.id.ll_game);
        mClassRecyclerViewTopGame.setHasFixedSize(false);
        mClassRecyclerViewTopGame.setLayoutManager(new GridLayoutManager(mContext, 4));
        headView.findViewById(R.id.iv_game_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //精选游戏
                if(CommonAppConfig.getInstance().isVisitorLogin()){
                    LoginNewActivity.forwardVisitor(mContext);
                    return;
                }
                ((MainActivity) mContext).goGameViewHolder();
            }
        });
        gameList = ((MainActivity) mContext).getGameList();
        if (gameList == null) {
            gameList = new ArrayList<>();
        }
        initGameAdapter(gameList);
    }

    /**
     * 是否显示更多游戏箭头
     *
     * @param e
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoreGameEvent(final MoreGameEvent e) {
        if (iv_more_game != null) {
            iv_more_game.setVisibility(e.isHasGame() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * 刷新主页精选游戏
     *
     * @param e
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoreGameRefreshEvent(final MoreGameRefreshEvent e) {
        if (obj != null) {
            refreshGameData(obj);
        }
    }

    /**
     * 下拉刷新精选游戏
     */
    private void refreshGameData(JSONObject obj) {
        List<LiveClassBean> datas = JSON.parseArray(obj.getString("hall_index"), LiveClassBean.class);
        if (llGame != null) {
            if (datas != null) {
                llGame.setVisibility(View.VISIBLE);
                gameList.clear();
                gameList.addAll(datas);
                initGameAdapter(gameList);
            } else {
                gameList.clear();
                llGame.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 精选游戏数据处理
     */
    private void initGameAdapter(List<LiveClassBean> list) {
        if (list == null || list.size() == 0) {
            llGame.setVisibility(View.GONE);
        } else {
            llGame.setVisibility(View.VISIBLE);
            if (list.size() > 8) {
                List<LiveClassBean> datas = new ArrayList<>();
                datas.addAll(list.subList(0, 8));
                list.clear();
                list.addAll(datas);
            }
            if (gameAdapter == null) {
                gameAdapter = new MainHomeLiveTopGameAdapter(mContext, list, false);
                gameAdapter.setOnItemClickListener(new OnItemClickListener<LiveClassBean>() {
                    @Override
                    public void onItemClick(LiveClassBean bean, int position) {
                        if (!canClick()) {
                            return;
                        }
                        if(CommonAppConfig.getInstance().isVisitorLogin()){
                            LoginNewActivity.forwardVisitor(mContext);
                            return;
                        }
//                        OpenUrlUtils.getInstance()
//                                .setContext(mContext)
//                                .setIs_king(bean.getIs_king())
//                                .setJump_type(bean.getJump_type())
//                                .setShopUrl(true)
//                                .setTitle(bean.getName())
//                                .setType(Integer.parseInt(bean.getShow_style()))
//                                .go(bean.getJump_url());
                        OpenUrlUtils.getInstance()
                                .setContext(mContext)
                                .setType(bean.getSlide_show_type())
                                .setSlideTarget(bean.getSlide_target())
                                .setJump_type(bean.getJump_type())
                                .setIs_king(bean.getIs_king())
                                .setTitle(bean.getName())
                                .setSlide_show_type_button(bean.getSlide_show_type_button())
                                .go(bean.getSlide_url());
                    }
                });

                if (mClassRecyclerViewTopGame != null) {
                    mClassRecyclerViewTopGame.setAdapter(gameAdapter);
                }
            } else {
                gameAdapter.notifyDataSetChanged();
            }

        }
    }

    /**
     * 跑马灯
     */
    private void showNotice() {
        if (tvMarquee == null || mNotice == null || TextUtils.isEmpty(mNotice.getNotice_switch())) {
            return;
        }
        if (mNotice.getNotice_switch().equals("1")) {
            llNotice.setVisibility(View.VISIBLE);
            tvMarquee.setList(mNotice.getNotice_txt());
            tvMarquee.startScroll();

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewNo.getLayoutParams();
            lp.topMargin = SizeUtils.dp2px(0);
            viewNo.setLayoutParams(lp);
        } else {
            llNotice.setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewNo.getLayoutParams();
            lp.topMargin = SizeUtils.dp2px(20);
            viewNo.setLayoutParams(lp);
        }
    }

    /**
     * 加载本地缓存数据
     */
    private void getStorgeData() {
        String homeSlideJson = SpUtil.getInstance().getStringValue(SpUtil.FAST_HOME_SLIDE);
        String homeListJson = SpUtil.getInstance().getStringValue(SpUtil.FAST_HOME_LIST);
        if (!TextUtils.isEmpty(homeSlideJson)) {
            mBannerList = JSON.parseArray(homeSlideJson, BannerBean.class);
            showBanner();
        }
        if (!TextUtils.isEmpty(homeListJson)) {
            List<LiveBean> dats = JSON.parseArray(homeListJson, LiveBean.class);
            LiveStorge.getInstance().put(Constants.LIVE_HOME, dats);
            if (mAdapter != null) {
                mAdapter.refreshData(dats);
            }
        }
    }

    private void getTopClass() {
        if (isFirstTop) {
            isFirstTop = false;
            return;
        }

        MainHttpUtil.getAdvert("10", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<LiveClassBean> list = JSON.parseArray(Arrays.toString(info), LiveClassBean.class);
                    initTopAdapter(list);
                }
            }
        });
    }

    /**
     * 顶部列表
     *
     * @param list
     */
    private void initTopAdapter(List<LiveClassBean> list) {
        if (list.size() == 0) {
            topView.setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mBanner.getLayoutParams();
            lp.topMargin = SizeUtils.dp2px(12);
            mBanner.setLayoutParams(lp);
            ivBg.setImageResource(R.mipmap.icon_m_t);
        } else {
            topView.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mBanner.getLayoutParams();
            lp.topMargin = SizeUtils.dp2px(0);
            mBanner.setLayoutParams(lp);
            ivBg.setImageResource(R.mipmap.icon_m_t2);
        }

        List<LiveClassBean> targetList = new ArrayList<>();
        if (list.size() <= 6) {
            targetList.addAll(list);
        } else {
            targetList.addAll(list.subList(0, 5));

            LiveClassBean bean2 = new LiveClassBean();
            bean2.setAll(true);
            bean2.setName("更多");
            targetList.add(bean2);
        }
//        targetList.clear();
        topAdapter = new MainHomeLiveClassAdapter(mContext, targetList, false);
        topAdapter.setOnItemClickListener(new OnItemClickListener<LiveClassBean>() {
            @Override
            public void onItemClick(LiveClassBean bean, int position) {
                if (!canClick()) {

                    return;
                }
                if(CommonAppConfig.getInstance().isVisitorLogin()){
                    LoginNewActivity.forwardVisitor(mContext);
                    return;
                }
                if (bean.isAll()) {//全部分类
                    showClassListDialog();
                } else {
//                  LiveClassActivity.forward(mContext, bean.getId(), bean.getName());
//                    new SmallProgramActivity().toActivity(mContext,bean.getSlide_url());

                    OpenUrlUtils.getInstance().setContext(mContext)
                            .setType(bean.getSlide_show_type())
                            .setSlideTarget(bean.getSlide_target())
                            .setJump_type(bean.getJump_type())
                            .setIs_king(bean.getIs_king())
                            .setSlide_show_type_button(bean.getSlide_show_type_button())
                            .go(bean.getSlide_url());
                }
            }
        });
        if (mClassRecyclerViewTop != null) {
            mClassRecyclerViewTop.setAdapter(topAdapter);
        }


        MainHomeLiveClassAdapter dialogAdapter = new MainHomeLiveClassAdapter(mContext, list, true);
        dialogAdapter.setOnItemClickListener(new OnItemClickListener<LiveClassBean>() {
            @Override
            public void onItemClick(LiveClassBean bean, int position) {
                if (!canClick()) {
                    return;
                }
                if(CommonAppConfig.getInstance().isVisitorLogin()){
                    LoginNewActivity.forwardVisitor(mContext);
                    return;
                }
//                LiveClassActivity.forward(mContext, bean.getId(), bean.getName());
//                new SmallProgramActivity().toActivity(mContext,bean.getSlide_url());
                if (mShowAnimator != null) {
                    mShowAnimator.cancel();
                }
                if (mHideAnimator != null) {
                    mHideAnimator.start();
                }
                OpenUrlUtils.getInstance().setContext(mContext)
                        .setType(bean.getSlide_show_type())
                        .setSlideTarget(bean.getSlide_target())
                        .setJump_type(bean.getJump_type())
                        .setIs_king(bean.getIs_king())
                        .setSlide_show_type_button(bean.getSlide_show_type_button())
                        .go(bean.getSlide_url());
            }
        });
        mClassRecyclerViewDialog.setAdapter(dialogAdapter);
        mClassRecyclerViewDialog.post(new Runnable() {
            @Override
            public void run() {
                initAnim();
            }
        });
    }


    public void hideAnimator() {
        if (mHideAnimator != null) {
            mHideAnimator.start();
        }
    }


    private void showBanner() {
        if (mBannerList == null || mBannerList.size() == 0 || mBanner == null) {
            return;
        }
//        if (mBannerShowed) {
//            return;
//        }
//        mBannerShowed = true;
        mBanner.setAdapter(new ImageAdapter(mBannerList, mContext))
                .setIndicator(new CircleIndicator(mContext))
                .setIndicatorSelectedColorRes(R.color.white)
                .setIndicatorNormalColorRes(R.color.color_838586)
                .setIndicatorGravity(IndicatorConfig.Direction.CENTER)
                .setIndicatorWidth(SizeUtils.dp2px(8), SizeUtils.dp2px(8))
                .setBannerRound2(SizeUtils.dp2px(10))
//                .setBannerGalleryEffect(18,8)
                //添加透明效果(画廊配合透明效果更棒)
                .addPageTransformer(new ScaleInTransformer())
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(Object data, int position) {

                        if(CommonAppConfig.getInstance().isVisitorLogin()){
                            LoginNewActivity.forwardVisitor(mContext);
                            return;
                        }

                        if (mBannerList != null) {
                            if (position >= 0 && position < mBannerList.size()) {
                                BannerBean bean = mBannerList.get(position);
                                L.e("WOLF","mBannerList:"+position);
                                L.e("WOLF","mBannerList:"+bean.toString());
                                if (bean != null) {
                                    OpenUrlUtils.getInstance().setContext(mContext)
                                            .setType(bean.getSlide_show_type())
                                            .setSlideTarget(bean.getSlide_target())
                                            .setJump_type(bean.getJump_type())
                                            .setIs_king(bean.getIs_king())
                                            .setTitle(bean.getName())
                                            .go(bean.getLink());
//                                    WebViewActivity.forwardRight(mContext, bean.getLink(), bean.getName(), true);
                                }
                            }
                        }
                    }
                })
                .start();
    }


    /**
     * 初始化弹窗动画
     */
    private void initAnim() {
        if (!isFirst) {
            return;
        } else {
            isFirst = false;
        }
        final int height = llClose.getHeight();
        llClose.setTranslationY(-height);
        mShowAnimator = ObjectAnimator.ofFloat(llClose, "translationY", 0);
        mShowAnimator.setDuration(200);
        mHideAnimator = ObjectAnimator.ofFloat(llClose, "translationY", -height);
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
//        flag_more=true;
        if (mBtnDismiss != null && mBtnDismiss.getVisibility() != View.VISIBLE) {
            mBtnDismiss.setVisibility(View.VISIBLE);
        }
        if (mShowAnimator != null) {
            mShowAnimator.start();
        }
    }


    @Override
    public void onItemClick(LiveBean bean, int position) {
        if(CommonAppConfig.getInstance().isVisitorLogin()){
            LoginNewActivity.forwardVisitor(mContext);
            return;
        }
        watchLive(bean, Constants.LIVE_HOME, position);
    }

//    private Handler handler=new android.os.Handler()
    @Override
    public void loadData() {
        if (mRefreshView != null) {
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
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }


    public void setCurrentPosition(String id) {
        if (tabAdapter != null && tabAdapter.getData() != null && tabAdapter.getData().size() > 0) {
            for (int i = 0; i < tabAdapter.getData().size(); i++) {
                if (tabAdapter.getData().get(i).getId() == Integer.parseInt(id)) {
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
                    }, 200);
                } else {
                    ((LiveClassBean) tabAdapter.getData().get(i)).setChecked(false);
                }
            }
            tabAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveEndEvent(final LiveEndEvent e) {
        L.e("WOLF", "直播已结束:" + e.getBean().getUid());
        List<LiveBean> list = ((List<LiveBean>) mAdapter.getList());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUid().equals(e.getBean().getUid())) {
                loadData();
            }
        }

    }

    public void isMoreGone() {
        if (((MainActivity) mContext).isMoreGone()) {
            if (iv_more != null) {
                iv_more.setVisibility(View.GONE);
            }
        }
    }
}
