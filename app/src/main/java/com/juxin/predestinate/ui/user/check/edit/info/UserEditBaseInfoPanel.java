package com.juxin.predestinate.ui.user.check.edit.info;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.area.City;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.baseui.picker.picker.AddressPicker;
import com.juxin.predestinate.module.logic.baseui.picker.picker.OptionPicker;
import com.juxin.predestinate.module.logic.config.InfoConfig;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.PickerDialogUtil;
import com.juxin.predestinate.ui.user.check.edit.EditKey;
import com.juxin.predestinate.ui.user.check.edit.custom.ContactEditDialog;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

import java.util.HashMap;

/**
 * 基本资料
 * Created by Su on 2017/5/3.
 */
public class UserEditBaseInfoPanel extends BasePanel implements RequestComplete {

    private TextView name, gender, age, home, height, income, marry,        //基本资料
            qq_info, qq, mobile_info, mobile, wechat_info, wechat;  //联系方式

    private UserDetail userDetail;

    public UserEditBaseInfoPanel(Context context) {
        super(context);
        setContentView(R.layout.f1_user_edit_base_info_panel);

        initView();
        initData();
        refreshView();
    }

    private void initView() {
        name = (TextView) findViewById(R.id.name);
        gender = (TextView) findViewById(R.id.gender);
        age = (TextView) findViewById(R.id.age);
        home = (TextView) findViewById(R.id.home);
        height = (TextView) findViewById(R.id.height);
        income = (TextView) findViewById(R.id.income);
        marry = (TextView) findViewById(R.id.marry);
        qq_info = (TextView) findViewById(R.id.qq_info);
        qq = (TextView) findViewById(R.id.qq);
        mobile_info = (TextView) findViewById(R.id.mobile_info);
        mobile = (TextView) findViewById(R.id.mobile);
        wechat_info = (TextView) findViewById(R.id.wechat_info);
        wechat = (TextView) findViewById(R.id.wechat);
    }

    private void initData() {
        findViewById(R.id.name_view).setOnClickListener(clickListener);
        findViewById(R.id.gender_view).setOnClickListener(clickListener);
        findViewById(R.id.age_view).setOnClickListener(clickListener);
        findViewById(R.id.home_view).setOnClickListener(clickListener);
        findViewById(R.id.height_view).setOnClickListener(clickListener);
        findViewById(R.id.income_view).setOnClickListener(clickListener);
        findViewById(R.id.marry_view).setOnClickListener(clickListener);
        findViewById(R.id.view_qq).setOnClickListener(clickListener);
        findViewById(R.id.view_mobile).setOnClickListener(clickListener);
        findViewById(R.id.view_wechat).setOnClickListener(clickListener);
    }


    public void refreshView() {
        userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        String sNotFilled = getContext().getResources().getString(R.string.str_not_filled);
        name.setText(userDetail.getNickname());
        gender.setText(userDetail.isMan() ? getContext().getString(R.string.txt_boy) :
                getContext().getString(R.string.txt_girl));
        age.setText(getContext().getString(R.string.user_info_age, userDetail.getAge()));
        home.setText(TextUtils.isEmpty(userDetail.getAddressShow()) ? sNotFilled : userDetail.getAddressShow());
        height.setText(userDetail.getHeight() + "cm");
        income.setText(TextUtils.isEmpty(userDetail.getIncome()) ? sNotFilled : userDetail.getIncome());
        marry.setText(TextUtils.isEmpty(userDetail.getMarry()) ? sNotFilled : userDetail.getMarry());
        qq_info.setText(TextUtils.isEmpty(userDetail.getQQ())
                || userDetail.getQQ() == "null" ? "QQ" : "QQ：" + userDetail.getQQ());
        mobile_info.setText(TextUtils.isEmpty(userDetail.getMobile())
                || userDetail.getMobile() == "null" ? context.getString(R.string.user_edit_info_phone)
                : context.getString(R.string.user_edit_info_phone) + "：" + userDetail.getMobile());
        wechat_info.setText(TextUtils.isEmpty(userDetail.getWeChat())
                || userDetail.getWeChat() == "null" ? context.getString(R.string.user_edit_info_wechat)
                : context.getString(R.string.user_edit_info_wechat) + "：" + userDetail.getWeChat());

        setAuthTip();
    }

    // 填充权限
    private void setAuthTip() {
        qq.setText(contactStatus(userDetail.getQQ(), userDetail.getQQAuth() != 2));
        mobile.setText(contactStatus(userDetail.getMobile(), userDetail.getMobileAuth() != 2));
        wechat.setText(contactStatus(userDetail.getWeChat(), userDetail.getWechatAuth() != 2));
    }

