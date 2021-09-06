package com.yunbao.phonelive.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.UriUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.model.Response;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.activity.BaseFotBitShotActivity;
import com.yunbao.common.bean.AdBean;
import com.yunbao.common.bean.AdvertBean;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.custom.CircleProgress;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.DownloadUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.utils.NetworkUtils;
import com.yunbao.live.views.LauncherAdViewHolder;
import com.yunbao.main.activity.LoginNewActivity;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.common.utils.AppUtil;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.UrlBean;
import com.yunbao.phonelive.utils.IMSavePhotoUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import pub.devrel.easypermissions.EasyPermissions;

import static com.yunbao.common.download.TasksManager.PATH;

/**
 * Created by cxf on 2018/9/17.
 */
@Route(path = RouteUtil.PATH_LAUNCHER)
public class LauncherActivity extends BaseFotBitShotActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks{

    private static final String TAG = "LauncherActivity";
    private static final int WHAT_GET_CONFIG = 0;
    private static final int WHAT_COUNT_DOWN = 1;
    private static final int WHAT_CHOOSE_LINE = 2;
    private static final long TIMEOUT = 3000;
    private Handler mHandler;
    protected Context mContext;
    private ViewGroup mRoot;
    private ImageView mCover;
    private ViewGroup mContainer;
    private CircleProgress mCircleProgress;
    private List<AdBean> mAdList;
    private List<ImageView> mImageViewList;
    private int mMaxProgressVal;
    private int mCurProgressVal;
    private int mAdIndex;
    private int mInterval = 2000;
    private View mBtnSkipImage;
    private View mBtnSkipVideo;
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayer mPlayer;
    private LauncherAdViewHolder mLauncherAdViewHolder;
    private boolean mPaused;
    private TextView tv_failed;
    String[] perms = {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};
    //Manifest.permission.ACCESS_COARSE_LOCATION
    private boolean mEnd;
    private boolean mStarted;
    private RelativeLayout rlLoading;
    private OkHttpClient mOkHttpClient;
    private List<String> list;
    private UrlBean bean;
    private int testTimes = 0;
    private boolean isFirst = true;
    private boolean isFirstPull = true;
    private int testPullTimes = 0;
    private List<LiveClassBean> tab4;
    private NetThread mTimeThread;
    private boolean haveNet = false;
    private boolean isactivity = true;//当前界面还在
    private ConfigBean configBean;
    private Handler handler = new Handler();
    public boolean isSkip = true; //图片渲染之后 就不能5秒自动跳转
    private LottieAnimationView animation_view;
    private DisplayCutout cutoutDisp = null;
    private String mTag = "LauncherActivity";

    private boolean preFinish;
    public String customToken;//是不是游客mToken
    public String customUid;
    public boolean isVisitor;//是不是游客
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //下面的代码是为了防止一个bug:
        // 收到极光通知后，点击通知，如果没有启动app,则启动app。然后切后台，再次点击桌面图标，app会重新启动，而不是回到前台。
        Intent intent = getIntent();
        if (!isTaskRoot()
                && intent != null
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }
        setStatusBar();
        requestPermissions();
        setContentView(R.layout.activity_launcher);
        mContext = this;
        mRoot = findViewById(R.id.root);
        mCover = findViewById(R.id.cover);
        mCircleProgress = findViewById(R.id.progress);
        mContainer = findViewById(R.id.container);
        mBtnSkipImage = findViewById(R.id.btn_skip_img);
        mBtnSkipVideo = findViewById(R.id.btn_skip_video);
        tv_failed = findViewById(R.id.tv_failed);
        animation_view = findViewById(R.id.animation_view);

