package com.juxin.predestinate.ui.pay.wepayother.third;

import android.content.Context;

import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.ui.pay.PayConst;
import com.third.wa5.sdk.IThirdCallback;
import com.third.wa5.sdk.ThirdApi;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by IQQ on 2017/5/8.
 */


//微信支付paytype=10  掌付公众号

public class ZFWechatPay {
    private static String time;
    private static Map<String, String> map;
    private static String cpParam = null;

    private static String getTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(date);
    }


    public static void Pay(final Context context, JSONObject json) {
        // 微信直接调用接口
        if (json == null) {
            PToast.showShort("支付失败，请重试。");
            return;
        }
        time = getTime();
        map = new HashMap<>();
        map.put(ThirdApi.CHANNEL_ID, json.optString("channelId"));
        map.put(ThirdApi.KEY, json.optString("key"));
        map.put(ThirdApi.SIGN, json.optString("sign"));
        map.put(ThirdApi.APP_ID, json.optString("appId"));
        map.put(ThirdApi.APP_NAME, context.getResources().getString(R.string.app_name));
        map.put(ThirdApi.PACKAGE_NAME, context.getPackageName());
        map.put(ThirdApi.APP_VERSION, ModuleMgr.getAppMgr().getVerCode() + "");
        map.put(ThirdApi.MONEY, json.optString("money"));
        map.put(ThirdApi.PRICE_POINT_DESC, json.optString("pricePointDec"));
        map.put(ThirdApi.PRICE_POINT_NAME, json.optString("pricePointName"));
        map.put(ThirdApi.QD, json.optString("qd"));
        map.put(ThirdApi.CP_PARAM, json.optString("cpParam"));
        map.put(ThirdApi.TIME, json.optString("time"));

        // 初始化接口
        ThirdApi.getInstance().init(context, map);
        ThirdApi.getInstance().wxp(context, map, new IThirdCallback() {
            @Override
            public void onSuccess() {
                PayConst.payFlag = true;
                PToast.showShort("支付成功！");
                //通知刷个人资料  在
                MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
            }

            @Override
            public void onFailed(String resultCode) {
                if (resultCode.equals("1007")) {
                    PToast.showShort("网络发生故障，请重试！");
                } else if (resultCode.equals("1011")) {
                    PToast.showShort("请检查-微信是否安装！\n是否允许读取列表权限");
                } else {
                    PToast.showShort("支付失败！, 失败码:" + resultCode);
                }
            }
        }, true);
    }

}
