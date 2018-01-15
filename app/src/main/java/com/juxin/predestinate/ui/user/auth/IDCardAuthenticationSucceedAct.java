package com.juxin.predestinate.ui.user.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.my.RedBoxRecordAct;

/**
 * Created by zm on 2017/5/16
 */
public class IDCardAuthenticationSucceedAct extends BaseActivity implements View.OnClickListener{

    public static final String IDCARDBACK = "idcardback";//1跳转我的钱包，2跳转首页
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_id_card_auth_succeed_act);
        initView();
    }

    private void initView() {
        initTitle();
        findViewById(R.id.id_card_succeed_btn_red).setOnClickListener(this);
        findViewById(R.id.id_card_succeed_btn_main).setOnClickListener(this);
    }

    private void initTitle() {
        setBackView(R.id.base_title_back);
        setTitle(getString(R.string.txt_authtype_id));
    }

    @Override
    public void onClick(View view) {
        Intent data = new Intent();
        switch (view.getId()){
            case R.id.id_card_succeed_btn_red:
                //返回钱包
                data.putExtra(IDCARDBACK, 1);
                setResult(RESULT_OK, data);
                finish();
                UIShow.show(this, RedBoxRecordAct.class,Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case R.id.id_card_succeed_btn_main:
                //返回首页
                data.putExtra(IDCARDBACK, 2);
                setResult(RESULT_OK, data);
                finish();
                UIShow.show(this, MyAuthenticationAct.class,Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            default:
                break;
        }
    }
}
