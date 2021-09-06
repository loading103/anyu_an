package com.yunbao.main.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.common.views.MyWebView;
import com.yunbao.main.R;
import com.yunbao.main.utils.IMRoundAngleImageView;

/**
 * 知道了通知栏
 */

public class MainNoticeViewHolder extends AbsViewHolder {

    private View mBg;
    private View mDialog;
    private TextView mbtn;
    private MyWebView webView;
    private IMRoundAngleImageView mTop;
    final String mimeType = "text/html";
    final String encoding = "utf-8";
    public MainNoticeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_know_notice;
    }

    @Override
    public void init() {
        mBg = findViewById(R.id.bg);
        mDialog = findViewById(R.id.dialog);
        mTop = (IMRoundAngleImageView) findViewById(R.id.top);
        mbtn = (TextView) findViewById(R.id.btn_sign);
        webView = (MyWebView) findViewById(R.id.im_web);
        int screenHeight = IMDensityUtil.getScreenHeight(mContext);
        ViewGroup.LayoutParams layoutParams = mDialog.getLayoutParams();
        layoutParams.height=(int) (screenHeight*4.0f/6.9);
        mDialog.setLayoutParams(layoutParams);
        mbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
    public void setData(String title, String content) {
        try {
            if(!TextUtils.isEmpty(title)){
                Glide.with(mContext).load(title).skipMemoryCache(false).into(mTop);
            }
            webView.loadDataWithBaseURL(null,getHtmlData(content), mimeType, encoding,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }
    public void show() {
        ViewParent parent = mContentView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(mContentView);
        }
        if (mParentView != null) {
            mParentView.addView(mContentView);
        }
    }

    private void dismiss() {
        ViewParent parent = mContentView.getParent();
        if (parent != null && parent == mParentView) {
            mParentView.removeView(mContentView);
        }
    }
}
