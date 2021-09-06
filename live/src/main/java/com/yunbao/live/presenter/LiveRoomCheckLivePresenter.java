package com.yunbao.live.presenter;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.DrawableRes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.model.Response;
import com.yunbao.common.Constants;
import com.yunbao.common.event.PreViewEvent;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.interfaces.DissmissDialogListener;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.live.dialog.PrivateLiveDialog;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.views.LiveEndLayoutView;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2017/9/29.
 */

public class LiveRoomCheckLivePresenter {

    private Context mContext;
    private int mLiveType;//直播间的类型  普通 密码 门票 计时等
    private int mLiveTypeVal;//收费价格等
    private String mLiveTypeMsg;//直播间提示信息或房间密码
    private int isPreview;//1 预览 2 不是预览
    private LiveBean mLiveBean;
    private ActionListener mActionListener;
    private  LiveEndLayoutView mEndContain;
    private String pullUrl;
    private int cdnSwitch;
    private String canScroll;
    public LiveRoomCheckLivePresenter(Context context, ActionListener actionListener) {
        mContext = context;
        mActionListener = actionListener;

    }

    /**
     * 观众 观看直播
     */
    public void checkLive(LiveBean bean, LiveEndLayoutView mEndContain) {
        mLiveBean = bean;
        this.mEndContain=mEndContain;
        canScroll = SpUtil.getInstance().getStringValue(SpUtil.SLIDE_UP);
        if(mContext instanceof LiveAudienceActivity){
            ((LiveAudienceActivity)mContext).setCanSlide("0");
        }
        LiveHttpUtil.checkLive(bean.getUid(), bean.getStream(), mCheckLiveCallback);
    }

    private HttpCallback mCheckLiveCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {

            if(mEndContain!=null && mEndContain.getVisibility()==View.VISIBLE){
                mEndContain.setVisibility(View.INVISIBLE);
            }
            if (code == 0) {
                if (info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mLiveType = obj.getIntValue("type");
                    mLiveTypeVal = obj.getIntValue("type_val");
                    mLiveTypeMsg = obj.getString("type_msg");
                    pullUrl = obj.getString("pull_url");
                    cdnSwitch = obj.getIntValue("cdn_switch");
                    isPreview = obj.getIntValue("is_preview");
                    if(isPreview==1){
                        EventBus.getDefault().post(new PreViewEvent(mLiveTypeVal,mLiveType,mLiveBean));
                        forwardNormalRoom();
                        if(mContext instanceof LiveAudienceActivity){
                            ((LiveAudienceActivity)mContext).setCanSlide(canScroll);
                        }
                    }else {
                        switch (mLiveType) {
                            case Constants.LIVE_TYPE_NORMAL:
                                forwardNormalRoom();
                                if(mContext instanceof LiveAudienceActivity){
                                    ((LiveAudienceActivity)mContext).setCanSlide(canScroll);
                                }
                                break;
                            case Constants.LIVE_TYPE_PWD:
                                forwardPwdRoom();
                                if(mContext instanceof LiveAudienceActivity){
                                    ((LiveAudienceActivity)mContext).setCanSlide(canScroll);
                                }
                                break;
                            case Constants.LIVE_TYPE_PAY:
                            case Constants.LIVE_TYPE_TIME:
                                forwardPayRoom();
                                break;
                        }
                    }
                }
            } else if (code == 1009) {
                if(mContext instanceof LiveAudienceActivity){
                    ((LiveAudienceActivity)mContext).setCanSlide(canScroll);
                }
                PrivateLiveDialog privateLiveDialog=PrivateCommonDialog(R.mipmap.icon_private_dialog5,
                        "当前主播正在进行私密互动哦~", "暂时无法进入");
                privateLiveDialog.setOnDismissDialogListener(new DissmissDialogListener() {
                    @Override
                    public void onDissmissListener() {
                        if(mContext instanceof LiveAudienceActivity){
                            ((LiveAudienceActivity)mContext).onBackPressed();
                        }
                    }
                });
            }else if (code == 1005) {
                if(mContext instanceof LiveAudienceActivity){
                    ((LiveAudienceActivity)mContext).setCanSlide(canScroll);
                }
                if(mEndContain==null ){
                    return;
                }
                //显示结束界面
                if(mEndContain.getVisibility()==View.INVISIBLE) {
                    mEndContain.setVisibility(View.VISIBLE);
                }
                if(mContext instanceof LiveAudienceActivity){
                    ((LiveAudienceActivity)mContext).onLiveEndBean(mLiveBean,mEndContain);
                }
            } else {
                if(mContext instanceof LiveAudienceActivity){
                    ((LiveAudienceActivity)mContext).setCanSlide(canScroll);
                }
                ToastUtil.show(msg);
            }
        }

