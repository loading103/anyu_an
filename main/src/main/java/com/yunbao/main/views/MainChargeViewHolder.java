package com.yunbao.main.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebChromeClient;
import com.yunbao.common.Constants;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.JsWebBean;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.bean.TabBarBean;
import com.yunbao.common.bean.TokenUtilsBean;
import com.yunbao.common.event.MainJsEvent;
import com.yunbao.common.event.MainRefreshJsEvent;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.AndroidInterface;
import com.yunbao.main.R;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.common.utils.AppUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by cxf on 2020/4/22.
 */

public class MainChargeViewHolder extends AbsMainHomeChildViewHolder {

    private AgentWeb mAgentWeb;
    private RelativeLayout rlLoading;
    private String htmlurl;
    private FrameLayout flRoot1;
    private FrameLayout flRoot;
    private TextView titleView;
    private TabBarBean.ListBean tabDate;

    private TextView mTvFinish;
    private ImageView btnBack;
    private TextView tvBack;
    private String currentUrl;
    private Button bt;
    private String title;
    public MainChargeViewHolder(Context context, ViewGroup parentView, LiveClassBean bean) {
        super(context, parentView, bean);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_recharge;
    }

    public  TabBarBean.ListBean getBarBean(){
        if(((MainActivity)mContext).tabdatas==null || ((MainActivity)mContext).tabdatas.size()==0){
            return null;
        }
        for (int i = 0; i < ((MainActivity)mContext).tabdatas.size(); i++) {
            if(((MainActivity)mContext).tabdatas.get(i).getId().equals("6")){
                return ((MainActivity)mContext).tabdatas.get(i);
            }
        }
        return null;
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
    @Override
    public void init() {
        EventBus.getDefault().register(this);
        rlLoading = (RelativeLayout) findViewById(com.yunbao.common.R.id.rl_loading);
        flRoot1 = (FrameLayout) findViewById(R.id.fl_root1);
        titleView = (TextView) findViewById(R.id.titleView);
        mTvFinish = (TextView) findViewById(R.id.tv_finish);
        flRoot = (FrameLayout) findViewById(R.id.fl_root);
        rlLoading.setVisibility(View.VISIBLE);
        flRoot.setBackground(mContext.getResources().getDrawable(R.drawable.main_title));
        tabDate=getBarBean();


        btnBack = (ImageView) findViewById(R.id.btn_back);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvBack.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
        tvBack.setTextColor(mContext.getResources().getColor(R.color.white));
        mTvFinish.setTextColor(mContext.getResources().getColor(R.color.white));
        titleView.setTextColor(mContext.getResources().getColor(R.color.white));
        btnBack.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.white)));
        mTvFinish.setVisibility(View.GONE);
        tvBack.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.INVISIBLE);


        if(tabDate==null || TextUtils.isEmpty(tabDate.getName())){
            return;
        }
        if(titleView!=null){
            titleView.setText(tabDate.getName());
        }

        if(TextUtils.isEmpty(tabDate.getUrl())) {
            return;
        }
        htmlurl=tabDate.getUrl();
        url=tabDate.getUrl();
        initScreen();
        initWeb(tabDate.getUrl());
        setTitleShow(true);
    }

    private void initWeb(String weburl) {
        if (TextUtils.isEmpty(weburl)) {
            return;
        }
        //如果是本地路由，且url没有处理过，先请求token和uidcan参数组装本地路径url
        if( !TextUtils.isEmpty(tabDate.getJump_type()) && "2".equals(tabDate.getJump_type()) && !weburl.startsWith("file:///android_asset")){
            getHtmlTokenUid(weburl);
            return;
        }else if (tabDate.getIs_king()!=null && tabDate.getIs_king().equals("1")&&"1".equals(tabDate.getJump_type())&&!weburl.contains("token")){
            addUidToken(weburl,false);
            return;
        }
        setWebUrl(weburl);
    }

    private void setWebUrl(String weburl) {
        Log.e("setWebUrl===：",weburl);
        if(mAgentWeb!=null){
            mAgentWeb.getWebCreator().getWebView().loadUrl(weburl);
            mAgentWeb.getWebCreator().getWebView().loadUrl( "javascript:window.location.reload( true )");
            return;
        }
        mAgentWeb = AgentWeb.with((Activity) mContext)
                .setAgentWebParent((ViewGroup) findViewById(R.id.fl_root1), new LinearLayout.LayoutParams(-1, -1))
                .closeIndicator()
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .createAgentWeb()
                .ready()
                .go(weburl);

        mAgentWeb.getWebCreator().getWebView().getSettings().setJavaScriptEnabled(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowUniversalAccessFromFileURLs(true);
        mAgentWeb.getWebCreator().getWebView().addJavascriptInterface(new AndroidInterface(null, mContext), "android");
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccessFromFileURLs(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setLoadWithOverviewMode(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);

        mAgentWeb.getWebCreator().getWebView().setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress>50){
                    rlLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });

        mAgentWeb.getWebCreator().getWebView().setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                currentUrl = url;
                return super.shouldOverrideUrlLoading(view,url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }
        });
    }


    /**
     * url添加uid&token
     * @param url
     * @param isRefresh 是否是刷新
     */
    private void addUidToken(final String url, final boolean isRefresh) {
        if(!isRefresh&&!TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.HTML_TOKEN))){
            List<TokenUtilsBean> tokenUtilsBeans=JSON.parseArray(SPUtils.getInstance().getString(Constants.HTML_TOKEN),TokenUtilsBean.class);
            String uidToken="uid="+tokenUtilsBeans.get(0).getUid()+"&token="+tokenUtilsBeans.get(0).getToken();
            setWebUrl(url+(url.contains("?")?"&":"?")+uidToken);
            return;
        }
        CommonHttpUtil.getUidToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if( isRefresh && (mAgentWeb==null||mAgentWeb.getWebCreator()==null)){
                    return;
                }
                if(info==null ||info.length==0){
                    SPUtils.getInstance().put(Constants.HTML_TOKEN,"");
                    if(mAgentWeb!=null){
                        mAgentWeb.getWebCreator().getWebView().loadUrl( url);
                        mAgentWeb.getWebCreator().getWebView().loadUrl( "javascript:window.location.reload( true )");
                    }else {
                        setWebUrl(url);
                    }
                    return;
                }
                List<TokenUtilsBean> tokens = JSON.parseArray(Arrays.toString(info), TokenUtilsBean.class);
                SPUtils.getInstance().put(Constants.HTML_TOKEN,JSON.toJSONString(tokens));
                String uidToken="uid="+tokens.get(0).getUid()+"&token="+tokens.get(0).getToken();
                String newUrl=url+(url.contains("?")?"&":"?")+uidToken;
                Log.e("-newurl---",newUrl );
                if(isRefresh){
                    if(mAgentWeb!=null){
                        mAgentWeb.getWebCreator().getWebView().loadUrl(newUrl);
                        mAgentWeb.getWebCreator().getWebView().loadUrl( "javascript:window.location.reload( true )");
                    }
                }else {
                    setWebUrl(newUrl);
                }
            }

        });
    }


    @Override
    public void loadData() {
        if(!isFirstLoadData()){
            if(TextUtils.isEmpty(htmlurl)){
                return;
            }
            if (htmlurl.startsWith("http")) {
                addUidToken(htmlurl,true);
            } else {
                getHtmlToken(htmlurl);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        if(mAgentWeb!=null){
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    /**
     * 组装本地路由路径
     */
    private void getHtmlTokenUid(final String url) {
        if(!TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.HTML_TOKEN))){
            List<TokenUtilsBean> tokenUtilsBeans=JSON.parseArray(SPUtils.getInstance().getString(Constants.HTML_TOKEN),TokenUtilsBean.class);
            goHtml(tokenUtilsBeans, url);
            return;
        }
        getHtmlToken(url);
    }
    private void getHtmlToken(final String url) {
        CommonHttpUtil.getUidToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(info==null ||info.length==0){
                    SPUtils.getInstance().put(Constants.HTML_TOKEN,"");
                    String newUrl=url.startsWith("file:///android")? url : "file:///android_asset/dist/index.html#" + url;
                    setWebUrl(newUrl);
                    return;
                }
                List<TokenUtilsBean> tokens = JSON.parseArray(Arrays.toString(info), TokenUtilsBean.class);
                SPUtils.getInstance().put(Constants.HTML_TOKEN,JSON.toJSONString(tokens));
                goHtml(tokens, url);
            }
        });
    }

    private void goHtml(List<TokenUtilsBean> tokens, String url) {
        String queryStr = tokens.get(0).getQueryStr();
        String htmlPath= url.startsWith("file:///android")?  url+"?"+queryStr : "file:///android_asset/dist/index.html#"+url+"?"+queryStr;
        setWebUrl(htmlPath);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainRefreshJsEvent(MainRefreshJsEvent e) {
        if (TextUtils.isEmpty(htmlurl)) {
            return;
        }
        if (htmlurl.startsWith("http")) {
            addUidToken(htmlurl,true);
        } else {
            getHtmlToken(htmlurl);
        }
    }
    /**
     * js回调
     *
     * @param bean
     */
    private String mBack;
    private String url;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJs(final MainJsEvent bean1) {
        final JsWebBean bean = bean1.getBean();
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
            if(mBack.equals("app")){
                btnBack.setVisibility(View.INVISIBLE);
                tvBack.setVisibility(View.INVISIBLE);
            }else {
                btnBack.setVisibility(View.VISIBLE);
                tvBack.setVisibility(View.VISIBLE);
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(  mAgentWeb.getWebCreator().getWebView().canGoBack()){
                            mAgentWeb.getWebCreator().getWebView().goBack();
                        }
                    }
                });
                tvBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(  mAgentWeb.getWebCreator().getWebView().canGoBack()){
                            mAgentWeb.getWebCreator().getWebView().goBack();
                        }
                    }
                });
            }
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
                        WebViewActivity.forwardRight(mContext, mUrl, bean.getRightTitle().getName(), false);
                    }else {//本地路由
                        if (mUrl.startsWith("http")) {//跳转是网页
                            WebViewActivity.forwardRight(mContext, mUrl, bean.getRightTitle().getName(), false);
                        }else {
                            OpenUrlUtils.getInstance()
                                    .setContext(mContext)
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
     * https://anyua.cc:8888/#/rechargeRecord?uid=1685910&token=9af530204a404548ab515aed598be43e&companyShortName=ttzb
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
     * 是否显示title
     * @param isShow
     */
    public void setTitleShow(boolean isShow){
        if(isShow){
            flRoot.setVisibility(View.VISIBLE);
        }else {
            flRoot.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 是否显示标题栏
     * @param msg
     */
    public void showTitle(String msg) {
        if(msg.equals("y")){
            setTitleShow(true);
        }else {
            setTitleShow(true);
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

    /**
     * 返回
     * @param back
     */
    public void back(String back) {
//        if(back.equals("app")){
//            ((MainActivity)mContext).finish();
//        }else {
        mAgentWeb.back();
//        }

    }
}
