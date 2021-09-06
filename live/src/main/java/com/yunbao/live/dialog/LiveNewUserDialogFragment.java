package com.yunbao.live.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveActivity;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.activity.LiveReportActivity;
import com.yunbao.live.bean.ImpressBean;
import com.yunbao.live.custom.MyTextView;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.utils.LiveTextRender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/15.
 * 直播间个人资料弹窗
 */

public class LiveNewUserDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private static final int TYPE_AUD_AUD = 1;//观众点别的观众
    private static final int TYPE_ANC_AUD = 2;//主播点观众
    private static final int TYPE_AUD_ANC = 3;//观众点主播
    private static final int TYPE_AUD_SELF = 4;//观众点自己
    private static final int TYPE_ANC_SELF = 5;//主播点自己

    private static final int SETTING_ACTION_SELF = 0;//设置 自己点自己
    private static final int SETTING_ACTION_AUD = 30;//设置 普通观众点普通观众 或所有人点超管
    private static final int SETTING_ACTION_ADM = 40;//设置 房间管理员点普通观众
    private static final int SETTING_ACTION_SUP = 60;//设置 超管点主播
    private static final int SETTING_ACTION_ANC_AUD = 501;//设置 主播点普通观众
    private static final int SETTING_ACTION_ANC_ADM = 502;//设置 主播点房间管理员
    private LinearLayout mRootGroup;
    private ImageView mAvatar;
    private ImageView mLevelAnchor;
    private ImageView mLevel;
    private ImageView mSex;
    private TextView mName;
    private TextView mID;
    private TextView mCity;
    private LinearLayout mImpressGroup;
    private TextView mFollow;
    private TextView mFans;
    private TextView mSign;
    private TextView btn_add;
    private TextView mConsume;//消费
    private TextView mVotes;//收入
    private TextView mConsumeTip;
    private TextView mVotesTip;
    private String mLiveUid;
    private String mStream;
    private String mToUid;
    private TextView mBtnFollow;
    private TextView mTvLevel;
    private TextView mTvZbLevel;
    private ImageView mIvVip;
    private int mType;
    private int mAction;
    private String mToName;//对方的名字
    private UserBean mUserBean;
    private boolean mFollowing;
    private String mOrUid;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_new_live_user;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(com.yunbao.common.R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mLiveUid = bundle.getString(Constants.LIVE_UID);
        mToUid = bundle.getString(Constants.TO_UID);
        mOrUid = bundle.getString(Constants.LIVE_OR_UID);
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mToUid)) {
            return;
        }
        mStream = bundle.getString(Constants.STREAM);
        mRootGroup = (LinearLayout) mRootView;
        mAvatar = (ImageView) mRootView.findViewById(R.id.avatar);
        mIvVip = (ImageView) mRootView.findViewById(R.id.vip);
        mLevelAnchor = (ImageView) mRootView.findViewById(R.id.anchor_level);
        mLevel = (ImageView) mRootView.findViewById(R.id.level);
        mTvLevel = (TextView) mRootView.findViewById(R.id.tv_leve);
        mTvZbLevel = (TextView) mRootView.findViewById(R.id.tv_zb_leve);
        mSex = (ImageView) mRootView.findViewById(R.id.sex);
        mName = (TextView) mRootView.findViewById(R.id.name);
        mID = (TextView) mRootView.findViewById(R.id.id_val);
        mCity = (TextView) mRootView.findViewById(R.id.city);
        mImpressGroup = (LinearLayout) mRootView.findViewById(R.id.impress_group);
        btn_add = (TextView) mRootView.findViewById(R.id.btn_add);
        mFollow = (TextView) mRootView.findViewById(R.id.follow);
        mFans = (TextView) mRootView.findViewById(R.id.fans);
        mSign = (TextView) findViewById(R.id.sign);
        mConsume = (TextView) mRootView.findViewById(R.id.consume);
        mVotes = (TextView) mRootView.findViewById(R.id.votes);
        mConsumeTip = (TextView) mRootView.findViewById(R.id.consume_tip);
        mVotesTip = (TextView) mRootView.findViewById(R.id.votes_tip);
        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
        getType();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View bottomView = null;
        if (mType == TYPE_AUD_ANC) {
            if (mImpressGroup.getVisibility() != View.VISIBLE) {
                mImpressGroup.setVisibility(View.VISIBLE);
                btn_add.setVisibility(View.GONE);
            }

            bottomView = inflater.inflate(R.layout.dialog_live_user_bottom_1, mRootGroup, true);
        } else if (mType == TYPE_AUD_AUD) {
            if (mImpressGroup.getVisibility() != View.VISIBLE) {
                mImpressGroup.setVisibility(View.INVISIBLE);
                btn_add.setVisibility(View.GONE);
            }
            bottomView = inflater.inflate(R.layout.dialog_live_user_bottom_1, mRootGroup, true);
        } else if (mType == TYPE_ANC_AUD) {
//            if (mImpressGroup.getVisibility() != View.VISIBLE) {
//                mImpressGroup.setVisibility(View.INVISIBLE);
//                btn_add.setVisibility(View.GONE);
//            }
//            bottomView = inflater.inflate(R.layout.dialog_live_user_bottom_2, mRootGroup, true);
            if (mImpressGroup.getVisibility() != View.VISIBLE) {
                mImpressGroup.setVisibility(View.VISIBLE);
                btn_add.setVisibility(View.GONE);
            }
            bottomView = inflater.inflate(R.layout.dialog_live_user_bottom_1, mRootGroup, true);
        } else if (mType == TYPE_AUD_SELF) {
            if (mImpressGroup.getVisibility() != View.VISIBLE) {
                mImpressGroup.setVisibility(View.INVISIBLE);
                btn_add.setVisibility(View.GONE);
            }
            bottomView = inflater.inflate(R.layout.dialog_live_user_bottom_3, mRootGroup, true);
        } else if (mType == TYPE_ANC_SELF) {
            if (mImpressGroup.getVisibility() != View.VISIBLE) {
                mImpressGroup.setVisibility(View.INVISIBLE);
                btn_add.setVisibility(View.GONE);
            }
            bottomView = inflater.inflate(R.layout.dialog_live_user_bottom_4, mRootGroup, true);
        }
        if (bottomView != null) {
            mBtnFollow = (TextView) bottomView.findViewById(R.id.btn_follow);
            if (mBtnFollow != null) {
                mBtnFollow.setOnClickListener(this);
            }
            View btnPriMsg = bottomView.findViewById(R.id.btn_pri_msg);
            if(mType ==TYPE_AUD_AUD){
                if (btnPriMsg != null) {
                    btnPriMsg.setVisibility(View.GONE);
                }
            }else {
                if (btnPriMsg != null) {
                    btnPriMsg.setVisibility(View.VISIBLE);
                }
            }
            if (btnPriMsg != null) {
                btnPriMsg.setOnClickListener(this);
            }
            View btnHomePage = bottomView.findViewById(R.id.btn_home_page);
            if (btnHomePage != null) {
                btnHomePage.setOnClickListener(this);
            }
        }
        loadData();
    }

    private void getType() {
        String uid = CommonAppConfig.getInstance().getUid();
        if (mToUid.equals(mLiveUid)) {
            if (mLiveUid.equals(uid)) {//主播点自己
                mType = TYPE_ANC_SELF;
            } else {//观众点主播
                mType = TYPE_AUD_ANC;
            }
        } else {
            if (mLiveUid.equals(uid)) {//主播点观众
                mType = TYPE_ANC_AUD;
            } else {
                if (mToUid.equals(uid)) {//观众点自己
                    mType = TYPE_AUD_SELF;
                } else {//观众点别的观众
                    mType = TYPE_AUD_AUD;
                }
            }
        }
    }

    private void loadData() {
        LiveHttpUtil.getLiveUser(mToUid, mLiveUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    showData(info[0]);
                }
            }
        });
    }

    private void showData(String data) {
        JSONObject obj = JSON.parseObject(data);
        mUserBean = JSON.toJavaObject(obj, UserBean.class);
        CommonAppConfig appConfig = CommonAppConfig.getInstance();
        mID.setText(mUserBean.getLiangNameTip());
        mToName = obj.getString("user_nicename");

        mName.setText(mToName);
        mCity.setText(obj.getString("city"));
        if (mUserBean != null && mUserBean.getVip() != null) {
            setVipLeve(mUserBean.getVip());
        }

        if (mAvatar == null) {
            return;
        }
        if (!TextUtils.isEmpty(obj.getString("avatar"))) {
            ImgLoader.displayAvatar(mContext, obj.getString("avatar"), mAvatar);
        }
        LevelBean anchorLevelBean = appConfig.getAnchorLevel(obj.getIntValue("level_anchor"));
        ImgLoader.display(mContext, anchorLevelBean.getThumb(), mLevelAnchor);
        LevelBean levelBean = appConfig.getLevel(obj.getIntValue("level"));
        ImgLoader.display(mContext, levelBean.getThumb(), mLevel);

        if (levelBean != null) {
            mTvLevel.setText(levelBean.getLevel() + "");
        }
        if (anchorLevelBean != null) {
            mTvZbLevel.setText(anchorLevelBean.getLevel() + "");
        }
        if (CommonAppConfig.getInstance().getConfig().getPersonal_sign().equals("0")) {
            mSign.setVisibility(View.INVISIBLE);
        } else {
            mSign.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(obj.getString("signature"))) {
                mSign.setText(obj.getString("signature"));
            }
        }

        mSex.setImageResource(CommonIconUtil.getSexIcon(obj.getIntValue("sex")));
        mFollow.setText(LiveTextRender.renderLiveUserDialogData(obj.getLongValue("follows")));
        mFans.setText(LiveTextRender.renderLiveUserDialogData(obj.getLongValue("fans")));
        mConsume.setText(LiveTextRender.renderLiveUserDialogData2(obj.getDoubleValue("consumption")));
        mVotes.setText(LiveTextRender.renderLiveUserDialogData2(obj.getDoubleValue("votestotal")));
        mConsumeTip.setText(WordUtil.getString(R.string.live_user_send) + appConfig.getCoinName());
        mVotesTip.setText(WordUtil.getString(R.string.live_user_get) + appConfig.getVotesName());
        if (mType == TYPE_AUD_ANC) {
            showImpress(obj.getString("label"));
        }
        mFollowing = obj.getIntValue("isattention") == 1;
        if (mFollowing) {
            mBtnFollow.setText(WordUtil.getString(R.string.following));
        }
        mAction = obj.getIntValue("action");
        L.e("action:" + mAction);
        if (mAction == SETTING_ACTION_AUD) {//设置 普通观众点普通观众 或所有人点超管
            L.e("action:1");
            View btnReport = mRootView.findViewById(R.id.btn_report);
            btnReport.setVisibility(View.VISIBLE);
            btnReport.setOnClickListener(this);
        } else if (mAction == SETTING_ACTION_ADM//设置 房间管理员点普通观众
                || mAction == SETTING_ACTION_SUP//设置 超管点主播
                || mAction == SETTING_ACTION_ANC_AUD//设置 主播点普通观众
                || mAction == SETTING_ACTION_ANC_ADM) {//设置 主播点房间管理员
//            if(mType==TYPE_AUD_ANC ||  mType==TYPE_AUD_AUD){
//                L.e("action:2");
//                View btnSetting = mRootView.findViewById(R.id.btn_setting);
//                btnSetting.setVisibility(View.GONE);
//                View btnReport = mRootView.findViewById(R.id.btn_report);
//                btnReport.setVisibility(View.VISIBLE);
//                btnSetting.setOnClickListener(this);
//            }else if(mType==TYPE_AUD_SELF ){
//                L.e("action:3");
//                View btnSetting = mRootView.findViewById(R.id.btn_setting);
//                btnSetting.setVisibility(View.GONE);
//                View btnReport = mRootView.findViewById(R.id.btn_report);
//                btnReport.setVisibility(View.GONE);
//            }else {
//                L.e("action:4");
//                View btnSetting = mRootView.findViewById(R.id.btn_setting);
//                btnSetting.setVisibility(View.VISIBLE);
//                View btnReport = mRootView.findViewById(R.id.btn_report);
//                btnReport.setVisibility(View.GONE);
//                btnSetting.setOnClickListener(this);
//            }
            if (mAction == SETTING_ACTION_ADM||mAction == SETTING_ACTION_SUP) {
                L.e("action:4");
                View btnSetting = mRootView.findViewById(R.id.btn_setting);
                btnSetting.setVisibility(View.VISIBLE);
                View btnReport = mRootView.findViewById(R.id.btn_report);
                btnReport.setVisibility(View.GONE);
                btnSetting.setOnClickListener(this);
            } else if (mType == TYPE_AUD_ANC || mType == TYPE_AUD_AUD) {
                L.e("action:2");
                View btnSetting = mRootView.findViewById(R.id.btn_setting);
                btnSetting.setVisibility(View.GONE);
                View btnReport = mRootView.findViewById(R.id.btn_report);
                btnReport.setVisibility(View.VISIBLE);
                btnSetting.setOnClickListener(this);
            } else if (mType == TYPE_AUD_SELF) {
                L.e("action:3");
                View btnSetting = mRootView.findViewById(R.id.btn_setting);
                btnSetting.setVisibility(View.GONE);
                View btnReport = mRootView.findViewById(R.id.btn_report);
                btnReport.setVisibility(View.GONE);
            } else {
                L.e("action:4");
                View btnSetting = mRootView.findViewById(R.id.btn_setting);
                btnSetting.setVisibility(View.VISIBLE);
                View btnReport = mRootView.findViewById(R.id.btn_report);
                btnReport.setVisibility(View.GONE);
                btnSetting.setOnClickListener(this);
            }
        } else if (mType == TYPE_AUD_SELF || mType == TYPE_ANC_SELF) {
            View btnSetting = mRootView.findViewById(R.id.btn_setting);
            btnSetting.setVisibility(View.GONE);
            View btnReport = mRootView.findViewById(R.id.btn_report);
            btnReport.setVisibility(View.GONE);
        }
    }

    private void showImpress(String impressJson) {
        List<ImpressBean> list = JSON.parseArray(impressJson, ImpressBean.class);
        if (list == null || list.size() == 0) {
            mImpressGroup.setVisibility(View.GONE);
            btn_add.setVisibility(View.VISIBLE);
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addImpress();
                }
            });
            return;
        }
        if (list.size() > 3) {
            list = list.subList(0, 3);
        }


        ImpressBean lastBean = new ImpressBean();
        lastBean.setName(WordUtil.getString(R.string.impress_add));
        lastBean.setColor("#999999");
        list.add(lastBean);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0, size = list.size(); i < size; i++) {
            MyTextView myTextView = (MyTextView) inflater.inflate(R.layout.view_impress_item_2, mImpressGroup, false);
            ViewGroup.LayoutParams layoutParams = myTextView.getLayoutParams();
            layoutParams.width = (IMDensityUtil.getScreenWidth(mContext) - IMDensityUtil.dip2px(mContext, 32)) / 4;
            myTextView.setLayoutParams(layoutParams);
            ImpressBean bean = list.get(i);
            if (i == size - 1) {
                myTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addImpress();
                    }
                });
            } else {
                bean.setCheck(1);
            }
            myTextView.setBean(bean);
            mImpressGroup.addView(myTextView);
        }
    }


    /**
     * 添加主播印象
     */
    private void addImpress() {
        dismiss();
        ((LiveActivity) mContext).openAddImpressWindow(mLiveUid);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_close) {
            dismiss();

        } else if (i == R.id.btn_follow) {
            setAttention();

        } else if (i == R.id.btn_pri_msg) {
            openChatRoomWindow();

        } else if (i == R.id.btn_home_page) {
            forwardHomePage();

        } else if (i == R.id.btn_setting) {
            setting();

        } else if (i == R.id.btn_report) {
            report();

        }
    }

    /**
     * 打开私信聊天窗口
     */
    private void openChatRoomWindow() {
        if (mUserBean != null) {
            dismiss();
            ImMessageUtil.getInstance().markAllMessagesAsRead(mToUid, true);
//            ((LiveActivity) mContext).openChatRoomWindow(mUserBean, mFollowing);
            if(mContext instanceof LiveAnchorActivity){
                ((LiveActivity) mContext).openAudienceChatRoom(mUserBean, mFollowing,true);
            }else {
                ((LiveActivity) mContext).openAudienceChatRoom(mUserBean, mFollowing,false);
            }
        }
    }

    /**
     * 关注
     */
    private void setAttention() {
        CommonHttpUtil.setAttention(mToUid, mAttentionCallback);
    }

    private CommonCallback<Integer> mAttentionCallback = new CommonCallback<Integer>() {

        @Override
        public void callback(Integer isAttention) {
            mFollowing = isAttention == 1;
            if (mBtnFollow != null) {
                mBtnFollow.setText(mFollowing ? R.string.following : R.string.follow);
            }
            if (isAttention == 1 && mLiveUid.equals(mToUid)) {//关注了主播
                ((LiveActivity) mContext).sendGzSystemMessage(
                        CommonAppConfig.getInstance().getUserBean().getUserNiceName() + WordUtil.getString(R.string.live_follow_anchor), "1");
            }
        }
    };

    /**
     * 跳转到个人主页
     */
    private void forwardHomePage() {
        dismiss();
        RouteUtil.forwardUserHome(mContext, mToUid);
    }

    /**
     * 举报
     */
    private void report() {
        LiveReportActivity.forward(mContext, mToUid);
    }

    /**
     * 设置
     * <p>
     * 某个大神说，角色是权限的集合。。
     * <p>
     * 理论上，角色的权限应该有服务端以数组或集合的形式返回，这样可以在后台动态配置某种角色的权限，而不是这样口头约定写死。。。
     * 然而，是服务端这样做的，我也很无奈。。。也许他们不知道如何做成动态配置的吧。。
     * <p>
     * 我一直想通过不断重构把代码写的像艺术品，然而，最近发现，这完全是多此一举，自讨苦吃。。是我太天真了。。
     * 下面是我发现的一篇文章，说的非常好，也点醒了我。。如果你们发现当前代码写的太烂，不堪入目的话，请阅读下面的文章。
     * <p>
     * https://www.jianshu.com/p/71521541cd25?utm_campaign=haruki&utm_content=note&utm_medium=reader_share&utm_source=weixin_timeline&from=timeline
     */
    private void setting() {
        List<Integer> list = new ArrayList<>();
        switch (mAction) {
            case SETTING_ACTION_ADM://设置 房间管理员点普通观众
                list.add(R.string.live_setting_kick);
                list.add(R.string.live_setting_gap);
                list.add(R.string.live_setting_gap_2);
                break;
            case SETTING_ACTION_SUP://设置 超管点主播
                list.add(R.string.live_setting_close_live);
                list.add(R.string.live_setting_close_live_2);
                list.add(R.string.live_setting_forbid_account);
                break;
            case SETTING_ACTION_ANC_AUD://设置 主播点普通观众
                list.add(R.string.live_setting_kick);
                list.add(R.string.live_setting_gap);
                list.add(R.string.live_setting_gap_2);
                list.add(R.string.live_setting_admin);
                list.add(R.string.live_setting_admin_list);
                break;
            case SETTING_ACTION_ANC_ADM://设置 主播点房间管理员
                list.add(R.string.live_setting_kick);
                list.add(R.string.live_setting_gap);
                list.add(R.string.live_setting_gap_2);
                list.add(R.string.live_setting_admin_cancel);
                list.add(R.string.live_setting_admin_list);
                break;
        }

        DialogUitl.showStringArrayDialog(mContext, list.toArray(new Integer[list.size()]), mArrayDialogCallback);
    }

    private DialogUitl.StringArrayDialogCallback mArrayDialogCallback = new DialogUitl.StringArrayDialogCallback() {
        @Override
        public void onItemClick(String text, int tag) {
            if (tag == R.string.live_setting_kick) {
                kick();

            } else if (tag == R.string.live_setting_gap) {//永久禁言
                setShutUp();

            } else if (tag == R.string.live_setting_gap_2) {//本场禁言
                setShutUp2();

            } else if (tag == R.string.live_setting_admin || tag == R.string.live_setting_admin_cancel) {
                setAdmin();

            } else if (tag == R.string.live_setting_admin_list) {
                adminList();

            } else if (tag == R.string.live_setting_close_live) {
                closeLive();

            } else if (tag == R.string.live_setting_forbid_account) {
                forbidAccount();

            } else if (tag == R.string.live_setting_close_live_2) {//禁用直播
                closeLive2();
            }
        }
    };

    /**
     * 查看管理员列表
     */
    private void adminList() {
        dismiss();
        ((LiveActivity) mContext).openAdminListWindow();
    }

    /**
     * 踢人
     */
    private void kick() {
        LiveHttpUtil.kicking(mLiveUid, mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (getDialog() != null) {
                        getDialog().dismiss();
                    }
                    ((LiveActivity) mContext).kickUser(mToUid, mToName);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 永久禁言
     */
    private void setShutUp() {
        LiveHttpUtil.setShutUp(mLiveUid, "0", 0, mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (getDialog() != null) {
                        getDialog().dismiss();
                    }
                    ((LiveActivity) mContext).setShutUp(mToUid, mToName, 0);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 本场禁言
     */
    private void setShutUp2() {
        LiveHttpUtil.setShutUp(mLiveUid, mStream, 1, mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (getDialog() != null) {
                        getDialog().dismiss();
                    }
                    ((LiveActivity) mContext).setShutUp(mToUid, mToName, 1);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    /**
     * 设置或取消管理员
     */
    private void setAdmin() {
        LiveHttpUtil.setAdmin(mLiveUid, mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    int res = JSON.parseObject(info[0]).getIntValue("isadmin");
                    if (res == 1) {//被设为管理员
                        mAction = SETTING_ACTION_ANC_ADM;
                    } else {//被取消管理员
                        mAction = SETTING_ACTION_ANC_AUD;
                    }
                    ((LiveActivity) mContext).sendSetAdminMessage(res, mToUid, mToName);
                }
            }
        });
    }


    /**
     * 超管关闭直播间
     */
    private void closeLive() {
        dismiss();
        LiveHttpUtil.superCloseRoom(mLiveUid, 0, mSuperCloseRoomCallback);
    }

    /**
     * 超管关闭直播间并禁止主播直播
     */
    private void closeLive2() {
        dismiss();
        LiveHttpUtil.superCloseRoom(mLiveUid, 1, mSuperCloseRoomCallback);
    }

    /**
     * 超管关闭直播间并禁用主播账户
     */
    private void forbidAccount() {
        dismiss();
        LiveHttpUtil.superCloseRoom(mLiveUid, 2, mSuperCloseRoomCallback);
    }

    private HttpCallback mSuperCloseRoomCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                ((LiveActivity) mContext).superCloseRoom();
            } else {
                ToastUtil.show(msg);
            }
        }
    };

    @Override
    public void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.GET_LIVE_USER);
        if (mAvatar != null) {
            ImgLoader.clear(CommonAppContext.sInstance, mAvatar);
            mAvatar.setImageDrawable(null);
        }
        mAvatar = null;
        super.onDestroy();
    }

    /**
     * 设置vip
     */
    private void setVipLeve(UserBean.Vip vip) {
        if (vip.getVip_is_king() == 1) {
            mIvVip.setVisibility(View.VISIBLE);
            if (vip.getType() > 35) {
                mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_35s));
                return;
            }
            switch (vip.getType()) {
                case 1:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_1s));
