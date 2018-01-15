package com.juxin.predestinate.module.logic.baseui.picker.picker;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.juxin.predestinate.module.logic.baseui.picker.widget.WheelView;
import com.juxin.predestinate.module.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 区间选择器<br>
 * <li><b>使用示例：</b></li>
 * <pre>
 *     <code>
 * RangePicker rangePicker = new RangePicker(this, InfoConfig.getInstance().getHeight().getShow());
 * rangePicker.setTitleText("身高要求");
 * rangePicker.setOnRangePickListener(new RangePicker.OnRangePickListener() {});
 * rangePicker.show();
 *     </code>
 * </pre>
 */
public class RangePicker extends WheelPicker {

    private List<String> rangeList = new ArrayList<String>();
    private String selectedFirstText = "", selectedSecondText = "";
    private int selectedFirstIndex = 0, selectedSecondIndex = 0;

    /**
     * Instantiates a new Wheel picker.
     *
     * @param activity the activity
     */
    public RangePicker(FragmentActivity activity, List<String> rangeList) {
        super(activity);
        this.rangeList = rangeList;
    }

    /**
     * 设置默认选中条目
     *
     * @param firstText  列表中起始条目的显示文字
     * @param secondText 别表中终止条目的显示文字
     */
    public void setSelectedItem(String firstText, String secondText) {
        if (firstText == null || secondText == null) return;

        for (int i = 0; i < rangeList.size(); i++) {
            String ft = rangeList.get(i);
            if (ft.contains(firstText)) {
                selectedFirstIndex = i;
                break;
            }
        }
        selectedSecondText = secondText;
    }

    @NonNull
    @Override
    protected View makeCenterView() {
        if (rangeList.isEmpty()) {
            throw new IllegalArgumentException("please initial data at first, can't be empty");
        }
        //创建根布局
        LinearLayout layout = new LinearLayout(activity);
        int padding = UIUtil.dip2px(activity,15);
        layout.setPadding(0, padding, 0, padding);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);

        //创建起始选区
        WheelView firstView = new WheelView(activity);
        int width = screenWidthPixels / 3;
        firstView.setLayoutParams(new LinearLayout.LayoutParams(width, WRAP_CONTENT));
        firstView.setTextSize(textSize);
        firstView.setTextColor(textColorNormal, textColorFocus);
        firstView.setLineVisible(lineVisible);
        firstView.setLineColor(lineColor);
        firstView.setOffset(offset);
        layout.addView(firstView);

        //中间的间距view
        View gap = new View(activity);
        gap.setLayoutParams(new LinearLayout.LayoutParams(padding, 1));
        layout.addView(gap);

        //创建终结选区
        final WheelView secondView = new WheelView(activity);
        secondView.setLayoutParams(new LinearLayout.LayoutParams(width, WRAP_CONTENT));
        secondView.setTextSize(textSize);
        secondView.setTextColor(textColorNormal, textColorFocus);
        secondView.setLineVisible(lineVisible);
        secondView.setLineColor(lineColor);
        secondView.setOffset(offset);
        layout.addView(secondView);

        //展示与选中逻辑
        final List<String> secondList = new ArrayList<String>();
        secondList.addAll(rangeList);
        firstView.setItems(rangeList, selectedFirstIndex);
        firstView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                selectedFirstText = item;
                selectedFirstIndex = selectedIndex;

                secondList.clear();
                secondList.add("不限");
                for (int i = selectedIndex + 1; i < rangeList.size(); i++) {
                    secondList.add(rangeList.get(i));
                }
                if (isUserScroll) {
                    secondView.setItems(secondList);
                } else {
                    for (int i = 0; i < secondList.size(); i++) {
                        String ft = secondList.get(i);
                        if (ft.contains(selectedSecondText)) {
                            selectedSecondIndex = i;
                            break;
                        }
                    }
                    secondView.setItems(secondList, selectedSecondIndex);
                }
            }
        });
        secondView.setItems(secondList, selectedSecondIndex);
        secondView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                selectedSecondText = item;
                selectedSecondIndex = selectedIndex;
            }
        });

        return layout;
    }

    @Override
    public void onSubmit() {
        super.onSubmit();
        if (onRangePickListener != null) {
            onRangePickListener.onRangePicked(selectedFirstText, selectedSecondText);
        }
    }

    private OnRangePickListener onRangePickListener;

    /**
     * set range pick listener
     *
     * @param onRangePickListener range pick listener
     */
    public void setOnRangePickListener(OnRangePickListener onRangePickListener) {
        this.onRangePickListener = onRangePickListener;
    }

    /**
     * The interface On address pick listener.
     */
    public interface OnRangePickListener {

        /**
         * On range picked.
         *
         * @param firstText  first checked.
         * @param secondText second checked.
         */
        void onRangePicked(String firstText, String secondText);
    }
}
