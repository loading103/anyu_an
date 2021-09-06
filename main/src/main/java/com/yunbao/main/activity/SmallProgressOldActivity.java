package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;
import com.lzf.easyfloat.EasyFloat;
import com.yunbao.main.R;
import com.yunbao.main.interfaces.AndroidInterface;
import com.yunbao.main.utils.IMStatusBarUtil;


public class SmallProgressOldActivity extends AppCompatActivity {
    ImageView ivContent;
    TextView tvContent;
    RelativeLayout rlRight;
    RelativeLayout rlLeft;
    LinearLayout llRoot;
    RelativeLayout rlLoading;
    ImageView iv_top_right;
    private AgentWeb mAgentWeb;
    private String programUrl;
    private String programName;
    private String twoImage;
    private boolean isIndex = true;
    private boolean flag = false;
    private String url;
    private Animation operatingAnim;
    private Animation operatingAnim1;
    private ImageView iv_back;
    private boolean isFirst=true;
    private String firstReloadUrl;
    private boolean isGame=false;
    private String screenUrl;
    private String browserUrl;
    private int isCDX=1;
    private LottieAnimationView animationView;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_small_program);
            IMStatusBarUtil.setTranslucent(this, 0);
            IMStatusBarUtil.setLightMode(this);
            initAnim();
            initView();
            initFloat();
    }

    private void initView() {
        llRoot=findViewById(R.id.ll_root);
        rlLoading=findViewById(R.id.rl_loading);
        animationView=findViewById(R.id.animation_view);
        rlLoading.setVisibility(View.VISIBLE);
        if(getIntent()!=null){
            url = getIntent().getStringExtra("url");
            LogUtils.i("WOLF","url："+url);
        }

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(llRoot, new LinearLayout.LayoutParams(-1, -1))
                .closeIndicator()
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .createAgentWeb()
                .ready()
                .go(url);

        mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(mAgentWeb, this));
        mAgentWeb.getWebCreator().getWebView().getSettings().setJavaScriptEnabled(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccessFromFileURLs(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setLoadWithOverviewMode(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        mAgentWeb.getWebCreator().getWebView().setWebViewClient(new WebViewClient(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.i("WOLF","shouldOverrideUrlLoading:"+request.getUrl());
//                return super.shouldOverrideUrlLoading(view, request);
                if(TextUtils.isEmpty(firstReloadUrl)){
                    firstReloadUrl=request.getUrl().toString();
                }
                if(!TextUtils.isEmpty(screenUrl)){
                    //跳转游戏
                    BarUtils.setStatusBarVisibility(SmallProgressOldActivity.this,false);
                    IMStatusBarUtil.setDarkMode(SmallProgressOldActivity.this);
                    iv_back.startAnimation(operatingAnim);
                    mAgentWeb.getWebCreator().getWebView().loadUrl(screenUrl);
                    screenUrl=null;
                    isCDX=2;
                    return true;
                }else if(!TextUtils.isEmpty(browserUrl)){
                    Log.i("WOLF","跳转浏览器:"+browserUrl);
                    Intent intent= new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(request.getUrl());
                    try {
                        startActivity(intent);
                    }catch (Exception e){
                        Log.i("WOLF","url错误");
                        browserUrl =null;
                        return true;
                    }

                    browserUrl =null;
                    return true;
                }
                Log.i("WOLF","false");
                return false;

            }

            //页面加载开始
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i("WOLF","onPageStarted:"+url);
            }
            //页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i("WOLF","onPageFinished:"+url);
                if(isCDX==2){
                    isCDX=3;
                    return;
                }

                if(isCDX==3&&url.contains(firstReloadUrl.substring(0,10))){
                    isCDX=1;
                    BarUtils.setStatusBarVisibility(SmallProgressOldActivity.this,true);
                    Log.i("WOLF","setLightMode");
                    IMStatusBarUtil.setLightMode(SmallProgressOldActivity.this);
                    iv_back.startAnimation(operatingAnim1);
                }
            }
        });
    }

    public void toActivity(Context context, String url) {
        Intent intent = new Intent(context, SmallProgressOldActivity.class);
        intent.putExtra("url",url);
        context.startActivity(intent);
    }

    /**
     * 初始化旋转动画
     */
    private void initAnim() {
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.anim_rotate_90_game);
        operatingAnim.setFillAfter(true);
        operatingAnim1 = AnimationUtils.loadAnimation(this, R.anim.anim_rotate_90_game2);
        operatingAnim1.setFillAfter(true);
    }

    private void initFloat(){
        EasyFloat.with(this).setLayout(R.layout.view_game_float4)
                .setGravity(Gravity.BOTTOM|Gravity.RIGHT, -SizeUtils.dp2px(16), -SizeUtils.dp2px(70))
                .show();
        iv_back=EasyFloat.getFloatView().findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCDX==3){
                    if (mAgentWeb.getWebCreator().getWebView().canGoBack()) {
                        mAgentWeb.getWebCreator().getWebView().goBack();
                    }
                }else {
                    finish();
                }
//                finish();
            }
        });
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                rlLoading.setVisibility(View.GONE);
                iv_back.setVisibility(View.VISIBLE);
            } else {
                rlLoading.setVisibility(View.VISIBLE);
                iv_back.setVisibility(View.VISIBLE);
            }
            if(newProgress == 100){
//                if(isFirst){
//                    if (mAgentWeb.getWebCreator().getWebView().canGoBack()) {
//                        mAgentWeb.getWebCreator().getWebView().goBack();
//                        isFirst=false;
//                    }
//                }
            }
        }
    };


    public void setScreen(String url) {
        Log.i("WOLF","横屏");
        screenUrl=url;
    }

    public void toBrowser(String url){
        Log.i("WOLF","浏览器");
        browserUrl =url;
    }

    public void onBackPressed() {
        if (!mAgentWeb.back()) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        AgentWebConfig.clearDiskCache(this);
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }
}
