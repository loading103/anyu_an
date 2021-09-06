package com.yunbao.common.bean;

import android.view.View;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2018/10/12.
 */

public class LiveGiftBean {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_DELUXE = 1;
    public static final int MARK_NORMAL = 0;
    public static final int MARK_HOT = 1;
    public static final int MARK_GUARD = 2;
    public static final int MARK_LUCK = 3;

    private int id;
    private int type;//0 普通礼物 1是豪华礼物
    private int mark;// 0 普通  1热门  2守护 3幸运
    private String name;
    private String price;
    private String icon;
    private String gifticon_mini;
    private String url;
    private boolean checked;
    private int page;
    private int num=0;//礼物数量
    private int only_danmu;//0 弹幕和聊天框一起发 1只发弹幕 2 只发聊天框
    private View mView;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOnly_danmu() {
        return only_danmu;
    }

    public void setOnly_danmu(int only_danmu) {
        this.only_danmu = only_danmu;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getGifticon_mini() {
        return gifticon_mini;
    }

    public void setGifticon_mini(String gifticon_mini) {
        this.gifticon_mini = gifticon_mini;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    @JSONField(name = "giftname")
    public String getName() {
        return name;
    }

    @JSONField(name = "giftname")
    public void setName(String name) {
        this.name = name;
    }

    @JSONField(name = "needcoin")
    public String getPrice() {
        return price;
    }

    @JSONField(name = "needcoin")
    public void setPrice(String price) {
        this.price = price;
    }

    @JSONField(name = "gifticon")
    public String getIcon() {
        return icon;
    }

    @JSONField(name = "gifticon")
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }
}
