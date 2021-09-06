package com.yunbao.live.views;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.upload.UploadListener;
import com.lzy.okserver.upload.UploadTask;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UrlBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.ActivityResultCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.ImageResultCallback;
import com.yunbao.common.mob.MobCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveActivity;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.activity.LiveChooseClassActivity;
import com.yunbao.live.adapter.LiveReadyShareAdapter;
import com.yunbao.live.bean.LiveRoomTypeBean;
import com.yunbao.live.dialog.LiveRoomTypeDialogFragment;
import com.yunbao.live.dialog.LiveTimeDialogFragment;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.utils.CheckAudioPermission;
import com.yunbao.live.utils.JumpPermissionManagement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by cxf on 2018/10/7.
 * 开播前准备
 */

public class LiveReadyViewHolder extends AbsViewHolder implements View.OnClickListener {

    private static final String TAG = "LiveReadyViewHolder";
    private ImageView mAvatar;
    private TextView mCoverText;
    private EditText mEditTitle;
    private RecyclerView mLiveShareRecyclerView;
    private LiveReadyShareAdapter mLiveShareAdapter;
    private ProcessImageUtil mImageUtil;
    private String mAvatarFile;
    private TextView mLocation;
    private TextView mLiveClass;
    private TextView mLiveTypeTextView;//房间类型TextView
    private int mLiveClassID;//直播频道id
    private int mLiveType;//房间类型
    private int mLiveTypeVal;//房间密码，门票收费金额
    private int mLiveTimeCoin;//计时收费金额
    private ActivityResultCallback mActivityResultCallback;
    private CommonCallback<LiveRoomTypeBean> mLiveTypeCallback;
    private int mLiveGoodsID;

    private OkHttpClient mOkHttpClient;
    private UrlBean bean;
    private int testTimes = 0;
    private static final long TIMEOUT = 3000;
    private String fastPush;
    private boolean isFirstPull = true;
    private RelativeLayout rlRoot;
    private boolean booleanValue;
    private boolean havePhoto;
    private Dialog dialog;
    private boolean mFlashOpen;

