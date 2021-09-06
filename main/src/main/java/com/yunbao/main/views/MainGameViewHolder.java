package com.yunbao.main.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;
import com.yunbao.common.activity.SmallProgramTitleActivity;
import com.yunbao.common.bean.JsWebBean;
import com.yunbao.common.interfaces.AndroidInterface;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.main.event.MainJsEvent;
import com.yunbao.main.event.RegSuccessEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.jmessage.support.qiniu.android.utils.Json;

/**
 * Created by cxf on 2020/04/25.
 * 游戏
 */

public class MainGameViewHolder extends AbsMainViewHolder {


    private AgentWeb mAgentWeb;
    private String url;
    private RelativeLayout rlLoading;
    private LinearLayout llRoot;
    private View v1;
    private FrameLayout flRoot;
    private TextView titleView;
    private TextView mTvFinish;
    private ImageView btnBack;
    private TextView tvBack;
    private Uri currentUrl;
    private Button bt;
    private String title;

    public MainGameViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public MainGameViewHolder(Context context, ViewGroup parentView,Object... args) {
        super(context, parentView,args);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_game;
    }


    @Override
    protected void processArguments(Object... args) {
        url=(String)args[0];
        title=(String)args[1];
    }

    @Override
    public void init() {
        EventBus.getDefault().register(this);
        llRoot = (LinearLayout) findViewById(R.id.ll_root);
        flRoot = (FrameLayout) findViewById(R.id.fl_root);
        mTvFinish = (TextView) findViewById(R.id.tv_finish);
        titleView = (TextView) findViewById(R.id.titleView);
        bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String js="{\"title\":\"直播测试一级页面\",\"rightTitle\":{\"name\" :\"测试二级页面\",\"url\":\"https://chenhui-file.tuofub.com/test/test_new.html\"},\"showNai\":\"y\",\"back\":\"h5\"}";
                EventBus.getDefault().post(new MainJsEvent(JSON.parseObject(js,JsWebBean.class)));
            }
        });
        v1 = (View) findViewById(R.id.v1);
        flRoot.setBackground(mContext.getResources().getDrawable(R.drawable.main_title));
        rlLoading = (RelativeLayout) findViewById(R.id.rl_loading);
        rlLoading.setVisibility(View.VISIBLE);

        btnBack = (ImageView) findViewById(R.id.btn_back);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);
        tvBack.setTextColor(mContext.getResources().getColor(R.color.white));
        mTvFinish.setTextColor(mContext.getResources().getColor(R.color.white));
        titleView.setTextColor(mContext.getResources().getColor(R.color.white));
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)mContext).finish();
            }
        });
        btnBack.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.white)));
        mTvFinish.setVisibility(View.GONE);
        tvBack.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.INVISIBLE);

        mAgentWeb = AgentWeb.with((MainActivity)mContext)
                .setAgentWebParent((ViewGroup)findViewById(R.id.fl_root2),
                        new LinearLayout.LayoutParams(-1, -1))
                .closeIndicator()
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .createAgentWeb()
                .ready()
                .go(url);

        mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(mAgentWeb, mContext));
        mAgentWeb.getWebCreator().getWebView().getSettings().setJavaScriptEnabled(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccessFromFileURLs(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setLoadWithOverviewMode(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        mAgentWeb.getWebCreator().getWebView().setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    rlLoading.setVisibility(View.GONE);
                } else {
                    rlLoading.setVisibility(View.VISIBLE);
                }
            }
        });
        mAgentWeb.getWebCreator().getWebView().setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                currentUrl=request.getUrl();
                return super.shouldOverrideUrlLoading(view, request);

            }
        });
        setTitleShow(true);
        setTitleName(title);
    }

    /**
     * 是否显示title
     * @param isShow
     */
    public void setTitleShow(boolean isShow){
        if(isShow){
            flRoot.setVisibility(View.VISIBLE);
            v1.setVisibility(View.GONE);
        }else {
            flRoot.setVisibility(View.GONE);
            v1.setVisibility(View.VISIBLE);
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
        if(back.equals("app")){
            ((MainActivity)mContext).finish();
        }else {
            mAgentWeb.back();
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
            setTitleShow(false);
        }
    }


    /**
     * js回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainJsEvent(final MainJsEvent e) {
        final JsWebBean bean = e.getBean();
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
            back(bean.getBack());
        }
        if(bean.getRightTitle()!=null&&bean.getRightTitle().getName()!=null){
            mTvFinish.setVisibility(View.VISIBLE);
            mTvFinish.setText(bean.getRightTitle().getName());
            mTvFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mUrl;
                    if(bean.getRightTitle().getUrl().startsWith("http")){
                        mUrl=bean.getRightTitle().getUrl();
                    }else {
                        mUrl=currentUrl.getHost()+bean.getRightTitle().getUrl();
                    }
                    new SmallProgramTitleActivity().toActivity(mContext,
                            mUrl,bean.getRightTitle().getName());
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
