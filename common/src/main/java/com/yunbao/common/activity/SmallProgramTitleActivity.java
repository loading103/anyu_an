package com.yunbao.common.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;
import com.lzf.easyfloat.EasyFloat;
import com.yunbao.common.Constants;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.R;
import com.yunbao.common.bean.JsWebBean;
import com.yunbao.common.bean.TokenUtilsBean;
import com.yunbao.common.event.FinishEvent;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.AndroidInterface;
import com.yunbao.common.interfaces.ImageResultCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.common.utils.IMStatusBarUtil;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.WebTokenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public class SmallProgramTitleActivity extends BaseFotBitShotActivity implements ImageResultCallback {
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
    private ImageView iv_back;
    private String firstReloadUrl;
    private boolean isGame=false;
    private boolean isscreen=false;
    private String screenUrl;
    private String browserUrl;
    private int isCDX=1;
    private LottieAnimationView animationView;
    private boolean loadTransparent;
    private String title;
    private TextView titleView;
    private FrameLayout flRoot;
    private TextView mTvFinish;
    private TextView tvBack;
    private ImageView btnBack;
    private Uri currentUrl;
    private boolean isShopUrl;
    private String mBack;
    private String htmlurl;
    private boolean isFirst2=true;
    private ValueCallback<Uri[]> mValueListCallback;
    private ProcessImageUtil mImageUtil;
    public WebTokenUtils webTokenUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_small_program_title);
        initPhoto();
        initView();
    }


    private void initPhoto() {
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(this);
    }
    @SuppressLint("SourceLockedOrientationActivity")
    private void initView() {
        EventBus.getDefault().register(this);
        llRoot=findViewById(R.id.ll_root);
        rlLoading=findViewById(R.id.rl_loading);
        animationView=findViewById(R.id.animation_view);
        rlLoading.setVisibility(View.VISIBLE);

        tvBack = findViewById(R.id.tv_back);
        flRoot = findViewById(R.id.fl_root);
        mTvFinish = findViewById(R.id.tv_finish);
        titleView = findViewById(R.id.titleView);
        btnBack = findViewById(R.id.btn_back);

        tvBack.setVisibility(View.VISIBLE);
        flRoot.setBackground(getResources().getDrawable(R.drawable.main_title));
        tvBack.setTextColor(getResources().getColor(R.color.white));
        mTvFinish.setTextColor(getResources().getColor(R.color.white));
        titleView.setTextColor(getResources().getColor(R.color.white));
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back(mBack);
            }
        });
        btnBack.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white)));
        mTvFinish.setVisibility(View.GONE);

        if(getIntent()!=null){
            url = getIntent().getStringExtra("url");
            title = getIntent().getStringExtra("title");
            isscreen = getIntent().getBooleanExtra("isscreen",false);
            loadTransparent = getIntent().getBooleanExtra("loadTransparent",false);
            isShopUrl =getIntent().getBooleanExtra("isShopUrl",false);
            htmlurl=getIntent().getStringExtra("htmlUrl");
            LogUtils.i("WOLF","url："+url);
            if(!TextUtils.isEmpty(title)&&!title.equals("normal")){
                titleView.setText(title);
            }
        }
        if(isscreen){//设置横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            BarUtils.setStatusBarVisibility(SmallProgramTitleActivity.this,false);
            IMStatusBarUtil.setDarkMode(SmallProgramTitleActivity.this);
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
                currentUrl=request.getUrl();
                if(TextUtils.isEmpty(firstReloadUrl)){
                    firstReloadUrl=request.getUrl().toString();
                }
                if(!TextUtils.isEmpty(screenUrl)){
                    //跳转游戏
                    BarUtils.setStatusBarVisibility(SmallProgramTitleActivity.this,false);
                    IMStatusBarUtil.setDarkMode(SmallProgramTitleActivity.this);
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
                    BarUtils.setStatusBarVisibility(SmallProgramTitleActivity.this,true);
                    Log.i("WOLF","setLightMode");
                    IMStatusBarUtil.setLightMode(SmallProgramTitleActivity.this);
                }
            }
        });
        webTokenUtils=new WebTokenUtils(SmallProgramTitleActivity.this,mAgentWeb,htmlurl);
    }

    public void toActivity(Context context, String url,String title) {
        Intent intent = new Intent(context, SmallProgramTitleActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("title",title);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.im_in_from_right, 0);
    }


    /**
     * 单独处理获取vip链接(需要不要拼接toekn)
     */
    public void toActivity(Context context, String url,String title,boolean vipToken) {
        String viptoken = SpUtil.getInstance().getStringValue(SpUtil.VIP_TOKEN);
        Log.e("-----vipUrl","viptoken="+viptoken);
        if(viptoken.equals("1")){
            vipHaveToken(context,url,title);
        }else {
            vipNoToken( context,url,title);
        }
    }

    /**
     * vip需要加token
     */
    private void vipHaveToken(final Context context, final String url, final String title) {
        CommonHttpUtil.getUidToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, final String[] info) {
                if (info == null || info.length == 0) {
                    SPUtils.getInstance().put(Constants.HTML_TOKEN, "");
                    vipNoToken( context,url,title);
                    return;
                }
                List<TokenUtilsBean> tokens = JSON.parseArray(Arrays.toString(info), TokenUtilsBean.class);
                SPUtils.getInstance().put(Constants.HTML_TOKEN, JSON.toJSONString(tokens));
                String uidToken = tokens.get(0).getQueryStr();
                String newurl=url + (url.contains("?") ? "&" : "?") + uidToken;
                vipNoToken( context,newurl,title);
            }
        });
    }


    private void vipNoToken(Context context, String url,String title) {
        Log.e("-----vipUrl","url="+url);
        Intent intent = new Intent(context, SmallProgramTitleActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("title",title);
        intent.putExtra("htmlUrl",url);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.im_in_from_right, 0);
    }

    public void toActivity(Context context, String url,String title, boolean isShopUrl,String htmlUrl) {
        Intent intent = new Intent(context, SmallProgramTitleActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("title",title);
        intent.putExtra("isShopUrl",isShopUrl);
        intent.putExtra("htmlUrl",htmlUrl);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.im_in_from_right, 0);
    }


    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

            if(!TextUtils.isEmpty(SmallProgramTitleActivity.this.title)&&SmallProgramTitleActivity.this.title.equals("normal")){
                titleView.setText(title);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                rlLoading.setVisibility(View.GONE);
            } else {
                rlLoading.setVisibility(View.VISIBLE);
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

    public void backClick(View v) {
        if (v.getId() == R.id.btn_back) {
            back(mBack);
        }
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
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        AgentWebConfig.clearDiskCache(this);
        mAgentWeb.getWebLifeCycle().onDestroy();
        EventBus.getDefault().unregister(this);
        if(webTokenUtils!=null){
            webTokenUtils.onDestroy();
        }
        if(mValueListCallback!=null){
            mValueListCallback.onReceiveValue(null);
            mValueListCallback=null;
        }
        super.onDestroy();
    }

    /**
     * 设置透明状态栏
     */
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (false) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0);
        }
    }


    /**
     * 设置标题
     * @param msg
     */
    public void setTitleName(String msg) {
        if(titleView!=null){
            titleView.setText(msg);

        }
    }

    public void back(String back) {
        if(TextUtils.isEmpty(back)||back.equals("app")){
            finish();
        }else {
            if(mAgentWeb.getWebCreator().getWebView().canGoBack()){
                mAgentWeb.back();
            }else {
                finish();
            }
        }

    }

    /**
     * 是否显示标题栏
     * @param msg
     */
    public void showTitle(String msg) {
        if(msg.equals("y")){
            flRoot.setVisibility(View.VISIBLE);
        }else {
            flRoot.setVisibility(View.GONE);
        }
    }

    /**
     * js回调
     * @param bean
     */
    public void onJs(final JsWebBean bean) {
        if(!TextUtils.isEmpty(bean.getTitle())){
            setTitleName(bean.getTitle());
        }
        if(!TextUtils.isEmpty(bean.getShowNai())){
            showTitle(bean.getShowNai());
        }
        if(bean.getBack()==null){
            tvBack.setVisibility(View.INVISIBLE);
            btnBack.setVisibility(View.INVISIBLE);
        }else {
            tvBack.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
        }
        if(!TextUtils.isEmpty(bean.getBack())){
            mBack=bean.getBack();
        }
        if(bean.getRightTitle()!=null&&bean.getRightTitle().getName()!=null){
            mTvFinish.setVisibility(View.VISIBLE);
            mTvFinish.setText(bean.getRightTitle().getName());
            mTvFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(url==null){
                        return;
                    }
                    String mUrl=bean.getRightTitle().getUrl();
                    if(url.startsWith("http")){
                        if (mUrl.startsWith("http")) {
                            mUrl = bean.getRightTitle().getUrl();
                        } else {
                            mUrl = "http://" + currentUrl.getHost() + mUrl;
                        }
                        new SmallProgramTitleActivity().toActivity(SmallProgramTitleActivity.this,
                                mUrl,bean.getRightTitle().getName());
                    }else {//本地路由
                        if (mUrl.startsWith("http")) {//跳转是网页
                            new SmallProgramTitleActivity().toActivity(SmallProgramTitleActivity.this,
                                    mUrl,bean.getRightTitle().getName());
                        }else {
                            OpenUrlUtils.getInstance().setContext(SmallProgramTitleActivity.this)
                                    .setType(3)
                                    .setTitle(bean.getTitle())
                                    .setSlideTarget("1")
                                    .setJump_type("2")
                                    .go(mUrl.replace("/#",""));
                        }
                    }

                }
            });
        }
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
}
