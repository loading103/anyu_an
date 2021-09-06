package com.yunbao.common.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by cxf on 2017/8/5.
 */

public class ConfigBean {
    private String android_download_state;//是不是强制更新 1是强制更新  0不是
    private String version;//Android apk安装包 版本号
    private String downloadApkUrl;//Android apk安装包 下载地址
    private String updateDes;//版本更新描述
    private String liveWxShareUrl;//直播间微信分享地址
    private String liveShareTitle;//直播间分享标题
    private String liveShareDes;//直播间分享描述
    private String videoShareTitle;//短视频分享标题
    private String videoShareDes;//短视频分享描述
    private int videoAuditSwitch;//短视频审核是否开启
    private int videoCloudType;//短视频云储存类型 1七牛云 2腾讯云
    private String videoQiNiuHost;//短视频七牛云域名
    private String txCosAppId;//腾讯云存储appId
    private String txCosRegion;//腾讯云存储区域
    private String txCosBucketName;//腾讯云存储桶名字
    private String txCosVideoPath;//腾讯云存储视频文件夹
    private String txCosImagePath;//腾讯云存储图片文件夹
    private String coinName;//钻石名称
    private String votesName;//映票名称
    private String[] liveTimeCoin;//直播间计时收费规则
    private String[] loginType;//三方登录类型
    private String[][] liveType;//直播间开播类型
    private String[] shareType;//分享类型
    private List<LiveClassBean> liveClass;//直播分类
    private int maintainSwitch;//维护开关
    private String maintainTips;//维护提示
    private String beautyKey;//萌颜鉴权码
    private int beautyMeiBai;//萌颜美白数值
    private int beautyMoPi;//萌颜磨皮数值
    private int beautyBaoHe;//萌颜饱和数值
    private int beautyFenNen;//萌颜粉嫩数值
    private int beautyBigEye;//萌颜大眼数值
    private int beautyFace;//萌颜瘦脸数值
    private String mAdInfo;//引导页 广告信息
    private List<VideoClassBean> videoclass;//视频分类
    private String goods_result;
    private String goods_server;
    private String goods_show_time;
    private String goods_show_title;
    private String king_id;
    private String user_custom;
    private String customer_service;
    private String personal_sign;
    private String forget_pass;//忘记密码跳转地址
    private String forget_pass_type;//忘记密码  1外部 2内部
    private String conversation_switch;//1 语音界面开
    private String private_switch;//私密直播开关 0-关 1-开
    private String private_limit;//申请私密直播限制
    private String private_timeout;//倒计时
    private String video_limit_rule;//视频观看限制详情
    private int register_invitation_switch;//注册邀请码填写开关
    private CustomerServiceData customer_service_data;
    private String login_way; //0-登录优先 1-内容优先
    private String token;//游客token
    private String uid;//游客uid
    private int live_preview_time;//预览倒计时

    public int getLive_preview_time() {
        return live_preview_time;
    }

    public void setLive_preview_time(int live_preview_time) {
        this.live_preview_time = live_preview_time;
    }

    public String getLogin_way() {
        return login_way;
    }

