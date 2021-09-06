package com.yunbao.main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.lzy.okgo.model.Response;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.live.custom.MyCountDownTimer;
import com.yunbao.main.R;
import com.yunbao.main.event.FindPsdEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by cxf on 2018/9/17.
 */

public class FindPswOneActivity extends AbsActivity implements TextWatcher {

    private TextView   TvCode;
    private TextView   mTvPhone;
    private EditText mEtPhoneCode;
    private EditText mEditPhone;
    private TextView nTvNext;
    private TextView btn_service;
    private TextView mTvCode;//获取验证码
    private TextView btn_register;//获取验证码
    private RelativeLayout ll_back;
    private  LinearLayout ll_root;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_psd_one;
    }

    @Override
    protected void main() {
        EventBus.getDefault().register(this);
        TvCode  =   findViewById(R.id.tv_code);
        mTvCode  =   findViewById(R.id.tv_photo_code);
        mTvPhone =   findViewById(R.id.tv_phone);
        mEtPhoneCode = findViewById(R.id.et_photo_code);
        mEditPhone =  findViewById(R.id.edit_phone);
        ll_root =  findViewById(R.id.ll_root);
        nTvNext=  findViewById(R.id.btn_new);
        ll_back=  findViewById(R.id.ll_back);
        btn_service=  findViewById(R.id.btn_service);
        btn_register=  findViewById(R.id.btn_register);
        initListener();
    }


    private void initListener() {

        mEtPhoneCode.addTextChangedListener(this);
        mEditPhone.addTextChangedListener(this);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!canClick()){
                    return;
                }
                startActivity(new Intent(FindPswOneActivity.this, RegisterNewActivity.class));
                finish();
            }
        });
        btn_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!canClick()){
                    return;
                }
                getService(FindPswOneActivity.this);
            }
        });
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ll_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideSoftInput(FindPswOneActivity.this);
            }
        });
        mTvCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!canClick()){
                    return;
                }
                getCode();
            }
        });
        nTvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!canClick()){
                    return;
                }
                onNextRegist();
            }
        });
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String phone = mEditPhone.getText().toString();
        String code = mEtPhoneCode.getText().toString();
        mTvPhone.setVisibility(TextUtils.isEmpty(phone) ?View.INVISIBLE:View.VISIBLE );
        TvCode.setVisibility(TextUtils.isEmpty(code) ?View.INVISIBLE:View.VISIBLE );
        nTvNext.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(code));
        nTvNext.setTextColor((!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(code))? getResources().getColor(R.color.white):getResources().getColor(R.color.color_BCBCBC));
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void getCode() {
        mTvCode.setEnabled(false);
        final String phoneNum = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)  ) {
            ToastUtil.show("请输入手机号");
            mTvCode.setEnabled(true);
            return;
        }
        if( phoneNum.length()!=11){ //如果不是11位全数字，必须是字母开头
            ToastUtil.show("请输入正确的手机号");
            mTvCode.setEnabled(true);
            return;
        }
        MainHttpUtil.getFindPwdCode(phoneNum, mGetCodeCallback);
    }

    private Handler mHandler;
    private HttpCallback mGetCodeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {

            if (code == 0) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(0);
                }
                if (!TextUtils.isEmpty(msg) && msg.contains("123456")) {
                    ToastUtil.show(msg);
                }
                ToastUtil.show("验证码已发送到您的手机，15分钟内有效");
                countDown();
            } else if(code==1004){
                mTvCode.setEnabled(true);
                showNoExits();
            } else {
                mTvCode.setEnabled(true);
                ToastUtil.show(msg);
            }
        }

        @Override
        public void onError(Response<JsonBean> response) {
            super.onError(response);
            mTvCode.setEnabled(true);
        }

        @Override
        public void onError(String message) {
            mTvCode.setEnabled(true);
            if(!TextUtils.isEmpty(message)){
                ToastUtil.show(message);
            }
        }
    };

    private void showNoExits() {
        DialogUitl.Builder builder = new DialogUitl.Builder(this);
        builder.setTitle("温馨提示")
                .setContent("手机号不存在")
                .setConfrimString("去注册")
                .setCancelString("取消")
                .setCancelable(true)
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        startActivity(new Intent(FindPswOneActivity.this,RegisterNewActivity.class));
                        finish();
                    }
                })
                .build()
                .show();
    }


    /**
     * 点击下一步
     */
    private void onNextRegist() {
        final String phoneNum = mEditPhone.getText().toString().trim();
        final String codes = mEtPhoneCode.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)  ) {
            ToastUtil.show("请输入手机号");
            return;
        }
        MainHttpUtil.CheckPhone(phoneNum,codes, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(code==0){
                    Intent intent=new Intent(FindPswOneActivity.this,FindPswScondActivity.class);
                    intent.putExtra("phone",phoneNum);
                    intent.putExtra("code",codes);
                    startActivity(intent);
                }else {
                    ToastUtil.show(msg);
                }
            }
            @Override
            public void onError(String response) {
                ToastUtil.show(response);
            }
        });

    }

    /**
     * 倒计时
     */
    private MyCountDownTimer mTimer;
    private void countDown() {
        if(mEtPhoneCode!=null){
            mTimer = new MyCountDownTimer(60000+30, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if(mEtPhoneCode!=null){
                        mTvCode.setEnabled(false);
                        mTvCode.setText((millisUntilFinished/1000)+"s");
                    }
                }

                @Override
                public void onFinish() {
                    if(mEtPhoneCode!=null){
                        mTvCode.setText("获取验证码");
                        mTvCode.setEnabled(true);
                    }
                }
            }.start();
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
            OpenUrlUtils.getInstance().setContext(context)
                    .setType(Integer.parseInt(data.getSlide_show_type()))
                    .setSlideTarget(data.getSlide_target())
                    .setTitle(data.getName())
                    .setJump_type(data.getJump_type())
                    .setIs_king("0")
                    .setSlide_show_type_button(data.getSlide_show_type_button())
                    .go(data.getSlide_url());
        }
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_REGISTER_CODE);
        EventBus.getDefault().unregister(this);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if(mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePsdSuccessEvent(FindPsdEvent e) {
        finish();
    }
}
