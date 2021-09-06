package com.yunbao.main.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.upload.UploadListener;
import com.lzy.okserver.upload.UploadTask;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.ActivityResultCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.ImageResultCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.event.HeadUpdateEvent;
import com.yunbao.main.event.MainJsEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.video.http.VideoHttpUtil;
import com.yunbao.video.upload.VideoUploadBean;
import com.yunbao.video.upload.VideoUploadCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * Created by cxf on 2018/9/29.
 * 我的 编辑资料
 *  *   "info": {
 *  *             "url": "http://132.232.122.151:8080/group1/upload",    视频上传地址（视频封面也用这个地址）
 *  *             "token": "8663d6f8edcf9717e21e2e63aeb779ee",         视频上传token
 *  *             //"url_other": "http://132.232.122.151:8080/group1/upload",    老包用的
 *  *             "image_url": "http://132.232.122.151:8080/group1/upload",      图片上传地址
 *  *             "image_token": "8663d6f8edcf9717e21e2e63aeb779ee",           图片上传token
 *  *             "video_path": "newrhlive/video/video/2020/10/15",                   视频存放路径（视频封面也用这个路径）
 *  *             //"image_path": "newrhlive/image//2020/10/15",                       老包用的
 *  *             "live_path": "newrhlive/image/live/2020/10/15",                          直播封面存放路径
 *  *             "user_path": "newrhlive/image/user/2020/10/15"                        用户头像存放路径
 *  *         }
 *  */

public class EditProfileActivity extends AbsActivity {

