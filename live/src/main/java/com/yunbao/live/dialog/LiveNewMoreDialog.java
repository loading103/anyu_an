package com.yunbao.live.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.AgentWebUtils;
import com.just.agentweb.WebChromeClient;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.TokenUtilsBean;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.event.MainRefreshJsEvent;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.AndroidInterface;
import com.yunbao.common.interfaces.DissmissDialogListener;
import com.yunbao.common.utils.IMImageLoadUtil;
import com.yunbao.common.utils.L;
import com.yunbao.live.R;
import com.yunbao.live.event.LiveCloseDialogEvent;
import com.yunbao.live.views.LiveAudienceViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

import static com.just.agentweb.AbsAgentWebSettings.USERAGENT_AGENTWEB;
import static com.just.agentweb.AbsAgentWebSettings.USERAGENT_UC;

/**
 * Describe:直播更多
 */
public class LiveNewMoreDialog extends AbsDialogFragment {
    private String url;
    private String htmlurl;
    private String currentUrl;
    private ViewGroup fl_root;
    private RelativeLayout rlLoading;
    private Animation operatingAnim;
    private Animation operatingAnim1;
    private WebView im_web;
    private WebSettings mWebSettings;
    private FrameLayout fl;
    private FrameLayout flMain;
    private LottieAnimationView animation_view;
    private boolean isFirst=true;
    private String mGoodsNo;//商品id
    private  ImageView iv_back;
    private CountDownTimer mTimer;
    private boolean isUrl;

    public  LiveNewMoreDialog(LiveAudienceViewHolder liveAudienceViewHolder, String url, String htmlurl,String mGoodsNo) {
        super();
        this.url=url;
        this.htmlurl=htmlurl;
        this.mGoodsNo=mGoodsNo;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_new_more2;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(final Window window) {
        window.setWindowAnimations(com.yunbao.im.R.style.leftToRightAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        //状态栏变色问题
//        window.setLayout((ViewGroup.LayoutParams.MATCH_PARENT), ScreenUtils.getScreenHeight()-  BarUtils.getStatusBarHeight()-BarUtils.getNavBarHeight());
//        //隐藏虚拟导航
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//                        //布局位于状态栏下方
//                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                        //全屏
//                        View.SYSTEM_UI_FLAG_FULLSCREEN |
//                        //隐藏导航栏
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
//                uiOptions |= 0x00001000;
//                window.getDecorView().setSystemUiVisibility(uiOptions);
//            }
//        });

        //设置底部导航栏不会遮挡布局
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        initAnim();
    }
    /**
     * 初始化旋转动画
     */
    private void initAnim() {
        operatingAnim = AnimationUtils.loadAnimation(mContext, com.yunbao.common.R.anim.anim_rotate_90_game);
        operatingAnim.setFillAfter(true);
        operatingAnim1 = AnimationUtils.loadAnimation(mContext, com.yunbao.common.R.anim.anim_rotate_90_game2);
        operatingAnim1.setFillAfter(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle bundle = getArguments();
        fl_root = (ViewGroup) findViewById(com.yunbao.common.R.id.fl_root);
        im_web = (WebView) findViewById(com.yunbao.common.R.id.im_web);
        animation_view = (LottieAnimationView) findViewById(com.yunbao.common.R.id.animation_view);
        im_web.setVisibility(View.INVISIBLE);
        rlLoading = (RelativeLayout) findViewById(com.yunbao.common.R.id.rl_loading);
        fl = (FrameLayout) findViewById(com.yunbao.common.R.id.fl);
        iv_back=(ImageView) findViewById(com.yunbao.common.R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        fl_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getDialog()!=null){
                    getDialog().dismiss();
                }
            }
        });
        rlLoading.setBackgroundColor(mContext.getResources().getColor(com.yunbao.common.R.color.color_4D000000));
        animation_view.setAnimation("live_loading.json");
        animation_view.playAnimation();
        rlLoading.setVisibility(View.VISIBLE);

        if(url.startsWith("http")){
            isUrl=true;
        }
        openUrl(url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void openUrl(String url) {
        WebSettings settings = im_web.getSettings();
        im_web.setWebChromeClient(webChromeClient);
        im_web.getSettings().setJavaScriptEnabled(true);

        im_web.getSettings().setAllowFileAccessFromFileURLs(true);
        //设置可以支持缩放
        settings.setSupportZoom(true);
        //设置出现缩放工具
        settings.setBuiltInZoomControls(true);
        //扩大比例的缩放
        settings.setUseWideViewPort(true);
        //自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);

        //缓存
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        im_web.addJavascriptInterface(new AndroidInterface(null, mContext) , "android");
        settings(im_web);

        String urls="";
        if(!TextUtils.isEmpty(mGoodsNo)){
            if(urls.contains("?")){
                urls=url+"&playGroupId="+mGoodsNo;
            }else {
                urls=url+"?playGroupId="+mGoodsNo;
            }
        }else {
            urls=url;
        }

//        String urls=!TextUtils.isEmpty(mGoodsNo)?url+"&playGroupId="+mGoodsNo:url;
        Log.e("更多加载url-----：",urls);

        addUidToken2(urls);

//        im_web.loadUrl(urls);
        im_web.setBackgroundColor(0);
        im_web.setWebViewClient(mWebClient);
    }

    private WebViewClient mWebClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            L.e("WOLF-----onPageStarted", url);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view1, String url) {
            L.e("WOLF-----shouldOverrideUrlLoading", url);
            currentUrl = url;
//            rlLoading.setVisibility(View.GONE);
//            im_web.setVisibility(View.VISIBLE);
//            iv_back.setVisibility(View.VISIBLE);
            return super.shouldOverrideUrlLoading(view1, url);
        }
        @Override
        public void onPageFinished(WebView view1, String url) {
            L.e("WOLF", "onPageFinished:" + url);
//            rlLoading.setVisibility(View.GONE);
//            im_web.setVisibility(View.VISIBLE);
//            iv_back.setVisibility(View.VISIBLE);
            super.onPageFinished(view1, url);
        }
    };