        @Override
        public boolean showLoadingDialog() {
            return false;
        }

        @Override
        public Dialog createLoadingDialog() {
            return DialogUitl.loadingNewWhiteDialog(mContext);
        }

        @Override
        public void onError(Response<JsonBean> response) {
            super.onError(response);
            if(mContext instanceof LiveAudienceActivity){
                ((LiveAudienceActivity)mContext).setCanSlide(canScroll);
            }
        }
    };

    /**
     * 前往普通房间
     */
    private void forwardNormalRoom() {
        enterLiveRoom();
    }

    /**
     * 前往密码房间
     */
    private void forwardPwdRoom() {
        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_input_password), DialogUitl.INPUT_TYPE_NUMBER_PASSWORD, new DialogUitl.SimpleCallback2() {
            @Override
            public void onCancelClick() {
                if(mContext instanceof LiveAudienceActivity){
                    ((LiveAudienceActivity)mContext).onBackPressed();
                }
            }

            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(WordUtil.getString(R.string.live_input_password));
                    return;
                }
                String password = MD5Util.getMD5(content);
                if (mLiveTypeMsg.equalsIgnoreCase(password)) {
                    dialog.dismiss();
                    enterLiveRoom();
                } else {
                    ToastUtil.show(WordUtil.getString(R.string.live_password_error));
                }
            }
        });
    }


    /**
     * 前往付费房间
     */
    private void forwardPayRoom() {
        DialogUitl.showSimpleDialog(mContext, mLiveTypeMsg,false, new DialogUitl.SimpleCallback2() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if(mContext instanceof LiveAudienceActivity){
                    ((LiveAudienceActivity)mContext).setCanSlide("1");
                }
                roomCharge();
            }
            @Override
            public void onCancelClick() {
                L.e("onCancelClick");
                if(mContext instanceof LiveAudienceActivity){
                    if(mContext instanceof LiveAudienceActivity){
                        ((LiveAudienceActivity)mContext).setCanSlide("1");
                    }
                    ((LiveAudienceActivity)mContext).onBackPressed();
                }
            }
        });
    }


    public void roomCharge() {
        if (mLiveBean == null) {
            return;
        }
        LiveHttpUtil.roomCharge(mLiveBean.getUid(), mLiveBean.getStream(), mRoomChargeCallback);
    }

    private HttpCallback mRoomChargeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                enterLiveRoom();
            } else {
                if(mContext instanceof  LiveAudienceActivity){
                    if (code == 1008 || code ==-1){
                        DialogUitl.showSimpleTipCallDialog(mContext, "温馨提示",  WordUtil.getString(R.string.live_coin_not_enough),  new DialogUitl.SimpleCallback() {
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                ((LiveAudienceActivity)mContext).onBackPressed();
                            }
                        });
                    }else {
                        ToastUtil.show(msg);
                    }
                }else {
                    ToastUtil.show(msg);
                }
            }
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }

        @Override
        public Dialog createLoadingDialog() {
            return DialogUitl.loadingNewDialog(mContext);
        }
    };

    public void cancel() {
        mActionListener = null;
        LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
    }

    /**
     * 进入直播间
     */
    private void enterLiveRoom() {
        if (mActionListener != null) {
            mActionListener.onLiveRoomChanged(mLiveBean,mLiveType, mLiveTypeVal,cdnSwitch,pullUrl);
        }
    }


    public interface ActionListener {
        void onLiveRoomChanged(LiveBean liveBean, int liveType, int liveTypeVal,int cdnSwitch,String pullUrl);
    }

    /**
     * 私密互动dialog
     * @param res
     * @param title
     * @param content
     */
    private PrivateLiveDialog PrivateCommonDialog(@DrawableRes int res, String title, String content) {
        PrivateLiveDialog privateDialog = new PrivateLiveDialog();
        privateDialog.setData(res,title,content);
        privateDialog.show(((LiveAudienceActivity)mContext).getSupportFragmentManager(), "PrivateLiveDialog");
        return privateDialog;
    }
}
