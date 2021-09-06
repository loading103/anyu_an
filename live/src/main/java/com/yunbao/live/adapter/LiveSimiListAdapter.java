package com.yunbao.live.adapter;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.IMImageLoadUtil;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.live.bean.PrivateUserBean;
import com.yunbao.live.dialog.LiveSiMiDialog;
import com.yunbao.live.socket.SocketChatUtil;

import java.util.List;

public class LiveSimiListAdapter extends BaseQuickAdapter<PrivateUserBean, BaseViewHolder>{

    private final TextView tv_start;
    private final LiveSiMiDialog liveSiMiDialog;
    private List<PrivateUserBean> data;

    public LiveSimiListAdapter(TextView tv_start, LiveSiMiDialog liveSiMiDialog) {
        super(R.layout.item_simi_list);
        this.tv_start=tv_start;
        this.liveSiMiDialog=liveSiMiDialog;
    }
    @Override
    protected void convert(final BaseViewHolder helper, final PrivateUserBean item) {

        handleState(helper,item);

        helper.getView(R.id.tv_refuse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                helper.getView(R.id.rl_contain).setVisibility(View.GONE);
//                helper.getView(R.id.tv_accepted).setVisibility(View.GONE);
//                helper.getView(R.id.tv_refused).setVisibility(View.VISIBLE);
                item.setSimiType(2);
////                data.get(helper.getAdapterPosition()).setSimiType(2);
                notifyStatus(true,helper,item);
            }
        });
        helper.getView(R.id.tv_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.getView(R.id.rl_contain).setVisibility(View.GONE);
                helper.getView(R.id.tv_refused).setVisibility(View.GONE);
                helper.getView(R.id.tv_accepted).setVisibility(View.VISIBLE);
                item.setSimiType(1);
//                data.get(helper.getAdapterPosition()).setSimiType(1);
                notifyStatus(false,helper,item);
            }
        });
        IMImageLoadUtil.CommonImageCircleLoad(getContext(),item.getPhoto(),(ImageView) helper.getView(R.id.iv_head));
        helper.setText(R.id.tv_name,item.getNickname());
    }

    private void notifyStatus(boolean  refused,final BaseViewHolder helper, final PrivateUserBean item) {
        int flag=0;
        int flag2=0;
        for (PrivateUserBean bean:getData()){
            if(bean.getSimiType()==1){
                flag++;
            }

            if(bean.getSimiType()==2){
                flag2++;
            }
        }

        if(flag>0){
            tv_start.setEnabled(true);
        }else {
            tv_start.setEnabled(false);
        }

        if(flag2==getData().size()){//全部拒绝
            DialogUitl.showSimpleDialog(getContext(),"您确定要拒绝所有申请用户吗？\n" +
                    "拒绝后，本次私密互动将被取消~", new DialogUitl.SimpleCallback2() {
                @Override
                public void onCancelClick() {
                    item.setSimiType(0);
                }

                @Override
                public void onConfirmClick(Dialog dialog, String content) {
//                    SocketChatUtil.sendOneKeyRefusePrivateLiveMsg(((LiveAnchorActivity)getContext()).getSocketClient(),
//                            JSON.toJSONString(getData()));
                    ((LiveAnchorActivity)getContext()).closePrivate(2);
                    liveSiMiDialog.dismiss();
                }
            });

        }else {
            if(refused){ //如果是拒绝，当全部人没有拒绝时候改变按钮状态
                helper.getView(R.id.rl_contain).setVisibility(View.GONE);
                helper.getView(R.id.tv_accepted).setVisibility(View.GONE);
                helper.getView(R.id.tv_refused).setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * 申请状态0是未处理，1是已接收，2是已拒绝
     */
    private void handleState( BaseViewHolder helper,PrivateUserBean item) {
        switch (item.getSimiType()){
            case 0:
                helper.getView(R.id.rl_contain).setVisibility(View.VISIBLE);
                helper.getView(R.id.tv_refused).setVisibility(View.GONE);
                helper.getView(R.id.tv_accepted).setVisibility(View.GONE);
                break;
            case 1:
                helper.getView(R.id.rl_contain).setVisibility(View.GONE);
                helper.getView(R.id.tv_refused).setVisibility(View.GONE);
                helper.getView(R.id.tv_accepted).setVisibility(View.VISIBLE);
                break;
            case 2:
                helper.getView(R.id.rl_contain).setVisibility(View.GONE);
                helper.getView(R.id.tv_refused).setVisibility(View.VISIBLE);
                helper.getView(R.id.tv_accepted).setVisibility(View.GONE);
                break;
        }
    }

    public void setDatas(List<PrivateUserBean> userdatas) {
        this.data=userdatas;
        setList(userdatas);
    }
}