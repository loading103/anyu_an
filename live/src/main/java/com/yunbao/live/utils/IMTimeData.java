package com.yunbao.live.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**


 /**
 * created by lbw at 17/3/1
 * 日期工具
 */
public class IMTimeData {
    /**
     * 时间戳转时间
     */
    public static String stampToTime(String stamp,String type) {
        if(type==null){
            type="yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(type);
        long lt = new Long(stamp);
        Date date = new Date(lt);
        String res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 获取当天的起始时间(时间戳)
     */
    public static Long getTodayStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        return todayStart.getTimeInMillis();
    }



    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    /**
     * 返回文字描述的日期
     *
     * @return
     */
//    public static String getTimeFormatText(long time) {
//        long diff =System.currentTimeMillis() - time;
//        long r = 0;
//        if (diff > year) {
//            r = (diff / year);//年前
//            return  stampToTime(time+"","yyyy-MM-dd HH:mm");
//        }
//        if (diff > month) {  //几个月前
//            r = (diff / month);
//            return  stampToTime(time+"","MM-dd HH:mm");
//        }
//        if (diff > day) {   //几天前
//            r = (diff / day);
//            if(r==1){
//                return  "昨天 "+stampToTime(time+"","HH:mm");
//            }else {
//                return  stampToTime(time+"","MM-dd HH:mm");
//            }
//        }
//        if (diff > hour) {   //几小时前
//            r = (diff / hour);
//            return  stampToTime(time+"","HH:mm");
//        }
//        if (diff > minute) {
//            r = (diff / minute);
//            return r + "分钟前";
//        }
//        return "刚刚";
//    }

    public static String getTimeFormatText(long time) {
        long diff =System.currentTimeMillis() - time;
        long r = 0;
        if (diff > year) {
            r = (diff / year);//年前
            return  stampToTime(time+"","更早");
        }
        if (diff > month) {  //几个月前
            r = (diff / month);
            return  stampToTime(time+"","更早");
        }
        if (diff > day) {   //几天前
            r = (diff / day);
            if(r==1){
                return  "昨天";
            }else {
                return  "更早";
            }
        }
        if (diff > hour) {   //几小时前
            r = (diff / hour);
            return  stampToTime(time+"","HH:mm");
        }
        if (diff > minute) {
            r = (diff / minute);
            return  stampToTime(time+"","HH:mm");
        }
        return  stampToTime(time+"","HH:mm");
    }
    public static long[] getDistanceTimes(String str1,String str2){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day=0;
        long hour=0;
        long min=0;
        long sec=0;
        try{
            one=df.parse(str1);
            two=df.parse(str2);
            long time1 =one.getTime();
            long time2 =two.getTime();
            long diff;
            if(time1<time2) {
                diff = time2 - time1;
            }else {
                diff=time1-time2;
            }
            day=diff/(24*60*60*1000);
            hour=(diff/(60*60*1000)-day*24);
            min=((diff/(60*1000))-day*24*60-hour*60);
            sec=(diff/1000-day*24*60*60-hour*60*60-min*60);
        }catch(ParseException e){
            e.printStackTrace();
        }
        long[] times={day,hour,min,sec};
        return times;
    }
}
