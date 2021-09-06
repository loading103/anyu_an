package com.yunbao.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.main.R;

import java.util.List;

import static com.yunbao.common.utils.ClickUtil.canClick;
import static com.yunbao.common.utils.ClickUtil.isFastClick;

/**
 * Created by cxf on 2018/10/19.
 * 直播分享
 */

public class MineTopAdapter extends RecyclerView.Adapter<MineTopAdapter.Vh> {

    private List<LiveClassBean> mList;
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;
    private Context context;

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public MineTopAdapter(Context context, List<LiveClassBean> mList) {
        this.context = context;
        this.mList = mList;
        mInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_mine_top, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Vh vh, final int position) {

        final LiveClassBean data = mList.get(position);
        if(data.isIs_null()){
            vh.itemView.setVisibility(View.GONE);
        }else {
            vh.itemView.setVisibility(View.VISIBLE);
            vh.mName.setText(data.getName());
            ImgLoader.displayWithPlaceError(context,data.getThumb(), vh.mIcon,R.mipmap.icon_app_bg,
                    R.mipmap.icon_app_bg);
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isFastClick()){
                        return;
                    }
                    OpenUrlUtils.getInstance().setContext(context)
                            .setType(data.getSlide_show_type())
                            .setSlideTarget(data.getSlide_target())
                            .setTitle(data.getName())
                            .setJump_type(data.getJump_type())
                            .setIs_king(data.getIs_king())
                            .setSlide_show_type_button(data.getSlide_show_type_button())
                            .go(data.getSlide_url());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mIcon;
        TextView mName;
        View itemView;

        public Vh(View itemView) {
            super(itemView);
            this.itemView=itemView;
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mName = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
