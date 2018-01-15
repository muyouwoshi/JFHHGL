package com.juxin.predestinate.ui.live.callback;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.util.ShareUtil;
import com.juxin.predestinate.ui.share.QQShareCallBack;

/**
 * Created by chengxiaobo on 2017/11/7.
 */

public class LiveShareQQShareCallBack extends QQShareCallBack {
    public LiveShareQQShareCallBack() {
        super(new LiveShareCodeCallBack());
    }

    public static class LiveShareCodeCallBack implements ShareUtil.ShareCodeCallBack{

        @Override
        public void onShareCodeCallBackSuccess(int pkg, float amount, int channel) {
            PToast.showShort("分享成功" );
        }

        @Override
        public void onShareCodeCallBackFailed(HttpResponse response) {
            PToast.showShort("分享失败" );
        }
    }
}
