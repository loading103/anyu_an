package com.yunbao.live.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.jpush.Gson;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.activity.SmallProgramTitleActivity;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.FollowUtilsBean;
import com.yunbao.common.bean.LiveGiftBean;
import com.yunbao.common.bean.TokenUtilsBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.CoinChangeEvent;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.event.LiveShowRedEvent;
import com.yunbao.common.greendao.entity.SocketMessageBean;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.KeyBoardHeightChangeListener;
import com.yunbao.common.mob.MobCallback;
import com.yunbao.common.mob.ShareData;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.KeyBoardHeightUtil2;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.event.ImUnReadCountEvent;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.live.R;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.live.bean.LiveBuyGuardMsgBean;
import com.yunbao.live.bean.LiveChatBean;
import com.yunbao.live.bean.LiveDanMuBean;
import com.yunbao.live.bean.LiveEnterRoomBean;
import com.yunbao.live.bean.LiveGiftPrizePoolWinBean;
import com.yunbao.live.bean.LiveGuardInfo;
import com.yunbao.live.bean.LiveLuckGiftWinBean;
import com.yunbao.live.bean.LiveReceiveGiftBean;
import com.yunbao.live.bean.LiveUserGiftBean;
import com.yunbao.live.dialog.GiftPrizePoolFragment;
import com.yunbao.live.dialog.LiveChatListDialogFragment;
import com.yunbao.live.dialog.LiveGuardBuyDialogFragment;
import com.yunbao.live.dialog.LiveGuardDialogFragment;
import com.yunbao.live.dialog.LiveInputDialogFragment;
import com.yunbao.live.dialog.LiveRedPackListDialogFragment;
import com.yunbao.live.dialog.LiveRedPackSendDialogFragment;
import com.yunbao.live.dialog.LiveShareDialogFragment;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.presenter.LiveLinkMicAnchorPresenter;
import com.yunbao.live.presenter.LiveLinkMicPkPresenter;
import com.yunbao.live.presenter.LiveLinkMicPresenter;
import com.yunbao.live.socket.SocketChatUtil;
import com.yunbao.live.socket.SocketClient;
import com.yunbao.live.socket.SocketMessageListener;
import com.yunbao.live.views.AbsLiveViewHolder;
import com.yunbao.live.views.LiveAddImpressViewHolder;
import com.yunbao.live.views.LiveAdminListViewHolder;
import com.yunbao.live.views.LiveContributeViewHolder;
import com.yunbao.live.views.LiveEndViewHolder;
import com.yunbao.live.views.LiveRoomViewHolder;
import com.yunbao.live.views.LiveWebViewHolder;
import com.yunbao.live.views.LiveWinViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by cxf on 2018/10/7.
 */

public abstract class LiveActivity extends AbsActivity implements SocketMessageListener, LiveShareDialogFragment.ActionListener, KeyBoardHeightChangeListener {

    protected ViewGroup mContainer;
    protected ViewGroup mPageContainer;
    protected LiveRoomViewHolder mLiveRoomViewHolder;
    protected AbsLiveViewHolder mLiveBottomViewHolder;
    protected LiveAddImpressViewHolder mLiveAddImpressViewHolder;
    protected LiveContributeViewHolder mLiveContributeViewHolder;
    protected LiveWebViewHolder mLiveLuckGiftTipViewHolder;
    protected LiveAdminListViewHolder mLiveAdminListViewHolder;
    protected LiveEndViewHolder mLiveEndViewHolder;
    protected LiveLinkMicPresenter mLiveLinkMicPresenter;//观众与主播连麦逻辑
    protected LiveLinkMicAnchorPresenter mLiveLinkMicAnchorPresenter;//主播与主播连麦逻辑
    protected LiveLinkMicPkPresenter mLiveLinkMicPkPresenter;//主播与主播PK逻辑
    //    protected GamePresenter mGamePresenter;
    protected SocketClient mSocketClient;
    protected LiveBean mLiveBean;
    protected int mLiveSDK;//sdk类型  0金山  1腾讯
    protected String mTxAppId;//腾讯sdkAppId
    protected boolean mIsAnchor;//是否是主播
    protected int mSocketUserType;//socket用户类型  30 普通用户  40 管理员  50 主播  60超管
    protected String mStream;
    protected String mLiveUid;
    protected String mOrUid;
    protected String mDanmuPrice;//弹幕价格
    protected String mCoinName;//钻石名称
    protected int mLiveType;//直播间的类型  普通 密码 门票 计时等
    protected int mLiveTypeVal;//收费价格,计时收费每次扣费的值
    protected KeyBoardHeightUtil2 mKeyBoardHeightUtil;
    protected int mDanMuLevel;//弹幕等级限制
    protected int mChatLevel;  //  发言用户限制等级
    protected int speak_limit_type;        // 发言限制类型 1-用户等级  2-VIP等级
    protected int vip_speak_limit;        //  发言VIP限制等级

    protected int user_level;//用户
    private ProcessImageUtil mImageUtil;
    private boolean mFirstConnectSocket;//是否是第一次连接成功socket
    private boolean mGamePlaying;//是否在游戏中
    private boolean mChatRoomOpened;//判断私信聊天窗口是否打开
    private LiveChatRoomFragment mLiveChatRoomDialogFragment;//私信聊天窗口
    protected LiveGuardInfo mLiveGuardInfo;
    private HashSet<DialogFragment> mDialogFragmentSet;
    protected int reConnectNum=0;//socket重连次数

    public boolean isScoller() {
        return isScoller;
    }

    public void setScoller(boolean scoller) {
        isScoller = scoller;
    }

