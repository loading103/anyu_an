package com.yunbao.common.event;

import com.yunbao.common.bean.UserBean;

public class SocketListClickEvent {
   private UserBean userBean;

    public SocketListClickEvent(UserBean userBean) {
        this.userBean = userBean;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
}
