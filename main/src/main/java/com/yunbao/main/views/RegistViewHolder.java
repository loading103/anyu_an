package com.yunbao.main.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.AdvertBean;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.im.utils.ImPushUtil;
import com.yunbao.live.custom.MyCountDownTimer;
import com.yunbao.main.R;
import com.yunbao.main.activity.EditMyInforActivity;
import com.yunbao.main.activity.LoginAndRegistActivity;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.main.activity.RecommendActivity;
import com.yunbao.main.bean.RecommendBean;
import com.yunbao.main.event.RegSuccessEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.utils.IMDeviceIdUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class RegistViewHolder extends AbsViewHolder implements View.OnClickListener, TextWatcher, TextView.OnEditorActionListener {

    private EditText mEditPhone;
    private EditText mEditPwd1;
    //    private EditText mEditPwd2;
    private TextView mBtnRegister;
    private Handler mHandler;
    private static final int TOTAL = 60;
    private int mCount = TOTAL;
    private Dialog mDialog;
    private boolean mFirstLogin;//是否是第一次登录
    private boolean mShowInvite;
    private TextView ivBack;
    private TextView btn_tip;
    private Dialog dialog;
//    private LinearLayout btn_service;

    private EditText mEditYq;
    //短信验证码
    private LinearLayout mllPhoneCode;
    private EditText mEtPhoneCode;
    private TextView   mTvPhoneCode;
    //图形验证码
    private LinearLayout mllPicCode;
    private EditText mEtPicCode;
    private ImageView mIvPicCode;
    private MyCountDownTimer mTimer;
    //是不是手机验证码
    private  int   typeCode=0; //0是不需要手机号，1是图形验证码，2是短信验证码
    private String device_id;
    private FrameLayout flRoot;
    private LinearLayout llYq;
    private String layered ;
    private String invitationCode;
    private LinearLayout ll_eye;
    private ImageView iv_eye;
    private boolean   canSee;//密码可见
    public RegistViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_register;
    }

    @Override
    public void init() {
        invitationCode = getMetaDataString("INVITATION_CODE");
        setTitle(WordUtil.getString(R.string.reg_register));
        findIdView();
        initListener();
        initView();
        device_id = IMDeviceIdUtil.getDeviceID(mContext);
        mDialog = DialogUitl.loadingDialog(mContext, mContext.getString(R.string.reg_register_ing));
        //如果不是电话号码，就获取图形验证码
        if(typeCode==1){
            getTuCode();
        }
        String type = ((LoginAndRegistActivity) mContext).getIntent().getStringExtra("type");
        if(type==null||!type.equals("LauncherActivity")){
            //跟新注册界面验证码类型
            CommonHttpUtil.getConfig(new CommonCallback<ConfigBean>() {
                @Override
                public void callback(ConfigBean bean) {
                }
            });
        }

        layered = getMetaDataString("LAYERED_ID");
    }

    public void initCodeType() {
        String stringValue = SpUtil.getInstance().getStringValue(SpUtil.DUAN_XIN);
        if(TextUtils.isEmpty(stringValue) || stringValue.equals("0")){
            typeCode=0;
            mllPhoneCode.setVisibility(View.GONE);
            mllPicCode.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(stringValue) && stringValue.equals("1")){
            typeCode=1;
            mllPhoneCode.setVisibility(View.GONE);
            mllPicCode.setVisibility(View.VISIBLE);
            getTuCode();
        }
        if(!TextUtils.isEmpty(stringValue) && stringValue.equals("2")){
            typeCode=2;
            mllPhoneCode.setVisibility(View.VISIBLE);
            mEditPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
            mEditPhone.setHint("请输入手机号");
            mllPicCode.setVisibility(View.GONE);
        }
    }


    private void findIdView() {
        mEditPhone =  (EditText) findViewById(R.id.edit_phone);
        mEditPwd1 =   (EditText)findViewById(R.id.edit_pwd_1);
//        mEditPwd2 =   (EditText)findViewById(R.id.edit_pwd_2);
        mBtnRegister = (TextView) findViewById(R.id.btn_register);
        ivBack =(TextView)findViewById(R.id.iv_back);
        mllPhoneCode = (LinearLayout) findViewById(R.id.ll_phone_code);
        mEtPhoneCode =  (EditText)findViewById(R.id.et_photo_code);
        mTvPhoneCode =  (TextView)findViewById(R.id.tv_photo_code);
        mllPicCode =   (LinearLayout) findViewById(R.id.ll_pic_code);
        mEtPicCode =    (EditText) findViewById(R.id.edit_code);
        mIvPicCode =   (ImageView) findViewById(R.id.pic_code);
        mEditYq  =   (EditText) findViewById(R.id.et_yq);
        flRoot  =   (FrameLayout) findViewById(R.id.fl_root);
        llYq  =   (LinearLayout) findViewById(R.id.ll_yq);
        btn_tip   =  (TextView)findViewById(R.id.btn_tip);
        ll_eye   =  (LinearLayout) findViewById(R.id.ll_eye);
        iv_eye   =  (ImageView)findViewById(R.id.iv_eye);
        mBtnRegister.setEnabled(false);
    }

    private void initListener() {
        ivBack.setOnClickListener(this);
        mTvPhoneCode.setOnClickListener(this);
        mIvPicCode.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mBtnRegister.setEnabled(false);
        btn_tip.setOnClickListener(this);
        ll_eye.setOnClickListener(this);
        mEditPhone.addTextChangedListener(this);
        mEditPwd1.addTextChangedListener(this);
//        mEditPwd2.addTextChangedListener(this);
        mEtPicCode.addTextChangedListener(this);
        mEtPhoneCode.addTextChangedListener(this);
        mEditPhone.setOnEditorActionListener(this);
        mEditPwd1.setOnEditorActionListener(this);
        mEtPicCode.setOnEditorActionListener(this);
    }
    private void initView() {
        String stringValue = SpUtil.getInstance().getStringValue(SpUtil.DUAN_XIN);
        if(TextUtils.isEmpty(stringValue) || stringValue.equals("0")){
            typeCode=0;
            mllPhoneCode.setVisibility(View.GONE);
            mllPicCode.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(stringValue) && stringValue.equals("1")){
            typeCode=1;
            mllPhoneCode.setVisibility(View.GONE);
            mllPicCode.setVisibility(View.VISIBLE);
        }
        if(!TextUtils.isEmpty(stringValue) && stringValue.equals("2")){
            typeCode=2;
            mllPhoneCode.setVisibility(View.VISIBLE);
            mEditPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
            mEditPhone.setHint("请输入手机号");
            mllPicCode.setVisibility(View.GONE);
        }

        if(CommonAppConfig.getInstance().getConfig()!=null && CommonAppConfig.getInstance().getConfig()
                .getRegister_invitation_switch()==1&&TextUtils.isEmpty(invitationCode)){
            llYq.setVisibility(View.VISIBLE);
        }else {
            llYq.setVisibility(View.GONE);
        }
    }


    private void changeEnable() {
        String phone = mEditPhone.getText().toString();
        String code ="";
        if(typeCode==2){
            code = mEtPhoneCode.getText().toString();
        }
        if(typeCode==1){
            code = mEtPicCode.getText().toString();
        }
        String pwd1 = mEditPwd1.getText().toString();
        boolean havedata =false;
        if(typeCode==0){
            havedata = !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd1)  ;
        }else {
            havedata = !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd1)  && !TextUtils.isEmpty(code);
        }
        mBtnRegister.setEnabled(havedata);
        mBtnRegister.setTextColor(havedata?mContext.getResources().getColor(R.color.color_666666) :mContext.getResources().getColor(R.color.white));

    }


    /**
     * 获取验证码
     */
    private void getCode() {
        final String phoneNum = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum) ||  phoneNum.length()<6 ) {
//            mEditPhone.setError(WordUtil.getString(R.string.toast_phone));
            ToastUtils.showShort("请输入正确格式的手机号码");
            mEditPhone.requestFocus();
            return;
        }

        if(!RegexUtils.isMobileSimple(phoneNum)){
            ToastUtils.showShort("请输入正确格式的手机号码");
            mEditPhone.requestFocus();
            return;
        }

        if( phoneNum.length()!=11){ //如果不是11位全数字，必须是字母开头
            char cha = phoneNum.charAt(0);
            if(!(cha>='a'&&cha<='z' ||cha>='A'&&cha<='Z')){
//                mEditPhone.setError("账号为字母开头6-12位字母和数字");
//                ToastUtils.showShort("账号为字母开头6-12位字母和数字");
                mEditPhone.requestFocus();
                return;
            }
        }
        final String pwd = mEditPwd1.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)  || pwd.length()<6) {
//            mEditPwd1.setError("密码为字母开头6-18位字母和数字");
            ToastUtils.showShort("密码为字母开头6-18位字母和数字");
            mEditPwd1.requestFocus();
            return;
        }
        char cha = pwd.charAt(0);
        if(!(cha>='a'&&cha<='z' ||cha>='A'&&cha<='Z')){
//            mEditPwd1.setError("密码为字母开头6-18位字母和数字");
            ToastUtils.showShort("密码为字母开头6-18位字母和数字");
            mEditPwd1.requestFocus();
            return;
        }