    private boolean isScoller = false;
    protected String mSpeakLimitHref;//发言限制等级跳转
    private LiveChatRoomFragment chatRoomDialogFragment;//私信聊天窗口
    private LiveChatListFragment chatListRoomDialogFragment;//私信聊天窗口

    @Override
    protected void main() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mIsAnchor = this instanceof LiveAnchorActivity;
        mPageContainer = (ViewGroup) findViewById(R.id.page_container);
        EventBus.getDefault().register(this);
        mImageUtil = new ProcessImageUtil(this);
        mDialogFragmentSet = new HashSet<>();
        getCogFig();
    }

    @Override
    public boolean forbitSootSceenEnble() {
        return true;
    }

    private void getCogFig() {
        CommonHttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                Log.e("----", "111");
            }
        });
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    public ViewGroup getPageContainer() {
        return mPageContainer;
    }

    public ProcessImageUtil getProcessImageUtil() {
        return mImageUtil;
    }

    public void addDialogFragment(DialogFragment dialogFragment) {
        if (mDialogFragmentSet != null && dialogFragment != null) {
            mDialogFragmentSet.add(dialogFragment);
        }
    }

    public void removeDialogFragment(DialogFragment dialogFragment) {
        if (mDialogFragmentSet != null && dialogFragment != null) {
            mDialogFragmentSet.remove(dialogFragment);
        }
    }

    private void hideDialogs() {
        if (mDialogFragmentSet != null) {
            for (DialogFragment dialogFragment : mDialogFragmentSet) {
                if (dialogFragment != null) {
                    dialogFragment.dismissAllowingStateLoss();
                }
            }
        }
    }


    /**
     * 连接成功socket后调用
     */


    @Override
    public void onConnect(boolean successConn) {
        reConnectNum=0;
        L.e("WOLF","onConnect:"+reConnectNum);
        if (successConn) {
            if (!mFirstConnectSocket) {
                L.e("WOLF", "第一次连接");
                mFirstConnectSocket = true;
//                if (mLiveType == Constants.LIVE_TYPE_PAY || mLiveType == Constants.LIVE_TYPE_TIME) {
//                    SocketChatUtil.sendUpdateVotesMessage(mSocketClient, mLiveTypeVal, 1);
//                }
                SocketChatUtil.getFakeFans(mSocketClient);
            } else {
                L.e("WOLF", "非第一次连接");
                if (isScoller) {
                    isScoller = false;
                } else {
                    if (mContext instanceof LiveAudienceActivity) {
                        ((LiveAudienceActivity) mContext).chooseLinePlay();
                    }
                }
            }
        }
    }

    public SocketClient getSocketClient() {
        return mSocketClient;
    }

    /**
     * 自己的socket断开
     */
    @Override
    public void onDisConnect() {

    }

    /**
     * 重连socket
     */
    @Override
    public void onReconnect() {

    }

    /**
     * 收到聊天消息
     * 29105
     */
    @Override
    public void onChat(LiveChatBean bean) {
        if (bean == null) {
            return;
        }

        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.insertChat(bean);
            //下注中奖弹窗
        }

        if (bean.getType() == LiveChatBean.SYSTEM && !TextUtils.isEmpty(bean.getUid()) && bean.getUid().equals(CommonAppConfig.getInstance().getUid()) && !TextUtils.isEmpty(bean.getAward_amount())) {
            LiveWinViewHolder winViewHolder = new LiveWinViewHolder(bean.getAward_amount());
            winViewHolder.show(getSupportFragmentManager(), "LiveWinViewHolder");
        }
        if (bean.getType() == LiveChatBean.LIGHT) {
            onLight();
        }
    }

    /**
     * 收到飘心消息
     */
    @Override
    public void onLight() {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.playLightAnim();
        }
    }

    /**
     * 收到用户进房间消息
     */
    @Override
    public void onEnterRoom(LiveEnterRoomBean bean) {
        if (mLiveRoomViewHolder != null) {
            LiveUserGiftBean u = bean.getUserBean();
            mLiveRoomViewHolder.insertUser(u);
            //谁谁谁进了直播间，单独列成跑马灯，不在消息列表中显示
//            mLiveRoomViewHolder.addComeInData(bean.getLiveChatBean());
            mLiveRoomViewHolder.insertEnterRoomChat(bean.getLiveChatBean());

            if (mContext instanceof LiveAnchorActivity && u.getLivePhone() != null && u.getLivePhone().equals("1") && CommonAppConfig.getInstance().getConfig().getConversation_switch().equals("1")) {//新用户消息
                LiveEnterRoomBean newBean = modelA2B(bean, LiveEnterRoomBean.class);
                newBean.getLiveChatBean().setLivePhone(u.getLivePhone());
                mLiveRoomViewHolder.insertChat(newBean.getLiveChatBean());
            }
            mLiveRoomViewHolder.onEnterRoom(bean);
        }
    }

    /**
     * 收到用户离开房间消息
     */
    @Override
    public void onLeaveRoom(UserBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.removeUser(bean.getId());
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceLeaveRoom(bean);
        }
    }

    /**
     * 收到礼物消息
     */
    @Override
    public void onSendGift(LiveReceiveGiftBean bean) {
        if (mLiveRoomViewHolder != null) {
            // mLiveRoomViewHolder.insertChat(bean.getLiveChatBean());
            mLiveRoomViewHolder.showGiftMessage(bean);
        }
    }

    /**
     * pk 时候收到礼物
     *
     * @param leftGift  左边的映票数
     * @param rightGift 右边的映票数
     */
    @Override
    public void onSendGiftPk(long leftGift, long rightGift) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onPkProgressChanged(leftGift, rightGift);
        }
    }

    /**
     * 收到弹幕消息
     */
    @Override
    public void onSendDanMu(LiveDanMuBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.showDanmu(bean);
        }
    }

    /**
     * 收到私密互动相关消息
     */
    @Override
    public void onSendPrivateLive(int type, String user) {

    }

    /**
     * 观众收到直播结束消息
     *
     * @param ct
     */
    @Override
    public void onLiveEnd(String ct) {
        hideDialogs();
    }

    /**
     * 超管关闭直播间
     *
     * @param s
     */
    @Override
    public void onSuperCloseLive(String s) {
        hideDialogs();
    }

    /**
     * 踢人
     */
    @Override
    public void onKick(String touid) {

    }

    /**
     * 禁言
     */
    @Override
    public void onShutUp(String touid, String content) {

    }

    /**
     * 设置或取消管理员
     */
    @Override
    public void onSetAdmin(String toUid, int isAdmin) {
        if (!TextUtils.isEmpty(toUid) && toUid.equals(CommonAppConfig.getInstance().getUid())) {
            mSocketUserType = isAdmin == 1 ? Constants.SOCKET_USER_TYPE_ADMIN : Constants.SOCKET_USER_TYPE_NORMAL;
        }
    }

    /**
     * 主播切换计时收费或更改计时收费价格的时候执行
     */
    @Override
    public void onChangeTimeCharge(int type, int typeVal) {

    }

    /**
     * 门票或计时收费更新主播映票数
     */
    @Override
    public void onUpdateVotes(String uid, String deltaVal, int first) {
        if (!CommonAppConfig.getInstance().getUid().equals(uid) || first != 1) {
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.updateVotes(deltaVal);
            }
        }
    }

    /**
     * 添加僵尸粉
     */
    @Override
    public void addFakeFans(List<LiveUserGiftBean> list) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.insertUser(list);
        }
    }

    /**
     * 直播间  收到购买守护消息
     */
    @Override
    public void onBuyGuard(LiveBuyGuardMsgBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onGuardInfoChanged(bean);
            LiveChatBean chatBean = new LiveChatBean();
            chatBean.setContent(bean.getUserName() + WordUtil.getString(R.string.guard_buy_msg));
            chatBean.setType(LiveChatBean.SYSTEM);
            mLiveRoomViewHolder.insertChat(chatBean);
        }
    }

    /**
     * 直播间 收到红包消息
     */
    @Override
    public void onRedPack(LiveChatBean liveChatBean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setRedPackBtnVisible(true);
            mLiveRoomViewHolder.insertChat(liveChatBean);
        }
    }

    /**
     * 观众与主播连麦  主播收到观众的连麦申请
     */
    @Override
    public void onAudienceApplyLinkMic(UserBean u) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceApplyLinkMic(u);
        }
    }

    /**
     * 观众与主播连麦  观众收到主播同意连麦的socket
     */
    @Override
    public void onAnchorAcceptLinkMic() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorAcceptLinkMic();
        }
    }

    /**
     * 观众与主播连麦  观众收到主播拒绝连麦的socket
     */
    @Override
    public void onAnchorRefuseLinkMic() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorRefuseLinkMic();
        }
    }

    /**
     * 观众与主播连麦  主播收到观众发过来的流地址
     */
    @Override
    public void onAudienceSendLinkMicUrl(String uid, String uname, String playUrl) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceSendLinkMicUrl(uid, uname, playUrl);
        }

    }

    /**
     * 观众与主播连麦  主播关闭观众的连麦
     */
    @Override
    public void onAnchorCloseLinkMic(String touid, String uname) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorCloseLinkMic(touid, uname);
        }
    }

    /**
     * 观众与主播连麦  观众主动断开连麦
     */
    @Override
    public void onAudienceCloseLinkMic(String uid, String uname) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceCloseLinkMic(uid, uname);
        }
    }

    /**
     * 观众与主播连麦  主播连麦无响应
     */
    @Override
    public void onAnchorNotResponse() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorNotResponse();
        }
    }

    /**
     * 观众与主播连麦  主播正在忙
     */
    @Override
    public void onAnchorBusy() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorBusy();
        }
    }

    /**
     * 主播与主播连麦  主播收到其他主播发过来的连麦申请的回调
     */
    @Override
    public void onLinkMicAnchorApply(UserBean u, String stream) {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播连麦  所有人收到对方主播的播流地址的回调
     *
     * @param playUrl 对方主播的播流地址
     * @param pkUid   对方主播的uid
     */
    @Override
    public void onLinkMicAnchorPlayUrl(String pkUid, String playUrl) {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorPlayUrl(pkUid, playUrl);
        }
        if (this instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) this).onLinkMicTxAnchor(true);
        }
    }

    /**
     * 主播与主播连麦  断开连麦的回调
     */
    @Override
    public void onLinkMicAnchorClose() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorClose();
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkClose();
        }
        if (this instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) this).onLinkMicTxAnchor(false);
        }
    }

    /**
     * 主播与主播连麦  对方主播拒绝连麦的回调
     */
    @Override
    public void onLinkMicAnchorRefuse() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播连麦  对方主播无响应的回调
     */
    @Override
    public void onLinkMicAnchorNotResponse() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播连麦  对方主播正在游戏
     */
    @Override
    public void onlinkMicPlayGaming() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播连麦  对方主播正在忙的回调
     */
    @Override
    public void onLinkMicAnchorBusy() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK  主播收到对方主播发过来的PK申请的回调
     *
     * @param u      对方主播的信息
     * @param stream 对方主播的stream
     */
    @Override
    public void onLinkMicPkApply(UserBean u, String stream) {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK 所有人收到PK开始的回调
     */
    @Override
    public void onLinkMicPkStart(String pkUid) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkStart(pkUid);
        }
    }

    /**
     * 主播与主播PK  所有人收到断开连麦pk的回调
     */
    @Override
    public void onLinkMicPkClose() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkClose();
        }
    }

    /**
     * 主播与主播PK  对方主播拒绝pk的回调
     */
    @Override
    public void onLinkMicPkRefuse() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK   对方主播正在忙的回调
     */
    @Override
    public void onLinkMicPkBusy() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK   对方主播无响应的回调
     */
    @Override
    public void onLinkMicPkNotResponse() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK   所有人收到PK结果的回调
     */
    @Override
    public void onLinkMicPkEnd(String winUid) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkEnd(winUid);
        }
    }


    /**
     * 连麦观众退出直播间
     */
    @Override
    public void onAudienceLinkMicExitRoom(String touid) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceLinkMicExitRoom(touid);
        }
    }


    /**
     * 幸运礼物中奖
     */
    @Override
    public void onLuckGiftWin(LiveLuckGiftWinBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onLuckGiftWin(bean);
        }
    }

    /**
     * 奖池中奖
     */
    @Override
    public void onPrizePoolWin(LiveGiftPrizePoolWinBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onPrizePoolWin(bean);
        }
    }


    /**
     * 奖池升级
     */
    @Override
    public void onPrizePoolUp(String level) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onPrizePoolUp(level);
        }
    }


    @Override
    public void onGameZjh(JSONObject obj) {
//        if (mGamePresenter != null) {
//            mGamePresenter.onGameZjhSocket(obj);
//        }
    }

    @Override
    public void onGameHd(JSONObject obj) {
//        if (mGamePresenter != null) {
//            mGamePresenter.onGameHdSocket(obj);
//        }
    }

    @Override
    public void onGameZp(JSONObject obj) {
//        if (mGamePresenter != null) {
//            mGamePresenter.onGameZpSocket(obj);
//        }
    }

    @Override
    public void onGameNz(JSONObject obj) {
//        if (mGamePresenter != null) {
//            mGamePresenter.onGameNzSocket(obj);
//        }
    }

    @Override
    public void onGameEbb(JSONObject obj) {
//        if (mGamePresenter != null) {
//            mGamePresenter.onGameEbbSocket(obj);
//        }
    }

    /**
     * 打开聊天输入框（直播间消息聊天框）
     */
    public void openChatWindow() {
        if (mKeyBoardHeightUtil == null) {
            mKeyBoardHeightUtil = new KeyBoardHeightUtil2(mContext, super.findViewById(android.R.id.content), this);
            mKeyBoardHeightUtil.start();
        }

        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.chatScrollToBottom();
        }
        LiveInputDialogFragment fragment = new LiveInputDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_DANMU_PRICE, mDanmuPrice);
        bundle.putString(Constants.COIN_NAME, mCoinName);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveInputDialogFragment");
    }
