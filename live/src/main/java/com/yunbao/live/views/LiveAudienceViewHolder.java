package com.yunbao.live.views;

import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.bean.TokenUtilsBean;
import com.yunbao.common.event.DialogShowEvent;
import com.yunbao.common.event.LiveShowRedEvent;
import com.yunbao.common.fragment.ButtomDialogFragment;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.utils.IMImageLoadUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveActivity;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.live.adapter.LiveListAdapter;
import com.yunbao.live.bean.GoodsResultBean;
import com.yunbao.live.bean.LiveMoreBean;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.live.bean.PrivateUserBean;
import com.yunbao.live.bean.TimeBean;
import com.yunbao.live.custom.ChildPresenter;
import com.yunbao.live.custom.MyCountDownTimer;
import com.yunbao.live.dialog.LiveNewMoreDialog;
import com.yunbao.live.dialog.PrivateLiveDialog;
import com.yunbao.common.event.DialogBottomEvent;
import com.yunbao.live.event.LiveOpenDialogEvent;
import com.yunbao.live.event.LivePrivateEvent;
import com.yunbao.live.event.LiveShowBottomEvent;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.common.interfaces.DissmissDialogListener;
import com.yunbao.live.socket.SocketChatUtil;
import com.yunbao.live.utils.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import pl.droidsonroids.gif.GifImageView;

import static com.yunbao.common.utils.ClickUtil.isFastClick;

/**
 * Created by cxf on 2018/10/9.
 * 观众直播间逻辑
 */

public class LiveAudienceViewHolder extends AbsLiveViewHolder implements LiveListAdapter.onItemClick {

