package com.yunbao.live.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.activity.SmallProgramTitleActivity;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.common.utils.IMImageLoadUtil;
import com.yunbao.live.R;
import com.yunbao.live.custom.MyCountDownTimer;

/**
 * 精彩回放引流
 */
public class LiveYinLiuDialog extends AbsDialogFragment implements View.OnClickListener {
    private RelativeLayout re_close;
    private TextView tv_time;
    private ImageView iv_content;
    private ImageView iv_go;
    private  String url;
    private  String picUrl;
    private  String time;
    private  int vip;
    private  String limitVip;
    private  String ad_is_king;
    private  String ad_show_style;
    public LiveYinLiuDialog( String url,String picUrl,String time,String limitVip,String ad_is_king,String ad_show_style) {
        this.url=url;
        this.picUrl=picUrl;
        this.time=time;
        this.limitVip=limitVip;
        this.ad_is_king=ad_is_king;
        this.ad_show_style=ad_show_style;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_yinliu;
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
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        window.setWindowAnimations(R.style.ActionSheetDialogAnimations);  //添加动画
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewId();
    }


    private void findViewId() {
        re_close =(RelativeLayout) findViewById(R.id.re_close);
        iv_content =(ImageView) findViewById(R.id.iv_content);
        iv_go =(ImageView) findViewById(R.id.iv_go);
        tv_time=(TextView) findViewById(R.id.tv_time);
        re_close.setOnClickListener(this);
        iv_go.setOnClickListener(this);
        iv_content.setOnClickListener(this);

        ViewGroup.LayoutParams lp = iv_content.getLayoutParams();
        lp.width = IMDensityUtil.getScreenWidth(mContext)-IMDensityUtil.dip2px(mContext,70);
        lp.height =(int)(lp.width*(255f/299));
        iv_content.setLayoutParams(lp);
        initPicBg();
    }

    /**
     * 固定宽度，根据比列调整高度
     */
    private void initPicBg() {
        Log.e("picUrl--------",picUrl);
        IMImageLoadUtil.CommonImageLoad(mContext,picUrl,iv_content);
        countDown();
        vip= SPUtils.getInstance().getInt(Constants.USER_VIP);
//        Glide.with(mContext)
//                .asBitmap()
//                .load(picUrl)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
//                        Log.e("onResourceReady--------", picUrl);
//                        float width = iv_content.getWidth();
//                        float scale = width / resource.getWidth();
//                        int afterWidth = (int) (resource.getWidth() * scale);
//                        int afterHeight = (int) (resource.getHeight() * scale);
//                        ViewGroup.LayoutParams lp = iv_content.getLayoutParams();
//                        lp.width = afterWidth;
//                        lp.height = afterHeight;
//                        iv_content.setLayoutParams(lp);
//                        iv_content.setImageBitmap(resource);
//                    }p
//
//                    @Override
//                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                        super.onLoadFailed(errorDrawable);
//                        Log.e("onLoadFailed--------","onLoadFailed");
//                    }
//
//                    @Override
//                    public void onLoadStarted(@Nullable Drawable errorDrawable) {
//                        super.onLoadStarted(errorDrawable);
//                        Log.e("onLoadStarted--------", "onLoadStarted");
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable errorDrawable) {
//                        super.onLoadCleared(errorDrawable);
//                        Log.e("onLoadCleared--------","onLoadCleared");
//                    }
//
//                    @Override
//                    public void removeCallback(@Nullable SizeReadyCallback cb) {
//                        super.removeCallback(cb);
//                        Log.e("removeCallback--------","removeCallback");
//                    }
//                });
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.iv_go || view.getId()==R.id.iv_content){
            if(!TextUtils.isEmpty(url)){
                //引流只支持外部浏览器跳转
                OpenUrlUtils.getInstance()
                        .setContext(mContext)
                        .setSlideTarget("1")
                        .setIs_king(ad_is_king)
                        .setType(Integer.parseInt(ad_show_style))
                        .go(url);
                dismiss();
            }
        }else  if(view.getId()==R.id.re_close){
            vip= SPUtils.getInstance().getInt(Constants.USER_VIP);
            if(vip>=Integer.parseInt(limitVip) || Integer.parseInt(limitVip)==0){
                dismiss();
            }else {
                DialogUitl.Builder builder = new DialogUitl.Builder(mContext);
                Dialog build = builder.build();
                Window window = build.getWindow();
                window.setWindowAnimations(com.yunbao.common.R.style.ActionSheetDialogAnimations);
                builder.setTitle("温馨提示")
                        .setContent("亲\uD83D\uDE0A！立即关闭广告需要达到VIP"+limitVip+"\n点击获取开通VIP！")
                        .setConfrimString("获取VIP")
                        .setCancelString("取消")
                        .setCancelable(false)
                        .setClickCallback(new DialogUitl.SimpleCallback() {
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                new SmallProgramTitleActivity().toActivity(mContext, CommonAppConfig.getInstance().getConfig().getVideo_limit_rule(),"normal",true);
                            }
                        })
                        .build()
                        .show();

            }
        }
    }


    /**
     * 倒计时
     */
    private MyCountDownTimer mTimer;
    private void countDown() {
        if(tv_time!=null){
            mTimer = new MyCountDownTimer(Integer.parseInt(time)*1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if(tv_time!=null){
                        tv_time.setClickable(false);
                        tv_time.setText((millisUntilFinished/1000)+"s");
                    }
                }

                @Override
                public void onFinish() {
                    try {
                        dismissAllowingStateLoss();
                        mTimer.cancel();
                        mTimer=null;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
    }
}
