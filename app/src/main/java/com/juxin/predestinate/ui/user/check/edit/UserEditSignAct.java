package com.juxin.predestinate.ui.user.check.edit;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.ui.utils.EmojiFilter;

import java.util.HashMap;

/**
 * 编辑签名页面
 */
public class UserEditSignAct extends BaseActivity implements TextWatcher, OnClickListener {
    private static final int MAX_INPUT_NUM = 120;     // 最大输入字数限制
    private static EmojiFilter emojiFilter;

    private EditText editTxt_sign_content;
    private TextView txt_sign_content_num;

    private String sign;
    private String signContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_user_edit_sign_act);

        initTitle();
        initView();
        fillData();
    }

    private void initTitle() {
        setBackView();
        setTitle(getString(R.string.user_info_edit_sign));
        setTitleRight(getString(R.string.save), this);
    }

    private void initView() {
        emojiFilter = new EmojiFilter();
        this.editTxt_sign_content = (EditText) findViewById(R.id.editTxt_sign_content);
        this.editTxt_sign_content.setFilters(new InputFilter[]{emojiFilter});
        this.editTxt_sign_content.addTextChangedListener(this);
        this.txt_sign_content_num = (TextView) findViewById(R.id.txt_sign_content_num);
        findViewById(R.id.img_sign_content_del).setOnClickListener(this);
    }

    private void fillData() {
        this.sign = getIntent().getStringExtra("sign");

        if (!TextUtils.isEmpty(sign)) {
            editTxt_sign_content.setText(sign);
            editTxt_sign_content.setSelection(sign.length());
            txt_sign_content_num.setText(format(sign.length()));

        } else {
            txt_sign_content_num.setText(format(0));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_title_right_txt:
                if (test())
                    updateAndClose();
                break;

            case R.id.img_sign_content_del:
                editTxt_sign_content.setText("");// 清空所有输入的数据
                break;
        }
    }

    private String format(int num) {
        return String.format(getString(R.string.user_info_edit_limit), num, MAX_INPUT_NUM);
    }

    private boolean test() {
        String content = editTxt_sign_content.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            PToast.showShort("个性签名不能为空");
            return false;
        }
        if (content.equals(sign)) {
            PToast.showShort("请编辑后再提交");
            return false;
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int balanceNum = s.toString().length();
        if (balanceNum > MAX_INPUT_NUM) {
            balanceNum = MAX_INPUT_NUM;
            signContent = s.toString().substring(0, MAX_INPUT_NUM);
            editTxt_sign_content.setText(signContent);
            editTxt_sign_content.setSelection(MAX_INPUT_NUM);
            PToast.showShort("字数已达上限");
        }
        txt_sign_content_num.setText(format(balanceNum));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void updateAndClose() {
        String contact = editTxt_sign_content.getText().toString().trim();
        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put(EditKey.s_key_sign, contact);
        ModuleMgr.getCenterMgr().updateMyInfo(postParams, null);
        finish();
    }
}
