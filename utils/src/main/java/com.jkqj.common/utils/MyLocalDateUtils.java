package com.jkqj.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * 自定义本地日期工具类
 *
 * @author cb
 * @date 2021-12-16
 */
@Slf4j
public final class MyLocalDateUtils {

    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String YYYY_MM = "yyyy-MM";
    private static final String YYYY = "yyyy";
    private static final String NOW = "至今";
    public static final LocalDate NOW_LOCAL_DATE = LocalDate.of(2999, 12, 31);

    private static final DateTimeFormatter FORMATTER_YYYY_MM_DD = DateTimeFormatter.ofPattern(YYYY_MM_DD);

    public static LocalDate smartParse(@Nonnull String date) {
        int length = date.length();
        if (length == YYYY_MM_DD.length()) {
            return LocalDate.parse(date, FORMATTER_YYYY_MM_DD);
        }
        if (length == YYYY_MM.length()) {
            return LocalDate.parse(date + "-01", FORMATTER_YYYY_MM_DD);
        }
        if (length == YYYY.length()) {
            return LocalDate.parse(date + "-01-01", FORMATTER_YYYY_MM_DD);
        }
        throw new IllegalArgumentException("日期格式错误");
    }

    public static LocalDate dayFormatStringToDate(String date) {
        return LocalDate.parse(date, FORMATTER_YYYY_MM_DD);
    }

    public static String dayFormatDateToString(LocalDate date) {
        if (date == null) {
            log.warn("date is null!");
            return null;
        }
        return FORMATTER_YYYY_MM_DD.format(date);
    }

    /**
     * 毫秒级时间戳转LocalDate
     *
     * @param epochMilli 毫秒级时间戳
     * @return LocalDate
     */
    public static LocalDate ofEpochMilli(long epochMilli) {
        return LocalDate.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.of("+8"));
    }

    /**
     * LocalDate转毫秒级时间戳
     *
     * @param localDate LocalDate
     * @return 毫秒级时间戳
     */
    public static long toEpochMilli(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneOffset.of("+8")).toInstant().toEpochMilli();
    }

    /**
     * 解析成localDate对象 年-月-日 (日默认补01)
     *
     * @param dateStr 年-月
     * @return
     */
    public static LocalDate parseYearMonth2LocalDate(String dateStr) {
        return LocalDate.parse(dateStr + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 格式化 年-月
     *
     * @param localDate localDate
     * @return
     */
    public static String formatLocalDateYearMonth(LocalDate localDate) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String format = df.format(localDate);
        return format.substring(0, format.length() - 3);
    }

    /**
     * 比较两个时间类型
     *
     * @param first
     * @param second
     * @return -1 first 小于 second, 1 first 大于 second, 0 first = second
     */
    public static int compare(LocalDate first, LocalDate second) {
        if (first.isAfter(second)) {
            return 1;
        }
        if (first.isBefore(second)) {
            return -1;
        }
        return 0;
    }

    /**
     * 转化开始时间统一为 yyyy-MM-dd 格式
     *
     * @param startDateStr 简历解析的开始时间
     * @return 转化后的开始时间
     */
    public static LocalDate parseStartDate(String startDateStr) {
        try {
            int length = startDateStr.length();
            if (length == YYYY.length()) {
                return LocalDate.parse(startDateStr + "-01-01", FORMATTER_YYYY_MM_DD);
            }

            if (length == YYYY_MM.length()) {
                return LocalDate.parse(startDateStr + "-01", FORMATTER_YYYY_MM_DD);
            }

            // 解析结果是 yyyy-M这种,需要手动补0
            if (length == 6) {
                String[] strArr = startDateStr.split("-");
                if (strArr[0].length() == 4 && strArr.length > 1 && strArr[1].length() == 1) {
                    return LocalDate.parse(strArr[0] + "-0" + strArr[1] + "-01");
                }
            }

            return LocalDate.parse(startDateStr, FORMATTER_YYYY_MM_DD);
        } catch (Exception e) {
            log.error("parseStartDate失败,e={}", e.getMessage());
        }
        return null;
    }

    /**
     * 转化结束时间统一为 yyyy-MM-dd 格式
     *
     * @param endDateStr 简历解析的结束时间
     * @return 转化后的结束时间
     */
    public static LocalDate parseEndDate(String endDateStr) {
        try {
            if (NOW.equals(endDateStr)) {
                return NOW_LOCAL_DATE;
            }

            int length = endDateStr.length();
            if (length == YYYY.length()) {
                return LocalDate.parse(endDateStr + "-12-31", FORMATTER_YYYY_MM_DD);
            }

            if (length == YYYY_MM.length()) {
                return LocalDate.parse(endDateStr + "-31", FORMATTER_YYYY_MM_DD);
            }

            // 解析结果是 yyyy-M这种,需要手动补0
            if (length == 6) {
                String[] strArr = endDateStr.split("-");
                if (strArr[0].length() == 4 && strArr.length > 1 && strArr[1].length() == 1) {
                    return LocalDate.parse(strArr[0] + "-0" + strArr[1] + "-31");
                }
            }

            return LocalDate.parse(endDateStr, FORMATTER_YYYY_MM_DD);
        } catch (Exception e) {
            log.error("parseEndDate失败,e={}", e.getMessage());
        }
        return null;
    }


    /**
     * 获取两个日期相差的年和月
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public static String getTerm(LocalDate startDate, LocalDate endDate) {
        String term = "";
        try {
            int startYear = startDate.getYear();
            int startMonth = startDate.getMonthValue();
            int startDay = startDate.getDayOfMonth();
            int endYear = endDate.getYear();
            int endMonth = endDate.getMonthValue();
            int endDay = endDate.getDayOfMonth();
            long y = ChronoUnit.YEARS.between(startDate, endDate);                    //获取两个日期间隔年
            long m = ChronoUnit.MONTHS.between(startDate, endDate);                   //获取两个日期间隔月

            int lastDayOfEndDate = endDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth(); //获取某个月的最后一天
            if (startYear == endYear) {
                if (startDay == endDay || lastDayOfEndDate == endDay) {
                    m = endMonth - startMonth;
                }
            } else {
                if (m >= 12) {
                    m = m - y * 12;
                }
            }
            term = (y == 0 ? "" : y + "年") + (m == 0 ? "" : +m + "个月");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return term;
    }
}