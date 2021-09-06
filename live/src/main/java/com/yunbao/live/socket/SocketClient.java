package com.yunbao.live.socket;


import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.google.gson.jpush.Gson;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.SocketListReFreshEvent;
import com.yunbao.common.greendao.GreenDaoUtils;
import com.yunbao.common.greendao.entity.SocketListBean;
import com.yunbao.common.greendao.entity.SocketMessageBean;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.bean.LiveBuyGuardMsgBean;
import com.yunbao.live.bean.LiveChatBean;
import com.yunbao.live.bean.LiveDanMuBean;
import com.yunbao.live.bean.LiveEnterRoomBean;
import com.yunbao.live.bean.LiveGiftPrizePoolWinBean;
import com.yunbao.live.bean.LiveLuckGiftWinBean;
import com.yunbao.live.bean.LiveReceiveGiftBean;
import com.yunbao.live.bean.LiveUserGiftBean;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.regex.Pattern;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by cxf on 2018/10/9.
 */

public class SocketClient {
    private static final String TAG = "socket";
    private Socket mSocket;
    private String mLiveUid;
    private String mStream;
    private String mContribution;
    private SocketHandler mSocketHandler;

    public SocketClient(String url, SocketMessageListener listener) {
        if (!TextUtils.isEmpty(url)) {
            try {
                IO.Options option = new IO.Options();
                option.forceNew = true;
                option.reconnection = true;
                option.reconnectionDelay = 2000;
                option.reconnectionAttempts=20;
                option.transports = new String[]{"websocket"};

                mSocket = IO.socket(url, option);
                mSocket.on(Socket.EVENT_CONNECT, mConnectListener);//连接成功
                mSocket.on(Socket.EVENT_DISCONNECT, mDisConnectListener);//断开连接
                mSocket.on(Socket.EVENT_CONNECT_ERROR, mErrorListener);//连接错误
                mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, mTimeOutListener);//连接超时
                mSocket.on(Socket.EVENT_RECONNECT, mReConnectListener);//重连
                mSocket.on(Constants.SOCKET_CONN, onConn);//连接socket消息
                mSocket.on(Constants.SOCKET_BROADCAST, onBroadcast);//接收服务器广播的具体业务逻辑相关的消息
                mSocket.on(Constants.SOCKET_LETTER, onPrivateBroadcast);//接收服务器广播的具体业务逻辑相关的消息
                mSocketHandler = new SocketHandler(listener);
            } catch (Exception e) {
                L.e(TAG, "socket url 异常--->" + e.getMessage());
            }
        }
    }

    public void connect(String liveuid, String stream, String contribution) {
        mLiveUid = liveuid;
        mStream = stream;
        mContribution = contribution;
        if (mSocket != null) {
            mSocket.connect();
        }
        if (mSocketHandler != null) {
            mSocketHandler.setLiveUid(liveuid);
        }
    }

    public void disConnect() {
        if (mSocket != null) {
            mSocket.close();
            mSocket.off();
        }
        if (mSocketHandler != null) {
            mSocketHandler.release();
        }
        mSocketHandler = null;
        mLiveUid = null;
        mStream = null;
        mContribution = null;
    }

    /**
     * 向服务发送连接消息
     */
    private void conn() {
        org.json.JSONObject data = new org.json.JSONObject();
        try {
            data.put("uid", CommonAppConfig.getInstance().getUid());
            data.put("token", CommonAppConfig.getInstance().getToken());
            data.put("liveuid", mLiveUid);
            data.put("roomnum", mLiveUid);
            data.put("stream", mStream);
            data.put("contribution", mContribution);
            data.put("livePhone", SpUtil.getInstance().getBooleanValue(SpUtil.NEW_USER_CALL) ? "1" : "0");
            mSocket.emit("conn", data);
            SpUtil.getInstance().setBooleanValue(SpUtil.NEW_USER_CALL, false);
            SpUtil.getInstance().setBooleanValue(SpUtil.NEW_USER, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener mConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--onConnect-->" + new Gson().toJson(args));
            conn();
        }
    };

    private Emitter.Listener mReConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--reConnect-->" + new Gson().toJson(args));
            //conn();
            Message msg = Message.obtain();
            msg.what = Constants.SOCKET_WHAT_RECONNECT;
            if (mSocketHandler != null) {
                mSocketHandler.sendMessage(msg);
            }
        }
    };

    private Emitter.Listener mDisConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--onDisconnect-->" + new Gson().toJson(args));
            if (mSocketHandler != null) {
                mSocketHandler.sendEmptyMessage(Constants.SOCKET_WHAT_DISCONN);
            }
        }
    };

    private Emitter.Listener mErrorListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--onConnectError-->" + args);
        }
    };

    private Emitter.Listener mTimeOutListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--onConnectTimeOut-->" + args);
        }
    };

    private Emitter.Listener onConn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mSocketHandler != null) {
                try {
                    String s = ((JSONArray) args[0]).getString(0);
                    L.e(TAG, "--onConn-->" + s);
                    Message msg = Message.obtain();
                    msg.what = Constants.SOCKET_WHAT_CONN;
                    msg.obj = s.equals("ok");
                    if (mSocketHandler != null && msg != null) {
                        mSocketHandler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Emitter.Listener onBroadcast = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mSocketHandler != null) {
                try {
                    JSONArray array = (JSONArray) args[0];
                    for (int i = 0; i < array.length(); i++) {
                        Message msg = Message.obtain();
                        msg.what = Constants.SOCKET_WHAT_BROADCAST;
                        if (Constants.SOCKET_STOP_PLAY.equals(array.getString(i))) {//关闭直播间
                            msg.obj = array.getString(i) + "|" + ((JSONArray) args[1]).getString(i);
                        } else {
                            msg.obj = array.getString(i);
                        }
                        if (mSocketHandler != null) {
                            mSocketHandler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };

    /**
     * 私信回调
     */
    private Emitter.Listener onPrivateBroadcast = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mSocketHandler != null) {
                try {
                    JSONArray array = (JSONArray) args[0];
                    for (int i = 0; i < array.length(); i++) {
                        Message msg = Message.obtain();
                        msg.what = Constants.SOCKET_PRIVATE_LETTER;
                        msg.obj = array.getString(i);
                        if (mSocketHandler != null) {
                            mSocketHandler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };

    public void send(SocketSendBean bean) {
        if (mSocket != null) {
            mSocket.emit(Constants.SOCKET_SEND, bean.create());
        }
    }

    public void sendPrivate(SocketSendBean bean) {
        if (mSocket != null) {
            mSocket.emit(Constants.SOCKET_LETTER, bean.create());
        }
    }


    private static class SocketHandler extends Handler {
        private SocketMessageListener mListener;
        private String mLiveUid;

        public SocketHandler(SocketMessageListener listener) {
            mListener = new WeakReference<>(listener).get();
        }

        public void setLiveUid(String liveUid) {
            mLiveUid = liveUid;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mListener == null) {
                return;
            }
            switch (msg.what) {
                case Constants.SOCKET_WHAT_CONN:
                    mListener.onConnect((Boolean) msg.obj);
                    break;
                case Constants.SOCKET_WHAT_BROADCAST:
                    processBroadcast((String) msg.obj);
                    break;
                case Constants.SOCKET_WHAT_DISCONN:
                    mListener.onDisConnect();
                    break;
                case Constants.SOCKET_PRIVATE_LETTER:
                    processPrivateLetter((String) msg.obj);
                    break;
                case Constants.SOCKET_WHAT_RECONNECT:
                    mListener.onReconnect();
                    break;
            }
        }

        /**
         * 私信回调
         */
        private void processPrivateLetter(String obj) {
            L.e("收到私信socket--->" + obj);
            SocketReceiveBean received = JSON.parseObject(obj, SocketReceiveBean.class);
            JSONObject map = received.getMsg().getJSONObject(0);
            String retcode = received.getRetcode();

            switch (Integer.parseInt(retcode)) {
                case 000000: //接收消息
                    L.e("收到私信socket000000--->");
                    ReceivePrivateLetterMsg(map);
                    break;
                case 111111: //发送消息回执成功
                    L.e("收到私信socket11111--->");
                    ReceiveSendMsgSuccess(map);
                    break;
                case 222222: //发送消息回执失败
                    L.e("收到私信socket2222--->");
                    ReceiveSendMsgFail(map, received.getRetmsg());
                    break;
            }
        }

        /**
         * 直播间回调
         */
        private void processBroadcast(String socketMsg) {
            L.e("收到socket--->" + socketMsg);
            if (Constants.SOCKET_STOP_PLAY.equals(socketMsg.split("\\|")[0])) {
                mListener.onSuperCloseLive(socketMsg.split("\\|")[1]);//超管关闭房间
                return;
            }
            SocketReceiveBean received = JSON.parseObject(socketMsg, SocketReceiveBean.class);
            JSONObject map = received.getMsg().getJSONObject(0);
            switch (map.getString("_method_")) {
                case Constants.SOCKET_SYSTEM://系统消息
                    if (map.getString("msgtype").equals("1000") && map.getString("button") != null && map.getString("button").equals("1")) {//带按钮
                        if (CommonAppConfig.getInstance().isShowGoods()) {
                            systemChatButtonMessage(map.getString("ct"),
                                    map.getString("url"),
                                    map.getString("slide_show_type"),
                                    map.getString("bt_title"),
                                    map.getString("jump_type"),
                                    map.getString("type"),
                                    map.getString("is_king"),
                                    map.getString("jump_url_android"),
                                    map.getString("jump_type_android"));
                        }
                    } else if (map.getString("msgtype").equals("666")) {//只发礼物消息
                        String num = map.getIntValue("gift_count") == 1 ? "" : map.getIntValue("gift_count") + "";
                        String s = map.getString("uname") + "送了" + map.getString("gift_name") + "," + num + "x" + map.getIntValue("gift_num");
                        L.e("WOLF", "礼物：" + s);
                        systemGiftChatMessage(s, map.getString("gifticon_mini"));
                    } else if (map.getString("msgtype").equals("777")) {//指定金额购买成功弹幕动效
                        if (CommonAppConfig.getInstance().isShowGoods()) {
                            systemChatEffectMessage(map.getString("ct"), map.getString("effect"), map.getString("effect_txt"));
                        }
                    } else if (map.getIntValue("is_notice") == 1 && TextUtils.isEmpty(map.getString("uid"))) {//只有文字的购买成功消息
                        if (CommonAppConfig.getInstance().isShowGoods()) {
                            systemChatMessage(map.getString("ct"), map.getString("follow"), map.getString("type"), map.getString("uid"), map.getString("award_amount"));
                        }
                    } else {//普通系统消息
                        systemChatMessage(map.getString("ct"), map.getString("follow"), map.getString("type"), map.getString("uid"), map.getString("award_amount"));
                    }
                    break;
                case Constants.SOCKET_KICK://踢人
                    systemChatMessage(map.getString("ct"));
                    mListener.onKick(map.getString("touid"));
                    break;
                case Constants.SOCKET_SHUT_UP://禁言
                    String ct = map.getString("ct");
                    systemChatMessage(ct);
                    mListener.onShutUp(map.getString("touid"), ct);
                    break;
                case Constants.SOCKET_SEND_MSG://文字消息，点亮，用户进房间，这种混乱的设计是因为服务器端逻辑就是这样设计的,客户端无法自行修改
                    String msgtype = map.getString("msgtype");
                    if ("2".equals(msgtype)) {//发言，点亮
                        L.e("WOLF", "点亮");
                        if ("409002".equals(received.getRetcode())) {
                            ToastUtil.show(R.string.live_you_are_shut);
                            return;
                        }
                        LiveChatBean chatBean = new LiveChatBean();
                        chatBean.setId(map.getString("uid"));
                        chatBean.setUserNiceName(map.getString("uname"));
                        chatBean.setLevel(map.getIntValue("level"));
                        chatBean.setAnchor(map.getIntValue("isAnchor") == 1);
                        chatBean.setManager(map.getIntValue("usertype") == Constants.SOCKET_USER_TYPE_ADMIN);
                        chatBean.setContent(map.getString("ct"));
                        int heart = map.getIntValue("heart");
                        chatBean.setHeart(heart);
                        if (heart > 0) {
                            chatBean.setType(LiveChatBean.LIGHT);
                        }
                        chatBean.setLiangName(map.getString("liangname"));
                        chatBean.setVipType(map.getIntValue("vip_type"));
                        chatBean.setVip_is_king(map.getIntValue("vip_is_king") + "");
                        chatBean.setGuardType(map.getIntValue("guard_type"));
                        mListener.onChat(chatBean);
                    } else if ("0".equals(msgtype)) {//用户进入房间
                        JSONObject obj = JSON.parseObject(map.getString("ct"));
                        LiveUserGiftBean u = JSON.toJavaObject(obj, LiveUserGiftBean.class);
                        UserBean.Vip vip = new UserBean.Vip();
                        int vipType = obj.getIntValue("vip_type");
                        int isKing = obj.getIntValue("vip_is_king");
                        vip.setType(vipType);
                        u.setVip(vip);
                        UserBean.Car car = new UserBean.Car();
                        car.setId(obj.getIntValue("car_id"));
                        car.setSwf(obj.getString("car_swf"));
                        car.setSwftime(obj.getFloatValue("car_swftime"));
                        car.setWords(obj.getString("car_words"));
                        u.setCar(car);
                        UserBean.Liang liang = new UserBean.Liang();
                        String liangName = obj.getString("liangname");
                        liang.setName(liangName);
                        u.setLiang(liang);
                        u.setLivePhone(obj.getString("livePhone"));
                        LiveChatBean chatBean = new LiveChatBean();
                        chatBean.setType(LiveChatBean.ENTER_ROOM);
                        chatBean.setId(u.getId());
                        chatBean.setUserNiceName(u.getUserNiceName());
                        chatBean.setLevel(u.getLevel());
                        chatBean.setVipType(vipType);
                        chatBean.setVip_is_king(isKing + "");
                        chatBean.setLiangName(liangName);
                        chatBean.setManager(obj.getIntValue("usertype") == Constants.SOCKET_USER_TYPE_ADMIN);
                        chatBean.setContent(WordUtil.getString(R.string.live_enter_room));
                        chatBean.setGuardType(obj.getIntValue("guard_type"));

                        mListener.onEnterRoom(new LiveEnterRoomBean(u, chatBean));
                    }
                    break;
                case Constants.SOCKET_LIGHT://飘心
                    mListener.onLight();
                    break;
                case Constants.SOCKET_SEND_GIFT://送礼物
                    L.e("WOLF", "送礼物");
                    LiveReceiveGiftBean receiveGiftBean = JSON.parseObject(map.getString("ct"), LiveReceiveGiftBean.class);
                    receiveGiftBean.setAvatar(map.getString("uhead"));
                    receiveGiftBean.setUserNiceName(map.getString("uname"));
                    LiveChatBean chatBean = new LiveChatBean();
                    chatBean.setUserNiceName(receiveGiftBean.getUserNiceName());
                    chatBean.setLevel(receiveGiftBean.getLevel());
                    chatBean.setId(map.getString("uid"));
                    chatBean.setLiangName(map.getString("liangname"));
                    chatBean.setVipType(map.getIntValue("vip_type"));
                    chatBean.setVip_is_king(map.getIntValue("vip_is_king") + "");
                    chatBean.setType(LiveChatBean.GIFT);
                    chatBean.setContent(WordUtil.getString(R.string.live_send_gift_1) + receiveGiftBean.getGiftCount() + WordUtil.getString(R.string.live_send_gift_2) + receiveGiftBean.getGiftName());
                    receiveGiftBean.setLiveChatBean(chatBean);
                    if (map.getIntValue("ifpk") == 1) {
                        if (!TextUtils.isEmpty(mLiveUid)) {
                            if (mLiveUid.equals(map.getString("roomnum"))) {
                                mListener.onSendGift(receiveGiftBean);
                                mListener.onSendGiftPk(map.getLongValue("pktotal1"), map.getLongValue("pktotal2"));
                            } else {
                                mListener.onSendGiftPk(map.getLongValue("pktotal2"), map.getLongValue("pktotal1"));
                            }
                        }
                    } else {
                        if (map.getIntValue("only_danmu") == 0 || map.getIntValue("only_danmu") == 1) {
                            mListener.onSendGift(receiveGiftBean);
                        }

                        if (map.getIntValue("only_danmu") == 0 || map.getIntValue("only_danmu") == 2) {
                            String s = receiveGiftBean.getUserNiceName() + "送了" + receiveGiftBean.getGiftName() + ",x" +
                                    (map.getIntValue("gift_num") == 0 ? receiveGiftBean.getGiftCount() : map.getIntValue("gift_num"));
                            L.e("WOLF", "礼物：" + s);
                            systemGiftChatMessage(s, receiveGiftBean.getGifticon_mini());
                        }
                    }

                    break;
                case Constants.SOCKET_SEND_BARRAGE://发弹幕
                    LiveDanMuBean liveDanMuBean = JSON.parseObject(map.getString("ct"), LiveDanMuBean.class);
                    liveDanMuBean.setAvatar(map.getString("uhead"));
                    liveDanMuBean.setUserNiceName(map.getString("uname"));
                    mListener.onSendDanMu(liveDanMuBean);
                    systemDanmuChatMessage(liveDanMuBean.getContent(), liveDanMuBean.getUserNiceName());
                    break;
                case Constants.SOCKET_LEAVE_ROOM://离开房间
                    UserBean u = JSON.parseObject(map.getString("ct"), UserBean.class);
                    mListener.onLeaveRoom(u);
                    break;
                case Constants.SOCKET_LIVE_END://主播关闭直播
                    mListener.onLiveEnd(map.getString("ct"));
                    break;
                case Constants.SOCKET_CHANGE_LIVE://主播切换计时收费类型
                    mListener.onChangeTimeCharge(map.getIntValue("type"), map.getIntValue("type_val"));
                    break;
                case Constants.SOCKET_UPDATE_VOTES:
                    mListener.onUpdateVotes(map.getString("uid"), map.getString("votes"), map.getIntValue("isfirst"));
                    break;
                case Constants.SOCKET_FAKE_FANS:
                    JSONObject obj = map.getJSONObject("ct");
                    String s = obj.getJSONObject("data").getJSONArray("info").getJSONObject(0).getString("list");
                    L.e("僵尸粉--->" + s);
                    List<LiveUserGiftBean> list = JSON.parseArray(s, LiveUserGiftBean.class);
                    mListener.addFakeFans(list);
                    break;
                case Constants.SOCKET_SET_ADMIN://设置或取消管理员
                    systemChatMessage(map.getString("ct"));
                    mListener.onSetAdmin(map.getString("touid"), map.getIntValue("action"));
                    break;
                case Constants.SOCKET_BUY_GUARD://购买守护
                    LiveBuyGuardMsgBean buyGuardMsgBean = new LiveBuyGuardMsgBean();
                    buyGuardMsgBean.setUid(map.getString("uid"));
                    buyGuardMsgBean.setUserName(map.getString("uname"));
                    buyGuardMsgBean.setVotes(map.getString("votestotal"));
                    buyGuardMsgBean.setGuardNum(map.getIntValue("guard_nums"));
                    buyGuardMsgBean.setGuardType(map.getIntValue("guard_type"));
                    mListener.onBuyGuard(buyGuardMsgBean);
                    break;
                case Constants.SOCKET_LINK_MIC://连麦
                    processLinkMic(map);
                    break;
                case Constants.SOCKET_LINK_MIC_ANCHOR://主播连麦
                    processLinkMicAnchor(map);
                    break;
                case Constants.SOCKET_LINK_MIC_PK://主播PK
                    processAnchorLinkMicPk(map);
                    break;
                case Constants.SOCKET_RED_PACK://红包消息
                    String uid = map.getString("uid");
                    if (TextUtils.isEmpty(uid)) {
                        return;
                    }
                    LiveChatBean liveChatBean = new LiveChatBean();
                    liveChatBean.setType(LiveChatBean.RED_PACK);
                    liveChatBean.setId(uid);
                    String name = uid.equals(mLiveUid) ? WordUtil.getString(R.string.live_anchor) : map.getString("uname");
                    liveChatBean.setContent(name + map.getString("ct"));
                    mListener.onRedPack(liveChatBean);
                    break;
                case Constants.SOCKET_LUCK_WIN://幸运礼物中奖
                    mListener.onLuckGiftWin(map.toJavaObject(LiveLuckGiftWinBean.class));
                    break;
                case Constants.SOCKET_PRIZE_POOL_WIN://奖池中奖
                    mListener.onPrizePoolWin(map.toJavaObject(LiveGiftPrizePoolWinBean.class));
                    break;
                case Constants.SOCKET_PRIZE_POOL_UP://奖池升级
                    mListener.onPrizePoolUp(map.getString("uplevel"));
                    break;
                //游戏socket
                case Constants.SOCKET_GAME_ZJH://游戏 智勇三张
                    if (CommonAppConfig.GAME_ENABLE) {
                        mListener.onGameZjh(map);
                    }
                    break;
                case Constants.SOCKET_GAME_HD://游戏 海盗船长
                    if (CommonAppConfig.GAME_ENABLE) {
                        mListener.onGameHd(map);
                    }
                    break;
                case Constants.SOCKET_GAME_ZP://游戏 幸运转盘
                    if (CommonAppConfig.GAME_ENABLE) {
                        mListener.onGameZp(map);
                    }
                    break;
                case Constants.SOCKET_GAME_NZ://游戏 开心牛仔
                    if (CommonAppConfig.GAME_ENABLE) {
                        mListener.onGameNz(map);
                    }
                    break;
                case Constants.SOCKET_GAME_EBB://游戏 二八贝
                    if (CommonAppConfig.GAME_ENABLE) {
                        mListener.onGameEbb(map);
                    }
                    break;
                case Constants.SOCKET_PRIVATE_LIVE://私密直播
                    mListener.onSendPrivateLive(map.getIntValue("msgtype"), map.getString("user"));
                    break;
                case Constants.SOCKET_SWITCH_LIVE://切换推拉流
                    mListener.onSendSwitchLive(map.getIntValue("cdn_switch"));
                    break;
                case Constants.SOCKET_USER_LIST://主播请求用户列表推给用户
                    mListener.onSendUserList(map.getString("userlist"), map.getString("nums"));
                    break;
                case Constants.SOCKET_STOP_LIVE://超管关播（直播间超管关播）
                    mListener.onStopLive();
                    break;
            }
        }

        /**
         * 接收到系统消息，显示在聊天栏中
         */
        private void systemChatMessage(String content) {
            LiveChatBean bean = new LiveChatBean();
            bean.setContent(isBase64(content) ? ConvertUtils.bytes2String(EncodeUtils.base64Decode(content)) : content);
            bean.setType(LiveChatBean.SYSTEM);
            mListener.onChat(bean);
        }

        /**
         * 接收到系统消息，跑动画effect 为1时需要展示特效否则不用
         * amount  金额
         */
        private void systemChatEffectMessage(String content, String effect, String effect_tex) {
            LiveChatBean bean = new LiveChatBean();
            bean.setContent(isBase64(content) ? ConvertUtils.bytes2String(EncodeUtils.base64Decode(content)) : content);
            bean.setEffect_tex(isBase64(effect_tex) ? ConvertUtils.bytes2String(EncodeUtils.base64Decode(effect_tex)) : effect_tex);
            bean.setType(LiveChatBean.SYSTEM);
            bean.setEffect(effect);
            bean.setType_head("1");
            mListener.onChat(bean);
        }

        /**
         * 接收到系统消息，显示在聊天栏中
         */
        private void systemChatMessage(String content, String follow, String type, String uid, String award_amount) {
            LiveChatBean bean = new LiveChatBean();
            if (!TextUtils.isEmpty(follow)) {
                bean.setFollow(follow);
            }
            bean.setContent(isBase64(content) ? ConvertUtils.bytes2String(EncodeUtils.base64Decode(content)) : content);
            bean.setType(LiveChatBean.SYSTEM);
            bean.setType_head(type);
            if (!TextUtils.isEmpty(uid)) {
                bean.setUid(uid);
            }
            if (!TextUtils.isEmpty(award_amount)) {
                bean.setAward_amount(award_amount);
            }
            mListener.onChat(bean);
        }

        /**
         * 接收到系统消息(带Button)，显示在聊天栏中
         */
        private void systemChatButtonMessage(String content, String url, String slide_show_type,
                                             String bt_title, String jump_type, String type, String is_king
                , String jump_url_android, String jump_type_android) {
            LiveChatBean bean = new LiveChatBean();
            bean.setContent(isBase64(content) ? ConvertUtils.bytes2String(EncodeUtils.base64Decode(content)) : content);
            bean.setType(LiveChatBean.SYSTEM_BUTTON);
            bean.setUrl(url);
            bean.setSlide_show_type(slide_show_type);
            bean.setBt_title(bt_title);
            bean.setJump_type(jump_type);
            bean.setType_head(type);
            bean.setIs_king(is_king);
            bean.setJump_url_android(jump_url_android);
            bean.setJump_type_android(jump_type_android);
            mListener.onChat(bean);
        }

        private static boolean isBase64(String str) {
            String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
            return Pattern.matches(base64Pattern, str);
        }

        /**
         * 接收到礼物消息，显示在聊天栏中
         */
        private void systemGiftChatMessage(String content, String gift_mini) {
            LiveChatBean bean = new LiveChatBean();
            bean.setContent(content);
            bean.setType(LiveChatBean.SYSTEM_GIFT);
            bean.setGifticon_mini(gift_mini);
            mListener.onChat(bean);
        }

        /**
         * 接收到弹幕消息，显示在聊天栏中
         */
        private void systemDanmuChatMessage(String content, String name) {
            LiveChatBean bean = new LiveChatBean();
            bean.setContent(content);
            bean.setType(LiveChatBean.SYSTEM_DANMU);
            bean.setUserNiceName(name);
            mListener.onChat(bean);
        }

        /**
         * 处理观众与主播连麦逻辑
         */
        private void processLinkMic(JSONObject map) {
            int action = map.getIntValue("action");
            switch (action) {
                case 1://主播收到观众连麦的申请
                    UserBean u = new UserBean();
                    u.setId(map.getString("uid"));
                    u.setUserNiceName(map.getString("uname"));
                    u.setAvatar(map.getString("uhead"));
                    u.setSex(map.getIntValue("sex"));
                    u.setLevel(map.getIntValue("level"));
                    mListener.onAudienceApplyLinkMic(u);
                    break;
                case 2://观众收到主播同意连麦的消息
                    if (map.getString("touid").equals(CommonAppConfig.getInstance().getUid())) {
                        mListener.onAnchorAcceptLinkMic();
                    }
                    break;
                case 3://观众收到主播拒绝连麦的消息
                    if (map.getString("touid").equals(CommonAppConfig.getInstance().getUid())) {
                        mListener.onAnchorRefuseLinkMic();
                    }
                    break;
                case 4://所有人收到连麦观众发过来的流地址
                    String uid = map.getString("uid");
                    if (!TextUtils.isEmpty(uid) && !uid.equals(CommonAppConfig.getInstance().getUid())) {
                        mListener.onAudienceSendLinkMicUrl(uid, map.getString("uname"), map.getString("playurl"));
                    }
                    break;
                case 5://连麦观众自己断开连麦
                    mListener.onAudienceCloseLinkMic(map.getString("uid"), map.getString("uname"));
                    break;
                case 6://主播断开已连麦观众的连麦
                    mListener.onAnchorCloseLinkMic(map.getString("touid"), map.getString("uname"));
                    break;
                case 7://已申请连麦的观众收到主播繁忙的消息
                    if (map.getString("touid").equals(CommonAppConfig.getInstance().getUid())) {
                        mListener.onAnchorBusy();
                    }
                    break;
                case 8://已申请连麦的观众收到主播无响应的消息
                    if (map.getString("touid").equals(CommonAppConfig.getInstance().getUid())) {
                        mListener.onAnchorNotResponse();
                    }
                    break;
                case 9://所有人收到已连麦的观众退出直播间消息
                    mListener.onAudienceLinkMicExitRoom(map.getString("touid"));
                    break;
            }
        }

        /**
         * 处理主播与主播连麦逻辑
         *
         * @param map
         */
        private void processLinkMicAnchor(JSONObject map) {
            int action = map.getIntValue("action");
            switch (action) {
                case 1://收到其他主播连麦的邀请的回调
                    UserBean u = new UserBean();
                    u.setId(map.getString("uid"));
                    u.setUserNiceName(map.getString("uname"));
                    u.setAvatar(map.getString("uhead"));
                    u.setSex(map.getIntValue("sex"));
                    u.setLevel(map.getIntValue("level"));
                    u.setLevelAnchor(map.getIntValue("level_anchor"));
                    mListener.onLinkMicAnchorApply(u, map.getString("stream"));
                    break;
                case 3://对方主播拒绝连麦的回调
                    mListener.onLinkMicAnchorRefuse();
                    break;
                case 4://所有人收到对方主播的播流地址的回调
                    mListener.onLinkMicAnchorPlayUrl(map.getString("pkuid"), map.getString("pkpull"));
                    break;
                case 5://断开连麦的回调
                    mListener.onLinkMicAnchorClose();
                    break;
                case 7://对方主播正在忙的回调
                    mListener.onLinkMicAnchorBusy();
                    break;
                case 8://对方主播无响应的回调
                    mListener.onLinkMicAnchorNotResponse();
                    break;
                case 9://对方主播正在游戏
                    mListener.onlinkMicPlayGaming();
                    break;
            }
        }

        /**
         * 处理主播与主播PK逻辑
         *
         * @param map
         */
        private void processAnchorLinkMicPk(JSONObject map) {
            int action = map.getIntValue("action");
            switch (action) {
                case 1://收到对方主播PK回调
                    UserBean u = new UserBean();
                    u.setId(map.getString("uid"));
                    u.setUserNiceName(map.getString("uname"));
                    u.setAvatar(map.getString("uhead"));
                    u.setSex(map.getIntValue("sex"));
                    u.setLevel(map.getIntValue("level"));
                    u.setLevelAnchor(map.getIntValue("level_anchor"));
                    mListener.onLinkMicPkApply(u, map.getString("stream"));
                    break;
                case 3://对方主播拒绝PK的回调
                    mListener.onLinkMicPkRefuse();
                    break;
                case 4://所有人收到PK开始址的回调
                    mListener.onLinkMicPkStart(map.getString("pkuid"));
                    break;
                case 5://PK时候断开连麦的回调
                    mListener.onLinkMicPkClose();
                    break;
                case 7://对方主播正在忙的回调
                    mListener.onLinkMicPkBusy();
                    break;
                case 8://对方主播无响应的回调
                    mListener.onLinkMicPkNotResponse();
                    break;
                case 9://pk结束的回调
                    mListener.onLinkMicPkEnd(map.getString("win_uid"));
                    break;
            }
        }

        /**
         * 发送私信成功回执,失败回执，接收消息3种
         * sendState;//1是发送成功  2是发送失败
         * sendType; //1是自己发送  2是接收
         */
        private void ReceiveSendMsgFail(JSONObject map, String retmsg) {
            SocketMessageBean bean = getLetterBean(map, false);
            bean.setSendType("1");
            bean.setSendState("2");
            bean.setUserId(bean.getTargetUid());
            GreenDaoUtils.getInstance().insertSocketMessageData(bean);
            SaveAndUpdateSocketList(bean, bean.getTargetUid(), false);
            mListener.onReceiveSendMsgFail(bean, retmsg);
        }

        private void ReceiveSendMsgSuccess(JSONObject map) {
            SocketMessageBean bean = getLetterBean(map, false);
            bean.setSendType("1");
            bean.setSendState("1");
            bean.setUserId(bean.getTargetUid());
            GreenDaoUtils.getInstance().insertSocketMessageData(bean);
            SaveAndUpdateSocketList(bean, bean.getTargetUid(), false);
            mListener.onReceiveSendMsgSuccess(bean);
        }

        private void ReceivePrivateLetterMsg(JSONObject map) {
            SocketMessageBean bean = getLetterBean(map, true);
            bean.setSendType("2");
            bean.setSendState("1");
            bean.setUserId(bean.getSendUid());
            GreenDaoUtils.getInstance().insertSocketMessageData(bean);
            SaveAndUpdateSocketList(bean, bean.getSendUid(), true);
            mListener.onReceivePrivateLetterMsg(bean);
        }

        /**
         * 如果有消息列表就跟新时间和消息，若没得就插入数据库
         */
        private void SaveAndUpdateSocketList(SocketMessageBean bean, String userId, boolean isReceive) {
            SocketListBean beans = new SocketListBean();
            beans.setUserId(userId);
            beans.setContent(bean.getContent());
            beans.setCurrentUid(CommonAppConfig.getInstance().getUid());
            beans.setTime(bean.getTime());
            beans.setUnReadCount(isReceive ? 1 : 0); //如果是接收消息 为1   显示红点
            beans.setAvatar(!TextUtils.isEmpty(bean.getAvatar()) ? bean.getAvatar() : "");
            beans.setNickname(!TextUtils.isEmpty(bean.getNickname()) ? bean.getNickname() : "");
            SocketListBean socketBean = GreenDaoUtils.getInstance().querySocketListBean(userId);
            if (socketBean != null) {
                beans.set_id(socketBean.get_id());
                GreenDaoUtils.getInstance().updateSocketListData(beans);
            } else {
                GreenDaoUtils.getInstance().insertSocketListData(beans);
            }
            EventBus.getDefault().post(new SocketListReFreshEvent());
        }

        private SocketMessageBean getLetterBean(JSONObject map, boolean isreceive) {
            SocketMessageBean bean = new SocketMessageBean();
            bean.setMsgId(map.getString("id"));
            bean.setContent(map.getString("content"));
            bean.setSendUid(map.getString("sendUid"));
            bean.setTargetUid(map.getString("targetUid"));
            bean.setTime(map.getString("time"));
            bean.setType(map.getString("type"));
            bean.setUrl(map.getString("url"));
            if (isreceive) {
                if (!TextUtils.isEmpty(map.getString("nickname"))) {
                    bean.setNickname(map.getString("nickname"));
                }
                if (!TextUtils.isEmpty(map.getString("avatar"))) {
                    bean.setAvatar(map.getString("avatar"));
                }
            } else {
                if (!TextUtils.isEmpty(map.getString("toNickname"))) {
                    bean.setNickname(map.getString("toNickname"));
                }
                if (!TextUtils.isEmpty(map.getString("toAvatar"))) {
                    bean.setAvatar(map.getString("toAvatar"));
                }
            }
            return bean;
        }

        public void release() {
            mListener = null;
        }
    }
}
