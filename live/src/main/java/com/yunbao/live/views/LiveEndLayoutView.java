package com.yunbao.live.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.jpush.Gson;
import com.google.gson.jpush.reflect.TypeToken;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.live.bean.EndBean;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;

import java.util.Arrays;
import java.util.List;


public class LiveEndLayoutView extends LinearLayout implements View.OnClickListener {
    private ImageView mAvatar1;
    private ImageView mAvatar2;
    private TextView mName;
    private TextView mDuration;//直播时长
    private TextView mVotes;//收获映票
    private TextView mWatchNum;//观看人数

    public LiveEndLayoutView(Context context) {
        this(context,null);
    }

    public LiveEndLayoutView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public LiveEndLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    /**
     * 初始化界面元素
     */
    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_live_end,this);
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


    public void showData(final LiveBean liveBean, String stream) {
        try {
            if (liveBean != null) {
                mName.setText(liveBean.getUserNiceName());
                L.e("WOLF","直播结束："+ liveBean.getAvatar());
                ImgLoader.displayBlur(CommonAppContext.sInstance, liveBean.getAvatar(), mAvatar1);
                ImgLoader.displayAvatar(CommonAppContext.sInstance, liveBean.getAvatar(), mAvatar2);
            }
            //直播已结束  只有第一次请求数据  来回上下滑动不用请求数据
            String booleanValue = SpUtil.getInstance().getStringValue(SpUtil.END_LIVE + liveBean.getStream());
            if(!TextUtils.isEmpty(booleanValue)){
                return;
            }

            LiveHttpUtil.getLiveEndInfo(stream, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        SpUtil.getInstance().setStringValue(SpUtil.END_LIVE + liveBean.getStream(), Arrays.toString(info));
                        JSONObject obj = JSON.parseObject(info[0]);
                        mVotes.setText(obj.getString("votes"));
                        mDuration.setText(obj.getString("length"));
                        mWatchNum.setText(StringUtil.toWan(obj.getLongValue("nums")));
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        if (getContext() instanceof LiveAnchorActivity) {
            ((LiveAnchorActivity) getContext()).superBackPressed();
        } else if (getContext() instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) getContext()).exitLiveRoom();
        }
    }

    public void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.GET_LIVE_END_INFO);
    }

}
