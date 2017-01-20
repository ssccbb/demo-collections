package com.sung.demo.democollections.CalendarSign;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sung.demo.democollections.CalendarSign.adapter.TopViewPagerAdapter;
import com.sung.demo.democollections.CalendarSign.utils.DateBean;
import com.sung.demo.democollections.CalendarSign.utils.OtherUtils;
import com.sung.demo.democollections.CalendarSign.view.CalendarView;
import com.sung.demo.democollections.CalendarSign.view.ContainerLayout;
import com.sung.demo.democollections.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Title:  scrollview内容的日历
 * Description:
 *
 * @author liu_yuwu
 * @date 2016/1/21.
 */
public class CalendarScrollViewActivity extends Activity {


    private ContainerLayout container;
    private TextView txToday;
    private ViewPager vpCalender;
    private ScrollView viewContent;

    private List<View> calenderViews = new ArrayList<>();


    /**
     * 日历向左或向右可翻动的天数
     */
    private int INIT_PAGER_INDEX = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_scrollview);

        Log.d("ContainerLayout", "开始初始化");
        container = (ContainerLayout) findViewById(R.id.container);
        txToday = (TextView) findViewById(R.id.tx_today);
        vpCalender = (ViewPager) findViewById(R.id.vp_calender);
        viewContent = (ScrollView) findViewById(R.id.view_content);
        initCalendar();
    }

    private void initCalendar() {
        Calendar calendar = Calendar.getInstance();
        txToday.setText(OtherUtils.formatDate(calendar.getTime()));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < 3; i++) {
            CalendarView calendarView = new CalendarView(CalendarScrollViewActivity.this, i, year, month);
            calendarView.setOnCalendarClickListener(new OnMyCalendarClickerListener());
            if (i == 0) {
                container.setRowNum(calendarView.getColorDataPosition() / 7);
            }
            calenderViews.add(calendarView);
        }
        final TopViewPagerAdapter adapter = new TopViewPagerAdapter(this, calenderViews, INIT_PAGER_INDEX, calendar);
        vpCalender.setAdapter(adapter);
        vpCalender.setCurrentItem(INIT_PAGER_INDEX);
        vpCalender.addOnPageChangeListener(new OnMyViewPageChangeListener());


        //@TODO 这里设置延时，因为立即设置界面还没有绘制完成，考虑添加绘制完成监听
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                initEventDays((CalendarView) adapter.getChildView(0));
            }
        }, 1000);
    }

    /**
     *
     * @param calendarView
     */
    private void initEventDays(CalendarView calendarView) {
        //设置含有事件的日期 1-9号
        List<String> eventDays = new ArrayList<>();//根据实际情况调整，传入时间格式(yyyy-MM)
        for(int j=0;j<10;j++) {
            eventDays.add(calendarView.getCurrentDay()+"-0"+j);
        }
        calendarView.setEventDays(eventDays);
    }

    /**
     * 日期滚动时回调
     */
    class OnMyViewPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

            CalendarView calendarView = (CalendarView) calenderViews.get(position % 3);
            txToday.setText(calendarView.getCurrentDay());
            container.setRowNum(0);
            calendarView.initFirstDayPosition(0);

            //设置含有事件的日期 1-9号
            initEventDays(calendarView);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 点击某个日期回调
     */
    class OnMyCalendarClickerListener implements CalendarView.OnCalendarClickListener {
        @Override
        public void onCalendarClick(int position, DateBean dateBean) {
            txToday.setText(OtherUtils.formatDate(dateBean.getDate()));
        }
    }
}
