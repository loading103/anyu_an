package com.yunbao.common.download;

import android.content.ContentValues;

/**
 * Created by Administrator on 2020/9/22.
 * Describe:
 */
public class TasksManagerModel {
    public final static String ID = "id";
    public final static String NAME = "name";
    public final static String URL = "url";
    public final static String PATH = "path";
    public final static String IMG = "img";
    public final static String VIP = "vip";
    public final static String SIZE = "size";
    public final static String EDIT = "edit";
    public final static String CHECKED = "checked";
    public final static String FILE_NAME = "file_name";
    public final static String VIDEO_ID = "video_id";
    public final static String FINISH = "finish";

    public final static String DOWN_EDIT = "downedit";
    public final static String DOWN_CHECKED = "downchecked";

    private int id;
    private String name;
    private String url;
    private String path;
    private String img;
    private String vip;
    private String size;
    private String edit;//是否是编辑状态
    private String checked;//是否选中
    private String file_name;//本地文件名称
    private String video_id;//视频id
    private String finish;//是否下载完成

    private String downedit;////下载好了的是否是编辑状态
    private String downchecked;//下载好了的是否选中

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getEdit() {
        return edit;
    }

    public void setEdit(String edit) {
        this.edit = edit;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDownedit() {
        return downedit;
    }

    public void setDownedit(String downedit) {
        this.downedit = downedit;
    }

    public String getDownchecked() {
        return downchecked;
    }

    public void setDownchecked(String downchecked) {
        this.downchecked = downchecked;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(NAME, name);
        cv.put(URL, url);
        cv.put(PATH, path);
        cv.put(IMG, img);
        cv.put(VIP, vip);
        cv.put(SIZE, size);
        cv.put(EDIT, edit);
        cv.put(CHECKED, checked);
        cv.put(FILE_NAME, file_name);
        cv.put(VIDEO_ID, video_id);
        cv.put(DOWN_CHECKED, downchecked);
        cv.put(DOWN_EDIT, downedit);
        cv.put(FINISH, finish);
        return cv;
    }
}
