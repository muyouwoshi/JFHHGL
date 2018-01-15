package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.os.Build;
import android.text.Html;
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
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.db.DBCallback;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.GiftMessage;
import com.juxin.predestinate.module.local.chat.msgtype.TextMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIUtil;

/**
 * 礼物消息展示panel
 * Created by Kind on 2017/5/10.
 */
public class ChatPanelGift extends ChatPanel {

    private ImageView iv_gift_img;
    private TextView tv_gift_hello, tv_gift_content, tv_gift_status;
    private LinearLayout llBottom;

    public ChatPanelGift(Context context, ChatAdapter.ChatInstance chatInstance, boolean sender) {
        super(context, chatInstance, R.layout.f1_chat_item_panel_gift, sender);
    }

    @Override
    public void initView() {
        iv_gift_img = (ImageView) findViewById(R.id.iv_gift_img);
        tv_gift_hello = (TextView) findViewById(R.id.tv_gift_hello);
        tv_gift_content = (TextView) findViewById(R.id.tv_gift_content);
        tv_gift_status = (TextView) findViewById(R.id.tv_gift_status);
        llBottom = (LinearLayout) findViewById(R.id.ll_gift_bottom);

        getContentView().setVisibility(View.INVISIBLE);
        int wid = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.AT_MOST);
        int hei = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        llBottom.measure(wid, hei);
        if (isSender()) {
            tv_gift_hello.setTextColor(getContext().getResources().getColor(R.color.white));
            tv_gift_content.setTextColor(getContext().getResources().getColor(R.color.color_EEEEEE));
            tv_gift_status.setVisibility(View.GONE);
        } else {
            tv_gift_hello.setTextColor(getContext().getResources().getColor(R.color.color_040000));
            tv_gift_content.setTextColor(getContext().getResources().getColor(R.color.color_777777));
            tv_gift_status.setVisibility(View.VISIBLE);
        }
        getContentView().setVisibility(View.INVISIBLE);
        float scale = PSP.getInstance().getFloat("MAXWIDTH", -1);//获取宽度
        if (scale > 0) {
            setTvWidth(scale);
        } else {
            llBottom.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    private ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { //监听布局宽度
        @Override
        public void onGlobalLayout() {
            int contentViewWidth = getContentView().getMeasuredWidth();
            int llTaskWidth = llBottom.getMeasuredWidth();
            if (contentViewWidth != 0 && llTaskWidth != 0 && llTaskWidth > contentViewWidth) {
                float maxWidth = contentViewWidth;
                PSP.getInstance().put("MAXWIDTH", maxWidth); //保存宽度
                setTvWidth(maxWidth);
            } else if (contentViewWidth != 0 && llTaskWidth != 0 && llTaskWidth <= contentViewWidth) {
                PSP.getInstance().put("MAXWIDTH", llTaskWidth); //保存宽度
                setTvWidth(llTaskWidth);
            }
            if (contentViewWidth != 0 && llTaskWidth != 0) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    llBottom.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
                } else {
                    llBottom.getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
                }
            }
        }
    };

    private void setTvWidth(float width) {
        if (llBottom.getLayoutParams() != null && getContentView() != null) {
            if (llBottom.getMeasuredWidth() != 0) {
                width = width <= llBottom.getMeasuredWidth() ? width : llBottom.getMeasuredWidth();
                if (width > llBottom.getMeasuredWidth()) {
                    llBottom.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
                }
            }
            llBottom.getLayoutParams().width = (int) width;
        }
        getContentView().setVisibility(View.VISIBLE);
    }

    @Override
    public boolean reset(BaseMessage msgData, UserInfoLightweight infoLightweight) {
        if (msgData == null || !(msgData instanceof GiftMessage)) return false;

        GiftMessage msg = (GiftMessage) msgData;
        GiftsList.GiftInfo giftInfo = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo(msg.getGiftID());
        if (giftInfo == null || !giftInfo.isHasData()) {
            PLogger.d("------>gift list is empty or gift list doesn't have this gift_id.");
            setVisibility(View.GONE);
            return false;
        }
        tv_gift_status.setText(msg.isGiftAutoReceived() ? getContext().getString(R.string.gift_has_auto_received)
                : (msgData.getfStatus() == 0 ? getContext().getString(R.string.gift_has_received)
                : getContext().getString(R.string.gift_click_to_receive)));
        ImageLoader.loadFitCenter(context, giftInfo.getPic(), iv_gift_img);

        if (((GiftMessage) msgData).getGiftFtype() == 6) {
            tv_gift_hello.setText(isSender()
                    ? getContext().getString(R.string.exceptional_unlock)
                    : Html.fromHtml(getContext().getString(R.string.receive)
                    + "<font color='#FD698C'>"
                    + (msg.getGiftCount() == 0 ? 1 : msg.getGiftCount()) +
                    getContext().getString(R.string.num) + giftInfo.getName() + "</font>"));

            tv_gift_content.setText(isSender() ? Html.fromHtml(getContext().getString(R.string.send_out)
                    + "<font color='#FD698C'>"
                    + (msg.getGiftCount() == 0 ? 1 : msg.getGiftCount()) +
                    getContext().getString(R.string.num) + giftInfo.getName() + "</font>") :
                    getContext().getString(R.string.from_asking_for_unlocking));
        } else {
            tv_gift_hello.setText(isSender()
                    ? (ModuleMgr.getCenterMgr().getMyInfo().isMan()
                    ? getContext().getString(R.string.chat_gift_hello_woman)
                    : getContext().getString(R.string.chat_gift_hello_man))
                    : (ModuleMgr.getCenterMgr().getMyInfo().isMan()
                    ? getContext().getString(R.string.chat_gift_hello_man)
                    : getContext().getString(R.string.chat_gift_hello_woman)));
            tv_gift_content.setText(Html.fromHtml(getContext().getString(R.string.send_you)
                    + "<font color='#FD698C'>"
                    + (msg.getGiftCount() == 0 ? 1 : msg.getGiftCount()) +
                    getContext().getString(R.string.num) + giftInfo.getName() + "</font>"));
        }
        return true;
    }

    @Override
    public boolean onClickContent(final BaseMessage msgData, boolean longClick) {
        if (msgData == null || !(msgData instanceof GiftMessage) || isSender()
                || msgData.getfStatus() == 0 || ((GiftMessage) msgData).isGiftAutoReceived()) {
            return false;
        }
        msgData.setfStatus(0);
        //别人给你发的礼物
        final GiftMessage msg = (GiftMessage) msgData;
        GiftsList.GiftInfo giftInfo = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo(msg.getGiftID());
        if (giftInfo == null || !giftInfo.isHasData()) {
            PLogger.d("------>gift list is empty or gift list doesn't have this gift_id.");
            return false;
        }

        ModuleMgr.getCommonMgr().receiveGift(msg.getGiftLogID(), giftInfo.getName(), msg.getGiftID(), new RequestComplete() {
            @Override
            public void onRequestComplete(final HttpResponse response) {
                if (!response.isOk()) {
                    msgData.setfStatus(1);
                    PToast.showShort(response.getMsg());
                    return;
                }

                ModuleMgr.getChatMgr().updateMsgFStatus(msg.getMsgID(), new DBCallback() {
                    @Override
                    public void OnDBExecuted(long result) {
                        if (result != MessageConstant.OK) {
                            return;
                        }

                        MsgMgr.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_gift_status.setText(getContext().getString(R.string.gift_has_received));
                            }
                        });

                        // 往数据库插一条14提示消息，标识已接受礼物
                        TextMessage textMessage = new TextMessage();
                        textMessage.setWhisperID(msgData.getWhisperID());
                        textMessage.setSendID(App.uid);
                        textMessage.setMsgDesc(getContext().getString(R.string.chat_gift_has_received));
                        textMessage.setcMsgID(BaseMessage.getCMsgID());
                        textMessage.setType(BaseMessage.BaseMessageType.hint.getMsgType());
                        textMessage.setJsonStr(textMessage.getJson(textMessage));
                        ModuleMgr.getChatMgr().onLocalReceiving(textMessage);

                    }
                });
            }
        });
        return true;
    }

    @Override
    public boolean onClickErrorResend(final BaseMessage msgData) {
        if (msgData == null || !(msgData instanceof GiftMessage)) {
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
