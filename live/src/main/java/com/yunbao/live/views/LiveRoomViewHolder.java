package com.yunbao.live.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.opensource.svgaplayer.SVGAImageView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.DissmissDialogListener;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.AppUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveActivity;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.live.adapter.LiveBuyAdapter;
import com.yunbao.live.adapter.LiveChatAdapter;
import com.yunbao.live.adapter.LiveUserAdapter;
import com.yunbao.live.bean.GoodsResultBean;
import com.yunbao.live.bean.LiveBuyGuardMsgBean;
import com.yunbao.live.bean.LiveChatBean;
import com.yunbao.live.bean.LiveContactBean;
import com.yunbao.live.bean.LiveDanMuBean;
import com.yunbao.live.bean.LiveEnterRoomBean;
import com.yunbao.live.bean.LiveGiftPrizePoolWinBean;
import com.yunbao.live.bean.LiveLuckGiftWinBean;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.live.bean.LiveReceiveGiftBean;
import com.yunbao.live.bean.LiveUserChatBean;
import com.yunbao.live.bean.LiveUserGiftBean;
import com.yunbao.live.custom.ChildFadePresenter;
import com.yunbao.live.custom.ChildPresenter;
import com.yunbao.live.custom.LiveLightView;
import com.yunbao.live.dialog.LiveContactDialog;
import com.yunbao.live.dialog.LiveNewUserDialogFragment;
import com.yunbao.live.event.LivePrivateEvent;
import com.yunbao.live.event.LiveShowBottomEvent;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.presenter.LiveDanmuPresenter;
import com.yunbao.live.presenter.LiveEnterRoomAnimPresenter;
import com.yunbao.live.presenter.LiveGiftAnimPresenter;
import com.yunbao.live.presenter.LiveLightAnimPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2018/10/9.
 * ?????????????????????
 */

public class LiveRoomViewHolder extends AbsViewHolder implements View.OnClickListener {

    private int mOffsetY;
    private ViewGroup mRoot;
    private ImageView mAvatar;
    private ImageView mLevelAnchor;
    private TextView mName;
    private TextView mID;
    private View mBtnFollow;
    private TextView mVotesName;//????????????
    private TextView mVotes;
    private TextView mGuardNum;//????????????
    private ChildPresenter mUserRecyclerView;
    private ChildFadePresenter mChatRecyclerView;
    private LiveUserAdapter mLiveUserAdapter;
    private LiveChatAdapter mLiveChatAdapter;
    private View mBtnRedPack;
    private String mLiveUid;
    private String mStream;
    private LiveLightAnimPresenter mLightAnimPresenter;
    private LiveEnterRoomAnimPresenter mLiveEnterRoomAnimPresenter;
    private LiveDanmuPresenter mLiveDanmuPresenter;
    private LiveGiftAnimPresenter mLiveGiftAnimPresenter;
    private LiveRoomHandler mLiveRoomHandler;
    private HttpCallback mRefreshUserListCallback;
    private HttpCallback mTimeChargeCallback;
    protected int mUserListInterval;//?????????????????????????????????
    private GifImageView mGifImageView;
    private SVGAImageView mSVGAImageView;
    private ViewGroup mLiveGiftPrizePoolContainer;
    private TextView mLiveTimeTextView;//?????????????????????
    private long mAnchorLiveTime;//??????????????????
    private View mBtnPrizePool;//????????????
    private TextView mPrizePoolLevel;//????????????
    private ImageView mIvBack;
    private TextView mTvCode;
    private TextView mTvNumber;
    private RecyclerView mRecyclerView;
    private LiveBuyAdapter mBuyAdapter;
    private FrameLayout flBuy;
    private LinearLayout linearlayout;

    /**
     * ??????????????????
     */
    private int tranxtime=2000;
    private int alphatime=1500;
    private int showtime =15000;
    private ObjectAnimator mBgAnimator1;
    private ObjectAnimator mBgAnimator2;
    private  Handler handler=new Handler();
    private int recycleViewH,flBuyW;
    private TextView tvTitle;
    private LinearLayout llTop;
    private String mOrUid;
    private LinearLayout group1;
    private ImageView ivStar;
    private FrameLayout flStar;
    private TextView tvStar;
    private FrameLayout flReplay;
    private TextView tvPlay;
    private TextView tvReplayChat;
    private TextView tvSimiState;

    private LiveUserChatBean mLiveUserChatData;
    private Paint mPaint;
    private int layerId;
    private LinearGradient linearGradient;
    private int number;//???????????????
    private String isVideo;
    private String live_tag;

    private LinearLayout llcomein;
    private ScrrollCHatTextView mScTv;
    private List<LiveChatBean> datas;    //??????????????????????????????
    private boolean  isShowFade = false;  //??????????????????????????????
    private RelativeLayout rlContent;
    private View vv;
    private boolean firstStart=true;
    private long startTime;

    public LiveRoomViewHolder(Context context, ViewGroup parentView, GifImageView gifImageView, SVGAImageView svgaImageView, ViewGroup liveGiftPrizePoolContainer) {
        super(context, parentView);
        mGifImageView = gifImageView;
        mSVGAImageView = svgaImageView;
        mLiveGiftPrizePoolContainer = liveGiftPrizePoolContainer;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_room;
    }

