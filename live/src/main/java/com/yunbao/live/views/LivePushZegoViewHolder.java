package com.yunbao.live.views;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.FlashlightUtils;
import com.faceunity.nama.FURenderer;
import com.faceunity.nama.module.IMakeupModule;
import com.faceunity.nama.utils.CameraUtils;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.utils.BitmapUtil;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.live.LiveConfig;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveActivity;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.bean.LiveKsyConfigBean;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.utils.AppLogger;
import com.yunbao.live.utils.CameraRenderer;
import com.yunbao.live.utils.SettingDataUtil;
import com.yunbao.live.utils.gles.core.GlUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.ZegoMediaPlayer;
import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.callback.IZegoMediaPlayerEventHandler;
import im.zego.zegoexpress.constants.ZegoLanguage;
import im.zego.zegoexpress.constants.ZegoPlayerState;
import im.zego.zegoexpress.constants.ZegoPublisherState;
import im.zego.zegoexpress.constants.ZegoRoomState;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.constants.ZegoVideoBufferType;
import im.zego.zegoexpress.constants.ZegoVideoConfigPreset;
import im.zego.zegoexpress.constants.ZegoVideoFrameFormat;
import im.zego.zegoexpress.constants.ZegoViewMode;
import im.zego.zegoexpress.entity.ZegoCDNConfig;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zegoexpress.entity.ZegoCustomVideoCaptureConfig;
import im.zego.zegoexpress.entity.ZegoEngineConfig;
import im.zego.zegoexpress.entity.ZegoRoomConfig;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zegoexpress.entity.ZegoUser;
import im.zego.zegoexpress.entity.ZegoVideoConfig;
import im.zego.zegoexpress.entity.ZegoVideoFrameParam;

import static com.lzy.okgo.utils.HttpUtils.runOnUiThread;

/**
 * Created by wolf on 2020/11/17.
 * ??????????????????
 */

public class LivePushZegoViewHolder extends AbsLivePushViewHolder implements ITXLivePushListener, FURenderer.OnTrackStatusChangedListener, CameraRenderer.OnRendererStatusListener,SensorEventListener {

    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;
    private int mMeiBaiVal;//???????????? ??????
    private int mMoPiVal;//???????????? ??????
    private int mHongRunVal;//???????????? ??????
    private String mBgmPath;//??????????????????
    private Bitmap mFilterBmp;
    private LiveKsyConfigBean mLiveKsyConfigBean;
    private FURenderer mFURenderer;
    private ZegoExpressEngine engine;
    private String userID;
    private String userName;
    String roomID;
    String publishStreamID;
    String playStreamID;
    private CameraRenderer mCameraRenderer;
    private SensorManager sensorManager;
    private ZegoMediaPlayer mPlayer;

    public LivePushZegoViewHolder(Context context, ViewGroup parentView, LiveKsyConfigBean liveKsyConfigBean) {
        super(context, parentView, liveKsyConfigBean);
    }


    @Override
    protected void processArguments(Object... args) {
        mLiveKsyConfigBean = (LiveKsyConfigBean) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_push_zego;
    }

    @Override
    public void init() {
        super.init();
        if (mLiveKsyConfigBean == null) {
            mLiveKsyConfigBean = LiveConfig.getDefaultKsyConfig();
        }
        mPreView = findViewById(R.id.pre_view);
        /** ???????????? */
        /** Request permission */
        checkOrRequestPermission();
        userID = CommonAppConfig.getInstance().getUid();
        userName = CommonAppConfig.getInstance().getUid();
        roomID = CommonAppConfig.getInstance().getUid();
        publishStreamID = CommonAppConfig.getInstance().getUid();
        initZego();
        login();
        mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.setVideoFPS(mLiveKsyConfigBean.getTargetFps());//????????????
        mLivePushConfig.setVideoEncodeGop(mLiveKsyConfigBean.getTargetGop());//GOP??????
        mLivePushConfig.setVideoBitrate(mLiveKsyConfigBean.getVideoKBitrate());
        mLivePushConfig.setMaxVideoBitrate(mLiveKsyConfigBean.getVideoKBitrateMax());
        mLivePushConfig.setMinVideoBitrate(mLiveKsyConfigBean.getVideoKBitrateMin());
        mLivePushConfig.setAutoAdjustBitrate(true);
        ZegoVideoConfigPreset resolutionType = ZegoVideoConfigPreset.PRESET_540P;
        switch (mLiveKsyConfigBean.getTargetResolution()) {
            case 1:
                resolutionType = ZegoVideoConfigPreset.PRESET_360P;
                break;
            case 2:
                resolutionType = ZegoVideoConfigPreset.PRESET_540P;
                break;
            case 3:
                resolutionType = ZegoVideoConfigPreset.PRESET_720P;
                break;
            case 4:
                resolutionType = ZegoVideoConfigPreset.PRESET_1080P;
                break;
        }
        switch (mLiveKsyConfigBean.getEncodeMethod()) {
            case 1:
                mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_AUTO);//??????
                break;
            case 2:
                mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_HARDWARE);//????????????
                break;
            case 3:
                mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
                break;
        }
        Bitmap bitmap = decodeResource(mContext.getResources(), R.mipmap.bg_live_tx_pause);
        mLivePushConfig.setPauseImg(bitmap);
        mLivePushConfig.setTouchFocus(false);//????????????
