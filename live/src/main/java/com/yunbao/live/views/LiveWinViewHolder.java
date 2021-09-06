package com.yunbao.live.views;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.live.R;

/**
 * 中奖
 */

public class LiveWinViewHolder extends AbsDialogFragment {

    private TextView tv_money;
    private  RelativeLayout root;
    public String number;

    private  boolean isclose;
    public LiveWinViewHolder(String number ) {
        this.number=number;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_win;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
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
        tv_money=(TextView) findViewById(R.id.tv_money);
        root =(RelativeLayout) findViewById(R.id.root);
        tv_money.setText(number);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(!isclose){
                        dismissAllowingStateLoss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        tv_money.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!isclose){
                        dismissAllowingStateLoss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },3000);

    }


    @Override
    public void onDestroy() {
        isclose=true;
        super.onDestroy();
    }
}