    @Override
    public void init() {
        EventBus.getDefault().register(this);
        mRoot = (ViewGroup) findViewById(R.id.root);
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mLevelAnchor = (ImageView) findViewById(R.id.level_anchor);
        mName = (TextView) findViewById(R.id.name);
        mID = (TextView) findViewById(R.id.id_val);
        mBtnFollow = findViewById(R.id.btn_follow);
//        mVotesName = (TextView) findViewById(R.id.votes_name);
        mVotes = (TextView) findViewById(R.id.votes);
        mGuardNum = (TextView) findViewById(R.id.guard_num);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mTvCode = (TextView) findViewById(R.id.tv_code);
        mTvNumber = (TextView) findViewById(R.id.tv_number);
        mRecyclerView = (ChildPresenter) findViewById(R.id.recyclerView);
        recycleViewH = mRecyclerView.getLayoutParams().height;
        flBuy = (FrameLayout) findViewById(R.id.fl_buy);
        flBuyW = flBuy.getLayoutParams().width;
        tvTitle = (TextView) findViewById(R.id.tv_title);
        linearlayout= (LinearLayout) findViewById(R.id.linearlayout);
        llTop= (LinearLayout) findViewById(R.id.ll_top);
        group1= (LinearLayout) findViewById(R.id.group_1);
        ivStar= (ImageView) findViewById(R.id.iv_star);
        flStar= (FrameLayout) findViewById(R.id.fl_star);
        tvStar= (TextView) findViewById(R.id.tv_star);
        flReplay= (FrameLayout) findViewById(R.id.fl_replay);
        tvPlay= (TextView) findViewById(R.id.tv_play);
        tvReplayChat= (TextView) findViewById(R.id.tv_replay_chat);
        llcomein= (LinearLayout) findViewById(R.id.ll_comein);
        mScTv= (ScrrollCHatTextView) findViewById(R.id.sctv);
        rlContent= (RelativeLayout) findViewById(R.id.rl_content);
        vv= (View) findViewById(R.id.vv);

        tvSimiState= (TextView) findViewById(R.id.tv_simi_state);
        //??????????????????
        mUserRecyclerView = (ChildPresenter) findViewById(R.id.user_recyclerView);
        mUserRecyclerView.setHasFixedSize(true);
        mUserRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mLiveUserAdapter = new LiveUserAdapter(mContext);
        mLiveUserAdapter.setOnItemClickListener(new OnItemClickListener<UserBean>() {
            @Override
            public void onItemClick(UserBean bean, int position) {
                showUserDialog(bean.getId());
            }
        });
        mUserRecyclerView.setAdapter(mLiveUserAdapter);

        //????????????
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(mContext,10, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mBuyAdapter = new LiveBuyAdapter();
        mRecyclerView.setAdapter(mBuyAdapter);
        //?????????
        mChatRecyclerView = (ChildFadePresenter) findViewById(R.id.chat_recyclerView);
        mChatRecyclerView.setHasFixedSize(true);
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
//        mChatRecyclerView.addItemDecoration(new TopGradual());
        mLiveChatAdapter = new LiveChatAdapter(mContext);
        mLiveChatAdapter.setOnItemClickListener(new OnItemClickListener<LiveChatBean>() {
            @Override
            public void onItemClick(LiveChatBean bean, int position) {
                L.e("WOLF","setOnItemClickListener:"+bean.getIs_king());
                if (position == 0) {
                    showUserDialog(bean.getId());
                } else if (position == 1) {
                    if(TextUtils.isEmpty(bean.getJump_url_android()) || TextUtils.isEmpty(bean.getJump_type_android())){
                        return;
                    }
                    if (bean != null ) {
//                        final OpenUrlUtils oUtils = OpenUrlUtils.getInstance()
//                                .setContext(mContext)
//                                .setLoadTransparent(true)
//                                .setCannotCancel(false)
//                                .setJump_type(bean.getJump_type())
//                                .setIs_king(bean.getIs_king())
//                                .setListener(new DialogInterface.OnDismissListener() {
//                                    @Override
//                                    public void onDismiss(DialogInterface dialogInterface) {
//                                        EventBus.getDefault().post(new LiveShowBottomEvent(true));
//                                    }
//                                }).setOndissDialogListener(new DissmissDialogListener() {
//                                    @Override
//                                    public void onDissmissListener() {
//                                        EventBus.getDefault().post(new LiveShowBottomEvent(true));
//                                    }
//                                })
//                                .setType(Integer.parseInt(bean.getSlide_show_type()));
//
//                        if(bean.getJump_type().equals("1")){
//                            oUtils.go(bean.getUrl());
//                        }else {
//                            oUtils.go(bean.getUrl());
//                        }
//                        L.e("WOLF",bean.getUrl());

                        final OpenUrlUtils oUtils = OpenUrlUtils.getInstance()
                                .setContext(mContext)
                                .setLoadTransparent(true)
                                .setCannotCancel(false)
                                .setJump_type(bean.getJump_type_android())
                                .setIs_king(bean.getIs_king())
                                .setSlide_show_type_button(bean.getSlide_show_type_button())
                                .setListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        EventBus.getDefault().post(new LiveShowBottomEvent(true));
                                    }
                                }).setOndissDialogListener(new DissmissDialogListener() {
                                    @Override
                                    public void onDissmissListener() {
                                        EventBus.getDefault().post(new LiveShowBottomEvent(true));
                                    }
                                })
                                .setType(Integer.parseInt(bean.getSlide_show_type()));

                        if(bean.getJump_type_android().equals("1")){
                            oUtils.go(bean.getJump_url_android());
                        }else {
                            oUtils.go(bean.getJump_url_android());
                        }
                        L.e("WOLF",bean.getJump_url_android());
                    }

                    EventBus.getDefault().post(new LiveShowBottomEvent(false));
                }
            }
        });
        mChatRecyclerView.setAdapter(mLiveChatAdapter);
        initChatRv(mChatRecyclerView);

