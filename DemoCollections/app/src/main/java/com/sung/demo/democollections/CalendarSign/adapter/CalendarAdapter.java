package com.sung.demo.democollections.CalendarSign.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sung.demo.democollections.CalendarSign.utils.DateBean;
import com.sung.demo.democollections.CalendarSign.utils.OtherUtils;
import com.sung.demo.democollections.CalendarSign.utils.SpecialCalendar;
import com.sung.demo.democollections.R;

import java.util.Calendar;
import java.util.List;

/**
 * Title: 日历gridview中的每一个item显示的textview
 * Description:
 *
 * @author liu_yuwu
 * @date 2016/1/27.
 */
public class CalendarAdapter extends BaseAdapter {
    private Context context;
    private DateBean[] dateBeans = new DateBean[SpecialCalendar.CALENDAR_ITEM_SIZE];
    private SpecialCalendar specialCalendar = new SpecialCalendar();
    private Resources res = null;


    private int colorDataPosition = -1;


    private DateBean firstDateBean;
    private int firstDatePosition;


    public CalendarAdapter(Context context, int jumpMonth, int year_c, int month_c) {
        this.context = context;
        this.res = context.getResources();
        dateBeans = specialCalendar.getDateByYearMonth(year_c, month_c, jumpMonth);
        colorDataPosition = specialCalendar.currentPosition;
    }

    @Override
    public int getCount() {
        return dateBeans.length;
    }

    @Override
    public Object getItem(int position) {
        return dateBeans[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public DateBean getFirstDateBean() {
        return firstDateBean;
    }

    public int getFirstDatePosition() {
        return firstDatePosition;
    }

    boolean hasValued = false;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.calendar_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DateBean dateBean = dateBeans[position];
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateBean.getDate());
        viewHolder.txDate.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        if (DateBean.CURRENT_MONTH == dateBean.getMonthType()) {
            //如果是当前日期
            if (firstDateBean == null) {
                firstDateBean = dateBean;
                firstDatePosition = position;
            }
            if (colorDataPosition == 0 && !hasValued) {
                colorDataPosition = position;
                hasValued = true;
            }
            viewHolder.txDate.setTextColor(res.getColor(R.color.calendar_text_color));
        } else {
            viewHolder.txDate.setTextColor(res.getColor(R.color.calendar_text_color_disable));
        }
        if (firstDateBean != null && colorDataPosition == position) {
            viewHolder.txDate.setBackgroundResource(R.drawable.calendar_item_bg);
        } else if (dateBean.isCurrentDay()) {
            viewHolder.txDate.setBackgroundResource(R.drawable.calendar_item_today_bg);
        } else {
            viewHolder.txDate.setBackgroundColor(res.getColor(android.R.color.transparent));
        }
        if (dateBean.getTag()) {
            viewHolder.imvPoint.setImageResource(R.drawable.calendar_item_point);
        }
        return convertView;
    }


    class ViewHolder {
        TextView txDate;
        ImageView imvPoint;

        public ViewHolder(View convertView) {
            txDate = (TextView) convertView.findViewById(R.id.tx_date);
            imvPoint = (ImageView) convertView.findViewById(R.id.imv_point);
        }
    }


    /**
     * @param position 设置点击的日期的颜色位置
     */
    public void setColorDataPosition(int position) {
        colorDataPosition = position;
        notifyDataSetChanged();
    }

    public void setDateList(List<String> dateList) {
        if (dateList != null && dateList.size() > 0) {
            for (DateBean dateBean : dateBeans) {
                String formatDate = OtherUtils.formatDate(dateBean.getDate(), OtherUtils.DATE_PATTERN_1);
                if (dateList.contains(formatDate)) {
                    dateBean.setTag(true);
                }
            }
            notifyDataSetChanged();
        }
    }

    public int getColorDataPosition() {
        return colorDataPosition;
    }
}
