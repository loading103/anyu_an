package com.yunbao.live.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ArrayUtils;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.live.R;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.live.bean.LiveUserGiftBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cxf on 2018/10/10.
 */

public class LiveUserAdapter extends RecyclerView.Adapter<LiveUserAdapter.Vh> {

    private Context mContext;
    private List<LiveUserGiftBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<UserBean> mOnItemClickListener;

    public LiveUserAdapter(Context context) {
        mContext=context;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if(mList.size()<=position){
                        return;
                    }
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<UserBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mWrap;
        ImageView mAvatar;
        ImageView mIcon;
        ImageView mGuardIcon;

        public Vh(View itemView) {
            super(itemView);
            mWrap = (ImageView) itemView.findViewById(R.id.wrap);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mGuardIcon = (ImageView) itemView.findViewById(R.id.guard_icon);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveUserGiftBean userBean, int position, Object payload) {
            itemView.setTag(position);
            if (payload == null) {
                ImgLoader.displayWithPlaceError(mContext,userBean.getAvatar(), mAvatar,R.mipmap.img_head_deafult,R.mipmap.img_head_deafult);
                LevelBean levelBean = CommonAppConfig.getInstance().getLevel(userBean.getLevel());
                if (levelBean != null) {
                    ImgLoader.display(mContext, levelBean.getThumbIcon(), mIcon);
                }
            }
            int guardType = userBean.getGuardType();
            if (guardType == Constants.GUARD_TYPE_NONE) {
                if (position == 0) {
                    if (userBean.hasContribution()) {
                        mWrap.setImageResource(R.mipmap.icon_live_user_list_1);
                        mWrap.setVisibility(View.VISIBLE);
                    }
                } else if (position == 1) {
                    if (userBean.hasContribution()) {
                        mWrap.setImageResource(R.mipmap.icon_live_user_list_2);
                        mWrap.setVisibility(View.VISIBLE);
                    }
                } else if (position == 2) {
                    if (userBean.hasContribution()) {
                        mWrap.setImageResource(R.mipmap.icon_live_user_list_3);
                        mWrap.setVisibility(View.VISIBLE);
                    }
                } else {
                    mWrap.setImageDrawable(null);
                    mWrap.setVisibility(View.INVISIBLE);
                }
            }
        }

    }

    public void refreshList(List<LiveUserGiftBean> list) {
        if (mList != null && list != null && list.size() > 0) {
            mList.clear();
            mList.addAll(list);
//            notifyDataSetChanged();
            Collections.sort(mList, new ComparatorValues());
            notifyDataSetChanged();
        }
    }

    private int findItemPosition(String uid) {
        if (!TextUtils.isEmpty(uid)) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                if (uid.equals(mList.get(i).getId())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void removeItem(String uid) {
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        int position = findItemPosition(uid);
        if (position >= 0) {
            mList.remove(position);
//            notifyItemRemoved(position);
//            notifyItemRangeChanged(position, mList.size(), Constants.PAYLOAD);

            Collections.sort(mList, new ComparatorValues());
            notifyDataSetChanged();
        }
    }

    public void insertItem(LiveUserGiftBean userBean) {
        if (userBean == null) {
            return;
        }
        int position = findItemPosition(userBean.getId());
        if (position >= 0) {
            return;
        }
        int size = mList.size();
        mList.add(userBean);
        Collections.sort(mList, new ComparatorValues());
//        notifyItemInserted(size);
        notifyDataSetChanged();
    }

    public void insertList(List<LiveUserGiftBean> list) {
        if (mList != null && list != null && list.size() > 0) {
            int position = mList.size();
            mList.addAll(list);
//            notifyItemRangeInserted(position, mList.size());
            Collections.sort(mList, new ComparatorValues());
            notifyDataSetChanged();
        }
    }

    /**
     * 守护信息发生变化
     */
    public void onGuardChanged(String uid, int guardType) {
        if (!TextUtils.isEmpty(uid)) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                LiveUserGiftBean bean = mList.get(i);
                if (uid.equals(bean.getId())) {
                    if (bean.getGuardType() != guardType) {
                        bean.setGuardType(guardType);
                        notifyItemChanged(i, Constants.PAYLOAD);
                    }
                    break;
                }
            }
        }
    }

    public void clear() {
        if (mList != null) {
            mList.clear();
        }
        notifyDataSetChanged();
    }

    public static final class ComparatorValues implements Comparator<LiveUserGiftBean>{

        @Override
        public int compare(LiveUserGiftBean object1, LiveUserGiftBean object2) {
            Double m1=0.0;
            Double m2=0.0;
            try{
                m1=Double.parseDouble(object1.getContribution()==null?"0.0":object1.getContribution());
            }catch (Exception e){
                m1=0.0;
            }
            try{
                m2=Double.parseDouble(object2.getContribution()==null?"0.0":object2.getContribution());
            }catch (Exception e){
                m2=0.0;
            }

            int result=0;
            if(m1>m2)
            {
                result=-1;
            }
            if(m1<m2)
            {

                result=1;
            }
            return result;
        }

    }
}
