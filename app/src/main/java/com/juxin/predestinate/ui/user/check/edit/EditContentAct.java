package com.juxin.predestinate.ui.user.check.edit;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.UIUtil;

import java.util.HashMap;

/**
 * 编辑内容 通用界面
 */
public class EditContentAct extends BaseActivity implements View.OnClickListener, TextWatcher {
    private static final int MAX_INPUT_NUM = 7;     // 最大输入字数限制

    private EditText editText;
    private TextView nameLimit;
    private String defaultValue; // 初始value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_user_edit_content_act);

        initView();
        fillData();
    }

    private void initTitle() {
        setBackView();
        setTitle(getString(R.string.user_info_edit_name));
        setTitleRight(getString(R.string.save), this);
        ModuleMgr.getCenterMgr().inputFilterSpace(editText, 7);
        editText.setHint(getString(R.string.user_info_put_name));
    }

    private void initView() {
        editText = (EditText) findViewById(R.id.edit_content_text);
        nameLimit = (TextView) findViewById(R.id.tv_name_num);
        editText.addTextChangedListener(this);
        initTitle();
    }

    private void fillData() {
        defaultValue = ModuleMgr.getCenterMgr().getMyInfo().getNickname();

        if (!TextUtils.isEmpty(defaultValue)) {
            editText.setText(defaultValue);
            UIUtil.endCursor(editText);
            nameLimit.setText(format(defaultValue.length()));
        } else {
            nameLimit.setText(format(0));
        }
    }

    @Override
    public void onClick(View v) {
        if (test())
            saveAndFinish();
    }

    private String format(int num) {
        return String.format(getString(R.string.user_info_edit_limit), num, MAX_INPUT_NUM);
    }

    /**
     * 检测输入数据是否合法
     */
    private boolean test() {
        String contact = editText.getText().toString().trim();
        if (TextUtils.isEmpty(contact)) {
            PToast.showShort("昵称不能为空");
            return false;
        }

        if (contact.length() < 2) {
            PToast.showShort("昵称至少2个字符");
            return false;
        }

        if (contact.equals(defaultValue)) {
            PToast.showShort("请编辑后在保存");
            return false;
        }

        return true;
    }

    /**
     * 保存修改信息值，并关闭当前页面
     */
    private void saveAndFinish() {
        String contact = editText.getText().toString().trim();
        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put(EditKey.s_key_nickName, contact);
        ModuleMgr.getCenterMgr().updateMyInfo(postParams, null);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int balanceNum = s.toString().length();
        if (balanceNum > MAX_INPUT_NUM) {
            balanceNum = MAX_INPUT_NUM;
            String temp = s.toString().substring(0, MAX_INPUT_NUM);
            editText.setText(temp);
            editText.setSelection(MAX_INPUT_NUM);
            PToast.showShort("字数已达上限");
        }
        nameLimit.setText(format(balanceNum));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
