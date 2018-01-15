package com.juxin.predestinate.module.util;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;
import com.juxin.predestinate.module.logic.baseui.picker.picker.AddressPicker;
import com.juxin.predestinate.module.logic.baseui.picker.picker.DatePicker;
import com.juxin.predestinate.module.logic.baseui.picker.picker.OptionPicker;
import com.juxin.predestinate.module.logic.baseui.picker.picker.RangePicker;
import com.juxin.predestinate.module.logic.config.AreaConfig;
import com.juxin.predestinate.ui.user.check.edit.custom.ContactEditDialog;
import com.juxin.predestinate.ui.user.check.edit.custom.NickEditDialog;

import java.util.List;

/**
 * Dialog工具类
 */
public class PickerDialogUtil {

    /**
     * 单项选择 Dialog
     *
     * @param activity
     * @param data     供选择的数据源
     */
    public static void showOptionPickerDialog(FragmentActivity activity, OptionPicker.OnOptionPickListener listener, List<String> data, String defaultValue, CharSequence title) {
        final OptionPicker optionPicker = new OptionPicker(activity, data);
        optionPicker.setTitleText(title);
        if (!TextUtils.isEmpty(defaultValue))
            optionPicker.setSelectedItem(defaultValue);
        optionPicker.setOnOptionPickListener(listener);
        optionPicker.show();
    }

    /**
     * 区间选择Dialog
     *
     * @param activity
     * @param listener
     * @param data       选择区间数据源
     * @param firstText  起始条目默认值
     * @param secondText 终止条目默认值
     * @param title
     */
    public static void showRangePickerDialog(FragmentActivity activity, RangePicker.OnRangePickListener listener, List<String> data, String firstText, String secondText, CharSequence title) {
        RangePicker rangePicker = new RangePicker(activity, data);
        rangePicker.setTitleText(title);
        rangePicker.setSelectedItem(firstText, secondText);
        rangePicker.setOnRangePickListener(listener);
        rangePicker.show();
    }

    /**
     * 显示日期选择器Dialog 默认年限范围从1960-当前年份
     *
     * @param activity
     * @param listener
     */
    public static void showDatePickerDialog(FragmentActivity activity, DatePicker.OnYearMonthDayPickListener listener, int year, int month, int day) {
        DatePicker datePicker = new DatePicker(activity);
        datePicker.setTitleText("日期");
        datePicker.setSelectedItem(year, month, day);
        datePicker.setOnDatePickListener(listener);
        datePicker.show();
    }

    /**
     * 显示日期选择器Dialog
     *
     * @param activity
     * @param start    选择器开始年份
     * @param end      终止年份
     */
    public static void showDatePickerDialog(FragmentActivity activity, int start, int end, DatePicker.OnYearMonthDayPickListener listener, CharSequence title) {
        DatePicker datePicker = new DatePicker(activity);
        datePicker.setTitleText(title);
        datePicker.setRange(start, end);
        datePicker.setOnDatePickListener(listener);
        datePicker.show();
    }

    /**
     * 显示地区选择框Dialog  有不限选项
     *
     * @param activity
     * @param listener
     */
    public static void showAddressPickerDialog1(FragmentActivity activity, AddressPicker.OnAddressPickListener listener, String proDefalutValue, String cityDefaultValue) {
        showAddressPickerDialog(activity, "居住地", listener, proDefalutValue, cityDefaultValue, true);
    }

    /**
     * 显示地区选择框Dialog 移除不限选项
     *
     * @param activity
     * @param listener
     */
    public static void showAddressPickerDialog2(FragmentActivity activity, AddressPicker.OnAddressPickListener listener, String proDefalutValue, String cityDefaultValue) {
        showAddressPickerDialog(activity, "居住地", listener, proDefalutValue, cityDefaultValue, false);
    }

    /**
     * 显示地区选择框Dialog
     *
     * @param activity
     * @param title
     * @param listener
     * @param limit    true : 有不限选项.  false : 移除不限选项
     */
    public static void showAddressPickerDialog(FragmentActivity activity, CharSequence title, AddressPicker.OnAddressPickListener listener, String proDefalutValue, String cityDefaultValue, boolean limit) {
        AddressPicker addressPicker;
        if (limit) {
            addressPicker = new AddressPicker(activity, AreaConfig.getInstance().getLimitProvince());
        } else {
            addressPicker = new AddressPicker(activity, AreaConfig.getInstance().getCommonProvince());
        }
        addressPicker.setTitleText(title);
        addressPicker.setSelectedItem(proDefalutValue, cityDefaultValue);
        addressPicker.setOnAddressPickListener(listener);
        addressPicker.show();
    }

    /**
     * 文字提示Dialog
     *
     * @param activity
     * @param listener 取消/确定按钮回调
     * @param content  对话框提示内容
     * @param title    对话框标题
     * @param isBoth   是否双按钮显示，true : 双按钮
     */
    public static void showSimpleTipDialog(FragmentActivity activity, SimpleTipDialog.ConfirmListener listener, CharSequence content, CharSequence title, boolean isBoth) {
        showSimpleTipDialog(activity, listener, content, title, "取消", "确定", isBoth);
    }

