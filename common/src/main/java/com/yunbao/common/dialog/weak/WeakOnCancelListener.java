package com.yunbao.common.dialog.weak;

import android.content.DialogInterface;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2020/9/16.
 * Describe:防止内存泄漏
 */
public class WeakOnCancelListener implements DialogInterface.OnCancelListener {
    private WeakReference<DialogInterface.OnCancelListener> mRef;

    public WeakOnCancelListener(DialogInterface.OnCancelListener real) {
        this.mRef = new WeakReference<>(real);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        DialogInterface.OnCancelListener real = mRef.get();
        if (real != null) {
            real.onCancel(dialog);
        }
    }
}