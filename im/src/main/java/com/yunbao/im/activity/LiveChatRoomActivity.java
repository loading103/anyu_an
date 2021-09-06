package com.yunbao.im.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.im.R;
import com.yunbao.im.views.LiveChatRoomDialogViewHolder1;

/**
 * Created by cxf on 2018/10/24.
 */

public class LiveChatRoomActivity extends AbsActivity  {
    private LiveChatRoomDialogViewHolder1 mChatRoomViewHolder;
    public static void forward(Context context, UserBean userBean, boolean following) {
        Intent intent = new Intent(context, LiveChatRoomActivity.class);
        intent.putExtra(Constants.USER_BEAN, userBean);
        intent.putExtra(Constants.FOLLOW, following);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_chat_room1;
    }
    @Override
    protected void main() {
        FrameLayout container= findViewById(R.id.container);
        Intent intent = getIntent();
        UserBean userBean = (UserBean)intent.getParcelableExtra(Constants.USER_BEAN);
        if (userBean == null) {
            return;
        }
        boolean following = intent.getBooleanExtra(Constants.FOLLOW,false);
        mChatRoomViewHolder = new LiveChatRoomDialogViewHolder1(mContext,  container, userBean, following);
        mChatRoomViewHolder.addToParent();
        mChatRoomViewHolder.loadData();
    }

}
