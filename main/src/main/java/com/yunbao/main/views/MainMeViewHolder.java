package com.yunbao.main.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.google.android.material.appbar.AppBarLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.model.Response;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.AdvertBean;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.bean.TokenUtilsBean;
import com.yunbao.common.bean.UrlBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.bean.UserItemBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ProcessResultUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.im.activity.ChatActivity;
import com.yunbao.live.LiveConfig;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.activity.LiveRecordActivity;
import com.yunbao.live.activity.RoomManageActivity;
import com.yunbao.live.bean.LiveKsyConfigBean;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.utils.CheckAudioPermission;
import com.yunbao.main.R;
import com.yunbao.main.activity.EditProfileActivity;
import com.yunbao.main.activity.FansActivity;
import com.yunbao.main.activity.FollowActivity;
import com.yunbao.main.activity.LoginNewActivity;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.main.activity.MyCoinActivity;
import com.yunbao.main.activity.MyProfitActivity;
import com.yunbao.main.activity.MyVideoActivity;
import com.yunbao.main.activity.SettingActivity;
import com.yunbao.main.adapter.MainMeAdapter;
import com.yunbao.main.adapter.MineTopAdapter;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.common.utils.AppUtil;
import com.yunbao.common.utils.MemoryUtils;
import com.yunbao.video.activity.VideoMyRecordActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.blankj.utilcode.util.ActivityUtils.startActivity;
import static com.yunbao.common.utils.ClickUtil.isFastClick;
import static com.yunbao.common.utils.RouteUtil.PATH_USER_HOME;

import com.yunbao.common.utils.AnimatorUtil;

import okhttp3.OkHttpClient;

/**
 * Created by cxf on 2018/9/22.
 * 我的
 */

public class MainMeViewHolder extends AbsMainViewHolder implements OnItemClickListener<UserItemBean>, View.OnClickListener {

    private AppBarLayout mAppBarLayout;
    private TextView mTtileView;
    private ImageView mAvatar;
    private TextView mName;
    private ImageView mSex;
    private ImageView mLevelAnchor;
    private ImageView mLevel;
    private TextView mID;
    private TextView mLive;
    private TextView mFollow;
    private TextView mFans;
    private boolean mPaused;
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerTop;
    private MainMeAdapter mAdapter;
    private LinearLayout mllRoot;
    private View mBg2;
    private ProcessResultUtil mProcessResultUtil;
    private HttpCallback mGetLiveSdkCallback;
    private List<LiveClassBean> datas = new ArrayList<>();
    private TextView tvMoney;
    private TextView tv_jf;
    private static final int MAX_DURATION = 15000;//最大录制时间15s
    private LinearLayout llTop;
    private LinearLayout llRight;
    private LinearLayout ll_money;
    private ImageView mSex2;
    private ImageView mLevelAnchor2;
    private ImageView mLevel2;
    private TextView mID2;
    private AnimatorUtil animatorUtil;
    private ImageView mIvVip;
    private View botView;
    private FrameLayout top;
    private static final int MAX_DURATIONS = 15000;//最大录制时间15s
    private OkHttpClient mOkHttpClient;
    private int testPushTimes = 0;
    private UrlBean bean;

