package com.yunbao.live.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.faceunity.nama.ui.BeautyViewHolder;
import com.faceunity.nama.ui.FaceUnityBeautyNewViewHolder;
import com.lzy.okgo.model.Response;
import com.opensource.svgaplayer.SVGAImageView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.LiveBackEvent;
import com.yunbao.common.event.LoginForBitEvent;
import com.yunbao.common.event.LoginInvalidEvent;
import com.yunbao.common.event.ShowMsgEvent;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.DissmissDialogListener;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.bean.LiveChatBean;
import com.yunbao.live.bean.LiveGuardInfo;
import com.yunbao.live.bean.LiveKsyConfigBean;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.live.bean.LiveRoomTypeBean;
import com.yunbao.live.bean.PrivateUserBean;
import com.yunbao.live.dialog.LiveFunctionDialogFragment;
import com.yunbao.live.dialog.LiveLinkMicListDialogFragment;
import com.yunbao.live.dialog.LiveNetsDiglog;
import com.yunbao.live.dialog.LiveRoomTypeDialogFragment;
import com.yunbao.live.dialog.LiveShopDialogFragment;
import com.yunbao.live.dialog.LiveSiMiDialog;
import com.yunbao.live.dialog.LiveTimeDialogFragment;
import com.yunbao.live.event.DissShopDialogEvent;
import com.yunbao.live.event.LinkMicTxMixStreamEvent;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.interfaces.LiveFunctionClickListener;
import com.yunbao.live.interfaces.LivePushListener;
import com.yunbao.live.music.LiveMusicDialogFragment;
import com.yunbao.live.presenter.LiveLinkMicAnchorPresenter;
import com.yunbao.live.presenter.LiveLinkMicPkPresenter;
import com.yunbao.live.presenter.LiveLinkMicPresenter;
import com.yunbao.live.socket.SocketChatUtil;
import com.yunbao.live.socket.SocketClient;
import com.yunbao.live.views.AbsLivePushViewHolder;
import com.yunbao.live.views.LiveAnchorViewHolder;
import com.yunbao.live.views.LiveEndViewHolder;
import com.yunbao.live.views.LiveMusicViewHolder;
import com.yunbao.live.views.LivePushTxViewHolder;
import com.yunbao.live.views.LivePushZegoViewHolder;
import com.yunbao.live.views.LiveReadyViewHolder;
import com.yunbao.live.views.LiveRoomViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2018/10/7.
 * ???????????????
 */

public class LiveAnchorActivity extends LiveActivity implements LiveFunctionClickListener {

    private static final String TAG = "LiveAnchorActivity";
    private Dialog mDialog;
    private CountDownTimer timer;
    private LiveNetsDiglog netsDialog;
    private boolean isFirst = true;
    private LiveShopDialogFragment fragmentShop;

    public boolean showLoadindDialog = false;//?????????????????????????????????
    private int pushFailedTime = 0;
    private CountDownTimer timer2;
    private CountDownTimer timer3;
    private int cdnSwitch;//7 ???????????? |?????? ???????????????
    private String mUrl;//???????????????url(???????????????)
    private String pushUrl;
    private int mLiveTimeCoin;

    public static void forward(Context context, int liveSdk, LiveKsyConfigBean bean) {
        Intent intent = new Intent(context, LiveAnchorActivity.class);
        intent.putExtra(Constants.LIVE_SDK, liveSdk);
        intent.putExtra(Constants.LIVE_KSY_CONFIG, bean);
        context.startActivity(intent);

    }

