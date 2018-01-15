package com.juxin.predestinate.ui.utils;

import android.text.method.NumberKeyListener;
import android.widget.EditText;

/**
 * 创建日期：2017/7/7
 * 描述:输入身份证号 前十七位0-9，后一位 0-9或X,x
 * android:maxLength="18"
 * 作者:lc
 */
public class IDCardListener extends NumberKeyListener {

    private char[] mychar = new char[0];
    private static char[] numChar = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static char[] fullChar = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'X', 'x'};

    private EditText editText;

    public IDCardListener(EditText editText) {
        this.editText = editText;
    }

    @Override
    protected char[] getAcceptedChars() {
        try {
            if (editText.getText().toString().length() == 17) {
                mychar = fullChar;
            } else {
                mychar = numChar;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mychar != null ? mychar : new char[0];
    }

    @Override
    public int getInputType() {
        return android.text.InputType.TYPE_CLASS_TEXT;
    }
}
