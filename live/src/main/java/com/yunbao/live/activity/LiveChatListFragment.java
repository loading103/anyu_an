package com.yunbao.live.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentActivity;

import com.alibaba.fastjson.JSON;
import com.google.gson.jpush.Gson;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.event.LiveShowRedEvent;
import com.yunbao.common.event.SocketListClickEvent;
import com.yunbao.common.event.SocketListReFreshEvent;
import com.yunbao.common.greendao.GreenDaoUtils;
import com.yunbao.common.greendao.entity.SocketListBean;
import com.yunbao.common.greendao.entity.SocketMessageBean;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.KeyBoardHeightChangeListener;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.KeyBoardHeightUtil2;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.im.views.ChatFollowViewHolder;
import com.yunbao.im.views.ChatNewListViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.socket.SocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.yunbao.im.views.ChatListViewHolder.TYPE_DIALOG;

/**
 * Created by cxf on 2018/10/24.
 * 直播间私信聊天窗口
 */

public class LiveChatListFragment extends AbsDialogFragment implements KeyBoardHeightChangeListener {

    private ChatNewListViewHolder mChatNewListViewHolder;
    private LiveChatRoomDialogViewHolder mChatRoomViewHolder;
    private int mOriginHeight;
    private SocketClient mSocketClient;
    private FrameLayout container_list;

    public KeyBoardHeightUtil2 mKeyBoardHeightUtil;
    public  LiveChatRoomFragment chatRoomDialogFragment;
    private ChatFollowViewHolder mChatFollowViewHolder;
    private boolean  isUser;
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_chat_room;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }


    public LiveChatListFragment(SocketClient mSocketClient,boolean  isUser) {
        this.mSocketClient = mSocketClient;
        this. isUser = isUser;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        if(!isUser){   //是用户返回列表不要动画
            window.setWindowAnimations(R.style.bottomToTopAnim2);
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        mOriginHeight = DpUtil.dp2px(300);
        params.height = mOriginHeight;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        container_list = (FrameLayout) mRootView.findViewById(R.id.container_list);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        //进入时候是列表界面
        mChatNewListViewHolder = new ChatNewListViewHolder(mContext,container_list,TYPE_DIALOG);
        mChatNewListViewHolder.addToParent();
        mChatNewListViewHolder.loadData();
    }



    @Override
    public void onDestroy() {
        ((LiveActivity) mContext).setChatRoomOpened(null, false);
        if (mChatNewListViewHolder != null) {
            mChatNewListViewHolder.release();
        }
        EventBus.getDefault().unregister(this);
        sendLiveHaveRedMessage();
        super.onDestroy();
    }



    public void scrollToBottom() {
        if (chatRoomDialogFragment != null) {
            chatRoomDialogFragment.scrollToBottom();
        }
    }

    /**
     * 接收到私信消息
     */
    public void onReceivePrivateLetterMsg(SocketMessageBean bean) {
        if (chatRoomDialogFragment != null) {
            chatRoomDialogFragment.onReceivePrivateLetterMsg(bean);
        }
    }
    /**
     * 接收到发送失败的回执
     */
    public void onReceiveSendMsgFail(SocketMessageBean bean, String retmsg) {
        if (chatRoomDialogFragment != null) {
            chatRoomDialogFragment.onReceiveSendMsgFail(bean, retmsg);
        }
    }
    /**
     * 接收到发送成功的回执
     */
    public void onReceiveSendMsgSuccess(SocketMessageBean bean) {
        if (chatRoomDialogFragment != null) {
            chatRoomDialogFragment.onReceiveSendMsgSuccess(bean);
        }

    }


    /**
     * 这里是点击列表进去item
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainJsEvent(SocketListClickEvent e) {
        UserBean userBean = e.getUserBean();
        Log.e("主播点击私信--",new Gson().toJson(userBean));
        getFollowData(userBean);
    }



    /**
     * 刷新消息列表界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainJsEvent(SocketListReFreshEvent e) {
       if(mChatNewListViewHolder!=null){
           mChatNewListViewHolder.refreshData();
       }
    }
    /**
     * 用户获取与主播是不是相互关注
     */
    private void getFollowData(final UserBean userBean) {
        CommonHttpUtil.getBothFollowed(userBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(code==0 && info.length>0){
                    Log.e("用户获取与主播是不是相互关注------",info[0]);
                    String isAttention = JSON.parseObject(info[0]).getString("isattent");
                    openChatRoomWindow(userBean,true,isAttention);
                }else {
                    openChatRoomWindow(userBean,true,"1");
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
     * 主播端打开私信聊天框
     */
    public void openChatRoomWindow(UserBean userBean, boolean following,String followState) {
        if (mKeyBoardHeightUtil == null) {
            mKeyBoardHeightUtil = new KeyBoardHeightUtil2(mContext, super.findViewById(android.R.id.content), this);
            mKeyBoardHeightUtil.start();
        }
        chatRoomDialogFragment = new LiveChatRoomFragment(mSocketClient,true,true);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.USER_BEAN, userBean);
        bundle.putBoolean(Constants.FOLLOW, following);
        bundle.putString(Constants.FOLLOWSTATE, followState);
        chatRoomDialogFragment.setArguments(bundle);
        chatRoomDialogFragment.show(((FragmentActivity)mContext).getSupportFragmentManager(), "LiveChatRoomDialogFragment");
        //去掉它的红点点
        SocketListBean querySocketListBean = GreenDaoUtils.getInstance().querySocketListBean(userBean.getId());
        if(querySocketListBean!=null){
            querySocketListBean.setUnReadCount(0);
            GreenDaoUtils.getInstance().updateSocketListData(querySocketListBean);
            EventBus.getDefault().post(new SocketListReFreshEvent());
            //发一条直播间界面有没有消息的需要显示直播间红点
            sendLiveHaveRedMessage();
        }

    }

    @Override
    public void onKeyBoardHeightChanged(int visibleHeight, int keyboardHeight) {

    }

    @Override
    public boolean isSoftInputShowed() {
        return false;
    }


    /**
     * 判断数据库所有未读消息是不是已经读了
     */
    private void sendLiveHaveRedMessage() {
        boolean  haveRed=false;
        List<SocketListBean> socketListBeans = GreenDaoUtils.getInstance().queryAllSocketList();
        if(socketListBeans==null || socketListBeans.size()==0){
            return;
        }
        for (int i = 0; i < socketListBeans.size(); i++) {
            if(socketListBeans.get(i).getUnReadCount()==1){
                haveRed=true;
                break;
            }
        }
        //有未读消息 直接通知显示小红点，无未读消息看系统消息是不是有小红点
        if(haveRed){
            EventBus.getDefault().post(new LiveShowRedEvent(haveRed));
        }else {
            if(mChatNewListViewHolder!=null){
                //系统消息是不是有红点
                boolean pointVisible = mChatNewListViewHolder.getPointVisible();
                EventBus.getDefault().post(new LiveShowRedEvent(pointVisible));
            }else {
                EventBus.getDefault().post(new LiveShowRedEvent(haveRed));
            }
        }
    }

}
