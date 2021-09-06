package com.yunbao.live.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.live.R;
import com.yunbao.live.bean.ItemNode;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.live.bean.RootNode;

import java.util.List;


/**
 * Created by Administrator on 2020/4/29.
 * Describe:
 */
public class RootNodeProvider extends BaseNodeProvider {
    private final List<LiveReadyBean> mList;
    private int posss=0;
    private boolean hasOpen=false;

    public RootNodeProvider(List<LiveReadyBean> mList) {
        this.mList=mList;
    }

    @Override
    public int getItemViewType() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_root_node;
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, BaseNode baseNode) {
        LiveReadyBean bean = ((RootNode) baseNode).getBean();
//        ImgLoader.display(getContext(),bean.getThumb(), (ImageView) baseViewHolder.getView(R.id.thumb));
        ImgLoader.displayWithPlaceError(getContext(),bean.getThumb(), (ImageView) baseViewHolder.getView(R.id.thumb),R.mipmap.img_default_pic2,
                R.mipmap.img_default_pic2);
        baseViewHolder.setText(R.id.name,bean.getName())
                .setText(R.id.des,bean.getDes());

//        if(baseViewHolder.getAdapterPosition()==mList.size()-1){
//            baseViewHolder.setVisible(R.id.thumb,false);
//            baseViewHolder.setVisible(R.id.name,false);
//            baseViewHolder.setVisible(R.id.des,false);
//            baseViewHolder.setVisible(R.id.radioButton,false);
////            baseViewHolder.setVisible(R.id.v,false);
//        }
    }

    @Override
    public void onClick( BaseViewHolder helper, View view, BaseNode data, int position) {
//            getAdapter().expandAndCollapseOther(i);

        if(position>=mList.size()-1){
            return;
        }

        RootNode lv = ((RootNode) getAdapter().getData().get(position));
        SPUtils.getInstance().put("type_id",lv.getBean().getId());
        SPUtils.getInstance().put("type_name",lv.getBean().getName());
        int pos=helper.getAdapterPosition();
        if(lv.isExpanded()){
            if(pos==posss){
                hasOpen=true;
            }else {
                getAdapter().collapse(pos);
                hasOpen=false;
            }
        }else {
            if(hasOpen){
                getAdapter().collapse(posss);
            }
            getAdapter().expand(helper.getAdapterPosition());
            posss=helper.getAdapterPosition();
            hasOpen=true;

            if(lv.getChildNode().size()!=0){
                for (int i = 0; i < lv.getChildNode().size(); i++) {
                    List<LiveReadyBean.GoodsBean> b = ((ItemNode) lv.getChildNode().get(i)).getBean();
                    for (int j = 0; j < b.size(); j++) {
                        b.get(j).setCheck(false);
                    }
                }
                // 改变指定的父node下的子node数据
                getAdapter().nodeSetData(data, 0, lv.getChildNode().get(0));
//                getAdapter().notifyDataSetChanged();
                SPUtils.getInstance().put("goods_id","");
                SPUtils.getInstance().put("goods_name","");
            }
        }
    }
}
