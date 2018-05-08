package cn.njmeter.intelligenthydrant.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cn.njmeter.intelligenthydrant.R;

/**
 * 时间工具类
 * Created by LiYuliang on 2017/9/22 0022.
 *
 * @author LiYuliang
 * @version 2018/02/01
 */
public class TimeUtils {

    private static final long ONE_MINUTE_MILLIONS = 60 * 1000;
    private static final long ONE_HOUR_MILLIONS = 60 * ONE_MINUTE_MILLIONS;
    private static final long ONE_DAY_MILLIONS = 24 * ONE_HOUR_MILLIONS;

    /**
     * 判断用户的设备时区是否为东八区（中国） 2014年7月31日
     *
     * @return 判断是否是东八区
     */
    public static boolean isInEasternEightZones() {
        boolean defaultVaule = true;
        defaultVaule = TimeZone.getDefault() == TimeZone.getTimeZone("GMT+08");
        return defaultVaule;
    }

    /**
     * 根据不同时区，转换时间 2014年7月31日
     *
     * @param date    日期
     * @param oldZone 旧时区
     * @param newZone 新时区
     * @return 时间
     */
    public static Date transformTime(Date date, TimeZone oldZone, TimeZone newZone) {
        Date finalDate = null;
        if (date != null) {
            int timeOffset = oldZone.getOffset(date.getTime()) - newZone.getOffset(date.getTime());
            finalDate = new Date(date.getTime() - timeOffset);
        }
        return finalDate;
    }