    private android.webkit.WebChromeClient webChromeClient = new android.webkit.WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            L.e("WOLF", "onProgressChanged:" + newProgress);
            if(newProgress>(isUrl?50:10)){
                rlLoading.setVisibility(View.GONE);
                im_web.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                iv_back.setVisibility(View.VISIBLE);
            }
        }
    };

    public void setRootShow(boolean isShow){
        fl_root.setVisibility(isShow?View.VISIBLE:View.GONE);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainRefreshJsEvent(MainRefreshJsEvent e) {
        if (TextUtils.isEmpty(htmlurl)) {
            return;
        }

        if (htmlurl.startsWith("http")) {
//            String urls=!TextUtils.isEmpty(mGoodsNo)?htmlurl+"&playGroupId="+mGoodsNo:htmlurl;
            String urls="";
            if(!TextUtils.isEmpty(mGoodsNo)){
                if(urls.contains("?")){
                    urls=url+"&playGroupId="+mGoodsNo;
                }else {
                    urls=url+"?playGroupId="+mGoodsNo;
                }
            }else {
                urls=url;
            }
//            im_web.loadUrl(urls);
            addUidToken(urls);
        } else {
            getHtmlTokenUid();
        }
    }

    /**
     * url添加uid&token
     * @param url
     */
    private void addUidToken(final String url) {
        CommonHttpUtil.getUidToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(info==null ||info.length==0){
                    SPUtils.getInstance().put(Constants.HTML_TOKEN,"");
                    im_web.loadUrl(url);
                    im_web.loadUrl( "javascript:window.location.reload( true )");
                    return;
                }
                List<TokenUtilsBean> tokens = JSON.parseArray(Arrays.toString(info), TokenUtilsBean.class);
                SPUtils.getInstance().put(Constants.HTML_TOKEN,JSON.toJSONString(tokens));
                String uidToken="uid="+tokens.get(0).getUid()+"&token="+tokens.get(0).getToken();
                L.e("WOLF",url+(url.contains("?")?"&":"?")+uidToken);
                im_web.loadUrl(url+(url.contains("?")?"&":"?")+uidToken);
                im_web.loadUrl( "javascript:window.location.reload( true )");
                if(isFirst){
                    isFirst=false;
                    im_web.reload();
                }
                mTimer = new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        isFirst=true;
                    }
                }.start();
            }
        });
    }

    /**
     * url添加uid&token
     * @param url
     */
    private void addUidToken2(final String url) {
        CommonHttpUtil.getUidToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(info==null ||info.length==0){
                    SPUtils.getInstance().put(Constants.HTML_TOKEN,"");
                    im_web.loadUrl(url);
                    im_web.loadUrl( "javascript:window.location.reload( true )");
                    return;
                }
                List<TokenUtilsBean> tokens = JSON.parseArray(Arrays.toString(info), TokenUtilsBean.class);
                SPUtils.getInstance().put(Constants.HTML_TOKEN,JSON.toJSONString(tokens));
                String uidToken="uid="+tokens.get(0).getUid()+"&token="+tokens.get(0).getToken();
                L.e("WOLF",url+(url.contains("?")?"&":"?")+uidToken);
                im_web.loadUrl(url+(url.contains("?")?"&":"?")+uidToken);
                im_web.loadUrl( "javascript:window.location.reload( true )");
                if(isFirst){
                    isFirst=false;
                    im_web.reload();
                }
                mTimer = new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        isFirst=true;
                    }
                }.start();
            }
        });
    }



    /**
     * 组装本地路由路径
     */
    private void getHtmlTokenUid() {
        if (TextUtils.isEmpty(htmlurl)) {
            return;
        }
        CommonHttpUtil.getUidToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                String newUrl = "";
                if (info == null || info.length == 0) {
                    SPUtils.getInstance().put(Constants.HTML_TOKEN,"");
                    if(htmlurl.startsWith("file:///android")){
                        newUrl =  htmlurl;
                    }else {
                        newUrl = "file:///android_asset/dist/index.html#" + htmlurl;
                    }
                    String urls="";
                    if(!TextUtils.isEmpty(mGoodsNo)){
                        if(newUrl.contains("?")){
                            urls=newUrl+"&playGroupId="+mGoodsNo;
                        }else {
                            urls=newUrl+"?playGroupId="+mGoodsNo;
                        }
                    }else {
                        urls=newUrl;
                    }
                    im_web.loadUrl(urls);
                    im_web.loadUrl( "javascript:window.location.reload( true )");
                    return;
                }
                List<TokenUtilsBean> tokens = JSON.parseArray(Arrays.toString(info), TokenUtilsBean.class);
                SPUtils.getInstance().put(Constants.HTML_TOKEN,JSON.toJSONString(tokens));
                String queryStr = tokens.get(0).getQueryStr();
                if (TextUtils.isEmpty(htmlurl)) {
                    newUrl = "file:///android_asset/dist/index.html#/?" + queryStr;
//                    String urls=!TextUtils.isEmpty(mGoodsNo)?newUrl+"&playGroupId="+mGoodsNo:newUrl;
                    String urls="";
                    if(!TextUtils.isEmpty(mGoodsNo)){
                        if(newUrl.contains("?")){
                            newUrl=url+"&playGroupId="+mGoodsNo;
                        }else {
                            newUrl=url+"?playGroupId="+mGoodsNo;
                        }
                    }else {
                        urls=newUrl;
                    }
                    im_web.loadUrl(urls);
                    im_web.loadUrl( "javascript:window.location.reload( true )");
                } else {
                    if (htmlurl.contains("?")) {
                        newUrl = "file:///android_asset/dist/index.html#" + htmlurl + "&" + queryStr;
                    } else {
                        newUrl = "file:///android_asset/dist/index.html#" + htmlurl + "?" + queryStr;
                    }
//                    String urls=!TextUtils.isEmpty(mGoodsNo)?newUrl+"&playGroupId="+mGoodsNo:newUrl;

                    String urls="";
                    if(!TextUtils.isEmpty(mGoodsNo)){
                        if(newUrl.contains("?")){
                            newUrl=url+"&playGroupId="+mGoodsNo;
                        }else {
                            newUrl=url+"?playGroupId="+mGoodsNo;
                        }
                    }else {
                        urls=newUrl;
                    }
                    Log.e("newUrl-----",urls);
                    im_web.loadUrl(urls);
                    im_web.loadUrl( "javascript:window.location.reload( true )");
                    if(isFirst){
                        isFirst=false;
                        im_web.reload();
                    }
                    CountDownTimer mTimer = new CountDownTimer(3000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            isFirst=true;
                        }
                    }.start();
                }
            }
        });
    }

    private void settings(WebView webView) {
        mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setSavePassword(false);
        if (AgentWebUtils.checkNetwork(webView.getContext())) {
            //根据cache-control获取数据。
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            //没网，则从本地获取，即离线加载
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //适配5.0不允许http和https混合使用情况
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mWebSettings.setTextZoom(100);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setSupportMultipleWindows(false);
        // 是否阻塞加载网络图片  协议http or https
        mWebSettings.setBlockNetworkImage(false);
        // 允许加载本地文件html  file协议
        mWebSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            mWebSettings.setAllowFileAccessFromFileURLs(true);
            // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            mWebSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        } else {
            mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setNeedInitialFocus(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        mWebSettings.setDefaultFontSize(16);
        mWebSettings.setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8
        mWebSettings.setGeolocationEnabled(true);
        String dir = AgentWebConfig.getCachePath(webView.getContext());
//        LogUtils.i(TAG, "dir:" + dir + "   appcache:" + AgentWebConfig.getCachePath(webView.getContext()));
        //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
        mWebSettings.setGeolocationDatabasePath(dir);
        mWebSettings.setDatabasePath(dir);
        mWebSettings.setAppCachePath(dir);
        //缓存文件最大值
        mWebSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        mWebSettings.setUserAgentString(mWebSettings
                .getUserAgentString()
                .concat(USERAGENT_AGENTWEB)
                .concat(USERAGENT_UC)
        );
//        LogUtils.i(TAG, "UserAgentString : " + mWebSettings.getUserAgentString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 安卓9.0后不允许多进程使用同一个数据目录，需设置前缀来区分
            // 参阅 https://blog.csdn.net/lvshuchangyin/article/details/89446629
            Context context = webView.getContext();
            String processName = ProcessUtils.getCurrentProcessName();
            if (!context.getApplicationContext().getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }
    /**
     * 关闭
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveCloseDialogEvent(LiveCloseDialogEvent e) {
        if(getDialog()!=null){
            getDialog().dismiss();
        }
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        dialogDiss();
    }

    @Override
    protected void dialogDiss() {
        if(ondissDialogListener!=null){
            ondissDialogListener.onDissmissListener();
        }
    }
    private DissmissDialogListener ondissDialogListener;

    public void setOnDissmissDialogListener(DissmissDialogListener ondissDialogListener) {
        this.ondissDialogListener = ondissDialogListener;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if(mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
        super.onDestroy();
    }
}
