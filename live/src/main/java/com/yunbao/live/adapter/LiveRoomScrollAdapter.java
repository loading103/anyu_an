package com.yunbao.live.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yunbao.common.CommonAppContext;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.live.views.LiveEndLayoutView;
import com.yunbao.live.R;
import com.yunbao.common.bean.LiveBean;

import java.util.List;

/**
 * Created by cxf on 2018/12/13.
 */

public class LiveRoomScrollAdapter extends RecyclerView.Adapter<LiveRoomScrollAdapter.Vh> {

    private Context mContext;
    private List<LiveBean> mList;
    private LayoutInflater mInflater;
    private int mCurPosition;
    private boolean mFirstLoad;
    private SparseArray<Vh> mMap;
    private LinearLayoutManager mLayoutManager;
    private ActionListener mActionListener;

    public LiveRoomScrollAdapter(Context context, List<LiveBean> list, int curPosition) {
        mContext=context;
        mList = list;
        mInflater = LayoutInflater.from(context);
        mCurPosition = curPosition;
        mFirstLoad = true;
        mMap = new SparseArray<>();
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_room, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position), position);
        if (mFirstLoad) {
            mFirstLoad = false;
            vh.hideCover();
            vh.onPageSelected(true);
        }
    }


    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        ViewGroup mContainer;
        ImageView mCover;
        LiveBean mLiveBean;
        LiveEndLayoutView ll_end;
        ImageView ivClose;
        public Vh(View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.container);
            mCover = itemView.findViewById(R.id.cover);
            ll_end = itemView.findViewById(R.id.ll_end);
            ivClose = itemView.findViewById(R.id.iv_close);
        }

        void setData(LiveBean bean, int position) {
            mLiveBean = bean;
            mMap.put(position, this);
            ImgLoader.displayBlur(CommonAppContext.sInstance,bean.getThumb(), mCover,R.mipmap.img_default_pic3);
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LiveAudienceActivity)mContext).onBackPressed();
                }
            });
        }

        void onPageOutWindow() {
            if (mCover != null && mCover.getVisibility() != View.VISIBLE) {
                mCover.setVisibility(View.VISIBLE);
                ivClose.setVisibility(View.VISIBLE);
            }
            if (mActionListener != null) {
                mActionListener.onPageOutWindow(mLiveBean.getUid());
            }
        }

        void onPageSelected(boolean first) {
            if (mActionListener != null) {
                mActionListener.onPageSelected(mLiveBean, mContainer,ll_end, first);
            }
        }

        void hideCover() {
            if (mCover != null && mCover.getVisibility() == View.VISIBLE) {
                mCover.setVisibility(View.INVISIBLE);
                ivClose.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void hideCover() {
        Vh vh = mMap.get(mCurPosition);
        if (vh != null) {
            vh.hideCover();
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull Vh vh) {
        vh.onPageOutWindow();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        mLayoutManager.setInitialPrefetchItemCount(4);
        recyclerView.scrollToPosition(mCurPosition);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int position = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (position >= 0 && mCurPosition != position) {
                    Vh vh = mMap.get(position);
                    if (vh != null) {
                        vh.onPageSelected(false);
                    }
                    mCurPosition = position;
                }
            }
        });
    }

    public interface ActionListener {
        void onPageSelected(LiveBean liveBean, ViewGroup container, LiveEndLayoutView container_end , boolean first);
 
        void onPageOutWindow(String liveUid);
    }


    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

}
