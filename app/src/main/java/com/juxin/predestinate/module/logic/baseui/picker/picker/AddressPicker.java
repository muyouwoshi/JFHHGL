package com.juxin.predestinate.module.logic.baseui.picker.picker;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.juxin.predestinate.bean.center.area.City;
import com.juxin.predestinate.bean.center.area.Province;
import com.juxin.predestinate.module.logic.baseui.picker.widget.WheelView;
import com.juxin.predestinate.module.logic.config.AreaConfig;
import com.juxin.predestinate.module.util.UIUtil;

import java.util.ArrayList;

/**
 * 地址选择器（包括省级、地级、县级）。
 * 地址数据见国家统计局官网（http://www.stats.gov.cn/tjsj/tjbz/xzqhdm）<br>
 * <li><b>使用示例：</b></li>
 * <pre>
 *     <code>
 * AddressPicker addressPicker = new AddressPicker(this, AreaConfig.getInstance().getLimitProvince());
 * addressPicker.setTitleText("居住地");
 * addressPicker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {});
 * addressPicker.show();
 *     </code>
 * </pre>
 */
public class AddressPicker extends LinkagePicker {

    private OnAddressPickListener onAddressPickListener;

    /**
     * Instantiates a new Address picker.
     *
     * @param activity the activity
     * @param data     the data
     */
    public AddressPicker(FragmentActivity activity, ArrayList<Province> data) {
        super(activity);
        int provinceSize = data.size();
        //添加省
        for (int x = 0; x < provinceSize; x++) {
            Province pro = data.get(x);
            firstList.add(pro.getProvinceName());
            ArrayList<City> cities = pro.getCities();
            ArrayList<String> xCities = new ArrayList<String>();
            int citySize = cities.size();
            //添加地市
            for (int y = 0; y < citySize; y++) {
                City cit = cities.get(y);
                xCities.add(cit.getCityName());
            }
            secondList.add(xCities);
        }
    }

    /**
     * Sets selected item.
     *
     * @param province the province
     * @param city     the city
     * @param county   the county
     */
    public void setSelectedItem(String province, String city, String county) {
        super.setSelectedItem(province, city, county);
    }

    /**
     * Sets on address pick listener.
     *
     * @param listener the listener
     */
    public void setOnAddressPickListener(OnAddressPickListener listener) {
        this.onAddressPickListener = listener;
    }

    @Deprecated
    @Override
    public void setOnLinkageListener(OnLinkageListener onLinkageListener) {
        throw new UnsupportedOperationException("Please use setOnAddressPickListener instead.");
    }

    @Override
    @NonNull
    protected View makeCenterView() {
        if (firstList.isEmpty()) {
            throw new IllegalArgumentException("please initial data at first, can't be empty");
        }
        //创建根布局
        LinearLayout layout = new LinearLayout(activity);
        int padding = UIUtil.dip2px(activity,15);
        layout.setPadding(padding, padding, padding, padding);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);

        //创建省滑动选区
        WheelView provinceView = new WheelView(activity);
        int width = (int) ((screenWidthPixels * 0.8 - 3 * padding) / 2);
        provinceView.setLayoutParams(new LinearLayout.LayoutParams(width, WRAP_CONTENT));
        provinceView.setTextSize(textSize);
        provinceView.setTextColor(textColorNormal, textColorFocus);
        provinceView.setLineVisible(lineVisible);
        provinceView.setLineColor(lineColor);
        provinceView.setOffset(offset);
        layout.addView(provinceView);

        //中间的间距view
        View gap = new View(activity);
        gap.setLayoutParams(new LinearLayout.LayoutParams(padding, 1));
        layout.addView(gap);

        //创建市滑动选区
        final WheelView cityView = new WheelView(activity);
        cityView.setLayoutParams(new LinearLayout.LayoutParams(width, WRAP_CONTENT));
        cityView.setTextSize(textSize);
        cityView.setTextColor(textColorNormal, textColorFocus);
        cityView.setLineVisible(lineVisible);
        cityView.setLineColor(lineColor);
        cityView.setOffset(offset);
        layout.addView(cityView);

        //联动逻辑
        provinceView.setItems(firstList, selectedFirstIndex);
        provinceView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                selectedFirstText = item;
                selectedFirstIndex = selectedIndex;
                //根据省份获取地市
                cityView.setItems(secondList.get(selectedFirstIndex), isUserScroll ? 0 : selectedSecondIndex);
            }
        });
        cityView.setItems(secondList.get(selectedFirstIndex), selectedSecondIndex);
        cityView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
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
        if (onAddressPickListener != null) {
            onAddressPickListener.onAddressPicked(AreaConfig.getInstance().getCity(selectedFirstText, selectedSecondText));
        }
    }

    /**
     * The interface On address pick listener.
     */
    public interface OnAddressPickListener {

        /**
         * On address picked.
         *
         * @param city the city
         */
        void onAddressPicked(City city);
    }
}
