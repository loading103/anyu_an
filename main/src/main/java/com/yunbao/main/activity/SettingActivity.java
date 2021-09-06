package com.yunbao.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.jpush.Gson;
import com.google.gson.jpush.reflect.TypeToken;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.AdvertBean;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.TabBarBean;
import com.yunbao.common.event.MainFinishEvent;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.GlideCatchUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.VersionUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.im.utils.ImPushUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.SettingAdapter;
import com.yunbao.main.bean.SettingBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.blankj.utilcode.util.LogUtils.getConfig;

/**
 * Created by cxf on 2018/9/30.
 */

public class SettingActivity extends AbsActivity implements OnItemClickListener<SettingBean> {

    private RecyclerView mRecyclerView;
    private Handler mHandler;
    private SettingAdapter mAdapter;
    private TextView tv_finish;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.setting));
        tv_finish=(TextView) findViewById(R.id.tv_finish);
        tv_finish.setVisibility(View.GONE);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        MainHttpUtil.getSettingList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<SettingBean> list = JSON.parseArray(Arrays.toString(info), SettingBean.class);
                SettingBean bean = new SettingBean();
                bean.setName(WordUtil.getString(R.string.setting_exit));
                bean.setLast(true);
                list.add(bean);

                SettingBean bean1 = new SettingBean();
                bean1.setName(WordUtil.getString(R.string.setting_switch));
                list.add(0,bean1);
                mAdapter = new SettingAdapter(mContext, list, VersionUtil.getVersion(), getCacheSize());
                mAdapter.setOnItemClickListener(SettingActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }


    @Override
    public void onItemClick(SettingBean bean, int position) {
        String href = bean.getHref();
        if (TextUtils.isEmpty(href)) {
            if (bean.isLast()) {//退出登录
                logout();
            } else if (bean.getId() == Constants.SETTING_MODIFY_PWD) {//修改密码
                forwardModifyPwd();
            } else if (bean.getId() == Constants.SETTING_UPDATE_ID) {//检查更新
                checkVersion();
            } else if (bean.getId() == Constants.SETTING_CLEAR_CACHE) {//清除缓存
                clearCache(position);
            }
        } else {
            if (bean.getId() == 17) {//意见反馈要在url上加版本号和设备号
                href += "&version=" + android.os.Build.VERSION.RELEASE + "&model=" + android.os.Build.MODEL;
            }
            WebViewActivity.forward(mContext, href);
        }
    }

    /**
     * 检查更新
     */
    private void checkVersion() {
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (VersionUtil.isLatest(configBean.getVersion())) {
                        ToastUtil.show(R.string.version_latest);
                    } else {
                        VersionUtil.showDialog(mContext, configBean.getUpdateDes(), configBean.getDownloadApkUrl());
                    }
                }
            }
        });

    }

    /**
     * 退出登录
     */
    private void logout() {

        String token = SpUtil.getInstance().getStringValue(SpUtil.CUSTOM_TOKEN);
        String uid = SpUtil.getInstance().getStringValue(SpUtil.CUSTOM_UID);
        //退出的时候让协议状态清空
        SpUtil.getInstance().setBooleanValue(SpUtil.VIDEO_XIEYI,false);
        SpUtil.getInstance().setBooleanValue(SpUtil.RECHARGE_XIEYI,false);

        CommonAppConfig.getInstance().clearLoginInfo();
        //退出极光
        ImMessageUtil.getInstance().logoutImClient();
        ImPushUtil.getInstance().logout();
        //友盟统计登出
        MobclickAgent.onProfileSignOff();
        //如果是游客模式，返回到首页g
        getConfigData(uid,token);

    }
    private Dialog dialog;
    private void getConfigData(final String uid, final String token) {
        if(dialog==null && !isDestroyed()){
            dialog = DialogUitl.loadingNew2WhiteDialog(mContext);
            dialog.show();
        }
        CommonHttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                String tabString = SpUtil.getInstance().getStringValue(SpUtil.FAST_TABBAR_LIST);
                List<TabBarBean.ListBean> tabdatas = new Gson().fromJson(tabString, new TypeToken<List<TabBarBean.ListBean>>() {}.getType());

                boolean isToLogin;
                if(!TextUtils.isEmpty(tabString)){
                    isToLogin= tabdatas.get(0).getId().equals("4") || tabdatas.get(0).getId().equals("6");//首页是视频或充值的游客模式跳转登录
                }else {
                    isToLogin=false;
                }

                if(bean.getLogin_way().equals("1")&&!isToLogin) {
                   SpUtil.getInstance().setStringValue(SpUtil.CUSTOM_TOKEN,bean.getToken());
                    SpUtil.getInstance().setStringValue(SpUtil.CUSTOM_UID,bean.getUid());
                    String token1 = SpUtil.getInstance().getStringValue(SpUtil.CUSTOM_TOKEN);
                    String uid1 = SpUtil.getInstance().getStringValue(SpUtil.CUSTOM_UID);
                    CommonAppConfig.getInstance().setmUid(uid1);
                    CommonAppConfig.getInstance().setVisitor(true);
                    EventBus.getDefault().post(new MainFinishEvent());
                    toMainActivity(uid1,token1);
                }else {
                    if(dialog!=null && !isDestroyed()){
                        dialog.dismiss();
                    }
                    CommonAppConfig.getInstance().setVisitor(false);
                    Intent intent = new Intent(SettingActivity.this, LoginNewActivity.class);
                    startActivity(intent);
                    EventBus.getDefault().post(new MainFinishEvent());
                    finish();
                }
            }
        });


    }

    /**
     * 切换到游客分层，比如游客只能看三个tabbar内容  登陆后能看五个  所以这里需要调用此接口
     */
    private void toMainActivity(final String uid, final String token) {
        MainHttpUtil.getAdvertCache1("",uid,token,new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(dialog!=null && !isDestroyed()){
                    dialog.dismiss();
                }
                if (code == 0) {
                    List<AdvertBean> advertBean = JSON.parseArray(Arrays.toString(info), AdvertBean.class);
                    if(advertBean!=null || advertBean.size()>0) {
                        CommonAppConfig.getInstance().setAdvert(JSON.toJSONString(advertBean.get(0)));
                        if(advertBean.get(0).getTabbar()!=null){
                            SpUtil.getInstance().setStringValue(SpUtil.FAST_TABBAR_LIST,JSON.toJSONString(advertBean.get(0).getTabbar()));
                        }
                        CommonAppConfig.getInstance().setHotList(Arrays.toString(info));
                        advertBean.get(0).getList().clear();
                    }
                    toMain(uid,token);
                }else {
                    toMain(uid,token);
                }
            }

            @Override
            public void onCacheSuccess(Response<JsonBean> response) {
                super.onCacheSuccess(response);
                if(dialog!=null && !isDestroyed()){
                    dialog.dismiss();
                }
                List<AdvertBean> advertBean = JSON.parseArray(Arrays.toString(response.body().getData().getInfo()), AdvertBean.class);
                if(advertBean!=null && advertBean.size()>0){
                    CommonAppConfig.getInstance().setAdvert(JSON.toJSONString(advertBean.get(0)));
                    CommonAppConfig.getInstance().setHotList(Arrays.toString(response.body().getData().getInfo()));
                    advertBean.get(0).getList().clear();
                    if(advertBean.get(0).getTabbar()!=null){
                        SpUtil.getInstance().setStringValue(SpUtil.FAST_TABBAR_LIST,JSON.toJSONString(advertBean.get(0).getTabbar()));
                    }
                }
                toMain(uid,token);
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                if(dialog!=null && !isDestroyed()){
                    dialog.dismiss();
                }
                toMain(uid,token);
            }
        });
    }

    private void toMain(String uid,String token) {
        Intent intent = new Intent(this, MainActivity.class);
        CommonAppConfig.getInstance().setmUid(uid);
        CommonAppConfig.getInstance().setmToken(token);
        startActivity(intent);
        finish();
    }

    /**
     * 修改密码
     */
    private void forwardModifyPwd() {
        startActivity(new Intent(mContext, ModifyPwdActivity.class));
    }

    /**
     * 获取缓存
     */
    private String getCacheSize() {
        return GlideCatchUtil.getInstance().getCacheSize();
    }

    /**
     * 清除缓存
     */
    private void clearCache(final int position) {
        final Dialog dialog = DialogUitl.loadingDialog(mContext, getString(R.string.setting_clear_cache_ing));
        dialog.show();
        GlideCatchUtil.getInstance().clearImageAllCache();
        File gifGiftDir = new File(CommonAppConfig.GIF_PATH);
        if (gifGiftDir.exists() && gifGiftDir.length() > 0) {
            gifGiftDir.delete();
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (mAdapter != null) {
                    mAdapter.setCacheString(getCacheSize());
                    mAdapter.notifyItemChanged(position);
                }
                ToastUtil.show(R.string.setting_clear_cache);
            }
        }, 2000);
    }


    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        MainHttpUtil.cancel(MainHttpConsts.GET_SETTING_LIST);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        super.onDestroy();
    }
}