    private String mLiveUid;
    private String mStream;
    private ChildPresenter recyclerView;
    private LiveListAdapter adapter;
    private ImageView ivList;
    private ImageView btnMore;
    private ImageView ivGame;
    private LiveReadyBean.GoodsBean goodsBean;
    public LinearLayout llRoot;
    private ButtomDialogFragment mDialog;
    private List<LiveMoreBean> moreBean;
    private LiveNewMoreDialog dialog;
    private String moreUrl;
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    update();
                    break;
            }
            super.handleMessage(msg);
        }

        void update() {
            //刷新内容
//            getMoreData();
//            isTimeRefresh=true;
        }
    };

    private boolean isTimeRefresh;
    private Timer timer;
    private GifImageView btnGift;
    private TextView tvTime;
    private MyCountDownTimer mTimer;
    private String mGoodsNo;
    private int postTimes = 0;

    private boolean isFirst = true;
    private CountDownTimer timer3;
    private OpenUrlUtils oUtils;
    private RelativeLayout rlMore;
    private boolean isFirstList = true;
    private FrameLayout flPrivate;
    private int mPrivateState;//私密直播状态 0-未开启（没有用户申请） 1-报名中 2-主播处理中 3 正在私密直播
    private String private_limit;
    private String private_timeout;
    private float contribution2;
    private TextView tvPrivateTime;
    private MyCountDownTimer mTimerPrivate;
    private MyCountDownTimer mTimerPrivate2;
    private ImageView iv_animation;
    private String private_switch;
    private String isvideo;
    private CountDownTimer timer4;
    private OpenUrlUtils oUtils2;
    private List<LiveClassBean> moreH5List;
    private String live_tag;
    private View view_red;

    public void setFirst(boolean first) {
        isFirst = first;
        if (isFirst) {
            if (timer3 != null) {
                timer3.cancel();
                timer3 = null;
            }
            OkGo.getInstance().cancelTag("result");
        }
    }

    public LiveAudienceViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_audience_new;
    }

    @Override
    public void init() {
        super.init();
        EventBus.getDefault().register(this);
//        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);
        findViewById(R.id.btn_msg_1).setOnClickListener(this);
//        findViewById(R.id.btn_red_pack).setOnClickListener(this);
        btnGift = (GifImageView) findViewById(R.id.btn_gift);
        btnGift.setOnClickListener(this);
        findViewById(R.id.iv_list).setOnClickListener(this);
        findViewById(R.id.btn_more).setOnClickListener(this);
        findViewById(R.id.iv_game).setOnClickListener(this);
        findViewById(R.id.fl_private).setOnClickListener(this);
        view_red =findViewById(R.id.view_red);
        recyclerView = (ChildPresenter) findViewById(R.id.recyclerView);
        btnMore = (ImageView) findViewById(R.id.btn_more);
        rlMore = (RelativeLayout) findViewById(R.id.rl_more);
        ivList = (ImageView) findViewById(R.id.iv_list);
        llRoot = (LinearLayout) findViewById(R.id.ll_root);
        tvTime = (TextView) findViewById(R.id.tv_time);
        ivGame = (ImageView) findViewById(R.id.iv_game);
        flPrivate = (FrameLayout) findViewById(R.id.fl_private);
        tvPrivateTime = (TextView) findViewById(R.id.tv_private_time);
        iv_animation= (ImageView) findViewById(R.id.iv_animation);
        getRightWebData();
        getMoreData(true);

        KeyboardUtils.registerSoftInputChangedListener(((LiveAudienceActivity) mContext), new KeyboardUtils.OnSoftInputChangedListener() {
            @Override
            public void onSoftInputChanged(int height) {
                EventBus.getDefault().post(new DialogBottomEvent(height));
            }
        });
    }

    /**
     * 右侧倒计时
     */
    private void getTime() {
        String u = CommonAppConfig.getInstance().getConfig().getGoods_server();
        String i = CommonAppConfig.getInstance().getConfig().getKing_id();
        OkGo.<String>post(CommonAppConfig.getInstance().getConfig().getGoods_server())
                .tag("time")
                .params("playGroupId", mGoodsNo)
                .params("companyShortName", CommonAppConfig.getInstance().getConfig().getKing_id())
//                .params("companyShortName","mm")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        TimeBean bean = JSON.parseObject(response.body(), TimeBean.class);
                        if (bean.getResult() == 1) {
                            int time = bean.getLeftTime();
                            if (time <= 1) {
                                startTime();
                                return;
                            }
                            postTimes = 0;
                            if (time > 3600) {
                                tvTime.setText(TimeUtils.convertSecToTimeString(bean.getLeftTime()));
                            } else {
                                tvTime.setText(TimeUtils.convertSecToTimeString2(bean.getLeftTime()));
                            }
                            countDown(time);

                            if (!isFirst) {
                                timer3 = new CountDownTimer(5000 + 30, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        getResult();
                                    }
                                }.start();
                            } else {
                                isFirst = false;
                            }

                        } else {
                            startTime();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        startTime();
                    }
                });
    }

    private void startTime() {
        if(mContext!=null&&((LiveAudienceActivity)mContext).isDestroyed()){
            return;
        }

        postTimes++;
        if (postTimes > 5) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getTime();
                }
            }, 2000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getTime();
                }
            }, 1000);
        }
    }


    /**
     * 获取结果
     */
    private void getResult() {
        OkGo.<String>post(CommonAppConfig.getInstance().getConfig().getGoods_result())
                .tag("result")
                .params("size", 1)
                .params("playGroupId", mGoodsNo)
//                .params("playGroupId", 15)-
                .params("companyShortName", CommonAppConfig.getInstance().getConfig().getKing_id())
//                .params("companyShortName","mm")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        GoodsResultBean bean = JSON.parseObject(response.body(), GoodsResultBean.class);
                        if (bean.getResult() == 1) {
                            if(bean.getSscHistoryList().size()==0){
                                return;
                            }

                            if(TextUtils.isEmpty(bean.getSscHistoryList().get(0).getOpenCode())){
                                timer4 = new CountDownTimer(5000+30, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        getResult();
                                    }
                                }.start();
                                return;
                            }

                            ((LiveAudienceActivity) mContext).showResult(bean.getSscHistoryList().get(0),goodsBean);
                        }
                    }
                });
    }

    /**
     * 倒计时
     */
    private void countDown(long time) {
        if (mTimer == null) {
            mTimer = new MyCountDownTimer(time * 1000 + 30, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    BigDecimal b = new BigDecimal(TimeUtils.div(millisUntilFinished, 1000, 2)).setScale(0, BigDecimal.ROUND_HALF_UP);
                    if (b.longValue() > 3600) {
                        tvTime.setText(TimeUtils.convertSecToTimeString(b.longValue()));
                    } else {
                        tvTime.setText(TimeUtils.convertSecToTimeString2(b.longValue()));
                    }
                }

                @Override
                public void onFinish() {
                    try {
                        mTimer = null;
                        String t = TimeUtils.convertSecToTimeString2(0);
                        tvTime.setText(t.equals("00:00") ? "封盘中" : t);
                        getTime();
                    } catch (Exception e) {
                        return;
                    }

                }
            }.start();
        }
    }

    private void getRightWebData() {
        LiveHttpUtil.getAdvert(20, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    L.e("WOLF", Arrays.toString(info));
                    List<LiveClassBean> list = JSON.parseArray(Arrays.toString(info), LiveClassBean.class);
                    if (list.size() == 0) {
//                        ViewGroup.LayoutParams lp = ivList.getLayoutParams();
//                        lp.width = 0;
//                        lp.height = 0;
//                        ivList.setLayoutParams(lp);
                    } else {
                        setRecycleView(list);
                    }
                }
            }
        });
    }

    public void setLiveInfo(String liveUid, String stream) {
        mLiveUid = liveUid;
        mStream = stream;
    }

    public void setGoods(LiveReadyBean.GoodsBean goodsBean) {
        this.goodsBean = goodsBean;
        mGoodsNo = goodsBean.getGoods_no();
        if (goodsBean != null && goodsBean.isFlag()) {
            CommonAppConfig.getInstance().setShowGoods(true);
            btnMore.setVisibility(View.VISIBLE);
            rlMore.setVisibility(View.VISIBLE);
            ImgLoader.displayCircle(CommonAppContext.sInstance, goodsBean.getThumb(), btnMore);
            getTime();
        } else {
            CommonAppConfig.getInstance().setShowGoods(false);
            btnMore.setVisibility(View.INVISIBLE);
            rlMore.setVisibility(View.INVISIBLE);
        }
//        initMoreDialog();
    }

    public void setGames(List<LiveReadyBean.GoodsBean> beans) {
        if (beans.size() == 0) {
            ivGame.setVisibility(View.INVISIBLE);
        } else {
            ivGame.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.btn_close) {
            close();

        } else if (i == R.id.btn_share) {
            openShareWindow();

        } else if (i == R.id.btn_red_pack) {
            ((LiveActivity) mContext).openRedPackSendWindow();

        } else if (i == R.id.btn_gift) {
            openGiftWindow();

        } else if (i == R.id.iv_game) {
            openShopWindow();

        } else if (i == R.id.btn_more) {
//            openMorewebWindow();
            if (goodsBean != null) {
                oUtils2 = OpenUrlUtils.getInstance().setContext(mContext)
                        .setLoadTransparent(true)
                        .setCannotCancel(false)
                        .setJump_type(goodsBean.getJump_type())
                        .setIs_king(goodsBean.getIs_king())
                        .setSlide_show_type_button(goodsBean.getSlide_show_type_button())
                        .setListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                ((LiveAudienceActivity) mContext).setChatShow(true);
                                llRoot.setVisibility(View.VISIBLE);
                            }
                        })
                        .setOndissDialogListener(new DissmissDialogListener() {
                            @Override
                            public void onDissmissListener() {
                                ((LiveAudienceActivity) mContext).setChatShow(true);
                                llRoot.setVisibility(View.VISIBLE);
                            }
                        })
                        .setType(Integer.parseInt(goodsBean.getShow_type()));
                oUtils2.go(goodsBean.getJump_url());