        rlLoading = findViewById(R.id.rl_loading);
        mBtnSkipImage.setOnClickListener(this);
        mBtnSkipVideo.setOnClickListener(this);
        ImgLoader.display(mContext, R.mipmap.screen, mCover);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_GET_CONFIG:
                        getConfig();
                        break;
                    case WHAT_COUNT_DOWN:
                        updateCountDown();
                        break;
                    case WHAT_CHOOSE_LINE:
                        chooseLine();
                        break;
                }
            }
        };
//        mHandler.sendEmptyMessageDelayed(WHAT_GET_CONFIG, 1000);
        SpUtil.getInstance().setStringValue(SpUtil.FAST_PULL, "");
        SpUtil.getInstance().setStringValue(SpUtil.FAST_PUSH, "");
        chooseLine();
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
     * 选择线路
     */
    private void chooseLine() {
        initClient(3000, 1);
        list = new ArrayList<>();
        String allUrl = SpUtil.getInstance().getStringValue(SpUtil.ALL_URL);
        if (!TextUtils.isEmpty(allUrl)) {
            UrlBean bean = JSON.parseObject(allUrl, UrlBean.class);
            list = bean.getApp_host();
        } else {
            String hosts = getMetaDataString("SERVER_HOST");
            list = Arrays.asList(hosts.split(","));
        }
        if (NetworkUtils.isNetWorkAvailable(this)) {
            for (int i = 0; i < list.size(); i++) {
                startTest(list.get(i), i);
            }
        } else {
            Toast.makeText(mContext, "当前网络环境不佳，请检查网络", Toast.LENGTH_SHORT).show();
            mTimeThread = new NetThread();
            mTimeThread.start();
        }
    }

    /**
     * 测试连接速度
     */
    private void startTest(String url, int position) {
        L.e(TAG, "测试连接速度：" + url);
        OkGo.<String>get(url)
                .tag(position)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        int p = (int) response.getRawResponse().request().tag();
                        if (isFirst) {
                            isFirst = false;
                            L.e(TAG, "最快线路：" + list.get(position));
                            SpUtil.getInstance().setStringValue(SpUtil.FAST_URL, list.get(position));
                            OkGo.cancelAll(mOkHttpClient);
                            String mUrl = list.get(position) + "/api/public/?service=";
                            HttpClient.getInstance().setmUrl(mUrl);
                            getUrl(list.get(position));

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        testTimes++;
                        if (testTimes == list.size()) {
                            testTimes = 0;
                            SpUtil.getInstance().setStringValue(SpUtil.FAST_URL, list.get(0));
                            String mUrl = list.get(0) + "/api/public/?service=";
                            HttpClient.getInstance().setmUrl(mUrl);
                            getUrl(list.get(position));
                        }
                    }
                });
    }

    /**
     * 获取推拉流地址和应用地址
     */
    private void getUrl(String url) {
        L.e("WOLF", url + "/api/public/?service=Home.getLivePath");
        OkGo.<String>get(url + "/api/public/?service=Home.getLivePath")
                .params("rhbyPath", "1")//适配老包
                .tag("url")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        L.e(TAG, "getUrl--:" + response.body());
//                        getHttpConfigBean(false);
                        JSONObject obj = JSON.parseObject(response.body());
                        if (obj.getJSONObject("data").getIntValue("code") == 0) {
                            bean = JSON.parseObject(obj.getJSONObject("data").getString("info"), UrlBean.class);
                            SpUtil.getInstance().setStringValue(SpUtil.ALL_URL, obj.getJSONObject("data").getString("info"));
                            for (int i = 0; i < bean.getPull().size(); i++) {
                                startTestPull(bean.getPull().get(i).getTest_url(), i);
                            }
                        } else {
                            tv_failed.setVisibility(View.VISIBLE);
                            animation_view.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (response.body() == null) {
                            tv_failed.setVisibility(View.VISIBLE);
                            animation_view.setVisibility(View.GONE);
                        }
                    }
                });
    }

    /**
     * 测试拉流地址速度
     */
    private void startTestPull(String url, int position) {
        L.e(TAG, "拉流地址：" + url);
        OkGo.<String>get(url)
                .tag(position)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (isFirstPull) {
                            isFirstPull = false;
                            L.e(TAG, "拉流最快线路：" + bean.getRhbyPath().getPull().get(position).getPull());
                            SpUtil.getInstance().setStringValue(SpUtil.FAST_PULL, bean.getRhbyPath().getPull().get(position).getPull());

                            OkGo.cancelAll(mOkHttpClient);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessageDelayed(WHAT_GET_CONFIG, 50);
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        L.e("WOLF", "拉流测试失败");
                        testPullTimes++;
                        L.e("WOLF", "size:" + bean.getPull().size());
                        if (testPullTimes == bean.getPull().size()) {
                            testPullTimes = 0;
                            SpUtil.getInstance().setStringValue(SpUtil.FAST_PULL, bean.getPull().get(0).getPull());
                            if (mHandler != null) {
                                mHandler.sendEmptyMessageDelayed(WHAT_GET_CONFIG, 50);
                            }
                        }
                    }
                });
    }

    /**
     * 动态添加权限
     */
    private void requestPermissions() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            //todo 已经获取了权限
        } else {
            //没有获取了权限，申请权限
            EasyPermissions.requestPermissions(this, "为了优化您的使用体验，请打开下列必要的权限", 0, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    /**
     * 图片倒计时
     */
    private void updateCountDown() {
        mCurProgressVal += 100;
        if (mCurProgressVal > mMaxProgressVal) {
            return;
        }
        if (mCircleProgress != null) {
            mCircleProgress.setCurProgress(mCurProgressVal);
        }
        int index = mCurProgressVal / mInterval;
        if (index < mAdList.size() && mAdIndex != index) {
            View v = mImageViewList.get(mAdIndex);
            if (v != null && v.getVisibility() == View.VISIBLE) {
                v.setVisibility(View.INVISIBLE);
            }
            mAdIndex = mCurProgressVal / mInterval;
        }
        if (mCurProgressVal < mMaxProgressVal) {
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_COUNT_DOWN, 100);
            }
        } else if (mCurProgressVal == mMaxProgressVal) {
            checkUidAndToken();
        }
    }

    /**
     * 获取Config信息（bean）isstart=false表示先请求数据不加载
     */
    private void getHttpConfigBean(boolean isstart) {
        CommonHttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                configBean = bean;
                if (isstart) {
                    setConfigBean(configBean);
                }
            }
        });
    }

    /**
     * 处理游客模式
     */
    private void handlerCustom(ConfigBean bean) {
        if(bean==null){
            return;
        }
        //游客模式下
        if(configBean.getLogin_way().equals("1")){
            //保存，推出登录的时候设置，用于下来刷新
            SpUtil.getInstance().setStringValue(SpUtil.CUSTOM_TOKEN,bean.getToken());
            SpUtil.getInstance().setStringValue(SpUtil.CUSTOM_UID,bean.getUid());

            CommonAppConfig.getInstance().setVisitor(true);
            String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(new String[]{SpUtil.UID, SpUtil.TOKEN});
            //已登录
            if(TextUtils.isEmpty(uidAndToken[1])){  //没得token 代表是游客
                CommonAppConfig.getInstance().setmToken(configBean.getToken());
                CommonAppConfig.getInstance().setmUid(configBean.getUid());
                customToken=configBean.getToken();
                customUid=configBean.getUid();
            }else {
                customUid=uidAndToken[0];
                customToken=uidAndToken[1];
            }
            isVisitor=true;
        }else {
            String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(new String[]{SpUtil.UID, SpUtil.TOKEN});
            if(uidAndToken[0].equals(CommonAppConfig.VISITOR_UID)){
                CommonAppConfig.getInstance().clearLoginInfo();
            }
            isVisitor=false;
            CommonAppConfig.getInstance().setVisitor(false);
        }
    }

    /**
     * 获取Config信息
     */
    private void getConfig() {
        if (configBean == null) {
            getHttpConfigBean(true);
        } else {
            setConfigBean(configBean);
        }
    }

    /**
     * 处理Config信息
     */
    public void setConfigBean(ConfigBean bean) {
        //获取到这里说明已经获取成功线路，如果5秒还是不能跳转就自行跳转到主界面
        //处理游客模式
        handlerCustom(bean);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSkip) {
                    isSkip = false;
                    checkUidAndToken();
                }
            }
        }, 5000);
        if (bean != null) {
//            AppContext.sInstance.initBeautySdk(bean.getBeautyKey());
            String beautyKey = getMetaDataString("BEAUTY_KEY");
            AppContext.sInstance.initBeautySdk(beautyKey);
            String adInfo = bean.getAdInfo();
            if (!TextUtils.isEmpty(adInfo)) {
                JSONObject obj = JSON.parseObject(adInfo);
                if (obj.getIntValue("switch") == 1) {
                    List<AdBean> list = JSON.parseArray(obj.getString("list"), AdBean.class);
                    if (list != null && list.size() > 0) {
                        mAdList = list;
                        mInterval = (int) (obj.getFloatValue("time") * 1000);
                        if (mContainer != null) {
                            mContainer.setOnClickListener(LauncherActivity.this);
                        }
                        playAD(obj.getIntValue("type") == 0);
                    } else {
                        checkUidAndToken();
                    }
                } else {
                    checkUidAndToken();
                }
            } else {
                checkUidAndToken();
            }
        }

    }

    /**
     * 检查uid和token是否存在
     */
    private void checkUidAndToken() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        initClient(10000, 3);
        String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(new String[]{SpUtil.UID, SpUtil.TOKEN});
        //如果之前是游客默认token，关闭游客模式需要清空本地token，去登陆界面
        if(!isVisitor && uidAndToken[0].equals(CommonAppConfig.VISITOR_UID)){
            CommonAppConfig.getInstance().clearLoginInfo();
            goLogin();
        }
        //看是不是游客登陆，是游客登陆就保存
        final String uid =  isVisitor? customUid:uidAndToken[0];
        final String token =  isVisitor? customToken:uidAndToken[1];
        if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
            if(isVisitor){
                CommonAppConfig.getInstance().setLoginInfo(uid, token, true);
            }
            getAdvert();
        } else {
            goLogin();
        }
    }


    public  void goLogin(){
        if (!isactivity || preFinish) {
            return;
        }
        Intent intent = new Intent(this, LoginNewActivity.class);
        intent.putExtra("type","LauncherActivity");
        startActivity(intent);
        preFinish = true;
        finish();
    }


    /**
     * 预加载与缓存首页数据
     */
    private void getAdvert() {
        MainHttpUtil.getAdvertCache("", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                Log.e("onSuccess------","onCacheSuccess");
                if (isDestroyed()) {
                    return;
                }
                if (code == 0) {
                    List<AdvertBean> advertBean = JSON.parseArray(Arrays.toString(info), AdvertBean.class);
                    if (advertBean != null || advertBean.size() > 0) {
                        CommonAppConfig.getInstance().setHotList(Arrays.toString(info));
                        SpUtil.getInstance().setStringValue(SpUtil.FAST_HME_LIST,JSON.toJSONString(advertBean.get(0).getAPP_mynav()));
                        if(advertBean.get(0).getTabbar()!=null){
                            Log.e("getTabbar------",JSON.toJSONString(advertBean.get(0).getTabbar()));
                            SpUtil.getInstance().setStringValue(SpUtil.FAST_TABBAR_LIST,JSON.toJSONString(advertBean.get(0).getTabbar()));
                        }
                        if (advertBean.get(0).getList() != null) {
                            advertBean.get(0).getList().clear();
                        }
                        forwardMainActivity(advertBean.get(0));
                    } else {
                        releaseVideo();
                        MainActivity.forward(mContext, true);
                    }
                } else {
                    releaseVideo();
                    MainActivity.forward(mContext, true);
                }
            }

            @Override
            public void onCacheSuccess(Response<JsonBean> response) {
                super.onCacheSuccess(response);
                List<AdvertBean> advertBean = JSON.parseArray(Arrays.toString(response.body().getData().getInfo()), AdvertBean.class);
                CommonAppConfig.getInstance().setHotList(Arrays.toString(response.body().getData().getInfo()));
                if (advertBean != null && advertBean.size() > 0) {
                    advertBean.get(0).getList().clear();
                    forwardMainActivity(advertBean.get(0));
                    if(advertBean.get(0).getTabbar()!=null){
                        SpUtil.getInstance().setStringValue(SpUtil.FAST_TABBAR_LIST,JSON.toJSONString(advertBean.get(0).getTabbar()));
                    }
                } else {
                    releaseVideo();
                    MainActivity.forward(mContext, true);
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                releaseVideo();
                MainActivity.forward(mContext, true);
            }
        });
    }

    /**
     * 跳转到首页
     *
     * @param advertBean
     */
    private void forwardMainActivity(AdvertBean advertBean) {
        releaseVideo();
        MainActivity.forward(mContext, advertBean, true);
//        finish();
    }

    @Override
    protected void onDestroy() {
        haveNet = true;
        isactivity = false;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
        MainHttpUtil.cancel(MainHttpConsts.GET_ADVERT);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        releaseVideo();
        if (mLauncherAdViewHolder != null) {
            mLauncherAdViewHolder.release();
        }
        mLauncherAdViewHolder = null;

        release();

        super.onDestroy();
    }

    /**
     * 设置透明状态栏
     */
    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_skip_img || i == R.id.btn_skip_video) {
            if (mBtnSkipImage != null) {
                mBtnSkipImage.setClickable(false);
            }
            if (mBtnSkipVideo != null) {
                mBtnSkipVideo.setClickable(false);
            }
            checkUidAndToken();
        } else if (i == R.id.container) {
            clickAD();
        }
    }

    /**
     * 点击广告
     */
    private void clickAD() {
        if (mAdList != null && mAdList.size() > mAdIndex) {
            AdBean adBean = mAdList.get(mAdIndex);
            if (adBean != null) {
                String link = adBean.getLink();
                if (!TextUtils.isEmpty(link)) {
                    if (mHandler != null) {
                        mHandler.removeCallbacksAndMessages(null);
                    }
                    if (mContainer != null) {
                        mContainer.setClickable(false);
                    }
                    releaseVideo();
                    if (mLauncherAdViewHolder == null) {
                        mLauncherAdViewHolder = new LauncherAdViewHolder(mContext, mRoot, link);
                        mLauncherAdViewHolder.addToParent();
                        mLauncherAdViewHolder.loadData();
                        mLauncherAdViewHolder.show();
                        mLauncherAdViewHolder.setActionListener(new LauncherAdViewHolder.ActionListener() {
                            @Override
                            public void onHideClick() {
                                checkUidAndToken();
                            }
                        });
                    }
                }
            }
        }
    }

    private void releaseVideo() {
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
            mPlayer.setPlayListener(null);
        }
        mPlayer = null;
    }


    /**
     * 播放广告
     */
    private void playAD(boolean isImage) {
        if (mContainer == null) {
            return;
        }
        if (isImage) {
            int imgSize = mAdList.size();
            if (imgSize > 0) {
                IMSavePhotoUtil.saveUrlList(this, mAdList);
                mImageViewList = new ArrayList<>();
                for (int i = 0; i < imgSize; i++) {
                    ImageView imageView = new ImageView(mContext);
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setBackgroundColor(0xffffffff);
                    mImageViewList.add(imageView);
                    int finalI = i;
                    //如果本地图片有该图片，就下载
                    String picUrl = mAdList.get(i).getUrl();
                    if (!TextUtils.isEmpty(picUrl)) {
                        String filename = picUrl.substring(picUrl.lastIndexOf("/") + 1, picUrl.length());
                        String path = IMSavePhotoUtil.path + filename;
                        if (new File(path) != null && new File(path).exists()) {
                            picUrl = path;
                        }
                    }
                    Glide.with(mContext)
                            .load(picUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    if (finalI == 0 && isSkip) {
                                        isSkip = false;
                                        mMaxProgressVal = imgSize * mInterval;
                                        if (mCircleProgress != null) {
                                            mCircleProgress.setMaxProgress(mMaxProgressVal);
                                        }
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessageDelayed(WHAT_COUNT_DOWN, 100);
                                        }
                                        if (mCover != null && mCover.getVisibility() == View.VISIBLE) {
                                            mCover.setVisibility(View.INVISIBLE);
                                            rlLoading.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                    return false;
                                }
                            })
                            .fitCenter()
                            .into(imageView);
                }
                for (int i = imgSize - 1; i >= 0; i--) {
                    mContainer.addView(mImageViewList.get(i));
                }
                if (mBtnSkipImage != null && mBtnSkipImage.getVisibility() != View.VISIBLE) {
                    mBtnSkipImage.setVisibility(View.VISIBLE);
                }
            } else {
                checkUidAndToken();
            }
        } else {
            if (mAdList == null || mAdList.size() == 0) {
                checkUidAndToken();
                return;
            }
            String videoUrl = mAdList.get(0).getUrl();
            if (TextUtils.isEmpty(videoUrl)) {
                checkUidAndToken();
                return;
            }
            String videoFileName = MD5Util.getMD5(videoUrl);
            if (TextUtils.isEmpty(videoFileName)) {
                checkUidAndToken();
                return;
            }
            File file = new File(PATH, videoFileName);
            //此处如果没有下载完成  就关闭app  下载进来视屏资源已存在  但是播放为黑屏，所以下载成功加一个标识
            if (file.exists()) {
                if (SpUtil.getInstance().getBooleanValue(SpUtil.VIDEO_SUCCESS)) {
                    isSkip = false;
                    playAdVideo(file);
                } else {
                    file.delete();
                    SpUtil.getInstance().setBooleanValue(SpUtil.VIDEO_SUCCESS, false);
                    downloadAdFile(videoUrl, videoFileName);
                }
            } else {
                SpUtil.getInstance().setBooleanValue(SpUtil.VIDEO_SUCCESS, false);
                downloadAdFile(videoUrl, videoFileName);
            }
        }
    }

    @Override
    protected void onPause() {
        mPaused = true;
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.setMute(true);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused) {
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.setMute(false);
            }
        }
        mPaused = false;
    }

    /**
     * 下载视频
     */
    private void downloadAdFile(String url, String fileName) {
        DownloadUtil downloadUtil = new DownloadUtil();

        downloadUtil.download("ad_video", PATH, fileName, url, new DownloadUtil.Callback() {
            @Override
            public void onSuccess(File file) {
                SpUtil.getInstance().setBooleanValue(SpUtil.VIDEO_SUCCESS, true);
//                playAdVideo2(file);
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onError(Throwable e) {
                checkUidAndToken();
            }
        });
    }

    /**
     * 播放视频
     */
    private void playAdVideo(File videoFile) {
        if (mBtnSkipVideo != null && mBtnSkipVideo.getVisibility() != View.VISIBLE) {
            mBtnSkipVideo.setVisibility(View.VISIBLE);
        }
        mTXCloudVideoView = new TXCloudVideoView(mContext);
        mTXCloudVideoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mTXCloudVideoView.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mTXCloudVideoView.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mContainer.addView(mTXCloudVideoView);
        mPlayer = new TXLivePlayer(mContext);
        mPlayer.setPlayerView(mTXCloudVideoView);
        mPlayer.setAutoPlay(true);
        mPlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int e, Bundle bundle) {
                L.e(TAG, "视频状态------>" + e);
                if (e == TXLiveConstants.PLAY_EVT_PLAY_END) {//获取到视频播放完毕的回调
                    checkUidAndToken();
                    L.e(TAG, "视频播放结束------>");
                } else if (e == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {////获取到视频宽高回调
                    float videoWidth = bundle.getInt("EVT_PARAM1", 0);
                    float videoHeight = bundle.getInt("EVT_PARAM2", 0);
                    if (mTXCloudVideoView != null && videoWidth > 0 && videoHeight > 0) {
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTXCloudVideoView.getLayoutParams();
                        int targetH = 0;
                        if (videoWidth >= videoHeight) {//横屏
                            params.gravity = Gravity.CENTER_VERTICAL;
                            targetH = (int) (mTXCloudVideoView.getWidth() / videoWidth * videoHeight);
                        } else {
                            targetH = ViewGroup.LayoutParams.MATCH_PARENT;
                        }
                        if (targetH != params.height) {
                            params.height = targetH;
                            mTXCloudVideoView.requestLayout();
                        }
                    }
                } else if (e == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {
                    if (mCover != null && mCover.getVisibility() == View.VISIBLE) {
                        mCover.setVisibility(View.INVISIBLE);
                        rlLoading.setVisibility(View.INVISIBLE);
                    }
                } else if (e == TXLiveConstants.PLAY_ERR_NET_DISCONNECT ||
                        e == TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND) {
                    ToastUtil.show(WordUtil.getString(R.string.live_play_error));
                    checkUidAndToken();
                }
            }

            @Override
            public void onNetStatus(Bundle bundle) {
            }

        });
        mPlayer.startPlay(videoFile.getAbsolutePath(), TXLivePlayer.PLAY_TYPE_LOCAL_VIDEO);
    }

    public void release() {
        mEnd = true;
        mStarted = false;
    }

    private static String getMetaDataString(String key) {
        String res = null;
        try {
            ApplicationInfo appInfo = CommonAppContext.sInstance.getPackageManager().getApplicationInfo(CommonAppContext.sInstance.getPackageName(), PackageManager.GET_META_DATA);
            res = appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }


    /**
     * 进来没有网，监听有网的变化
     */
    private class NetThread extends Thread {
        @Override
        public void run() {
            while (!haveNet) {
                try {
                    Thread.sleep(300);
                    Log.e("----", "检查是不是有网");
                    if (NetworkUtils.isNetWorkAvailable(LauncherActivity.this)) {
                        haveNet = true;
                        resetConnect();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void resetConnect() {
        for (int i = 0; i < list.size(); i++) {
            startTest(list.get(i), i);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i(mTag, "onAttachedToWindow");
        //挖孔屏,适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                WindowInsets windowInsets = getWindow().getDecorView().getRootWindowInsets();
                if (windowInsets != null) {
                    cutoutDisp = windowInsets.getDisplayCutout();
                } else {
                    Log.i(mTag, "windowInsets is null");
                }
            } catch (Exception e) {
                Log.e(mTag, "error:" + e.toString());
            }
            if (cutoutDisp != null) {
                Log.i(mTag, "will set mode,cutout:" + cutoutDisp.toString());
                WindowManager.LayoutParams windowManagerDu = getWindow().getAttributes();
                windowManagerDu.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                getWindow().setAttributes(windowManagerDu);
                AppUtil.CutOutSafeHeight = getStatusBarHeight();
                Log.i(mTag, "statusBar height:" + AppUtil.CutOutSafeHeight);
            }
        }
    }

    //获取状态栏高度
    public int getStatusBarHeight() {
        Context context = getApplicationContext();
        int result = 0;
        if (cutoutDisp != null) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }
}
