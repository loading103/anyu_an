package com.yunbao.main.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.bean.ShopLeftBean;
import com.yunbao.common.bean.ShopTopBean;
import com.yunbao.common.custom.UndateEvent;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.SaveGameDateUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.LoginNewActivity;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.main.adapter.ShopLeftAdapter;
import com.yunbao.main.adapter.ShopRightAdapter;
import com.yunbao.common.bean.ShopRightBean;
import com.yunbao.main.adapter.ShopRightTypeTwoAdapter;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.common.utils.AppUtil;
import com.yunbao.main.widget.ViewShopTop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * ????????????
 */
public class MainShopViewHolder extends AbsMainViewHolder implements OnItemClickListener, ViewShopTop.TopItemClickListener, View.OnClickListener, OnRefreshListener {
    private TextView mTvTitle;
    private TextView tv_try;
    private ViewShopTop mTopView;
    private RelativeLayout flRoot;
    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    private RecyclerView recyclerView3;
    private ShopLeftAdapter adapter1;
    private ShopRightAdapter adapter2;
    private ShopRightTypeTwoAdapter adapter3;
    private RelativeLayout mNoAllView;
    private RelativeLayout mNoLeftView;
    private LinearLayout mllAllView;
    private LinearLayout mllLeftView;
    private SmartRefreshLayout refreshLayout;
    private SmartRefreshLayout refreshLayout3;
    private List<ShopTopBean> topdatas = new ArrayList<>();
    private List<ShopRightBean> rightdatas = new ArrayList<>();

    private boolean isFirstData = true;//???????????????
    private MainActivity activity;
    private View view_empet;
    private View view_empet1;
    private View net_empet;
    private List<ShopLeftBean> leftlists;
    private View botView;
    private HashMap<String, String> data = new HashMap<>();//????????????id???????????????
    private ShopLeftBean leftBean;//?????????????????????
    private  int lastposition=0;//??????????????????????????????????????????

