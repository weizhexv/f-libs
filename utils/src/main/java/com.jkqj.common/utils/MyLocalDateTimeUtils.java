package com.jkqj.common.utils;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Date;

/**
 * 自定义本地日期时间工具类
 *
 * @author cb
 * @date 2021-12-16
 */
@Slf4j
public final class MyLocalDateTimeUtils {

    private static String DAY_FORMAT = "yyyy-MM-dd";
    private static String MONTH_DAY_FORMAT = "M月d日";
    private static String MONTH_DAY_HOUR_FORMAT = "M月d日H点";
    private static String MONTH_DAY_BLANK_HOUR_MINUTE_FORMAT = "M月d日 H点m分";
    private static String INT_DATE_FORMAT = "yyyyMMdd";
    private static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static String MILLI_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static String DAY_HOUR_MINUTE_FORMAT = "yyyy-MM-dd HH:mm";
    private static String MONTH_DAY_HOUR_MINUTE_FORMAT = "M月d日HH:mm";
    private static String HOUR_MINUTE = "HH:mm";
    private static String YEAR_MONTH = "yyyy-MM";

    private static DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern(DAY_FORMAT);
    private static DateTimeFormatter monthDayFormatter = DateTimeFormatter.ofPattern(MONTH_DAY_FORMAT);
    private static DateTimeFormatter monthDayBlankHourMinuteFormatter = DateTimeFormatter.ofPattern(MONTH_DAY_BLANK_HOUR_MINUTE_FORMAT);
    private static DateTimeFormatter intDateFormatter = DateTimeFormatter.ofPattern(INT_DATE_FORMAT);
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
    private static DateTimeFormatter milliTimeFormatter = DateTimeFormatter.ofPattern(MILLI_TIME_FORMAT);
    private static DateTimeFormatter dayHourMinuteFormatter = DateTimeFormatter.ofPattern(DAY_HOUR_MINUTE_FORMAT);
    private static DateTimeFormatter monthDayHourMinuteFormatter = DateTimeFormatter.ofPattern(MONTH_DAY_HOUR_MINUTE_FORMAT);
    private static DateTimeFormatter hourMinuteFormatter = DateTimeFormatter.ofPattern(HOUR_MINUTE);
    private static DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern(YEAR_MONTH);
    private static final DateTimeFormatter ISO8601Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");

    private static final String[] WEEKS = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    public static String getWeekDayName(LocalDate localDate) {
        return WEEKS[localDate.getDayOfWeek().get(ChronoField.DAY_OF_WEEK)];
    }

    public static String getDayOfWeekName(LocalDateTime dateTime) {
        return WEEKS[getDayOfWeek(dateTime)];
    }

    public static int getDayOfWeek(LocalDateTime dateTime) {
        return dateTime.get(ChronoField.DAY_OF_WEEK);
    }

    public static int getWeekOfMonth(LocalDateTime dateTime) {
        return dateTime.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
    }

    public static LocalDateTime dayFormatAtStart(String dateTime) {
        return LocalDateTime.parse(dateTime + " 00:00:00.000", milliTimeFormatter);
    }

    public static LocalDateTime dayFormatAtEnd(String dateTime) {
        return LocalDateTime.parse(dateTime + " 23:59:59.999", milliTimeFormatter);
    }

    public static LocalDateTime timeFormat(String dateTime) {
        return LocalDateTime.parse(dateTime, timeFormatter);
    }

    public static LocalDateTime milliTimeFormat(String dateTime) {
        return LocalDateTime.parse(dateTime, milliTimeFormatter);
    }

    public static LocalDateTime dayHourMinuteFormat(String dateTime) {
        return LocalDateTime.parse(dateTime, dayHourMinuteFormatter);
    }

    public static String dayFormat(LocalDateTime dateTime) {
        if (dateTime == null) {
            log.warn("dateTime is null!");
            return null;
        }
        return dayFormatter.format(dateTime);
    }

    public static String monthDayBlankHourMinuteFormat(LocalDateTime dateTime) {
        if (dateTime == null) {
            log.warn("dateTime is null!");
            return null;
        }
        return monthDayBlankHourMinuteFormatter.format(dateTime);
    }