    private static final String TAG = "EditProfileActivity";
    private ImageView mAvatar;
    private TextView mName;
    private TextView mSign;
    private TextView mBirthday;
    private TextView mSex;
    private ProcessImageUtil mImageUtil;
    private UserBean mUserBean;
    private RelativeLayout btnSign;
    private TextView tvWx;
    private TextView tvQq;
    private TextView tvTl;
    private TextView tvTg;
    private TextView tvFb;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.edit_profile));
        EventBus.getDefault().register(this);
        TextView mTvFinish = findViewById(R.id.tv_finish);
        mTvFinish.setVisibility(View.GONE);
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mSign = (TextView) findViewById(R.id.sign);
        mBirthday = (TextView) findViewById(R.id.birthday);
        mSex = (TextView) findViewById(R.id.sex);
        btnSign = (RelativeLayout) findViewById(R.id.btn_sign);
        tvWx = (TextView) findViewById(R.id.tv_wx);
        tvQq = (TextView) findViewById(R.id.tv_qq);
        tvTl = (TextView) findViewById(R.id.tv_tl);
        tvTg = (TextView) findViewById(R.id.tv_tg);
        tvFb = (TextView) findViewById(R.id.tv_fb);

        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(final File file) {
                if (file != null) {
                    ImgLoader.display(mContext, file, mAvatar);

                    VideoHttpUtil.getUploadUrlInfo(new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
//                                String token=obj.getString("token");
//                                String url=obj.getString("url");
//                                String url_other=obj.getString("url_other");
//                                String image_path=obj.getString("image_path");

                                String url=obj.getString("image_url");
                                String token=obj.getString("image_token");
                                String image_path=obj.getString("user_path");

                                startUpLoad(file,token, url,image_path);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure() {
            }
        });
        mUserBean = CommonAppConfig.getInstance().getUserBean();
        if (mUserBean != null) {
            showData(mUserBean);
        } else {
            MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
                @Override
                public void callback(UserBean u) {
                    mUserBean = u;
                    showData(u);
                }
            });
        }

        if(CommonAppConfig.getInstance().getConfig().getPersonal_sign().equals("0")){
            btnSign.setVisibility(View.GONE);
        }else {

            btnSign.setVisibility(View.VISIBLE);
        }
    }

    private void startUpLoad(final File file, final String token, final String url, String image_path) {
        PostRequest<String> postRequest = OkGo.<String>post(url)
                .params("scene", "default")
                .params("auth_token", token)
                .params("output", "json")
                .params("file", file)
                .params("path", image_path)
                .converter(new StringConvert());
        String mTag=file.getName();
        UploadTask<String> mTask = OkUpload.request(mTag, postRequest)
                .save()
                .register(new UploadListener<String>(mTag) {
                    @Override
                    public void onStart(Progress progress) {
                        L.e(TAG, "onStart------progress----->" + progress.fraction * 100);
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        L.e(TAG, "onProgress------progress----->" + progress.fraction * 100);
                    }

                    @Override
                    public void onError(Progress progress) {
                        L.e(TAG, "onProgress------progress----->" + progress.fraction * 100);
                    }

                    @Override
                    public void onFinish(String s, Progress progress) {
                        L.e(TAG, "onFinish------progress----->" + progress.fraction * 100);
                        L.e(TAG, "onFinish------s----->" + s);
                        try{
                            JSONObject obj = JSON.parseObject(s);
                            String imageUrl = obj.getString("src");
                            updateAvatar(imageUrl);
                        }catch (Exception e){
                            L.e(TAG, "onFinish------s----->资源服错误");
                        }

                    }

                    @Override
                    public void onRemove(Progress progress) {
                        L.e(TAG, "onRemove------progress----->" + progress);

                    }
                });
        mTask.start();
    }

    private void updateAvatar(String imageUrl) {
        MainHttpUtil.updateAvatar(imageUrl, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    ToastUtil.show(R.string.edit_profile_update_avatar_success);
                    UserBean bean = CommonAppConfig.getInstance().getUserBean();
                    if (bean != null) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        L.e("WOLF",obj.toJSONString());
                        bean.setAvatar(obj.getString("avatar"));
                        bean.setAvatarThumb(obj.getString("avatarThumb"));
                        CommonAppConfig.getInstance().setUserBean(bean);
                    }
                }
            }
        });
    }


    public void editProfileClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_avatar) {
            Intent intent=new Intent(this,ChoosePhotoActivity.class);
            startActivity(intent);
        } else if (i == R.id.btn_name) {
            forwardName();

        } else if (i == R.id.btn_sign) {
            forwardSign();

        } else if (i == R.id.btn_birthday) {
            editBirthday();

        } else if (i == R.id.btn_sex) {
            forwardSex();

        } else if (i == R.id.btn_impression) {
            forwardImpress();

        }else if (i == R.id.btn_wx) {
            forwardChat(Constants.WX,mUserBean.getWechat());

        }else if (i == R.id.btn_qq) {
            forwardChat(Constants.QQ,mUserBean.getQq());

        }else if (i == R.id.btn_tl) {
            forwardChat(Constants.TL,mUserBean.getTeliao());

        }else if (i == R.id.btn_tg) {
            forwardChat(Constants.TG,mUserBean.getTelegram());

        }else if (i == R.id.btn_fb) {
            forwardChat(Constants.FB,mUserBean.getFacebook());

        }
    }

    private void editAvatar() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{
                R.string.camera, R.string.alumb}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.camera) {
                    mImageUtil.getImageByCamera();
                } else {
                    mImageUtil.getImageByAlumb();
                }
            }
        });
    }

    private void forwardName() {
        if (mUserBean == null) {
            return;
        }
        Intent intent = new Intent(mContext, EditNameActivity.class);
        intent.putExtra(Constants.NICK_NAME, mUserBean.getUserNiceName());
        mImageUtil.startActivityForResult(intent, new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    String name = intent.getStringExtra(Constants.NICK_NAME);
                    mUserBean.setUserNiceName(name);
                    mName.setText(name);
                }
            }
        });
    }


    private void forwardSign() {
        if (mUserBean == null) {
            return;
        }
        Intent intent = new Intent(mContext, EditSignActivity.class);
        intent.putExtra(Constants.SIGN, mUserBean.getSignature());
        mImageUtil.startActivityForResult(intent, new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    String sign = intent.getStringExtra(Constants.SIGN);
                    mUserBean.setSignature(sign);
                    mSign.setText(sign);
                }
            }

        });
    }

    private void forwardChat(String type,String content) {
        if (mUserBean == null) {
            return;
        }
        Intent intent = new Intent(mContext, EditChatAppActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("content", content);
        mImageUtil.startActivityForResult(intent, new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    String type = intent.getStringExtra("type");
                    String content = intent.getStringExtra("content");
                    switch (type){
                        case Constants.WX:
                            mUserBean.setWechat(content);
                            tvWx.setText(content);
                            break;
                        case Constants.QQ:
                            mUserBean.setQq(content);
                            tvQq.setText(content);
                            break;
                        case Constants.TL:
                            mUserBean.setTeliao(content);
                            tvTl.setText(content);
                            break;
                        case Constants.TG:
                            mUserBean.setTelegram(content);
                            tvTg.setText(content);
                            break;
                        case Constants.FB:
                            mUserBean.setFacebook(content);
                            tvFb.setText(content);
                            break;
                    }

                }
            }

        });
    }

    private void editBirthday() {
        if (mUserBean == null) {
            return;
        }
        DialogUitl.showDatePickerDialog(mContext, new DialogUitl.DataPickerCallback() {
            @Override
            public void onConfirmClick(final String date) {
                MainHttpUtil.updateFields("{\"birthday\":\"" + date + "\"}", new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (info.length > 0) {
                                ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                                mUserBean.setBirthday(date);
                                mBirthday.setText(date);
                            }
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }
        });
    }

    private void forwardSex() {
        if (mUserBean == null) {
            return;
        }
        Intent intent = new Intent(mContext, EditSexActivity.class);
        intent.putExtra(Constants.SEX, mUserBean.getSex());
        mImageUtil.startActivityForResult(intent, new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    int sex = intent.getIntExtra(Constants.SEX, 0);
                    if (sex == 1) {
                        mSex.setText(R.string.sex_male);
                        mUserBean.setSex(sex);
                    } else if (sex == 2) {
                        mSex.setText(R.string.sex_female);
                        mUserBean.setSex(sex);
                    }
                }
            }

        });
    }

    /**
     * 我的印象
     */
    private void forwardImpress() {
        startActivity(new Intent(mContext, MyImpressActivity.class));
    }

    @Override
    protected void onDestroy() {
        if (mImageUtil != null) {
            mImageUtil.release();
        }
        MainHttpUtil.cancel(MainHttpConsts.UPDATE_AVATAR);
        MainHttpUtil.cancel(MainHttpConsts.UPDATE_FIELDS);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void showData(UserBean u) {
        L.e("WOLF","图片地址："+u.getAvatar());
        ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
        mName.setText(u.getUserNiceName());
        mSign.setText(u.getSignature());
        mBirthday.setText(u.getBirthday());
        mSex.setText(u.getSex() == 1 ? R.string.sex_male : R.string.sex_female);
        tvWx.setText(u.getWechat());
        tvQq.setText(u.getQq());
        tvTl.setText(u.getTeliao());
        tvTg.setText(u.getTelegram());
        tvFb.setText(u.getFacebook());
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainJsEvent(final HeadUpdateEvent e) {
        ImgLoader.displayAvatar(mContext,e.getUrl(), mAvatar);
    }

}
