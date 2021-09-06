package com.yunbao.video.event;

/**
 * Created by cxf on 2018/12/15.
 */

public class VideoViewNumAddEvent {
    private String videoId;

    public VideoViewNumAddEvent(String videoId){
        this.videoId=videoId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
