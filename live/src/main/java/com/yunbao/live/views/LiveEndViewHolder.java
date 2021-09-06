package com.yunbao.live.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;

/**
 * Created by cxf on 2018/10/9.
 */

public class LiveEndViewHolder extends AbsViewHolder implements View.OnClickListener {

    private ImageView mAvatar1;
    private ImageView mAvatar2;
    private TextView mName;
    private TextView mDuration;//直播时长
    private TextView mVotes;//收获映票
    private TextView mWatchNum;//观看人数

    public LiveEndViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_end;
    }

    @Override
    public void init() {
        mAvatar1 = (ImageView) findViewById(R.id.avatar_1);
        mAvatar2 = (ImageView) findViewById(R.id.avatar_2);
        mName = (TextView) findViewById(R.id.name);
        mDuration = (TextView) findViewById(R.id.duration);
        mVotes = (TextView) findViewById(R.id.votes);
        mWatchNum = (TextView) findViewById(R.id.watch_num);
        findViewById(R.id.btn_back).setOnClickListener(this);
        TextView votesName = (TextView) findViewById(R.id.votes_name);
        votesName.setText(WordUtil.getString(R.string.live_votes) + CommonAppConfig.getInstance().getVotesName());
    }

    /**
     * 用户端
     * @param liveBean
     * @param stream
     */
    public void showData(LiveBean liveBean, String stream) {
        LiveHttpUtil.getLiveEndInfo(stream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mVotes.setText(obj.getString("votes"));
                    mDuration.setText(obj.getString("length"));
                    mWatchNum.setText(StringUtil.toWan(obj.getLongValue("nums")));
                }
            }
        });
        if (liveBean != null) {
            mName.setText(liveBean.getUserNiceName());
            L.e("WOLF","直播结束："+ liveBean.getAvatar());
            ImgLoader.displayBlur(CommonAppContext.sInstance, liveBean.getAvatar(), mAvatar1);
            ImgLoader.displayAvatar(CommonAppContext.sInstance, liveBean.getAvatar(), mAvatar2);
//            Glide.with(mContext).load(liveBean.getAvatar()).into(mAvatar2);
        }

    }

    /**
     * 主播端
     * @param liveBean
     * @param info
     */
    public void showDataAnchor(LiveBean liveBean, String info) {
        if(TextUtils.isEmpty(info)){
            mVotes.setText("0");
            mDuration.setText("0");
            mWatchNum.setText("0");
        }else {
            JSONObject obj = JSON.parseObject(info);
            mVotes.setText(obj.getString("votes"));
            mDuration.setText(obj.getString("length"));
            mWatchNum.setText(StringUtil.toWan(obj.getLongValue("nums")));
        }
        if (liveBean != null) {
            mName.setText(liveBean.getUserNiceName());
            L.e("WOLF","直播结束："+ liveBean.getAvatar());
            ImgLoader.displayBlur(CommonAppContext.sInstance, liveBean.getAvatar(), mAvatar1);
            ImgLoader.displayAvatar(CommonAppContext.sInstance, liveBean.getAvatar(), mAvatar2);
//            Glide.with(mContext).load(liveBean.getAvatar()).into(mAvatar2);
        }

    }

    @Override
    public void onClick(View v) {
        if (mContext instanceof LiveAnchorActivity) {
            ((LiveAnchorActivity) mContext).superBackPressed();
        } else if (mContext instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) mContext).exitLiveRoom();
        }
    }

    @Override
    public void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.GET_LIVE_END_INFO);
    }

}
