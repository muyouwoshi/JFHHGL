package com.juxin.predestinate.module.logic.baseui.picker.picker;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.utils.TypeConvertUtil;
import com.juxin.predestinate.module.logic.baseui.picker.widget.WheelView;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 日期选择器<br>
 * <li><b>使用示例：</b></li>
 * <pre>
 *     <code>
 * DatePicker datePicker = new DatePicker(this);
 * datePicker.setTitleText("出生日期");
 * datePicker.setRange(1965, 2016);//添加起止时间。如果不主动设置，默认为从1960-当前年份
 * datePicker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {});
 * datePicker.show();
 *     </code>
 * </pre>
 */
public class DatePicker extends WheelPicker {

    /* 年月日 */
    public static final int YEAR_MONTH_DAY = 0;
    /* 年月 */
    public static final int YEAR_MONTH = 1;
    /* 月日 */
    public static final int MONTH_DAY = 2;
    private ArrayList<String> years = new ArrayList<String>();
    private ArrayList<String> months = new ArrayList<String>();
    private ArrayList<String> days = new ArrayList<String>();
    private OnDatePickListener onDatePickListener;
    private String yearLabel = "年", monthLabel = "月", dayLabel = "日";
    private int selectedYearIndex = 0, selectedMonthIndex = 0, selectedDayIndex = 0;
    private int mode = YEAR_MONTH_DAY;

    /**
     * 安卓开发应避免使用枚举类（enum），因为相比于静态常量enum会花费两倍以上的内存。
     *
     * @link http ://developer.android.com/training/articles/memory.html#Overhead
     */
    @IntDef(flag = false, value = {YEAR_MONTH_DAY, YEAR_MONTH, MONTH_DAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    /**
     * Instantiates a new Date picker.
     *
     * @param activity the activity
     */
    public DatePicker(FragmentActivity activity) {
        this(activity, YEAR_MONTH_DAY);
    }

    /**
     * Instantiates a new Date picker.
     *
     * @param activity the activity
     * @param mode     the mode
     * @see #YEAR_MONTH_DAY #YEAR_MONTH_DAY#YEAR_MONTH_DAY
     * @see #YEAR_MONTH #YEAR_MONTH#YEAR_MONTH
     * @see #MONTH_DAY #MONTH_DAY#MONTH_DAY
     */
    public DatePicker(FragmentActivity activity, @Mode int mode) {
        super(activity);
        this.mode = mode;

        int currentYear = TimeUtil.getInspectYear();
        for (int i = currentYear - 60; i <= currentYear - 18; i++) { // 年龄限制 18~60岁
            years.add(String.valueOf(i));
        }
        for (int i = 1; i <= 12; i++) {
            months.add(TimeUtil.fillZero(i));
        }
        for (int i = 1; i <= 31; i++) {
            days.add(TimeUtil.fillZero(i));
        }
        setSelectedItem(currentYear, 1, 1);
    }

    /**
     * Sets label.
     *
     * @param yearLabel  the year label
     * @param monthLabel the month label
     * @param dayLabel   the day label
     */
    public void setLabel(String yearLabel, String monthLabel, String dayLabel) {
        this.yearLabel = yearLabel;
        this.monthLabel = monthLabel;
        this.dayLabel = dayLabel;
    }

    /**
     * Sets range.
     *
     * @param startYear the start year
     * @param endYear   the end year
     */
    public void setRange(int startYear, int endYear) {
        years.clear();
        for (int i = startYear; i <= endYear; i++) {
            years.add(String.valueOf(i));
        }
        setSelectedItem(TimeUtil.getInspectYear(), 1, 1);
    }

    private int findItemIndex(ArrayList<String> items, int item) {
        //折半查找有序元素的索引
        int index = Collections.binarySearch(items, item, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                String lhsStr = lhs.toString();
                String rhsStr = rhs.toString();
                lhsStr = lhsStr.startsWith("0") ? lhsStr.substring(1) : lhsStr;
                rhsStr = rhsStr.startsWith("0") ? rhsStr.substring(1) : rhsStr;
                return Integer.parseInt(lhsStr) - Integer.parseInt(rhsStr);
            }
        });
        if (index < 0) {
            index = 0;
        }
        return index;
    }

