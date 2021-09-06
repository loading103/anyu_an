package com.yunbao.common.http;

import java.io.Serializable;

/**
 * Created by cxf on 2017/8/5.
 */

public class JsonBean implements Serializable {
    private int ret;
    private String msg;
    private Data data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