//        mLivePushConfig.setANS(true);//????????????
        mLivePushConfig.enableAEC(true);//?????????????????????????????????????????????????????????????????????
        mLivePushConfig.setAudioSampleRate(48000);
        mLivePushConfig.setAudioChannels(1);//????????????
        mLivePushConfig.enableNearestIP(false);//false ????????????????????????????????????????????????

        ZegoVideoConfig videoConfig = new ZegoVideoConfig(resolutionType);
        videoConfig.fps=mLiveKsyConfigBean.getTargetFps();
        videoConfig.bitrate=mLiveKsyConfigBean.getVideoKBitrate();
        engine.setVideoConfig(videoConfig);

//        mLivePusher.setConfig(mLivePushConfig);
//        mLivePusher.setMirror(true);
//        mLivePusher.setPushListener(this);
//        mLivePusher.setBGMVolume(1f);
//        mLivePusher.setMicVolume(4f);
//        mLivePusher.setBGMNofify(new TXLivePusher.OnBGMNotify() {
//            @Override
//            public void onBGMStart() {
//
//
//            }
//
//            @Override
//            public void onBGMProgress(long l, long l1) {
//
//            }
//
//            @Override
//            public void onBGMComplete(int i) {
//                if (!TextUtils.isEmpty(mBgmPath) && mLivePusher != null) {
//                    mLivePusher.playBGM(mBgmPath);
//                }
//            }
//        });
        if (CommonAppConfig.getInstance().isTiBeautyEnable()) {
            mFURenderer = new FURenderer.Builder(mContext)
                    .setInputTextureType(FURenderer.INPUT_TEXTURE_EXTERNAL_OES)
                    .setCameraFacing(FURenderer.CAMERA_FACING_FRONT)
                    .setInputImageOrientation(CameraUtils.getCameraOrientation(FURenderer.CAMERA_FACING_FRONT))
                    .setRunBenchmark(true)
                    .setOnDebugListener(new FURenderer.OnDebugListener() {
                        @Override
                        public void onFpsChanged(double fps, double callTime) {
                            Log.d(TAG, "send buffer onFpsChanged FPS: " + String.format("%.2f", fps) + ", callTime: " + String.format("%.2f", callTime));
                        }
                    })
                    .build();

            // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//            mLivePusher.setVideoProcessListener(new TXLivePusher.VideoCustomProcessListener() {
//                private boolean mIsFirstFrame = true;
//
//                /**
//                 * ???OpenGL??????????????????????????????????????????????????????????????????
//                 * @param i  ??????ID
//                 * @param i1      ???????????????
//                 * @param i2     ???????????????
//                 * @return ?????????SDK?????????
//                 * ?????????SDK??????????????????????????????GLES20.GL_TEXTURE_2D??????????????????SDK???????????????????????????GLES20.GL_TEXTURE_2D
//                 */
//                @Override
//                public int onTextureCustomProcess(int i, int i1, int i2) {
//                    if (mIsFirstFrame) {
//                        mFURenderer.onSurfaceCreated();
//                        mIsFirstFrame = false;
//                    }
//                    return mFURenderer.onDrawFrameSingleInput(i, i1, i2);
//                }
//
//                /**
//                 * ???????????????????????????
//                 * @param floats   ????????????????????????????????????????????????P???X,Y????????????[0.f, 1.f]
//                 */
//                @Override
//                public void onDetectFacePoints(float[] floats) {
//
//                }
//
//                /**
//                 * ???OpenGL????????????????????????????????????????????????OpenGL??????
//                 */
//                @Override
//                public void onTextureDestoryed() {
//                    L.e("WOLF","onTextureDestoryed");
//                    mFURenderer.onSurfaceDestroyed();
//                    mIsFirstFrame = true;
//                }
//            });
        }
        /** ??????????????????????????????????????? */
        /** Start preview and set the local preview view. */
        ZegoCanvas zegoCanvas = new ZegoCanvas(null);
        zegoCanvas.viewMode = ZegoViewMode.SCALE_TO_FILL;
        engine.startPreview(zegoCanvas);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mLivePushListener != null) {
                    mLivePushListener.onPreviewStart();
                }
            }
        },1000);

        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
