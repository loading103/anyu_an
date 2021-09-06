package com.yunbao.common.event;


public class ShowXieYiEvent {

    private String content;
    private boolean isvideo;

    public ShowXieYiEvent(String content,boolean isvideo) {
        this.content = content;
        this.isvideo=isvideo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isIsvideo() {
        return isvideo;
    }

    public void setIsvideo(boolean isvideo) {
        this.isvideo = isvideo;
    }
}
