package com.yunbao.main.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.main.R;

/**
 * Created by cxf on 2018/9/26.
 * 直播 直播
 */

public class MainLiveLiveAdapter extends RefreshAdapter<LiveBean> {
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private View.OnClickListener mOnClickListener;

    public MainLiveLiveAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        if(mList.size()<position+1){
                            return;
                        }
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
       if (position % 2 == 0) {
            return LEFT;
        }
        return RIGHT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == LEFT) {
            return new Vh(mInflater.inflate(R.layout.item_main_home_live_left2, parent, false));
        }
        return new Vh(mInflater.inflate(R.layout.item_main_home_live_right2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mCover;
        TextView mName;
        TextView mTitle;
        TextView mNum;
        TextView mTvGoods;
        TextView tvPlayback;
        ImageView ivFirst;
        LinearLayout llRight;

        public Vh(View itemView) {
            super(itemView);
            mCover = (ImageView) itemView.findViewById(R.id.cover);
            mName = (TextView) itemView.findViewById(R.id.name);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mNum = (TextView) itemView.findViewById(R.id.num);
            mTvGoods = (TextView) itemView.findViewById(R.id.tv_goods);
            tvPlayback = (TextView) itemView.findViewById(R.id.tv_playback);
            ivFirst = (ImageView) itemView.findViewById(R.id.iv_first);
            llRight = (LinearLayout) itemView.findViewById(R.id.ll_right);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveBean bean, int position) {
            itemView.setTag(position);
            ImgLoader.displayWithPlaceError(mContext,bean.getThumb()+"?width=400", mCover,R.mipmap.img_default_pic2,
                    R.mipmap.img_default_pic2);
            mName.setText(bean.getUserNiceName());
            mTitle.setText(TextUtils.isEmpty(bean.getTitle())? WordUtil.getString(R.string.live_normal):bean.getTitle());
            mNum.setText(bean.getNums());
            mTvGoods.setText(bean.getGoods_name());
            if(TextUtils.isEmpty(bean.getGoods_name())){
                mTvGoods.setVisibility(View.INVISIBLE);
            }else {
                mTvGoods.setVisibility(View.VISIBLE);
            }

            if(bean.getIsvideo().equals("1")){
                ivFirst.setVisibility(View.GONE);
                tvPlayback.setVisibility(View.GONE);
                llRight.setVisibility(View.GONE);
//                if(TextUtils.isEmpty(bean.getLive_tag())){
//                    tvPlayback.setVisibility(View.GONE);
//                    llRight.setVisibility(View.GONE);
//                }else {
//                    tvPlayback.setVisibility(View.VISIBLE);
//                    llRight.setVisibility(View.VISIBLE);
//                    tvPlayback.setText(bean.getLive_tag());
//                }
            }else {
                ivFirst.setVisibility(View.VISIBLE);
                tvPlayback.setVisibility(View.VISIBLE);
                llRight.setVisibility(View.VISIBLE);
                if(TextUtils.isEmpty(bean.getLive_tag())){
                    if(bean.getIsvideo().equals("2")){
                        ivFirst.setVisibility(View.GONE);
                        tvPlayback.setVisibility(View.GONE);
                        llRight.setVisibility(View.GONE);
                    }else {
                        tvPlayback.setText("直播中");
                        llRight.setBackgroundResource(R.mipmap.icon_live_item_right);
                    }
                }else {
                    tvPlayback.setText(bean.getLive_tag());
                    if((!TextUtils.isEmpty(bean.getLive_tag()))&&bean.getLive_tag().length()==4){
                        llRight.setBackgroundResource(R.mipmap.icon_live_item_right4);
                    }else if((!TextUtils.isEmpty(bean.getLive_tag()))&&bean.getLive_tag().length()==5){
                        llRight.setBackgroundResource(R.mipmap.icon_live_item_right5);
                    }else {
                        llRight.setBackgroundResource(R.mipmap.icon_live_item_right);
                    }
                }
            }
        }
    }

}
