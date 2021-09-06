package com.yunbao.main.event;

/**
 * Created by cxf on 2019/3/25.
 */

public class CallEvent {

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public CallEvent(int status) {
        this.status = status;
    }

}
