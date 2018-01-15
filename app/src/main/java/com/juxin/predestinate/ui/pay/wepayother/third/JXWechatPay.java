package com.juxin.predestinate.ui.pay.wepayother.third;

import android.content.Context;
import android.content.Intent;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.module.local.pay.PayWX;

import java.net.URISyntaxException;
import java.net.URLEncoder;


/**
 * Created by IQQ on 2017/5/8.
 */


//微信支付paytype=9  杰莘公众号

public class JXWechatPay {
    private static String START_INTENT = "#Intent;package=com.tencent.mm;scheme=weixin;i.translate_link_scene=1;end";

    private static String getStartUri(String appID) {
        return "intent://dl/businessWebview/link?appid=" + appID;
    }

    public static void Pay(final Context context, PayWX pay_voice) {
        Intent intent = null;
        String appID = pay_voice.getApp_id();
        String url = URLEncoder.encode(pay_voice.getPayData());
        try {
            intent = Intent.parseUri(getStartUri(appID) + "&url=" + url + START_INTENT, 1);
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.setComponent(null);
            context.startActivity(intent);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            PToast.showShort("支付失败");
        }
    }
}
