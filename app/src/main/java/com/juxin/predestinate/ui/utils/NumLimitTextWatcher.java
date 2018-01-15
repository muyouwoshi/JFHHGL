package com.juxin.predestinate.ui.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 创建日期：2017/7/6
 * 描述:
 * 作者:lc
 */
public class NumLimitTextWatcher implements TextWatcher {

    private int sum;
    private int selectionStart;
    private int selectionEnd;

    private EditText editText;
    private TextView textView;
    private CharSequence temp;

    public NumLimitTextWatcher(EditText editText, TextView textView, int sum) {
        this.editText = editText;
        this.textView = textView;
        this.sum = sum;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        temp = s;
    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            textView.setText(s.length() + "/" + sum);
            selectionStart = editText.getSelectionStart();
            selectionEnd = editText.getSelectionEnd();
            if (temp.length() > sum) {
                s.delete(selectionStart - 1, selectionEnd);
                editText.setText(s);
                editText.setSelection(s.length());//设置光标在最后
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
