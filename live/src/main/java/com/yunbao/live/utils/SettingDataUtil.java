package com.yunbao.live.utils;

import im.zego.zegoexpress.constants.ZegoScenario;

import static com.yunbao.live.utils.PreferenceUtil.KEY_APP_ID;
import static com.yunbao.live.utils.PreferenceUtil.KEY_APP_SIGN;
import static com.yunbao.live.utils.PreferenceUtil.KEY_SCENARIO;
import static com.yunbao.live.utils.PreferenceUtil.KEY_TEST_ENVIRONMENT;

public class SettingDataUtil {
    private volatile static SettingDataUtil singleton;

    private SettingDataUtil() {
    }

    public static SettingDataUtil getSingleton() {
        if (singleton == null) {
            synchronized (SettingDataUtil.class) {
                if (singleton == null) {
                    singleton = new SettingDataUtil();
                }
            }
        }
        return singleton;
    }

    public static Long getAppId() {
        try {
            String appidStr = PreferenceUtil.getInstance().getStringValue(KEY_APP_ID, "1");
            return Long.parseLong(appidStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1L;
    }

    public static String getAppKey() {
        return PreferenceUtil.getInstance().getStringValue(KEY_APP_SIGN, "");
    }

    public static Boolean getEnv() {
        return PreferenceUtil.getInstance().getBooleanValue(KEY_TEST_ENVIRONMENT, true);
    }

    public static ZegoScenario getScenario() {
        ZegoScenario scenario = ZegoScenario.GENERAL;
        int a = PreferenceUtil.getInstance().getIntValue(KEY_SCENARIO, 0);
        switch (a) {
            case 0:
                scenario = ZegoScenario.GENERAL;
                break;
            case 1:
                scenario = ZegoScenario.COMMUNICATION;
                break;
            case 2:
                scenario = ZegoScenario.LIVE;
                break;
            default:
                break;
        }
        return scenario;

    }
}
