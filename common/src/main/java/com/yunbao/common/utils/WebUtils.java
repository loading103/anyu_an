package com.yunbao.common.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.AgentWebUtils;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.CusWebView;
import com.yunbao.common.R;
import com.yunbao.common.bean.TokenBean;
import com.yunbao.common.bean.TokenUtilsBean;
import com.yunbao.common.event.MainRefreshJsEvent;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.AndroidInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

import static com.just.agentweb.AbsAgentWebSettings.USERAGENT_AGENTWEB;
import static com.just.agentweb.AbsAgentWebSettings.USERAGENT_UC;
import static com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext;

public class WebUtils {

    private AlertDialog shareDialog;
    private String  url;
    private String  htmlurl;
    private RelativeLayout rlLoading;
    private LottieAnimationView animation_view;
    private Context context;
    private String currentUrl;
    private CusWebView im_web;
    private boolean isFirst=true;
    private WebSettings mWebSettings;
    private CountDownTimer mTimer;
    private RelativeLayout mRoot;
    private ImageView iv_back;
    private boolean isUrl;
    public WebTokenUtils  webTokenUtils;
    public WebUtils() {
    }
    @SuppressLint({"ResourceAsColor", "NewApi"})
    public void showWebViewDialog(Context context, String url,boolean isShopUrl,String htmlUrl) {
        this.context=context;
        dissmissAlertDialog();
        this.url=url;
        this.htmlurl=htmlUrl;
        EventBus.getDefault().register(this);
        creatAlertDialog();
    }

    private void creatAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View dialogView = View.inflate(context, R.layout.layout_dialog, null);
        im_web = dialogView.findViewById(R.id.im_web);
        iv_back = dialogView.findViewById(R.id.iv_back);
        mRoot = dialogView.findViewById(R.id.rl_root);
        rlLoading= dialogView.findViewById(R.id.rl_loading);
        animation_view= dialogView.findViewById(R.id.animation_view);
        animation_view.setAnimation("live_loading.json");
        animation_view.playAnimation();
        rlLoading.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmissAlertDialog();
            }
        });
        webTokenUtils=new WebTokenUtils(mContext,im_web,htmlurl);
        settings(im_web);
        if(url.startsWith("http")){
            isUrl=true;
        }
        im_web.loadUrl(url);
        builder.setCancelable(false);
        builder.setView(dialogView);
        shareDialog = builder.show();
        Window window = shareDialog.getWindow();
        window.setWindowAnimations(R.style.ActionSheetDialogAnimations);  //添加动
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = IMDensityUtil.getScreenWidth(context)-IMDensityUtil.dip2px(context,20);
        params.height=(int)(IMDensityUtil.getScreenHeight(context)*3.0f/4);
        shareDialog.getWindow().setAttributes(params);
        shareDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
    }

    public AlertDialog getShareDialog() {
        return shareDialog;
    }





    public void  dissmissAlertDialog(){
        if(shareDialog!=null && shareDialog!=null){
            shareDialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainRefreshJsEvent(MainRefreshJsEvent e) {
        webTokenUtils.RefreshJsEvent();
    }

    private void settings(WebView webView) {
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(mWebClient);
        webView.addJavascriptInterface(new AndroidInterface(null, context) , "android");
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
            rlLoading.setVisibility(View.GONE);
            im_web.setVisibility(View.VISIBLE);
            return super.shouldOverrideUrlLoading(view1, url);
        }

        @Override
        public void onPageFinished(WebView view1, String url) {
            L.e("WOLF", "onPageFinished:" + url);
            im_web.setVisibility(View.VISIBLE);
            rlLoading.setVisibility(View.GONE);
            super.onPageFinished(view1, url);
        }
    };

    private WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            L.e("WOLF", "onProgressChanged:" + newProgress);
            if(newProgress>(isUrl?50:10)){
                rlLoading.setVisibility(View.GONE);
                im_web.setVisibility(View.VISIBLE);
            }
        }
    };

}
