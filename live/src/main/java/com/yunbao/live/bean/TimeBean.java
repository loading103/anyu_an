package com.yunbao.live.bean;

/**
 * Created by Administrator on 2020/6/27.
 * Describe:
 */
public class TimeBean {

    /**
     * enable : true
     * isFengpan : 1
     * leftOpenTime : 52
     * leftTime : 49
     * nextOpenTime : 1593223860000
     * number : 202006270612
     * openDataTime : 1593223860000
     * opening : true
     * result : 1
     */

    private boolean enable;
    private int isFengpan;
    private int leftOpenTime;
    private int leftTime;
    private long nextOpenTime;
    private String number;
    private long openDataTime;
    private boolean opening;
    private int result;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getIsFengpan() {
        return isFengpan;
    }

    public void setIsFengpan(int isFengpan) {
        this.isFengpan = isFengpan;
    }

    public int getLeftOpenTime() {
        return leftOpenTime;
    }

    public void setLeftOpenTime(int leftOpenTime) {
        this.leftOpenTime = leftOpenTime;
    }

    public int getLeftTime() {
        return leftTime;
    }

    public void setLeftTime(int leftTime) {
        this.leftTime = leftTime;
    }

    public long getNextOpenTime() {
        return nextOpenTime;
    }

    public void setNextOpenTime(long nextOpenTime) {
        this.nextOpenTime = nextOpenTime;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getOpenDataTime() {
        return openDataTime;
    }

    public void setOpenDataTime(long openDataTime) {
        this.openDataTime = openDataTime;
    }

    public boolean isOpening() {
        return opening;
    }

    public void setOpening(boolean opening) {
        this.opening = opening;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
