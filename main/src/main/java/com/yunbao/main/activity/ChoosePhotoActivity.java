package com.yunbao.main.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.upload.UploadListener;
import com.lzy.okserver.upload.UploadTask;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.MineHeadAdapter;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.HeaderBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.ImageResultCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.common.utils.IMImageLoadUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.live.adapter.LiveGiftChildAdapter;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.main.R;
import com.yunbao.main.event.HeadUpdateEvent;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.utils.IMSavePhotoUtil;
import com.yunbao.video.http.VideoHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ChoosePhotoActivity extends AbsActivity implements View.OnClickListener, ImageResultCallback {

    private static final String TAG = "ChoosePhotoActivity";
    private ImageView mIvBg;
    private ImageView mIvHead;
    private ImageView mIvBack;
    private ImageView mIvChange;
    private TextView mContent;
    private TextView mTvSure,mTvSuccess;
    private TextView mTvFail;
    private RecyclerView recyclerView;
    private MineHeadAdapter adapter;
    private LottieAnimationView animationView;
    private  RelativeLayout rlLoading;

    private ProcessImageUtil mImageUtil;
    private UserBean mUserBean;
    public List<HeaderBean>datas;
    private ConfigBean config;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_photo;
    }

    @Override
    protected void main() {
        finById();
        initListener();
        initRecycleView();
        mUserBean = CommonAppConfig.getInstance().getUserBean();
        config = CommonAppConfig.getInstance().getConfig();

        if(mUserBean.getVip()!=null && mUserBean.getVip().getVip_is_king()==1){
            if(config!=null && !TextUtils.isEmpty(config.getUser_custom())){
                if(mUserBean.getVip().getType()>=Integer.parseInt(config.getUser_custom())){
                    mIvChange.setVisibility(View.VISIBLE);
                    mContent.setVisibility(View.VISIBLE);
//                    mTvSure.setVisibility(View.VISIBLE);
                }else {
                    mIvChange.setVisibility(View.GONE);
                    mContent.setVisibility(View.VISIBLE);
//                    mTvSure.setVisibility(View.GONE);
                }
                mContent.setText("温馨提示：当VIP等级达到"+config.getUser_custom()+"级后才能使用自定义头像");
            }
        }else {
            if(config!=null && !TextUtils.isEmpty(config.getUser_custom())){
                if(mUserBean.getLevel()>=Integer.parseInt(config.getUser_custom())){
                    mIvChange.setVisibility(View.VISIBLE);
                    mContent.setVisibility(View.VISIBLE);
//                    mTvSure.setVisibility(View.VISIBLE);
                }else {
                    mIvChange.setVisibility(View.GONE);
                    mContent.setVisibility(View.VISIBLE);
//                    mTvSure.setVisibility(View.GONE);
                }
                mContent.setText("温馨提示：当用户等级达到"+config.getUser_custom()+"级后才能使用自定义头像");
            }
        }

        if(mUserBean!=null){
            IMImageLoadUtil.CommonImageCircleLoad(CommonAppContext.sInstance,mUserBean.getAvatar(),mIvHead);
            ImgLoader.displayBlur(CommonAppContext.sInstance,mUserBean.getAvatar(),mIvBg);
        }

    }

    private void finById() {
        mIvBack = findViewById(R.id.btn_back);
        mIvBg =  findViewById(R.id.iv_bg);
        mIvHead =  findViewById(R.id.iv_header);
        mIvChange=  findViewById(R.id.iv_change);
        mContent =  findViewById(R.id.tv_content);
        mTvSure =  findViewById(R.id.tv_sure);
        mTvFail =  findViewById(R.id.tv_fail);
        mTvSuccess =  findViewById(R.id.tv_success);
        recyclerView =  findViewById(R.id.recyclerView);

        rlLoading=findViewById(com.yunbao.common.R.id.rl_loading);
        animationView=findViewById(com.yunbao.common.R.id.animation_view);
        animationView.setAnimation("live_loading.json");
    }

    private void initListener() {
        mIvBack.setOnClickListener(this);
        mTvSure.setOnClickListener(this);
        mIvChange.setOnClickListener(this);
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(this);
    }

    private void initRecycleView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3, GridLayoutManager.VERTICAL, false));
