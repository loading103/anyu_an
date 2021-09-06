package com.yunbao.live.views;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.faceunity.nama.FURenderer;
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

import java.lang.ref.WeakReference;

import static android.content.Context.SENSOR_SERVICE;
import static com.blankj.utilcode.util.ViewUtils.runOnUiThread;


/**
 * Created by cxf on 2018/10/7.
 * 腾讯云直播推流
 */

public class LivePushTxViewHolder extends AbsLivePushViewHolder implements ITXLivePushListener, FURenderer.OnTrackStatusChangedListener, SensorEventListener {

    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;
    private int mMeiBaiVal;//基础美颜 美白
    private int mMoPiVal;//基础美颜 磨皮
    private int mHongRunVal;//基础美颜 红润
    private String mBgmPath;//背景音乐路径
    private Bitmap mFilterBmp;
    private LiveKsyConfigBean mLiveKsyConfigBean;

    private FURenderer mFURenderer;
    private long currentTime;
    private TXPhoneStateListener mPhoneListener;
    private RotationObserver mRotationObserver;
    private SensorManager mSensorManager;

    public LivePushTxViewHolder(Context context, ViewGroup parentView, LiveKsyConfigBean liveKsyConfigBean) {
        super(context, parentView, liveKsyConfigBean);
    }

    @Override
    protected void processArguments(Object... args) {
        mLiveKsyConfigBean = (LiveKsyConfigBean) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_push_tx;
    }

    @Override
    public void init() {
        super.init();
        if (mLiveKsyConfigBean == null) {
            mLiveKsyConfigBean = LiveConfig.getDefaultKsyConfig();
        }
        mLivePusher = new TXLivePusher(mContext);
        mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.setVideoFPS(mLiveKsyConfigBean.getTargetFps());//视频帧率
        mLivePushConfig.setVideoEncodeGop(mLiveKsyConfigBean.getTargetGop());//GOP大小
        mLivePushConfig.setVideoBitrate(mLiveKsyConfigBean.getVideoKBitrate());
        mLivePushConfig.setMaxVideoBitrate(mLiveKsyConfigBean.getVideoKBitrateMax());
        mLivePushConfig.setMinVideoBitrate(mLiveKsyConfigBean.getVideoKBitrateMin());
        mLivePushConfig.setAutoAdjustBitrate(true);
        int resolutionType = TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960;
        switch (mLiveKsyConfigBean.getTargetResolution()) {
            case 1:
                resolutionType = TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640;
                break;
            case 2:
                resolutionType = TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960;
                break;
            case 3:
                resolutionType = TXLiveConstants.VIDEO_RESOLUTION_TYPE_720_1280;
                break;
            case 4:
                resolutionType = TXLiveConstants.VIDEO_RESOLUTION_TYPE_1080_1920;
                break;
        }
        mLivePushConfig.setVideoResolution(resolutionType);
        switch (mLiveKsyConfigBean.getEncodeMethod()) {
            case 1:
                mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_AUTO);//自动
                break;
            case 2:
                mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_HARDWARE);//硬件加速
                break;
            case 3:
                mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
                break;
        }
        Bitmap bitmap = decodeResource(mContext.getResources(), R.mipmap.bg_live_tx_pause);
        mLivePushConfig.setPauseImg(bitmap);
        mLivePushConfig.setTouchFocus(false);//自动对焦
