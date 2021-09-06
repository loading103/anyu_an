package com.yunbao.common.dialog.weak;

import android.content.DialogInterface;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2020/9/16.
 * Describe:防止内存泄漏
 */
public class WeakOnDismissListener implements DialogInterface.OnDismissListener {
    private WeakReference<DialogInterface.OnDismissListener> mRef;

    public WeakOnDismissListener(DialogInterface.OnDismissListener real) {
        this.mRef = new WeakReference<>(real);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        DialogInterface.OnDismissListener real = mRef.get();
        if (real != null) {
            real.onDismiss(dialog);
        }
    }
}