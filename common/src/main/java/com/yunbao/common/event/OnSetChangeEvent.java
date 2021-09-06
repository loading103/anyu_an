package com.yunbao.common.event;

import android.content.Context;

/**
 * Created by cxf on 2018/11/1.
 * 钻石数量变化的事件
 */

public class OnSetChangeEvent {

    private String Id;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public OnSetChangeEvent(String id, Context context) {
        Id = id;
        this.context = context;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public OnSetChangeEvent(String id) {
        Id = id;
    }
}
