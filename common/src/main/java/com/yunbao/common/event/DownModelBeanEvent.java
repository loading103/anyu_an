package com.yunbao.common.event;

import com.yunbao.common.bean.VideoBean;

/**
 * @author jinxin
 * 剑之所指，心之所向
 * @date 2020/9/26
 */
public class DownModelBeanEvent {
    private VideoBean bean;

    public DownModelBeanEvent(VideoBean bean) {
        this.bean = bean;
    }

    public VideoBean getBean() {
        return bean;
    }

    public void setBean(VideoBean bean) {
        this.bean = bean;
    }
}
