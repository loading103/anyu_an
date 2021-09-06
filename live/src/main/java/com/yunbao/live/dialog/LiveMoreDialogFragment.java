package com.yunbao.live.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.just.agentweb.AgentWeb;
import com.yunbao.common.Constants;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.live.R;

/**
 * Created by cxf on 2018/10/24.
 * 直播间礼物旁边更多
 */

public class LiveMoreDialogFragment extends AbsDialogFragment {


    private AgentWeb mAgentWeb;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_buttom;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(300);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String more_web_url = bundle.getString(Constants.MORE_WEB_URL);
            openUrl(more_web_url);
        }

    }

    private void openUrl(String url) {
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((ViewGroup)findViewById(R.id.fl_root), new LinearLayout.LayoutParams(-1, -1))
                .closeIndicator()
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .createAgentWeb()
                .ready()
                .go(url);
    }

}
