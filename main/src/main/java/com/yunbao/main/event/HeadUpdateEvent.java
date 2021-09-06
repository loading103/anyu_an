package com.yunbao.main.event;

/**
 * Created by cxf on 2019/3/25.
 */

public class HeadUpdateEvent {

    private String url;

    public String getUrl() {
        return url;
    }

    public HeadUpdateEvent(String url) {
        this.url = url;
    }
}
