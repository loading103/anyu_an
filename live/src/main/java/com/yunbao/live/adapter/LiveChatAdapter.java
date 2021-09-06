package com.yunbao.live.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.text.style.TextAppearanceSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.TextLinks;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.L;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.live.bean.LiveChatBean;
import com.yunbao.live.utils.LiveTextRender;
import com.yunbao.live.utils.TextViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/10.
 */

public class LiveChatAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<LiveChatBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<LiveChatBean> mOnItemClickListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    public LiveChatAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    LiveChatBean bean = (LiveChatBean) tag;
                    if (bean.getType() != LiveChatBean.SYSTEM && mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean, bean.getType() == LiveChatBean.SYSTEM_BUTTON ? 1 : 0);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<LiveChatBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == LiveChatBean.RED_PACK) {
            return new RedPackVh(mInflater.inflate(R.layout.item_live_chat_red_pack, parent, false));
        } else if (viewType == LiveChatBean.SYSTEM_BUTTON) {
            return new VhBt(mInflater.inflate(R.layout.item_live_chat, parent, false));
        } else if (viewType == LiveChatBean.SYSTEM_GIFT) {
            return new VhGift(mInflater.inflate(R.layout.item_live_chat, parent, false));
        }else if (viewType == LiveChatBean.SYSTEM_DANMU) {
            return new VhDanmu(mInflater.inflate(R.layout.item_live_chat, parent, false));
        } else {
            return new Vh(mInflater.inflate(R.layout.item_live_chat, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position));
        } else if (vh instanceof RedPackVh) {
            ((RedPackVh) vh).setData(mList.get(position));
        } else if (vh instanceof VhBt) {
            ((VhBt) vh).setData(mList.get(position));
        } else if (vh instanceof VhGift) {
            ((VhGift) vh).setData(mList.get(position));
        }else if (vh instanceof VhDanmu) {
            ((VhDanmu) vh).setData(mList.get(position));
        }

        //需要把item设置不透明，否则可能会因为复用导致item刚显示就是看不见的
        vh.itemView.setAlpha(1);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    }

    class RedPackVh extends RecyclerView.ViewHolder {

        TextView mTextView;

        public RedPackVh(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }

        void setData(LiveChatBean bean) {
            mTextView.setText(bean.getContent());
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mTextView;
        ImageView ivContent;
        FrameLayout flRoot;

        public Vh(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_content);
            ivContent = (ImageView) itemView.findViewById(R.id.iv_content);
            flRoot = (FrameLayout) itemView.findViewById(R.id.fl_root);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveChatBean bean) {
            itemView.setTag(bean);
            if(mContext instanceof LiveAudienceActivity){
                mTextView.setTextSize(15);
            }else {
                mTextView.setTextSize(17);
            }
            if (bean.getType() == LiveChatBean.SYSTEM) {
                if (!TextUtils.isEmpty(bean.getFollow())) {
                    mTextView.setTextColor(0xffffdd00);
                    mTextView.setText(bean.getContent());
                    flRoot.setBackground(mContext.getDrawable(R.drawable.bg_live_chat_item_follow));
                    Drawable drawable = mContext.getResources().getDrawable(R.mipmap.live_chat_star);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mTextView.setCompoundDrawablePadding(SizeUtils.dp2px(5));
                    mTextView.setCompoundDrawables(null, null, drawable, null);
//                    mTextView.setPadding(0,SizeUtils.dp2px(2),0,0);
                    ivContent.setVisibility(View.GONE);
                    mTextView.setGravity(Gravity.CENTER_VERTICAL);
                } else {
                    flRoot.setBackground(mContext.getDrawable(R.drawable.bg_live_chat_item));
//                    TextViewUtils.addSystemIcoTextView(mContext,mTextView,"·  "+bean.getContent());
                    if (bean.getType_head() != null && bean.getType_head().equals("1")) {
                        ivContent.setVisibility(View.VISIBLE);
                        mTextView.setTextColor(0xffffdd00);
                        mTextView.setText("·       " + bean.getContent());
                        mTextView.setCompoundDrawablePadding(0);
                        mTextView.setCompoundDrawables(null, null, null, null);

                        mTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                setMargins(ivContent, 0, SizeUtils.dp2px(2), 0, 0);
                            }
                        });
                    } else {
                        mTextView.setTextColor(0xffffdd00);
                        ivContent.setVisibility(View.GONE);
                        mTextView.setText(bean.getContent());
                        mTextView.setCompoundDrawablePadding(0);
                        mTextView.setCompoundDrawables(null, null, null, null);
                    }
                }
            } else if(bean.getLivePhone()!=null&&bean.getLivePhone().equals("1")){//新用户
                mTextView.setTextColor(0xffffdd00);
                flRoot.setBackground(mContext.getDrawable(R.drawable.bg_live_chat_item));
                ivContent.setVisibility(View.GONE);
                SpanUtils.with(mTextView)
                        .appendImage(R.mipmap.icon_chat_new_user, SpanUtils.ALIGN_CENTER)
                        .append(bean.getUserNiceName()+" 接受邀请进入直播间，请主动对待！")
                        .create();
            }else {
                if (bean.getType() == LiveChatBean.ENTER_ROOM || bean.getType() == LiveChatBean.LIGHT) {
                    mTextView.setTextColor(0xffc8c8c8);
                } else {
                    mTextView.setTextColor(0xffffffff);
                }
                LiveTextRender.render(mContext, mTextView, bean);
                ivContent.setVisibility(View.GONE);
                mTextView.setCompoundDrawablePadding(0);
                mTextView.setCompoundDrawables(null, null, null, null);
            }
        }
    }

    class VhBt extends RecyclerView.ViewHolder {

        TextView tvContent;
        ImageView ivContent;
        FrameLayout flRoot;

        public VhBt(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            ivContent = (ImageView) itemView.findViewById(R.id.iv_content);
            flRoot = (FrameLayout) itemView.findViewById(R.id.fl_root);
            tvContent.setOnClickListener(mOnClickListener);
        }


        void setData(LiveChatBean bean) {
            tvContent.setTag(bean);
            tvContent.setTextColor(0xffffdd00);
            if(mContext instanceof LiveAudienceActivity){
                tvContent.setTextSize(15);
            }else {
                tvContent.setTextSize(17);
            }

//            tvContent.setText(bean.getContent());
            if (bean.getType_head().equals("1")) {
                TextViewUtils.addTagToTextView(mContext, tvContent, "·       " + bean.getContent(), bean.getBt_title());
                flRoot.setBackground(mContext.getDrawable(R.drawable.bg_live_chat_item_buy));
                ivContent.setVisibility(View.VISIBLE);

                tvContent.post(new Runnable() {
                    @Override
                    public void run() {
                        if (tvContent.getLineCount() > 1) {
                            setMargins(ivContent, 0, SizeUtils.dp2px(2), 0, 0);
                        } else {
                            setMargins(ivContent, 0, SizeUtils.dp2px(5), 0, 0);
                        }
                    }
                });

            } else {
                TextViewUtils.addTagToTextView(mContext, tvContent, bean.getContent(), bean.getBt_title());
                flRoot.setBackground(mContext.getDrawable(R.drawable.bg_live_chat_item_buy));
                ivContent.setVisibility(View.GONE);
            }
        }
    }

    class VhGift extends RecyclerView.ViewHolder {

        TextView mTextView;
        ImageView ivContent;
        FrameLayout flRoot;

        public VhGift(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_content);
            ivContent = (ImageView) itemView.findViewById(R.id.iv_content);
            flRoot = (FrameLayout) itemView.findViewById(R.id.fl_root);
//            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveChatBean bean) {
            itemView.setTag(bean);
            mTextView.setTextColor(0xffffdd00);
            if(mContext instanceof LiveAudienceActivity){
                mTextView.setTextSize(15);
            }else {
                mTextView.setTextSize(17);
            }

            final String[] sp = bean.getContent().split("送了");

            Glide.with(mContext).asBitmap().load(bean.getGifticon_mini()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @androidx.annotation.Nullable Transition<? super Bitmap> transition) {
                    if (resource != null) {
                        SpanUtils.with(mTextView)
                                .append(sp[0])
                                .append("送了"+sp[1].split(",")[0])
                                .setForegroundColor(mContext.getResources().getColor(R.color.color_8031F2))
                                .appendImage(resource, SpanUtils.ALIGN_CENTER)
                                .append(sp[1].split(",")[1])
                                .setForegroundColor(mContext.getResources().getColor(R.color.color_8031F2))
                                .create();
                    }else {
                        SpanUtils.with(mTextView)
                                .append(sp[0])
                                .append("送了"+sp[1].split(",")[0])
                                .setForegroundColor(mContext.getResources().getColor(R.color.color_8031F2))
                                .appendImage(R.mipmap.icon_live_gift, SpanUtils.ALIGN_CENTER)
                                .append(sp[1].split(",")[1])
                                .setForegroundColor(mContext.getResources().getColor(R.color.color_8031F2))
                                .create();
                    }
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    SpanUtils.with(mTextView)
                            .append(sp[0])
                            .append("送了"+sp[1].split(",")[0])
                            .setForegroundColor(mContext.getResources().getColor(R.color.color_8031F2))
                            .appendImage(R.mipmap.icon_live_gift, SpanUtils.ALIGN_CENTER)
                            .append(sp[1].split(",")[1])
                            .setForegroundColor(mContext.getResources().getColor(R.color.color_8031F2))
                            .create();
                }
            });

            ivContent.setVisibility(View.GONE);
        }
    }

    class VhDanmu extends RecyclerView.ViewHolder {

        TextView mTextView;
        ImageView ivContent;
        FrameLayout flRoot;

        public VhDanmu(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_content);
            ivContent = (ImageView) itemView.findViewById(R.id.iv_content);
            flRoot = (FrameLayout) itemView.findViewById(R.id.fl_root);
//            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveChatBean bean) {
            itemView.setTag(bean);
            ivContent.setVisibility(View.GONE);
            if(mContext instanceof LiveAudienceActivity){
                mTextView.setTextSize(15);
            }else {
                mTextView.setTextSize(17);
            }

//            flRoot.setBackground(mContext.getDrawable(R.drawable.bg_live_chat_item_danmu));
            flRoot.setBackground(mContext.getDrawable(R.drawable.bg_live_chat_item));
            mTextView.setIncludeFontPadding(false);
            SpanUtils.with(mTextView)
                    .appendImage(R.mipmap.icon_chat_danmu, SpanUtils.ALIGN_CENTER)
                    .append(" ")
                    .append(bean.getUserNiceName())
                    .setHorizontalAlign(Layout.Alignment.ALIGN_CENTER)
                    .setForegroundColor(mContext.getResources().getColor(R.color.color_D435F0))
                    .append(" ")
                    .append(bean.getContent())
                    .setHorizontalAlign(Layout.Alignment.ALIGN_CENTER)
                    .setForegroundColor(mContext.getResources().getColor(R.color.color_ffdd00))
                    .create();
        }
    }

    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public void insertItem(LiveChatBean bean) {
        if (bean == null) {
            return;
        }
        int size = mList.size();
        mList.add(bean);
        notifyItemInserted(size);
        int lastItemPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
        if (lastItemPosition != size - 1) {
            mRecyclerView.smoothScrollToPosition(size);
        } else {
            mRecyclerView.scrollToPosition(size);
        }
    }

    public void insertEnterRoomItem(LiveChatBean bean) {
        if (bean == null || mList==null) {
            return;
        }
        int size = mList.size();
        if(size>0   &&  mList.get(size-1).getContent().contains("进入了直播间") && TextUtils.isEmpty(mList.get(size-1).getLivePhone())){
            mList.set(size-1,bean);
            notifyItemChanged(size-1);
            mRecyclerView.scrollToPosition(size);
            return;
        }

        mList.add(bean);
        notifyItemInserted(size);
        int lastItemPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
        if (lastItemPosition != size - 1) {
            mRecyclerView.smoothScrollToPosition(size);
        } else {
            mRecyclerView.scrollToPosition(size);
        }
    }

    public void scrollToBottom() {
        if (mList.size() > 0) {
            mRecyclerView.smoothScrollToPosition(mList.size() - 1);
        }
    }

    public void clear() {
        if (mList != null) {
            mList.clear();

        }
        notifyDataSetChanged();
    }
}
