package com.sung.demo.democollections.CalendarSign.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.sung.demo.democollections.CalendarSign.view.CalendarView;

import java.util.Calendar;
import java.util.List;


/**
 * Title: 以当前月份分割，前100个月和后100个月的viewpager日历适配器
 * Description:
 *
 * @author liu_yuwu
 * @date 2016/1/27.
 */
public class TopViewPagerAdapter extends PagerAdapter {

    private List<View> viewLists;
    private int count = 3;
    private Context context;

    private int year;
    private int month;
    private int day;

    /**
     *
     */
    private int initPageIndex;

    /**
     * 是否第一次进入
     */
    boolean isFirstIn = true;

    public TopViewPagerAdapter(Context context, List<View> lists, int INIT_PAGER_INDEX, Calendar calendar) {
        this.context = context;
        viewLists = lists;
        count = lists.size();
        initPageIndex = INIT_PAGER_INDEX;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public int getCount() {                                                                 //获得size
        return initPageIndex * 2 + 1;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View child = viewLists.get(position % count);
        CalendarView calendarView = (CalendarView) child;
        if (calendarView != null) {
            if (isFirstIn) {
                isFirstIn = false;
            } else {
                calendarView.refreshView(context, position - initPageIndex, year, month);
                viewLists.remove(child);
                viewLists.add(position % count,calendarView);
                container.removeView(child);
            }
            container.addView(calendarView);
        }
        return calendarView;
    }

    /**
     * @param position
     * @return
     */
    public CalendarView getChildView(int position) {
        if (viewLists != null && viewLists.size() > 0) {
            return (CalendarView) viewLists.get(position);
        }
        return null;
    }

}