package com.yunbao.main.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.jpush.Gson;
import com.google.gson.jpush.reflect.TypeToken;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.ViewPagerAdapter;
import com.yunbao.common.bean.AdvertBean;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.bean.ShopLeftBean;
import com.yunbao.common.bean.ShopRightBean;
import com.yunbao.common.bean.ShopTopBean;
import com.yunbao.common.bean.TabBarBean;
import com.yunbao.common.bean.VideoClassBean;
import com.yunbao.common.custom.TabButton;
import com.yunbao.common.custom.TabButtonGroup;
import com.yunbao.common.event.DownNotifyDataEvent;
import com.yunbao.common.event.LoginForBitEvent;
import com.yunbao.common.event.LoginInvalidEvent;
import com.yunbao.common.event.MainFinishEvent;
import com.yunbao.common.event.MainShowDowningEvent;
import com.yunbao.common.event.OnSetChangeEvent;
import com.yunbao.common.event.UnClickEvent;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.LocationUtil;
import com.yunbao.common.utils.MemoryUtils;
import com.yunbao.common.utils.ProcessResultUtil;
import com.yunbao.common.utils.RandomUtil;
import com.yunbao.common.utils.ScreenShotListenManager;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.VersionUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.activity.ChatActivity;
import com.yunbao.im.event.ImUnReadCountEvent;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.im.utils.ImPushUtil;
import com.yunbao.live.GetAppIDConfig;
import com.yunbao.live.LiveConfig;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.bean.LiveKsyConfigBean;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.utils.LiveStorge;
import com.yunbao.live.utils.PreferenceUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.BonusBean;
import com.yunbao.main.dialog.IMNoticeDiglog;
import com.yunbao.main.dialog.MainStartDialogFragment;
import com.yunbao.main.dialog.VoiceCallDialogFragment;
import com.yunbao.main.event.HeadUpdateEvent;
import com.yunbao.main.event.MoreGameEvent;
import com.yunbao.main.event.RegSuccessEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.interfaces.MainAppBarLayoutListener;
import com.yunbao.main.interfaces.MainStartChooseCallback;
import com.yunbao.main.presenter.CheckLivePresenter;
import com.yunbao.main.views.AbsMainViewHolder;
import com.yunbao.main.views.MainChargeViewHolder;
import com.yunbao.main.views.MainDowningViewHolder;
import com.yunbao.main.views.MainGameViewHolder;
import com.yunbao.main.views.MainHomeVideoViewHolder;
import com.yunbao.main.views.MainHomeViewHolder;
import com.yunbao.main.views.MainListViewHolder;
import com.yunbao.main.views.MainLiveViewHolder;
import com.yunbao.main.views.MainMeViewHolder;
import com.yunbao.main.views.MainShopViewHolder;
import com.yunbao.main.views.NewBonusViewHolder;
import com.yunbao.video.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.yunbao.live.utils.PreferenceUtil.KEY_APP_ID;
import static com.yunbao.live.utils.PreferenceUtil.KEY_APP_SIGN;


public class MainActivity extends AbsActivity implements MainAppBarLayoutListener {
    private static final String BUNDLE_FRAGMENTS_KEY = "android:support:fragments";
    private ViewGroup mRootView;
    private TabButtonGroup mTabButtonGroup;
    private ViewPager mViewPager;
    private List<FrameLayout> mViewList;
    private MainHomeViewHolder mHomeViewHolder;
    //    private MainNearViewHolder mNearViewHolder;
    private MainLiveViewHolder mNearViewHolder;
    //    private MainGameViewHolder mListViewHolder;
    private MainMeViewHolder mMeViewHolder;
    private MainGameViewHolder mGameViewHolder;
    private MainHomeVideoViewHolder mVideoViewHolder;
    private MainListViewHolder mMainListViewHolder;
    private MainDowningViewHolder mMainDowningHolder;
    private MainShopViewHolder mShopViewHolder;
    private MainChargeViewHolder mChargeViewHolder;
    private AbsMainViewHolder[] mViewHolders;
    private View mBottom;
    private int mDp70;
    private ProcessResultUtil mProcessResultUtil;
    private CheckLivePresenter mCheckLivePresenter;
    private boolean mFristLoad;
    private long mLastClickBackTime;//上次点击back键的时间
    private HttpCallback mGetLiveSdkCallback;
    private List<VideoClassBean> list;
    private List<LiveClassBean> tab3;
    private LiveClassBean tab4;
    private List<LiveClassBean> topList;
    private List<LiveClassBean> gameList;
    private List<ShopTopBean> shopList;
    private List<ShopRightBean> shopRightList;
    private TabButton tb4;
    public String name;
    private LiveClassBean tab4_launcher;
    private ImageView btnService;
    private AdvertBean advertBean;
    private List<LiveBean> listHot;
    private ImageView iv_login;
    private RelativeLayout rl_root;

    private boolean hasGame;//是否有游戏大厅

    public List<LiveClassBean> getListClass() {
        return listClass;
    }

    public List<LiveClassBean> getGameList() {
        return gameList;
    }

    private List<LiveClassBean> listClass;

    private boolean haveShouYe = true; //有直播或是首页  提示电话推荐
    private boolean isMoreGone = false; //有首页，没得直播   隐藏更多
    public List<TabBarBean.ListBean> tabdatas;
    public   boolean  isFristHot=true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && this.clearFragmentsTag()) {
            //重建时清除 fragment的状态
            savedInstanceState.remove(BUNDLE_FRAGMENTS_KEY);
        }
        super.onCreate(savedInstanceState);
    }

    protected boolean clearFragmentsTag() {
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null && this.clearFragmentsTag()) {
            //销毁时不保存fragment的状态
            outState.remove(BUNDLE_FRAGMENTS_KEY);
        }
    }


