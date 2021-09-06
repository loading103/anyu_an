package com.yunbao.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.yunbao.common.activity.SmallProgramActivity;
import com.yunbao.common.activity.SmallProgramTitleActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.TokenUtilsBean;
import com.yunbao.common.event.LiveCloseEvent;
import com.yunbao.common.event.OnSetChangeEvent;
import com.yunbao.common.fragment.ButtomDialogFragment;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.DissmissDialogListener;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.WebUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Wolf on 2020/4/28.
 * Describe:
 */
public class OpenUrlUtils {
    private static int type = 1;
    private static String slide_target = "1";// 1是跳转url 其他跳转直播分类
    private String title = "";// 内置浏览器标题
    private boolean loadTransparent;//loading是否透明
    private boolean cannotCancel;//dialog是否不能取消
    private boolean isShopUrl;//如果是购物大厅的界面 需要更换加载动画
    private static OpenUrlUtils sInstance;
    private ButtomDialogFragment fragment;
    private AlertDialog webDialog;
    private String jump_type; //1代表是正常url链接，2代表室本地html路由
    private String is_king; //1代表是要带，其他表示不要
    private int slide_show_type_button = 0; //0-有按钮 1-无按钮(全屏横全屏竖)
    private DialogInterface.OnDismissListener listener; //监听弹窗消失
    private Handler handler = new Handler();
    private DissmissDialogListener ondissDialogListener; //监听底部弹窗消失

    private String htmlUrl;//路由地址
    private WeakReference<Context> mReference;

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public OpenUrlUtils setHtmlUrls(String htmlUrl) {
        this.htmlUrl = htmlUrl;
        return sInstance;
    }

    public DialogInterface.OnDismissListener getListener() {
        return listener;
    }

    public OpenUrlUtils setListener(DialogInterface.OnDismissListener listener) {
        this.listener = listener;
        return sInstance;
    }


    public OpenUrlUtils setOndissDialogListener(DissmissDialogListener ondissDialogListener) {
        this.ondissDialogListener = ondissDialogListener;
        return sInstance;
    }


    public String getIs_king() {
        return is_king;
    }

    public OpenUrlUtils setIs_king(String is_king) {
        this.is_king = is_king;
        return sInstance;
    }

    public String getJump_type() {
        return jump_type;
    }

    public OpenUrlUtils setJump_type(String jump_type) {
        this.jump_type = jump_type;
        return sInstance;
    }

    public boolean isShopUrl() {
        return isShopUrl;
    }

    public OpenUrlUtils setShopUrl(boolean shopUrl) {
        isShopUrl = shopUrl;
        return sInstance;
    }

    public static OpenUrlUtils getInstance() {
        if (sInstance == null) {
            sInstance = new OpenUrlUtils();
        }
        sInstance.setHtmlUrl(null);
        return sInstance;
    }

    public OpenUrlUtils setContext(Context context) {
        if (null != context) {
            mReference = new WeakReference<>(context);//防止内存泄漏
        }
        return sInstance;
    }

    public OpenUrlUtils setType(int type) {
        this.type = type;
        return sInstance;
    }

    public String getTitle() {
        return title;
    }

    public OpenUrlUtils setTitle(String title) {
        this.title = title;
        return sInstance;
    }

    public boolean isCannotCancel() {
        return cannotCancel;
    }

    public OpenUrlUtils setCannotCancel(boolean cannotCancel) {
        this.cannotCancel = cannotCancel;
        return sInstance;
    }

    public boolean isLoadTransparent() {
        return loadTransparent;
    }

    public OpenUrlUtils setLoadTransparent(boolean loadTransparent) {
        this.loadTransparent = loadTransparent;
        return sInstance;
    }

    public String getSlideTarget() {
        return slide_target;
    }

    public OpenUrlUtils setSlideTarget(String slide_target) {
        this.slide_target = slide_target;
        return sInstance;
    }

    public int getSlide_show_type_button() {
        return slide_show_type_button;
    }

    public OpenUrlUtils setSlide_show_type_button(int slide_show_type_button) {
        this.slide_show_type_button = slide_show_type_button;
        return sInstance;
    }

    /**
     * data.getSlide_target(),  1是跳转url  其他是跳转回直播分类
     * data.getSlide_show_type(),  展示类型
     * data.getSlide_url()
     * https://user.im1888.com/Index?tbc=true&appId=5f816d426b7c1df8186a4373&loginType=4&deviceId=
     */
    public void go(String url) {
        //如果是本地路由，且url没有处理过，先请求token和uidcan参数组装本地路径url
//        setHtmlUrl(url);
        htmlUrl = null;
//        if(!jump_type.equals("2") && url.endsWith("deviceId=")){
        if (url.endsWith("deviceId=")) {
            url = url + DeviceUtils.getUniqueDeviceId();
        }
        if (!TextUtils.isEmpty(jump_type) && "2".equals(jump_type) && !url.startsWith("file:///android_asset") && !url.startsWith("http")) {
            getHtmlTokenUid(url);
            setHtmlUrl(url);
            return;
        } else if (is_king != null && is_king.equals("1") && "1".equals(jump_type) && !url.contains("token")) {
            addUidToken(url);
            return;
        }
        L.e("缓存：", url);


        if ("1".equals(slide_target)) {// 1是跳转url
            switch (type) {
                case 1://全屏-横屏
                    SmallProgramActivity.toSmallActivity(mReference.get(), url, isShopUrl, TextUtils.isEmpty(htmlUrl) ? url : htmlUrl, slide_show_type_button);
                    break;
                case 2://浏览器
                    toBrowser(url);
                    break;
                case 3://内置
                    new SmallProgramTitleActivity().toActivity(mReference.get(), url, title, isShopUrl, TextUtils.isEmpty(htmlUrl) ? url : htmlUrl);
                    break;
                case 4://底部弹出
                    Log.e("URL:", url);
                    openMorewebWindow(mReference.get(), url, isLoadTransparent(), !cannotCancel, isShopUrl, TextUtils.isEmpty(htmlUrl) ? url : htmlUrl);
                    break;
                case 5://弹窗
                    WebUtils webUtils = new WebUtils();
                    webUtils.showWebViewDialog(mReference.get(), url, isShopUrl, TextUtils.isEmpty(htmlUrl) ? url : htmlUrl);
                    webDialog = webUtils.getShareDialog();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            webDialog.setOnDismissListener(listener);
                        }
                    }, 100);

