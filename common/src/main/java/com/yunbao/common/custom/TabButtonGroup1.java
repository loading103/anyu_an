package com.yunbao.common.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.yunbao.common.utils.AnimatorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 */

public class TabButtonGroup1 extends LinearLayout implements View.OnClickListener {

    private List<TabButton> mList;
    private ViewPager mViewPager;
    private int mCurPosition;
    private ValueAnimator mAnimator;
    private View mAnimView;
    private Runnable mRunnable;
    private AnimatorUtil animatorUtil;

    public TabButtonGroup1(Context context) {
        this(context, null);
    }

    public TabButtonGroup1(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabButtonGroup1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        animatorUtil = new AnimatorUtil();
        mList = new ArrayList<>();
        mCurPosition = 0;
//        mAnimator = ValueAnimator.ofFloat(0.9f, 1.3f, 1f);
//        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float v = (float) animation.getAnimatedValue();
//                if (mAnimView != null) {
//                    mAnimView.setScaleX(v);
//                    mAnimView.setScaleY(v);
//                }
//            }
//        });
//        mAnimator.setDuration(300);
//        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//        mAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mAnimView = null;
//            }
//        });



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
        Object tag = v.getTag();
        if (tag != null) {
            int position = (int) tag;
            if (position == mCurPosition) {
                return;
            }
            mList.get(mCurPosition).setChecked(false);
            TabButton tbn = mList.get(position);
            tbn.setChecked(true);
            mCurPosition = position;
//            if(mAnimView!=null){
//                float scaleX = mAnimView.getScaleX();
//                float scaleY = mAnimView.getScaleY();
//                mAnimView.setScaleX(1f);
//                mAnimView.setScaleY(1f);
//                mAnimView=null;
//            }
//            mAnimView = tbn;
//            mAnimator.start();

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
        mAnimator.start();
        postDelayed(mRunnable,150);
    }
}