    private ViewGroup mRoot;
    private ViewGroup mContainerWrap;
    private AbsLivePushViewHolder mLivePushViewHolder;
    private LiveReadyViewHolder mLiveReadyViewHolder;
    private BeautyViewHolder mLiveBeautyViewHolder;
    private LiveAnchorViewHolder mLiveAnchorViewHolder;
    private LiveMusicViewHolder mLiveMusicViewHolder;
    private boolean mStartPreview;//??????????????????
    private boolean mStartLive;//?????????????????????
    private List<Integer> mGameList;//????????????
    private boolean mBgmPlaying;//???????????????????????????
    private LiveKsyConfigBean mLiveKsyConfigBean;
    private Handler handler = new Handler();
    private List<LiveReadyBean.GoodsBean> games;
    private LiveSiMiDialog liveSiMiDialog; //??????????????????
    private CommonCallback<LiveRoomTypeBean> mLiveTypeCallback;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_anchor;
    }

    @Override
    protected void main() {
        super.main();
        Intent intent = getIntent();
        mLiveSDK = intent.getIntExtra(Constants.LIVE_SDK, Constants.LIVE_SDK_KSY);
        mLiveKsyConfigBean = intent.getParcelableExtra(Constants.LIVE_KSY_CONFIG);
        L.e(TAG, "??????sdk----->" + (mLiveSDK == Constants.LIVE_SDK_KSY ? "?????????" : "?????????"));
        mRoot = (ViewGroup) findViewById(R.id.root);
        mSocketUserType = Constants.SOCKET_USER_TYPE_ANCHOR;
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        mLiveUid = u.getId();
        mLiveBean = new LiveBean();
        mLiveBean.setUid(mLiveUid);
        mLiveBean.setUserNiceName(u.getUserNiceName());
        mLiveBean.setAvatar(u.getAvatar());
        mLiveBean.setAvatarThumb(u.getAvatarThumb());
        mLiveBean.setLevelAnchor(u.getLevelAnchor());
        mLiveBean.setGoodNum(u.getGoodName());
        mLiveBean.setCity(u.getCity());
        //????????????????????????
        if (mLiveSDK == Constants.LIVE_SDK_TX) {
            mLivePushViewHolder = new LivePushTxViewHolder(mContext, (ViewGroup) findViewById(R.id.preview_container), mLiveKsyConfigBean);
        } else {
//            mLivePushViewHolder = new LivePushKsyViewHolder(mContext, (ViewGroup) findViewById(R.id.preview_container), mLiveKsyConfigBean);
            mLivePushViewHolder = new LivePushTxViewHolder(mContext, (ViewGroup) findViewById(R.id.preview_container), mLiveKsyConfigBean);
        }
        mLivePushViewHolder.setLivePushListener(new LivePushListener() {
            @Override
            public void onPreviewStart() {
                L.e("mStearm--->??????:", "onPreviewStart");
//                ToastUtil.show("onPreviewStart");
                //??????????????????

                mStartPreview = true;
            }

            @Override
            public void onPushStart() {
                L.e("mStearm--->??????:", "onPushStart");
//                ToastUtil.show("onPushStart");
                //??????????????????
                LiveHttpUtil.changeLive(mStream);
                if (timer2 != null) {
                    timer2.cancel();
                    timer2 = null;
                }
            }

            @Override
            public void onPushFailed() {
                L.e("mStearm--->??????:", "onPushFailed");

                if (timer3 != null) {
                    timer3.cancel();
                    timer3 = null;
                }
                L.e("mStearm--->??????:", "??????????????????111");
                //??????????????????
//                if (showLoadindDialog) {
//                    showLoadindDialog = false;
//                    return;
//                }
                L.e("mStearm--->??????:", "??????????????????222");
//                ToastUtil.show(R.string.live_push_failed);
                if (netsDialog == null || !netsDialog.isShowing()) {
                    if (mDialog != null) {
                        mDialog.show();

                    }
                }
                L.e("mStearm--->??????:", "??????????????????333");
                /*if (!NetworkUtils.isMobileDataEnable(mContext) && !NetworkUtils.isWifiDataEnable(mContext)) {
                    if (isFirst) {
                        isFirst = false;
                        ToastUtil.show("??????????????????????????????????????????");
                    }
                } else {
                    countDown();
                }*/
                countDown();

                countDown2();
            }

            @Override
            public void onPush() {
                L.e("mStearm--->??????:", "onPush");
                //?????????
                countDown3();
            }
        });
        mLivePushViewHolder.addToParent();
        mLivePushViewHolder.subscribeActivityLifeCycle();
        mContainerWrap = (ViewGroup) findViewById(R.id.container_wrap);
        mContainer = (ViewGroup) findViewById(R.id.container);
        //???????????????????????????
        mLiveReadyViewHolder = new LiveReadyViewHolder(mContext, mContainer);
        mLiveReadyViewHolder.addToParent();
        mLiveReadyViewHolder.subscribeActivityLifeCycle();
        mLiveLinkMicPresenter = new LiveLinkMicPresenter(mContext, mLivePushViewHolder, true, mLiveSDK, mContainer);
        mLiveLinkMicPresenter.setLiveUid(mLiveUid);
        mLiveLinkMicAnchorPresenter = new LiveLinkMicAnchorPresenter(mContext, mLivePushViewHolder, true, mLiveSDK, mContainer);
        mLiveLinkMicPkPresenter = new LiveLinkMicPkPresenter(mContext, mLivePushViewHolder, true, mContainer);

        mDialog = DialogUitl.loadingNew2Dialog(mContext);
        beauty(false);
        mLiveTypeCallback = new CommonCallback<LiveRoomTypeBean>() {
            @Override
            public void callback(LiveRoomTypeBean bean) {
                switch (bean.getId()) {
                    case Constants.LIVE_TYPE_NORMAL:
                        if(mLiveType==Constants.LIVE_TYPE_NORMAL){
                            return;
                        }
                        onLiveTypeNormal(bean);
                        break;
                    case Constants.LIVE_TYPE_PWD:
                        if(mLiveType==Constants.LIVE_TYPE_PWD){
                            return;
                        }
                        onLiveTypePwd(bean);
                        break;
                    case Constants.LIVE_TYPE_PAY:
                        if(mLiveType==Constants.LIVE_TYPE_PAY){
                            return;
                        }
                        onLiveTypePay(bean);
                        break;
                    case Constants.LIVE_TYPE_TIME:
                        if(mLiveType==Constants.LIVE_TYPE_TIME){
                            return;
                        }
                        onLiveTypeTime(bean);
                        break;
                }
            }
        };
    }

    public boolean isStartPreview() {
        return mStartPreview;
    }


    public void setShowLoadindDialog(boolean showLoadindDialog) {
        this.showLoadindDialog = showLoadindDialog;
    }

    /**
     * ?????????
     */
    private void countDown() {
        if (timer == null) {
            timer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    L.e("mStearm--->??????:","onTick:"+millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    L.e("mStearm--->??????:","onFinish");
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    openLoadingWindow();
                }
            }.start();
        }
    }

    /**
     * ????????? ??????
     */
    private void countDown2() {
        if (timer2 == null) {
            timer2 = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
//                    endLive2();
                    endLive();
                }
            }.start();
        }
    }

    /**
     * ????????? ??????
     */
    private void countDown3() {
        if (timer3 == null) {
            timer3 = new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    if (timer2 != null) {
                        timer2.cancel();
                        timer2 = null;
                    }
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    if (netsDialog != null) {
                        netsDialog.dissmiss();
                    }
                }
            }.start();
        }
    }

    /**
     * ?????????????????????
     */
    public void openLoadingWindow() {
        if (netsDialog != null && netsDialog.isShowing()) {
            return;
        }
        try {
            netsDialog = new LiveNetsDiglog(this);
            Log.e("=---------1", "222");
            netsDialog.showNetsDiglog();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void dismissWindow() {
        try {
            if (netsDialog != null) {
                Log.e("----------2", "dialog??????");
                netsDialog.dissmiss();
            }
            if (mDialog != null) {
                Log.e("----------1", "mDialog??????");
                mDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e("----------3", e.getMessage());
            return;
        }
    }

    /**
     * ??????
     */
    public void reConnection() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

//        if (!NetworkUtils.isMobileDataEnable(mContext) && !NetworkUtils.isWifiDataEnable(mContext)) {
//            if (isFirst) {
//                isFirst = false;
//                ToastUtil.show("??????????????????????????????????????????");
//            }
//        } else {
//            checkLiveing();
//        }
        checkLiveing();
    }

    /**
     * ??????????????????
     */
    private void checkLiveing() {
        L.e("mStearm--->??????:","checkLiveing:"+((LiveAnchorActivity) mContext).getStream());
        LiveHttpUtil.checkLiveing(((LiveAnchorActivity) mContext).getStream(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                L.e("mStearm--->??????:","onSuccess");
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if(obj.getIntValue("status")==0){
                        ToastUtil.show("?????????????????????????????????");
                        endLive();
                    }else {
                        //????????????
                        L.e("mStearm--->??????:","????????????");
                        if (mLivePushViewHolder != null) {
                            L.e("mStearm--->??????:","stopPush");
                            mLivePushViewHolder.stopPush();
                            if (cdnSwitch == 8) {//????????????
                                L.e("mStearm--->??????:","????????????");
                                resetLiveUrl(8);
                            } else if (cdnSwitch != 7) {//???????????????
                                L.e("mStearm--->??????:","???????????????");
                                mLivePushViewHolder.startPush(mUrl, false);
                            } else {//????????????
                                L.e("mStearm--->??????:","????????????");
                                String s = "?ip=" + (SpUtil.getInstance().getStringValue(SpUtil.FAST_PUSH).replace("rtmp://", "").split(":")[0]);
                                if (TextUtils.isEmpty(pushUrl)) {
                                    mLivePushViewHolder.startPush(mUrl + s, false);
                                } else {
                                    mLivePushViewHolder.startPush(CommonAppConfig.getInstance().getFastPush() + pushUrl + s, false);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onError() {
                super.onError();
                L.e("mStearm--->??????:","onError");
            }
        });
    }

    /**
     * ???????????????????????????????????????
     *
     * @param functionID
     */
    @Override
    public void onClick(int functionID) {
        switch (functionID) {
            case Constants.LIVE_FUNC_BEAUTY://??????
                beauty(true);
                break;
            case Constants.LIVE_FUNC_CAMERA://????????????
                toggleCamera();
                break;
            case Constants.LIVE_FUNC_FLASH://???????????????
                toggleFlash();
                break;
            case Constants.LIVE_FUNC_MUSIC://??????
                openMusicWindow();
                break;
            case Constants.LIVE_FUNC_SHARE://??????
                openShareWindow();
                break;
            case Constants.LIVE_FUNC_GAME://??????
                openGameWindow();
                break;
            case Constants.LIVE_FUNC_RED_PACK://??????
                openRedPackSendWindow();
                break;
            case Constants.LIVE_FUNC_LINK_MIC://??????
                openLinkMicAnchorWindow();
                break;
            case Constants.LIVE_FUNC_TYPE://????????????
                chooseLiveType();
                break;
        }
    }

    /**
     * ??????????????????
     */
    private void chooseLiveType() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CHECKED_ID, mLiveType);
        LiveRoomTypeDialogFragment fragment = new LiveRoomTypeDialogFragment();
        fragment.setArguments(bundle);
        fragment.setCallback(mLiveTypeCallback);
        fragment.show(((LiveAnchorActivity) mContext).getSupportFragmentManager(), "LiveRoomTypeDialogFragment");
    }

    /**
     * ????????????
     */
    private void onLiveTypeNormal(final LiveRoomTypeBean bean) {
        DialogUitl.showSimpleDialog(mContext, "????????????","??????????????????????????????????????????????????????", false,
                new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        mLiveType = bean.getId();
                        mLiveTypeVal = 0;
                        mLiveTimeCoin = 0;
                        changeLiveType();
                    }

                });
    }

    /**
     * ????????????
     */
    private void onLiveTypePwd(final LiveRoomTypeBean bean) {
        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_set_pwd), DialogUitl.INPUT_TYPE_NUMBER_PASSWORD, 8, new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, final String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.live_set_pwd_empty);
                } else {
                    dialog.dismiss();
                    DialogUitl.showSimpleDialog(mContext, "????????????","??????????????????????????????????????????????????????", false,
                            new DialogUitl.SimpleCallback() {
                                @Override
                                public void onConfirmClick(Dialog dialog, String content1) {
                                    mLiveType = bean.getId();
                                    if (StringUtil.isInt(content)) {
                                        mLiveTypeVal = Integer.parseInt(content);
                                    }
                                    mLiveTimeCoin = 0;
                                    changeLiveType();
                                }
                            });
                }
            }
        });
    }

    /**
     * ????????????
     */
    private void onLiveTypePay(final LiveRoomTypeBean bean) {
        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_set_fee), DialogUitl.INPUT_TYPE_NUMBER, 8, new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, final String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.live_set_fee_empty);
                } else {
                    dialog.dismiss();
                    DialogUitl.showSimpleDialog(mContext, "????????????","??????????????????????????????????????????????????????", false,
                            new DialogUitl.SimpleCallback() {
                                @Override
                                public void onConfirmClick(Dialog dialog, String content1) {
                                    mLiveType = bean.getId();
                                    if (StringUtil.isInt(content)) {
                                        mLiveTypeVal = Integer.parseInt(content);
                                    }
                                    mLiveTimeCoin = 0;
                                    changeLiveType();
                                }
                            });
                }
            }
        });
    }

    /**
     * ????????????
     */
    private void onLiveTypeTime(final LiveRoomTypeBean bean) {
        LiveTimeDialogFragment fragment = new LiveTimeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CHECKED_COIN, mLiveTimeCoin);
        fragment.setArguments(bundle);
        fragment.setCommonCallback(new CommonCallback<Integer>() {
            @Override
            public void callback(final Integer coin) {
                DialogUitl.showSimpleDialog(mContext, "????????????","??????????????????????????????????????????????????????", false,
                        new DialogUitl.SimpleCallback() {
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                mLiveType = bean.getId();
                                mLiveTypeVal = coin;
                                mLiveTimeCoin = coin;
                                changeLiveType();
                            }
                        });
            }
        });
        fragment.show(((LiveAnchorActivity) mContext).getSupportFragmentManager(), "LiveTimeDialogFragment");
    }

    /**
     * ??????????????????
     */
    private void changeLiveType() {
        LiveHttpUtil.changeLiveType(mLiveType,mLiveTypeVal,mStream,new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show("????????????");
                    SocketChatUtil.sendChangeLive(((LiveAnchorActivity)mContext).getSocketClient(),mLiveType,mLiveTypeVal);
                }else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    //?????????????????????
    public void beforeCamera() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.setOpenCamera(true);
        }
    }


    /**
     * ????????????
     */
    public void toggleCamera() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.toggleCamera();
        }
    }

    /**
     * ???????????????
     */
    public void toggleFlash() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.toggleFlash();
        }
    }

    /**
     * ????????????
     *
     * @param isShow ????????????
     */
    public void beauty(boolean isShow) {

        if (mLiveBeautyViewHolder == null) {
            if (CommonAppConfig.getInstance().isTiBeautyEnable()) {
//                mLiveBeautyViewHolder = new TiBeautyNewViewHolder(mContext, mContainer);
                mLiveBeautyViewHolder = new FaceUnityBeautyNewViewHolder(mContext, mContainer);
                ((FaceUnityBeautyNewViewHolder) mLiveBeautyViewHolder).setModuleManager(((LivePushTxViewHolder) mLivePushViewHolder).getFURenderer());
            } else {
//                mLiveBeautyViewHolder = new DefaultBeautyViewHolder(mContext, mContainer);
            }

            mLiveBeautyViewHolder.setVisibleListener(new BeautyViewHolder.VisibleListener() {
                @Override
                public void onVisibleChanged(boolean visible) {
                    if (mLiveReadyViewHolder != null) {
                        if (visible) {
                            ((FaceUnityBeautyNewViewHolder) mLiveBeautyViewHolder).setShow();
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    ((FaceUnityBeautyNewViewHolder) mLiveBeautyViewHolder).setShow();
//                                }
//                            },1000);
                            mLiveReadyViewHolder.hide();
                        } else {
                            mLiveReadyViewHolder.show();
                        }
                    }
                }
            });
//            if (mLivePushViewHolder != null) {
//                mLiveBeautyViewHolder.setEffectListener(mLivePushViewHolder.getEffectListener());
//            }
        }
        if (!CommonAppConfig.getInstance().isTiBeautyEnable() && isShow) {
            ToastUtil.show("????????????????????????");
            return;
        }
        if (isShow) {
            mLiveBeautyViewHolder.show();
        } else {
            mLiveBeautyViewHolder.hide();
        }
    }

    /**
     * ????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveBackEvent(LiveBackEvent e) {
        if (mLiveBeautyViewHolder != null && mLiveBeautyViewHolder.isShowed()) {
            mLiveBeautyViewHolder.hide();
        }
    }

    /**
     * ??????
     */
    public void light() {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.playLightAnim();
        }
    }

    /**
     * ??????????????????
     */
    private void openMusicWindow() {
        if (isLinkMicAnchor() || isLinkMicAnchor()) {
            ToastUtil.show(R.string.link_mic_not_bgm);
            return;
        }
        LiveMusicDialogFragment fragment = new LiveMusicDialogFragment();
        fragment.setActionListener(new LiveMusicDialogFragment.ActionListener() {
            @Override
            public void onChoose(String musicId) {
                if (mLivePushViewHolder != null) {
                    if (mLiveMusicViewHolder == null) {
                        mLiveMusicViewHolder = new LiveMusicViewHolder(mContext, mContainer, mLivePushViewHolder);
                        mLiveMusicViewHolder.subscribeActivityLifeCycle();
                        mLiveMusicViewHolder.addToParent();
                    }
                    mLiveMusicViewHolder.play(musicId);
                    mBgmPlaying = true;
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "LiveMusicDialogFragment");
    }

    /**
     * ??????????????????
     */
    public void stopBgm() {
        if (mLiveMusicViewHolder != null) {
            mLiveMusicViewHolder.release();
        }
        mLiveMusicViewHolder = null;
        mBgmPlaying = false;
    }

    public boolean isBgmPlaying() {
        return mBgmPlaying;
    }


    /**
     * ??????????????????
     */
    public void showFunctionDialog() {
        LiveFunctionDialogFragment fragment = new LiveFunctionDialogFragment();
        Bundle bundle = new Bundle();
        boolean hasGame = false;
        if (CommonAppConfig.GAME_ENABLE && mGameList != null) {
            hasGame = mGameList.size() > 0;
        }
        bundle.putBoolean(Constants.HAS_GAME, hasGame);
        bundle.putInt(Constants.ROOM_TYPE,mLiveType);
        bundle.putInt(Constants.ROOM_TYPE_VAL,mLiveTypeVal);
        fragment.setArguments(bundle);
        fragment.setFunctionClickListener(this);
        fragment.show(getSupportFragmentManager(), "LiveFunctionDialogFragment");
    }

    /**
     * ????????????????????????
     */
    private void openLinkMicAnchorWindow() {
        if (mLiveLinkMicAnchorPresenter != null && !mLiveLinkMicAnchorPresenter.canOpenLinkMicAnchor()) {
            return;
        }
        LiveLinkMicListDialogFragment fragment = new LiveLinkMicListDialogFragment();
        fragment.show(getSupportFragmentManager(), "LiveLinkMicListDialogFragment");
    }


    /**
     * ????????????????????????
     */
    private void openGameWindow() {
        if (isLinkMic() || isLinkMicAnchor()) {
            ToastUtil.show(R.string.live_link_mic_cannot_game);
            return;
        }
//        if (mGamePresenter != null) {
//            GameDialogFragment fragment = new GameDialogFragment();
//            fragment.setGamePresenter(mGamePresenter);
//            fragment.show(getSupportFragmentManager(), "GameDialogFragment");
//        }
    }


    /**
     * ????????????
     */
    public void closeGame() {
//        if (mGamePresenter != null) {
//            mGamePresenter.closeGame();
//        }
    }


    /**
     * ????????????
     *
     * @param data createRoom???????????????
     */
    public void startLiveSuccess(String data, int liveType, int liveTypeVal) {
        mLiveType = liveType;
        mLiveTypeVal = liveTypeVal;
        //??????createRoom???????????????
        JSONObject obj = JSON.parseObject(data);
        mStream = obj.getString("stream");
        games = JSON.parseArray(obj.getJSONArray("games").toJSONString(), LiveReadyBean.GoodsBean.class);
        mDanmuPrice = obj.getString("barrage_fee");
        String playUrl = obj.getString("pull");
        L.e("createRoom----????????????--->" + playUrl);
        mLiveBean.setPull(playUrl);
        mTxAppId = obj.getString("tx_appid");
        cdnSwitch = obj.getIntValue("cdn_switch");
        CommonAppConfig.getInstance().setShowGoods(true);//??????????????????????????????????????????
        //??????????????????????????????????????????????????????
        if (mLiveReadyViewHolder != null) {
            mLiveReadyViewHolder.removeFromParent();
            mLiveReadyViewHolder.release();
        }
        mLiveReadyViewHolder = null;
        if (mLiveRoomViewHolder == null) {
            mLiveRoomViewHolder = new LiveRoomViewHolder(mContext, mContainer, (GifImageView) findViewById(R.id.gift_gif), (SVGAImageView) findViewById(R.id.gift_svga), mContainerWrap);
            mLiveRoomViewHolder.addToParent(0);
            mLiveRoomViewHolder.subscribeActivityLifeCycle();
            mLiveRoomViewHolder.setLiveInfo(mLiveUid, mStream, "", obj.getIntValue("userlist_time") * 1000);
            mLiveRoomViewHolder.setVotes(obj.getString("votestotal"));
            mLiveRoomViewHolder.setBackShow(false);
            UserBean u = CommonAppConfig.getInstance().getUserBean();
            if (u != null) {
                mLiveRoomViewHolder.setRoomNum(u.getLiangNameTip());
                mLiveRoomViewHolder.setName(u.getUserNiceName());
                mLiveRoomViewHolder.setAvatar(u.getAvatar());
                mLiveRoomViewHolder.setAnchorLevel(u.getLevelAnchor());
            }
//            mLiveRoomViewHolder.startAnchorLight();
        }
        if (mLiveAnchorViewHolder == null) {
            mLiveAnchorViewHolder = new LiveAnchorViewHolder(mContext, mContainer, obj.getJSONObject("goods").toJSONString());
            mLiveAnchorViewHolder.subscribeActivityLifeCycle();
            mLiveAnchorViewHolder.addToParent(1);
            mLiveAnchorViewHolder.setUnReadCount(((LiveActivity) mContext).getImUnReadCount());
            mLiveAnchorViewHolder.setGames(games);
        }
        mLiveBottomViewHolder = mLiveAnchorViewHolder;

        //??????socket
        if (mSocketClient == null) {
            mSocketClient = new SocketClient(obj.getString("chatserver"), this);
            if (mLiveLinkMicPresenter != null) {
                mLiveLinkMicPresenter.setSocketClient(mSocketClient);
            }
            if (mLiveLinkMicAnchorPresenter != null) {
                mLiveLinkMicAnchorPresenter.setSocketClient(mSocketClient);
                mLiveLinkMicAnchorPresenter.setPlayUrl(playUrl);
                mLiveLinkMicAnchorPresenter.setSelfStream(mStream);
            }
            if (mLiveLinkMicPkPresenter != null) {
                mLiveLinkMicPkPresenter.setSocketClient(mSocketClient);
                mLiveLinkMicPkPresenter.setLiveUid(mLiveUid);
                mLiveLinkMicPkPresenter.setSelfStream(mStream);
            }
        }
        mSocketClient.connect(mLiveUid, mStream, "");

        //????????????
        if (mLivePushViewHolder != null) {
            mUrl = obj.getString("push");
            pushUrl = obj.getString("push_url");
            if (cdnSwitch == 7) {//????????????
//                ToastUtil.show("?????????????????????");
                String s = "?ip=" + (SpUtil.getInstance().getStringValue(SpUtil.FAST_PUSH).replace("rtmp://", "").split(":")[0]);
                if (TextUtils.isEmpty(obj.getString("push_url"))) {
                    mLivePushViewHolder.startPush(obj.getString("push") + s, true);
                } else {
                    mLivePushViewHolder.startPush(CommonAppConfig.getInstance().getFastPush() + obj.getString("push_url") + s, true);
                }
            } else {//???????????????
//                ToastUtil.show("????????????????????????");
                mLivePushViewHolder.startPush(obj.getString("push"), true);
            }
//            String s="?ip="+(SpUtil.getInstance().getStringValue(SpUtil.FAST_PUSH).replace("rtmp://","").split(":")[0]);
//            if(TextUtils.isEmpty(obj.getString("push_url"))){
//                mLivePushViewHolder.startPush(obj.getString("push")+s);
//            }else {
//                mLivePushViewHolder.startPush(CommonAppConfig.getInstance().getFastPush()+obj.getString("push_url")+s);
//            }
        }
        //????????????????????????
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.startAnchorLiveTime2();
        }
        mStartLive = true;
        mLiveRoomViewHolder.startRefreshUserList();

        //????????????
        mLiveGuardInfo = new LiveGuardInfo();
        int guardNum = obj.getIntValue("guard_nums");
        mLiveGuardInfo.setGuardNum(guardNum);
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setGuardNum(guardNum);

        }

        //????????????
        int giftPrizePoolLevel = obj.getIntValue("jackpot_level");
        if (giftPrizePoolLevel >= 0) {
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.showPrizePoolLevel(String.valueOf(giftPrizePoolLevel));
            }
        }

        //????????????
        if (CommonAppConfig.GAME_ENABLE) {
            mGameList = JSON.parseArray(obj.getString("game_switch"), Integer.class);
//            GameParam param = new GameParam();
//            param.setContext(mContext);
//            param.setParentView(mContainerWrap);
//            param.setTopView(mContainer);
//            param.setInnerContainer(mLiveRoomViewHolder.getInnerContainer());
//            param.setGameActionListener(new GameActionListenerImpl(LiveAnchorActivity.this, mSocketClient));
//            param.setLiveUid(mLiveUid);
//            param.setStream(mStream);
//            param.setAnchor(true);
//            param.setCoinName(mCoinName);
//            param.setObj(obj);
//            mGamePresenter = new GamePresenter(param);
//            mGamePresenter.setGameList(mGameList);
        }
    }

    /**
     * ????????????????????????????????????
     * @param ct
     */
    @Override
    public void onLiveEnd(String ct) {
        super.onLiveEnd(ct);
//        DialogUitl.showSimpleDialog(mContext, ct, new DialogUitl.SimpleCallback() {
//            @Override
//            public void onConfirmClick(Dialog dialog, String content) {
//                endLive();
//            }
//        });
        ToastUtil.show(ct);
        endLive();
    }

    /**
     * ????????????
     */
    public void closeLive() {
        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.live_end_live), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                endLive();
            }
        });
    }

    /**
     * ????????????
     */
    public void endLive() {
        //??????socket
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        //?????????????????????
        LiveHttpUtil.stopLive(mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (mLivePushViewHolder != null) {
                        mLivePushViewHolder.stopPush();
                    }
                    if (mLiveEndViewHolder == null) {
                        mLiveEndViewHolder = new LiveEndViewHolder(mContext, mRoot);
                        mLiveEndViewHolder.subscribeActivityLifeCycle();
                        mLiveEndViewHolder.addToParent();
                        mLiveEndViewHolder.showDataAnchor(mLiveBean, info[0]);
                    }
                    release();
                    mStartLive = false;
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext, WordUtil.getString(R.string.live_end_ing));
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                if (mLivePushViewHolder != null) {
                    mLivePushViewHolder.stopPush();
                }
                if (mLiveEndViewHolder == null) {
                    mLiveEndViewHolder = new LiveEndViewHolder(mContext, mRoot);
                    mLiveEndViewHolder.subscribeActivityLifeCycle();
                    mLiveEndViewHolder.addToParent();
                    mLiveEndViewHolder.showDataAnchor(mLiveBean,"");
                }
                release();
                mStartLive = false;
//                finish();
            }
        });
    }

    /**
     * ????????????
     */
    public void endLive2() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        //??????socket
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }

        if (mLiveEndViewHolder == null) {
            mLiveEndViewHolder = new LiveEndViewHolder(mContext, mRoot);
            mLiveEndViewHolder.subscribeActivityLifeCycle();
            mLiveEndViewHolder.addToParent();
            mLiveEndViewHolder.showData(mLiveBean, mStream);
        }
        release();
        mStartLive = false;
    }

    @Override
    public void onBackPressed() {
        if (mLiveBeautyViewHolder != null && mLiveBeautyViewHolder.isShowed()) {
            mLiveBeautyViewHolder.hide();
            return;
        }
        if (mStartLive) {
            if (!canBackPressed()) {
                return;
            }
            closeLive();
        } else {
            if (mLivePushViewHolder != null) {
                mLivePushViewHolder.release();
            }
            if (mLiveLinkMicPresenter != null) {
                mLiveLinkMicPresenter.release();
            }
            mLivePushViewHolder = null;
            mLiveLinkMicPresenter = null;
            superBackPressed();
        }
    }

    public void superBackPressed() {
        super.onBackPressed();
    }

    public void release() {
        LiveHttpUtil.cancel(LiveHttpConsts.CHANGE_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.STOP_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.LIVE_PK_CHECK_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.SET_LINK_MIC_ENABLE);
        if (mLiveReadyViewHolder != null) {
            mLiveReadyViewHolder.release();
        }
        if (mLiveMusicViewHolder != null) {
            mLiveMusicViewHolder.release();
        }
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.release();
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.release();
        }
        if (mLiveBeautyViewHolder != null) {
            mLiveBeautyViewHolder.release();
        }
