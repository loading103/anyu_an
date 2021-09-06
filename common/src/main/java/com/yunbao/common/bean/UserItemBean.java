package com.yunbao.common.bean;

/**
 * Created by cxf on 2018/9/28.
 * 我的 页面的item
 */

public class UserItemBean {

    private int id;
    private String name;
    private String thumb;
    private String href;
    private boolean mGroupLast;
    private boolean mAllLast;
    private String is_king;
    private String jump_type;
    private String slide_jump_style;
    private String slide_target;
    private int slide_show_type_button=0;//0-有按钮 1-无按钮

    public int getSlide_show_type_button() {
        return slide_show_type_button;
    }

    public void setSlide_show_type_button(int slide_show_type_button) {
        this.slide_show_type_button = slide_show_type_button;
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

    public String getSlide_jump_style() {
        return slide_jump_style;
    }

    public void setSlide_jump_style(String slide_jump_style) {
        this.slide_jump_style = slide_jump_style;
    }

    public String getSlide_target() {
        return slide_target;
    }

    public void setSlide_target(String slide_target) {
        this.slide_target = slide_target;
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

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public boolean isGroupLast() {
        return mGroupLast;
    }

    public void setGroupLast(boolean groupLast) {
        mGroupLast = groupLast;
    }

    public boolean isAllLast() {
        return mAllLast;
    }

    public void setAllLast(boolean allLast) {
        mAllLast = allLast;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserItemBean bean = (UserItemBean) o;

        if (id != bean.id) return false;
        if (name != null ? !name.equals(bean.name) : bean.name != null) return false;
        if (thumb != null ? !thumb.equals(bean.thumb) : bean.thumb != null) return false;
        return href != null ? href.equals(bean.href) : bean.href == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (thumb != null ? thumb.hashCode() : 0);
        result = 31 * result + (href != null ? href.hashCode() : 0);
        return result;
    }
}
