package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.live.utils.LiveStorge;
import com.yunbao.main.R;
import com.yunbao.main.adapter.MainHomeFollowAdapter;
import com.yunbao.main.event.LiveEndEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.presenter.CheckLivePresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/10/27.
 */

public class LiveClassActivity extends AbsActivity implements OnItemClickListener<LiveBean> {

    private int mClassId;
    private CommonRefreshView mRefreshView;
    private MainHomeFollowAdapter mAdapter;
    private CheckLivePresenter mCheckLivePresenter;

    public static void forward(Context context, int classId, String className) {
        Intent intent = new Intent(context, LiveClassActivity.class);
        intent.putExtra(Constants.CLASS_ID, classId);
        intent.putExtra(Constants.CLASS_NAME, className);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_class;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        mClassId = intent.getIntExtra(Constants.CLASS_ID, -1);
        if (mClassId < 0) {
            return;
        }
        String liveClassName = intent.getStringExtra(Constants.CLASS_NAME);
        setTitle(liveClassName);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live_class);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveBean>() {
            @Override
            public RefreshAdapter<LiveBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeFollowAdapter(mContext);
                    mAdapter.setOnItemClickListener(LiveClassActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getClassLive(mClassId, p, callback);
            }

            @Override
            public List<LiveBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), LiveBean.class);
            }

            @Override
            public void onRefreshSuccess(List<LiveBean> list, int listCount) {
                LiveStorge.getInstance().put(Constants.LIVE_CLASS_PREFIX + mClassId, list);
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<LiveBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    @Override
    public void onItemClick(LiveBean bean, int position) {
        watchLive(bean, position);
    }


    /**
     * 观看直播
     */
    public void watchLive(LiveBean liveBean, int position) {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        mCheckLivePresenter.watchLive(liveBean, Constants.LIVE_CLASS_PREFIX + mClassId, position);
    }

    @Override
    protected void onDestroy() {
        LiveStorge.getInstance().remove(Constants.LIVE_CLASS_PREFIX + mClassId);
        MainHttpUtil.cancel(MainHttpConsts.GET_CLASS_LIVE);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRefreshView.initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveEndEvent(final LiveEndEvent e) {
        List<LiveBean> list = ((List<LiveBean>) mAdapter.getList());
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getUid().equals(e.getBean().getUid())){
                mRefreshView.initData();
            }
        }
    }
}
