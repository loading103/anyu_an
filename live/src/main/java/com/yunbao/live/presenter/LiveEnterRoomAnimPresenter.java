package com.yunbao.live.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.GifCacheUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ScreenDimenUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.bean.LiveChatBean;
import com.yunbao.live.bean.LiveEnterRoomBean;
import com.yunbao.live.bean.LiveUserGiftBean;
import com.yunbao.live.custom.MyCountDownTimer;
import com.yunbao.live.utils.LiveTextRender;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2018/10/11.
 * 观众进场动画效果
 */

public class LiveEnterRoomAnimPresenter {

    private Context mContext;
    private View mBg;
    private View mUserGroup;
    private ImageView mAvatar;
    private TextView mName;
    private View mStar;
    private GifImageView mGifImageView;
    private GifDrawable mGifDrawable;
    private TextView mWordText,mfollowText,mMoneyText,mNewUser,mSimiText;
    private MediaController mMediaController;//koral--/android-gif-drawable 这个库用来播放gif动画的
    private int mDp500;
    private boolean mIsAnimating;//是否在执行动画
    private boolean mIsFollowAnimating;//是否在执行动画
    private boolean mIsMoneyAnimating;//是否在执行动画
    private boolean mIsNewUserAnimating;//是否在执行动画
    private ConcurrentLinkedQueue<LiveEnterRoomBean> mQueue;
    private ConcurrentLinkedQueue<LiveChatBean> mFollowQueue;
    private ConcurrentLinkedQueue<LiveChatBean> mMoneyQueue;
    private ConcurrentLinkedQueue<LiveUserGiftBean> mNewUserQueue;
    private Handler mHandler;
    private int mScreenWidth;
    private CommonCallback<File> mDownloadGifCallback;
    private boolean mShowGif;
    private boolean mEnd;
    private final Interpolator interpolator1;
    private final Interpolator interpolator2;
    private final Interpolator interpolator3;


    public LiveEnterRoomAnimPresenter(Context context, View root) {
        mContext = context;
        mBg = root.findViewById(R.id.jg_bg);
        mUserGroup = root.findViewById(R.id.jg_user);
        mAvatar = (ImageView) root.findViewById(R.id.jg_avatar);
        mName = (TextView) root.findViewById(R.id.jg_name);
        mStar = root.findViewById(R.id.star);
        mGifImageView = (GifImageView) root.findViewById(R.id.enter_room_gif);
        mWordText = (TextView) root.findViewById(R.id.enter_room_word);
        mfollowText = (TextView) root.findViewById(R.id.follow);
        mMoneyText = (TextView) root.findViewById(R.id.money);
        mNewUser = (TextView) root.findViewById(R.id.new_user);
        mSimiText= (TextView) root.findViewById(R.id.tv_simi);
        mDp500 = DpUtil.dp2px(500);
        //初始化队列
        mQueue = new ConcurrentLinkedQueue<>();
        mFollowQueue = new ConcurrentLinkedQueue<>();
        mNewUserQueue = new ConcurrentLinkedQueue<>();
        mMoneyQueue = new ConcurrentLinkedQueue<>();
        //初始化动画效果
        interpolator1 = new AccelerateDecelerateInterpolator();
        interpolator2 = new LinearInterpolator();
        interpolator3 = new AccelerateInterpolator();

        initEnterRoomAnim();
        initFollowAnim();
        initMoneyAnim();
        initNewUserAnim();
        initSimiAnim();
    }


    /**
     * 释放资源
     */
    public void release() {
        mEnd = true;
        cancelAnim();
        mDownloadGifCallback = null;
        mHandler = null;
    }


