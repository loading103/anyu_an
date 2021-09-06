package com.yunbao.im.event;

import com.yunbao.im.bean.ImUserBean;

/**
 * Created by cxf on 2018/11/26.
 */

public class ChatFollowEvent {
    private boolean isFollow;

    public ChatFollowEvent(boolean follow){
        this.isFollow=follow;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }
}