//        if (CommonAppContext.sInstance.beautyNeed.equals("1") && TiSDKManager.getInstance() != null) {
//            TiSDKManager.getInstance().destroy();
//        }
//        if (mGamePresenter != null) {
//            mGamePresenter.release();
//        }
        mLiveMusicViewHolder = null;
        mLiveReadyViewHolder = null;
        mLivePushViewHolder = null;
        mLiveLinkMicPresenter = null;
        mLiveBeautyViewHolder = null;
//        mGamePresenter = null;

        if (timer != null) {
            timer.cancel();
        }

        if (timer2 != null) {
            timer2.cancel();
            timer2 = null;
        }

        if (timer3 != null) {
            timer3.cancel();
            timer3 = null;
        }
        super.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.e("LiveAnchorActivity-------onDestroy------->");
    }


    @Override
    protected void onPause() {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.anchorPause();
        }
        super.onPause();
//        sendSystemMessage(WordUtil.getString(R.string.live_anchor_leave));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.anchorResume();
        }
//        sendSystemMessage(WordUtil.getString(R.string.live_anchor_come_back));
    }

    /**
     * ??????????????????
     */
    @Override
    public void onChat(LiveChatBean bean) {
        super.onChat(bean);
        if (bean != null && !TextUtils.isEmpty(bean.getFollow()) && bean.getFollow().equals("1")) {
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.onFollowAnchor(bean);
            }
            return;
        }
    }


    /**
     * ?????????????????????
     *
     * @param s
     */
    @Override
    public void onSuperCloseLive(String s) {
        endLive();
        if (mContext != null && !isDestroyed()) {
//            DialogUitl.showSimpleTipDialog(mContext, WordUtil.getString(R.string.live_illegal));
            DialogUitl.showSimpleTipCallDialog(LiveAnchorActivity.this, "????????????", "??????????????????????????????????????????????????????????????????????????????????????????\n" + s, new DialogUitl.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {

                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginInvalidEvent(LoginInvalidEvent e) {
        endLive();
        release();
    }

    @Override
    protected void showForBiten() {
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        release();
        DialogUitl.showSimpleTipCallDialog(LiveAnchorActivity.this, "????????????", "???????????????APP?????????????????????????????????????????????????????????????????????????????????????????????????????????", new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                EventBus.getDefault().post(new LoginForBitEvent());
                finish();
            }
        });

    }
    //    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onGameWindowChangedEvent(GameWindowChangedEvent e) {
//        if (mLiveRoomViewHolder != null) {
//            mLiveRoomViewHolder.setOffsetY(e.getGameViewHeight());
//        }
//        if (mLiveAnchorViewHolder != null) {
//            mLiveAnchorViewHolder.setGameBtnVisible(e.isOpen());
//        }
//    }

    public void setBtnFunctionDark() {
        if (mLiveAnchorViewHolder != null) {
            mLiveAnchorViewHolder.setBtnFunctionDark();
        }
    }


    /**
     * ??????????????????
     */
    public void openShopGameWindow() {
        if (games == null || games.size() == 0) {
            Toast.makeText(mContext, "?????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        fragmentShop = new LiveShopDialogFragment(games);
        fragmentShop.show(getSupportFragmentManager(), "LiveShopDialogFragment");
        fragmentShop.setLiveGuardInfo();
        //??????????????????????????????????????????
        fragmentShop.setOnDissmissDialogListener(new DissmissDialogListener() {
            @Override
            public void onDissmissListener() {

                if (mLiveAnchorViewHolder != null) {
                    mLiveAnchorViewHolder.setRootVisibility();
                }
                setChatShow(true);
            }
        });
    }

    /**
     * ????????????????????????
     *
     * @param list
     */
    public void openSiMiDialog(List<PrivateUserBean> list, int time) {
        if (list == null || list.size() == 0) {
            return;
        }
        liveSiMiDialog = new LiveSiMiDialog(list, time);
        liveSiMiDialog.show(getSupportFragmentManager(), "openSiMiDialog");
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     *
     * @type 1 ?????????????????????2 ??????????????????????????????
     */
    public void closePrivate(final int type) {
        LiveHttpUtil.doClosePrivate(((LiveAnchorActivity) mContext).getStream(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    L.e("????????????", "????????????????????????");
                    if (type == 1) {
                        SocketChatUtil.sendPrivateLiveMsg(((LiveAnchorActivity) mContext).getSocketClient(), 4);
                    } else {
                        SocketChatUtil.sendOneKeyRefusePrivateLiveMsg(((LiveAnchorActivity) mContext).getSocketClient(),
                                JSON.toJSONString(mLiveAnchorViewHolder.getList()));
                    }
                }
            }
        });
    }

    /**
     * ????????????item,??????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DissShopDialogEvent event) {
        if (fragmentShop != null) {
            fragmentShop.dismiss();
        }
        if (!event.isSetVisible()) {
            if (mLiveAnchorViewHolder != null) {
                mLiveAnchorViewHolder.setRootVisibility();
            }
            setChatShow(true);
        } else {
            if (mLiveAnchorViewHolder != null) {
                mLiveAnchorViewHolder.setRootGone();
            }
            setChatShow(false);
        }

    }


    /**
     * ?????????????????????  ????????????????????????????????????
     * ????????????
     */
    @Override
    public void onLinkMicAnchorApply(UserBean u, String stream) {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorApply(u, stream);
        }
    }

    /**
     * ?????????????????????  ?????????????????????????????????
     */
    @Override
    public void onLinkMicAnchorRefuse() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorRefuse();
        }
    }

    /**
     * ?????????????????????  ??????????????????????????????
     */
    @Override
    public void onLinkMicAnchorNotResponse() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicNotResponse();
        }
    }

    /**
     * ?????????????????????  ????????????????????????
     */
    @Override
    public void onlinkMicPlayGaming() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onlinkMicPlayGaming();
        }
    }


    /**
     * ?????????????????????  ??????????????????????????????
     */
    @Override
    public void onLinkMicAnchorBusy() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorBusy();
        }
    }

    /**
     * ????????????????????????
     *
     * @param pkUid  ???????????????uid
     * @param stream ???????????????stream
     */
    public void linkMicAnchorApply(final String pkUid, String stream) {
        L.e("??????:" + stream);
        LiveHttpUtil.livePkCheckLive(pkUid, stream, mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mLiveSDK == Constants.LIVE_SDK_TX) {
                        String playUrl = null;
                        JSONObject obj = JSON.parseObject(info[0]);
                        if (obj != null) {
                            String accUrl = obj.getString("pull");//???????????????????????????
                            if (!TextUtils.isEmpty(accUrl)) {
                                playUrl = accUrl;
                            }
                        }
                        if (mLiveLinkMicAnchorPresenter != null) {
                            mLiveLinkMicAnchorPresenter.applyLinkMicAnchor(pkUid, playUrl, mStream);
                        }
                    } else {
                        if (mLiveLinkMicAnchorPresenter != null) {
                            mLiveLinkMicAnchorPresenter.applyLinkMicAnchor(pkUid, null, mStream);
                        }
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * ????????????pk??????????????????
     */
    public void setPkBtnVisible(boolean visible) {
        if (mLiveAnchorViewHolder != null) {
            if (visible) {
                if (mLiveLinkMicAnchorPresenter.isLinkMic()) {
                    mLiveAnchorViewHolder.setPkBtnVisible(true);
                }
            } else {
                mLiveAnchorViewHolder.setPkBtnVisible(false);
            }
        }
    }

    /**
     * ??????????????????pk
     */
    public void applyLinkMicPk() {
        String pkUid = null;
        if (mLiveLinkMicAnchorPresenter != null) {
            pkUid = mLiveLinkMicAnchorPresenter.getPkUid();
        }
        if (!TextUtils.isEmpty(pkUid) && mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.applyLinkMicPk(pkUid, mStream);
        }
    }

    /**
     * ???????????????PK  ????????????????????????????????????PK???????????????
     *
     * @param u      ?????????????????????
     * @param stream ???????????????stream
     */
    @Override
    public void onLinkMicPkApply(UserBean u, String stream) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkApply(u, stream);
        }
    }

    /**
     * ???????????????PK  ??????????????????pk?????????
     */
    @Override
    public void onLinkMicPkRefuse() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkRefuse();
        }
    }

    /**
     * ???????????????PK   ??????????????????????????????
     */
    @Override
    public void onLinkMicPkBusy() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkBusy();
        }
    }

    /**
     * ???????????????PK   ??????????????????????????????
     */
    @Override
    public void onLinkMicPkNotResponse() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkNotResponse();
        }
    }

    /**
     * ??????sdk????????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLinkMicTxMixStreamEvent(LinkMicTxMixStreamEvent e) {
//        if (mLivePushViewHolder != null && mLivePushViewHolder instanceof LivePushZegoViewHolder) {
//            ((LivePushZegoViewHolder) mLivePushViewHolder).onLinkMicTxMixStreamEvent(e.getType(), e.getToStream());
//        }
    }

    /**
     * ?????????????????????
     *
     * @param isShow
     */
    public void setChatShow(boolean isShow) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setChatShow(isShow);
        }
    }

    /**
     * H5??????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMsgEvent(ShowMsgEvent e) {
        doRedirect(e);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param e
     */
    private void doRedirect(ShowMsgEvent e) {
        LiveHttpUtil.doRedirect(mLiveUid, e.getBean().getGoodsUrl(), e.getBean().getGoodsName(),
                e.getBean().getOuthParam(), e.getBean().getGoodsId(), e.getBean().getActiveId(),
                new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {

                        }
                    }
                });
    }

    /**
     * ??????????????????????????????
     */
    @Override
    public void onSendPrivateLive(int type, String user) {
        if (mLiveAnchorViewHolder != null) {
            mLiveAnchorViewHolder.onSendPrivateLive(type, user);
        }
    }


    /**
     * ???????????????????????????
     *
     * @param cdn_switch
     */
    @Override
    public void onSendSwitchLive(final int cdn_switch) {
        resetLiveUrl(cdn_switch);
    }

    /**
     * ??????????????????
     *
     * @param cdn_switch ????????????
     */
    private void resetLiveUrl(final int cdn_switch) {
        LiveHttpUtil.resetLiveUrl(1, mStream,
                new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (mLivePushViewHolder != null && !TextUtils.isEmpty(info[0])) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                L.e("WOLF","???????????????"+CommonAppConfig.getInstance().getFastPush());
                                cdnSwitch = cdn_switch;
                                mUrl = obj.getString("url");
                                if (cdn_switch != 7) {//???????????????
//                                    ToastUtil.show("????????????????????????");
                                    mLivePushViewHolder.stopPush();
                                    showAutoDismissLoading();
                                    mLivePushViewHolder.startPush(obj.getString("url"), false);
                                } else {
//                                    ToastUtil.show("?????????????????????");
                                    String s = "?ip=" + (SpUtil.getInstance().getStringValue(SpUtil.FAST_PUSH).replace("rtmp://", "").split(":")[0]);
                                    mLivePushViewHolder.stopPush();
                                    showAutoDismissLoading();
                                    mLivePushViewHolder.startPush(CommonAppConfig.getInstance().getFastPush() + obj.getString("url") + s, false);
                                }

                                mLivePushViewHolder.reStartBgm();

                            }
                        }

                    }
                });
    }

    /**
     * ????????????dialog
     */
    private void showAutoDismissLoading() {
        if (mDialog != null) {
            mDialog.show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
            }
        }, 1000);
    }
    /**
     * ???????????????????????????????????????socket
     */
    public void sendUserList(String userList,String nums) {
        SocketChatUtil.sendUserList(((LiveAnchorActivity)mContext).getSocketClient(), userList,nums);
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void onStopLive() {
        super.onStopLive();
//        ToastUtil.show("??????????????????");
        endLive();
    }

    @Override
    public void onReconnect() {
        reConnectNum++;
        L.e("WOLF","onReconnect:"+reConnectNum);
        if(reConnectNum>=10){
            endLive();
        }
    }
}
