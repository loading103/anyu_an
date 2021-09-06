package com.yunbao.common.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lzy.okgo.model.Response;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.event.LoginForBitEvent;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.ScreenShotListenManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2017/8/3.
 */

public  class BaseFotBitShotActivity extends AppCompatActivity {

    public ScreenShotListenManager screenManager; // 截屏管理
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(forbitSootSceenEnble()){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            ScreebShotManege();
        }
    }


    /**
     * 监听截屏
     */
    protected void ScreebShotManege() {
        if(forbitSootSceenEnble()) {
            screenManager = ScreenShotListenManager.newInstance(BaseFotBitShotActivity.this);
            screenManager.startListen();
            if (ContextCompat.checkSelfPermission(BaseFotBitShotActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BaseFotBitShotActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            } else {
                listenerScreenShot();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 123:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(BaseFotBitShotActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},123);
                }else {
                    listenerScreenShot();
                }
                break;
        }
    }

    // 截屏监听
    protected void listenerScreenShot() {
        if(forbitSootSceenEnble()) {
            screenManager.setListener(
                    new ScreenShotListenManager.OnScreenShotListener() {
                        public void onShot(final String imagePath) {
                            if (CommonAppConfig.getInstance().isFrontGround()) { //如果app处理前台
                                getFotBitShotData();
                            }
                        }
                    });
        }
    }

    /**
     * 请求接口判断截屏是警告还是封号
     */
    private void getFotBitShotData() {
        CommonHttpUtil.getFotBitShotData(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                //10000是警告，20000是封号
                if (code == 20000) {
                    showForBiten();
                }else {
                    DialogUitl.showSimpleTipDialog(BaseFotBitShotActivity.this, "严重警告", "禁止截图和录屏，再次使用将会被封号!");
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                DialogUitl.showSimpleTipDialog(BaseFotBitShotActivity.this, "严重警告", "禁止截图和录屏，再次使用将会被封号!");

            }
        });

    }

    /**
     * 封号处理（通知MainActivity处理）
     */
    protected void showForBiten() {
        DialogUitl.showSimpleTipCallDialog(BaseFotBitShotActivity.this, "处罚通知", "检测到你在APP内多次使用截屏以及录屏功能，现对你进行封号处罚，如有疑问请与客服联系！", new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                EventBus.getDefault().post(new LoginForBitEvent());
                finish();
            }
        });
    }
    @Override
    protected void onDestroy() {
       if(forbitSootSceenEnble()){
           screenManager.stopListen();
       }
        super.onDestroy();
    }

    public  boolean  forbitSootSceenEnble(){
        return false;
    }

}
