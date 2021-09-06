package com.yunbao.live.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.event.LiveShowRedEvent;
import com.yunbao.common.event.SocketListReFreshEvent;
import com.yunbao.common.greendao.GreenDaoUtils;
import com.yunbao.common.greendao.entity.SocketListBean;
import com.yunbao.common.greendao.entity.SocketMessageBean;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.im.event.ChatFollowEvent;
import com.yunbao.im.event.ImUserMsgEvent;
import com.yunbao.im.interfaces.ChatRoomActionListener;
import com.yunbao.im.views.ChatFollowViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.socket.SocketClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by cxf on 2018/10/24.
 * 直播间私信聊天窗口
 */

public class LiveChatRoomFragment extends AbsDialogFragment {
    private final boolean isList;//是否列表传入
    private FrameLayout container;
    private FrameLayout containerList;
    private ChatFollowViewHolder mChatFollowViewHolder;
    private LiveChatRoomDialogViewHolder mChatRoomViewHolder;
    private int mOriginHeight;
    private SocketClient mSocketClient;
    private boolean   isfinished=false;
    private boolean   isAnchor;//是不是主播
    private UserBean userBean;
    private String followstate;//==1 相互关注 ==2 你没关注主播  ==3 主播没关注你   ==0都没关注
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


    public LiveChatRoomFragment(SocketClient mSocketClient,boolean isAnchor,boolean isList) {
        this.mSocketClient = mSocketClient;
        this.isAnchor=isAnchor;
        this.isList=isList;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim2);
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
        container=   mRootView.findViewById(R.id.container);
        containerList=mRootView.findViewById(R.id.container_list);
        ((LiveActivity) mContext).setChatRoomOpened(this, true);
        Bundle bundle = getArguments();
        isfinished=false;
        if (bundle == null) {
            return;
        }
        userBean = bundle.getParcelable(Constants.USER_BEAN);
        followstate = bundle.getString(Constants.FOLLOWSTATE);
        if (userBean == null) {
            return;
        }
        setFollowState();
    }

    private void setFollowState() {
        if( TextUtils.isEmpty(followstate) || followstate.equals("1") ){//相互关注
            containerList.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
            initChatView(userBean,true);
        }else {
            containerList.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
            initNoFollowView();
        }

    }

    private void initNoFollowView() {
        mChatFollowViewHolder = new ChatFollowViewHolder(mContext,containerList,followstate,userBean,isAnchor,isList);
        mChatFollowViewHolder.addToParent();
        mChatFollowViewHolder.setOnFinishLisenter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(!isAnchor){
                    ((LiveActivity)mContext).openUserListRoomWindow(CommonAppConfig.getInstance().getUserBean(),true,true);
                }
            }
        });
    }

    private void initChatView(final UserBean userBean, boolean following) {
        mChatRoomViewHolder = new LiveChatRoomDialogViewHolder(mContext, (ViewGroup) mRootView.findViewById(R.id.container), userBean, following, mSocketClient,isList);
        mChatRoomViewHolder.setActionListener(new ChatRoomActionListener() {
            @Override
            public void onCloseClick() {
                dismiss();
                if(!isAnchor){
                    //去掉它的红点点
                    SocketListBean querySocketListBean = GreenDaoUtils.getInstance().querySocketListBean(userBean.getId());
                    if(querySocketListBean!=null){
                        querySocketListBean.setUnReadCount(0);
                        GreenDaoUtils.getInstance().updateSocketListData(querySocketListBean);
                    }
                    ((LiveActivity)mContext).openUserListRoomWindow(CommonAppConfig.getInstance().getUserBean(),true,true);
                }
            }

            @Override
            public void onPopupWindowChanged(int deltaHeight) {
                addHeight(deltaHeight);
            }

            @Override
            public void onChooseImageClick() {
            }

            @Override
            public void onCameraClick() {
            }

            @Override
            public void onVoiceInputClick() {
            }

            @Override
            public void onLocationClick() {
            }

            @Override
            public void onVoiceClick() {
            }

            @Override
            public ViewGroup getImageParentView() {
                return ((LiveActivity) mContext).getPageContainer();
            }

        });
        mChatRoomViewHolder.addToParent();
        mChatRoomViewHolder.loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatFollowEvent(final ChatFollowEvent e) {
        if(e.isFollow()){
            if(container==null){
                return;
            }
            containerList.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
            initChatView(userBean, false);
        }
    }

    @Override
    public void onDestroy() {
        ((LiveActivity) mContext).setChatRoomOpened(null, false);
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.release();
        }
        isfinished=true;
        //让直播间私信小红点消失
        sendLiveHaveRedMessage();
        super.onDestroy();
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
//        if(haveRed){
//            EventBus.getDefault().post(new LiveShowRedEvent(haveRed));
//        }else {
//            if(mChatNewListViewHolder!=null){
//                //系统消息是不是有红点
//                boolean pointVisible = mChatNewListViewHolder.getPointVisible();
//                EventBus.getDefault().post(new LiveShowRedEvent(pointVisible));
//            }else {
//                EventBus.getDefault().post(new LiveShowRedEvent(haveRed));
//            }
//        }
        EventBus.getDefault().post(new LiveShowRedEvent(haveRed));
    }

    /**
     * 增加高度
     */
    private void addHeight(int deltaHeight) {
        if(getDialog()==null){
            return;
        }
        Window window = getDialog().getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = mOriginHeight + deltaHeight;
        window.setAttributes(params);
        if (deltaHeight > 0) {
            scrollToBottom();
        }
    }


    public void scrollToBottom() {
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.scrollToBottom();
        }
    }

    /**
     * 接收到私信消息
     */
    public void onReceivePrivateLetterMsg(SocketMessageBean bean) {
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.onReceivePrivateLetterMsg(bean);
        }
        //去掉它的红点点
        if(isfinished){
            return;
        }
        SocketListBean querySocketListBean = GreenDaoUtils.getInstance().querySocketListBean(bean.getUserId());
        if(querySocketListBean!=null){
            querySocketListBean.setUnReadCount(0);
            GreenDaoUtils.getInstance().updateSocketListData(querySocketListBean);
            EventBus.getDefault().post(new SocketListReFreshEvent());
        }

    }
    /**
     * 接收到发送失败的回执
     */
    public void onReceiveSendMsgFail(SocketMessageBean bean, String retmsg) {
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.onReceiveSendMsgFail(bean, retmsg);
        }
    }
    /**
     * 接收到发送成功的回执
     */
    public void onReceiveSendMsgSuccess(SocketMessageBean bean) {
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.onReceiveSendMsgSuccess(bean);
        }
    }
}
