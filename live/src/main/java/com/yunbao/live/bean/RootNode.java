package com.yunbao.live.bean;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;


import java.util.List;

/**
 * Created by Administrator on 2020/4/29.
 * Describe:
 */
public class RootNode extends BaseExpandNode {
    private List<BaseNode> childNode;
    private LiveReadyBean bean;
    private boolean isEx;

    public RootNode(List<BaseNode> childNode, LiveReadyBean bean) {
        this.childNode = childNode;
        this.bean = bean;
    }

    public LiveReadyBean getBean() {
        return bean;
    }

    public boolean isEx() {
        return isEx;
    }

    public void setEx(boolean ex) {
        isEx = ex;
    }

    @Override
    public List<BaseNode> getChildNode() {
        return childNode;
    }
}
