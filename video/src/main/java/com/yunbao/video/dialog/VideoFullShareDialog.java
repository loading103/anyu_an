package com.yunbao.video.dialog;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.video.R;
import com.yunbao.video.activity.VideoDetailActivity;
import com.yunbao.common.bean.VideoBean;
import com.yunbao.video.event.VideoDeleteEvent;
import com.yunbao.video.http.VideoHttpUtil;

import org.greenrobot.eventbus.EventBus;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Administrator on 2020/6/15.
 * Describe:视频详情分享(全屏)
 */
public class VideoFullShareDialog extends AbsDialogFragment {
    private boolean isSelf;
    private VideoBean bean;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_video_details_share_full;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog3;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = (int)(IMDensityUtil.getScreenWidth(getContext())*0.47);
        params.gravity=Gravity.END |Gravity.CENTER_VERTICAL ;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageView ivContent = (ImageView) findViewById(R.id.iv_content);
        TextView tvContent = (TextView) findViewById(R.id.tv_content);
        if(isSelf){
            ivContent.setImageResource(R.mipmap.video_delete);
            tvContent.setText("删除");
        }else {
            ivContent.setImageResource(R.mipmap.video_jb);
            tvContent.setText("举报");
        }

       findViewById(R.id.ll_share).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //分享
               copyLink();

               ToastUtil.show("复制成功");
               dismiss();
           }
       });

        findViewById(R.id.ll_jb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelf){//删除
                    DialogUitl.showSimpleDialog(mContext, "您确定要删除此视频吗？", new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            deleteVideo(bean);
                        }
                    });
                }else {//举报
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.patientIf");
                    intent.putExtra(Constants.TO_UID, bean.getUid());
                    mContext.startActivity(intent);
                    dismiss();
                }
            }
        });
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public void setBean(VideoBean bean) {
        this.bean=bean;
    }

    /**
     * 删除视频
     */
    public void deleteVideo(final VideoBean videoBean) {
        VideoHttpUtil.videoDelete(videoBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    EventBus.getDefault().post(new VideoDeleteEvent(videoBean.getId()));
                    dismiss();
                    ((VideoDetailActivity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //切换竖屏
                    ((VideoDetailActivity)mContext).finish();
                }
            }
        });
    }

    /**
     * 复制链接
     */
    private void copyLink() {
        if (TextUtils.isEmpty(bean.getUid())) {
            return;
        }
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean == null) {
            return;
        }
        String link = configBean.getLiveWxShareUrl() + bean.getUid();
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", link);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(WordUtil.getString(R.string.copy_success));
    }
}
