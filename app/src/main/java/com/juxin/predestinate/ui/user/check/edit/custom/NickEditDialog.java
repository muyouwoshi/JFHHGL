package com.juxin.predestinate.ui.user.check.edit.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.TimerUtil;
import com.juxin.predestinate.ui.user.check.edit.EditKey;

import java.util.HashMap;

/**
 * 修改昵称弹框
 */
public class NickEditDialog extends SimpleTipDialog {
    private Context context;
    private EditText editText;
    private String defaultVlaue;

    public NickEditDialog(FragmentActivity context, String defaultVlaue) {
        super(context);
        this.context = context;
        this.defaultVlaue = defaultVlaue;
    }

    @NonNull
    @Override
    protected View makeCenterView() {
        View view = LayoutInflater.from(context).inflate(R.layout.f1_user_info_edit_name_dialog, null);
        editText = (EditText) view.findViewById(R.id.edit);
        if (defaultVlaue != null) {
            editText.setText(defaultVlaue);
        }
        editText.setSelection(editText.getText().length());
        return view;
    }

    @Override
    protected void onSubmit() {
        String result = editText.getText().toString();
        if (TextUtils.isEmpty(result)) {
            PToast.showShort(context.getString(R.string.user_edit_info_input_nickname));
            return;
        }
        if (result.length() > 7) {
            PToast.showShort(context.getString(R.string.user_edit_info_input_nickname_limt));
            return;
        }

        if (result.equals(defaultVlaue)) {
            super.onSubmit();
            return;
        }
        LoadingDialog.show((FragmentActivity) context, context.getString(R.string.loading_reg_update));
        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put(EditKey.s_key_nickName, result);
        ModuleMgr.getCenterMgr().updateMyInfo(postParams, new RequestComplete() {
            @Override
            public void onRequestComplete(final HttpResponse response) {
                LoadingDialog.closeLoadingDialog(200);
            }
        });
        super.onSubmit();
    }
}
