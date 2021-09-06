package com.yunbao.main.views;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.jpush.Gson;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebChromeClient;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.bean.TokenUtilsBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.event.MainRefreshJsEvent;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.AndroidInterface;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.L;
import com.yunbao.live.utils.LiveStorge;
import com.yunbao.main.R;
import com.yunbao.main.activity.LoginNewActivity;
import com.yunbao.main.adapter.MainHomeFollowAdapter;
import com.yunbao.main.event.LiveEndEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.presenter.CheckLivePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;


/**
 * Created by cxf on 2020/4/22.
 * 首页接入
 */

public class MainHomeAccessViewHolder extends AbsMainHomeChildViewHolder implements OnRefreshListener {

    private AgentWeb mAgentWeb;
    private LiveClassBean bean;
    private int mClassId;
    private CommonRefreshView mRefreshView;
    private MainHomeFollowAdapter mAdapter;
    private CheckLivePresenter mCheckLivePresenter;
    private RelativeLayout rlLoading;
    private String htmlurl;
    private FrameLayout flRoot;
    private SmartRefreshLayout refreshLayout;
    public MainHomeAccessViewHolder(Context context, ViewGroup parentView, LiveClassBean bean) {
        super(context, parentView, bean);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_access;
    }

    @Override
    protected void processArguments(Object... args) {
        bean = (LiveClassBean) args[0];
    }



    @Override
    public void init() {

        EventBus.getDefault().register(this);
        rlLoading = (RelativeLayout) findViewById(com.yunbao.common.R.id.rl_loading);
        flRoot = (FrameLayout) findViewById(R.id.fl_root);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        Log.e("bean------",new Gson().toJson(bean));
        if (bean.getSlide_target().equals("2")) {
            mRefreshView.setVisibility(View.VISIBLE);
            rlLoading.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.GONE);
            initRv();
        } else {
            rlLoading.setVisibility(View.GONE);
            mRefreshView.setVisibility(View.GONE);
            htmlurl=bean.getSlide_url();
            initRefreshView();
            initWeb(bean.getSlide_url());
        }
    }

    private void initRefreshView() {
        refreshLayout.setEnableRefresh(false);//是否启用下拉刷新功能
        refreshLayout.setEnableLoadMore(false);//是否启用上拉加载功能
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setReboundDuration(100);
        refreshLayout.setOnRefreshListener(this);
    }

    private void initRv() {

        mClassId = Integer.parseInt(bean.getSlide_url());
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live_class);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveBean>() {
            @Override
            public RefreshAdapter<LiveBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeFollowAdapter(mContext);
                    mAdapter.setOnItemClickListener(new OnItemClickListener<LiveBean>() {
                        @Override
                        public void onItemClick(LiveBean bean, int position) {
                            if(CommonAppConfig.getInstance().isVisitorLogin()){
                                LoginNewActivity.forwardVisitor(mContext);
                                return;
                            }
                            watchLive(bean, position);
                        }
                    });
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getClassLive(mClassId, p, callback);
            }

            @Override
            public List<LiveBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), LiveBean.class);
            }

            @Override
            public void onRefreshSuccess(List<LiveBean> list, int listCount) {
                LiveStorge.getInstance().put(Constants.LIVE_CLASS_PREFIX + mClassId, list);
            }


            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<LiveBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
    }
    /**
     * {"checked":false,"des":"","id":64,"isAll":false,"isMore":false,
     * "is_king":"1","is_null":false,"jump_type":"1","name":"测试2",
     * "orderNo":0,"slide_show_type":7,"slide_target":"1","slide_url":"http://www.baidu.com","thumb":""}
     */
    private void initWeb(String weburl) {
        //如果是本地路由，且url没有处理过，先请求token和uidcan参数组装本地路径url
        if( !TextUtils.isEmpty(bean.getJump_type()) && "2".equals(bean.getJump_type()) && !weburl.startsWith("file:///android_asset")){
            getHtmlTokenUid(weburl);
            return;
        }else if (bean.getIs_king()!=null && bean.getIs_king().equals("1")&&"1".equals(bean.getJump_type())&&!weburl.contains("token")){
            addUidToken(weburl,false);
            return;
        }
        setWebUrl(weburl);
    }

    public void  setWebUrl(String weburl){
        Log.e("weburl===：",weburl);
        if(mAgentWeb!=null){
            mAgentWeb.getWebCreator().getWebView().loadUrl(weburl);
            mAgentWeb.getWebCreator().getWebView().loadUrl( "javascript:window.location.reload( true )");
            return;
        }
        mAgentWeb = AgentWeb.with((Activity) mContext)
                .setAgentWebParent((ViewGroup) findViewById(R.id.fl_root), new LinearLayout.LayoutParams(-1, -1))
                .closeIndicator()
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .createAgentWeb()
                .ready()
                .go(weburl);

        mAgentWeb.getWebCreator().getWebView().getSettings().setJavaScriptEnabled(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowUniversalAccessFromFileURLs(true);

        mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(mAgentWeb, mContext));
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccessFromFileURLs(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setLoadWithOverviewMode(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        mAgentWeb.getWebCreator().getWebView().setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
                    setWebUrl(url);
                    return;
                }
                List<TokenUtilsBean> tokens = JSON.parseArray(Arrays.toString(info), TokenUtilsBean.class);
                SPUtils.getInstance().put(Constants.HTML_TOKEN,JSON.toJSONString(tokens));
                String uidToken="uid="+tokens.get(0).getUid()+"&token="+tokens.get(0).getToken();
                String newUrl=url+(url.contains("?")?"&":"?")+uidToken;
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

    /**
     * 观看直播
     */
    public void watchLive(LiveBean liveBean, int position) {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        mCheckLivePresenter.watchLive(liveBean, Constants.LIVE_CLASS_PREFIX + mClassId, position);
    }

    @Override
    public void loadData() {
        if(!isFirstLoadData()){
            if (htmlurl.startsWith("http")) {
                addUidToken(htmlurl,true);
            } else {
                getHtmlToken(htmlurl);
            }
        }
    }

    @Override
    public void onResume() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
        super.onResume();
    }


    @Override
    public void onDestroy() {
        LiveStorge.getInstance().remove(Constants.LIVE_CLASS_PREFIX + mClassId);
        MainHttpUtil.cancel(MainHttpConsts.GET_CLASS_LIVE);
        if(mAgentWeb!=null){
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        release();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveEndEvent(final LiveEndEvent e) {
        List<LiveBean> list = ((List<LiveBean>) mAdapter.getList());
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getUid().equals(e.getBean().getUid())){
                mRefreshView.initData();
            }
        }
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
                }
                List<TokenUtilsBean> tokens = JSON.parseArray(Arrays.toString(info), TokenUtilsBean.class);
                SPUtils.getInstance().put(Constants.HTML_TOKEN,JSON.toJSONString(tokens));
                goHtml(tokens, url);
            }
        });
    }

    private void goHtml(List<TokenUtilsBean> tokens, String url) {
        String queryStr = tokens.get(0).getQueryStr();
        String htmlPath=url.startsWith("file:///android")? url+"?"+queryStr : "file:///android_asset/dist/index.html#"+url+"?"+queryStr;
        setWebUrl(htmlPath);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (htmlurl.startsWith("http")) {
            addUidToken(htmlurl,true);
        } else {
            getHtmlToken(htmlurl);
        }
        if(refreshLayout!=null){
            refreshLayout.finishRefresh(1000);
        }
    }
}
