package com.faceunity.nama.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.faceunity.nama.FURenderer;
import com.faceunity.nama.R;
import com.yunbao.common.views.AbsViewHolder;

import static com.lzy.okgo.utils.HttpUtils.runOnUiThread;


/**
 * Created by wolf on 2020/11/16.
 * 即构美颜
 */

public class FaceUnityBeautyNewViewHolder extends AbsViewHolder implements View.OnClickListener, BeautyViewHolder, SensorEventListener {

    private SparseArray<View> mSparseArray;
    private int mCurKey;
    private VisibleListener mVisibleListener;
    private boolean mShowed;
    private FURenderer mFURenderer;
    private TextView mTextView;
    private FaceUnityView faceUnityView;

    public FaceUnityBeautyNewViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_new_beauty_face;
    }



    @Override
    public void init() {
        faceUnityView = (FaceUnityView) findViewById(R.id.faceunity_control);
        findViewById(R.id.fl_root).setOnClickListener(this);
    }

    public void setShow(){
        if(faceUnityView!=null){
            faceUnityView.setShow();
        }
    }

    public void setModuleManager(FURenderer mFURenderer) {

        faceUnityView.setModuleManager(mFURenderer);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void show() {
        if (mVisibleListener != null) {
            mVisibleListener.onVisibleChanged(true);
        }
        if (mParentView != null && mContentView != null) {

            ViewParent parent = mContentView.getParent();
            mContentView.setTag("mContentView");
            if(mParentView.findViewWithTag("mContentView")==null){
                if (parent != null) {
                    ((ViewGroup) parent).removeView(mContentView);
                }
                mParentView.addView(mContentView);
            }
            if(mContentView!=null){
                mContentView.setVisibility(View.VISIBLE);
            }
        }
        mShowed = true;
    }

    @Override
    public void hide() {
//        removeFromParent();
        if(mContentView!=null){
            mContentView.setVisibility(View.GONE);
        }

        if (mVisibleListener != null) {
            mVisibleListener.onVisibleChanged(false);
        }
        mShowed = false;
    }

    @Override
    public void onClick(View v) {
        hide();
    }

    @Override
    public boolean isShowed() {
        return mShowed;
    }

    @Override
    public void release() {
        mVisibleListener = null;
    }

    @Override
    public void setVisibleListener(VisibleListener visibleListener) {
        mVisibleListener = visibleListener;
    }

}
