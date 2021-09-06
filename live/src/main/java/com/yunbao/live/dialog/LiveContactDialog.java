package com.yunbao.live.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.yunbao.common.Constants;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.live.adapter.LiveContactAdapter;
import com.yunbao.live.bean.LiveContactBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2020/5/13.
 * Describe:直播联系主播
 */
public class LiveContactDialog extends AbsDialogFragment {

    private final List<LiveContactBean> list;

    public LiveContactDialog(List<LiveContactBean> list) {
        this.list=list;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_contact;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }


    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setWindowAnimations(R.style.ActionSheetDialogAnimations);  //添加动画
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        RecyclerView recyclerView =(RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager=new LinearLayoutManager(mContext);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        LiveContactAdapter adapter=new LiveContactAdapter();
        adapter.addChildClickViewIds(R.id.tv_copy,R.id.tv_open);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {

                if (view.getId() == R.id.tv_copy) {
                    copy(((LiveContactBean)adapter.getData().get(position)).getContent());
                    ToastUtil.show("复制成功");
                }else if(view.getId() == R.id.tv_open){
                    String type = ((LiveContactBean) adapter.getData().get(position)).getType();
                    switch (type){
                        case Constants.WX:
                            openApp("com.tencent.mm","微信");
                            break;
                        case Constants.QQ:
                            openApp("com.tencent.mobileqq","QQ");
                            break;
                        case Constants.TL:
                            openApp("com.rhby.cailexun","特聊");
                            break;
                        case Constants.TG:
                            openApp("org.telegram.messenger","Telegram");
                            break;
                        case Constants.FB:
                            openApp("com.facebook.katana","Facebook");
                            break;
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setEmptyView(R.layout.layout_empty_contact);
        adapter.addData(list);
    }

    /**
     * 打开应用
     */
    private void openApp(String packageName,String name) {
        if(!AppUtils.isAppInstalled(packageName)){
            ToastUtil.show("未安装"+name);
            return;
        }
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        startActivity(intent);
    }

    /**
     * 复制
     *
     * @param content
     */
    private void copy(String content) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }
}