//        String pwd2 = mEditPwd2.getText().toString().trim();
//        if (TextUtils.isEmpty(pwd2)) {
//            mEditPwd2.setError(WordUtil.getString(R.string.reg_input_pwd_2));
//            mEditPwd2.requestFocus();
//            return;
//        }
//        if (!pwd.equals(pwd2)) {
//            mEditPwd2.setError(WordUtil.getString(R.string.reg_pwd_error));
//            mEditPwd2.requestFocus();
//            return;
//        }
        MainHttpUtil.getRegisterCode(phoneNum, mGetCodeCallback);
    }

    private HttpCallback mGetCodeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(0);
                }
                if (!TextUtils.isEmpty(msg) && msg.contains("123456")) {
                    ToastUtils.showShort(msg);
                }
                ToastUtils.showShort("验证码已发送到您的手机，15分钟内有效");
                countDown();
            } else {
                ToastUtils.showShort(msg);
            }
        }
    };

    /**
     * 注册并登陆
     */
    private void register() {

        final String phoneNum = mEditPhone.getText().toString().trim();

        if(mllPhoneCode.getVisibility()==View.VISIBLE){
            if(!RegexUtils.isMobileSimple(phoneNum)){
                ToastUtils.showShort("请输入正确格式的手机号码");
                mEditPhone.requestFocus();
                return;
            }
        }

        if (TextUtils.isEmpty(phoneNum) ||  phoneNum.length()<6 ) {
//            mEditPhone.setError(WordUtil.getString(R.string.toast_phone));
            ToastUtils.showShort(WordUtil.getString(R.string.toast_phone));
            mEditPhone.requestFocus();
            return;
        }
        if( phoneNum.length()!=11){ //如果不是11位全数字，必须是字母开头
            char cha = phoneNum.charAt(0);
            if(!(cha>='a'&&cha<='z' ||cha>='A'&&cha<='Z')){
//                mEditPhone.setError("账号为字母开头6-12位字母和数字");
                ToastUtils.showShort("账号为字母开头6-12位字母和数字");
                mEditPhone.requestFocus();
                return;
            }
        }
        final String pwd = mEditPwd1.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)  || pwd.length()<6) {
//            mEditPwd1.setError("密码为字母开头6-18位字母和数字");
            ToastUtils.showShort("密码为字母开头6-18位字母和数字");
            mEditPwd1.requestFocus();
            return;
        }
        char cha = pwd.charAt(0);
        if(!(cha>='a'&&cha<='z' ||cha>='A'&&cha<='Z')){
//            mEditPwd1.setError("密码为字母开头6-18位字母和数字");
            ToastUtils.showShort("密码为字母开头6-18位字母和数字");
            mEditPwd1.requestFocus();
            return;
        }
        //验证码
        String code="";
        if(typeCode==1){
            code = mEtPicCode.getText().toString();
        }
        if(typeCode==2){
            code = mEtPhoneCode.getText().toString();
        }
        mEtPicCode.getText().toString();
