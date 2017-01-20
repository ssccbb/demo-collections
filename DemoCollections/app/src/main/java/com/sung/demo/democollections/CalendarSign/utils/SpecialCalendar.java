package com.sung.demo.democollections.CalendarSign.utils;

import android.util.Log;

import java.util.Calendar;

/**
 * 闰年月算法
 *
 * @author chaogege
 */
public class SpecialCalendar {

    public static final int CALENDAR_ITEM_SIZE = 42;
    public int currentPosition = 0;

    public DateBean[] getDateByYearMonth(int year, int month, int jumpMonth) {
        Calendar calCurrent = Calendar.getInstance();
        int[] realYearMonth = getRealYearMonth(year, month, jumpMonth);
        year = realYearMonth[0];
        month = realYearMonth[1];
        //@todo 在这里根据真实的日期获取接口中的事实数据
        DateBean[] dateBeans = new DateBean[CALENDAR_ITEM_SIZE];
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        // 本月总天数
        int daySizeThisMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 本月第一天是周几
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int weekdayOfMonth = cal.get(Calendar.DAY_OF_WEEK) - 1;
        // 上个月总天数
        cal.set(Calendar.MONTH, month - 1);
        int daySizeLastMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);


        int lastMonthDay = 1;
        for (int i = 0; i < CALENDAR_ITEM_SIZE; i++) {
            DateBean dateBean = new DateBean();
            if (i < weekdayOfMonth) {
                // 前一个月
                int day = daySizeLastMonth - weekdayOfMonth + i + 1;
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.add(Calendar.MONTH, -1);
                cal.set(Calendar.DAY_OF_MONTH, day);
                dateBean.setMonthType(DateBean.LAST_MONTH);
            } else if (i < daySizeThisMonth + weekdayOfMonth) {
                if (currentPosition == 0) {
                    currentPosition = i;
                }
                // 本月
                int day = i - weekdayOfMonth + 1;
                if (year == calCurrent.get(Calendar.YEAR) && month == calCurrent.get(Calendar.MONTH) && day == calCurrent.get(Calendar.DAY_OF_MONTH)) {
                    dateBean.setIsCurrentDay(true);
                    currentPosition = i;
                }
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                dateBean.setMonthType(DateBean.CURRENT_MONTH);
            } else {
                // 下一个月
                cal.set(year, month, lastMonthDay);
                cal.add(Calendar.MONTH, 1);
                lastMonthDay++;
                dateBean.setMonthType(DateBean.NEXT_MONTH);
            }
            dateBean.setDate(cal.getTime());
            dateBeans[i] = dateBean;
        }


        for (DateBean dateBean : dateBeans) {
            Log.d("getDateByYearMonth", "year:" + year + "month:" + month + "jumpMonth:" + jumpMonth + "    " + OtherUtils.formatDate(dateBean.getDate()));
        }

        return dateBeans;
    }

    private int[] getRealYearMonth(int year, int month, int jumpMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month + jumpMonth);
        return new int[]{calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH)};
    }

}