//        mVotesName.setText(CommonAppConfig.getInstance().getVotesName());
        mBtnFollow.setOnClickListener(this);
        mAvatar.setOnClickListener(this);
        group1.setOnClickListener(this);
        findViewById(R.id.btn_votes).setOnClickListener(this);
        findViewById(R.id.btn_guard).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_copy).setOnClickListener(this);
        findViewById(R.id.iv_buy_close).setOnClickListener(this);
        findViewById(R.id.tv_replay_chat).setOnClickListener(this);
        mBtnPrizePool = findViewById(R.id.btn_prize_pool_level);
        mPrizePoolLevel = (TextView) findViewById(R.id.prize_pool_level);
        mBtnPrizePool.setOnClickListener(this);
        mBtnRedPack = findViewById(R.id.btn_red_pack);
        mBtnRedPack.setOnClickListener(this);
        if (mContext instanceof LiveAudienceActivity) {
            mRoot.setOnClickListener(this);
        } else {
            mLiveTimeTextView = (TextView) findViewById(R.id.live_time);
            mLiveTimeTextView.setVisibility(View.VISIBLE);
        }
        mLightAnimPresenter = new LiveLightAnimPresenter(mContext, mParentView);
        mLiveEnterRoomAnimPresenter = new LiveEnterRoomAnimPresenter(mContext, mContentView);
        mLiveRoomHandler = new LiveRoomHandler(this);
        mRefreshUserListCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mLiveUserAdapter != null) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        L.e("WOLF", "????????????????????????info[0]???" + info[0]);
                        List<LiveUserGiftBean> list = JSON.parseArray(obj.getString("userlist"), LiveUserGiftBean.class);
                        String nums = obj.getString("nums");
                        mLiveUserAdapter.refreshList(list);
                        if(mContext instanceof LiveAnchorActivity){
                            if(list==null || list.size()==0 ){
                                return;
                            }
                            ((LiveAnchorActivity) mContext).sendUserList(JSON.toJSONString(list),obj.getString("nums"));
                        }
                        if(mTvNumber!=null && !TextUtils.isEmpty(nums)){
                            mTvNumber.setText(nums);
                        }
                    }
                }
            }
        };

        mTimeChargeCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (mContext instanceof LiveAudienceActivity) {
                    ((LiveAudienceActivity)mContext).dismissDialog();
                    final LiveAudienceActivity liveAudienceActivity = (LiveAudienceActivity) mContext;
                    if (code == 0) {
                        liveAudienceActivity.roomChargeUpdateVotes();
                        //????????????????????????
                        if(((LiveAudienceActivity)mContext).getIsPreview()==3){
                            return;
                        }
                        ((LiveAudienceActivity)mContext).setIsPreview(3);
                        ((LiveAudienceActivity)mContext). enterTimeRoom();
                    } else {
                        if (mLiveRoomHandler != null) {
                            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_TIME_CHARGE);
                        }
                        if (code == 1008 || code ==-1) {//????????????
                            liveAudienceActivity.setCoinNotEnough(true);
                            DialogUitl.showSimpleTipCallDialog(mContext, "????????????",  WordUtil.getString(R.string.live_coin_not_enough),  new DialogUitl.SimpleCallback() {
                                @Override
                                public void onConfirmClick(Dialog dialog, String content) {
                                    liveAudienceActivity.exitLiveRoom();
                                }
                            });
                        }
                    }
                }
            }
        };

        if (mContext instanceof LiveAnchorActivity) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvSimiState.getLayoutParams();
            lp.topMargin = SizeUtils.dp2px(32);
            tvSimiState.setLayoutParams(lp);
        }
        initScreen();
    }

    /**
     * ???????????????
     */
    private void initScreen() {
        if(AppUtil.CutOutSafeHeight>0){
            L.e("WOLF","CutOutSafeHeight:"+SizeUtils.px2dp(AppUtil.CutOutSafeHeight));
            RelativeLayout.LayoutParams linearParams =(RelativeLayout.LayoutParams) vv.getLayoutParams();
            if(SizeUtils.px2dp(AppUtil.CutOutSafeHeight)>24){
                linearParams.height = SizeUtils.dp2px(SizeUtils.px2dp(AppUtil.CutOutSafeHeight)-24);
            }else {
                linearParams.height = SizeUtils.dp2px(0);
            }
            vv.setLayoutParams(linearParams);
        }
    }

    /**
     * ???????????????????????????
     * @param mChatRecyclerView
     *
     *  ??????socket--->"{\"retcode\":\"000000\",\"retmsg\":\"ok\",\"msg\":[{\"_method_\":\"userList\",\"ct\":\"{\\\"nums\\\":\\\"2\\\",\\\"userlist\\\":[{\\\"avatar\\\":\\\"http://132.232.122.151:8888/group1/app/image/2020/09/11/10d310056dcad3382bf144e4d7b36b88.jpg\\\",\\\"contribution\\\":0.1,\\\"guard_type\\\":\\\"0\\\",\\\"id\\\":\\\"30024\\\",\\\"level\\\":\\\"1\\\"},{\\\"avatar\\\":\\\"http://47.75.111.156/public/appapi/images/user_default.png\\\",\\\"contribution\\\":0.1,\\\"guard_type\\\":
     *  \\\"0\\\",\\\"id\\\":\\\"29523\\\",\\\"level\\\":\\\"1\\\"}],\\\"votestotal\\\":\\\"0.05\\\"}\"}]}"
     */
    private void initChatRv(final ChildFadePresenter mChatRecyclerView) {
        RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(mChatRecyclerView==null || isShowFade ){
                    return;
                }
                if(!isRecyclerScrollable()) {
                    mChatRecyclerView.setFadingEdgeLength(IMDensityUtil.dip2px(mContext,0));
                }else {
                    isShowFade =true;
                    mChatRecyclerView.setFadingEdgeLength(IMDensityUtil.dip2px(mContext,50));
                }
            }
        };

        mChatRecyclerView.addOnScrollListener(mScrollListener);
    }
    /**
     * ???????????????????????????
     */
    public boolean isRecyclerScrollable() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mChatRecyclerView.getLayoutManager();
        RecyclerView.Adapter adapter = mChatRecyclerView.getAdapter();
        if (layoutManager == null || adapter == null) return false;
        return layoutManager.findFirstVisibleItemPosition()>0;
    }

    /**
     * ?????????????????????
     */
    public void setChatShow(boolean isShow) {
        if (mChatRecyclerView != null) {
            mChatRecyclerView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * ??????????????????
     */
    public void setAvatar(String url) {
        if (mAvatar != null) {
            ImgLoader.displayWithPlaceError(mContext, url, mAvatar,R.mipmap.img_head_deafult,R.mipmap.img_head_deafult);

        }
    }

    /**
     * ??????????????????
     */
    public void setAnchorLevel(int anchorLevel) {
        if (mLevelAnchor != null) {
            LevelBean levelBean = CommonAppConfig.getInstance().getAnchorLevel(anchorLevel);
            if (levelBean != null) {
                ImgLoader.display(mContext, levelBean.getThumbIcon(), mLevelAnchor);
            }
        }
    }

    /**
     * ???????????????
     */
    public void setName(String name) {
        if (mName != null) {
            mName.setText(name);
        }
    }

    /**
     * ???????????????
     */
    public void setRoomNum(String roomNum) {
        if (mID != null) {
            mID.setText(roomNum);
        }
    }

    /**
     * ????????????
     * @param live_tag
     */
    public void setReplayName(String isVideo,String live_tag) {
        this.isVideo=isVideo;
        this.live_tag=live_tag;
        if (tvStar != null) {
            tvStar.setText("<"+live_tag+"> ?????????????????????????????????!");
        }
        if (tvPlay != null) {
            tvPlay.setText(TextUtils.isEmpty(live_tag)?"?????????":live_tag);
        }

        if(isVideo.equals("1")){
            flReplay.setVisibility(View.VISIBLE);
            tvReplayChat.setVisibility(View.VISIBLE);
            mUserRecyclerView.setVisibility(View.GONE);
        }else {
            tvReplayChat.setVisibility(View.GONE);
            mUserRecyclerView.setVisibility(View.VISIBLE);
            if(isVideo.equals("2")&&TextUtils.isEmpty(live_tag)){
                flReplay.setVisibility(View.GONE);
            }else {
                flReplay.setVisibility(View.VISIBLE);
            }
        }

    }

    /**
     * ??????????????????
     */
    public void setAttention(int attention) {
        if (mBtnFollow != null) {
            if (attention == 0) {
                if (mBtnFollow.getVisibility() != View.VISIBLE) {
                    mBtnFollow.setVisibility(View.VISIBLE);
                }

                if(!(TextUtils.isEmpty(isVideo))&&isVideo.equals("1")){
                    group1.post(new Runnable() {
                        @Override
                        public void run() {
                            int width = group1.getWidth();
                            L.e("WOLF","?????????"+SizeUtils.px2dp(width));
                            setMargins(flStar,SizeUtils.dp2px(SizeUtils.px2dp(width)>130?
                                            SizeUtils.px2dp(width)-50:SizeUtils.px2dp(width)-30),
                                    SizeUtils.dp2px(1),0,0);
                            flStar.setVisibility(View.VISIBLE);
                        }
                    });
                }else {
                    flStar.setVisibility(View.GONE);
                }
            } else {
                if (mBtnFollow.getVisibility() == View.VISIBLE) {
                    mBtnFollow.setVisibility(View.GONE);
                }
                flStar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * ?????????????????????????????????
     */
    public void setUserList(List<LiveUserGiftBean> list) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.refreshList(list);
//            mTvNumber.setText(list.size() + "");
        }
    }

    /**
     * ?????????????????????
     */
    public void setVotes(String votes) {
        if (mVotes != null) {
            mVotes.setText(votes);
        }
    }

    /**
     * ??????????????????
     */
    public void setNumber(String number) {
        this.number=Integer.parseInt(number);
        if (mTvNumber != null) {
            mTvNumber.setText(number);
        }
    }

    /**
     * ????????????????????????
     */
    public void setGuardNum(int guardNum) {
        if (mGuardNum != null) {
            if (guardNum > 0) {
                mGuardNum.setText(guardNum + WordUtil.getString(R.string.ren));
            } else {
                mGuardNum.setText(R.string.main_list_no_data);
            }
        }
    }

    public void setLiveInfo(String liveUid, String stream, String or_uid,int userListInterval) {
        mLiveUid = liveUid;
        mStream = stream;
        mOrUid = or_uid;
        mUserListInterval = userListInterval;
    }


    /**
     * ????????????????????????
     */
    public void onGuardInfoChanged(LiveBuyGuardMsgBean bean) {
        setGuardNum(bean.getGuardNum());
        setVotes(bean.getVotes());
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.onGuardChanged(bean.getUid(), bean.getGuardType());
        }
    }

    /**
     * ??????????????????????????????
     */
    public void setRedPackBtnVisible(boolean visible) {
        if (mBtnRedPack != null) {
            if (visible) {
                if (mBtnRedPack.getVisibility() != View.VISIBLE) {
                    mBtnRedPack.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnRedPack.getVisibility() == View.VISIBLE) {
                    mBtnRedPack.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public int a=1;
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.root) {
            clickTwoLight();
        }
        if (!canClick()) {
            return;
        }
        if (i == R.id.avatar) {
            showAnchorUserDialog();
        } else if (i == R.id.group_1 ) {
            showAnchorUserDialog();
        }else if (i == R.id.btn_follow) {
            follow();

        } else if (i == R.id.btn_votes) {
            openContributeWindow();

        } else if (i == R.id.btn_guard) {
            ((LiveActivity) mContext).openGuardListWindow();

        } else if (i == R.id.btn_red_pack) {
            ((LiveActivity) mContext).openRedPackListWindow();

        } else if (i == R.id.btn_prize_pool_level) {
            ((LiveActivity) mContext).openPrizePoolWindow();
        } else if (i == R.id.iv_back) {
            ((LiveActivity) mContext).onBackPressed();
        } else if (i == R.id.tv_copy) {
            if (!TextUtils.isEmpty(mTvCode.getText().toString())) {
                copy(mTvCode.getText().toString());
                ToastUtil.show("????????????");
            }
        } else if (i == R.id.iv_buy_close) {
            flBuy.setTranslationX(DpUtil.dp2px(500));
            flBuy.setAlpha(1);
        }else if (i == R.id.tv_replay_chat) {
            if(mLiveUserChatData==null){
                return;
            }

            List<LiveContactBean> list=new ArrayList<>();
            if(!TextUtils.isEmpty(mLiveUserChatData.getWecaht())){
                list.add(new LiveContactBean(Constants.WX,mLiveUserChatData.getWecaht()));
            }
            if(!TextUtils.isEmpty(mLiveUserChatData.getQq())){
                list.add(new LiveContactBean(Constants.QQ,mLiveUserChatData.getQq()));
            }
            if(!TextUtils.isEmpty(mLiveUserChatData.getTeliao())){
                list.add(new LiveContactBean(Constants.TL,mLiveUserChatData.getTeliao()));
            }
            if(!TextUtils.isEmpty(mLiveUserChatData.getTelegram())){
                list.add(new LiveContactBean(Constants.TG,mLiveUserChatData.getTelegram()));
            }
            if(!TextUtils.isEmpty(mLiveUserChatData.getFacebook())){
                list.add(new LiveContactBean(Constants.FB,mLiveUserChatData.getFacebook()));
            }
            openContactWindow(list);
        }
    }

    /**
     * ????????????????????????
     * @param list
     */
    public void openContactWindow(List<LiveContactBean> list) {
        LiveContactDialog dialog = new LiveContactDialog(list);
        dialog.show(((LiveActivity)mContext).getSupportFragmentManager(), "LiveContactDialog");
    }

    /**
     * ??????????????????light();
     */
    private  long lastClickTime;
    private void clickTwoLight() {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= 1000) {
            lastClickTime = curClickTime;
            return;
        }
        lastClickTime = curClickTime;
        light();
    }

    /**
     * ????????????
     * @param time
     * @param data
     */
    public void showResult(long time, GoodsResultBean.SscHistoryListBean data, LiveReadyBean.GoodsBean goodsBean){
        if(data==null||goodsBean==null){
            return;
        }
        tvTitle.setText(goodsBean.getName()+"  "+data.getNumber()+CommonAppConfig.getInstance().getConfig().getGoods_show_title());
        startBuyResultAnim( time==-1?showtime:time*1000,data);
    }

    /**
     * ????????????
     */
    public void dismissResult(){
        cancelAnim();
        flBuy.setTranslationX(DpUtil.dp2px(500));
        flBuy.setAlpha(1);
    }

    private void startBuyResultAnim(final long time,GoodsResultBean.SscHistoryListBean data) {
        List<String> list = new ArrayList<>();
        for(String s:data.getOpenCode().split(",")){
            list.add(s);
        }
        ViewGroup.LayoutParams layoutParams = mRecyclerView.getLayoutParams();
        ViewGroup.LayoutParams layoutParams1 = flBuy.getLayoutParams();


        if(list.size()<=10){
            if(list.size()<=7){
                //???????????????8???item??????item??????
                int width = (int) (flBuyW * 8.0 / 10);
                TextPaint textPaint = tvTitle.getPaint();
                int width0 = (int) textPaint.measureText(tvTitle.getText().toString())+ IMDensityUtil.dip2px(mContext,40);
                layoutParams1.width= Math.max(width, width0);
                Log.e("------","width="+width+"---width0="+width0);
                flBuy.setLayoutParams(layoutParams1);
            }
            layoutParams.height=(int)(recycleViewH*1.0/2);
            mRecyclerView.setLayoutParams(layoutParams);
        }
        mBuyAdapter.setNewData(list);



        Interpolator interpolator1 = new AccelerateDecelerateInterpolator();
        Interpolator interpolator2 = new AccelerateInterpolator();

        mBgAnimator1 = ObjectAnimator.ofFloat(flBuy, "translationX", 5);
        mBgAnimator1.setInterpolator(interpolator1);
        mBgAnimator1.setDuration(tranxtime);

        mBgAnimator2 = ObjectAnimator.ofFloat(flBuy, "alpha", 1, 0);
        mBgAnimator2.setInterpolator(interpolator2);
        mBgAnimator2.setDuration(alphatime);
        mBgAnimator1.start();
        mBgAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mBgAnimator2==null){
                            return;
                        }
                        mBgAnimator2.start();
                    }
                },time);
            }
        });
        mBgAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                flBuy.setTranslationX(DpUtil.dp2px(500));
                flBuy.setAlpha(1);
            }
        });

    }

    /**
     * ???????????????
     *
     * @param isShow
     */
    public void setBackShow(boolean isShow) {
        mIvBack.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * ??????
     *
     * @param content
     */
    private void copy(String content) {
        //???????????????????????????
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        // ?????????????????????ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // ???ClipData?????????????????????????????????
        cm.setPrimaryClip(mClipData);
    }

    /**
     * ????????????
     */
    private void follow() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        CommonHttpUtil.setAttentionMJ(CommonHttpConsts.SET_ATTENTION,mLiveUid,mOrUid,new CommonCallback<Integer>() {
            @Override
            public void callback(Integer isAttention) {
                if (isAttention == 1) {
                    ((LiveActivity) mContext).sendGzSystemMessage(
                            CommonAppConfig.getInstance().getUserBean().getUserNiceName() + WordUtil.getString(R.string.live_follow_anchor),"1");
                }
            }
        });
    }

    /**
     * ????????????????????????????????????????????????
     */
    public void insertUser(LiveUserGiftBean bean) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.insertItem(bean);
