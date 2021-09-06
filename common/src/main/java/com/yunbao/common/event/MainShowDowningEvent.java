package com.yunbao.common.event;

/**
 * Created by cxf on 2019/3/25.
 */

public class MainShowDowningEvent {
    private  boolean  isshow;

    public MainShowDowningEvent(boolean isshow) {
        this.isshow = isshow;
    }

    public boolean isIsshow() {
        return isshow;
    }

    public void setIsshow(boolean isshow) {
        this.isshow = isshow;
    }
}