//                    animatorUtil.FrameNoOneAnimation(mIvVip);
                    break;
                case 2:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_2s));
                    break;
                case 3:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_3s));
                    break;
                case 4:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_4s));
                    break;
                case 5:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_5s));
                    break;
                case 6:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_6s));
                    break;
                case 7:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_7s));
                    break;
                case 8:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_8s));
                    break;
                case 9:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_9s));
                    break;
                case 10:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_10s));
                    break;
                case 11:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_11s));
                    break;
                case 12:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_12s));
                    break;
                case 13:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_13s));
                    break;
                case 14:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_14s));
                    break;
                case 15:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_15s));
                    break;
                case 16:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_16s));
                    break;
                case 17:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_17s));
                    break;
                case 18:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_18s));
                    break;
                case 19:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_19s));
                    break;
                case 20:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_20s));
                    break;
                case 21:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_21s));
                    break;
                case 22:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_22s));
                    break;
                case 23:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_23s));
                    break;
                case 24:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_24s));
                    break;
                case 25:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_25s));
                    break;
                case 26:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_26s));
                    break;
                case 27:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_27s));
                    break;
                case 28:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_28s));
                    break;
                case 29:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_29s));
                    break;
                case 30:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_30s));
                    break;
                case 31:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_31s));
                    break;
                case 32:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_32s));
                    break;
                case 33:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_33s));
                    break;
                case 34:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_34s));
                    break;
                case 35:
                    mIvVip.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.vip_35s));
                    break;
            }
        } else {
            mIvVip.setVisibility(View.GONE);
        }
    }
}