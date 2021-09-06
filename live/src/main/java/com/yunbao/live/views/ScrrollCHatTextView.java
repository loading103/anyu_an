package com.yunbao.live.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


import com.yunbao.common.R;
import com.yunbao.live.bean.LiveChatBean;
import com.yunbao.live.utils.LiveTextRender;

import java.util.List;

/**
 * 上下滚动的 textView
 */
public class ScrrollCHatTextView extends LinearLayout {
    private TextView mBannerTV1;
    private TextView mBannerTV2;
    private Handler handler;
    private boolean isShow = false;
    private int startY1, endY1, startY2, endY2;
    private Runnable runnable;
    private List<LiveChatBean> datas;
    private int position = 0;
    private int offsetY = 100;
    private long showTime=2000;

    public ScrrollCHatTextView(Context context) {
        this(context, null);
    }

    public ScrrollCHatTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrrollCHatTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = LayoutInflater.from(context).inflate(R.layout.widget_chat_text_layout, this);
        mBannerTV1 = view.findViewById(R.id.tv_banner1);
        mBannerTV2 = view.findViewById(R.id.tv_banner2);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                isShow = !isShow;
                if (position == datas.size() - 1) {
                    handler.postDelayed(runnable, showTime);
                    isShow = !isShow;
                    return;
                }
                if (isShow) {
                    LiveTextRender.render(getContext(), mBannerTV1, datas.get(position++));
                    LiveTextRender.render(getContext(), mBannerTV2, datas.get(position));
                    mBannerTV1.setCompoundDrawablePadding(0);
                    mBannerTV1.setCompoundDrawables(null, null, null, null);
                    mBannerTV2.setCompoundDrawablePadding(0);
                    mBannerTV2.setCompoundDrawables(null, null, null, null);
                } else {
                    LiveTextRender.render(getContext(), mBannerTV2, datas.get(position++));
                    LiveTextRender.render(getContext(), mBannerTV1, datas.get(position));
                    mBannerTV1.setCompoundDrawablePadding(0);
                    mBannerTV1.setCompoundDrawables(null, null, null, null);
                    mBannerTV2.setCompoundDrawablePadding(0);
                    mBannerTV2.setCompoundDrawables(null, null, null, null);
                }

                startY1 = isShow ? 0 : offsetY;
                endY1 = isShow ? -offsetY : 0;
                ObjectAnimator.ofFloat(mBannerTV1, "translationY", startY1, endY1).setDuration(300).start();

                startY2 = isShow ? offsetY : 0;
                endY2 = isShow ? 0 : -offsetY;
                ObjectAnimator.ofFloat(mBannerTV2, "translationY", startY2, endY2).setDuration(300).start();

                handler.postDelayed(runnable, showTime);
            }
        };
    }

    public List<LiveChatBean> getList() {
        return datas;
    }

    public void setList(List<LiveChatBean> datas) {
        this.datas = datas;
    }

    public void startScroll() {
        LiveTextRender.render(getContext(), mBannerTV1, datas.get(0));
        handler.postDelayed(runnable,showTime);
    }

    public void stopScroll() {
        handler.removeCallbacks(runnable);
        if(datas!=null){
            datas.clear();
        }
        position = 0;
    }

}