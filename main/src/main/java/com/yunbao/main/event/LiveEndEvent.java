package com.yunbao.main.event;

import com.yunbao.common.bean.LiveBean;

/**
 * Created by cxf on 2019/3/25.
 */

public class LiveEndEvent {

    private LiveBean bean;

    public LiveEndEvent(LiveBean bean) {
        this.bean = bean;
    }

    public LiveBean getBean() {
        return bean;
    }
}