//
//    /**
//     * 打开私信列表窗口
//     */
//    public void openChatListWindow() {
//        LiveChatListDialogFragment fragment = new LiveChatListDialogFragment();
//        if (!mIsAnchor) {
//            Bundle bundle = new Bundle();
//            bundle.putString(Constants.LIVE_UID, mLiveUid);
//            fragment.setArguments(bundle);
//        }
//        fragment.show(getSupportFragmentManager(), "LiveChatListDialogFragment");
//    }


    /**
     * 主播端端打开私信分两种，点私信图标跳转到list界面(此处处理),点某个人私信跳转到聊天界面（别处处理）
     * 用户端直接跳转到聊天界面
     * 进入聊天界面需要先判断是不是相互关注了
     */
    private boolean islistActivity = false;

    public void openChatRoomWindow(UserBean userBean, boolean following) {
        if (mContext instanceof LiveAnchorActivity) {
            openAnchorChatList(userBean, following,false);
        } else {
            openAudienceChatRoom(userBean, following, false);
        }
    }

    /**
     * 用户端转到聊天列表
     */

    public void openUserListRoomWindow(UserBean userBean, boolean following,boolean  isUser) {
            openAnchorChatList(userBean, following,true);
    }
    /**
     * 主播端点击私信跳转（此处跳转到列表界面）
     */
    private void openAnchorChatList(UserBean userBean, boolean following,boolean  isUser) {
        islistActivity = true;
        chatListRoomDialogFragment = new LiveChatListFragment(mSocketClient,  isUser);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.USER_BEAN, userBean);
        bundle.putBoolean(Constants.FOLLOW, following);
        chatListRoomDialogFragment.setArguments(bundle);
        chatListRoomDialogFragment.show(getSupportFragmentManager(), "LiveChatRoomDialogFragment");
    }

    /**
     * 用户击私信跳转聊天界面，需要判断是不是相互关注
     */
    public void openAudienceChatRoom(UserBean userBean, boolean following, boolean isAnchor) {
        islistActivity = false;
        if (userBean == null && mLiveBean != null) {
            userBean = new UserBean();
            userBean.setId(mLiveBean.getUid());
            userBean.setUserNiceName(mLiveBean.getUserNiceName());
            userBean.setAvatar(mLiveBean.getAvatar());
        }
        if (userBean == null) {
            return;
        }
        Log.e("用户点击私信--", new Gson().toJson(userBean));
        getFollowData(userBean, following, isAnchor);
    }

    /**
     * 用户获取与主播是不是相互关注
     */
    public void getFollowData(final UserBean userBean, final boolean following, final boolean isAnchor) {
        CommonHttpUtil.getBothFollowed(userBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    Log.e("用户获取与主播是不是相互关注------", info[0]);
                    String isAttention = JSON.parseObject(info[0]).getString("isattent");
                    openAudienceChatRoomWindow(userBean, following, isAttention, isAnchor);
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onError(String message) {
                super.onError(message);
                ToastUtil.show(message);
            }
        });
    }

    /**
     * 用户端打开私信聊天框
     */
    public void openAudienceChatRoomWindow(UserBean userBean, boolean following, String followState, boolean isAnchor) {
        if (mKeyBoardHeightUtil == null) {
            mKeyBoardHeightUtil = new KeyBoardHeightUtil2(mContext, super.findViewById(android.R.id.content), this);
            mKeyBoardHeightUtil.start();
        }
        chatRoomDialogFragment = new LiveChatRoomFragment(mSocketClient, isAnchor, false);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.USER_BEAN, userBean);
        bundle.putBoolean(Constants.FOLLOW, following);
        bundle.putString(Constants.FOLLOWSTATE, followState);
        chatRoomDialogFragment.setArguments(bundle);
        chatRoomDialogFragment.show(getSupportFragmentManager(), "LiveChatRoomDialogFragment");
    }

    /**
     * 接收到私信消息
     */
    @Override
    public void onReceivePrivateLetterMsg(SocketMessageBean bean) {
        EventBus.getDefault().post(new LiveShowRedEvent(true));
        if (mContext instanceof LiveAnchorActivity) {
            if (islistActivity) {
                if (chatListRoomDialogFragment != null) {
                    chatListRoomDialogFragment.onReceivePrivateLetterMsg(bean);
                }
            } else {
                if (chatRoomDialogFragment != null) {
                    chatRoomDialogFragment.onReceivePrivateLetterMsg(bean);
                }
            }

        } else {
            if (islistActivity) {
                if (chatListRoomDialogFragment != null) {
                    chatListRoomDialogFragment.onReceivePrivateLetterMsg(bean);
                }
            }else if (chatRoomDialogFragment != null) {
                //用户端收到私信消息小时底部小红点，在点击私信和私信弹窗消失时候消失小红点
                chatRoomDialogFragment.onReceivePrivateLetterMsg(bean);
            }
        }

    }

    /**
     * 接收到发送消息失败回执
     */
    @Override
    public void onReceiveSendMsgFail(SocketMessageBean bean, String retmsg) {
        if (mContext instanceof LiveAnchorActivity) {
            if (islistActivity) {
                if (chatListRoomDialogFragment != null) {
                    chatListRoomDialogFragment.onReceiveSendMsgFail(bean, retmsg);
                }
            } else {
                if (chatRoomDialogFragment != null) {
                    chatRoomDialogFragment.onReceiveSendMsgFail(bean, retmsg);
                }
            }
        } else {
            if (islistActivity) {
                if (chatListRoomDialogFragment != null) {
                    chatListRoomDialogFragment.onReceiveSendMsgFail(bean, retmsg);
                }
            }else if (chatRoomDialogFragment != null) {
                chatRoomDialogFragment.onReceiveSendMsgFail(bean, retmsg);
            }
        }
    }

    /**
     * 接收到发送消息成功回执
     */
    @Override
    public void onReceiveSendMsgSuccess(SocketMessageBean bean) {
        if (mContext instanceof LiveAnchorActivity) {
            if (islistActivity) {
                if (chatListRoomDialogFragment != null) {
                    chatListRoomDialogFragment.onReceiveSendMsgSuccess(bean);
                }
            } else {
                if (chatRoomDialogFragment != null) {
                    chatRoomDialogFragment.onReceiveSendMsgSuccess(bean);
                }
            }

        } else {
            if (islistActivity) {
                if (chatListRoomDialogFragment != null) {
                    chatListRoomDialogFragment.onReceiveSendMsgSuccess(bean);
                }
            }else if (chatRoomDialogFragment != null) {
                chatRoomDialogFragment.onReceiveSendMsgSuccess(bean);
            }
        }
    }

    /**
     * 发 弹幕 消息
     */
    public void sendDanmuMessage(String content) {
        if (!mIsAnchor) {
            UserBean u = CommonAppConfig.getInstance().getUserBean();
            String is_super = SpUtil.getInstance().getStringValue(SpUtil.IS_SUPER);
            //是超管，直接发消息
            if (!TextUtils.isEmpty(is_super) && is_super.equals("1")) {
                LiveHttpUtil.sendDanmu(content, mLiveUid, mStream, mOrUid, mDanmuCallback);
                return;
            }

            if (u != null && u.getVip().getVip_is_king() == 1) {
                if (u.getVip().getType() < mDanMuLevel) {
                    showLimitChatDialog(mContext, mDanMuLevel, "弹幕");
                    return;
                }
            } else if (u != null && u.getLevel() < mDanMuLevel) {
                ToastUtil.show(String.format(WordUtil.getString(R.string.live_level_danmu_limit), mDanMuLevel));
                return;
            }
        }
        LiveHttpUtil.sendDanmu(content, mLiveUid, mStream, mOrUid, mDanmuCallback);
    }

    private HttpCallback mDanmuCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                UserBean u = CommonAppConfig.getInstance().getUserBean();
                if (u != null) {
                    u.setLevel(obj.getIntValue("level"));
                    String coin = obj.getString("coin");
                    u.setCoin(coin);

                    onCoinChanged(coin);
                }
                SocketChatUtil.sendDanmuMessage(mSocketClient, obj.getString("barragetoken"));
            } else {
                ToastUtil.show(msg);
            }
        }
    };


    /**
     * 发 聊天 消息
     */
    public void sendChatMessage(String content) {
        if (!mIsAnchor) {
            UserBean u = CommonAppConfig.getInstance().getUserBean();
            String is_super = SpUtil.getInstance().getStringValue(SpUtil.IS_SUPER);
            //是超管，直接发消息
            if (!TextUtils.isEmpty(is_super) && is_super.equals("1")) {
                int guardType = mLiveGuardInfo != null ? mLiveGuardInfo.getMyGuardType() : Constants.GUARD_TYPE_NONE;
                SocketChatUtil.sendChatMessage(mSocketClient, content, mIsAnchor, mSocketUserType, guardType);
                return;
            }
//            if (u != null && u.getVip().getVip_is_king() == 1) {
//                if (u.getVip().getType() < mChatLevel) {
//                    showLimitChatDialog(mContext, mChatLevel, "消息");
//                    return;
//                }
//            } else {
//                if (u != null && u.getLevel() < mChatLevel) {
//                    ToastUtil.show(String.format(WordUtil.getString(R.string.live_level_chat_limit), mChatLevel));
//                    return;
//                }
//            }
            if (speak_limit_type == 2) {
                if (u.getVip().getType() < mChatLevel) {
                    showLimitChatDialog(mContext, mChatLevel, "消息");
                    return;
                }
            } else {
                if (u != null && u.getLevel() < vip_speak_limit) {
                    ToastUtil.show(String.format(WordUtil.getString(R.string.live_level_chat_limit), vip_speak_limit));
                    return;
                }
            }

        }
        int guardType = mLiveGuardInfo != null ? mLiveGuardInfo.getMyGuardType() : Constants.GUARD_TYPE_NONE;
        SocketChatUtil.sendChatMessage(mSocketClient, content, mIsAnchor, mSocketUserType, guardType);
    }

    /**
     * 限制发言dialog
     */
    public void showLimitChatDialog(final Context context, int level, String type) {
        DialogUitl.Builder builder = new DialogUitl.Builder(context);
        builder.setTitle("温馨提示")
                .setContent("亲\uD83D\uDE0A！VIP" + level + "才可发送" + type + "\n如您已是VIP" + level + "请重进直播间！")
                .setConfrimString("获取VIP")
                .setCancelString("取消")
                .setCancelable(false)
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        new SmallProgramTitleActivity().toActivity(context,
                                CommonAppConfig.getInstance().getConfig().getVideo_limit_rule(), "normal", true);
                    }
                })
                .build()
                .show();
    }

    /**
     * 发 系统 消息
     */
    public void sendSystemMessage(String content) {
        SocketChatUtil.sendSystemMessage(mSocketClient, content);
    }

    /**
     * 发 礼物系统 消息
     *
     * @param bean
     */
    public void sendGiftSystemMessage(LiveGiftBean bean, String count) {
        SocketChatUtil.sendGiftSystemMessage(mSocketClient, bean, count);
    }

    /**
     * 发 关注消息
     */
    public void sendGzSystemMessage(String content, String follow) {
        SocketChatUtil.sendSystemMessage(mSocketClient, content, follow);
    }

    /**
     * 发 送礼物 消息
     */
    public void sendGiftMessage(LiveGiftBean giftBean, String giftToken, int only_danmu) {
        SocketChatUtil.sendGiftMessage(mSocketClient, giftBean.getType(), giftToken, mLiveUid, giftBean.getGifticon_mini(), giftBean.getNum(), only_danmu);
    }

    /**
     * 主播或管理员踢人
     */
    public void kickUser(String toUid, String toName) {
        SocketChatUtil.sendKickMessage(mSocketClient, toUid, toName);
    }

    /**
     * 禁言
     */
    public void setShutUp(String toUid, String toName, int type) {
        SocketChatUtil.sendShutUpMessage(mSocketClient, toUid, toName, type);
    }

    /**
     * 设置或取消管理员消息
     */
    public void sendSetAdminMessage(int action, String toUid, String toName) {
        SocketChatUtil.sendSetAdminMessage(mSocketClient, action, toUid, toName);
    }


    /**
     * 超管关闭直播间
     */
    public void superCloseRoom() {
        SocketChatUtil.superCloseRoom(mSocketClient);
    }

    /**
     * 更新主播映票数
     */
    public void sendUpdateVotesMessage(int deltaVal) {
        SocketChatUtil.sendUpdateVotesMessage(mSocketClient, deltaVal);
    }


    /**
     * 发送购买守护成功消息
     */
    public void sendBuyGuardMessage(String votes, int guardNum, int guardType) {
        SocketChatUtil.sendBuyGuardMessage(mSocketClient, votes, guardNum, guardType);
    }

    /**
     * 发送发红包成功消息
     */
    public void sendRedPackMessage() {
        SocketChatUtil.sendRedPackMessage(mSocketClient);
    }


    /**
     * 打开添加印象窗口
     */
    public void openAddImpressWindow(String toUid) {
        if (mLiveAddImpressViewHolder == null) {
            mLiveAddImpressViewHolder = new LiveAddImpressViewHolder(mContext, mPageContainer);
            mLiveAddImpressViewHolder.subscribeActivityLifeCycle();
        }
        mLiveAddImpressViewHolder.setToUid(toUid);
        mLiveAddImpressViewHolder.addToParent();
        mLiveAddImpressViewHolder.show();
    }

    /**
     * 直播间贡献榜窗口
     */
    public void openContributeWindow() {
        if (mLiveContributeViewHolder == null) {
            mLiveContributeViewHolder = new LiveContributeViewHolder(mContext, mPageContainer, mLiveUid, mOrUid);
            mLiveContributeViewHolder.subscribeActivityLifeCycle();
            mLiveContributeViewHolder.addToParent();
        }
        mLiveContributeViewHolder.show();
        if (CommonAppConfig.LIVE_ROOM_SCROLL && !mIsAnchor) {
            ((LiveAudienceActivity) this).setScrollFrozen(true);
        }
    }


    /**
     * 直播间管理员窗口
     */
    public void openAdminListWindow() {
        if (mLiveAdminListViewHolder == null) {
            mLiveAdminListViewHolder = new LiveAdminListViewHolder(mContext, mPageContainer, mLiveUid);
            mLiveAdminListViewHolder.subscribeActivityLifeCycle();
            mLiveAdminListViewHolder.addToParent();
        }
        mLiveAdminListViewHolder.show();
    }

    /**
     * 是否能够返回
     */
    protected boolean canBackPressed() {
        if (mLiveContributeViewHolder != null && mLiveContributeViewHolder.isShowed()) {
            mLiveContributeViewHolder.hide();
            return false;
        }
        if (mLiveAddImpressViewHolder != null && mLiveAddImpressViewHolder.isShowed()) {
            mLiveAddImpressViewHolder.hide();
            return false;
        }
        if (mLiveAdminListViewHolder != null && mLiveAdminListViewHolder.isShowed()) {
            mLiveAdminListViewHolder.hide();
            return false;
        }
        if (mLiveLuckGiftTipViewHolder != null && mLiveLuckGiftTipViewHolder.isShowed()) {
            mLiveLuckGiftTipViewHolder.hide();
            return false;
        }
        return true;
    }

    /**
     * 打开分享窗口
     */
    public void openShareWindow() {
        LiveShareDialogFragment fragment = new LiveShareDialogFragment();
        fragment.setActionListener(this);
        fragment.show(getSupportFragmentManager(), "LiveShareDialogFragment");
    }

    /**
     * 分享点击事件回调
     */
    @Override
    public void onItemClick(String type) {
        if (Constants.LINK.equals(type)) {
            copyLink();
        } else {
            shareLive(type, null);
        }
    }

    /**
     * 复制直播间链接
     */
    private void copyLink() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean == null) {
            return;
        }
        String link = configBean.getLiveWxShareUrl() + mLiveUid;
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", link);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(WordUtil.getString(R.string.copy_success));
    }


    /**
     * 分享直播间
     */
    public void shareLive(String type, MobCallback callback) {
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean == null) {
            return;
        }
