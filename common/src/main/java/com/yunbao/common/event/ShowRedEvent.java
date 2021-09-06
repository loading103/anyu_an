package com.yunbao.common.event;

/**
 * @author：jianxin 创建时间：2020/9/27
 */
public class ShowRedEvent {
    private boolean showred;

    public boolean isShowred() {
        return showred;
    }

    public void setShowred(boolean showred) {
        this.showred = showred;
    }

    public ShowRedEvent(boolean showred) {
        this.showred = showred;
    }
}
