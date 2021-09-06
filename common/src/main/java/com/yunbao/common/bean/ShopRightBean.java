package com.yunbao.common.bean;


import com.yunbao.common.greendao.entity.ShopRightDbBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cxf on 2018/2/2.
 * 排行榜实体类
 *
 *    "branch_id":"8",
 *                         "id":"2",
 *                         "is_hot":"0",
 *                         "is_king":"1",
 *                         "jump_type":"2",
 *                         "jump_url":"/recharge",
 *                         "name":"打算的群二翁奥所",
 *                         "pic":"http://132.232.122.151:8080/group1/system/image/2020/06/19/53801cf11cc020e7a4ed06c132c8c097.png",
 *                         "show_style":"6",
 *                         "sort":
 */

public class ShopRightBean  implements Serializable {
    private String id;
    private String is_hot;
    private String sort;
    private String name;
    private String pic;
    private String show_style;
    private String jump_url;
    private String jump_type;
    private boolean choosed;
    private String is_king;
    private String parent_id;
    private int slide_show_type_button=0;//0-有按钮 1-无按钮
    private List<ShopRightDbBean> children;

    public int getSlide_show_type_button() {
        return slide_show_type_button;
    }

    public void setSlide_show_type_button(int slide_show_type_button) {
        this.slide_show_type_button = slide_show_type_button;
    }

    public String getIs_hot() {
        return is_hot;
    }

    public void setIs_hot(String is_hot) {
        this.is_hot = is_hot;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public List<ShopRightDbBean> getChildren() {
        return children;
    }

    public void setChildren(List<ShopRightDbBean> children) {
        this.children = children;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getShow_style() {
        return show_style;
    }

    public void setShow_style(String show_style) {
        this.show_style = show_style;
    }

    public String getJump_url() {
        return jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }

    public boolean isChoosed() {
        return choosed;
    }

    public void setChoosed(boolean choosed) {
        this.choosed = choosed;
    }
}