    /**
     * Sets selected item.
     *
     * @param year  the year
     * @param month the month
     * @param day   the day
     */
    public void setSelectedItem(int year, int month, int day) {
        if (year <= 0 || month <= 0 || day <= 0) return;

        selectedYearIndex = findItemIndex(years, year);
        selectedMonthIndex = findItemIndex(months, month);
        selectedDayIndex = findItemIndex(days, day);
    }

    /**
     * Sets selected item.
     *
     * @param yearOrMonth the year or month
     * @param monthOrDay  the month or day
     */
    public void setSelectedItem(int yearOrMonth, int monthOrDay) {
        if (mode == MONTH_DAY) {
            selectedMonthIndex = findItemIndex(months, yearOrMonth);
            selectedDayIndex = findItemIndex(days, monthOrDay);
        } else {
            selectedYearIndex = findItemIndex(years, yearOrMonth);
            selectedMonthIndex = findItemIndex(months, monthOrDay);
        }
    }

    /**
     * Sets on date pick listener.
     *
     * @param listener the listener
     */
    public void setOnDatePickListener(OnDatePickListener listener) {
        this.onDatePickListener = listener;
    }

    @Override
    @NonNull
    protected View makeCenterView() {
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        int padding = UIUtil.dip2px(activity, 15);
        layout.setPadding(0, padding, 0, padding);
        layout.setGravity(Gravity.CENTER);

        //创建年滑动选区
        WheelView yearView = new WheelView(activity.getBaseContext());
        yearView.setLayoutParams(new LinearLayout.LayoutParams(UIUtil.dip2px(activity, 80), WRAP_CONTENT));
        yearView.setTextSize(textSize);
        yearView.setTextColor(textColorNormal, textColorFocus);
        yearView.setLineVisible(lineVisible);
        yearView.setLineColor(lineColor);
        yearView.setOffset(offset);
        layout.addView(yearView);
        TextView yearTextView = new TextView(activity);
        yearTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        yearTextView.setTextSize(textSize);
        yearTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(yearLabel)) {
            yearTextView.setText(yearLabel);
        }
        layout.addView(yearTextView);

