package com.yunbao.live.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.live.R;

import java.util.List;

/**
 * Created by Wolf on 2020/04/25.
 * Describe:更多
 */
public class LiveListAdapter extends BaseQuickAdapter<LiveClassBean, BaseViewHolder> {
    public List<LiveClassBean> list;
    public int pageSize=3;//配置显示个数
    public LiveListAdapter() {
        super(R.layout.item_live_line);
    }
    public LiveListAdapter(List<LiveClassBean> list) {
        super(R.layout.item_live_line,list);
        this.list=list;
    }
    @Override
    public int getItemCount() {
        if(list.size()%pageSize==0){
            return  list.size()/pageSize;
        }else {
            return  list.size()/pageSize+1;
        }
    }
    @Override
    protected void convert(BaseViewHolder helper, LiveClassBean item) {
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        LinearLayout view = holder.findView(R.id.lin_root);

        if(list.size()<pageSize){
            for (int i =  0; i < list.size() ; i++) {
                addView(view, i);
            }
        }else if(list.size()-position*pageSize>0&&list.size()-position*pageSize<pageSize && list.size()>pageSize){
            for (int i = list.size()-pageSize; i < list.size(); i++) {
                addView(view, i);
            }
        }else {
            for (int i =  position*pageSize; i < position*pageSize+pageSize ; i++) {
                addView(view, i);
            }
        }

    }

    private void addView(LinearLayout view, int i) {
        View view1= LayoutInflater.from(getContext()).inflate(R.layout.item_live_list,null);
        TextView tv_title = view1.findViewById(R.id.tv_title);
        ImageView iv_content = view1.findViewById(R.id.iv_content);
        LinearLayout ll_item = view1.findViewById(R.id.ll_item);

        tv_title.setText(list.get(i).getName());
        ImgLoader.displayCircle(getContext(),list.get(i).getThumb(),iv_content);
        final int finalI = i;

        ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClickListener(list.get(finalI));
                }
            }
        });
        view.addView(view1);
    }

    public interface  onItemClick{
        void onItemClickListener(LiveClassBean bean);
    }
    private  onItemClick onItemClickListener;

    public void setItemClickListener(onItemClick onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}