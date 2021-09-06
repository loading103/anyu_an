package com.yunbao.common.event;

/**
 * @author jinxin
 * 剑之所指，心之所向
 * @date 2020/9/26
 */
public class DownSuccessVideoEvent {
    private  int size;

    public DownSuccessVideoEvent(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
