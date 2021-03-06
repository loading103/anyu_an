package com.yunbao.live.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.opensource.svgaplayer.SVGAImageView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UrlBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.MyViewPager;
import com.yunbao.common.event.FollowAncorEvent;
import com.yunbao.common.event.PreViewEvent;
import com.yunbao.common.event.ShowMsgEvent;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DownloadUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RandomUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.adapter.LiveRoomScrollAdapter;
import com.yunbao.live.bean.GoodsResultBean;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.live.bean.LiveGuardInfo;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.live.bean.LiveUserChatBean;
import com.yunbao.live.bean.LiveUserGiftBean;
import com.yunbao.live.custom.MyCountDownTimer;
import com.yunbao.live.dialog.LiveGiftDialogFragment;
import com.yunbao.live.dialog.LiveMoreDialogFragment;
import com.yunbao.live.dialog.LiveShopDialogFragment;
import com.yunbao.live.dialog.LiveYinLiuDialog;
import com.yunbao.live.event.DissShopDialogEvent;
import com.yunbao.live.event.LinkMicTxAccEvent;
import com.yunbao.live.event.LiveCloseDialogEvent;
import com.yunbao.live.event.LiveCloseEvent;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.common.interfaces.DissmissDialogListener;
import com.yunbao.live.presenter.LiveLinkMicAnchorPresenter;
import com.yunbao.live.presenter.LiveLinkMicPkPresenter;
import com.yunbao.live.presenter.LiveLinkMicPresenter;
import com.yunbao.live.presenter.LiveRoomCheckLivePresenter;
import com.yunbao.live.socket.SocketChatUtil;
import com.yunbao.live.socket.SocketClient;
import com.yunbao.live.utils.LiveStorge;
import com.yunbao.live.views.LiveAudienceViewHolder;
import com.yunbao.live.views.LiveEndLayoutView;
import com.yunbao.live.views.LiveEndViewHolder;
import com.yunbao.live.views.LivePlayTxViewHolder;
import com.yunbao.live.views.LiveRoomPlayViewHolder;
import com.yunbao.live.views.LiveRoomViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2018/10/10.
 */

public class LiveAudienceActivity extends LiveActivity {

    private static final String TAG = "LiveAudienceActivity";
    private LiveReadyBean.GoodsBean goods;
    private List<LiveReadyBean.GoodsBean> games;
    private UserBean.Vip vipBean;
    private boolean mLighted;
    private long mLastLightClickTime;
    private LiveGiftDialogFragment fragmentGift;
    private LiveShopDialogFragment fragmentShop;
    protected LiveYinLiuDialog liveYinLiuDialog;//????????????
    private MyCountDownTimer mTimer;
    private int cdnSwitch;//7 ???????????? |?????? ???????????????
    private String mUrl;//???????????????url(???????????????)
    private String issuper;//1????????????0???????????? ?????????????????????vip??????
    private int isPreview;//1 ?????? 2 ?????????

    public static void forward(Context context, LiveBean liveBean, int liveType, int liveTypeVal, String key, int position, int liveSdk, int cdnSwitch, String pullUrl, int isPreview) {
        Intent intent = new Intent(context, LiveAudienceActivity.class);
        intent.putExtra(Constants.LIVE_BEAN, liveBean);
        intent.putExtra(Constants.LIVE_TYPE, liveType);
        intent.putExtra(Constants.LIVE_TYPE_VAL, liveTypeVal);
        intent.putExtra(Constants.LIVE_KEY, key);
        intent.putExtra(Constants.LIVE_POSITION, position);
        intent.putExtra(Constants.LIVE_SDK, liveSdk);
        intent.putExtra(Constants.LIVE_CDN, cdnSwitch);
        intent.putExtra(Constants.LIVE_PULL_URL, pullUrl);
        intent.putExtra(Constants.LIVE_IS_PREVIEW, isPreview);
        context.startActivity(intent);
    }

    private String mKey;
    private int mPosition;
    private RecyclerView mRecyclerView;
    private LiveRoomScrollAdapter mRoomScrollAdapter;
    private View mMainContentView;
    private MyViewPager mViewPager;
    private ViewGroup mSecondPage;//?????????????????????
    private FrameLayout mContainerWrap;
    private LiveRoomPlayViewHolder mLivePlayViewHolder;
    private LiveAudienceViewHolder mLiveAudienceViewHolder;

    private boolean mEnd;
    private boolean mCoinNotEnough;//????????????
    private LiveRoomCheckLivePresenter mCheckLivePresenter;
    private Handler handler = new Handler();
    private JSONObject obj;
    private String canSlide;//????????????????????????
    Dialog dialog;

    public boolean ismEnd() {
        return mEnd;
    }

