package com.yunbao.phonelive.bean;

import java.util.List;

/**
 * Created by Administrator on 2020/5/29.
 * Describe:
 */
public class UrlBean {

    private UrlBean.RhbyPathBean rhbyPath;
    /**
     * push : rtmp://47.75.58.155:1935
     * test_url : rtmp://47.75.58.155:2935/nginx.html
     */

    private List<UrlBean.PushBean> push;
    /**
     * pull : rtmp://47.75.58.155:1935
     * test_url : rtmp://47.75.58.155:2935/nginx.html
     */

    private List<UrlBean.PullBean> pull;
    /**
     * push : [{"push":"rtmp://47.75.58.155:1935","test_url":"rtmp://47.75.58.155:2935/nginx.html"}]
     * pull : [{"pull":"rtmp://47.75.58.155:1935","test_url":"rtmp://47.75.58.155:2935/nginx.html"}]
     * app_host : ["http://47.75.111.156/","http://47.75.111.156/"]
     * rhbyPath : {"push":[{"push":"rtmp://47.75.58.155:1935","test_url":"rtmp://47.75.58.155:2935/nginx.html"}],"pull":[{"pull":"rtmp://47.75.58.155:1935","test_url":"rtmp://47.75.58.155:2935/nginx.html"}]}
     */

    private List<String> app_host;

    public UrlBean.RhbyPathBean getRhbyPath() {
        return rhbyPath;
    }

    public void setRhbyPath(UrlBean.RhbyPathBean rhbyPath) {
        this.rhbyPath = rhbyPath;
    }

    public List<UrlBean.PushBean> getPush() {
        return push;
    }

    public void setPush(List<UrlBean.PushBean> push) {
        this.push = push;
    }

    public List<UrlBean.PullBean> getPull() {
        return pull;
    }

    public void setPull(List<UrlBean.PullBean> pull) {
        this.pull = pull;
    }

    public List<String> getApp_host() {
        return app_host;
    }

    public void setApp_host(List<String> app_host) {
        this.app_host = app_host;
    }

    public static class RhbyPathBean {
        /**
         * push : rtmp://47.75.58.155:1935
         * test_url : rtmp://47.75.58.155:2935/nginx.html
         */

        private List<UrlBean.RhbyPathBean.PushBean> push;
        /**
         * pull : rtmp://47.75.58.155:1935
         * test_url : rtmp://47.75.58.155:2935/nginx.html
         */

        private List<UrlBean.RhbyPathBean.PullBean> pull;

        public List<UrlBean.RhbyPathBean.PushBean> getPush() {
            return push;
        }

        public void setPush(List<UrlBean.RhbyPathBean.PushBean> push) {
            this.push = push;
        }

        public List<UrlBean.RhbyPathBean.PullBean> getPull() {
            return pull;
        }

        public void setPull(List<UrlBean.RhbyPathBean.PullBean> pull) {
            this.pull = pull;
        }

        public static class PushBean {
            private String push;
            private String test_url;

            public String getPush() {
                return push;
            }

            public void setPush(String push) {
                this.push = push;
            }

            public String getTest_url() {
                return test_url;
            }

            public void setTest_url(String test_url) {
                this.test_url = test_url;
            }
        }

        public static class PullBean {
            private String pull;
            private String test_url;

            public String getPull() {
                return pull;
            }

            public void setPull(String pull) {
                this.pull = pull;
            }

            public String getTest_url() {
                return test_url;
            }

            public void setTest_url(String test_url) {
                this.test_url = test_url;
            }
        }
    }

    public static class PushBean {
        private String push;
        private String test_url;

        public String getPush() {
            return push;
        }

        public void setPush(String push) {
            this.push = push;
        }

        public String getTest_url() {
            return test_url;
        }

        public void setTest_url(String test_url) {
            this.test_url = test_url;
        }
    }

    public static class PullBean {
        private String pull;
        private String test_url;

        public String getPull() {
            return pull;
        }

        public void setPull(String pull) {
            this.pull = pull;
        }

        public String getTest_url() {
            return test_url;
        }

        public void setTest_url(String test_url) {
            this.test_url = test_url;
        }
    }
}
