package com.yunbao.main.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.main.bean.BonusBean;
import com.yunbao.main.dialog.ClauseDialog;
import com.yunbao.main.http.MainHttpUtil;

import java.util.List;

/**
 * Created by cxf on 2018/9/30.
 * 每日签到
 */

public class NewBonusViewHolder extends AbsDialogFragment implements View.OnClickListener {

    private View mBg;
    private View mDialog;
    private TextView mDayView;
    private ImageView btn_close;
    private View mResult;
    private View mImg1;
    private View mImg2;
    private View mImgBg;
    private View mBtnSign;
    private List<BonusBean> mList;
    private int mDay;//今天是一周的第几天
    private JSONObject jsonObject;
    private String    continueDay;
    private ImageView[] mItemViews;
    private TextView[] mItemTvs;
    private CloseListener mCloseListener;
    private String danwei;//单位
    private ClauseDialog clauseDialog;
    public boolean  isClicked=false;


    public NewBonusViewHolder(List<BonusBean> list, int day, String continueDay, JSONObject obj) {
        this.mList = list;
        this.mDay =  day-1;
        this.jsonObject = obj;
        this.continueDay = continueDay;

    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_new_bonus;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.ActionSheetDialogAnimations);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        //状态栏变色问题
        window.setLayout((ViewGroup.LayoutParams.MATCH_PARENT), ScreenUtils.getScreenHeight()-  BarUtils.getStatusBarHeight());
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    public void init() {
        mBg = findViewById(R.id.bg);
        mDialog = findViewById(R.id.dialog);
        mDayView = (TextView) findViewById(R.id.day);
        mItemViews = new ImageView[7];
        mItemViews[0] = (ImageView) findViewById(R.id.btn_day_1_signed);
        mItemViews[1] = (ImageView) findViewById(R.id.btn_day_2_signed);
        mItemViews[2] = (ImageView) findViewById(R.id.btn_day_3_signed);
        mItemViews[3] = (ImageView) findViewById(R.id.btn_day_4_signed);
        mItemViews[4] = (ImageView) findViewById(R.id.btn_day_5_signed);
        mItemViews[5] = (ImageView) findViewById(R.id.btn_day_6_signed);
        mItemViews[6] = (ImageView) findViewById(R.id.btn_day_7_signed);
        mItemTvs = new TextView[7];
        mItemTvs[0] = (TextView) findViewById(R.id.tv_add_1);
        mItemTvs[1] = (TextView) findViewById(R.id.tv_add_2);
        mItemTvs[2] = (TextView) findViewById(R.id.tv_add_3);
        mItemTvs[3] = (TextView) findViewById(R.id.tv_add_4);
        mItemTvs[4] = (TextView) findViewById(R.id.tv_add_5);
        mItemTvs[5] = (TextView) findViewById(R.id.tv_add_6);
        mItemTvs[6] = (TextView) findViewById(R.id.tv_add_7);
        mResult = findViewById(R.id.result);
        mImg1 = findViewById(R.id.img_1);
        mImg2 = findViewById(R.id.img_2);
        mImgBg = findViewById(R.id.img_bg);
        btn_close= (ImageView) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(this);
        mBtnSign = findViewById(R.id.btn_sign);
        mBtnSign.setOnClickListener(this);
        danwei = CommonAppConfig.getInstance().getConfig().getCoinName();
        setData();
        initClause();

    }

    private void initClause() {
        clauseDialog=new ClauseDialog(mContext, jsonObject.getString("bonus_terms_content"));
        clauseDialog.setCommitClick(new ClauseDialog.CommitListener() {
            @Override
            public void onCommitClick(boolean isCommit) {
                if(isCommit){
                    getBonus();
                }else {
                    clauseDialog.dismiss();
                    dismiss();
                }
            }
        });
        clauseDialog.showNoticeDiglog();
    }

    public void setData() {
        String s = WordUtil.getString(R.string.bonus_sign_1) + "<font color='#ffdd00'>" + continueDay + "</font>" + WordUtil.getString(R.string.bonus_day);
        mDayView.setText(Html.fromHtml(s));
        for (int i = 0; i < mList.size(); i++) {
            mItemTvs[i].setText("+"+mList.get(i).getCoin()+danwei);
        }
        for (int i = 0, length = mItemViews.length; i < length; i++) {
            if (i <= mDay - 1) {
                mItemViews[i].setVisibility(View.VISIBLE);
            } else {
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mImgBg != null) {
            mImgBg.clearAnimation();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_close) {
            mDialog.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity)mContext).showKnowNotice(jsonObject);
                }
            },1100);
            dismissAllowingStateLoss();
            if (mCloseListener != null) {
                mCloseListener.onCancelClick();
            }
        } else if (i == R.id.btn_sign) {
                if(jsonObject.getIntValue("bonus_terms_switch")==1 && clauseDialog!=null){
                    clauseDialog.showDialog();
                    isClicked=true;
                    getDialog().hide();
                }else {
                    getBonus();
            }
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        if(isClicked&&getDialog()!=null){
            getDialog().hide();
        }
    }

    /**
     * 获取签到奖励
     */
    private boolean isfirstClick=true;
    private void getBonus() {
        MainHttpUtil.getBonus(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                Log.e("点击签到-------------",code+ "msg:"+msg);
                if(!isfirstClick){
                    return;
                }
                isfirstClick=false;
                if (code == 0) {
                    if(clauseDialog!=null){
                        clauseDialog.dismiss();
                    }
                    if(getDialog()!=null){
                        getDialog().show();
                    }
                    playSuccessAnim();
                } else {
                    ToastUtil.show(msg);
                }
                mDialog.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCloseListener != null) {
                            mCloseListener.onCancelClick();
                        }
                    }
                },1100);

            }
        });
    }
    /**
     * 播放签到成功动画
     */
    private void playSuccessAnim() {
        mDialog.setVisibility(View.INVISIBLE);
        btn_close.setVisibility(View.INVISIBLE);
        mResult.setVisibility(View.VISIBLE);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(1100);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        mImgBg.startAnimation(rotateAnimation);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.5f, 1.15f, 1f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                mImg1.setScaleX(v);
                mImg1.setScaleY(v);
                mImg2.setScaleX(v);
                mImg2.setScaleY(v);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showCoin();
            }
        });
        valueAnimator.setDuration(1200);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();
    }

    private void showCoin() {
        if (mImgBg != null) {
            mImgBg.clearAnimation();
        }
        if (mBg != null && mImg1 != null && mImg2 != null ) {
            playHideAnim();
            ((MainActivity)mContext).showKnowNotice(jsonObject);
        }
    }

    private void playHideAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0.2f);
        valueAnimator.setDuration(1500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                mBg.setAlpha(1 - animation.getAnimatedFraction());
                mImg1.setScaleX(v);
                mImg1.setScaleY(v);
                mImg2.setScaleX(v);
                mImg2.setScaleY(v);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                dismiss();
                dismissAllowingStateLoss();
            }
        });
        valueAnimator.start();
    }

    public interface CloseListener {
        void onCancelClick();
    }
}
