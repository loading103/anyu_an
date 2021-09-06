package com.yunbao.live.dialog;

import android.content.DialogInterface;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yunbao.common.Constants;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.L;
import com.yunbao.live.R;
import com.yunbao.live.adapter.LiveMoreAdapter;
import com.yunbao.live.adapter.LiveMoreFooterAdapter;
import com.yunbao.live.adapter.LiveMoreGameAdapter;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.live.bean.LiveMoreBean;
import com.yunbao.live.event.LiveCloseDialogEvent;
import com.yunbao.live.event.LiveOpenDialogEvent;
import com.yunbao.common.interfaces.DissmissDialogListener;
import com.yunbao.live.presenter.CheckLivePresenter;
import com.yunbao.live.views.LiveAudienceViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2020/5/13.
 * Describe:直播更多
 */
public class LiveMoreDialog extends AbsDialogFragment {

    private final LiveAudienceViewHolder liveAudienceViewHolder;
    private LiveMoreBean data;
    private LiveMoreGameAdapter adapterGame;
    private CheckLivePresenter mCheckLivePresenter;

    private LiveMoreFooterAdapter adapterFooter;

    public LiveMoreDialog(LiveAudienceViewHolder liveAudienceViewHolder) {
        super();
        this.liveAudienceViewHolder = liveAudienceViewHolder;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_more2;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_IMMERSIVE
//                | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        getDialog().getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(com.yunbao.im.R.style.leftToRightAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.RIGHT;
        window.setAttributes(params);
        //全屏显示
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // android 5.0 需要做一下
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            //给 dialog 的顶层view 添加一个状态栏收起监听，让状态栏收起
//            ViewCompat.setOnApplyWindowInsetsListener(window.getDecorView(), new OnApplyWindowInsetsListener() {
//                @Override
//                public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
//                    return insets.consumeSystemWindowInsets();
//                }
//            });
//        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(null);
        EventBus.getDefault().register(this);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        final LinearLayout llMore = (LinearLayout) findViewById(R.id.ll_more);
        final TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        final TextView tvBack = (TextView) findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMore.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        LiveMoreAdapter adapter = new LiveMoreAdapter(this,data.getSlide());
        adapter.addChildClickViewIds(R.id.rl1);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.rl1) {
                    if(data.getSlide().get(position).getSlide().size() <= 8&& position==0){
                        return;
                    }
                    if (data.getSlide().get(position).getSlide().size() <= 8 && position==1) {
                        return;
                    }
                    if (data.getSlide().get(position).getSlide().size() <= 4 && position==2) {
                        return;
                    }
                    llMore.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    tvTitle.setText(data.getSlide().get(position).getCat_name());
                    adapterGame.setList(data.getSlide().get(position).getSlide());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setTextViewStyles(tvTitle);
                            setTextViewStyles(tvBack);
                        }
                    }, 100);
                }
            }
        });
        if(data!=null  && data.getLive_avatar()!=null && data.getLive_avatar().size()>0){
            View footer = getLayoutInflater().inflate(R.layout.view_footer_live_more, null);
            initFooter(footer);
            adapter.addFooterView(footer);
        }
        recyclerView.setAdapter(adapter);
        adapter.setList(data.getSlide());
        L.e("屏幕宽度");
        L.e("屏幕宽度：" + SizeUtils.px2dp(ScreenUtils.getScreenWidth()));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                L.e("rv高度：" + recyclerView.getMeasuredHeight());
//                L.e("屏幕高度：" + ScreenUtils.getScreenHeight());
                L.e("屏幕宽度：" + SizeUtils.px2dp(ScreenUtils.getScreenWidth()));
            }
        }, 50);


        recyclerView2.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        adapterGame = new LiveMoreGameAdapter();
        adapterGame.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                LiveMoreBean.SlideBeanX.SlideBean b = (LiveMoreBean.SlideBeanX.SlideBean) adapter.getData().get(position);
                L.e("WOLF","type1:"+b.getSlide_target());
                L.e("WOLF","type2:"+b.getSlide_show_type());
                if("1".equals(b.getSlide_target())&&b.getSlide_show_type()==4.){
                    EventBus.getDefault().post(new LiveOpenDialogEvent(b));
                }else {
                    OpenUrlUtils.getInstance().setContext(getContext())
                            .setType(b.getSlide_show_type())
                            .setSlideTarget(b.getSlide_target())
                            .setJump_type(b.getJump_type())
                            .setIs_king(b.getIs_king())
                            .setSlide_show_type_button(b.getSlide_show_type_button())
                            .go(b.getSlide_url());
                }
//                OpenUrlUtils.with(getContext())
////                        .setType(b.getSlide_show_type())
////                        .setSlideTarget(b.getSlide_target())
////                        .go(b.getSlide_url());
                getDialog().dismiss();
            }
        });
        recyclerView2.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));
        recyclerView2.setAdapter(adapterGame);
    }

    private void initFooter(View footer) {
        adapterFooter = new LiveMoreFooterAdapter();
        adapterFooter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                watchLive(data.getLive_avatar().get(position), Constants.LIVE_HOME, position);
                liveAudienceViewHolder.close();
            }
        });
        final RecyclerView rvFooter = footer.findViewById(R.id.recyclerView);
        rvFooter.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        rvFooter.setAdapter(adapterFooter);

        adapterFooter.setList(data.getLive_avatar());
    }

    public void setData(LiveMoreBean bean) {
        this.data = bean;
    }

    public LiveMoreBean getData() {
        return this.data;
    }

    public LiveMoreFooterAdapter getAdapterFooter() {
        return adapterFooter;
    }

    /**
     * 观看直播
     */
    public void watchLive(LiveBean liveBean, String key, int position) {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        mCheckLivePresenter.watchLive(liveBean, key, position);
    }

    /**
     * 设置textview 的颜色渐变
     *
     * @param text
     */
    public void setTextViewStyles(TextView text) {
        LinearGradient mLinearGradient = new LinearGradient(0, 0, 0, text.getMeasuredHeight(),
                getContext().getResources().getColor(R.color.color_gradient_light),
                getContext().getResources().getColor(R.color.color_gradient_dark), Shader.TileMode.CLAMP);
        text.getPaint().setShader(mLinearGradient);
        text.invalidate();
    }



    /**
     * 关闭
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveCloseDialogEvent(LiveCloseDialogEvent e) {
        if(getDialog()!=null){
            getDialog().dismiss();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        dialogDiss();
    }

    @Override
    protected void dialogDiss() {
        if(ondissDialogListener!=null){
            ondissDialogListener.onDissmissListener();
        }
    }
    private DissmissDialogListener ondissDialogListener;
    public void setOnDissmissDialogListener(DissmissDialogListener ondissDialogListener) {
        this.ondissDialogListener = ondissDialogListener;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
