package com.yunbao.common.event;

import com.yunbao.common.bean.LiveBean;

public class PreViewEvent {
    private int mLiveTypeVal;
    private int  mLiveType;
    private  LiveBean  bean;

    public void setmLiveType(int mLiveType) {
        this.mLiveType = mLiveType;
    }

    public int getmLiveTypeVal() {
        return mLiveTypeVal;
    }


    public LiveBean getBean() {
        return bean;
    }

    public void setBean(LiveBean bean) {
        this.bean = bean;
    }

    public void setmLiveTypeVal(int mLiveTypeVal) {
        this.mLiveTypeVal = mLiveTypeVal;
    }

    public int getmLiveType() {
        return mLiveType;
    }

    public PreViewEvent(int mLiveTypeVal) {
        this.mLiveTypeVal = mLiveTypeVal;
    }

    public PreViewEvent(int mLiveTypeVal, int mLiveType) {
        this.mLiveTypeVal = mLiveTypeVal;
        this.mLiveType = mLiveType;
    }

    public PreViewEvent(int mLiveTypeVal, int mLiveType, LiveBean bean) {
        this.mLiveTypeVal = mLiveTypeVal;
        this.mLiveType = mLiveType;
        this.bean = bean;
    }
}
