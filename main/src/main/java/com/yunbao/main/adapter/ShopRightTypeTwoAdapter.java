package com.yunbao.main.adapter;


import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.bean.ShopRightBean;
import com.yunbao.common.custom.UndateEvent;
import com.yunbao.common.greendao.GreenDaoUtils;
import com.yunbao.common.greendao.entity.ShopRightDbBean;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.LoginNewActivity;

import org.greenrobot.eventbus.EventBus;


public class ShopRightTypeTwoAdapter extends BaseQuickAdapter<ShopRightBean, BaseViewHolder> {
    public ShopRightTypeTwoAdapter() {
        super(R.layout.item_shop_right_two);
    }

    @Override
    protected void convert(final BaseViewHolder helper, ShopRightBean item) {
        helper.setText(R.id.tv_name,item.getName());
        RecyclerView recyclerView = (RecyclerView)helper.getView(R.id.recyclerView);
        initRecycleView(recyclerView,item);
    }

    private Handler handler=new Handler();
    private void initRecycleView(RecyclerView recyclerView, final ShopRightBean item) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        ShopRightChildAdapter adapter = new ShopRightChildAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setList(item.getChildren());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if(!ClickUtil.canClick()){
                    return;
                }
                if(CommonAppConfig.getInstance().isVisitorLogin()){
                    LoginNewActivity.forwardVisitor(getContext());
                    return;
                }
                ShopRightDbBean dbBean = item.getChildren().get(position);
                if (dbBean != null && !TextUtils.isEmpty(dbBean.getJump_url())) {
                    dbBean.setClickTime(System.currentTimeMillis());
                    dbBean.setUserId(CommonAppConfig.getInstance().getUid());
                    GreenDaoUtils.getInstance().insertShopRightData(dbBean);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new UndateEvent());
                        }
                    },1000);
                    OpenUrlUtils.getInstance()
                            .setContext(getContext())
                            .setIs_king(dbBean.getIs_king())
                            .setJump_type(dbBean.getJump_type())
                            .setShopUrl(true)
                            .setTitle(dbBean.getName())
                            .setType(Integer.parseInt(dbBean.getShow_style()))
                            .setSlide_show_type_button(dbBean.getSlide_show_type_button())
                            .go(dbBean.getJump_url());
                }
            }
        });
    }
}