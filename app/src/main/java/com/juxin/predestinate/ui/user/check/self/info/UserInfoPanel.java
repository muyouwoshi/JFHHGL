package com.juxin.predestinate.ui.user.check.self.info;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.juxin.library.utils.TypeConvertUtil;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.ui.user.util.CenterConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户详细资料列表
 * Created by Su on 2016/6/6.
 */
public class UserInfoPanel extends BasePanel {
    private List<UserPersonInfo> userInfoList;
    private UserInfoAdapter userInfoAdapter;

    private UserDetail userDetail;
    private String[] zoneDatas, baseDatas, detailDatas, contactDatas;
    private String notFill = getContext().getString(R.string.str_not_filled);
    private String secretStr = getContext().getString(R.string.user_edit_info_auth_secret);
    private int channel;

    public UserInfoPanel(Context context, UserDetail userDetail, int channel) {
        super(context);
        setContentView(R.layout.f1_user_info_base_panel);
        this.userDetail = userDetail;
        this.channel = channel;

        initData();
        initView();
        refreshData();
    }

    private void initView() {
        userInfoAdapter = new UserInfoAdapter(getContext());
        RecyclerView list_info = (RecyclerView) findViewById(R.id.list_info);
        list_info.setLayoutManager(new LinearLayoutManager(getContext()));
        list_info.setNestedScrollingEnabled(false);
        list_info.setAdapter(userInfoAdapter);
    }

    private void initData() {
        userInfoList = new ArrayList<>();
        zoneDatas = getContext().getResources().getStringArray(R.array.zone_data);
        baseDatas = getContext().getResources().getStringArray(R.array.base_data);
        detailDatas = getContext().getResources().getStringArray(R.array.detail_data);
//        contactDatas = getContext().getResources().getStringArray(R.array.contact_data);

        for (String zoneData : zoneDatas) {
            userInfoList.add(new UserPersonInfo(getContext().getString(R.string.user_edit_zone_info), zoneData));
        }
        for (String baseData : baseDatas) {
            userInfoList.add(new UserPersonInfo(getContext().getString(R.string.user_edit_base_info), baseData));
        }
        for (String detailData : detailDatas) {
            userInfoList.add(new UserPersonInfo(getContext().getString(R.string.user_edit_info_detail_info), detailData));
        }
//        for (String contactData : contactDatas) { //注销：防止导流
//            userInfoList.add(new UserPersonInfo(getContext().getString(R.string.user_edit_info_contact), contactData));
//        }
    }

    private void refreshData() {
        fillZoneValue();
        fillBaseValue();
        fillDetailValue();
//        fillContactValue();  //防止导流
        userInfoAdapter.setList(userInfoList);
    }

    public void refreshView(UserDetail userDetail) {
        this.userDetail = userDetail;
        //fillBaseValue();
        userInfoAdapter.setList(userInfoList);
    }

    // 填充个人空间
    private void fillZoneValue() {
        String online = TextUtils.isEmpty(userDetail.getOnline_text()) ? secretStr : userDetail.getOnline_text();
        String datingfor = TextUtils.isEmpty(userDetail.getDatingfor()) ? secretStr : userDetail.getDatingfor();
        String concept = TextUtils.isEmpty(userDetail.getConcept()) ? secretStr : userDetail.getConcept();
        String favplace = TextUtils.isEmpty(userDetail.getFavplace()) ? secretStr : userDetail.getFavplace();
        String favaction = TextUtils.isEmpty(userDetail.getFavaction()) ? secretStr : userDetail.getFavaction();
        String[] zoneValues = new String[]{online, datingfor, concept, favplace, favaction};
        for (int i = 0; i < zoneDatas.length; i++) {
            userInfoList.get(i).setValue(zoneValues[i]);
        }
    }

    // 填充基本资料
    private void fillBaseValue() {
        String gender = userDetail.isMan() ? getContext().getString(R.string.txt_boy) :
                getContext().getString(R.string.txt_girl);
        String address = userDetail.getAddressShow();
        if (!TextUtils.isEmpty(address) && address.length() > 14) {
            address = address.substring(0, 14) + "...";
        }
        String height = userDetail.getHeight() == 0 ? notFill : (userDetail.getHeight() + "cm");
        String income = TextUtils.isEmpty(userDetail.getIncome()) ? notFill : userDetail.getIncome();
        String marry = TextUtils.isEmpty(userDetail.getMarry()) ? notFill : userDetail.getMarry();
        String age = userDetail.getAge() == 0 ? notFill : getContext().getString(R.string.user_info_age, userDetail.getAge());
        String nickName = userDetail.getNickname();
        String[] baseValues = new String[]{nickName, gender, age, address, height, income, marry};
        int count = zoneDatas.length;
        for (int i = count; i < baseDatas.length + count; i++) {
            userInfoList.get(i).setValue(baseValues[i - count]);
        }
    }

