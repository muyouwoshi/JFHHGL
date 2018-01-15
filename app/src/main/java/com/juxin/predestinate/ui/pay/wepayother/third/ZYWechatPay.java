package com.juxin.predestinate.ui.pay.wepayother.third;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.ui.pay.PayConst;
import com.zy.i.IPayResult;
import com.zy.pay.ZYPayTools;

import org.json.JSONObject;


/**
 * Created by IQQ on 2017/5/8.
 */


//微信支付paytype=12  卓越 wap

public class ZYWechatPay {
    public static void Pay(final Context context, JSONObject json) {
        //支付类型 微信 WAP：ZYPayTools.PAY_TYPE_WX 微信扫码：ZYPayTools.PAY_TYPE_WXSCAN 支付宝：WFTPayTools.PAY_TYPE_ALI
        int payType = ZYPayTools.PAY_TYPE_WX;
        String outTradeNum = json.optString("outTradeNum"); //商户订单号
        int totalFee = json.optInt("totalFee"); //订单金额单位：分
        String goodsName = json.optString("goodsName");//商品名称（不能为中文）
        String notifyUrl = json.optString("notifyUrl");//异步通知接口
        String attach = json.optString("attach"); //透传参数
        LoadingDialog.show((FragmentActivity) context);
        new ZYPayTools((Activity) context).startPay(ZYPayTools.PAY_TYPE_WX, outTradeNum, totalFee, goodsName, notifyUrl, attach, new IPayResult() {
            @Override
            public void PayResult(boolean isPay) {
                LoadingDialog.closeLoadingDialog();
                if (isPay) {
                    PayConst.payFlag = true;
                    PToast.showShort("支付成功");
                    //通知刷个人资料  在
                    MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
                } else {
                    PToast.showShort("支付失败");
                }
            }
        });

    }
}
