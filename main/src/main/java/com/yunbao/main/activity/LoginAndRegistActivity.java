package com.yunbao.main.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.transition.Fade;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.utils.IMHideInPutUtil;
import com.yunbao.main.R;
import com.yunbao.main.event.RegSuccessEvent;
import com.yunbao.main.views.LoginViewHolder;
import com.yunbao.main.views.RegistViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class LoginAndRegistActivity extends AbsActivity implements EasyPermissions.PermissionCallbacks{
    private LoginViewHolder mLoginViewHolder;
    private RegistViewHolder mRegistViewHolder;
    private FrameLayout fl_login;
    private FrameLayout fl_regist;

    String[] perms = {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};
    private FrameLayout container;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_regist;
    }

    @Override
    protected void main() {
        EventBus.getDefault().register(this);
        requestPermissions();
        getWindow().setEnterTransition( new Fade().setDuration(300));
        getWindow().setExitTransition( new Fade().setDuration(300));
        initView();
//        playVideo();
    }

    private void initView() {
        fl_login=findViewById(R.id.fl_login);
        fl_regist=findViewById(R.id.fl_regist);
        container=findViewById(R.id.container);

        //添加开播前设置控件
        mLoginViewHolder = new LoginViewHolder(mContext, fl_login);
        mLoginViewHolder.addToParent();
        mLoginViewHolder.subscribeActivityLifeCycle();

        mRegistViewHolder = new RegistViewHolder(mContext, fl_regist);
        mRegistViewHolder.addToParent();
        mRegistViewHolder.subscribeActivityLifeCycle();
        showRegistView(false);

        ToastUtils.setGravity(Gravity.CENTER,0,0);
        ToastUtils.setMsgColor(Color.parseColor("#ffffff"));
        ToastUtils.setBgResource(R.drawable.bg_toast_new);
    }

    /**
     * 显示登陆或是注册界面
     */
    public  void  showRegistView(boolean showRegist){
        if(fl_regist==null || fl_login==null){
           return;
        }
        if(showRegist){
            fl_regist.setVisibility(View.VISIBLE);
            fl_login.setVisibility(View.GONE);
            if(mRegistViewHolder!=null){
                mRegistViewHolder.initCodeType();
            }
        }else {
            fl_regist.setVisibility(View.GONE);
            fl_login.setVisibility(View.VISIBLE);
        }
    }

    public static void forward() {
        Intent intent = new Intent(CommonAppContext.sInstance, LoginAndRegistActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        CommonAppContext.sInstance.startActivity(intent);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegSuccessEvent(RegSuccessEvent e) {
        finish();
    }

    @Override
    protected void onPause() {
//        if (mVideoView != null && !isStopVideo) {
//            mVideoView.runInBackground(false);
//        }
        super.onPause();

        if(container!=null){
            container.setBackground(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(container!=null){
//            container.setBackgroundResource(R.mipmap.icon_login_bg_bg);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    /**
     * 动态添加权限
     */
    private void requestPermissions() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            //todo 已经获取了权限
        } else {
            //没有获取了权限，申请权限
            EasyPermissions.requestPermissions(this, "为了优化您的使用体验，请打开下列必要的权限", 0, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    public void setStopVideo(boolean stopVideo) {
    }
    /**
     * 点击软键盘外面的区域关闭软键盘
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (IMHideInPutUtil.isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    assert v != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    v.clearFocus();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }
}
