package com.yunbao.main.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.main.event.FindPsdEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by cxf on 2018/9/17.
 */

public class FindPswScondActivity extends AbsActivity implements TextWatcher, View.OnClickListener {

    private TextView tv_pwd;
    private EditText edit_pwd_1;
    private LinearLayout ll_close;
    private LinearLayout ll_eye;
    private ImageView iv_eye;
    private boolean   canSee;//密码可见

    private TextView tv_pwd_2;
    private EditText edit_pwd_2;
    private LinearLayout ll_close_2;
    private LinearLayout ll_eye_2;
    private ImageView iv_eye_2;
    private TextView btn_finish;
    private TextView btn_register;
    private RelativeLayout ll_back;
    private LinearLayout ll_root;
    private boolean   canSee2;//密码可见

    private boolean   havtFoused1=true;//edit_pwd_1是不是有焦点
    private boolean   havtFoused2=false;//edit_pwd_2是不是有焦点

    private  String phone;
    private  String code;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_psd_scond;
    }

    @Override
    protected void main() {
        findViewByIds();
        initListener();
        phone=getIntent().getStringExtra("phone");
        code=getIntent().getStringExtra("code");
    }

    private void findViewByIds() {
        tv_pwd =   findViewById(R.id.tv_pwd);
        edit_pwd_1   =   findViewById(R.id.edit_pwd_1);
        ll_close =   findViewById(R.id.ll_close);
        ll_eye   =   findViewById(R.id.ll_eye);
        iv_eye   =  findViewById(R.id.iv_eye);
        tv_pwd_2 =   findViewById(R.id.tv_pwd_2);
        edit_pwd_2   =   findViewById(R.id.edit_pwd_2);
        ll_close_2 =   findViewById(R.id.ll_close_2);
        ll_eye_2   =   findViewById(R.id.ll_eye_2);
        iv_eye_2   =  findViewById(R.id.iv_eye_2);
        btn_finish   =  findViewById(R.id.btn_finish);
        btn_register=  findViewById(R.id.btn_register);
        ll_back=  findViewById(R.id.ll_back);
        ll_root=  findViewById(R.id.ll_root);
    }


    private void initListener() {
        edit_pwd_1.addTextChangedListener(this);
        edit_pwd_2.addTextChangedListener(this);
        btn_finish.setOnClickListener(this);
        ll_close.setOnClickListener(this);
        ll_close_2.setOnClickListener(this);
        ll_eye.setOnClickListener(this);
        ll_eye_2.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        ll_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideSoftInput(FindPswScondActivity.this);
            }
        });
        edit_pwd_1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    havtFoused1=true;
                } else {
                    havtFoused1=false;
                    if(ll_close!=null){
                        ll_close.setVisibility(View.GONE);
                    }
                }
            }
        });
        edit_pwd_2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    havtFoused2=true;
                } else {
                    havtFoused2=false;
                    if(ll_close_2!=null){
                        ll_close_2.setVisibility(View.GONE);
                    }
                }
            }
        });
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String pwd1 = edit_pwd_1.getText().toString();
        String pwd2 = edit_pwd_2.getText().toString();
        tv_pwd.setVisibility(TextUtils.isEmpty(pwd1) ?View.INVISIBLE:View.VISIBLE );
        boolean s= TextUtils.isEmpty(pwd1) && havtFoused1;
        boolean b = TextUtils.isEmpty(pwd2) && havtFoused2;
        if((!TextUtils.isEmpty(pwd1) && havtFoused1)){
            ll_close.setVisibility(View.VISIBLE);
        } else{
            ll_close.setVisibility(View.GONE);
        }
        if((!TextUtils.isEmpty(pwd2) && havtFoused2)){
            ll_close_2.setVisibility(View.VISIBLE);
        } else{
            ll_close_2.setVisibility(View.GONE);
        }
        tv_pwd_2.setVisibility(TextUtils.isEmpty(pwd2) ?View.INVISIBLE:View.VISIBLE );
        btn_finish.setEnabled(!TextUtils.isEmpty(pwd1) && !TextUtils.isEmpty(pwd2));
        btn_finish.setTextColor((!TextUtils.isEmpty(pwd1) && !TextUtils.isEmpty(pwd2))? getResources().getColor(R.color.white):getResources().getColor(R.color.color_BCBCBC));
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_REGISTER_CODE);
        super.onDestroy();
    }
    @Override
    public void onClick(View view) {
        if(!canClick()){
            return;
        }
        if (view.getId() == R.id.ll_eye) {
            llEyeClick();
        }else  if (view.getId() == R.id.ll_eye_2) {
            llEye2Click();
        } else if (view.getId() == R.id.ll_close) {
            edit_pwd_1.getText().clear();
        }else if (view.getId() == R.id.ll_close_2) {
            edit_pwd_2.getText().clear();
        }else if (view.getId() == R.id.btn_finish) {
            changePsd();
        }else if(view.getId()== R.id.btn_register){
            startActivity(new Intent(FindPswScondActivity.this,RegisterNewActivity.class));
            finish();
        }else if(view.getId()== R.id.ll_back){
            finish();
        }
    }

    private void llEyeClick() {
        if(canSee){
            iv_eye.setImageResource(R.mipmap.icon_close_see);
            edit_pwd_1.setTransformationMethod(PasswordTransformationMethod.getInstance());
            edit_pwd_1.setSelection(edit_pwd_1.getText().length());
        }else {
            iv_eye.setImageResource(R.mipmap.icon_can_see);
            edit_pwd_1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            edit_pwd_1.setSelection(edit_pwd_1.getText().length());
        }
        canSee=!canSee;
    }

    private void llEye2Click() {
        if(canSee2){
            iv_eye_2.setImageResource(R.mipmap.icon_close_see);
            edit_pwd_2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            edit_pwd_2.setSelection(edit_pwd_2.getText().length());
        }else {
            iv_eye_2.setImageResource(R.mipmap.icon_can_see);
            edit_pwd_2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            edit_pwd_2.setSelection(edit_pwd_2.getText().length());
        }
        canSee2=!canSee2;
    }

    private void changePsd() {
        String text1 = edit_pwd_1.getText().toString();
        String text2 = edit_pwd_2.getText().toString();
        if(!text1.equals(text2)){
            ToastUtil.show("两次输入的密码不同，请重新输入");
            return;
        }
        MainHttpUtil.findPwd(phone, text1, text2, code, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show("密码修改成功");
                    EventBus.getDefault().post(new FindPsdEvent());
                    finish();
                } else {
                    if (!TextUtils.isEmpty(msg)) {
                        ToastUtil.show(msg);
                    }
                }
            }

            @Override
            public void onError(String msg) {
                if (!TextUtils.isEmpty(msg)) {
                    ToastUtil.show(msg);
                }
            }
        });
    }
}
