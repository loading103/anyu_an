package com.yunbao.common.dialog.weak;

import android.content.DialogInterface;

/**
 * Created by Administrator on 2020/9/16.
 * Describe:防止内存泄漏
 */
public class Weak {
    public static WeakOnCancelListener proxy(DialogInterface.OnCancelListener real) {
        return new WeakOnCancelListener(real);
    }

    public static WeakOnDismissListener proxy(DialogInterface.OnDismissListener real) {
        return new WeakOnDismissListener(real);
    }

    public static WeakOnShowListener proxy(DialogInterface.OnShowListener real) {
        return new WeakOnShowListener(real);
    }

}