    public MainMeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_me;
    }

    @Override
    public void init() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @SuppressLint("NewApi")
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float totalScrollRange = appBarLayout.getTotalScrollRange();
                float rate = -1 * verticalOffset / totalScrollRange;
                if (mTtileView != null) {
                    mTtileView.setAlpha(rate);
                }
            }
        });

        mTtileView = (TextView) findViewById(R.id.titleView);
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mSex = (ImageView) findViewById(R.id.sex);
        mSex2 = (ImageView) findViewById(R.id.sex2);
        mLevelAnchor = (ImageView) findViewById(R.id.level_anchor);
        mLevelAnchor2 = (ImageView) findViewById(R.id.level_anchor2);
        mLevel = (ImageView) findViewById(R.id.level);
        mLevel2 = (ImageView) findViewById(R.id.level2);
        mID = (TextView) findViewById(R.id.id_val);
        mID2 = (TextView) findViewById(R.id.id_val2);
        mLive = (TextView) findViewById(R.id.live);
        mFollow = (TextView) findViewById(R.id.follow);
        mFans = (TextView) findViewById(R.id.fans);
        mRecyclerTop = (RecyclerView) findViewById(R.id.recyclerview);
        mllRoot = (LinearLayout) findViewById(R.id.ll_root);
        ll_money = (LinearLayout) findViewById(R.id.ll_money);
        tvMoney = (TextView) findViewById(R.id.tv_money);
        tv_jf = (TextView) findViewById(R.id.tv_jf);
        top = (FrameLayout) findViewById(R.id.top);
        mBg2 = findViewById(R.id.bg_2);
        botView = findViewById(R.id.botView);
        setViewVisible();
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        llRight = (LinearLayout) findViewById(R.id.ll_right);
        mIvVip = (ImageView) findViewById(R.id.iv_leve);
        findViewById(R.id.btn_edit).setOnClickListener(this);
        findViewById(R.id.btn_live).setOnClickListener(this);
        findViewById(R.id.btn_follow).setOnClickListener(this);
        findViewById(R.id.btn_fans).setOnClickListener(this);
        animatorUtil = new AnimatorUtil();
        mProcessResultUtil = new ProcessResultUtil((MainActivity) mContext);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        initScreen();

        loadCacheData();
    }

    /**
     * 挖孔屏适配
     */
    private void initScreen() {
        if (AppUtil.CutOutSafeHeight > 0) {
            RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) top.getLayoutParams();
            linearParams.height = SizeUtils.dp2px(SizeUtils.px2dp(AppUtil.CutOutSafeHeight) + 40);
            top.setLayoutParams(linearParams);
        }
    }

    private void loadCacheData() {
        if (!TextUtils.isEmpty(CommonAppConfig.getInstance().getHotList())) {
//            AdvertBean bean = JSON.parseObject(CommonAppConfig.getInstance().getHotList(), AdvertBean.class);
            List<AdvertBean> advertBean = JSON.parseArray(CommonAppConfig.getInstance().getHotList(), AdvertBean.class);
            if (advertBean == null || advertBean.size() == 0) {
                return;
            }
            AdvertBean bean = advertBean.get(0);
            SpUtil.getInstance().setStringValue(SpUtil.FAST_HME_LIST, JSON.toJSONString(bean.getAPP_mynav()));
            datas = bean.getAPP_mynav();
            if (datas == null || datas.size() == 0) {
                mllRoot.setVisibility(View.GONE);
                mBg2.setVisibility(View.GONE);
                return;
            }
            mllRoot.setVisibility(View.VISIBLE);
            mBg2.setVisibility(View.VISIBLE);
            setTopRecycle(datas);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowed() && mPaused) {
            loadData();
        }
        mPaused = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
    }

    @Override
    public void loadData() {
        setViewVisible();
        if (isFirstLoadData()) {
            CommonAppConfig appConfig = CommonAppConfig.getInstance();
            UserBean u = appConfig.getUserBean();

            List<UserItemBean> list = appConfig.getUserItemList();
            if (u != null && list != null) {
                showData(u, list);
            }
            //顶部数据
            String stringValue = SpUtil.getInstance().getStringValue(SpUtil.FAST_HME_LIST);
            getStorteData(stringValue);
            if (TextUtils.isEmpty(stringValue)) {
                MainHttpUtil.getAdvert("30", new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            L.e("----", Arrays.toString(info));
                            SpUtil.getInstance().setStringValue(SpUtil.FAST_HME_LIST, Arrays.toString(info));
                            datas = JSON.parseArray(Arrays.toString(info), LiveClassBean.class);
                            if (datas == null || datas.size() == 0) {
                                mllRoot.setVisibility(View.GONE);
                                mBg2.setVisibility(View.GONE);
                                return;
                            }
                            mllRoot.setVisibility(View.VISIBLE);
                            mBg2.setVisibility(View.VISIBLE);
                            setTopRecycle(datas);
                        }
                    }
                });
            }
