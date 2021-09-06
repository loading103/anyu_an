package com.yunbao.live.bean;

/**
 * Created by Administrator on 2020/8/4.
 * Describe:
 */
public class LiveContactBean {
    private String type;
    private String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LiveContactBean(String type, String content) {
        this.type = type;
        this.content = content;
    }
}
