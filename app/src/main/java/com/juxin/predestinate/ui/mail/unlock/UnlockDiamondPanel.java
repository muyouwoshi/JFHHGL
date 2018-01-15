package com.juxin.predestinate.ui.mail.unlock;


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.config.base.Goods;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;
import com.juxin.predestinate.ui.user.paygoods.GoodsListPanel;
import com.juxin.predestinate.ui.user.paygoods.GoodsPayTypePanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IQQ on 2017-11-01.
 */

public class UnlockDiamondPanel extends BasePanel implements View.OnClickListener {
    private GoodsListPanel goodsPanel; // 商品列表
    private GoodsPayTypePanel payTypePanel; // 支付方式
    private List<Goods> payGoods = new ArrayList<>();
    private UnlockMsgDlg.OnUnlockDlgMsg msg;
    private ImageView iv_show;

    public UnlockDiamondPanel(Context context, UnlockMsgDlg.OnUnlockDlgMsg msg) {
        super(context);
        this.msg = msg;
        setContentView(R.layout.spread_unlock_diamond_layout);
        payGoods.clear();
        List<Goods> goods = ModuleMgr.getCommonMgr().getCommonConfig().getPay().getDiamondList();
        if (goods != null) {
            int size = goods.size();
            for (int i = 0; i < size; i++) {
                if (i >= 2) {
                    break;
                }
                payGoods.add(goods.get(i));
            }
        }
        initView();
    }

    private void initView() {
        LinearLayout ll_have = (LinearLayout) findViewById(R.id.spread_ll_unlock_diamond_have);
        LinearLayout ll_nothave = (LinearLayout) findViewById(R.id.spread_ll_unlock_diamond_not_have);
        TextView tv_balance = (TextView) findViewById(R.id.spread_tv_unlock_diamond_remain_have);
        findViewById(R.id.spread_ll_unlock_btn_havediamond).setOnClickListener(this);
        findViewById(R.id.spread_ll_unlock_btn_show).setOnClickListener(this);
        iv_show = (ImageView) findViewById(R.id.spread_iv_unlock_btn_show_btn);
        LinearLayout container = (LinearLayout) findViewById(R.id.pay_type_container);
        Button btn_recharge = (Button) findViewById(R.id.btn_recharge);
        // 商品
        goodsPanel = new GoodsListPanel(getContext(), GoodsConstant.DLG_DIAMOND_CHAT);
        container.addView(goodsPanel.getContentView());
        goodsPanel.refresh(payGoods);

        // 支付方式
        payTypePanel = new GoodsPayTypePanel(getContext(), GoodsConstant.PAY_TYPE_NEW);
        container.addView(payTypePanel.getContentView());

        btn_recharge.setOnClickListener(this);

        UserDetail myDetail = ModuleMgr.getCenterMgr().getMyInfo();
        if (myDetail.getDiamand() > 0) {
            ll_have.setVisibility(View.VISIBLE);
            ll_nothave.setVisibility(View.GONE);
            //默认选中 下次不显示
            iv_show.setBackgroundResource(R.drawable.spread_unlock_checked);
            UnlockComm.setShowDiamondMsg(true);

            tv_balance.setText(String.valueOf(myDetail.getDiamand()));
        } else {
            ll_have.setVisibility(View.GONE);
            ll_nothave.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_recharge:  // 充值
                if (payGoods.size() <= 0) return;
                SourcePoint.getInstance().lockSource("gem_unlock_gempay");
                Goods payGood = payGoods.get(goodsPanel.getPosition());
                UIShow.showPayNoListAct((FragmentActivity) getContext(), payGood.getPid(), payTypePanel.getPayType(), 0, "");
                if (null != msg) msg.onDismiss();
                break;
            case R.id.spread_ll_unlock_btn_havediamond:
                UnlockComm.sendUnlockMessage("2", -1);
                StatisticsShareUnlock.onAlert_unlockchat_paysend(1);
                if (null != msg) msg.onDismiss();
                break;
            case R.id.spread_ll_unlock_btn_show:
                if (UnlockComm.isShowDiamondMsg()) {
                    iv_show.setBackgroundResource(R.drawable.spread_unlock_unchecked);
                    UnlockComm.setShowDiamondMsg(false);
                } else {
                    iv_show.setBackgroundResource(R.drawable.spread_unlock_checked);
                    UnlockComm.setShowDiamondMsg(true);
                }
                break;

        }
    }
}
