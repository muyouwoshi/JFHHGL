package com.juxin.predestinate.module.util.my;

import android.util.Log;

import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.bean.my.SendGiftResultInfo;


/**
 * Created by zm on 2017/5/9.
 */

public class GiftHelper {

    public interface OnSendGiftCallback{
        void onSendGiftCallback(boolean isOk,GiftsList.GiftInfo giftInfo,SendGiftResultInfo resultInfo);
    }

    public interface OnRequestGiftListCallback{
        void onRequestGiftListCallback(boolean isOk);
    }

    private static boolean bigDialogIsCanShow = false;//大礼物弹框是否显示（只有在首页和私聊页才显示）

    public static boolean isBigDialogIsCanShow() {
        return bigDialogIsCanShow;
    }

    public static void setBigDialogIsCanShow(boolean bigDialogIsCanShow) {
        GiftHelper.bigDialogIsCanShow = bigDialogIsCanShow;
    }
}
