package com.yunbao.common.download;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.R;
import com.yunbao.common.event.DownSuccessVideoEvent;
import com.yunbao.common.utils.GetFileSize;
import com.yunbao.common.utils.IMImageLoadUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2020/9/22.
 * Describe:
 */
public class TaskItemAdapter extends RecyclerView.Adapter<TaskItemViewHolder> {
    private List<TasksManagerModel> mList;
    public FileDownloadListener taskDownloadListener = new FileDownloadSampleListener() {

        private TaskItemViewHolder checkCurrentHolder(final BaseDownloadTask task) {
            final TaskItemViewHolder tag = (TaskItemViewHolder) task.getTag();
            if (tag==null || tag.id != task.getId()) {
                return null;
            }

            return tag;
        }

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.pending(task, soFarBytes, totalBytes);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }

            tag.updateDownloading(FileDownloadStatus.pending, soFarBytes, totalBytes,task.getId());
            tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_pending);
        }

        @Override
        protected void started(BaseDownloadTask task) {
            super.started(task);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }

            tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_started);
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            super.connected(task, etag, isContinue, soFarBytes, totalBytes);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }

            tag.updateDownloading(FileDownloadStatus.connected, soFarBytes, totalBytes,task.getId());
            tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_connected);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.progress(task, soFarBytes, totalBytes);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }

            String progress = (int)(soFarBytes * 100.0f / totalBytes) + "%";
            tag.taskStatusTv.setText("已下载："+progress);
            tag.updateDownloading(FileDownloadStatus.progress, soFarBytes, totalBytes,task.getId());

        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            super.error(task, e);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }
            tag.updateNotDownloaded(FileDownloadStatus.error, task.getLargeFileSoFarBytes(), task.getLargeFileTotalBytes(),task.getId());
            TasksManager.getImpl().removeTaskForViewHolder(task.getId());
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.paused(task, soFarBytes, totalBytes);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }
            tag.updateNotDownloaded(FileDownloadStatus.paused, soFarBytes, totalBytes,task.getId());
            tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_paused);
            TasksManager.getImpl().removeTaskForViewHolder(task.getId());
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            super.completed(task);
            final TaskItemViewHolder tag = checkCurrentHolder(task);
            if (tag == null) {
                return;
            }

            tag.updateDownloaded(task.getId());
            TasksManager.getImpl().removeTaskForViewHolder(task.getId());
            if(mList.size()==0){
                return;
            }
            try {
                mList.remove(tag.position);
                notifyDataSetChanged();
                EventBus.getDefault().post(new DownSuccessVideoEvent(mList.size()));
            }catch (Exception e){
                notifyDataSetChanged();
                EventBus.getDefault().post(new DownSuccessVideoEvent(mList.size()));
            }
        }
    };
    private View.OnClickListener taskActionOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() == null) {
                return;
            }

            TaskItemViewHolder holder = (TaskItemViewHolder) v.getTag();

            if((!TextUtils.isEmpty(mList.get(holder.position).getEdit()))&&mList.get(holder.position).getEdit().equals("1")){
                if(TextUtils.isEmpty(mList.get(holder.position).getChecked())){
                    mList.get(holder.position).setChecked("1");
                }else {
                    if(mList.get(holder.position).getChecked().equals("1")){
                        mList.get(holder.position).setChecked("0");
                    }else {
                        mList.get(holder.position).setChecked("1");
                    }
                }
                notifyDataSetChanged();
                return;
            }

            LinearLayout ll_root =((LinearLayout) v);
            TextView tvStatus=ll_root.findViewById(R.id.tv_status);

            int id = mList.get(holder.position).getId();

            CharSequence action = tvStatus.getText();
            if (action.equals("下载中")) {
                if( holder.taskStatusTv.getText().equals("状态: 加载中...") || holder.taskStatusTv.getText().equals("状态: 下载完成")){
                    final TasksManagerModel model = mList.get(holder.position);
                    final BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
                            .setPath(model.getPath())
                            .setCallbackProgressTimes(100)
                            .setListener(taskDownloadListener);

                    TasksManager.getImpl().addTaskForViewHolder(task);
                    TasksManager.getImpl().updateViewHolder(holder.id, holder);
                    task.start();
                }else {
                    // to pause
                    FileDownloader.getImpl().pause(holder.id);
                }

            } else if (action.equals("已暂停") || action.equals("等待下载")|| action.equals("重新下载")) {
                // to start
                final TasksManagerModel model = mList.get(holder.position);
                final BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
                        .setPath(model.getPath())
                        .setCallbackProgressTimes(100)
                        .setListener(taskDownloadListener);

                TasksManager.getImpl().addTaskForViewHolder(task);
                TasksManager.getImpl().updateViewHolder(holder.id, holder);
                task.start();
            } else if (action.equals(v.getResources().getString(R.string.delete))) {
                // to delete
                new File(mList.get(holder.position).getPath()).delete();
                holder.taskActionBtn.setEnabled(true);
                //todo
                holder.updateNotDownloaded(FileDownloadStatus.INVALID_STATUS, 0, 0,mList.get(holder.position).getId());
            }
        }
    };

    @Override
    public TaskItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskItemViewHolder holder = new TaskItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tasks_manager, parent, false));
        holder.taskActionBtn.setOnClickListener(taskActionOnClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(TaskItemViewHolder holder, int position) {
        final TasksManagerModel model = mList.get(position);

        holder.update(model.getId(), position);
        holder.taskActionBtn.setTag(holder);
        holder.taskNameTv.setText(model.getName());

        if(TextUtils.isEmpty(model.getVip()) || Integer.parseInt(model.getVip())==0){
            holder.ivVip.setVisibility(View.INVISIBLE);
        }else {
            holder.ivVip.setVisibility(View.VISIBLE);
        }

        IMImageLoadUtil.CommonImageLoad(CommonAppContext.sInstance,model.getImg(),holder.ivContent);
        TasksManager.getImpl().updateViewHolder(holder.id, holder);

        for (int i = 0; i < TasksManager.getImpl().getTaskCounts(); i++) {
            Log.e("---------", TasksManager.getImpl().get(i).getId()+"--id-"+ TasksManager.getImpl().get(i).getVideo_id()+TasksManager.getImpl().get(i).getName()+"   position="+i+"   statue="+TasksManager.getImpl().getStatus(TasksManager.getImpl().get(i).getId(),TasksManager.getImpl().get(i).getPath()));
        }
        holder.taskActionBtn.setEnabled(true);


        if (TasksManager.getImpl().isReady()) {
            final int status = TasksManager.getImpl().getStatus(model.getId(), model.getPath());
            if (status == FileDownloadStatus.pending || status == FileDownloadStatus.started || status == FileDownloadStatus.connected) {
                // start task, but file not created yet
                holder.updateDownloading(status, TasksManager.getImpl().getSoFar(model.getId()), TasksManager.getImpl().getTotal(model.getId()),model.getId());
            } else if (!new File(model.getPath()).exists() && !new File(FileDownloadUtils.getTempPath(model.getPath())).exists()) {
                // not exist file
//                holder.updateNotDownloaded1(status, 0, 0,holder,taskDownloadListener);
                holder.updateNotDownloaded(status, 0, 0,model.getId());
            } else if (TasksManager.getImpl().isDownloaded(status)) {
                // already downloaded and exist
                holder.updateDownloaded(model.getId());
            } else if (status == FileDownloadStatus.progress) {
                // downloading
                holder.updateDownloading(status, TasksManager.getImpl().getSoFar(model.getId()), TasksManager.getImpl().getTotal(model.getId()),model.getId());
            } else {
                // not start
                holder.updateNotDownloaded(status, TasksManager.getImpl().getSoFar(model.getId()), TasksManager.getImpl().getTotal(model.getId()),model.getId());
            }
        } else {
            holder.taskStatusTv.setText(R.string.tasks_manager_demo_status_loading);
            holder.taskActionBtn.setEnabled(false);
        }
        if((!TextUtils.isEmpty(mList.get(position).getEdit()))&&mList.get(position).getEdit().equals("1")){
            holder.ivCheck.setVisibility(View.VISIBLE);
            if((!TextUtils.isEmpty(mList.get(position).getChecked()))&&mList.get(position).getChecked().equals("1")){
                holder.ivCheck.setImageResource(R.mipmap.icon_d_checked);
            }else {
                holder.ivCheck.setImageResource(R.mipmap.icon_d_check);
            }

        }else {
            holder.ivCheck.setVisibility(View.GONE);
        }

        try {
            if(holder.taskStatusTv.getText().equals("状态: 未下载")|| holder.taskStatusTv.getText().equals("状态: 出错")){
                holder.taskActionBtn.performClick();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addList(List<TasksManagerModel> list) {
        this.mList=list;
        notifyDataSetChanged();
    }
}
