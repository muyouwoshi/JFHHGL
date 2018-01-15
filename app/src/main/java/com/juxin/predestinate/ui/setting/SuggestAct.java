package com.juxin.predestinate.ui.setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.debug.GMHelper;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;

/**
 * 意见反馈
 *
 * @author xy
 */
public class SuggestAct extends BaseActivity {

    private EditText et_suggest;
    private EditText et_contact;
    private CharSequence temp;
    private int maxCount = 120;
    private int selectionStart;
    private int selectionEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_activity_suggest);
        setBackView(getResources().getString(R.string.title_suggest));
        initView();
    }


    private void initView() {
        et_suggest = (EditText) this.findViewById(R.id.edt_suggest_value);
        et_contact = (EditText) this.findViewById(R.id.edt_suggest_qq);
        GMHelper.gmInvokeAuto(et_contact);
        findViewById(R.id.btn_suggest_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSuggest();
            }
        });

        et_suggest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                s.length() + "/120"
                selectionStart = et_suggest.getSelectionStart();
                selectionEnd = et_suggest.getSelectionEnd();
                if (temp.length() > maxCount) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    et_suggest.setText(s);
                    et_suggest.setSelection(tempSelection);//设置光标在最后
                }
            }
        });
    }


    private void submitSuggest() {
        String suggesttext = et_suggest.getText().toString();
        String qq = et_contact.getText().toString();
        if (suggesttext.equals("")) {
            PToast.showShort(getResources().getString(R.string.toast_suggest_isnull));
            et_suggest.setFocusable(true);
            et_suggest.setFocusableInTouchMode(true);
            et_suggest.requestFocus();
            return;
        }
        if (qq.equals("")) {
            PToast.showShort(getResources().getString(R.string.toast_contactr_isnull));
            et_contact.setFocusable(true);
            et_contact.setFocusableInTouchMode(true);
            et_contact.requestFocus();
            return;
        }
        ModuleMgr.getCenterMgr().feedBack(this, qq, suggesttext);
    }
}