    public MainShopViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_shop;
    }

    @Override
    public void init() {
        EventBus.getDefault().register(this);
        findViewId();
        initScreen();
        initRefreshView();
        initRecycleView();
    }

    private void initRefreshView() {
        refreshLayout.setEnableRefresh(true);//??????????????????????????????
        refreshLayout.setEnableLoadMore(false);//??????????????????????????????
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setReboundDuration(100);
        refreshLayout.setOnRefreshListener(this);

        refreshLayout3.setEnableRefresh(false);//??????????????????????????????
        refreshLayout3.setEnableLoadMore(false);//??????????????????????????????
        refreshLayout3.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout3.setReboundDuration(100);
        refreshLayout3.setOnRefreshListener(this);

    }

    private void findViewId() {
        activity = (MainActivity) this.mContext;
        mTvTitle = (TextView) findViewById(R.id.titleView);
        tv_try = (TextView) findViewById(R.id.tv_try);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout3 = (SmartRefreshLayout) findViewById(R.id.refreshLayout3);
        mTopView = (ViewShopTop) findViewById(R.id.view_top);
        flRoot = (RelativeLayout) findViewById(R.id.fl_root);
        recyclerView1 = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerview2);
        recyclerView3 = (RecyclerView) findViewById(R.id.recyclerview3);
        mllAllView = (LinearLayout) findViewById(R.id.ll_content_all);
        mllLeftView = (LinearLayout) findViewById(R.id.ll_content_left);
        mNoAllView = (RelativeLayout) findViewById(R.id.ll_no_content_all);
        mNoLeftView = (RelativeLayout) findViewById(R.id.ll_no_content_left);
        net_empet = (RelativeLayout) findViewById(R.id.no_net);
        botView=  findViewById(R.id.botView);
        setViewVisible();
        mTopView.setTopItemClickListener(this);
        tv_try.setOnClickListener(this);
        flRoot.setBackground(this.mContext.getResources().getDrawable(R.drawable.main_title));
        //??????????????????
        if (!TextUtils.isEmpty(((MainActivity) this.mContext).getTab3Name())) {
            mTvTitle.setText(((MainActivity) this.mContext).getTab3Name());
        }
        String name = SpUtil.getInstance().getStringValue(SpUtil.SHOP_NAME);
        if(!TextUtils.isEmpty(name)){
            mTvTitle.setText(name);
        }
        //?????????
        view_empet = LayoutInflater.from(mContext).inflate(R.layout.view_no_data_shop, null);
        view_empet1 = LayoutInflater.from(mContext).inflate(R.layout.view_no_data_shop, null);
    }

    /**
     * ???????????????
     */
    private void initScreen() {
        if(AppUtil.CutOutSafeHeight>0){
            LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) flRoot.getLayoutParams();
            linearParams.height = SizeUtils.dp2px(SizeUtils.px2dp(AppUtil.CutOutSafeHeight)+40);
            flRoot.setLayoutParams(linearParams);
        }
    }

    private void initRecycleView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView1.setLayoutManager(linearLayoutManager);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(gridLayoutManager);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(mContext);
        linearLayoutManager3.setOrientation(RecyclerView.VERTICAL);
        recyclerView3.setLayoutManager(linearLayoutManager3);

        adapter1 = new ShopLeftAdapter();
        adapter2 = new ShopRightAdapter();
        adapter3 = new ShopRightTypeTwoAdapter();
        recyclerView1.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);
        recyclerView3.setAdapter(adapter3);
        adapter1.setOnItemClickListener(this);
        adapter2.setOnItemClickListener(this);
        adapter2.setEmptyView(view_empet);
        adapter3.setEmptyView(view_empet1);
    }

    @Override
    public void loadData() {
        setViewVisible();
        if (isFirstData) {
            topdatas = activity.getShopList();
            rightdatas = activity.getShopRightList();
            if (topdatas == null || topdatas.size() == 0) {
                getShopData(false);
            } else {
                setStorgeDate(false);
            }
        } else {
            getShopData(true);
        }
    }

    /**
     * ?????????????????????????????????
     * ?????????????????????????????????????????????????????????
     */
    private void setStorgeDate(boolean isrefresh) {
        if (isFirstData || isrefresh) {
            leftlists = topdatas.get(0).getChildren();
            if (leftlists != null && leftlists.size() > 0) {
                mllLeftView.setVisibility(View.VISIBLE);
                mNoLeftView.setVisibility(View.INVISIBLE);
                adapter1.setList(getLeftNiData(leftlists));
            } else {
                mllLeftView.setVisibility(View.INVISIBLE);
                mNoLeftView.setVisibility(View.VISIBLE);
            }
        }
        if(lastposition==0){
            refreshLayout.setVisibility(View.GONE);
            refreshLayout3.setVisibility(View.VISIBLE);
            rightdatas=SaveGameDateUtil.getSaveGameList(rightdatas);
            adapter3.setList(rightdatas);
        }else {
            refreshLayout.setVisibility(View.VISIBLE);
            refreshLayout3.setVisibility(View.GONE);
            adapter2.setList(rightdatas);
        }

        isFirstData = false;
    }

    /**
     * ????????????item
     */
    @Override
    public void setOnItemClickListener(ShopTopBean bean, int position) {
        leftlists = bean.getChildren();
        adapter1.setList(getLeftNiData(leftlists));
        if (leftlists != null && leftlists.size() > 0) {
            mllLeftView.setVisibility(View.VISIBLE);
            mNoLeftView.setVisibility(View.INVISIBLE);
            getShopDetail(leftlists.get(0), false);
        } else {
            mllLeftView.setVisibility(View.INVISIBLE);
            mNoLeftView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ?????????????????????item
     */
    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        if (adapter instanceof ShopLeftAdapter) {
            leftItemClick(position);
        } else {
            if(!ClickUtil.canClick()){
                return;
            }
            if(CommonAppConfig.getInstance().isVisitorLogin()){
                LoginNewActivity.forwardVisitor(mContext);
                return;
            }
            rightItemClick(position);
        }
    }

    /**
     * ?????????????????????
     */
    private List<ShopLeftBean> getLeftNiData(List<ShopLeftBean> date
    ) {
        if (date == null) {
            return null;
        }
        if (leftBean==null) {     //????????????????????????+
            for (int i = 0; i < date.size(); i++) {
                if (i == 0) {
                    date.get(0).setChoosed(true);
                    leftBean = date.get(i);
                } else {
                    date.get(i).setChoosed(false);
                }
            }
        } else {
            boolean havedata = false;
            for (int i = 0; i < date.size(); i++) {
                if (date.get(i).getId().equals(leftBean.getId())) {
                    date.get(i).setChoosed(true);
                    leftBean = date.get(i);
                    havedata = true;
                } else {
                    date.get(i).setChoosed(false);
                }
            }
            if (!havedata) { //?????????????????????????????????
                date.get(0).setChoosed(true);
                leftBean = date.get(0);
            }
        }
        return date;
    }

    /**
     * ?????????????????????
     */
    private List<ShopTopBean> getTopMnData(List<ShopTopBean> dates) {
        if (dates == null) {
            return null;
        }
        for (int i = 0; i < dates.size(); i++) {
            if (i == 0) {
                dates.get(i).setChoosed(true);
            } else {
                dates.get(i).setChoosed(false);
            }
        }
        return dates;
    }

    /**
     * ??????item??????
     */
    private void leftItemClick(int position) {
        lastposition=position;
        for (int i = 0; i < leftlists.size(); i++) {
            if (leftlists.get(i).isChoosed()) {
                if (i == position) {
                    return;
                }
            }
        }
        for (int i = 0; i < leftlists.size(); i++) {
            if (i == position) {
                leftlists.get(i).setChoosed(true);
            } else {
                leftlists.get(i).setChoosed(false);
            }
        }
        adapter1.notifyDataSetChanged();
        getShopDetail(leftlists.get(position), false);
    }

    /**
     * ??????item??????
     */
    private void rightItemClick(int position) {
        ShopRightBean bean = rightdatas.get(position);
        if (bean != null && !TextUtils.isEmpty(bean.getJump_url())) {
            SaveGameDateUtil.saveClickGame(bean);
            OpenUrlUtils.getInstance()
                    .setContext(mContext)
                    .setIs_king(bean.getIs_king())
                    .setJump_type(bean.getJump_type())
                    .setShopUrl(true)
                    .setTitle(bean.getName())
                    .setType(Integer.parseInt(bean.getShow_style()))
                    .setSlide_show_type_button(bean.getSlide_show_type_button())
                    .go(bean.getJump_url());
        }
    }

    /**
     * ?????????????????????
     */
    private void getShopData(final boolean isrefresh) {
        MainHttpUtil.getShopData(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    String infordata = Arrays.toString(info);
                    topdatas = JSON.parseArray(infordata, ShopTopBean.class);
                    if (topdatas != null && topdatas.size() > 0) {
                        mllAllView.setVisibility(View.VISIBLE);
                        mNoAllView.setVisibility(View.INVISIBLE);
                        if (isrefresh) {
                            setStorgeDate(true);
                            getShopDetail(leftBean, isrefresh);
                        } else {
                            if (topdatas == null || topdatas.size() == 0 || topdatas.get(0).getChildren() == null || topdatas.get(0).getChildren().size() == 0) {
                                return;
                            }
                            setStorgeDate(true);
                            getShopDetail( topdatas.get(0).getChildren().get(0), isrefresh);
                        }
                    } else {
                        mllAllView.setVisibility(View.INVISIBLE);
                        mNoAllView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                if (isrefresh || refreshLayout != null) {
                    refreshLayout.finishRefresh(500, false);//??????false??????????????????
                }
            }
        });
    }

    /**
     * ??????????????????????????????
     */
    private void getShopDetail(ShopLeftBean bean, final boolean isrefresh) {
        leftBean = bean;
        if (isrefresh || refreshLayout != null) {
            refreshLayout.finishRefresh(500, true);//??????false??????????????????
        }
        recyclerView2.setVisibility(View.VISIBLE);
        net_empet.setVisibility(View.GONE);
        rightdatas =leftBean.getChildren();

        if(lastposition==0){
            refreshLayout.setVisibility(View.GONE);
            refreshLayout3.setVisibility(View.VISIBLE);
            rightdatas=SaveGameDateUtil.getSaveGameList(rightdatas);
            adapter3.setList(rightdatas);
        }else {
            refreshLayout.setVisibility(View.VISIBLE);
            refreshLayout3.setVisibility(View.GONE);
            adapter2.setList(rightdatas);
        }

    }

    /**
     * ???????????????????????????????????????
     */
    @Override
    public void onClick(View view) {
        if (leftBean!=null) {
            getShopDetail(leftBean, false);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getShopData(true);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishEvent(UndateEvent e) {
        getShopDetail(leftBean, false);
    }

    public void setTitleName(String name) {
        if(mTvTitle!=null){
            mTvTitle.setText(name);
        }
    }
    public  void  setViewVisible(){
        if(botView==null){
            return;
        }
        if(((MainActivity)mContext).getBottomVisible()){
            botView.setVisibility(View.VISIBLE);
        }else {
            botView.setVisibility(View.GONE);
        }
    }
}
