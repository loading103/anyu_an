package com.yunbao.common.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.AgentWebUtils;
import com.yunbao.common.CommonAppConfig;
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
import com.yunbao.common.utils.AppUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WebTokenUtils;
import com.yunbao.common.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.just.agentweb.AbsAgentWebSettings.USERAGENT_AGENTWEB;
import static com.just.agentweb.AbsAgentWebSettings.USERAGENT_UC;

/**
 * Created by cxf on 2018/9/25.
 */

public class WebViewActivity extends AbsActivity implements ImageResultCallback {

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private final int CHOOSE = 100;//Android 5.0以下的
    private final int CHOOSE_ANDROID_5 = 200;//Android 5.0以上的
    private ValueCallback<Uri> mValueCallback;
    private ValueCallback<Uri[]> mValueCallback2;
    private RelativeLayout rlLoading;
    private boolean isNew;//是否是新增后台配置跳转
    private boolean isShopUrl;
    private LottieAnimationView animationView;
    private TextView tvBack;
    private FrameLayout flRoot;
    private TextView titleView;
    private ImageView btnBack;
    private TextView mTvFinish;
    private String currentUrl;
    private String htmlurl;
    private String mBack;
    private boolean isThis;
    private boolean isFirst=true;
    private String url;
    private  String titles;
    private ValueCallback<Uri[]> mValueListCallback;
    private ProcessImageUtil mImageUtil;
    public WebTokenUtils webTokenUtils;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void main() {
        initPhoto();
        EventBus.getDefault().register(this);
        titles = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra(Constants.URL);

        boolean showLoading = getIntent().getBooleanExtra("showLoading", false);
        htmlurl = getIntent().getStringExtra("htmlurl");

        if(TextUtils.isEmpty(htmlurl)){
            if(!url.contains("?")){
                htmlurl=url;
            }else {
                htmlurl=url.substring(0,url.indexOf("?"));
            }
        }
        isNew = getIntent().getBooleanExtra("isNew", false);
        isShopUrl = getIntent().getBooleanExtra("isShopUrl", false);
        rlLoading = (RelativeLayout) findViewById(com.yunbao.common.R.id.rl_loading);
        tvBack = findViewById(R.id.tv_back);
        flRoot = findViewById(R.id.fl_root);
        mTvFinish = findViewById(R.id.tv_finish);
        initScreen();
        titleView = findViewById(R.id.titleView);
        btnBack = findViewById(R.id.btn_back);
        animationView = findViewById(R.id.animation_view);
        mTvFinish.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(titles)) {
            titleView.setText(titles);
        }
        animationView.setAnimation("live_loading.json");
        animationView.playAnimation();
        if (showLoading) {
            rlLoading.setVisibility(View.VISIBLE);
        }

        if (isNew) {
            tvBack.setVisibility(View.VISIBLE);
            flRoot.setBackground(getResources().getDrawable(R.drawable.main_title));
            tvBack.setTextColor(getResources().getColor(R.color.white));
            mTvFinish.setTextColor(getResources().getColor(R.color.white));
            titleView.setTextColor(getResources().getColor(R.color.white));
            btnBack.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            mTvFinish.setVisibility(View.GONE);
        }

