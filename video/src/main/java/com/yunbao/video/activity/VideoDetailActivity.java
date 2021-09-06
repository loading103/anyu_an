package com.yunbao.video.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.model.Response;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.youth.banner.Banner;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.OpenUrlUtils;
import com.yunbao.common.activity.SmallProgramTitleActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.custom.CommonRefreshView2;
import com.yunbao.common.download.TasksManager;
import com.yunbao.common.download.TasksManagerModel;
import com.yunbao.common.event.DownModelBeanEvent;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.JsonBean;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.AnimatorUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.IMImageLoadUtil;
import com.yunbao.common.utils.IMStatusBarUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.video.R;
import com.yunbao.video.adapter.VideoAdapter;
import com.yunbao.video.adapter.VideoCommentAdapter2;
import com.yunbao.common.bean.VideoBean;
import com.yunbao.video.bean.VideoCommentBean;
import com.yunbao.video.bean.VideoDetailsBean;
import com.yunbao.video.custom.MyRecyclerView;
import com.yunbao.video.dialog.VideoShareDialog;
import com.yunbao.video.event.VideoCommentEvent;
import com.yunbao.video.event.VideoLikeEvent;
import com.yunbao.video.event.VideoRefreshCommentEvent;
import com.yunbao.video.event.VideoViewNumAddEvent;
import com.yunbao.video.http.VideoHttpUtil;
import com.yunbao.video.utils.VideoStorge;
import com.yunbao.video.views.CommonVideoPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;

import static com.yunbao.common.utils.ClickUtil.isFastClick;
import static com.yunbao.common.utils.RouteUtil.PATH_USER_HOME;

public class VideoDetailActivity extends GSYBaseActivityDetail implements View.OnClickListener, OnItemClickListener<VideoCommentBean>, VideoCommentAdapter2.ActionListener {
    public CommonVideoPlayer detail_player;
    private RecyclerView recyclerView;
    private MyRecyclerView likeRecyclerView;
    private LottieAnimationView animationView;
    private RelativeLayout mRootView;
    private LinearLayout llBottom;
    private TextView etContent;
    private String mVideoKey;
    private TextView tvPl;
    private TextView tvLike;
    private TextView tvShare;
    private CommonRefreshView2 mRefreshView;
    private VideoCommentAdapter2 mVideoCommentAdapter;
    private String mVideoId;
    private VideoBean bean;
    private boolean mNeedRefresh;
    private TextView tvAll;
    private TextView tvTitle;
    private ImageView ivHead;
    private TextView tvName;
    private TextView tvUser1;
    private TextView tvUser2;
    private TextView tvUser3;
    private TextView tvFocus;
    private Banner banner;
    private VideoAdapter likeAdapter;
    private ImageView ivAd;
    private TextView tvCai;
    private VideoDetailsBean videoDetailsBean;
    private List<VideoCommentBean> list;
    private View empty;

    //标记是否需要二次滑动
    private boolean shouldMove;
    //需要滑动到的item位置
    private int mPosition;
    private FrameLayout flContent;
    private boolean  islandClick=false;//判断是不是点击全屏
    private ImageView tvDownload;
    private RelativeLayout rlnotice;
    private RelativeLayout ll_root1;
    private RelativeLayout ll_root2;
    private ImageView iv_vip;
    private boolean isDownloaded;//是否下载
    private String size;
    private String modelPath;//已下载视频的本地资源路径

    private String vip_download_limit;//下载vip限制
    private int myVip;
    private boolean isloaded=false;

    public static void forward(Context context, int position, String videoKey, int page) {
        Intent intent = new Intent(CommonAppContext.sInstance, VideoDetailActivity.class);
        intent.putExtra(Constants.VIDEO_POSITION, position);
        intent.putExtra(Constants.VIDEO_KEY, videoKey);
        intent.putExtra(Constants.VIDEO_PAGE, page);
        context.startActivity(intent);
    }

    public static void forward(Context context, VideoBean bean) {
        Intent intent = new Intent(CommonAppContext.sInstance, VideoDetailActivity.class);
        intent.putExtra(Constants.VIDEO_BEAN, bean);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_video_detail;
    }



