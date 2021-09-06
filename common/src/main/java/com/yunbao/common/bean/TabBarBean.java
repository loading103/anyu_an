package com.yunbao.common.bean;

import java.util.List;

/**
 * @author：jianxin 创建时间：2020/9/28
 */
public class TabBarBean {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 3
         * name : 购物大厅
         */

        private String id;
        private String name;
        private String url;
        private String is_king;
        private String jump_type;
        private String prompt_switch; //提示开关
        private  String prompt_content;//提示内容
        public ListBean(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getPrompt_switch() {
            return prompt_switch;
        }

        public void setPrompt_switch(String prompt_switch) {
            this.prompt_switch = prompt_switch;
        }

        public String getPrompt_content() {
            return prompt_content;
        }

        public void setPrompt_content(String prompt_content) {
            this.prompt_content = prompt_content;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
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
    }
}