        L.e("H5--->" + url);
        FrameLayout rootView = (FrameLayout) findViewById(R.id.rootView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mTvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back(mBack);
            }
        });
        mWebView = new WebView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.topMargin = DpUtil.dp2px(1);
        mWebView.setLayoutParams(params);
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rootView.addView(mWebView);
        mWebView.addJavascriptInterface(new AndroidInterface(null, WebViewActivity.this), "android");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                L.e("H5-------->" + url);
                currentUrl = url;
                if (url.startsWith(Constants.COPY_PREFIX)) {
                    String content = url.substring(Constants.COPY_PREFIX.length());
                    if (!TextUtils.isEmpty(content)) {
                        copy(content);
                    }
                } else {
                    view.loadUrl(url);
                }
                return super.shouldOverrideUrlLoading(view,url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!isNew && TextUtils.isEmpty(titles)) {
                    setTitle(view.getTitle());
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                    rlLoading.setVisibility(View.GONE);
                } else {
                    mProgressBar.setProgress(newProgress);
                    rlLoading.setVisibility(View.VISIBLE);
                }
            }

            //以下是在各个Android版本中 WebView调用文件选择器的方法
            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                openImageChooserActivity(valueCallback);
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                openImageChooserActivity(valueCallback);
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback,
                                        String acceptType, String capture) {
                openImageChooserActivity(valueCallback);
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mValueListCallback = filePathCallback;
                choosePhoto();
                return true;
            }
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
//               setTitle(title);
            }
        });

        webTokenUtils=new WebTokenUtils(WebViewActivity.this,mWebView,htmlurl);
        webTokenUtils.initWebView();
        KeyboardUtils.fixAndroidBug5497(this);
        mWebView.loadUrl(url);
    }
    @Override
    public void backClick(View v) {
        back(mBack);
    }

    private void initPhoto() {
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(this);
    }
    /**
     * 挖孔屏适配
     */
    private void initScreen() {
        if(AppUtil.CutOutSafeHeight>0){
            LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) flRoot.getLayoutParams();
            linearParams.height = SizeUtils.dp2px(SizeUtils.px2dp(AppUtil.CutOutSafeHeight)+40);
            flRoot.setLayoutParams(linearParams);
        }
    }

    private void openImageChooserActivity(ValueCallback<Uri> valueCallback) {
        mValueCallback = valueCallback;
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, WordUtil.getString(R.string.choose_flie)), CHOOSE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case CHOOSE://5.0以下选择图片后的回调
                processResult(resultCode, intent);
                break;
            case CHOOSE_ANDROID_5://5.0以上选择图片后的回调
                processResultAndroid5(resultCode, intent);
                break;
        }
    }

    private void processResult(int resultCode, Intent intent) {
        if (mValueCallback == null) {
            return;
        }
        if (resultCode == RESULT_OK && intent != null) {
            Uri result = intent.getData();
            mValueCallback.onReceiveValue(result);
        } else {
            mValueCallback.onReceiveValue(null);
        }
        mValueCallback = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void processResultAndroid5(int resultCode, Intent intent) {
        if (mValueCallback2 == null) {
            return;
        }
        if (resultCode == RESULT_OK && intent != null) {
            mValueCallback2.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
        } else {
            mValueCallback2.onReceiveValue(null);
        }
        mValueCallback2 = null;
    }
    @Override
    public void onBackPressed() {
        mExit();
    }

    private void mExit() {
        if (isNeedExitActivity()) {
            finish();
            overridePendingTransition(0, R.anim.im_out_from_right);
        } else {
            finish();
            overridePendingTransition(0, R.anim.im_out_from_right);
        }
    }

    private boolean isNeedExitActivity() {
        if (mWebView != null) {
            String url = mWebView.getUrl();
            if (!TextUtils.isEmpty(url)) {
                return url.contains("g=Appapi&m=Auth&a=success")//身份认证成功页面
                        || url.contains("g=Appapi&m=Family&a=home");//家族申请提交成功页面

            }
        }
        return false;
    }
    public static void forwardNo(Context context, String url) {
        String invitecode = SpUtil.getInstance().getStringValue(SpUtil.INVITE_MEMBER);
        url += "?uid=" + CommonAppConfig.getInstance().getUid() + "&token=" + CommonAppConfig.getInstance().getToken() + "&code=" + invitecode;
        Log.e("-------",url);
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        context.startActivity(intent);
    }
    public static void forwardNo(Context context, String url,String title) {
        String invitecode = SpUtil.getInstance().getStringValue(SpUtil.INVITE_MEMBER);
        url += "?uid=" + CommonAppConfig.getInstance().getUid() + "&token=" + CommonAppConfig.getInstance().getToken() + "&code=" + invitecode;
        Log.e("-------",url);
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    public static void forward(Context context, String url, boolean addArgs) {
        if (addArgs) {
            url += "&uid=" + CommonAppConfig.getInstance().getUid() + "&token=" + CommonAppConfig.getInstance().getToken();
        }
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        context.startActivity(intent);
    }
    public static void forward(Context context, String url, boolean addArgs,String title) {
        if (addArgs) {
            url += "&uid=" + CommonAppConfig.getInstance().getUid() + "&token=" + CommonAppConfig.getInstance().getToken();
        }
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }
    public static void forward2(Context context, String url, String title, boolean addArgs) {
        if (addArgs) {
            url += "&uid=" + CommonAppConfig.getInstance().getUid() + "&token=" + CommonAppConfig.getInstance().getToken();
        }
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        intent.putExtra("title", title);
        intent.putExtra("showLoading", true);
        intent.putExtra("isNew", true);
        context.startActivity(intent);
    }

    public static void forwardToRight(Context context, String url, String title, boolean isShopUrl) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        intent.putExtra("title", title);
        intent.putExtra("showLoading", true);
        intent.putExtra("isNew", true);
        intent.putExtra("isShopUrl", isShopUrl);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.im_in_from_right, 0);
    }

    public static void forwardToRight(Context context, String url, String title, boolean isShopUrl, String htmlurl) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        intent.putExtra("title", title);
        intent.putExtra("showLoading", true);
        intent.putExtra("isNew", true);
        intent.putExtra("isShopUrl", isShopUrl);
        intent.putExtra("htmlurl", htmlurl);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.im_in_from_right, 0);
    }

    public static void forward(Context context, String url) {
        forward(context, url, true);
    }
    public static void forward(Context context, String url,String title) {
        forward(context, url, true,title);
    }

    public static void forwardRight(Context context, String url, String title, boolean isShopUrl) {
        forwardToRight(context, url, title, isShopUrl);
    }

    public static void forwardRight(Context context, String url, String title, boolean isShopUrl, String htmlUrl) {
        forwardToRight(context, url, title, isShopUrl, htmlUrl);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.destroy();
        }
        if(webTokenUtils!=null){
            webTokenUtils.onDestroy();
        }
        if(mValueListCallback!=null){
            mValueListCallback.onReceiveValue(null);
            mValueListCallback=null;
        }

        if(mWebView!=null){
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.destroy();
            mWebView=null;
        }

        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 复制到剪贴板
     */
    private void copy(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", content);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(getString(R.string.copy_success));
    }

    /**
     * 设置标题
     *
     * @param msg
     */
    public void setTitleName(String msg) {
        if (titleView != null) {
            titleView.setText(msg);
        }
    }

    public void back(String back) {
        if (TextUtils.isEmpty(back) || back.equals("app")) {
            isThis = true;
            if (isNeedExitActivity()) {
                finish();
            } else {
                finish();
            }
        } else {
            if(mWebView.canGoBack()){
                mWebView.goBack();
            }else {
                isThis = true;
                if (isNeedExitActivity()) {
                    finish();
                } else {
                    finish();
                }
            }
        }

    }

    /**
     * 是否显示标题栏
     *
     * @param msg
     */
    public void showTitle(String msg) {
        if (msg.equals("y")) {
            flRoot.setVisibility(View.VISIBLE);
        } else {
            flRoot.setVisibility(View.GONE);
        }
    }

    /**
     * js回调
     *
     * @param bean
     */
    public void onJs(final JsWebBean bean) {
        if (!TextUtils.isEmpty(bean.getTitle())) {
            setTitleName(bean.getTitle());
        }
        if (!TextUtils.isEmpty(bean.getShowNai())) {
            showTitle(bean.getShowNai());
        }
        if (bean.getBack() == null) {
            tvBack.setVisibility(View.INVISIBLE);
            btnBack.setVisibility(View.INVISIBLE);
        } else {
            tvBack.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(bean.getBack())) {
            mBack = bean.getBack();
        }
        if (bean.getRightTitle() != null && bean.getRightTitle().getName() != null) {
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
                            mUrl = "http://" + getHost(currentUrl) + mUrl;
                        }
                        WebViewActivity.forwardRight(WebViewActivity.this, mUrl, bean.getRightTitle().getName(), isShopUrl);
                    }else {//本地路由
                        if (mUrl.startsWith("http")) {//跳转是网页
                            WebViewActivity.forwardRight(WebViewActivity.this, mUrl, bean.getRightTitle().getName(), isShopUrl);
                        }else {
                            OpenUrlUtils.getInstance()
                                    .setContext(WebViewActivity.this)
                                    .setType(6)
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
     * 根据 url 获取 host name
     * http://www.gcssloop.com/ => www.gcssloop.com
     */
    public static String getHost(String url) {
        if (url == null || url.trim().equals("")) {
            return "";
        }
        String host = "";



        Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            host = matcher.group();
        }
        return host;
    }

    /**
     * 结束此页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishEvent(FinishEvent e) {
        if (isNeedExitActivity()) {
            finish();
        } else {
            finish();
        }
    }

    public void resetWebview() {
        webTokenUtils.RefreshJsEvent();
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
