package com.yunbao.im.activity;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.greendao.entity.SocketListBean;
import com.yunbao.im.R;
import com.yunbao.im.bean.ImUserBean;
import com.yunbao.im.views.ChatListViewHolder;
import com.yunbao.im.views.ChatNewListViewHolder;

/**
 * Created by cxf on 2018/10/24.
 */

public class ChatActivity extends AbsActivity {

    private ChatNewListViewHolder mChatListViewHolder;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, ChatActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_list;
    }

    @Override
    protected void main() {
        mChatListViewHolder = new ChatNewListViewHolder(mContext, (ViewGroup) findViewById(R.id.root),ChatListViewHolder.TYPE_ACTIVITY);
        mChatListViewHolder.addToParent();
        mChatListViewHolder.loadData();
    }

    @Override
    protected void onDestroy() {
        if (mChatListViewHolder != null) {
            mChatListViewHolder.release();
        }
        super.onDestroy();
    }
}
