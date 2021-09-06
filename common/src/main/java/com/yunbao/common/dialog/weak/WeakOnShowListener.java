package com.yunbao.common.dialog.weak;

import android.content.DialogInterface;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2020/9/16.
 * Describe:防止内存泄漏
 */
public class WeakOnShowListener implements DialogInterface.OnShowListener {
    private WeakReference<DialogInterface.OnShowListener> mRef;

    public WeakOnShowListener(DialogInterface.OnShowListener real) {
        this.mRef = new WeakReference<>(real);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        DialogInterface.OnShowListener real = mRef.get();
        if (real != null) {
            real.onShow(dialog);
        }
    }
}
