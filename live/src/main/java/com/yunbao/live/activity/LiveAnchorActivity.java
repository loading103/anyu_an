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
 * 主播直播间
 */

public class LiveAnchorActivity extends LiveActivity implements LiveFunctionClickListener {

    private static final String TAG = "LiveAnchorActivity";
    private Dialog mDialog;
    private CountDownTimer timer;
    private LiveNetsDiglog netsDialog;
    private boolean isFirst = true;
    private LiveShopDialogFragment fragmentShop;

    public boolean showLoadindDialog = false;//点击照相通知这边不谈窗
    private int pushFailedTime = 0;
    private CountDownTimer timer2;
    private CountDownTimer timer3;
    private int cdnSwitch;//7 本地推流 |其他 阿里云推流
    private String mUrl;//阿里云拉流url(非本地拉流)
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
    private boolean mStartPreview;//是否开始预览
    private boolean mStartLive;//是否开始了直播
    private List<Integer> mGameList;//游戏开关
    private boolean mBgmPlaying;//是否在播放背景音乐
    private LiveKsyConfigBean mLiveKsyConfigBean;
    private Handler handler = new Handler();
    private List<LiveReadyBean.GoodsBean> games;
    private LiveSiMiDialog liveSiMiDialog; //私密主播弹窗
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
        L.e(TAG, "直播sdk----->" + (mLiveSDK == Constants.LIVE_SDK_KSY ? "金山云" : "腾讯云"));
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
        //添加推流预览控件
        if (mLiveSDK == Constants.LIVE_SDK_TX) {
            mLivePushViewHolder = new LivePushTxViewHolder(mContext, (ViewGroup) findViewById(R.id.preview_container), mLiveKsyConfigBean);
        } else {
//            mLivePushViewHolder = new LivePushKsyViewHolder(mContext, (ViewGroup) findViewById(R.id.preview_container), mLiveKsyConfigBean);
            mLivePushViewHolder = new LivePushTxViewHolder(mContext, (ViewGroup) findViewById(R.id.preview_container), mLiveKsyConfigBean);
        }
        mLivePushViewHolder.setLivePushListener(new LivePushListener() {
            @Override
            public void onPreviewStart() {
                L.e("mStearm--->推流:", "onPreviewStart");
//                ToastUtil.show("onPreviewStart");
                //开始预览回调

                mStartPreview = true;
            }

            @Override
            public void onPushStart() {
                L.e("mStearm--->推流:", "onPushStart");
//                ToastUtil.show("onPushStart");
                //开始推流回调
                LiveHttpUtil.changeLive(mStream);
                if (timer2 != null) {
                    timer2.cancel();
                    timer2 = null;
                }
            }

            @Override
            public void onPushFailed() {
                L.e("mStearm--->推流:", "onPushFailed");

                if (timer3 != null) {
                    timer3.cancel();
                    timer3 = null;
                }
                L.e("mStearm--->推流:", "推流失败回调111");
                //推流失败回调
//                if (showLoadindDialog) {
//                    showLoadindDialog = false;
//                    return;
//                }
                L.e("mStearm--->推流:", "推流失败回调222");
//                ToastUtil.show(R.string.live_push_failed);
                if (netsDialog == null || !netsDialog.isShowing()) {
                    if (mDialog != null) {
                        mDialog.show();

                    }
                }
                L.e("mStearm--->推流:", "推流失败回调333");
                /*if (!NetworkUtils.isMobileDataEnable(mContext) && !NetworkUtils.isWifiDataEnable(mContext)) {
                    if (isFirst) {
                        isFirst = false;
                        ToastUtil.show("当前网络环境不佳，请检查网络");
                    }
                } else {
                    countDown();
                }*/
                countDown();

                countDown2();
            }

            @Override
            public void onPush() {
                L.e("mStearm--->推流:", "onPush");
                //推流中
                countDown3();
            }
        });
        mLivePushViewHolder.addToParent();
        mLivePushViewHolder.subscribeActivityLifeCycle();
        mContainerWrap = (ViewGroup) findViewById(R.id.container_wrap);
        mContainer = (ViewGroup) findViewById(R.id.container);
        //添加开播前设置控件
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
     * 倒计时
     */
    private void countDown() {
        if (timer == null) {
            timer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    L.e("mStearm--->推流:","onTick:"+millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    L.e("mStearm--->推流:","onFinish");
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    openLoadingWindow();
                }
            }.start();
        }
    }

    /**
     * 倒计时 关播
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
     * 倒计时 异常
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
     * 打开加载中窗口
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
                Log.e("----------2", "dialog消失");
                netsDialog.dissmiss();
            }
            if (mDialog != null) {
                Log.e("----------1", "mDialog消失");
                mDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e("----------3", e.getMessage());
            return;
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

//        if (!NetworkUtils.isMobileDataEnable(mContext) && !NetworkUtils.isWifiDataEnable(mContext)) {
//            if (isFirst) {
//                isFirst = false;
//                ToastUtil.show("当前网络环境不佳，请检查网络");
//            }
//        } else {
//            checkLiveing();
//        }
        checkLiveing();
    }

    /**
     * 检查开播状态
     */
    private void checkLiveing() {
        L.e("mStearm--->推流:","checkLiveing:"+((LiveAnchorActivity) mContext).getStream());
        LiveHttpUtil.checkLiveing(((LiveAnchorActivity) mContext).getStream(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                L.e("mStearm--->推流:","onSuccess");
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if(obj.getIntValue("status")==0){
                        ToastUtil.show("连接已断开，请重新开播");
                        endLive();
                    }else {
                        //开始推流
                        L.e("mStearm--->推流:","开始推流");
                        if (mLivePushViewHolder != null) {
                            L.e("mStearm--->推流:","stopPush");
                            mLivePushViewHolder.stopPush();
                            if (cdnSwitch == 8) {//金山推流
                                L.e("mStearm--->推流:","金山推流");
                                resetLiveUrl(8);
                            } else if (cdnSwitch != 7) {//阿里云推流
                                L.e("mStearm--->推流:","阿里云推流");
                                mLivePushViewHolder.startPush(mUrl, false);
                            } else {//本地推流
                                L.e("mStearm--->推流:","本地推流");
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
                L.e("mStearm--->推流:","onError");
            }
        });
    }

    /**
     * 主播直播间功能按钮点击事件
     *
     * @param functionID
     */
    @Override
    public void onClick(int functionID) {
        switch (functionID) {
            case Constants.LIVE_FUNC_BEAUTY://美颜
                beauty(true);
                break;
            case Constants.LIVE_FUNC_CAMERA://切换镜头
                toggleCamera();
                break;
            case Constants.LIVE_FUNC_FLASH://切换闪光灯
                toggleFlash();
                break;
            case Constants.LIVE_FUNC_MUSIC://伴奏
                openMusicWindow();
                break;
            case Constants.LIVE_FUNC_SHARE://分享
                openShareWindow();
                break;
            case Constants.LIVE_FUNC_GAME://游戏
                openGameWindow();
                break;
            case Constants.LIVE_FUNC_RED_PACK://红包
                openRedPackSendWindow();
                break;
            case Constants.LIVE_FUNC_LINK_MIC://连麦
                openLinkMicAnchorWindow();
                break;
            case Constants.LIVE_FUNC_TYPE://房间类型
                chooseLiveType();
                break;
        }
    }

    /**
     * 选择直播类型
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
     * 普通房间
     */
    private void onLiveTypeNormal(final LiveRoomTypeBean bean) {
        DialogUitl.showSimpleDialog(mContext, "温馨提示","确认将房间类型更换为普通房间类型吗？", false,
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
     * 密码房间
     */
    private void onLiveTypePwd(final LiveRoomTypeBean bean) {
        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_set_pwd), DialogUitl.INPUT_TYPE_NUMBER_PASSWORD, 8, new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, final String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.live_set_pwd_empty);
                } else {
                    dialog.dismiss();
                    DialogUitl.showSimpleDialog(mContext, "温馨提示","确认将房间类型更换为密码房间类型吗？", false,
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
     * 付费房间
     */
    private void onLiveTypePay(final LiveRoomTypeBean bean) {
        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_set_fee), DialogUitl.INPUT_TYPE_NUMBER, 8, new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, final String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.live_set_fee_empty);
                } else {
                    dialog.dismiss();
                    DialogUitl.showSimpleDialog(mContext, "温馨提示","确认将房间类型更换为付费房间类型吗？", false,
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
     * 计时房间
     */
    private void onLiveTypeTime(final LiveRoomTypeBean bean) {
        LiveTimeDialogFragment fragment = new LiveTimeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CHECKED_COIN, mLiveTimeCoin);
        fragment.setArguments(bundle);
        fragment.setCommonCallback(new CommonCallback<Integer>() {
            @Override
            public void callback(final Integer coin) {
                DialogUitl.showSimpleDialog(mContext, "温馨提示","确认将房间类型更换为计时房间类型吗？", false,
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
     * 改变直播类型
     */
    private void changeLiveType() {
        LiveHttpUtil.changeLiveType(mLiveType,mLiveTypeVal,mStream,new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show("更换成功");
                    SocketChatUtil.sendChangeLive(((LiveAnchorActivity)mContext).getSocketClient(),mLiveType,mLiveTypeVal);
                }else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    //打开相机前执行
    public void beforeCamera() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.setOpenCamera(true);
        }
    }


    /**
     * 切换镜头
     */
    public void toggleCamera() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.toggleCamera();
        }
    }

    /**
     * 切换闪光灯
     */
    public void toggleFlash() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.toggleFlash();
        }
    }

    /**
     * 设置美颜
     *
     * @param isShow 是否显示
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
            ToastUtil.show("美颜功能暂未开放");
            return;
        }
        if (isShow) {
            mLiveBeautyViewHolder.show();
        } else {
            mLiveBeautyViewHolder.hide();
        }
    }

    /**
     * 美颜返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveBackEvent(LiveBackEvent e) {
        if (mLiveBeautyViewHolder != null && mLiveBeautyViewHolder.isShowed()) {
            mLiveBeautyViewHolder.hide();
        }
    }

    /**
     * 飘心
     */
    public void light() {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.playLightAnim();
        }
    }

    /**
     * 打开音乐窗口
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
     * 关闭背景音乐
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
     * 打开功能弹窗
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
     * 打开主播连麦窗口
     */
    private void openLinkMicAnchorWindow() {
        if (mLiveLinkMicAnchorPresenter != null && !mLiveLinkMicAnchorPresenter.canOpenLinkMicAnchor()) {
            return;
        }
        LiveLinkMicListDialogFragment fragment = new LiveLinkMicListDialogFragment();
        fragment.show(getSupportFragmentManager(), "LiveLinkMicListDialogFragment");
    }


    /**
     * 打开选择游戏窗口
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
     * 关闭游戏
     */
    public void closeGame() {
//        if (mGamePresenter != null) {
//            mGamePresenter.closeGame();
//        }
    }


    /**
     * 开播成功
     *
     * @param data createRoom返回的数据
     */
    public void startLiveSuccess(String data, int liveType, int liveTypeVal) {
        mLiveType = liveType;
        mLiveTypeVal = liveTypeVal;
        //处理createRoom返回的数据
        JSONObject obj = JSON.parseObject(data);
        mStream = obj.getString("stream");
        games = JSON.parseArray(obj.getJSONArray("games").toJSONString(), LiveReadyBean.GoodsBean.class);
        mDanmuPrice = obj.getString("barrage_fee");
        String playUrl = obj.getString("pull");
        L.e("createRoom----播放地址--->" + playUrl);
        mLiveBean.setPull(playUrl);
        mTxAppId = obj.getString("tx_appid");
        cdnSwitch = obj.getIntValue("cdn_switch");
        CommonAppConfig.getInstance().setShowGoods(true);//默认每个开播的直播间都有商品
        //移除开播前的设置控件，添加直播间控件
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

        //连接socket
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

        //开始推流
        if (mLivePushViewHolder != null) {
            mUrl = obj.getString("push");
            pushUrl = obj.getString("push_url");
            if (cdnSwitch == 7) {//本地推流
//                ToastUtil.show("当前为本地推流");
                String s = "?ip=" + (SpUtil.getInstance().getStringValue(SpUtil.FAST_PUSH).replace("rtmp://", "").split(":")[0]);
                if (TextUtils.isEmpty(obj.getString("push_url"))) {
                    mLivePushViewHolder.startPush(obj.getString("push") + s, true);
                } else {
                    mLivePushViewHolder.startPush(CommonAppConfig.getInstance().getFastPush() + obj.getString("push_url") + s, true);
                }
            } else {//阿里云推流
//                ToastUtil.show("当前为阿里云推流");
                mLivePushViewHolder.startPush(obj.getString("push"), true);
            }
//            String s="?ip="+(SpUtil.getInstance().getStringValue(SpUtil.FAST_PUSH).replace("rtmp://","").split(":")[0]);
//            if(TextUtils.isEmpty(obj.getString("push_url"))){
//                mLivePushViewHolder.startPush(obj.getString("push")+s);
//            }else {
//                mLivePushViewHolder.startPush(CommonAppConfig.getInstance().getFastPush()+obj.getString("push_url")+s);
//            }
        }
        //开始显示直播时长
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.startAnchorLiveTime2();
        }
        mStartLive = true;
        mLiveRoomViewHolder.startRefreshUserList();

        //守护相关
        mLiveGuardInfo = new LiveGuardInfo();
        int guardNum = obj.getIntValue("guard_nums");
        mLiveGuardInfo.setGuardNum(guardNum);
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setGuardNum(guardNum);

        }

        //奖池等级
        int giftPrizePoolLevel = obj.getIntValue("jackpot_level");
        if (giftPrizePoolLevel >= 0) {
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.showPrizePoolLevel(String.valueOf(giftPrizePoolLevel));
            }
        }

        //游戏相关
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
     * 直播间已不存在，关闭直播
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
     * 关闭直播
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
     * 结束直播
     */
    public void endLive() {
        //断开socket
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        //请求关播的接口
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
     * 结束直播
     */
    public void endLive2() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        //断开socket
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
     * 收到聊天消息
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
     * 超管关闭直播间
     *
     * @param s
     */
    @Override
    public void onSuperCloseLive(String s) {
        endLive();
        if (mContext != null && !isDestroyed()) {
//            DialogUitl.showSimpleTipDialog(mContext, WordUtil.getString(R.string.live_illegal));
            DialogUitl.showSimpleTipCallDialog(LiveAnchorActivity.this, "下播通知", "因违反以下规定，已被强制下播可重新上播，严重违规可导致封号！\n" + s, new DialogUitl.SimpleCallback() {
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
        DialogUitl.showSimpleTipCallDialog(LiveAnchorActivity.this, "处罚通知", "检测到你在APP内多次使用截屏以及录屏功能，现对你进行封号处罚，如有疑问请与客服联系！", new DialogUitl.SimpleCallback() {
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
     * 打开商品窗口
     */
    public void openShopGameWindow() {
        if (games == null || games.size() == 0) {
            Toast.makeText(mContext, "暂未获取到商品", Toast.LENGTH_SHORT).show();
            return;
        }
        fragmentShop = new LiveShopDialogFragment(games);
        fragmentShop.show(getSupportFragmentManager(), "LiveShopDialogFragment");
        fragmentShop.setLiveGuardInfo();
        //监听此弹窗消失，显示其他弹框
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
     * 打开私密申请列表
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
     * 主播关闭私密互动（一键拒绝所有用户也是这个接口）
     *
     * @type 1 关闭私密互动；2 一键拒绝所有私密互动
     */
    public void closePrivate(final int type) {
        LiveHttpUtil.doClosePrivate(((LiveAnchorActivity) mContext).getStream(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    L.e("私密互动", "获取报名信息成功");
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
     * 点击商品item,影藏底部弹窗
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
     * 主播与主播连麦  主播收到其他主播发过来的
     * 连麦申请
     */
    @Override
    public void onLinkMicAnchorApply(UserBean u, String stream) {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorApply(u, stream);
        }
    }

    /**
     * 主播与主播连麦  对方主播拒绝连麦的回调
     */
    @Override
    public void onLinkMicAnchorRefuse() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorRefuse();
        }
    }

    /**
     * 主播与主播连麦  对方主播无响应的回调
     */
    @Override
    public void onLinkMicAnchorNotResponse() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicNotResponse();
        }
    }

    /**
     * 主播与主播连麦  对方主播正在游戏
     */
    @Override
    public void onlinkMicPlayGaming() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onlinkMicPlayGaming();
        }
    }


    /**
     * 主播与主播连麦  对方主播正在忙的回调
     */
    @Override
    public void onLinkMicAnchorBusy() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorBusy();
        }
    }

    /**
     * 发起主播连麦申请
     *
     * @param pkUid  对方主播的uid
     * @param stream 对方主播的stream
     */
    public void linkMicAnchorApply(final String pkUid, String stream) {
        L.e("连麦:" + stream);
        LiveHttpUtil.livePkCheckLive(pkUid, stream, mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mLiveSDK == Constants.LIVE_SDK_TX) {
                        String playUrl = null;
                        JSONObject obj = JSON.parseObject(info[0]);
                        if (obj != null) {
                            String accUrl = obj.getString("pull");//自己主播的低延时流
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
     * 设置连麦pk按钮是否可见
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
     * 发起主播连麦pk
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
     * 主播与主播PK  主播收到对方主播发过来的PK申请的回调
     *
     * @param u      对方主播的信息
     * @param stream 对方主播的stream
     */
    @Override
    public void onLinkMicPkApply(UserBean u, String stream) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkApply(u, stream);
        }
    }

    /**
     * 主播与主播PK  对方主播拒绝pk的回调
     */
    @Override
    public void onLinkMicPkRefuse() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkRefuse();
        }
    }

    /**
     * 主播与主播PK   对方主播正在忙的回调
     */
    @Override
    public void onLinkMicPkBusy() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkBusy();
        }
    }

    /**
     * 主播与主播PK   对方主播无响应的回调
     */
    @Override
    public void onLinkMicPkNotResponse() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkNotResponse();
        }
    }

    /**
     * 腾讯sdk连麦时候主播混流
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLinkMicTxMixStreamEvent(LinkMicTxMixStreamEvent e) {
//        if (mLivePushViewHolder != null && mLivePushViewHolder instanceof LivePushZegoViewHolder) {
//            ((LivePushZegoViewHolder) mLivePushViewHolder).onLinkMicTxMixStreamEvent(e.getType(), e.getToStream());
//        }
    }

    /**
     * 聊天框是否显示
     *
     * @param isShow
     */
    public void setChatShow(boolean isShow) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setChatShow(isShow);
        }
    }

    /**
     * H5购买成功返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMsgEvent(ShowMsgEvent e) {
        doRedirect(e);
    }

    /**
     * 购买商品后出发发送直播间内消息
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
     * 收到私密互动相关消息
     */
    @Override
    public void onSendPrivateLive(int type, String user) {
        if (mLiveAnchorViewHolder != null) {
            mLiveAnchorViewHolder.onSendPrivateLive(type, user);
        }
    }


    /**
     * 收到切换推拉流消息
     *
     * @param cdn_switch
     */
    @Override
    public void onSendSwitchLive(final int cdn_switch) {
        resetLiveUrl(cdn_switch);
    }

    /**
     * 切换直播推流
     *
     * @param cdn_switch 线路类型
     */
    private void resetLiveUrl(final int cdn_switch) {
        LiveHttpUtil.resetLiveUrl(1, mStream,
                new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (mLivePushViewHolder != null && !TextUtils.isEmpty(info[0])) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                L.e("WOLF","最快推流："+CommonAppConfig.getInstance().getFastPush());
                                cdnSwitch = cdn_switch;
                                mUrl = obj.getString("url");
                                if (cdn_switch != 7) {//阿里云推流
//                                    ToastUtil.show("切换为阿里云推流");
                                    mLivePushViewHolder.stopPush();
                                    showAutoDismissLoading();
                                    mLivePushViewHolder.startPush(obj.getString("url"), false);
                                } else {
//                                    ToastUtil.show("切换为本地推流");
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
     * 自动消失dialog
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
     * 直播端定时请求用户列表，发socket
     */
    public void sendUserList(String userList,String nums) {
        SocketChatUtil.sendUserList(((LiveAnchorActivity)mContext).getSocketClient(), userList,nums);
    }

    /**
     * 直播间超管关闭直播
     */
    @Override
    public void onStopLive() {
        super.onStopLive();
//        ToastUtil.show("超管关闭直播");
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
