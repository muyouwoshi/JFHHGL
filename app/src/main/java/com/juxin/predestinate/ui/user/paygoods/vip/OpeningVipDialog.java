package com.juxin.predestinate.ui.user.paygoods.vip;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.Goods;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;
import com.juxin.predestinate.ui.user.paygoods.GoodsListPanel;
import com.juxin.predestinate.ui.user.paygoods.GoodsPayTypePanel;

import java.util.List;

/**
 * Created by duanzheng on 2017/7/17.
 * 开通vip
 */
public class OpeningVipDialog extends BaseDialogFragment implements View.OnClickListener {

    private LinearLayout pay_goods_layout, pay_type_layout;
    private GoodsListPanel goodsPanel;
    private GoodsPayTypePanel payTypePanel;

    private LinearLayout root, content;
    private TextView send, tv_look_vip;

    // vip购买在线配置
    private List<Goods> goodsList = ModuleMgr.getCenterMgr().getMyInfo().isB() ?
            ModuleMgr.getCommonMgr().getCommonConfig().getPay().getBBuyVipList() :
            ModuleMgr.getCommonMgr().getCommonConfig().getPay().getBuyVipList();

    public OpeningVipDialog() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, -1);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_openvip_bottom_dlg);
        View contentView = getContentView();
        initView(contentView);
        initData();
        return contentView;
    }

    private void initView(View contentView) {
        send = (TextView) contentView.findViewById(R.id.send);
        pay_goods_layout = (LinearLayout) contentView.findViewById(R.id.pay_goods);
        pay_type_layout = (LinearLayout) contentView.findViewById(R.id.pay_type);
        root = (LinearLayout) contentView.findViewById(R.id.root);
        content = (LinearLayout) contentView.findViewById(R.id.content);
        tv_look_vip = (TextView) contentView.findViewById(R.id.tv_look_vip);
    }

    private void initData() {
        root.setOnClickListener(this);
        content.setOnClickListener(this);

        goodsPanel = new GoodsListPanel(getContext(), GoodsConstant.DLG_VIP_VIDEO_NEW);
        pay_goods_layout.addView(goodsPanel.getContentView());
        goodsPanel.refresh(goodsList);

        payTypePanel = new GoodsPayTypePanel(getContext(), GoodsConstant.PAY_TYPE_NEW);
        pay_type_layout.addView(payTypePanel.getContentView());

        send.setOnClickListener(this);
        tv_look_vip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                dismiss();
                Goods payGood = goodsList.get(goodsPanel.getPosition());
                UIShow.showPayNoListAct(getActivity(), payGood.getPid(), payTypePanel.getPayType(), 0, "");
                StatisticsUser.abTestVipPay(payGood.getPid(), payGood.getCost(), payTypePanel.getPayType());
                break;
            case R.id.content:
                break;
            case R.id.root:
                dismiss();
                break;
            case R.id.tv_look_vip:
                UIShow.showOpenVipActivity(getContext());
                break;
            default:
                break;
        }
    }
}