    public LiveReadyViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_ready;
    }

    @Override
    public void init() {
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mCoverText = (TextView) findViewById(R.id.cover_text);
        mEditTitle = (EditText) findViewById(R.id.edit_title);
        mLocation = (TextView) findViewById(R.id.location);
        mLocation.setText(CommonAppConfig.getInstance().getCity());
        mLiveClass = (TextView) findViewById(R.id.live_class);
        mLiveTypeTextView = (TextView) findViewById(R.id.btn_room_type);
        mLiveShareRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        mLiveShareRecyclerView.setHasFixedSize(true);
        mLiveShareRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mLiveShareAdapter = new LiveReadyShareAdapter(mContext);
        mLiveShareRecyclerView.setAdapter(mLiveShareAdapter);
        mImageUtil = ((LiveActivity) mContext).getProcessImageUtil();

        booleanValue = SpUtil.getInstance().getBooleanValue(SpUtil.HAVEGOODS);
        if (booleanValue) {
            mLiveClass.setVisibility(View.VISIBLE);
        } else {
            mLiveClass.setVisibility(View.GONE);
        }

        mImageUtil.setImageResultCallback(new ImageResultCallback() {

            @Override
            public void beforeCamera() {
                ((LiveAnchorActivity) mContext).beforeCamera();
            }

            @Override
            public void onSuccess(final File file) {
                ((LiveAnchorActivity) mContext).dismissWindow();
                if (file != null) {
                    ImgLoader.display(mContext, file, mAvatar);
                    if (mAvatarFile == null) {
                        mCoverText.setText(WordUtil.getString(R.string.live_cover_2));
                        mCoverText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_live_cover));
                    }
                    havePhoto = true;
                    LiveHttpUtil.getUploadUrlInfo(new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
//                                String token = obj.getString("token");
//                                String url = obj.getString("url");
//                                String url_other = obj.getString("url_other");
//                                String image_path = obj.getString("image_path");

                                String url=obj.getString("image_url");
                                String token=obj.getString("image_token");
                                String image_path=obj.getString("live_path");

                                startUpLoad(file, token, url, image_path);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure() {
                ((LiveAnchorActivity) mContext).dismissWindow();
            }
        });
        mLiveClass.setOnClickListener(this);
        findViewById(R.id.avatar_group).setOnClickListener(this);
        findViewById(R.id.btn_camera).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_beauty).setOnClickListener(this);
        findViewById(R.id.btn_start_live).setOnClickListener(this);
        mLiveTypeTextView.setOnClickListener(this);
        mActivityResultCallback = new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                mLiveClassID = intent.getIntExtra(Constants.CLASS_ID, 0);
                mLiveGoodsID = intent.getIntExtra(Constants.GOODS_ID, 0);
                mLiveClass.setText(intent.getStringExtra(Constants.CLASS_NAME) + "-" +
                        intent.getStringExtra(Constants.GOODS_NAME));
            }
        };
        mLiveTypeCallback = new CommonCallback<LiveRoomTypeBean>() {
            @Override
            public void callback(LiveRoomTypeBean bean) {
                switch (bean.getId()) {
                    case Constants.LIVE_TYPE_NORMAL:
                        onLiveTypeNormal(bean);
                        break;
                    case Constants.LIVE_TYPE_PWD:
                        onLiveTypePwd(bean);
                        break;
                    case Constants.LIVE_TYPE_PAY:
                        onLiveTypePay(bean);
                        break;
                    case Constants.LIVE_TYPE_TIME:
                        onLiveTypeTime(bean);
                        break;
                }
            }
        };
        initEvents();
    }

    private void initEvents() {
        mEditTitle.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEditTitle.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        mEditTitle.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEditTitle.setSingleLine(true);
        mEditTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i) {
                    case EditorInfo.IME_ACTION_DONE:
//                        ToastUtil.show("点击了完成");
                        KeyboardUtils.hideSoftInput(mEditTitle);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        mEditTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 10) {
                    Toast.makeText(mContext, "标题最多可输入10个字", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideSoftInput(mEditTitle);
            }
        });
    }

    private void startUpLoad(final File file, final String token, final String url, String image_path) {
        if(dialog==null){
            dialog = DialogUitl.loadingNewDialog(mContext);
        }
        dialog.show();
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
                        L.e(TAG, "onStart------progress----->" + progress.fraction * 100);
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        L.e(TAG, "onProgress------progress----->" + progress.fraction * 100);
                    }

                    @Override
                    public void onError(Progress progress) {
                        L.e(TAG, "onProgress------progress----->" + progress.fraction * 100);
                        if(dialog!=null){
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFinish(String s, Progress progress) {
                        if(dialog!=null){
                            dialog.dismiss();
                        }
                        L.e(TAG, "onFinish------progress----->" + progress.fraction * 100);
                        L.e(TAG, "onFinish------s----->" + s);
                        try{
                            JSONObject obj = JSON.parseObject(s);
                            String imageUrl = obj.getString("src");
                            mAvatarFile = imageUrl;
                            L.e("WOLF","imageUrl:"+imageUrl);
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

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.avatar_group) {
            setAvatar();

        } else if (i == R.id.btn_camera) {
            toggleCamera();

        } else if (i == R.id.btn_close) {
            close();

        } else if (i == R.id.live_class) {
            chooseLiveClass();

        } else if (i == R.id.btn_beauty) {
            beauty();

        } else if (i == R.id.btn_room_type) {
            chooseLiveType();

        } else if (i == R.id.btn_start_live) {
            startLive();

        }
    }

    /**
     * 设置头像
     */
    private void setAvatar() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{
                R.string.camera, R.string.alumb}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.camera) {
                    if (mContext instanceof LiveAnchorActivity) {
                        ((LiveAnchorActivity) mContext).setShowLoadindDialog(true);
                    }
                    mImageUtil.getImageByCamera();
                } else {
                    mImageUtil.getImageByAlumb();
                }
            }
        });
    }

    /**
     * 切换镜头
     */
    private void toggleCamera() {
        ((LiveAnchorActivity) mContext).toggleCamera();
    }

    /**
     * 关闭
     */
    private void close() {
        ((LiveAnchorActivity) mContext).onBackPressed();
    }

    /**
     * 选择直播频道
     */
    private void chooseLiveClass() {
        Intent intent = new Intent(mContext, LiveChooseClassActivity.class);
        intent.putExtra(Constants.CLASS_ID, mLiveClassID);
        mImageUtil.startActivityForResult(intent, mActivityResultCallback);
    }

    /**
     * 设置美颜
     */

    private void beauty() {
        ((LiveAnchorActivity) mContext).beauty(true);
    }

    /**
     * 选择直播类型
     */
    private void chooseLiveType() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CHECKED_ID, mLiveType);
        LiveRoomTypeDialogFragment fragment = new LiveRoomTypeDialogFragment();
        fragment.setArguments(bundle);
        fragment.setCallback(mLiveTypeCallback);
        fragment.show(((LiveAnchorActivity) mContext).getSupportFragmentManager(), "LiveRoomTypeDialogFragment");
    }

    /**
     * 普通房间
     */
    private void onLiveTypeNormal(LiveRoomTypeBean bean) {
        mLiveType = bean.getId();
        mLiveTypeTextView.setText(bean.getName());
        mLiveTypeVal = 0;
        mLiveTimeCoin = 0;
    }

    /**
     * 密码房间
     */
    private void onLiveTypePwd(final LiveRoomTypeBean bean) {
        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_set_pwd), DialogUitl.INPUT_TYPE_NUMBER_PASSWORD, 8, new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.live_set_pwd_empty);
                } else {
                    mLiveType = bean.getId();
                    mLiveTypeTextView.setText(bean.getName());
                    if (StringUtil.isInt(content)) {
                        mLiveTypeVal = Integer.parseInt(content);
                    }
                    mLiveTimeCoin = 0;
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 付费房间
     */
    private void onLiveTypePay(final LiveRoomTypeBean bean) {
        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_set_fee), DialogUitl.INPUT_TYPE_NUMBER, 8, new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.live_set_fee_empty);
                } else {
                    mLiveType = bean.getId();
                    mLiveTypeTextView.setText(bean.getName());
                    if (StringUtil.isInt(content)) {
                        mLiveTypeVal = Integer.parseInt(content);
                    }
                    mLiveTimeCoin = 0;
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 计时房间
     */
    private void onLiveTypeTime(final LiveRoomTypeBean bean) {
        LiveTimeDialogFragment fragment = new LiveTimeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CHECKED_COIN, mLiveTimeCoin);
        fragment.setArguments(bundle);
        fragment.setCommonCallback(new CommonCallback<Integer>() {
            @Override
            public void callback(Integer coin) {
                mLiveType = bean.getId();
                mLiveTypeTextView.setText(bean.getName());
                mLiveTypeVal = coin;
                mLiveTimeCoin = coin;
            }
        });
        fragment.show(((LiveAnchorActivity) mContext).getSupportFragmentManager(), "LiveTimeDialogFragment");
    }

    public void hide() {
        if (mContentView != null && mContentView.getVisibility() == View.VISIBLE) {
            mContentView.setVisibility(View.INVISIBLE);
        }
    }


    public void show() {
        if (mContentView != null && mContentView.getVisibility() != View.VISIBLE) {
            mContentView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击开始直播按钮
     */
    private void startLive() {
//        FlashUtils.INSTANCE.init(mContext);
//        boolean open = !mFlashOpen;
//        if(open){
//            FlashUtils.INSTANCE.open();
//        }else {
//            FlashUtils.INSTANCE.close();
//        }
//        mFlashOpen=open;

        boolean startPreview = ((LiveAnchorActivity) mContext).isStartPreview();
        if (!startPreview) {
            ToastUtil.show(R.string.please_wait);
            return;
        }
        if (mLiveClassID == 0 && booleanValue) {
            ToastUtil.show(R.string.live_choose_live_class);
            return;
        }
        if (!havePhoto) {
            ToastUtil.show(R.string.live_choose_live_photo);
            return;
        }
        if (mLiveShareAdapter != null) {
            String type = mLiveShareAdapter.getShareType();
            if (!TextUtils.isEmpty(type)) {
                ((LiveActivity) mContext).shareLive(type, new MobCallback() {
                    @Override
                    public void onSuccess(Object data) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onFinish() {
                        createRoom();
                    }
                });
            } else {
                createRoom();
            }
        } else {
            createRoom();
        }
    }

    /**
     * 请求创建直播间接口，开始直播
     */
    private void createRoom() {
        if (mLiveClassID == 0 && booleanValue) {
            ToastUtil.show(R.string.live_choose_live_class);
            return;
        }

        fastPush = SpUtil.getInstance().getStringValue(SpUtil.FAST_PUSH);
        if (!TextUtils.isEmpty(fastPush)) {
//            showDialog(fastPush);
            enterRoom();
        } else {
            initClient();
            bean = JSON.parseObject(SpUtil.getInstance().getStringValue(SpUtil.ALL_URL), UrlBean.class);
            for (int i = 0; i < bean.getRhbyPath().getPush().size(); i++) {
                startTestPush(bean.getRhbyPath().getPush().get(i).getTest_url(), i);
            }
        }
    }

    private void enterRoom() {
        if (!CheckAudioPermission.isHasPermission(mContext)) {
            ToastUtil.show("请开启录音权限以正常直播");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    PermissionUtils.launchAppDetailsSettings();
                }
            }, 1000);
//            JumpPermissionManagement.GoToSetting(((LiveAnchorActivity)mContext));
            return;
        }

        if (!actionCheckPermission()) {
            ToastUtil.show("请开启拍照权限以正常直播");
            return;
        }

        String title = mEditTitle.getText().toString().trim();
        final Dialog dialog = DialogUitl.loadingNewDialog(mContext);
        dialog.show();
        L.e("WOLF","createRoom");
        LiveHttpUtil.createRoom(title, mLiveClassID, mLiveGoodsID, mLiveType, mLiveTypeVal, mAvatarFile, fastPush, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    L.e("开播", "createRoom------->" + info[0]);
                    ((LiveAnchorActivity) mContext).startLiveSuccess(info[0], mLiveType, mLiveTypeVal);
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }
        });
    }

    public void release() {
        mImageUtil = null;
        mActivityResultCallback = null;
        mLiveTypeCallback = null;
    }

    @Override
    public void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.CREATE_ROOM);
    }


    /**
     * 测试推流地址速度
     *
     * @param url
     * @param position
     */
    private void startTestPush(String url, final int position) {
        L.e("WOLF", "推流测试地址：" + url);
//        OkGo.<String>get("http://"+url.replace("rtmp://","")+"/nginx.html")
        OkGo.<String>get(url)
                .tag(position)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        int p = (int) response.getRawResponse().request().tag();
                        if (isFirstPull) {
                            isFirstPull = false;
                            L.e("WOLF", "推流最快线路：" + bean.getRhbyPath().getPush().get(position).getPush());
//                            Toast.makeText(mContext,"推流最快线路："+bean.getPull().get(position).getPull(),Toast.LENGTH_SHORT).show();
                            fastPush = bean.getRhbyPath().getPush().get(position).getPush();
                            SpUtil.getInstance().setStringValue(SpUtil.FAST_PUSH, bean.getRhbyPath().getPush().get(position).getPush());
                            OkGo.cancelAll(mOkHttpClient);
                            enterRoom();
//                            showDialog("");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        testTimes++;
                        if (testTimes == bean.getRhbyPath().getPush().size()) {
                            testTimes = 0;
                            fastPush = bean.getRhbyPath().getPush().get(0).getPush();
//                            Toast.makeText(mContext,"推流测试失败："+bean.getPush().get(0).getPush(),Toast.LENGTH_SHORT).show();
                            SpUtil.getInstance().setStringValue(SpUtil.FAST_PUSH, bean.getRhbyPath().getPush().get(0).getPush());
//                            showDialog("");
                            enterRoom();
                        }
                    }
                });
    }

    private void showDialog(String fastPush) {
        if (!fastPush.equals("")) {
            DialogUitl.showSimpleDialog(mContext, "再次进入无需选择，最快推流：" + SpUtil.getInstance().getStringValue(SpUtil.FAST_PUSH) + "\n", new DialogUitl.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    enterRoom();
                }
            });
        } else {
            List<String> a = new ArrayList();
            for (int i = 0; i < bean.getRhbyPath().getPush().size(); i++) {
                a.add(bean.getRhbyPath().getPush().get(i).getPush());
            }
            DialogUitl.showSimpleDialog(mContext, "所有推流:" + TextUtils.join(", ", a) + "\n" +
                    "最快推流：" + SpUtil.getInstance().getStringValue(SpUtil.FAST_PUSH) + "\n", new DialogUitl.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    enterRoom();
                }
            });
        }
    }

    private void initClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        builder.retryOnConnectionFailure(true);
        mOkHttpClient = builder.build();

        OkGo.getInstance().init(CommonAppContext.sInstance)
                .setOkHttpClient(mOkHttpClient)
                .setCacheMode(CacheMode.NO_CACHE)
                .setRetryCount(1);
    }

    /**
     * 检查是否有拍照权限
     * @return
     */
    public boolean actionCheckPermission() {
        String perms = Manifest.permission.CAMERA;
        Integer nRet = 0;
        nRet = ContextCompat.checkSelfPermission(mContext, perms);
        if (nRet == PERMISSION_GRANTED) {
            return true;
        } else if (nRet == PERMISSION_DENIED) {
            return false;
        } else {
            return false;
        }
    }
}
