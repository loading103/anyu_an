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
                mSocket.on(Socket.EVENT_CONNECT, mConnectListener);//????????????
                mSocket.on(Socket.EVENT_DISCONNECT, mDisConnectListener);//????????????
                mSocket.on(Socket.EVENT_CONNECT_ERROR, mErrorListener);//????????????
                mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, mTimeOutListener);//????????????
                mSocket.on(Socket.EVENT_RECONNECT, mReConnectListener);//??????
                mSocket.on(Constants.SOCKET_CONN, onConn);//??????socket??????
                mSocket.on(Constants.SOCKET_BROADCAST, onBroadcast);//?????????????????????????????????????????????????????????
                mSocket.on(Constants.SOCKET_LETTER, onPrivateBroadcast);//?????????????????????????????????????????????????????????
                mSocketHandler = new SocketHandler(listener);
            } catch (Exception e) {
                L.e(TAG, "socket url ??????--->" + e.getMessage());
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
     * ???????????????????????????
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
                        if (Constants.SOCKET_STOP_PLAY.equals(array.getString(i))) {//???????????????
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
     * ????????????
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
         * ????????????
         */
        private void processPrivateLetter(String obj) {
            L.e("????????????socket--->" + obj);
            SocketReceiveBean received = JSON.parseObject(obj, SocketReceiveBean.class);
            JSONObject map = received.getMsg().getJSONObject(0);
            String retcode = received.getRetcode();

            switch (Integer.parseInt(retcode)) {
                case 000000: //????????????
                    L.e("????????????socket000000--->");
                    ReceivePrivateLetterMsg(map);
                    break;
                case 111111: //????????????????????????
                    L.e("????????????socket11111--->");
                    ReceiveSendMsgSuccess(map);
                    break;
                case 222222: //????????????????????????
                    L.e("????????????socket2222--->");
                    ReceiveSendMsgFail(map, received.getRetmsg());
                    break;
            }
        }

        /**
         * ???????????????
         */
        private void processBroadcast(String socketMsg) {
            L.e("??????socket--->" + socketMsg);
            if (Constants.SOCKET_STOP_PLAY.equals(socketMsg.split("\\|")[0])) {
                mListener.onSuperCloseLive(socketMsg.split("\\|")[1]);//??????????????????
                return;
            }
            SocketReceiveBean received = JSON.parseObject(socketMsg, SocketReceiveBean.class);
            JSONObject map = received.getMsg().getJSONObject(0);
            switch (map.getString("_method_")) {
                case Constants.SOCKET_SYSTEM://????????????
                    if (map.getString("msgtype").equals("1000") && map.getString("button") != null && map.getString("button").equals("1")) {//?????????
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
                    } else if (map.getString("msgtype").equals("666")) {//??????????????????
                        String num = map.getIntValue("gift_count") == 1 ? "" : map.getIntValue("gift_count") + "";
                        String s = map.getString("uname") + "??????" + map.getString("gift_name") + "," + num + "x" + map.getIntValue("gift_num");
                        L.e("WOLF", "?????????" + s);
                        systemGiftChatMessage(s, map.getString("gifticon_mini"));
                    } else if (map.getString("msgtype").equals("777")) {//????????????????????????????????????
                        if (CommonAppConfig.getInstance().isShowGoods()) {
                            systemChatEffectMessage(map.getString("ct"), map.getString("effect"), map.getString("effect_txt"));
                        }
                    } else if (map.getIntValue("is_notice") == 1 && TextUtils.isEmpty(map.getString("uid"))) {//?????????????????????????????????
                        if (CommonAppConfig.getInstance().isShowGoods()) {
                            systemChatMessage(map.getString("ct"), map.getString("follow"), map.getString("type"), map.getString("uid"), map.getString("award_amount"));
                        }
                    } else {//??????????????????
                        systemChatMessage(map.getString("ct"), map.getString("follow"), map.getString("type"), map.getString("uid"), map.getString("award_amount"));
                    }
                    break;
                case Constants.SOCKET_KICK://??????
                    systemChatMessage(map.getString("ct"));
                    mListener.onKick(map.getString("touid"));
                    break;
                case Constants.SOCKET_SHUT_UP://??????
                    String ct = map.getString("ct");
                    systemChatMessage(ct);
                    mListener.onShutUp(map.getString("touid"), ct);
                    break;
                case Constants.SOCKET_SEND_MSG://???????????????????????????????????????????????????????????????????????????????????????????????????????????????,???????????????????????????
                    String msgtype = map.getString("msgtype");
                    if ("2".equals(msgtype)) {//???????????????
                        L.e("WOLF", "??????");
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
                    } else if ("0".equals(msgtype)) {//??????????????????
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
                case Constants.SOCKET_LIGHT://??????
                    mListener.onLight();
                    break;
                case Constants.SOCKET_SEND_GIFT://?????????
                    L.e("WOLF", "?????????");
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
                            String s = receiveGiftBean.getUserNiceName() + "??????" + receiveGiftBean.getGiftName() + ",x" +
                                    (map.getIntValue("gift_num") == 0 ? receiveGiftBean.getGiftCount() : map.getIntValue("gift_num"));
                            L.e("WOLF", "?????????" + s);
                            systemGiftChatMessage(s, receiveGiftBean.getGifticon_mini());
                        }
                    }

                    break;
                case Constants.SOCKET_SEND_BARRAGE://?????????
                    LiveDanMuBean liveDanMuBean = JSON.parseObject(map.getString("ct"), LiveDanMuBean.class);
                    liveDanMuBean.setAvatar(map.getString("uhead"));
                    liveDanMuBean.setUserNiceName(map.getString("uname"));
                    mListener.onSendDanMu(liveDanMuBean);
                    systemDanmuChatMessage(liveDanMuBean.getContent(), liveDanMuBean.getUserNiceName());
                    break;
                case Constants.SOCKET_LEAVE_ROOM://????????????
                    UserBean u = JSON.parseObject(map.getString("ct"), UserBean.class);
                    mListener.onLeaveRoom(u);
                    break;
                case Constants.SOCKET_LIVE_END://??????????????????
                    mListener.onLiveEnd(map.getString("ct"));
                    break;
                case Constants.SOCKET_CHANGE_LIVE://??????????????????????????????
                    mListener.onChangeTimeCharge(map.getIntValue("type"), map.getIntValue("type_val"));
                    break;
                case Constants.SOCKET_UPDATE_VOTES:
                    mListener.onUpdateVotes(map.getString("uid"), map.getString("votes"), map.getIntValue("isfirst"));
                    break;
                case Constants.SOCKET_FAKE_FANS:
                    JSONObject obj = map.getJSONObject("ct");
                    String s = obj.getJSONObject("data").getJSONArray("info").getJSONObject(0).getString("list");
                    L.e("?????????--->" + s);
                    List<LiveUserGiftBean> list = JSON.parseArray(s, LiveUserGiftBean.class);
                    mListener.addFakeFans(list);
                    break;
                case Constants.SOCKET_SET_ADMIN://????????????????????????
                    systemChatMessage(map.getString("ct"));
                    mListener.onSetAdmin(map.getString("touid"), map.getIntValue("action"));
                    break;
                case Constants.SOCKET_BUY_GUARD://????????????
                    LiveBuyGuardMsgBean buyGuardMsgBean = new LiveBuyGuardMsgBean();
                    buyGuardMsgBean.setUid(map.getString("uid"));
                    buyGuardMsgBean.setUserName(map.getString("uname"));
                    buyGuardMsgBean.setVotes(map.getString("votestotal"));
                    buyGuardMsgBean.setGuardNum(map.getIntValue("guard_nums"));
                    buyGuardMsgBean.setGuardType(map.getIntValue("guard_type"));
                    mListener.onBuyGuard(buyGuardMsgBean);
                    break;
                case Constants.SOCKET_LINK_MIC://??????
                    processLinkMic(map);
                    break;
                case Constants.SOCKET_LINK_MIC_ANCHOR://????????????
                    processLinkMicAnchor(map);
                    break;
                case Constants.SOCKET_LINK_MIC_PK://??????PK
                    processAnchorLinkMicPk(map);
                    break;
                case Constants.SOCKET_RED_PACK://????????????
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
                case Constants.SOCKET_LUCK_WIN://??????????????????
                    mListener.onLuckGiftWin(map.toJavaObject(LiveLuckGiftWinBean.class));
                    break;
                case Constants.SOCKET_PRIZE_POOL_WIN://????????????
                    mListener.onPrizePoolWin(map.toJavaObject(LiveGiftPrizePoolWinBean.class));
                    break;
                case Constants.SOCKET_PRIZE_POOL_UP://????????????
                    mListener.onPrizePoolUp(map.getString("uplevel"));
                    break;
                //??????socket
                case Constants.SOCKET_GAME_ZJH://?????? ????????????
                    if (CommonAppConfig.GAME_ENABLE) {
                        mListener.onGameZjh(map);
                    }
                    break;
                case Constants.SOCKET_GAME_HD://?????? ????????????
                    if (CommonAppConfig.GAME_ENABLE) {
                        mListener.onGameHd(map);
                    }
                    break;
                case Constants.SOCKET_GAME_ZP://?????? ????????????
                    if (CommonAppConfig.GAME_ENABLE) {
                        mListener.onGameZp(map);
                    }
                    break;
                case Constants.SOCKET_GAME_NZ://?????? ????????????
                    if (CommonAppConfig.GAME_ENABLE) {
                        mListener.onGameNz(map);
                    }
                    break;
                case Constants.SOCKET_GAME_EBB://?????? ?????????
                    if (CommonAppConfig.GAME_ENABLE) {
                        mListener.onGameEbb(map);
                    }
                    break;
                case Constants.SOCKET_PRIVATE_LIVE://????????????
                    mListener.onSendPrivateLive(map.getIntValue("msgtype"), map.getString("user"));
                    break;
                case Constants.SOCKET_SWITCH_LIVE://???????????????
                    mListener.onSendSwitchLive(map.getIntValue("cdn_switch"));
                    break;
                case Constants.SOCKET_USER_LIST://????????????????????????????????????
                    mListener.onSendUserList(map.getString("userlist"), map.getString("nums"));
                    break;
                case Constants.SOCKET_STOP_LIVE://???????????????????????????????????????
                    mListener.onStopLive();
                    break;
            }
        }

        /**
         * ?????????????????????????????????????????????
         */
        private void systemChatMessage(String content) {
            LiveChatBean bean = new LiveChatBean();
            bean.setContent(isBase64(content) ? ConvertUtils.bytes2String(EncodeUtils.base64Decode(content)) : content);
            bean.setType(LiveChatBean.SYSTEM);
            mListener.onChat(bean);
        }

        /**
         * ?????????????????????????????????effect ???1?????????????????????????????????
         * amount  ??????
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
         * ?????????????????????????????????????????????
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
         * ?????????????????????(???Button)????????????????????????
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
         * ?????????????????????????????????????????????
         */
        private void systemGiftChatMessage(String content, String gift_mini) {
            LiveChatBean bean = new LiveChatBean();
            bean.setContent(content);
            bean.setType(LiveChatBean.SYSTEM_GIFT);
            bean.setGifticon_mini(gift_mini);
            mListener.onChat(bean);
        }

        /**
         * ?????????????????????????????????????????????
         */
        private void systemDanmuChatMessage(String content, String name) {
            LiveChatBean bean = new LiveChatBean();
            bean.setContent(content);
            bean.setType(LiveChatBean.SYSTEM_DANMU);
            bean.setUserNiceName(name);
            mListener.onChat(bean);
        }

        /**
         * ?????????????????????????????????
         */
        private void processLinkMic(JSONObject map) {
            int action = map.getIntValue("action");
            switch (action) {
                case 1://?????????????????????????????????
                    UserBean u = new UserBean();
                    u.setId(map.getString("uid"));
                    u.setUserNiceName(map.getString("uname"));
                    u.setAvatar(map.getString("uhead"));
                    u.setSex(map.getIntValue("sex"));
                    u.setLevel(map.getIntValue("level"));
                    mListener.onAudienceApplyLinkMic(u);
                    break;
                case 2://???????????????????????????????????????
                    if (map.getString("touid").equals(CommonAppConfig.getInstance().getUid())) {
                        mListener.onAnchorAcceptLinkMic();
                    }
                    break;
                case 3://???????????????????????????????????????
                    if (map.getString("touid").equals(CommonAppConfig.getInstance().getUid())) {
                        mListener.onAnchorRefuseLinkMic();
                    }
                    break;
                case 4://????????????????????????????????????????????????
                    String uid = map.getString("uid");
                    if (!TextUtils.isEmpty(uid) && !uid.equals(CommonAppConfig.getInstance().getUid())) {
                        mListener.onAudienceSendLinkMicUrl(uid, map.getString("uname"), map.getString("playurl"));
                    }
                    break;
                case 5://??????????????????????????????
                    mListener.onAudienceCloseLinkMic(map.getString("uid"), map.getString("uname"));
                    break;
                case 6://????????????????????????????????????
                    mListener.onAnchorCloseLinkMic(map.getString("touid"), map.getString("uname"));
                    break;
                case 7://???????????????????????????????????????????????????
                    if (map.getString("touid").equals(CommonAppConfig.getInstance().getUid())) {
                        mListener.onAnchorBusy();
                    }
                    break;
                case 8://??????????????????????????????????????????????????????
                    if (map.getString("touid").equals(CommonAppConfig.getInstance().getUid())) {
                        mListener.onAnchorNotResponse();
                    }
                    break;
                case 9://??????????????????????????????????????????????????????
                    mListener.onAudienceLinkMicExitRoom(map.getString("touid"));
                    break;
            }
        }

        /**
         * ?????????????????????????????????
         *
         * @param map
         */
        private void processLinkMicAnchor(JSONObject map) {
            int action = map.getIntValue("action");
            switch (action) {
                case 1://??????????????????????????????????????????
                    UserBean u = new UserBean();
                    u.setId(map.getString("uid"));
                    u.setUserNiceName(map.getString("uname"));
                    u.setAvatar(map.getString("uhead"));
                    u.setSex(map.getIntValue("sex"));
                    u.setLevel(map.getIntValue("level"));
                    u.setLevelAnchor(map.getIntValue("level_anchor"));
                    mListener.onLinkMicAnchorApply(u, map.getString("stream"));
                    break;
                case 3://?????????????????????????????????
                    mListener.onLinkMicAnchorRefuse();
                    break;
                case 4://???????????????????????????????????????????????????
                    mListener.onLinkMicAnchorPlayUrl(map.getString("pkuid"), map.getString("pkpull"));
                    break;
                case 5://?????????????????????
                    mListener.onLinkMicAnchorClose();
                    break;
                case 7://??????????????????????????????
                    mListener.onLinkMicAnchorBusy();
                    break;
                case 8://??????????????????????????????
                    mListener.onLinkMicAnchorNotResponse();
                    break;
                case 9://????????????????????????
                    mListener.onlinkMicPlayGaming();
                    break;
            }
        }

        /**
         * ?????????????????????PK??????
         *
         * @param map
         */
        private void processAnchorLinkMicPk(JSONObject map) {
            int action = map.getIntValue("action");
            switch (action) {
                case 1://??????????????????PK??????
                    UserBean u = new UserBean();
                    u.setId(map.getString("uid"));
                    u.setUserNiceName(map.getString("uname"));
                    u.setAvatar(map.getString("uhead"));
                    u.setSex(map.getIntValue("sex"));
                    u.setLevel(map.getIntValue("level"));
                    u.setLevelAnchor(map.getIntValue("level_anchor"));
                    mListener.onLinkMicPkApply(u, map.getString("stream"));
                    break;
                case 3://??????????????????PK?????????
                    mListener.onLinkMicPkRefuse();
                    break;
                case 4://???????????????PK??????????????????
                    mListener.onLinkMicPkStart(map.getString("pkuid"));
                    break;
                case 5://PK???????????????????????????
                    mListener.onLinkMicPkClose();
                    break;
                case 7://??????????????????????????????
                    mListener.onLinkMicPkBusy();
                    break;
                case 8://??????????????????????????????
                    mListener.onLinkMicPkNotResponse();
                    break;
                case 9://pk???????????????
                    mListener.onLinkMicPkEnd(map.getString("win_uid"));
                    break;
            }
        }

        /**
         * ????????????????????????,???????????????????????????3???
         * sendState;//1???????????????  2???????????????
         * sendType; //1???????????????  2?????????
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
         * ???????????????????????????????????????????????????????????????????????????
         */
        private void SaveAndUpdateSocketList(SocketMessageBean bean, String userId, boolean isReceive) {
            SocketListBean beans = new SocketListBean();
            beans.setUserId(userId);
            beans.setContent(bean.getContent());
            beans.setCurrentUid(CommonAppConfig.getInstance().getUid());
            beans.setTime(bean.getTime());
            beans.setUnReadCount(isReceive ? 1 : 0); //????????????????????? ???1   ????????????
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