    /**
     * 文字提示Dialog，可设置是否点击返回键消失，其他参数说明见{@link #showSimpleTipDialog}
     *
     * @param isCancelable false:不消失
     */
    public static void showTipDialogCancelBack(FragmentActivity activity, SimpleTipDialog.ConfirmListener listener, CharSequence content, CharSequence title, String cancelTxt, String submitTxt, boolean isBoth, boolean isCancelable) {
        SimpleTipDialog tipDialog = new SimpleTipDialog(activity);
        tipDialog.setTitleText(title);
        tipDialog.setTitleLocation(Gravity.CENTER);
        tipDialog.setContentString(content);
        tipDialog.setCancelText(cancelTxt);
        tipDialog.setSubmitText(submitTxt);
        tipDialog.setCancelVisible(isBoth);
        tipDialog.setConfirmListener(listener);
        tipDialog.setDialogCancelable(isCancelable);
        tipDialog.show();
    }

    /**
     * 显示普通提示dialog
     *
     * @param activity  FragmentActivity实例
     * @param listener  按钮点击监听
     * @param content   提示文字内容
     * @param title     dialog标题
     * @param cancelTxt 取消按钮文字
     * @param submitTxt 确定按钮文字
     * @param isBoth    是否显示两个按钮，如果为false，则默认值显示确认按钮
     */
    public static void showSimpleTipDialog(FragmentActivity activity, SimpleTipDialog.ConfirmListener listener, CharSequence content, CharSequence title, String cancelTxt, String submitTxt, boolean isBoth) {
        SimpleTipDialog tipDialog = new SimpleTipDialog(activity);
        tipDialog.setTitleText(title);
        tipDialog.setTitleLocation(Gravity.CENTER);
        tipDialog.setContentString(content);
        tipDialog.setCancelText(cancelTxt);
        tipDialog.setSubmitText(submitTxt);
        tipDialog.setCancelVisible(isBoth);
        tipDialog.setConfirmListener(listener);
        tipDialog.show();
    }

    /**
     * 显示普通提示dialog
     *
     * @param activity  FragmentActivity实例
     * @param listener  按钮点击监听
     * @param content   提示文字内容
     * @param title     dialog标题
     * @param cancelTxt 取消按钮文字
     * @param submitTxt 确定按钮文字
     * @param isBoth    是否显示两个按钮，如果为false，则默认值显示确认按钮
     * @param color     文字内容颜色
     */
    public static void showSimpleTipDialogExt(FragmentActivity activity, SimpleTipDialog.ConfirmListener listener, CharSequence content, CharSequence title, String cancelTxt, String submitTxt, boolean isBoth, int color) {
        SimpleTipDialog tipDialog = new SimpleTipDialog(activity);
        tipDialog.setTitleText(title);
        tipDialog.setTitleLocation(Gravity.CENTER);
        tipDialog.setContentString(content);
        tipDialog.setContentColor(color);
        tipDialog.setCancelText(cancelTxt);
        tipDialog.setSubmitText(submitTxt);
        tipDialog.setCancelVisible(isBoth);
        tipDialog.setConfirmListener(listener);
        tipDialog.show();
    }

    /**
     * 心动专用，Dialog文字内容左对齐
     */
    public static void showSimpleTipDialogXD(FragmentActivity activity, SimpleTipDialog.ConfirmListener listener, CharSequence content, CharSequence title, boolean isBoth) {
        SimpleTipDialog tipDialog = new SimpleTipDialog(activity);
        tipDialog.setTitleText(title);
        tipDialog.setTitleLocation(Gravity.CENTER);
        tipDialog.setTextGravity(Gravity.START);
        tipDialog.setContentString(content);
        tipDialog.setCancelText("取消");
        tipDialog.setSubmitText("确定");
        tipDialog.setCancelVisible(isBoth);
        tipDialog.setConfirmListener(listener);
        tipDialog.show();
    }

    /**
     * 简单提示对话框（退出，删除照片等）
     */
    public static void showSimpleAlertDialog(FragmentActivity activity, SimpleTipDialog.ConfirmListener listener, CharSequence content, CharSequence title) {
        showSimpleAlertDialog(activity, listener, content, R.color.txt_color1, title);
    }

    /**
     * 简单提示对话框,可自定义提示文字颜色
     */
    public static void showSimpleAlertDialog(FragmentActivity activity, SimpleTipDialog.ConfirmListener listener, CharSequence content, int color, CharSequence title) {
        SimpleTipDialog tipDialog = new SimpleTipDialog(activity);
        tipDialog.setTitleText(title);
        tipDialog.setContentString(content);
        tipDialog.setContentColor(color);
        tipDialog.setConfirmListener(listener);
        tipDialog.show();
    }

    // --------------- 个人资料专用 ------------------

    /**
     * 修改昵称编辑框
     */
    public static void showNickEditDialog(FragmentActivity activity, String defaultValut) {
        NickEditDialog editDialog = new NickEditDialog(activity, defaultValut);
        editDialog.setTitleText(activity.getString(R.string.user_edit_info_nick));
        editDialog.show();
    }

    /**
     * 联系方式修改
     */
    public static void showContactEditDialog(FragmentActivity activity, String contactDefault, int authDefault, int type) {
        ContactEditDialog dialog = new ContactEditDialog(activity, contactDefault, authDefault, type);
        String title;
        switch (type) {
            case ContactEditDialog.CONTACT_TYPE_QQ:
                title = "QQ";
                break;
            case ContactEditDialog.CONTACT_TYPE_MOBILE:
                title = activity.getString(R.string.user_edit_info_contact);
                break;
            case ContactEditDialog.CONTACT_TYPE_WECHAT:
                title = activity.getString(R.string.user_edit_info_wechat);
                break;
            default:
                title = activity.getString(R.string.user_edit_info_contact);
        }
        dialog.setTitleText(title);
        dialog.show();
    }
}
