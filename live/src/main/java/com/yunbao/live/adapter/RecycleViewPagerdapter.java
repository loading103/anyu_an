package com.yunbao.live.adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.interfaces.DissmissDialogListener;
import com.yunbao.common.utils.IMImageLoadUtil;
import com.yunbao.live.R;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.live.event.DissShopDialogEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class RecycleViewPagerdapter extends RecyclerView.Adapter<RecycleViewPagerdapter.ViewHolder> {
    private Context mcontext;
    private  int  page =-1;
    private int  pageSize =-1;
    private  List<LiveReadyBean.GoodsBean>  datas =new ArrayList<>();
    private ViewHolder viewHolder;

    public RecycleViewPagerdapter(Context mcontext, int page, int pageSize, List<LiveReadyBean.GoodsBean> datas) {
        this.mcontext = mcontext;
        this.page = page;
        this.pageSize = pageSize;
        this.datas = datas;
    }
    @Override
    public int getItemCount() {
        if (datas.size() > (page + 1) * pageSize){
            return pageSize;
        }  else {
            return  datas.size() - page * pageSize;
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_shop, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final int pos=position + page * pageSize;
        String name="";
        if(datas.get(pos).getName().length()<=4){
            name=datas.get(pos).getName();
        }else {
            name=datas.get(pos).getName().substring(0,4)+"...";
        }
        IMImageLoadUtil.CommonImageCircleLoad(mcontext,datas.get(pos).getThumb(), holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果是底部弹出，又要隐藏
                if(datas.get(pos).getShow_type().equals("4")){
                    OpenUrlUtils.getInstance().setContext(mcontext)
                            .setType(Integer.parseInt((datas.get(pos).getShow_type())))
                            .setJump_type(datas.get(pos).getJump_type())
                            .setIs_king(datas.get(pos).getIs_king())
                            .setLoadTransparent(true)
                            .setTitle(datas.get(pos).getName())
                            .setSlide_show_type_button(datas.get(pos).getSlide_show_type_button())
                            .setListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    //显示之前的,false表示要显示消失的东西
                                    EventBus.getDefault().post(new DissShopDialogEvent());
                                }
                            })
                            .setOndissDialogListener(new DissmissDialogListener() {
                                @Override
                                public void onDissmissListener() {
                                    //显示之前的,false表示要显示消失的东西
                                    EventBus.getDefault().post(new DissShopDialogEvent());
                                }
                            })
                            .go(datas.get(pos).getJump_url());
                    holder.itemView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new DissShopDialogEvent(true));
                        }
                    },300);
                    return;
                }
                OpenUrlUtils.getInstance().setContext(mcontext)
                        .setType(Integer.parseInt((datas.get(pos).getShow_type())))
                        .setJump_type(datas.get(pos).getJump_type())
                        .setIs_king(datas.get(pos).getIs_king())
                        .setTitle(datas.get(pos).getName())
                        .setSlide_show_type_button(datas.get(pos).getSlide_show_type_button())
                        .go(datas.get(pos).getJump_url());
                //影藏底部和显示之前消失的
                EventBus.getDefault().post(new DissShopDialogEvent());
            }
        });

        if(position==0&&page==0&&datas.get(pos).isFlag()){
            holder.ivFirst.setVisibility(View.VISIBLE);
            holder.textView.setText("直播中");
            holder.textView.setTextColor(mcontext.getResources().getColor(R.color.color_E438E4));
        }else {
            holder.ivFirst.setVisibility(View.GONE);
            holder.textView.setText(name);
            holder.textView.setTextColor(mcontext.getResources().getColor(R.color.white));
        }
    }


    @Override
    public long getItemId(int position) {
        return position + page * pageSize;
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        GifImageView ivFirst;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView =  itemView.findViewById(R.id.entrance_image);
            textView =  itemView.findViewById(R.id.entrance_name);
            ivFirst =  itemView.findViewById(R.id.iv_first);
        }
    }
    public void setData(List<LiveReadyBean.GoodsBean> datas){
        this.datas=datas;
        notifyDataSetChanged();
    }
}