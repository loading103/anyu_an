package com.yunbao.common.download;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.yunbao.common.CommonAppContext;
import com.yunbao.common.R;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.GetFileSize;
import com.yunbao.common.utils.IMImageLoadUtil;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2020/9/22.
 * Describe:本地文件
 */
public class TaskFileItemAdapter extends RecyclerView.Adapter<TaskItemViewHolder> {

    private  List<TasksManagerModel> mList;
    private OnItemClickListener<TasksManagerModel> mOnItemClickListener;

    public TaskFileItemAdapter() {

    }

    @Override
    public TaskItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskItemViewHolder holder = new TaskItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tasks_manager1, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(TaskItemViewHolder holder, final int position) {
        holder.update(mList.get(position).getId(), position);
        holder.taskNameTv.setText(mList.get(position).getName());

        if(!TextUtils.isEmpty(mList.get(position).getPath()) && new File(mList.get(position).getPath()).exists()){
            try {
                String fileSizes = GetFileSize.getFileSizes(new File(mList.get(position).getPath()));
                holder.taskStatusTv.setText(fileSizes);
            } catch (Exception e) {
                holder.taskStatusTv.setText("未知大小");
                e.printStackTrace();
            }
        }

        if(TextUtils.isEmpty(mList.get(position).getVip()) || Integer.parseInt(mList.get(position).getVip())==0){
            holder.ivVip.setVisibility(View.INVISIBLE);
        }else {
            holder.ivVip.setVisibility(View.VISIBLE);
        }
        IMImageLoadUtil.CommonImageLoad(CommonAppContext.sInstance,mList.get(position).getImg(),holder.ivContent);
        holder.taskActionBtn.setEnabled(true);

        holder.tvStatus.setVisibility(View.GONE);
        holder.ivStatus.setVisibility(View.GONE);
        holder.taskPb.setVisibility(View.INVISIBLE);

        if((!TextUtils.isEmpty(mList.get(position).getDownedit()))&&mList.get(position).getDownedit().equals("1")){
            holder.ivCheck.setVisibility(View.VISIBLE);
            if((!TextUtils.isEmpty(mList.get(position).getDownchecked()))&&mList.get(position).getDownchecked().equals("1")){
                holder.ivCheck.setImageResource(R.mipmap.icon_d_checked);
            }else {
                holder.ivCheck.setImageResource(R.mipmap.icon_d_check);
            }

        }else {
            holder.ivCheck.setVisibility(View.GONE);
        }

        holder.taskActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener!=null){
                    mOnItemClickListener.onItemClick(mList.get(position),position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addList(List<TasksManagerModel> list){
        this.mList=list;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<TasksManagerModel> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