//        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 0, IMDensityUtil.dip2px(this,10));
//        recyclerView.addItemDecoration(decoration);
        getHeaderData();
        adapter = new MineHeadAdapter(datas);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                for (int i = 0; i < datas.size(); i++) {
                    if(i==position){
                        datas.get(i).setCheck(true);
                        mTvSure.setEnabled(true);
                        IMImageLoadUtil.CommonImageCircle(mContext,datas.get(i).getId(),mIvHead);
                        ImgLoader.displayBlur(CommonAppContext.sInstance,datas.get(i).getId(),mIvBg);
                    }else {
                        datas.get(i).setCheck(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void getHeaderData() {
        if(datas==null){
            datas=new ArrayList<>();
        }
        datas.add(new HeaderBean(R.mipmap.icon_header_1,false));
        datas.add(new HeaderBean(R.mipmap.icon_header_2,false));
        datas.add(new HeaderBean(R.mipmap.icon_header_3,false));
        datas.add(new HeaderBean(R.mipmap.icon_header_4,false));
        datas.add(new HeaderBean(R.mipmap.icon_header_5,false));
        datas.add(new HeaderBean(R.mipmap.icon_header_6,false));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back) {
            finish();
        }else  if (view.getId() == R.id.tv_sure) {
           updataMinmap();
        }else  if (view.getId() == R.id.iv_change) {
            editAvatar();
        }
    }

    /**
     * 选择本地minmap图片上传
     */
    private void updataMinmap() {
        for (int i = 0; i < datas.size(); i++) {
            if(datas.get(i).isCheck()){
                File file = IMSavePhotoUtil.getFile(datas.get(i).getId());
                if(file==null){
                    mTvFail.setVisibility(View.VISIBLE);
                    mTvSuccess.setVisibility(View.GONE);
                    return;
                }
                upadteHeadView(IMSavePhotoUtil.getFile(datas.get(i).getId()));
            }
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



    @Override
    public void beforeCamera() {

    }

    @Override
    public void onSuccess(final File file) {
        if (file != null) {
            upadteHeadView(file);
        }
    }

    /**
     * 获取头像
     */
    private void upadteHeadView(final File file) {

        animationView.playAnimation();
        rlLoading.setVisibility(View.VISIBLE);

        VideoHttpUtil.getUploadUrlInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
//                    String token=obj.getString("token");
//                    String url=obj.getString("url");
//                    String url_other=obj.getString("url_other");
//                    String image_path=obj.getString("image_path");

                    String url=obj.getString("image_url");
                    String token=obj.getString("image_token");
                    String image_path=obj.getString("user_path");
                    
                    startUpLoad(file,token, url,image_path);
                }
            }
        });
    }

    @Override
    public void onFailure() {
        mTvFail.setVisibility(View.VISIBLE);
        mTvSure.setVisibility(View.GONE);
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
                        try {
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
                if(mTvFail==null){
                    return;
                }
                if (code == 0 && info.length > 0) {
                    mTvFail.setVisibility(View.GONE);
                    mTvSuccess.setVisibility(View.VISIBLE);
                    rlLoading.setVisibility(View.GONE);
                    mTvFail.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },1500);
                    UserBean bean = CommonAppConfig.getInstance().getUserBean();
                    if (bean != null) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        L.e("WOLF",obj.toJSONString());
                        bean.setAvatar(obj.getString("avatar"));
                        bean.setAvatarThumb(obj.getString("avatarThumb"));
                        IMImageLoadUtil.CommonImageCircleLoad(CommonAppContext.sInstance,obj.getString("avatar"),mIvHead);
//                        IMImageLoadUtil.CommonImageCircleLoadXh(mContext,obj.getString("avatar"),mIvBg);
                        ImgLoader.displayBlur(CommonAppContext.sInstance,obj.getString("avatar"),mIvBg);
                        CommonAppConfig.getInstance().setUserBean(bean);
                        EventBus.getDefault().post(new HeadUpdateEvent(obj.getString("avatar")));
                    }
                }else {
                    rlLoading.setVisibility(View.GONE);
                    mTvFail.setVisibility(View.VISIBLE);
                    mTvSuccess.setVisibility(View.GONE);
                }
            }
        });
    }

}