    public static String monthDayFormat(LocalDateTime dateTime) {
        if (dateTime == null) {
            log.warn("dateTime is null!");
            return null;
        }
        return monthDayFormatter.format(dateTime);
    }

    public static String dayHourMinuteFormat(LocalDateTime dateTime) {
        if (dateTime == null) {
            log.warn("dateTime is null!");
            return null;
        }
        return dayHourMinuteFormatter.format(dateTime);
    }

    public static String monthDayHourMinuteFormat(LocalDateTime dateTime) {
        if (dateTime == null) {
            log.warn("dateTime is null!");
            return null;
        }
        return monthDayHourMinuteFormatter.format(dateTime);
    }

    public static String hourMinuteFormat(LocalDateTime dateTime) {
        if (dateTime == null) {
            log.warn("dateTime is null!");
            return null;
        }
        return hourMinuteFormatter.format(dateTime);
    }

    public static String yearMonthFormat(LocalDateTime dateTime) {
        if (dateTime == null) {
            log.warn("dateTime is null!");
            return null;
        }
        return yearMonthFormatter.format(dateTime);
    }

    public static String dayFormat(LocalDateTime dateTime, String format) {
        if (dateTime == null) {
            log.warn("dateTime is null!");
            return null;
        }
        return DateTimeFormatter.ofPattern(format).format(dateTime);
    }

    public static String timeFormat(LocalDateTime dateTime) {
        if (dateTime == null) {
            log.warn("dateTime is null");
            return null;
        }
        return timeFormatter.format(dateTime);
    }

    public static String milliTimeFormat(LocalDateTime dateTime) {
        if (dateTime == null) {
            log.warn("dateTime is null");
            return null;
        }
        return milliTimeFormatter.format(dateTime);
    }

    public static int intDateFormat(LocalDateTime dateTime) {
        if (dateTime == null) {
            log.warn("dateTime is null");
            return 0;
        }
        return Integer.parseInt(intDateFormatter.format(dateTime));
    }

    public static LocalDateTime withTimeStartAtDay(LocalDateTime dateTime) {
        return dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public static LocalDateTime withTimeEndAtDay(LocalDateTime dateTime) {
        return dateTime.withHour(23).withMinute(59).withSecond(59).withNano(999);
    }

    public static LocalDateTime getTodayStart() {
        return withTimeStartAtDay(LocalDateTime.now());
    }

    public static LocalDateTime getTodayEnd() {
        return withTimeEndAtDay(LocalDateTime.now());
    }

    /**
     * 获取到毫秒级时间戳
     *
     * @param localDateTime 具体时间
     * @return long 毫秒级时间戳
     */
    public static long toEpochMilli(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 毫秒级时间戳转 LocalDateTime
     *
     * @param epochMilli 毫秒级时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime ofEpochMilli(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.of("+8"));
    }

    /**
     * 比较两个时间类型
     *
     * @param first
     * @param second
     * @return -1 first 小于 second, 1 first 大于 second, 0 first = second
     */
    public static int compare(LocalDateTime first, LocalDateTime second) {
        if (first.isAfter(second)) {
            return 1;
        }
        if (first.isBefore(second)) {
            return -1;
        }
        return 0;
    }

    /**
     * 毫秒时间戳转化格式为ISO-8601字符串
     *
     * @param epochMilli 毫秒时间戳
     * @return 固定格式的时间字符串
     */
    public static String formatISO8601(long epochMilli) {
        LocalDateTime localDateTime = ofEpochMilli(epochMilli);

        return ISO8601Formatter.format(ZonedDateTime.of(localDateTime, ZoneId.of("+8")));
    }

    /**
     * 转换成Date
     *
     * @param localDateTime 具体时间
     * @return date日期
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            log.warn("dateTime is null");
            return null;
        }

        return new Date(toEpochMilli(localDateTime));
    }

    public static String convertToStandDateFormat(String str) {
        if (StringUtils.isBlank(str)) {
            return "";
        }

        str = StringUtils.removeEnd(str, "年");
        str = StringUtils.removeEnd(str, "月");
        str = StringUtils.removeEnd(str, "日");

        String[] arr = StringUtils.split(str.trim(), "/.-年月");

        return Lambdas.join(Lists.newArrayList(arr), s -> s.length() > 1 ? s : "0" + s, "-");
    }

}