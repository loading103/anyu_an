package com.yunbao.live.http;

import android.text.TextUtils;

import com.lzy.okgo.request.PostRequest;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.live.LiveConfig;

import java.io.File;

/**
 * Created by cxf on 2019/3/21.
 */

public class LiveHttpUtil {

    private static final String SALT = "76576076c1f5f657b634e966c8836a06";

    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }

    /**
     * 获取当前直播间的用户列表
     */
    public static void getUserList(String liveuid, String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.getUserLists", LiveHttpConsts.GET_USER_LIST)
                .params("liveuid", liveuid)
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 当直播间是门票收费，计时收费或切换成计时收费的时候，观众请求这个接口
     *
     * @param liveUid 主播的uid
     * @param stream  主播的stream
     */
    public static void roomCharge(String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.roomCharge", LiveHttpConsts.ROOM_CHARGE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("liveuid", liveUid)
                .execute(callback);

    }

    /**
     * 当直播间是计时收费的时候，观众每隔一分钟请求这个接口进行扣费
     *
     * @param liveUid 主播的uid
     * @param stream  主播的stream
     */
    public static void timeCharge(String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.timeCharge", LiveHttpConsts.TIME_CHARGE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("liveuid", liveUid)
                .execute(callback);
    }


    /**
     * 获取用户余额
     */
    public static void getCoin(HttpCallback callback) {
        HttpClient.getInstance().get("Live.getCoin", LiveHttpConsts.GET_COIN)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 获取用户的直播记录
     *
     * @param touid 对方的uid
     */
    public static void getLiveRecord(String touid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("User.getLiverecord", LiveHttpConsts.GET_LIVE_RECORD)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取直播回放url
     *
     * @param recordId 视频的id
     */
    public static void getAliCdnRecord(String recordId, HttpCallback callback) {
        HttpClient.getInstance().get("User.getAliCdnRecord", LiveHttpConsts.GET_ALI_CDN_RECORD)
                .params("id", recordId)
                .execute(callback);
    }


    /**
     * 获取主播印象列表
     */
    public static void getAllImpress(String touid, HttpCallback callback) {
        HttpClient.getInstance().get("User.GetUserLabel", LiveHttpConsts.GET_ALL_IMPRESS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 给主播设置印象
     */
    public static void setImpress(String touid, String ImpressIDs, HttpCallback callback) {
        HttpClient.getInstance().get("User.setUserLabel", LiveHttpConsts.SET_IMPRESS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("labels", ImpressIDs)
                .execute(callback);
    }


    /**
     * 获取当前直播间的管理员列表
     */
    public static void getAdminList(String liveUid, HttpCallback callback) {
        HttpClient.getInstance().get("Live.getAdminList", LiveHttpConsts.GET_ADMIN_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .execute(callback);
    }

    /**
     * 主播设置或取消直播间的管理员
     */
    public static void setAdmin(String liveUid, String touid, HttpCallback callback) {
        HttpClient.getInstance().get("Live.setAdmin", LiveHttpConsts.SET_ADMIN)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 获取直播间的禁言列表
     */
    public static void getLiveShutUpList(String liveUid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Livemanage.getShutList", LiveHttpConsts.GET_LIVE_SHUT_UP_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 直播间解除禁言
     */
    public static void liveCancelShutUp(String liveUid, String toUid, HttpCallback callback) {
        HttpClient.getInstance().get("Livemanage.cancelShut", LiveHttpConsts.LIVE_CANCEL_SHUT_UP)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("touid", toUid)
                .execute(callback);
    }

    /**
     * 获取直播间的拉黑列表
     */
    public static void getLiveBlackList(String liveUid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Livemanage.getKickList", LiveHttpConsts.GET_LIVE_BLACK_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 直播间解除拉黑
     */
    public static void liveCancelBlack(String liveUid, String toUid, HttpCallback callback) {
        HttpClient.getInstance().get("Livemanage.cancelKick", LiveHttpConsts.LIVE_CANCEL_BLACK)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("touid", toUid)
                .execute(callback);
    }


    /**
     * 直播结束后，获取直播收益，观看人数，时长等信息
     */
    public static void getLiveEndInfo(String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.stopInfo", LiveHttpConsts.GET_LIVE_END_INFO)
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 获取直播间举报内容列表
     */
    public static void getLiveReportList(HttpCallback callback) {
        HttpClient.getInstance().get("Live.getReportClass", LiveHttpConsts.GET_LIVE_REPORT_LIST)
                .execute(callback);
    }

    /**
     * 举报用户
     */
    public static void setReport(String touid, String content, HttpCallback callback) {
        HttpClient.getInstance().get("Live.setReport", LiveHttpConsts.SET_REPORT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("content", content)
                .execute(callback);
    }

    /**
     * 直播间点击聊天列表和头像出现的弹窗
     */
    public static void getLiveUser(String touid, String liveUid, HttpCallback callback) {
        HttpClient.getInstance().get("Live.getPop", LiveHttpConsts.GET_LIVE_USER)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("liveuid", liveUid)
                .execute(callback);
    }

    /**
     * 主播或管理员踢人
     */
    public static void kicking(String liveUid, String touid, HttpCallback callback) {
        HttpClient.getInstance().get("Live.kicking", LiveHttpConsts.KICKING)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("touid", touid)
                .execute(callback);
    }


    /**
     * 主播或管理员禁言
     */
    public static void setShutUp(String liveUid, String stream, int type, String touid, HttpCallback callback) {
        HttpClient.getInstance().get("Live.setShutUp", LiveHttpConsts.SET_SHUT_UP)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("type", type)
                .params("touid", touid)
                .execute(callback);
    }


    /**
     * 超管关闭直播间或禁用账户
     * @param type  0表示关闭当前直播 1表示禁播，2表示封禁账号
     */
    public static void superCloseRoom(String liveUid, int type, HttpCallback callback) {
        HttpClient.getInstance().get("Live.superStopRoom", LiveHttpConsts.SUPER_CLOSE_ROOM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("type", type)
                .execute(callback);
    }


    /**
     * 守护商品类型列表
     */
    public static void getGuardBuyList(HttpCallback callback) {
        HttpClient.getInstance().get("Guard.getList", LiveHttpConsts.GET_GUARD_BUY_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 购买守护接口
     */
    public static void buyGuard(String liveUid, String stream, int guardId, HttpCallback callback) {
        HttpClient.getInstance().get("Guard.BuyGuard", LiveHttpConsts.BUY_GUARD)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("guardid", guardId)
                .execute(callback);
    }


    /**
     * 查看主播的守护列表
     */
    public static void getGuardList(String liveUid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Guard.GetGuardList", LiveHttpConsts.GET_GUARD_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("p", p)
                .execute(callback);
    }


    /**
     * 观众跟主播连麦时，获取自己的流地址
     */
    public static void getLinkMicStream(HttpCallback callback) {
        HttpClient.getInstance().get("Linkmic.RequestLVBAddrForLinkMic", LiveHttpConsts.GET_LINK_MIC_STREAM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .execute(callback);
    }

    /**
     * 主播连麦成功后，要把这些信息提交给服务器
     *
     * @param touid    连麦用户ID
     * @param pull_url 连麦用户播流地址
     */
    public static void linkMicShowVideo(String touid, String pull_url) {
        HttpClient.getInstance().get("Live.showVideo", LiveHttpConsts.LINK_MIC_SHOW_VIDEO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("pull_url", pull_url)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {

                    }
                });
    }


    /**
     * 主播设置是否允许观众发起连麦
     */
    public static void setLinkMicEnable(boolean linkMicEnable, HttpCallback callback) {
        HttpClient.getInstance().get("Linkmic.setMic", LiveHttpConsts.SET_LINK_MIC_ENABLE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("ismic", linkMicEnable ? 1 : 0)
                .execute(callback);
    }


    /**
     * 观众检查主播是否允许连麦
     */
    public static void checkLinkMicEnable(String liveUid, HttpCallback callback) {
        HttpClient.getInstance().get("Linkmic.isMic", LiveHttpConsts.CHECK_LINK_MIC_ENABLE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("liveuid", liveUid)
                .execute(callback);
    }

    /**
     * 连麦pk检查对方主播在线状态
     */
    public static void livePkCheckLive(String liveUid, String stream, String uidStream, HttpCallback callback) {
        HttpClient.getInstance().get("Livepk.checkLive", LiveHttpConsts.LIVE_PK_CHECK_LIVE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("uid_stream", uidStream)
                .execute(callback);
    }

    /**
     * 直播间发红包
     */
    public static void sendRedPack(String stream, String coin, String count, String title, int type, int sendType, HttpCallback callback) {
        HttpClient.getInstance().get("Red.SendRed", LiveHttpConsts.SEND_RED_PACK)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("coin", coin)
                .params("nums", count)
                .params("des", title)
                .params("type", type)
                .params("type_grant", sendType)
                .execute(callback);
    }

    /**
     * 获取直播间红包列表
     */
    public static void getRedPackList(String stream, HttpCallback callback) {
        String sign = MD5Util.getMD5("stream=" + stream + "&" + SALT);
        HttpClient.getInstance().get("Red.GetRedList", LiveHttpConsts.GET_RED_PACK_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 直播间抢红包
     */
    public static void robRedPack(String stream, int redPackId, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String sign = MD5Util.getMD5("redid=" + redPackId + "&stream=" + stream + "&uid=" + uid + "&" + SALT);
        HttpClient.getInstance().get("Red.RobRed", LiveHttpConsts.ROB_RED_PACK)
                .params("uid", uid)
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("redid", redPackId)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 直播间红包领取详情
     */
    public static void getRedPackResult(String stream, int redPackId, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String sign = MD5Util.getMD5("redid=" + redPackId + "&stream=" + stream + "&" + SALT);
        HttpClient.getInstance().get("Red.GetRedRobList", LiveHttpConsts.GET_RED_PACK_RESULT)
                .params("uid", uid)
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("redid", redPackId)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 发送弹幕
     */
    public static void sendDanmu(String content, String liveUid, String stream, String or_uid,HttpCallback callback) {
        HttpClient.getInstance().get("Live.sendBarrage", LiveHttpConsts.SEND_DANMU)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("giftid", "1")
                .params("giftcount", "1")
                .params("content", content)
                .params("or_uid", or_uid)
                .execute(callback);
    }

    /**
     * 检查直播间状态，是否收费 是否有密码等
     *
     * @param liveUid 主播的uid
     * @param stream  主播的stream
     */
    public static void checkLive(String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.checkLive", LiveHttpConsts.CHECK_LIVE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .execute(callback);
    }


    /**
     * 观众进入直播间
     */
    public static void enterRoom(String liveUid, String stream,String or_uid, HttpCallback callback) {
        HttpClient.getInstance().get("Live.enterRoom", LiveHttpConsts.ENTER_ROOM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("city", CommonAppConfig.getInstance().getCity())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("or_uid", or_uid)
//                .params("pull", CommonAppConfig.getInstance().getFastPull())
                .execute(callback);
    }


    /**
     * 获取礼物列表，同时会返回剩余的钱
     */
    public static void getGiftList(HttpCallback callback) {
        HttpClient.getInstance().get("Live.getGiftList", LiveHttpConsts.GET_GIFT_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 观众给主播送礼物
     */
    public static void sendGift(String liveUid, String stream, int giftId, String giftCount,String or_uid, HttpCallback callback) {
        HttpClient.getInstance().get("Live.sendGift", LiveHttpConsts.SEND_GIFT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("giftid", giftId)
                .params("giftcount", giftCount)
                .params("or_uid", or_uid)
                .execute(callback);
    }

    /**
     * 连麦pk搜索主播
     */
    public static void livePkSearchAnchor(String key, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Livepk.Search", LiveHttpConsts.LIVE_PK_SEARCH_ANCHOR)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("key", key)
                .params("p", p)
                .execute(callback);
    }


    /**
     * 获取主播连麦pk列表
     */
    public static void getLivePkList(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Livepk.GetLiveList", LiveHttpConsts.GET_LIVE_PK_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    /**
     * 主播添加背景音乐时，搜索歌曲
     *
     * @param key      关键字
     * @param callback
     */
    public static void searchMusic(String key, HttpCallback callback) {
        HttpClient.getInstance().get("Livemusic.searchMusic", LiveHttpConsts.SEARCH_MUSIC)
                .params("key", key)
                .execute(callback);
    }

    /**
     * 获取歌曲的地址 和歌词的地址
     */
    public static void getMusicUrl(String musicId, HttpCallback callback) {
        HttpClient.getInstance().get("Livemusic.getDownurl", LiveHttpConsts.GET_MUSIC_URL)
                .params("audio_id", musicId)
                .execute(callback);
    }


    /**
     * 主播开播
     *
     * @param title    直播标题
     * @param type     直播类型 普通 密码 收费等
     * @param typeVal  密码 价格等
     * @param file     封面图片文件
     * @param callback
     */
    public static void createRoom(String title, int liveClassId,int liveGoodsId, int type, int typeVal, String file,String fastPush, HttpCallback callback) {
        CommonAppConfig appConfig = CommonAppConfig.getInstance();
        UserBean u = appConfig.getUserBean();
        if (u == null) {
            return;
        }
        L.e("WOLF","file:request");
        PostRequest<JsonBean> request = HttpClient.getInstance().post("Live.createRoom", LiveHttpConsts.CREATE_ROOM)
                .params("uid", appConfig.getUid())
                .params("token", appConfig.getToken())
                .params("user_nicename", u.getUserNiceName())
                .params("avatar", u.getAvatar())
                .params("avatar_thumb", u.getAvatarThumb())
                .params("city", appConfig.getCity())
                .params("province", appConfig.getProvince())
                .params("lat", appConfig.getLat())
                .params("lng", appConfig.getLng())
                .params("title", title)
                .params("liveclassid", liveClassId)
                .params("goods_id", liveGoodsId)
                .params("type", type)
                .params("type_val", typeVal)
                .params("push", fastPush)
                .params("deviceinfo", LiveConfig.getSystemParams());
        L.e("WOLF","file:11"+file);
        if (file != null) {
            L.e("WOLF","file:"+file);
            request.params("file", file);
        }
        request.execute(callback);
    }

    /**
     * 修改直播状态
     */
    public static void changeLive(String stream) {
        HttpClient.getInstance().get("Live.changeLive", LiveHttpConsts.CHANGE_LIVE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("status", "1")
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        L.e("开播---changeLive---->" + info[0]);
                    }
                });
    }

    /**
     * 主播结束直播
     */
    public static void stopLive(String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.stopRoom", LiveHttpConsts.STOP_LIVE)
                .params("stream", stream)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 主播开播前获取sdk类型  0金山  1腾讯
     */
    public static void getLiveSdk(String ramSize,HttpCallback callback) {
        HttpClient.getInstance().get("Live.getSDK", LiveHttpConsts.GET_LIVE_SDK)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("android_device",ramSize)
                .execute(callback);
    }


    /**
     * 腾讯sdk 跟主播连麦时，获取主播的低延时流
     */
    public static void getTxLinkMicAccUrl(String originStreamUrl, HttpCallback callback) {
        HttpClient.getInstance().get("Linkmic.RequestPlayUrlWithSignForLinkMic", LiveHttpConsts.GET_TX_LINK_MIC_ACC_URL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("originStreamUrl", originStreamUrl)
                .execute(callback);
    }


    /**
     * 连麦时候 主播混流
     */
    public static void linkMicTxMixStream(String mergeparams) {
        HttpClient.getInstance().get("Linkmic.MergeVideoStream", LiveHttpConsts.LINK_MIC_TX_MIX_STREAM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("mergeparams", mergeparams)
                .execute(CommonHttpUtil.NO_CALLBACK);
    }


    /**
     * 我是哪些直播间的管理员，返回这些直播间列表
     */
    public static void getMyAdminRoomList(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Livemanage.GetRoomList", LiveHttpConsts.GET_MY_ADMIN_ROOM_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }


    /**
     * 获取直播间奖池等级
     */
    public static void getLiveGiftPrizePool(String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Jackpot.GetJackpot", LiveHttpConsts.GET_LIVE_GIFT_PRIZE_POOL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 直播间右侧h5
     */
    public static void getAdvert(int type,HttpCallback callback) {
        HttpClient.getInstance().get("Home.getadvert", LiveHttpConsts.GET_ADVERT)
                .params("type", type)
                .params("uid",  CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 直播间右侧h5
     * live_class_id:直播分类ID,不传时获取所有商品
     */
    public static void getGoods(String live_class_id,HttpCallback callback) {
        HttpClient.getInstance().get("Live.GetGoods", LiveHttpConsts.LIVE_GETGOODS)
                .params("live_class_id", live_class_id)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .execute(callback);
    }

    /**
     * 直播间右侧更多
     * cat_idname:分类标识,不传时获取所有
     */
    public static void getLocation(String cat_idname,HttpCallback callback) {
        HttpClient.getInstance().get("Live.GetLocation", LiveHttpConsts.GET_LOCATION)
                .params("cat_idname", cat_idname)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .execute(callback);
    }

    /**
     * 充值页面，我的钻石
     */
    public static void getBalance(HttpCallback callback) {
        HttpClient.getInstance().get("User.getBalance", LiveHttpConsts.GET_BALANCE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 获取文件上传url信息
     */
    public static void getUploadUrlInfo(HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        HttpClient.getInstance().get("Upload.GetUploadUrlInfo", LiveHttpConsts.GET_UPLOAD_URL_INFO)
                .params("uid", uid)
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }
    public static void getUploadMyUrlInfo(String uid,HttpCallback callback) {
        HttpClient.getInstance().get("Upload.GetUploadUrlInfo", LiveHttpConsts.GET_UPLOAD_URL_INFO)
                .params("uid", uid)
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }
    /**
     * 获取视频评论
     */
    public static void getVideoCommentList(String videoid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Video.GetComments", LiveHttpConsts.GET_VIDEO_COMMENT_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("videoid", videoid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 购买商品后出发发送直播间内消息
     */
    public static void doRedirect(String roomnum,String url,String goods_name, String outh_param,String goods_id,String active_id, HttpCallback callback) {
        HttpClient.getInstance().get("Live.Redirect", LiveHttpConsts.DO_REDIRECT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("roomnum", roomnum)
                .params("url", url)
                .params("goods_name", goods_name)
                .params("outh_param", outh_param)
                .params("goods_id", goods_id)
                .params("active_id", active_id)
                .execute(callback);
    }

    /**
     * 用于直播间触发僵尸粉购买消息
     */
    public static void doMachineBuy(int time,String goods_id,String room_id, HttpCallback callback) {
        HttpClient.getInstance().get("Live.MachineBuy", LiveHttpConsts.DO_MACHINEBUY)
                .params("time", time)
                .params("goods_id", goods_id)
                .params("room_id", room_id)
                .execute(callback);
    }

    /**
     * 用户申请私密互动
     */
    public static void doApplyPri(String nickname,String photo,String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.applyPri", LiveHttpConsts.DO_APPLY_PRI)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("nickname", nickname)
                .params("photo", photo)
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 用户报名倒计时结束,主播获取申请用户列表
     */
    public static void getLiveHandle(String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.liveHandle", LiveHttpConsts.GET_LIVE_HANDLE)
                .params("liveuid", CommonAppConfig.getInstance().getUid())
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 主播关闭私密互动一键拒绝所有用户也是这个接口）
     */
    public static void doClosePrivate(String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.closePrivate", LiveHttpConsts.DO_CLOSE_PRIVATE)
                .params("liveuid", CommonAppConfig.getInstance().getUid())
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 主播开始私密互动
     */
    public static void startPrivate(String stream,String refuse_users, HttpCallback callback) {
        HttpClient.getInstance().get("Live.startPrivate", LiveHttpConsts.DO_START_PRIVATE)
                .params("liveuid", CommonAppConfig.getInstance().getUid())
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("refuse_users", refuse_users)
                .execute(callback);
    }

    /**
     * 检查开播状态
     */
    public static void checkLiveing(String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.checkLiveing", LiveHttpConsts.CHECK_LIVEING)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 获取新的推拉流地址
     */
    public static void resetLiveUrl(int type,String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.resetLiveUrl", LiveHttpConsts.RESET_LIVE_URL)
                .params("type", type)
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 直播改变直播类型
     */
    public static void changeLiveType(int type, int typeVal,String mStream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.ChangeLiveType", LiveHttpConsts.CHANGE_LIVE_TYPE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("type", type)
                .params("type_val", typeVal)
                .params("stream", mStream)
                .execute(callback);
    }
}