//            getShopGoods();

        }
        MainHttpUtil.getBaseInfo(mCallback);


    }

    /**
     * 获取是不是有商品
     */
    private void getShopGoods() {
        LiveHttpUtil.getGoods("", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                isClick=true;
                if (code == 0) {
                    Log.e("----getShopGoods-", Arrays.toString(info));
                    List<LiveReadyBean> datas = JSON.parseArray(Arrays.toString(info), LiveReadyBean.class);
                    if (datas == null || datas.size() == 0) {
                        SpUtil.getInstance().setBooleanValue(SpUtil.HAVEGOODS, false);
                    } else {
                        SpUtil.getInstance().setBooleanValue(SpUtil.HAVEGOODS, true);
                    }

                    String allUrl = SpUtil.getInstance().getStringValue(SpUtil.ALL_URL);
                    L.e("WOLF","allUrl:"+allUrl);
                    if (!TextUtils.isEmpty(allUrl)) {
                        bean = JSON.parseObject(allUrl, UrlBean.class);
                        List<UrlBean.RhbyPathBean.PushBean> list_push = bean.getRhbyPath().getPush();
                        initClient(3000, 1);
                        for (int i = 0; i < list_push.size(); i++) {
                            L.e("WOLF","push_url:"+list_push.get(i).getPush());
                            startTestPush(list_push.get(i).getTest_url(), i, list_push.get(i).getPush());
                        }
                    } else {
                        toLive();
                    }
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                isClick=true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingNewDialog(mContext);
            }
        });
    }

    /**
     * 初始化网络
     *
     * @param TIMEOUT
     */
    private void initClient(int TIMEOUT, int count) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        builder.retryOnConnectionFailure(true);
        mOkHttpClient = builder.build();

        OkGo.getInstance().init(CommonAppContext.sInstance)
                .setOkHttpClient(mOkHttpClient)
                .setCacheMode(CacheMode.NO_CACHE)
                .setRetryCount(count);
    }

    /**
     * 选择最快推流
     *
     * @param url
     * @param position
     * @param push
     */
    private void startTestPush(final String url, final int position, final String push) {
        OkGo.<String>get(url)
                .tag(position)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        OkGo.cancelAll(mOkHttpClient);
                        L.e("WOLF","最快推流:"+push);
                        SpUtil.getInstance().setStringValue(SpUtil.FAST_PUSH, push);
                        toLive();
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        testPushTimes++;
                        if (testPushTimes == bean.getRhbyPath().getPush().size()) {
                            testPushTimes = 0;
                            L.e("WOLF","最快推流:"+bean.getRhbyPath().getPush().get(0).getPush());
                            SpUtil.getInstance().setStringValue(SpUtil.FAST_PUSH, bean.getRhbyPath().getPush().get(0).getPush());
                            toLive();
                        }
                    }
                });
    }

    private void toLive() {
        mProcessResultUtil.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        }, mStartLiveRunnable);
    }

    /**
     * 加载缓存数据
     */
    private void getStorteData(String stringValue) {
        if (!TextUtils.isEmpty(stringValue)) {
            datas = JSON.parseArray(stringValue, LiveClassBean.class);
            if (datas == null || datas.size() == 0) {
                mllRoot.setVisibility(View.GONE);
                mBg2.setVisibility(View.GONE);
                return;
            }
            mllRoot.setVisibility(View.VISIBLE);
            mBg2.setVisibility(View.VISIBLE);
            setTopRecycle(datas);
        }
    }


    public void setTopRecycle(List<LiveClassBean> list) {
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, list.size() == 1 ? 4 : list.size());
//        if(list.size()==1){
//            ViewGroup.LayoutParams layoutParams = mRecyclerTop.getLayoutParams();
//            layoutParams.width= ViewGroup.LayoutParams.WRAP_CONTENT;
//            mRecyclerTop.setLayoutParams(layoutParams);
//        }
        if (list.size() == 1) {
            LiveClassBean bean = new LiveClassBean();
            bean.setIs_null(true);
            list.add(bean);
            list.add(bean);
            list.add(bean);
        }
        mRecyclerTop.setLayoutManager(layoutManager); //设置布局管理器
        MineTopAdapter adapter = new MineTopAdapter(mContext, list);
        mRecyclerTop.setAdapter(adapter);
    }

    private CommonCallback<UserBean> mCallback = new CommonCallback<UserBean>() {
        @Override
        public void callback(UserBean bean) {
            List<UserItemBean> list = CommonAppConfig.getInstance().getUserItemList();
            if (bean != null) {
                showData(bean, list);
            }
        }
    };

    private void showData(UserBean u, List<UserItemBean> list) {
//        for (int i = 0; i < list.size(); i++) {
//            if(list.get(i).getId()==30){
//                list.remove(i);
//            }
//        }

        ImgLoader.displayAvatar(CommonAppContext.sInstance, u.getAvatar(), mAvatar);
        mTtileView.setText(u.getUserNiceName());
        mName.setText(u.getUserNiceName());
        setVipLeve(u.getVip());

        mSex.setImageResource(CommonIconUtil.getSexIcon(u.getSex()));
        mSex2.setImageResource(CommonIconUtil.getSexIcon(u.getSex()));
        CommonAppConfig appConfig = CommonAppConfig.getInstance();
        LevelBean anchorLevelBean = appConfig.getAnchorLevel(u.getLevelAnchor());
        if (anchorLevelBean != null) {
            ImgLoader.display(CommonAppContext.sInstance, anchorLevelBean.getThumb(), mLevelAnchor);
            ImgLoader.display(CommonAppContext.sInstance, anchorLevelBean.getThumb(), mLevelAnchor2);
        }
        LevelBean levelBean = appConfig.getLevel(u.getLevel());
        if (levelBean != null) {
            ImgLoader.display(CommonAppContext.sInstance, levelBean.getThumb(), mLevel);
            ImgLoader.display(CommonAppContext.sInstance, levelBean.getThumb(), mLevel2);
        }
        mID.setText(u.getLiangNameTip());
        mID2.setText(u.getLiangNameTip());
        mLive.setText(StringUtil.toWan(u.getFans()) + "粉丝");
        mFollow.setText(StringUtil.toWan(u.getFollows()) + "关注");
        mFans.setText(StringUtil.toWan(u.getFans()));
        if (list != null && list.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new MainMeAdapter(mContext, list);
                mAdapter.setOnItemClickListener(this);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setList(list);
            }
        }
        if (u.getKing() != null && !TextUtils.isEmpty(u.getKing().getKing_res()) && u.getKing().getKing_res().equals("1")) {
            ll_money.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(u.getKing().getBalance())) {
                tvMoney.setText(String.format("余额：%1$s", u.getKing().getBalance()));
            }
            if (!TextUtils.isEmpty(u.getKing().getPoints())) {
                tv_jf.setText(String.format("积分：%1$s", u.getKing().getPoints()));
            }
            llTop.setVisibility(View.GONE);
            llRight.setVisibility(View.VISIBLE);
        } else {
            ll_money.setVisibility(View.INVISIBLE);
            llTop.setVisibility(View.VISIBLE);
            llRight.setVisibility(View.GONE);
        }
    }

    private boolean  isClick=true;//处理多次立即直播界面
    @Override
    public void onItemClick(UserItemBean bean, int position) {
        if(isFastClick()){
            return;
        }
        //如果是游客点击
        if(isVisitorClick()){
            return;
        }
        String url = bean.getHref();
        if (TextUtils.isEmpty(url)) {
            switch (bean.getId()) {
                case 1:
                    forwardProfit(bean.getName());
                    break;
                case 2:
                    forwardCoin();
                    break;
                case 13:
                    forwardSetting();
                    break;
                case 19:
                    forwardMyVideo();
                    break;
                case 20:
                    forwardRoomManage();
                    break;
                case 30: //立即直播
                    if(!isClick){
                        return;
                    }
                    isClick=false;
                    if (!CommonAppConfig.getInstance().getUserBean().getAuth_islimit().equals("1")) {
                        ToastUtil.show("请先进行身份认证或等待审核");
                        isClick=true;
                        return;
                    }

                    if (!actionCheckPermission()) {
                        ToastUtil.show("请开启拍照权限以正常直播");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PermissionUtils.launchAppDetailsSettings();
                            }
                        }, 1000);
                        isClick=true;
                        return;
                    }

                    if (!CheckAudioPermission.isHasPermission(mContext)) {
                        ToastUtil.show("请开启录音权限以正常直播");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PermissionUtils.launchAppDetailsSettings();
                            }
                        }, 1000);
                        isClick=true;
                        return;
                    }

                    getShopGoods();
                    break;
                case 31: //发布视频
                    forwardVideo();
