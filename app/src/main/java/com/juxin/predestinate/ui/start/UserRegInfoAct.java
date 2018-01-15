package com.juxin.predestinate.ui.start;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.baseui.picker.picker.OptionPicker;
import com.juxin.predestinate.module.logic.config.InfoConfig;
import com.juxin.predestinate.module.util.CommonUtil;
import com.juxin.predestinate.module.util.PickerDialogUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

/**
 * 注册页面
 * Created YAO on 2017/4/19.
 */
public class UserRegInfoAct extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private EditText et_nickname;
    private TextView txt_reg_info_age;
    private RadioGroup rg_gender;
    private Button bt_submit;
    private ImageView iv_protocol;

    private String nickname;
    private int age;
    private String ageVlaue;
    private int gender = 1;
    private boolean isAgree = true; // 是否同意用户协议

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p2_user_reg_info_act);
        setBackView(getResources().getString(R.string.title_reg));
        initView();
        initEvent();
    }

    private void initView() {
        iv_protocol = (ImageView) findViewById(R.id.iv_protocol);
        et_nickname = (EditText) findViewById(R.id.edtTxt_reg_info_nickname);
        txt_reg_info_age = (TextView) findViewById(R.id.txt_reg_info_age);
        rg_gender = (RadioGroup) findViewById(R.id.rg_reg_info_gender);
        bt_submit = (Button) findViewById(R.id.btn_reg_info_submit);
        findViewById(R.id.ly_protocol).setVisibility(CommonUtil.isPurePackage() ? View.VISIBLE : View.GONE);
    }

    private void initEvent() {
        txt_reg_info_age.setOnClickListener(clickListener);
        bt_submit.setOnClickListener(clickListener);
        rg_gender.setOnCheckedChangeListener(this);
        iv_protocol.setOnClickListener(clickListener);
        findViewById(R.id.tv_protocol).setOnClickListener(clickListener);
    }

    private NoDoubleClickListener clickListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.txt_reg_info_age:
                    PickerDialogUtil.showOptionPickerDialog(UserRegInfoAct.this, new OptionPicker.OnOptionPickListener() {
                        @Override
                        public void onOptionPicked(String option) {
                            age = InfoConfig.getInstance().getAgeN().getSubmitWithShow(option);
                            ageVlaue = option;
                            txt_reg_info_age.setText(ageVlaue);
                        }
                    }, InfoConfig.getInstance().getAgeN().getShow(), getResources().getString(R.string.dal_age_default), getResources().getString(R.string.daltitle_age));
                    break;
                case R.id.btn_reg_info_submit:
                    StatisticsUser.userRegister(nickname, age, gender);
                    if (validInput()) {
                        LoadingDialog.show(UserRegInfoAct.this, getResources().getString(R.string.loading_reg));
                        ModuleMgr.getCenterMgr().getMyInfo().setGender(gender);
                        ModuleMgr.getLoginMgr().onRegister(UserRegInfoAct.this, nickname, age, gender);
                    }
                    break;

                case R.id.iv_protocol:  // 用户协议同意状态
                    isAgree = !isAgree;
                    iv_protocol.setImageResource(isAgree ? R.drawable.f1_protocol_agree :
                            R.drawable.f1_protocol_not_agree);
                    break;

                case R.id.tv_protocol:  // 跳转用户协议:
                    UIShow.showRegisterAgree(UserRegInfoAct.this);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 检查用户输入的信息是否有误
     *
     * @return
     */
    private boolean validInput() {
        nickname = et_nickname.getText().toString();
        if (TextUtils.isEmpty(nickname)) {
            PToast.showShort(getResources().getString(R.string.toast_nickname_isnull));
            return false;
        }
        if (age == 0 || ageVlaue == null) {
            PToast.showShort(getResources().getString(R.string.toast_age_isnull));
            return false;
        }
        if (!isAgree) {
            PToast.showShort(getString(R.string.user_register_protocol_tips));
            return false;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            default:
            case R.id.radio_reg_info_male:
                ((TextView)findViewById(R.id.man_text)).setTextColor(ActivityCompat.getColor(this,R.color.hei_color));
                ((TextView)findViewById(R.id.woman_text)).setTextColor(ActivityCompat.getColor(this,R.color.trans_gray));
                gender = 1; // 性别男
                break;
            case R.id.radio_reg_info_female:
                ((TextView)findViewById(R.id.woman_text)).setTextColor(ActivityCompat.getColor(this,R.color.hei_color));
                ((TextView)findViewById(R.id.man_text)).setTextColor(ActivityCompat.getColor(this,R.color.trans_gray));
                gender = 2; // 性别女
                break;
        }
    }
}
