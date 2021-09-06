package com.yunbao.video.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ClickListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.yunbao.common.Constants;
import com.yunbao.common.utils.IMStatusBarUtil;
import com.yunbao.video.R;

import java.io.File;

import static com.yunbao.common.Constants.VIDEO_SAVE_SAVE_AND_PUB;

/**
 * Created by cxf on 2018/11/26.
 */

public class VideoMyRecordActivity extends AppCompatActivity implements JCameraListener, ClickListener {
    private JCameraView jCameraView;
    private static final int MAX_DURATIONS = 15000;//最大录制时间15s
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        initView();
        initListener();
    }

    private void initListener() {
        jCameraView.setJCameraLisenter(this);
        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
        jCameraView.setChooseClickListener(this);
    }

    private void initView() {
        IMStatusBarUtil.setTranslucentForImageView(this, 0, null);
        jCameraView=findViewById(R.id.jCameraView);
        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

    @Override
    public void captureSuccess(Bitmap bitmap) {

    }

    @Override
    public void recordSuccess(String url, Bitmap firstFrame) {
        if(TextUtils.isEmpty(url)){
            return;
        }
        VideoPublishActivity.forward(this, url, VIDEO_SAVE_SAVE_AND_PUB , 0);
        finish();
    }

    @Override
    public void onClick() {
        Intent intent = new Intent(this, VideoChooseActivity.class);
        intent.putExtra(Constants.VIDEO_DURATION, MAX_DURATIONS);
        startActivityForResult(intent, 0);
    }
    /**
     * 选择本地视频的回调
     */
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
           String mVideoPath = data.getStringExtra(Constants.VIDEO_PATH);
            VideoPublishActivity.forward(this, mVideoPath, VIDEO_SAVE_SAVE_AND_PUB , 0);
            finish();
        }
    }
}
