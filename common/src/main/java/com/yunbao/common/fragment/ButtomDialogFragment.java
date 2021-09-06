package com.yunbao.common.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.AgentWebUtils;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.R;
import com.yunbao.common.activity.SmallProgramTitleActivity;
import com.yunbao.common.bean.TokenBean;
import com.yunbao.common.bean.TokenUtilsBean;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.dialog.weak.WeakDialog;
import com.yunbao.common.event.DialogBottomEvent;
import com.yunbao.common.event.DialogShowEvent;
import com.yunbao.common.event.MainRefreshJsEvent;
import com.yunbao.common.event.ShowMsgEvent;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.AndroidInterface;
import com.yunbao.common.interfaces.DissmissDialogListener;
import com.yunbao.common.interfaces.OnHiddenListener;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.WebTokenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.just.agentweb.AbsAgentWebSettings.USERAGENT_AGENTWEB;
import static com.just.agentweb.AbsAgentWebSettings.USERAGENT_UC;

/**
 * Created by cxf on 2018/10/24.
 * 直播间礼物旁边更多
 */

public class ButtomDialogFragment extends AbsDialogFragment {
    private final boolean canCancel;
    private ViewGroup fl_root;
    boolean isaplha = false;
    private RelativeLayout rlLoading;
    private WebView im_web;
    private boolean isShopUrl;
    private LottieAnimationView animation_view;
    private DissmissDialogListener ondissDialogListener;
    private String currentUrl;
    private String htmlurl;
    private FrameLayout fl;
    private FrameLayout flMain;
    private boolean mCan = true;
    private CountDownTimer mTimer;
    private boolean isUrl;

    public WebTokenUtils  webTokenUtils;
    public void setmCan(boolean mCan) {
        this.mCan = mCan;
    }

    public ButtomDialogFragment(boolean isaplha, boolean canCancel, boolean isShopUrl, String htmlUrl) {
        this.isaplha = isaplha;
        this.canCancel = canCancel;
        this.isShopUrl = isShopUrl;
        this.htmlurl = htmlUrl;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_buttom;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return canCancel;
    }

    @Override
    protected void setWindowAttributes(final Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setBackgroundDrawableResource(R.color.transparent);
        //状态栏变色问题
        window.setLayout((ViewGroup.LayoutParams.MATCH_PARENT), ScreenUtils.getScreenHeight()-  BarUtils.getStatusBarHeight()-BarUtils.getNavBarHeight());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle bundle = getArguments();
        fl_root = (ViewGroup) findViewById(R.id.fl_root);
        animation_view = (LottieAnimationView) findViewById(R.id.animation_view);
        rlLoading = (RelativeLayout) findViewById(R.id.rl_loading);
        fl = (FrameLayout) findViewById(R.id.fl);
        flMain = (FrameLayout) findViewById(R.id.fl_main);

        ViewGroup.LayoutParams ls = flMain.getLayoutParams();
        ls.height = (int) (ScreenUtils.getScreenWidth() * 0.8938);
        flMain.setLayoutParams(ls);
        im_web = new WebView(mContext.getApplicationContext());
        fl.addView(im_web);
        im_web.setVisibility(View.INVISIBLE);
        fl_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    if (mCan) {
                        getDialog().dismiss();
                    } else {
                        getDialog().hide();
                        EventBus.getDefault().post(new DialogShowEvent());
                    }
                }
            }
        });

        animation_view.setAnimation("live_loading.json");
        animation_view.playAnimation();
        rlLoading.setVisibility(View.VISIBLE);

        if (bundle == null) {
            return;
        }
        String more_web_url = bundle.getString(Constants.MORE_WEB_URL);
        if(more_web_url.startsWith("http")){
            isUrl=true;
        }
        openUrl(more_web_url);
    }

    public ViewGroup getFl_root() {
        return fl_root;
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void openUrl(String url) {
        webTokenUtils=new WebTokenUtils(mContext,im_web,htmlurl);
        webTokenUtils.initWebView();
        im_web.setWebViewClient(mWebClient);
        im_web.setWebChromeClient(webChromeClient);
        im_web.addJavascriptInterface(new AndroidInterface(null, mContext), "android");
        im_web.loadUrl(url);
        im_web.setBackgroundColor(0);
    }

    private WebViewClient mWebClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            L.e("WOLF-----onPageStarted", url);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view1, String url) {
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
        public void onProgressChanged(WebView view1, int newProgress) {
            super.onProgressChanged(view1, newProgress);
            L.e("WOLF", "onProgressChanged:" + newProgress);
            if(newProgress>(isUrl?50:10)){
                rlLoading.setVisibility(View.GONE);
                im_web.setVisibility(View.VISIBLE);
            }
        }
    };

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
    public void setOnDissmissDialogListener(DissmissDialogListener ondissDialogListener) {
        this.ondissDialogListener = ondissDialogListener;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainRefreshJsEvent(MainRefreshJsEvent e) {
        if(webTokenUtils!=null){
            webTokenUtils.RefreshJsEvent();
        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if(mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
        if(webTokenUtils!=null){
            webTokenUtils.onDestroy();
        }
        if(ondissDialogListener!=null){
            ondissDialogListener=null;

        }
        super.onDestroy();
    }
    /**
     * 底淡键盘
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDialogBottomEvent(DialogBottomEvent e) {
        setMarginBottom(e.getHeight());
    }
    /**
     * 底部距离
     * @param height
     */
    public void setMarginBottom(int height){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.height = (int) (ScreenUtils.getScreenWidth() * 0.8938);
        layoutParams.gravity = Gravity.BOTTOM;
        int h= SizeUtils.px2dp(height)>41?SizeUtils.px2dp(height)-41:SizeUtils.px2dp(height);
        layoutParams.setMargins(0,0,0,SizeUtils.dp2px(h));
        flMain.setLayoutParams(layoutParams);
    }
}

