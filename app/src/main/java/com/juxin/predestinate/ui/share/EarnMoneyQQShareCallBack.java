package com.juxin.predestinate.ui.share;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.util.ShareUtil;
import com.juxin.predestinate.ui.mail.unlock.UnlockComm;

/**
 * 类描述：
 * 创建时间：2017/11/7 14:46
 * 修改时间：2017/11/7 14:46
 * Created by zhoujie on 2017/11/7
 * 修改备注：
 */

public class EarnMoneyQQShareCallBack extends QQShareCallBack<EarnMoneyQQShareCallBack.EarnMoneyShareCodeCallBack> {
    public EarnMoneyQQShareCallBack() {
        super(new EarnMoneyShareCodeCallBack());
    }

    public static class EarnMoneyShareCodeCallBack implements ShareUtil.ShareCodeCallBack {

        @Override
        public void onShareCodeCallBackSuccess(int pkg, float amount, int channel) {
            PToast.showShort("分享成功");
        }

        @Override
        public void onShareCodeCallBackFailed(HttpResponse response) {
            PToast.showShort("分享失败");
        }
    }
}
