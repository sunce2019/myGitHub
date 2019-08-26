package com.pay.util;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by frank on 2017/10/9.
 */
public class DateUtils {


    private final static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
    private final static SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat sdfDays = new SimpleDateFormat("yyyyMMdd");
    private final static SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static SimpleDateFormat sdfTimes = new SimpleDateFormat("yyyyMMddHHmmss");
    private final static SimpleDateFormat sdfMd = new SimpleDateFormat("MMdd");





    /**
     * 获取ddHH格式
     * @return
     */
    @Test
    public static String getSdfMd() {
        return sdfMd.format(new Date());
    }

    /**
     * 获取yyyyMMddHHmmss格式
     * @return
     */
    public static String getSdfTimes() {
        return sdfTimes.format(new Date());
    }

    public static String getSdfTimes(Date date){
        return sdfTimes.format(date);
    }


    /**
     * 获取YYYY格式
     * @return
     */
    public static String getYear() {
        return sdfYear.format(new Date());
    }

    /**
     * 获取YYYY-MM-DD格式
     * @return
     */
    public static String getDay() {
        return sdfDay.format(new Date());
    }

    public static String getDay(Date date){
        return sdfDay.format(date);
    }

    /**
     * 获取YYYYMMDD格式
     * @return
     */
    public static String getDays(){
        return sdfDays.format(new Date());
    }

    /**
     * 获取YYYY-MM-DD HH:mm:ss格式
     * @return
     */
    public static String getTime() {
        return sdfTime.format(new Date());
    }

    /**
     * @Title: compareDate
     * @Description:
     * @param s
     * @param e
     * @return boolean
     * @throws
     * @author fh
     */
    public static boolean compareDate(String s, String e) {
        if(fomatDate(s)==null||fomatDate(e)==null){
            return false;
        }
        return fomatDate(s).getTime() >=fomatDate(e).getTime();
    }

