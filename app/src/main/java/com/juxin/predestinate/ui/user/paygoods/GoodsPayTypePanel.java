package com.juxin.predestinate.ui.user.paygoods;

import android.content.Context;
import android.view.View;

import com.juxin.library.view.BasePanel;
import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

import java.util.List;

/**
 * 支付方式通用panel
 * <p>
 * Created by Su on 2017/5/4.
 */
public class GoodsPayTypePanel extends BasePanel implements View.OnClickListener {

    private int payType = GoodsConstant.PAY_TYPE_WECHAT;  // 充值类型，默认微信充值

    // 本地支持的支付类型列表
    private static final String[] PAY_TYPE_ALL = {
            GoodsConstant.PAY_TYPE_WECHAT_NAME, GoodsConstant.PAY_TYPE_ALIPAY_NAME};

    private List<String> payTypeList;            // 在线配置控制支付类型
    private CustomFrameLayout payWeChat, payAli; // 支付方式

    /**
     * @param itemType 加载布局样式
     */
    public GoodsPayTypePanel(Context context, int itemType) {
        super(context);
        if (itemType == GoodsConstant.PAY_TYPE_NEW) {
            setContentView(R.layout.p1_goods_of_payment);
        } else {
            setContentView(R.layout.f1_goods_pay_type);
        }

        initView();
    }

    private void initView() {
        payTypeList = ModuleMgr.getCommonMgr().getCommonConfig().getPay().getPayTypes();

        payWeChat = (CustomFrameLayout) findViewById(R.id.pay_type_wexin);
        payAli = (CustomFrameLayout) findViewById(R.id.pay_type_alipay);

        initPayType();
        payWeChat.setOnClickListener(this);
        payAli.setOnClickListener(this);

        initDefaultType();
    }

    /**
     * 支付方式显隐
     */
    private void initPayType() {
        if (payTypeList == null) return;

        for (int i = 0; i < PAY_TYPE_ALL.length; i++) {
            if (!payTypeList.contains(PAY_TYPE_ALL[i])) disable(PAY_TYPE_ALL[i]);
        }
    }

    private void disable(String defaultType) {
        // 微信支付
        if (defaultType.equals(GoodsConstant.PAY_TYPE_WECHAT_NAME)) {
            payWeChat.setVisibility(View.GONE);
            return;
        }

        // 支付宝支付
        if (defaultType.equals(GoodsConstant.PAY_TYPE_ALIPAY_NAME)) {
            payAli.setVisibility(View.GONE);
            return;
        }
    }

    /**
     * 默认支付方式
     */
    private void initDefaultType() {
        if (payTypeList == null || payTypeList.isEmpty()) {
            init();
            return;
        }
        String defaultType = payTypeList.get(0);

        // 微信支付
        if (defaultType.equals(GoodsConstant.PAY_TYPE_WECHAT_NAME)) {
            payType = GoodsConstant.PAY_TYPE_WECHAT;
            payWeChat.showOfIndex(GoodsConstant.PAY_STATUS_CHOOSE);
            payAli.showOfIndex(GoodsConstant.PAY_STATUS_UNCHOOSE);
            return;
        }

        // 支付宝支付
        if (defaultType.equals(GoodsConstant.PAY_TYPE_ALIPAY_NAME)) {
            payType = GoodsConstant.PAY_TYPE_ALIPAY;
            payWeChat.showOfIndex(GoodsConstant.PAY_STATUS_UNCHOOSE);
            payAli.showOfIndex(GoodsConstant.PAY_STATUS_CHOOSE);
            return;
        }

    }

    // 未获取到在线配置
    private void init() {
        payType = GoodsConstant.PAY_TYPE_WECHAT;
        payWeChat.showOfIndex(GoodsConstant.PAY_STATUS_CHOOSE);
        payAli.showOfIndex(GoodsConstant.PAY_STATUS_UNCHOOSE);
    }

    /**
     * 获取支付类型
     */
    public int getPayType() {
        return payType;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_type_wexin:
                payType = GoodsConstant.PAY_TYPE_WECHAT;
                payWeChat.showOfIndex(GoodsConstant.PAY_STATUS_CHOOSE);
                payAli.showOfIndex(GoodsConstant.PAY_STATUS_UNCHOOSE);
                break;

            case R.id.pay_type_alipay:
                payType = GoodsConstant.PAY_TYPE_ALIPAY;
                payAli.showOfIndex(GoodsConstant.PAY_STATUS_CHOOSE);
                payWeChat.showOfIndex(GoodsConstant.PAY_STATUS_UNCHOOSE);
                break;
        }
    }
}
