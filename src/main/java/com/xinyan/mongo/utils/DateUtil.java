package com.xinyan.mongo.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * 时间与日期的工具类
 *
 * @author wenbin_zhu
 */
@Slf4j
public class DateUtil {

    private static Random random = new Random();

    // ** yyyyMMddHHmmssSSS
    public static final String fm_yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";

    // ** yyyy-MM-dd HH:mm:ss.SSS
    public static final String fm_yyyy_MM_dd_HHmmssSSS = "yyyy-MM-dd HH:mm:ss.SSS";

    // ** yyyyMMddHHmmss
    public static final String fm_yyyyMMddHHmmss = "yyyyMMddHHmmss";

    // ** yyyy-MM-dd HH:mm:ss
    public static final String fm_yyyy_MM_dd_HHmmss = "yyyy-MM-dd HH:mm:ss";

    // ** yyyy-MM-dd HH:mm:ss
    public static final String HHmm = "HH:mm";

    // ** yyyyMMdd
    public static final String fm_yyyyMMdd = "yyyyMMdd";

    // ** yyyyMM
    public static final String fm_yyyyMM = "yyyyMM";

    // ** yyMM
    public static final String fm_yyMM = "yyMM";

    // ** yyyy-MM-dd
    public static final String fm_yyyy_MM_dd = "yyyy-MM-dd";

    // ** yyyy/MM/dd
    public static final String fmx_yyyy_MM_dd = "yyyy/MM/dd";

    // ** yyyy-MM.dd
    public static final String fmp_yyyy_MM_dd = "yyyy-MM.dd";

    // ** yyyy
    public static final String fm_yyyy = "yyyy";

    // ** MM.dd
    public static final String fm_MM_dd = "MM-dd";

    // ** yyyy年MM月dd日
    public static final String cn_yyyyMMdd = "yyyy年MM月dd日";

    // ** MM月dd日
    public static final String cn_MMdd = "MM月dd日";

    public static final String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";

    // ** yyyy-MM-dd HH:mm:ss
    public static final String fm_HHmmss = "HH点mm分ss秒";

    /**
     * 锁对象
     */
    private static final Object lockObj = new Object();

