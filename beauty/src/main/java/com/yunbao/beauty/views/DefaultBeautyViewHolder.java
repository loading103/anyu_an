package com.yunbao.beauty.views;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.yunbao.beauty.R;
import com.yunbao.beauty.custom.TextSeekBar;
import com.yunbao.beauty.interfaces.BeautyEffectListener;
import com.yunbao.beauty.interfaces.BeautyViewHolder;
import com.yunbao.beauty.interfaces.DefaultBeautyEffectListener;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.views.AbsViewHolder;


/**
 * Created by cxf on 2018/6/22.
 * 默认美颜
 */

public class DefaultBeautyViewHolder extends AbsViewHolder implements View.OnClickListener, BeautyViewHolder{

    private SparseArray<View> mSparseArray;
    private int mCurKey;
    private DefaultBeautyEffectListener mEffectListener;
    private VisibleListener mVisibleListener;
    private boolean mShowed;
    private RangeSeekBar seekBar;
    private RangeSeekBar seekBar2;
    private RangeSeekBar seekBar3;
    private ImageView ivCheck;
    private boolean isCheck=true;

    public DefaultBeautyViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_beauty_default;
    }

    @Override
    public void init() {
        findViewById(R.id.btn_beauty).setOnClickListener(this);
        findViewById(R.id.btn_filter).setOnClickListener(this);
        findViewById(R.id.btn_hide).setOnClickListener(this);
        findViewById(R.id.rl_root).setOnClickListener(this);
        mSparseArray = new SparseArray<>();
        mSparseArray.put(R.id.btn_beauty, findViewById(R.id.group_beauty));
        mSparseArray.put(R.id.btn_filter, findViewById(R.id.group_filter));
        mCurKey = R.id.btn_beauty;
        TextSeekBar.OnSeekChangeListener onSeekChangeListener = new TextSeekBar.OnSeekChangeListener() {
            @Override
            public void onProgressChanged(View view, int progress) {
                if (mEffectListener != null) {
                    int i = view.getId();
                    if (i == R.id.seek_meibai) {
                        mEffectListener.onMeiBaiChanged(progress);
                    } else if (i == R.id.seek_mopi) {
                        mEffectListener.onMoPiChanged(progress);
                    } else if (i == R.id.seek_hongrun) {
                        mEffectListener.onHongRunChanged(progress);
                    }
                }
            }
        };

        TextSeekBar seekMeiBai = ((TextSeekBar) findViewById(R.id.seek_meibai));
        TextSeekBar seekMoPi = ((TextSeekBar) findViewById(R.id.seek_mopi));
        TextSeekBar seekHongRun = ((TextSeekBar) findViewById(R.id.seek_hongrun));
        seekBar = ((RangeSeekBar) findViewById(R.id.seekBar));
        seekBar2 = ((RangeSeekBar) findViewById(R.id.seekBar2));
        seekBar3 = ((RangeSeekBar) findViewById(R.id.seekBar3));
        ivCheck = ((ImageView) findViewById(R.id.iv_check));
        seekMeiBai.setOnSeekChangeListener(onSeekChangeListener);
        seekMoPi.setOnSeekChangeListener(onSeekChangeListener);
        seekHongRun.setOnSeekChangeListener(onSeekChangeListener);

        seekBar.setProgress(0);
        seekBar2.setProgress(0);
        seekBar3.setProgress(0);
        seekBar.setIndicatorTextDecimalFormat("0");
        seekBar2.setIndicatorTextDecimalFormat("0");
        seekBar3.setIndicatorTextDecimalFormat("0");

        ivCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCheck){
                    isCheck=false;
                    ivCheck.setImageResource(R.mipmap.btn_uncheck);
                    mEffectListener.onMeiBaiChanged(0);
                    mEffectListener.onMoPiChanged(0);
                    mEffectListener.onHongRunChanged(0);
                    seekBar.setProgressColor(mContext.getResources().getColor(R.color.color_AAAAAA));
                    seekBar2.setProgressColor(mContext.getResources().getColor(R.color.color_AAAAAA));
                    seekBar3.setProgressColor(mContext.getResources().getColor(R.color.color_AAAAAA));
                    seekBar.setEnabled(false);
                    seekBar2.setEnabled(false);
                    seekBar3.setEnabled(false);
                }else {
                    isCheck=true;
                    ivCheck.setImageResource(R.mipmap.btn_check);
                    mEffectListener.onMeiBaiChanged((int)seekBar.getLeftSeekBar().getProgress());
                    mEffectListener.onMoPiChanged((int)seekBar2.getLeftSeekBar().getProgress());
                    mEffectListener.onHongRunChanged((int)seekBar3.getLeftSeekBar().getProgress());
                    seekBar.setProgressColor(mContext.getResources().getColor(R.color.colorPrimary));
                    seekBar2.setProgressColor(mContext.getResources().getColor(R.color.colorPrimary));
                    seekBar3.setProgressColor(mContext.getResources().getColor(R.color.colorPrimary));
                    seekBar.setEnabled(true);
                    seekBar2.setEnabled(true);
                    seekBar3.setEnabled(true);
                }
                seekBar.setProgress((int)seekBar.getLeftSeekBar().getProgress());
                seekBar2.setProgress((int)seekBar2.getLeftSeekBar().getProgress());
                seekBar3.setProgress((int)seekBar3.getLeftSeekBar().getProgress());
            }
        });

        seekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (mEffectListener != null&&isCheck) {
                    mEffectListener.onMeiBaiChanged((int)view.getLeftSeekBar().getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });
        seekBar2.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (mEffectListener != null&&isCheck) {
                    mEffectListener.onMoPiChanged((int)view.getLeftSeekBar().getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });
        seekBar3.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (mEffectListener != null&&isCheck) {
                    mEffectListener.onHongRunChanged((int)view.getLeftSeekBar().getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

        //滤镜
        RecyclerView filterRecyclerView = (RecyclerView) findViewById(R.id.filter_recyclerView);
        filterRecyclerView.setHasFixedSize(true);
        filterRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
    }


    @Override
    public void setEffectListener(BeautyEffectListener effectListener) {
        if (effectListener != null && effectListener instanceof DefaultBeautyEffectListener) {
            mEffectListener = (DefaultBeautyEffectListener) effectListener;
        }
    }

    @Override
    public void show() {
        if (mVisibleListener != null) {
            mVisibleListener.onVisibleChanged(true);
        }
        if (mParentView != null && mContentView != null) {
            ViewParent parent = mContentView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mContentView);
            }
            mParentView.addView(mContentView);
        }
        mShowed = true;
    }

    @Override
    public void hide() {
        removeFromParent();
        if (mVisibleListener != null) {
            mVisibleListener.onVisibleChanged(false);
        }
        mShowed = false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_beauty || id == R.id.btn_filter) {
            toggle(id);
        } else if (id == R.id.btn_hide) {
            hide();
        }else if (id == R.id.rl_root) {
            hide();
        }
    }

    private void toggle(int key) {
        if (mCurKey == key) {
            return;
        }
        mCurKey = key;
        for (int i = 0, size = mSparseArray.size(); i < size; i++) {
            View v = mSparseArray.valueAt(i);
            if (mSparseArray.keyAt(i) == key) {
                if (v.getVisibility() != View.VISIBLE) {
                    v.setVisibility(View.VISIBLE);
                }
            } else {
                if (v.getVisibility() == View.VISIBLE) {
                    v.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public boolean isShowed() {
        return mShowed;
    }

    @Override
    public void release() {
        mVisibleListener = null;
        mEffectListener = null;
    }

    @Override
    public void setVisibleListener(VisibleListener visibleListener) {
        mVisibleListener = visibleListener;
    }
}
