package com.yunbao.main.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FileUtils;
import com.yunbao.common.activity.IMVideoPlayerActivity;
import com.yunbao.common.bean.VideoBean;
import com.yunbao.common.event.DownModelBeanEvent;
import com.yunbao.common.event.DownSuccessVideoEvent;
import com.yunbao.common.event.MainShowDowningEvent;
import com.yunbao.common.event.ShowRedEvent;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.IMMemoryUtils;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.live.R;
import com.yunbao.common.download.TaskFileItemAdapter;
import com.yunbao.common.download.TasksManager;
import com.yunbao.common.download.TasksManagerModel;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.video.activity.VideoDetailActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class MainDownLoadViewHolder extends AbsViewHolder implements View.OnClickListener {

    private static final String TAG = "MainDownLoadViewHolder";
    private RecyclerView mRecycleView;
    private TaskFileItemAdapter adapter;
    private FrameLayout flGoto;
    private List<TasksManagerModel> mdatas = new ArrayList<>();
    private TextView tvNum;
    private boolean isEdit = false;//是否是编辑状态
    private LinearLayout llEdit;
    private TextView tvAll;
    private TextView tvDelete;
    private TextView tvSize;
    private TextView tvFinish;
    private View botView;
    private RelativeLayout rl_nodata;
    private int size;

    public MainDownLoadViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_down;
    }

    @Override
    public void init() {
        EventBus.getDefault().register(this);
        mRecycleView = (RecyclerView) findViewById(R.id.recyclerView);
        flGoto = (FrameLayout) findViewById(R.id.fl_goto);
        tvNum = (TextView) findViewById(R.id.tv_num);
        llEdit = (LinearLayout) findViewById(R.id.ll_edit);
        tvAll = (TextView) findViewById(R.id.tv_all);
        tvDelete = (TextView) findViewById(R.id.tv_delete);
        tvSize = (TextView) findViewById(R.id.tv_size);
        rl_nodata=  (RelativeLayout) findViewById(com.yunbao.common.R.id.rl_nodata);
        botView=  findViewById(R.id.view);
        setViewVisible();
        flGoto.setOnClickListener(this);
        tvAll.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        initRecycleView();
        getDownedFiles();
        tvSize.setText(String.format("总空间%s，可用空间%s", IMMemoryUtils.getAvailMemory(true), IMMemoryUtils.getAvailMemory(false)));
        //此处延迟刷新一次  不然有报错
        tvSize.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mdatas == null || mdatas.size() == 0) {
                    getDownedFiles();
                }
            }
        }, 1000);
    }

    /**
     * 获取本地文件下载完成的文件
     */
    private void getDownedFiles() {
        if(mdatas==null){
            mdatas=new ArrayList<>();
        }else {
            mdatas.clear();
        }
        for (int j = 0; j < TasksManager.getImpl().getTaskCounts(); j++) {
            if( TasksManager.getImpl().getStatus(TasksManager.getImpl().get(j).getId(),TasksManager.getImpl().get(j).getPath())==-3){
                mdatas.add(TasksManager.getImpl().get(j));
            }
        }
        adapter.addList(mdatas);
        setDownCount((TasksManager.getImpl().getTaskCounts() - mdatas.size()));
        if(mdatas==null || mdatas.size()==0){
            rl_nodata.setVisibility(View.VISIBLE);
        }else {
            rl_nodata.setVisibility(View.GONE);
        }
    }

    private void initRecycleView() {
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        adapter=new TaskFileItemAdapter();
        adapter.setOnItemClickListener(new OnItemClickListener<TasksManagerModel>() {
            @Override
            public void onItemClick(TasksManagerModel bean, int position) {
                TasksManagerModel b = mdatas.get(position);
                if(isEdit){
                    if(TextUtils.isEmpty(b.getDownchecked())){
                        b.setDownchecked("1");
                    }else {
                        if(b.getDownchecked().equals("1")){
                            b.setDownchecked("0");
                        }else {
                            b.setDownchecked("1");
                        }
                    }
                    adapter.addList(mdatas);
                    if(mdatas==null || mdatas.size()==0){
                        rl_nodata.setVisibility(View.VISIBLE);
                    }else {
                        rl_nodata.setVisibility(View.GONE);
                    }
                    setDownCount((TasksManager.getImpl().getTaskCounts()-mdatas.size()));
                }else {
                    if(mdatas==null ||mdatas.size()==0){
                        getDownedFiles();
                        return;
                    }
                    /**
                     * 进入播放界面
                     */
//                    Intent intent=new Intent(mContext, IMVideoPlayerActivity.class);
//                    intent.putExtra("path",mdatas.get(position).getPath());
//                    mContext.startActivity(intent);
                    /**
                     * 进入详情界面
                     */
                    TasksManagerModel taskBean = mdatas.get(position);
                    VideoBean  videoBean=new VideoBean();
                    videoBean.setSize(taskBean.getSize());
                    videoBean.setId(taskBean.getVideo_id());
                    videoBean.setTitle(taskBean.getName());
                    videoBean.setHref(taskBean.getUrl());
                    videoBean.setThumb(taskBean.getImg());
                    if(!TextUtils.isEmpty(taskBean.getVip())){
                        videoBean.setVip_limit(Integer.parseInt(taskBean.getVip()));
                    }
                    VideoDetailActivity.forward(mContext, videoBean);
                }
            }
        });
        mRecycleView.setAdapter(adapter);
    }

    public void edit(boolean isEdit, TextView tvFinish){
        this.isEdit=isEdit;
        if(tvFinish!=null){
            this.tvFinish=tvFinish;
        }
        if(mdatas!=null&&mdatas.size()>0){
            for (int i = 0; i < mdatas.size(); i++) {
                mdatas.get(i).setDownedit(isEdit?"1":"0");
                mdatas.get(i).setDownchecked("0");
            }
        }
        adapter.addList(mdatas);
        if(mdatas==null || mdatas.size()==0){
            rl_nodata.setVisibility(View.VISIBLE);
        }else {
            rl_nodata.setVisibility(View.GONE);
        }
        if(isEdit){
            llEdit.setVisibility(View.VISIBLE);
            tvSize.setVisibility(View.GONE);
        }else {
            llEdit.setVisibility(View.GONE);
            tvSize.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if (id== R.id.fl_goto) {
            if(tvFinish!=null){
                tvFinish.setText("编辑");
                edit(false, null);
            }
            EventBus.getDefault().post(new MainShowDowningEvent(true));

        }if (id== R.id.tv_all) {
            for (int i = 0; i < mdatas.size(); i++) {
                mdatas.get(i).setDownchecked("1");
            }
            adapter.addList(mdatas);
            if(mdatas==null || mdatas.size()==0){
                rl_nodata.setVisibility(View.VISIBLE);
            }else {
                rl_nodata.setVisibility(View.GONE);
            }
        }if (id== R.id.tv_delete) {
            DialogUitl.showSimpleDialog(mContext, "是否删除选中的视频?", new DialogUitl.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    deleteVideo();
                }
            });

        }
    }

    /**
     * 删除视频
     */
    private void deleteVideo() {
        List<TasksManagerModel> deletelist=new ArrayList<>();
        for (int i = 0; i < mdatas.size(); i++) {
            if((!TextUtils.isEmpty(mdatas.get(i).getDownchecked())&& mdatas.get(i).getDownchecked().equals("1"))){
                if(TasksManager.getImpl().deleteTask(mdatas.get(i))!=null){
                    deletelist.add(mdatas.get(i));
                    if(new File(mdatas.get(i).getPath()).exists()){
                        new File(mdatas.get(i).getPath()).delete();
                    }
                }
            }
        }
        mdatas.removeAll(deletelist);
        adapter.addList(mdatas);
        if(mdatas==null || mdatas.size()==0){
            rl_nodata.setVisibility(View.VISIBLE);
        }else {
            rl_nodata.setVisibility(View.GONE);
        }
        tvFinish.setText("编辑");
        edit(false, null);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    /**
     * 选定此页面
     */
    public void refresh() {
        setViewVisible();
    }

    /**
     * 下载成功通知这里刷新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainJsEvent(final DownSuccessVideoEvent e) {
        if ( e != null) {
            setDownCount(e.getSize());
            getDownedFiles();
        }
    }

    /**
     * 添加下载一个资源
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainJsEvent(final DownModelBeanEvent e) {
        tvNum.postDelayed(new Runnable() {
            @Override
            public void run() {
                setDownCount((TasksManager.getImpl().getTaskCounts()-mdatas.size()));
            }
        },1000);

    }

    /**
     * 获取下载个数，更新内存大小
     */
    public void  setDownCount(int number){
        if(tvNum==null){
            return;
        }
        tvNum.setText(number+"");
        if(number==0){
            flGoto.setVisibility(View.GONE);
            EventBus.getDefault().post(new ShowRedEvent(false));
        }else {
            flGoto.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new ShowRedEvent(true));
        }
        tvSize.setText(String.format("总空间%s，可用空间%s", IMMemoryUtils.getAvailMemory(true),IMMemoryUtils.getAvailMemory(false)));
    }

    /**
     * 主界面底部tabbar不存在的时候去掉底部白条
     */
    public  void  setViewVisible(){
        if(botView==null){
            return;
        }
        if(((MainActivity)mContext).getBottomVisible()){
           botView.setVisibility(View.VISIBLE);
        }else {
            botView.setVisibility(View.GONE);
        }
    }
}