                    break;
                case 6://右边弹出
                    WebViewActivity.forwardRight(mReference.get(), url, title, isShopUrl, TextUtils.isEmpty(htmlUrl) ? url : htmlUrl);
                    break;
                case 7://
                    break;
                case 8://全屏-竖屏
                    new SmallProgramActivity().toActivity(mReference.get(), url, isShopUrl, TextUtils.isEmpty(htmlUrl) ? url : htmlUrl, slide_show_type_button);
                    break;
            }
        } else {
            EventBus.getDefault().post(new OnSetChangeEvent(url, mReference.get()));
            if (mReference.get() instanceof Activity) {
                Activity callbackActivity = (Activity) mReference.get();
                String className = callbackActivity.getClass().getName();
                if (className.equals("LiveAudienceActivity")) {
                    EventBus.getDefault().post(new LiveCloseEvent());
                }
            }
        }
    }

    /**
     * url添加uid&token
     *
     * @param url
     */
    private void addUidToken(final String url) {
        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.HTML_TOKEN))) {
            List<TokenUtilsBean> tokenUtilsBeans = JSON.parseArray(SPUtils.getInstance().getString(Constants.HTML_TOKEN), TokenUtilsBean.class);
            String uidToken = tokenUtilsBeans.get(0).getQueryStr() ;
            go(url + (url.contains("?") ? "&" : "?") + uidToken);
            return;
        }

        CommonHttpUtil.getUidToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info == null || info.length == 0) {
                    SPUtils.getInstance().put(Constants.HTML_TOKEN, "");
                    go(url);
                    return;
                }
                List<TokenUtilsBean> tokens = JSON.parseArray(Arrays.toString(info), TokenUtilsBean.class);
                SPUtils.getInstance().put(Constants.HTML_TOKEN, JSON.toJSONString(tokens));
                String uidToken = tokens.get(0).getQueryStr();
                go(url + (url.contains("?") ? "&" : "?") + uidToken);
            }
        });
    }

    /**
     * 组装本地路由路径
     */
    private void getHtmlTokenUid(final String url) {
        //如果不是1，不需要token,就拼接路由
        if (is_king != null && !is_king.equals("1")) {
            String htmlPath = "";
            if (TextUtils.isEmpty(url)) {
                htmlPath = "file:///android_asset/dist/index.html";
            } else {
                htmlPath = "file:///android_asset/dist/index.html#" + url;
            }
            go(htmlPath);
            return;
        }

        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.HTML_TOKEN))) {
            List<TokenUtilsBean> tokenUtilsBeans = JSON.parseArray(SPUtils.getInstance().getString(Constants.HTML_TOKEN), TokenUtilsBean.class);
            goHtml(tokenUtilsBeans, url);
            return;
        }

        CommonHttpUtil.getUidToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info == null || info.length == 0) {
                    SPUtils.getInstance().put(Constants.HTML_TOKEN, "");
                    go("file:///android_asset/dist/index.html#" + url);
                    return;
                }
                List<TokenUtilsBean> tokens = JSON.parseArray(Arrays.toString(info), TokenUtilsBean.class);
                SPUtils.getInstance().put(Constants.HTML_TOKEN, JSON.toJSONString(tokens));
                goHtml(tokens, url);
            }
        });
    }

    private void goHtml(List<TokenUtilsBean> tokens, String url) {
        String queryStr = tokens.get(0).getQueryStr();
        String htmlPath = "";
        if (TextUtils.isEmpty(url)) {
            htmlPath = "file:///android_asset/dist/index.html#/?" + queryStr;
        } else {
            if (url.contains("?")) {
                htmlPath = "file:///android_asset/dist/index.html#" + url + "&" + queryStr;
            } else {
                htmlPath = "file:///android_asset/dist/index.html#" + url + "?" + queryStr;
            }
        }
        Log.e("---htmlPath--", htmlPath);
        htmlUrl = url;
//        setHtmlUrl(url);
        go(htmlPath);
    }

    /**
     * 打开更多web窗口
     */
    public void openMorewebWindow(Context context, String url, boolean aplha, boolean canCancel, boolean isShopUrl, String htmlUrl) {
        fragment = new ButtomDialogFragment(aplha, canCancel, isShopUrl, htmlUrl);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MORE_WEB_URL, url);
        fragment.setArguments(bundle);
        fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "LiveMoreDialogFragment");
        if (ondissDialogListener != null) {
            fragment.setOnDissmissDialogListener(ondissDialogListener);
        }
    }

    /**
     * @return 打开更多web窗口 时获取dialog
     */
    public ButtomDialogFragment getMoreDialog() {
        return fragment;
    }


    /**
     * 跳转浏览器
     *
     * @param url
     */
    private void toBrowser(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        try {
            mReference.get().startActivity(intent);
        } catch (Exception e) {
            Log.i("WOLF", "url错误");
        }
    }
}
