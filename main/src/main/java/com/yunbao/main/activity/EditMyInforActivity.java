package com.yunbao.main.activity;

import android.app.Dialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.upload.UploadListener;
import com.lzy.okserver.upload.UploadTask;
import com.umeng.analytics.MobclickAgent;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.AdvertBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.ImageResultCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.IMImageLoadUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.im.utils.ImPushUtil;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.RecommendBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 完善个人信息
 */

public class EditMyInforActivity extends AbsActivity implements View.OnClickListener, TextWatcher {

    private EditText et_name;
    private ImageView iv_edit;
    private ImageView iv_header;
    private TextView tv_input;
    private RelativeLayout re_nan;
    private RelativeLayout re_nv;
    private TextView tv_ensure;

    private   int sex=1; //性别0-保密 1-男 2-女'
    private String phone;
    private String psw;
    private String headurl="user_default";
    private String uid;
    private boolean mFirstLogin;//是否是第一次登录
    private boolean mShowInvite;//显示邀请码弹窗
    private String mLoginType = Constants.MOB_PHONE;//登录方式

    private Dialog dialog;
    private ProcessImageUtil mImageUtil;
    private LinearLayout llRoot;
    public boolean isactivited=true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_my_infor;
    }

    @Override
    protected void main() {
        et_name =  findViewById(R.id.et_name);
        iv_header =  findViewById(R.id.iv_head);
        iv_edit =  findViewById(R.id.iv_head_edit);
        tv_input =  findViewById(R.id.tv_input);
        re_nan =  findViewById(R.id.re_nan);
        re_nv =  findViewById(R.id.re_nv);
        tv_ensure=  findViewById(R.id.tv_ensure);
        llRoot=  findViewById(R.id.ll_root);
        iv_header.setOnClickListener(this);
        tv_ensure.setOnClickListener(this);
        re_nan.setOnClickListener(this);
        re_nv.setOnClickListener(this);
        re_nv.setOnClickListener(this);
        llRoot.setOnClickListener(this);
        et_name.addTextChangedListener(this);
        phone= getIntent().getStringExtra("phone");
        psw= getIntent().getStringExtra("pwd");
        uid= getIntent().getStringExtra("uid");

        String user_custom = CommonAppConfig.getInstance().getConfig().getUser_custom();
        if(TextUtils.isEmpty(user_custom)  ||  (!TextUtils.isEmpty(user_custom) && Integer.parseInt(user_custom)==0)){
            tv_input.setVisibility(View.VISIBLE);
            iv_edit.setVisibility(View.VISIBLE);
            iv_header.setEnabled(true);
        }else {
            tv_input.setVisibility(View.GONE);
            iv_edit.setVisibility(View.GONE);
            iv_header.setEnabled(false);
        }
        IMImageLoadUtil.CommonImageLineCircleLoad1(EditMyInforActivity.this,R.mipmap.img_head_deafult,iv_header);
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {

            @Override
            public void beforeCamera() {
            }
            @Override
            public void onSuccess(final File file) {
                UpdataHeadView(file);
            }
            @Override
            public void onFailure() {
            }
        });
    }


    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.UPDATE_FIELDS);
        isactivited=false;
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.iv_head){
            setAvatar();
        }else  if(view.getId()==R.id.re_nan){
            nanClick(1);
        }else if(view.getId()==R.id.re_nv){
            nanClick(2);
        }else if(view.getId()==R.id.tv_ensure){
            ClickUtils.applySingleDebouncing(tv_ensure, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logIn();
                }
            });
        }else if(view.getId()==R.id.ll_root){
            KeyboardUtils.hideSoftInput(et_name);
        }
    }
    /**
     * 男女切换
     */
    private   void   nanClick(int sexType){
        if(sexType==1){
            re_nan.setBackground(getResources().getDrawable(R.drawable.im_shape_nan_bg));
            re_nv.setBackground(getResources().getDrawable(R.drawable.im_shape_nu_bg));
        }else {
            re_nv.setBackground(getResources().getDrawable(R.drawable.im_shape_nan_bg));
            re_nan.setBackground(getResources().getDrawable(R.drawable.im_shape_nu_bg));
        }
        sex=sexType;
    }
    /**
     * 登录
     */
    private void logIn() {
        Log.e("----登录账号==",phone);
        Log.e("----登录密码==",psw);
        if(TextUtils.isEmpty(et_name.getText().toString())){
            Toast.makeText(mContext, "请输入您的昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        MainHttpUtil.login(phone, psw,et_name.getText().toString(),headurl,sex+"", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                SpUtil.getInstance().setStringValue(SpUtil.ZHANG_HAO,phone);
                onLoginSuccess(code, msg, info);
            }

//            @Override
//            public boolean showLoadingDialog() {
//                return true;
//            }
//
//            @Override
//            public Dialog createLoadingDialog() {
//                return DialogUitl.loadingNew3Dialog(mContext);
//            }
        });
    }
    /**
     * 登录结果
     */
    private void onLoginSuccess(int code, String msg, String[] info) {
        if (code == 0 && info.length > 0) {
            JSONObject obj = JSON.parseObject(info[0]);
            String uid = obj.getString("id");
            String token = obj.getString("token");
            mFirstLogin = obj.getIntValue("isreg") == 1;
            mShowInvite = obj.getIntValue("isagent") == 1;
            CommonAppConfig.getInstance().setLoginInfo(uid, token, true);
            SpUtil.getInstance().setBooleanValue(SpUtil.NEW_USER, mFirstLogin);
            String invitecode = obj.getString("invitecode");
            if (invitecode != null) {
                SpUtil.getInstance().setStringValue(SpUtil.INVITE_MEMBER, obj.getString("invitecode"));
            }

            getBaseUserInfo();
            //友盟统计登录
            MobclickAgent.onProfileSignIn(mLoginType, uid);
            ImPushUtil.getInstance().resumePush();
        }else {
            if(!TextUtils.isEmpty(msg)){
                ToastUtil.show(msg);
            }
        }
    }
    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        if(!EditMyInforActivity.this.isFinishing() && dialog==null){
            dialog = DialogUitl.loadingNew3Dialog(this);
            dialog.show();
        }
        MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                getAdvert();
            }
        });
    }

    /**
     * 首页预加载
     */
    private void getAdvert(){
        MainHttpUtil.getAdvertCache("", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(!EditMyInforActivity.this.isFinishing() && dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
                try {
                    if (code == 0) {
                        List<AdvertBean> advertBean = JSON.parseArray(Arrays.toString(info), AdvertBean.class);
                        if(advertBean!=null && advertBean.size()>0){
                            CommonAppConfig.getInstance().setAdvert(JSON.toJSONString(advertBean.get(0)));
                            CommonAppConfig.getInstance().setHotList(Arrays.toString(info));
                            if(advertBean.get(0).getTabbar()!=null){
                                SpUtil.getInstance().setStringValue(SpUtil.FAST_TABBAR_LIST,JSON.toJSONString(advertBean.get(0).getTabbar()));
                            }
                            advertBean.get(0).getList().clear();
                        }
                        toMain();
                    }else {
                        toMain();
                    }
                }catch (Exception e){
                    ToastUtil.show("数据异常，请稍后再试");
                }
            }

            @Override
            public void onCacheSuccess(Response<JsonBean> response) {
                super.onCacheSuccess(response);
                try {
                    L.e("缓存：onCacheSuccess:"+Arrays.toString(response.body().getData().getInfo()));
                    List<AdvertBean> advertBean = JSON.parseArray(Arrays.toString(response.body().getData().getInfo()), AdvertBean.class);
                    if(advertBean!=null || advertBean.size()>0) {
                        CommonAppConfig.getInstance().setAdvert(JSON.toJSONString(advertBean.get(0)));
                        CommonAppConfig.getInstance().setHotList(Arrays.toString(response.body().getData().getInfo()));
                        advertBean.get(0).getList().clear();
                        if(advertBean.get(0).getTabbar()!=null){
                            SpUtil.getInstance().setStringValue(SpUtil.FAST_TABBAR_LIST,JSON.toJSONString(advertBean.get(0).getTabbar()));
                        }
                    }
                    toMain();
                }catch (Exception e){
                    ToastUtil.show("数据异常，请稍后再试");
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                if(!isactivited){
                    return;
                }
                if(!EditMyInforActivity.this.isFinishing() && dialog != null && dialog.isShowing()){
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
    }


    /**
     * 设置头像
     */
    private void setAvatar() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{
                com.yunbao.live.R.string.camera, com.yunbao.live.R.string.alumb}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == com.yunbao.live.R.string.camera) {
                    mImageUtil.getImageByCamera();
                } else {
                    mImageUtil.getImageByAlumb();
                }
            }
        });
    }

    /**
     * 选择图片成功  获取上传图片的url
     */
    private void UpdataHeadView(final File file) {
        if (file != null) {
            IMImageLoadUtil.CommonImageLineCircleLoad1(EditMyInforActivity.this,file.getPath(),iv_header);
            LiveHttpUtil.getUploadMyUrlInfo(uid,new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
//                        String token = obj.getString("token");
//                        String url = obj.getString("url");
//                        String url_other = obj.getString("url_other");
//                        String image_path = obj.getString("image_path");

                        String url=obj.getString("image_url");
                        String token=obj.getString("image_token");
                        String image_path=obj.getString("user_path");

                        startUpLoad(file, token, url, image_path);
                    }
                }
            });
        }
    }

    /**
     * 获取上传图片的url成功  上传图片
     */
    private void startUpLoad(final File file, final String token, final String url, String image_path) {
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .params("scene", "default")
                .params("auth_token", token)
                .params("output", "json")
                .params("path", image_path)
                .params("file", file)
                .converter(new StringConvert());

        String mTag = file.getName();
        UploadTask<String> mTask = OkUpload.request(mTag, postRequest)
                .save()
                .register(new UploadListener<String>(mTag) {
                    @Override
                    public void onStart(Progress progress) {
                    }

                    @Override
                    public void onProgress(Progress progress) {
                    }

                    @Override
                    public void onError(Progress progress) {
                    }

                    @Override
                    public void onFinish(String s, Progress progress) {
                        try{
                            JSONObject obj = JSON.parseObject(s);
                            String imageUrl = obj.getString("src");
                            headurl = imageUrl;
                        }catch (Exception e){
                        }
                    }

                    @Override
                    public void onRemove(Progress progress) {

                    }
                });
        mTask.start();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(!TextUtils.isEmpty(editable.toString()) && editable.toString().length()==8){
            Toast.makeText(mContext, "昵称不能超过八个字", Toast.LENGTH_SHORT).show();
        }
    }
}
