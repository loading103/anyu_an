package com.yunbao.im.views;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yunbao.common.bean.UserBean;
import com.yunbao.common.greendao.GreenDaoUtils;
import com.yunbao.common.greendao.entity.SocketMessageBean;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.im.R;
import com.yunbao.im.adapter.ChatImRoomAdapter;

import java.util.List;


/**
 * Created by cxf on 2018/10/24.
 */

public class LiveChatRoomDialogViewHolder1 extends AbsViewHolder implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private TextView mTitleView;
    private UserBean mUserBean;
    private String mToUid;
    private boolean mFollowing;
    private View mFollowGroup;
    private ChatImRoomAdapter chatRoomAdapter;
    private List<SocketMessageBean> datas;

    public LiveChatRoomDialogViewHolder1(Context context, ViewGroup parentView, UserBean userBean, boolean following) {
        super(context, parentView, userBean, following);
    }

    @Override
    protected void processArguments(Object... args) {
        mUserBean = (UserBean) args[0];
        mFollowing = (boolean) args[1];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_room_3;
    }

    @Override
    public void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mTitleView = (TextView) findViewById(R.id.titleView);
        mFollowGroup = findViewById(R.id.btn_follow_group);
        if (!mFollowing) {
            mFollowGroup.setVisibility(View.VISIBLE);
            mFollowGroup.findViewById(R.id.btn_close_follow).setOnClickListener(this);
            mFollowGroup.findViewById(R.id.btn_follow).setOnClickListener(this);
        }
        findViewById(R.id.btn_back).setOnClickListener(this);
    }

    public void loadData() {
        if (mUserBean == null) {
            return;
        }
        mToUid = mUserBean.getId();
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mTitleView.setText(mUserBean.getUserNiceName());
        datas = GreenDaoUtils.getInstance().querySocketMessageDataAccordField(mToUid);
        if(datas==null || datas.size()==0){
            return;
        }
        chatRoomAdapter =new ChatImRoomAdapter(mContext,datas,mUserBean);
        mRecyclerView.setAdapter(chatRoomAdapter);
        mRecyclerView.scrollToPosition(datas.size()-1);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_back) {
            ((Activity)mContext).finish();

        } else if (i == R.id.btn_close_follow) {
            closeFollow();

        } else if (i == R.id.btn_follow) {
            follow();

        }
    }

    /**
     * 关闭关注提示
     */
    private void closeFollow() {
        if (mFollowGroup != null && mFollowGroup.getVisibility() == View.VISIBLE) {
            mFollowGroup.setVisibility(View.GONE);
        }
    }

    /**
     * 关注
     */
    private void follow() {
        CommonHttpUtil.setAttention(mToUid, null);
    }
    /**
     * 释放资源
     */
    public void release() {
    }
}
