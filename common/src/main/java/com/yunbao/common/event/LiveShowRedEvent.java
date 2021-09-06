package com.yunbao.common.event;

public class LiveShowRedEvent {
    private  boolean isshow;

    public LiveShowRedEvent(boolean isshow) {
        this.isshow = isshow;
    }

    public boolean isIsshow() {
        return isshow;
    }

    public void setIsshow(boolean isshow) {
        this.isshow = isshow;
    }
}
