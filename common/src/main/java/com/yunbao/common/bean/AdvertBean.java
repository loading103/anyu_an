package com.yunbao.common.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2020/7/27.
 * Describe:
 */
public class AdvertBean implements Serializable {

    /**
     * app_nav : [{"id":"46","name":"puma","thumb":"/data/upload/20200424/5ea2a12f236a0.png","des":"puma","orderno":"1","slide_url":"http://www.jd.com/","slide_target":"1","is_king":"1","slide_jump_style":"1","slide_show_type":"3","jump_type":"1","cat_idname":"app_nav","cat_name":"APP首页中上方分类内容"}]
     * app_title : [{"id":"145","name":"会议室","thumb":"/group1/system/image/2020/07/22/0fc94bbcce162470f4a4c555d077da17.png","des":"用途及用途","orderno":"0","slide_url":"http://www.baidu.com","slide_target":"1","is_king":"1","slide_jump_style":"1","slide_show_type":"7","jump_type":"1","cat_idname":"app_title","cat_name":"APP首页顶部导航"}]
     * APP_mynav : [{"id":"71","name":"耐克","thumb":"/data/upload/20200501/5eac02b804ce7.jpeg","des":"","orderno":"0","slide_url":"http://www.baidu.com","slide_target":"1","is_king":"0","slide_jump_style":"2","slide_show_type":"3","jump_type":"1","cat_idname":"APP_mynav","cat_name":"APP 我的页面中上位置内容"}]
     * APP_tab_4 : {"name":"购物大厅"}
     * "hall_index":[
     *         {
     *             "branch_id":"51",
     *             "id":"80",
     *             "is_hot":"0",
     *             "is_king":"0",
     *             "jump_type":"1",
     *             "jump_type_android":"1",
     *             "jump_type_ios":"1",
     *             "jump_url":"http://www.baidu.com",
     *             "jump_url_android":"http://www.baidu.com",
     *             "jump_url_ios":"http://www.jd.com",
     *             "name":"阿瑟的全文",
     *             "pic":"http://132.232.122.151:8080/group1/system/image/2020/07/01/04e8d1c128b9128b2693f0d34507b67a.png",
     *             "show_style":"6",
     *             "sort":"0"
     *         }
     *     ],
     */

    private LiveClassBean APP_tab_4;
    private List<LiveClassBean> app_nav;
    private List<LiveClassBean> app_title;
    private List<LiveClassBean> APP_mynav;
    private List<LiveClassBean> hall_index;
    private List<LiveBean> list;
    private List<BannerBean> slide;
    private List<ListBarBean> tabbar;

    private NoticeBean notice;


    public List<ListBarBean> getTabbar() {
        return tabbar;
    }

    public void setTabbar(List<ListBarBean> tabbar) {
        this.tabbar = tabbar;
    }

    public List<LiveClassBean> getHall_index() {
        return hall_index;
    }

    public void setHall_index(List<LiveClassBean> hall_index) {
        this.hall_index = hall_index;
    }

    public List<BannerBean> getSlide() {
        return slide;
    }

    public void setSlide(List<BannerBean> slide) {
        this.slide = slide;
    }

    public NoticeBean getNotice() {
        return notice;
    }

    public void setNotice(NoticeBean notice) {
        this.notice = notice;
    }

    public List<LiveBean> getList() {
        return list;
    }

    public void setList(List<LiveBean> list) {
        this.list = list;
    }

    public LiveClassBean getAPP_tab_4() {
        return APP_tab_4;
    }

    public void setAPP_tab_4(LiveClassBean APP_tab_4) {
        this.APP_tab_4 = APP_tab_4;
    }

    public List<LiveClassBean> getApp_nav() {
        return app_nav;
    }

    public void setApp_nav(List<LiveClassBean> app_nav) {
        this.app_nav = app_nav;
    }

    public List<LiveClassBean> getApp_title() {
        return app_title;
    }

    public void setApp_title(List<LiveClassBean> app_title) {
        this.app_title = app_title;
    }

    public List<LiveClassBean> getAPP_mynav() {
        return APP_mynav;
    }

    public void setAPP_mynav(List<LiveClassBean> APP_mynav) {
        this.APP_mynav = APP_mynav;
    }
}
