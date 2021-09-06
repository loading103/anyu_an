package com.yunbao.common.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.R;
import com.yunbao.common.bean.TabBarBean;
import com.yunbao.common.dialog.VideoClauseDialog;
import com.yunbao.common.event.ShowXieYiEvent;
import com.yunbao.common.event.UnClickEvent;
import com.yunbao.common.utils.AnimatorUtil;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.SpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 */

public class TabButtonGroup extends LinearLayout implements View.OnClickListener {

    private List<TabButton> mList;
    private ViewPager mViewPager;
    private int mCurPosition;
    private Runnable mRunnable;
    private View mAnimView;
    private ValueAnimator mAnimator;
    private AnimatorUtil animatorUtil;
    private List<TabBarBean.ListBean> datas;

    public TabButtonGroup(Context context) {
        this(context, null);
    }

    public TabButtonGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabButtonGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        animatorUtil = new AnimatorUtil();
        mList = new ArrayList<>();
        mCurPosition =-1;

        mRunnable=new Runnable() {
            @Override
            public void run() {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(mCurPosition, false);
                }
            }
        };
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View v = getChildAt(i);
            v.setTag(i);
            v.setOnClickListener(this);
            mList.add((TabButton) v);
        }
    }

    @Override
    public void onClick(View v) {
        if(!ClickUtil.canClick()){
            return;
        }
        Object tag = v.getTag();
        if (tag != null) {
            int position = (int) tag;
            if (position == mCurPosition) {
                return;
            }
            //游客点击部分界面直接进去登陆界面
            if(isVisitorCan(position)){
                return;
            }
            //点击视频和充值有相关协议
            if(isVideoAndCharge(position)){
                return;
            }
            mList.get(mCurPosition).setChecked(false);
            TabButton tbn = mList.get(position);
            tbn.setChecked(true);
            mCurPosition = position;
            animatorUtil.FrameAnimation(tbn.getmImg());
            postDelayed(mRunnable,150);
        }
    }



    public void  setViewger(int position){
        mList.get(mCurPosition).setChecked(false);
        TabButton tbn = mList.get(position);
        tbn.setChecked(true);
        mCurPosition = position;
        animatorUtil.FrameAnimation(tbn.getmImg());
        postDelayed(mRunnable,150);
    }
    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
    }

    public void cancelAnim() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    public void setCheck(int position) {
        if (position == mCurPosition) {
            return;
        }
        mList.get(mCurPosition).setChecked(false);
        TabButton tbn = mList.get(position);
        tbn.setChecked(true);
        mCurPosition = position;
        mAnimView = tbn;
        if(mAnimator!=null){
            mAnimator.start();
            postDelayed(mRunnable,150);
        }
    }
    public void setCheckeded(int position) {
        if (position == mCurPosition) {
            return;
        }
        if(mCurPosition!=-1){
            mList.get(mCurPosition).setChecked(false);
        }
        TabButton tbn = mList.get(position);
        tbn.setChecked(true);
        mCurPosition = position;
        mAnimView = tbn;
        animatorUtil.FrameAnimation(tbn.getmImg());
        postDelayed(mRunnable,150);
    }
    public void setCheckeded(int position,boolean  isfirst) {
        if (position == mCurPosition) {
            return;
        }
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setChecked(false);
        }
        TabButton tbn = mList.get(position);
        tbn.setChecked(true);
        mCurPosition = position;
        mAnimView = tbn;
        animatorUtil.FrameAnimation(tbn.getmImg());
        postDelayed(mRunnable,150);
    }
    public void setData(List<TabBarBean.ListBean> datas) {
        this.datas=datas;
    }

    public   boolean  isVisitorCan(int position){
        if(CommonAppConfig.getInstance().isVisitorLogin()){
            if(position>=datas.size()){
                EventBus.getDefault().post(new UnClickEvent());
                return true;
            }
            if(datas.get(position).getId().equals("1") || datas.get(position).getId().equals("2") || datas.get(position).getId().equals("3")){
                return false;
            }else {
                EventBus.getDefault().post(new UnClickEvent());
                return true;
            }
        }
        return false;
    }

    /**
     * 如果是视屏和充值，需要弹出协议
     */
    private boolean isVideoAndCharge(int position) {
        if(datas==null || datas.size()==0){
            return false;
        }
        //如果是视频，且协议开关打开，再判断是不是同意了协议
        if(datas.get(position).getId().equals("4")  && datas.get(position).getPrompt_switch().equals("1")){
            boolean showVideoed = SpUtil.getInstance().getBooleanValue(SpUtil.VIDEO_XIEYI);
            if(TextUtils.isEmpty(datas.get(position).getPrompt_content())){
                return false;
            }
            if(!showVideoed){   //显示过了就不显示
                showVideoXieYi(datas.get(position).getPrompt_content(),true,position);
                return  true;
            }else {
                return false;
            }
        }
        if(datas.get(position).getId().equals("6") && datas.get(position).getPrompt_switch().equals("1")){
            boolean showVideoed = SpUtil.getInstance().getBooleanValue(SpUtil.RECHARGE_XIEYI);
            if(TextUtils.isEmpty(datas.get(position).getPrompt_content())){
                return false;
            }
            if(!showVideoed){   //显示过了就不显示
                showVideoXieYi(datas.get(position).getPrompt_content(),false,position);
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

    private void showVideoXieYi(String json, final boolean isVideo,final int position) {
        final VideoClauseDialog clauseDialog=new VideoClauseDialog(getContext(), json);
        clauseDialog.setCommitClick(new VideoClauseDialog.CommitListener() {
            @Override
            public void onCommitClick(boolean isCommit) {
                if(isCommit){
                    if(isVideo){
                        SpUtil.getInstance().setBooleanValue(SpUtil.VIDEO_XIEYI,true);
                    }else {
                        SpUtil.getInstance().setBooleanValue(SpUtil.RECHARGE_XIEYI,true);
                    }
                    mList.get(mCurPosition).setChecked(false);
                    TabButton tbn = mList.get(position);
                    tbn.setChecked(true);
                    mCurPosition = position;
                    animatorUtil.FrameAnimation(tbn.getmImg());
                    postDelayed(mRunnable,150);
                    clauseDialog.dismiss();
                }else {
                    clauseDialog.dismiss();
                }
            }
        });
        clauseDialog.showNoticeDiglog();
        clauseDialog.showDialog();
    }
}
