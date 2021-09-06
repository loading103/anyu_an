package com.yunbao.beauty.custom;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by cxf on 2018/6/7.
 */

public class SquareImageView2 extends androidx.appcompat.widget.AppCompatImageView {
    public SquareImageView2(Context context) {
        super(context);
    }

    public SquareImageView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