    @Override
    public <T extends View> T findViewById(@IdRes int id) {
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            if (mMainContentView != null) {
                return mMainContentView.findViewById(id);
            }
        }
        return super.findViewById(id);
    }

    @Override
    protected int getLayoutId() {
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            return R.layout.activity_live_audience_2;
        }
        return R.layout.activity_live_audience;
    }

    public void setScrollFrozen(boolean frozen) {
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutFrozen(frozen);
        }
    }

    public int getIsPreview() {
        return isPreview;
    }

    public void setIsPreview(int isPreview) {
        this.isPreview = isPreview;
    }

    public void setCanSlide(String canSlide) {
        this.canSlide = canSlide;
    }

    @Override
    protected void main() {
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            canSlide = SpUtil.getInstance().getStringValue(SpUtil.SLIDE_UP);
            mRecyclerView = super.findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return canSlide.equals("1") ? true : false;
                }
            });
            mMainContentView = LayoutInflater.from(mContext).inflate(R.layout.activity_live_audience, null, false);
        }
        dialog = DialogUitl.loadingNew2WhiteDialog(mContext);
        dialog.show();
        super.main();
        Intent intent = getIntent();
        cdnSwitch = intent.getIntExtra(Constants.LIVE_CDN, 7);
        isPreview = intent.getIntExtra(Constants.LIVE_IS_PREVIEW, 2);
        mUrl = intent.getStringExtra(Constants.LIVE_PULL_URL);
        mLiveSDK = intent.getIntExtra(Constants.LIVE_SDK, Constants.LIVE_SDK_KSY);
        mLiveType = intent.getIntExtra(Constants.LIVE_TYPE, Constants.LIVE_TYPE_NORMAL);
        mLiveTypeVal = intent.getIntExtra(Constants.LIVE_TYPE_VAL, 0);
        L.e(TAG, "??????sdk----->" + (mLiveSDK == Constants.LIVE_SDK_KSY ? "?????????" : "?????????"));
        if (mLiveSDK == Constants.LIVE_SDK_TX || CommonAppConfig.LIVE_ROOM_SCROLL) {
            //?????????????????????
            mLivePlayViewHolder = new LivePlayTxViewHolder(mContext, (ViewGroup) findViewById(R.id.play_container));
        } else {
            mLivePlayViewHolder = new LivePlayTxViewHolder(mContext, (ViewGroup) findViewById(R.id.play_container));
            //??????????????????
//            mLivePlayViewHolder = new LivePlayKsyViewHolder(mContext, (ViewGroup) findViewById(R.id.play_container));
        }
        mLivePlayViewHolder.addToParent();
        mLivePlayViewHolder.subscribeActivityLifeCycle();
        mViewPager = (MyViewPager) findViewById(R.id.viewPager);
        mSecondPage = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.view_audience_page, mViewPager, false);
        mContainerWrap = mSecondPage.findViewById(R.id.container_wrap);
        mContainer = mSecondPage.findViewById(R.id.container);
        mLiveRoomViewHolder = new LiveRoomViewHolder(mContext, mContainer, (GifImageView) mSecondPage.findViewById(R.id.gift_gif), (SVGAImageView) mSecondPage.findViewById(R.id.gift_svga), mContainerWrap);
        mLiveRoomViewHolder.addToParent();
        mLiveRoomViewHolder.subscribeActivityLifeCycle();
        mLiveAudienceViewHolder = new LiveAudienceViewHolder(mContext, mContainer);
        mLiveAudienceViewHolder.subscribeActivityLifeCycle();

        mLiveAudienceViewHolder.addToParent();
        mLiveAudienceViewHolder.setUnReadCount(getImUnReadCount());
        mLiveBottomViewHolder = mLiveAudienceViewHolder;
        mViewPager.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                if (position == 0) {
                    View view = new View(mContext);
                    view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    container.addView(view);
                    return view;
                } else {
                    container.addView(mSecondPage);
                    return mSecondPage;
                }
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            }
        });
        mViewPager.setCurrentItem(1);
        mLiveLinkMicPresenter = new LiveLinkMicPresenter(mContext, mLivePlayViewHolder, false, mLiveSDK, mLiveAudienceViewHolder.getContentView());
        mLiveLinkMicAnchorPresenter = new LiveLinkMicAnchorPresenter(mContext, mLivePlayViewHolder, false, mLiveSDK, null);
        mLiveLinkMicPkPresenter = new LiveLinkMicPkPresenter(mContext, mLivePlayViewHolder, false, null);
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            mKey = intent.getStringExtra(Constants.LIVE_KEY);
            mPosition = intent.getIntExtra(Constants.LIVE_POSITION, 0);
            List<LiveBean> list = LiveStorge.getInstance().get(mKey);
            mRoomScrollAdapter = new LiveRoomScrollAdapter(mContext, list, mPosition);
            mRoomScrollAdapter.setActionListener(new LiveRoomScrollAdapter.ActionListener() {
                @Override
                public void onPageSelected(LiveBean liveBean, ViewGroup container, LiveEndLayoutView ll_end, boolean first) {
                    L.e(TAG, "onPageSelected----->" + liveBean);
                    if (mMainContentView != null && container != null) {
                        mLighted = false;
                        ViewParent parent = mMainContentView.getParent();
                        if (parent != null) {
                            ViewGroup viewGroup = (ViewGroup) parent;
                            if (viewGroup != container) {
                                viewGroup.removeView(mMainContentView);
                                container.addView(mMainContentView);
                            }
                        } else {
                            container.addView(mMainContentView);
                        }
                    }
                    if (!first) {
                        L.e(TAG, "?????????????????????");
                        setScoller(true);
                        checkLive(liveBean, ll_end, true);
                        mLiveAudienceViewHolder.setFirst(true);
                        mLivePlayViewHolder.setCover("");
                    }
                    mLiveRoomViewHolder.dismissResult();
                }

                @Override
                public void onPageOutWindow(String liveUid) {
                    L.e(TAG, "onPageOutWindow----->" + liveUid);
                    if (TextUtils.isEmpty(mLiveUid) || mLiveUid.equals(liveUid)) {
                        LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
                        LiveHttpUtil.cancel(LiveHttpConsts.ENTER_ROOM);
                        LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
                        clearRoomData(false);
                    }
                }
            });
            mRecyclerView.setAdapter(mRoomScrollAdapter);
        }
        LiveBean liveBean = intent.getParcelableExtra(Constants.LIVE_BEAN);
        setLiveRoomData(liveBean);
        mLivePlayViewHolder.setData(mLiveType, mLiveTypeVal, mLiveUid, mStream);
        enterRoom(false, true);

    }

    private void setLiveRoomData(LiveBean liveBean) {
        mLiveBean = liveBean;
        mLiveUid = liveBean.getUid();
        mStream = liveBean.getStream();
        mOrUid = liveBean.getLiveuid();
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.setCover(liveBean.getThumb());
            String playUrl = liveBean.getIsvideo().equals("1") ? liveBean.getPull() :
                    CommonAppConfig.getInstance().getFastPull() + liveBean.getPull() + "?ip=" +
                            CommonAppConfig.getInstance().getFastPull().replace("rtmp://", "");
            if (cdnSwitch == 7) {//????????????
                mLivePlayViewHolder.play(playUrl);
            } else {//?????????????????????
                mLivePlayViewHolder.play(liveBean.getIsvideo().equals("1") ? playUrl : mUrl);
            }
            L.e("WOLF", "playUrl:" + playUrl);
        }

        if (mLiveAudienceViewHolder != null) {
            mLiveAudienceViewHolder.setLiveInfo(mLiveUid, mStream);
            mLiveAudienceViewHolder.setPrivateInfo(liveBean.getIsvideo(), liveBean.getLive_tag());
        }

        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setAvatar(liveBean.getAvatar());
            mLiveRoomViewHolder.setAnchorLevel(liveBean.getLevelAnchor());
            mLiveRoomViewHolder.setName(liveBean.getUserNiceName());
            mLiveRoomViewHolder.setRoomNum(liveBean.getLiangNameTip());
            mLiveRoomViewHolder.setReplayName(liveBean.getIsvideo(), liveBean.getLive_tag());
        }
        if (!TextUtils.isEmpty(mLiveUid)) {
            if (mLiveLinkMicPkPresenter != null) {
                mLiveLinkMicPkPresenter.setLiveUid(mLiveUid);
            }
            if (mLiveLinkMicPresenter != null) {
                mLiveLinkMicPresenter.setLiveUid(mLiveUid);
            }
        } else {
            finish();
        }

    }

    /**
     * ????????????false???true????????????????????????
     */
    public void clearRoomData(boolean saveHeadview) {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }

        isPreview = 2;
        mSocketClient = null;
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.stopPlay();
        }
        if (mLiveRoomViewHolder != null) {
            if (saveHeadview) {
                mLiveRoomViewHolder.clearDataSaveHead();
            } else {
                mLiveRoomViewHolder.clearData();
            }
        }
        if (mLiveAudienceViewHolder != null) {
            mLiveAudienceViewHolder.clearData();
        }

