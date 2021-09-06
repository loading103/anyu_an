package com.yunbao.im.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.SocketListClickEvent;
import com.yunbao.common.event.SocketListReFreshEvent;
import com.yunbao.common.greendao.GreenDaoUtils;
import com.yunbao.common.greendao.entity.SocketListBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.im.R;
import com.yunbao.im.activity.LiveChatRoomActivity;
import com.yunbao.im.activity.SystemMessageActivity;
import com.yunbao.im.adapter.ImNewListAdapter;
import com.yunbao.im.bean.SystemMessageBean;
import com.yunbao.im.dialog.SystemMessageDialogFragment;
import com.yunbao.im.event.ImUnReadCountEvent;
import com.yunbao.im.event.SystemMsgEvent;
import com.yunbao.im.http.ImHttpConsts;
import com.yunbao.im.http.ImHttpUtil;
import com.yunbao.im.utils.ImMessageUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * 消息列表界面
 */

public class ChatNewListViewHolder extends AbsViewHolder implements View.OnClickListener, OnItemChildClickListener {

    public static final int TYPE_ACTIVITY = 0;
    public static final int TYPE_DIALOG = 1;
    private int mType;
    private View mBtnSystemMsg;
    private RecyclerView mRecyclerView;
    private ImNewListAdapter mAdapter;
    private View mSystemMsgRedPoint;//系统消息的红点
    private TextView mSystemMsgContent;
    private TextView mSystemTime;
    private View mBtnBack;
    private String mLiveUid;//主播的uid
    private List<SocketListBean> datas;
    private View headView;
    public ChatNewListViewHolder(Context context, ViewGroup parentView, int type) {
        super(context, parentView, type);
    }

    @Override
    protected void processArguments(Object... args) {
        mType = (int) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_list;
    }

    @Override
    public void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mBtnBack = findViewById(R.id.btn_back);
        findViewById(R.id.btn_ignore).setOnClickListener(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ImNewListAdapter();
        mAdapter.addChildClickViewIds(R.id.right,R.id.content);
        mAdapter.setOnItemChildClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        headView = newHeadView();
        mAdapter.addHeaderView(headView);
        initSetView();
        EventBus.getDefault().register(this);

    }

    private void initSetView() {
        if (mType == TYPE_ACTIVITY) {
            mBtnBack.setOnClickListener(this);
        } else {
            mBtnBack.setVisibility(View.INVISIBLE);
            View top = findViewById(R.id.top);
            top.setBackgroundColor(0xfff9fafb);
        }
    }