//            mTvNumber.setText(mLiveUserAdapter.getItemCount() + "");
            number=number+1;
            mTvNumber.setText(number+"");
        }
    }

    /**
     * ????????????????????????????????????
     */
    public void insertUser(List<LiveUserGiftBean> list) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.insertList(list);
//            mTvNumber.setText(mLiveUserAdapter.getItemCount() + "");
            number=number+list.size();
            mTvNumber.setText(number+ "");
        }
    }

    /**
     * ????????????????????????????????????????????????
     */
    public void removeUser(String uid) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.removeItem(uid);
//            mTvNumber.setText(mLiveUserAdapter.getItemCount() + "");
            number=number-1;
            mTvNumber.setText(number+ "");
        }
    }

    /**
     * ??????????????????
     */
    private void refreshUserList() {
        if (!TextUtils.isEmpty(mLiveUid) && mRefreshUserListCallback != null && mLiveUserAdapter != null) {
            LiveHttpUtil.cancel(LiveHttpConsts.GET_USER_LIST);
            LiveHttpUtil.getUserList(mLiveUid, mStream, mRefreshUserListCallback);
            if(mContext  instanceof  LiveAnchorActivity){
                startRefreshUserList();
            }
        }
    }

    /**
     * ????????????????????????
     */
    public void startRefreshUserList() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_REFRESH_USER_LIST, mUserListInterval > 0 ? mUserListInterval : 60000);
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void requestTimeCharge() {
        if (!TextUtils.isEmpty(mLiveUid) && mTimeChargeCallback != null) {
            LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
            if (mContext instanceof LiveAudienceActivity) {
                ((LiveAudienceActivity) mContext).clearRoomData(true);
                ((LiveAudienceActivity) mContext).showDialog();
            }
            LiveHttpUtil.timeCharge(mLiveUid, mStream, mTimeChargeCallback);
            startRequestTimeCharge();
        }
    }

    /**
     * ???????????????????????????????????????
     */
    public void startRequestTimeCharge() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_TIME_CHARGE, 60000);
        }
    }

    /**
     * ??????????????????????????????
     */
    public void insertChat(LiveChatBean bean) {
        //effect ???1?????????????????????????????????
        if(!TextUtils.isEmpty(bean.getEffect()) && bean.getEffect().equals("1")){
            if (mLiveEnterRoomAnimPresenter != null) {
                mLiveEnterRoomAnimPresenter.ShowMoneyAnim(bean);
            }
            return;
        }
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.insertItem(bean);
        }
    }
    /**
     * ???????????????????????????????????????
     */
    public void insertEnterRoomChat(LiveChatBean bean) {
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.insertEnterRoomItem(bean);
        }
    }

    /**
     * ??????????????????
     */
    public void playLightAnim() {
        if (mLightAnimPresenter != null) {
            mLightAnimPresenter.play();
        }
    }

    /**
     * ??????
     */
    private void light() {
        ((LiveAudienceActivity) mContext).light();
    }

    public void setOffsetY(int offsetY) {
        LiveLightView.sOffsetY = offsetY;
        mOffsetY = offsetY;
    }


    /**
     * ??????????????????
     */
    public void onKeyBoardChanged(int visibleHeight, int keyBoardHeight) {
        if (mRoot != null) {
            if (keyBoardHeight == 0) {
                mRoot.setTranslationY(0);
                return;
            }
            if (mOffsetY == 0) {
                mRoot.setTranslationY(-keyBoardHeight);
                return;
            }
            if (mOffsetY > 0 && mOffsetY < keyBoardHeight) {
                mRoot.setTranslationY(mOffsetY - keyBoardHeight);
            }
        }
    }

    /**
     * ????????????????????????
     */
    public void chatScrollToBottom() {
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.scrollToBottom();
        }
    }

    /**
     * ??????
     */
    public void refreshAdapter() {
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.notifyDataSetChanged();
        }
    }

    /**
     * ?????????????????? ????????????,????????????
     */
    public void onEnterRoom(LiveEnterRoomBean bean) {
        if (bean == null) {
            return;
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.enterRoom(bean);
        }
    }


    /**
     *????????????
     */
    public void onFollowAnchor(LiveChatBean bean) {
        if (bean == null) {
            return;
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.FollowAnchor(bean);
        }
    }
    /**
     * ????????????
     */
    public void showDanmu(LiveDanMuBean bean) {
        if (mVotes != null) {
            mVotes.setText(bean.getVotes());
        }
        if (mLiveDanmuPresenter == null) {
            mLiveDanmuPresenter = new LiveDanmuPresenter(mContext, mParentView);
        }
        mLiveDanmuPresenter.showDanmu(bean);
    }

    /**
     * ?????????????????????????????????
     */
    private void showAnchorUserDialog() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        if(!TextUtils.isEmpty(mOrUid)&&!mOrUid.equals("0")){
            showUserDialog(mOrUid);
        }else {
            showUserDialog(mLiveUid);
        }
    }

    /**
     * ????????????????????????
     */
    private void showUserDialog(String toUid) {
        if (!TextUtils.isEmpty(mLiveUid) && !TextUtils.isEmpty(toUid)) {
            LiveNewUserDialogFragment fragment = new LiveNewUserDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.LIVE_UID, mLiveUid);
            bundle.putString(Constants.STREAM, mStream);
            bundle.putString(Constants.TO_UID, toUid);
            bundle.putString(Constants.LIVE_OR_UID, mOrUid);
            fragment.setArguments(bundle);
            fragment.show(((LiveActivity) mContext).getSupportFragmentManager(), "LiveUserDialogFragment");
        }
    }

    /**
     * ????????????????????????
     */
    private void openContributeWindow() {
        ((LiveActivity) mContext).openContributeWindow();
    }


    /**
     * ??????????????????
     */
    public void showGiftMessage(LiveReceiveGiftBean bean) {
        mVotes.setText(bean.getVotes());
        if (mLiveGiftAnimPresenter == null) {
            mLiveGiftAnimPresenter = new LiveGiftAnimPresenter(mContext, mContentView, mGifImageView, mSVGAImageView, mLiveGiftPrizePoolContainer);
        }
        mLiveGiftAnimPresenter.showGiftAnim(bean);
    }

    /**
     * ?????????????????????
     *
     * @param deltaVal ?????????????????????
     */
    public void updateVotes(String deltaVal) {
        if (mVotes == null) {
            return;
        }
        String votesVal = mVotes.getText().toString().trim();
        if (TextUtils.isEmpty(votesVal)) {
            return;
        }
        try {
            double votes = Double.parseDouble(votesVal);
            double addVotes = Double.parseDouble(deltaVal);
            votes += addVotes;
            mVotes.setText(StringUtil.format(votes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ViewGroup getInnerContainer() {
        return (ViewGroup) findViewById(R.id.inner_container);
    }


    /**
     * ????????????????????????
     */
    private void showAnchorLiveTime() {
//        if (mLiveTimeTextView != null) {
//            mAnchorLiveTime += 1000;
//            mLiveTimeTextView.setText(StringUtil.getDurationText(mAnchorLiveTime));
//            startAnchorLiveTime();
//        }

        if (mLiveTimeTextView != null) {
            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);
            mLiveRoomHandler.sendEmptyMessageAtTime(LiveRoomHandler.WHAT_ANCHOR_LIVE_TIME, next);
            mLiveTimeTextView.setText(StringUtil.getDurationText(next-startTime));
        }
    }

    /**
     * ????????????????????????2
     */
    public void startAnchorLiveTime2() {
        if(firstStart){
            firstStart=false;
            startTime=SystemClock.uptimeMillis();
        }
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_ANCHOR_LIVE_TIME, 1000);
        }
    }

    public void startAnchorLiveTime() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_ANCHOR_LIVE_TIME, 1000);
        }
    }


    /**
     * ??????????????????
     */
    public void startAnchorLight() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_ANCHOR_LIGHT);
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_ANCHOR_LIGHT, 500);
        }
    }


    /**
     * ??????????????????50??????????????????
     */
    public void anchorPause() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_ANCHOR_PAUSE, 50000);
        }
    }

    /**
     * ?????????????????????????????????
     */
    public void anchorResume() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_ANCHOR_PAUSE);
        }
    }

    /**
     * ??????????????????
     */
    private void anchorEndLive() {
        if (mContext instanceof LiveAnchorActivity) {
            ((LiveAnchorActivity) mContext).endLive();
        }
    }

    /**
     * ????????????
     */
    private void anchorLight() {
        if (mContext instanceof LiveAnchorActivity) {
            ((LiveAnchorActivity) mContext).light();
            startAnchorLight();
        }
    }


    /**
     * ??????????????????
     */
    public void onLuckGiftWin(LiveLuckGiftWinBean bean) {
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.showLuckGiftWinAnim(bean);
        }
    }


    /**
     * ????????????
     */
    public void onPrizePoolWin(LiveGiftPrizePoolWinBean bean) {
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.showPrizePoolWinAnim(bean);
        }
    }

    /**
     * ????????????
     */
    public void onPrizePoolUp(String level) {
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.showPrizePoolUp(level);
        }
    }

    /**
     * ??????????????????
     */
    public void showPrizePoolLevel(String level) {
//        if (mBtnPrizePool != null && mBtnPrizePool.getVisibility() != View.VISIBLE) {
//            mBtnPrizePool.setVisibility(View.VISIBLE);
//        }
//        if (mPrizePoolLevel != null) {
//            mPrizePoolLevel.setText(String.format(WordUtil.getString(R.string.live_gift_prize_pool_3), level));
//        }
    }

    /**
     * ????????????????????????
     * @param live_user
     */
    public void setLiveUserChat(LiveUserChatBean live_user) {
        mLiveUserChatData=live_user;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLivePrivateEvent(final LivePrivateEvent e) {
        if(e.getType()==0){
            tvSimiState.setVisibility(View.GONE);
            isShowPlay();
        }else if(e.getType()==1){
            tvSimiState.setVisibility(View.VISIBLE);
            flReplay.setVisibility(View.GONE);
        }else if(e.getType()==2){
            mLiveEnterRoomAnimPresenter.ShowSimiAnim(e.getTime());
        }

    }

    /**
     * ???????????????????????????
     */
    private void isShowPlay() {
        if(TextUtils.isEmpty(isVideo)){
            return;
        }

        if(isVideo.equals("1")){
            flReplay.setVisibility(View.VISIBLE);
        }else {
            if(isVideo.equals("2")&& TextUtils.isEmpty(live_tag)){
                flReplay.setVisibility(View.GONE);
            }else {
                flReplay.setVisibility(View.VISIBLE);
            }
        }
    }

    public void release() {
        LiveHttpUtil.cancel(LiveHttpConsts.GET_USER_LIST);
        LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.release();
        }
        mLiveRoomHandler = null;
        if (mLightAnimPresenter != null) {
            mLightAnimPresenter.release();
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.release();
        }
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.release();
        }
        mRefreshUserListCallback = null;
        mTimeChargeCallback = null;
    }



    public void clearData() {
        LiveHttpUtil.cancel(LiveHttpConsts.GET_USER_LIST);
        LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);
        tvSimiState.setVisibility(View.GONE);
        isShowFade =false;
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeCallbacksAndMessages(null);
        }
        if (mAvatar != null) {
            mAvatar.setImageDrawable(null);
        }
        if (mLevelAnchor != null) {
            mLevelAnchor.setImageDrawable(null);
        }
        if (mName != null) {
            mName.setText("");
        }
        if (mID != null) {
            mID.setText("");
        }
        if (llcomein != null) {
            llcomein.setVisibility(View.GONE);
            mScTv.stopScroll();
        }

        if (mVotes != null) {
            mVotes.setText("");
        }
        if (mGuardNum != null) {
            mGuardNum.setText("");
        }
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.clear();
        }
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.clear();
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.cancelAnim();
//            mLiveEnterRoomAnimPresenter.resetAnimView();
        }
        if (mLiveDanmuPresenter != null) {
            mLiveDanmuPresenter.release();
            mLiveDanmuPresenter.reset();
        }
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.cancelAllAnim();
        }

        if(flReplay!=null){
            flReplay.setVisibility(View.GONE);
        }

        if(flStar!=null){
            flStar.setVisibility(View.GONE);
        }

        if(tvReplayChat!=null){
            tvReplayChat.setVisibility(View.GONE);
        }

        if(mUserRecyclerView!=null){
            mUserRecyclerView.setVisibility(View.VISIBLE);
        }

        if(mLiveUserChatData!=null){
            mLiveUserChatData=null;
        }
    }




    public void clearDataSaveHead() {
        LiveHttpUtil.cancel(LiveHttpConsts.GET_USER_LIST);
        LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);
        tvSimiState.setVisibility(View.GONE);
        isShowFade =false;
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeCallbacksAndMessages(null);
        }
