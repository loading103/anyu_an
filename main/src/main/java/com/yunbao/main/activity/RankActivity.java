package com.yunbao.main.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.live.activity.LiveAddImpressActivity;
import com.yunbao.main.R;
import com.yunbao.main.event.HeadUpdateEvent;
import com.yunbao.main.views.MainListViewHolder;
import com.yunbao.main.views.UserHomeViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 排行榜
 */
public class RankActivity extends AbsActivity {

    private MainListViewHolder mMainListViewHolder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_empty;
    }

    @Override
    protected void main() {
        mMainListViewHolder = new MainListViewHolder(mContext, (ViewGroup) findViewById(R.id.container));
        mMainListViewHolder.addToParent();
        mMainListViewHolder.subscribeActivityLifeCycle();
        mMainListViewHolder.loadData();
    }




    @Override
    protected void onDestroy() {
        if (mMainListViewHolder != null) {
            mMainListViewHolder.release();
        }
        super.onDestroy();
    }
}