    /**
     * 添加头部
     */
    private View newHeadView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_im_list_head,null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(60)));
        mBtnSystemMsg = view.findViewById(R.id.btn_system_msg);
        mBtnSystemMsg.setOnClickListener(this);
        mSystemMsgRedPoint = view.findViewById(R.id.red_point);
        mSystemMsgContent = view.findViewById(R.id.msg);
        mSystemTime = view.findViewById(R.id.time);
        if (!CommonAppConfig.APP_IS_YUNBAO_SELF) {
            ImageView avatar = view.findViewById(R.id.avatar);
            avatar.setImageResource(CommonAppConfig.getInstance().getAppIconRes());
        }
        return  view;
    }

    public void release() {
        EventBus.getDefault().unregister(this);
        ImHttpUtil.cancel(ImHttpConsts.GET_SYSTEM_MESSAGE_LIST);
    }

    public void setLiveUid(String liveUid) {
        mLiveUid = liveUid;
    }

    public void loadData() {
        getSystemMessageList();
        datas = GreenDaoUtils.getInstance().queryAllSocketList();
        if(datas==null || datas.size()==0){
            return;
        }
        mAdapter.setList(datas);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_back) {
            if(mType==TYPE_ACTIVITY){
                ((Activity)mContext).finish();
            }
        } else if (i == R.id.btn_ignore) {
            ignoreUnReadCount();
            EventBus.getDefault().post(new ImUnReadCountEvent("0"));
        } else if (i == R.id.btn_system_msg) {
            forwardSystemMessage();
            EventBus.getDefault().post(new ImUnReadCountEvent("0"));
        }
    }

    /**
     * 前往系统消息(点击headView)
     */
    private void forwardSystemMessage() {
        SpUtil.getInstance().setBooleanValue(SpUtil.HAS_SYSTEM_MSG, false);
        if (mSystemMsgRedPoint != null && mSystemMsgRedPoint.getVisibility() == View.VISIBLE) {
            mSystemMsgRedPoint.setVisibility(View.INVISIBLE);
        }
        if (mType == TYPE_ACTIVITY) {
            SystemMessageActivity.forward(mContext);
        } else {
            SystemMessageDialogFragment fragment = new SystemMessageDialogFragment();
            fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "SystemMessageDialogFragment");
        }
    }

    /**
     * 忽略未读
     */
    private void ignoreUnReadCount() {
        SpUtil.getInstance().setBooleanValue(SpUtil.HAS_SYSTEM_MSG, false);
        if (mSystemMsgRedPoint != null && mSystemMsgRedPoint.getVisibility() == View.VISIBLE) {
            mSystemMsgRedPoint.setVisibility(View.INVISIBLE);
        }
        ImMessageUtil.getInstance().markAllConversationAsRead();
        if (mAdapter != null) {
            //去掉它的红点点
            List<SocketListBean> socketListBeans = GreenDaoUtils.getInstance().queryAllSocketList();
            if(socketListBeans==null){
                return;
            }
            for (int i = 0; i < socketListBeans.size(); i++) {
                socketListBeans.get(i).setUnReadCount(0);
                GreenDaoUtils.getInstance().updateSocketListData(socketListBeans.get(i));
            }
            if(mType==TYPE_ACTIVITY){
                for (int i = 0; i < datas.size(); i++) {
                    datas.get(i).setUnReadCount(0);
                }
                mAdapter.notifyDataSetChanged();
            }
            EventBus.getDefault().post(new SocketListReFreshEvent());
        }
        ToastUtil.show(R.string.im_msg_ignore_unread_2);
    }

    /**
     * 获取系统消息
     */
    private void getSystemMessageList() {
        ImHttpUtil.getSystemMessageList(1, mSystemMsgCallback);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSystemMsgEvent(SystemMsgEvent e) {
        getSystemMessageList();
    }

   private HttpCallback  mSystemMsgCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                SystemMessageBean bean = JSON.parseObject(info[0], SystemMessageBean.class);
                if (mSystemMsgContent != null) {
                    mSystemMsgContent.setText(bean.getContent());
                }
                if (mSystemTime != null) {
                    mSystemTime.setText(bean.getAddtime());
                }
                if (SpUtil.getInstance().getBooleanValue(SpUtil.HAS_SYSTEM_MSG)) {
                    if (mSystemMsgRedPoint != null && mSystemMsgRedPoint.getVisibility() != View.VISIBLE) {
                        mSystemMsgRedPoint.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    };


    /**
     * 进入聊天消息界面（此处去掉了输入框）
     */
    public void openChatRoomWindow(UserBean userBean, boolean following,int position) {
        if(mType==TYPE_ACTIVITY){
            datas.get(position).setUnReadCount(0);
            mAdapter.notifyDataSetChanged();
            LiveChatRoomActivity.forward(mContext,userBean,following);
        }else {
           EventBus.getDefault().post(new SocketListClickEvent(userBean));
        }
    }
    public void refreshData() {
        List<SocketListBean> socketListBeans = GreenDaoUtils.getInstance().queryAllSocketList();
        if(datas==null){
            datas=new ArrayList<>();
        }
        datas.clear();
        datas.addAll(socketListBeans);
        if(mAdapter!=null){
            mAdapter.setList(datas);
        }
    }

    @Override
    public void onItemChildClick(@NonNull final BaseQuickAdapter adapter, @NonNull View view, final int position) {
        if(view.getId()==R.id.right){
            DialogUitl.showSimpleDialog(mContext, "温馨提示", "确认删除该条消息吗？", false, new DialogUitl.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    GreenDaoUtils.getInstance().deleteSocketMessageAccordField(datas.get(position).getUserId());
                    GreenDaoUtils.getInstance().deleteSocketListData(datas.get(position).getUserId());
                    adapter.remove(position);
                }
            });
        }else if(view.getId()==R.id.content){
            UserBean userBean=new UserBean();
            userBean.setId(datas.get(position).getUserId());
            userBean.setAvatar(datas.get(position).getAvatar());
            userBean.setUserNiceName(datas.get(position).getNickname());

            openChatRoomWindow(userBean,true,position);
        }
    }

    /**
     * 获取系统消息是不是有红点
     */
    public  boolean  getPointVisible(){
        if(mSystemMsgRedPoint!=null){
            if(mSystemMsgRedPoint.getVisibility()==View.VISIBLE){
                return  true;
            }else {
                return  false;
            }
        }
        return false;
    }
}
