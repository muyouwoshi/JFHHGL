package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.GiftMessage;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 索要礼物消息，只能出现在发送左侧
 * Created by Kind on 2017/5/10.
 */
public class ChatPanelGiveMeGift extends ChatPanel {

    private ImageView iv_gift_img;
    private TextView tv_gift_hello, tv_gift_content;
    private View rl_container;

    public ChatPanelGiveMeGift(Context context, ChatAdapter.ChatInstance chatInstance, boolean sender) {
        super(context, chatInstance, R.layout.f1_chat_item_panel_give_me_gift, sender);
        setShowParentLayout(false);
    }

    @Override
    public void initView() {
        iv_gift_img = (ImageView) findViewById(R.id.iv_gift_img);
        tv_gift_hello = (TextView) findViewById(R.id.tv_gift_hello);
        tv_gift_content = (TextView) findViewById(R.id.tv_gift_content);
        rl_container = findViewById(R.id.rl_container);
        if (isSender()) {//防止数据库出错，索要礼物消息展示在右侧的情况
            rl_container.setBackgroundResource(R.drawable.y1_talk_box_24x24_me);
            rl_container.setPadding(0, 0, 0, 0);
        }
        getContentView().setVisibility(View.INVISIBLE);
        int wid = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.AT_MOST);
        int hei = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        rl_container.measure(wid, hei);
        float scale = PSP.getInstance().getFloat("MAXWIDTHTWO", -1);//获取宽度
        if (scale > 0) {
            setTvWidth(scale);
        } else {
            rl_container.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    private ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { //监听布局宽度
        @Override
        public void onGlobalLayout() {
            int contentViewWidth = getContentView().getMeasuredWidth();
            int llTaskWidth = rl_container.getMeasuredWidth();
            if (contentViewWidth != 0 && llTaskWidth != 0 && llTaskWidth > contentViewWidth) {
                float maxWidth = contentViewWidth;
                PSP.getInstance().put("MAXWIDTHTWO", maxWidth); //保存宽度
                setTvWidth(maxWidth);
            } else if (contentViewWidth != 0 && llTaskWidth != 0 && llTaskWidth <= contentViewWidth) {
                PSP.getInstance().put("MAXWIDTHTWO", llTaskWidth); //保存宽度
                setTvWidth(llTaskWidth);
            }
            if (contentViewWidth != 0 && llTaskWidth != 0) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    rl_container.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
                } else {
                    rl_container.getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
                }
            }
        }
    };

    private void setTvWidth(float width) {
        if (rl_container.getLayoutParams() != null && getContentView() != null) {
            if (rl_container.getMeasuredWidth() != 0) {
                width = width <= rl_container.getMeasuredWidth() ? width : rl_container.getMeasuredWidth();
                if (width > rl_container.getMeasuredWidth()) {
                    rl_container.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
                }
            }
            rl_container.getLayoutParams().width = (int) width;
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
            return false;
        }

        ImageLoader.loadFitCenter(context, giftInfo.getPic(), iv_gift_img);
        tv_gift_hello.setText(ModuleMgr.getCenterMgr().getMyInfo().isMan() ?
                getContext().getString(R.string.chat_gift_hello_man)
                : getContext().getString(R.string.chat_gift_hello_woman));
        tv_gift_content.setText(Html.fromHtml("想要<font color='#FD698C'>"
                + (msg.getGiftCount() == 0 ? 1 : msg.getGiftCount()) +
                "个" + giftInfo.getName() + "</font>"));
        return true;
    }

    @Override
    public boolean onClickContent(BaseMessage msgData, boolean longClick) {
        if (msgData == null || !(msgData instanceof GiftMessage)) {
            return false;
        }
        GiftMessage msg = (GiftMessage) msgData;

        SourcePoint.getInstance().lockSource(App.activity, "content_sendgift");
        UserInfoLightweight info = getChatInstance().chatAdapter.getUserInfo(msg.getLWhisperID());
        UIShow.showDiamondSendGiftDlg(App.getActivity(), msg.getGiftID(), msg.getWhisperID(),
                info == null ? "" : String.valueOf(info.getChannel_uid()));
        return true;
    }
}
