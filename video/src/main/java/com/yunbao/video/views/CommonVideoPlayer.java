package com.yunbao.video.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.video.R;
import com.yunbao.video.activity.VideoDetailActivity;
import com.yunbao.common.bean.VideoBean;
import com.yunbao.video.dialog.VideoFullShareDialog;
import com.yunbao.video.dialog.VideoShareDialog;


@SuppressLint("ViewConstructor")
public class CommonVideoPlayer extends StandardGSYVideoPlayer {
    private Context context;
    private VideoBean bean;
    private VideoShareDialog dialog;
    private VideoFullShareDialog dialog1;

    public CommonVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public CommonVideoPlayer(Context context) {
        super(context);
    }

    public CommonVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public int getLayoutId() {
        return R.layout.video_layout_standard_exent;
    }
    @Override
    protected void init(final Context context) {
        super.init(context);
        this.context=context;
        ImageView mIvMore = findViewById(R.id.more);
        ImageView mIvBack = findViewById(R.id.back);
        TextView mTvTitle = findViewById(R.id.title);
        mIvBack.setOnClickListener(this);
        mIvMore.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        if( v.getId()== R.id.back){
            if(mIfCurrentIsFullscreen){
                clearFullscreenLayout();
            }else {
                ((Activity)context).finish();
            }
        }else  if(v.getId()== R.id.more) {
            if(((VideoDetailActivity)context).isHorion()){
                openFullShareDialog();
            }else {
                openShareWindow();
            }

        }
    }

    @Override
    protected void clearFullscreenLayout() {
        super.clearFullscreenLayout();
        ((VideoDetailActivity)context).hideTableBar();
    }

    /**
     * 分享
     */
    public void openShareWindow() {
        if(bean==null){
            bean= ((VideoDetailActivity)context).getBean();
            if(bean==null) {
                return;
            }
        }
        dialog = new VideoShareDialog();
        if(CommonAppConfig.getInstance().getUid().equals(bean.getUid())){
            dialog.setSelf(true);
        }else {
            dialog.setSelf(false);
        }
        dialog.setBean(bean);
        dialog.show(((VideoDetailActivity)getContext()).getSupportFragmentManager(), "VideoShareDialog");
    }
    /**
     * 全屏分享
     */
    private void openFullShareDialog() {
        if(bean==null){
            bean= ((VideoDetailActivity)context).getBean();
            if(bean==null){
                return;
            }
        }
        dialog1 = new VideoFullShareDialog();
        if(CommonAppConfig.getInstance().getUid().equals(bean.getUid())){
            dialog1.setSelf(true);
        }else {
            dialog1.setSelf(false);
        }
        dialog1.setBean(bean);
        dialog1.show(((VideoDetailActivity)getContext()).getSupportFragmentManager(), "VideoShareDialog");
    }
    public void setDateBean(VideoBean bean) {
        this.bean = bean;
    }

}
