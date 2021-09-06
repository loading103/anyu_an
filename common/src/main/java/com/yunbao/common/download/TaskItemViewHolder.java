package com.yunbao.common.download;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SpanUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.R;
import com.yunbao.common.utils.IMRoundAngle;

/**
 * Created by Administrator on 2020/9/22.
 * Describe:
 */
public class TaskItemViewHolder extends RecyclerView.ViewHolder {
    public TaskItemViewHolder(View itemView) {
        super(itemView);
        assignViews();
    }

    private View findViewById(final int id) {
        return itemView.findViewById(id);
    }

    /**
     * viewHolder position
     */
    public int position;
    /**
     * download id
     */
    public int id;

    public void update(final int id, final int position) {
        this.id = id;
        this.position = position;
    }


    public void updateDownloaded(int taskId) {
        taskPb.setMax(1);
        taskPb.setProgress(1);

//        taskStatusTv.setText(R.string.tasks_manager_demo_status_completed);
//        tvStatus.setText(R.string.delete);
        TasksManagerModel model=null;
        try {
            model = TasksManager.getImpl().getById(taskId);
            if(model==null){
                model = TasksManager.getImpl().get(position);
            }
        }catch (Exception e){
            model = TasksManager.getImpl().get(position);
        }
        if(model==null){
            return;
        }

        taskNameTv.setText(setName(model.getName())+"（下载完成）");
    }

    public void updateNotDownloaded(final int status, final long sofar, final long total,int taskId) {
        if (sofar > 0 && total > 0) {
            final float percent = sofar / (float) total;
            taskPb.setMax(100);
            taskPb.setProgress((int) (percent * 100));
        } else {
            taskPb.setMax(1);
            taskPb.setProgress(0);
        }
        TasksManagerModel model=null;
        try {
            model = TasksManager.getImpl().getById(taskId);
            if(model==null){
                model = TasksManager.getImpl().get(position);
            }
        }catch (Exception e){
            model = TasksManager.getImpl().get(position);
        }
        if(model==null){
            return;
        }
        switch (status) {
            case FileDownloadStatus.error:
                taskStatusTv.setText(R.string.tasks_manager_demo_status_error);
                SpanUtils.with(taskNameTv)
                        .append(setName(model.getName()))
                        .append("（下载失败）")
                        .setForegroundColor(Color.parseColor("#DA0A0A"))
                        .create();
                tvStatus.setText("重新下载");
                ivStatus.setImageResource(R.mipmap.download_1);
                break;
            case FileDownloadStatus.paused:
                taskStatusTv.setText(R.string.tasks_manager_demo_status_paused);
                SpanUtils.with(taskNameTv)
                        .append(setName(model.getName()))
                        .append("（已暂停）")
                        .setForegroundColor(Color.parseColor("#0A7BDA"))
                        .create();
                tvStatus.setText("已暂停");
                ivStatus.setImageResource(R.mipmap.download_3);
                break;
            default:
                taskStatusTv.setText(R.string.tasks_manager_demo_status_not_downloaded);
                SpanUtils.with(taskNameTv)
                        .append(setName(model.getName()))
                        .append("（等待下载）")
                        .setForegroundColor(Color.parseColor("#9A9A9A"))
                        .create();
                tvStatus.setText("等待下载");
                ivStatus.setImageResource(R.mipmap.download_4);
                break;
        }

    }

    public   String  setName(String name){
        if(TextUtils.isEmpty(name)){
            return "";
        }
        String tvName=name.length()>8?name.substring(0,7)+"...":name;
        return  tvName;
    }

    public void updateDownloading(final int status, final long sofar, final long total,int taskId) {
        final float percent = sofar
                / (float) total;
        taskPb.setMax(100);
        taskPb.setProgress((int) (percent * 100));

        TasksManagerModel model=null;
        try {
            model = TasksManager.getImpl().getById(taskId);
            if(model==null){
                model = TasksManager.getImpl().get(position);
            }
        }catch (Exception e){
            model = TasksManager.getImpl().get(position);
        }
        if(model==null){
            return;
        }
        switch (status) {
            case FileDownloadStatus.pending:
                taskStatusTv.setText(R.string.tasks_manager_demo_status_pending);
                SpanUtils.with(taskNameTv)
                        .append(setName(model.getName()))
                        .append("（等待下载）")
                        .setForegroundColor(Color.parseColor("#9A9A9A"))
                        .create();
                tvStatus.setText("等待下载");
                ivStatus.setImageResource(R.mipmap.download_4);
                break;
            case FileDownloadStatus.started:
                taskStatusTv.setText(R.string.tasks_manager_demo_status_started);
                SpanUtils.with(taskNameTv)
                        .append(setName(model.getName()))
                        .append("（等待下载）")
                        .setForegroundColor(Color.parseColor("#9A9A9A"))
                        .create();
                tvStatus.setText("等待下载");
                ivStatus.setImageResource(R.mipmap.download_4);
                break;
            case FileDownloadStatus.connected:
                taskStatusTv.setText(R.string.tasks_manager_demo_status_connected);
                SpanUtils.with(taskNameTv)
                        .append(setName(model.getName()))
                        .append("（等待下载）")
                        .setForegroundColor(Color.parseColor("#9A9A9A"))
                        .create();
                tvStatus.setText("等待下载");
                ivStatus.setImageResource(R.mipmap.download_4);
                break;
            case FileDownloadStatus.progress:
//                taskStatusTv.setText(R.string.tasks_manager_demo_status_progress);
                SpanUtils.with(taskNameTv)
                        .append(setName(model.getName()))
                        .append("（下载中）")
                        .setForegroundColor(Color.parseColor("#06B827"))
                        .create();
                tvStatus.setText("下载中");
                ivStatus.setImageResource(R.mipmap.download_2);
                break;
            default:
                taskStatusTv.setText(CommonAppContext.sInstance.getString(
                        R.string.tasks_manager_demo_status_downloading, status));
                SpanUtils.with(taskNameTv)
                        .append(setName(model.getName()))
                        .append("（下载中）")
                        .setForegroundColor(Color.parseColor("#06B827"))
                        .create();
                tvStatus.setText("下载中");
                ivStatus.setImageResource(R.mipmap.download_2);
                break;
        }


    }
    public TextView taskNameTv;
    public TextView taskStatusTv;
    public ProgressBar taskPb;
    public LinearLayout taskActionBtn;
    public TextView tvStatus;
    public ImageView ivStatus;
    public IMRoundAngle ivContent;
    public FrameLayout flStatus;
    public ImageView ivCheck;
    public ImageView ivVip;
    private void assignViews() {
        taskNameTv = (TextView) findViewById(R.id.task_name_tv);
        taskStatusTv = (TextView) findViewById(R.id.task_status_tv);
        taskPb = (ProgressBar) findViewById(R.id.task_pb);
        taskActionBtn = (LinearLayout) findViewById(R.id.task_action_btn);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        ivStatus= (ImageView) findViewById(R.id.iv_status);
        ivContent = (IMRoundAngle) findViewById(R.id.iv_content);
        flStatus = (FrameLayout) findViewById(R.id.fl_status);
        ivCheck = (ImageView) findViewById(R.id.iv_check);
        ivVip= (ImageView) findViewById(R.id.iv_vip);
    }

}