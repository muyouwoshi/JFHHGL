package com.juxin.predestinate.ui.user.check.secret.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.Goods;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;
import com.juxin.predestinate.ui.user.paygoods.GoodsListPanel;
import com.juxin.predestinate.ui.user.paygoods.GoodsPayTypePanel;

import java.util.ArrayList;
import java.util.List;

/**
 * 查看视频: 钻石充值弹框
 * Created by Su on 2017/5/17.
 */
public class SecretDiamondDlg extends BaseActivity implements View.OnClickListener {

    private List<Goods> payGoods = new ArrayList<>();  // 商品信息
    private GoodsListPanel goodsPanel; // 商品列表
    private GoodsPayTypePanel payTypePanel; // 支付方式

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_secret_diamond_dlg);
        payGoods.clear();
        List<Goods> goodses = ModuleMgr.getCommonMgr().getCommonConfig().getPay().getDiamondList();
        if (goodses != null) {
            int size = goodses.size();
            for (int i = 0; i < size; i++) {
                if (i >= 2) {
                    break;
                }
                payGoods.add(goodses.get(i));
            }
        }
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_recharge).setOnClickListener(this);

        fillGoodsPanel();
    }

    private void fillGoodsPanel() {
        // 商品列表
        LinearLayout container = (LinearLayout) findViewById(R.id.pay_container);
        goodsPanel = new GoodsListPanel(this, GoodsConstant.DLG_DIAMOND_GIFT);
        container.addView(goodsPanel.getContentView());
        goodsPanel.refresh(payGoods);

        // 支付方式
        LinearLayout payContainer = (LinearLayout) findViewById(R.id.pay_type_container);
        payTypePanel = new GoodsPayTypePanel(this, GoodsConstant.PAY_TYPE_OLD);
        payContainer.addView(payTypePanel.getContentView());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_recharge:  // 充值
                UIShow.showPayNoListAct(this, payGoods.get(goodsPanel.getPosition()).getPid(), payTypePanel.getPayType(),
                        0, "");
                break;
        }
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }
}
