package com.yunbao.live.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.adapter.ExpandableGridAdapter;
import com.yunbao.live.adapter.LiveReadyClassAdapter;
import com.yunbao.live.adapter.LiveReadyNewAdapter;
import com.yunbao.live.bean.ItemNode;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.live.bean.RootNode;
import com.yunbao.live.http.LiveHttpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2018/10/7.
 * 选择直播频道
 */

public class LiveChooseClassActivity extends AbsActivity implements OnItemClickListener<LiveClassBean> {

    private ExpandableListView expandableGridView;
    private TextView tvFinish;
    private LiveReadyNewAdapter nodeAdapter;
    private int sign = -1;// 控制列表的展开
    private List<LiveReadyBean> mList;
    private ExpandableGridAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_choose_class;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.live_class_choose));
        SPUtils.getInstance().put("type_id","");
        SPUtils.getInstance().put("type_name","");
        SPUtils.getInstance().put("goods_id","");
        SPUtils.getInstance().put("goods_name","");
        expandableGridView = (ExpandableListView) findViewById(R.id.list);
        tvFinish = (TextView) findViewById(R.id.tv_finish);
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = SPUtils.getInstance().getString("goods_id", "");
                if(!id.equals("")){
                    String type_id=SPUtils.getInstance().getString("type_id","");
                    String type_name=SPUtils.getInstance().getString("type_name","");
                    String goods_id=SPUtils.getInstance().getString("goods_id","");
                    String goods_name=SPUtils.getInstance().getString("goods_name","");

                    L.e("CLASS_ID:",type_id);
                    Intent intent = new Intent();
                    if(!TextUtils.isEmpty(type_id)){
                        intent.putExtra(Constants.CLASS_ID, Integer.parseInt(type_id));
                    }
                    if(!TextUtils.isEmpty(goods_id)){
                        intent.putExtra(Constants.GOODS_ID, Integer.parseInt(goods_id));
                    }
                    intent.putExtra(Constants.CLASS_NAME, type_name);
                    intent.putExtra(Constants.GOODS_NAME, goods_name);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        getData();
    }

    private void initModle() {
        adapter = new ExpandableGridAdapter(this,mList);
        expandableGridView.setAdapter(adapter);
    }

    private void setListener() {
        expandableGridView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                if (sign == -1) {
                    // 展开被选的group
                    expandableGridView.expandGroup(groupPosition);
                    // 设置被选中的group置于顶端
                    expandableGridView.setSelectedGroup(groupPosition);
                    sign = groupPosition;
                } else if (sign == groupPosition) {
                    expandableGridView.collapseGroup(sign);
                    sign = -1;
                } else {
                    expandableGridView.collapseGroup(sign);
                    // 展开被选的group
                    expandableGridView.expandGroup(groupPosition);
                    // 设置被选中的group置于顶端
                    expandableGridView.setSelectedGroup(groupPosition);
                    sign = groupPosition;
                }
                return true;
            }
        });
    }

    private void getData() {
        LiveHttpUtil.getGoods("",new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    mList = JSON.parseArray(Arrays.toString(info), LiveReadyBean.class);
//                    if(mList!=null&&mList.size()!=0){
//                        mList.add(mList.get(0));
//                    }
                    initModle();
                    setListener();
                }
            }
        });
    }


    @Override
    public void onItemClick(LiveClassBean bean, int position) {
        Intent intent = new Intent();
        intent.putExtra(Constants.CLASS_ID, bean.getId());
        intent.putExtra(Constants.CLASS_NAME, bean.getName());
        setResult(RESULT_OK, intent);
        finish();
    }
}
