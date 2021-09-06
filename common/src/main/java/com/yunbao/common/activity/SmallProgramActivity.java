package com.yunbao.common.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;
import com.lzf.easyfloat.EasyFloat;
import com.yunbao.common.R;
import com.yunbao.common.event.FinishEvent;
import com.yunbao.common.interfaces.AndroidInterface;
import com.yunbao.common.interfaces.ImageResultCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.WebTokenUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;


public class SmallProgramActivity extends BaseFotBitShotActivity implements ImageResultCallback {
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
    private boolean isscreen=false;
    private String screenUrl;
    private String browserUrl;
    private int isCDX=1;
    private LottieAnimationView animationView;
    private boolean loadTransparent;
    private boolean isShopUrl;
    private String htmlUrl;//路由地址
    private Uri mUrl;
    private ValueCallback<Uri[]> mValueListCallback;
    private ProcessImageUtil mImageUtil;
    private int slide_show_type_button;//0-有按钮 1-无按钮
    public WebTokenUtils  webTokenUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideBottomUIMenu();
        setContentView(R.layout.activity_small_program);
        initPhoto();
        initAnim();
        initView();
//        if(slide_show_type_button==0){
            initFloat();
//        }
    }

    private void initPhoto() {
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(this);
    }

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void initView() {
        llRoot=findViewById(R.id.ll_root);
        rlLoading=findViewById(R.id.rl_loading);
        animationView=findViewById(R.id.animation_view);

        if(getIntent()!=null){
            url = getIntent().getStringExtra("url");
            isscreen = getIntent().getBooleanExtra("isscreen",false);
            loadTransparent = getIntent().getBooleanExtra("loadTransparent",false);
            isShopUrl =getIntent().getBooleanExtra("isShopUrl",false);
            htmlUrl= getIntent().getStringExtra("htmlurl");
            slide_show_type_button= getIntent().getIntExtra("slide_show_type_button",0);
        }
        if(isscreen){//设置横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            BarUtils.setStatusBarVisibility(this,false);
        }
        if(loadTransparent){
            rlLoading.setAlpha(0.5f);
        }
        animationView.setAnimation("live_loading.json");
        animationView.playAnimation();
        rlLoading.setVisibility(View.VISIBLE);

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
                mUrl=request.getUrl();
                if(TextUtils.isEmpty(firstReloadUrl)){
                    firstReloadUrl=request.getUrl().toString();
                }
                if(!TextUtils.isEmpty(screenUrl)){
                    if(iv_back!=null){
                        iv_back.startAnimation(operatingAnim);
                    }
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
                        browserUrl =null;
                        return true;
                    }

                    browserUrl =null;
                    return true;
                }
                return false;
            }

            //页面加载开始
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
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
                    if(iv_back!=null){
                        iv_back.startAnimation(operatingAnim1);
                    }
                }
            }
        });

        webTokenUtils=new WebTokenUtils(SmallProgramActivity.this,mAgentWeb,htmlUrl);
    }

    public void toActivity(Context context, String url,boolean isShopUrl,String htmlUrl,int slide_show_type_button) {
        Intent intent = new Intent(context, SmallProgramActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("isShopUrl",isShopUrl);
        intent.putExtra("htmlurl",htmlUrl);
        intent.putExtra("slide_show_type_button",slide_show_type_button);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.im_in_from_right, 0);
    }

    /**
     *横屏
     */
    public  static void toSmallActivity(Context context, String url,boolean isShopUrl, String htmlurl,int slide_show_type_button) {
        Intent intent = new Intent(context, SmallProgramActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("isscreen",true);
        intent.putExtra("isShopUrl",isShopUrl);
        intent.putExtra("htmlurl",htmlurl);
        intent.putExtra("slide_show_type_button",slide_show_type_button);
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
        if(isscreen){
            EasyFloat.with(this).setLayout(R.layout.view_game_float4)
                    .setGravity(Gravity.BOTTOM|Gravity.RIGHT, -SizeUtils.dp2px(50), -SizeUtils.dp2px(22))
                    .show();
        }else {
            EasyFloat.with(this).setLayout(R.layout.view_game_float4)
                    .setGravity(Gravity.TOP|Gravity.LEFT, SizeUtils.dp2px(12), SizeUtils.dp2px(5))
                    .show();
        }

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
                    overridePendingTransition(0, R.anim.im_out_from_right);
                }
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
                if(slide_show_type_button==0){ //0-有按钮 1-无按钮
                    if(iv_back!=null){
                        iv_back.setVisibility(View.VISIBLE);
                    }
                }else {
                    if(iv_back!=null){
                        iv_back.setVisibility(View.GONE);
                    }
                }

            }else {
                if(iv_back!=null){
                    iv_back.setVisibility(View.VISIBLE);
                }
            }
        }
        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            mValueListCallback = filePathCallback;
            choosePhoto();
            return true;
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
            overridePendingTransition(0, R.anim.im_out_from_right);
        }
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        hideBottomUIMenu();
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        AgentWebConfig.clearDiskCache(this);
        mAgentWeb.getWebLifeCycle().onDestroy();
        if(webTokenUtils!=null){
            webTokenUtils.onDestroy();
        }
        if(mValueListCallback!=null){
            mValueListCallback.onReceiveValue(null);
            mValueListCallback=null;
        }
        super.onDestroy();
    }

    public void back() {
    }
    /**
     * 结束此页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishEvent(FinishEvent e) {
        finish();
    }

    public void resetWebview() {
        if(webTokenUtils!=null){
            webTokenUtils.RefreshJsEvent();
        }
    }
    @Override

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            hideBottomUIMenu();
        }

    }

    /**
     * 选择图片
     */
    private void choosePhoto() {
        DialogUitl.showStringArrayDialog1(this, new Integer[]{
                R.string.camera, R.string.alumb}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.camera) {
                    mImageUtil.getImageByCamera();
                } else {
                    mImageUtil.getImageByAlumb();
                }
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mValueListCallback!=null){
                    mValueListCallback.onReceiveValue(null);
                    mValueListCallback=null;
                }
            }
        });
    }


    @Override
    public void beforeCamera() {

    }

    @Override
    public void onSuccess(File file) {
        if (file != null) {
            handleUpdatePhoto(file);
        }
    }

    @Override
    public void onFailure() {
        if(mValueListCallback!=null){
            mValueListCallback.onReceiveValue(null);
            mValueListCallback=null;
        }
    }

    /**
     * 处理选择文件之后
     */
    public void handleUpdatePhoto(File file) {
        if(mValueListCallback==null){
            return;
        }
        if(mValueListCallback!=null){
            Uri uri = getImageContentUri(this, file);
            Uri[] uridatas = new Uri[]{uri};
            mValueListCallback.onReceiveValue(uridatas);
            mValueListCallback=null;

        }
    }

    public static Uri getImageContentUri(Context context,File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 返回app页面
     */
    public void backApp() {
        finish();
        overridePendingTransition(0, R.anim.im_out_from_right);
    }
}