    public void cancelAnim() {

        isshowing=false;
        CommonHttpUtil.cancel(CommonHttpConsts.DOWNLOAD_GIF);
        if(mBg!=null){
            mBg.setVisibility(View.INVISIBLE);
            mWordText.setVisibility(View.INVISIBLE);
            mUserGroup.setVisibility(View.INVISIBLE);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mTextAnimator1 != null) {
            mTextAnimator1.cancel();
        }
        if (mTextAnimator2 != null) {
            mTextAnimator2.cancel();
        }
        if (mTextAnimator3 != null) {
            mTextAnimator3.cancel();
        }
        if (mBgAnimator1 != null) {
            mBgAnimator1.cancel();
        }
        if (mBgAnimator2 != null) {
            mBgAnimator2.cancel();
        }
        if (mBgAnimator3 != null) {
            mBgAnimator3.cancel();
        }
        if (mUserAnimator1 != null) {
            mUserAnimator1.cancel();
        }
        if (mUserAnimator2 != null) {
            mUserAnimator2.cancel();
        }
        if (mUserAnimator3 != null) {
            mUserAnimator3.cancel();
        }
        if (mfollowAnimator1 != null) {
            mfollowAnimator1.cancel();
        }
        if (mfollowAnimator2 != null) {
            mfollowAnimator2.cancel();
        }
        if (mfollowAnimator3 != null) {
            mfollowAnimator3.cancel();
        }
        if (mfollowAnimator3 != null) {
            mfollowAnimator3.cancel();
        }
        if (simiAnimator1 != null) {
            simiAnimator1.cancel();
        }
        if (simiAnimator2 != null) {
            simiAnimator2.cancel();
        }
        if (simiAnimator3 != null) {
            simiAnimator3.cancel();
        }
        if (mStar != null) {
            mStar.clearAnimation();
        }
        if (mQueue != null) {
            mQueue.clear();
        }
        if (mFollowQueue != null) {
            mFollowQueue.clear();
        }
        if (mNewUserQueue != null) {
            mNewUserQueue.clear();
        }
        if (mMediaController != null) {
            mMediaController.hide();
            mMediaController.setAnchorView(null);
        }
        if (mGifImageView != null) {
            mGifImageView.setImageDrawable(null);
        }
        if (mGifDrawable != null && !mGifDrawable.isRecycled()) {
            mGifDrawable.stop();
            mGifDrawable.recycle();
            mGifDrawable = null;
        }
        mIsAnimating = false;
        mIsFollowAnimating=false;
        mIsNewUserAnimating=false;
    }

    public void resetAnimView() {
        if (mBg != null) {
            mBg.setTranslationX(mDp500);
        }
        if (mfollowText != null) {
            mfollowText.setTranslationX(mDp500);
            mfollowText.setAlpha(1);
        }

        if (mNewUser != null) {
            mNewUser.setTranslationX(mDp500);
            mNewUser.setAlpha(1);
        }

        if (mMoneyText != null) {
            mMoneyText.setTranslationX(mDp500);
            mMoneyText.setAlpha(1);
        }
        if (mUserGroup != null) {
            mUserGroup.setTranslationX(-mDp500);
        }
        if (mSimiText != null) {
            mSimiText.setTranslationX(mDp500);
            mSimiText.setAlpha(1);
        }
        if (mAvatar != null) {
            mAvatar.setImageDrawable(null);
        }
        if (mName != null) {
            mName.setText("");
        }
    }


