package com.yunbao.main.event;

import com.yunbao.common.bean.JsWebBean;

/**
 * Created by cxf on 2019/3/25.
 */

public class MoreGameEvent {
    private boolean hasGame;

    public MoreGameEvent(boolean hasGame){
        this.hasGame=hasGame;
    }

    public boolean isHasGame() {
        return hasGame;
    }

    public void setHasGame(boolean hasGame) {
        this.hasGame = hasGame;
    }
}
