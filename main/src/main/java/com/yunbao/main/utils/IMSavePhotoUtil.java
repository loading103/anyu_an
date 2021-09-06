package com.yunbao.main.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;

import com.yunbao.common.CommonAppContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class IMSavePhotoUtil {
    /**
     * 保存drawable下的图片到绝对路径下
     * filename为空  用时间错保存
     */
    public static  Bitmap saveDrawableIcon( int Res) {
        Resources res = CommonAppContext.sInstance.getResources();
        BitmapDrawable d = (BitmapDrawable) res.getDrawable(Res);
        return d.getBitmap();

    }

    public static File getFile(int Res) {
        Bitmap bitmap = saveDrawableIcon(Res);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        File file = new File(Environment.getExternalStorageDirectory() + "/header.jpg");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            int x = 0;
            byte[] b = new byte[1024 * 100];
            while ((x = is.read(b)) != -1) {
                fos.write(b, 0, x);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
