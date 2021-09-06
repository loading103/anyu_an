package com.yunbao.common.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by cxf on 2018/9/25.
 */

public class LiveClassBean implements Serializable {
    protected int id;
    protected String name;
    protected String thumb;
    protected int orderNo;
    private boolean isAll;
    private boolean isMore;
    private String des;
    private boolean checked;
    private String slide_target;
    private String slide_url;
    private String slide_login_data;
    private String jump_type;
    private int slide_show_type;
    private String is_king;
    private int slide_show_type_button=0;//0-有按钮 1-无按钮
    private boolean is_null;

    public int getSlide_show_type_button() {
        return slide_show_type_button;
    }

    public void setSlide_show_type_button(int slide_show_type_button) {
        this.slide_show_type_button = slide_show_type_button;
    }

    public boolean isIs_null() {
        return is_null;
    }

    public void setIs_null(boolean is_null) {
        this.is_null = is_null;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @JSONField(name = "orderno")
    public int getOrderNo() {
        return orderNo;
    }

    @JSONField(name = "orderno")
    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public boolean isAll() {
        return isAll;
    }

    public void setAll(boolean all) {
        isAll = all;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public String getSlide_target() {
        return slide_target;
    }

    public void setSlide_target(String slide_target) {
        this.slide_target = slide_target;
    }

    public String getSlide_url() {
        return slide_url;
    }

    public void setSlide_url(String slide_url) {
        this.slide_url = slide_url;
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
}
