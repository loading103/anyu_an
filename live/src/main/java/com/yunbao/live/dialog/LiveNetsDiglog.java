package com.yunbao.live.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.activity.LiveAudienceActivity;

public class LiveNetsDiglog implements View.OnClickListener {
    private Context context;
    private TextView tv_try;
    private TextView tv_exit;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    public LiveNetsDiglog(Context context) {
        this.context=context;
    }

    public void showNetsDiglog() {
        builder = new AlertDialog.Builder(context);
        View dialogView =  View.inflate(context, R.layout.dialog_live_net, null);
        tv_try =   dialogView.findViewById(R.id.tv_try);
        tv_exit =  dialogView. findViewById(R.id.tv_exit);

        tv_try.setOnClickListener(this);
        tv_exit.setOnClickListener(this);
        builder.setView(dialogView);


        dialog =  builder.show();
        Window window=dialog.getWindow();
        dialog.getWindow().setDimAmount(0);//设置昏暗度为0
        window.setWindowAnimations(R.style.ActionSheetDialogAnimations);  //添加动画
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = IMDensityUtil.getScreenWidth(context) - IMDensityUtil.dip2px(context,70);
        int screenHeight = IMDensityUtil.getScreenHeight(context);
        params.height=(int) (screenHeight*4.0f/6.9);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.tv_try){
            dialog.dismiss();
            if(context instanceof LiveAudienceActivity){
                ((LiveAudienceActivity)context).reConnection();
            }else if (context instanceof LiveAnchorActivity){
                ((LiveAnchorActivity)context).reConnection();
            }
        }else if(view.getId()==R.id.tv_exit){
            dialog.dismiss();
            if(context instanceof LiveAudienceActivity){
                ((LiveAudienceActivity)context).release();
                ((LiveAudienceActivity)context).finish();
            }else if (context instanceof LiveAnchorActivity){
                ((LiveAnchorActivity)context).release();
                ((LiveAnchorActivity)context).finish();
            }
        }
    }

    public boolean isShowing() {
        if(dialog!=null && dialog.isShowing()){
            return true;
        }else {
            return false;
        }
    }

    public void dissmiss() {
        if(dialog!=null ){
            dialog.dismiss();
        }
    }
}
