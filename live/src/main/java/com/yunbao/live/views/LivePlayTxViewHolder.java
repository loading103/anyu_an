package com.yunbao.live.views;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaSync;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.model.Response;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.live.custom.MyCountDownTimer;
import com.yunbao.live.dialog.LiveNetDialog;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.utils.NetworkUtils;

import static com.tencent.rtmp.TXLiveConstants.PLAY_EVT_PLAY_PROGRESS;

/**
 * Created by cxf on 2018/10/10.
 * 直播间播放器  腾讯播放器
 */

public class LivePlayTxViewHolder extends LiveRoomPlayViewHolder implements ITXLivePlayListener {

    private static final String TAG = "LiveTxPlayViewHolder";
    private ViewGroup mRoot;
    private ViewGroup mSmallContainer;
    private ViewGroup mLeftContainer;
    private ViewGroup mRightContainer;
    private ViewGroup mPkContainer;
    private TXCloudVideoView mVideoView;
    private TXCloudVideoView mVideoView2;
    private View mLoading;
    private ImageView mCover;
    private TXLivePlayer mPlayer;//直播
    private TXLivePlayer mPlayer2;//视频
    private boolean mPaused;//是否切后台了
    private boolean mStarted;//是否开始了播放
    private boolean mEnd;//是否结束了播放
    private boolean mPausedPlay;//是否被动暂停了播放
    private boolean mChangeToLeft;
    private boolean mChangeToAnchorLinkMic;
    private String mUrl;
    private int mPlayType;
    private HttpCallback mGetTxLinkMicAccUrlCallback;
    private Handler mHandler;
    private LiveNetDialog dialog;
    private CountDownTimer timer;
    private Dialog mDialog;
    private boolean isFirst = true;
    private boolean voiceSwitch;
    private MyCountDownTimer mTimer;
    private int mLiveType;
    private int mLiveTypeVal;
    private LiveBean beandata;
    private String mLiveUid;
    private String mStream;
    private Handler mLiveRoomHandler;
    private HttpCallback mTimeChargeCallback;
    private CountDownTimer timer2;

