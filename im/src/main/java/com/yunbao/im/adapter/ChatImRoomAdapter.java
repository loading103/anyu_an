package com.yunbao.im.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.greendao.entity.SocketMessageBean;
import com.yunbao.common.utils.IMImageLoadUtil;
import com.yunbao.common.utils.IMRoundAngleImageView;
import com.yunbao.im.R;
import com.yunbao.im.utils.IMTimeData;
import com.yunbao.im.utils.ImTextRender;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天的对话界面
 */
public class ChatImRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private Context context;
    private LayoutInflater mLayoutInflater;
    private List<SocketMessageBean> messagelist = new ArrayList<>();
    private UserBean customer;
    public static final int MSG_LEFT_TEXT = 0;//接收消息类型字文（文字）
    public static final int MSG_RIGHT_TEXT = 1;//发送消息类型

    public ChatImRoomAdapter(Context context, List<SocketMessageBean> messagelist, UserBean userBean) {
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.customer=userBean;
        this.messagelist=messagelist;
    }
    /**
     * 不同类型的布局(同一种消息分左右两种不同的布局)
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case MSG_LEFT_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_im_left_text, parent, false);
                holder = new TextLeftViewHolder(view);
                break;
            case MSG_RIGHT_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_im_right_text, parent, false);
                holder = new TextRightViewHolder(view);
                break;
        }
        return holder;
    }


    /**
     * 第1种类型（文字左边）
     */
    class TextLeftViewHolder extends  RecyclerView.ViewHolder {
        public IMRoundAngleImageView mIvHeadView;
        public TextView mTvChatTime;
        private TextView mContent;
        private LinearLayout mllText;
        public TextLeftViewHolder(View view) {
            super(view);
            mContent =  view.findViewById(R.id.im_tv_content);
            mllText =  view.findViewById(R.id.ll_text);
            mIvHeadView =  view.findViewById(R.id.im_iv_head);
            mTvChatTime =  view.findViewById(R.id.im_chat_time);
        }
    }
    /**
     * 第2种类型（文字右边）
     */
    class TextRightViewHolder extends  RecyclerView.ViewHolder{
        private TextView mContent;
        private LinearLayout mllText;
        public IMRoundAngleImageView mIvHeadView;
        public TextView mTvChatTime;
        public ImageView mIvFail;
        public TextRightViewHolder(View view) {
            super(view);
            mContent =  view.findViewById(R.id.im_tv_content);
            mllText =  view.findViewById(R.id.ll_text);
            mIvHeadView =  view.findViewById(R.id.im_iv_head);
            mTvChatTime =  view.findViewById(R.id.im_chat_time);
            mIvFail =  view.findViewById(R.id.im_iv_fail);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SocketMessageBean tbub = messagelist.get(position);
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case MSG_LEFT_TEXT:
                TextLeftLayout((TextLeftViewHolder) holder, tbub, position);
                break;
            case MSG_RIGHT_TEXT:
                TextRightLayout((TextRightViewHolder) holder, tbub, position);
                break;
        }
    }
    @Override
    public int getItemViewType(int position) {
        return Handlemessage(messagelist.get(position));
    }
    @Override
    public int getItemCount() {
        return messagelist.size();
    }
    /**
     * 处理第1种类型数据
     */
    private void TextLeftLayout(final TextLeftViewHolder holder, final SocketMessageBean tbub, final int position) {
        handleTimeContent(holder.mTvChatTime,Long.parseLong(tbub.getTime()),position);
        IMImageLoadUtil.CommonImageCircleLoad(context,customer.getAvatar(),holder.mIvHeadView);
        holder.mContent.setText( ImTextRender.renderChatMessage(tbub.getContent()));
    }

    /**
     * 处理第2种类型数据
     */
    private void TextRightLayout(final TextRightViewHolder holder, final SocketMessageBean tbub, final int position) {
        handleTimeContent(holder.mTvChatTime,Long.parseLong(tbub.getTime()),position);

        UserBean bean1 = CommonAppConfig.getInstance().getUserBean();
        if(bean1!=null){
            IMImageLoadUtil.CommonImageCircleLoad(context,bean1.getAvatar(),holder.mIvHeadView);
        }
        holder.mContent.setText(ImTextRender.renderChatMessage(tbub.getContent()));
        //发送状态的变化
        if(tbub.getSendState().equals("1")){
            holder.mIvFail.setVisibility(View.GONE);
        }else {
            holder.mIvFail.setVisibility(View.VISIBLE);
        }
        Log.e("处理第2种类型数据","position="+position+"tbub="+tbub.getContent());
    }

    private void handleTimeContent(TextView tvTime, long timestamp,int position) {
        if(position==0){
            tvTime.setVisibility(View.VISIBLE);
            tvTime.setText(IMTimeData.stampToTime( timestamp+"","MM月dd日 HH:mm"));
            return;
        }
        if(position<messagelist.size()) {
            long nexttime = Long.parseLong(messagelist.get(position-1).getTime());
            long time = Long.parseLong(messagelist.get(position).getTime());
            String times = IMTimeData.stampToTime(time + "", "MM月dd日 HH:mm");
            boolean b = (time - nexttime) > 3000*60;
            if(b){
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(times);
            }else {
                tvTime.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 处理数据源类型 通过msgtype和sendtype组合
     */
    private  int Handlemessage(SocketMessageBean bean) {
        if (bean.getSendType().equals("1")) {
            return MSG_RIGHT_TEXT;
        } else if (bean.getSendType().equals("2")) { {
                return MSG_LEFT_TEXT;
            }
        }
        return 0;
    }

}
