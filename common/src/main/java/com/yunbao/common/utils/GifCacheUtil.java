package com.yunbao.common.utils;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.interfaces.CommonCallback;

import java.io.File;

/**
 * Created by cxf on 2018/10/17.
 */

public class GifCacheUtil {

    public static void getFile(String fileName, String url, final CommonCallback<File> commonCallback) {
        if (commonCallback == null) {
            return;
        }
        File dir = new File(CommonAppConfig.GIF_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        if (file.exists()) {
            L.e("gif礼物"+"----------->缓存存在");
            commonCallback.callback(file);
        } else {
            L.e("gif礼物"+"----------->下载2");
            DownloadUtil downloadUtil = new DownloadUtil();
            downloadUtil.download(CommonHttpConsts.DOWNLOAD_GIF, dir, fileName, url, new DownloadUtil.Callback() {
                @Override
                public void onSuccess(File file) {
                    commonCallback.callback(file);
                }

                @Override
                public void onProgress(int progress) {
                    L.e("gif礼物"+"----------->下载中："+progress);
                }

                @Override
                public void onError(Throwable e) {
                    commonCallback.callback(null);
                }
            });
        }
    }

}
