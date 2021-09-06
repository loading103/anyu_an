package com.yunbao.main.interfaces;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.just.agentweb.AgentWeb;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.activity.SmallProgramActivity;
import com.yunbao.common.activity.SmallProgramTitleActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.JsWebBean;
import com.yunbao.common.bean.MsgButtonBean;
import com.yunbao.common.event.MainJsEvent;
import com.yunbao.common.event.MainRefreshJsEvent;
import com.yunbao.common.event.ShowMsgEvent;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.bean.JSBean;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

/**
 * Created by Administrator on 2019/12/26.
 * Describe:
 */
public class AndroidInterface {
    private Handler deliver = new Handler(Looper.getMainLooper());
    private AgentWeb agent;
    private Context context;

    public AndroidInterface(AgentWeb agent, Context context) {
        this.agent = agent;
        this.context = context;
    }

    @JavascriptInterface
    public void WKJS_CALL_OC_Cache(final String msg) {
        deliver.post(new Runnable() {
            @Override
            public void run() {
                Log.i("WOLF", "main Thread:" + msg);
                JSBean bean = JSON.parseObject(msg, JSBean.class);
                if(!TextUtils.isEmpty(msg)){
                    if(bean.getMessage().equals("paymentFnIOS")){
                        ((SmallProgramActivity)context).toBrowser(bean.getData().getUrl());
                    }else{
                        ((SmallProgramActivity)context).setScreen(bean.getData().getUrl());
                    }
                }
            }
        });
    }

    @JavascriptInterface
    public void NativeModel(final String msg) {
        Log.i("WOLF", "NativeModel:" + msg);
        deliver.post(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(msg)){
                    JsWebBean bean = JSON.parseObject(msg, JsWebBean.class);
                    if(context instanceof SmallProgramActivity){
                        if(bean.equals("app")){
                            ((SmallProgramActivity)context).back();
                        }
                    }else if(context instanceof SmallProgramTitleActivity){
                        ((SmallProgramTitleActivity)context).onJs(bean);
                    }else {
                        EventBus.getDefault().post(new MainJsEvent(bean));
                    }
                }
            }
        });
    }

    @JavascriptInterface
    public void showMsgButton(final String msg) {
        Log.i("WOLF", "showMsgButton:" + msg);
        deliver.post(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(msg)){
                    MsgButtonBean bean = JSON.parseObject(msg, MsgButtonBean.class);
                    EventBus.getDefault().post(new ShowMsgEvent(bean));
                }
            }
        });
    }

    @JavascriptInterface
    public void openChat(final String msg) {
        Log.i("WOLF", "openChat:" + msg);
        deliver.post(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(msg)){
                    ComponentName componet = null;
                    if(msg.equals("qq")){
                        if(AppUtils.isAppInstalled("com.tencent.mobileqq")){
                            componet = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.SplashActivity");
                        }else {
                            ToastUtil.show("您还没有安装QQ，请先安装");
                            return;
                        }
                    }else if(msg.equals("wx")){
                        if(AppUtils.isAppInstalled("com.tencent.mm")){
                            componet = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                        }else {
                            ToastUtil.show("您还没有安装微信，请先安装");
                            return;
                        }
                    }else if(msg.equals("zfb")){
                        if(AppUtils.isAppInstalled("com.eg.android.AlipayGphone")){
                            componet = new ComponentName("com.eg.android.AlipayGphone", "com.alipay.mobile.quinox.splash.ShareDispenseActivity");
                        }else {
                            ToastUtil.show("您还没有安装支付宝，请先安装");
                            return;
                        }
                    }
                    Intent intent = new Intent();
                    intent.setComponent(componet);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    @JavascriptInterface
    public void openBrowser(final String msg) {
        Log.i("WOLF", "openBrowser:" + msg);
        deliver.post(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(msg)){
                    JSONObject obj = JSON.parseObject(msg);
                    String type = obj.getString("type");
                    if(type.equals("1")){
                        WebViewActivity.forward(context, obj.getString("url"),false);
                    }else if(type.equals("2")){
                        if(!obj.getString("url").startsWith("http")){
                            ToastUtil.show("路由不支持外部跳转");
                            return;
                        }
                        Intent intent= new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri uri = Uri.parse(obj.getString("url"));
                        intent.setData(uri);
                        try {
                            startActivity(intent);
                        }catch (Exception e){
                            Log.i("WOLF","url错误");
                        }
                    }
                }
            }
        });
    }

    @JavascriptInterface
    public void saveQrcode(final String msg) {
        Log.i("WOLF", "saveQrcode:"+msg);
        deliver.post(new Runnable() {
            @Override
            public void run() {//保存图片
                if(PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    saveImageToGallery(ImageUtils.bytes2Bitmap(EncodeUtils.base64Decode(msg)));
                    ToastUtil.show("已成功保存到相册，赶快分享吧");
                }else {
                    ToastUtil.show("没有存储权限");
                }
            }
        });
    }

    /**
     * 保存图片到相册
     */
    public void saveImageToGallery(Bitmap mBitmap) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ToastUtil.show("sdcard未使用");
            return;
        }
        // 首先保存图片
        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsoluteFile();
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(CommonAppContext.sInstance.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } // 最后通知图库更新

        CommonAppContext.sInstance.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + "")));
    }


    @JavascriptInterface
    public void resetWebview() {
        Log.i("WOLF", "resetWebview");
        deliver.post(new Runnable() {
            @Override
            public void run() {//刷新token
                if (context instanceof SmallProgramActivity) {
                    ((SmallProgramActivity) context).resetWebview();
                } else if (context instanceof SmallProgramTitleActivity) {
                    ((SmallProgramTitleActivity) context).resetWebview();
                } else if (context instanceof WebViewActivity) {
                    ((WebViewActivity) context).resetWebview();
                } else {
                    EventBus.getDefault().post(new MainRefreshJsEvent());
                }
            }
        });
    }
}
