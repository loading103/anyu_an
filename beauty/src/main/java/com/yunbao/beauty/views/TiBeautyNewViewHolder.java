package com.yunbao.beauty.views;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.yunbao.beauty.R;
import com.yunbao.beauty.TiPanelLayout;
import com.yunbao.beauty.interfaces.BeautyEffectListener;
import com.yunbao.beauty.interfaces.BeautyViewHolder;
import com.yunbao.beauty.interfaces.TiBeautyEffectListener;
import com.yunbao.common.views.AbsViewHolder;

import cn.tillusory.sdk.TiSDKManager;


/**
 * Created by cxf on 2018/6/22.
 * 萌颜UI相关
 */

public class TiBeautyNewViewHolder extends AbsViewHolder implements View.OnClickListener, BeautyViewHolder {

    private SparseArray<View> mSparseArray;
    private int mCurKey;
    private TieZhiAdapter mTieZhiAdapter;
    private TiBeautyEffectListener mEffectListener;
    private VisibleListener mVisibleListener;
    private boolean mShowed;
    private TiPanelLayout view;

    public TiBeautyNewViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_new_beauty;
    }



    @Override
    public void init() {
        FrameLayout flRoot=(FrameLayout)findViewById(R.id.fl_root);
        view=new TiPanelLayout(mContext);
        flRoot.addView(view,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.init(TiSDKManager.getInstance());
    }

    @Override
    public void setEffectListener(BeautyEffectListener effectListener) {
        if (effectListener != null && effectListener instanceof TiBeautyEffectListener) {
            mEffectListener = (TiBeautyEffectListener) effectListener;
        }
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

    }

    @Override
    public boolean isShowed() {
        return mShowed;
    }

    @Override
    public void release() {
        mVisibleListener = null;
        mEffectListener = null;
        if (mTieZhiAdapter != null) {
            mTieZhiAdapter.clear();
        }
    }

    @Override
    public void setVisibleListener(VisibleListener visibleListener) {
        mVisibleListener = visibleListener;
    }
}
