package com.yunbao.live.bean;

import java.util.List;

/**
 * Created by Administrator on 2020/6/30.
 * Describe:
 */
public class GoodsResultBean {

    /**
     * result : 1
     * sscHistoryList : [{"number":"202009120675","openCode":"4,4,5,8,6","playGroupId":null,"openTime":1599880440000}]
     */

    private int result;
    private List<SscHistoryListBean> sscHistoryList;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<SscHistoryListBean> getSscHistoryList() {
        return sscHistoryList;
    }

    public void setSscHistoryList(List<SscHistoryListBean> sscHistoryList) {
        this.sscHistoryList = sscHistoryList;
    }

    public static class SscHistoryListBean {
        /**
         * number : 202009120675
         * openCode : 4,4,5,8,6
         * playGroupId : null
         * openTime : 1599880440000
         */

        private String number;
        private String openCode;
        private String playGroupId;
        private long openTime;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getOpenCode() {
            return openCode;
        }

        public void setOpenCode(String openCode) {
            this.openCode = openCode;
        }

        public String getPlayGroupId() {
            return playGroupId;
        }

        public void setPlayGroupId(String playGroupId) {
            this.playGroupId = playGroupId;
        }

        public long getOpenTime() {
            return openTime;
        }

        public void setOpenTime(long openTime) {
            this.openTime = openTime;
        }
    }
}
