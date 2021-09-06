package com.yunbao.live.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveActivity;

/**
 * Created by cxf on 2018/10/9.
 */

public abstract class AbsLiveViewHolder extends AbsViewHolder implements View.OnClickListener {

    private TextView mRedPoint;

    private boolean isClickChat;

    public AbsLiveViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public AbsLiveViewHolder(Context context, ViewGroup parentView,Object... args) {
        super(context, parentView,args);
    }

    @Override
    public void init() {
        findViewById(R.id.btn_chat).setOnClickListener(this);
        if(findViewById(R.id.btn_msg)!=null){
            findViewById(R.id.btn_msg).setOnClickListener(this);
        }
        mRedPoint = (TextView) findViewById(R.id.red_point);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_msg) {
//            ((LiveActivity) mContext).openChatListWindow();
            ((LiveActivity) mContext).openChatRoomWindow(CommonAppConfig.getInstance().getUserBean(),true);
        } else if (i == R.id.btn_chat) {
            ((LiveActivity) mContext).openChatWindow();
            isClickChat=true;
        }
    }

    public void setUnReadCount(String unReadCount) {
        if (mRedPoint != null) {
            if ("0".equals(unReadCount)) {
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mRedPoint.getVisibility() != View.VISIBLE) {
                    mRedPoint.setVisibility(View.VISIBLE);
                }
            }
            mRedPoint.setText(unReadCount);
        }
    }

    public boolean isClickChat() {
        return isClickChat;
    }
}
