package com.yunbao.live.socket;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.LiveGiftBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;

import java.util.UUID;

/**
 * Created by cxf on 2018/10/9.
 * 直播间发言
 */

public class SocketChatUtil {

    /**
     * 发言
     */
    public static void sendChatMessage(SocketClient client, String content, boolean isAnchor, int userType, int guardType) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SEND_MSG)
                .param("action", 0)
                .param("msgtype", 2)
                .param("usertype", userType)
                .param("isAnchor", isAnchor ? 1 : 0)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("liangname", u.getGoodName())
                .param("vip_type", u.getVip().getType())
                .param("vip_is_king", u.getVip().getVip_is_king())
                .param("guard_type", guardType)
                .param("ct", content));
    }

    /**
     * 点亮
     */
    public static void sendLightMessage(SocketClient client, int heart, int guardType) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SEND_MSG)
                .param("action", 0)
                .param("msgtype", 2)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("liangname", u.getGoodName())
                .param("vip_type", u.getVip().getType())
                .param("vip_is_king", u.getVip().getVip_is_king())
                .param("heart", heart)
                .param("guard_type", guardType)
                .param("ct", WordUtil.getString(R.string.live_lighted)));

    }

    /**
     * 发送飘心消息
     */
    public static void sendFloatHeart(SocketClient client) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_LIGHT)
                .param("action", 2)
                .param("msgtype", 0)
                .param("ct", ""));
    }

    /**
     * 发送弹幕消息
     */
    public static void sendDanmuMessage(SocketClient client, String danmuToken) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SEND_BARRAGE)
                .param("action", 7)
                .param("msgtype", 1)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("uhead", u.getAvatar())
                .param("ct", danmuToken));
    }


    /**
     * 发送礼物消息
     */
    public static void sendGiftMessage(SocketClient client, int giftType, String giftToken, String liveUid,String gift_mini,int num,int only_danmu) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SEND_GIFT)
                .param("action", 0)
                .param("msgtype", 1)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("uhead", u.getAvatar())
                .param("evensend", giftType)
                .param("liangname", u.getGoodName())
                .param("vip_type", u.getVip().getType())
                .param("vip_is_king", u.getVip().getVip_is_king())
                .param("ct", giftToken)
                .param("roomnum", liveUid)
                .param("gifticon_mini", gift_mini)
                .param("gift_num", num)
                .param("only_danmu", only_danmu));
    }


    /**
     * 主播或管理员 踢人
     */
    public static void sendKickMessage(SocketClient client, String toUid, String toName) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_KICK)
                .param("action", 2)
                .param("msgtype", 4)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("touid", toUid)
                .param("toname", toName)
                .param("ct", toName + WordUtil.getString(R.string.live_kicked)));
    }


    /**
     * 主播或管理员 禁言
     */
    public static void sendShutUpMessage(SocketClient client, String toUid, String toName, int type) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SHUT_UP)
                .param("action", 1)
                .param("msgtype", 4)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("touid", toUid)
                .param("toname", toName)
                .param("ct", toName + WordUtil.getString(type == 0 ? R.string.live_shut : R.string.live_shut_2)));
    }

    /**
     * 设置或取消管理员消息
     */
    public static void sendSetAdminMessage(SocketClient client, int action, String toUid, String toName) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        String s = action == 1 ? WordUtil.getString(R.string.live_set_admin) : WordUtil.getString(R.string.live_set_admin_cancel);
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SET_ADMIN)
                .param("action", action)
                .param("msgtype", 1)
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("touid", toUid)
                .param("toname", toName)
                .param("ct", toName + " " + s));
    }

    /**
     * 超管关闭直播间
     */
    public static void superCloseRoom(SocketClient client) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_STOP_LIVE)
                .param("action", 19)
                .param("msgtype", 1)
                .param("ct", ""));
    }

    /**
     * 发系统消息
     */
    public static void sendSystemMessage(SocketClient client, String content) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SYSTEM)
                .param("action", 13)
                .param("msgtype", 4)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("ct", content));
    }
    /**
     * 发关注系统消息
     */
    public static void sendSystemMessage(SocketClient client, String content, String  follow) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SYSTEM)
                .param("action", 13)
                .param("msgtype", 4)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("ct", content)
                .param("follow", follow));
    }

    /**
     * 获取僵尸粉
     */
    public static void getFakeFans(SocketClient client) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_FAKE_FANS)
                .param("timestamp", "")
                .param("msgtype", "0"));
    }


    /**
     * 更新主播映票数
     */
    public static void sendUpdateVotesMessage(SocketClient client, int votes, int first) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_UPDATE_VOTES)
                .param("action", 1)
                .param("msgtype", 26)
                .param("votes", votes)
                .param("uid", CommonAppConfig.getInstance().getUid())
                .param("isfirst", first)
                .param("ct", ""));
    }

    /**
     * 更新主播映票数
     */
    public static void sendUpdateVotesMessage(SocketClient client, int votes) {
        sendUpdateVotesMessage(client, votes, 0);
    }

    /**
     * 发送购买守护成功消息
     */
    public static void sendBuyGuardMessage(SocketClient client, String votes, int guardNum, int guardType) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_BUY_GUARD)
                .param("action", 0)
                .param("msgtype", 0)
                .param("uid", u.getId())
                .param("uname", u.getUserNiceName())
                .param("uhead", u.getAvatar())
                .param("votestotal", votes)
                .param("guard_nums", guardNum)
                .param("guard_type", guardType));
    }

    /**
     * 发送发红包成功消息
     */
    public static void sendRedPackMessage(SocketClient client) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_RED_PACK)
                .param("action", 0)
                .param("msgtype", 0)
                .param("uid", u.getId())
                .param("uname", u.getUserNiceName())
                .param("ct", WordUtil.getString(R.string.red_pack_22))
        );

    }

    /**
     * 发只有礼物的系统消息
     */
    public static void sendGiftSystemMessage(SocketClient client, LiveGiftBean bean,String count) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SYSTEM)
                .param("action", 13)
                .param("msgtype", 666)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("gift_name", bean.getName())
                .param("gift_num", bean.getNum())
                .param("gifticon_mini", bean.getGifticon_mini())
                .param("gift_count", count)
                .param("ct", ""));
    }

    /**
     * 私密互动
     * @type 0 申请私密直播；1 报名倒计时结束；4 结束私密互动；5 私密开播成功
     */
    public static void sendPrivateLiveMsg(SocketClient client,int type) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_PRIVATE_LIVE)
                .param("msgtype", type));
    }

    /**
     * 拒绝私密互动(非一键拒绝)
     * @user 被拒绝的用户
     */
    public static void sendRefusePrivateLiveMsg(SocketClient client,String user) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_PRIVATE_LIVE)
                .param("msgtype", 2)
                .param("user", user));
    }


    /**
     * 一键拒绝私密互动
     * @user 所有申请用户
     */
    public static void sendOneKeyRefusePrivateLiveMsg(SocketClient client,String user) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_PRIVATE_LIVE)
                .param("msgtype", 3)
                .param("user", user));
    }

    /**
     * 私密开播成功
     */
    public static void sendPrivateLivePlayMsg(SocketClient client,String user) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_PRIVATE_LIVE)
                .param("msgtype", 5)
                .param("user", user));
    }

    /**
     * 直播切换直播类型
     */
    public static void sendChangeLive(SocketClient client,int type,int type_val) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_CHANGE_LIVE)
                .param("msgtype", 27)
                .param("action", 1)
                .param("type", type)
                .param("type_val", type_val)
                .param("retcode", "000000")
                .param("retmsg", "ok"));
    }

    /**
     * 发送用户列表
     */
    public static void sendUserList(SocketClient client, String userlist,String nums) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_USER_LIST)
                .param("userlist", userlist)
                .param("nums", nums)
        );
    }

    /**
     * 发送私信消息
     * 发送消息体{
     *     id:'16位UUID',
     *     content:'内容',
     *     sendUid:'发送者id',
     *     targetUid:'接收者id',
     *     time:'时间撮'，
     *     avatar:'头像',
     *     nickname:'昵称',
     *     stream:'房间id',
     *     type:'text',
     *     url:'预留',
     * }
     */
    public static void sendPrivateMsg(SocketClient client, String content,String mToUid,UserBean mUserBean) {
        if (client == null) {
            return;
        }
        client.sendPrivate(new SocketSendBean()
                .param("_method_", Constants.SOCKET_PEIVATE_LATE)
                .param("id",  UUID.randomUUID().toString())
                .param("content",  content)
                .param("sendUid",  CommonAppConfig.getInstance().getUid())
                .param("targetUid",  mToUid)
                .param("time", System.currentTimeMillis()+"")
                .param("type",  "text")
                .param("nickname", CommonAppConfig.getInstance().getUserBean().getUserNiceName())
                .param("avatar",  CommonAppConfig.getInstance().getUserBean().getAvatar())
                .param("toNickname", mUserBean.getUserNiceName())
                .param("toAvatar",  mUserBean.getAvatar())
                .param("url", "")
        );
    }



}
