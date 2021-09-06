package com.yunbao.im.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.FollowUtilsBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.FollowAncorEvent;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.im.R;
import com.yunbao.im.event.ChatFollowEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Wolf on 2020/11/27.
 * Describe:直播间私聊关注
 */
public class ChatFollowViewHolder extends AbsViewHolder {
    private ImageView tvFollow;
    private ImageView tvFollowed;
    private TextView tv_name;
    private ImageView btnBack;
    private String followstate;//==1 相互关注 ==主播关注了我  ==2 我关注了主播   ==0都没关注
    private UserBean userBean;
    private TextView  tv_content;
    private boolean isAnchor;
    private Boolean isList;

    public ChatFollowViewHolder(Context context, ViewGroup parentView,String followstate,UserBean userBean,boolean isAnchor,boolean isList) {
        super(context, parentView,followstate,userBean,isAnchor,isList);
    }
    @Override
    protected void processArguments(Object... args) {
        followstate = (String) args[0];
        userBean = (UserBean) args[1];
        isAnchor= (Boolean) args[2];
        isList= (Boolean) args[3];
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_follow;
    }

    @Override
    public void init() {
        btnBack=(ImageView) findViewById(R.id.btn_back);
        tvFollow=(ImageView) findViewById(R.id.tv_follow);
        tvFollowed=(ImageView) findViewById(R.id.tv_followed);
        tv_name=(TextView) findViewById(R.id.tv_name);
        tv_content=(TextView) findViewById(R.id.tv_content);
        if(userBean==null){
            return;
        }
        tv_name.setText(userBean.getUserNiceName());
        tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followAncor();
            }
        });
        setFollowState(followstate);
//        if(!isList){
//            btnBack.setRotation(-90);
//        }
    }
    public void setOnFinishLisenter(View.OnClickListener lisenter) {
        btnBack.setOnClickListener(lisenter);
    }
    public void  setFollowState(String state){
        if(TextUtils.isEmpty(state)){
            return;
        }
        followstate=state;
        if(followstate.equals("2")){  //你关注主播，主播没关注你
            tvFollowed.setVisibility(View.VISIBLE);
            tvFollow.setVisibility(View.GONE);
//            tv_content.setText(isAnchor?"等待用户关注":"等待主播关注");
            tv_content.setText("等待对方关注");
        }else {
            tvFollowed.setVisibility(View.GONE);
            tvFollow.setVisibility(View.VISIBLE);
//            tv_content.setText(isAnchor?"与用户相互关注才能发送私信消息哦":"与主播相互关注才能发送私信消息哦");
            tv_content.setText("与对方相互关注才能发送私信消息哦");
        }
    }


    /**
     * 关注主播
     */
    private void followAncor() {
        if(userBean==null){
            return;
        }
        if (TextUtils.isEmpty(userBean.getId())) {
            return;
        }
        CommonHttpUtil.setAttentionMJ(CommonHttpConsts.SET_ATTENTION,userBean.getId(),"",new CommonCallback<Integer>() {
            @Override
            public void callback(Integer isAttention) {
                if (isAttention == -1) { //关注失败
                    ToastUtil.show("关注失败,请稍后再试");
                    return;
                }
                if (isAttention == 1) { //关注成功,判断是不是相互关注
                    EventBus.getDefault().post(new FollowAncorEvent());
                    getFollowData();
                }
            }
        });
    }

    /**
     * 用户获取与主播是不是相互关注
     */
    private void getFollowData() {
        CommonHttpUtil.getBothFollowed(userBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(code==0){
                    String isAttention = JSON.parseObject(info[0]).getString("isattent");
                    followstate=isAttention;
                    if(followstate.equals("1")){
                        EventBus.getDefault().post(new ChatFollowEvent(true));
                        return;
                    }
                    setFollowState(followstate);
                }else {
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

}