//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setLoginIcon();
//        if(mHomeViewHolder!=null){
//            mHomeViewHolder.setVisitor();
//        }
//        if(mViewPager!=null){
//            mViewPager.setCurrentItem(0);
//            mTabButtonGroup.setCheck(0);
//        }
//        String stringValue = SpUtil.getInstance().getStringValue(SpUtil.FAST_TABBAR_LIST);
//        setTabBarVisible(stringValue);
//        //游客模式   游客从主界面到登陆成功成用户回主界面调用
//        if(!isShowSign){
//            requestBonus();
//        }
//    }

    public void  setLoginIcon(){
        if(iv_login==null){
            return;
        }
        if(CommonAppConfig.getInstance().isVisitorLogin()){
            iv_login.setVisibility(View.VISIBLE);
            iv_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginNewActivity.forwardVisitor(mContext);
                }
            });
        }else {
            iv_login.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean forbitSootSceenEnble() {
        return true;
    }

    @Override
    protected void main() {
        iv_login=(ImageView) findViewById(R.id.iv_login);
        boolean showInvite = getIntent().getBooleanExtra(Constants.SHOW_INVITE, false);
        //每次进入让协议状态清空
        SpUtil.getInstance().setBooleanValue(SpUtil.VIDEO_XIEYI,false);
        SpUtil.getInstance().setBooleanValue(SpUtil.RECHARGE_XIEYI,false);
        //根据内容会动态添加tabbar;
        getTabData();
        advertBean = (AdvertBean) getIntent().getSerializableExtra("advertBean");
        if (advertBean != null) {
            topList = advertBean.getApp_title();
            tab4_launcher = advertBean.getAPP_tab_4();
            listClass = advertBean.getApp_nav();
            gameList = advertBean.getHall_index();
        } else {
            String s = CommonAppConfig.getInstance().getAdvert();
            if (!TextUtils.isEmpty(s)) {
                AdvertBean bean = JSON.parseObject(s, AdvertBean.class);
                topList = bean.getApp_title();
                tab4_launcher = bean.getAPP_tab_4();
                listClass = bean.getApp_nav();
//                gameList = advertBean.getHall_index();
                CommonAppConfig.getInstance().setAdvert("");
            }
        }
        mRootView = findViewById(R.id.rootView);
        mTabButtonGroup = findViewById(R.id.tab_group);
        mViewPager = findViewById(R.id.viewPager);
        tb4 = findViewById(R.id.tb4);
        mViewPager.setOffscreenPageLimit(5);
        rl_root = findViewById(R.id.rl_root);
        mMainDowningHolder = new MainDowningViewHolder(mContext, rl_root);
        mMainDowningHolder.addToParent();
        mMainDowningHolder.subscribeActivityLifeCycle();

        mViewList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                loadPageData(position, true);
                if (mViewHolders != null) {
                    for (int i = 0, length = mViewHolders.length; i < length; i++) {
                        AbsMainViewHolder vh = mViewHolders[i];
                        if (vh != null) {
                            vh.setShowed(position == i);
                        }
                    }
                }

                //如果位置是视屏,则刷新VIP接口
                if (position == getVideoPosition()) {
                    getLimitData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabButtonGroup.setViewPager(mViewPager);
        mViewHolders = new AbsMainViewHolder[5];
        mDp70 = DpUtil.dp2px(70);
        mBottom = findViewById(R.id.bottom);
        mProcessResultUtil = new ProcessResultUtil(this);

        loadPageData(0, true);

        EventBus.getDefault().register(this);
        checkVersion();
        loginIM();
        CommonAppConfig.getInstance().setLaunched(true);
        getTab4();
//        getMainMeData();
        //判断显示哪个table
//        String tabString = getIntent().getStringExtra("tabBar");

        String tabString = SpUtil.getInstance().getStringValue(SpUtil.FAST_TABBAR_LIST);
        setTabBarVisible(tabString);
        setLoginIcon();
        if (PreferenceUtil.getInstance().getStringValue(KEY_APP_ID, "").equals("")) {
            PreferenceUtil.getInstance().setStringValue(KEY_APP_ID, String.valueOf(GetAppIDConfig.appID));
        }
        if (PreferenceUtil.getInstance().getStringValue(KEY_APP_SIGN, "").equals("")) {
            PreferenceUtil.getInstance().setStringValue(KEY_APP_SIGN, GetAppIDConfig.appSign);
        }
    }


    /**
     * 截屏弹窗
     */
    @Override
    protected void listenerScreenShot() {
        screenManager.setListener(
                new ScreenShotListenManager.OnScreenShotListener() {
                    public void onShot(final String imagePath) {
                        if (ActivityUtils.getTopActivity() instanceof MainActivity && CommonAppConfig.getInstance().isFrontGround()) {
                            if(CommonAppConfig.getInstance().isVisitorLogin()){
                                return;
                            }
                            getFotBitShotData();
                        }
                    }
                });

    }

    /**
     * 请求接口判断截屏是警告还是封号
     */
    private void getFotBitShotData() {
        CommonHttpUtil.getFotBitShotData(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                //10000是警告，20000是封号
                if (code == 20000) {
                    showForBitDialog();
                } else {
                    DialogUitl.showSimpleTipDialog(MainActivity.this, "严重警告", "禁止截图和录屏，再次使用将会被封号!");
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                DialogUitl.showSimpleTipDialog(MainActivity.this, "严重警告", "禁止截图和录屏，再次使用将会被封号!");

            }
        });

    }

    /**
     * 截屏弹窗封号点击推出
     */
    protected void showForBitDialog() {
        DialogUitl.showSimpleTipCallDialog(MainActivity.this, "处罚通知", "检测到你在APP内多次使用截屏以及录屏功能，现对你进行封号处罚，如有疑问请与客服联系！", new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                EventBus.getDefault().post(new LoginInvalidEvent());
                CommonAppConfig.getInstance().clearLoginInfo();
                //退出极光
                ImMessageUtil.getInstance().logoutImClient();
                ImPushUtil.getInstance().logout();
                //友盟统计登出
                MobclickAgent.onProfileSignOff();
                LoginNewActivity.forward();
                finish();
            }
        });
    }

    /**
     * 检测到被封号处罚
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(LoginForBitEvent e) {
        EventBus.getDefault().post(new LoginInvalidEvent());
        CommonAppConfig.getInstance().clearLoginInfo();
        //退出极光
        ImMessageUtil.getInstance().logoutImClient();
        ImPushUtil.getInstance().logout();
        //友盟统计登出
        MobclickAgent.onProfileSignOff();
        LoginNewActivity.forward();
        finish();
    }

    /**
     * 提前获取我的界面的顶部数据
     */
    private void getMainMeData() {
        MainHttpUtil.getAdvert("30", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    SpUtil.getInstance().setStringValue(SpUtil.FAST_HME_LIST, Arrays.toString(info));
                }
            }
        });
    }

    /**
     * 顶部列表新增标题
     *
     * @return
     */
    public List<LiveClassBean> getTopList() {
        return topList;
    }

    /**
     * 视频详情限制等级
     */
    private void getLimitData() {
        CommonHttpUtil.getUserVip(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject bean = JSON.parseObject(info[0]);
                    SPUtils.getInstance().put(Constants.USER_VIP, bean.getIntValue("vip"));
                }
            }
        });
    }

    private void getTab4() {
        List<AdvertBean> advertBean = JSON.parseArray(CommonAppConfig.getInstance().getHotList(), AdvertBean.class);
        if(advertBean==null || advertBean.size()==0 || advertBean.get(0)==null ){
            return;
        }
        tab4 = advertBean.get(0).getAPP_tab_4();
        mFristLoad = true;
        if (tab4 != null) {
            tb4.setText(tab4.getName());
            name = tab4.getName();
        } else {
            if (mTabButtonGroup != null && mTabButtonGroup.getChildCount() > 3) {
                mTabButtonGroup.getChildAt(3).setVisibility(View.GONE);
            }
        }
//        loadPageData(0, true);
        try {
            getShopData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 购物大厅预加载
     */
    private void getShopData() {
        MainHttpUtil.getShopData(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    try {
                        SpUtil.getInstance().setStringValue(SpUtil.SHOPONE, Arrays.toString(info));
                        shopList = JSON.parseArray(Arrays.toString(info), ShopTopBean.class);
                        if (shopList != null && shopList.size() > 0 && shopList.get(0).getChildren() != null && shopList.get(0).getChildren().size() > 0) {
                            shopRightList = shopList.get(0).getChildren().get(0).getChildren();
                        }
                    } catch (Exception e) {
                        SpUtil.getInstance().setStringValue(SpUtil.SHOPONE, "");
                    }
                } else {
                    getStorgeShopData();
                }

            }

            @Override
            public void onError() {
                getStorgeShopData();
            }

            @Override
            public void onError(String message) {
                getStorgeShopData();
            }
        });
    }

    /**
     * 获取本地缓存(顶部)
     */
    private void getStorgeShopData() {
        String data = SpUtil.getInstance().getStringValue(SpUtil.SHOPONE);
        if (!TextUtils.isEmpty(data)) {
            shopList = JSON.parseArray(data, ShopTopBean.class);
            if (shopList != null && shopList.size() > 0) {
                List<ShopLeftBean> children = shopList.get(0).getChildren();
                if (children != null && children.size() > 0) {
                    shopRightList = children.get(0).getChildren();
                }
            }
        }
    }


    public List<ShopRightBean> getShopRightList() {
        return shopRightList;
    }

    public List<ShopTopBean> getShopList() {
        return shopList;
    }

    public void mainClick(View v) {
        if (!canClick()) {
            return;
        }
        if(CommonAppConfig.getInstance().isVisitorLogin()){
            LoginNewActivity.forwardVisitor(mContext);
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_start) {
//            showStartDialog();
            mViewPager.setCurrentItem(2);
            mTabButtonGroup.setCheck(2);
        } else if (i == R.id.btn_search) {
            SearchActivity.forward(mContext);
        } else if (i == R.id.btn_msg) {
            ChatActivity.forward(mContext);
        } else if (i == R.id.btn_service) {
            if (CommonAppConfig.getInstance().getConfig().getCustomer_service().equals("0")) {
                ToastUtil.show("客服系统异常，请稍后再试");
            } else {
                ConfigBean.CustomerServiceData data = CommonAppConfig.getInstance().getConfig().getCustomer_service_data();
                String url = data.getSlide_url();

                OpenUrlUtils.getInstance().setContext(mContext)
                        .setType(Integer.parseInt(data.getSlide_show_type()))
                        .setSlideTarget(data.getSlide_target())
                        .setTitle(data.getName())
                        .setJump_type(data.getJump_type())
                        .setIs_king("0")
                        .setSlide_show_type_button(data.getSlide_show_type_button())
                        .go(url);
            }
        } else if (i == R.id.btn_rank) {//排行榜
            startActivity(new Intent(MainActivity.this, RankActivity.class));
//            HtmlConfig htmlConfig=new HtmlConfig();
//            WebViewActivity.forward(mContext,htmlConfig.getRank()+CommonAppConfig.getInstance().getUid());
        }
    }

    private void showStartDialog() {
        MainStartDialogFragment dialogFragment = new MainStartDialogFragment();
        dialogFragment.setMainStartChooseCallback(mMainStartChooseCallback);
        dialogFragment.show(getSupportFragmentManager(), "MainStartDialogFragment");
    }

    private MainStartChooseCallback mMainStartChooseCallback = new MainStartChooseCallback() {
        @Override
        public void onLiveClick() {
            mProcessResultUtil.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            }, mStartLiveRunnable);
        }

        @Override
        public void onVideoClick() {
            mProcessResultUtil.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            }, mStartVideoRunnable);
        }
    };

    private Runnable mStartLiveRunnable = new Runnable() {
        @Override
        public void run() {
            if (CommonAppConfig.LIVE_SDK_CHANGED) {
                if (mGetLiveSdkCallback == null) {
                    mGetLiveSdkCallback = new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                try {
                                    JSONObject obj = JSON.parseObject(info[0]);
                                    LiveAnchorActivity.forward(mContext, obj.getIntValue("live_sdk"), JSON.parseObject(obj.getString("android"), LiveKsyConfigBean.class));
                                } catch (Exception e) {
                                    LiveAnchorActivity.forward(mContext, CommonAppConfig.LIVE_SDK_USED, LiveConfig.getDefaultKsyConfig());
                                }
                            }
                        }
                    };
                }
                String memory=MemoryUtils.getTotalMemory(mContext).replace("GB","").trim();
                LiveHttpUtil.getLiveSdk(memory,mGetLiveSdkCallback);
            } else {
                LiveAnchorActivity.forward(mContext, CommonAppConfig.LIVE_SDK_USED, LiveConfig.getDefaultKsyConfig());
            }
        }
    };


    private Runnable mStartVideoRunnable = new Runnable() {
        @Override
        public void run() {
//            startActivity(new Intent(mContext, VideoRecordActivity.class));
        }
    };

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (configBean.getMaintainSwitch() == 1) {//开启维护
                        DialogUitl.showSimpleTipDialog(mContext, WordUtil.getString(R.string.main_maintain_notice), configBean.getMaintainTips());
                    }
                    if (!VersionUtil.isLatest(configBean.getVersion())) {
                        if (!TextUtils.isEmpty(configBean.getAndroid_download_state()) && configBean.getAndroid_download_state().equals("1")) {
                            showFouseDialog(mContext, configBean.getUpdateDes(), configBean.getDownloadApkUrl());
                        } else {
                            showDialog(mContext, configBean.getUpdateDes(), configBean.getDownloadApkUrl());
                        }
                    } else {
                        requestBonus();
                    }
                }
            }
        });
    }

    /**
     * 填写邀请码
     */
    private void showInvitationCode() {
        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.main_input_invatation_code), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(final Dialog dialog, final String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.main_input_invatation_code);
                    return;
                }
                MainHttpUtil.setDistribut(content, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                            dialog.dismiss();
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }
        });
    }

    /**
     * 签到奖励
     */
    private void requestBonus() {
        MainHttpUtil.requestBonus(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (obj.getIntValue("bonus_switch") == 0) {
                        showKnowNotice(obj);
                        return;
                    }
                    int day = obj.getIntValue("bonus_day");
                    if (day <= 0) {
                        showKnowNotice(obj);
                        return;
                    }
                    Log.e("签到结果：", obj.toJSONString());
                    List<BonusBean> list = JSON.parseArray(obj.getString("bonus_list"), BonusBean.class);
                    NewBonusViewHolder bonusViewHolder = new NewBonusViewHolder(list, day, obj.getString("count_day"), obj);
                    bonusViewHolder.show(getSupportFragmentManager(), "NewBonusViewHolderFragment");
                }
            }
        });
    }


    /**
     * 知道了的通知框LOAD_DEFAULT
     */
    public void showKnowNotice(JSONObject obj) {
        String title = obj.getString("pop_title");
        String content = obj.getString("pop_content");
        int popSwitch = obj.getIntValue("pop_switch");
        if (popSwitch == 0 && haveShouYe) {
            //TODO 无公告，显示语音通话界面
            if (ActivityUtils.getTopActivity() instanceof MainActivity) {
                if (SpUtil.getInstance().getBooleanValue(SpUtil.NEW_USER) && CommonAppConfig.getInstance().getConfig().getConversation_switch().equals("1")) {
                    getLiveRecommend();
                }
            }
            return;
        }
        if (ActivityUtils.getTopActivity() instanceof MainActivity && haveShouYe) {
            IMNoticeDiglog imNoticeDiglog = new IMNoticeDiglog(this, title, content);
            imNoticeDiglog.setCancerClick(new IMNoticeDiglog.CloseListener() {
                @Override
                public void onCancelClick() {
                    //TODO 公告显示完毕，显示语音通话界面
                    if (SpUtil.getInstance().getBooleanValue(SpUtil.NEW_USER) && CommonAppConfig.
                            getInstance().getConfig().getConversation_switch().equals("1")) {
                        getLiveRecommend();
                    }
//                    getLiveRecommend();
                }
            });
            imNoticeDiglog.showNoticeDiglog();
        }
    }

    private void getLiveRecommend() {
        MainHttpUtil.getLiveRecommend(1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<LiveBean> beans = JSON.parseArray(Arrays.toString(info), LiveBean.class);

                Iterator<LiveBean> it_b = beans.iterator();
                while (it_b.hasNext()) {
                    LiveBean a = it_b.next();
//                    if(a.getIsvideo().equals("1") || a.getIs_pri().equals("1")){
//                        it_b.remove();
//                    }
                    if (a.getIs_pri().equals("1")) {
                        it_b.remove();
                    }
                }

                LiveStorge.getInstance().put(Constants.LIVE_RECOMMEND, beans);

                if (beans.size() > 1) {
                    int p = RandomUtil.nextInt(beans.size() - 1);
                    showCallDialog(beans.get(p), p);
                } else if (beans.size() == 1) {
                    showCallDialog(beans.get(0), 0);
                }
            }
        });
    }

    /**
     * 电话dialog
     *
     * @param liveBean
     */
    private void showCallDialog(LiveBean liveBean, int position) {
        final VoiceCallDialogFragment dialogFragment = new VoiceCallDialogFragment();
        dialogFragment.setBean(liveBean);
        dialogFragment.setPosition(position);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isDestroyed() && ActivityUtils.getTopActivity() instanceof MainActivity) {
                    dialogFragment.show(getSupportFragmentManager(), "VoiceCallDialogFragment");
                }
            }
        }, 3000);
    }

    /**
     * 登录IM
     */
    private void loginIM() {
        String uid = CommonAppConfig.getInstance().getUid();
        ImMessageUtil.getInstance().loginImClient(uid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mFristLoad) {
                mFristLoad = false;
//            getLocation();
//            loadPageData(0, false);
                if (mHomeViewHolder != null) {
                    mHomeViewHolder.setShowed(true);
                }
                if (ImPushUtil.getInstance().isClickNotification()) {//MainActivity是点击通知打开的
                    ImPushUtil.getInstance().setClickNotification(false);
                    int notificationType = ImPushUtil.getInstance().getNotificationType();
                    if (notificationType == Constants.JPUSH_TYPE_LIVE) {
                        if (mHomeViewHolder != null) {
                            mHomeViewHolder.setCurrentPage(0);
                        }
                    } else if (notificationType == Constants.JPUSH_TYPE_MESSAGE) {
                        //第一次不调用
                        if (mHomeViewHolder != null && !isFristHot) {
                            isFristHot=false;
                            mHomeViewHolder.setCurrentPage(1);
                        }
                    }
                    ImPushUtil.getInstance().setNotificationType(Constants.JPUSH_TYPE_NONE);
                } else {
                    //第一次不调用
                    if (mHomeViewHolder != null && !isFristHot) {
                        isFristHot=false;
                        mHomeViewHolder.setCurrentPage(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所在位置
     */
    private void getLocation() {
        L.e("定位", "开始定位");
        mProcessResultUtil.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, new Runnable() {
            @Override
            public void run() {
                LocationUtil.getInstance().startLocation();
            }
        });
    }

    @Override
    protected void onDestroy() {

        if (mTabButtonGroup != null) {
            mTabButtonGroup.cancelAnim();
        }

        EventBus.getDefault().unregister(this);
        LiveHttpUtil.cancel(LiveHttpConsts.GET_LIVE_SDK);
        MainHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        MainHttpUtil.cancel(MainHttpConsts.REQUEST_BONUS);
        MainHttpUtil.cancel(MainHttpConsts.GET_BONUS);
        MainHttpUtil.cancel(MainHttpConsts.SET_DISTRIBUT);
        if (mCheckLivePresenter != null) {
            mCheckLivePresenter.cancel();
        }
        LocationUtil.getInstance().stopLocation();
        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
        CommonAppConfig.getInstance().setGiftListJson(null);
        CommonAppConfig.getInstance().setLaunched(false);
        LiveStorge.getInstance().clear();
        VideoStorge.getInstance().clear();
        super.onDestroy();
    }

    public static void forward(Context context, AdvertBean advertBean, boolean finish) {
        getTabBarVisible(1, context, advertBean, false, finish, false);
    }

    public static void forward(Context context, boolean finish) {
        getTabBarVisible(2, context, null, false, finish, false);
    }

    public static void forward(Context context, boolean showInvite, boolean finish) {
        getTabBarVisible(3, context, null, showInvite, finish, false);
    }

    public static void forward(Context context, boolean showInvite, boolean finish, boolean isResigest) {
        getTabBarVisible(3, context, null, showInvite, finish, isResigest);
    }

    /**
     * [{"id":"3","name":"购物大厅"},{"id":"4","name":"视频"},{"id":"5","name":"我的"}]
     * isResigest是不是从注册界面来的  要发送关闭登录界面的指令
     */
    public static void getTabBarVisible(final int type, final Context context, final AdvertBean advertBean, final boolean showInvite, final boolean finish, final boolean isResigest) {
        startActivity(type, context, advertBean, showInvite, finish, isResigest);
    }

    /**
     * 如果不返回 ，默认显示首页
     */
    public static void startActivity(int type, Context context, AdvertBean advertBean, boolean showInvite, boolean finish, boolean isResigest) {
        if (type == 1) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(Constants.SHOW_INVITE, false);
            intent.putExtra("advertBean", (Serializable) advertBean);
            context.startActivity(intent);
            if (finish) {
                ((Activity) context).finish();
            }
            if (isResigest) {
                EventBus.getDefault().post(new RegSuccessEvent());
            }
        }
        if (type == 2) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(Constants.SHOW_INVITE, false);
            context.startActivity(intent);
            if (finish) {
                ((Activity) context).finish();
            }
            if (isResigest) {
                EventBus.getDefault().post(new RegSuccessEvent());
            }
        }
        if (type == 3) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(Constants.SHOW_INVITE, showInvite);
            context.startActivity(intent);
            if (finish) {
                ((Activity) context).finish();
            }
            if (isResigest) {
                EventBus.getDefault().post(new RegSuccessEvent());
            }
        }
    }


    public  void  getTabData(){
        String tabString = SpUtil.getInstance().getStringValue(SpUtil.FAST_TABBAR_LIST);
        if (TextUtils.isEmpty(tabString)) {
            tabdatas=null;
            return;
        }
        try {
            tabdatas = new Gson().fromJson(tabString, new TypeToken<List<TabBarBean.ListBean>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
    /**
     * 判断显示哪个table
     */
    private void setTabBarVisible(String tabString) {
        //全部关闭，只显示主页
        if (TextUtils.isEmpty(tabString)) {
            setTotleBarGone();
            return;
        }
        try {
            tabdatas = new Gson().fromJson(tabString, new TypeToken<List<TabBarBean.ListBean>>() {}.getType());
            if (tabdatas == null || tabdatas.size() == 0) { //如果不返回 ，默认显示首页
                setTotleBarGone();
                return;
            }
            if(mTabButtonGroup!=null){
                mTabButtonGroup.setData(tabdatas);
            }

            if (tabdatas.size() == 5 && mTabButtonGroup != null) { //如果为 ，默认不处理
                for (int i = 0; i < mTabButtonGroup.getChildCount(); i++) {
                    ((TabButton) mTabButtonGroup.getChildAt(i)).setVisibility(View.VISIBLE);
                    ((TabButton) mTabButtonGroup.getChildAt(i)).setText(tabdatas.get(i).getName());

                    if (tabdatas.get(i).getId().equals("3")) {
                        SpUtil.getInstance().setStringValue(SpUtil.SHOP_NAME, tabdatas.get(i).getName());
                        if (mShopViewHolder != null && !TextUtils.isEmpty(tabdatas.get(i).getName())) {
                            mShopViewHolder.setTitleName(tabdatas.get(i).getName());
                        }
                    }
                    if (tabdatas.get(i).getId().equals("4")) {
                        SpUtil.getInstance().setStringValue(SpUtil.VIDEO_NAME, tabdatas.get(i).getName());
                        if (mVideoViewHolder != null && !TextUtils.isEmpty(tabdatas.get(i).getName())) {
                            mVideoViewHolder.setTitleName(tabdatas.get(i).getName());
                        }
                    }
                }
                hasGame = true;
                return;
            }


            if (tabdatas.size() == 1) {
                mBottom.setVisibility(View.GONE);
            } else {
                mBottom.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < mTabButtonGroup.getChildCount(); i++) {
                mTabButtonGroup.getChildAt(i).setVisibility(View.GONE);
            }
            for (int i = 0; i < tabdatas.size(); i++) {
                if (i == 0) {   //默认显示第一个界面
                    mViewPager.setCurrentItem(i);
                }
                if (tabdatas.size() != 1) { //只有一个开启 底部不显示
                    mTabButtonGroup.getChildAt(i).setVisibility(View.VISIBLE);
                    ((TabButton) mTabButtonGroup.getChildAt(i)).setText(tabdatas.get(i).getName());
                }
            }
            //没得主页和直播,不弹推荐视屏
            haveShouYe = false;

            boolean haveSy = false;
            boolean haveZb = false;
            for (int i = 0; i < tabdatas.size(); i++) {
                if (tabdatas.get(i).getId().equals("1") || tabdatas.get(i).getId().equals("2")) {
                    haveShouYe = true;
                }
                if (tabdatas.get(i).getId().equals("1")) {
                    haveSy = true;
                }
                if (tabdatas.get(i).getId().equals("2")) {
                    haveZb = true;
                }
                if (tabdatas.get(i).getId().equals("3")) {
                    SpUtil.getInstance().setStringValue(SpUtil.SHOP_NAME, tabdatas.get(i).getName());
                    if (mShopViewHolder != null && !TextUtils.isEmpty(tabdatas.get(i).getName())) {
                        mShopViewHolder.setTitleName(tabdatas.get(i).getName());
                    }
                    hasGame = true;
                }
                if (tabdatas.get(i).getId().equals("4")) {
                    SpUtil.getInstance().setStringValue(SpUtil.VIDEO_NAME, tabdatas.get(i).getName());
                    if (mVideoViewHolder != null && !TextUtils.isEmpty(tabdatas.get(i).getName())) {
                        mVideoViewHolder.setTitleName(tabdatas.get(i).getName());
                    }
                }
            }
            isMoreGone = haveShouYe && !haveZb;

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        EventBus.getDefault().post(new MoreGameEvent(hasGame));

    }

    /**
     * 全部关闭，只显示主页
     */
    public void setTotleBarGone() {
        if (mTabButtonGroup != null && mTabButtonGroup.getChildCount() > 0) {
            for (int i = 0; i < mTabButtonGroup.getChildCount(); i++) {
                mTabButtonGroup.getChildAt(i).setVisibility(View.GONE);
            }
            mBottom.setVisibility(View.GONE);
        }
    }

    /**
     * 观看直播
     */
    public void watchLive(LiveBean liveBean, String key, int position) {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        mCheckLivePresenter.watchLive(liveBean, key, position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
        String unReadCount = e.getUnReadCount();
        if (!TextUtils.isEmpty(unReadCount)) {
            if (mHomeViewHolder != null) {
                mHomeViewHolder.setUnReadCount(unReadCount);
            }
            if (mNearViewHolder != null) {
                mNearViewHolder.setUnReadCount(unReadCount);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (rl_root != null && mMainDowningHolder != null && rl_root.getVisibility() == View.VISIBLE) {
            rl_root.setVisibility(View.INVISIBLE);
            mMainDowningHolder.edit(false);
            return;
        }
        long curTime = System.currentTimeMillis();
        if (curTime - mLastClickBackTime > 2000) {
            mLastClickBackTime = curTime;
            ToastUtil.show(R.string.main_click_next_exit);
            return;
        }
        super.onBackPressed();
    }


    private void loadPageData(int position, boolean needlLoadData) {
        if (mViewHolders == null) {
            return;
        }
        if (mHomeViewHolder != null) {
            mHomeViewHolder.setLiveTopMiss();
        }
        AbsMainViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                Log.e("AbsMainViewHolder------",position+"");
                if (position == 0) {
                    vh =  addViewHolderPosition(position,vh,parent);
                    mTabButtonGroup.setCheckeded(0,true);
                } else if (position == 1) {
                    vh = addViewHolderPosition(position,vh,parent);
                } else if (position == 2) {
                    vh =  addViewHolderPosition(position,vh,parent);
                } else if (position == 3) {
                    vh =  addViewHolderPosition(position,vh,parent);
                } else if (position == 4) {
                    vh =  addViewHolderPosition(position,vh,parent);
                } else if (position == 5) {
                    vh =  addViewHolderPosition(position,vh,parent);
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
            }
        }
        if (needlLoadData && vh != null) {
            vh.loadData();
        }
    }

    private AbsMainViewHolder addViewHolderPosition(int position,  AbsMainViewHolder vh,  FrameLayout parent ) {
        if(tabdatas==null || tabdatas.size()==0){
            mHomeViewHolder = new MainHomeViewHolder(mContext, parent);
            mHomeViewHolder.setAppBarLayoutListener(this);
            vh = mHomeViewHolder;
            return vh;
        }
        if(tabdatas.get(position).getId().equals("1")){
            mHomeViewHolder = new MainHomeViewHolder(mContext, parent);
            mHomeViewHolder.setAppBarLayoutListener(this);
            vh = mHomeViewHolder;

        }else  if(tabdatas.get(position).getId().equals("2")){
            mNearViewHolder = new MainLiveViewHolder(mContext, parent);
            mNearViewHolder.setAppBarLayoutListener(this);
            vh = mNearViewHolder;
        }else  if(tabdatas.get(position).getId().equals("3")){
            mShopViewHolder = new MainShopViewHolder(mContext, parent);
            vh = mShopViewHolder;
        }else  if(tabdatas.get(position).getId().equals("4")){
            mVideoViewHolder = new MainHomeVideoViewHolder(mContext, parent);
            mVideoViewHolder.isShowTitle(true);
            vh = mVideoViewHolder;
        }else  if(tabdatas.get(position).getId().equals("5")){
            mMeViewHolder = new MainMeViewHolder(mContext, parent);
            vh = mMeViewHolder;
        }else  if(tabdatas.get(position).getId().equals("6")){
            mChargeViewHolder = new MainChargeViewHolder(mContext, parent,null);
            vh = mChargeViewHolder;
        }
        addIconPosition();
        return vh;
    }

    private void addIconPosition() {
        for (int i = 0; i < tabdatas.size(); i++) {
            if(tabdatas.get(i).getId().equals("1")){
                ((TabButton) mTabButtonGroup.getChildAt(i)).setSelectedIcon(R.drawable.main_first);
                ((TabButton) mTabButtonGroup.getChildAt(i)).setUnSelectedIcon(R.mipmap.tab1_no);
                ((TabButton) mTabButtonGroup.getChildAt(i)).setText(tabdatas.get(i).getName());
            }else  if(tabdatas.get(i).getId().equals("2")){
                ((TabButton) mTabButtonGroup.getChildAt(i)).setSelectedIcon(R.drawable.main_scond);
                ((TabButton) mTabButtonGroup.getChildAt(i)).setUnSelectedIcon(R.mipmap.tab2_no);
                ((TabButton) mTabButtonGroup.getChildAt(i)).setText(tabdatas.get(i).getName());
            }else  if(tabdatas.get(i).getId().equals("3")){
                ((TabButton) mTabButtonGroup.getChildAt(i)).setSelectedIcon(R.drawable.main_third);
                ((TabButton) mTabButtonGroup.getChildAt(i)).setUnSelectedIcon(R.mipmap.tab3_no);
                ((TabButton) mTabButtonGroup.getChildAt(i)).setText(tabdatas.get(i).getName());
            }else  if(tabdatas.get(i).getId().equals("4")){
                ((TabButton) mTabButtonGroup.getChildAt(i)).setSelectedIcon(R.drawable.main_third_new);
                ((TabButton) mTabButtonGroup.getChildAt(i)).setUnSelectedIcon(R.mipmap.tab3_new_no);
                ((TabButton) mTabButtonGroup.getChildAt(i)).setText(tabdatas.get(i).getName());
            }else  if(tabdatas.get(i).getId().equals("5")){
                ((TabButton) mTabButtonGroup.getChildAt(i)).setSelectedIcon(R.drawable.main_fourth);
                ((TabButton) mTabButtonGroup.getChildAt(i)).setUnSelectedIcon(R.mipmap.tab4_no);
                ((TabButton) mTabButtonGroup.getChildAt(i)).setText(tabdatas.get(i).getName());
            } else  if(tabdatas.get(i).getId().equals("6")){
                ((TabButton) mTabButtonGroup.getChildAt(i)).setSelectedIcon(R.drawable.main_fiveth);
                ((TabButton) mTabButtonGroup.getChildAt(i)).setUnSelectedIcon(R.mipmap.tab5_no);
                ((TabButton) mTabButtonGroup.getChildAt(i)).setText(tabdatas.get(i).getName());
            }
        }
    }

    @Override
    public void onOffsetChanged(float rate) {
//        if (mBottom != null) {
//            float curY = mBottom.getTranslationY();
//            float targetY = rate * mDp70;
//            if (curY != targetY) {
//                mBottom.setTranslationY(targetY);
//            }
//        }
    }

    /**
     * 通知界面跳转到相应的直播类型分类
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final OnSetChangeEvent event) {
        String id = event.getId();
        Context context = event.getContext();
        boolean b = context instanceof MainActivity;
        if (!b) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        if (mViewPager != null && mTabButtonGroup != null) {
            mTabButtonGroup.setViewger(1);
        }
        if (mNearViewHolder != null) {
            if (id.equals("-1")) {
                mNearViewHolder.loadDataByPosition(1);
            } else {
                mNearViewHolder.loadData(id);
            }
        }
    }

    public String getTab3Name() {
        return name;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainJsEvent(final HeadUpdateEvent e) {
        if (mMeViewHolder != null) {
            mMeViewHolder.updataHeader(e.getUrl());
        }
    }

    /**
     * 版本更新
     *
     * @param context
     * @param versionTip
     * @param downloadUrl
     */
    public void showDialog(final Context context, String versionTip, final String downloadUrl) {
        DialogUitl.Builder builder = new DialogUitl.Builder(context);
        builder.setTitle(WordUtil.getString(com.yunbao.common.R.string.version_update))
                .setContent(versionTip)
                .setConfrimString(WordUtil.getString(com.yunbao.common.R.string.version_immediate_use))
                .setCancelString(WordUtil.getString(com.yunbao.common.R.string.version_not_update))
                .setCancelable(false)
                .setClickCallback(new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {
                        requestBonus();
                    }

                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        if (!TextUtils.isEmpty(downloadUrl)) {
                            try {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                intent.setData(Uri.parse(downloadUrl));
                                context.startActivity(intent);
                            } catch (Exception e) {
                                ToastUtil.show(com.yunbao.common.R.string.version_download_url_error);
                            }
                        } else {
                            ToastUtil.show(com.yunbao.common.R.string.version_download_url_error);
                        }
                    }
                })
                .build()
                .show();
    }

    /**
     * 版本更新(q强制更新)
     */
    public void showFouseDialog(final Context context, String versionTip, final String downloadUrl) {
        final Dialog dialog = new Dialog(context, com.yunbao.common.R.style.dialog2);
        dialog.setContentView(com.yunbao.common.R.layout.dialog_simple_tip);
        TextView contentTextView = dialog.findViewById(com.yunbao.common.R.id.content);
        TextView titleView = dialog.findViewById(com.yunbao.common.R.id.title);
        TextView btn_confirm = dialog.findViewById(com.yunbao.common.R.id.btn_confirm);
        btn_confirm.setText("立即更新");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        titleView.setText(WordUtil.getString(com.yunbao.common.R.string.version_update));
        if (!TextUtils.isEmpty(versionTip)) {
            contentTextView.setText(versionTip);
        }
        dialog.findViewById(com.yunbao.common.R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(downloadUrl)) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.setData(Uri.parse(downloadUrl));
                        context.startActivity(intent);
                    } catch (Exception e) {
                        ToastUtil.show(com.yunbao.common.R.string.version_download_url_error);
                    }
                } else {
                    ToastUtil.show(com.yunbao.common.R.string.version_download_url_error);
                }
            }
        });
        dialog.show();
    }


    /**
     * 打开下载界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainJsEvent(final MainShowDowningEvent e) {
        if (rl_root != null && mMainDowningHolder != null) {
            if (e.isIsshow()) {
                rl_root.setVisibility(View.VISIBLE);
                if (mMainDowningHolder != null) {
                    mMainDowningHolder.setVisible();
                }
            } else {
                rl_root.setVisibility(View.INVISIBLE);
                mMainDowningHolder.edit(false);
            }
        }
    }

    /**
     * 更新下载界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainJsEvent(final DownNotifyDataEvent e) {
        if (mMainDowningHolder != null) {
            mMainDowningHolder.postNotifyDataChanged();
        }
    }

    /**
     * 点击退出登录时调用
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainJsEvent(MainFinishEvent e) {
        finish();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UnClick(UnClickEvent e) {
        LoginNewActivity.forwardVisitor(mContext);
    }
    public boolean isMoreGone() {
        return isMoreGone;
    }

    public void setMoreGone(boolean moreGone) {
        isMoreGone = moreGone;
    }

    /**
     * 回去底部是不是隐藏
     */
    public boolean getBottomVisible() {
        if (mBottom != null) {
            if (mBottom.getVisibility() == View.VISIBLE) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 跳转到游戏大厅
     */
    public void goGameViewHolder() {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(2);
            mTabButtonGroup.setCheckeded(2);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                EventBus.getDefault().post(new MoreGameRefreshEvent());
//            }
//        }, 1000);

    }


    private int getVideoPosition() {
        if(tabdatas==null  || tabdatas.size()==0){
            return 0;
        }
        for (int i = 0; i < tabdatas.size(); i++) {
            if(tabdatas.get(i).getId().equals("4")){
                return i;
            }
        }
        return -1;
    }




}
