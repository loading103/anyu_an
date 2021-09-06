package com.yunbao.live.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.DialogShowEvent;
import com.yunbao.common.event.LiveShowRedEvent;
import com.yunbao.common.fragment.ButtomDialogFragment;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.DissmissDialogListener;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.adapter.LiveBuyAdapter;
import com.yunbao.live.bean.GoodsResultBean;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.live.bean.PrivateUserBean;
import com.yunbao.live.bean.TimeBean;
import com.yunbao.live.custom.ChildPresenter;
import com.yunbao.live.custom.MyCountDownTimer;
import com.yunbao.live.event.LivePrivateEvent;
import com.yunbao.live.event.LiveShowBottomEvent;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.socket.SocketChatUtil;
import com.yunbao.live.utils.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2018/10/9.
 * 主播直播间逻辑
 */

public class LiveAnchorViewHolder extends AbsLiveViewHolder{

    private ImageView mBtnFunction;
    private View mBtnGameClose;//关闭游戏的按钮
    private View mBtnPk;//主播连麦pk按钮
    private Drawable mDrawable0;
    private Drawable mDrawable1;
    private Drawable mDrawableLinkMic0;//允许连麦
    private Drawable mDrawableLinkMic1;//禁止连麦
    private ImageView mLinkMicIcon;//是否允许连麦的标记
    private TextView mLinkMicTip;//是否允许连麦的提示
    private HttpCallback mChangeLinkMicCallback;
    private boolean mLinkMicEnable;
    private ImageView btnMore;
    private TextView tvTime;
    private LiveReadyBean.GoodsBean goods;
    private String mGoodsNo;
    private Timer timer;
    private GifImageView btnGift;
    private MyCountDownTimer mTimer;
    private boolean isFirst=true;
    private CountDownTimer timer3;
    private int postTimes=0;
    private RelativeLayout rlBottom;
    private FrameLayout flBuy;
    private ChildPresenter mRecyclerView;

    private LinearLayout mllroot;
    private ImageView mIvGame;
    /**
     * 弹窗进入动画
     */
    private int tranxtime=2000;
    private int alphatime=1500;
    private int showtime =15000;
    private ObjectAnimator mBgAnimator1;
    private ObjectAnimator mBgAnimator2;
    private  Handler handler=new Handler();
    private int recycleViewH,flBuyW;
    private TextView tvTitle;
    private  LiveBuyAdapter mBuyAdapter;
    private ButtomDialogFragment mDialog;
    private OpenUrlUtils oUtils;
    private RelativeLayout rlMore;
    private TextView tvPrivateTime;
    private MyCountDownTimer mTimerPrivate;
    private String private_limit;
    private String private_timeout;
    private int mPrivateState;
    private  List<UserBean> userdatas;  //私密直播列表datas
    private MyCountDownTimer mTimerPrivate2;
    private FrameLayout flPrivate;
    private TextView tvPrivateTitle;
    private ImageView ivPrivate;
    private CountDownTimer timer4;
    private View view_red;
    public void setList(List<PrivateUserBean> list) {
        this.list = list;
    }

    public List<PrivateUserBean> getList() {
        return list;
    }

    private List<PrivateUserBean> list;
    private long cTime;//当前筛选剩余时间

