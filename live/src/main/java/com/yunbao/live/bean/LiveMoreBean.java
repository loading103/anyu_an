package com.yunbao.live.bean;

import com.yunbao.common.bean.LiveBean;

import java.util.List;

/**
 * Created by Administrator on 2020/5/13.
 * Describe:
 */
public class LiveMoreBean {

    private List<SlideBeanX> slide;
    private List<LiveBean> live_avatar;

    public List<SlideBeanX> getSlide() {
        return slide;
    }

    public void setSlide(List<SlideBeanX> slide) {
        this.slide = slide;
    }

    public List<LiveBean> getLive_avatar() {
        return live_avatar;
    }

    public void setLive_avatar(List<LiveBean> live_avatar) {
        this.live_avatar = live_avatar;
    }

    public static class SlideBeanX {
        /**
         * cid : 9
         * cat_name : 位置标题A
         * cat_idname : posi_a
         * cat_remark : null
         * cat_status : 1
         * cat_img :
         * slide_target : 0
         * slide_url : 0
         * slide_jump_style : 0
         * slide_show_type : 1,8,6
         * slide : [{"slide_id":"73","slide_cid":"9","slide_name":"Acontent1","slide_pic":"/data/upload/20200512/5eba68b6b5fa9.jpg","slide_target":"1","slide_url":"https://www.baidu.com","slide_des":"","slide_content":null,"slide_status":"1","listorder":"0","slide_login_data":"0","slide_jump_style":"1","slide_show_type":"8"}]
         */

        private String cid;
        private String cat_name;
        private String cat_idname;
        private Object cat_remark;
        private String cat_status;
        private String cat_img;
        private String slide_target;
        private String slide_url;
        private String slide_jump_style;
        private String slide_show_type;
        private List<SlideBean> slide;

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getCat_name() {
            return cat_name;
        }

        public void setCat_name(String cat_name) {
            this.cat_name = cat_name;
        }

        public String getCat_idname() {
            return cat_idname;
        }

        public void setCat_idname(String cat_idname) {
            this.cat_idname = cat_idname;
        }

        public Object getCat_remark() {
            return cat_remark;
        }

        public void setCat_remark(Object cat_remark) {
            this.cat_remark = cat_remark;
        }

        public String getCat_status() {
            return cat_status;
        }

        public void setCat_status(String cat_status) {
            this.cat_status = cat_status;
        }

        public String getCat_img() {
            return cat_img;
        }

        public void setCat_img(String cat_img) {
            this.cat_img = cat_img;
        }

        public String getSlide_target() {
            return slide_target;
        }

        public void setSlide_target(String slide_target) {
            this.slide_target = slide_target;
        }

        public String getSlide_url() {
            return slide_url;
        }

        public void setSlide_url(String slide_url) {
            this.slide_url = slide_url;
        }

        public String getSlide_jump_style() {
            return slide_jump_style;
        }

        public void setSlide_jump_style(String slide_jump_style) {
            this.slide_jump_style = slide_jump_style;
        }

        public String getSlide_show_type() {
            return slide_show_type;
        }

        public void setSlide_show_type(String slide_show_type) {
            this.slide_show_type = slide_show_type;
        }

        public List<SlideBean> getSlide() {
            return slide;
        }

        public void setSlide(List<SlideBean> slide) {
            this.slide = slide;
        }

        public static class SlideBean {
            /**
             * slide_id : 73
             * slide_cid : 9
             * slide_name : Acontent1
             * slide_pic : /data/upload/20200512/5eba68b6b5fa9.jpg
             * slide_target : 1
             * slide_url : https://www.baidu.com
             * slide_des :
             * slide_content : null
             * slide_status : 1
             * listorder : 0
             * slide_login_data : 0
             * slide_jump_style : 1
             * slide_show_type : 8
             */

            private String slide_id;
            private String slide_cid;
            private String slide_name;
            private String slide_pic;
            private String slide_target;
            private String slide_url;
            private String slide_des;
            private Object slide_content;
            private String slide_status;
            private String listorder;
            private String slide_login_data;
            private String slide_jump_style;
            private int slide_show_type;
            private String jump_type; //1代表是正常url链接，2代表室本地html路由
            private String is_king;
            private int slide_show_type_button=0;//0-有按钮 1-无按钮

