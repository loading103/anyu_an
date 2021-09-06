package com.yunbao.main.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.AdvertBean;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.mob.LoginData;
import com.yunbao.common.mob.MobBean;
import com.yunbao.common.mob.MobCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.utils.ImPushUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.LoginTypeAdapter;
import com.yunbao.main.bean.RecommendBean;
import com.yunbao.main.event.RegSuccessEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by cxf on 2018/9/17.
 */

public class LoginActivity2 extends AbsActivity implements EasyPermissions.PermissionCallbacks, OnItemClickListener<MobBean> {

    private EditText mEditPhone;
    private EditText mEditPwd;
    private ImageView mBtnLogin;
    private RecyclerView mRecyclerView;
    private boolean mFirstLogin;//是否是第一次登录
    private boolean mShowInvite;//显示邀请码弹窗
    private String mLoginType = Constants.MOB_PHONE;//登录方式
    private TextView mBtnTp;
    private Dialog dialog;
    String[] perms = {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};
    @Override
    protected int getLayoutId() {
        return R.layout.activity_login2;
    }

    @Override
    protected void main() {
        mEditPhone = (EditText) findViewById(R.id.edit_phone);
        mEditPwd = (EditText) findViewById(R.id.edit_pwd);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnTp = findViewById(R.id.btn_tip);
        mBtnTp.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        mBtnLogin.setEnabled(false);
        mBtnLogin.setImageResource(R.mipmap.icon_login_right2);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mEditPhone.getText().toString();
                String pwd = mEditPwd.getText().toString();
                mBtnLogin.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd));
                mBtnLogin.setImageResource((!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd))?
                        R.mipmap.icon_login_right:R.mipmap.icon_login_right2);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mEditPhone.addTextChangedListener(textWatcher);
        mEditPwd.addTextChangedListener(textWatcher);
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            List<MobBean> list = MobBean.getLoginTypeList(configBean.getLoginType());
            if (list != null && list.size() > 0) {
                mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                LoginTypeAdapter adapter = new LoginTypeAdapter(mContext, list);
                adapter.setOnItemClickListener(this);
                mRecyclerView.setAdapter(adapter);
            }
        }
        EventBus.getDefault().register(this);
        requestPermissions();
    }


    public static void forward() {
        Intent intent = new Intent(CommonAppContext.sInstance, LoginActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        CommonAppContext.sInstance.startActivity(intent);
    }


    public void loginClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_login) {
            login();

        } else if (i == R.id.btn_register) {
            register();

        } else if (i == R.id.btn_tip) {
            forwardTip();
        } else if (i == R.id.btn_service) {
            getService(this);
        } else if (i == R.id.tv_forget) {
//            if (TextUtils.isEmpty(CommonAppConfig.getInstance().getConfig().getForget_pass())) {
//                ToastUtil.show("忘记密码功能异常，请稍后再试");
//            } else {
//                if (CommonAppConfig.getInstance().getConfig().getForget_pass_type().equals("1")) {
//                    WebViewActivity.forwardfalse(mContext, CommonAppConfig.getInstance().getConfig().getForget_pass(), "忘记密码");
//                } else {
//                    String htmlPath = "file:///android_asset/dist/index.html#" + CommonAppConfig.getInstance().getConfig().getForget_pass();
//                    WebViewActivity.forwardfalse(mContext, htmlPath, "忘记密码");
//                }
//            }
            showDialog(this);
        }
    }

    /**
     * 联系客服
     */
    private static void getService(final Context context) {
        if (CommonAppConfig.getInstance().getConfig().getCustomer_service().equals("0")) {
            ToastUtil.show("客服系统异常，请稍后再试");
        } else {
            ConfigBean.CustomerServiceData data = CommonAppConfig.getInstance().getConfig().getCustomer_service_data();
            String url;
            if (!data.getJump_type().equals("2")) {
                url = data.getSlide_url() + DeviceUtils.getUniqueDeviceId();
            } else {
                url = data.getSlide_url();
            }
            OpenUrlUtils.getInstance().setContext(context)
                    .setType(Integer.parseInt(data.getSlide_show_type()))
                    .setSlideTarget(data.getSlide_target())
                    .setTitle(data.getName())
                    .setJump_type(data.getJump_type())
                    .setIs_king("0")
                    .setSlide_show_type_button(data.getSlide_show_type_button())
                    .go(url);
        }
    }

    public static void showDialog(final Context context) {
        DialogUitl.Builder builder = new DialogUitl.Builder(context);
        builder.setTitle("温馨提示")
                .setContent(WordUtil.getString(R.string.forget_password))
                .setConfrimString("联系客服")
                .setCancelString("暂不联络")
                .setCancelable(true)
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        getService(context);
                    }
                })
                .build()
                .show();
    }

    //注册
    private void register() {
        startActivity(new Intent(mContext, RegisterActivity2.class));
        overridePendingTransition(0, 0);
    }

    //忘记密码
    private void forgetPwd() {
        startActivity(new Intent(mContext, FindPwdActivity.class));
    }


    //手机号密码登录
    private void login() {
        String phoneNum = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < 6) {
            mEditPhone.setError(WordUtil.getString(R.string.reg_input_phone));
            mEditPhone.requestFocus();

            return;
        }


        String pwd = mEditPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            mEditPwd.setError(WordUtil.getString(R.string.login_input_pwd));
            mEditPwd.requestFocus();
            return;
        }
        mLoginType = Constants.MOB_PHONE;
        MainHttpUtil.login(phoneNum, pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
//            @Override
//            public boolean showLoadingDialog() {
//                return true;
//            }
            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingNew3Dialog(mContext);
            }
        });

    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public boolean isabc(String str) {
        Pattern p = Pattern.compile("^[A-Za-z]+$");
        Matcher m = p.matcher(str);
        boolean isValid = m.matches();
        return isValid;
    }

    //登录即代表同意服务和隐私条款
    private void forwardTip() {
        HtmlConfig config = new HtmlConfig();
        WebViewActivity.forward(mContext, config.getLOGIN_PRIVCAY());
    }

    //登录成功！
    private void onLoginSuccess(int code, String msg, String[] info) {
        if (code == 0 && info.length > 0) {
            JSONObject obj = JSON.parseObject(info[0]);
            String uid = obj.getString("id");
            String token = obj.getString("token");
            mFirstLogin = obj.getIntValue("isreg") == 1;
            mShowInvite = obj.getIntValue("isagent") == 1;
            CommonAppConfig.getInstance().setLoginInfo(uid, token, true);
            SpUtil.getInstance().setBooleanValue(SpUtil.NEW_USER,mFirstLogin);
            String invitecode = obj.getString("invitecode");
            if(invitecode!=null){
                SpUtil.getInstance().setStringValue(SpUtil.INVITE_MEMBER,obj.getString("invitecode"));
            }

            getBaseUserInfo();
            //友盟统计登录
            MobclickAgent.onProfileSignIn(mLoginType, uid);
            ImPushUtil.getInstance().resumePush();
        } else  if (code ==10042 && info.length > 0) {
            String phone = mEditPhone.getText().toString().trim();
            String pwd = mEditPwd.getText().toString().trim();
            JSONObject obj = JSON.parseObject(info[0]);
            String uid = obj.getString("uid");
            Intent intent =new Intent(LoginActivity2.this,EditMyInforActivity.class);
            intent.putExtra("phone",phone);
            intent.putExtra("uid",uid);
            intent.putExtra("pwd",pwd);
            startActivity(intent);
        } else {
            ToastUtil.show(msg);
        }
    }

    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        if(!LoginActivity2.this.isDestroyed()){
            if(dialog==null){
                dialog = DialogUitl.loadingNew3Dialog(this);
            }
            dialog.show();
            MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
                @Override
                public void callback(UserBean bean) {
                    getAdvert();
                }
            });
        }
    }

    /**
     * 首页预加载
     */
    private void getAdvert(){
        MainHttpUtil.getAdvertCache("", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(!LoginActivity2.this.isFinishing() && dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
                if (code == 0) {
                    List<AdvertBean> advertBean = JSON.parseArray(Arrays.toString(info), AdvertBean.class);
                    if(advertBean!=null && advertBean.size()>0) {
                        CommonAppConfig.getInstance().setAdvert(JSON.toJSONString(advertBean.get(0)));
                        CommonAppConfig.getInstance().setHotList(Arrays.toString(info));
                        advertBean.get(0).getList().clear();
                    }
                    toMain();
                }else {
                    toMain();
                }
            }

            @Override
            public void onCacheSuccess(Response<JsonBean> response) {
                super.onCacheSuccess(response);
                L.e("缓存：onCacheSuccess:"+Arrays.toString(response.body().getData().getInfo()));
                List<AdvertBean> advertBean = JSON.parseArray(Arrays.toString(response.body().getData().getInfo()), AdvertBean.class);
                if(advertBean!=null && advertBean.size()>0){
                    CommonAppConfig.getInstance().setAdvert(JSON.toJSONString(advertBean.get(0)));
                    CommonAppConfig.getInstance().setHotList(Arrays.toString(response.body().getData().getInfo()));
                    advertBean.get(0).getList().clear();
                }
                toMain();
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                if(!LoginActivity2.this.isFinishing() && dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }

                toMain();
            }
        });
    }

    private void toMain() {
        if (mFirstLogin) {
//            RecommendActivity.forward(mContext, mShowInvite);
            //判断有没得推介列表
            MainHttpUtil.getRecommend(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        List<RecommendBean> list = JSON.parseArray(Arrays.toString(info), RecommendBean.class);
                        if(list==null || list.size()==0){
                            MainActivity.forward(mContext, mShowInvite,true);
                        }else {
                            RecommendActivity.forward(mContext, mShowInvite);
                            finish();
                        }
//                        finish();
                    }
                }
            });
        } else {
            MainActivity.forward(mContext, mShowInvite,true);
//            finish();
        }
        SPUtils.getInstance().remove(Constants.HTML_TOKEN);
    }

    /**
     * 三方登录
     */
    private void loginBuyThird(LoginData data) {
        mLoginType = data.getType();
        MainHttpUtil.loginByThird(data.getOpenID(), data.getNickName(), data.getAvatar(), data.getType(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
        });
    }

    @Override
    public void onItemClick(MobBean bean, int position) {
//        if (mLoginUtil == null) {
//            return;
//        }
//        final Dialog dialog = DialogUitl.loginAuthDialog(mContext);
//        dialog.show();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegSuccessEvent(RegSuccessEvent e) {
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_QQ_LOGIN_UNION_ID);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN_BY_THIRD);
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);

        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
            dialog=null;
        }

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

}