    // 填充详细资料
    private void fillDetailValue() {
        String edu = TextUtils.isEmpty(userDetail.getEdu()) ? notFill : userDetail.getEdu();
        String job = TextUtils.isEmpty(userDetail.getJob()) ? notFill : userDetail.getJob();
        String birthday = TextUtils.isEmpty(userDetail.getBirthday()) ? notFill : userDetail.getBirthday();
        String weight = TextUtils.isEmpty(userDetail.getWeight()) ? notFill : userDetail.getWeight();
        String star = TextUtils.isEmpty(userDetail.getStar()) ? notFill : userDetail.getStar();
        String[] detailValues = new String[]{edu, job, birthday, weight, star};
        int count = zoneDatas.length + baseDatas.length;
        for (int i = count; i < detailDatas.length + count; i++) {
            userInfoList.get(i).setValue(detailValues[i - count]);
        }
    }

    // 填充联系方式资料 注销 防止导流
    private void fillContactValue() {
        String qq = TextUtils.isEmpty(userDetail.getQQ()) ? "" : userDetail.getQQ();
        String qqValue = qq + "-" + userDetail.getQQAuth();

        String mobile = TextUtils.isEmpty(userDetail.getMobile()) ? "" : userDetail.getMobile();
        String mobileValue = mobile + "-" + userDetail.getMobileAuth();

        String weChat = TextUtils.isEmpty(userDetail.getWeChat()) ? "" : userDetail.getWeChat();
        String weChatValue = weChat + "-" + userDetail.getWechatAuth();

        String[] contactValues = new String[]{qqValue, mobileValue, weChatValue};
        int count = zoneDatas.length + baseDatas.length + detailDatas.length;
        for (int i = count; i < contactDatas.length + count; i++) {
            if (contactValues[i - count] != null) {
                int auth = TypeConvertUtil.toInt(contactValues[i - count].split("-")[1], 0);
                String value = contactValues[i - count].split("-")[0];
                userInfoList.get(i).setValue(limits(auth));
                if (limitsForVisiable(auth)) {//判断是否可见内容
                    userInfoList.get(i).setContact(value);
                } else {
                    userInfoList.get(i).setContact("");
                }
            }
        }
    }

    /**
     * @param auth
     * @return 女号看男号：联系方式公开 不显示文字 反之显示保密
     * 男号看女号：联系方式仅对vip公开 男号vip不显示文字，非vip显示仅对vip公开 反之显示保密
     */
    private String limits(int auth) {
        // 男性用户
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            if (ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
                return auth == 1 ? "" : secretStr;
            } else {
                return auth == 1 ? getContext().getString(R.string.user_edit_info_just_vip_open) : secretStr;
            }
        }

        return auth == 1 ? "" : secretStr;
    }

    private boolean limitsForVisiable(int auth) {
        // 保密状态，男女用户均不显示内容
        if (auth != 1) {
            return false;
        }

        // 男Vip用户显示公开内容， 非Vip不显示内容
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            return ModuleMgr.getCenterMgr().getMyInfo().isVip();
        }

        // 女用户显示
        return true;
    }

    //****************************************************************************************

    /**
     * 判断最终显示的字符串，默认查看的用户都为异性
     *
     * @param secret   用户资料保密级别
     * @param field    用户联系方式字段
     * @param position 用户联系方式字段 0 qq 1 手机号 2 微信号
     * @return 最终显示的字符串
     */
    private String judgeShowString(int secret, String field, int position) {
        String showString = "";
        boolean isVip = ModuleMgr.getCenterMgr().getMyInfo().isVip();
        if (secret == 2) {//保密显示
            showString = secretStr;
        } else if (isVip || !userDetail.isMan() || TextUtils.isEmpty(field)) {//女或vip显示联系方式
            showString = field;
        } else if (position == 1) {//隐藏部分手机号
            if (!"".equals(field) && field.length() >= 4) {//手机号处理
                showString = field.substring(0, 4) + "***" +
                        getContext().getString(R.string.user_edit_info_just_vip_open);
            }
        } else {
            showString = getContext().getString(R.string.user_edit_info_just_vip_open);
        }
        return showString;
    }
}
