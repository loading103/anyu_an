package com.yunbao.main.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FileUtils;
import com.yunbao.common.bean.VideoBean;
import com.yunbao.common.download.TaskItemAdapter;
import com.yunbao.common.download.TasksManager;
import com.yunbao.common.download.TasksManagerModel;
import com.yunbao.common.event.DownModelBeanEvent;
import com.yunbao.common.event.DownSuccessVideoEvent;
import com.yunbao.common.event.MainShowDowningEvent;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.IMMemoryUtils;
import com.yunbao.common.views.AbsViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainDowningViewHolder extends AbsViewHolder implements View.OnClickListener {

    private TextView tvBack;
    private FrameLayout flRoot;
    private TextView titleView;
    private ImageView btnBack;
    public TextView mTvFinish;
    private RecyclerView mRecycleView;
    private TaskItemAdapter adapter;
    private LinearLayout llEdit;
    private TextView tvAll;
    private TextView tvDelete;
    private TextView tvSize;
    private RelativeLayout rl_nodata;
    private boolean isEdit;//是否编辑状态
    private List<TasksManagerModel> list;
    public MainDowningViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return com.yunbao.live.R.layout.activity_download;
    }

    @Override
    public void init() {
        EventBus.getDefault().register(this);
        initView();
        initRecycleView();
        TasksManager.getImpl().onCreate(new WeakReference(this));
        getStorage();
        getListData();
    }

    private void initView() {
        tvBack = (TextView)findViewById(com.yunbao.common.R.id.tv_back);
        flRoot =  (FrameLayout)findViewById(com.yunbao.common.R.id.fl_root);
        mTvFinish =  (TextView)findViewById(com.yunbao.common.R.id.tv_finish);
        titleView =  (TextView)findViewById(com.yunbao.common.R.id.titleView);
        btnBack =  (ImageView) findViewById(com.yunbao.common.R.id.btn_back);
        rl_nodata=  (RelativeLayout) findViewById(com.yunbao.common.R.id.rl_nodata);

        mTvFinish.setVisibility(View.VISIBLE);
        tvBack.setVisibility(View.GONE);
        mTvFinish.setTextColor(mContext.getResources().getColor(com.yunbao.common.R.color.white));
        titleView.setTextColor(mContext.getResources().getColor(com.yunbao.common.R.color.white));
        btnBack.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, com.yunbao.common.R.color.white)));
        flRoot.setBackground(mContext.getResources().getDrawable(com.yunbao.common.R.drawable.main_title));
        mTvFinish.setText("编辑");
        titleView.setText("正在下载");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new MainShowDowningEvent(false));
            }
        });
        mRecycleView= (RecyclerView) findViewById(com.yunbao.common.R.id.recyclerView);
        tvSize= (TextView) findViewById(com.yunbao.common.R.id.tv_size);
        llEdit= (LinearLayout) findViewById(com.yunbao.common.R.id.ll_edit);
        tvAll= (TextView) findViewById(com.yunbao.common.R.id.tv_all);
        tvDelete= (TextView) findViewById(com.yunbao.common.R.id.tv_delete);
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
        if(isEdit){
            mTvFinish.setText("取消");
        }else {
            mTvFinish.setText("编辑");
        }
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
        if(list==null || list.size()==0){
            rl_nodata.setVisibility(View.VISIBLE);
        }else {
            rl_nodata.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()== com.yunbao.common.R.id.tv_all) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setChecked("1");
            }
            adapter.addList(list);
            if(list==null || list.size()==0){
                rl_nodata.setVisibility(View.VISIBLE);
            }else {
                rl_nodata.setVisibility(View.GONE);
            }
        }else if (v.getId()== com.yunbao.common.R.id.tv_delete) {
            DialogUitl.showSimpleDialog(mContext, "是否删除选中的视频?", new DialogUitl.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    deleteVideo();
                }
            });

        }else if(v.getId()== com.yunbao.common.R.id.tv_finish){
            if(mTvFinish.getText().toString().equals("编辑")){
                edit(true);
            }else {
                edit(false);
            }
        }
    }

    private void deleteVideo() {
        List<TasksManagerModel> deletelist=new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if((!TextUtils.isEmpty(list.get(i).getChecked())&& list.get(i).getChecked().equals("1"))){
                if(TasksManager.getImpl().deleteTask(list.get(i))!=null){
                    deletelist.add(list.get(i));
                }
            }
        }
        list.removeAll(deletelist);
        adapter.addList(list);
        EventBus.getDefault().post(new DownSuccessVideoEvent(list.size()));
        if(list==null || list.size()==0){
            rl_nodata.setVisibility(View.VISIBLE);
            mTvFinish.setText("编辑");
            llEdit.setVisibility(View.GONE);
            tvSize.setVisibility(View.VISIBLE);

        }else {
            rl_nodata.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroy() {
        if(list!=null){
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setEdit("0");
            }
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    /**
     * 存储空间
     */
    private void getStorage() {
        tvSize.setText(String.format("总空间%s，可用空间%s", IMMemoryUtils.getAvailMemory(true),IMMemoryUtils.getAvailMemory(false)));

    }


    public void   setVisible(){
        getListData();
    }

    public void addOneDownload(VideoBean bean) {
        TasksManagerModel model=new TasksManagerModel();
        model.setUrl(bean.getHref());
        model.setImg(bean.getThumb());
        model.setName(bean.getTitle());
        model.setSize(bean.getSize());
        model.setVip(bean.getVip_limit()+"");
        model.setVideo_id(bean.getId());
        model.setFinish("0");
        TasksManager.getImpl().addTask(model);
        if(model==null || TextUtils.isEmpty(model.getPath())){
            return;
        }
        list.add(model);
        adapter.notifyDataSetChanged();
        if(titleView!=null && list!=null){
            titleView.setText("正在下载("+list.size()+")");
        }
    }

    /**
     * 获取数据，下载完成的去掉
     */
    private void getListData() {
        list =new ArrayList<>();
        //状态==-3是下载完成
        for (int j = 0; j < TasksManager.getImpl().getTaskCounts(); j++) {
            if( TasksManager.getImpl().getStatus(TasksManager.getImpl().get(j).getId(),TasksManager.getImpl().get(j).getPath())!=-3){
                list.add(TasksManager.getImpl().get(j));
            }
        }
        EventBus.getDefault().post(new DownSuccessVideoEvent(list.size()));
        adapter.addList(list);
        if(list==null || list.size()==0){
            rl_nodata.setVisibility(View.VISIBLE);
        }else {
            rl_nodata.setVisibility(View.GONE);
        }
        if(titleView!=null && list!=null){
            titleView.setText("正在下载("+list.size()+")");
        }
    }

    /**
     * 添加下载一个资源
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainJsEvent(final DownModelBeanEvent e) {
        Log.e("下载-------",e.getBean().getTitle());
        addOneDownload(e.getBean());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainJsEvent(final DownSuccessVideoEvent e) {
        if(titleView!=null && list!=null){
            titleView.setText("正在下载("+list.size()+")");
            if(list.size()==0){
                rl_nodata.setVisibility(View.VISIBLE);
            }else {
                rl_nodata.setVisibility(View.GONE);
            }
        }
    }
}
