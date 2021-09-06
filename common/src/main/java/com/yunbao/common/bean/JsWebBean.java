package com.yunbao.common.bean;

/**
 * Created by Administrator on 2020/5/15.
 * Describe:
 */
public class JsWebBean {

    /**
     * title : 标题
     * rightTitle : {"name":"按钮名称","url":"链接"}
     * showNai : y
     * back : app
     */
    private String title;
    private RightTitleBean rightTitle;
    private String showNai;
    private String back;
    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RightTitleBean getRightTitle() {
        return rightTitle;
    }

    public void setRightTitle(RightTitleBean rightTitle) {
        this.rightTitle = rightTitle;
    }

    public String getShowNai() {
        return showNai;
    }

    public void setShowNai(String showNai) {
        this.showNai = showNai;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public static class RightTitleBean {
        /**
         * name : 按钮名称
         * url : 链接
         */

        private String name;
        private String url;

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
    }
}
