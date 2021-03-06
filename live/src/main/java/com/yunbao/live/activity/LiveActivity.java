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
    protected LiveLinkMicPresenter mLiveLinkMicPresenter;//???????????????????????????
    protected LiveLinkMicAnchorPresenter mLiveLinkMicAnchorPresenter;//???????????????????????????
    protected LiveLinkMicPkPresenter mLiveLinkMicPkPresenter;//???????????????PK??????
    //    protected GamePresenter mGamePresenter;
    protected SocketClient mSocketClient;
    protected LiveBean mLiveBean;
    protected int mLiveSDK;//sdk??????  0??????  1??????
    protected String mTxAppId;//??????sdkAppId
    protected boolean mIsAnchor;//???????????????
    protected int mSocketUserType;//socket????????????  30 ????????????  40 ?????????  50 ??????  60??????
    protected String mStream;
    protected String mLiveUid;
    protected String mOrUid;
    protected String mDanmuPrice;//????????????
    protected String mCoinName;//????????????
    protected int mLiveType;//??????????????????  ?????? ?????? ?????? ?????????
    protected int mLiveTypeVal;//????????????,??????????????????????????????
    protected KeyBoardHeightUtil2 mKeyBoardHeightUtil;
    protected int mDanMuLevel;//??????????????????
    protected int mChatLevel;  //  ????????????????????????
    protected int speak_limit_type;        // ?????????????????? 1-????????????  2-VIP??????
    protected int vip_speak_limit;        //  ??????VIP????????????

    protected int user_level;//??????
    private ProcessImageUtil mImageUtil;
    private boolean mFirstConnectSocket;//??????????????????????????????socket
    private boolean mGamePlaying;//??????????????????
    private boolean mChatRoomOpened;//????????????????????????????????????
    private LiveChatRoomFragment mLiveChatRoomDialogFragment;//??????????????????
    protected LiveGuardInfo mLiveGuardInfo;
    private HashSet<DialogFragment> mDialogFragmentSet;
    protected int reConnectNum=0;//socket????????????

    public boolean isScoller() {
        return isScoller;
    }

    public void setScoller(boolean scoller) {
        isScoller = scoller;
    }

    private boolean isScoller = false;
    protected String mSpeakLimitHref;//????????????????????????
    private LiveChatRoomFragment chatRoomDialogFragment;//??????????????????
    private LiveChatListFragment chatListRoomDialogFragment;//??????????????????

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
     * ????????????socket?????????
     */


    @Override
    public void onConnect(boolean successConn) {
        reConnectNum=0;
        L.e("WOLF","onConnect:"+reConnectNum);
        if (successConn) {
            if (!mFirstConnectSocket) {
                L.e("WOLF", "???????????????");
                mFirstConnectSocket = true;
//                if (mLiveType == Constants.LIVE_TYPE_PAY || mLiveType == Constants.LIVE_TYPE_TIME) {
//                    SocketChatUtil.sendUpdateVotesMessage(mSocketClient, mLiveTypeVal, 1);
//                }
                SocketChatUtil.getFakeFans(mSocketClient);
            } else {
                L.e("WOLF", "??????????????????");
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
     * ?????????socket??????
     */
    @Override
    public void onDisConnect() {

    }

    /**
     * ??????socket
     */
    @Override
    public void onReconnect() {

    }

    /**
     * ??????????????????
     * 29105
     */
    @Override
    public void onChat(LiveChatBean bean) {
        if (bean == null) {
            return;
        }

        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.insertChat(bean);
            //??????????????????
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
     * ??????????????????
     */
    @Override
    public void onLight() {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.playLightAnim();
        }
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void onEnterRoom(LiveEnterRoomBean bean) {
        if (mLiveRoomViewHolder != null) {
            LiveUserGiftBean u = bean.getUserBean();
            mLiveRoomViewHolder.insertUser(u);
            //??????????????????????????????????????????????????????????????????????????????
//            mLiveRoomViewHolder.addComeInData(bean.getLiveChatBean());
            mLiveRoomViewHolder.insertEnterRoomChat(bean.getLiveChatBean());

            if (mContext instanceof LiveAnchorActivity && u.getLivePhone() != null && u.getLivePhone().equals("1") && CommonAppConfig.getInstance().getConfig().getConversation_switch().equals("1")) {//???????????????
                LiveEnterRoomBean newBean = modelA2B(bean, LiveEnterRoomBean.class);
                newBean.getLiveChatBean().setLivePhone(u.getLivePhone());
                mLiveRoomViewHolder.insertChat(newBean.getLiveChatBean());
            }
            mLiveRoomViewHolder.onEnterRoom(bean);
        }
    }

    /**
     * ??????????????????????????????
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
     * ??????????????????
     */
    @Override
    public void onSendGift(LiveReceiveGiftBean bean) {
        if (mLiveRoomViewHolder != null) {
            // mLiveRoomViewHolder.insertChat(bean.getLiveChatBean());
            mLiveRoomViewHolder.showGiftMessage(bean);
        }
    }

    /**
     * pk ??????????????????
     *
     * @param leftGift  ??????????????????
     * @param rightGift ??????????????????
     */
    @Override
    public void onSendGiftPk(long leftGift, long rightGift) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onPkProgressChanged(leftGift, rightGift);
        }
    }

    /**
     * ??????????????????
     */
    @Override
    public void onSendDanMu(LiveDanMuBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.showDanmu(bean);
        }
    }

    /**
     * ??????????????????????????????
     */
    @Override
    public void onSendPrivateLive(int type, String user) {

    }

    /**
     * ??????????????????????????????
     *
     * @param ct
     */
    @Override
    public void onLiveEnd(String ct) {
        hideDialogs();
    }

    /**
     * ?????????????????????
     *
     * @param s
     */
    @Override
    public void onSuperCloseLive(String s) {
        hideDialogs();
    }

    /**
     * ??????
     */
    @Override
    public void onKick(String touid) {

    }

    /**
     * ??????
     */
    @Override
    public void onShutUp(String touid, String content) {

    }

    /**
     * ????????????????????????
     */
    @Override
    public void onSetAdmin(String toUid, int isAdmin) {
        if (!TextUtils.isEmpty(toUid) && toUid.equals(CommonAppConfig.getInstance().getUid())) {
            mSocketUserType = isAdmin == 1 ? Constants.SOCKET_USER_TYPE_ADMIN : Constants.SOCKET_USER_TYPE_NORMAL;
        }
    }

    /**
     * ??????????????????????????????????????????????????????????????????
     */
    @Override
    public void onChangeTimeCharge(int type, int typeVal) {

    }

    /**
     * ??????????????????????????????????????????
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
     * ???????????????
     */
    @Override
    public void addFakeFans(List<LiveUserGiftBean> list) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.insertUser(list);
        }
    }

    /**
     * ?????????  ????????????????????????
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
     * ????????? ??????????????????
     */
    @Override
    public void onRedPack(LiveChatBean liveChatBean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setRedPackBtnVisible(true);
            mLiveRoomViewHolder.insertChat(liveChatBean);
        }
    }

    /**
     * ?????????????????????  ?????????????????????????????????
     */
    @Override
    public void onAudienceApplyLinkMic(UserBean u) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceApplyLinkMic(u);
        }
    }

    /**
     * ?????????????????????  ?????????????????????????????????socket
     */
    @Override
    public void onAnchorAcceptLinkMic() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorAcceptLinkMic();
        }
    }

    /**
     * ?????????????????????  ?????????????????????????????????socket
     */
    @Override
    public void onAnchorRefuseLinkMic() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorRefuseLinkMic();
        }
    }

    /**
     * ?????????????????????  ???????????????????????????????????????
     */
    @Override
    public void onAudienceSendLinkMicUrl(String uid, String uname, String playUrl) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceSendLinkMicUrl(uid, uname, playUrl);
        }

    }

    /**
     * ?????????????????????  ???????????????????????????
     */
    @Override
    public void onAnchorCloseLinkMic(String touid, String uname) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorCloseLinkMic(touid, uname);
        }
    }

    /**
     * ?????????????????????  ????????????????????????
     */
    @Override
    public void onAudienceCloseLinkMic(String uid, String uname) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceCloseLinkMic(uid, uname);
        }
    }

    /**
     * ?????????????????????  ?????????????????????
     */
    @Override
    public void onAnchorNotResponse() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorNotResponse();
        }
    }

    /**
     * ?????????????????????  ???????????????
     */
    @Override
    public void onAnchorBusy() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorBusy();
        }
    }

    /**
     * ?????????????????????  ?????????????????????????????????????????????????????????
     */
    @Override
    public void onLinkMicAnchorApply(UserBean u, String stream) {
        //??????????????????????????????
    }

    /**
     * ?????????????????????  ???????????????????????????????????????????????????
     *
     * @param playUrl ???????????????????????????
     * @param pkUid   ???????????????uid
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
     * ?????????????????????  ?????????????????????
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
     * ?????????????????????  ?????????????????????????????????
     */
    @Override
    public void onLinkMicAnchorRefuse() {
        //??????????????????????????????
    }

    /**
     * ?????????????????????  ??????????????????????????????
     */
    @Override
    public void onLinkMicAnchorNotResponse() {
        //??????????????????????????????
    }

    /**
     * ?????????????????????  ????????????????????????
     */
    @Override
    public void onlinkMicPlayGaming() {
        //??????????????????????????????
    }

    /**
     * ?????????????????????  ??????????????????????????????
     */
    @Override
    public void onLinkMicAnchorBusy() {
        //??????????????????????????????
    }

    /**
     * ???????????????PK  ????????????????????????????????????PK???????????????
     *
     * @param u      ?????????????????????
     * @param stream ???????????????stream
     */
    @Override
    public void onLinkMicPkApply(UserBean u, String stream) {
        //??????????????????????????????
    }

    /**
     * ???????????????PK ???????????????PK???????????????
     */
    @Override
    public void onLinkMicPkStart(String pkUid) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkStart(pkUid);
        }
    }

    /**
     * ???????????????PK  ???????????????????????????pk?????????
     */
    @Override
    public void onLinkMicPkClose() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkClose();
        }
    }

    /**
     * ???????????????PK  ??????????????????pk?????????
     */
    @Override
    public void onLinkMicPkRefuse() {
        //??????????????????????????????
    }

    /**
     * ???????????????PK   ??????????????????????????????
     */
    @Override
    public void onLinkMicPkBusy() {
        //??????????????????????????????
    }

    /**
     * ???????????????PK   ??????????????????????????????
     */
    @Override
    public void onLinkMicPkNotResponse() {
        //??????????????????????????????
    }

    /**
     * ???????????????PK   ???????????????PK???????????????
     */
    @Override
    public void onLinkMicPkEnd(String winUid) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkEnd(winUid);
        }
    }


    /**
     * ???????????????????????????
     */
    @Override
    public void onAudienceLinkMicExitRoom(String touid) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceLinkMicExitRoom(touid);
        }
    }


    /**
     * ??????????????????
     */
    @Override
    public void onLuckGiftWin(LiveLuckGiftWinBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onLuckGiftWin(bean);
        }
    }

    /**
     * ????????????
     */
    @Override
    public void onPrizePoolWin(LiveGiftPrizePoolWinBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onPrizePoolWin(bean);
        }
    }


    /**
     * ????????????
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
     * ???????????????????????????????????????????????????
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
//     * ????????????????????????
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
     * ????????????????????????????????????????????????????????????list??????(????????????),?????????????????????????????????????????????????????????
     * ????????????????????????????????????
     * ?????????????????????????????????????????????????????????
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
     * ???????????????????????????
     */

    public void openUserListRoomWindow(UserBean userBean, boolean following,boolean  isUser) {
            openAnchorChatList(userBean, following,true);
    }
    /**
     * ????????????????????????????????????????????????????????????
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
     * ?????????????????????????????????????????????????????????????????????
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
        Log.e("??????????????????--", new Gson().toJson(userBean));
        getFollowData(userBean, following, isAnchor);
    }

    /**
     * ??????????????????????????????????????????
     */
    public void getFollowData(final UserBean userBean, final boolean following, final boolean isAnchor) {
        CommonHttpUtil.getBothFollowed(userBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    Log.e("??????????????????????????????????????????------", info[0]);
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
     * ??????????????????????????????
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
     * ?????????????????????
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
                //????????????????????????????????????????????????????????????????????????????????????????????????????????????
                chatRoomDialogFragment.onReceivePrivateLetterMsg(bean);
            }
        }

    }

    /**
     * ?????????????????????????????????
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
     * ?????????????????????????????????
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
     * ??? ?????? ??????
     */
    public void sendDanmuMessage(String content) {
        if (!mIsAnchor) {
            UserBean u = CommonAppConfig.getInstance().getUserBean();
            String is_super = SpUtil.getInstance().getStringValue(SpUtil.IS_SUPER);
            //???????????????????????????
            if (!TextUtils.isEmpty(is_super) && is_super.equals("1")) {
                LiveHttpUtil.sendDanmu(content, mLiveUid, mStream, mOrUid, mDanmuCallback);
                return;
            }

            if (u != null && u.getVip().getVip_is_king() == 1) {
                if (u.getVip().getType() < mDanMuLevel) {
                    showLimitChatDialog(mContext, mDanMuLevel, "??????");
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
     * ??? ?????? ??????
     */
    public void sendChatMessage(String content) {
        if (!mIsAnchor) {
            UserBean u = CommonAppConfig.getInstance().getUserBean();
            String is_super = SpUtil.getInstance().getStringValue(SpUtil.IS_SUPER);
            //???????????????????????????
            if (!TextUtils.isEmpty(is_super) && is_super.equals("1")) {
                int guardType = mLiveGuardInfo != null ? mLiveGuardInfo.getMyGuardType() : Constants.GUARD_TYPE_NONE;
                SocketChatUtil.sendChatMessage(mSocketClient, content, mIsAnchor, mSocketUserType, guardType);
                return;
            }
//            if (u != null && u.getVip().getVip_is_king() == 1) {
//                if (u.getVip().getType() < mChatLevel) {
//                    showLimitChatDialog(mContext, mChatLevel, "??????");
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
                    showLimitChatDialog(mContext, mChatLevel, "??????");
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
     * ????????????dialog
     */
    public void showLimitChatDialog(final Context context, int level, String type) {
        DialogUitl.Builder builder = new DialogUitl.Builder(context);
        builder.setTitle("????????????")
                .setContent("???\uD83D\uDE0A???VIP" + level + "????????????" + type + "\n????????????VIP" + level + "?????????????????????")
                .setConfrimString("??????VIP")
                .setCancelString("??????")
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
     * ??? ?????? ??????
     */
    public void sendSystemMessage(String content) {
        SocketChatUtil.sendSystemMessage(mSocketClient, content);
    }

    /**
     * ??? ???????????? ??????
     *
     * @param bean
     */
    public void sendGiftSystemMessage(LiveGiftBean bean, String count) {
        SocketChatUtil.sendGiftSystemMessage(mSocketClient, bean, count);
    }

    /**
     * ??? ????????????
     */
    public void sendGzSystemMessage(String content, String follow) {
        SocketChatUtil.sendSystemMessage(mSocketClient, content, follow);
    }

    /**
     * ??? ????????? ??????
     */
    public void sendGiftMessage(LiveGiftBean giftBean, String giftToken, int only_danmu) {
        SocketChatUtil.sendGiftMessage(mSocketClient, giftBean.getType(), giftToken, mLiveUid, giftBean.getGifticon_mini(), giftBean.getNum(), only_danmu);
    }

    /**
     * ????????????????????????
     */
    public void kickUser(String toUid, String toName) {
        SocketChatUtil.sendKickMessage(mSocketClient, toUid, toName);
    }

    /**
     * ??????
     */
    public void setShutUp(String toUid, String toName, int type) {
        SocketChatUtil.sendShutUpMessage(mSocketClient, toUid, toName, type);
    }

    /**
     * ??????????????????????????????
     */
    public void sendSetAdminMessage(int action, String toUid, String toName) {
        SocketChatUtil.sendSetAdminMessage(mSocketClient, action, toUid, toName);
    }


    /**
     * ?????????????????????
     */
    public void superCloseRoom() {
        SocketChatUtil.superCloseRoom(mSocketClient);
    }

    /**
     * ?????????????????????
     */
    public void sendUpdateVotesMessage(int deltaVal) {
        SocketChatUtil.sendUpdateVotesMessage(mSocketClient, deltaVal);
    }


    /**
     * ??????????????????????????????
     */
    public void sendBuyGuardMessage(String votes, int guardNum, int guardType) {
        SocketChatUtil.sendBuyGuardMessage(mSocketClient, votes, guardNum, guardType);
    }

    /**
     * ???????????????????????????
     */
    public void sendRedPackMessage() {
        SocketChatUtil.sendRedPackMessage(mSocketClient);
    }


    /**
     * ????????????????????????
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
     * ????????????????????????
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
     * ????????????????????????
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
     * ??????????????????
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
     * ??????????????????
     */
    public void openShareWindow() {
        LiveShareDialogFragment fragment = new LiveShareDialogFragment();
        fragment.setActionListener(this);
        fragment.show(getSupportFragmentManager(), "LiveShareDialogFragment");
    }

    /**
     * ????????????????????????
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
     * ?????????????????????
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
     * ???????????????
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
     * ????????????????????????
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
     * ???????????????????????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
        String unReadCount = e.getUnReadCount();
        if (!TextUtils.isEmpty(unReadCount) && mLiveBottomViewHolder != null) {
            mLiveBottomViewHolder.setUnReadCount(unReadCount);
        }
    }

    /**
     * ??????????????????????????????
     */
    protected String getImUnReadCount() {
        return ImMessageUtil.getInstance().getAllUnReadMsgCount();
    }

    /**
     * ??????????????????????????????
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
     * ??????????????????
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
     * ???????????????????????????
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
     * ????????????????????????
     */
    public void openRedPackSendWindow() {
        LiveRedPackSendDialogFragment fragment = new LiveRedPackSendDialogFragment();
        fragment.setStream(mStream);
        //fragment.setCoinName(mCoinName);
        fragment.show(getSupportFragmentManager(), "LiveRedPackSendDialogFragment");
    }

    /**
     * ???????????????????????????
     */
    public void openRedPackListWindow() {
        LiveRedPackListDialogFragment fragment = new LiveRedPackListDialogFragment();
        fragment.setStream(mStream);
        fragment.setCoinName(mCoinName);
        fragment.show(getSupportFragmentManager(), "LiveRedPackListDialogFragment");
    }


    /**
     * ??????????????????
     */
    public void openPrizePoolWindow() {
        GiftPrizePoolFragment fragment = new GiftPrizePoolFragment();
        fragment.setLiveUid(mLiveUid);
        fragment.setStream(mStream);
        fragment.show(getSupportFragmentManager(), "GiftPrizePoolFragment");
    }

    /**
     * ????????????????????????
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
     * ?????????????????????
     */
    @Override
    public void onKeyBoardHeightChanged(int visibleHeight, int keyboardHeight) {
        if (mChatRoomOpened) {//????????????????????????????????????
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
     * ??????????????????
     */
    public boolean isGamePlaying() {
        return mGamePlaying;
    }

    public void setGamePlaying(boolean gamePlaying) {
        mGamePlaying = gamePlaying;
    }

    /**
     * ??????????????????
     */
    public boolean isLinkMic() {
        return mLiveLinkMicPresenter != null && mLiveLinkMicPresenter.isLinkMic();
    }

    /**
     * ????????????????????????
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

//            Log.d(TAG, "modelA2B A=" + modelA.getClass() + " B=" + bClass + " ?????????=" + instanceB);
            return instanceB;
        } catch (Exception e) {

//            Log.e(TAG, "modelA2B Exception=" + modelA.getClass() + " " + bClass + " " + e.getMessage());
            return null;
        }
    }

    //??????????????????????????????????????????????????????????????????
    @Override
    public void onSendUserList(String user, String nums) {
//        Toast.makeText(mContext, "nums="+nums, Toast.LENGTH_SHORT).show();
    }


    /**
     * ??????????????????????????????
     */
    @Override
    public void onStopLive() {

    }
}
