package com.yunbao.live.event;

/**
 * Created by cxf on 2019/3/25.
 */

public class LivePrivateEvent {

    private int type;
    private int time;

    public LivePrivateEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public LivePrivateEvent(int type, int time) {
        this.type = type;
        this.time = time;
    }
}
