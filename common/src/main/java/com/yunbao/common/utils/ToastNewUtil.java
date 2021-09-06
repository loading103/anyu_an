package com.yunbao.common.utils;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.yunbao.common.CommonAppContext;
import com.yunbao.common.R;

/**
 * Created by cxf on 2017/8/3.
 */

public class ToastNewUtil {
    private static Toast sToast;

    static {
        sToast = makeToast();
    }


    private static Toast makeToast() {
        Toast toast = new Toast(CommonAppContext.sInstance);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = LayoutInflater.from(CommonAppContext.sInstance).inflate(R.layout.view_toast_new, null);
        toast.setView(view);
        return toast;
    }


    public static void show(int res) {
        show(WordUtil.getString(res));
    }

    public static void show(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        sToast.setText(s);
        sToast.show();
    }

}