//        if (mMobShareUtil == null) {
//            mMobShareUtil = new MobShareUtil();
//        }
        ShareData data = new ShareData();
        data.setTitle(configBean.getLiveShareTitle());
        data.setDes(mLiveBean.getUserNiceName() + configBean.getLiveShareDes());
        data.setImgUrl(mLiveBean.getAvatarThumb());
        String webUrl = configBean.getDownloadApkUrl();
        if (Constants.MOB_WX.equals(type) || Constants.MOB_WX_PYQ.equals(type)) {
            webUrl = configBean.getLiveWxShareUrl() + mLiveUid;
        }
        data.setWebUrl(webUrl);
//        mMobShareUtil.execute(type, data, callback);
    }

    /**
     * 监听关注变化事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
//        if (!TextUtils.isEmpty(mLiveUid) && mLiveUid.equals(e.getToUid())) {
//            if (mLiveRoomViewHolder != null) {
//                mLiveRoomViewHolder.setAttention(e.getIsAttention());
//            }
//        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setAttention(e.getIsAttention());
        }
    }

    /**
     * 监听私信未读消息数变化事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
        String unReadCount = e.getUnReadCount();
        if (!TextUtils.isEmpty(unReadCount) && mLiveBottomViewHolder != null) {
            mLiveBottomViewHolder.setUnReadCount(unReadCount);
        }
    }

    /**
     * 获取私信未读消息数量
     */
    protected String getImUnReadCount() {
        return ImMessageUtil.getInstance().getAllUnReadMsgCount();
    }

    /**
     * 监听钻石数量变化事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangeEvent(CoinChangeEvent e) {
        onCoinChanged(e.getCoin());
        if (e.isChargeSuccess() && this instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) this).onChargeSuccess();
        }
    }

    public void onCoinChanged(String coin) {
//        if (mGamePresenter != null) {
//            mGamePresenter.setLastCoin(coin);
//        }
    }

    /**
     * 守护列表弹窗
     */
    public void openGuardListWindow() {
        LiveGuardDialogFragment fragment = new LiveGuardDialogFragment();
        fragment.setLiveGuardInfo(mLiveGuardInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putBoolean(Constants.ANCHOR, mIsAnchor);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGuardDialogFragment");
    }

    /**
     * 打开购买守护的弹窗
     */
    public void openBuyGuardWindow() {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream) || mLiveGuardInfo == null) {
            return;
        }
        LiveGuardBuyDialogFragment fragment = new LiveGuardBuyDialogFragment();
        fragment.setLiveGuardInfo(mLiveGuardInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.COIN_NAME, mCoinName);
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putString(Constants.STREAM, mStream);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGuardBuyDialogFragment");
    }

    /**
     * 打开发红包的弹窗
     */
    public void openRedPackSendWindow() {
        LiveRedPackSendDialogFragment fragment = new LiveRedPackSendDialogFragment();
        fragment.setStream(mStream);
        //fragment.setCoinName(mCoinName);
        fragment.show(getSupportFragmentManager(), "LiveRedPackSendDialogFragment");
    }

    /**
     * 打开发红包列表弹窗
     */
    public void openRedPackListWindow() {
        LiveRedPackListDialogFragment fragment = new LiveRedPackListDialogFragment();
        fragment.setStream(mStream);
        fragment.setCoinName(mCoinName);
        fragment.show(getSupportFragmentManager(), "LiveRedPackListDialogFragment");
    }


    /**
     * 打开奖池弹窗
     */
    public void openPrizePoolWindow() {
        GiftPrizePoolFragment fragment = new GiftPrizePoolFragment();
        fragment.setLiveUid(mLiveUid);
        fragment.setStream(mStream);
        fragment.show(getSupportFragmentManager(), "GiftPrizePoolFragment");
    }

    /**
     * 打开幸运礼物说明
     */
    public void openLuckGiftTip() {
        if (mLiveLuckGiftTipViewHolder == null) {
            HtmlConfig config = new HtmlConfig();
            mLiveLuckGiftTipViewHolder = new LiveWebViewHolder(mContext, mPageContainer, config.getLUCK_GIFT_TIP());
            mLiveLuckGiftTipViewHolder.subscribeActivityLifeCycle();
            mLiveLuckGiftTipViewHolder.addToParent();
        }
        mLiveLuckGiftTipViewHolder.show();
        if (CommonAppConfig.LIVE_ROOM_SCROLL && !mIsAnchor) {
            ((LiveAudienceActivity) this).setScrollFrozen(true);
        }
    }

    /**
     * 键盘高度的变化
     */
    @Override
    public void onKeyBoardHeightChanged(int visibleHeight, int keyboardHeight) {
        if (mChatRoomOpened) {//判断私信聊天窗口是否打开
            if (mLiveChatRoomDialogFragment != null) {
                mLiveChatRoomDialogFragment.scrollToBottom();
            }
        } else {
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.onKeyBoardChanged(visibleHeight, keyboardHeight);
            }
        }
    }

    @Override
    public boolean isSoftInputShowed() {
        if (mKeyBoardHeightUtil != null) {
            return mKeyBoardHeightUtil.isSoftInputShowed();
        }
        return false;
    }

    public void setChatRoomOpened(LiveChatRoomFragment chatRoomDialogFragment, boolean chatRoomOpened) {
        mChatRoomOpened = chatRoomOpened;
        mLiveChatRoomDialogFragment = chatRoomDialogFragment;
    }

    /**
     * 是否在游戏中
     */
    public boolean isGamePlaying() {
        return mGamePlaying;
    }

    public void setGamePlaying(boolean gamePlaying) {
        mGamePlaying = gamePlaying;
    }

    /**
     * 是否在连麦中
     */
    public boolean isLinkMic() {
        return mLiveLinkMicPresenter != null && mLiveLinkMicPresenter.isLinkMic();
    }

    /**
     * 主播是否在连麦中
     */
    public boolean isLinkMicAnchor() {
        return mLiveLinkMicAnchorPresenter != null && mLiveLinkMicAnchorPresenter.isLinkMic();
    }


    @Override
    protected void onPause() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.pause();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.resume();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.resume();
        }
    }

    protected void release() {
        EventBus.getDefault().unregister(this);
        LiveHttpUtil.cancel(LiveHttpConsts.SEND_DANMU);
        if (mKeyBoardHeightUtil != null) {
            mKeyBoardHeightUtil.release();
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.release();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.release();
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.release();
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.release();
        }
        if (mLiveAddImpressViewHolder != null) {
            mLiveAddImpressViewHolder.release();
        }
        if (mLiveContributeViewHolder != null) {
            mLiveContributeViewHolder.release();
        }
        if (mLiveLuckGiftTipViewHolder != null) {
            mLiveLuckGiftTipViewHolder.release();
        }
//        if (mMobShareUtil != null) {
//            mMobShareUtil.release();
//        }
        if (mImageUtil != null) {
            mImageUtil.release();
        }
//        if (mGamePresenter != null) {
//            mGamePresenter.release();
//        }
        mKeyBoardHeightUtil = null;
        mLiveLinkMicPresenter = null;
        mLiveLinkMicAnchorPresenter = null;
        mLiveLinkMicPkPresenter = null;
        mLiveRoomViewHolder = null;
        mLiveAddImpressViewHolder = null;
        mLiveContributeViewHolder = null;
        mLiveLuckGiftTipViewHolder = null;
//        mMobShareUtil = null;
        mImageUtil = null;
        L.e("LiveActivity--------release------>");
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }

    public String getStream() {
        return mStream;
    }

    public String getTxAppId() {
        return mTxAppId;
    }

    public static <B> B modelA2B(Object modelA, Class<B> bClass) {
        try {
            Gson gson = new Gson();
            String gsonA = gson.toJson(modelA);
            B instanceB = gson.fromJson(gsonA, bClass);

//            Log.d(TAG, "modelA2B A=" + modelA.getClass() + " B=" + bClass + " 转换后=" + instanceB);
            return instanceB;
        } catch (Exception e) {

//            Log.e(TAG, "modelA2B Exception=" + modelA.getClass() + " " + bClass + " " + e.getMessage());
            return null;
        }
    }

    //主播端推送用户列表给用户端，用户端重写该方法
    @Override
    public void onSendUserList(String user, String nums) {
//        Toast.makeText(mContext, "nums="+nums, Toast.LENGTH_SHORT).show();
    }


    /**
     * 直播间超管关闭直播间
     */
    @Override
    public void onStopLive() {

    }
}
