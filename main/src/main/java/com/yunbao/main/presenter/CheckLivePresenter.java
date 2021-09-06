package com.yunbao.main.presenter;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.live.dialog.PrivateLiveDialog;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.main.event.LiveEndEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2017/9/29.
 */

public class CheckLivePresenter {

    private Context mContext;
    private LiveBean mLiveBean;//选中的直播间信息
    private String mKey;
    private int mPosition;
    private int mLiveType;//直播间的类型  普通 密码 门票 计时等
    private int mLiveTypeVal;//收费价格等
    private String mLiveTypeMsg;//直播间提示信息或房间密码
    private int mLiveSdk;
    private HttpCallback mCheckLiveCallback;
    private String pullUrl;
    private int cdnSwitch;
    private int isPreview;

    public CheckLivePresenter(Context context) {
        mContext = context;
    }

    /**
     * 观众 观看直播
     */
    public void watchLive(LiveBean bean, String key, int position) {
        mLiveBean = bean;
        mKey = key;
        mPosition = position;
        if (mCheckLiveCallback == null) {
            mCheckLiveCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    L.e("WOLF","直播结束："+code);
                    if (code == 0) {
                        if (info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            mLiveType = obj.getIntValue("type");
                            mLiveTypeVal = obj.getIntValue("type_val");
                            mLiveTypeMsg = obj.getString("type_msg");
                            pullUrl = obj.getString("pull_url");
                            cdnSwitch = obj.getIntValue("cdn_switch");
                            isPreview = obj.getIntValue("is_preview");
                            if (CommonAppConfig.LIVE_SDK_CHANGED) {
                                mLiveSdk = obj.getIntValue("live_sdk");
                            } else {
                                mLiveSdk = CommonAppConfig.LIVE_SDK_USED;
                            }
                            if(isPreview==1){
                                forwardNormalRoom();
                            }else {
                                switch (mLiveType) {
                                    case Constants.LIVE_TYPE_NORMAL:
                                        forwardNormalRoom();
                                        break;
                                    case Constants.LIVE_TYPE_PWD:
                                        forwardPwdRoom();
                                        break;
                                    case Constants.LIVE_TYPE_PAY:
                                    case Constants.LIVE_TYPE_TIME:
                                        forwardPayRoom();
                                        break;
                                }
                            }
                        }
                    } else if(code==1009){
                       PrivateCommonDialog(com.yunbao.live.R.mipmap.icon_private_dialog5, "当前主播正在进行私密互动哦~", "暂时无法进入");
                    }else {
                        ToastUtil.show(msg);
                        if(code==1005){//直播已结束
                            EventBus.getDefault().post(new LiveEndEvent(mLiveBean));
                        }
                    }
                }
//
//                @Override
//                public boolean showLoadingDialog() {
//                    return false;
//                }
//
//                @Override
//                public Dialog createLoadingDialog() {
//                    return null;
//                }
            };
        }
        LiveHttpUtil.checkLive(bean.getUid(), bean.getStream(), mCheckLiveCallback);
    }


    /**
     * 前往普通房间
     */
    private void forwardNormalRoom() {
        forwardLiveAudienceActivity();
    }

    /**
     * 前往密码房间
     */
    private void forwardPwdRoom() {
        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_input_password), DialogUitl.INPUT_TYPE_NUMBER_PASSWORD, new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(WordUtil.getString(R.string.live_input_password));
                    return;
                }
                String password = MD5Util.getMD5(content);
                if (mLiveTypeMsg.equalsIgnoreCase(password)) {
                    dialog.dismiss();
                    forwardLiveAudienceActivity();
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
        DialogUitl.showSimpleDialog(mContext, mLiveTypeMsg, false,new DialogUitl.SimpleCallback2() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                roomCharge();
            }
            @Override
            public void onCancelClick() {
                L.e("onCancelClick");
                if(mContext instanceof LiveAudienceActivity){
                    ((LiveAudienceActivity)mContext).onBackPressed();
                }
            }
        });
    }


    public void roomCharge() {
        LiveHttpUtil.roomCharge(mLiveBean.getUid(), mLiveBean.getStream(), mRoomChargeCallback);
    }

    private HttpCallback mRoomChargeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                forwardLiveAudienceActivity();
            } else {
                ToastUtil.show(msg);
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
        LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
    }

    /**
     * 跳转到直播间
     */
    private void forwardLiveAudienceActivity() {
        LiveAudienceActivity.forward(mContext, mLiveBean, mLiveType, mLiveTypeVal, mKey, mPosition, mLiveSdk,cdnSwitch,pullUrl,isPreview);
    }

    /**
     * 私密直播dialog
     * @param res
     * @param title
     * @param content
     */
    private PrivateLiveDialog PrivateCommonDialog(@DrawableRes int res, String title, String content) {
        PrivateLiveDialog privateDialog = new PrivateLiveDialog();
        privateDialog.setData(res,title,content);
        privateDialog.show(((MainActivity)mContext).getSupportFragmentManager(), "PrivateLiveDialog");
        return privateDialog;
    }
}
