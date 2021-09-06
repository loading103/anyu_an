package com.yunbao.live.event;

import com.yunbao.live.bean.LiveMoreBean;

/**
 * Created by cxf on 2019/3/25.
 */

public class LiveOpenDialogEvent {

    private LiveMoreBean.SlideBeanX.SlideBean bean;

    public LiveOpenDialogEvent(LiveMoreBean.SlideBeanX.SlideBean bean) {
        this.bean = bean;
    }

    public LiveMoreBean.SlideBeanX.SlideBean getBean() {
        return bean;
    }
}