//        FlashUtils.INSTANCE.init(((LiveAnchorActivity)mContext));
    }

    /**
     * ????????????
     *
     */
    private void login() {
        /** ?????????????????? */
        /** Create user */
        ZegoUser user = new ZegoUser(userID, userName);

        ZegoRoomConfig config = new ZegoRoomConfig();
        /* ??????????????????/?????????????????? */
        /* Enable notification when user login or logout */
        config.isUserStatusNotify = true;

        /** ?????????????????? */
        /** Begin to login room */
        engine.loginRoom(roomID, user, config);
        AppLogger.getInstance().i("Login room OK, userID = " + userID + " , userName = " + userName);
    }

    /**
     * ?????????sdk
     */
    private void initZego() {
        /** ?????????SDK, ??????????????????????????????????????? */
        /** Initialize SDK, use test environment, access to general scenarios */
        ZegoEngineConfig zegoEngineConfig = new ZegoEngineConfig();
        zegoEngineConfig.customVideoCaptureMainConfig = new ZegoCustomVideoCaptureConfig();
        zegoEngineConfig.customVideoCaptureMainConfig.bufferType = ZegoVideoBufferType.RAW_DATA;
        ZegoExpressEngine.setEngineConfig(zegoEngineConfig);
        engine = ZegoExpressEngine.createEngine(SettingDataUtil.getAppId(), SettingDataUtil.getAppKey(), SettingDataUtil.getEnv(), SettingDataUtil.getScenario(), CommonAppContext.sInstance, null);
        AppLogger.getInstance().i("?????????SDK??????");
//        ToastUtil.show("?????????SDK??????");
        engine.setDebugVerbose(true, ZegoLanguage.CHINESE);

        ((GLSurfaceView)mPreView).setEGLContextClientVersion(GlUtil.getSupportGLVersion(mContext));
        mCameraRenderer = new CameraRenderer((LiveAnchorActivity)mContext, ((GLSurfaceView)mPreView), this);
        engine.setCustomVideoCaptureHandler(mCameraRenderer);

        engine.setEventHandler(new IZegoEventHandler() {
            @Override
            public void onRoomStateUpdate(String roomID, ZegoRoomState state, int errorCode, org.json.JSONObject extendedData) {
                /** ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????SDK???????????????????????? */
                /** Room status update callback: after logging into the room, when the room connection status changes
                 * (such as room disconnection, login authentication failure, etc.), the SDK will notify through the callback
                 */
                AppLogger.getInstance().i("onRoomStateUpdate: roomID = " + roomID + ", state = " + state + ", errorCode = " + errorCode);
                if(state==ZegoRoomState.CONNECTING&&errorCode==100250){//??????????????????
                    mStartPush = false;
                    if (mLivePushListener != null) {
                        mLivePushListener.onPushFailed();
                    }
                }
            }


            @Override
            public void onRoomUserUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoUser> userList) {
                /** ???????????????????????????????????????????????????????????????????????????SDK???????????????????????? */
                /** User status is updated. After logging into the room, when a user is added or deleted in the room,
                 * the SDK will notify through this callback
                 */
                AppLogger.getInstance().i("onRoomUserUpdate: roomID = " + roomID + ", updateType = " + updateType);
                for (int i = 0; i < userList.size(); i++) {
                    AppLogger.getInstance().i("userID = " + userList.get(i).userID + ", userName = " + userList.get(i).userName);
                }
            }

            @Override
            public void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoStream> streamList) {
                /** ????????????????????????????????????????????????????????????????????????????????????SDK???????????????????????? */
                /** The stream status is updated. After logging into the room, when there is a new publish or delete of audio and video stream,
                 * the SDK will notify through this callback */
                AppLogger.getInstance().i("onRoomStreamUpdate: roomID = " + roomID + ", updateType = " + updateType);
                for (int i = 0; i < streamList.size(); i++) {
                    AppLogger.getInstance().i("streamID = " + streamList.get(i).streamID);
                }
            }

            @Override
            public void onDebugError(int errorCode, String funcName, String info) {
                /** ???????????????????????? */
                /** Printing debugging error information */
                AppLogger.getInstance().i("onDebugError: errorCode = " + errorCode + ", funcName = " + funcName + ", info = " + info);
            }

            @Override
            public void onPublisherStateUpdate(String streamID, ZegoPublisherState state, int errorCode, org.json.JSONObject extendedData) {
                /** ????????????????????????????????????????????????????????????????????????????????????????????????????????????SDK???????????????????????? */
                /** After calling the stream publishing interface successfully, when the status of the stream changes,
                 * such as the exception of streaming caused by network interruption, the SDK will notify through this callback
                 */
                AppLogger.getInstance().i("onPublisherStateUpdate: streamID = " + streamID + ", state = " + state + ", errCode = " + errorCode);
                if(state==ZegoPublisherState.PUBLISHING){
                    if (!mStartPush) {
                        mStartPush = true;
                        if (mLivePushListener != null) {
                            mLivePushListener.onPushStart();
                        }
                    }
                }
            }

            @Override
            public void onPlayerStateUpdate(String streamID, ZegoPlayerState state, int errorCode, org.json.JSONObject extendedData) {
                /** ????????????????????????????????????????????????????????????????????????????????????????????????????????????SDK???????????????????????? */
                /** After calling the streaming interface successfully, when the status of the stream changes,
                 * such as network interruption leading to abnormal situation, the SDK will notify through
                 * this callback */
                AppLogger.getInstance().i("onPlayerStateUpdate: streamID = " + streamID + ", state = " + state + ", errCode = " + errorCode);
            }
        });
    }

    /** ????????????????????? */
    /** Check and request permission */
    public boolean checkOrRequestPermission() {
        String[] PERMISSIONS_STORAGE = {
                "android.permission.CAMERA",
                "android.permission.RECORD_AUDIO"};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, "android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mContext, "android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED) {
                ((LiveAnchorActivity)mContext).requestPermissions(PERMISSIONS_STORAGE, 101);
                return false;
            }
        }
        return true;
    }

    public FURenderer getFURenderer() {
        return mFURenderer;
    }

    /*public DefaultBeautyEffectListener getDefaultEffectListener() {
        return new DefaultBeautyEffectListener() {
            @Override
            public void onFilterChanged(FilterBean bean) {
                if (bean == null || mLivePusher == null) {
                    return;
                }
                if (mFilterBmp != null) {
                    mFilterBmp.recycle();
                }
                int filterSrc = bean.getFilterSrc();
                if (filterSrc != 0) {
                    Bitmap bitmap = BitmapUtil.getInstance().decodeBitmap(filterSrc);
                    if (bitmap != null) {
                        mFilterBmp = bitmap;
                        mLivePusher.setFilter(bitmap);
                    } else {
                        mLivePusher.setFilter(null);
                    }
                } else {
                    mLivePusher.setFilter(null);
                }
            }

            @Override
            public void onMeiBaiChanged(int progress) {
                if (mLivePusher != null) {
                    int v = progress / 10;
                    if (mMeiBaiVal != v) {
                        mMeiBaiVal = v;
                        mLivePusher.setBeautyFilter(0, mMeiBaiVal, mMoPiVal, mHongRunVal);
                    }
                }
            }

            @Override
            public void onMoPiChanged(int progress) {
                if (mLivePusher != null) {
                    int v = progress / 10;
                    if (mMoPiVal != v) {
                        mMoPiVal = v;
                        mLivePusher.setBeautyFilter(0, mMeiBaiVal, mMoPiVal, mHongRunVal);
                    }
                }
            }

            @Override
            public void onHongRunChanged(int progress) {
                if (mLivePusher != null) {
                    int v = progress / 10;
                    if (mHongRunVal != v) {
                        mHongRunVal = v;
                        mLivePusher.setBeautyFilter(0, mMeiBaiVal, mMoPiVal, mHongRunVal);
                    }
                }
            }
        };
    }
*/
    @Override
    public void changeToLeft() {
        if (mPreView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPreView.getLayoutParams();
            params.width = mPreView.getWidth() / 2;
            params.height = DpUtil.dp2px(250);
            params.topMargin = DpUtil.dp2px(130);
            mPreView.setLayoutParams(params);
        }
    }

    @Override
    public void changeToBig() {
        if (mPreView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPreView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.topMargin = 0;
            mPreView.setLayoutParams(params);
        }
    }


    /**
     * ????????????
     */
    @Override
    public void toggleCamera() {
        if (mLivePusher != null) {
            if (mFlashOpen) {
                toggleFlash();
            }
            mLivePusher.switchCamera();
            mCameraFront = !mCameraFront;
            mLivePusher.setMirror(mCameraFront);
        }

        if(mCameraRenderer!=null){
            if (mFlashOpen) {
                toggleFlash();
            }
            mCameraRenderer.switchCamera();
            mCameraFront = !mCameraFront;
        }
    }

    /**
     * ?????????????????????
     */
    @Override
    public void toggleFlash() {
//        if (mCameraFront) {
//            ToastUtil.show(R.string.live_open_flash);
//            return;
//        }

//        FlashUtils.INSTANCE.init(mContext);
//        boolean open = !mFlashOpen;
//        if(open){
//            FlashUtils.INSTANCE.open();
//        }else {
//            FlashUtils.INSTANCE.close();
//        }
//        mFlashOpen=open;
    }

    /**
     * ????????????
     *
     * @param pushUrl ????????????
     */
    @Override
    public void startPush(String pushUrl, boolean isFirst) {
        if (engine == null) {
            ToastUtil.show("sdk???????????????");
            return;
        }

        L.e("WOLF", "startPush:" + pushUrl);
        ZegoCDNConfig zegoCDNConfig = new ZegoCDNConfig();
        zegoCDNConfig.url = pushUrl;
        zegoCDNConfig.authParam = "";
        /** ????????????CDN??????, ??????????????????????????? **/
        engine.enablePublishDirectToCDN(true, zegoCDNConfig);

        /** ???????????? */
        /** Begin to publish stream */
        engine.startPublishingStream(publishStreamID);

        if (isFirst) {
            startCountDown();
        }
    }

    /**
     * ????????????
     */
    @Override
    public void stopPush() {
        if (engine == null) {
            ToastUtil.show("sdk???????????????");
            return;
        }

        engine.stopPlayingStream(publishStreamID);
    }


    @Override
    public void onPause() {
        mPaused = true;
        if (mStartPush && mLivePusher != null) {
            mLivePusher.pauseBGM();
            mLivePusher.pausePusher();
        }

        mCameraRenderer.onPause();
        //?????????????????????
        sensorManager.unregisterListener(this);
        L.e("WOLF","onPause");
    }

    @Override
    public void onResume() {
        if (mPaused && mStartPush && mLivePusher != null) {
            mLivePusher.resumePusher();
            mLivePusher.resumeBGM();
        }
        mPaused = false;
        //????????????????????????,?????????????????????FURenderer
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mCameraRenderer.onResume();

        L.e("WOLF","onResume");
    }

    @Override
    public void startBgm(String path) {
        if (mLivePusher != null) {
            boolean result = mLivePusher.playBGM(path);
            if (result) {
                mBgmPath = path;
            }
        }
        if(engine!=null){
            if(mPlayer==null){
                mPlayer = engine.createMediaPlayer();
            }
            mPlayer.loadResource(path,null);
            mPlayer.setEventHandler(new IZegoMediaPlayerEventHandler() {
                @Override
                public void onMediaPlayerPlayingProgress(ZegoMediaPlayer mediaPlayer, long millisecond) {
                    super.onMediaPlayerPlayingProgress(mediaPlayer, millisecond);
                    L.e("WOLF","???????????????"+millisecond);
                }
            });
            mPlayer.enableAux(true);
            mPlayer.enableRepeat(true);
            mPlayer.start();
            mBgmPath = path;
        }
    }

    @Override
    public void pauseBgm() {
        if (mLivePusher != null) {
            mLivePusher.pauseBGM();
        }
        if(mPlayer!=null){
            mPlayer.pause();
        }
    }

    @Override
    public void resumeBgm() {
        if (mLivePusher != null) {
            mLivePusher.resumeBGM();
        }
        if(mPlayer!=null){
            mPlayer.resume();
        }
    }

    @Override
    public void stopBgm() {
        if (mLivePusher != null) {
            mLivePusher.stopBGM();
        }
        if(mPlayer!=null){
            mPlayer.stop();
        }
        mBgmPath = null;
    }

    @Override
    protected void onCameraRestart() {
        L.e("WOLF","onCameraRestart");
        if (mLivePusher != null && mPreView != null) {
            mLivePusher.stopCameraPreview(true);
            mLivePusher.startCameraPreview((TXCloudVideoView) mPreView);
        }
    }

    @Override
    public void release() {
        super.release();
        LiveHttpUtil.cancel(LiveHttpConsts.LINK_MIC_TX_MIX_STREAM);
        if (mLivePusher != null) {
            mLivePusher.stopBGM();
            mLivePusher.stopPusher();
            mLivePusher.stopScreenCapture();
            mLivePusher.stopCameraPreview(false);
            mLivePusher.setVideoProcessListener(null);
            mLivePusher.setBGMNofify(null);
            mLivePusher.setPushListener(null);
        }
        mLivePusher = null;
        if (mLivePushConfig != null) {
            mLivePushConfig.setPauseImg(null);
        }
        mLivePushConfig = null;
        ZegoExpressEngine.setEngineConfig(null);
        engine.logoutRoom(roomID);
        ZegoExpressEngine.destroyEngine(null);
//        engine = null;
        AppLogger.getInstance().i("??????SDK??????");
//        FlashUtils.INSTANCE.close();
    }

    /**
     *
     * ??????Bgm
     */

    @Override
    public void reStartBgm() {
        if (!TextUtils.isEmpty(mBgmPath) && mLivePusher != null && ((LiveAnchorActivity) mContext).isBgmPlaying()) {
            mLivePusher.playBGM(mBgmPath);
        }

        if (!TextUtils.isEmpty(mBgmPath) && mPlayer != null && ((LiveAnchorActivity) mContext).isBgmPlaying()) {
            if(engine!=null){
                if(mPlayer==null){
                    mPlayer = engine.createMediaPlayer();
                }
                mPlayer.loadResource(mBgmPath,null);
                mPlayer.setEventHandler(new IZegoMediaPlayerEventHandler() {
                    @Override
                    public void onMediaPlayerPlayingProgress(ZegoMediaPlayer mediaPlayer, long millisecond) {
                        super.onMediaPlayerPlayingProgress(mediaPlayer, millisecond);
                        L.e("WOLF","???????????????"+millisecond);
                    }
                });
                mPlayer.enableAux(true);
                mPlayer.enableRepeat(true);
                mPlayer.start();
            }
        }
    }

    @Override
    public void onPushEvent(int e, Bundle bundle) {
        if (e == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {
            ToastUtil.show(R.string.live_push_failed_1);

        } else if (e == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) {
            ToastUtil.show(R.string.live_push_failed_1);

        } else if (e == TXLiveConstants.PUSH_ERR_NET_DISCONNECT || e == TXLiveConstants.PUSH_ERR_INVALID_ADDRESS) {
            L.e(TAG, "???????????????????????????------>");
            mStartPush = false;
            if (mLivePushListener != null) {
                mLivePushListener.onPushFailed();
            }
        } else if (e == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            L.e(TAG, "?????????????????????------>");
            if (mLivePushConfig != null && mLivePusher != null) {
                mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
                mLivePusher.setConfig(mLivePushConfig);
            }
        } else if (e == TXLiveConstants.PUSH_EVT_FIRST_FRAME_AVAILABLE) {//????????????
            L.e(TAG, "mStearm--->???????????????");
            if (mLivePushListener != null) {
                mLivePushListener.onPreviewStart();
            }
        } else if (e == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {//????????????
            L.e(TAG, "mStearm--->????????????");
            if (!mStartPush) {
                mStartPush = true;
                if (mLivePushListener != null) {
                    mLivePushListener.onPushStart();
                }
            }
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    /**
     * ??????sdk????????????????????????
     *
     * @param linkMicType ???????????? 1?????????????????????  0 ?????????????????????
     * @param toStream    ?????????stream
     */
    public void onLinkMicTxMixStreamEvent(int linkMicType, String toStream) {
        String txAppId = getTxAppId();
        String selfAnchorStream = getLiveStream();
        if (TextUtils.isEmpty(txAppId) || TextUtils.isEmpty(selfAnchorStream)) {
            return;
        }
        String mixParams = null;
        if (linkMicType == Constants.LINK_MIC_TYPE_NORMAL) {
            mixParams = createMixParams(txAppId, selfAnchorStream, toStream);
        } else if (linkMicType == Constants.LINK_MIC_TYPE_ANCHOR) {
            mixParams = createMixParams2(txAppId, selfAnchorStream, toStream);
        }
        if (TextUtils.isEmpty(mixParams)) {
            return;
        }
        LiveHttpUtil.linkMicTxMixStream(mixParams);
    }


    /**
     * ?????????????????? ?????????????????????
     *
     * @param txAppId          ?????????appId
     * @param selfAnchorStream ???????????????stream
     * @param toStream         ?????????stream
     * @return
     */
    private String createMixParams(String txAppId, String selfAnchorStream, String toStream) {
        JSONObject obj = new JSONObject();
        long timestamp = System.currentTimeMillis() / 1000;
        obj.put("timestamp", timestamp);
        obj.put("eventId", timestamp);
        JSONObject interFace = new JSONObject();
        interFace.put("interfaceName", "Mix_StreamV2");
        JSONObject para = new JSONObject();
        para.put("app_id", txAppId);
        para.put("interface", "mix_streamv2.start_mix_stream_advanced");
        para.put("mix_stream_session_id", selfAnchorStream);
        para.put("output_stream_id", selfAnchorStream);
        JSONArray array = new JSONArray();
        JSONObject mainAnchor = new JSONObject();//?????????
        mainAnchor.put("input_stream_id", selfAnchorStream);
        JSONObject mainLayoutParams = new JSONObject();
        mainLayoutParams.put("image_layer", 1);
        mainAnchor.put("layout_params", mainLayoutParams);
        array.add(mainAnchor);

        if (!TextUtils.isEmpty(toStream)) {
            JSONObject smallAnchor = new JSONObject();//?????????
            smallAnchor.put("input_stream_id", toStream);
            JSONObject smallLayoutParams = new JSONObject();
            smallLayoutParams.put("image_layer", 2);
            smallLayoutParams.put("image_width", 0.25);
            smallLayoutParams.put("image_height", 0.21);
            smallLayoutParams.put("location_x", 0.75);
            smallLayoutParams.put("location_y", 0.6);
            smallAnchor.put("layout_params", smallLayoutParams);
            array.add(smallAnchor);
        }

        para.put("input_stream_list", array);
        interFace.put("para", para);
        obj.put("interface", interFace);
        return obj.toString();
    }


    /**
     * ?????????????????? ?????????????????????
     *
     * @param txAppId          ?????????appId
     * @param selfAnchorStream ???????????????stream
     * @param toStream         ?????????stream
     * @return
     */
    private String createMixParams2(String txAppId, String selfAnchorStream, String toStream) {
        JSONObject obj = new JSONObject();
        long timestamp = System.currentTimeMillis() / 1000;
        obj.put("timestamp", timestamp);
        obj.put("eventId", timestamp);
        JSONObject interFace = new JSONObject();
        interFace.put("interfaceName", "Mix_StreamV2");
        JSONObject para = new JSONObject();
        para.put("app_id", txAppId);
        para.put("interface", "mix_streamv2.start_mix_stream_advanced");
        para.put("mix_stream_session_id", selfAnchorStream);
        para.put("output_stream_id", selfAnchorStream);
        JSONArray array = new JSONArray();


        if (!TextUtils.isEmpty(toStream)) {

            JSONObject bg = new JSONObject();//??????
            bg.put("input_stream_id", "canvas1");//?????????id,????????????????????????
            JSONObject bgLayoutParams = new JSONObject();
            bgLayoutParams.put("image_layer", 1);
            bgLayoutParams.put("input_type", 3);
            bg.put("layout_params", bgLayoutParams);
            array.add(bg);

            JSONObject selfAnchor = new JSONObject();//????????????
            selfAnchor.put("input_stream_id", selfAnchorStream);
            JSONObject selfLayoutParams = new JSONObject();
            selfLayoutParams.put("image_layer", 2);
            selfLayoutParams.put("image_width", 0.5);
            selfLayoutParams.put("image_height", 0.5);
            selfLayoutParams.put("location_x", 0);
            selfLayoutParams.put("location_y", 0.25);
            selfAnchor.put("layout_params", selfLayoutParams);
            array.add(selfAnchor);

            JSONObject toAnchor = new JSONObject();//????????????
            toAnchor.put("input_stream_id", toStream);
            JSONObject toLayoutParams = new JSONObject();
            toLayoutParams.put("image_layer", 3);
            toLayoutParams.put("image_width", 0.5);
            toLayoutParams.put("image_height", 0.5);
            toLayoutParams.put("location_x", 0.5);
            toLayoutParams.put("location_y", 0.25);
            toAnchor.put("layout_params", toLayoutParams);
            array.add(toAnchor);
        } else {
            JSONObject mainAnchor = new JSONObject();//?????????
            mainAnchor.put("input_stream_id", selfAnchorStream);
            JSONObject mainLayoutParams = new JSONObject();
            mainLayoutParams.put("image_layer", 1);
            mainAnchor.put("layout_params", mainLayoutParams);
            array.add(mainAnchor);
        }

        para.put("input_stream_list", array);
        interFace.put("para", para);
        obj.put("interface", interFace);
        return obj.toString();
    }

    private String getLiveStream() {
        return ((LiveActivity) mContext).getStream();
    }

    private String getTxAppId() {
        return ((LiveActivity) mContext).getTxAppId();
    }

    @Override
    public void onTrackStatusChanged(int type, final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                mTextView.setVisibility(status > 0 ? View.INVISIBLE : View.VISIBLE);
//                ToastUtil.show("?????????????????????");
            }
        });

    }

    @Override
    public void onSurfaceCreated() {
        mFURenderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(int viewWidth, int viewHeight) {

    }

    private byte[] readBack;
    private ByteBuffer byteBuffer;
    @Override
    public int onDrawFrame(byte[] nv21Byte, int texId, int cameraWidth, int cameraHeight, int cameraRotation, float[] mvpMatrix, float[] texMatrix, long timeStamp) {
        if (null == readBack) {
            readBack = new byte[nv21Byte.length];
        }
        int tex2D = mFURenderer.onDrawFrameDualInput(nv21Byte, texId, cameraWidth, cameraHeight, readBack, cameraWidth, cameraHeight);
        // ?????????????????????????????????VideoCaptureFormat
        // Constructing VideoCaptureFormat using captured video frame information
        ZegoVideoFrameParam param = new ZegoVideoFrameParam();
        param.width = cameraWidth;
        param.height = cameraHeight;
        param.strides[0] = cameraWidth;
        param.strides[1] = cameraWidth;
        param.format = ZegoVideoFrameFormat.NV21;
        param.rotation = cameraRotation;
        long now; //?????????????????? surfaceTexture ???????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            now = SystemClock.elapsedRealtime();
        } else {
            now = TimeUnit.MILLISECONDS.toMillis(SystemClock.elapsedRealtime());
        }
        // ????????????????????????ZEGO SDK
        // Pass the collected data to ZEGO SDK
        if (byteBuffer == null) {
            byteBuffer = ByteBuffer.allocateDirect(readBack.length);
        }
        byteBuffer.put(readBack);
        byteBuffer.flip();
        engine.sendCustomVideoCaptureRawData(byteBuffer, byteBuffer.limit(), param, now);
        return tex2D;
    }

    @Override
    public void onSurfaceDestroy() {
        mFURenderer.onSurfaceDestroyed();
    }

    @Override
    public void onCameraChanged(int cameraFacing, int cameraOrientation) {
        mFURenderer.onCameraChanged(cameraFacing, cameraOrientation);
        IMakeupModule makeupModule = mFURenderer.getMakeupModule();
        if (makeupModule != null) {
            boolean isBack = cameraFacing == FURenderer.CAMERA_FACING_BACK;
            makeupModule.setIsMakeupFlipPoints(isBack ? 1 : 0);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && null != mFURenderer) {
            float x = event.values[0];
            float y = event.values[1];
            if (Math.abs(x) > 3 || Math.abs(y) > 3) {
                if (Math.abs(x) > Math.abs(y)) {
                    mFURenderer.onDeviceOrientationChanged(x > 0 ? 0 : 180);
                } else {
                    mFURenderer.onDeviceOrientationChanged(y > 0 ? 90 : 270);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