//        mLivePushConfig.setANS(true);//噪声抑制
        mLivePushConfig.enableAEC(true);//开启回声消除：连麦时必须开启，非连麦时不要开启
        mLivePushConfig.setAudioSampleRate(48000);
        mLivePushConfig.setAudioChannels(1);//声道数量
        mLivePushConfig.enableNearestIP(false);//false 不绑定腾讯云，推流到非腾讯云地址
        mLivePusher.setConfig(mLivePushConfig);
        mLivePusher.setMirror(true);
        mLivePusher.setPushListener(this);
        mLivePusher.setBGMVolume(1f);
        mLivePusher.setMicVolume(4f);
        mLivePusher.setBGMNofify(new TXLivePusher.OnBGMNotify() {
            @Override
            public void onBGMStart() {


            }

            @Override
            public void onBGMProgress(long l, long l1) {

            }

            @Override
            public void onBGMComplete(int i) {
                if (!TextUtils.isEmpty(mBgmPath) && mLivePusher != null) {
                    mLivePusher.playBGM(mBgmPath);
                }
            }
        });
        if (CommonAppConfig.getInstance().isTiBeautyEnable()) {
//            mTiSDKManager = TiSDKManager.getInstance();
//            if(mTiSDKManager==null||mLivePusher==null){
//                return;
//            }
//            mLivePusher.setVideoProcessListener(new TXLivePusher.VideoCustomProcessListener() {
//                @Override
//                public int onTextureCustomProcess(int i, int i1, int i2) {
//                    if (mTiSDKManager != null) {
//                        return mTiSDKManager.renderTexture2D(
//                                i,//2D纹理Id
//                                i1,//图像宽度
//                                i2,//图像高度
//                                TiRotation.CLOCKWISE_ROTATION_0,//TiRotation枚举，图像顺时针旋转的角度
//                                false//图像是否左右镜像
//                        );
//                    }
//                    return 0;
//                }
//
//                @Override
//                public void onDetectFacePoints(float[] floats) {
//
//                }
//
//
//                @Override
//                public void onTextureDestoryed() {
//                }
//            });

            mFURenderer = new FURenderer
                    .Builder(mContext)
                    .setInputTextureType(FURenderer.INPUT_TEXTURE_2D)
                    .setOnTrackStatusChangedListener(this)
                    .setCreateMakeup(true)
                    .setCreateBodySlim(true)
                    .setCreateSticker(true)
                    .build();

            // 设置自定义视频处理回调，在主播预览及编码前回调出来，用户可以用来做自定义美颜或者增加视频特效
            mLivePusher.setVideoProcessListener(new TXLivePusher.VideoCustomProcessListener() {
                private boolean mIsFirstFrame = true;

                /**
                 * 在OpenGL线程中回调，在这里可以进行采集图像的二次处理
                 * @param i  纹理ID
                 * @param i1      纹理的宽度
                 * @param i2     纹理的高度
                 * @return 返回给SDK的纹理
                 * 说明：SDK回调出来的纹理类型是GLES20.GL_TEXTURE_2D，接口返回给SDK的纹理类型也必须是GLES20.GL_TEXTURE_2D
                 */
                @Override
                public int onTextureCustomProcess(int i, int i1, int i2) {
                    if (mIsFirstFrame) {
                        Log.d(TAG, "onTextureCustomProcess: texture:" + i + ", width:" + i1 + ", height:" + i2);
                        mFURenderer.onSurfaceCreated();
                        mIsFirstFrame = false;
                        currentTime = System.currentTimeMillis();
                        return 0;
                    }
                    //三星s6总是会出现花屏
                    if (System.currentTimeMillis() - currentTime < 200){
                        mFURenderer.onDrawFrameSingleInput(i, i1, i2);
                    }
                    return mFURenderer.onDrawFrameSingleInput(i, i1, i2);
                }

                /**
                 * 增值版回调人脸坐标
                 * @param floats   归一化人脸坐标，每两个值表示某点P的X,Y值。值域[0.f, 1.f]
                 */
                @Override
                public void onDetectFacePoints(float[] floats) {

                }

                /**
                 * 在OpenGL线程中回调，可以在这里释放创建的OpenGL资源
                 */
                @Override
                public void onTextureDestoryed() {
                    L.e("WOLF","onTextureDestoryed");
                    mFURenderer.onSurfaceDestroyed();
                    mIsFirstFrame = true;
                }
            });
        }

        ((LiveAnchorActivity)mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mPhoneListener = new TXPhoneStateListener(mLivePusher);
        TelephonyManager tm = (TelephonyManager) CommonAppContext.sInstance.getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        mRotationObserver = new RotationObserver(new Handler());
        mRotationObserver.startObserver();

        mPreView = findViewById(R.id.camera_preview);
        mLivePusher.stopCameraPreview(true);
        mLivePusher.startCameraPreview((TXCloudVideoView) mPreView);

        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public FURenderer getFURenderer() {
        return mFURenderer;
    }

//    public DefaultBeautyEffectListener getDefaultEffectListener() {
//        return new DefaultBeautyEffectListener() {
//            @Override
//            public void onFilterChanged(FilterBean bean) {
//                if (bean == null || mLivePusher == null) {
//                    return;
//                }
//                if (mFilterBmp != null) {
//                    mFilterBmp.recycle();
//                }
//                int filterSrc = bean.getFilterSrc();
//                if (filterSrc != 0) {
//                    Bitmap bitmap = BitmapUtil.getInstance().decodeBitmap(filterSrc);
//                    if (bitmap != null) {
//                        mFilterBmp = bitmap;
//                        mLivePusher.setFilter(bitmap);
//                    } else {
//                        mLivePusher.setFilter(null);
//                    }
//                } else {
//                    mLivePusher.setFilter(null);
//                }
//            }
//
//            @Override
//            public void onMeiBaiChanged(int progress) {
//                if (mLivePusher != null) {
//                    int v = progress / 10;
//                    if (mMeiBaiVal != v) {
//                        mMeiBaiVal = v;
//                        mLivePusher.setBeautyFilter(0, mMeiBaiVal, mMoPiVal, mHongRunVal);
//                    }
//                }
//            }
//
//            @Override
//            public void onMoPiChanged(int progress) {
//                if (mLivePusher != null) {
//                    int v = progress / 10;
//                    if (mMoPiVal != v) {
//                        mMoPiVal = v;
//                        mLivePusher.setBeautyFilter(0, mMeiBaiVal, mMoPiVal, mHongRunVal);
//                    }
//                }
//            }
//
//            @Override
//            public void onHongRunChanged(int progress) {
//                if (mLivePusher != null) {
//                    int v = progress / 10;
//                    if (mHongRunVal != v) {
//                        mHongRunVal = v;
//                        mLivePusher.setBeautyFilter(0, mMeiBaiVal, mMoPiVal, mHongRunVal);
//                    }
//                }
//            }
//        };
//    }

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
     * 切换镜头
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
    }

    /**
     * 打开关闭闪光灯
     */
    @Override
    public void toggleFlash() {
        if (mCameraFront) {
            ToastUtil.show(R.string.live_open_flash);
            return;
        }
        if (mLivePusher != null) {
            boolean open = !mFlashOpen;
            if (mLivePusher.turnOnFlashLight(open)) {
                mFlashOpen = open;
            }
        }
    }

    /**
     * 开始推流
     *
     * @param pushUrl 推流地址
     */
    @Override
    public void startPush(String pushUrl, boolean isFirst) {
        L.e("WOLF", "startPush:" + pushUrl);
        if (mLivePusher != null) {
            mLivePusher.startPusher(pushUrl);
        }
        if (isFirst) {
            startCountDown();
        }
    }

    /**
     * 结束推流
     */
    @Override
    public void stopPush() {
        if (mLivePusher != null) {
            mLivePusher.stopPusher();
        }
    }


    @Override
    public void onPause() {
        mPaused = true;
        if (mStartPush && mLivePusher != null) {
            mLivePusher.pauseBGM();
            mLivePusher.pausePusher();
        }
    }

    @Override
    public void onResume() {
        if (mPaused && mStartPush && mLivePusher != null) {
            mLivePusher.resumePusher();
            mLivePusher.resumeBGM();
        }
        mPaused = false;
    }

    @Override
    public void startBgm(String path) {
        if (mLivePusher != null) {
            boolean result = mLivePusher.playBGM(path);
            if (result) {
                mBgmPath = path;
            }
        }
    }

    @Override
    public void pauseBgm() {
        if (mLivePusher != null) {
            mLivePusher.pauseBGM();
        }
    }

    @Override
    public void resumeBgm() {
        if (mLivePusher != null) {
            mLivePusher.resumeBGM();
        }
    }

    @Override
    public void stopBgm() {
        if (mLivePusher != null) {
            mLivePusher.stopBGM();
        }
        mBgmPath = null;
    }

    @Override
    protected void onCameraRestart() {
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
        if(mFURenderer!=null){
            mFURenderer.onSurfaceDestroyed();
            mFURenderer=null;
        }

        mRotationObserver.stopObserver();
        TelephonyManager tm = (TelephonyManager) CommonAppContext.sInstance.getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
        if (null != mSensorManager) {
            mSensorManager.unregisterListener(this);
        }
    }

    /**
     * 重启Bgm
     */
    @Override
    public void reStartBgm() {
        if (!TextUtils.isEmpty(mBgmPath) && mLivePusher != null && ((LiveAnchorActivity) mContext).isBgmPlaying()) {
            mLivePusher.playBGM(mBgmPath);
        }
    }

    @Override
    public void onPushEvent(int e, Bundle bundle) {
        if (e == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {
            ToastUtil.show(R.string.live_push_failed_1);

        } else if (e == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) {
            ToastUtil.show(R.string.live_push_failed_1);

        } else if (e == TXLiveConstants.PUSH_ERR_NET_DISCONNECT || e == TXLiveConstants.PUSH_ERR_INVALID_ADDRESS) {
            L.e(TAG, "网络断开，推流失败------>");
            mStartPush = false;
            if (mLivePushListener != null) {
                mLivePushListener.onPushFailed();
            }
        } else if (e == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            L.e(TAG, "不支持硬件加速------>");
            if (mLivePushConfig != null && mLivePusher != null) {
                mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
                mLivePusher.setConfig(mLivePushConfig);
            }
        } else if (e == TXLiveConstants.PUSH_EVT_FIRST_FRAME_AVAILABLE) {//预览成功
            L.e(TAG, "mStearm--->初始化完毕");
            if (mLivePushListener != null) {
                mLivePushListener.onPreviewStart();
            }
        } else if (e == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {//推流成功
            L.e(TAG, "mStearm--->推流成功");
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
     * 腾讯sdk连麦时候主播混流
     *
     * @param linkMicType 混流类型 1主播与主播连麦  0 用户与主播连麦
     * @param toStream    对方的stream
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
     * 计算混流参数 观众与主播连麦
     *
     * @param txAppId          腾讯云appId
     * @param selfAnchorStream 自己主播的stream
     * @param toStream         对方的stream
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
        JSONObject mainAnchor = new JSONObject();//大主播
        mainAnchor.put("input_stream_id", selfAnchorStream);
        JSONObject mainLayoutParams = new JSONObject();
        mainLayoutParams.put("image_layer", 1);
        mainAnchor.put("layout_params", mainLayoutParams);
        array.add(mainAnchor);

        if (!TextUtils.isEmpty(toStream)) {
            JSONObject smallAnchor = new JSONObject();//小主播
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
     * 计算混流参数 主播与主播连麦
     *
     * @param txAppId          腾讯云appId
     * @param selfAnchorStream 自己主播的stream
     * @param toStream         对方的stream
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

            JSONObject bg = new JSONObject();//背景
            bg.put("input_stream_id", "canvas1");//背景的id,这个字符串随便写
            JSONObject bgLayoutParams = new JSONObject();
            bgLayoutParams.put("image_layer", 1);
            bgLayoutParams.put("input_type", 3);
            bg.put("layout_params", bgLayoutParams);
            array.add(bg);

            JSONObject selfAnchor = new JSONObject();//自己主播
            selfAnchor.put("input_stream_id", selfAnchorStream);
            JSONObject selfLayoutParams = new JSONObject();
            selfLayoutParams.put("image_layer", 2);
            selfLayoutParams.put("image_width", 0.5);
            selfLayoutParams.put("image_height", 0.5);
            selfLayoutParams.put("location_x", 0);
            selfLayoutParams.put("location_y", 0.25);
            selfAnchor.put("layout_params", selfLayoutParams);
            array.add(selfAnchor);

            JSONObject toAnchor = new JSONObject();//对方主播
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
            JSONObject mainAnchor = new JSONObject();//大主播
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
//                ToastUtil.show("没有检测到人脸");
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        if (Math.abs(x) > 3 || Math.abs(y) > 3) {
            if (Math.abs(x) > Math.abs(y)) {
                mFURenderer.onDeviceOrientationChanged(x > 0 ? 270 : 90);
            } else {
                mFURenderer.onDeviceOrientationChanged(y > 0 ? 0 : 180);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    static class TXPhoneStateListener extends PhoneStateListener {
        WeakReference<TXLivePusher> mPusher;

        public TXPhoneStateListener(TXLivePusher pusher) {
            mPusher = new WeakReference<TXLivePusher>(pusher);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            TXLivePusher pusher = mPusher.get();
            switch (state) {
                //电话等待接听
                case TelephonyManager.CALL_STATE_RINGING:
                    if (pusher != null)
                        pusher.pausePusher();
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (pusher != null)
                        pusher.pausePusher();
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (pusher != null)
                        pusher.resumePusher();
                    break;
                default:
            }
        }
    }

    //观察屏幕旋转设置变化，类似于注册动态广播监听变化机制
    private class RotationObserver extends ContentObserver {
        ContentResolver mResolver;

        public RotationObserver(Handler handler) {
            super(handler);
            mResolver = mContext.getContentResolver();
        }

        //屏幕旋转设置改变时调用
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //更新按钮状态
            if (isActivityCanRotation()) {
//                onActivityRotation();
            } else {
                mLivePushConfig.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_DOWN);
                mLivePusher.setRenderRotation(0);
                mLivePusher.setConfig(mLivePushConfig);
            }
        }

        public void startObserver() {
            mResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, this);
        }

        public void stopObserver() {
            mResolver.unregisterContentObserver(this);
        }
    }

    /**
     * 判断Activity是否可旋转。只有在满足以下条件的时候，Activity才是可根据重力感应自动旋转的。
     * 系统“自动旋转”设置项打开；
     *
     * @return false---Activity可根据重力感应自动旋转
     */
    protected boolean isActivityCanRotation() {
        // 判断自动旋转是否打开
        int flag = Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
        if (flag == 0) {
            return false;
        }
        return true;
    }

    /**
     * 获取相机方向
     *
     * @param cameraFacing
     * @return
     */
    public int getCameraOrientation(int cameraFacing) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraId = -1;
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == cameraFacing) {
                cameraId = i;
                break;
            }
        }
        if (cameraId < 0) {
            // no front camera, regard it as back camera
            return 90;
        } else {
            return info.orientation;
        }
    }
}