        //创建月滑动选区
        WheelView monthView = new WheelView(activity.getBaseContext());
        monthView.setLayoutParams(new LinearLayout.LayoutParams(UIUtil.dip2px(activity, 80), WRAP_CONTENT));
        monthView.setTextSize(textSize);
        monthView.setTextColor(textColorNormal, textColorFocus);
        monthView.setLineVisible(lineVisible);
        monthView.setLineColor(lineColor);
        monthView.setOffset(offset);
        layout.addView(monthView);
        TextView monthTextView = new TextView(activity);
        monthTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        monthTextView.setTextSize(textSize);
        monthTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(monthLabel)) {
            monthTextView.setText(monthLabel);
        }
        layout.addView(monthTextView);

        //创建日滑动选区
        final WheelView dayView = new WheelView(activity.getBaseContext());
        dayView.setLayoutParams(new LinearLayout.LayoutParams(UIUtil.dip2px(activity, 80), WRAP_CONTENT));
        dayView.setTextSize(textSize);
        dayView.setTextColor(textColorNormal, textColorFocus);
        dayView.setLineVisible(lineVisible);
        dayView.setLineColor(lineColor);
        dayView.setOffset(offset);
        layout.addView(dayView);
        TextView dayTextView = new TextView(activity);
        dayTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        dayTextView.setTextSize(textSize);
        dayTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(dayLabel)) {
            dayTextView.setText(dayLabel);
        }
        layout.addView(dayTextView);

        //显示模式及联动逻辑处理
        if (mode == YEAR_MONTH) {
            dayView.setVisibility(View.GONE);
            dayTextView.setVisibility(View.GONE);
        } else if (mode == MONTH_DAY) {
            yearView.setVisibility(View.GONE);
            yearTextView.setVisibility(View.GONE);
        }
        if (mode != MONTH_DAY) {//年数参与的时候需要判断是否闰年
            yearView.setItems(years, selectedYearIndex);
            yearView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                    selectedYearIndex = selectedIndex;
                    //需要根据年份及月份动态计算天数
                    int maxDays = TimeUtil.calculateDaysInMonth(stringToYearMonthDay(item), stringToYearMonthDay(months.get(selectedMonthIndex)));
                    resetDays(maxDays, dayView);
                }
            });
        }
        monthView.setItems(months, selectedMonthIndex);
        monthView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                selectedMonthIndex = selectedIndex;
                if (mode != YEAR_MONTH) {
                    //年月日或年月模式下，需要根据年份及月份动态计算天数
                    int maxDays = TimeUtil.calculateDaysInMonth(stringToYearMonthDay(years.get(selectedYearIndex)), stringToYearMonthDay(item));
                    resetDays(maxDays, dayView);
                }
            }
        });
        if (mode != YEAR_MONTH) {
            dayView.setItems(days, selectedDayIndex);
            dayView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                    selectedDayIndex = selectedIndex;
                }
            });
        }
        return layout;
    }

    /**
     * 年或月变化时重置日期items
     *
     * @param maxDays 计算月的天数
     * @param dayView 日期控件
     */
    private void resetDays(int maxDays, WheelView dayView) {
        days.clear();
        for (int i = 1; i <= maxDays; i++) {
            days.add(TimeUtil.fillZero(i));
        }
        if (selectedDayIndex >= maxDays) {
            //年或月变动时，保持之前选择的日不动：如果之前选择的日是之前年月的最大日，则日自动为该年月的最大日
            selectedDayIndex = days.size() - 1;
        }
        dayView.setItems(days, selectedDayIndex);
    }

    /**
     * @return 将字符串格式的年月日转化为int
     */
    private int stringToYearMonthDay(String text) {
        if (text.startsWith("0")) {
            //截取掉前缀0以便转换为整数
            text = text.substring(1);
        }
        return TypeConvertUtil.toInt(text);
    }

    @Override
    protected void onSubmit() {
        super.onSubmit();
        if (onDatePickListener != null) {
            String year = getSelectedYear();
            String month = getSelectedMonth();
            String day = getSelectedDay();
            switch (mode) {
                case YEAR_MONTH:
                    ((OnYearMonthPickListener) onDatePickListener).onDatePicked(year, month);
                    break;
                case MONTH_DAY:
                    ((OnMonthDayPickListener) onDatePickListener).onDatePicked(month, day);
                    break;
                default:
                    ((OnYearMonthDayPickListener) onDatePickListener).onDatePicked(year, month, day);
                    break;
            }
        }
    }

    /**
     * Gets selected year.
     *
     * @return the selected year
     */
    public String getSelectedYear() {
        return years.get(selectedYearIndex);
    }

    /**
     * Gets selected month.
     *
     * @return the selected month
     */
    public String getSelectedMonth() {
        return months.get(selectedMonthIndex);
    }

    /**
     * Gets selected day.
     *
     * @return the selected day
     */
    public String getSelectedDay() {
        return days.get(selectedDayIndex);
    }

    /**
     * The interface On date pick listener.
     */
    protected interface OnDatePickListener {
    }

    /**
     * The interface On year month day pick listener.
     */
    public interface OnYearMonthDayPickListener extends OnDatePickListener {

        /**
         * On date picked.
         *
         * @param year  the year
         * @param month the month
         * @param day   the day
         */
        void onDatePicked(String year, String month, String day);
    }

    /**
     * The interface On year month pick listener.
     */
    public interface OnYearMonthPickListener extends OnDatePickListener {

        /**
         * On date picked.
         *
         * @param year  the year
         * @param month the month
         */
        void onDatePicked(String year, String month);
    }

    /**
     * The interface On month day pick listener.
     */
    public interface OnMonthDayPickListener extends OnDatePickListener {

        /**
         * On date picked.
         *
         * @param month the month
         * @param day   the day
         */
        void onDatePicked(String month, String day);
    }
}
