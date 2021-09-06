package com.yunbao.common.bean;

/**
 * Created by Administrator on 2020/5/25.
 * Describe:
 */
public class VideoClassBean {

    /**
     * des : 2020年05月25日11:27:363
     * id : 20
     * name : 男神
     * orderno : 0
     * thumb : /data/upload/20200525/5ecb3b26544e6.jpeg
     */

    private String des;
    private String id;
    private String name;
    private String orderno;
    private String thumb;
    private boolean isAll;
    private boolean isMore;
    private boolean checked;

    public boolean isAll() {
        return isAll;
    }

    public void setAll(boolean all) {
        isAll = all;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