    @Override
    public boolean forbitSootSceenEnble() {
        return  true;
    }
    @Override
    protected void main() {
        EventBus.getDefault().register(this);
        Intent intent = getIntent();


        mVideoKey = intent.getStringExtra(Constants.VIDEO_KEY);
        bean = intent.getParcelableExtra(Constants.VIDEO_BEAN);
        if (mVideoKey != null) {
            if (TextUtils.isEmpty(mVideoKey)) {
                return;
            }
            int position = intent.getIntExtra(Constants.VIDEO_POSITION, 0);
            int page = intent.getIntExtra(Constants.VIDEO_PAGE, 1);

            List<VideoBean> list = VideoStorge.getInstance().get(mVideoKey);
            if (list == null || list.size() == 0) {
                return;
            }

            bean = list.get(position);
            mVideoId = bean.getId();
        } else {
            mVideoId = bean.getId();
        }

        setStatusBars();
        findViewByIds();
//        addPlayHoler();
        initData();
        getData();
        mRefreshView.initData();
        EventBus.getDefault().post(new VideoViewNumAddEvent(mVideoId));
        initDownload();
    }

    private void initDownload() {
        TasksManagerModel model=null;
        for (int j = 0; j < TasksManager.getImpl().getTaskCounts(); j++) {
            if(bean.getId().equals(TasksManager.getImpl().get(j).getVideo_id())){
                model=TasksManager.getImpl().get(j);
            }
        }

        if(model!=null){
            tvDownload.setImageResource(R.mipmap.video_downed);
            isDownloaded=true;
            rlnotice.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(model.getPath()) && new File(model.getPath()).exists()){
                modelPath=model.getPath();
            }
        }else {
            isDownloaded=false;
            rlnotice.setVisibility(View.VISIBLE);
            rlnotice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rlnotice.setVisibility(View.INVISIBLE);
                }
            });
            rlnotice.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(rlnotice!=null){
                        AnimatorUtil animatorUtil=new AnimatorUtil();
                        animatorUtil.objectAnimation(rlnotice);
                    }
                }
            },3000);
        }
        initVideoBuilderMode();
    }

    private void getData() {
        VideoHttpUtil.getVideo(mVideoId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    L.e("WOLF", "getVideo:" + info[0]);
                    isloaded=true;
                    videoDetailsBean = JSON.parseObject(info[0], VideoDetailsBean.class);

                    if(ll_root1!=null){
                        ll_root1.setVisibility(View.VISIBLE);
                        ll_root2.setVisibility(View.VISIBLE);
                        ivAd.setVisibility(View.VISIBLE);
                    }

                    if(videoDetailsBean!=null){
                        size = videoDetailsBean.getSize();
                        vip_download_limit=videoDetailsBean.getVip_download_limit();
                        if( !TextUtils.isEmpty(vip_download_limit ) && Integer.parseInt(vip_download_limit)==0){
                            iv_vip.setVisibility(View.INVISIBLE);
                        }else {
                            iv_vip.setVisibility(View.INVISIBLE);
                        }
                    }
                    bindData(videoDetailsBean);
                }
                if(code==1000){//视频已删除
                    if(ll_root1!=null){
                        ll_root1.setVisibility(View.GONE);
                        ll_root2.setVisibility(View.GONE);
                        ivAd.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onError(Response<JsonBean> response) {
                isloaded=true;
            }
            @Override
            public void onError() {
                isloaded=true;
            }
        });

//        VideoHttpUtil.getHomeVideoList("all","1",1, new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//                if (code == 0) {
//                    L.e("WOLF", "getHomeVideoList:" + Arrays.toString(info));
//                    List<VideoBean> list = JSON.parseArray(Arrays.toString(info), VideoBean.class);
//                    List<VideoBean> a = new ArrayList<>();
//                    for (int i = 0; i < list.size(); i++) {
//                        if (i < 4) {
//                            a.add(list.get(i));
//                        }
//                    }
//                    likeAdapter.addData(a);
//                    if (a.size() == 0) {
//                        tvCai.setVisibility(View.GONE);
//                    }
//                }
//            }
//        });

        VideoHttpUtil.getGuessLike(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<VideoBean> list = JSON.parseArray(Arrays.toString(info), VideoBean.class);
                    likeAdapter.addData(list);
                    if (list.size() == 0) {
                        tvCai.setVisibility(View.GONE);
                    }
                }
            }
        });
    }



    private void bindData(VideoDetailsBean data) {
        tvTitle.setText(data.getTitle());
        Glide.with(CommonAppContext.sInstance).load(data.getUserinfo().getAvatar()).
                apply(RequestOptions.bitmapTransform(new CropCircleWithBorderTransformation(SizeUtils.dp2px(2.0f)
                        , getResources().getColor(R.color.color_FE41F4))))
                .into(ivHead);
        tvName.setText(data.getUserinfo().getUser_nicename());
        tvUser1.setText(data.getDatetime());
        tvUser2.setText(data.getLikes());
        tvUser3.setText(data.getViews());
        if (data.getIsattent().equals("1")) {
            tvFocus.setText("已关注");
            tvFocus.setBackground(getResources().getDrawable(R.drawable.shape_video_star2));
        } else {
            tvFocus.setText("关注");
            tvFocus.setBackground(getResources().getDrawable(R.drawable.shape_video_star));
        }
        if (tvPl != null && !data.getComments().equals("0")) {
            tvPl.setText(data.getComments());
        } else {
            tvPl.setText("评论");
        }
        if (tvLike != null && !data.getLikes().equals("0")) {
            tvLike.setText(data.getLikes());
        } else {
            tvLike.setText("喜欢");
        }

        if (data.getIslike().equals("1")) {
            Drawable drawable = getResources().getDrawable(R.mipmap.bt_zan2);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvLike.setCompoundDrawables(null, drawable, null, null);
        } else {
            Drawable drawable = getResources().getDrawable(R.mipmap.bt_dz);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvLike.setCompoundDrawables(null, drawable, null, null);
        }

        if (tvShare != null && !data.getShares().equals("0")) {
            tvShare.setText(data.getShares());
        } else {
            tvShare.setText("分享");
        }
        tvAll.setText("全部评论" + "(" + data.getComments() + ")");

        RequestOptions myOptions = new RequestOptions().transform(new MultiTransformation<>(new CenterCrop(),
                new RoundedCorners(10)));
        if (data.getAdv() != null) {
            Glide.with(CommonAppContext.sInstance).load(data.getAdv().getThumb()).apply(myOptions).into(ivAd);
        } else {
            ivAd.setVisibility(View.GONE);
        }


        data.getId();
    }

    private void initData() {
        tvPl = findViewById(R.id.tv_pl);
        tvLike = findViewById(R.id.tv_like);
        tvShare = findViewById(R.id.tv_share);
        etContent = findViewById(R.id.et_content);
        tvCai = findViewById(R.id.tv_cai);

        tvPl.setOnClickListener(this);
        tvLike.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        etContent.setOnClickListener(this);

        animationView = findViewById(R.id.animation_view);
        mRootView = findViewById(R.id.mRootView);
        llBottom = findViewById(R.id.ll_bottom);
        flContent = findViewById(R.id.fl_content);

        mRefreshView = (CommonRefreshView2) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.empty_null);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (Exception e) {
                    L.e("onLayoutChildren------>" + e.getMessage());
                }
            }
        });

        mVideoCommentAdapter = new VideoCommentAdapter2(mContext);
        mVideoCommentAdapter.setOnItemClickListener(VideoDetailActivity.this);
        mVideoCommentAdapter.setActionListener(VideoDetailActivity.this);
        mRefreshView.setRecyclerViewAdapter(mVideoCommentAdapter);
        mRefreshView.setRefreshEnable(false);
        mRefreshView.setDataHelper(new CommonRefreshView2.DataHelper<VideoCommentBean>() {
            @Override
            public RefreshAdapter<VideoCommentBean> getAdapter() {
                return mVideoCommentAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if (!TextUtils.isEmpty(mVideoId)) {
                    L.e("WOLF", "loadData:" + mVideoId + ":" + p);
                    if (flContent != null) {
                        flContent.removeAllViews();
                        flContent.addView(mRefreshView);
                    }
                    VideoHttpUtil.getVideoCommentList(mVideoId, p, callback);
                }
            }

            @Override
            public List<VideoCommentBean> processData(String[] info) {
                L.e("WOLF", "info2:" + info[0]);
                JSONObject obj = JSON.parseObject(info[0]);
                String commentNum = obj.getString("comments");
                EventBus.getDefault().post(new VideoCommentEvent(mVideoId, commentNum));
                list = JSON.parseArray(obj.getString("commentlist"), VideoCommentBean.class);
                for (VideoCommentBean bean : list) {
                    if (bean != null) {
                        bean.setParentNode(true);
                    }
                }
                return list;
            }

            @Override
            public void onRefreshSuccess(List<VideoCommentBean> list, int listCount) {
                if (empty != null) {
                    if (list.size() > 0) {
                        empty.setVisibility(View.GONE);
                    } else {
                        empty.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onRefreshFailure() {
                if (list == null) {
                    if (flContent != null) {
                        flContent.removeAllViews();
                        View view = LayoutInflater.from(mContext).inflate(R.layout.view_no_net_live_video, flContent, false);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                        params.gravity = Gravity.CENTER;
                        view.setLayoutParams(params);
                        flContent.addView(view);
                        view.findViewById(R.id.tv_try).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getData();
                                mRefreshView.initData();
                            }
                        });
                    }
                }
            }

            @Override
            public void onLoadMoreSuccess(List<VideoCommentBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });

        View header = mVideoCommentAdapter.getHeadView();
        likeRecyclerView = header.findViewById(R.id.recyclerView);
        tvAll = header.findViewById(R.id.tv_all);
        tvTitle = header.findViewById(R.id.tv_title);
        ivHead = header.findViewById(R.id.iv_head);
        tvName = header.findViewById(R.id.tv_name);
        tvUser1 = header.findViewById(R.id.tv_user1);

        tvUser2 = header.findViewById(R.id.tv_user2);
        tvUser3 = header.findViewById(R.id.tv_user3);
        tvFocus = header.findViewById(R.id.tv_focus);
        tvDownload = header.findViewById(R.id.tv_download);
        rlnotice = header.findViewById(R.id.rlnotice);
        iv_vip = header.findViewById(R.id.iv_vip);
        ll_root1 = header.findViewById(R.id.ll_root1);
        ll_root2 = header.findViewById(R.id.ll_root2);


        tvFocus.setOnClickListener(this);
        tvDownload.setOnClickListener(this);
        ivAd = header.findViewById(R.id.iv_ad);
        ivAd.setOnClickListener(this);
        ivHead.setOnClickListener(this);
        //请求到数据再显示
        ll_root1.setVisibility(View.GONE);
        ll_root2.setVisibility(View.GONE);
        ivAd.setVisibility(View.GONE);

        likeRecyclerView.setNestedScrollingEnabled(false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        likeRecyclerView.setLayoutManager(gridLayoutManager);
        likeAdapter = new VideoAdapter();
        likeAdapter.setOnItemClickListener(new com.chad.library.adapter.base.listener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if(isFastClick()){
                    return;
                }
                int vip = SPUtils.getInstance().getInt(Constants.USER_VIP);
                if(vip<likeAdapter.getData().get(position).getVip_limit()){
                    showLimitDialog(mContext,likeAdapter.getData().get(position).getVip_limit(),position);
                }else {
                    VideoDetailActivity.forward(mContext, likeAdapter.getData().get(position));
                }
            }
        });
        likeRecyclerView.setAdapter(likeAdapter);

        empty = mVideoCommentAdapter.getEmptyView();
    }

    /**
     * 限制观看dialog
     */
    public void showLimitDialog(final Context context, int vip, final int position) {
        DialogUitl.Builder builder = new DialogUitl.Builder(context);
        builder.setTitle("温馨提示")
                .setContent("亲\uD83D\uDE0A！观看该视频需要达到VIP"+vip+"\n点击获取开通VIP！")
                .setConfrimString("获取VIP")
                .setCancelString("取消")
                .setCancelable(false)
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        new SmallProgramTitleActivity().toActivity(context, CommonAppConfig.getInstance().getConfig().getVideo_limit_rule(),"normal",true);
                    }
                })
                .build()
                .show();
    }

    @Override
    public void onItemClick(VideoCommentBean bean, int position) {
        if (!TextUtils.isEmpty(mVideoId) && !TextUtils.isEmpty(bean.getUid())) {
            ((AbsVideoCommentActivity) mContext).openCommentInputWindow(false, mVideoId, bean.getUid(), bean);
        }
    }

    public void needRefresh() {
        mNeedRefresh = true;
    }

    private void setStatusBars() {
        IMStatusBarUtil.setTranslucentForImageView(this, 0, null);
    }


    private void findViewByIds() {
        detail_player = findViewById(R.id.detail_player);
        recyclerView = findViewById(R.id.recyclerView);
        detail_player.setDateBean(bean);

    }

    /**
     * 重写全屏时候的返回键
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_pl) {
            if (list != null && list.size() != 0) {
                recyclerView.smoothScrollToPosition(1);
            } else {
                recyclerView.smoothScrollToPosition(1);
            }
        } else if (v.getId() == R.id.tv_like) {
            clickLike();
        } else if (v.getId() == R.id.tv_share) {
            openShareWindow();
        } else if (v.getId() == R.id.et_content) {
            if (!TextUtils.isEmpty(mVideoId) && !TextUtils.isEmpty(bean.getUid())) {
                ((AbsVideoCommentActivity) mContext).openCommentInputWindow(false, mVideoId, bean.getUid(), null);
            }
            recyclerView.smoothScrollToPosition(1);
        } else if (v.getId() == R.id.tv_focus) {
            follow();
        } else if (v.getId() == R.id.iv_ad) {
            if(videoDetailsBean==null){
                return;
            }
            OpenUrlUtils.getInstance().setContext(mContext).setSlideTarget(videoDetailsBean.getAdv().getSlide_target())
                    .setJump_type(videoDetailsBean.getAdv().getJump_type())
                    .setIs_king(videoDetailsBean.getAdv().getIs_king())
                    .setType(Integer.parseInt(videoDetailsBean.getAdv().getSlide_show_type()))
                    .setSlide_show_type_button(videoDetailsBean.getAdv().getSlide_show_type_button())
                    .go(videoDetailsBean.getAdv().getSlide_url());
        } else if (v.getId() == R.id.iv_head) {
            forwardUserHome(mContext, bean.getUid());
        }else if (v.getId() == R.id.tv_download) {
            if(!isDownloaded){
                download();
            }
        }
    }

    /**
     * 下载
     */
    private void download() {

        if(bean==null ||TextUtils.isEmpty(bean.getId())){
            return;
        }
        if(!isloaded){
            Toast.makeText(this, "正在加载数据，请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(bean.getHref())){
            Toast.makeText(this, "下载链接为空，无法下载", Toast.LENGTH_SHORT).show();
            return;
        }
        myVip = SPUtils.getInstance().getInt(Constants.USER_VIP);
        if(!TextUtils.isEmpty(vip_download_limit) && Integer.parseInt(vip_download_limit)>myVip){
            showNotVip();
            return;
        }
        isDownloaded=true;
        tvDownload.setImageResource(R.mipmap.video_downed);
        ToastUtil.show("下载中...");
        if(!TextUtils.isEmpty(size)){
            bean.setSize(size);
        }
//        if(!TextUtils.isEmpty(vip_download_limit)){
//            bean.setVip_limit(Integer.parseInt(vip_download_limit));
//        }
        EventBus.getDefault().post(new DownModelBeanEvent(bean));
    }

    private void showNotVip() {
        DialogUitl.Builder builder = new DialogUitl.Builder(mContext);
        Dialog build = builder.build();
        Window window = build.getWindow();
        window.setWindowAnimations(com.yunbao.common.R.style.ActionSheetDialogAnimations);
        builder.setTitle("温馨提示")
                .setContent("亲\uD83D\uDE0A！下载该视频需要达到VIP"+vip_download_limit+"\n点击获取开通VIP！")
                .setConfrimString("获取VIP")
                .setCancelString("取消")
                .setCancelable(false)
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        new SmallProgramTitleActivity().toActivity(mContext, CommonAppConfig.getInstance().getConfig().getVideo_limit_rule(),"normal",true);
                    }
                })
                .build()
                .show();
    }

    /**
     * 跳转到个人主页
     */
    public static void forwardUserHome(Context context, String toUid) {
        ARouter.getInstance().build(PATH_USER_HOME)
                .withString(Constants.TO_UID, toUid)
                .navigation();
    }

    /**
     * 关注
     */
    private void follow() {
        CommonHttpUtil.setAttention(bean.getUid(), new CommonCallback<Integer>() {
            @Override
            public void callback(Integer bean) {
//                upDateData();
                if (tvFocus.getText().equals("关注")) {
                    tvFocus.setText("已关注");
                    tvFocus.setBackground(getResources().getDrawable(R.drawable.shape_video_star2));
                } else {
                    tvFocus.setText("关注");
                    tvFocus.setBackground(getResources().getDrawable(R.drawable.shape_video_star));
                }
            }
        });
    }

    /**
     * 分享
     */
    public void openShareWindow() {
        VideoShareDialog dialog = new VideoShareDialog();
        if (CommonAppConfig.getInstance().getUid().equals(bean.getUid())) {
            dialog.setSelf(true);
        } else {
            dialog.setSelf(false);
        }
        dialog.setBean(bean);
        dialog.show(getSupportFragmentManager(), "VideoShareDialog");
    }


    @Override
    public void onExpandClicked(final VideoCommentBean commentBean) {
        final VideoCommentBean parentNodeBean = commentBean.getParentNodeBean();
        if (parentNodeBean == null) {
            return;
        }
        VideoHttpUtil.getCommentReply(parentNodeBean.getId(), parentNodeBean.getChildPage(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<VideoCommentBean> list = JSON.parseArray(Arrays.toString(info), VideoCommentBean.class);
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    if (parentNodeBean.getChildPage() == 1) {
                        if (list.size() > 1) {
                            list = list.subList(1, list.size());
                        }
                    }
                    for (VideoCommentBean bean : list) {
                        bean.setParentNodeBean(parentNodeBean);
                    }
                    List<VideoCommentBean> childList = parentNodeBean.getChildList();
                    if (childList != null) {
                        childList.addAll(list);
                        if (childList.size() < parentNodeBean.getReplyNum()) {
                            parentNodeBean.setChildPage(parentNodeBean.getChildPage() + 1);
                        }
                        if (mVideoCommentAdapter != null) {
                            mVideoCommentAdapter.insertReplyList(commentBean, list.size());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onCollapsedClicked(VideoCommentBean commentBean) {
        VideoCommentBean parentNodeBean = commentBean.getParentNodeBean();
        if (parentNodeBean == null) {
            return;
        }
        List<VideoCommentBean> childList = parentNodeBean.getChildList();
        VideoCommentBean node0 = childList.get(0);
        int orignSize = childList.size();
        parentNodeBean.removeChild();
        parentNodeBean.setChildPage(1);
        if (mVideoCommentAdapter != null) {
            mVideoCommentAdapter.removeReplyList(node0, orignSize - childList.size());
        }
    }

    /**
     * 评论数发生变化
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoRefreshCommentEvent(VideoRefreshCommentEvent e) {
        mRefreshView.initData();
        upDateData();
    }

    private void upDateData() {
        VideoHttpUtil.getVideo(mVideoId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    VideoDetailsBean videoDetailsBean = JSON.parseObject(info[0], VideoDetailsBean.class);
                    if (tvPl != null && !videoDetailsBean.getComments().equals("0")) {
                        tvPl.setText(videoDetailsBean.getComments());
                    } else {
                        tvPl.setText("评论");
                    }
                    tvAll.setText("全部评论" + "(" + videoDetailsBean.getComments() + ")");

                    if (tvLike != null && !videoDetailsBean.getLikes().equals("0")) {
                        tvLike.setText(videoDetailsBean.getLikes());
                    } else {
                        tvLike.setText("喜欢");
                    }

                    if (tvShare != null && !videoDetailsBean.getShares().equals("0")) {
                        tvShare.setText(videoDetailsBean.getShares());
                    } else {
                        tvShare.setText("分享");
                    }

                    tvUser1.setText(videoDetailsBean.getDatetime());
                    tvUser2.setText(videoDetailsBean.getLikes());
                    tvUser3.setText(videoDetailsBean.getViews());
                    if (videoDetailsBean.getIsattent().equals("1")) {
                        tvFocus.setText("已关注");
                        tvFocus.setBackground(getResources().getDrawable(R.drawable.shape_video_star2));
                    } else {
                        tvFocus.setText("关注");
                        tvFocus.setBackground(getResources().getDrawable(R.drawable.shape_video_star));
                    }
                }
            }
        });
    }

    /**
     * 点赞,取消点赞
     */
    private void clickLike() {
        final VideoBean mVideoBean = bean;
        if (mVideoBean == null) {
            return;
        }
        VideoHttpUtil.setVideoLike(mTag, mVideoBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String likeNum = obj.getString("likes");
                    int like = obj.getIntValue("islike");
                    if (mVideoBean != null) {
                        mVideoBean.setLikeNum(likeNum);
                        mVideoBean.setLike(like);
                        EventBus.getDefault().post(new VideoLikeEvent(mVideoBean.getId(), like, likeNum));
                    }
                    if (tvLike != null && !likeNum.equals("0")) {
                        tvLike.setText(likeNum);
                    } else {
                        tvLike.setText("喜欢");
                    }
                    if (tvLike != null) {
                        if (like == 1) {
                            Drawable drawable = getResources().getDrawable(R.mipmap.bt_zan2);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            tvLike.setCompoundDrawables(null, drawable, null, null);
                        } else {
                            Drawable drawable = getResources().getDrawable(R.mipmap.bt_dz);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            tvLike.setCompoundDrawables(null, drawable, null, null);
                        }
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    @Override
    public void onPause() {
        Log.e("---------","onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("---------","onResume");
        IMStatusBarUtil.setTranslucentForImageView(this, 0, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public GSYBaseVideoPlayer getGSYVideoPlayer() {
        return detail_player;
    }



    @Override
    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
        //内置封面可参考SampleCoverVideo
        if(bean==null){
            return null;
        }
        String videUrl=TextUtils.isEmpty(modelPath)?bean.getHref():modelPath;
        ImageView imageView = new ImageView(this);
        IMImageLoadUtil.CommonImageLoadCp(this,bean.getThumb(),imageView);
        return new GSYVideoOptionBuilder()
                .setThumbImageView(imageView)
                .setUrl(videUrl)
                .setCacheWithPlay(true)
                .setVideoTitle(bean.getTitle())
                .setIsTouchWiget(true)
                .setAutoFullWithSize(true)
                .setRotateViewAuto(true)
                .setLockLand(true)
                .setShowFullAnimation(false)//打开动画
                .setNeedLockFull(true)
                .setFullHideActionBar(true)
                .setSeekRatio(1);
    }

    @Override
    public void clickForFullScreen() {

    }

    @Override
    public boolean getDetailOrientationRotateAuto() {
        return true;
    }

    private boolean   isHorion;
    private  Handler handler=new Handler();
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isHorion=true;
        } else {
            isHorion=false;
            hideTableBar();
        }
    }
    /**
     * 影藏状态栏
     */
    public void hideTableBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CommonUtil.hideSupportActionBar(VideoDetailActivity.this,true,true);
            }
        },500);
    }

    public boolean isHorion() {
        return isHorion;
    }
    public VideoBean getBean() {
        return bean;
    }
}
