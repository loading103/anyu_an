package com.yunbao.main.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.AppBarLayout;
import com.lzy.okgo.model.Response;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.bean.LiveGiftBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.GifCacheUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.LoginNewActivity;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.common.utils.AppUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 首页
 */

public class MainHomeViewHolder extends AbsMainHomeParentViewHolder2 implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private MainHomeFollowViewHolder mFollowViewHolder;
    private MainHomeLiveViewHolder mLiveViewHolder;
    private MainHomeVideoViewHolder mVideoViewHolder;
    private MainHomeAccessViewHolder mAccessViewHolder;
    private List<LiveClassBean> list;
    private ImageView btnService;
    private ImageView iv_logo;
    private String agent_url;
    private String agent_pic;
    private AppBarLayout appBarLayout;


    public MainHomeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home;
    }


    @Override
    public void init() {

        super.init();
        btnService=(ImageView) findViewById(R.id.btn_service);
        iv_logo=(ImageView) findViewById(R.id.iv_logo);

        appBarLayout=(AppBarLayout) findViewById(R.id.appBarLayout);
        iv_logo.setOnClickListener(this);
        list=((MainActivity)mContext).getTopList();

        agent_url = SpUtil.getInstance().getStringValue(SpUtil.AGENT_URL);
        agent_pic = SpUtil.getInstance().getStringValue(SpUtil.AGENT_PIC);
        setIvLogoVisible();
        setVisitor();
        initScreen();

        if(list!=null){
            showTitle();
//            loadData();
        }else {
            MainHttpUtil.getAdvert("11",new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        L.e("WOLF", Arrays.toString(info));
                        list = JSON.parseArray(Arrays.toString(info), LiveClassBean.class);
                        showTitle();
//                        loadData();
                    }else {
                        list=new ArrayList<>();
                        showTitle();
//                        loadData();
                    }
                }

                @Override
                public void onError(Response<JsonBean> response) {
                    super.onError(response);
                    list=new ArrayList<>();
                    showTitle();
//                    loadData();
                }
            });
        }

        if(mViewPager!=null){
            mViewPager.addOnPageChangeListener(this);
        }

        if(!CommonAppConfig.getInstance().isVisitorLogin()){
            cacheGift();
        }
    }

    /**
     * 缓存礼物
     */
    private void cacheGift() {
        LiveHttpUtil.getGiftList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String giftJson = obj.getString("giftlist");
                    List<LiveGiftBean> list = JSON.parseArray(giftJson, LiveGiftBean.class);
                    CommonAppConfig.getInstance().setGiftListJson(giftJson);
                    L.e("gif礼物"+"----------->判断缓存");
                   for(LiveGiftBean bean:list){
                       if (bean.getUrl().endsWith(".gif") ||bean.getUrl().endsWith(".svga")) {
//                           L.e("gif礼物"+"----------->缓存:"+bean.getName());
                           GifCacheUtil.getFile(Constants.GIF_GIFT_PREFIX + bean.getId(), bean.getUrl(), new CommonCallback<File>() {
                               @Override
                               public void callback(File bean) {
                               }
                           });
                       }
                    }
                }
            }
        });
    }

    /**
     * 是游客显示登陆按钮
     */
    public void setVisitor() {
        if(CommonAppConfig.getInstance().isVisitorLogin()){
            if(mViewPager!=null){
                mViewPager.setCurrentItem(1);

            }
            if(mViewPager!=null){
                mViewPager.setNoScroll(true);
            }
        }else {
            if(mViewPager!=null){
                mViewPager.setNoScroll(false);
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

    @Override
    public void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        AbsMainHomeChildViewHolder vh = mViewHolders[position];
        setLiveTopMiss();
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mFollowViewHolder = new MainHomeFollowViewHolder(mContext, parent);
                    iv_logo.setVisibility(View.GONE);
                    vh = mFollowViewHolder;
                } else if (position == 1) {
                    mLiveViewHolder = new MainHomeLiveViewHolder(mContext, parent);
                    setIvLogoVisible();
                    vh = mLiveViewHolder;
                } /*else if (position == 2) {
                    mVideoViewHolder = new MainHomeVideoViewHolder(mContext, parent);
                    mVideoViewHolder.setShowed(false);
                    iv_logo.setVisibility(View.GONE);
                    vh = mVideoViewHolder;
                }*/ else {
                    mAccessViewHolder = new MainHomeAccessViewHolder(mContext, parent,list.get(position-2));
                    iv_logo.setVisibility(View.GONE);
                    vh = mAccessViewHolder;
                }
                if (vh == null) {
                    return;
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
        return 2+list.size();
    }

    @Override
    protected String[] getTitles() {
        List<String> strList=new ArrayList();
        strList.add( WordUtil.getString(R.string.follow));
        strList.add( WordUtil.getString(R.string.live_main));
//        strList.add( WordUtil.getString(R.string.video));
        for (LiveClassBean bean:list) {
            strList.add(bean.getName());
        }
        return (String[])strList.toArray(new String[strList.size()]);
    }

    public void setCurrentPosition(String id){
        if(mLiveViewHolder!=null){
            mLiveViewHolder.setCurrentPosition(id);
//            mLiveViewHolder.setCurrentPosition(id);
        }
    }


    public void  setLiveTopMiss(){
        if(mLiveViewHolder!=null){
            mLiveViewHolder.hideAnimator();
        }
    }

    public void hindService(){
        btnService.setVisibility(View.GONE);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        loadPageData(position);
        if(position==1){
            setIvLogoVisible();
        }else {
            iv_logo.setVisibility(View.GONE);
        }
        appBarLayout.setExpanded(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 显示图标
     */
    public void  setIvLogoVisible(){
        if(TextUtils.isEmpty(agent_pic)){
            iv_logo.setVisibility(View.GONE);
        }else {
            iv_logo.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(agent_pic)
//                    .placeholder(com.yunbao.common.R.mipmap.im_icon_stub_loading)
//                    .error(com.yunbao.common.R.mipmap.im_icon_stub)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(iv_logo);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.iv_logo){
            if(CommonAppConfig.getInstance().isVisitorLogin()){
                LoginNewActivity.forwardVisitor(mContext);
                return;
            }
            if(!TextUtils.isEmpty(agent_url)){
                WebViewActivity.forwardNo(mContext, agent_url);
            }
        }
    }
}
