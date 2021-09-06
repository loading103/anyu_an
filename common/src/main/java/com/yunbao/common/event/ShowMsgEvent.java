package com.yunbao.common.event;

import com.yunbao.common.bean.MsgButtonBean;

/**
 * Created by cxf on 2018/9/28.
 */

public class ShowMsgEvent {
    private MsgButtonBean bean;

    public ShowMsgEvent(MsgButtonBean bean) {
        this.bean=bean;
    }

    public MsgButtonBean getBean() {
        return bean;
    }

    public void setBean(MsgButtonBean bean) {
        this.bean = bean;
    }
}
