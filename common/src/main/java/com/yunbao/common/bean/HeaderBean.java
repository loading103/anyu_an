package com.yunbao.common.bean;

/**
 * Created by cxf on 2017/9/21.
 */

public class HeaderBean {

    private int id;
    private boolean isCheck;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public HeaderBean(int id, boolean isCheck) {
        this.id = id;
        this.isCheck = isCheck;
    }
}
