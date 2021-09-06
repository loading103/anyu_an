package com.yunbao.common.utils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.SPUtils;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.AgentWebUtils;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.TokenUtilsBean;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;

import java.util.Arrays;
import java.util.List;

import static com.just.agentweb.AbsAgentWebSettings.USERAGENT_AGENTWEB;
import static com.just.agentweb.AbsAgentWebSettings.USERAGENT_UC;

public class WebTokenUtils {
    public WebView mWebView;
    public Context context;
    public String htmlurl;
    public long lastTime = 0;
    public boolean isAgentWeb;
    private AgentWeb mAgentWeb;
    private WebSettings mWebSettings;
    private Handler handler = new Handler();
    public boolean isfirst;
    public static final String FILE_PATH = "file:///android_asset/dist/index.html#";
    public String   Tag="WebTokenUtils----";

    public WebTokenUtils(Context context, WebView mWebView, String htmlurl) {
        isAgentWeb = false;
        isfirst = true;
        this.mWebView = mWebView;
        this.context = context;
        if (!htmlurl.contains("?")) {
            this.htmlurl = htmlurl;
        } else {
            this.htmlurl = htmlurl.substring(0, htmlurl.indexOf("?"));
        }
    }

    public WebTokenUtils(Context context, AgentWeb mAgentWeb, String htmlurl) {
        isAgentWeb = true;
        isfirst = true;
        this.mAgentWeb = mAgentWeb;
        this.context = context;
        if (htmlurl==null||!htmlurl.contains("?")) {
            this.htmlurl = htmlurl;
        } else {
            this.htmlurl = htmlurl.substring(0, htmlurl.indexOf("?"));
        }
    }

    /**
     * 弹窗情况下的webview没有在这里初始化 因为存在圆角问题
     */
    public void initWebView() {
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        //设置可以支持缩放
        mWebSettings.setSupportZoom(true);
        //设置出现缩放工具
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setSavePassword(false);
        if (AgentWebUtils.checkNetwork(mWebView.getContext())) {
            //根据cache-control获取数据。
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            //没网，则从本地获取，即离线加载
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //适配5.0不允许http和https混合使用情况
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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
        //自适应屏幕
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setNeedInitialFocus(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        mWebSettings.setDefaultFontSize(16);
        mWebSettings.setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8
        mWebSettings.setGeolocationEnabled(true);
        String dir = AgentWebConfig.getCachePath(mWebView.getContext());
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 安卓9.0后不允许多进程使用同一个数据目录，需设置前缀来区分
            // 参阅 https://blog.csdn.net/lvshuchangyin/article/details/89446629
            Context context = mWebView.getContext();
            String processName = ProcessUtils.getCurrentProcessName();
            if (!context.getApplicationContext().getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);

            }
        }
        mWebView.setBackgroundColor(0);
    }

    public void RefreshJsEvent() {
        Log.e(Tag, "RefreshJsEvent,htmlurl=" + htmlurl);
        if (TextUtils.isEmpty(htmlurl)) {
            return;
        }
        if (htmlurl.startsWith("http")) {
            addUidToken();
        } else {
            getHtmlTokenUid();
        }
    }

    /**
     * url添加uid&token
     */
    private void addUidToken() {
        CommonHttpUtil.getUidToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, final String[] info) {
                if (isfirst) {
                    refreshHttpUrl(info);
                    isfirst = false;
                    return;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mAgentWeb == null && mWebView == null) {
                            return;
                        }

                        refreshHttpUrl(info);
                    }
                }, 2000);
            }
        });
    }

    /**
     * 延迟3秒组装http路径
     *
     */
    private void refreshHttpUrl(String[] info) {
        if (info == null || info.length == 0) {
            SPUtils.getInstance().put(Constants.HTML_TOKEN, "");
            Log.e(Tag, "refreshHttpUrl,htmlurl1=" + htmlurl);
            if (isAgentWeb) {
                mAgentWeb.getWebCreator().getWebView().loadUrl(htmlurl);
                mAgentWeb.getWebCreator().getWebView().loadUrl("javascript:window.location.reload( true )");
            } else {
                mWebView.loadUrl(htmlurl);
                mWebView.loadUrl("javascript:window.location.reload( true )");
            }
            return;
        }
        List<TokenUtilsBean> tokens = JSON.parseArray(Arrays.toString(info), TokenUtilsBean.class);
        SPUtils.getInstance().put(Constants.HTML_TOKEN, JSON.toJSONString(tokens));
        String uidToken = tokens.get(0).getQueryStr();
        Log.e(Tag, "refreshHttpUrl,htmlurl2=" + htmlurl + (htmlurl.contains("?") ? "&" : "?") + uidToken);
        if (isAgentWeb) {
            mAgentWeb.getWebCreator().getWebView().loadUrl(htmlurl + (htmlurl.contains("?") ? "&" : "?") + uidToken);
            mAgentWeb.getWebCreator().getWebView().loadUrl("javascript:window.location.reload( true )");
        } else {
            mWebView.loadUrl(htmlurl + (htmlurl.contains("?") ? "&" : "?") + uidToken);
            mWebView.loadUrl("javascript:window.location.reload( true )");
        }
    }

    /**
     * 组装本地路由路径
     */
    private void getHtmlTokenUid() {
        CommonHttpUtil.getUidToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, final String[] info) {
                if (isfirst) {
                    refreshHtmlTokenUrl(info);
                    isfirst = false;
                    return;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mAgentWeb == null && mWebView == null) {
                            return;
                        }
                        refreshHtmlTokenUrl(info);
                    }
                }, 2000);

            }
        });
    }

    /**
     * 延迟3秒组装本地路由路径
     */
    private void refreshHtmlTokenUrl(final String[] info) {
        String newUrl = "";
        if (info == null || info.length == 0) {
            SPUtils.getInstance().put(Constants.HTML_TOKEN, "");
            newUrl = htmlurl.startsWith("file:///android") ? htmlurl : FILE_PATH + htmlurl;
            Log.e(Tag, "refreshHtmlTokenUrl,newUrl=" + newUrl);
            if (isAgentWeb) {
                mAgentWeb.getWebCreator().getWebView().loadUrl(newUrl);
                mAgentWeb.getWebCreator().getWebView().loadUrl("javascript:window.location.reload( true )");
            } else {
                mWebView.loadUrl(newUrl);
                mWebView.loadUrl("javascript:window.location.reload( true )");
            }
            return;
        }
        List<TokenUtilsBean> tokens = JSON.parseArray(Arrays.toString(info), TokenUtilsBean.class);
        SPUtils.getInstance().put(Constants.HTML_TOKEN, JSON.toJSONString(tokens));
        String queryStr = tokens.get(0).getQueryStr();
        if (htmlurl.contains("?")) {
            newUrl = htmlurl.startsWith("file:///android") ? (htmlurl + "&" + queryStr) : (FILE_PATH + htmlurl + "&" + queryStr);
        } else {
            newUrl = htmlurl.startsWith("file:///android") ? (htmlurl + "?" + queryStr) : (FILE_PATH + htmlurl + "?" + queryStr);
        }
        Log.e(Tag, "refreshHtmlTokenUrl,newUrl=" + newUrl);
        if (isAgentWeb) {
            mAgentWeb.getWebCreator().getWebView().loadUrl(newUrl);
            mAgentWeb.getWebCreator().getWebView().loadUrl("javascript:window.location.reload( true )");
        } else {
            mWebView.loadUrl(newUrl);
            mWebView.loadUrl("javascript:window.location.reload( true )");
        }
    }

    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