    public LiveAnchorViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public LiveAnchorViewHolder(Context context, ViewGroup parentView,Object... args) {
        super(context, parentView,args);
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        goods = JSON.parseObject((String)args[0], LiveReadyBean.GoodsBean.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_anchor;
    }

    @Override
    public void init() {
        super.init();
        EventBus.getDefault().register(this);
        mDrawable0 = ContextCompat.getDrawable(mContext, R.mipmap.icon_live_func_0);
        mDrawable1 = ContextCompat.getDrawable(mContext, R.mipmap.icon_live_func_1);
        mBtnFunction = (ImageView) findViewById(R.id.btn_function);
        btnMore=(ImageView) findViewById(R.id.btn_more);
        rlMore=(RelativeLayout) findViewById(R.id.rl_more);
        flBuy = (FrameLayout) findViewById(R.id.fl_buy);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        mRecyclerView = (ChildPresenter) findViewById(R.id.recyclerView);
        recycleViewH = mRecyclerView.getLayoutParams().height;
        mllroot=(LinearLayout)findViewById(R.id.ll_root);
        mIvGame=(ImageView) findViewById(R.id.iv_game);
        view_red= findViewById(R.id.view_red);
        initRecycle();
        flBuyW = flBuy.getLayoutParams().width;
        btnMore.setOnClickListener(this);
        tvTime=(TextView) findViewById(R.id.tv_time);
        rlBottom=(RelativeLayout) findViewById(R.id.rl_bottom);
        mBtnFunction.setImageDrawable(mDrawable0);
        mBtnFunction.setOnClickListener(this);
        mIvGame.setOnClickListener(this);
        mBtnGameClose = findViewById(R.id.btn_close_game);
        mBtnGameClose.setOnClickListener(this);
        findViewById(R.id.iv_buy_close).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        mBtnPk = findViewById(R.id.btn_pk);
        mBtnPk.setOnClickListener(this);
        mDrawableLinkMic0 = ContextCompat.getDrawable(mContext, R.mipmap.icon_live_link_mic);
        mDrawableLinkMic1 = ContextCompat.getDrawable(mContext, R.mipmap.icon_live_link_mic_1);
        mLinkMicIcon = (ImageView) findViewById(R.id.link_mic_icon);
        mLinkMicTip = (TextView) findViewById(R.id.link_mic_tip);
        tvPrivateTime = (TextView) findViewById(R.id.tv_private_time);
        flPrivate = (FrameLayout) findViewById(R.id.fl_private);
        tvPrivateTitle = (TextView) findViewById(R.id.tv_private_title);
        ivPrivate = (ImageView) findViewById(R.id.iv_private);
        findViewById(R.id.btn_link_mic).setOnClickListener(this);
        findViewById(R.id.fl_private).setOnClickListener(this);
        initData();
    }

    private void initRecycle() {
        //代购列表
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(mContext,10, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mBuyAdapter = new LiveBuyAdapter();
        mRecyclerView.setAdapter(mBuyAdapter);
    }

    private void initData() {
        if(goods!=null){
            if(goods.isFlag()){
                btnMore.setVisibility(View.VISIBLE);
                rlMore.setVisibility(View.VISIBLE);
                ImgLoader.displayCircle(mContext,goods.getThumb(),btnMore);
            }else {
                btnMore.setVisibility(View.INVISIBLE);
                rlMore.setVisibility(View.INVISIBLE);
            }
            mGoodsNo=goods.getGoods_no();
            getTime();
        }

        String private_switch = CommonAppConfig.getInstance().getConfig().getPrivate_switch();
        private_limit = CommonAppConfig.getInstance().getConfig().getPrivate_limit();
        private_timeout = CommonAppConfig.getInstance().getConfig().getPrivate_timeout();
//        if(private_switch.equals("1")){
//            flPrivate.setVisibility(View.VISIBLE);
//        }else {
//            flPrivate.setVisibility(View.GONE);
//        }

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
        } else if(i == R.id.iv_buy_close){
            flBuy.setTranslationX(DpUtil.dp2px(500));
            flBuy.setAlpha(1);
        }else if (i == R.id.btn_function) {
            showFunctionDialog();

        } else if (i == R.id.btn_close_game) {
            closeGame();

        } else if (i == R.id.btn_pk) {
            applyLinkMicPk();

        } else if (i == R.id.btn_link_mic) {
            changeLinkMicEnable();

        }else if (i == R.id.btn_more) {
            if(goods!=null) {
                    oUtils = OpenUrlUtils.getInstance()
                            .setContext(mContext)
                            .setLoadTransparent(true)
                            .setCannotCancel(false)
                            .setJump_type(goods.getJump_type())
                            .setIs_king(goods.getIs_king())
                            .setSlide_show_type_button(goods.getSlide_show_type_button())
                            .setListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    ((LiveAnchorActivity) mContext).setChatShow(true);
                                    rlBottom.setVisibility(View.VISIBLE);
                                    mllroot.setVisibility(View.VISIBLE);
                                }
                            })
                            .setOndissDialogListener(new DissmissDialogListener() {
                                @Override
                                public void onDissmissListener() {
                                    ((LiveAnchorActivity) mContext).setChatShow(true);
                                    rlBottom.setVisibility(View.VISIBLE);
                                    mllroot.setVisibility(View.VISIBLE);
                                }
                            })
                            .setType(Integer.parseInt(goods.getShow_type()));
                    oUtils.go(goods.getJump_url());

//                mllroot.setVisibility(View.INVISIBLE);
//                rlBottom.setVisibility(View.INVISIBLE);
                ((LiveAnchorActivity) mContext).setChatShow(false);
                }
        }else if (i == R.id.iv_game) {
            gameClick();
        }else if (i == R.id.fl_private) {
            if(mPrivateState==0){

            }else if(mPrivateState==1){

            }else if(mPrivateState==2){//筛选中
                if(list!=null){
                    ((LiveAnchorActivity)mContext).openSiMiDialog(list,Integer.parseInt(cTime/1000+"")+1);
                }
            }else if(mPrivateState==3){//主播关闭私密直播
                DialogUitl.showSimpleDialog(mContext,"当前正在私密互动，确定要退出私密互动吗？", new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        ((LiveAnchorActivity)mContext).closePrivate(1);
                    }
                });
            }
        }
    }

    private void gameClick() {
//        mllroot.setVisibility(View.INVISIBLE);
//        rlBottom.setVisibility(View.INVISIBLE);
//        ((LiveAnchorActivity)mContext).setChatShow(false);
        ((LiveAnchorActivity)mContext).openShopGameWindow();
    }

    /**
     * 设置游戏按钮是否可见
     */
    public void setGameBtnVisible(boolean show) {
        if (mBtnGameClose != null) {
            if (show) {
                if (mBtnGameClose.getVisibility() != View.VISIBLE) {
                    mBtnGameClose.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnGameClose.getVisibility() == View.VISIBLE) {
                    mBtnGameClose.setVisibility(View.INVISIBLE);
                }
            }
        }
    }



    /**
     * 关闭游戏
     */
    private void closeGame() {
        ((LiveAnchorActivity) mContext).closeGame();
    }

    /**
     * 关闭直播
     */
    private void close() {
        ((LiveAnchorActivity) mContext).closeLive();
    }

    /**
     * 显示功能弹窗
     */
    private void showFunctionDialog() {
        if (mBtnFunction != null) {
            mBtnFunction.setImageDrawable(mDrawable1);
        }
        ((LiveAnchorActivity) mContext).showFunctionDialog();
    }

    /**
     * 设置功能按钮变暗
     */
    public void setBtnFunctionDark() {
        if (mBtnFunction != null) {
            mBtnFunction.setImageDrawable(mDrawable0);
        }
    }

    /**
     * 设置连麦pk按钮是否可见
     */
    public void setPkBtnVisible(boolean visible) {
        if (mBtnPk != null) {
            if (visible) {
                if (mBtnPk.getVisibility() != View.VISIBLE) {
                    mBtnPk.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnPk.getVisibility() == View.VISIBLE) {
                    mBtnPk.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 发起主播连麦pk
     */
    private void applyLinkMicPk() {
        ((LiveAnchorActivity) mContext).applyLinkMicPk();
    }

    public void setLinkMicEnable(boolean linkMicEnable) {
        mLinkMicEnable = linkMicEnable;
        showLinkMicEnable();
    }

    private void showLinkMicEnable() {
        if (mLinkMicEnable) {
            if (mLinkMicIcon != null) {
                mLinkMicIcon.setImageDrawable(mDrawableLinkMic1);
            }
            if (mLinkMicTip != null) {
                mLinkMicTip.setText(R.string.live_link_mic_5);
            }
        } else {
            if (mLinkMicIcon != null) {
                mLinkMicIcon.setImageDrawable(mDrawableLinkMic0);
            }
            if (mLinkMicTip != null) {
                mLinkMicTip.setText(R.string.live_link_mic_4);
            }
        }
    }


    private void changeLinkMicEnable() {
        if (mChangeLinkMicCallback == null) {
            mChangeLinkMicCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        showLinkMicEnable();
                    }
                }
            };
        }
        mLinkMicEnable = !mLinkMicEnable;
        LiveHttpUtil.setLinkMicEnable(mLinkMicEnable, mChangeLinkMicCallback);
    }

    /**
     * 右侧倒计时
     */
    private void getTime() {
        String id = CommonAppConfig.getInstance().getConfig().getKing_id();
        OkGo.<String>post(CommonAppConfig.getInstance().getConfig().getGoods_server())
                .tag("time")
                .params("playGroupId",mGoodsNo)
                .params("companyShortName",CommonAppConfig.getInstance().getConfig().getKing_id())
//                .params("companyShortName","mm")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        TimeBean bean = JSON.parseObject(response.body(), TimeBean.class);
                        if(bean.getResult()==1){
                            int time = bean.getLeftTime();
                            if(time<=1){
                                startTime();
                                return;
                            }
                            postTimes=0;
                            if(time>3600){
                                tvTime.setText(TimeUtils.convertSecToTimeString(bean.getLeftTime()));
                            }else {
                                tvTime.setText(TimeUtils.convertSecToTimeString2(bean.getLeftTime()));
                            }
                            countDown(time);
                            doMachineBuy(time);

                            if(!isFirst){
                                timer3 = new CountDownTimer(5000+30, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        getResult();
                                    }
                                }.start();
                            }else {
                                isFirst=false;
                            }
                        }else {
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
        postTimes++;
        if (postTimes > 5) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getTime();
                }
            }, 2000);
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getTime();
                }
            }, 1000);
        }
    }

    /**
     *触发僵尸粉购买消息
     * @param time
     */
    private void doMachineBuy(int time) {
        LiveHttpUtil.doMachineBuy(time,goods.getId(),CommonAppConfig.getInstance().getUid(),
                new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {

                        }
                    }
                });
    }

    /**
     * 获取结果
     */
    private void getResult() {
        L.e("WOLF","获取结果");
        OkGo.<String>post(CommonAppConfig.getInstance().getConfig().getGoods_result())
                .tag("result")
                .params("size",1)
                .params("playGroupId",mGoodsNo)
                .params("companyShortName",CommonAppConfig.getInstance().getConfig().getKing_id())
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
                            showResult(Long.parseLong(CommonAppConfig.getInstance().getConfig().getGoods_show_time()),
                                    bean.getSscHistoryList().get(0),goods);
                        }
                    }
                });
    }

    /**
     * 倒计时
     */
    private void countDown(long time) {
        L.e("WOLF","countDown开始:"+time);
        if(mTimer==null){
            mTimer = new MyCountDownTimer(time*1000+30, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    BigDecimal b = new BigDecimal(TimeUtils.div(millisUntilFinished,
                            1000,2)).setScale(0, BigDecimal.ROUND_HALF_UP);
                    L.e("WOLF","countDown:"+b.longValue());

                    if(b.longValue()>3600){
                        tvTime.setText(TimeUtils.convertSecToTimeString(b.longValue()));
                    }else {
                        tvTime.setText(TimeUtils.convertSecToTimeString2(b.longValue()));
                    }
                }

                @Override
                public void onFinish() {
                    try {
                        mTimer=null;
                        L.e("WOLF","countDown:完成");
                        String t=TimeUtils.convertSecToTimeString2(0);
                        tvTime.setText(t.equals("00:00")?"封盘中":t);
                        getTime();
                    }catch (Exception e){
                        return;
                    }

                }
            }.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAnim();
        if (timer != null) {// 停止timer
            timer.cancel();
            timer = null;
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (timer3 != null) {
            timer3.cancel();
            timer3 = null;
        }

        if (timer4 != null) {
            timer4.cancel();
            timer4 = null;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDialogShowEvent(final DialogShowEvent e) {
        mllroot.setVisibility(View.VISIBLE);
        rlBottom.setVisibility(View.VISIBLE);
        ((LiveAnchorActivity) mContext).setChatShow(true);
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
    /**
     * 显示结果
     * @param time
     * @param data
     */
    public void showResult(long time,GoodsResultBean.SscHistoryListBean data,LiveReadyBean.GoodsBean goods){
        tvTitle.setText(goods.getName()+"  "+data.getNumber()+CommonAppConfig.getInstance().getConfig().getGoods_show_title());
        startBuyResultAnim( time==-1?showtime:time*1000,data);
    }
    private void startBuyResultAnim(final long time,GoodsResultBean.SscHistoryListBean data) {
        List<String> list = new ArrayList<>();
        for(String s:data.getOpenCode().split(",")){
            list.add(s);
        }
        ViewGroup.LayoutParams layoutParams = mRecyclerView.getLayoutParams();
        ViewGroup.LayoutParams layoutParams1 = flBuy.getLayoutParams();
        if(list.size()<=10){
            if(list.size()<=7){
                //字长取字，8个item长取item长度
                int width = (int) (flBuyW * 8.0 / 10);
                TextPaint textPaint = tvTitle.getPaint();
                int width0 = (int) textPaint.measureText(tvTitle.getText().toString())+ IMDensityUtil.dip2px(mContext,40);
                layoutParams1.width= Math.max(width, width0);
                Log.e("------","width="+width+"---width0="+width0);
                flBuy.setLayoutParams(layoutParams1);
            }
            layoutParams.height=(int)(recycleViewH*1.0/2);
            mRecyclerView.setLayoutParams(layoutParams);
        }
        mBuyAdapter.setNewData(list);

        Interpolator interpolator1 = new AccelerateDecelerateInterpolator();
        Interpolator interpolator2 = new AccelerateInterpolator();

        mBgAnimator1 = ObjectAnimator.ofFloat(flBuy, "translationX", 20);
        mBgAnimator1.setInterpolator(interpolator1);
        mBgAnimator1.setDuration(tranxtime);

        mBgAnimator2 = ObjectAnimator.ofFloat(flBuy, "alpha", 1, 0);
        mBgAnimator2.setInterpolator(interpolator2);
        mBgAnimator2.setDuration(alphatime);
        mBgAnimator1.start();
        mBgAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mBgAnimator2==null){
                            return;
                        }
                        mBgAnimator2.start();
                    }
                },time);
            }
        });
        mBgAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                flBuy.setTranslationX(DpUtil.dp2px(500));
                flBuy.setAlpha(1);
            }
        });

    }



    public void cancelAnim() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (mBgAnimator1 != null) {
            mBgAnimator1.cancel();
        }
        if (mBgAnimator2 != null) {
            mBgAnimator2.cancel();
        }
        EventBus.getDefault().unregister(this);
    }


    /**
     * 是否显示底部
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveShowBottomEvent(LiveShowBottomEvent e) {
        if(e.isShow()){
            rlBottom.setVisibility(View.VISIBLE);
            mllroot.setVisibility(View.VISIBLE);
            ((LiveAnchorActivity)mContext).setChatShow(true);
        }else {
//            rlBottom.setVisibility(View.INVISIBLE);
//            mllroot.setVisibility(View.INVISIBLE);
//            ((LiveAnchorActivity)mContext).setChatShow(false);
        }
    }

    public void setRootVisibility() {
        mllroot.setVisibility(View.VISIBLE);
        rlBottom.setVisibility(View.VISIBLE);
        ((LiveAnchorActivity)mContext).setChatShow(true);
    }
    public void setRootGone() {
//        mllroot.setVisibility(View.INVISIBLE);
//        rlBottom.setVisibility(View.INVISIBLE);
//        ((LiveAnchorActivity)mContext).setChatShow(false);
    }

    public void setGames(List<LiveReadyBean.GoodsBean> games) {
        if(games!=null&&games.size()>0){
            mIvGame.setVisibility(View.VISIBLE);
        }else {
            mIvGame.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 私密直播socket消息
     * @param type
     */
    public void onSendPrivateLive(int type,String user) {
        if(type==0){//报名阶段开启，开始倒计时
            startPrivateTime();
            choosePrivateStatus(1);
            EventBus.getDefault().post(new LivePrivateEvent(2,Integer.parseInt(private_timeout)));
        }else if(type==1){//直播端进入筛选阶段
            startPrivateTime2();
            choosePrivateStatus(2);
        }else if(type==2){//拒绝（非一键拒绝）
            if(TextUtils.isEmpty(user)){//拒绝所有
                choosePrivateStatus(0);
            }
        }else if(type==3){//一键拒绝所有
            choosePrivateStatus(0);
        }else if((type==4)){//结束私密直播
            choosePrivateStatus(0);
        }else if((type==5)){//私密开播成功
            choosePrivateStatus(3);
        }
    }

    /**
     * 私密直播按钮状态
     * @param type 0 私密直播；1 申请中；2 等待开播;3 私密直播中
     */
    public void choosePrivateStatus(int type){
        mPrivateState=type;
        if(type==0){
            ivPrivate.setImageResource(R.mipmap.icon_private_zb1);
            tvPrivateTime.setVisibility(View.GONE);
            flPrivate.setVisibility(View.GONE);
            tvPrivateTitle.setVisibility(View.GONE);
            EventBus.getDefault().post(new LivePrivateEvent(0));
        }else if(type==1){
            ivPrivate.setImageResource(R.mipmap.icon_private_zb1);
            tvPrivateTime.setVisibility(View.GONE);
            flPrivate.setVisibility(View.GONE);
            tvPrivateTitle.setVisibility(View.GONE);
            EventBus.getDefault().post(new LivePrivateEvent(0));
        }else if(type==2){
            ivPrivate.setImageResource(R.mipmap.icon_private_zb1);
            tvPrivateTime.setVisibility(View.VISIBLE);
            flPrivate.setVisibility(View.VISIBLE);
            tvPrivateTitle.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new LivePrivateEvent(0));
        }else if(type==3){
            ivPrivate.setImageResource(R.mipmap.icon_private_zb2);
            tvPrivateTime.setVisibility(View.GONE);
            flPrivate.setVisibility(View.VISIBLE);
            tvPrivateTitle.setVisibility(View.GONE);
            EventBus.getDefault().post(new LivePrivateEvent(1));
        }
    }

    /**
     * 私密直播报名阶段倒计时
     */
    private void startPrivateTime() {
        if(mTimerPrivate2!=null){
            mTimerPrivate2.cancel();
            mTimerPrivate2=null;
        }
        if (mTimerPrivate == null) {
            L.e("私密互动","开始10s报名倒计时");
            mTimerPrivate = new MyCountDownTimer(Integer.parseInt(private_timeout)
                    * 1000 + 30, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    L.e("私密互动",millisUntilFinished+"");
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
                        L.e("私密互动","10s倒计时结束");
                        mTimerPrivate = null;
                        String t = TimeUtils.convertSecToTimeString2(0);
                        tvPrivateTime.setText(t);
                        //报名结束，状态更改为主播筛选状态
                        //TODO
                        mPrivateState=2;
                        getLiveHandle();
                    } catch (Exception e) {
                        return;
                    }

                }
            }.start();
        }
    }

    /**
     * 用户报名倒计时结束,主播获取申请用户列表
     */
    private void getLiveHandle() {
        L.e("私密互动","开始获取报名信息");
        LiveHttpUtil.getLiveHandle(((LiveAnchorActivity)mContext).getStream(),new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    L.e("私密互动","获取报名信息成功:"+Arrays.toString(info));
                    list = JSON.parseArray(Arrays.toString(info), PrivateUserBean.class);
                    ((LiveAnchorActivity)mContext).openSiMiDialog(list,300);
                    SocketChatUtil.sendPrivateLiveMsg(((LiveAnchorActivity)mContext).getSocketClient(),1);
                }
            }
        });
    }

    /**
     * 私密互动筛选阶段倒计时
     */
    private void startPrivateTime2() {
        if(mTimerPrivate!=null){
            mTimerPrivate.cancel();
            mTimerPrivate=null;
        }
        if (mTimerPrivate2 == null) {
            mTimerPrivate2 = new MyCountDownTimer( 300* 1000 + 30, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    cTime=millisUntilFinished;
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

                        List<PrivateUserBean> a=new ArrayList<>();
                        List<PrivateUserBean> b=new ArrayList<>();
                        for(PrivateUserBean bean:list){
                            if(bean.getSimiType()==1){
                                a.add(bean);
                            }else {
                                b.add(bean);
                            }
                        }
                        if(a.size()==0){
                            ((LiveAnchorActivity)mContext).closePrivate(2);
                        }else {
                            startPrivate(JSON.toJSONString(a),JSON.toJSONString(b));
                        }
                    } catch (Exception e) {
                        return;
                    }

                }
            }.start();
        }
    }

    /**
     * 主播开始私密互动
     * @param all_users 所有用户
     * @param refuse_users 拒绝的用户
     */
    private void startPrivate(final String all_users,final String refuse_users){
        LiveHttpUtil.startPrivate(((LiveAnchorActivity)mContext).getStream(),refuse_users,new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if(!TextUtils.isEmpty(refuse_users)&&!refuse_users.equals("[]")){
                        SocketChatUtil.sendRefusePrivateLiveMsg(((LiveAnchorActivity)mContext).getSocketClient(),refuse_users);
                    }
//                    SocketChatUtil.sendPrivateLiveMsg(((LiveAnchorActivity)mContext).getSocketClient(),5);
//                    PrivateUserPlayBean bean=new PrivateUserPlayBean();
//                    bean.setAccept_user(JSON.parseArray(accept_users,PrivateUserBean.class));
//                    bean.setRefuse_user(JSON.parseArray(refuse_users,PrivateUserBean.class));
                    SocketChatUtil.sendPrivateLivePlayMsg(((LiveAnchorActivity)mContext).getSocketClient(),JSON.toJSONString(all_users));
                }else {
                    ToastUtil.show(msg);
                }
            }
        });
    }
}