    /**
     * 权限展示逻辑
     */
    private String contactStatus(String contact, boolean showVip) {
        return TextUtils.isEmpty(contact) || contact == "null" ? getContext().getResources().getString(R.string.str_not_filled)
                : (showVip ? context.getString(R.string.user_edit_info_just_vip_open)
                : context.getString(R.string.user_edit_info_auth_secret));
    }

    private NoDoubleClickListener clickListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            final UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
            switch (v.getId()) {
                case R.id.name_view:
                    PickerDialogUtil.showNickEditDialog((FragmentActivity) getContext(), userDetail.getNickname());
                    break;

                case R.id.gender_view:
                    PToast.showShort(context.getString(R.string.user_edit_info_not_gender));
                    break;

                case R.id.age_view:
                    PToast.showShort(context.getString(R.string.user_edit_info_not_age));
                    break;

                case R.id.home_view:
                    final String province = userDetail.getProvinceName() == null ? "" : userDetail.getProvinceName();
                    final String city = userDetail.getCityName() == null ? "" : userDetail.getCityName();
                    PickerDialogUtil.showAddressPickerDialog2((FragmentActivity) getContext(), new AddressPicker.OnAddressPickListener() {
                        @Override
                        public void onAddressPicked(City city) {
                            if (city.getCityID() == userDetail.getScity() && city.getProvinceID() == userDetail.getSprovince())
                                return;

                            LoadingDialog.show((FragmentActivity) getContext(), context.getString(R.string.loading_reg_update));
                            HashMap<String, Object> postParams = new HashMap<>();
                            postParams.put(EditKey.s_key_pro, city.getProvinceID());
                            postParams.put(EditKey.s_key_city, city.getCityID());
                            ModuleMgr.getCenterMgr().updateMyInfo(postParams, UserEditBaseInfoPanel.this);
                        }
                    }, province, city);
                    break;

                case R.id.height_view:
                    InfoConfig.SimpleConfig simpleConfig = InfoConfig.getInstance().getHeightN();
                    showOptionPickerDialog(userDetail.getHeight() + "cm", simpleConfig, EditKey.s_key_height, context.getString(R.string.user_edit_info_height));
                    break;

                case R.id.income_view:
                    InfoConfig.SimpleConfig incomeConfig = InfoConfig.getInstance().getIncomeN();
                    showOptionPickerDialog(userDetail.getIncome(), incomeConfig, EditKey.s_key_income, context.getString(R.string.user_edit_info_income));
                    break;

                case R.id.marry_view:
                    InfoConfig.SimpleConfig marryConfig = InfoConfig.getInstance().getMarry();
                    showOptionPickerDialog(userDetail.getMarry(), marryConfig, EditKey.s_key_marry, context.getString(R.string.user_edit_info_marry));
                    break;

                case R.id.view_qq:
                    String qqValue = TextUtils.isEmpty(userDetail.getQQ())
                            || userDetail.getQQ() == "null" ? "" : userDetail.getQQ();
                    PickerDialogUtil.showContactEditDialog((FragmentActivity) getContext(), qqValue, userDetail.getQQAuth(), ContactEditDialog.CONTACT_TYPE_QQ);
                    break;

                case R.id.view_mobile:
                    String moblieValue = TextUtils.isEmpty(userDetail.getMobile())
                            || userDetail.getMobile() == "null" ? "" : userDetail.getMobile();
                    PickerDialogUtil.showContactEditDialog((FragmentActivity) getContext(), moblieValue, userDetail.getMobileAuth(), ContactEditDialog.CONTACT_TYPE_MOBILE);
                    break;

                case R.id.view_wechat:
                    String weChatValue = TextUtils.isEmpty(userDetail.getWeChat())
                            || userDetail.getWeChat() == "null" ? "" : userDetail.getWeChat();
                    PickerDialogUtil.showContactEditDialog((FragmentActivity) getContext(), weChatValue, userDetail.getWechatAuth(), ContactEditDialog.CONTACT_TYPE_WECHAT);
                    break;
            }
        }
    };

    // 展示单项选择Dialog
    private void showOptionPickerDialog(final String value, final InfoConfig.SimpleConfig config, final String postKey, String title) {
        PickerDialogUtil.showOptionPickerDialog((FragmentActivity) getContext(), new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(String option) {
                if (value == null || !value.equals(option)) {
                    LoadingDialog.show((FragmentActivity) getContext(), context.getString(R.string.loading_reg_update));
                    HashMap<String, Object> postParams = new HashMap<>();
                    postParams.put(postKey, config.getSubmitWithShow(option));
                    ModuleMgr.getCenterMgr().updateMyInfo(postParams, UserEditBaseInfoPanel.this);
                }
            }
        }, config.getShow(), value, title);
    }

    @Override
    public void onRequestComplete(final HttpResponse response) {
        LoadingDialog.closeLoadingDialog(200);
    }
}
