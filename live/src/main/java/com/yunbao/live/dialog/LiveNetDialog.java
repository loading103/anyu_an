package com.yunbao.live.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.activity.LiveAudienceActivity;

/**
 * Created by Administrator on 2020/5/13.
 * Describe:直播网络不佳
 */
public class LiveNetDialog extends AbsDialogFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_net;
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
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewById(R.id.tv_try).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(getContext() instanceof LiveAudienceActivity){
                    ((LiveAudienceActivity)getContext()).reConnection();
                }else if (getContext() instanceof LiveAnchorActivity){
                    ((LiveAnchorActivity)getContext()).reConnection();
                }
                }
            });

            findViewById(R.id.tv_exit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if(getContext() instanceof LiveAudienceActivity){
                    ((LiveAudienceActivity)getContext()).release();
                    ((LiveAudienceActivity)getContext()).finish();
                }else if (getContext() instanceof LiveAnchorActivity){
                    ((LiveAnchorActivity)getContext()).release();
                    ((LiveAnchorActivity)getContext()).finish();
                }
            }
        });
    }

}
