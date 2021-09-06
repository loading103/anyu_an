package com.yunbao.common.utils;

import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import com.yunbao.common.CommonAppContext;

import java.io.File;

/**
 * @author：jianxin 创建时间：2020/9/25
 */
public class IMMemoryUtils {
    /**
     * 获取内存可用空间
     * @return
     */
    public static String getAvailMemory(boolean isTotal) {// 获取android当前可用内存大小
        String totalSizes="";
        String availSizes="";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//有SD卡
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            //内存是分块的 分别获取块的数量和每块的大小
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            long availableBlocks = stat.getAvailableBlocks();//可用内存块的数量

            long totalSize = totalBlocks * blockSize;
            long availSize = availableBlocks * blockSize;

            long hasUsed = totalSize - availSize;

            //格式化
            totalSizes = Formatter.formatFileSize(CommonAppContext.sInstance, totalSize);
            availSizes =Formatter.formatFileSize(CommonAppContext.sInstance, availSize);
        } else {
            //如果没有SD卡
            File path2 = Environment.getDataDirectory();
            StatFs stat2 = new StatFs(path2.getPath());
            long blockSize2 = stat2.getBlockSize();
            long totalBlocks2 = stat2.getBlockCount();
            long availableBlocks2 = stat2.getAvailableBlocks();

            long totalSize2 = totalBlocks2 * blockSize2;
            long availSize2 = availableBlocks2 * blockSize2;
            long hasUsed = totalSize2 - availSize2;
            totalSizes = Formatter.formatFileSize(CommonAppContext.sInstance, totalSize2);
            availSizes = Formatter.formatFileSize(CommonAppContext.sInstance, availSize2);
        }
        if(isTotal){
            return totalSizes;
        }else {
            return availSizes;
        }
    }

}