//                llRoot.setVisibility(View.INVISIBLE);
//                ((LiveAudienceActivity) mContext).setChatShow(false);


//                mDialog=oUtils.getMoreDialog();
            }
        } else if (i == R.id.iv_list) {
            ivList.setEnabled(false);
            getMoreData(false);
        }else if(i==R.id.btn_msg_1){ //用户发送私信
            view_red.setVisibility(View.INVISIBLE);
            ((LiveActivity)mContext).openChatRoomWindow(null,false);
        } else if (i == R.id.fl_private) {//私密互动
            if(isvideo.equals("2")){
                ToastUtil.show("当前主播暂未开启私密直播");
                return;
            }
            try {
                if(mPrivateState==0){
                    doApplyPri();
                }else if(mPrivateState==1){
                    doApplyPri();
                }else if(mPrivateState==2){
                    PrivateCommonDialog(R.mipmap.icon_private_dialog3,"申请已经结束了","期待下一场的表演吧！");
//                ToastUtil.show("抱歉，私密互动申请已结束，暂时无法申请");
                }
            }catch (Exception e){
                return;
            }
        }
    }

    /**
     * 私密直播dialog
     * @param res
     * @param title
     * @param content
     */
    private PrivateLiveDialog PrivateCommonDialog(@DrawableRes int res, String title, String content) {
        PrivateLiveDialog privateDialog = new PrivateLiveDialog();
        privateDialog.setData(res,title,content);
        privateDialog.show(((LiveAudienceActivity)mContext).getSupportFragmentManager(), "PrivateLiveDialog");
        return privateDialog;
    }

    /**
     * 用户申请私密直播
     *
     */
    private void doApplyPri() {
        String key=CommonAppConfig.getInstance().getUid()+mStream;
        //保存2位小数
        double sendTotle = (double) Math.round(SPUtils.getInstance().getFloat(key, 0.0f) * 100) / 100;
        double limitTotle = (double) Math.round(Double.parseDouble(private_limit) * 100) / 100;
        if(sendTotle<limitTotle){
            String format = String.format("%.2f", limitTotle -sendTotle);
            PrivateCommonDialog(R.mipmap.icon_private_dialog2,"只差"+format+"元的距离","快给主播一场礼物风暴吧！");
            return;
        }
        String nickName=CommonAppConfig.getInstance().getUserBean().getUserNiceName();
        String avatar=CommonAppConfig.getInstance().getUserBean().getAvatar();
        LiveHttpUtil.doApplyPri(nickName, avatar,mStream,new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    int is_one = obj.getIntValue("is_one");
                    if(is_one==1){//第一个申请，发送socket
                        SocketChatUtil.sendPrivateLiveMsg(((LiveAudienceActivity)mContext).getSocketClient(),0);
                    }

                    if(obj.getIntValue("flag")==1){//该用户第一次申请
                        ToastUtil.show("您已申请成功，精彩即将呈现！");
                    }else {
                        ToastUtil.show("您已提过申请，别心急，精彩即将呈现！");
                    }
                }
            }
        });
    }

    /**
     * 底部右边更多（url）
     */
    private void getMoreData(final boolean isfirst) {
//        LiveHttpUtil.getLocation("", new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//                if (code == 0) {
//                    moreBean = JSON.parseArray(Arrays.toString(info), LiveMoreBean.class);
//                    if (moreBean.get(0).getSlide().size() == 0 && moreBean.get(0).getLive_avatar().size() == 0) {
//                        ivList.setVisibility(View.INVISIBLE);
//                    } else {
//                        ivList.setVisibility(View.VISIBLE);
//                        if (!isFirstList) {
////                            openMoreWindow(moreBean.get(0));
//                            openMoreUrlWindow(moreBean.get(0));
//                        } else {
//                            isFirstList = false;
//                        }
//                    }
//                }
//            }
//        });

        if(moreH5List!=null&&moreH5List.size()!=0){
            ivList.setEnabled(true);
            openMoreUrlWindow(moreH5List.get(0),isfirst);
            return;
        }
        LiveHttpUtil.getAdvert(21, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ivList.setEnabled(true);
                if (code == 0) {
                    moreH5List = JSON.parseArray(Arrays.toString(info), LiveClassBean.class);
                    if(moreH5List==null || moreH5List.size()==0){
                        return;
                    }
                    openMoreUrlWindow(moreH5List.get(0),isfirst);
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                ivList.setEnabled(true);

            }

            @Override
            public void onError() {
                super.onError();
                ivList.setEnabled(true);
            }
        });
    }

    /**
     * 打开更多窗口
     *
     */
