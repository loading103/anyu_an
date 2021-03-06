package com.yunbao.beauty.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.yunbao.beauty.R;

/**
 * Created by Anko on 2019-09-05.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public enum TiMakeupType {
    BLUSHER_MAKEUP(R.string.blusher, R.drawable.ic_ti_blusher_normal),
    EYELASH_MAKEUP(R.string.eyelash, R.drawable.ic_ti_eyelash_normal),
    EYEBROW_MAKEUP(R.string.eyebrow, R.drawable.ic_ti_eyebrow_normal);

    private int stringId;
    private int imageId;

    TiMakeupType(@StringRes int stringId, @DrawableRes int imageId) {
        this.stringId = stringId;
        this.imageId = imageId;
    }

    public String getString(@NonNull Context context) {
        return context.getResources().getString(stringId);
    }

    public Drawable getImageDrawable(@NonNull Context context) {
        return context.getResources().getDrawable(imageId);
    }
}