    /**
     * 格式化日期
     * @return
     */
    public static Date fomatDate(String date) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return fmt.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 校验日期是否合法
     * @return
     */
    public static boolean isValidDate(String s) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            fmt.parse(s);
            return true;
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            return false;
        }
    }

    /**
     * @param startTime
     * @param endTime
     * @return
     */
    public static int getDiffYear(String startTime,String endTime) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            //long aa=0;
            int years=(int) (((fmt.parse(endTime).getTime()-fmt.parse(startTime).getTime())/ (1000 * 60 * 60 * 24))/365);
            return years;
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            return 0;
        }
    }

    /**
     * <li>功能描述：时间相减得到天数
     * @param beginDateStr
     * @param endDateStr
     * @return
     * long
     * @author Administrator
     */
    public static long getDaySub(String beginDateStr,String endDateStr){
        long day=0;
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date beginDate = null;
        java.util.Date endDate = null;

        try {
            beginDate = format.parse(beginDateStr);
            endDate= format.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);
        //System.out.println("相隔的天数="+day);

        return day;
    }

    /**
     * 得到n天之后的日期
     * @param days
     * @return
     */
    public static String getAfterDayDate(int days) {

        Calendar canlendar = Calendar.getInstance(); // java.util包
        canlendar.add(Calendar.DATE, days); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();

        SimpleDateFormat sdfd = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdfd.format(date);

        return dateStr;
    }

    /**
     * 得到n天之后是周几
     * @param days
     * @return
     */
    public static String getAfterDayWeek(String days) {
        int daysInt = Integer.parseInt(days);
        Calendar canlendar = Calendar.getInstance(); // java.util包
        canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        String dateStr = sdf.format(date);
        return dateStr;
    }
    /**
     * 得到一个日期的开始和结束时刻
     * @param d 一个Date
     * @param b true则返回这个date的零点，false则返回24点少一秒
     * @return
     */
    public static Date getToDate(Date d,boolean b){
        Calendar date = Calendar.getInstance();
        date.setTime(d);
        if(b){
            date.set(Calendar.AM_PM,Calendar.AM);
            date.set(Calendar.HOUR,00);
            date.set(Calendar.MINUTE,00);
            date.set(Calendar.SECOND,00);
        }else{
            date.set(Calendar.AM_PM,Calendar.PM);
            date.set(Calendar.HOUR,11);
            date.set(Calendar.MINUTE,59);
            date.set(Calendar.SECOND,59);
        }
        return date.getTime();
    }

    /**
     * getDate2Str
     *
     * 取得某日期时间的特定表示格式的字符串
     *
     * @param format
     *            时间格式
     * @param date
     *            某日期（Date）
     * @return 某日期的字符串 123
     *
     */
    public static String getDate2Str(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        if (date == null) {
            return "";
        }
        simpleDateFormat.applyPattern(format);
        return simpleDateFormat.format(date);
    }

    public static String getDate2yymmddHHmmStr(Date date) {
        if (date == null) {
            date = new Date();
        }
        return getDate2Str("yyyyMMddHHmm", date);
    }

    /**
     *
     * getStrToDate
     *
     * 将特定格式的时间字符串转化为Date类型
     *
     * @param format
     *            时间格式
     * @param str
     *            某日期的字符串
     * @return 某日期（Date）
     */
    private static  Date getStrToDate(String format, String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern(format);
        ParsePosition parseposition = new ParsePosition(0);
        return simpleDateFormat.parse(str, parseposition);
    }

    /**
     * getDate2SStr
     *
     * 将日期转换为长字符串（包含：年-月-日 时:分:秒）
     *
     * @param date
     *            日期
     * @return 返回型如：yyyy-MM-dd HH:mm:ss 的字符串
     */
    public static String getDate2SStr(Date date) {
        return getDate2Str("yyyy-MM-dd HH:mm:ss", date);
    }

    public static String getDate2ymdStr(Date date) {
        if (date == null) {
            return "";
        }
        return getDate2Str("yyyy-MM-dd", date);
    }

    /**
     * getDate2yymmddStr
     *
     * @param date
     * @return
     */
    public static String getDate2yymmddStr(Date date) {
        if (date == null) {
            return "";
        }
        return getDate2Str("yyyyMMdd", date);
    }

    /**
     * getDate2yymmddHHmmssStr
     *
     * @param date
     * @return
     */
    public static  String getDate2yymmddHHmmssStr(Date date) {
        if (date == null) {
            return "";
        }
        return getDate2Str("yyyyMMddHHmmss", date);
    }

    /**
     *
     * getStr2SDate
     *
     * 将某指定的字符串转换为型如：yyyy-MM-dd HH:mm:ss的时间
     *
     * @param str
     *            将被转换为Date的字符串
     * @return 转换后的Date
     *
     */
    public static Date getStr2SDate(String str) {
        return getStrToDate("yyyy-MM-dd HH:mm:ss", str);
    }

    /**
     * getDateTime2Right
     *
     * 设置日志的默认时间，本函数是返回默认时间= 23:59:59 if(para==null) return null
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String getDateTime2Right(String str) {
        str += " 23:59:59";
        if (!isDateTime(str))
            return null;

        return str;
    }

    /**
     * isDateTime
     *
     * 检测字符串是否为日期
     *
     * @param dateTime
     *
     * @return
     */
    private static boolean isDateTime(String dateTime) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        ParsePosition pos = new ParsePosition(0);
        Date dt = df.parse(dateTime, pos);

        if (dt == null)
            return false;
        return true;
    }


    //-----------------------------------------------------------------------
    /**
     *
     * addYears
     *
     * Adds a number of years to a date returning a new object.
     *
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     *
     * @param amount  the amount to add, may be negative
     *
     * @return the new {@code Date} with the amount added
     *
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addYears(Date date, int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a number of months to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addMonths(Date date, int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    //-----------------------------------------------------------------------
    /**
     *
     * addWeeks
     *
     * Adds a number of weeks to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     *
     * @param date
     * @param amount
     * @return
     */
    public static Date addWeeks(Date date, int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * addDays
     *
     * Adds a number of days to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addDays(Date date, int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    //-----------------------------------------------------------------------
    /**
     *
     * addHours
     *
     * Adds a number of hours to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     *
     * @param date
     * @param amount
     * @return
     */
    public static Date addHours(Date date, int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    //-----------------------------------------------------------------------
    /**
     *
     * addMinutes
     *
     * Adds a number of minutes to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     *
     */
    public static Date addMinutes(Date date, int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    //-----------------------------------------------------------------------
    /**
     *
     * addSeconds
     *
     * Adds a number of seconds to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     *
     */
    public static Date addSeconds(Date date, int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    //-----------------------------------------------------------------------
    /**
     *
     * addMilliseconds
     *
     * Adds a number of milliseconds to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     *
     */
    public static Date addMilliseconds(Date date, int amount) {
        return add(date, Calendar.MILLISECOND, amount);
    }

    //-----------------------------------------------------------------------
    /**
     *
     * add
     *
     * Adds to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param calendarField  the calendar field to add to
     * @param amount  the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     *
     */
    private static Date add(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * convert
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String convert(Date date, String dateFormat) {
        if (date == null) {
            return null;
        }
        if (null == dateFormat) {
            dateFormat = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(dateFormat).format(date);
    }
}
