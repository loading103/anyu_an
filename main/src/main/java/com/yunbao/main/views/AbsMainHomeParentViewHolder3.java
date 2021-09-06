package com.yunbao.main.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.yunbao.common.adapter.ViewPagerAdapter;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.main.R;
import com.yunbao.main.interfaces.AppBarStateListener;
import com.yunbao.main.interfaces.MainAppBarExpandListener;
import com.yunbao.main.interfaces.MainAppBarLayoutListener;
import com.yunbao.main.widget.ColorTransitionPagerTitleView1;
import com.yunbao.main.widget.IMScaleTransitionPagerTitleView1;
import com.yunbao.main.widget.LinePagerIndicator1;
import com.yunbao.main.widget.SimplePagerTitleView1;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/2/20.
 */

public abstract class AbsMainHomeParentViewHolder3 extends AbsMainViewHolder {

    private AppBarLayout mAppBarLayout;
    protected ViewPager mViewPager;
    private MagicIndicator mIndicator;
    private TextView mRedPoint;//显示未读消息数量的红点
    protected AbsMainHomeChildViewHolder[] mViewHolders;
    private MainAppBarLayoutListener mAppBarLayoutListener;
    private MainAppBarExpandListener mAppBarExpandListener;
    private boolean mPaused;
    protected List<FrameLayout> mViewList;
    public boolean isGetData;

    public AbsMainHomeParentViewHolder3(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    public AbsMainHomeParentViewHolder3(Context context, ViewGroup parentView, List<LiveClassBean> list) {
        super(context, parentView,list);
    }

    @Override
    public void init() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, int state) {
                switch (state) {
                    case AppBarStateListener.EXPANDED:
                        if (mAppBarExpandListener != null) {
                            mAppBarExpandListener.onExpand(true);
                        }
                        break;
                    case AppBarStateListener.MIDDLE:
                    case AppBarStateListener.COLLAPSED:
                        if (mAppBarExpandListener != null) {
                            mAppBarExpandListener.onExpand(false);
                        }
                        break;
                }
            }
        });
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float totalScrollRange = appBarLayout.getTotalScrollRange();
                float rate = -1 * verticalOffset / totalScrollRange;
                if (mAppBarLayoutListener != null) {
                    mAppBarLayoutListener.onOffsetChanged(rate);
                }
            }
        });

    }

    public void showTitle() {
        mViewList = new ArrayList<>();
        int pageCount = getPageCount();
        for (int i = 0; i < pageCount; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewHolders = new AbsMainHomeChildViewHolder[pageCount];
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        if (pageCount > 1) {
            mViewPager.setOffscreenPageLimit(pageCount - 1);
        }
        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                loadPageData(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
        final String[] titles = getTitles();
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
//                SimplePagerTitleView1 simplePagerTitleView = new ColorTransitionPagerTitleView1(context);
                SimplePagerTitleView1 simplePagerTitleView = new IMScaleTransitionPagerTitleView1(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.gray1));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.getPaint().setFakeBoldText(true);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mViewPager != null) {
                            mViewPager.setCurrentItem(index);
                        }
                    }
                });
                return simplePagerTitleView;
            }


            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator1 linePagerIndicator = new LinePagerIndicator1(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setXOffset(DpUtil.dp2px(5));
                linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
//                linePagerIndicator.setColors(ContextCompat.getColor(mContext, com.yunbao.common.R.color.color_7D4AFF),
//                        ContextCompat.getColor(mContext, com.yunbao.common.R.color.color_ED2AFF));
                linePagerIndicator.setLineHeight((float) UIUtil.dip2px(context, 3.0));
                linePagerIndicator.setLineWidth((float) UIUtil.dip2px(context, 30.0));
                linePagerIndicator.setRoundRadius((float) UIUtil.dip2px(context, 6.0));
                linePagerIndicator.setStartInterpolator(new AccelerateInterpolator());
                linePagerIndicator.setEndInterpolator(new DecelerateInterpolator(2.0f));

                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.colorAccent));
                return linePagerIndicator;
            }
        });
        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
        mViewPager.setCurrentItem(1);
        mRedPoint = (TextView) findViewById(R.id.red_point);
        String unReadCount = ImMessageUtil.getInstance().getAllUnReadMsgCount();
        setUnReadCount(unReadCount);
    }


    /**
     * 设置AppBarLayout滑动监听
     */
    public void setAppBarLayoutListener(MainAppBarLayoutListener appBarLayoutListener) {
        mAppBarLayoutListener = appBarLayoutListener;
    }

    /**
     * 设置AppBarLayout展开监听
     */
    public void setAppBarExpandListener(MainAppBarExpandListener appBarExpandListener) {
        mAppBarExpandListener = appBarExpandListener;
    }

    @Override
    public void loadData() {
        if (mViewPager != null) {
            loadPageData(mViewPager.getCurrentItem());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowed() && mPaused) {
            loadData();
        }
        mPaused = false;
    }


    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppBarLayoutListener = null;
        mAppBarExpandListener = null;
        super.onDestroy();
    }

    public void setCurrentPage(int position) {
        if (mViewPager == null) {
            return;
        }
        if (mViewPager.getCurrentItem() == position) {
            loadPageData(position);
        } else {
            mViewPager.setCurrentItem(position, false);
        }
    }

    /**
     * 显示未读消息
     */
    public void setUnReadCount(String unReadCount) {
        if (mRedPoint != null) {
            if ("0".equals(unReadCount)) {
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mRedPoint.getVisibility() != View.VISIBLE) {
                    mRedPoint.setVisibility(View.VISIBLE);
                }
//                mRedPoint.setText(unReadCount);
            }
        }
    }

    protected abstract void loadPageData(int position);

    protected abstract int getPageCount();

    protected abstract String[] getTitles();


    public void setCurrentOne(){
        if(mViewPager!=null){
            mViewPager.setCurrentItem(1);
        }
    }
}
