package com.juxin.predestinate.ui.share;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.module.util.ShareUtil;
import com.tangzi.sharelibrary.utils.ShareUtils;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

/**
 * 类描述：
 * 创建时间：2017/11/7 13:54
 * 修改时间：2017/11/7 13:54
 * Created by zhoujie on 2017/11/7
 * 修改备注：
 */

public class QQShareCallBack<CallBack extends ShareUtil.ShareCodeCallBack> implements ShareUtils.CallBack, IUiListener {
    /**
     * channel  分享方式：1:微信， 2:朋友圈， 3:QQ， 4:QQ空间
     */
    private int channel = ShareUtil.CHANNEL_QQ_FRIEND;
    /**
     * 分享种类， 0 ：用户信息分享，1：直播分享
     */
    private int scontent;
    /**
     * 0：默认， 1：分享解锁， 2：红包分享
     */
    private int opt;

    private CallBack callBack;
    public QQShareCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getChannel(){
        return channel;
    }

    public int getScontent() {
        return scontent;
    }

    public int getOpt() {
        return opt;
    }

    public void setOpt(int opt) {
        this.opt = opt;
    }

    public void setScontent(int scontent) {
        this.scontent = scontent;
    }
    @Override
    public void onComplete(Object o) {
        onSuccess(o);
    }

    @Override
    public void onError(UiError uiError) {
        PToast.showShort("QQ分享失败");
    }

    @Override
    public void onSuccess(Object o) {
        if(callBack != null){
            ShareUtil.shareCodeCallBack(scontent,opt,channel,callBack);
        }
    }

    @Override
    public void onCancel() {
        PToast.showShort("QQ分享失败");
    }
}
