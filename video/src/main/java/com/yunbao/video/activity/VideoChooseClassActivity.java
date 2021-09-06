package com.yunbao.video.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.bean.VideoClassBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.video.R;
import com.yunbao.video.adapter.VideoTypeAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/10/7.
 * 选择视频分类
 */

public class VideoChooseClassActivity extends AbsActivity {

    private RecyclerView mRecyclerView;
    private TextView tvFinish;
    private List<VideoClassBean> list;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_choose_class;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.video_class_choose));
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        findViewById(R.id.tv_finish).setVisibility(View.GONE);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        VideoTypeAdapter adapter=new VideoTypeAdapter();
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new com.chad.library.adapter.base.listener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent();
                intent.putExtra(Constants.CLASS_ID, list.get(position).getId());
                intent.putExtra(Constants.CLASS_NAME,list.get(position).getName());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            list = configBean.getVideoclass();
            if(list.size()>0&&list.get(0).getName().equals("我的下载")){
                list.remove(0);
            }
            if(list.size()>0&&list.get(0).getName().equals("推荐")){
                list.remove(0);
            }
            adapter.addData(list);
        }
    }
}