//                    String urls ="file:///android_asset/o.html";
//                    OpenUrlUtils.getInstance(mContext)
//                            .setSlide_target(datas.get(0).getSlide_target())
//                            .setType(4)
//                            .setJump_type("2")
//                            .setLoadTransparent(true)
//                            .go(urls,mContext);
                    break;
                case 33: //我的钱包
                    forwardMyCoin(bean.getName());
                    break;
                case 32: //我的消息
                    forwardChatActivity();
                    break;
            }
        } else {
            if (bean.getId() == 8) {//三级分销
//                ThreeDistributActivity.forward(mContext, bean.getName(), url);
                WebViewActivity.forwardNo(mContext, url,TextUtils.isEmpty(bean.getName())?"":bean.getName());
            } else {
                if (TextUtils.isEmpty(bean.getJump_type())) {
                    WebViewActivity.forward(mContext, url,TextUtils.isEmpty(bean.getName())?"":bean.getName());
                } else {
//                    if (bean.getId() == 14 || bean.getId() == 15) {//登录密码 取款密码
//                        if (url.endsWith("deviceId=")) {
//                            url = url + DeviceUtils.getUniqueDeviceId();
//                        }
//                        addUidToken(url, TextUtils.isEmpty(bean.getName())?"":bean.getName());
//                    } else {
//                        OpenUrlUtils.getInstance().setContext(mContext)
//                                .setSlideTarget(bean.getSlide_target())
//                                .setJump_type(bean.getJump_type())
//                                .setIs_king(bean.getIs_king())
//                                .setType(Integer.parseInt(bean.getSlide_jump_style()))
//                                .setTitle(TextUtils.isEmpty(bean.getName())?"":bean.getName())
//                                .setSlide_show_type_button(bean.getSlide_show_type_button())
//                                .go(url);
//                    }
                    OpenUrlUtils.getInstance().setContext(mContext)
                            .setSlideTarget(bean.getSlide_target())
                            .setJump_type(bean.getJump_type())
                            .setIs_king(bean.getIs_king())
                            .setType(Integer.parseInt(bean.getSlide_jump_style()))
                            .setTitle(TextUtils.isEmpty(bean.getName())?"":bean.getName())
                            .setSlide_show_type_button(bean.getSlide_show_type_button())
                            .go(url);
                }
            }
        }
    }

    /**
     * url添加uid&token
     *
     * @param url
     */
    private void addUidToken(final String url, final String name) {
        CommonHttpUtil.getUidToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info == null || info.length == 0) {
                    SPUtils.getInstance().put(Constants.HTML_TOKEN,"");
                    WebViewActivity.forwardRight(mContext, url, name, true);
                    return;
                }
                List<TokenUtilsBean> tokens = JSON.parseArray(Arrays.toString(info), TokenUtilsBean.class);
                SPUtils.getInstance().put(Constants.HTML_TOKEN, JSON.toJSONString(tokens));
                String uidToken = "uid=" + tokens.get(0).getUid() + "&token=" + tokens.get(0).getToken();
                WebViewActivity.forwardRight(mContext, url + (url.contains("?") ? "&" : "?") + uidToken, name, true);
            }
        });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        //如果是游客点击
        if(isVisitorClick()){
            return;
        }
        if (i == R.id.btn_edit) {
//            forwardEditProfile();
            forwardUserHome(mContext, CommonAppConfig.getInstance().getUid());
        } else if (i == R.id.btn_live) {
            forwardLiveRecord();

        } else if (i == R.id.btn_follow) {
            forwardFollow();

        } else if (i == R.id.btn_fans) {
            forwardFans();

        }
    }

    /**
     * 跳转到个人主页
     */
    public static void forwardUserHome(Context context, String toUid) {
        ARouter.getInstance().build(PATH_USER_HOME)
                .withString(Constants.TO_UID, toUid)
                .navigation();
    }

    /**
     * 发送视频(安卓10 不兼容，直接选取本地视屏)
     */
    private void forwardVideo() {
        mContext.startActivity(new Intent(mContext, VideoMyRecordActivity.class));
    }


    /**
     * 编辑个人资料
     */
    private void forwardEditProfile() {
        mContext.startActivity(new Intent(mContext, EditProfileActivity.class));
    }

    /**
     * 我的关注
     */
    private void forwardChatActivity() {
        ChatActivity.forward(mContext);
    }

    /**
     * 我的关注
     */
    private void forwardFollow() {
        FollowActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }

    /**
     * 我的粉丝
     */
    private void forwardFans() {
        FansActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }

    /**
     * 我的钱包
     */
    private void forwardMyCoin(String name) {
        Intent intent = new Intent(mContext, MyCoinActivity.class);
        intent.putExtra("name",name);
        mContext.startActivity(intent);
    }

    /**
     * 直播记录
     */
    private void forwardLiveRecord() {
        LiveRecordActivity.forward(mContext, CommonAppConfig.getInstance().getUserBean());
    }

    /**
     * 我的收益
     * @param name
     */
    private void forwardProfit(String name) {
        mContext.startActivity(new Intent(mContext, MyProfitActivity.class).putExtra("title",name));
    }

    /**
     * 我的钻石
     */
    private void forwardCoin() {
        RouteUtil.forwardMyCoin(mContext);
    }

    /**
     * 设置
     */
    private void forwardSetting() {
        mContext.startActivity(new Intent(mContext, SettingActivity.class));
    }

    /**
     * 我的视频
     */
    private void forwardMyVideo() {
        mContext.startActivity(new Intent(mContext, MyVideoActivity.class));
    }

    /**
     * 房间管理
     */
    private void forwardRoomManage() {
        mContext.startActivity(new Intent(mContext, RoomManageActivity.class));
    }

    private Runnable mStartLiveRunnable = new Runnable() {
        @Override
        public void run() {
//            if (CommonAppConfig.LIVE_SDK_CHANGED) {
//                if (mGetLiveSdkCallback == null) {
//                    mGetLiveSdkCallback = new HttpCallback() {
//                        @Override
//                        public void onSuccess(int code, String msg, String[] info) {
//                            if (code == 0 && info.length > 0) {
//                                try {
//                                    JSONObject obj = JSON.parseObject(info[0]);
//                                    LiveAnchorActivity.forward(mContext, obj.getIntValue("live_sdk"), JSON.parseObject(obj.getString("android"), LiveKsyConfigBean.class));
//                                } catch (Exception e) {
//                                    LiveAnchorActivity.forward(mContext, CommonAppConfig.LIVE_SDK_USED, LiveConfig.getDefaultKsyConfig());
//                                }
//                            }
//                        }
//                    };
//                }
//                LiveHttpUtil.getLiveSdk(mGetLiveSdkCallback);
//            } else {
//                LiveAnchorActivity.forward(mContext, CommonAppConfig.LIVE_SDK_USED, LiveConfig.getDefaultKsyConfig());
//            }
            initClient(10000, 3);
            if (mGetLiveSdkCallback == null) {
                mGetLiveSdkCallback = new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            try {
                                L.e("SDK返回数据："+info[0]);
                                JSONObject obj = JSON.parseObject(info[0]);
                                LiveAnchorActivity.forward(mContext, CommonAppConfig.LIVE_SDK_USED, JSON.parseObject(obj.getString("android"), LiveKsyConfigBean.class));
                            } catch (Exception e) {
                                LiveAnchorActivity.forward(mContext, CommonAppConfig.LIVE_SDK_USED, LiveConfig.getDefaultKsyConfig());
                            }
                        }
                    }
                };
            }
            String memory=MemoryUtils.getTotalMemory(mContext).replace("GB","").trim();
            L.e("SDK请求数据：内存大小："+memory+" uid:"+CommonAppConfig.getInstance().getUid());
            LiveHttpUtil.getLiveSdk(memory+"",mGetLiveSdkCallback);
        }
    };

    public void updataHeader(String url) {
        ImgLoader.displayAvatar(mContext, url, mAvatar);
    }

    /**
     * 检查是否有拍照权限
     *
     * @return
     */
    public boolean actionCheckPermission() {
        String perms = Manifest.permission.CAMERA;
        Integer nRet = 0;
        nRet = ContextCompat.checkSelfPermission(mContext, perms);
        if (nRet == PERMISSION_GRANTED) {
            return true;
        } else if (nRet == PERMISSION_DENIED) {
            return false;
        } else {
            return false;
        }
    }

    /**
     * 设置vip
     */
    private void setVipLeve(UserBean.Vip vip) {
        if (vip.getVip_is_king() == 1) {
            mIvVip.setVisibility(View.VISIBLE);
            if(vip.getType()>35){
                mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_35l));
                return ;
            }
            switch (vip.getType()) {
                case 1:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_1l));
