package com.yunbao.common.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ImageView;


public class AnimatorUtil {

    private  AnimationDrawable animationDrawable;

    public AnimatorUtil() {
    }

    /**
     * 帧动画
     */
    @SuppressLint("WrongConstant")
    public    void  FrameAnimation(View view) {
        if(animationDrawable!=null){
            animationDrawable.stop();
            animationDrawable=null;
        }
        animationDrawable = (AnimationDrawable) ((ImageView)view).getDrawable();
        if(animationDrawable==null){
            return;
        }
        //判断是否在运行
        if(!animationDrawable.isRunning()){
            //开启帧动画
            animationDrawable.setOneShot(true); //为true时 转一次  停留在最后一帧
            animationDrawable.start();
        }else {
            animationDrawable.selectDrawable(0);//暂停时留在第一帧
            animationDrawable.stop();
        }
    }
    /**
     * 帧动画
     */
    @SuppressLint("WrongConstant")
    public    void  FrameNoOneAnimation(View view) {
        if(animationDrawable!=null){
            animationDrawable.stop();
            animationDrawable=null;
        }
        animationDrawable = (AnimationDrawable) ((ImageView)view).getDrawable();
        //判断是否在运行
        if(!animationDrawable.isRunning()){
            //开启帧动画
            animationDrawable.setOneShot(false); //为true时 转一次  停留在最后一帧
            animationDrawable.start();
        }else {
            animationDrawable.selectDrawable(0);//暂停时留在第一帧
            animationDrawable.stop();
        }
    }

    @SuppressLint("WrongConstant")
    public void  objectAnimation(final View view) {
        ObjectAnimator  objectAnimator1 = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0f);
        objectAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float h = (float)valueAnimator.getAnimatedValue();
                if(h==0.0){
                    view.setVisibility(View.INVISIBLE);
                }
            }
        });
        objectAnimator1.setDuration(900);
        objectAnimator1.setRepeatMode(Animation.RESTART);
        objectAnimator1.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator1.start();
    }
}