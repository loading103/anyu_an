package com.yunbao.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.yunbao.common.CommonAppContext;

import java.util.Map;

/**
 * Created by cxf on 2018/9/17.
 * SharedPreferences 封装
 */

public class SpUtil {



    private static SpUtil sInstance;
    private SharedPreferences mSharedPreferences;

    public static final String UID = "uid";
    public static final String TOKEN = "token";
    public static final String USER_INFO = "userInfo";
    public static final String CONFIG = "config";
    public static final String IM_LOGIN = "jimLogin";
    public static final String HAS_SYSTEM_MSG = "hasSystemMsg";
    public static final String LOCATION_LNG = "locationLng";
    public static final String LOCATION_LAT = "locationLat";
    public static final String LOCATION_PROVINCE = "locationProvince";
    public static final String LOCATION_CITY = "locationCity";
    public static final String LOCATION_DISTRICT = "locationDistrict";
    public static final String TI_BEAUTY_ENABLE = "tiBeautyEnable";
    public static final String ALL_URL = "getLivePath";
    public static final String FAST_URL = "fastUrl";
    public static final String FAST_PULL = "fastPull";
    public static final String FAST_PUSH = "fastPush";
    public static final String FAST_HOME_SLIDE = "shouyeslide";
    public static final String FAST_HOME_LIST = "shouyeslidelist";
    public static final String FAST_HME_LIST = "shouyemelist";
    public static final String FAST_TABBAR_LIST = "tabbarlist";
    public static final String VOICESET = "voiceset";
    public static final String SHOPONE = "shop_one";
    public static final String SHOPTWO = "shop_two";
    public static final String SHOW_GOODS = "shop_goods";
    public static final String HAVEGOODS = "havegood";
    public static final String GET_ADVERT = "get_advert";
    public static final String HOT_LIST = "hot_list";
    public static final String AGENT_URL = "agent_url";
    public static final String AGENT_PIC = "agent_pic";
    public static final String DUAN_XIN = "captcha_switch";
    public static final String SLIDE_UP = "can_slide_up";
    public static final String INVITE_MEMBER = "invite_number";
    public static final String NEW_USER = "new_user";
    public static final String NEW_USER_CALL = "new_user_call";
    public static final String REFRESH_TOP = "refresh_top";
    public static final String SHOP_NAME = "top_title";
    public static final String VIDEO_NAME = "video_title";
    public static final String VIDEO_SUCCESS ="video_success" ;
    public static final String END_LIVE ="end_liveed" ;
    public static final String CUSTOM_TOKEN ="custom_token" ;
    public static final String CUSTOM_UID ="custom_uid" ;
    public static final String ZHANG_HAO ="zhang_hao" ;
    public static final String IS_SUPER = "is_super";
    public static final String VIP_TOKEN = "vip_token";
    public static final String VIDEO_XIEYI = "video_xieyi";
    public static final String RECHARGE_XIEYI = "recharge_xieyi";
    private SpUtil() {
        mSharedPreferences = CommonAppContext.sInstance.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
    }

    public static SpUtil getInstance() {
        if (sInstance == null) {
            synchronized (SpUtil.class) {
                if (sInstance == null) {
                    sInstance = new SpUtil();
                }
            }
        }
        return sInstance;
    }

    /**
     * 保存一个字符串
     */
    public void setStringValue(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 获取一个字符串
     */
    public String getStringValue(String key) {
        return mSharedPreferences.getString(key, "");
    }

    /**
     * 保存多个字符串
     */
    public void setMultiStringValue(Map<String, String> pairs) {
        if (pairs == null || pairs.size() == 0) {
            return;
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (Map.Entry<String, String> entry : pairs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                editor.putString(key, value);
            }
        }
        editor.apply();
    }

    /**
     * 获取多个字符串
     */
    public String[] getMultiStringValue(String... keys) {
        if (keys == null || keys.length == 0) {
            return null;
        }
        int length = keys.length;
        String[] result = new String[length];
        for (int i = 0; i < length; i++) {
            String temp = "";
            if (!TextUtils.isEmpty(keys[i])) {
                temp = mSharedPreferences.getString(keys[i], "");
            }
            result[i] = temp;
        }
        return result;
    }


    /**
     * 保存一个布尔值
     */
    public void setBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 获取一个布尔值
     */
    public boolean getBooleanValue(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    /**
     * 保存多个布尔值
     */
    public void setMultiBooleanValue(Map<String, Boolean> pairs) {
        if (pairs == null || pairs.size() == 0) {
            return;
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (Map.Entry<String, Boolean> entry : pairs.entrySet()) {
            String key = entry.getKey();
            Boolean value = entry.getValue();
            if (!TextUtils.isEmpty(key)) {
                editor.putBoolean(key, value);
            }
        }
        editor.apply();
    }

    /**
     * 获取多个布尔值
     */
    public boolean[] getMultiBooleanValue(String[] keys) {
        if (keys == null || keys.length == 0) {
            return null;
        }
        int length = keys.length;
        boolean[] result = new boolean[length];
        for (int i = 0; i < length; i++) {
            boolean temp = false;
            if (!TextUtils.isEmpty(keys[i])) {
                temp = mSharedPreferences.getBoolean(keys[i], false);
            }
            result[i] = temp;
        }
        return result;
    }


    public void removeValue(String... keys) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (String key : keys) {
            editor.remove(key);
        }
        editor.apply();
    }

}
