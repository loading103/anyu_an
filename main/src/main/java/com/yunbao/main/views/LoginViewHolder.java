package com.yunbao.main.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.OpenUrlUtils;
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
import com.yunbao.common.utils.IMHideInPutUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.im.utils.ImPushUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.EditMyInforActivity;
import com.yunbao.main.activity.FindPwdActivity;
import com.yunbao.main.activity.LoginAndRegistActivity;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.main.activity.RecommendActivity;
import com.yunbao.main.adapter.LoginTypeAdapter;
import com.yunbao.main.bean.RecommendBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginViewHolder extends AbsViewHolder implements View.OnClickListener, OnItemClickListener<MobBean>, TextView.OnEditorActionListener {
    private EditText mEditPhone;
    private EditText mEditPwd;
    private TextView btn_register;
    private TextView btn_login;
    private TextView tv_forget;
    private TextView btn_service;
    private RecyclerView mRecyclerView;
    private boolean mFirstLogin;//是否是第一次登录
    private boolean mShowInvite;//显示邀请码弹窗
    private String mLoginType = Constants.MOB_PHONE;//登录方式
    private TextView mBtnTp;
    private Dialog dialog;

    private LinearLayout ll_eye;
    private ImageView iv_eye;
    private boolean   canSee;//密码可见
    public LoginViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_login;
    }

    @Override
    public void init() {
        main();
    }

    private void main() {
        mEditPhone = (EditText) findViewById(R.id.edit_phone);
        mEditPwd = (EditText) findViewById(R.id.edit_pwd);
        ll_eye   =  (LinearLayout) findViewById(R.id.ll_eye);
        iv_eye   =  (ImageView)findViewById(R.id.iv_eye);
        mEditPhone.setOnEditorActionListener(this);
        mEditPwd.setOnEditorActionListener(this);
        ll_eye.setOnClickListener(this);
        initListener();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mEditPhone.getText().toString();
                String pwd = mEditPwd.getText().toString();
                boolean enbleClick = !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd);
                btn_login.setEnabled(enbleClick);
                btn_login.setTextColor(enbleClick?mContext.getResources().getColor(R.color.color_666666) :mContext.getResources().getColor(R.color.white));
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
    }

    private void initListener() {
        btn_register=(TextView)findViewById(R.id.btn_register);
        btn_login=(TextView)findViewById(R.id.btn_login);
        btn_service=(TextView)findViewById(R.id.btn_service);
        tv_forget=(TextView)findViewById(R.id.tv_forget);
        mBtnTp =(TextView)findViewById(R.id.btn_tip);
        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_service.setOnClickListener(this);
        tv_forget.setOnClickListener(this);
        mBtnTp.setOnClickListener(this);
        btn_login.setEnabled(false);
    }

    /**
     * 联系客服
     */
    private  void getService(final Context context) {
        if (CommonAppConfig.getInstance().getConfig().getCustomer_service().equals("0")) {
            ToastUtils.showShort("客服系统异常，请稍后再试");
        } else {
            ConfigBean.CustomerServiceData data = CommonAppConfig.getInstance().getConfig().getCustomer_service_data();
            String url =data.getSlide_url();
//            if (!data.getJump_type().equals("2")) {
//                url = data.getSlide_url() + DeviceUtils.getUniqueDeviceId();
//            } else {
//                url = data.getSlide_url();
//            }
            ((LoginAndRegistActivity)context).setStopVideo(true);
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

    public  void showDialog(final Context context) {
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
        ((LoginAndRegistActivity)mContext).showRegistView(true);
    }
    //忘记密码
    private void forgetPwd() {
        mContext.startActivity(new Intent(mContext, FindPwdActivity.class));
    }

    //手机号密码登录
    private void login() {
        String phoneNum = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < 6) {
//            mEditPhone.setError(WordUtil.getString(R.string.reg_input_phone));
            ToastUtils.showShort(WordUtil.getString(R.string.reg_input_phone));
            mEditPhone.requestFocus();
            return;
        }


        String pwd = mEditPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
//            mEditPwd.setError(WordUtil.getString(R.string.login_input_pwd));
            ToastUtils.showShort(WordUtil.getString(R.string.login_input_pwd));
            mEditPwd.requestFocus();
            return;
        }
        mLoginType = Constants.MOB_PHONE;
        if(!canClick()){
            return;
        }
        MainHttpUtil.login(phoneNum, pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }

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
            ((LoginAndRegistActivity)mContext).setStopVideo(true);
            String phone = mEditPhone.getText().toString().trim();
            String pwd = mEditPwd.getText().toString().trim();
            JSONObject obj = JSON.parseObject(info[0]);
            String uid = obj.getString("uid");
            Intent intent =new Intent(mContext, EditMyInforActivity.class);
            intent.putExtra("phone",phone);
            intent.putExtra("uid",uid);
            intent.putExtra("pwd",pwd);
            mContext.startActivity(intent);
        } else {
            ToastUtils.showShort(msg);
        }
    }

    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        if(!((LoginAndRegistActivity)mContext).isDestroyed()){
            if(dialog==null){
                dialog = DialogUitl.loadingNew3Dialog(mContext);
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
                if(!((LoginAndRegistActivity)mContext).isFinishing() && dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
                if (code == 0) {
                    List<AdvertBean> advertBean = JSON.parseArray(Arrays.toString(info), AdvertBean.class);
                    if(advertBean!=null && advertBean.size()>0) {
                        CommonAppConfig.getInstance().setAdvert(JSON.toJSONString(advertBean.get(0)));
                        if(advertBean.get(0).getTabbar()!=null){
                            SpUtil.getInstance().setStringValue(SpUtil.FAST_TABBAR_LIST,JSON.toJSONString(advertBean.get(0).getTabbar()));
                        }
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
                    if(advertBean.get(0).getTabbar()!=null){
                        SpUtil.getInstance().setStringValue(SpUtil.FAST_TABBAR_LIST,JSON.toJSONString(advertBean.get(0).getTabbar()));
                    }
                }
                toMain();
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                if(!((LoginAndRegistActivity)mContext).isFinishing() && dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }

                toMain();
            }
        });
    }

    private void toMain() {
        ((LoginAndRegistActivity)mContext).setStopVideo(true);
        if (mFirstLogin) {
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
                            ((LoginAndRegistActivity)mContext).finish();
                        }
                    }
                }
            });
        } else {
            MainActivity.forward(mContext, mShowInvite,true);
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
//        mLoginUtil.execute(bean.getType(), new MobCallback() {
//            @Override
//            public void onSuccess(Object data) {
//                if (data != null) {
//                    loginBuyThird((LoginData) data);
//                }
//            }
//
//            @Override
//            public void onError() {
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onFinish() {
//                if(!((LoginAndRegistActivity)mContext).isFinishing() && dialog != null && dialog.isShowing()){
//                    dialog.dismiss();
//                }
//            }
//        });
    }

    @Override
    public void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.LOGIN);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_QQ_LOGIN_UNION_ID);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN_BY_THIRD);
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
//        if (mLoginUtil != null) {
//            mLoginUtil.release();
//        }

        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
            dialog=null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if(!canClick()){
            return;
        }
        int i = view.getId();
        if (i == R.id.btn_login) {
            login();
        } else if (i == R.id.btn_register) {
            register();
        } else if (i == R.id.btn_tip) {
            ((LoginAndRegistActivity) mContext).setStopVideo(true);
            forwardTip();
        } else if (i == R.id.btn_service) {
            getService(mContext);
        } else if (i == R.id.tv_forget) {
            showDialog(mContext);
        }else if (view.getId() == R.id.ll_eye) {
            if(canSee){
                iv_eye.setImageResource(R.mipmap.password_no_eye);
                mEditPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }else {
                iv_eye.setImageResource(R.mipmap.password_eye);
                mEditPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            canSee=!canSee;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if ((actionId == 0 || actionId == 3) && event != null) {
            IMHideInPutUtil.hideInputMethod(mContext,textView);
            return true;
        }
        return false;

    }
}

