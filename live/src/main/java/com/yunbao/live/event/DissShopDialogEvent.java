package com.yunbao.live.event;

public class DissShopDialogEvent {
    private boolean setVisible;

    public DissShopDialogEvent(boolean setVisible) {
        this.setVisible = setVisible;
    }

    public DissShopDialogEvent() {
    }

    public boolean isSetVisible() {
        return setVisible;
    }

    public void setSetVisible(boolean setVisible) {
        this.setVisible = setVisible;
    }
}
