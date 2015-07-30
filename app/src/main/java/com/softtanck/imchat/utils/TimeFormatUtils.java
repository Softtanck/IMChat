package com.softtanck.imchat.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Tanck
 * @Description 时间转换 增加了之前时间
 * @date Jan 19, 2015 3:34:22 PM
 */
@SuppressLint("SimpleDateFormat")
public class TimeFormatUtils {

    public static final String yyyyMMdd_HHmmss = "yyyy-MM-dd HH:mm:ss";
    public static final String dd = "dd";
    public static final String ahhmm = "ahh:mm";
    public static final String hhmm = "hh:mm";
    public static final String E = "E";
    public static final String E_ahhmm = "E ahh:mm";
    public static final String MMdd = "MM-dd";
    public static final String MMdd_HHmm = "yy-MM-dd HH:mm";
    public static final String YYMMDD = "yy-MM-dd";

    public static String formatRoughly(String time) {
        String result = formatRoughly(parse(time));
        return result != null ? result : time;
    }

    public static String format(String time) {
        String result = format(parse(time));
        return result != null ? result : time;
    }


    public static String parseLongtimeToYear(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return TimeFormatUtils.timeToStr(calendar.getTime());
    }


    /**
     * 在月初或者月末的显示【 昨天 前天 】 逻辑过于复杂 忽略至else逻辑中
     */
    private static String formatRoughly(Date record) {
        try {
            SimpleDateFormat sdfDd = new SimpleDateFormat(dd);
            int recordDay = Integer.parseInt(sdfDd.format(record));
            Date current = new Date(System.currentTimeMillis());
            int currentDay = Integer.parseInt(sdfDd.format(current));
            if (recordDay == currentDay) {
                return new SimpleDateFormat(ahhmm).format(record);
            } else if (currentDay - recordDay == 1) {
                return "昨天";
            } else if (currentDay - recordDay == 2) {
                return "前天";
            } else {
                return new SimpleDateFormat(MMdd).format(record);
            }
        } catch (Exception e) {
//            LogUtils.e("format roughly time fail!");
            return null;
        }
    }

    public static String format(Date record) {
        try {
            SimpleDateFormat sdfDd = new SimpleDateFormat(dd);
            int recordDay = Integer.parseInt(sdfDd.format(record));
            Date current = new Date(System.currentTimeMillis());
            int currentDay = Integer.parseInt(sdfDd.format(current));
            if (recordDay == currentDay) {
                return new SimpleDateFormat(ahhmm).format(record);
            } else if (currentDay - recordDay == 1) {
                return "昨天" + new SimpleDateFormat(hhmm).format(record);
            } else if (currentDay - recordDay == 2) {
                return "前天" + new SimpleDateFormat(hhmm).format(record);
            } else {
                return new SimpleDateFormat(MMdd_HHmm).format(record);
            }
        } catch (Exception e) {
//            LogUtils.e("format time fail!");
            return null;
        }
    }

    private static Date parse(String time) {
        try {
            return new SimpleDateFormat(yyyyMMdd_HHmmss).parse(time);
        } catch (ParseException e) {
//            LogUtils.e("parse [" + yyyyMMdd_HHmmss + "] fail!");
            return new Date();
        }
    }


    /**
     * 日期格式
     */
    private final static ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 时间格式
     */
    private final static ThreadLocal<SimpleDateFormat> timeFormat = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    /**
     * 获取当前时间:Date
     */
    public static Date getDate() {
        return new Date();
    }

    /**
     * 获取当前时间:Calendar
     */
    public static Calendar getCal() {
        return Calendar.getInstance();
    }

    /**
     * 日期转换为字符串:yyyy-MM-dd
     */
    public static String dateToStr(Date date) {
        if (date != null)
            return dateFormat.get().format(date);
        return null;
    }

    /**
     * 时间转换为字符串:yyyy-MM-dd HH:mm:ss
     */
    public static String timeToStr(Date date) {
        if (date != null)
            return timeFormat.get().format(date);
        return null;
    }


    /**
     * 字符串转换为时间:yyyy-MM-dd HH:mm:ss
     */
    public static Date strToTime(String str) {
        Date date = null;
        try {
            date = timeFormat.get().parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 友好的方式显示时间
     */
    public static String friendlyFormat(long xtime) {
        String str = parseLongtimeToYear(xtime);
        Date date = strToTime(str);
        if (date == null)
            return ":)";
        Calendar now = getCal();
        String time = new SimpleDateFormat("HH:mm").format(date);

        // 第一种情况，日期在同一天
        String curDate = dateFormat.get().format(now.getTime());
        String paramDate = dateFormat.get().format(date);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((now.getTimeInMillis() - date.getTime()) / 3600000);
            if (hour > 0)
                return time;
            int minute = (int) ((now.getTimeInMillis() - date.getTime()) / 60000);
            if (minute < 2)
                return "刚刚";
            if (minute > 5)
                return minute + "分钟前";
            if (minute > 10)
                return minute + "分钟前";
            if (minute > 15)
                return minute + "分钟前";
            if (minute > 30)
                return "半个小时以前";
            return minute + "分钟前";
        }

        // 第二种情况，不在同一天
        int days = (int) ((getBegin(getDate()).getTime() - getBegin(date).getTime()) / 86400000);
        if (days == 1)
            return "昨天 " + time;
        if (days == 2)
            return "前天 " + time;
        if (days <= 7)
            return days + "天前";
        return dateToStr(date);
    }

    /**
     * 返回日期的0点:2012-07-07 20:20:20 --> 2012-07-07 00:00:00
     */
    public static Date getBegin(Date date) {
        return strToTime(dateToStr(date) + " 00:00:00");
    }
}