    /**
     * 存放不同的日期模板格式的sdf的Map
     */
    private static Map<String, ThreadLocal<SimpleDateFormat>> dateFormatMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     *
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getDateFormat(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = dateFormatMap.get(pattern);

        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = dateFormatMap.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
                    // System.out.println("put new sdf of pattern " + pattern + " to map");

                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {

                        @Override
                        protected SimpleDateFormat initialValue() {
                            // System.out.println("thread: " + Thread.currentThread() + " init pattern: " + pattern);
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    dateFormatMap.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }

    /**
     * 去除时分秒
     *
     * @param cur 待处理日期
     * @return 只包含年月日的日期
     */
    public static Date parse(Date cur) {
        if (cur == null) {
            return cur;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(cur);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 字符串转成日期
     *
     * @param cur 日期字符串
     * @param fm  format格式
     * @return
     */
    public static Date stringToDate(String cur, String fm) {
        if (StringUtils.isBlank(cur)) {
            return null;
        }
        if (StringUtils.isBlank(fm)) {
            fm = fm_yyyyMMdd;
        }
        try {
            return getDateFormat(fm).parse(cur);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 当前时间转字符串
     *
     * @param fm 格式(DateTimeUtil.fmx_yyyy_MM_dd ...)
     * @return 日期字符
     */
    public static String dateToString(String fm) {
        Date cur = Calendar.getInstance().getTime();
        if (StringUtils.isBlank(fm)) {
            fm = fm_yyyy_MM_dd_HHmmss;
        }
        return getDateFormat(fm).format(cur);
    }

    /**
     * 日期转字符串
     *
     * @param cur 日期
     * @param fm  格式(DateTimeUtil.fmx_yyyy_MM_dd ...)
     * @return 日期字符
     */
    public static String dateToString(Date cur, String fm) {
        if (cur == null) {
            cur = Calendar.getInstance().getTime();
        }
        if (StringUtils.isBlank(fm)) {
            fm = fm_yyyy_MM_dd_HHmmss;
        }
        return getDateFormat(fm).format(cur);
    }

    /**
     * 日期转字符串
     *
     * @param cur 日期
     * @param fm  格式(DateTimeUtil.fmx_yyyy_MM_dd ...)
     * @return 日期字符
     */
    public static String dateToString(Long cur, String fm) {
        if (cur == null) {
            cur = Calendar.getInstance().getTime().getTime();
        }
        if (StringUtils.isBlank(fm)) {
            fm = fm_yyyy_MM_dd_HHmmss;
        }
        return getDateFormat(fm).format(cur);
    }

    /**
     * 得到前面某一天的日期
     *
     * @param cur 当前日期
     * @param num 前天多少天
     * @return 前面某一天的日期
     */
    public static Date getBeginDate(Date cur, int num) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);
        c.add(Calendar.DATE, -num);
        return c.getTime();
    }

    /**
     * 得到后面某一天的日期
     *
     * @param cur 当前日期
     * @param num 后面多少天
     * @return 后面某一天的日期
     */
    public static Date getAfterDate(Date cur, int num) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);
        c.add(Calendar.DATE, num);
        return c.getTime();
    }

    /**
     * 得到前一天的日期
     *
     * @param cur 当前日期
     * @return 前一天日期（昨天）
     */
    public static Date yesterday(Date cur) {
        return getBeginDate(cur, 1);
    }

    /**
     * 得到明天的日期
     *
     * @param cur 当前日期
     * @return 明天日期
     */
    public static Date tomorrow(Date cur) {
        return getAfterDate(cur, 1);
    }

    /**
     * 根据某日期得到当月第一天日期
     *
     * @param cur 日期
     * @return 当月第一天
     */
    public static Date firstDayOfMonth(Date cur) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    /**
     * 根据某日期得到当年第一天日期
     *
     * @param cur 日期
     * @return 当年第一天
     */
    public static Date firstDayOfYear(Date cur) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);
        c.add(Calendar.YEAR, 0);
        c.set(Calendar.DAY_OF_YEAR, 1);
        return c.getTime();
    }

    /**
     * 根据某日期得到上月第一天日期
     *
     * @param cur 日期
     * @return 上月第一天
     */
    public static Date firstDayOfLastMonth(Date cur) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    /**
     * 根据某日期得到当周第一天日期
     *
     * @param cur 日期
     * @return 当周第一天(星期一)
     */
    public static Date firstDayOfWeek(Date cur) {
        Calendar cd = Calendar.getInstance();
        cd.setFirstDayOfWeek(Calendar.MONDAY);// 中国周一为一周内的第一天
        cd.setTime(cur);
        cd.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cd.getTime();
    }

