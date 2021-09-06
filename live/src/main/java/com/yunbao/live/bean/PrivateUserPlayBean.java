package com.yunbao.live.bean;

import java.util.List;

/**
 * 开播成功bean
 */

public class PrivateUserPlayBean {
    private List<PrivateUserBean> accept_user;
    private List<PrivateUserBean> refuse_user;

    public List<PrivateUserBean> getAccept_user() {
        return accept_user;
    }

    public void setAccept_user(List<PrivateUserBean> accept_user) {
        this.accept_user = accept_user;
    }

    public List<PrivateUserBean> getRefuse_user() {
        return refuse_user;
    }

    public void setRefuse_user(List<PrivateUserBean> refuse_user) {
        this.refuse_user = refuse_user;
    }
}
