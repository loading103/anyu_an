package com.yunbao.common.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class ShopRightDbBean implements Serializable {
    private static final long serialVersionUID = 2030555082670563109L;
    @Id(autoincrement = true)
    private Long _id;   //id,表的主键并自增长

    private String  branch_id;

    private String id;

    private String name;

    private String pic;

    private String show_style;

    private String jump_url;

    private String jump_type;

    private boolean choosed;

    private String is_king;

    private String parent_id;

    private String userId;

    private long clickTime;

    private int slide_show_type_button=0;//0-有按钮 1-无按钮

    @Generated(hash = 206069986)
    public ShopRightDbBean(Long _id, String branch_id, String id, String name, String pic, String show_style, String jump_url,
            String jump_type, boolean choosed, String is_king, String parent_id, String userId, long clickTime,
            int slide_show_type_button) {
        this._id = _id;
        this.branch_id = branch_id;
        this.id = id;
        this.name = name;
        this.pic = pic;
        this.show_style = show_style;
        this.jump_url = jump_url;
        this.jump_type = jump_type;
        this.choosed = choosed;
        this.is_king = is_king;
        this.parent_id = parent_id;
        this.userId = userId;
        this.clickTime = clickTime;
        this.slide_show_type_button = slide_show_type_button;
    }

    @Generated(hash = 984524668)
    public ShopRightDbBean() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getBranch_id() {
        return this.branch_id;
    }

    public void setBranch_id(String branch_id) {
        this.branch_id = branch_id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return this.pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getShow_style() {
        return this.show_style;
    }

    public void setShow_style(String show_style) {
        this.show_style = show_style;
    }

    public String getJump_url() {
        return this.jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }

    public String getJump_type() {
        return this.jump_type;
    }

    public void setJump_type(String jump_type) {
        this.jump_type = jump_type;
    }

    public boolean getChoosed() {
        return this.choosed;
    }

    public void setChoosed(boolean choosed) {
        this.choosed = choosed;
    }

    public String getIs_king() {
        return this.is_king;
    }

    public void setIs_king(String is_king) {
        this.is_king = is_king;
    }

    public String getParent_id() {
        return this.parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getClickTime() {
        return this.clickTime;
    }

    public void setClickTime(long clickTime) {
        this.clickTime = clickTime;
    }

    public int getSlide_show_type_button() {
        return slide_show_type_button;
    }

    public void setSlide_show_type_button(int slide_show_type_button) {
        this.slide_show_type_button = slide_show_type_button;
    }
}
