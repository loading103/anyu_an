package com.yunbao.common;

import com.yunbao.common.utils.SpUtil;

/**
 * Created by cxf on 2018/10/15.
 */

public class HtmlConfig {
    private  String host;

    //登录即代表同意服务和隐私条款
    public  String LOGIN_PRIVCAY = "/index.php?g=portal&m=page&a=index&id=4";
    //直播间贡献榜
    public  String LIVE_LIST = "/index.php?g=Appapi&m=contribute&a=index&uid=";
    //个人主页分享链接
    public  String SHARE_HOME_PAGE ="/index.php?g=Appapi&m=home&a=index&touid=";
    //提现记录
    public  String CASH_RECORD ="/index.php?g=Appapi&m=cash&a=index";
    //支付宝回调地址
    public  String ALI_PAY_NOTIFY_URL = "/Appapi/Pay/notify_ali";
    //视频分享地址
    public  String SHARE_VIDEO = "/index.php?g=appapi&m=video&a=index&videoid=";
    //直播间幸运礼物说明
    public  String LUCK_GIFT_TIP = "/index.php?g=portal&m=page&a=index&id=26";
    //排行榜
    public  String RANK = "/index.php?g=Appapi&m=contribute&a=index&uid=";

    public String getLOGIN_PRIVCAY() {
        host=SpUtil.getInstance().getStringValue(SpUtil.FAST_URL);
        return host+LOGIN_PRIVCAY;
    }

    public String getLIVE_LIST() {
        host=SpUtil.getInstance().getStringValue(SpUtil.FAST_URL);
        return host+LIVE_LIST;
    }

    public String getSHARE_HOME_PAGE() {
        host=SpUtil.getInstance().getStringValue(SpUtil.FAST_URL);
        return host+SHARE_HOME_PAGE;
    }

    public String getCASH_RECORD() {
        host=SpUtil.getInstance().getStringValue(SpUtil.FAST_URL);
        return host+CASH_RECORD;
    }

    public String getALI_PAY_NOTIFY_URL() {
        host=SpUtil.getInstance().getStringValue(SpUtil.FAST_URL);
        return host+ALI_PAY_NOTIFY_URL;
    }

    public String getSHARE_VIDEO() {
        host=SpUtil.getInstance().getStringValue(SpUtil.FAST_URL);
        return host+SHARE_VIDEO;
    }

    public String getLUCK_GIFT_TIP() {
        host=SpUtil.getInstance().getStringValue(SpUtil.FAST_URL);
        return host+LUCK_GIFT_TIP;
    }

    public String getRank() {
        host=SpUtil.getInstance().getStringValue(SpUtil.FAST_URL);
        return host+RANK;
    }
}
