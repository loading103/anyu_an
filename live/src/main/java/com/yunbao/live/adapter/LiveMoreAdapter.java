package com.yunbao.live.adapter;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.utils.IMDensityUtil;
import com.yunbao.live.R;
import com.yunbao.live.bean.LiveMoreBean;
import com.yunbao.live.dialog.LiveMoreDialog;
import com.yunbao.live.event.LiveOpenDialogEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class LiveMoreAdapter extends BaseQuickAdapter<LiveMoreBean.SlideBeanX, BaseViewHolder> {
    private final LiveMoreDialog liveMoreDialog;

    public LiveMoreAdapter(LiveMoreDialog liveMoreDialog, List<LiveMoreBean.SlideBeanX> data) {
        super(R.layout.item_live_more);
        this.liveMoreDialog=liveMoreDialog;
    }

    @Override
    protected void convert(final BaseViewHolder helper, LiveMoreBean.SlideBeanX item) {
        helper.setText(R.id.tv_title,item.getCat_name());

        RecyclerView recyclerView = helper.getView(R.id.recyclerView);
        final TextView tvMore = helper.getView(R.id.tv_more);

        int position = helper.getAdapterPosition();
        //判断高度
        if(position==0 ){
            setReLayout(recyclerView, IMDensityUtil.dip2px(getContext(),140));
            if(item.getSlide().size()>8){
                tvMore.setVisibility(View.VISIBLE);
            }else {
                tvMore.setVisibility(View.GONE);
            }

        }
        if(position==1) {
            setReLayout(recyclerView, IMDensityUtil.dip2px(getContext(),140));
            if(item.getSlide().size()>8){
                tvMore.setVisibility(View.VISIBLE);
            }else {
                tvMore.setVisibility(View.GONE);
            }
        }
        if(position==2) {
            setReLayout(recyclerView, IMDensityUtil.dip2px(getContext(),70));
            if(item.getSlide().size()>4){
                tvMore.setVisibility(View.VISIBLE);
            }else {
                tvMore.setVisibility(View.GONE);
            }
        }



        LiveMoreGameAdapter adapterGame = new LiveMoreGameAdapter();
        adapterGame.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                LiveMoreBean.SlideBeanX.SlideBean b = (LiveMoreBean.SlideBeanX.SlideBean) adapter.getData().get(position);
//                OpenUrlUtils.with(getContext())
//                        .setType(b.getSlide_show_type())
//                        .setSlideTarget(b.getSlide_target())
//                        .go(b.getSlide_url());
                if("1".equals(b.getSlide_target())&&b.getSlide_show_type()==4){
                    EventBus.getDefault().post(new LiveOpenDialogEvent(b));
                }else {
                    OpenUrlUtils.getInstance()
                            .setContext(getContext())
                            .setType(b.getSlide_show_type())
                            .setSlideTarget(b.getSlide_target())
                            .setJump_type(b.getJump_type())
                            .setIs_king(b.getIs_king())
                            .setSlide_show_type_button(b.getSlide_show_type_button())
                            .go(b.getSlide_url());
                }
                liveMoreDialog.getDialog().dismiss();
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4,LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterGame);

        if(position==0 ){
            if(item.getSlide().size()>8){
                adapterGame.setList(item.getSlide().subList(0,8));
            }else {
                adapterGame.setList(item.getSlide());
            }
        }
        if(position==1) {
            if(item.getSlide().size()>8){
                adapterGame.setList(item.getSlide().subList(0,8));
            }else {
                adapterGame.setList(item.getSlide());
            }
        }
        if(position==2) {
            if(item.getSlide().size()>4){
                adapterGame.setList(item.getSlide().subList(0,4));
            }else {
                adapterGame.setList(item.getSlide());
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setTextViewStyles(tvMore);
            }
        },100);
    }

    /**

     * 设置textview 的颜色渐变

     * @param text

     */

    public void setTextViewStyles(TextView text){
        LinearGradient mLinearGradient =new LinearGradient(0, 0, 0,  text.getMeasuredHeight(),
                getContext().getResources().getColor(R.color.color_gradient_light),
                getContext().getResources().getColor(R.color.color_gradient_dark), Shader.TileMode.CLAMP);
        text.getPaint().setShader(mLinearGradient);
        text.invalidate();
    }

    public void setReLayout(RecyclerView recyclerView,int h){
        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        layoutParams.height=h;
        recyclerView.setLayoutParams(layoutParams);
    }
}