package com.xiaoyou.friends.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.util.ShareUtil;
import com.juxin.predestinate.module.util.TimerUtil;
import com.juxin.predestinate.ui.mail.unlock.UnlockComm;
import com.tangzi.sharelibrary.utils.ShareUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = WXEntryActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWechat();
    }

    private void initWechat() {
        boolean flag;
        try {
            flag = ShareUtils.getWxApi().handleIntent(getIntent(), this);
        } catch (Exception e) {
            flag = false;
        }
        if (!flag) {
            Log.e(TAG, "无法接收微信端传送过来的数据");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        ShareUtils.getWxApi().handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.e(TAG, "onReq=" + req.toString());
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        ShareUtil.getInstance().onWXResult(resp.errCode);
//        switch (resp.errCode) {
//            case BaseResp.ErrCode.ERR_OK:
//                shareCallback();
//                break;
//            case BaseResp.ErrCode.ERR_USER_CANCEL:
//            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//            case BaseResp.ErrCode.ERR_UNSUPPORT:
//                shareFailed();
//            default:
//                break;
//        }
        finish();
    }

    private void shareFailed() {
        if (ShareUtil.shareFunction == ShareUtil.FUNCTION_REDBAG_SHARE) {
            ShareUtil.setIsCanShare(false);
            redbagShareFailed();
        } else if (ShareUtil.shareFunction == ShareUtil.FUNCTION_UNLOCK_SHARE) {
            unlockShareFailed();
        } else if (ShareUtil.shareFunction == ShareUtil.FUNCTION_LIVE_SHARE) {
            liveShareFailed();
        } else {
            normalShareFailed();
        }
    }

    private void shareCallback() {
        if (ShareUtil.shareFunction == ShareUtil.FUNCTION_REDBAG_SHARE) {
            ShareUtil.setIsCanShare(false);
            redbagShareSuccess();
        } else if (ShareUtil.shareFunction == ShareUtil.FUNCTION_UNLOCK_SHARE) {
            unlockShareSuccess();
        } else if (ShareUtil.shareFunction == ShareUtil.FUNCTION_LIVE_SHARE) {
            liveShareSuccess();
        } else {
            normalShareSuccess();
        }
    }

    private void liveShareSuccess() {
        ShareUtil.shareCodeCallBack(ShareUtil.scontent, ShareUtil.opt, ShareUtil.CHANNEL_WX_FRIEND, new ShareUtil.ShareCodeCallBack() {
            @Override
            public void onShareCodeCallBackSuccess(int pkg, float amount, int channel) {
            }

            @Override
            public void onShareCodeCallBackFailed(HttpResponse response) {
            }
        });
        PToast.showShort("分享成功");
    }

    private void liveShareFailed() {
        PToast.showShort("分享失败");
    }

    private void unlockShareSuccess() {
        ShareUtil.shareCodeCallBack(ShareUtil.scontent, ShareUtil.opt, ShareUtil.CHANNEL_WX_FRIEND, new ShareUtil.ShareCodeCallBack() {
            @Override
            public void onShareCodeCallBackSuccess(int pkg, float amount, int channel) {
                if (UnlockComm.getShareNum() == 0) {
                    UnlockComm.sendUnlockMessage("1", 0);
                    UnlockComm.setShareNum(UnlockComm.getShareNum() + 1);
                }
            }

            @Override
            public void onShareCodeCallBackFailed(HttpResponse response) {
                PToast.showShort("分享失败");
            }
        });
    }

    private void unlockShareFailed() {
        PToast.showShort("分享失败");
    }

    //明哥加
    private void redbagShareSuccess() {
        MsgMgr.getInstance().sendMsg(MsgType.MT_SHARE_SUCCEED, ShareUtil.channel);
    }

    private void redbagShareFailed() {
        MsgMgr.getInstance().sendMsg(MsgType.MT_SHARE_FAILURE, 0);
    }

    private void normalShareSuccess() {
        PToast.showShort("分享成功");
        ShareUtil.shareCodeCallBack(ShareUtil.scontent, ShareUtil.opt, ShareUtil.CHANNEL_WX_FRIEND, null);
    }

    private void normalShareFailed() {
        PToast.showShort("分享失败");
    }
}