package com.juxin.predestinate.ui.user.paygoods.diamond;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.Goods;
import com.juxin.predestinate.bean.live.RoomPayInfo;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.StatisticsManager;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;
import com.juxin.predestinate.ui.user.paygoods.GoodsListPanel;
import com.juxin.predestinate.ui.user.paygoods.GoodsPayTypePanel;

import java.util.List;

/**
 * 钻石底部弹框
 * Created by Su on 2017/8/22.
 */
public class GoodsDiamondBottomDlg extends BaseDialogFragment implements View.OnClickListener {

    private GoodsListPanel goodsPanel; // 商品列表
    private GoodsPayTypePanel payTypePanel; // 支付方式

    // 钻石购买在线配置
    private List<Goods> goodsList = ModuleMgr.getCommonMgr().getCommonConfig().getPay().getDiamondList();

    public GoodsDiamondBottomDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, -1);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_bottom_diamond_dlg);
        initView();
        return getContentView();
    }

    private void initView() {
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_recharge).setOnClickListener(this);
        TextView ycoin_remain = (TextView) findViewById(R.id.ycoin_remain);
        ycoin_remain.setText(String.valueOf(ModuleMgr.getCenterMgr().getMyInfo().getDiamand()));

        // 商品
        LinearLayout container = (LinearLayout) findViewById(R.id.goods_container);
        goodsPanel = new GoodsListPanel(getContext(), GoodsConstant.DLG_DIAMOND_VIDEO);
        container.addView(goodsPanel.getContentView());
        goodsPanel.refresh(goodsList);

        // 支付方式
        LinearLayout payContainer = (LinearLayout) findViewById(R.id.pay_type_container);
        payTypePanel = new GoodsPayTypePanel(getContext(), GoodsConstant.PAY_TYPE_NEW);
        payContainer.addView(payTypePanel.getContentView());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_recharge:  // 充值
                //当配置信息中的数据为null或者数量为0的话给个提示，防止报错奔溃
                if (goodsList == null || goodsList.isEmpty()) {
                    PToast.showLong("无法获取商品信息！");
                    return;
                }
                Goods payGood = goodsList.get(goodsPanel.getPosition());
                setRoomPayInfo(payGood);
                UIShow.showPayNoListAct(getActivity(), payGood.getPid(), payTypePanel.getPayType(), 0, "");
                StatisticsManager.alert_gem_pay(payGood.getPid(), payGood.getCost(), payGood.getNum(), payTypePanel.getPayType());
                dismiss();
                break;

            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    private void setRoomPayInfo(Goods payGood) {
        if (SourcePoint.getInstance().getRoomPaySource(false) != null) {
            RoomPayInfo info = SourcePoint.getInstance().getRoomPaySource(false);
            info.gem_num = payGood.getNum();
            info.pay_type = payTypePanel.getPayType() == GoodsConstant.PAY_TYPE_WECHAT ? GoodsConstant.PAY_TYPE_WECHAT_NAME : GoodsConstant.PAY_TYPE_ALIPAY_NAME;
            info.price = payGood.getCost();
            info.product_id = payGood.getPid();
            SourcePoint.getInstance().lockRoompaySource(info);
        }
    }
}