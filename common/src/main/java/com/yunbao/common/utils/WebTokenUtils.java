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
     * ??????????????????webview???????????????????????? ????????????????????????
     */
    public void initWebView() {
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        //????????????????????????
        mWebSettings.setSupportZoom(true);
        //????????????????????????
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setSavePassword(false);
        if (AgentWebUtils.checkNetwork(mWebView.getContext())) {
            //??????cache-control???????????????
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            //?????????????????????????????????????????????
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //??????5.0?????????http???https??????????????????
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
        // ??????????????????????????????  ??????http or https
        mWebSettings.setBlockNetworkImage(false);
        // ????????????????????????html  file??????
        mWebSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // ?????? file url ????????? Javascript ??????????????????????????? .????????????
            mWebSettings.setAllowFileAccessFromFileURLs(true);
            // ???????????? file url ????????? Javascript ??????????????????????????????????????????????????? http???https ???????????????
            mWebSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        } else {
            mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        //???????????????
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setNeedInitialFocus(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");//??????????????????
        mWebSettings.setDefaultFontSize(16);
        mWebSettings.setMinimumFontSize(12);//?????? WebView ??????????????????????????????????????? 8
        mWebSettings.setGeolocationEnabled(true);
        String dir = AgentWebConfig.getCachePath(mWebView.getContext());
//        LogUtils.i(TAG, "dir:" + dir + "   appcache:" + AgentWebConfig.getCachePath(webView.getContext()));
        //?????????????????????  api19 ????????????,??????????????? webkit ?????????
        mWebSettings.setGeolocationDatabasePath(dir);
        mWebSettings.setDatabasePath(dir);
        mWebSettings.setAppCachePath(dir);
        //?????????????????????
        mWebSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        mWebSettings.setUserAgentString(mWebSettings
                .getUserAgentString()
                .concat(USERAGENT_AGENTWEB)
                .concat(USERAGENT_UC)
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // ??????9.0???????????????????????????????????????????????????????????????????????????
            // ?????? https://blog.csdn.net/lvshuchangyin/article/details/89446629
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
     * url??????uid&token
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
     * ??????3?????????http??????
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
     * ????????????????????????
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
     * ??????3???????????????????????????
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
