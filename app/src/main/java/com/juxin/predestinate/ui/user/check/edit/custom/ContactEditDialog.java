package com.juxin.predestinate.ui.user.check.edit.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.BaseUtil;
import com.juxin.predestinate.module.util.ChineseFilter;
import com.juxin.predestinate.ui.user.check.edit.EditKey;

import java.util.HashMap;

/**
 * 修改QQ，微信，手机弹框
 * Created by Su on 2017/5/16.
 */
public class ContactEditDialog extends SimpleTipDialog implements RadioGroup.OnCheckedChangeListener {
    public static final int CONTACT_TYPE_QQ = 1;
    public static final int CONTACT_TYPE_MOBILE = 2;
    public static final int CONTACT_TYPE_WECHAT = 3;

    private Context context;
    private EditText editText;
    private RadioGroup rgAuth;
    private RadioButton radioClose;  // 保密
    private RadioButton radioOpen;   // vip公开

    private String contact;
    private int auth = 1;
    private int type = CONTACT_TYPE_MOBILE;

    private String editkey;  // 修改的字段
    private String authkey;  // 修改权限字段

    public ContactEditDialog(FragmentActivity context, String contactDefault, int authDefault, int type) {
        super(context);
        this.contact = contactDefault;
        this.auth = authDefault == 0 ? 1 : authDefault;
        this.type = type;
        this.context = context;
    }

    @NonNull
    @Override
    protected View makeCenterView() {
        View view = LayoutInflater.from(context).inflate(R.layout.f1_user_info_edit_contact_dialog, null);
        editText = (EditText) view.findViewById(R.id.edit);
        rgAuth = (RadioGroup) view.findViewById(R.id.rg_auth);
        radioClose = (RadioButton) view.findViewById(R.id.radio_close);
        radioOpen = (RadioButton) view.findViewById(R.id.radio_open);
        rgAuth.setOnCheckedChangeListener(this);

        if (type == CONTACT_TYPE_WECHAT)
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        initData();
        return view;
    }

    private void initData() {
        editText.setText(contact);
        editText.setSelection(editText.getText().length());
        if (type == CONTACT_TYPE_MOBILE && !TextUtils.isEmpty(contact)) {
            if (ModuleMgr.getCenterMgr().getMyInfo().isVerifyCellphone()) {
                PToast.showShort(context.getString(R.string.user_edit_info_not_phone));
                editText.setEnabled(false);
            }
        }
        if (auth == 2) {
            radioOpen.setChecked(false);
            radioClose.setChecked(true);
        } else {
            radioOpen.setChecked(true);
            radioClose.setChecked(false);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.radio_close) {
            auth = 2;
        } else if (checkedId == R.id.radio_open) {
            auth = 1;
        }
    }

    /**
     * 更新个人资料
     */
    private void reqUpdateInfo() {
        LoadingDialog.show((FragmentActivity) context, context.getString(R.string.loading_reg_update));
        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put(editkey, contact);
        postParams.put(authkey, auth);
        ModuleMgr.getCenterMgr().updateMyInfo(postParams, new RequestComplete() {
            @Override
            public void onRequestComplete(final HttpResponse response) {
                LoadingDialog.closeLoadingDialog(200);
            }
        });
    }

    /**
     * 检测输入数据是否合法
     */
    private boolean test() {
        String message;
        switch (type) {
            case CONTACT_TYPE_QQ:
                editkey = EditKey.s_key_qq;
                authkey = EditKey.s_key_qqAuth;
                message = context.getString(R.string.user_edit_info_input_qq);
                break;
            case CONTACT_TYPE_MOBILE:
                editkey = EditKey.s_key_mobile;
                authkey = EditKey.s_key_mobileAuth;
                message = context.getString(R.string.user_edit_info_input_mobile);
                break;
            case CONTACT_TYPE_WECHAT:
                editkey = EditKey.s_key_wechat;
                authkey = EditKey.s_key_wechatAuth;
                message = context.getString(R.string.user_edit_info_input_wechat);
                break;
            default:
                message = context.getString(R.string.user_edit_info_input_mobile);
        }
        if (TextUtils.isEmpty(contact)) {
            PToast.showShort(message);
            return false;
        }
        if (type == CONTACT_TYPE_MOBILE) {
            if (!BaseUtil.isNumeric(contact) || contact.length() != 11) {
                PToast.showShort(context.getString(R.string.user_edit_info_correct_mobile));
                return false;
            }
        }

        if (type == CONTACT_TYPE_WECHAT) {
            if (!ChineseFilter.isWeChatNumber(contact)) {
                PToast.showShort(context.getString(R.string.user_edit_info_correct_wechat));
                return false;
            }
        }

        if (type == CONTACT_TYPE_QQ) {
            if (!ChineseFilter.isQQNumber(contact)) {
                PToast.showShort(context.getString(R.string.user_edit_info_correct_qq));
                return false;
            }
        }
        return true;
    }

    /**
     * 是否编辑
     */
    private boolean isEdited() {
        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        if (type == CONTACT_TYPE_MOBILE) {
            if (contact.equals(userDetail.getMobile()) && auth == userDetail.getMobileAuth()) {
                return false;
            }
        }

        if (type == CONTACT_TYPE_WECHAT) {
            if (contact.equals(userDetail.getWeChat()) && auth == userDetail.getWechatAuth()) {
                return false;
            }
        }

        if (type == CONTACT_TYPE_QQ) {
            if (contact.equals(userDetail.getQQ()) && auth == userDetail.getQQAuth()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onSubmit() {
        contact = editText.getText().toString();

        if (!test()) return;

        if (isEdited())
            reqUpdateInfo();
        super.onSubmit();
    }
}
