package com.yunbao.common.event;

/**
 * Created by cxf on 2019/3/25.
 */

public class LiveShowBottomEvent {

    private boolean mIsShow;

    public LiveShowBottomEvent(boolean isShow) {
        mIsShow = isShow;
    }

    public boolean isShow() {
        return mIsShow;
    }
}
