package com.yunbao.phonelive;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SPUtils;
import com.faceunity.nama.FURenderer;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.util.FileDownloadLog;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
//import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.rtmp.TXLiveBase;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.utils.L;
import com.yunbao.im.event.JPushEvent;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.im.utils.ImPushUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by cxf on 2017/8/3.
 */

public class AppContext extends CommonAppContext {

    public static AppContext sInstance;
    private boolean mBeautyInited;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //腾讯云鉴权url
        String ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/eeccdd8befec816b56b7fbaa7d56b214/TXLiveSDK.licence";
        //腾讯云鉴权key
        String ugcKey = "16a17f1305a538cf7adea21827cef7bc";
        TXLiveBase.getInstance().setLicence(this, ugcLicenceUrl, ugcKey);
        L.setDeBug(BuildConfig.DEBUG);
        //初始化腾讯bugly
        CrashReport.initCrashReport(this);
        CrashReport.setAppVersion(this, CommonAppConfig.getInstance().getVersion());
        //初始化ShareSdk
//        MobSDK.init(this);
        //初始化极光推送
        JPushInterface.setDebugMode(true); 	// 调试时设置为true,发布时设置为false
        JPushInterface.init(sInstance);
        SPUtils.getInstance().put("reg_id",JPushInterface.getRegistrationID(this));
        L.e("pushID:"+JPushInterface.getRegistrationID(this));
        ImPushUtil.getInstance().init(this);
        //初始化极光IM
        ImMessageUtil.getInstance().init();


        //初始化 ARouter
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
        initDownLoad();

//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            LeakCanary.install(this);
//        }

        EventBus.getDefault().register(this);
        FURenderer.setup(this);//美颜
    }

    private void initDownLoad() {
        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15000) // set connection timeout.
                        .readTimeout(15000) // set read timeout.
                ))
                .commit();

    }

    /**
     * 初始化萌颜
     */
    public void initBeautySdk(String beautyKey) {
        if(beautyNeed.equals("1")){
            if(!TextUtils.isEmpty(beautyKey)){
                if (!mBeautyInited) {
                    mBeautyInited = true;
//                    TiSDK.init(beautyKey, this);
                    CommonAppConfig.getInstance().setTiBeautyEnable(true);
                    L.e("萌颜初始化------->");
                }else {
                    CommonAppConfig.getInstance().setTiBeautyEnable(true);
                }

            }else{
                CommonAppConfig.getInstance().setTiBeautyEnable(true);
            }
        }else {
            if(!TextUtils.isEmpty(beautyKey)){
                if (!mBeautyInited) {
                    mBeautyInited = true;
                    CommonAppConfig.getInstance().setTiBeautyEnable(false);
                    L.e("萌颜初始化------->");
                }else {
                    CommonAppConfig.getInstance().setTiBeautyEnable(false);
                }

            }else{
                CommonAppConfig.getInstance().setTiBeautyEnable(false);
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void JPushEvent(JPushEvent e) {
        if(e.getType().equals("stopPush")){
            L.e("TAG", "stopPush------>");
            JPushInterface.stopPush(this);
        }else {
            L.e("TAG", "resumePush------>");
            if (JPushInterface.isPushStopped(this)) {
            JPushInterface.resumePush(this);
        }
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        fixTimeoutException();
        fixTimeoutException10();
    }

    /**
     * 修复OPPO 5.0-6.0 TimeoutException异常
     */
    private void fixTimeoutException() {
        if (Build.BRAND.equalsIgnoreCase("oppo") && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            try {
                Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");

                Method method = clazz.getSuperclass().getDeclaredMethod("stop");
                method.setAccessible(true);

                Field field = clazz.getDeclaredField("INSTANCE");
                field.setAccessible(true);

                method.invoke(field.get(null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修复TimeoutException异常(通用)
     */
    private void fixTimeoutException10() {
        final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (t.getName().equals("FinalizerWatchdogDaemon") && e instanceof TimeoutException) {
                Log.e("ignore", "ignore");
            } else {
                defaultUncaughtExceptionHandler.uncaughtException(t, e);
            }
        });
    }
}