    public void setLogin_way(String login_way) {
        this.login_way = login_way;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getRegister_invitation_switch() {
        return register_invitation_switch;
    }

    public void setRegister_invitation_switch(int register_invitation_switch) {
        this.register_invitation_switch = register_invitation_switch;
    }

    public String getVideo_limit_rule() {
        return video_limit_rule;
    }

    public void setVideo_limit_rule(String video_limit_rule) {
        this.video_limit_rule = video_limit_rule;
    }

    public String getAndroid_download_state() {
        return android_download_state;
    }

    public void setAndroid_download_state(String android_download_state) {
        this.android_download_state = android_download_state;
    }

    public String getPrivate_switch() {
        return private_switch;
    }

    public void setPrivate_switch(String private_switch) {
        this.private_switch = private_switch;
    }

    public String getPrivate_limit() {
        return private_limit;
    }

    public void setPrivate_limit(String private_limit) {
        this.private_limit = private_limit;
    }

    public String getPrivate_timeout() {
        return private_timeout;
    }

    public void setPrivate_timeout(String private_timeout) {
        this.private_timeout = private_timeout;
    }

    public String getConversation_switch() {
        return conversation_switch;
    }

    public void setConversation_switch(String conversation_switch) {
        this.conversation_switch = conversation_switch;
    }

    public CustomerServiceData getCustomer_service_data() {
        return customer_service_data;
    }

    public void setCustomer_service_data(CustomerServiceData customer_service_data) {
        this.customer_service_data = customer_service_data;
    }

    public String getForget_pass_type() {
        return forget_pass_type;
    }

    public void setForget_pass_type(String forget_pass_type) {
        this.forget_pass_type = forget_pass_type;
    }

    public String getForget_pass() {
        return forget_pass;
    }

    public void setForget_pass(String forget_pass) {
        this.forget_pass = forget_pass;
    }

    public static class CustomerServiceData{
        private int id;
        private String name;
        private String thumb;
        private String des;
        private String orderno;
        private String slide_url;
        private String slide_target;
        private String is_king;
        private String slide_jump_style;
        private String slide_show_type;
        private String jump_type;
        private int slide_show_type_button=0;//0-有按钮 1-无按钮

        public int getSlide_show_type_button() {
            return slide_show_type_button;
        }

        public void setSlide_show_type_button(int slide_show_type_button) {
            this.slide_show_type_button = slide_show_type_button;
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

        public String getSlide_url() {
            return slide_url;
        }

        public void setSlide_url(String slide_url) {
            this.slide_url = slide_url;
        }

        public String getSlide_target() {
            return slide_target;
        }

        public void setSlide_target(String slide_target) {
            this.slide_target = slide_target;
        }

        public String getIs_king() {
            return is_king;
        }

        public void setIs_king(String is_king) {
            this.is_king = is_king;
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

        public String getJump_type() {
            return jump_type;
        }

        public void setJump_type(String jump_type) {
            this.jump_type = jump_type;
        }
    }

    public String getUser_custom() {
        return user_custom;
    }

    public void setUser_custom(String user_custom) {
        this.user_custom = user_custom;
    }

    public String getCustomer_service() {
        return customer_service;
    }

    public void setCustomer_service(String customer_service) {
        this.customer_service = customer_service;
    }

    public String getPersonal_sign() {
        return personal_sign;
    }

    public void setPersonal_sign(String personal_sign) {
        this.personal_sign = personal_sign;
    }

    public String getGoods_result() {
        return goods_result;
    }

    public void setGoods_result(String goods_result) {
        this.goods_result = goods_result;
    }

    public String getGoods_server() {
        return goods_server;
    }

    public void setGoods_server(String goods_server) {
        this.goods_server = goods_server;
    }

    public String getGoods_show_time() {
        return goods_show_time;
    }

    public void setGoods_show_time(String goods_show_time) {
        this.goods_show_time = goods_show_time;
    }

    public String getGoods_show_title() {
        return goods_show_title;
    }

    public void setGoods_show_title(String goods_show_title) {
        this.goods_show_title = goods_show_title;
    }

    public String getKing_id() {
        return king_id;
    }

    public void setKing_id(String king_id) {
        this.king_id = king_id;
    }

    @JSONField(name = "apk_ver")
    public String getVersion() {
        return version;
    }

    @JSONField(name = "apk_ver")
    public void setVersion(String version) {
        this.version = version;
    }

    @JSONField(name = "apk_url")
    public String getDownloadApkUrl() {
        return downloadApkUrl;
    }

    @JSONField(name = "apk_url")
    public void setDownloadApkUrl(String downloadApkUrl) {
        this.downloadApkUrl = downloadApkUrl;
    }

    @JSONField(name = "apk_des")
    public String getUpdateDes() {
        return updateDes;
    }

    @JSONField(name = "apk_des")
    public void setUpdateDes(String updateDes) {
        this.updateDes = updateDes;
    }

    @JSONField(name = "wx_siteurl")
    public void setLiveWxShareUrl(String liveWxShareUrl) {
        this.liveWxShareUrl = liveWxShareUrl;
    }

    @JSONField(name = "wx_siteurl")
    public String getLiveWxShareUrl() {
        return liveWxShareUrl;
    }

    @JSONField(name = "share_title")
    public String getLiveShareTitle() {
        return liveShareTitle;
    }

    @JSONField(name = "share_title")
    public void setLiveShareTitle(String liveShareTitle) {
        this.liveShareTitle = liveShareTitle;
    }

    @JSONField(name = "share_des")
    public String getLiveShareDes() {
        return liveShareDes;
    }

    @JSONField(name = "share_des")
    public void setLiveShareDes(String liveShareDes) {
        this.liveShareDes = liveShareDes;
    }

    @JSONField(name = "name_coin")
    public String getCoinName() {
        return coinName;
    }

    @JSONField(name = "name_coin")
    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    @JSONField(name = "name_votes")
    public String getVotesName() {
        return votesName;
    }

    @JSONField(name = "name_votes")
    public void setVotesName(String votesName) {
        this.votesName = votesName;
    }

    @JSONField(name = "live_time_coin")
    public String[] getLiveTimeCoin() {
        return liveTimeCoin;
    }

    @JSONField(name = "live_time_coin")
    public void setLiveTimeCoin(String[] liveTimeCoin) {
        this.liveTimeCoin = liveTimeCoin;
    }

    @JSONField(name = "login_type")
    public String[] getLoginType() {
        return loginType;
    }

    @JSONField(name = "login_type")
    public void setLoginType(String[] loginType) {
        this.loginType = loginType;
    }

    @JSONField(name = "live_type")
    public String[][] getLiveType() {
        return liveType;
    }

    @JSONField(name = "live_type")
    public void setLiveType(String[][] liveType) {
        this.liveType = liveType;
    }

    @JSONField(name = "share_type")
    public String[] getShareType() {
        return shareType;
    }

    @JSONField(name = "share_type")
    public void setShareType(String[] shareType) {
        this.shareType = shareType;
    }

    @JSONField(name = "liveclass")
    public List<LiveClassBean> getLiveClass() {
        return liveClass;
    }

    @JSONField(name = "liveclass")
    public void setLiveClass(List<LiveClassBean> liveClass) {
        this.liveClass = liveClass;
    }

    @JSONField(name = "maintain_switch")
    public int getMaintainSwitch() {
        return maintainSwitch;
    }

    @JSONField(name = "maintain_switch")
    public void setMaintainSwitch(int maintainSwitch) {
        this.maintainSwitch = maintainSwitch;
    }

    @JSONField(name = "maintain_tips")
    public String getMaintainTips() {
        return maintainTips;
    }

    @JSONField(name = "maintain_tips")
    public void setMaintainTips(String maintainTips) {
        this.maintainTips = maintainTips;
    }

    @JSONField(name = "sprout_key")
    public String getBeautyKey() {
        return beautyKey;
    }

    @JSONField(name = "sprout_key")
    public void setBeautyKey(String beautyKey) {
        this.beautyKey = beautyKey;
    }

    @JSONField(name = "sprout_white")
    public int getBeautyMeiBai() {
        return beautyMeiBai;
    }

    @JSONField(name = "sprout_white")
    public void setBeautyMeiBai(int beautyMeiBai) {
        this.beautyMeiBai = beautyMeiBai;
    }

    @JSONField(name = "sprout_skin")
    public int getBeautyMoPi() {
        return beautyMoPi;
    }

    @JSONField(name = "sprout_skin")
    public void setBeautyMoPi(int beautyMoPi) {
        this.beautyMoPi = beautyMoPi;
    }

    @JSONField(name = "sprout_saturated")
    public int getBeautyBaoHe() {
        return beautyBaoHe;
    }

    @JSONField(name = "sprout_saturated")
    public void setBeautyBaoHe(int beautyBaoHe) {
        this.beautyBaoHe = beautyBaoHe;
    }

    @JSONField(name = "sprout_pink")
    public int getBeautyFenNen() {
        return beautyFenNen;
    }

    @JSONField(name = "sprout_pink")
    public void setBeautyFenNen(int beautyFenNen) {
        this.beautyFenNen = beautyFenNen;
    }

    @JSONField(name = "sprout_eye")
    public int getBeautyBigEye() {
        return beautyBigEye;
    }

    @JSONField(name = "sprout_eye")
    public void setBeautyBigEye(int beautyBigEye) {
        this.beautyBigEye = beautyBigEye;
    }

    @JSONField(name = "sprout_face")
    public int getBeautyFace() {
        return beautyFace;
    }

    @JSONField(name = "sprout_face")
    public void setBeautyFace(int beautyFace) {
        this.beautyFace = beautyFace;
    }


    public String[] getVideoShareTypes() {
        return shareType;
    }

    @JSONField(name = "video_share_title")
    public String getVideoShareTitle() {
        return videoShareTitle;
    }

    @JSONField(name = "video_share_title")
    public void setVideoShareTitle(String videoShareTitle) {
        this.videoShareTitle = videoShareTitle;
    }

    @JSONField(name = "video_share_des")
    public String getVideoShareDes() {
        return videoShareDes;
    }

    @JSONField(name = "video_share_des")
    public void setVideoShareDes(String videoShareDes) {
        this.videoShareDes = videoShareDes;
    }

    @JSONField(name = "video_audit_switch")
    public int getVideoAuditSwitch() {
        return videoAuditSwitch;
    }

    @JSONField(name = "video_audit_switch")
    public void setVideoAuditSwitch(int videoAuditSwitch) {
        this.videoAuditSwitch = videoAuditSwitch;
    }

    @JSONField(name = "cloudtype")
    public int getVideoCloudType() {
        return videoCloudType;
    }

    @JSONField(name = "cloudtype")
    public void setVideoCloudType(int videoCloudType) {
        this.videoCloudType = videoCloudType;
    }

    @JSONField(name = "qiniu_domain")
    public String getVideoQiNiuHost() {
        return videoQiNiuHost;
    }

    @JSONField(name = "qiniu_domain")
    public void setVideoQiNiuHost(String videoQiNiuHost) {
        this.videoQiNiuHost = videoQiNiuHost;
    }

    @JSONField(name = "txcloud_appid")
    public String getTxCosAppId() {
        return txCosAppId;
    }

    @JSONField(name = "txcloud_appid")
    public void setTxCosAppId(String txCosAppId) {
        this.txCosAppId = txCosAppId;
    }

    @JSONField(name = "txcloud_region")
    public String getTxCosRegion() {
        return txCosRegion;
    }

    @JSONField(name = "txcloud_region")
    public void setTxCosRegion(String txCosRegion) {
        this.txCosRegion = txCosRegion;
    }

    @JSONField(name = "txcloud_bucket")
    public String getTxCosBucketName() {
        return txCosBucketName;
    }

    @JSONField(name = "txcloud_bucket")
    public void setTxCosBucketName(String txCosBucketName) {
        this.txCosBucketName = txCosBucketName;
    }

    @JSONField(name = "txvideofolder")
    public String getTxCosVideoPath() {
        return txCosVideoPath;
    }

    @JSONField(name = "txvideofolder")
    public void setTxCosVideoPath(String txCosVideoPath) {
        this.txCosVideoPath = txCosVideoPath;
    }

    @JSONField(name = "tximgfolder")
    public String getTxCosImagePath() {
        return txCosImagePath;
    }

    @JSONField(name = "tximgfolder")
    public void setTxCosImagePath(String txCosImagePath) {
        this.txCosImagePath = txCosImagePath;
    }

    @JSONField(name = "guide")
    public String getAdInfo() {
        return mAdInfo;
    }
    @JSONField(name = "guide")
    public void setAdInfo(String adInfo) {
        mAdInfo = adInfo;
    }

    public List<VideoClassBean> getVideoclass() {
        return videoclass;
    }

    public void setVideoclass(List<VideoClassBean> videoclass) {
        this.videoclass = videoclass;
    }
}
