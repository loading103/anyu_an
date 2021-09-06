package com.yunbao.live.bean;

import java.util.List;

/**
 * Created by Administrator on 2020/4/29.
 * Describe:
 */
public class LiveReadyBean {

    /**
     * id : 1
     * name : 音乐
     * thumb : http://47.52.247.162/data/upload/20200423/5ea13378ac44a.png
     * des : 流行、摇滚、说唱、民族等，线上最强演唱会
     * orderno : 1
     * goods : [{"id":"6","name":"测试商品2","thumb":"20200428/5ea81f28d5d44.jpeg","jump_url":"http://baidu.com","is_king":"0","jump_style":"1","show_type":"2"}]
     */

    private String id;
    private String name;
    private String thumb;
    private String des;
    private String orderno;
    private List<GoodsBean> goods;
    private List<GoodsBean> games;
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

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public List<GoodsBean> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsBean> goods) {
        this.goods = goods;
    }

    public List<GoodsBean> getGames() {
        return games;
    }

    public void setGames(List<GoodsBean> games) {
        this.games = games;
    }

    public static class GoodsBean {
        /**
         * id : 6
         * name : 测试商品2
         * thumb : 20200428/5ea81f28d5d44.jpeg
         * jump_url : http://baidu.com
         * is_king : 0
         * jump_style : 1
         * show_type : 2
         */

        private String id;
        private String name;
        private String thumb;
        private String jump_url;
        private String is_king;
        private String jump_style;
        private String show_type;
        private String goods_no;
        private String jump_type;
        private int slide_show_type_button=0;//0-有按钮 1-无按钮
        private boolean flag;//true 有商品信息 false 没有商品信息
        private boolean isCheck;

        public int getSlide_show_type_button() {
            return slide_show_type_button;
        }

        public void setSlide_show_type_button(int slide_show_type_button) {
            this.slide_show_type_button = slide_show_type_button;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public String getGoods_no() {
            return goods_no;
        }

        public void setGoods_no(String goods_no) {
            this.goods_no = goods_no;
        }

        public String getJump_type() {
            return jump_type;
        }

        public void setJump_type(String jump_type) {
            this.jump_type = jump_type;
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

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getJump_url() {
            return jump_url;
        }

        public void setJump_url(String jump_url) {
            this.jump_url = jump_url;
        }

        public String getIs_king() {
            return is_king;
        }

        public void setIs_king(String is_king) {
            this.is_king = is_king;
        }

        public String getJump_style() {
            return jump_style;
        }

        public void setJump_style(String jump_style) {
            this.jump_style = jump_style;
        }

        public String getShow_type() {
            return show_type;
        }

        public void setShow_type(String show_type) {
            this.show_type = show_type;
        }

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }
    }
}