//        String pwd2 = mEditPwd2.getText().toString().trim();
//        if (TextUtils.isEmpty(pwd2)) {
//            mEditPwd2.setError(WordUtil.getString(R.string.reg_input_pwd_2));
//            mEditPwd2.requestFocus();
//            return;
//        }
//        if (!pwd.equals(pwd2)) {
//            mEditPwd2.setError(WordUtil.getString(R.string.reg_pwd_error));
//            mEditPwd2.requestFocus();
//            return;
//        }
        if (mDialog != null) {
            mDialog.dismiss();
        }
        String yqm ;
        if(TextUtils.isEmpty(invitationCode)){
            yqm= mEditYq.getText().toString();
        }else {
            yqm=invitationCode;
        }
        MainHttpUtil.register(phoneNum, pwd, pwd, device_id, code, yqm, layered, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                L.e("WOLF", msg);
                if (((LoginAndRegistActivity) mContext).isFinishing()) {
                    return;
                }
                if (code == 0) {
                    login(phoneNum, pwd);
                } else {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    getTuCode();
                    ToastUtils.showShort(msg);
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public void onError() {
                getTuCode();
                if (mDialog != null) {
                    mDialog.dismiss();
                }
            }
        });
    }

    private void login(final String phoneNum, final String pwd) {
        dialog = DialogUitl.loadingNew3Dialog(mContext);
        dialog.show();
        MainHttpUtil.login(phoneNum, pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {

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
                    MobclickAgent.onProfileSignIn(Constants.MOB_PHONE, uid);
                    ImPushUtil.getInstance().resumePush();
                } else  if (code ==10042 && info.length > 0) {
                    ((LoginAndRegistActivity)mContext).setStopVideo(true);
                    JSONObject obj = JSON.parseObject(info[0]);
                    String uid = obj.getString("uid");
                    Intent intent =new Intent(mContext, EditMyInforActivity.class);
                    intent.putExtra("phone",phoneNum);
                    intent.putExtra("uid",uid);
                    intent.putExtra("pwd",pwd);
                    mContext.startActivity(intent);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }else {
                    if(!TextUtils.isEmpty(msg)){
                        ToastUtils.showShort(msg);
                    }
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onError() {
                if (mDialog != null) {
                    mDialog.dismiss();
                }

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                if (bean != null) {
                    getAdvert();
                }else {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    private void toMain() {
        ((LoginAndRegistActivity)mContext).setStopVideo(true);
        if (mFirstLogin) {
//            RecommendActivity.forward(mContext, mShowInvite);
            //判断有没得推介列表
            MainHttpUtil.getRecommend(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        List<RecommendBean> list = JSON.parseArray(Arrays.toString(info), RecommendBean.class);
                        if(list==null || list.size()==0){
                            MainActivity.forward(mContext, mShowInvite,true,true);
                        }else {
                            RecommendActivity.forward(mContext, mShowInvite);
                            EventBus.getDefault().post(new RegSuccessEvent());
                        }

                    }
                }
            });
        } else {
            MainActivity.forward(mContext, mShowInvite,true,true);
        }
        SPUtils.getInstance().remove(Constants.HTML_TOKEN);
    }

    /**
     * 首页预加载
     */
    private void getAdvert(){
        MainHttpUtil.getAdvertCache("", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(dialog!=null){
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
                    toMain();
                }else {
                    toMain();
                }
            }

            @Override
            public void onCacheSuccess(Response<JsonBean> response) {
                super.onCacheSuccess(response);
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
                if(dialog!=null){
                    dialog.dismiss();
                }

                toMain();
            }
        });
    }

    @Override
    public void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_REGISTER_CODE);
        MainHttpUtil.cancel(MainHttpConsts.REGISTER);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN);
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if(!canClick()){
            return;
        }
        if (view.getId() == R.id.iv_back) {
            ((LoginAndRegistActivity) mContext).showRegistView(false);
        } else if (view.getId() == R.id.tv_photo_code) {
            getCode();
        } else if (view.getId() == R.id.btn_register) {
            register();
        } else if (view.getId() == R.id.pic_code) {
            getTuCode();
        } else if (view.getId() == R.id.btn_tip) {
            ((LoginAndRegistActivity) mContext).showRegistView(true);
            forwardTip();
        }else if (view.getId() == R.id.ll_eye) {
            if(canSee){
                iv_eye.setImageResource(R.mipmap.password_no_eye);
                mEditPwd1.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }else {
                iv_eye.setImageResource(R.mipmap.password_eye);
                mEditPwd1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            canSee=!canSee;
        }

    }

    //登录即代表同意服务和隐私条款
    private void forwardTip() {
        HtmlConfig config = new HtmlConfig();
        WebViewActivity.forward(mContext, config.getLOGIN_PRIVCAY());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        changeEnable();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
    /**
     * 倒计时
     */
    private void countDown() {
        if(mTvPhoneCode!=null){
            mTimer = new MyCountDownTimer(60000+30, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if(mTvPhoneCode!=null){
                        mTvPhoneCode.setClickable(false);
                        mTvPhoneCode.setText((millisUntilFinished/1000)+"s");
                    }
                }

                @Override
                public void onFinish() {
                    if(mTvPhoneCode!=null){
                        mTvPhoneCode.setClickable(true);
                        mTvPhoneCode.setText("获取验证码");
                    }
                }
            }.start();
        }
    }


    /**
     * 获取图形验证码
     */
    private void getTuCode() {
        String url=  SpUtil.getInstance().getStringValue(SpUtil.FAST_URL)+ "/api/public/?service=User.getVerify&t="+device_id;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        //初始化开启接受消息的服务
        Call call = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                InputStream is = response.body().byteStream();
                final Bitmap bm = BitmapFactory.decodeStream(is);
                ((LoginAndRegistActivity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mIvPicCode!=null){
                            mIvPicCode.setImageBitmap(bm);
                        }
                    }
                });
            }
        });
    }

    private  String getMetaDataString(String key) {
        String res = null;
        try {
            ApplicationInfo appInfo = CommonAppContext.sInstance.getPackageManager().getApplicationInfo(CommonAppContext.sInstance.getPackageName(), PackageManager.GET_META_DATA);
            res = appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    protected void setTitle(String title) {
        TextView titleView = (TextView) findViewById(com.yunbao.common.R.id.titleView);
        if (titleView != null) {
            titleView.setText(title);
        }
    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return false;
    }
}
