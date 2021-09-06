package com.yunbao.common.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.yunbao.common.R;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.common.utils.IMRoundAngleImageView;
import com.yunbao.common.views.MyWebView;

/**
 * 条款弹框
 */
public class VideoClauseDialog implements View.OnClickListener {
    private Context context;
    private TextView tvNo;
    private TextView tvYes;
    private AlertDialog dialog;
    private MyWebView webView;
    private IMRoundAngleImageView mTop;
    final String mimeType = "text/html";
    final String encoding = "utf-8";

    public String title;

    public String content;
    private AlertDialog.Builder builder;
    private CommitListener mCommitListener;

    public VideoClauseDialog(Context context) {
        this.context=context;
    }

    public VideoClauseDialog(Context context, String content) {
        this.context=context;
        this.content=content;
    }
    public void showNoticeDiglog() {
        builder = new AlertDialog.Builder(context);
        View dialogView =  View.inflate(context, R.layout.dialog_clause_video, null);
        tvNo =   dialogView.findViewById(R.id.tv_no);
        tvYes =  dialogView. findViewById(R.id.tv_yes);
        webView =  dialogView. findViewById(R.id.im_web);
        initWebView();
        setWebData();
        tvNo.setOnClickListener(this);
        tvYes.setOnClickListener(this);
        builder.setView(dialogView);
    }

    private void initWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDefaultFontSize(14);
        webView.setWebViewClient(mWebViewClient);
    }


    public void setWebData() {
        try {
            webView.loadDataWithBaseURL(null,getHtmlData(content), mimeType, encoding,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    public WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.e("----","shouldOverrideUrlLoading:"+request.getUrl());
            return false;
        }

        //页面加载开始
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.e("----","onPageStarted:"+url);
        }
        //页面加载完成
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e("----","onPageFinished:"+url);
            if(context==null||((Activity)context).isDestroyed()){
                return;
            }

//            dialog =  builder.show();
//            Window window=dialog.getWindow();
//            dialog.getWindow().setDimAmount(0);//设置昏暗度为0
//            dialog.setCancelable(false);
//
//            window.setWindowAnimations(R.style.ActionSheetDialogAnimations);  //添加动画
//            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//            params.width = IMDensityUtil.getScreenWidth(context) - IMDensityUtil.dip2px(context,70);
//            int screenHeight = IMDensityUtil.getScreenHeight(context);
//            params.height=(int) (screenHeight*3.5f/6.9);
//            window.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.im_shape__bg2));
        }
    };

    public void dismiss(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }

    public   void   showDialog(){
        dialog =  builder.show();
        Window window=dialog.getWindow();
        dialog.getWindow().setDimAmount(0);//设置昏暗度为0
        dialog.setCancelable(false);

        window.setWindowAnimations(R.style.ActionSheetDialogAnimations);  //添加动画
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = IMDensityUtil.getScreenWidth(context) - IMDensityUtil.dip2px(context,70);
        int screenHeight = IMDensityUtil.getScreenHeight(context);
        params.height=(int) (screenHeight*3.5f/6.9);
        window.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.im_shape__bg2));
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.tv_yes){
            //立即领取
            if(mCommitListener!=null){
                mCommitListener.onCommitClick(true);
            }
        }else {
            if(mCommitListener!=null){
                mCommitListener.onCommitClick(false);
            }
            if(dialog!=null){
                dialog.dismiss();
            }
        }
    }

    public void setCommitClick(CommitListener commitListener) {
        mCommitListener = commitListener;
    }

    public interface CommitListener {
        void onCommitClick(boolean isCommit);
    }
}
