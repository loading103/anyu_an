package com.yunbao.live.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.google.gson.jpush.Gson;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.adapter.LiveSimiListAdapter;
import com.yunbao.live.bean.PrivateUserBean;
import com.yunbao.live.bean.PrivateUserPlayBean;
import com.yunbao.live.custom.MyCountDownTimer;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.socket.SocketChatUtil;
import com.yunbao.live.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 私密主播列表
 */
public class LiveSiMiDialog extends AbsDialogFragment implements View.OnClickListener {


    private final int time;
    private TextView tv_refuse;
    private TextView tv_accept;
    private TextView tv_start;
    private TextView tv_refused;
    private TextView tv_accepted;
    private TextView tv_time;
    private RecyclerView recyclerView;
    private  List<PrivateUserBean> userdatas=new ArrayList<>();

    private MyCountDownTimer mTimer;
    private TextView tvSum;
    private LiveSimiListAdapter adapter;

    public LiveSiMiDialog(List<PrivateUserBean> userdatas,int time) {
        this.userdatas=userdatas;
        this.time=time;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_simi;
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
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        window.setWindowAnimations(R.style.ActionSheetDialogAnimations);  //添加动画
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewId();
        initRecycleView();
        countDown();
        tvSum.setText("总人数:"+userdatas.size());
    }


    private void findViewId() {
        recyclerView =(RecyclerView) findViewById(R.id.recyclerView);
        tv_refuse =(TextView) findViewById(R.id.tv_refuse);
        tv_accept =(TextView) findViewById(R.id.tv_accept);
        tv_start =(TextView) findViewById(R.id.tv_start);
        tv_time=(TextView) findViewById(R.id.tv_time);
        tvSum=(TextView) findViewById(R.id.tv_sum);
        tv_refuse.setOnClickListener(this);
        tv_accept.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        findViewById(R.id.ll_root).setOnClickListener(this);
    }

    private void initRecycleView() {
        LinearLayoutManager layoutManager=new LinearLayoutManager(mContext);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        boolean flag=false;
        tv_start.setEnabled(false);
        for (PrivateUserBean bean:userdatas){
            if(bean.getSimiType()==1||bean.getSimiType()==2){
                flag=true;
            }
            if(bean.getSimiType()==1){
                tv_start.setEnabled(true);
            }
        }

        if(!flag){
            for (int i = 0; i < userdatas.size(); i++) {
                userdatas.get(i).setSimiType(0);
            }
        }

        adapter=new LiveSimiListAdapter(tv_start,this);
        recyclerView.setAdapter(adapter);
        adapter.setList(userdatas);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.tv_refuse){
//            dismiss();
            DialogUitl.showSimpleDialog(mContext,"您确定要拒绝所有申请用户吗？\n" +
                    "拒绝后，本次私密互动将被取消~", new DialogUitl.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    for (PrivateUserBean bean:userdatas){
                        bean.setSimiType(2);
                    }
                    adapter.setList(userdatas);
                    ((LiveAnchorActivity)mContext).closePrivate(2);
//                    SocketChatUtil.sendOneKeyRefusePrivateLiveMsg(((LiveAnchorActivity)mContext).getSocketClient(),JSON.toJSONString(userdatas));
                    dismiss();
                }
            });
        }else  if(view.getId()==R.id.tv_accept){
            for (PrivateUserBean bean:userdatas){
                bean.setSimiType(1);
            }
            adapter.setList(userdatas);
            tv_start.setEnabled(true);
//            dismiss();
        }else  if(view.getId()==R.id.ll_root){
            dismiss();
        }else if(view.getId()==R.id.tv_start){
            List<PrivateUserBean> a=new ArrayList<>();
            List<PrivateUserBean> b=new ArrayList<>();
            for(PrivateUserBean bean:userdatas){
                if(bean.getSimiType()==1){
                    a.add(bean);
                }else {
                    b.add(bean);
                }
            }
            startPrivate(JSON.toJSONString(userdatas),JSON.toJSONString(b));
        }
    }

    /**
     * 主播开始私密互动
     * @param all_users 所有用户
     * @param refuse_users 拒绝的用户
     */
    private void startPrivate(final String all_users,final String refuse_users){
        LiveHttpUtil.startPrivate(((LiveAnchorActivity)mContext).getStream(),refuse_users,new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if(getDialog()==null){
                        return;
                    }
                    if(!TextUtils.isEmpty(refuse_users)&&!refuse_users.equals("[]")){
                        SocketChatUtil.sendRefusePrivateLiveMsg(((LiveAnchorActivity)mContext).getSocketClient(),refuse_users);
                    }
//                    SocketChatUtil.sendPrivateLiveMsg(((LiveAnchorActivity)mContext).getSocketClient(),5);
                    SocketChatUtil.sendPrivateLivePlayMsg(((LiveAnchorActivity)mContext).getSocketClient(),all_users);
                    dismiss();
                }else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 倒计时
     */
    private void countDown() {
        if(tv_time!=null){
            mTimer = new MyCountDownTimer(time*1000+30, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if(tv_time!=null){
//                        tv_time.setText((millisUntilFinished/1000)+"s");
                        tv_time.setText(TimeUtils.convertSecToTimeString2((millisUntilFinished/1000)));
                    }
                }

                @Override
                public void onFinish() {
                    List<PrivateUserBean> a=new ArrayList<>();
                    List<PrivateUserBean> b=new ArrayList<>();
                    for(PrivateUserBean bean:userdatas){
                        if(bean.getSimiType()==1){
                            a.add(bean);
                        }else {
                            b.add(bean);
                        }
                    }
                    if(a.size()==0){
                        ((LiveAnchorActivity)mContext).closePrivate(2);
                    }else {
                        startPrivate(JSON.toJSONString(userdatas),JSON.toJSONString(b));
                    }
//                    if(getDialog()!=null){
//                        dismiss();
//                    }
                }
            }.start();
        }
    }

    @Override
    public void onDestroy() {
        if(mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
        super.onDestroy();
    }
}