    /**
     * 得到系统当前时间
     *
     * @return 当前时间（毫秒值）
     */
    public static String getCurrentTimeMillis() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 得到系统当前年月日时分秒
     *
     * @return 当前时间（年月日时分秒）
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    /**
     * 得到系统当前年月日时分秒
     *
     * @return 当前时间（年月日时分秒）
     */
    public static String getCurrentFormatDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    /**
     * 得到系统当前时分秒
     *
     * @return 当前时间（时分秒）
     */
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    /**
     * 得到系统当前时分秒结尾带空格
     *
     * @return 当前时间（时分秒）
     */
    public static String getCurrentTimeWithSpace() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss ", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    /**
     * 得到系统当前日期
     *
     * @return 当前时间（年月日）
     */
    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    /**
     * 获取当前日期是星期几
     *
     * @return 返回当前日期是星期几
     */
    public static String getCurrentWeekOfDate() {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 获取某个日期是星期几
     *
     * @param date 日期
     * @return 返回该日期是星期几
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 获取短时间格式
     *
     * @param dateStr "2016-01-06T09:37:04"
     * @return
     */
    public static String getShortTime(String dateStr) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        try {
            Date date = sdf.parse(dateStr);
            str = getShortTime(date.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return str;
    }

    /**
     * 获取短时间格式
     *
     * @return
     */
    public static String getShortTime(long millis) {
        Date date = new Date(millis);
        Date curDate = new Date();
        String str = "";
        long durTime = curDate.getTime() - date.getTime();
        int dayStatus = calculateDayStatus(date, new Date());
        if (durTime <= 10 * ONE_MINUTE_MILLIONS) {
            str = "刚刚";
        } else if (durTime < ONE_HOUR_MILLIONS) {
            str = durTime / ONE_MINUTE_MILLIONS + "分钟前";
        } else if (dayStatus == 0) {
            str = durTime / ONE_HOUR_MILLIONS + "小时前";
        } else if (dayStatus == -1) {
            str = "昨天" + DateFormat.format("HH:mm", date);
        } else if (isSameYear(date, curDate) && dayStatus < -1) {
            str = DateFormat.format("MM-dd", date).toString();
        } else {
            str = DateFormat.format("yyyy-MM", date).toString();
        }


//        if (durTime <= 10 * ONE_MINUTE_MILLIONS) {
//            str = "刚刚";
//        } else if (durTime < ONE_HOUR_MILLIONS) {
//            str = durTime / ONE_MINUTE_MILLIONS + "分钟前";
//        } else if (durTime < ONE_HOUR_MILLIONS * 24) {
//            str = durTime / ONE_HOUR_MILLIONS + "小时前";
//        } else {
//            Date date = new Date(millis);
//            str = DateFormat.format("MM-dd HH:mm", date) + "";
//        }
        return str;
    }

    /**
     * @param dateStr "2016-01-06T09:37:04"
     * @return
     */
    public static Date getDate(String dateStr) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            Date date = sdf.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return
     */
    public static String toDateStr(Date date) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取时间倒计时
     *
     * @param dateStr "2016-01-06T09:37:04"
     * @return
     */
    public static String getTimeDown(String dateStr) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        try {
            Date date = sdf.parse(dateStr);
            Date curDate = new Date();

            long durTime = curDate.getTime() - date.getTime();
            int dayStatus = calculateDayStatus(date, curDate);

            if (durTime <= 10 * ONE_MINUTE_MILLIONS) {
                str = "刚刚";
            } else if (durTime < ONE_HOUR_MILLIONS) {
                str = durTime / ONE_MINUTE_MILLIONS + "分钟前";
            } else if (dayStatus == 0) {
                str = durTime / ONE_HOUR_MILLIONS + "小时前";
            } else if (dayStatus == -1) {
                str = "昨天" + DateFormat.format("HH:mm", date);
            } else if (isSameYear(date, curDate) && dayStatus < -1) {
                str = DateFormat.format("MM-dd", date).toString();
            } else {
                str = DateFormat.format("yyyy-MM", date).toString();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return str;
    }

    public static String getCurrentTimeDown(long currentTimeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        Date curDate = new Date(currentTimeMillis);
        String str = formatter.format(curDate);
        return getTimeDown(str);
    }

    /**
     * 返回 2016.1.1
     *
     * @return
     */
    public static String getTimePoint(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        SimpleDateFormat tosdf = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
        try {
            Date date = sdf.parse(dateStr);
            return tosdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 生成时间  时:分:秒
     *
     * @param position
     * @return
     */
    public static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds);
        }
    }

    /**
     * 生成时间  时:分'秒''
     *
     * @param position
     * @return
     */
    public static String generateTimeFormatte(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d’%02d’%02d”", hours, minutes, seconds);
        } else if (minutes > 0) {

            return String.format(Locale.US, "%02d’%02d”", minutes, seconds);
        } else {
            return String.format(Locale.US, "%02d”", seconds);
        }
    }

    /**
     * 生成时间  秒''
     *
     * @param position
     * @return
     */
    public static String generateSecFormatte(long position) {
        int totalSeconds = (int) (position / 1000);
        return String.format(Locale.US, "%02d”", totalSeconds);
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(String str1) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        Date one;
        String distanceTime = "-1";
        try {
            one = df.parse(str1);
            Date now = new Date();
            long time1 = one.getTime();
            long nowTime = now.getTime();
            long diff;
            if (time1 > nowTime) {
                diff = time1 - nowTime;
            } else {
                return "-1";
            }
            long day = 0;
            long hour = 0;
            long min = 0;
            long sec = 0;
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            distanceTime = day + "天" + hour + "小时" + min + "分" + sec + "秒";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return distanceTime;

    }

    public static boolean isSameYear(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarYear = tarCalendar.get(Calendar.YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comYear = compareCalendar.get(Calendar.YEAR);

        return tarYear == comYear;
    }

    public static int calculateDayStatus(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarDayOfYear = tarCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comDayOfYear = compareCalendar.get(Calendar.DAY_OF_YEAR);

        return tarDayOfYear - comDayOfYear;
    }

    /**
     * "2016-01-06T09:37:04"
     *
     * @param time
     * @return
     */
    public static String getFormatTime(String time) {
        String str = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        try {
            Date date = sdf.parse(time);
            str = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 返回 0小时0分
     *
     * @param timeMillis 毫秒
     * @return
     */
    public static String getTimeString(long timeMillis) {
        long minutes = timeMillis / ONE_MINUTE_MILLIONS;
        if (minutes < 60) {
            return minutes + "分钟";
        } else {
            long remainder = minutes % 60;
            long hour = minutes / 60;
            if (remainder == 0) {

                return hour + "小时";
            } else {
                return hour + "小时" + remainder + "分";
            }
        }
    }

    /**
     * 根据月日判断星座
     *
     * @param month
     * @param day
     * @return int
     */
    public static String getConstellation(int month, int day) {

        final String[] constellationArr = {"魔羯座", "水瓶座", "双鱼座", "牡羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座"};

        final int[] constellationEdgeDay = {20, 18, 20, 20, 20, 21, 22, 22, 22, 22, 21, 21};
        if (day <= constellationEdgeDay[month - 1]) {
            month = month - 1;
        }
        if (month >= 0) {
            return constellationArr[month];
        }
        //default to return 魔羯
        return constellationArr[11];

    }

    /**
     * 根据出生日期获得年龄
     *
     * @param birthDay
     * @return
     */
    public static int getAge(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) {
            throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
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
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }

    public static String getFormatTime(long timeMin) {
        int min = (int) (timeMin / ONE_MINUTE_MILLIONS);
        StringBuilder time = new StringBuilder();
        //有分钟
        time.append(min > 0 ? min + ":" : "");
        int sec = (int) (timeMin - min * ONE_MINUTE_MILLIONS) / 1000;
        time.append(min > 0 ? sec + "" : sec + "秒");
        return time.toString();
    }

    public static String zeroTime(int time) {
        return time < 10 ? "0" + time : time + "";
    }

    /**
     * 获取某一天是星期几
     *
     * @param date 日期
     * @return 返回星期
     */
    public static String getDayOfWeek(String date) {
        String dayOfWeek = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                dayOfWeek = "星期日";
                break;
            case Calendar.MONDAY:
                dayOfWeek = "星期一";
                break;
            case Calendar.TUESDAY:
                dayOfWeek = "星期二";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeek = "星期三";
                break;
            case Calendar.THURSDAY:
                dayOfWeek = "星期四";
                break;
            case Calendar.FRIDAY:
                dayOfWeek = "星期五";
                break;
            case Calendar.SATURDAY:
                dayOfWeek = "星期六";
                break;
            default:
                break;
        }
        return dayOfWeek;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param date1 较小的时间
     * @param date2 较大的时间
     * @return 相差天数
     */
    public static int getDaysBetween(String date1, String date2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d1);
            long time1 = cal.getTimeInMillis();
            cal.setTime(d2);
            long time2 = cal.getTimeInMillis();
            long days = (time2 - time1) / (1000 * 3600 * 24);
            return Math.abs(Integer.parseInt(String.valueOf(days)));
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 判断两个日期的月份差
     *
     * @param bt 日期1
     * @param et 日期2
     * @return 月份差，返回-1表示异常
     */
    public static int getMonthCount(String bt, String et) {
        int count = -1;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(sdf.parse(bt));
            c2.setTime(sdf.parse(et));
            count = c2.get(Calendar.YEAR) * 12 + c2.get(Calendar.MONTH) - c1.get(Calendar.YEAR) * 12 - c1.get(Calendar.MONTH);
            count = Math.abs(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 弹出日期选择框Dialog并设置日期和星期
     *
     * @param tv 文本控件
     */
    public static void selectData(Context context, TextView tv) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dataPickerDialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(year, monthOfYear, dayOfMonth);
            String date = DateFormat.format("yyyy-MM-dd", calendar).toString();
            tv.setText(String.format(context.getString(R.string.date), date));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        DatePicker datePicker = dataPickerDialog.getDatePicker();
        datePicker.setMaxDate(new Date(System.currentTimeMillis()).getTime());
        dataPickerDialog.show();
    }

    /**
     * 获取指定日期所在月份的第一天
     *
     * @param date 指定日期，必须为 yyyy-MM-dd 格式
     * @return 所在月第一天
     */
    public static String getFirstDayOfMonth(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return DateFormat.format("yyyy-MM-dd", calendar).toString();
    }

    /**
     * 获取指定日期一个月前的日期
     *
     * @param date 指定日期，必须为 yyyy-MM-dd 格式
     * @return 一个月前的日期
     */
    public static String getLastMonthDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //月份减1
        calendar.add(Calendar.MONTH, -1);
        return DateFormat.format("yyyy-MM-dd", calendar).toString();
    }

    /**
     * 获取指定日期前一天的日期
     *
     * @param date 指定日期，必须为 yyyy-MM-dd 格式
     * @return 前一天的日期
     */
    public static String getLastDayDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //天数减1
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return DateFormat.format("yyyy-MM-dd", calendar).toString();
    }

    /**
     * 获取指定日期的中国日历表达形式
     *
     * @param date 指定日期，必须为 yyyy-MM-dd 格式
     * @return 转换为的中国日历表达方式 yyyy年MM月dd日
     */
    public static String getChineseDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateFormat.format("yyyy年MM月dd日", calendar).toString();
    }

    /**
     * 获取指定日期在一年的第几周
     *
     * @param date 指定日期，必须为 yyyy-MM-dd 格式
     * @return 在当年的周数
     */
    public static int getWeekNumber(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取用于文件名字的当前时间
     * 由于文件名字不能出现空格，所以格式化的时候均以"-"连接
     *
     * @return 当前时间
     */
    public static String getCurrentTimeForFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
}
