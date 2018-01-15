package com.juxin.predestinate.ui.user.paygoods.diamond;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.Goods;
import com.juxin.predestinate.module.local.statistics.StatisticsDiscovery;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;
import com.juxin.predestinate.ui.user.paygoods.GoodsListPanel;
import com.juxin.predestinate.ui.user.paygoods.GoodsPayTypePanel;

import java.util.List;

/**
 * 钻石充值弹框
 * Created by Su on 2017/4/12.
 */
public class GoodsDiamondDialog extends BaseActivity implements View.OnClickListener {

    private GoodsListPanel goodsPanel; // 商品列表
    private GoodsPayTypePanel payTypePanel; // 支付方式

    private int needDiamond;  // 送礼需要钻石数
    private int type = GoodsConstant.DLG_DIAMOND_NORMAL;   // 正常展示样式

    private int fromTag = -1; //打开来源
    private long touid = -1; //是否因为某个用户充值 （统计用 可选）
    private String channel_uid; //是否因为某个用户充值渠道id （统计用 可选）

    // 钻石购买在线配置
    private List<Goods> goodsList = ModuleMgr.getCommonMgr().getCommonConfig().getPay().getDiamondList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_goods_diamond_dialog);

        initView();
    }

    private void initView() {
        type = getIntent().getIntExtra(GoodsConstant.DLG_TYPE_KEY, GoodsConstant.DLG_DIAMOND_NORMAL);
        needDiamond = getIntent().getIntExtra(GoodsConstant.DLG_GIFT_NEED, 0);
        fromTag = getIntent().getIntExtra(GoodsConstant.DLG_OPEN_FROM, -1);
        touid = getIntent().getLongExtra(GoodsConstant.DLG_OPEN_TOUID, -1);
        channel_uid = getIntent().getStringExtra(GoodsConstant.DLG_OPEN_CHANNEL_UID);

        LinearLayout ll_diamond_tips = (LinearLayout) findViewById(R.id.ll_diamond_tips);
        TextView tv_decdiamod = (TextView) findViewById(R.id.tv_decdiamod); // 送礼差钻石数
        findViewById(R.id.btn_recharge).setOnClickListener(this);

        if (type == GoodsConstant.DLG_DIAMOND_GIFT_SHORT && needDiamond > 0) {
            ll_diamond_tips.setVisibility(View.VISIBLE);
            tv_decdiamod.setText(getString(R.string.goods_diamond_need, needDiamond));
        }

        fillGoodsPanel();
    }

    private void fillGoodsPanel() {
        // 商品列表
        LinearLayout container = (LinearLayout) findViewById(R.id.pay_type_container);
        goodsPanel = new GoodsListPanel(this);
        container.addView(goodsPanel.getContentView());
        goodsPanel.refresh(goodsList);

        // 支付方式
        payTypePanel = new GoodsPayTypePanel(this, GoodsConstant.PAY_TYPE_OLD);
        container.addView(payTypePanel.getContentView());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_recharge:  // 充值
                UIShow.showPayNoListAct(this, goodsList.get(goodsPanel.getPosition()).getPid(), payTypePanel.getPayType(),
                        touid, channel_uid);
                //统计
                if (fromTag == Constant.OPEN_FROM_HOT) {
                    StatisticsDiscovery.onPayGift(touid, goodsList.get(goodsPanel.getPosition()).getNum(),
                            String.valueOf(goodsList.get(goodsPanel.getPosition()).getCost()), type);
                } else if (fromTag == Constant.OPEN_FROM_CHAT_FRAME) {
                    StatisticsMessage.chatGiveGiftPay(touid, type,
                            goodsList.get(goodsPanel.getPosition()).getNum(),
                            goodsList.get(goodsPanel.getPosition()).getCost());
                }
                finish();
                break;
        }
    }
}
