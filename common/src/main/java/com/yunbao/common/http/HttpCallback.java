package com.yunbao.common.http;

import android.app.Dialog;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.R;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

/**
 * Created by cxf on 2017/8/7.
 */

public abstract class HttpCallback extends AbsCallback<JsonBean> {

    private Dialog mLoadingDialog;

    @Override
    public JsonBean convertResponse(okhttp3.Response response) throws Throwable {
        return JSON.parseObject(response.body().string(), JsonBean.class);
    }

    @Override
    public void onSuccess(Response<JsonBean> response) {
        JsonBean bean = response.body();
        if (bean != null) {
            if (200 == bean.getRet()) {
                Data data = bean.getData();
                L.e("网络请求成功---->"+response.getRawCall().request().tag()+ " : ", JSONArray.toJSONString(data.getInfo()));
                if (data != null) {
                    if (700 == data.getCode()) {
                        //token过期，重新登录
                        RouteUtil.forwardLoginInvalid(data.getMsg());
                    } else {
                        onSuccess(data.getCode(), data.getMsg(), data.getInfo());
                    }
                } else {
                    L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
                }
            } else {
                L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
//                ToastUtil.show(bean.getMsg());//老大让不显示
            }

        } else {
            L.e("服务器返回值异常--->bean = null");
        }
    }

    @Override
    public void onError(Response<JsonBean> response) {
        Throwable t = response.getException();
        L.e("网络请求错误---->"+response.getRawCall().request().tag()+ " : " + t.getClass() + " : " + t.getMessage());
        if (showLoadingDialog() && mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        if (t instanceof SocketTimeoutException){
            onError(WordUtil.getString(R.string.load_failure2));
            return;
        }
        if ( t instanceof ConnectException || t instanceof UnknownHostException || t instanceof UnknownServiceException || t instanceof SocketException) {
            onError(WordUtil.getString(R.string.load_failure));
            return;
        }
        onError();
//        Toast.makeText(CommonAppContext.sInstance.getBaseContext(), t.getMessage(), Toast.LENGTH_SHORT).show();//老大让不显示
    }

    public void onError(String message) {
//        ToastUtil.show(message);//老大让不显示

    }
    public void onError() {

    }

    public abstract void onSuccess(int code, String msg, String[] info);

    @Override
    public void onStart(Request<JsonBean, ? extends Request> request) {
        L.e("网络请求开始---->"+request.getTag()+ " : " +request.getUrl());
        onStart();
    }

    public void onStart() {
        try{
            if (showLoadingDialog()) {
                if (mLoadingDialog == null) {
                    mLoadingDialog = createLoadingDialog();
                }
                mLoadingDialog.show();
            }
        }catch (Exception e){
            return;
        }

    }

    @Override
    public void onFinish() {
        if (showLoadingDialog() && mLoadingDialog != null) {
            try{
                mLoadingDialog.dismiss();
            }catch (IllegalArgumentException e){
                L.e("界面已销毁报错："+e.getMessage());
            }
        }
    }

    public Dialog createLoadingDialog() {
        return null;
    }

    public boolean showLoadingDialog() {
        return false;
    }

}