//                    animatorUtil.FrameNoOneAnimation(mIvVip);
                    break;
                case 2:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_2l));
                    break;
                case 3:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_3l));
                    break;
                case 4:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_4l));
                    break;
                case 5:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_5l));
                    break;
                case 6:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_6l));
                    break;
                case 7:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_7l));
                    break;
                case 8:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_8l));
                    break;
                case 9:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_9l));
                    break;
                case 10:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_10l));
                    break;
                case 11:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_11l));
                    break;
                case 12:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_12l));
                    break;
                case 13:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_13l));
                    break;
                case 14:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_14l));
                    break;
                case 15:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_15l));
                    break;
                case 16:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_16l));
                    break;
                case 17:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_17l));
                    break;
                case 18:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_18l));
                    break;
                case 19:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_19l));
                    break;
                case 20:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_20l));
                    break;
                case 21:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_21l));
                    break;
                case 22:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_22l));
                    break;
                case 23:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_23l));
                    break;
                case 24:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_24l));
                    break;
                case 25:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_25l));
                    break;
                case 26:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_26l));
                    break;
                case 27:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_27l));
                    break;
                case 28:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_28l));
                    break;
                case 29:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_29l));
                    break;
                case 30:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_30l));
                    break;
                case 31:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_31l));
                    break;
                case 32:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_32l));
                    break;
                case 33:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_33l));
                    break;
                case 34:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_34l));
                    break;
                case 35:
                    mIvVip.setImageDrawable( mContext.getResources().getDrawable(com.yunbao.live.R.mipmap.vip_35l));
                    break;

            }
        } else {
            mIvVip.setVisibility(View.GONE);
        }
    }

    public void setViewVisible() {
        if (botView == null) {
            return;
        }
        if (((MainActivity) mContext).getBottomVisible()) {
            botView.setVisibility(View.VISIBLE);
        } else {
            botView.setVisibility(View.GONE);
        }
    }



    public boolean   isVisitorClick() {
        if (CommonAppConfig.getInstance().isVisitorLogin()) {
            LoginNewActivity.forwardVisitor(mContext);
            return true;
        }
        return false;
    }
}
