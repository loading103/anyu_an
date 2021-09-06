package com.yunbao.main.event;

import com.yunbao.common.bean.JsWebBean;

/**
 * Created by cxf on 2019/3/25.
 */

public class MainJsEvent {

    private JsWebBean bean;

    public MainJsEvent(JsWebBean bean) {
        this.bean = bean;
    }

    public JsWebBean getBean() {
        return bean;
    }
}