//        if (mGamePresenter != null) {
//            mGamePresenter.clearGame();
//        }
        if (mLiveEndViewHolder != null) {
            mLiveEndViewHolder.removeFromParent();
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.clearData();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.clearData();
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.clearData();
        }
        if (mLivePlayViewHolder != null) {
            ((LivePlayTxViewHolder) mLivePlayViewHolder).clearTime();
        }
    }

    private void checkLive(LiveBean bean, LiveEndLayoutView mMainContentView, final boolean isenterRoomLoading) {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new LiveRoomCheckLivePresenter(mContext, new LiveRoomCheckLivePresenter.ActionListener() {
                @Override
                public void onLiveRoomChanged(LiveBean liveBean, int liveType, int liveTypeVal, int cdn_Switch, String pullUrl) {
                    if (liveBean == null) {
                        return;
                    }
                    cdnSwitch = cdn_Switch;
                    mUrl = pullUrl;
                    setLiveRoomData(liveBean);
                    mLiveType = liveType;
                    mLiveTypeVal = liveTypeVal;
                    if (mRoomScrollAdapter != null) {
                        mRoomScrollAdapter.hideCover();
                    }
                    enterRoom(isenterRoomLoading, true);
                }
            });
        }
        mCheckLivePresenter.checkLive(bean, mMainContentView);
    }

    /**
     * ????????????????????????????????? ?????????enterRoom
     */
    public void enterTimeRoom() {
        setLiveRoomData(mLiveBean);
        if (mRoomScrollAdapter != null) {
            mRoomScrollAdapter.hideCover();
        }
        enterRoom(false, false);
    }

    /**
     * isenterRoomLoading???????????????loading???????????????????????????????????????????????????
     * isshow??????????????????????????????????????????????????????????????????  ????????????enterRoom,???????????????????????????
     */

    public void enterRoom(boolean isenterRoomLoading, final boolean isshow) {
        if (isenterRoomLoading) {
            if (dialog != null && !isDestroyed()) {
                dialog.show();
            }
        }
        LiveHttpUtil.enterRoom(mLiveUid, mStream, mOrUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                L.e("enterRoom", "onSuccess:" + info[0]);
                if (dialog != null && !isDestroyed()) {
                    dialog.dismiss();
                }
                if (code == 0 && info.length > 0) {
                    obj = JSON.parseObject(info[0]);
                    mDanmuPrice = obj.getString("barrage_fee");
                    mSocketUserType = obj.getIntValue("usertype");
                    mChatLevel = obj.getIntValue("speak_limit");
                    speak_limit_type = obj.getIntValue("speak_limit_type");
                    vip_speak_limit = obj.getIntValue("vip_speak_limit");
                    mDanMuLevel = obj.getIntValue("barrage_limit");
                    mSpeakLimitHref = obj.getString("speak_limit_href");
                    cdnSwitch = obj.getIntValue("cdn_switch");
                    issuper = obj.getString("issuper");
                    SpUtil.getInstance().setStringValue(SpUtil.IS_SUPER, issuper);
                    if (cdnSwitch != 7) {//???????????????
//                        ToastUtil.show("????????????????????????");
//                        mUrl=obj.getString("pull_url");
//                        updateUrl(obj.getString("pull_url"));
//                        mLivePlayViewHolder.setCover("");
                    } else {
//                        ToastUtil.show("?????????????????????");
                    }


                    goods = JSON.parseObject(obj.getJSONObject("goods").toJSONString(), LiveReadyBean.GoodsBean.class);
                    games = JSON.parseArray(obj.getJSONArray("games").toJSONString(), LiveReadyBean.GoodsBean.class);
                    vipBean = JSON.parseObject(obj.getJSONObject("vip").toJSONString(), UserBean.Vip.class);
                    if (vipBean != null) {
                        CommonAppConfig.getInstance().upDataVip(vipBean.getType(), vipBean.getVip_is_king());
                    }
                    //????????????
                    int ad_switch = obj.getIntValue("ad_switch");
                    int ad_time = obj.getIntValue("ad_time");


                    String ad_pic = obj.getString("ad_pic");
                    String ad_href = obj.getString("ad_href");

                    String ad_vip = obj.getString("ad_shut_vip_limit");
                    String ad_countdown = obj.getString("ad_shut_countdown");
                    String ad_is_king = obj.getString("ad_is_king");
                    String ad_show_style = obj.getString("ad_show_style");
                    if (isshow) {
                        startYinliuDialog(ad_switch, ad_time, ad_href, ad_pic, ad_vip, ad_countdown, ad_is_king, ad_show_style);
                    }
                    mLiveAudienceViewHolder.setGoods(goods);
                    mLiveAudienceViewHolder.setGames(games);
                    mLiveAudienceViewHolder.setPrivateLive(obj.getFloatValue("contribution2"),
                            obj.getIntValue("private_state"), obj.getIntValue("timeout"));
                    String key = CommonAppConfig.getInstance().getUid() + mStream;
                    SPUtils.getInstance().put(key, obj.getFloatValue("contribution2"));

                    if (mSocketClient != null) {
                        stopSocket();
                        if (mLiveRoomViewHolder != null) {
                            mLiveRoomViewHolder.clearChat();
                        }
                    }

                    //??????socket
                    mSocketClient = new SocketClient(obj.getString("chatserver"), LiveAudienceActivity.this);
                    if (mLiveLinkMicPresenter != null) {
                        mLiveLinkMicPresenter.setSocketClient(mSocketClient);
                    }
                    mSocketClient.connect(mLiveUid, mStream, obj.getString("contribution"));
                    if (mLiveRoomViewHolder != null) {
                        mLiveRoomViewHolder.setLiveInfo(mLiveUid, mStream, mOrUid, obj.getIntValue("userlist_time") * 1000);
                        mLiveRoomViewHolder.setVotes(obj.getString("votestotal"));
                        mLiveRoomViewHolder.setAttention(obj.getIntValue("isattention"));
                        List<LiveUserGiftBean> list = JSON.parseArray(obj.getString("userlists"), LiveUserGiftBean.class);
                        mLiveRoomViewHolder.setUserList(list);
//                        mLiveRoomViewHolder.setNumber(list.size()+1+"");
                        mLiveRoomViewHolder.setNumber(obj.getString("nums"));
                        mLiveRoomViewHolder.startRefreshUserList();
                        LiveUserChatBean live_user = JSON.parseObject(obj.getString("live_user"), LiveUserChatBean.class);
                        mLiveRoomViewHolder.setLiveUserChat(live_user);
                        if (mLiveType == Constants.LIVE_TYPE_TIME) {//????????????
                            mLiveRoomViewHolder.startRequestTimeCharge();
                        }
                    }
                    //?????????????????????????????????????????????
                    String linkMicUid = obj.getString("linkmic_uid");
                    String linkMicPull = obj.getString("linkmic_pull");
                    if (!TextUtils.isEmpty(linkMicUid) && !"0".equals(linkMicUid) && !TextUtils.isEmpty(linkMicPull)) {
                        if (mLiveSDK != Constants.LIVE_SDK_TX && mLiveLinkMicPresenter != null) {
                            mLiveLinkMicPresenter.onLinkMicPlay(linkMicUid, linkMicPull);
                        }
                    }
                    //???????????????????????????
                    JSONObject pkInfo = JSON.parseObject(obj.getString("pkinfo"));
                    if (pkInfo != null) {
                        String pkUid = pkInfo.getString("pkuid");
                        if (!TextUtils.isEmpty(pkUid) && !"0".equals(pkUid)) {
                            if (mLiveSDK != Constants.LIVE_SDK_TX) {
                                String pkPull = pkInfo.getString("pkpull");
                                if (!TextUtils.isEmpty(pkPull) && mLiveLinkMicAnchorPresenter != null) {
                                    mLiveLinkMicAnchorPresenter.onLinkMicAnchorPlayUrl(pkUid, pkPull);
                                }
                            } else {
                                if (mLivePlayViewHolder instanceof LivePlayTxViewHolder) {
                                    ((LivePlayTxViewHolder) mLivePlayViewHolder).setAnchorLinkMic(true, 0);
                                }
                            }
                        }
                        if (pkInfo.getIntValue("ifpk") == 1 && mLiveLinkMicPkPresenter != null) {//pk?????????
                            mLiveLinkMicPkPresenter.onEnterRoomPkStart(pkUid, pkInfo.getLongValue("pk_gift_liveuid"), pkInfo.getLongValue("pk_gift_pkuid"), pkInfo.getIntValue("pk_time") * 1000);
                        }
                    }

                    //????????????
                    mLiveGuardInfo = new LiveGuardInfo();
                    int guardNum = obj.getIntValue("guard_nums");
                    mLiveGuardInfo.setGuardNum(guardNum);
                    JSONObject guardObj = obj.getJSONObject("guard");
                    if (guardObj != null) {
                        mLiveGuardInfo.setMyGuardType(guardObj.getIntValue("type"));
                        mLiveGuardInfo.setMyGuardEndTime(guardObj.getString("endtime"));
                    }
                    if (mLiveRoomViewHolder != null) {
                        mLiveRoomViewHolder.setGuardNum(guardNum);
                        //????????????
                        mLiveRoomViewHolder.setRedPackBtnVisible(obj.getIntValue("isred") == 1);
                    }
                    //????????????
                    int giftPrizePoolLevel = obj.getIntValue("jackpot_level");
                    if (giftPrizePoolLevel >= 0) {
                        if (mLiveRoomViewHolder != null) {
                            mLiveRoomViewHolder.showPrizePoolLevel(String.valueOf(giftPrizePoolLevel));
                        }
                    }

                    //????????????
                    if (CommonAppConfig.GAME_ENABLE && mLiveRoomViewHolder != null && mLiveRoomViewHolder.getInnerContainer() != null) {
//                        GameParam param = new GameParam();
//                        param.setContext(mContext);
//                        param.setParentView(mContainerWrap);
//                        param.setTopView(mContainer);
//                        param.setInnerContainer(mLiveRoomViewHolder.getInnerContainer());
//                        param.setGameActionListener(new GameActionListenerImpl(LiveAudienceActivity.this, mSocketClient));
//                        param.setLiveUid(mLiveUid);
//                        param.setStream(mStream);
//                        param.setAnchor(false);
//                        param.setCoinName(mCoinName);
//                        param.setObj(obj);
//                        if (mGamePresenter == null) {
//                            mGamePresenter = new GamePresenter();
//                        }
//                        mGamePresenter.setGameParam(param);
                    }
                }
            }
        });
    }

    /**
     * ??????loading
     */
    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void showDialog() {
        if (dialog != null && !isDestroyed()) {
            dialog.show();
        }
    }

    /**
     * ??????????????????
     */
    private void startYinliuDialog(int adswitch, int adtime, final String picHeaf, final String picUrl, final String ad_vip, final String ad_countdown, final String ad_is_king, final String ad_show_style) {
        if (adswitch == 0 || TextUtils.isEmpty(picUrl)) {
            return;
        }
        mTimer = new MyCountDownTimer(adtime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("?????????????????????", "millisUntilFinished=" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                Log.e("???????????????????????????", "picUrl=" + picUrl + "---linkUrl" + picHeaf);
                showSimiDialog(picUrl, picHeaf, ad_vip, ad_countdown, ad_is_king, ad_show_style);

            }
        }.start();

    }

    /**
     * ???????????????????????????
     */
    private void showSimiDialog(String picUrl, final String picHeaf, String ad_vip, String ad_countdown, String ad_is_king, String ad_show_style) {
        if (!TextUtils.isEmpty(picUrl)) {
            String FileName = picUrl.substring(picUrl.lastIndexOf("/"), picUrl.length());
            if (TextUtils.isEmpty(FileName)) {
                return;
            }
            File file = new File(getCacheDir(), FileName);

            if (file.exists()) {
                Log.e("??????----------", FileName);
                liveYinLiuDialog = new LiveYinLiuDialog(picHeaf, file.getPath(), ad_countdown, ad_vip, ad_is_king, ad_show_style);
                liveYinLiuDialog.show(getSupportFragmentManager(), "LiveYinLiuDialog");
            } else {
                Log.e("?????????----------", FileName);
                downloadAdFile(picUrl, picHeaf, FileName, ad_countdown, ad_vip, ad_is_king, ad_show_style);
            }


        }
    }


    /**
     * ???????????????????????????
     */
    private void downloadAdFile(String url, final String picHeaf, String fileName, final String time, final String limitVip, final String ad_is_king, final String ad_show_style) {
        DownloadUtil downloadUtil = new DownloadUtil();
        downloadUtil.download("ad_video", getCacheDir(), fileName, url, new DownloadUtil.Callback() {
            @Override
            public void onSuccess(File file) {
                liveYinLiuDialog = new LiveYinLiuDialog(picHeaf, file.getPath(), time, limitVip, ad_is_king, ad_show_style);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (liveYinLiuDialog != null && !LiveAudienceActivity.this.isFinishing()) {
                                liveYinLiuDialog.show(getSupportFragmentManager(), "LiveYinLiuDialog");
                            }
                        } catch (Exception e) {
                            return;
                        }

                    }
                }, 2000);

            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onError(Throwable e) {
            }
        });
    }

    /**
     * ??????????????????
     */
    public void openGiftWindow() {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream)) {
            return;
        }
        fragmentGift = new LiveGiftDialogFragment();
        fragmentGift.setLiveGuardInfo(mLiveGuardInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putString(Constants.LIVE_STREAM, mStream);
        bundle.putString(Constants.LIVE_OR_UID, mOrUid);
        fragmentGift.setArguments(bundle);
        fragmentGift.show(((LiveAudienceActivity) mContext).getSupportFragmentManager(), "LiveGiftDialogFragment");
    }

    /**
     * ??????????????????
     */
    public void openGameWindow() {
        if (games == null || games.size() == 0) {
            Toast.makeText(mContext, "?????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        fragmentShop = new LiveShopDialogFragment(games);
        fragmentShop.show(getSupportFragmentManager(), "LiveShopDialogFragment");
        fragmentShop.setLiveGuardInfo();

        //????????????????????????????????????
//        if(mLiveAudienceViewHolder!=null){
//            mLiveAudienceViewHolder.llRoot.setVisibility(View.INVISIBLE);
//        }
//        setChatShow(false);

        //  ??????????????????????????????????????????
        fragmentShop.setOnDissmissDialogListener(new DissmissDialogListener() {
            @Override
            public void onDissmissListener() {
                if (mLiveAudienceViewHolder != null) {
                    mLiveAudienceViewHolder.llRoot.setVisibility(View.VISIBLE);
                }
                setChatShow(true);
            }
        });
    }

    /**
     * ????????????web??????
     */
    public void openMorewebWindow() {
        LiveMoreDialogFragment fragment = new LiveMoreDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MORE_WEB_URL, "http://www.baidu.com");
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveMoreDialogFragment");
    }

    /**
     * ????????????
     */
    private void endPlay() {
        LiveHttpUtil.cancel(LiveHttpConsts.ENTER_ROOM);
        if (mEnd) {
            return;
        }
        mEnd = true;
        //??????socket
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        mSocketClient = null;
        //????????????
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.release();
        }
        mLivePlayViewHolder = null;
        release();
    }


    public LiveReadyBean.GoodsBean getGoods() {
        return goods;
    }

    @Override
    public void release() {
        super.release();
        if (mRoomScrollAdapter != null) {
            mRoomScrollAdapter.setActionListener(null);
        }
        mRoomScrollAdapter = null;
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param ct
     */
    @Override
    public void onLiveEnd(String ct) {
        super.onLiveEnd(ct);
        ToastUtil.show(ct);
        liveEnd();
    }

    /**
     * ????????????
     */
    private void liveEnd() {
        if (!CommonAppConfig.LIVE_ROOM_SCROLL) {
            if (mViewPager != null) {
                if (mViewPager.getCurrentItem() != 1) {
                    mViewPager.setCurrentItem(1, false);
                }
                mViewPager.setCanScroll(false);
            }
            endPlay();
        } else {
            if (mLivePlayViewHolder != null) {
                mLivePlayViewHolder.stopPlay2();
            }
        }

        if (mLiveEndViewHolder == null) {
            mLiveEndViewHolder = new LiveEndViewHolder(mContext, mSecondPage);
            mLiveEndViewHolder.subscribeActivityLifeCycle();
            mLiveEndViewHolder.addToParent();
        }
        mLiveEndViewHolder.showData(mLiveBean, mStream);

        if (fragmentGift != null) {
            fragmentGift.dismiss();
        }
        if (fragmentShop != null) {
            fragmentShop.dismiss();
        }
    }

    /**
     * ??????????????????????????????
     */
    public void onLiveEndBean(final LiveBean mLiveBean, LiveEndLayoutView ll_end) {
        if (ll_end != null) {
            ll_end.showData(mLiveBean, mLiveBean.getStream());
        }
    }

    /**
     * ??????????????????????????????
     */
    public void onLiveEndDissloading() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * ????????????????????????
     */
    @Override
    public void onKick(String touid) {
        if (!TextUtils.isEmpty(touid) && touid.equals(CommonAppConfig.getInstance().getUid())) {//??????????????????
            exitLiveRoom();
            ToastUtil.show(WordUtil.getString(R.string.live_kicked_2));
        }
    }

    /**
     * ????????????????????????
     */
    @Override
    public void onShutUp(String touid, String content) {
        if (!TextUtils.isEmpty(touid) && touid.equals(CommonAppConfig.getInstance().getUid())) {
            DialogUitl.showSimpleTipDialog(mContext, content);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mEnd && !canBackPressed()) {
            return;
        }
        exitLiveRoom();
    }

    /**
     * ???????????????
     */
    public void exitLiveRoom() {
        endPlay();
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        endPlay();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * ??????
     */
    public void light() {
        if (!mLighted) {
            mLighted = true;
            int guardType = mLiveGuardInfo != null ? mLiveGuardInfo.getMyGuardType() : Constants.GUARD_TYPE_NONE;
            SocketChatUtil.sendLightMessage(mSocketClient, 1 + RandomUtil.nextInt(6), guardType);
        } else {
            long cutTime = System.currentTimeMillis();
            if (cutTime - mLastLightClickTime < 5000) {
                if (mLiveRoomViewHolder != null) {
                    mLiveRoomViewHolder.playLightAnim();
                }
            } else {
                mLastLightClickTime = cutTime;
                SocketChatUtil.sendFloatHeart(mSocketClient);
            }
        }
        //????????????????????????
//        if (mLiveRoomViewHolder != null) {
//            mLiveRoomViewHolder.playLightAnim();
//        }
    }


    /**
     * ?????????????????????????????????
     */
    public void roomChargeUpdateVotes() {
//        sendUpdateVotesMessage(mLiveTypeVal);
    }



    /**
     * ????????????
     */
    public void pausePlay() {
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.pausePlay();
        }
    }

    /**
     * ????????????
     */
    public void resumePlay() {
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.resumePlay();
        }
    }

    /**
     * ????????????
     */
    public void onChargeSuccess() {
        if (mLiveType == Constants.LIVE_TYPE_TIME) {
            if (mCoinNotEnough) {
                mCoinNotEnough = false;
                LiveHttpUtil.roomCharge(mLiveUid, mStream, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            roomChargeUpdateVotes();
                            if (mLiveRoomViewHolder != null) {
                                if (mLivePlayViewHolder != null) {
                                    ((LivePlayTxViewHolder) mLivePlayViewHolder).reConnection();
                                }

                                resumePlay();
                                enterRoom(false, false);
                                mLiveRoomViewHolder.startRequestTimeCharge();
                            }
                        } else {
                            if (code == 1008 || code == -1) {//????????????
                                mCoinNotEnough = true;
                                DialogUitl.showSimpleTipCallDialog(mContext, "????????????", WordUtil.getString(R.string.live_coin_not_enough), new DialogUitl.SimpleCallback() {
                                    @Override
                                    public void onConfirmClick(Dialog dialog, String content) {
                                        exitLiveRoom();
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
    }

    public void setCoinNotEnough(boolean coinNotEnough) {
        mCoinNotEnough = coinNotEnough;
    }

    /**
     * ????????????????????????
     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onGameWindowChangedEvent(GameWindowChangedEvent e) {
//        if (mLiveRoomViewHolder != null) {
//            mLiveRoomViewHolder.setOffsetY(e.getGameViewHeight());
//        }
//    }

    /**
     * ??????sdk??????????????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLinkMicTxAccEvent(LinkMicTxAccEvent e) {
        if (mLivePlayViewHolder != null && mLivePlayViewHolder instanceof LivePlayTxViewHolder) {
            ((LivePlayTxViewHolder) mLivePlayViewHolder).onLinkMicTxAccEvent(e.isLinkMic());
        }
    }

    /**
     * H5??????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMsgEvent(ShowMsgEvent e) {
        doRedirect(e);
    }

    /**
     * ????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseEvent(LiveCloseEvent e) {
        onBackPressed();
    }

    //??????????????????????????????????????????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseEvent(PreViewEvent e) {
        isPreview = 1;
        if (mLivePlayViewHolder != null) {
            ((LivePlayTxViewHolder) mLivePlayViewHolder).setmLiveTypeVal(e.getmLiveTypeVal());
            ((LivePlayTxViewHolder) mLivePlayViewHolder).setmLiveType(e.getmLiveType());
            ((LivePlayTxViewHolder) mLivePlayViewHolder).setBeandata(e.getBean());
        }
    }

    /**
     * ????????????item,??????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DissShopDialogEvent event) {
        if (fragmentShop != null) {
            fragmentShop.dismiss();
        }
        if (!event.isSetVisible()) {
            if (mLiveAudienceViewHolder != null) {
                mLiveAudienceViewHolder.llRoot.setVisibility(View.VISIBLE);
            }
            setChatShow(true);
        } else {
//            if(mLiveAudienceViewHolder!=null){
//                mLiveAudienceViewHolder.llRoot.setVisibility(View.INVISIBLE);
//            }
//            setChatShow(false);
        }

    }

    /**
     *????????????
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FollowAncorEvent event) {
        sendGzSystemMessage(CommonAppConfig.getInstance().getUserBean().getUserNiceName() + WordUtil.getString(R.string.live_follow_anchor), "1");
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param e
     */
    private void doRedirect(ShowMsgEvent e) {
        LiveHttpUtil.doRedirect(mLiveUid, e.getBean().getGoodsUrl(), e.getBean().getGoodsName(),
                e.getBean().getOuthParam(), e.getBean().getGoodsId(), e.getBean().getActiveId(),
                new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {

                        }
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                EventBus.getDefault().post(new LiveCloseDialogEvent());
//            }
//        },100);
        EventBus.getDefault().post(new LiveCloseDialogEvent());
    }

    /**
     * ??????sdk????????????????????????
     *
     * @param linkMic true???????????? false????????????
     */
    public void onLinkMicTxAnchor(boolean linkMic) {
        if (mLivePlayViewHolder != null && mLivePlayViewHolder instanceof LivePlayTxViewHolder) {
            ((LivePlayTxViewHolder) mLivePlayViewHolder).setAnchorLinkMic(linkMic, 5000);
        }
    }

    /**
     * ?????????????????????
     *
     * @param isShow
     */
    public void setChatShow(boolean isShow) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setChatShow(isShow);
        }
    }

    /**
     * ????????????
     */
    public void reConnection() {
        if (mLivePlayViewHolder != null) {
            /*if (mLivePlayViewHolder instanceof LivePlayKsyViewHolder) {
                ((LivePlayKsyViewHolder) mLivePlayViewHolder).reConnection();
            } else {
                ((LivePlayTxViewHolder) mLivePlayViewHolder).reConnection();
            }*/
            ((LivePlayTxViewHolder) mLivePlayViewHolder).reConnection();
        }
        chooseLinePlay();
    }

    /**
     * ???????????????????????????
     */
    public void chooseLinePlay() {
        if (mLiveBean.getIsvideo().equals("1")) {
            return;
        }

        if (cdnSwitch != 7) {//?????????
            updateUrl(mUrl);
            return;
        }


        String s = SpUtil.getInstance().getStringValue(SpUtil.ALL_URL);
        UrlBean bean = JSON.parseObject(s, UrlBean.class);
        int index = (int) (Math.random() * bean.getRhbyPath().getPull().size());//??????????????????

        SpUtil.getInstance().setStringValue(SpUtil.FAST_PULL, bean.getRhbyPath().getPull().get(index).getPull());
        String playUrl = mLiveBean.getIsvideo().equals("1") ? mLiveBean.getPull() : bean.getRhbyPath().getPull().
                get(index).getPull() + mLiveBean.getPull() + "?ip=" + bean.getRhbyPath().getPull().get(index).
                getPull().replace("rtmp://", "");
        pausePlay();
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.play(playUrl);
            ((LivePlayTxViewHolder) mLivePlayViewHolder).setFirst(true);
        }
        L.e("WOLF", "?????????????????????" + playUrl);
    }


    /**
     * ?????????????????????????????????????????????
     *
     * @param pull_url ?????????????????????
     */
    public void updateUrl(String pull_url) {
        if (mLiveBean.getIsvideo().equals("1")) {
            mLivePlayViewHolder.play(mLiveBean.getPull());
            ((LivePlayTxViewHolder) mLivePlayViewHolder).setFirst(true);
            return;
        }
        pausePlay();
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.play(pull_url);
            ((LivePlayTxViewHolder) mLivePlayViewHolder).setFirst(true);
        }
    }

    /**
     * ????????????
     *
     * @param data
     */
    public void showResult(GoodsResultBean.SscHistoryListBean data, LiveReadyBean.GoodsBean goods) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.showResult(Long.parseLong(CommonAppConfig.getInstance().getConfig().getGoods_show_time()), data, goods);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation != Configuration.ORIENTATION_LANDSCAPE && mLiveAudienceViewHolder.isClickChat()) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if(mLiveRoomViewHolder!=null){
//                        mLiveRoomViewHolder.resetTop();
//                        mLiveRoomViewHolder.refreshAdapter();
//                    }
//                }
//            },2000);
        }
    }

    /**
     * ??????????????????????????????
     */
    @Override
    public void onSendPrivateLive(int type, String user) {
        if (mLiveAudienceViewHolder != null) {
            mLiveAudienceViewHolder.onSendPrivateLive(type, user);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param cdn_switch
     */
    @Override
    public void onSendSwitchLive(final int cdn_switch) {
        LiveHttpUtil.resetLiveUrl(2, mStream,
                new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (!TextUtils.isEmpty(info[0])) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                mUrl = obj.getString("url");
                                cdnSwitch = cdn_switch;
//                                if(cdn_switch!=7){//???????????????
////                                    ToastUtil.show("????????????????????????");
//                                    updateUrl(obj.getString("url"));
//                                }else {
////                                    ToastUtil.show("?????????????????????");
//                                    mLiveBean.setPull(obj.getString("url"));
//                                    chooseLinePlay();
//                                }
                                if (mLivePlayViewHolder != null) {
                                    ((LivePlayTxViewHolder) mLivePlayViewHolder).openLoadingWindow();
                                }
                            }
                        }
                    }
                });
    }


    /**
     * ?????????????????????????????????????????????
     *
     * @param totalcoin
     */
    public void setSendTotalcoin(String totalcoin) {
        String key = CommonAppConfig.getInstance().getUid() + mStream;
        float value = SPUtils.getInstance().getFloat(key) + Float.parseFloat(totalcoin);
        SPUtils.getInstance().put(key, value);
    }


    /**
     * ???????????????????????????????????????
     */

    public void setRoomViewVisible(int visible) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setTopVisible(visible);
        }
    }

    /**
     * ?????????????????????
     *
     * @param s
     */
    @Override
    public void onSuperCloseLive(String s) {
        super.onSuperCloseLive(s);
        liveEnd();
    }

    /**
     * ????????????????????????
     *
     * @param type
     * @param typeVal
     */
    @Override
    public void onChangeTimeCharge(int type, int typeVal) {
        super.onChangeTimeCharge(type, typeVal);
        String s = "????????????";
        switch (type) {
            case Constants.LIVE_TYPE_NORMAL:
                s = "????????????";
                break;
            case Constants.LIVE_TYPE_PWD:
                s = "????????????";
                break;
            case Constants.LIVE_TYPE_PAY:
                s = "????????????";
                break;
            case Constants.LIVE_TYPE_TIME:
                s = "????????????";
                break;
        }
        mLivePlayViewHolder.stopPlay();
        mLivePlayViewHolder.stopPlay2();
        //??????socket
        stopSocket();
        DialogUitl.showSimpleTipCallDialog(mContext, "????????????", "?????????????????????????????????" + s + "??????????????????????????????????????????", new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                onBackPressed();
            }
        });
    }

    /**
     * ???????????????????????????????????????
     */
    @Override
    public void onSendUserList(String user,String nums) {
        super.onSendUserList(user,nums);
        if(TextUtils.isEmpty(user)){
            return;
        }
        if(mLiveRoomViewHolder!=null){
            mLiveRoomViewHolder.refreshUserList(user,nums);
        }
    }

    public void stopSocket() {
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        mSocketClient = null;
    }

}

