package com.yunbao.main.bean;

/**
 * Created by Administrator on 2020/4/22.
 * Describe:
 */
public class TabBean {
    private String content;
    private boolean isCheck;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public TabBean(String content, boolean isCheck) {
        this.content = content;
        this.isCheck = isCheck;
    }
}
