package com.xiaoyou.friends.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.ui.pay.PayConst;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Kind on 2017/4/26.
 */

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constant.WEIXIN_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {}

    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:// 表示支付成功
                    PayConst.payFlag = true;
                    PToast.showShort(R.string.pay_errcode_success);
                    // 更新信息
                    MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:// 用户取消支付
                    PToast.showShort(R.string.pay_errcode_cancel);
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    PToast.showShort(R.string.pay_errcode_deny);
                    break;
                default:
                    PToast.showShort(R.string.pay_errcode_unknown);
                    break;
            }
            finish();
        } else {
            WXPayEntryActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
