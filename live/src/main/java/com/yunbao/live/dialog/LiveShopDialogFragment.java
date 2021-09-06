package com.yunbao.live.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.live.R;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.live.custom.ShopPageViewPager;
import com.yunbao.common.interfaces.DissmissDialogListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品的弹窗
 */

public class LiveShopDialogFragment extends AbsDialogFragment {
    private ShopPageViewPager mViewPager;
    private int maxheight;
    private List<LiveReadyBean.GoodsBean> data=new ArrayList<>();
    private DissmissDialogListener ondissDialogListener;

    public LiveShopDialogFragment(List<LiveReadyBean.GoodsBean> data) {
        this.data = data;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_shop;
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
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        if(data.size()<=5){
            params.height = IMDensityUtil.dip2px(mContext,110);
        }else {
            params.height =WindowManager.LayoutParams.WRAP_CONTENT;
        }
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(null);
        mViewPager = mRootView.findViewById(R.id.viewPager);
        maxheight = mViewPager.getLayoutParams().height;
    }

    public Handler handler=new Handler();
    public void setLiveGuardInfo() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mViewPager==null){
                    return;
                }
                mViewPager.setData( mContext,data);
            }
        },200);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        dialogDiss();
    }

    @Override
    protected void dialogDiss() {
        if(ondissDialogListener!=null){
            ondissDialogListener.onDissmissListener();
        }
    }

    public void setOnDissmissDialogListener(DissmissDialogListener ondissDialogListener) {
        this.ondissDialogListener = ondissDialogListener;
    }
}
