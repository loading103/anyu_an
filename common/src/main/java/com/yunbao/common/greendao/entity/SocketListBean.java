package com.yunbao.common.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class SocketListBean {

    @Id(autoincrement = true)
    private Long _id;   //id,表的主键并自增长
    private  String userId;
    private  String content;
    private  String time;
    private  String avatar;
    private  String nickname;
    private String currentUid;
    private int unReadCount;
    @Generated(hash = 355971192)
    public SocketListBean(Long _id, String userId, String content, String time,
            String avatar, String nickname, String currentUid, int unReadCount) {
        this._id = _id;
        this.userId = userId;
        this.content = content;
        this.time = time;
        this.avatar = avatar;
        this.nickname = nickname;
        this.currentUid = currentUid;
        this.unReadCount = unReadCount;
    }
    @Generated(hash = 1387953696)
    public SocketListBean() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getNickname() {
        return this.nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getCurrentUid() {
        return this.currentUid;
    }
    public void setCurrentUid(String currentUid) {
        this.currentUid = currentUid;
    }
    public int getUnReadCount() {
        return this.unReadCount;
    }
    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

}
