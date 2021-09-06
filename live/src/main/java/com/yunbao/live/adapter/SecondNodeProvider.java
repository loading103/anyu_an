package com.yunbao.live.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.bean.ItemNode;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.live.bean.RootNode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2020/4/29.
 * Describe:
 */
public class SecondNodeProvider extends BaseNodeProvider {
    private final List<LiveReadyBean> mList;

    public SecondNodeProvider(List<LiveReadyBean> mList) {
        this.mList=mList;
    }

    @Override
    public int getItemViewType() {
        return 1;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_section_content;
    }

    @Override
    public void convert(final BaseViewHolder baseViewHolder, final BaseNode baseNode) {
        if (baseNode == null) {
            return;
        }
        RecyclerView recyclerView = baseViewHolder.getView(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),6));
        LiveGiftChildAdapter adapter=new LiveGiftChildAdapter();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                for (int i = 0; i < adapter.getData().size(); i++) {
                    if(i==position){
                        ((LiveReadyBean.GoodsBean)adapter.getData().get(i)).setCheck(true);
                        SPUtils.getInstance().put("goods_id",((LiveReadyBean.GoodsBean)adapter.
                                getData().get(i)).getId());
                        SPUtils.getInstance().put("goods_name",((LiveReadyBean.GoodsBean)adapter.
                                getData().get(i)).getName());
                    }else {
                        ((LiveReadyBean.GoodsBean)adapter.getData().get(i)).setCheck(false);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);

        ItemNode entity = (ItemNode) baseNode;
        adapter.addData(entity.getBean());
    }

    @Override
    public void onClick( BaseViewHolder helper,  View view, BaseNode data, int position) {
    }
}
