package com.juxin.predestinate.module.local.login;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.juxin.library.log.PSP;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.util.CenterConstant;

import org.json.JSONObject;

/**
 * 获取推广码
 *
 * @author IQQ
 * @date 2017/9/13
 */
public class PromoCode {

    /**
     * 获取分享码[分享码格式：以yfb开头，|进行切割的字符串，|不能为最后一位]
     *
     * @param context 上下文
     * @return 分享码，如果格式不符，返回空字符串
     */
    static String getCode(Context context) {
        String memoStr = getClipboardContent(context);
        String promoCodeStart = "yfb";
        if (memoStr.toLowerCase().startsWith(promoCodeStart) &&
                memoStr.indexOf("|") < memoStr.length() - 1) {
            return memoStr;
        }
        return "";
    }

    /**
     * 获取剪贴板第一条内容
     *
     * @param context 上下文
     * @return 剪贴板第一条内容
     */
    private static String getClipboardContent(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            //getText过期
            if (clipboardManager.hasPrimaryClip()) {
                return String.valueOf(clipboardManager.getPrimaryClip().getItemAt(0).getText());
            }
        }
        return "";
    }

    /**
     * 置空剪贴板
     */
    private static void setClipBoardContentNull(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            ClipData clipData = ClipData.newPlainText(" ", " ");
            clipboardManager.setPrimaryClip(clipData);
        }
    }

    /**
     * 设置分销层级，设置该用户是从那个分享渠道过来的，不进行弹窗提示
     *
     * @param context 上下文
     */
    public static void initPromoUser(final Context context) {
        initPromoUser(context, false);
    }

    /**
     * 根据推广码获取服务器口令并持行。
     *
     * @param context 上下文
     * @param ifPop   是否弹窗提示
     */
    public static void initPromoUser(final Context context, final Boolean ifPop) {
        String code = getCode(context);
        if ("".equals(code)) {
            return;
        }
        ModuleMgr.getCommonMgr().extractShareinfo(code, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (!response.isOk()) {
                    return;
                }
                JSONObject jsonObject = response.getResponseJsonRes();
                if (null == jsonObject) {
                    return;
                }
                String cmd = jsonObject.optString("cmd");
                if ("".equals(cmd)) {
                    return;
                }
                if (cmd.contains(CenterConstant.USER_SHARE_UID)) {
                    String[] str = cmd.split("=");
                    if (str.length == 2) {
                        PSP.getInstance().put(CenterConstant.USER_SHARE_UID, str[1]);
                        setClipBoardContentNull(context);
                        if (ifPop) {
                            UIShow.showSureUserDlg((FragmentActivity) context);
                        }
                    }
                }else if(cmd.contains(CenterConstant.USER_SHARE_LIVE)){
                    String[] str = cmd.split("=");
                    if (str.length == 2) {
//                        PSP.getInstance().put(CenterConstant.USER_SHARE_UID, str[1]);
                        setClipBoardContentNull(context);
                        if (ifPop) {
                            UIShow.showSureLiveRoomDlg(context,Long.parseLong(str[1]),"");
                        }
                    }
                }
            }
        });
    }
}