            public int getSlide_show_type_button() {
                return slide_show_type_button;
            }

            public void setSlide_show_type_button(int slide_show_type_button) {
                this.slide_show_type_button = slide_show_type_button;
            }

            public String getIs_king() {
                return is_king;
            }

            public void setIs_king(String is_king) {
                this.is_king = is_king;
            }

            public String getJump_type() {
                return jump_type;
            }

            public void setJump_type(String jump_type) {
                this.jump_type = jump_type;
            }

            public String getSlide_id() {
                return slide_id;
            }

            public void setSlide_id(String slide_id) {
                this.slide_id = slide_id;
            }

            public String getSlide_cid() {
                return slide_cid;
            }

            public void setSlide_cid(String slide_cid) {
                this.slide_cid = slide_cid;
            }

            public String getSlide_name() {
                return slide_name;
            }

            public void setSlide_name(String slide_name) {
                this.slide_name = slide_name;
            }

            public String getSlide_pic() {
                return slide_pic;
            }

            public void setSlide_pic(String slide_pic) {
                this.slide_pic = slide_pic;
            }

            public String getSlide_target() {
                return slide_target;
            }

            public void setSlide_target(String slide_target) {
                this.slide_target = slide_target;
            }

            public String getSlide_url() {
                return slide_url;
            }

            public void setSlide_url(String slide_url) {
                this.slide_url = slide_url;
            }

            public String getSlide_des() {
                return slide_des;
            }

            public void setSlide_des(String slide_des) {
                this.slide_des = slide_des;
            }

            public Object getSlide_content() {
                return slide_content;
            }

            public void setSlide_content(Object slide_content) {
                this.slide_content = slide_content;
            }

            public String getSlide_status() {
                return slide_status;
            }

            public void setSlide_status(String slide_status) {
                this.slide_status = slide_status;
            }

            public String getListorder() {
                return listorder;
            }

            public void setListorder(String listorder) {
                this.listorder = listorder;
            }

            public String getSlide_login_data() {
                return slide_login_data;
            }

            public void setSlide_login_data(String slide_login_data) {
                this.slide_login_data = slide_login_data;
            }

            public String getSlide_jump_style() {
                return slide_jump_style;
            }

            public void setSlide_jump_style(String slide_jump_style) {
                this.slide_jump_style = slide_jump_style;
            }

            public int getSlide_show_type() {
                return slide_show_type;
            }

            public void setSlide_show_type(int slide_show_type) {
                this.slide_show_type = slide_show_type;
            }
        }
    }

    public static class LiveAvatarBean {

        /**
         * id : 103
         * stream : 27480_1559485509
         * pull : http://47.75.111.156/data/upload/video/4.mp4
         * avatar : http://47.75.111.156/data/upload/20200430/5eaa97867ebd8.jpg
         * user_nicename : 你的(*￣︶￣)
         * goods_id : 9
         * signature :
         * name : 电脑
         * live_class_id : 0
         * pic : http://47.75.111.156/data/upload/20200429/5ea93cb87a238.jpeg
         * show_type : 4
         * jump_style : 1
         */

        private String id;
        private String stream;
        private String pull;
        private String avatar;
        private String user_nicename;
        private String goods_id;
        private String signature;
        private String name;
        private String live_class_id;
        private String pic;
        private String show_type;
        private String jump_style;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStream() {
            return stream;
        }

        public void setStream(String stream) {
            this.stream = stream;
        }

        public String getPull() {
            return pull;
        }

        public void setPull(String pull) {
            this.pull = pull;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getUser_nicename() {
            return user_nicename;
        }

        public void setUser_nicename(String user_nicename) {
            this.user_nicename = user_nicename;
        }

        public String getGoods_id() {
            return goods_id;
        }

        public void setGoods_id(String goods_id) {
            this.goods_id = goods_id;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLive_class_id() {
            return live_class_id;
        }

        public void setLive_class_id(String live_class_id) {
            this.live_class_id = live_class_id;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getShow_type() {
            return show_type;
        }

        public void setShow_type(String show_type) {
            this.show_type = show_type;
        }

        public String getJump_style() {
            return jump_style;
        }

        public void setJump_style(String jump_style) {
            this.jump_style = jump_style;
        }
    }
}
