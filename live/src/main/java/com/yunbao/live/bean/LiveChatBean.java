package com.yunbao.live.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2017/8/22.
 */

public class LiveChatBean {

    public static final int NORMAL = 0;
    public static final int SYSTEM = 1;
    public static final int GIFT = 2;
    public static final int ENTER_ROOM = 3;
    public static final int LIGHT = 4;
    public static final int RED_PACK = 5;
    public static final int SYSTEM_BUTTON = 6;//带按钮的系统消息
    public static final int SYSTEM_GIFT = 7;//礼物系统消息
    public static final int SYSTEM_DANMU = 8;//弹幕系统消息

    private String id;
    private String userNiceName;
    private int level;
    private String content;
    private int heart;
    private int type; //0是普通消息  1是系统消息 2是礼物消息
    private String liangName;
    private int vipType;
    private int guardType;
    private boolean anchor;
    private boolean manager;
    private String url;
    private String jump_type;
    private String slide_show_type;//跳转方式
    private int slide_show_type_button=0;//0-有按钮 1-无按钮
    private String bt_title;//按钮标题
    private String is_king;
    private String follow;
    private String type_head;
    private String effect;
    private String  effect_tex;
    private String  gifticon_mini;
    private String  vip_is_king;
    private String  livePhone;//1 邀请用户
    private String  uid;//  中奖是不是自己
    private String  award_amount;//  中奖金额

    private String jump_type_android;  //安卓跳转路由类型 1-外部链接 2-APP内部路由
    private String jump_url_android;   //安卓跳转URL
    private String jump_type_ios;  //ios跳转路由类型 1-外部链接 2-APP内部路由
    private String jump_url_ios;   //ios跳转URL

    public int getSlide_show_type_button() {
        return slide_show_type_button;
    }

    public void setSlide_show_type_button(int slide_show_type_button) {
        this.slide_show_type_button = slide_show_type_button;
    }

    public String getJump_type_ios() {
        return jump_type_ios;
    }

    public void setJump_type_ios(String jump_type_ios) {
        this.jump_type_ios = jump_type_ios;
    }

    public String getJump_url_ios() {
        return jump_url_ios;
    }

    public void setJump_url_ios(String jump_url_ios) {
        this.jump_url_ios = jump_url_ios;
    }

    public String getJump_type_android() {
        return jump_type_android;
    }

    public void setJump_type_android(String jump_type_android) {
        this.jump_type_android = jump_type_android;
    }

    public String getJump_url_android() {
        return jump_url_android;
    }

    public void setJump_url_android(String jump_url_android) {
        this.jump_url_android = jump_url_android;
    }

    public String getAward_amount() {
        return award_amount;
    }

    public void setAward_amount(String award_amount) {
        this.award_amount = award_amount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLivePhone() {
        return livePhone;
    }

    public void setLivePhone(String livePhone) {
        this.livePhone = livePhone;
    }

    public String getVip_is_king() {
        return vip_is_king;
    }

    public void setVip_is_king(String vip_is_king) {
        this.vip_is_king = vip_is_king;
    }

    public String getGifticon_mini() {
        return gifticon_mini;
    }

    public void setGifticon_mini(String gifticon_mini) {
        this.gifticon_mini = gifticon_mini;
    }

    public String getEffect_tex() {
        return effect_tex;
    }

    public void setEffect_tex(String effect_tex) {
        this.effect_tex = effect_tex;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getType_head() {
        return type_head;
    }

    public void setType_head(String type_head) {
        this.type_head = type_head;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getIs_king() {
        return is_king;
    }

    public void setIs_king(String is_king) {
        this.is_king = is_king;
    }

    public String getJump_type() {
        return jump_type;
    }

    public void setJump_type(String jump_type) {
        this.jump_type = jump_type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSlide_show_type() {
        return slide_show_type;
    }

    public void setSlide_show_type(String slide_show_type) {
        this.slide_show_type = slide_show_type;
    }

    public String getBt_title() {
        return bt_title;
    }

    public void setBt_title(String bt_title) {
        this.bt_title = bt_title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JSONField(name = "user_nicename")
    public String getUserNiceName() {
        return userNiceName;
    }

    @JSONField(name = "user_nicename")
    public void setUserNiceName(String userNiceName) {
        this.userNiceName = userNiceName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @JSONField(name = "liangname")
    public String getLiangName() {
        return liangName;
    }

    @JSONField(name = "liangname")
    public void setLiangName(String liangName) {
        if(!"0".equals(liangName)){
            this.liangName = liangName;
        }
    }

    public boolean isAnchor() {
        return anchor;
    }

    public void setAnchor(boolean anchor) {
        this.anchor = anchor;
    }

    @JSONField(name = "vip_type")
    public int getVipType() {
        return vipType;
    }

    @JSONField(name = "vip_type")
    public void setVipType(int vipType) {
        this.vipType = vipType;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    @JSONField(name = "guard_type")
    public int getGuardType() {
        return guardType;
    }

    @JSONField(name = "guard_type")
    public void setGuardType(int guardType) {
        this.guardType = guardType;
    }
}
