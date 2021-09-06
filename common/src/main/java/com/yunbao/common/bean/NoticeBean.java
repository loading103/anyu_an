package com.yunbao.common.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cxf on 2019/3/30.
 */

public class NoticeBean implements Serializable {

    /**
     * notice_switch : 1
     * notice_txt : ["豆腐干士大夫","的分公司分管的首付大概","大师傅大师傅","啊多发点是","撒大噶收到","豆腐干大师傅好歹是个","地方还是太弱","时感到附属国","撒旦发射点","广东佛山噶士大夫士大夫"]
     */

    private String notice_switch;
    private List<String> notice_txt;

    public String getNotice_switch() {
        return notice_switch;
    }

    public void setNotice_switch(String notice_switch) {
        this.notice_switch = notice_switch;
    }

    public List<String> getNotice_txt() {
        return notice_txt;
    }

    public void setNotice_txt(List<String> notice_txt) {
        this.notice_txt = notice_txt;
    }
}
