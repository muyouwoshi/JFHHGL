package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;


/**
 * 钻石说明界面
 * Created by zm on 2017/4/19.
 */

public class MyDiamondsExplainAct extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_wode_diamonds_explain_act);
        initView();
    }
    //初始化
    private void initView(){
        setBackView(R.id.base_title_back);//设置返回按钮
        setTitle(getString(R.string.diamond_show));//标题
    }
}