//        if (mAvatar != null) {
//            mAvatar.setImageDrawable(null);
//        }
//        if (mLevelAnchor != null) {
//            mLevelAnchor.setImageDrawable(null);
//        }
//        if (mName != null) {
//            mName.setText("");
//        }
//        if (mID != null) {
//            mID.setText("");
//        }
        if (llcomein != null) {
            llcomein.setVisibility(View.GONE);
            mScTv.stopScroll();
        }
//
//        if (mVotes != null) {
//            mVotes.setText("");
//        }
//        if (mGuardNum != null) {
//            mGuardNum.setText("");
//        }
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.clear();
        }
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.clear();
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.cancelAnim();
        }
        if (mLiveDanmuPresenter != null) {
            mLiveDanmuPresenter.release();
            mLiveDanmuPresenter.reset();
        }
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.cancelAllAnim();
        }

        if(flReplay!=null){
            flReplay.setVisibility(View.GONE);
        }

        if(flStar!=null){
            flStar.setVisibility(View.GONE);
        }

        if(tvReplayChat!=null){
            tvReplayChat.setVisibility(View.GONE);
        }

        if(mUserRecyclerView!=null){
            mUserRecyclerView.setVisibility(View.VISIBLE);
        }

        if(mLiveUserChatData!=null){
            mLiveUserChatData=null;
        }
    }


    private static class LiveRoomHandler extends Handler {

        private LiveRoomViewHolder mLiveRoomViewHolder;
        private static final int WHAT_REFRESH_USER_LIST = 1;
        private static final int WHAT_TIME_CHARGE = 2;//??????????????????????????????????????????
        private static final int WHAT_ANCHOR_LIVE_TIME = 3;//?????????????????????
        private static final int WHAT_ANCHOR_PAUSE = 4;//???????????????
        private static final int WHAT_ANCHOR_LIGHT = 5;//????????????

        public LiveRoomHandler(LiveRoomViewHolder liveRoomViewHolder) {
            mLiveRoomViewHolder = new WeakReference<>(liveRoomViewHolder).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mLiveRoomViewHolder != null) {
                switch (msg.what) {
                    case WHAT_REFRESH_USER_LIST:
                        L.e("WOLF", "??????????????????");
                        mLiveRoomViewHolder.refreshUserList();
                        break;
                    case WHAT_TIME_CHARGE:
                        mLiveRoomViewHolder.requestTimeCharge();
                        break;
                    case WHAT_ANCHOR_LIVE_TIME:
                        L.e("WOLF","WHAT_ANCHOR_LIVE_TIME");
                        mLiveRoomViewHolder.showAnchorLiveTime();
                        break;
                    case WHAT_ANCHOR_PAUSE:
                        mLiveRoomViewHolder.anchorEndLive();
                        break;
                    case WHAT_ANCHOR_LIGHT:
                        mLiveRoomViewHolder.anchorLight();
                        break;
                }
            }
        }

        public void release() {
            removeCallbacksAndMessages(null);
            mLiveRoomViewHolder = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAnim();
        LiveHttpUtil.cancel(LiveHttpConsts.GET_USER_LIST);
        LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);
        EventBus.getDefault().unregister(this);
    }

    public void cancelAnim() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (mBgAnimator1 != null) {
            mBgAnimator1.cancel();
        }
        if (mBgAnimator2 != null) {
            mBgAnimator2.cancel();
        }
    }

    public void resetTop(){
        BarUtils.addMarginTopEqualStatusBarHeight(llTop);
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public void  setTopVisible(int visible){
        if(mRoot!=null){
            mRoot.setVisibility(visible);
        }
    }


    /**
     * ???????????????????????????????????????
     */
    public void  addComeInData(LiveChatBean bean){

        if(datas==null){
            datas=new ArrayList<>();
        }
        if (datas.size()==0){
            datas.add(bean);
            llcomein.setVisibility(View.VISIBLE);
            mScTv.setList(datas);
            mScTv.startScroll();
        }else {
            datas.add(bean);
        }
    }

    /**
     * ???????????????
     */
    public void clearChat(){
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.clear();
        }
    }

    /**
     * ???????????????????????????
     */
    public void refreshUserList(String content,String nums) {
        if(mTvNumber!=null && !TextUtils.isEmpty(nums)){
            mTvNumber.setText(nums);
        }
        List<LiveUserGiftBean> list = JSON.parseArray(content, LiveUserGiftBean.class);
        if(mLiveUserAdapter!=null){
            mLiveUserAdapter.refreshList(list);
        }
    }
}
