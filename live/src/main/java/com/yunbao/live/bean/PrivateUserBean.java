package com.yunbao.live.bean;

/**
 *
 */

public class PrivateUserBean {


    /**
     * uid : 29134
     * nickname : liwei2
     * photo : fksadjfksadfjalfdjd
     */

    private String uid;
    private String nickname;
    private String photo;
    protected int simiType;//私密的接受拒绝状态 0是未处理，1是已接收，2是已拒绝

    public int getSimiType() {
        return simiType;
    }

    public void setSimiType(int simiType) {
        this.simiType = simiType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
