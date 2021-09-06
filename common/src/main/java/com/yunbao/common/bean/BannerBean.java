package com.yunbao.common.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by cxf on 2019/3/30.
 */

public class BannerBean implements Serializable {
    private String mImageUrl;
    private String mLink;
    private String name;
    private String slide_login_data;
    private int slide_show_type;
    private String slide_target;
    private String jump_type;
    private String is_king;

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

    @JSONField(name = "slide_pic")
    public String getImageUrl() {
        return mImageUrl;
    }

    @JSONField(name = "slide_pic")
    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    @JSONField(name = "slide_url")
    public String getLink() {
        return mLink;
    }

    @JSONField(name = "slide_url")
    public void setLink(String link) {
        mLink = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlide_login_data() {
        return slide_login_data;
    }

    public void setSlide_login_data(String slide_login_data) {
        this.slide_login_data = slide_login_data;
    }

    public int getSlide_show_type() {
        return slide_show_type;
    }

    public void setSlide_show_type(int slide_show_type) {
        this.slide_show_type = slide_show_type;
    }

    public String getSlide_target() {
        return slide_target;
    }

    public void setSlide_target(String slide_target) {
        this.slide_target = slide_target;
    }

    @Override
    public String toString() {
        return "BannerBean{" +
                "mImageUrl='" + mImageUrl + '\'' +
                ", mLink='" + mLink + '\'' +
                ", name='" + name + '\'' +
                ", slide_login_data='" + slide_login_data + '\'' +
                ", slide_show_type=" + slide_show_type +
                ", slide_target='" + slide_target + '\'' +
                ", jump_type='" + jump_type + '\'' +
                ", is_king='" + is_king + '\'' +
                '}';
    }
}
