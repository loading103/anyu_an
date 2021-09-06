package com.yunbao.live.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.interfaces.DissmissDialogListener;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.activity.LiveAudienceActivity;

/**
 * Created by Administrator on 2020/5/13.
 * Describe:私密互动通用dialog
 */
public class PrivateLiveDialog extends AbsDialogFragment {

    private ImageView ivContent;
    private TextView tvTitle,tvContent,tvCommit;
    private int res;
    private String title,content;
    private DissmissDialogListener ondissDialogListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_private_live;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = SizeUtils.dp2px(300);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.setWindowAnimations(com.yunbao.common.R.style.ActionSheetDialogAnimations);  //添加动

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ivContent=(ImageView) findViewById(R.id.iv_content);
        tvTitle=(TextView) findViewById(R.id.tv_title);
        tvContent=(TextView) findViewById(R.id.tv_content);
        tvCommit=(TextView) findViewById(R.id.tv_commit);

        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ondissDialogListener!=null){
                    ondissDialogListener.onDissmissListener();
                }
                dismiss();
            }
        });

        ivContent.setImageResource(res);
        tvTitle.setText(title);
        tvContent.setText(content);
    }

    public void setData(@DrawableRes int res,String title,String content){
        this.res=res;
        this.title=title;
        this.content=content;
    }


    public void setOnDismissDialogListener(DissmissDialogListener ondissDialogListener) {
        this.ondissDialogListener = ondissDialogListener;
    }
}
