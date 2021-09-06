package com.yunbao.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yunbao.common.bean.VideoClassBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.main.R;

import java.util.List;

import jp.wasabeef.glide.transformations.GrayscaleTransformation;

/**
 * Created by cxf on 2018/9/25.
 */

public class MainHomeVideoClassAdapter extends RecyclerView.Adapter<MainHomeVideoClassAdapter.Vh> {

    private Context mContext;

    public List<VideoClassBean> getmList() {
        return mList;
    }

    public void setmList(List<VideoClassBean> mList) {
        this.mList = mList;
    }

    private List<VideoClassBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<VideoClassBean> mOnItemClickListener;
    private boolean mDialog;
    public  boolean  showred=false;

    public MainHomeVideoClassAdapter(Context context, List<VideoClassBean> list, boolean dialog) {
        mContext=context;
        mList = list;
        mInflater = LayoutInflater.from(context);
        mDialog = dialog;
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    VideoClassBean bean = mList.get(position);
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean, position);
                    }
                }
            }
        };
    }


    public boolean isShowred() {
        return showred;
    }

    public void setShowred(boolean showred) {
        this.showred = showred;
    }

    public void setOnItemClickListener(OnItemClickListener<VideoClassBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int res = mDialog ? R.layout.item_main_home_live_class : R.layout.item_main_home_live_class_22;
        return new Vh(mInflater.inflate(res, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView mImg;
        View mRed;
        TextView mName;
        FrameLayout flBg;

        public Vh(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.img);
            mRed =  itemView.findViewById(R.id.iv_red);
            mName = (TextView) itemView.findViewById(R.id.name);
            flBg = (FrameLayout) itemView.findViewById(R.id.fl_bg);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(VideoClassBean bean, int position) {
            itemView.setTag(position);
            mName.setText(bean.getName());
            if(!mDialog && position==0 && showred){
                mRed.setVisibility(View.VISIBLE);
            }else {
                mRed.setVisibility(View.GONE);
            }
            if(bean.isChecked()){
                flBg.setBackgroundResource(R.drawable.shape_item_choose);
                mName.setTextColor(mContext.getResources().getColor(R.color.white));
                if(position==0 ){
                    ImgLoader.display(mContext,R.mipmap.download, mImg);
                }else if(position==1){
                    ImgLoader.display(mContext,R.mipmap.icon_video_tj, mImg);
                }else {
                    ImgLoader.displayWithPlaceError(mContext,bean.getThumb(), mImg,R.mipmap.icon_app_bg,R.mipmap.icon_app_bg);
                }
            }else {
                flBg.setBackgroundResource(R.drawable.shape_item_unchoose);
                mName.setTextColor(mContext.getResources().getColor(R.color.color_AAAAAA));
                if(position==0 ){
                    Glide.with(mContext).load(R.mipmap.download)
                            .apply(RequestOptions.bitmapTransform(new GrayscaleTransformation()))
                            .into(mImg);
                }else if(position==1){
                    Glide.with(mContext).load(R.mipmap.icon_video_tj)
                            .apply(RequestOptions.bitmapTransform(new GrayscaleTransformation()))
                            .into(mImg);
                }else {
                    Glide.with(mContext).load(bean.getThumb())
                            .apply(RequestOptions.bitmapTransform(new GrayscaleTransformation()))
                            .placeholder(R.mipmap.icon_app_bg)
                            .error(R.mipmap.icon_app_bg)
                            .into(mImg);
                }
            }

        }
    }
}



