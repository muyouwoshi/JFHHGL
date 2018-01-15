package com.juxin.predestinate.ui.mail.unlock;

import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.util.ShareUtil;
import com.juxin.predestinate.ui.share.QQShareCallBack;

/**
 * 创建日期：2017/11/7
 * 描述:
 *
 * @author :lc
 */
public class UnlockShareQQShareCallBack extends QQShareCallBack<UnlockShareQQShareCallBack.UnlockShareShareCodeCallBack>{

    public UnlockShareQQShareCallBack() {
        super(new UnlockShareShareCodeCallBack());
    }

    public static class UnlockShareShareCodeCallBack implements ShareUtil.ShareCodeCallBack{

        @Override
        public void onShareCodeCallBackSuccess(int pkg, float amount, int channel) {
            if (UnlockComm.getShareNum() == 0) {//解锁弹窗
                UnlockComm.sendUnlockMessage("1", 0);
                UnlockComm.setShareNum(UnlockComm.getShareNum() + 1);
            }
        }

        @Override
        public void onShareCodeCallBackFailed(HttpResponse response) {

        }
    }
}
