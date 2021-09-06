package com.yunbao.live.bean;

import androidx.annotation.DrawableRes;

import com.chad.library.adapter.base.entity.node.BaseNode;


import java.util.List;

/**
 * Created by Administrator on 2020/4/29.
 * Describe:
 */
public class ItemNode extends BaseNode {
    private List<LiveReadyBean.GoodsBean> bean;
    public ItemNode(List<LiveReadyBean.GoodsBean> bean) {
        this.bean = bean;
    }

    public List<LiveReadyBean.GoodsBean> getBean() {
        return bean;
    }

    public void setBean(List<LiveReadyBean.GoodsBean> bean) {
        this.bean = bean;
    }

    @Override
    public List<BaseNode> getChildNode() {
        return null;
    }
}
