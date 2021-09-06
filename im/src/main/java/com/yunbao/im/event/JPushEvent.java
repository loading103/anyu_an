package com.yunbao.im.event;

/**
 * Created by cxf on 2018/9/28.
 */

public class JPushEvent {
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public JPushEvent(String type) {
        this.type=type;
    }

}
