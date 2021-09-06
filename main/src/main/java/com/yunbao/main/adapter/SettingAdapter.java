package com.yunbao.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suke.widget.SwitchButton;
import com.yunbao.common.Constants;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.SettingBean;
import com.yunbao.common.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by cxf on 2018/9/30.
 */

public class SettingAdapter extends RecyclerView.Adapter {

    private static final int NORMAL = 0;
    private static final int VERSION = 1;
    private static final int LAST = 2;
    private static final int SWITCH = 3;
    private List<SettingBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<SettingBean> mOnItemClickListener;
    private String mVersionString;
    private String mCacheString;


    public SettingAdapter(Context context, List<SettingBean> list, String version, String cache) {
        mList = list;
        mVersionString = version;
        mCacheString = cache;
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    SettingBean bean = mList.get(position);
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean, position);
                    }
                }
            }
        };
    }

    public void setCacheString(String cacheString) {
        mCacheString = cacheString;
    }

    public void setOnItemClickListener(OnItemClickListener<SettingBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        SettingBean bean = mList.get(position);
        if(position==0){
            return SWITCH;
        }
        if (bean.getId() == Constants.SETTING_UPDATE_ID || bean.getId() == Constants.SETTING_CLEAR_CACHE) {
            return VERSION;
        } else if (bean.isLast()) {
            return LAST;
        } else {
            return NORMAL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VERSION) {
            return new Vh2(mInflater.inflate(R.layout.item_setting_1, parent, false));
        } else if (viewType == LAST) {
            return new Vh(mInflater.inflate(R.layout.item_setting_2, parent, false));
        } else if (viewType == SWITCH){
            return new Vh3(mInflater.inflate(R.layout.item_setting_3, parent, false));
        }else {
            return new Vh(mInflater.inflate(R.layout.item_setting, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position), position);
        } else if (vh instanceof Vh3) {
            setVh3Data((Vh3) vh);
        } else {
            ((Vh2) vh).setData(mList.get(position), position);
        }
    }

    private void setVh3Data(final Vh3 vh) {
        boolean booleanValue = SpUtil.getInstance().getBooleanValue(SpUtil.VOICESET);
        if(!booleanValue){
            vh.mSwitch.setChecked(true);
        }else{
            vh.mSwitch.setChecked(false);
        }

        vh.mSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if(isChecked){
                    SpUtil.getInstance().setBooleanValue(SpUtil.VOICESET,false);
                }else {
                    SpUtil.getInstance().setBooleanValue(SpUtil.VOICESET,true);
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        TextView mName;

        public Vh(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(SettingBean bean, int position) {
            itemView.setTag(position);
            mName.setText(bean.getName());
        }
    }

    class Vh2 extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mText;

        public Vh2(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);
            mText = (TextView) itemView.findViewById(R.id.text);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(SettingBean bean, int position) {
            itemView.setTag(position);
            mName.setText(bean.getName());
            if (bean.getId() == Constants.SETTING_CLEAR_CACHE) {
                mText.setText(mCacheString);
            } else {
                mText.setText(mVersionString);
            }
        }
    }
    class Vh3 extends RecyclerView.ViewHolder {
        TextView mName;
        SwitchButton mSwitch;

        public Vh3(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);
            mSwitch = (SwitchButton) itemView.findViewById(R.id.switch_button);
        }
    }
}