    /**
     * 初始化进场的动画
     */
    private ObjectAnimator mBgAnimator1;
    private ObjectAnimator mBgAnimator2;
    private ObjectAnimator mBgAnimator3;
    private ObjectAnimator mTextAnimator1;
    private ObjectAnimator mTextAnimator2;
    private ObjectAnimator mTextAnimator3;
    private ObjectAnimator mUserAnimator1;
    private ObjectAnimator mUserAnimator2;
    private ObjectAnimator mUserAnimator3;
    private Animation mStarAnim;
    private void initEnterRoomAnim() {
        mBgAnimator1 = ObjectAnimator.ofFloat(mBg, "translationX", DpUtil.dp2px(70));
        mBgAnimator1.setDuration(1000);
        mBgAnimator1.setInterpolator(interpolator1);

        mBgAnimator2 = ObjectAnimator.ofFloat(mBg, "translationX", 0);
        mBgAnimator2.setDuration(700);
        mBgAnimator2.setInterpolator(interpolator2);

        mBgAnimator3 = ObjectAnimator.ofFloat(mBg, "translationX", -mDp500);
        mBgAnimator3.setDuration(300);
        mBgAnimator3.setInterpolator(interpolator3);


        mTextAnimator1 = ObjectAnimator.ofFloat(mWordText, "translationX", 0);
        mTextAnimator1.setDuration(1000);
        mTextAnimator1.setInterpolator(interpolator3);

        mTextAnimator2 = ObjectAnimator.ofFloat(mWordText, "translationX", DpUtil.dp2px(50));
        mTextAnimator2.setDuration(1000);
        mTextAnimator2.setInterpolator(interpolator1);

        mTextAnimator3 = ObjectAnimator.ofFloat(mWordText, "translationX", DpUtil.dp2px(450));
        mTextAnimator3.setDuration(400);
        mTextAnimator3.setInterpolator(interpolator2);


        mUserAnimator1 = ObjectAnimator.ofFloat(mUserGroup, "translationX", DpUtil.dp2px(70));
        mUserAnimator1.setDuration(1000);
        mUserAnimator1.setInterpolator(interpolator1);
        mUserAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBgAnimator2.start();
                if(!TextUtils.isEmpty(mWordText.getText().toString())){
                    mTextAnimator2.start();
                }
                mUserAnimator2.start();
            }
        });

        mUserAnimator2 = ObjectAnimator.ofFloat(mUserGroup, "translationX", 0);
        mUserAnimator2.setDuration(700);
        mUserAnimator2.setInterpolator(interpolator2);
        mUserAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mStar.startAnimation(mStarAnim);
            }
        });

        mUserAnimator3 = ObjectAnimator.ofFloat(mUserGroup, "translationX", mDp500);
        mUserAnimator3.setDuration(450);
        mUserAnimator3.setInterpolator(interpolator3);
        mUserAnimator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBg.setTranslationX(mDp500);
                mUserGroup.setTranslationX(-mDp500);
                if (!mShowGif) {
                    getNextEnterRoom();
                }
            }
        });

        mStarAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mStarAnim.setDuration(3500);
        mStarAnim.setInterpolator(interpolator2);
        mStarAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBgAnimator3.start();
                mUserAnimator3.start();
                if(!TextUtils.isEmpty(mWordText.getText().toString())){
                    mTextAnimator3.start();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mScreenWidth = ScreenDimenUtil.getInstance().getScreenWdith();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mShowGif = false;
                if (mWordText != null) {
                }
                if (mMediaController != null) {
                    mMediaController.hide();
                }
                if (mGifImageView != null) {
                    mGifImageView.setImageDrawable(null);
                }
                if (mGifDrawable != null && !mGifDrawable.isRecycled()) {
                    mGifDrawable.stop();
                    mGifDrawable.recycle();
                }
                getNextEnterRoom();
            }
        };
        mDownloadGifCallback = new CommonCallback<File>() {
            @Override
            public void callback(File file) {
                if (file != null) {
                    playGif(file);
                }
            }
        };
    }

    /**
     * 关注主播动画初始化
     */
    private ObjectAnimator mfollowAnimator1;
    private ObjectAnimator mfollowAnimator2;
    private ObjectAnimator mfollowAnimator3;
    private void initFollowAnim() {
        mfollowAnimator1 = ObjectAnimator.ofFloat(mfollowText, "translationX", 30);
        mfollowAnimator1.setDuration(1000);
        mfollowAnimator1.setInterpolator(interpolator3);

        mfollowAnimator2 = ObjectAnimator.ofFloat(mfollowText, "translationX", 31);
        mfollowAnimator2.setDuration(1000);
        mfollowAnimator2.setInterpolator(interpolator1);

        mfollowAnimator3 = ObjectAnimator.ofFloat(mfollowText, "alpha", 1,0);
        mfollowAnimator3.setDuration(400);
        mfollowAnimator3.setInterpolator(interpolator2);
        mfollowAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mfollowAnimator2!=null){
                    mfollowAnimator2.start();
                }
            }
        });
        mfollowAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mfollowAnimator3!=null){
                    mfollowAnimator3.start();
                }
            }
        });
        mfollowAnimator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mfollowText!=null){
                    mfollowText.setTranslationX(mDp500);
                    mfollowText.setAlpha(1);
                    getNextFollow();
                }
            }
        });
    }

    /**
     * 新用户进入动画初始化
     */
    private ObjectAnimator mNewUserAnimator1;
    private ObjectAnimator mNewUserAnimator2;
    private ObjectAnimator mNewUserAnimator3;
    private void initNewUserAnim() {
        mNewUserAnimator1 = ObjectAnimator.ofFloat(mNewUser, "translationX", 30);
        mNewUserAnimator1.setDuration(1000);
        mNewUserAnimator1.setInterpolator(interpolator3);

        mNewUserAnimator2 = ObjectAnimator.ofFloat(mNewUser, "translationX", 30);
        mNewUserAnimator2.setDuration(10000);
        mNewUserAnimator2.setInterpolator(interpolator1);

        mNewUserAnimator3 = ObjectAnimator.ofFloat(mNewUser, "alpha", 1,0);
        mNewUserAnimator3.setDuration(400);
        mNewUserAnimator3.setInterpolator(interpolator2);
        mNewUserAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mNewUserAnimator2!=null){
                    mNewUserAnimator2.start();
                }
            }
        });

        mNewUserAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mNewUserAnimator3!=null){
                    mNewUserAnimator3.start();
                }
            }
        });

        mNewUserAnimator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mNewUser!=null){
                    mNewUser.setTranslationX(mDp500);
                    mNewUser.setAlpha(1);
                    getNewUserFollow();
                }
            }

        });
    }

    /**
     * 关注主播动画初始化
     */
    private ObjectAnimator moneyAnimator1;
    private ObjectAnimator moneyAnimator2;
    private ObjectAnimator moneyAnimator3;
    private void initMoneyAnim() {
        moneyAnimator1 = ObjectAnimator.ofFloat(mMoneyText, "translationX", 30);
        moneyAnimator1.setDuration(1000);
        moneyAnimator1.setInterpolator(interpolator3);

        moneyAnimator2 = ObjectAnimator.ofFloat(mMoneyText, "translationX", 31);
        moneyAnimator2.setDuration(2000);
        moneyAnimator2.setInterpolator(interpolator1);

        moneyAnimator3 = ObjectAnimator.ofFloat(mMoneyText, "alpha", 1,0);
        moneyAnimator3.setDuration(1000);
        moneyAnimator3.setInterpolator(interpolator2);
        moneyAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(moneyAnimator2!=null){
                    moneyAnimator2.start();
                }
            }
        });
        moneyAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(moneyAnimator3!=null){
                    moneyAnimator3.start();
                }
            }
        });
        moneyAnimator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mMoneyText!=null){
                    mMoneyText.setTranslationX(mDp500);
                    mMoneyText.setAlpha(1);
                    getNextMoney();
                }
            }
        });
    }

    /**
     * 私密主播动画初始化
     */
    private ObjectAnimator simiAnimator1;
    private ObjectAnimator simiAnimator2;
    private ObjectAnimator simiAnimator3;
    private void initSimiAnim() {
        simiAnimator1 = ObjectAnimator.ofFloat(mSimiText, "translationX", 30);
        simiAnimator1.setDuration(1000);
        simiAnimator1.setInterpolator(interpolator3);
        simiAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                simiAnimator2.start();
            }
        });

        simiAnimator2 = ObjectAnimator.ofFloat(mSimiText, "translationX", 30);
        simiAnimator2.setDuration(3000);
        simiAnimator2.setInterpolator(interpolator3);
        simiAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                simiAnimator3.start();
            }
        });

        simiAnimator3 = ObjectAnimator.ofFloat(mSimiText, "alpha", 1,0);
        simiAnimator3.setDuration(1000);
        simiAnimator3.setInterpolator(interpolator2);
        simiAnimator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mSimiText!=null){
                    mSimiText.setTranslationX(mDp500);
                    mSimiText.setAlpha(1);
                    isshowing=false;
                }
            }
        });
    }
    /**
     * 进场动画
     */
    public void enterRoom(LiveEnterRoomBean bean) {
        if (mIsAnimating) {
            mQueue.offer(bean);
        } else {
            startAnim(bean);
        }
        if(mBg!=null){
            mBg.setVisibility(View.VISIBLE);
            mWordText.setVisibility(View.VISIBLE);
            mUserGroup.setVisibility(View.VISIBLE);
        }
        if(mContext instanceof LiveAnchorActivity){

            if(bean.getUserBean().getLivePhone()!=null&&bean.getUserBean().getLivePhone ().
                    equals("1")&&CommonAppConfig.getInstance().getConfig().getConversation_switch().equals("1")){
                //新用户进场
                NewUserAnchor(bean.getUserBean());
            }
        }
    }
    /**
     * 关注主播动画
     */
    public void FollowAnchor(LiveChatBean bean) {
        if (mIsFollowAnimating) {

            mFollowQueue.offer(bean);
        } else {
            mIsFollowAnimating=true;
            mfollowText.setText(bean.getContent());
            mfollowAnimator1.start();
        }

    }

    /**
     * 新用户进入动画
     */
    public void NewUserAnchor(LiveUserGiftBean bean) {
        if (mIsNewUserAnimating) {
            mNewUserQueue.offer(bean);
        } else {
            mIsNewUserAnimating=true;
            SpanUtils.with(mNewUser)
                    .append(bean.getUserNiceName()+" ")
                    .setFontSize(SizeUtils.sp2px(16))
                    .append("已接受邀请进入直播间")
                    .setFontSize(SizeUtils.sp2px(12))
                    .setVerticalAlign(SpanUtils.ALIGN_CENTER)
                    .create();
            mNewUserAnimator1.start();
        }

    }

    /**
     * 显示消费金额动画
     */
    public void ShowMoneyAnim(LiveChatBean bean) {
        if (mIsMoneyAnimating) {
            mMoneyQueue.offer(bean);
        } else {
            mIsMoneyAnimating=true;
            mMoneyText.setText(bean.getEffect_tex());
            moneyAnimator1.start();
        }

    }
    /**
     * 显示私密主播动画
     */
    private boolean  isshowing=false;
    public void ShowSimiAnim(int time) {
//        if(mTimer!=null){
//            return;
//        }
//        mTimer = new MyCountDownTimer(time*1000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                if(mSimiText!=null){
//                    mSimiText.setText(String.format("大家正在申请私密直播，%s秒后开始处理哦！",millisUntilFinished/1000+""));
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                if(simiAnimator2!=null){
//                    simiAnimator2.start();
//                }
//                if (mTimer != null) {
//                    mTimer.cancel();
//                    mTimer=null;
//                }
//            }
//        }.start();
        if(!isshowing){
            isshowing=true;
            mSimiText.setText(String.format("大家正在申请私密互动，%s秒后开始处理哦！",time+""));
            simiAnimator1.start();
        }

    }
    /**
     * 播放下一个进场消息
     */
    private void getNextEnterRoom() {
        if (mQueue == null) {
            return;
        }
        LiveEnterRoomBean bean = mQueue.poll();
        if (bean == null) {
            mIsAnimating = false;
        } else {
            startAnim(bean);
        }
    }

    /**
     * 播放下一个关注消息
     */
    private void getNextFollow() {
        if (mFollowQueue == null) {
            return;
        }
        LiveChatBean bean = mFollowQueue.poll();
        if (bean == null) {
            mIsFollowAnimating = false;
        } else {
            mIsFollowAnimating=true;
            mfollowText.setText(bean.getContent());
            mfollowAnimator1.start();
        }
    }

    /**
     * 播放下一个新用户进入消息
     */
    private void getNewUserFollow() {
        if (mNewUserQueue == null) {
            return;
        }
        LiveUserGiftBean bean = mNewUserQueue.poll();
        if (bean == null) {
            mIsNewUserAnimating = false;
        } else {
            mIsNewUserAnimating=true;
            SpanUtils.with(mNewUser)
                    .append(bean.getUserNiceName()+" ")
                    .setFontSize(SizeUtils.sp2px(16))
                    .append("已接受邀请进入直播间")
                    .setFontSize(SizeUtils.sp2px(12))
                    .create();
            mNewUserAnimator1.start();
        }
    }

    /**
     * 播放下一个金钱动画
     */
    private void getNextMoney() {
        if (mMoneyQueue == null) {
            return;
        }
        LiveChatBean bean = mMoneyQueue.poll();
        if (bean == null) {
            mIsMoneyAnimating = false;
        } else {
            mIsMoneyAnimating=true;
            mMoneyText.setText(bean.getEffect_tex());
            moneyAnimator1.start();
        }
    }



    /**
     * 进场动画参数配置
     */
    private void startAnim(LiveEnterRoomBean bean) {
        UserBean u = bean.getUserBean();
        LiveChatBean liveChatBean = bean.getLiveChatBean();
        if (u != null && liveChatBean != null) {
            mIsAnimating = true;
            boolean needAnim = false;
            if (u.getVipType() != 0 || liveChatBean.getGuardType() != Constants.GUARD_TYPE_NONE) {
                needAnim = true;
                ImgLoader.displayAvatar(mContext,bean.getUserBean().getAvatar(), mAvatar);
                renderEnterRoom(mContext,mName, liveChatBean);
            }
            UserBean.Car car = u.getCar();
            if (car != null && car.getId() != 0) {
                String url = car.getSwf();
                if (!TextUtils.isEmpty(url)) {
                    needAnim = true;
                    mShowGif = true;
                    mWordText.setText(u.getUserNiceName() + car.getWords());
                    GifCacheUtil.getFile(Constants.GIF_CAR_PREFIX + car.getId(), url, mDownloadGifCallback);
                }
            }
            if (!needAnim) {
                getNextEnterRoom();
            }
        }
    }
    /**
     * 渲染用户进入房间消息
     */
    public  void renderEnterRoom(Context context,final TextView textView, final LiveChatBean bean) {
        final LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
        if (levelBean == null) {
            return;
        }
        ImgLoader.displayDrawable(context,levelBean.getThumb(), new ImgLoader.DrawableCallback() {
            @Override
            public void onLoadSuccess(Drawable drawable) {
                if (textView != null) {
                    SpannableStringBuilder builder =  LiveTextRender.createPrefix2(drawable, bean,false);
                    int start = builder.length();
                    String name = bean.getUserNiceName() + " ";
                    builder.append(name);
                    int end = start + name.length();
                    builder.setSpan(new ForegroundColorSpan(0xffffffff), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(13, true), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan( new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(bean.getContent());
                    textView.setText(builder);
                    mBgAnimator1.start();
                    if(!TextUtils.isEmpty(mWordText.getText().toString())){
                        mTextAnimator1.start();
                    }
                    mUserAnimator1.start();
                }
            }

            @Override
            public void onLoadFailed() {
                if (textView != null) {
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.mipmap.chat_level_default);
                    SpannableStringBuilder builder =  LiveTextRender.createPrefix2(drawable, bean,false);
                    int start = builder.length();
                    String name = bean.getUserNiceName() + " ";
                    builder.append(name);
                    int end = start + name.length();
                    builder.setSpan(new ForegroundColorSpan(0xffffffff), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(13, true), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan( new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(bean.getContent());
                    textView.setText(builder);
                    mBgAnimator1.start();
                    if(!TextUtils.isEmpty(mWordText.getText().toString())){
                        mTextAnimator1.start();
                    }
                    mUserAnimator1.start();
                }
            }

        });
    }

    /**
     * 调整mGifImageView的大小
     */
    private void resizeGifImageView(Drawable drawable) {
        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();
        ViewGroup.LayoutParams params = mGifImageView.getLayoutParams();
        params.height = (int) (mScreenWidth * h / w);
        mGifImageView.setLayoutParams(params);
    }

    /**
     * 播放gif
     */
    private void playGif(File file) {
        if (mEnd) {
            return;
        }
        try {
            mGifDrawable = new GifDrawable(file);
            mGifDrawable.setLoopCount(1);
            resizeGifImageView(mGifDrawable);
            mGifImageView.setImageDrawable(mGifDrawable);
            if (mMediaController == null) {
                mMediaController = new MediaController(mContext);
                mMediaController.setVisibility(View.GONE);
            }
            mMediaController.setMediaPlayer((GifDrawable) mGifImageView.getDrawable());
            mMediaController.setAnchorView(mGifImageView);
            int duration = mGifDrawable.getDuration();
            mMediaController.show(duration);
            if (duration < 4000) {
                duration = 4000;
            }
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(0, duration);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(0, 4000);
            }
        }
    }
}