//    public void openMoreWindow(LiveMoreBean bean) {
//        if (dialog != null && dialog.getDialog() != null && dialog.getDialog().isShowing()) {
//            return;
//        }
//
//
//        isTimeRefresh = false;
//        dialog = new LiveMoreDialog(this);
//        dialog.setData(bean);
//        dialog.show(((LiveAudienceActivity) mContext).getSupportFragmentManager(), "LiveMoreDialog");
//        dialog.setOnDissmissDialogListener(new DissmissDialogListener() {
//            @Override
//            public void onDissmissListener() {
//                ((LiveAudienceActivity) mContext).setChatShow(true);
//                ((LiveAudienceActivity) mContext).setRoomViewVisible(View.VISIBLE);
//                llRoot.setVisibility(View.VISIBLE);
//            }
//        });
//        ((LiveAudienceActivity) mContext).setChatShow(false);
//
//        llRoot.setVisibility(View.INVISIBLE);
//        ((LiveAudienceActivity) mContext).setRoomViewVisible(View.INVISIBLE);
//    }

    /**
     * 判断是路由还是http
     */
    public void openMoreUrlWindow(LiveClassBean data,boolean isfirdt) {
        String slide_url = data.getSlide_url();
        if (!TextUtils.isEmpty(slide_url)) {
            ivList.setVisibility(View.VISIBLE);
        } else {
            ivList.setVisibility(View.INVISIBLE);
        }
        if(isfirdt){
            return;
        }
        if( !TextUtils.isEmpty(data.getJump_type()) && "2".equals(data.getJump_type()) && !slide_url.startsWith("file:///android_asset")){
            getHtmlTokenUid(slide_url,data.getIs_king());
            return;
        }
        Log.e("goHtml slide_url==",slide_url);
        startOpenUrl(slide_url,slide_url);
    }

    /**
     * 跳转更多
     */
    private void startOpenUrl(String url,String htmlurl) {
        LiveNewMoreDialog   dialog = new LiveNewMoreDialog(this,url,htmlurl,mGoodsNo);
        dialog.show(((LiveAudienceActivity) mContext).getSupportFragmentManager(), "LiveNewMoreDialog");
        dialog.setOnDissmissDialogListener(new DissmissDialogListener() {
            @Override
            public void onDissmissListener() {
                ((LiveAudienceActivity) mContext).setChatShow(true);
                ((LiveAudienceActivity) mContext).setRoomViewVisible(View.VISIBLE);
                llRoot.setVisibility(View.VISIBLE);
            }
        });
//        ((LiveAudienceActivity) mContext).setChatShow(false);
//        llRoot.setVisibility(View.INVISIBLE);
//        ((LiveAudienceActivity) mContext).setRoomViewVisible(View.INVISIBLE);
    }

    /**
     *
     * 组装本地路由路径
     */
    private void getHtmlTokenUid(final String url,String is_king) {
        //如果不是1，不需要token,就拼接路由
        if(is_king!=null && !is_king.equals("1")){
            String htmlPath="";
            if(TextUtils.isEmpty(url)) {
                htmlPath = "file:///android_asset/dist/index.html";
            }else {
                htmlPath="file:///android_asset/dist/index.html#"+url;
            }
            Log.e("goHtml--444",htmlPath);
            startOpenUrl(htmlPath,url);
            return;
        }
        if(!TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.HTML_TOKEN))){
            List<TokenUtilsBean> tokenUtilsBeans=JSON.parseArray(SPUtils.getInstance().getString(Constants.HTML_TOKEN),TokenUtilsBean.class);
            goHtml(tokenUtilsBeans, url);
            return;
        }

        CommonHttpUtil.getUidToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(info==null ||info.length==0){
                    Log.e("goHtml--222","htmlPath");
                    SPUtils.getInstance().put(Constants.HTML_TOKEN,"");
                    startOpenUrl("file:///android_asset/dist/index.html#"+url,url);
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
        String htmlPath ="";
        if(TextUtils.isEmpty(url)) {
            htmlPath = "file:///android_asset/dist/index.html#/?"+queryStr;
        }else {
            if(url.contains("?")){
                htmlPath="file:///android_asset/dist/index.html#"+url+"&"+queryStr;
            }else {
                htmlPath="file:///android_asset/dist/index.html#"+url+"?"+queryStr;
            }
        }
        Log.e("goHtml--1111",htmlPath);
        startOpenUrl(htmlPath,url);
    }

    /**
     * 退出直播间
     */
    public void close() {
        ((LiveAudienceActivity) mContext).onBackPressed();
    }


    /**
     * 打开礼物窗口
     */
    private void openGiftWindow() {
        ((LiveAudienceActivity) mContext).openGiftWindow();
    }

    /**
     * 打开商品窗口
     */
    private void openShopWindow() {
        ((LiveAudienceActivity) mContext).openGameWindow();
    }

    /**
     * 打开更多web窗口
     */
    private void openMorewebWindow() {
        ((LiveAudienceActivity) mContext).openMorewebWindow();
    }

    /**
     * 打开分享窗口
     */
    private void openShareWindow() {
        ((LiveActivity) mContext).openShareWindow();
    }


    /**
     * 私密直播
     *
     * @param contribution2
     * @param private_state
     * @param timeout
     */
    public void setPrivateLive(float contribution2, int private_state, int timeout) {
        private_switch = CommonAppConfig.getInstance().getConfig().getPrivate_switch();
        private_limit = CommonAppConfig.getInstance().getConfig().getPrivate_limit();
        private_timeout = CommonAppConfig.getInstance().getConfig().getPrivate_timeout();
        this.contribution2=contribution2;

        if (private_switch.equals("1")&&!isvideo.equals("1")) {
            flPrivate.setVisibility(View.VISIBLE);
        } else {
            flPrivate.setVisibility(View.GONE);
        }
        L.e("私密直播："+"进入直播间");
        switch (private_state) {
            case 0://未开启（没有用户申请）
                L.e("私密直播："+"未开启");
                choosePrivateStatus(0);
                break;
            case 1://报名中
                L.e("私密直播："+"报名中："+timeout);
                startPrivateTime(timeout);
                choosePrivateStatus(1);
                break;
            case 2://主播处理中
                L.e("私密直播："+"主播处理中："+timeout);
                startPrivateTime2(timeout);
                choosePrivateStatus(2);
                break;
            case 3://正在私密直播
                L.e("私密直播："+"正在私密直播");
//                PrivateLiveDialog privateLiveDialog=PrivateCommonDialog(R.mipmap.icon_private_dialog5,
//                        "抱歉，当前主播已开启私密直播", "暂时无法进入！");
//                privateLiveDialog.setOnDismissDialogListener(new DissmissDialogListener() {
//                    @Override
//                    public void onDissmissListener() {
//                        ((LiveAudienceActivity)mContext).exitLiveRoom();
//                    }
//                });
                choosePrivateStatus(3);
                break;
        }
    }


    /**
     * 是否显示底部
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveShowBottomEvent(LiveShowBottomEvent e) {
        L.e("收到消息：");
        if (e.isShow()) {
            llRoot.setVisibility(View.VISIBLE);
            ((LiveAudienceActivity) mContext).setChatShow(true);
        } else {
//            llRoot.setVisibility(View.INVISIBLE);
//            ((LiveAudienceActivity) mContext).setChatShow(false);
        }
    }
    /**
     * 是否显示底部
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveShowEvent(LiveOpenDialogEvent e) {
        L.e("收到消息：");
        OpenUrlUtils oUtils = OpenUrlUtils.getInstance()
                .setContext(mContext)
                .setLoadTransparent(true)
                .setCannotCancel(false)
                .setJump_type(e.getBean().getJump_type())
                .setIs_king(e.getBean().getIs_king())
                .setSlide_show_type_button(e.getBean().getSlide_show_type_button())
                .setListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        ((LiveAudienceActivity) mContext).setChatShow(true);
                        llRoot.setVisibility(View.VISIBLE);
                    }
                })
                .setOndissDialogListener(new DissmissDialogListener() {
                    @Override
                    public void onDissmissListener() {
                        ((LiveAudienceActivity) mContext).setChatShow(true);
                        llRoot.setVisibility(View.VISIBLE);
                    }
                })
                .setType(e.getBean().getSlide_show_type());
        oUtils.go(e.getBean().getSlide_url());
//
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                llRoot.setVisibility(View.INVISIBLE);
//                ((LiveAudienceActivity) mContext).setChatShow(false);
            }
        }, 150);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDialog != null && mDialog.getDialog() != null) {
            mDialog.getDialog().dismiss();
        }
        EventBus.getDefault().unregister(this);

        if (timer != null) {// 停止timer
            timer.cancel();
            timer = null;
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (timer4 != null) {
            timer4.cancel();
            timer4 = null;
        }
        OkGo.getInstance().cancelTag("time");

        String key=CommonAppConfig.getInstance().getUid()+mStream;
        if(SPUtils.getInstance().contains(key)){
            SPUtils.getInstance().remove(key);
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onLiveEndEvent(final LiveEndEvent e) {
//        if (dialog != null && dialog.getDialog() != null && dialog.getDialog().isShowing()) {
//            List<LiveBean> list = ((List<LiveBean>) dialog.getData().getLive_avatar());
//            for (int i = 0; i < list.size(); i++) {
//                if (list.get(i).getUid().equals(e.getBean().getUid())) {
//                    dialog.getAdapterFooter().remove(i);
//                }
//            }
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDialogShowEvent(final DialogShowEvent e) {
        llRoot.setVisibility(View.VISIBLE);
        ((LiveAudienceActivity) mContext).setChatShow(true);
    }

    /**
     * 是不是显示私信小红点
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDialogShowEvent(final LiveShowRedEvent e) {
       if(view_red!=null){
           view_red.setVisibility(e.isIsshow()?View.VISIBLE:View.INVISIBLE);
       }
    }
    private void setRecycleView(List<LiveClassBean> list) {
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
        if (list.size() < 3) {
            linearParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        } else {
            linearParams.height = SizeUtils.dp2px(235);
        }
        recyclerView.setLayoutParams(linearParams);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
//        ItemDecoration2 decoration1 = new ItemDecoration2(mContext, 0x00000000, 10, 10);

        adapter = new LiveListAdapter(list);
//        recyclerView.addItemDecoration(decoration1);
        recyclerView.setAdapter(adapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        adapter.setItemClickListener(this);
    }

    @Override
    public void onItemClickListener(LiveClassBean bean) {
        if (bean == null) {
            return;
        }
        if(isFastClick()){
            return;
        }
        String url = bean.getSlide_url();
        if (!TextUtils.isEmpty(url)) {
            if (bean.getSlide_show_type() == 4 && bean.getSlide_target().equals("1")) {

                OpenUrlUtils oUtils = OpenUrlUtils.getInstance()
                        .setContext(mContext)
                        .setLoadTransparent(true)
                        .setCannotCancel(false)
                        .setListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                ((LiveAudienceActivity) mContext).setChatShow(true);
                                llRoot.setVisibility(View.VISIBLE);
                            }
                        })
                        .setOndissDialogListener(new DissmissDialogListener() {
                            @Override
                            public void onDissmissListener() {
                                ((LiveAudienceActivity) mContext).setChatShow(true);
                                llRoot.setVisibility(View.VISIBLE);
                            }
                        })
                        .setJump_type(bean.getJump_type())
                        .setIs_king(bean.getIs_king())
                        .setTitle(bean.getName())
                        .setSlide_show_type_button(bean.getSlide_show_type_button())
                        .setType(bean.getSlide_show_type());
                oUtils.go(bean.getSlide_url());
//                    llRoot.setVisibility(View.INVISIBLE);
//                    ((LiveAudienceActivity) mContext).setChatShow(false);
            } else {
                OpenUrlUtils.getInstance()
                        .setContext(mContext)
                        .setType(bean.getSlide_show_type())
                        .setSlideTarget(bean.getSlide_target())
                        .setJump_type(bean.getJump_type())
                        .setIs_king(bean.getIs_king())
                        .setTitle(bean.getName())
                        .setSlide_show_type_button(bean.getSlide_show_type_button())
                        .go(bean.getSlide_url());
            }
        }

    }

    @Override
    public void release() {
        super.release();
        if (timer != null) {// 停止timer
            timer.cancel();
            timer = null;
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerPrivate != null) {
            mTimerPrivate.cancel();
            mTimerPrivate = null;
        }

        if (mTimerPrivate2 != null) {
            mTimerPrivate2.cancel();
            mTimerPrivate2 = null;
        }

        OkGo.getInstance().cancelTag("time");
    }

    /**
     * 收到私密直播socket消息
     */
    public void onSendPrivateLive(int type,String user) {
        L.e("私密直播："+"收到私密直播socket消息"+type);
        if(type==0){//申请成功，开始倒计时
            //开始倒计时
            startPrivateTime(Integer.parseInt(private_timeout));
            choosePrivateStatus(1);
        }else if(type==1){//直播端进入筛选阶段
            startPrivateTime2(300);
            choosePrivateStatus(2);
        }else if(type==2){//拒绝（非一键）
            if(((LiveAudienceActivity)mContext).ismEnd()){
                return;
            }
            List<PrivateUserBean> list=JSON.parseArray(user,PrivateUserBean.class);
            for(PrivateUserBean bean:list){
                if((bean.getUid() + "").equals(CommonAppConfig.getInstance().getUid())){
                    choosePrivateStatus(0);
//                    PrivateLiveDialog privateLiveDialog=PrivateCommonDialog(R.mipmap.icon_private_dialog1,"抱歉，主播未接受您的私密直播申请",
//                            "当前主播已开启私密直播，暂时无法进入！");
//                    privateLiveDialog.setOnDismissDialogListener(new DissmissDialogListener() {
//                        @Override
//                        public void onDissmissListener() {
//                            ((LiveAudienceActivity)mContext).onLiveEnd();
//                        }
//                    });
                    ((LiveAudienceActivity)mContext).exitLiveRoom();
                    ToastUtil.show("抱歉！您的申请未能被接受，去看看其他直播间吧~");
                }
            }
        }else if((type==3)){//一键拒绝
            choosePrivateStatus(0);
            List<PrivateUserBean> list=JSON.parseArray(user,PrivateUserBean.class);
            for(PrivateUserBean bean:list){
                if((bean.getUid() + "").equals(CommonAppConfig.getInstance().getUid())){
                    PrivateCommonDialog(R.mipmap.icon_private_dialog4,"抱歉，您的申请未能被接受",
                            "快去打动主播的心吧！");
                }
            }
        }else if((type==4)){//结束私密直播
            choosePrivateStatus(0);
        }else if((type==5)){//私密开播成功
            choosePrivateStatus(3);

            boolean flag=false;
            List<PrivateUserBean> bean=JSON.parseArray(user,PrivateUserBean.class);
            for(PrivateUserBean a:bean){
                if((a.getUid()+"").equals(CommonAppConfig.getInstance().getUid())){
                    flag=true;
                }
            }
            if(!flag){
//                PrivateLiveDialog privateLiveDialog=PrivateCommonDialog(R.mipmap.icon_private_dialog1,"抱歉，主播未接受您的私密直播申请",
//                        "当前主播已开启私密直播，暂时无法进入！");
//                privateLiveDialog.setOnDismissDialogListener(new DissmissDialogListener() {
//                    @Override
//                    public void onDissmissListener() {
//                        ((LiveAudienceActivity)mContext).onLiveEnd();
//                    }
//                });
                ((LiveAudienceActivity)mContext).exitLiveRoom();
//                ToastUtil.show("当前主播正在进行私密直播哦~暂时无法进入");
                ToastUtil.show("当前主播正在进行私密互动哦~暂时无法进入");
            }

        }
    }

    /**
     * 私密直播按钮状态
     * @param type 0 私密直播；1 申请中；2 等待开播;3 私密直播中
     */
    public void choosePrivateStatus(int type){
        if (private_switch.equals("1")&&!isvideo.equals("1")) {
            flPrivate.setVisibility(View.VISIBLE);
        } else {
            flPrivate.setVisibility(View.GONE);
            return;
        }

        mPrivateState=type;
        if(type==0){
            IMImageLoadUtil.CommonGifLoadCp(CommonAppContext.sInstance,R.drawable.gif_smzb,iv_animation);
            tvPrivateTime.setVisibility(View.GONE);
            flPrivate.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new LivePrivateEvent(0));
        }else if(type==1){
            IMImageLoadUtil.CommonGifLoadCp(CommonAppContext.sInstance,R.drawable.gif_apply,iv_animation);
            tvPrivateTime.setVisibility(View.VISIBLE);
            flPrivate.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new LivePrivateEvent(0));
        }else if(type==2){
            IMImageLoadUtil.CommonGifLoadCp(CommonAppContext.sInstance,R.drawable.gif_wait,iv_animation);
            tvPrivateTime.setVisibility(View.VISIBLE);
            flPrivate.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new LivePrivateEvent(0));
        }else if(type==3){
            flPrivate.setVisibility(View.GONE);
            EventBus.getDefault().post(new LivePrivateEvent(1));
        }
    }

    /**
     * 私密直播报名阶段倒计时
     */
    private void startPrivateTime(int time) {
        if(mTimerPrivate2!=null){
            mTimerPrivate2.cancel();
            mTimerPrivate2=null;
        }
        if (mTimerPrivate == null) {
            mTimerPrivate = new MyCountDownTimer(time * 1000 + 30, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    BigDecimal b = new BigDecimal(TimeUtils.div(millisUntilFinished, 1000, 2)).setScale(0, BigDecimal.ROUND_HALF_UP);
                    if (b.longValue() > 3600) {
                        tvPrivateTime.setText(TimeUtils.convertSecToTimeString(b.longValue()));
                    } else {
                        tvPrivateTime.setText(TimeUtils.convertSecToTimeString2(b.longValue()));
                    }
                }

                @Override
                public void onFinish() {
                    try {
                        mTimerPrivate = null;
                        String t = TimeUtils.convertSecToTimeString2(0);
                        tvPrivateTime.setText(t);
                        //报名结束，状态更改为主播筛选状态
                        //TODO
                        mPrivateState=2;
//                        tvPrivateTime.setText("报名结束，主播筛选阶段");
                    } catch (Exception e) {
                        return;
                    }

                }
            }.start();
        }
    }


    /**
     * 私密直播筛选阶段倒计时
     */
    private void startPrivateTime2(int time){
        if(mTimerPrivate!=null){
            mTimerPrivate.cancel();
            mTimerPrivate=null;
        }
        if (mTimerPrivate2 == null) {
            mTimerPrivate2 = new MyCountDownTimer( time* 1000 + 30, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    BigDecimal b = new BigDecimal(TimeUtils.div(millisUntilFinished, 1000, 2)).setScale(0, BigDecimal.ROUND_HALF_UP);
                    if (b.longValue() > 3600) {
                        tvPrivateTime.setText(TimeUtils.convertSecToTimeString(b.longValue()));
                    } else {
                        tvPrivateTime.setText(TimeUtils.convertSecToTimeString2(b.longValue()));
                    }
                }

                @Override
                public void onFinish() {
                    try {
                        mTimerPrivate2 = null;
                        String t = TimeUtils.convertSecToTimeString2(0);
                        tvPrivateTime.setText(t);
                        //主播未筛选
                        //TODO
                        mPrivateState=0;
                    } catch (Exception e) {
                        return;
                    }

                }
            }.start();
        }
    }

    /**
     * 是否回放
     * @param isvideo
     * @param live_tag
     */
    public void setPrivateInfo(String isvideo, String live_tag) {
        this.isvideo=isvideo;
        this.live_tag=live_tag;
    }

    /**
     * 恢复设置
     */
    public void clearData() {
        if(btnMore!=null){
            btnMore.setVisibility(View.INVISIBLE);
        }
        if(rlMore!=null){
            rlMore.setVisibility(View.INVISIBLE);
        }
    }
}


