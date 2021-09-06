package com.yunbao.common.event;

import com.yunbao.common.bean.LiveBean;

/**
 * Created by cxf on 2019/3/25.
 */

public class DialogBottomEvent {

    private int height;

    public DialogBottomEvent(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
