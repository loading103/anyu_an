package com.yunbao.main.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

/**
 * Created by cxf on 2018/9/29.
 * 设置聊天app信息
 */

public class EditChatAppActivity extends AbsActivity implements View.OnClickListener {

    private EditText mEditText;
    private String t;
    private String type;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_chat_app;
    }

    @Override
    protected void main() {
        mEditText = (EditText) findViewById(R.id.edit);
        mEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(20)
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    for (int i = 0; i < s.length(); i++) {
                        char c = s.charAt(i);
                        if (c >= 0x4e00 && c <= 0X9fff) { // 根据字节码判断
                            // 如果是中文，则清除输入的字符，否则保留
                            s.delete(i,i+1);
                        }
                    }
                }
            }
        });

        findViewById(R.id.btn_save).setOnClickListener(this);
        type = getIntent().getStringExtra("type");
        switch (type){
            case Constants.WX:
                setTitle("修改微信账号");
                mEditText.setHint("请输入微信账号");
                t="微信";
                break;
            case Constants.QQ:
                setTitle("修改QQ账号");
                mEditText.setHint("请输入QQ账号");
                t="QQ";
                break;
            case Constants.TL:
                setTitle("修改特聊账号");
                mEditText.setHint("请输入特聊账号");
                t="特聊";
                break;
            case Constants.TG:
                setTitle("修改Telegram账号");
                mEditText.setHint("请输入Telegram账号");
                t="Telegram";
                break;
            case Constants.FB:
                setTitle("修改Facebook账号");
                mEditText.setHint("请输入Facebook账号");
                t="Facebook";
                break;
        }
        String content = getIntent().getStringExtra("content");
        if (!TextUtils.isEmpty(content)) {
            if (content.length() > 20) {
                content = content.substring(0, 20);
            }
            mEditText.setText(content);
            mEditText.setSelection(content.length());
        }
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        final String content = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.show("请输入"+t);
            return;
        }
        MainHttpUtil.updateFields("{\""+type+"\""+":\"" + content + "\"}", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        ToastUtil.show(obj.getString("msg"));
                        UserBean u = CommonAppConfig.getInstance().getUserBean();
                        if (u != null) {
                            switch (type){
                                case Constants.WX:
                                    u.setWechat(content);
                                    break;
                                case Constants.QQ:
                                    u.setQq(content);
                                    break;
                                case Constants.TL:
                                    u.setTeliao(content);
                                    break;
                                case Constants.TG:
                                    u.setTelegram(content);
                                    break;
                                case Constants.FB:
                                    u.setFacebook(content);
                                    break;
                            }
                        }
                        Intent intent = getIntent();
                        intent.putExtra("type", type);
                        intent.putExtra("content", content);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.UPDATE_FIELDS);
        super.onDestroy();
    }
}
