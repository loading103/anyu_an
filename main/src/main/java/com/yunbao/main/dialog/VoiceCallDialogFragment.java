package com.yunbao.main.dialog;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yunbao.common.Constants;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.main.R;
import com.yunbao.main.event.CallEvent;
import com.yunbao.main.interfaces.MainStartChooseCallback;
import com.yunbao.main.presenter.CheckLivePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 语音通话
 * Created by cxf on 2018/9/29.
 */

public class VoiceCallDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private MainStartChooseCallback mCallback;
    private LiveBean liveBean;
    private CheckLivePresenter mCheckLivePresenter;
    private int position;
    private CountDownTimer timer;
    private MediaPlayer mediaPlayer;
    private Vibrator mVibrator;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_voice_call;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    protected void setWindowAttributes(Window window) {
//        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        mRootView.findViewById(R.id.ll_cancer).setOnClickListener(this);
        mRootView.findViewById(R.id.ll_accept).setOnClickListener(this);
        TextView tvName =mRootView.findViewById(R.id.tv_name);
        ImageView ivHead =mRootView.findViewById(R.id.iv_head);
        tvName.setText(liveBean.getUserNiceName());
        Glide.with(mContext).load(liveBean.getThumb())
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(IMDensityUtil.dip2px(mContext,5),0)))
                .into(ivHead);

        countDown();
        SpUtil.getInstance().setBooleanValue(SpUtil.NEW_USER,false);

    }

    private void initVibrateSong() {
        long[] patter = {1000, 3000};
        mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        AudioAttributes audioAttributes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//解决android 10 震动bug
            audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM) //key
                    .build();
            mVibrator.vibrate(patter, 1, audioAttributes);
        }else {
            mVibrator.vibrate(patter, 1);
        }

        mediaPlayer= MediaPlayer.create(getContext(), R.raw.call);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });

    }

    public void setMainStartChooseCallback(MainStartChooseCallback callback) {
        mCallback = callback;
    }

    public void setBean(LiveBean liveBean){
        this.liveBean = liveBean;
    }

    @Override
    public void onClick(View v) {
        if(!canClick()){
            return;
        }
        int i = v.getId();
        if (i == R.id.ll_cancer) {
            SpUtil.getInstance().setBooleanValue(SpUtil.NEW_USER,false);
            dismissAllowingStateLoss();
        } else if (i == R.id.ll_accept) {
//            if (mCallback != null) {
//                mCallback.onLiveClick();
//            }
            SpUtil.getInstance().setBooleanValue(SpUtil.NEW_USER,true);
            SpUtil.getInstance().setBooleanValue(SpUtil.NEW_USER_CALL,true);
            watchLive(liveBean,Constants.LIVE_RECOMMEND,position);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(getDialog()!=null){
                        dismissAllowingStateLoss();
                    }
                }
            },500);

        }
    }

    /**
     * 观看直播
     */
    public void watchLive(LiveBean liveBean, String key, int position) {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        mCheckLivePresenter.watchLive(liveBean, key, position);
    }

    public void setPosition(int position) {
        this.position=position;
    }

    @Override
    public void onStart() {
        super.onStart();
        initVibrateSong();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
        if(mVibrator!=null){
            mVibrator.cancel();
            mVibrator=null;
        }

        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    @Override
    public void onDestroy() {
        if(timer!=null){
            timer.cancel();
            timer=null;
        }

        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 通话状态
     * @param e
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallEvent(final CallEvent e) {
        switch (e.getStatus()){
            case 0:
            case 1:
                if(mVibrator!=null){
                    mVibrator.cancel();
                    mVibrator=null;
                }
                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer=null;
                }
                break;
            case 2:
                initVibrateSong();
                break;
        }
    }

    /**
     * 倒计时
     */
    private void countDown() {
        if(timer==null){
            timer = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    SpUtil.getInstance().setBooleanValue(SpUtil.NEW_USER,false);
                    if(getDialog()!=null){
                        getDialog().dismiss();
                    }
                }
            }.start();
        }
    }
}
