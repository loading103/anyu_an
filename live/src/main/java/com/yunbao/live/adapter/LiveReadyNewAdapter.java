package com.yunbao.live.adapter;

import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.yunbao.live.bean.ItemNode;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.live.bean.RootNode;

import java.util.List;

/**
 * Created by Administrator on 2020/4/29.
 * Describe:新开播准备
 */
public class LiveReadyNewAdapter extends BaseNodeAdapter {
    public LiveReadyNewAdapter(List<LiveReadyBean> mList) {
        super();
        // 需要占满一行的，使用此方法（例如section）
        addFullSpanNodeProvider(new RootNodeProvider(mList));
        // 普通的item provider
        addNodeProvider(new SecondNodeProvider(mList));
    }

    @Override
    protected int getItemType(List<? extends BaseNode> list, int i) {
        BaseNode node = list.get(i);
        if (node instanceof RootNode) {
            return 0;
        } else if (node instanceof ItemNode) {
            return 1;
        }
        return -1;
    }
}
