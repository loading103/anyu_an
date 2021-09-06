package com.yunbao.common.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SocketMessageBean {

    @Id(autoincrement = true)
    private Long _id;   //id,表的主键并自增长
    private  String msgId;
    private  String content;
    private  String sendUid;
    private  String targetUid;
    private  String time;
    private  String type;
    private  String url;
    private  String avatar;
    private  String nickname;
    private String currentUid;
    private String userId;
    private String sendState;//1是发送成功  2是发送失败
    private String sendType; //1是自己发送  2是接收
    @Generated(hash = 77460448)
    public SocketMessageBean(Long _id, String msgId, String content, String sendUid,
            String targetUid, String time, String type, String url, String avatar,
            String nickname, String currentUid, String userId, String sendState,
            String sendType) {
        this._id = _id;
        this.msgId = msgId;
        this.content = content;
        this.sendUid = sendUid;
        this.targetUid = targetUid;
        this.time = time;
        this.type = type;
        this.url = url;
        this.avatar = avatar;
        this.nickname = nickname;
        this.currentUid = currentUid;
        this.userId = userId;
        this.sendState = sendState;
        this.sendType = sendType;
    }
    @Generated(hash = 1002578492)
    public SocketMessageBean() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getMsgId() {
        return this.msgId;
    }
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getSendUid() {
        return this.sendUid;
    }
    public void setSendUid(String sendUid) {
        this.sendUid = sendUid;
    }
    public String getTargetUid() {
        return this.targetUid;
    }
    public void setTargetUid(String targetUid) {
        this.targetUid = targetUid;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
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
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getSendState() {
        return this.sendState;
    }
    public void setSendState(String sendState) {
        this.sendState = sendState;
    }
    public String getSendType() {
        return this.sendType;
    }
    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

}
