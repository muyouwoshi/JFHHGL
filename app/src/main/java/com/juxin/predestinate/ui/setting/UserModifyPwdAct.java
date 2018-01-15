package com.juxin.predestinate.ui.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;

/**
 * 修改密码
 * 
 * @author:XY
 */
public class UserModifyPwdAct extends BaseActivity implements OnClickListener{
	private EditText edtTxt_modify_oldpwd;
	private EditText edtTxt_modify_newpwd;
	private EditText edtTxt_modify_renewpwd;

	private String oldpwd;
	private String newpwd;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.f1_user_modify_pwd_act);
		setBackView(getResources().getString(R.string.title_pwd_set));
		initView();
	}

	private void initView() {
		edtTxt_modify_oldpwd = (EditText) findViewById(R.id.edtTxt_modify_oldpwd);
		edtTxt_modify_newpwd = (EditText) findViewById(R.id.edtTxt_modify_newpwd);
		edtTxt_modify_renewpwd = (EditText) findViewById(R.id.edtTxt_modify_renewpwd);
		Button btn_modify_pwd_submit = (Button) findViewById(R.id.btn_modify_pwd_submit);
		TextView txt_modifypwd_id = (TextView) findViewById(R.id.txt_modifypwd_id);
		txt_modifypwd_id.setText(App.uid+ "");
		btn_modify_pwd_submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_modify_pwd_submit:
			if (!checkInputInfo()) {
				ModuleMgr.getCenterMgr().modifyPassword(this,oldpwd, newpwd);
			}
			break;
		}
	}

	private boolean checkInputInfo() {
		oldpwd = edtTxt_modify_oldpwd.getText().toString();
		if (TextUtils.isEmpty(oldpwd)) {
			PToast.showShort(getResources().getString(R.string.toast_oldpwd_isnull));
			return true;
		}
		newpwd = edtTxt_modify_newpwd.getText().toString();
		if (TextUtils.isEmpty(newpwd) || newpwd.length() < 8 || newpwd.length() > 20) {
			PToast.showShort(getResources().getString(R.string.toast_pwd_error));
			return true;
		}
		String renewpwd = edtTxt_modify_renewpwd.getText().toString();
		if (TextUtils.isEmpty(renewpwd) || !newpwd.equals(renewpwd)) {
			PToast.showShort(getResources().getString(R.string.toast_pwd_next_error));
			return true;
		}
		return false;
	}

	public  void exitApp() {
		this.setResult(200);
        this.finish();
	}
}
