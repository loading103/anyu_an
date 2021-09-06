package com.yunbao.common.activity;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FileUtils;
import com.yunbao.common.R;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.download.TaskItemAdapter;
import com.yunbao.common.download.TasksManager;
import com.yunbao.common.download.TasksManagerModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wolf on 2020/09/23.
 * 下载视频
 */

public class DownloadActivity extends AbsActivity implements View.OnClickListener {

    private TextView tvBack;
    private FrameLayout flRoot;
    private TextView titleView;
    private ImageView btnBack;
    private TextView mTvFinish;
    private String mBack;
    private RecyclerView mRecycleView;
    private  TaskItemAdapter adapter;
    private LinearLayout llEdit;
    private TextView tvAll;
    private TextView tvDelete;

    public static  String[] BIG_FILE_URLS = {
            "http://132.232.122.151:8080/group1/app/video/2020/09/07/adddc6887b54fc10c70c5923321b7142.mp4",
            "http://132.232.122.151:8080/group1/app/video/2020/07/05/4854f6bd3a70326d8e66ea4351afc0fa.mp4",
            "http://132.232.122.151:8080/group1/app/video/2020/09/07/8b8caa2f9676922008a515eb813eaf68.mp4",
            "http://132.232.122.151:8080/group1/app/video/2020/07/05/df4b1a98240d50cf327bc391b8e62580.mp4",
            "http://132.232.122.151:8080/group1/app/video/2020/06/09/f637e22d42d4a94cb93a14dbeee26d6f.mp4",
    };
    private TextView tvSize;
    private boolean isEdit;//是否编辑状态
    private List<TasksManagerModel> list;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_download;
    }

    @Override
    protected void main() {
        initView();
        initRecycleView();
        TasksManager.getImpl().onCreate(new WeakReference<>(this));
        getStorage();

        list =new ArrayList<>();
        for (int i = 0; i < TasksManager.getImpl().getTaskCounts(); i++) {
            list.add(TasksManager.getImpl().get(i));
        }
        adapter.addList(list);
    }

    private void initView() {
        tvBack = findViewById(R.id.tv_back);
        flRoot = findViewById(R.id.fl_root);
        mTvFinish = findViewById(R.id.tv_finish);
        titleView = findViewById(R.id.titleView);
        btnBack = findViewById(R.id.btn_back);
        mTvFinish.setVisibility(View.VISIBLE);
        tvBack.setVisibility(View.GONE);
        mTvFinish.setTextColor(getResources().getColor(com.yunbao.common.R.color.white));
        titleView.setTextColor(getResources().getColor(com.yunbao.common.R.color.white));
        btnBack.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        flRoot.setBackground(getResources().getDrawable(R.drawable.main_title));
        mTvFinish.setText("编辑");
        titleView.setText("正在下载");
        mRecycleView= (RecyclerView) findViewById(R.id.recyclerView);
        tvSize= (TextView) findViewById(R.id.tv_size);
        llEdit= (LinearLayout) findViewById(R.id.ll_edit);
        tvAll= (TextView) findViewById(R.id.tv_all);
        tvDelete= (TextView) findViewById(R.id.tv_delete);
        mTvFinish.setOnClickListener(this);
        tvAll.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
    }

    private void initRecycleView() {
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        adapter=new TaskItemAdapter();
        mRecycleView.setAdapter(adapter);
    }

    public void postNotifyDataChanged() {
        if (adapter != null) {
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public void edit(boolean isEdit){
        this.isEdit=isEdit;

        if(list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setEdit(isEdit?"1":"0");
                list.get(i).setChecked("0");
            }
        }
        adapter.addList(list);

        if(isEdit){
            llEdit.setVisibility(View.VISIBLE);
            tvSize.setVisibility(View.GONE);
        }else {
            llEdit.setVisibility(View.GONE);
            tvSize.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()== R.id.tv_all) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setChecked("1");
            }
            adapter.addList(list);
        }else if (v.getId()== R.id.tv_delete) {
            for (int i = 0; i < list.size(); i++) {
                if((!TextUtils.isEmpty(list.get(i).getChecked())&&
                        list.get(i).getChecked().equals("1"))){
                    if(TasksManager.getImpl().deleteTask(list.get(i))!=null){
                        list.remove(list.get(i));
                    }
                }
            }
            adapter.addList(list);
        }else if(v.getId()== R.id.tv_finish){
            if(mTvFinish.getText().toString().equals("编辑")){
                mTvFinish.setText("取消");
                edit(true);
            }else {
                mTvFinish.setText("编辑");
                edit(false);
            }
        }
    }


    @Override
    public void onDestroy() {
        if(list!=null){
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setEdit("0");
            }
        }

        super.onDestroy();
//        TasksManager.getImpl().onDestroy();
//        FileDownloader.getImpl().pauseAll();

    }

    /**
     * 存储空间
     */
    private void getStorage() {
        StatFs state = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long blockSize = state.getBlockSize();
        long blockCount = state.getBlockCount();
        long availableCount = state.getAvailableBlocks();
        long totalSpace = blockCount * blockSize / 1024/1024/1024;       /* 存储空间大小 in KB */
        long freeSpace = availableCount * blockSize / 1024/1024/1024;    /* 可用的存储空间大小 in KB */
        tvSize.setText("总空间"+totalSpace+"G，可用空间"+freeSpace+"G");
    }
}