    /**
     * 获取某日期上周第一天日期
     *
     * @param cur 日期
     * @return 上周第一天日期（周一）
     */
    public static Date firstDayOfLastWeek(Date cur) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);// 中国周一为一周内的第一天
        calendar.setTime(cur);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime();
    }

    /**
     * 根据某日期得到上月最后一天日期
     *
     * @param cur 日期
     * @return 上月最后一天
     */
    public static Date lastDayOfLastMonth(Date cur) {
        Calendar a = Calendar.getInstance();
        a.setTime(cur);
        a.add(Calendar.MONTH, -1);// 把日期设置为上个月同一天
        a.set(Calendar.DATE, 1);//把日期设置为该月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        return a.getTime();
    }

    /**
     * 根据某日期得到月最后一天日期
     *
     * @param cur 日期
     * @return 月最后一天
     */
    public static Date lastDayOfMonth(Date cur) {
        Calendar a = Calendar.getInstance();
        a.setTime(cur);
        a.set(Calendar.DATE, 1);//把日期设置为该月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        return a.getTime();
    }


    /**
     * 获取某日期上周最后一天日期
     *
     * @param cur 日期
     * @return 上周最后一天日期（周日）
     */
    public static Date lastDayOfLastWeek(Date cur) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);// 中国周一为一周内的第一天
        calendar.setTime(cur);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return calendar.getTime();
    }

    /**
     * 根据某日期得到上周同一天日期
     *
     * @param cur 日期
     * @return 上周同一天
     */
    public static Date curDayOfLastWeek(Date cur) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);
        c.add(Calendar.WEEK_OF_YEAR, -1);
        return c.getTime();
    }

    /**
     * 根据某日期得到两周前的同一天日期
     *
     * @param cur 日期
     * @return 两周前的同一天
     */
    public static Date curDayOflastTwoWeek(Date cur) {
        return curDayOfLastWeek(curDayOfLastWeek(cur));
    }

    /**
     * 根据某日期得到上个月同一天日期
     *
     * @param cur 日期
     * @return 上个月同一天
     */
    public static Date curDayOfLastMonth(Date cur) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);
        c.add(Calendar.MONTH, -1);
        return c.getTime();
    }

    /**
     * @author wenbin_zhu
     * @Title curDate
     * @Description: 获取当前时间
     */
    public static Date curDate(Date cur) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);
        return c.getTime();
    }

    /**
     * 根据某日期得到上两个月同一天日期
     *
     * @param cur 日期
     * @return 上两个月同一天
     */
    public static Date curDayOfLastTwoMonth(Date cur) {
        return curDayOfLastMonth(curDayOfLastMonth(cur));
    }

    /**
     * 根据某日期得到去年同一天
     *
     * @param cur 日期
     * @return 去年同一天对应的日期
     */
    public static Date curDayOfLastYear(Date cur) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(cur);
        cal.add(Calendar.YEAR, -1);
        return cal.getTime();
    }

    /**
     * 根据某日期得到上一年同一天对应的周一日期
     *
     * @param cur 日期
     * @return 上一年同一天对应的周一日期
     */
    public static Date firstDayOfWeekByLastYear(Date cur) {
        return firstDayOfWeek(curDayOfLastYear(cur));
    }

    /**
     * 判断某日期是不是周一
     *
     * @param cur 当前日期
     * @return true为周一，默认false
     */
    public static boolean curDateIsMonday(Date cur) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(cur);
        if (cd.get(Calendar.DAY_OF_WEEK) == 2) {
            return true;
        }
        return false;
    }

    /**
     * 根据某日期得到年份
     *
     * @param cur 日期
     * @return 年份
     */
    public static int getOfYear(Date cur) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);
        return c.get(Calendar.YEAR);
    }

    /**
     * 根据某日期得到月份
     *
     * @param cur 日期
     * @return 月份
     */
    public static int getOfMonth(Date cur) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);
        return c.get(Calendar.MONTH) + 1;// 月份下标从0开始
    }

    /**
     * 根据某日期得到日期是当月的哪一号（哪一天）
     *
     * @param cur 日期
     * @return 当月第几天
     */
    public static int getDayOfMonth(Date cur) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据某日期得到星期
     *
     * @param cur 日期
     * @return 星期几(1, 2, 3, 4, 5, 6, 7)
     */
    public static int getDayOfWeek(Date cur) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);
        c.setFirstDayOfWeek(Calendar.MONDAY);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 自定义创建一个日期
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 日期
     */
    public static Date createCustomDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DATE, day);
        return c.getTime();
    }

    /**
     * @author wenbin_zhu
     * @Title createCustomDate
     * @Description:创建时分秒自定义时间
     */
    public static Date createCustom(int hour, int min, int sec, Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);
        c.set(Calendar.SECOND, sec);
        return c.getTime();
    }

    /**
     * @author wenbin_zhu
     * @Title createCustomDate
     * @Description:创建时分秒自定义时间
     */
    public static Date createCustom(int hour, int min, int sec, int millsec, Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date == null ? Calendar.getInstance().getTime() : date);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);
        c.set(Calendar.SECOND, sec);
        c.set(Calendar.MILLISECOND, sec);
        return c.getTime();
    }

    /**
     * @author wenbin_zhu
     * @Title getCurHour
     * @Description: 获取当前时间时分秒
     */
    public static int getCurHourMinSecond(int type, Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(type);
    }

    /**
     * 根据开始时间、结束时间得到两个时间段内所有的日期(包含开始日期与结束日期)
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 两个日期之间的日期
     */
    public static List<Date> getDateRangeList(Date start, Date end) {
        ArrayList<Date> ret = new ArrayList<Date>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        Date tmpDate = calendar.getTime();
        long endTime = end.getTime();
        while (tmpDate.before(end) || tmpDate.getTime() == endTime) {
            ret.add(parse(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            tmpDate = calendar.getTime();
        }
        return ret;
    }

    /**
     * 根据开始时间、结束时间得到两个时间段内所有的日期(包含开始日期与结束日期)
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 两个日期之间的天数
     */
    public static int getDateRangeNum(Date start, Date end) {
        ArrayList<Date> ret = new ArrayList<Date>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        Date tmpDate = calendar.getTime();
        long endTime = end.getTime();
        while (tmpDate.before(end) || tmpDate.getTime() == endTime) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            ret.add(parse(calendar.getTime()));
            tmpDate = calendar.getTime();
        }
        return ret.size();
    }

    /**
     * @param cur 英文日期字符,如：{Mar 1, 2013}
     * @return 日期
     * @throws ParseException
     */
    public static Date englishStringToDate(String cur) {
        try {
            // cur = "Sun Nov 13 21:56:41 +0800 2011";
            if (StringUtils.isBlank(cur)) {
                return null;
            }
            return new SimpleDateFormat("MMM dd, yyyy", Locale.US).parse(cur);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 判断是不是在时间区间内：
     *
     * @param cur  要判断的时间
     * @param from 开始时间
     * @param to   结束时间
     * @return true=[from <= cur < to]，false=反之则返回false
     */
    public static boolean isBetween(Date cur, Date from, Date to) {
        if (null == cur || null == from || null == to) {
            return false;
        }
        Calendar _cur = Calendar.getInstance();
        _cur.setTime(cur);

        Calendar c = Calendar.getInstance();
        c.setTime(from);

        int result = _cur.compareTo(c);
        if (result > 0) {
            // 大于开始时间
            c.setTime(to);
            result = _cur.compareTo(c);
            if (result < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是不是在时间区间内：
     *
     * @param cur  要判断的时间
     * @param from 开始时间(含)
     * @param to   结束时间(含)
     * @return true=[from <= cur <= to]，false=反之则返回false
     */
    public static boolean hasIncludeTime(Date cur, Date from, Date to) {
        if (null == cur || null == from || null == to) {
            return false;
        }
        // 如果大于的话返回的是正整数，等于是0，小于的话就是负整数

        Calendar _cur = Calendar.getInstance();
        _cur.setTime(cur);

        Calendar c = Calendar.getInstance();
        c.setTime(from);

        int result = _cur.compareTo(c);
        if (result >= 0) {
            // 大于开始时间
            c.setTime(to);
            result = _cur.compareTo(c);
            if (result <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 前面某一天的日期的开始时间
     *
     * @return
     */
    public static Date getDayBegin(Date cur) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    /**
     * 获得某一天的日期的结束时间
     *
     * @return
     */
    public static Date getDayEnd(Date cur) {
        Calendar c = Calendar.getInstance();
        c.setTime(cur);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    /**
     * @author wenbin_zhu
     * @Title randomTime
     * @Description: 随机生成时间，范围：date-24：00
     */
    public static Date curRandHourMinSec(Date date) {
        int hour = DateUtil.getCurHourMinSecond(Calendar.HOUR_OF_DAY, date);
        int min = DateUtil.getCurHourMinSecond(Calendar.MINUTE, date);
        int sec = DateUtil.getCurHourMinSecond(Calendar.SECOND, date);
        hour = random.nextInt(24 - hour) + hour;
        min = random.nextInt(60 - min) + min;
        sec = random.nextInt(60 - sec) + sec;
        Date c = DateUtil.createCustom(hour, min, sec, date);
        return c;
    }

    /**
     * @author wenbin_zhu
     * @Title randomTime
     * @Description: 随机生成时间，范围：date-24：00
     */
    public static Date cumsDate(int hmax, int hmin, int mmax, int mmin, int smax, int smin, Date date) {
        int h = random.nextInt(hmax - hmin) + hmin;
        if (h >= 20) {
            h = 20;
            mmax = 30;
            mmin = 0;
        }
        int m = random.nextInt(mmax - mmin) + mmin;
        int s = random.nextInt(smax - smin) + smin;
        Date c = DateUtil.createCustom(h, m, s, date);
        return c;
    }

    /**
     * 为日期增加分钟
     *
     * @param curDateType
     * @param amount
     * @return
     */
    public static Date addMinute(Date curDateType, int amount) {
        Calendar cld = Calendar.getInstance();
        if (curDateType == null) {
            curDateType = Calendar.getInstance().getTime();
        }
        cld.setTime(curDateType);
        cld.add(Calendar.MINUTE, amount);
        return cld.getTime();
    }

    /**
     * 为日期增加分钟
     *
     * @param curDate
     * @param amount
     * @return
     */
    public static Date addDate(Date curDate, int field, int amount) {
        Calendar cld = Calendar.getInstance();
        if (curDate == null) {
            curDate = Calendar.getInstance().getTime();
        }
        cld.setTime(curDate);
        cld.add(field, amount);
        return cld.getTime();
    }

    /**
     * @author wenbin_zhu
     * @Title getMaxDayOfYear
     * @Description: 获取最大天数
     */
    public static int getMaxDayOfYear(Date date) {
        Calendar s = Calendar.getInstance();
        s.setTime(date);
        return s.getActualMaximum(Calendar.DAY_OF_YEAR);
    }

    /**
     * @author wenbin_zhu
     * @Title getMaxDayOfYear
     * @Description: 获取最大天数
     */
    public static boolean isToday(Date date) {
        Calendar p = Calendar.getInstance();
        p.setTime(date);
        int pyear = p.get(Calendar.YEAR);
        int pmonth = p.get(Calendar.MONTH);
        int pday = p.get(Calendar.DAY_OF_YEAR);
        p.setTime(Calendar.getInstance().getTime());
        int tyear = p.get(Calendar.YEAR);
        int tmonth = p.get(Calendar.MONTH);
        int tday = p.get(Calendar.DAY_OF_YEAR);
        if (pyear == tyear && pmonth == tmonth && pday == tday) {
            return true;
        }
        return false;
    }

    /**
     * @author wenbin_zhu
     * @Title random
     * @Description:时间内生成随机数
     */
    public static Date randomTime(long begin, long end) {
        long d = (long) (Math.random() * (end - begin));
        long rtn = begin + d;
        if (rtn == begin || rtn == end) {
            return randomTime(begin, end);
        }
        return new Date(rtn);
    }

    /**
     * @author wenbin_zhu
     * @Title randomTimeByRange
     * @Description:时间内生成随机数
     */
    public static Date randomTimeByRange(long begin, long rang) {
        long rtn = begin - (long) (Math.random() * rang);
        if (rtn == begin) {
            return randomTime(begin, rang);
        }
        return new Date(rtn);
    }

    /**
     * @author wenbin_zhu
     * @Title secondDiff
     * @Description: 获取两个时间的秒数
     */
    public static Long secondDiff(Date d1, Date d2) {
        return (d2.getTime() - d1.getTime()) / 1000;
    }

    /**
     * @author wenbin_zhu
     * @Title secondDiff
     * @Description: 获取两个时间的毫秒
     */
    public static Long millisecondDiff(Date d1, Date d2) {
        if (d2 == null) {
            d2 = Calendar.getInstance().getTime();
        }
        return d2.getTime() - d1.getTime();
    }

    /**
     * @author wenbin_zhu
     * @Title secondDiff
     * @Description: 获取两个时间的秒数
     */
    public static Long getCalTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTime().getTime();
    }
}