    public LivePlayTxViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_play_tx;
    }

    @Override
    public void init() {
        mRoot = (ViewGroup) findViewById(R.id.root);
        mSmallContainer = (ViewGroup) findViewById(R.id.small_container);
        mLeftContainer = (ViewGroup) findViewById(R.id.left_container);
        mRightContainer = (ViewGroup) findViewById(R.id.right_container);
        mPkContainer = (ViewGroup) findViewById(R.id.pk_container);
        mLoading = findViewById(R.id.loading);
        mCover = (ImageView) findViewById(R.id.cover);
        mVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mVideoView2 = (TXCloudVideoView) findViewById(R.id.video_view2);

        mPlayer = new TXLivePlayer(mContext);
        mPlayer2 = new TXLivePlayer(mContext);
        mPlayer.setPlayListener(this);
        mPlayer2.setPlayListener(this);
        mPlayer.setPlayerView(mVideoView);
        mPlayer2.setPlayerView(mVideoView2);

        mPlayer.enableHardwareDecode(false);
        mPlayer2.enableHardwareDecode(false);
        mPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mPlayer2.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mPlayer2.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        TXLivePlayConfig playConfig = new TXLivePlayConfig();
        playConfig.setAutoAdjustCacheTime(true);
        playConfig.setMaxAutoAdjustCacheTime(5.0f);
        playConfig.setMinAutoAdjustCacheTime(1.0f);
        mPlayer.setConfig(playConfig);
        mPlayer2.setConfig(playConfig);
        mDialog = DialogUitl.loadingNew2Dialog(mContext);
        voiceSwitch = SpUtil.getInstance().getBooleanValue(SpUtil.VOICESET);

        mTimeChargeCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ((LiveAudienceActivity)mContext).dismissDialog();
                if (mContext instanceof LiveAudienceActivity) {
                    final LiveAudienceActivity liveAudienceActivity = (LiveAudienceActivity) mContext;
                    if (code == 0 ) {
                        //已经扣费在直播间
                        if(((LiveAudienceActivity)mContext).getIsPreview()==3){
                            return;
                        }
                        ((LiveAudienceActivity)mContext).setIsPreview(3);
                        ((LiveAudienceActivity)mContext). enterTimeRoom();
                        liveAudienceActivity.roomChargeUpdateVotes();
                    } else {
                        if(timer2!=null){
                            timer2.cancel();
                            timer2=null;
                        }
                        liveAudienceActivity.pausePlay();
                        if (code == 1008 || code ==-1) {//余额不足
                            liveAudienceActivity.setCoinNotEnough(true);
                            DialogUitl.showSimpleTipCallDialog(mContext, "温馨提示",  WordUtil.getString(R.string.live_coin_not_enough),  new DialogUitl.SimpleCallback() {
                                @Override
                                public void onConfirmClick(Dialog dialog, String content) {
                                    liveAudienceActivity.exitLiveRoom();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                ((LiveAudienceActivity)mContext).dismissDialog();
            }
        };

    }

    public void setFirst(boolean first) {
        isFirst = first;
//        if (mLoading != null && mLoading.getVisibility() != View.VISIBLE) {
//            mLoading.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onPlayEvent(final int e, Bundle bundle) {
        L.e(TAG, "onPlayEvent----->" + e);
        if (mEnd) {
            return;
        }
        switch (e) {

            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN://播放开始
                L.e("Play:" + "PLAY_EVT_PLAY_BEGIN");
                if (mLoading != null && mLoading.getVisibility() == View.VISIBLE) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                cancelTimer();
                closeLoadingWindow();
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
                L.e("Play:" + "PLAY_EVT_PLAY_LOADING");

                ((LiveAudienceActivity)mContext).dismissDialog();
                if (mLoading != null && mLoading.getVisibility() != View.VISIBLE) {
                    L.e("Play:" + "显示loading");
                    mLoading.setVisibility(View.VISIBLE);
                    countDown();
                }
                break;
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME://第一帧
                L.e("Play:" + "PLAY_EVT_RCV_FIRST_I_FRAME");
                hideCover();
                ((LiveAudienceActivity)mContext).dismissDialog();
                if(((LiveAudienceActivity)mContext).getIsPreview()==1){
                    int time=CommonAppConfig.getInstance().getConfig().getLive_preview_time();
                    mTimer = new MyCountDownTimer(time * 1000+100, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.e("onTick--------",millisUntilFinished+"---mLiveType"+mLiveType+"---mLiveTypeVal"+mLiveTypeVal);
                        }

                        @Override
                        public void onFinish() {
//                            ((LiveAudienceActivity)mContext).pausePlay();
                            //类似上下滑动房间的效果
                            if(mLiveTypeVal==0){
                                return;
                            }
                            ((LiveAudienceActivity)mContext).clearRoomData(true);
                            String content="您的预览时间已经结束，当前房间需"+((mLiveType== Constants.LIVE_TYPE_PAY)?"":"每分钟") +"支付" + mLiveTypeVal + CommonAppConfig.getInstance().getConfig().getCoinName()+"才能观看哦！";
                            DialogUitl.showSimpleDialog(mContext, "温馨提示",content , false, new DialogUitl.SimpleCallback2() {
                                @Override
                                public void onCancelClick() {
                                    ((LiveAudienceActivity)mContext).onBackPressed();
                                }
                                @Override
                                public void onConfirmClick(Dialog dialog, String content) {
                                    Log.e("mLiveType--------",mLiveType+"");
                                    if(mLiveType==Constants.LIVE_TYPE_PAY){
                                        doPay();
                                    }else {
                                        requestTimeCharge();
                                    }
                                }
                            });
                        }
                    }.start();
                }
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_END://播放结束
                L.e("Play:" + "PLAY_EVT_PLAY_END");
                replay();
                break;
            case TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION://获取视频宽高
                L.e("Play:" + "PLAY_EVT_CHANGE_RESOLUTION");
                if (mChangeToLeft || mChangeToAnchorLinkMic) {
                    return;
                }
                float width = bundle.getInt("EVT_PARAM1", 0);
                float height = bundle.getInt("EVT_PARAM2", 0);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mVideoView.getLayoutParams();
                int targetH = 0;
                if (width >= height) {//横屏 9:16=0.5625
                    targetH = (int) (mVideoView.getWidth() / width * height);
                } else {
                    targetH = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                if (targetH != params.height) {
                    params.height = targetH;
                    params.gravity = Gravity.CENTER;
                    mVideoView.requestLayout();
                }

                FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) mVideoView2.getLayoutParams();
                int targetH2 = 0;
                if (width >= height) {//横屏 9:16=0.5625
                    targetH2 = (int) (mVideoView2.getWidth() / width * height);
                } else {
                    targetH2 = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                if (targetH2 != params2.height) {
                    params2.height = targetH;
                    params2.gravity = Gravity.CENTER;
                    mVideoView2.requestLayout();
                }

                break;
            case TXLiveConstants.PLAY_ERR_NET_DISCONNECT://播放失败
            case TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND:
                L.e("Play:" + "播放失败");
//                ToastUtil.show(WordUtil.getString(R.string.live_play_error));
//                if(dialog==null||dialog.getDialog()==null||!dialog.getDialog().isShowing()){
//                    if(mDialog!=null){
////                        mDialog.show();
//                        mDialog.dismiss();
//                    }
//                }
                if (mLoading != null && mLoading.getVisibility() != View.VISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
                if (!NetworkUtils.isMobileDataEnable(mContext) && !NetworkUtils.isWifiDataEnable(mContext)) {
                    if (isFirst) {
                        isFirst = false;
//                        ToastUtil.show("当前网络环境不佳，请检查网络");
                    }
//                    countDown();
                } else {
                    countDown();
                }
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS://播放进度
                // 加载进度, 单位是毫秒
                int duration_ms = bundle.getInt(TXLiveConstants.EVT_PLAYABLE_DURATION_MS);
                // 播放进度, 单位是毫秒
                int progress_ms = bundle.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS);
                // 视频总长, 单位是毫秒
                int duration_ms_all = bundle.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS);
                if(progress_ms==duration_ms_all){
                    replay();
                }
                break;
        }
    }

    /**
     * 门票房
     * http://47.75.111.156//api/public/?service=Live.roomCharge1021
     * roomCharge :: []655 876
     */
    private void doPay() {
        ((LiveAudienceActivity)mContext).showDialog();
        String liveuid ="";
        String Stream ="";
        if(beandata!=null && !TextUtils.isEmpty(beandata.getUid())){
            liveuid=beandata.getUid();
            Stream=beandata.getStream();
        }else {
            liveuid=mLiveUid;
            Stream=mStream;
        }
        Log.e("mLiveUid---------","mLiveUid="+liveuid+"----mStream="+Stream);
        LiveHttpUtil.roomCharge(liveuid, Stream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ((LiveAudienceActivity)mContext).dismissDialog();
                if (code == 0) {
                    ((LiveAudienceActivity)mContext).setIsPreview(2);
                    ((LiveAudienceActivity)mContext).enterTimeRoom();
                }
                if (code == 1008 || code ==-1) {//余额不足
                    DialogUitl.showSimpleTipCallDialog(mContext, "温馨提示",  WordUtil.getString(R.string.live_coin_not_enough),  new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            ((LiveAudienceActivity)mContext).onBackPressed();
                        }
                    });
                }
            }

            @Override
            public void onError(String message) {
                super.onError(message);
                ((LiveAudienceActivity)mContext).dismissDialog();
                ToastUtil.show(message);
            }
        });

    }

    /**
     * 请求计时收费的扣费接口
     */
    private void requestTimeCharge() {
        if (!TextUtils.isEmpty(mLiveUid) && mTimeChargeCallback != null) {
            LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
            Log.e("------------","timeCharge");
            ((LiveAudienceActivity)mContext).showDialog();
            LiveHttpUtil.timeCharge(mLiveUid, mStream, mTimeChargeCallback);
            countDownPay();
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }


    @Override
    public void hideCover() {
        if (mCover != null) {
            mCover.animate().alpha(0).setDuration(500).start();
        }
    }

    @Override
    public void setCover(String coverUrl) {
        if (mCover != null) {
            if (TextUtils.isEmpty(coverUrl)) {
//                ImgLoader.displayBlur(CommonAppContext.sInstance,R.mipmap.img_default_pic3, mCover);
                mCover.setVisibility(View.INVISIBLE);
            } else {
                ImgLoader.displayBlur(CommonAppContext.sInstance, coverUrl, mCover, R.mipmap.img_default_pic3);
                mCover.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setData(int liveType, int liveTypeVal, String liveUid, String stream) {
        this.mLiveType=liveType;
        this.mLiveTypeVal=liveTypeVal;
        this.mLiveUid=liveUid;
        this.mStream=stream;
    }

    /**
     * 循环播放
     */
    private void replay() {
        if (mStarted && mPlayer != null) {
            mPlayer.seek(0);
            mPlayer.resume();
        }

        if (mStarted && mPlayer2 != null) {
            mPlayer2.seek(0);
            mPlayer2.resume();
        }
    }


    public void setmLiveTypeVal(int mLiveTypeVal) {
        this.mLiveTypeVal = mLiveTypeVal;
    }
    public void setmLiveType(int mLiveType) {
        this.mLiveType = mLiveType;
    }

    public void setBeandata(LiveBean beandata) {
        this.beandata = beandata;
    }

    /**
     * 暂停播放
     */
    @Override
    public void pausePlay() {
        if (!mPausedPlay) {
            mPausedPlay = true;
            if (!mPaused) {
                if (mPlayer != null) {
                    mPlayer.pause();
                }
                if (mPlayer2 != null) {
                    mPlayer2.pause();
                }
            }
            if (mCover != null) {
                mCover.setAlpha(1f);
                if (mCover.getVisibility() != View.VISIBLE) {
                    mCover.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 暂停播放后恢复
     */
    @Override
    public void resumePlay() {
        if (mPausedPlay) {
            mPausedPlay = false;
            if (!mPaused) {
                if (mPlayer != null) {
                    mPlayer.resume();
                }
                if (mPlayer2 != null) {
                    mPlayer2.resume();
                }
            }
            hideCover();
        }
    }

    /**
     * 开始播放
     *
     * @param url 流地址
     */
    @Override
    public void play(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        int playType = -1;
        if (url.startsWith("rtmp://")) {
            playType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
        } else if (url.endsWith(".flv")) {
            playType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
        } else if (url.endsWith(".m3u8")) {
            playType = TXLivePlayer.PLAY_TYPE_VOD_HLS;
        } else if (url.endsWith(".mp4")) {
            playType = TXLivePlayer.PLAY_TYPE_VOD_MP4;
        }
        if (playType == -1) {
            ToastUtil.show(R.string.live_play_error_2);
            return;
        }

        if (playType == TXLivePlayer.PLAY_TYPE_LIVE_RTMP) {
            L.e(TAG, "play1");
            if (mPlayer != null) {
                mVideoView.setVisibility(View.VISIBLE);
                mVideoView2.setVisibility(View.INVISIBLE);
                int result = mPlayer.startPlay(url.trim(), playType);
                if (result == 0) {
                    mStarted = true;
                    mUrl = url;
                    mPlayType = playType;
                }
            }
        } else {
            L.e(TAG, "play2");
            if (mPlayer2 != null) {
                mVideoView.setVisibility(View.INVISIBLE);
                mVideoView2.setVisibility(View.VISIBLE);
                int result = mPlayer2.startPlay(url.trim(), playType);
                if (result == 0) {
                    mStarted = true;
                    mUrl = url;
                    mPlayType = playType;
                }

            }
        }

        L.e(TAG, "play----url--->" + url);
    }

    @Override
    public void stopPlay() {
        mChangeToLeft = false;
        mChangeToAnchorLinkMic = false;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mCover != null) {
            mCover.setAlpha(1f);
            if (mCover.getVisibility() != View.VISIBLE) {
                mCover.setVisibility(View.VISIBLE);
            }
        }
        stopPlay2();
    }

    @Override
    public void stopPlay2() {
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
        }

        if (mPlayer2 != null) {
            mPlayer2.stopPlay(false);
        }
    }

    @Override
    public void release() {
        mEnd = true;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        LiveHttpUtil.cancel(LiveHttpConsts.GET_TX_LINK_MIC_ACC_URL);
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
            mPlayer.setPlayListener(null);
        }
        mPlayer = null;

        if (mPlayer2 != null) {
            mPlayer2.stopPlay(false);
            mPlayer2.setPlayListener(null);
        }
        mPlayer2 = null;

        L.e(TAG, "release------->");

        if (dialog != null) {
            dialog = null;
        }
        if (mDialog != null) {
            mDialog = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (timer2 != null) {
            timer2.cancel();
            timer2 = null;
        }
    }





    @Override
    public ViewGroup getSmallContainer() {
        return mSmallContainer;
    }


    @Override
    public ViewGroup getRightContainer() {
        return mRightContainer;
    }

    @Override
    public ViewGroup getPkContainer() {
        return mPkContainer;
    }

    @Override
    public void changeToLeft() {
        mChangeToLeft = true;
        if (mVideoView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mVideoView.getLayoutParams();
            params.width = mVideoView.getWidth() / 2;
            params.height = DpUtil.dp2px(250);
            params.topMargin = DpUtil.dp2px(130);
            mVideoView.setLayoutParams(params);
        }

        if (mVideoView2 != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mVideoView2.getLayoutParams();
            params.width = mVideoView2.getWidth() / 2;
            params.height = DpUtil.dp2px(250);
            params.topMargin = DpUtil.dp2px(130);
            mVideoView2.setLayoutParams(params);
        }

        if (mLoading != null && mLeftContainer != null) {
            ViewParent viewParent = mLoading.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(mLoading);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DpUtil.dp2px(24), DpUtil.dp2px(24));
//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DpUtil.dp2px(40), DpUtil.dp2px(40));
            params.gravity = Gravity.CENTER;
            mLoading.setLayoutParams(params);
            mLeftContainer.addView(mLoading);
        }
    }

    @Override
    public void changeToBig() {
        mChangeToLeft = false;
        if (mVideoView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mVideoView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.topMargin = 0;
            mVideoView.setLayoutParams(params);
        }

        if (mVideoView2 != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mVideoView2.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.topMargin = 0;
            mVideoView2.setLayoutParams(params);
        }

        if (mLoading != null && mRoot != null) {
            ViewParent viewParent = mLoading.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(mLoading);
            }
//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DpUtil.dp2px(24), DpUtil.dp2px(24));
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DpUtil.dp2px(70), DpUtil.dp2px(70));
            params.gravity = Gravity.CENTER;
            mLoading.setLayoutParams(params);
            mRoot.addView(mLoading);
        }
    }

    @Override
    public void onResume() {
        if (!voiceSwitch) {
            return;
        }
        if (!mPausedPlay && mPaused && mPlayer != null) {
            mPlayer.resume();
        }

        if (!mPausedPlay && mPaused && mPlayer2 != null) {
            mPlayer2.resume();
        }
        mPaused = false;
    }

    @Override
    public void onPause() {
        if (!voiceSwitch) {
            return;
        }
        if (!mPausedPlay && mPlayer != null) {
            mPlayer.pause();
        }

        if (!mPausedPlay && mPlayer2 != null) {
            mPlayer2.pause();
        }
        mPaused = true;
    }

    @Override
    public void onDestroy() {
//        release();
    }

    /**
     * 腾讯sdk连麦时候切换低延时流
     */
    public void onLinkMicTxAccEvent(boolean linkMic) {
        if (mStarted && mPlayer != null && !TextUtils.isEmpty(mUrl)) {
            mPlayer.stopPlay(false);
            if (linkMic) {
                if (mGetTxLinkMicAccUrlCallback == null) {
                    mGetTxLinkMicAccUrlCallback = new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                if (obj != null) {
                                    String accUrl = obj.getString("streamUrlWithSignature");
                                    if (!TextUtils.isEmpty(accUrl) && mPlayer != null) {
                                        L.e(TAG, "低延时流----->" + accUrl);
                                        mPlayer.startPlay(accUrl, TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
                                    }
                                }
                            }
                        }
                    };
                }
                LiveHttpUtil.getTxLinkMicAccUrl(mUrl, mGetTxLinkMicAccUrlCallback);
            } else {
                mPlayer.startPlay(mUrl, mPlayType);
            }
        }

        if (mStarted && mPlayer2 != null && !TextUtils.isEmpty(mUrl)) {
            mPlayer2.stopPlay(false);
            if (linkMic) {
                if (mGetTxLinkMicAccUrlCallback == null) {
                    mGetTxLinkMicAccUrlCallback = new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                if (obj != null) {
                                    String accUrl = obj.getString("streamUrlWithSignature");
                                    if (!TextUtils.isEmpty(accUrl) && mPlayer2 != null) {
                                        L.e(TAG, "低延时流----->" + accUrl);
                                        mPlayer2.startPlay(accUrl, TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
                                    }
                                }
                            }
                        }
                    };
                }
                LiveHttpUtil.getTxLinkMicAccUrl(mUrl, mGetTxLinkMicAccUrlCallback);
            } else {
                mPlayer2.startPlay(mUrl, mPlayType);
            }
        }
    }

    /**
     * 设置主播连麦模式
     *
     * @param anchorLinkMic
     */
    public void setAnchorLinkMic(final boolean anchorLinkMic, int delayTime) {
        if (mVideoView == null) {
            return;
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mChangeToAnchorLinkMic = anchorLinkMic;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mVideoView.getLayoutParams();
                if (anchorLinkMic) {
                    params.height = DpUtil.dp2px(250);
                    params.topMargin = DpUtil.dp2px(130);
                    params.gravity = Gravity.TOP;
                } else {
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.topMargin = 0;
                    params.gravity = Gravity.CENTER;
                }
                mVideoView.setLayoutParams(params);

                FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) mVideoView2.getLayoutParams();
                if (anchorLinkMic) {
                    params2.height = DpUtil.dp2px(250);
                    params2.topMargin = DpUtil.dp2px(130);
                    params2.gravity = Gravity.TOP;
                } else {
                    params2.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    params2.topMargin = 0;
                    params2.gravity = Gravity.CENTER;
                }
                mVideoView2.setLayoutParams(params2);
            }
        }, delayTime);

    }


    /**
     * 倒计时
     */
    private void countDown() {
        if (timer == null) {
            timer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    L.e("Play:" + "打开窗口");
                    openLoadingWindow();
                }
            }.start();
        }
    }

    /**
     * 倒计时收费
     */
    private void countDownPay() {
        if (timer2 == null) {
            timer2 = new CountDownTimer(60000+100, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.e("onTick倒计时收费",millisUntilFinished+"---mLiveType"+mLiveType+"---mLiveTypeVal"+mLiveTypeVal);

                }

                @Override
                public void onFinish() {
                    requestTimeCharge();
                }
            }.start();
        }else {
            timer2.start();
        }
    }

    /**
     * 取消到计时
     */
    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * 打开加载中窗口
     */
    public void openLoadingWindow() {
        try {
            if (dialog != null && dialog.getDialog() != null && dialog.getDialog().isShowing()) {

            } else {
                dialog = new LiveNetDialog();
                dialog.show(((LiveAudienceActivity) mContext).getSupportFragmentManager(), "LiveNetDialog");
            }
            if (mLoading != null && mLoading.getVisibility() == View.VISIBLE) {
                mLoading.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            return;
        }

    }

    /**
     * 关闭加载中窗口
     */
    private void closeLoadingWindow() {
        if (dialog != null && dialog.getDialog() != null && dialog.getDialog().isShowing()) {
            dialog.dismissAllowingStateLoss();
        }
    }


    /**
     * 重连
     */
    public void reConnection() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
//        if(mDialog!=null){
//            mDialog.show();
//        }
//        if (mLoading != null && mLoading.getVisibility() != View.VISIBLE) {
//            mLoading.setVisibility(View.VISIBLE);
//        }
        if (!NetworkUtils.isMobileDataEnable(mContext) && !NetworkUtils.isWifiDataEnable(mContext)) {
            if (isFirst) {
                isFirst = false;
//                ToastUtil.show("当前网络环境不佳，请检查网络");
            }
        }

        if (!mPlayer.isPlaying()) {
            if (mLoading != null && mLoading.getVisibility() != View.VISIBLE) {
                mLoading.setVisibility(View.VISIBLE);
            }
        }
    }

    public void clearTime() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if(timer2!=null){
            timer2.cancel();
            mTimer=null;
        }
    }


}
