package com.caiyu.lib_base.utils;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    private static final long ONE_MINUTE = 60000L;
//    private static final long ONE_WEEK = 604800000L;

    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";

    public static final String[] constellationArr =
            {"水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};

    public static final int[] constellationEdgeDay = {20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<SimpleDateFormat>();

    /**
     * @param milliseconds the number of milliseconds since Jan. 1, 1970, midnight GMT.
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String format(long milliseconds) {

        Date date = new Date(milliseconds);
        Date now = new Date();

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM-dd HH:mm");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        long delta = now.getTime() - milliseconds;
        if (delta < 0) {
            return dateFormat3.format(date);
        } else if (delta < 1L * ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        } else if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        } else {
            Calendar calendar = Calendar.getInstance();
            long ms = 1000 * (calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND));//毫秒数
            long ms_now = calendar.getTimeInMillis();

            String ret = "";
            if (ms_now - milliseconds < ms) {
                ret = "今天 " + dateFormat1.format(date);
            } else if (ms_now - milliseconds < (ms + 24 * 3600 * 1000)) {
                ret = "昨天 " + dateFormat1.format(date);
            } else if (ms_now - milliseconds < (ms + 24 * 3600 * 1000 * 2)) {
                ret = "前天 " + dateFormat1.format(date);
            } else if (date.getYear() == now.getYear()) {
                ret = dateFormat2.format(date);
            } else {
                ret = dateFormat3.format(date);
            }
            return ret;
        }
    }

    /**
     * 用于聊天
     *
     * @param milliseconds
     * @return
     */
    public static String format1(long milliseconds) {

        Date date = new Date(milliseconds);
        Date now = new Date();

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM-dd");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd");

        long delta = now.getTime() - milliseconds;
        if (delta < 0) {
            return dateFormat3.format(date);
        } else if (delta < 1L * ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        } else if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        } else {
            Calendar calendar = Calendar.getInstance();
            long ms = 1000 * (calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND));//毫秒数
            long ms_now = calendar.getTimeInMillis();

            String ret = "";
            if (ms_now - milliseconds < ms) {
                ret = "今天 " + dateFormat1.format(date);
            } else if (ms_now - milliseconds < (ms + 24 * 3600 * 1000)) {
                ret = "昨天 " + dateFormat1.format(date);
            } else if (ms_now - milliseconds < (ms + 24 * 3600 * 1000 * 2)) {
                ret = "前天 " + dateFormat1.format(date);
            } else if (getYear(date.getTime()) == getYear()) {
                ret = dateFormat2.format(date);
            } else {
                ret = dateFormat3.format(date);
            }
            return ret;
        }
    }

    /**
     * 格式化日期
     *
     * @param unixTime unix
     * @return 日期字符串
     */
    public static String formatYearMonthDate(long unixTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(unixTime * 1000);
        return dateFormat.format(date);

    }

    /**
     * 格式化日期
     *
     * @return 日期字符串
     */
    public static String formatYearMonthDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);

    }

    /**
     * @param milliseconds
     * @return
     * @description: 获取时间年
     * @return: int
     */
    public static int getYear(long milliseconds) {
        Date date = new Date(milliseconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }


    /**
     * 获取当前年份
     *
     * @return
     */
    public static int getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }


    /**
     * 获取当前日
     *
     * @return
     */
    public static int getDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public static int getMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;
    }


    /**
     * @param milliseconds
     * @return
     * @description: 获取时间月
     * @return: int
     */
    public static int getMonth(long milliseconds) {
        Date date = new Date(milliseconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static String getChineseMonth(long milliseconds) {
        String month = "";

        switch (getMonth(milliseconds)) {
            case 1:
                month = "一月";
                break;
            case 2:
                month = "二月";
                break;
            case 3:
                month = "三月";
                break;
            case 4:
                month = "四月";
                break;
            case 5:
                month = "五月";
                break;
            case 6:
                month = "六月";
                break;
            case 7:
                month = "七月";
                break;
            case 8:
                month = "八月";
                break;
            case 9:
                month = "九月";
                break;
            case 10:
                month = "十月";
                break;
            case 11:
                month = "十一月";
                break;
            case 12:
                month = "十二月";
                break;

            default:
                break;
        }

        return month;
    }

    /**
     * @param milliseconds
     * @return
     * @description: 获取时间日
     * @return: int
     */
    public static int getDay(long milliseconds) {
        Date date = new Date(milliseconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @param milliseconds
     * @return
     * @description: 获取时间小时
     * @return: int
     */
    public static int getHour(long milliseconds) {
        Date date = new Date(milliseconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * @param milliseconds
     * @return
     * @description: 获取时间分钟
     * @return: int
     */
    public static int getMinute(long milliseconds) {
        Date date = new Date(milliseconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    public static long toSeconds(long date) {
        return date / 1000L;
    }

    public static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    public static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    public static long toDays(long date) {
        return toHours(date) / 24L;
    }

    public static long dayToMinutes(int day) {
        return day * 24 * 60;
    }

    /**
     * 根据日期获取星座
     *
     * @return
     */
    public static String getConstellation(String strdate) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = sdf.parse(strdate);
        } catch (Exception e) {

        }


        if (date == null) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day < constellationEdgeDay[month]) {
            month = month - 1;
        }
        if (month >= 0) {
            return constellationArr[month];
        }
        // default to return 魔羯
        return constellationArr[11];
    }


    /**
     * 时间字符串转时间戳(指定时间格式)
     *
     * @param time
     * @param format
     * @return
     */
    public static String toTimestamp(String time, String format) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date d;
        try {
            d = sdf.parse(time);
            long l = d.getTime() / 1000;
            re_time = String.valueOf(l);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return re_time;
    }

    /**
     * 时间字符串转时间戳(固定时间格式)
     *
     * @param time
     * @return
     */
    public static String toTimestamp(String time) {
        return toTimestamp(time, "yyyy-MM-dd");
    }


    /**
     * 时间字符串转时间戳(指定时间格式)-当前时间
     *
     * @param format
     * @return
     */
    public static String toCurrentTimestamp(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * 将毫秒时间转换成 mm:ss 的形式
     *
     * @param duration
     * @return
     */
    public static String formatVideoDuration(long duration) {
        Date date = new Date(duration);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(date);
    }

    /**
     * 返回当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     */
    public static boolean isToday(String day) {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = null;
        try {
            date = getDateFormat().parse(day);
        } catch (ParseException e) {
            return false;
        }
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    public static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }

    public static boolean isCloseEnough(long var0, long var2) {
        long var4 = var0 - var2;
        if (var4 < 0L) {
            var4 = -var4;
        }

        return var4 < 30000L;
    }

    @SuppressLint({"DefaultLocale"})
    public static String toTimeBySecond(int var0) {
        int var1 = var0 / 60;
        boolean var2 = false;
        if (var1 >= 60) {
            int var4 = var1 / 60;
            var1 %= 60;
        }

        int var3 = var0 % 60;
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(var1), Integer.valueOf(var3)});
    }

    @SuppressLint({"DefaultLocale"})
    public static String toTime(int var0) {
        int var5 = var0 % 1000;
        var0 /= 1000;
        int var1 = var0 / 60;
        boolean var2 = false;
        if (var1 >= 60) {
            int var4 = var1 / 60;
            var1 %= 60;
        }
        if (var0 == 0 && var5 > 0) {
            var0++;
        }
        int var3 = var0 % 60;
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(var1), Integer.valueOf(var3)});
    }

    /**
     * 获取当前时间 格式为  HH:mm
     * 如 19:11
     *
     * @return
     */
    public static String getCurrentHM() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(System.currentTimeMillis());
    }

    /**
     * 时间戳转成时间格式
     *
     * @param time
     * @return
     */
    public static String timedate(String time, String format) {
        SimpleDateFormat sdr = new SimpleDateFormat(format);
        long lcc = Long.valueOf(time);
        long i = Long.valueOf(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;

    }

    public static String timedate(String time) {
        return timedate(time, "yyyy-MM-dd");
    }


    /**
     * 是否在日期内
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static boolean isTimeValid(long startTime, long endTime) {
        String currentTime = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        long time = Long.valueOf(currentTime);
        if (time > startTime && time < endTime) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * date 转String
     *
     * @return
     */
    public static String dataToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 时间字符串转DATE
     *
     * @param serverTime
     * @param format
     * @return
     */
    public static Date parseServerTime(String serverTime, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINESE);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date date = null;
        try {
            date = sdf.parse(serverTime);
        } catch (Exception e) {
        }
        return date;
    }

    /**
     * 根据日期判断年龄
     *
     * @param birthDay
     * @return
     * @throws Exception
     */

    public static String getAge(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth)
                    age--;
            } else {
                age--;
            }
        }
        return String.valueOf(age);
    }

    /**
     * 将秒转化为 HH:mm:ss 的格式
     *
     * @param time 秒
     * @return
     */
    public static String formatTime(int time) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String hh = decimalFormat.format(time / 3600);
        String mm = decimalFormat.format(time % 3600 / 60);
        String ss = decimalFormat.format(time % 60);

        if (hh.equals("00")) {
            return mm + ":" + ss;
        } else {
            return hh + ":" + mm + ":" + ss;
        }
    }
}
