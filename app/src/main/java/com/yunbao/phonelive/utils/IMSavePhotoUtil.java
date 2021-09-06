package com.yunbao.phonelive.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.yunbao.common.bean.AdBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class IMSavePhotoUtil {

    public static final   String path = Environment.getExternalStorageDirectory().getPath() + "/jianxin/";
    /**
     * 将图片(url)保存到本地相册
     * http:/132.232.122.151:8888/group1/system/image/2020/06/11/a62406a9c7cebc8811c390ec2dcba7fc.jpg
     */
    public static void saveUrlList(final Context context,List<AdBean> datas) {
        if(datas==null&&datas.size()==0){
            return;
        }
        for (int i = 0; i < datas.size(); i++) {
            saveUrlToPhoto(context,datas.get(i).getUrl());
        }
    }
    public static void saveUrlToPhoto(final Context context, final String imgUrl) {
        if (TextUtils.isEmpty(imgUrl)) {
            Log.e("----------","未获取到图片");
            return;
        }
        String filename=imgUrl.substring(imgUrl.lastIndexOf("/")+1,imgUrl.length());
        new Thread(new Runnable() {
            @Override
            public void run() {

                File picdir = new File(path);
                if (!picdir.exists()) {
                    picdir.mkdirs();
                }
                File file = new File(path, filename);
                if(file!=null && file.exists()){
//                    Log.e("----------","图片已经保存，不需要保存了");
                    return;
                }
                try {
                    // 保存图片
                    URL url = new URL(imgUrl);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(1000 * 6);
                    if (con.getResponseCode() == 200) {
                        InputStream inputStream = con.getInputStream();
                        byte[] b = getBytes(inputStream);
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        fileOutputStream.write(b);
                        fileOutputStream.close();
                    } else {
                        return;
                    }
//                    Log.e("----------","图片保存成功"+filename);
                } catch (Exception e) {
//                    Log.e("----------","图片保存失败"+e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 将InputStream，转换为字节
     */
    public static byte[] getBytes(InputStream inputStream) throws Exception {
        byte[] b = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int len = -1;
        while ((len = inputStream.read(b)) != -1) {
            byteArrayOutputStream.write(b, 0, len);
        }
        byteArrayOutputStream.close();
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
}
