package com.yunbao.main.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.activity.SmallProgramTitleActivity;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.main.R;
import com.yunbao.main.utils.IMRoundAngleImageView;
import com.yunbao.common.views.MyWebView;

public class IMNoticeDiglog implements View.OnClickListener {
    private Context context;
    private TextView mbtn;
    private AlertDialog dialog;
    private MyWebView webView;
    private IMRoundAngleImageView mTop;
    final String mimeType = "text/html";
    final String encoding = "utf-8";
    
    public String title;

    public String content;
    private AlertDialog.Builder builder;
    private CloseListener mCloseListener;

    public IMNoticeDiglog(Context context) {
        this.context=context;
    }

    public IMNoticeDiglog(Context context,String title, String content) {
        this.context=context;
        this.title=title;
        this.content=content;
    }
    public void showNoticeDiglog() {
        builder = new AlertDialog.Builder(context);
        View dialogView =  View.inflate(context, R.layout.view_know_notice1, null);
        mTop =   dialogView.findViewById(R.id.top);
        mbtn =  dialogView. findViewById(R.id.btn_sign);
        webView =  dialogView. findViewById(R.id.im_web);
        initWebView();
        setWebData();

        mbtn.setOnClickListener(this);
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
            if(!TextUtils.isEmpty(title)){
//                Glide.with(context).load(title).skipMemoryCache(false).into(mTop);
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

    public WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {//7.0以下
            Log.e("----","shouldOverrideUrlLoading:"+url);
            OpenUrlUtils.getInstance().setContext(context)
                    .setType(3)
                    .setSlideTarget("1")
                    .setJump_type("1")
                    .setTitle("normal")
                    .go(url+"");
            if(dialog!=null){
                dialog.dismiss();
            }
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.e("----","shouldOverrideUrlLoading:"+request.getUrl());
            OpenUrlUtils.getInstance().setContext(context)
                    .setType(3)
                    .setSlideTarget("1")
                    .setJump_type("1")
                    .setTitle("normal")
                    .go(request.getUrl()+"");
            if(dialog!=null){
                dialog.dismiss();
            }
            return true;
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

            dialog =  builder.show();
            Window window=dialog.getWindow();
            dialog.getWindow().setDimAmount(0);//设置昏暗度为0
            dialog.setCancelable(false);
//            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            window.setWindowAnimations(R.style.ActionSheetDialogAnimations);  //添加动画
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = IMDensityUtil.getScreenWidth(context) - IMDensityUtil.dip2px(context,70);
            int screenHeight = IMDensityUtil.getScreenHeight(context);
            params.height=(int) (screenHeight*3.5f/6.9);
            window.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.im_shape__bg2));
        }
    };

    @Override
    public void onClick(View view) {
        if(dialog!=null){
            dialog.dismiss();
        }

        if(mCloseListener!=null){
            mCloseListener.onCancelClick();
        }
    }

    public void setCancerClick(CloseListener closeListener) {
        mCloseListener = closeListener;
    }

    public interface CloseListener {
        void onCancelClick();
    }
}
