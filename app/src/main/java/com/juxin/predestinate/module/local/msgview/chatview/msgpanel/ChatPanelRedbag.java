package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.db.DBCallback;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.GiftMessage;
import com.juxin.predestinate.module.local.chat.msgtype.GiftRedPackageMesaage;
import com.juxin.predestinate.module.local.chat.msgtype.TextMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.ChineseFilter;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.my.CommonDlg.GiftRedPackageDlg;
import com.juxin.predestinate.ui.user.my.CommonDlg.GiftRedPackageStatusDlg;

import java.util.HashMap;

/**
 * 礼物消息展示panel
 * Created by zm on 2017/10/11.
 */
public class ChatPanelRedbag extends ChatPanel {

    private GiftRedPackageMesaage giftRedPackageMesaage;
    private ImageView imgLeft, imgRight;
    private TextView tvTitle;
    private boolean isCanReq = true;

    public ChatPanelRedbag(Context context, ChatAdapter.ChatInstance chatInstance, boolean sender) {
        super(context, chatInstance, R.layout.redbag_chat_redbag, sender);
        setShowParentLayout(false);
    }

    @Override
    public void initView() {
        imgLeft = (ImageView) findViewById(R.id.img_left);
        imgRight = (ImageView) findViewById(R.id.img_right);
        tvTitle = (TextView) findViewById(R.id.tv_invite_tv_title);
    }

    @Override
    public boolean reset(BaseMessage msgData, UserInfoLightweight infoLightweight) {
        if (msgData == null || !(msgData instanceof GiftRedPackageMesaage)) return false;

        if (isSender()) {
            imgLeft.setVisibility(View.INVISIBLE);
            imgRight.setVisibility(View.VISIBLE);
        } else {
            imgLeft.setVisibility(View.VISIBLE);
            imgRight.setVisibility(View.INVISIBLE);
        }
        if (!TextUtils.isEmpty(msgData.getMsgDesc())) {
            tvTitle.setText(msgData.getMsgDesc());
        } else {
            if (isSender()) {
                tvTitle.setText("您送给TA一个红包");
            } else {
                tvTitle.setText("TA送您一个红包");
            }
        }
        isCanReq = true;

        return true;
    }

    @Override
    public boolean onClickContent(final BaseMessage msgData, boolean longClick) {
        if (msgData == null || !(msgData instanceof GiftRedPackageMesaage)) {
            return false;
        }
        giftRedPackageMesaage = (GiftRedPackageMesaage) msgData;
        HashMap<String, Object> params = new HashMap<>();
        double d = (double) (giftRedPackageMesaage.getRed_money()) / 100;
        params.put("money", ChineseFilter.subZeroString(ChineseFilter.formatNum(d, 2) + ""));
        Statistics.userBehavior(SendPoint.page_chat_click_readpackage, params);
        if (isSender()) {
            if (giftRedPackageMesaage.getfStatus() == 0) {
                GiftRedPackageStatusDlg giftRedPackageStatusDlg = new GiftRedPackageStatusDlg(giftRedPackageMesaage.getRed_money(), true, false);
                giftRedPackageStatusDlg.showDialog((FragmentActivity) App.getActivity());
            } else {
                GiftRedPackageStatusDlg giftRedPackageStatusDlg = new GiftRedPackageStatusDlg(giftRedPackageMesaage.getRed_money(), false, false);
                giftRedPackageStatusDlg.showDialog((FragmentActivity) App.getActivity());
            }
        } else {
            if (giftRedPackageMesaage.getfStatus() == 0) {
                GiftRedPackageDlg giftRedPackageDlg = new GiftRedPackageDlg(giftRedPackageMesaage.getRed_money(), true, false);
                giftRedPackageDlg.showDialog((FragmentActivity) App.getActivity());
            } else if (isCanReq) {
                isCanReq = false;
                ModuleMgr.getCommonMgr().receiveRewardHongbao(giftRedPackageMesaage.getRed_id(), new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        if (response.isOk()) {
                            GiftRedPackageDlg giftRedPackageDlg = new GiftRedPackageDlg(giftRedPackageMesaage.getRed_money(), false, false);
                            giftRedPackageDlg.showDialog((FragmentActivity) App.getActivity());
                            giftRedPackageMesaage.setfStatus(0);
                            ModuleMgr.getChatMgr().updateMsgSpecialMsgID(giftRedPackageMesaage.getType(), giftRedPackageMesaage.getSpecialMsgID(), true, null);
                            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_TASK, null);//通知更新密友任务数据
                            getChatInstance().chatContentAdapter.notifyDataSetChanged();
                        } else {
                            isCanReq = true;
                            PToast.showShort(response.getMsg());
                        }
                    }
                });
            }
        }

        return true;
    }

    @Override
    public boolean onClickErrorResend(final BaseMessage msgData) {
        if (msgData == null || !(msgData instanceof GiftRedPackageMesaage)) {
            return false;
        }
        setDialog(msgData, new SimpleTipDialog.ConfirmListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSubmit() {
                if (getChatInstance() != null && getChatInstance().chatContentAdapter != null) {
                    msgData.setStatus(MessageConstant.SENDING_STATUS);
                    getChatInstance().chatContentAdapter.notifyDataSetChanged();
                }
            }
        });
        return true;
    }
}
