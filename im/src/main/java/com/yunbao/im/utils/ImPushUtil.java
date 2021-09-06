package com.yunbao.im.utils;

import android.content.Context;

import com.blankj.utilcode.util.SPUtils;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.utils.L;
import com.yunbao.im.event.JPushEvent;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by cxf on 2017/8/3.
 * 极光推送相关
 */

public class ImPushUtil {

    public static final String TAG = "极光推送";
    private static ImPushUtil sInstance;
    private boolean mClickNotification;
    private int mNotificationType;
    private Context context;

    private ImPushUtil() {

    }

    public static ImPushUtil getInstance() {
        if (sInstance == null) {
            synchronized (ImPushUtil.class) {
                if (sInstance == null) {
                    sInstance = new ImPushUtil();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        this.context=context;
    }


    public void logout() {
        stopPush();
    }

    public void resumePush() {
//        if (JPushInterface.isPushStopped(CommonAppContext.sInstance)) {
//            JPushInterface.resumePush(CommonAppContext.sInstance);
//        }
        EventBus.getDefault().post(new JPushEvent("resumePush"));
    }

    public void stopPush() {
//
        EventBus.getDefault().post(new JPushEvent("stopPush"));
    }

    public boolean isClickNotification() {
        return mClickNotification;
    }

    public void setClickNotification(boolean clickNotification) {
        mClickNotification = clickNotification;
    }

    public int getNotificationType() {
        return mNotificationType;
    }

    public void setNotificationType(int notificationType) {
        mNotificationType = notificationType;
    }

    /**
     * 获取极光推送 RegistrationID
     */
    public String getPushID() {
        String pushId = SPUtils.getInstance().getString("reg_id");
        L.e(TAG, "getPushID------>" + pushId);
        return pushId;
    }
}
