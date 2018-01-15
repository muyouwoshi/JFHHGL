package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.Goods;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.third.recyclerholder.CustomRecyclerView;
import com.juxin.predestinate.ui.user.my.adapter.MyDiamondAdapter;
import com.juxin.predestinate.ui.user.my.view.DividerItemDecoration;

import java.util.List;

/**
 * 我的钻石页面
 * Created by @author zm on 2017/4/19
 */
public class MyDiamondsAct extends BaseActivity {

    private TextView tvDiamondSum;
    private CustomRecyclerView crlList;
    private RecyclerView rlvList;
    private MyDiamondAdapter mMyDiamondAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_wode_my_diamonds_act);
        initView();
    }

    private void initView() {
        setBackView(R.id.base_title_back);
        tvDiamondSum = (TextView) findViewById(R.id.wode_diamond_txv_sum);
        crlList = (CustomRecyclerView) findViewById(R.id.wode_diamond_crl_list);
        rlvList = crlList.getRecyclerView();
        rlvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rlvList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST, R.drawable.p1_decoration_px1));
        setTitle(getString(R.string.my_diamonds_explain));
        setTitleRight(getString(R.string.explain), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statistics.userBehavior(SendPoint.menu_me_gem_explain);
                //跳转到钻石说明页
                UIShow.showMyDiamondsExplainAct(MyDiamondsAct.this);
            }
        });
        initData();
    }

    private void initData() {
        tvDiamondSum.setText(ModuleMgr.getCenterMgr().getMyInfo().getDiamand() + "");
        mMyDiamondAdapter = new MyDiamondAdapter(this);
        List<Goods> dataList = ModuleMgr.getCommonMgr().getCommonConfig().getPay().getDiamondList();
        rlvList.setAdapter(mMyDiamondAdapter);
        mMyDiamondAdapter.setList(dataList);
        crlList.showRecyclerView();
    }
}