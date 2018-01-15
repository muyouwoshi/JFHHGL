package com.juxin.predestinate.module.local.msgview.chatview.input;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.library.utils.InputUtils;
import com.juxin.library.utils.TypeConvertUtil;
import com.juxin.library.utils.ViewUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.config.VideoVerifyBean;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.base.ChatViewPanel;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.mail.unlock.UnlockComm;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;

/**
 * 输入面板
 * Created by Kind on 2017/3/30.
 */
public class ChatInputPanel extends ChatViewPanel implements View.OnClickListener, View.OnTouchListener, TextWatcher, PObserver {

    private View chatBtnVoice = null;
    private View chatBtnText = null;

    private View chatBtnExpression = null;

    private TextView chatVoiceRecord = null;
    private EditText chatTextEdit = null;

    private View chatBtnExtend = null;
    private View chatBtnSend = null;

    private View bgline = null;
    //    private View bg = null;
    private View input_monthly = null;

    public ChatInputPanel(Context context, ChatAdapter.ChatInstance chatInstance) {
        super(context, chatInstance);
        chatInstance.chatInputPanel = this;
        setContentView(R.layout.p1_chat_input);
        initView();
    }

    /**
     * 初始化所有需要显示的View。
     */
    public void initView() {
        bgline = findViewById(R.id.bg_line);
//        bg = findViewById(R.id.bg);
        input_monthly = findViewById(R.id.input_monthly);

        //@author Mr.Huang
        //@updateDate 2017-09-04
        TextView tv = ViewUtils.findById(input_monthly, R.id.tv_text);
        ImageView iv = ViewUtils.findById(input_monthly, R.id.iv_icon);
//        if (ModuleMgr.getCenterMgr().getMyInfo().isB()) {
//            tv.setText(Html.fromHtml("充值VIP，解锁与此美女聊天"));
//            iv.setBackgroundResource(R.drawable.f2_lockimg);
//        } else {
//            iv.setBackgroundResource(R.drawable.f2_lockimg);
//            tv.setText("解锁并索要联系方式");
//        }

        iv.setBackgroundResource(R.drawable.f2_lockimg);
        tv.setText("回复并索要联系方式");

        chatBtnVoice = findViewById(R.id.chat_voice);
        chatBtnText = findViewById(R.id.chat_text);

        chatBtnExpression = findViewById(R.id.chat_expression);

        chatVoiceRecord = (TextView) findViewById(R.id.chat_voice_record);
        chatTextEdit = (EditText) findViewById(R.id.chat_text_edit);

        chatBtnExtend = findViewById(R.id.chat_extend);
        chatBtnSend = findViewById(R.id.chat_send);

        input_monthly.setOnClickListener(this);
        chatBtnVoice.setOnClickListener(this);
        chatBtnText.setOnClickListener(this);

        chatBtnExpression.setOnClickListener(this);
        chatVoiceRecord.setOnClickListener(this);

        chatTextEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    onClickChatTextEdit();
                }
                return false;
            }
        });
        chatBtnExtend.setOnClickListener(this);
        chatBtnSend.setOnClickListener(this);

        chatVoiceRecord.setOnTouchListener(this);
        chatTextEdit.addTextChangedListener(this);

        showSendBtn(false);

        onClickChatGift();
        onClickRedPackage();
    }

    /**
     * 添加一个表情到输入框中。
     *
     * @param simleItem 表情文件。
     */
    public void addSmile(EmojiPack.EmojiItem simleItem) {
        try {
            chatTextEdit.append(ChatSmile.getSmiledText(getContext(), simleItem.key, UIUtil.dp2px(20)));
        } catch (Exception e) {
        }
    }

    public void showSendBtn() {
        try {
            String context = chatTextEdit.getText().toString();
            context = context.trim();

            showSendBtn(!TextUtils.isEmpty(context));
        } catch (Exception e) {
        }
    }

    /**
     * 显示或者隐藏输入按钮。
     *
     * @param show 是否显示输入按钮。
     */
    private void showSendBtn(boolean show) {
        if (!getChatInstance().chatAdapter.isShowExtend()) {
            chatBtnExtend.setVisibility(View.GONE);
            chatBtnSend.setVisibility(View.VISIBLE);
            return;
        }

        if (show) {
            chatBtnSend.setVisibility(View.VISIBLE);
            chatBtnExtend.setVisibility(View.GONE);
        } else {
            chatBtnExtend.setVisibility(View.VISIBLE);
            chatBtnSend.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_voice:
                Statistics.userBehavior(SendPoint.chatframe_tool_btnvoice, TypeConvertUtil.toLong(getChatInstance().chatAdapter.getWhisperId()));
                onClickChatVoice();
                break;
            case R.id.chat_text:
                onClickChatText();
                break;
            case R.id.chat_expression:
                showChatExpression();
                break;
            case R.id.chat_voice_record:
                onClickChatVoiceRecord();
                break;
            case R.id.chat_text_edit:
                onClickChatTextEdit();
                break;
            case R.id.chat_extend:
                onClickChatExtend();
                break;
            case R.id.chat_send:
                onClickChatSend();
                break;
            case R.id.input_monthly:
                try {
                    long otherID = getChatInstance().chatAdapter.getLWhisperId();
                    Statistics.userBehavior(SendPoint.page_chat_unlock, otherID);

                    Context context = getContext();
                    if (!(context instanceof FragmentActivity)) {
                        return;
                    }
                    FragmentActivity activity = (FragmentActivity) context;

                    UnlockComm.getCanMsgAndShow(activity, otherID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private String channelId = "";
    private String whisperId = "";
    private int know = 0;
    private ChatRecordPanel chatRecordPanel = null;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ChatMediaPlayer.getInstance().stopPlayVoice();
                getChatInstance().chatAdapter.moveToBottom();

                chatVoiceRecord.setText("松开结束");
                chatVoiceRecord.setPressed(true);

                channelId = getChatInstance().chatAdapter.getChannelId();
                whisperId = getChatInstance().chatAdapter.getWhisperId();
                know = getChatInstance().chatAdapter.getChatInfo().getKnow();

                chatRecordPanel = getChatInstance().chatRecordPanelUser;

                if (chatRecordPanel == null) {
                    chatRecordPanel = getChatInstance().chatRecordPanel;
                }

                chatRecordPanel.onTouch(action, 0f);
                break;

            case MotionEvent.ACTION_MOVE:
                chatRecordPanel.onTouch(action, event.getY());
                break;

            case MotionEvent.ACTION_UP:
                chatVoiceRecord.setText("按住说话");
                chatVoiceRecord.setPressed(false);
                chatRecordPanel.onTouch(action, event.getY(), channelId, whisperId, know);
                break;

            default:
                chatVoiceRecord.setText("按住说话");
                chatVoiceRecord.setPressed(false);
                getChatInstance().chatRecordPanel.onTouch(action, 0f);
                return false;
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        sendSystemMsgTyping();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean empty = TextUtils.isEmpty(s);

        showSendBtn(!empty);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > Constant.CHAT_TEXT_LIMIT) {
            PToast.showShort("字数超出限制");
        }
    }

    public void sendSystemMsgTyping() {
        if (getChatInstance().chatAdapter.isTyping()) {
            getChatInstance().chatAdapter.setTyping(false);
        }
    }

    public void sendSystemMsgCancelInput() {
        if (!getChatInstance().chatAdapter.isTyping()) {
            getChatInstance().chatAdapter.setTyping(true);
        }
    }

    /**
     * 切换到语音输入模式。0
     */
    private void onClickChatVoice() {
        chatBtnVoice.setVisibility(View.INVISIBLE);
        chatTextEdit.setVisibility(View.GONE);

        chatBtnText.setVisibility(View.VISIBLE);

        chatVoiceRecord.setVisibility(View.VISIBLE);

        showSendBtn(false);
        closeAllInput();

        getChatInstance().chatAdapter.moveToBottom();
    }

    /**
     * 切换到文本输入模式。
     */
    private void onClickChatText() {
        chatBtnVoice.setVisibility(View.VISIBLE);
        chatTextEdit.setVisibility(View.VISIBLE);

        chatBtnText.setVisibility(View.INVISIBLE);
        chatVoiceRecord.setVisibility(View.INVISIBLE);

        showSendBtn();

        getChatInstance().chatAdapter.moveToBottom();
    }

    /**
     * 打开表情面板。
     */
    public void showChatExpression() {
        Statistics.userBehavior(SendPoint.chatframe_tool_face, getChatInstance().chatAdapter.getLWhisperId());

        chatTextEdit.setVisibility(View.VISIBLE);
        InputUtils.HideKeyboard(chatTextEdit);
        getChatInstance().chatExtendPanel.show(false);
        getChatInstance().chatSmilePanel.showToggle();

        getChatInstance().chatAdapter.moveToBottom();
    }

    /**
     * 发送语音消息，走OnTouch事件，此处不起作用。
     */
    private void onClickChatVoiceRecord() {
    }

    private void onClickChatTextEdit() {
        getChatInstance().chatExtendPanel.show(false);
        getChatInstance().chatSmilePanel.show(false);

        // 键盘弹出需要时间
        MsgMgr.getInstance().delay(new Runnable() {
            @Override
            public void run() {
                getChatInstance().chatAdapter.moveToBottom();
            }
        }, 500);
    }

    /**
     * 打开扩展功能框。
     */
    private void onClickChatExtend() {
        onClickChatText();

        InputUtils.HideKeyboard(chatTextEdit);
        getChatInstance().chatExtendPanel.show(true);
        getChatInstance().chatSmilePanel.show(false);

        getChatInstance().chatAdapter.moveToBottom();
    }

    public void closeAllInput() {
        try {
            sendSystemMsgCancelInput();
            InputUtils.HideKeyboard(chatTextEdit);
            getChatInstance().chatExtendPanel.show(false);
            getChatInstance().chatSmilePanel.show(false);

        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }

    /**
     * 发送消息。
     */
    private void onClickChatSend() {
        try {
            String context = chatTextEdit.getText().toString().trim();
            StatisticsMessage.chatSendBtn(context, getChatInstance().chatAdapter.getLWhisperId());

            if (TextUtils.isEmpty(context)) {
                return;
            }
            if (context.length() > Constant.CHAT_TEXT_LIMIT) {
                PToast.showShort("字数超出限制,请分条发送.");
                return;
            }

            String channelID = getChatInstance().chatAdapter.getChannelId();
            String whisperID = getChatInstance().chatAdapter.getWhisperId();

            ModuleMgr.getChatMgr().sendTextMsg(channelID, whisperID, context, getChatInstance().chatAdapter.getChatInfo().getKnow());
            chatTextEdit.setText("");
            sendSystemMsgCancelInput();
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }

    /**
     * 发送礼物
     */
    private void onClickChatGift() {
        getChatInstance().chatViewLayout.onClickChatGift(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Statistics.userBehavior(SendPoint.chatframe_tool_btngift,
                        getChatInstance().chatAdapter.getLWhisperId());
                SourcePoint.getInstance().lockSource((Activity) getContext(), "gift");
                PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.GET_RED_BAG_TIP), false);
                MsgMgr.getInstance().sendMsg(MsgType.MT_GET_BAG_TIP, "");
                closeAllInput();

                long otherId = getChatInstance().chatAdapter.getLWhisperId();

                UserInfoLightweight info = getChatInstance().chatAdapter.getUserInfo(otherId);
                UIShow.showBottomGiftDlg(getContext(), otherId, Constant.OPEN_FROM_CHAT_FRAME,
                        info == null ? "" : String.valueOf(info.getChannel_uid()));
            }
        });
    }

    /**
     * -1: 标识还没有设置此属性的值，这个值在PrivateChatAct.java中设置，他会请求当前聊天用户的信息，里面包括了是否解锁的属性
     * 0: 未解锁
     * 1: 已解锁
     */
    private int isUnlocked = -1;

    /**
     * 当前聊天对象的UID
     */
    private long tuid;

    public void setUnlocked(boolean isUnlocked) {
        this.isUnlocked = (isUnlocked ? 1 : 0);
    }

    public void setTUID(long tuid) {
        this.tuid = tuid;
    }

    /**
     * 男号--看看她 / 女号--邀请他
     */
    private void onClickLookAtHer() {
        getChatInstance().chatViewLayout.onClickLookAtHer(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAllInput();
                boolean isInviteHe = false;
                if (!ModuleMgr.getCenterMgr().getMyInfo().isMan()) {//女号--邀请他
                    isInviteHe = true;
                    VideoVerifyBean bean = ModuleMgr.getCommonMgr().getVideoVerify();
                    if ((!bean.getBooleanVideochat() && !bean.getBooleanAudiochat())) {
                        PToast.showShort("请在设置中开启视频、语音通话");
                        return;
                    }
                }
                SourcePoint.getInstance().lockSource(App.activity, "lookta");
                long whisperId = getChatInstance().chatAdapter.getLWhisperId();
                UserInfoLightweight info = getChatInstance().chatAdapter.getUserInfo(whisperId);
                VideoAudioChatHelper.getInstance().inviteVAChat((Activity) getContext(), whisperId, AgoraConstant.RTC_CHAT_VIDEO, true,
                        Constant.APPEAR_TYPE_NO, info == null ? "" : String.valueOf(info.getChannel_uid()), isInviteHe,
                        SourcePoint.getInstance().getSource());
            }
        });
    }

    /**
     * @updateAuthor Mr.Huang
     * @updateDate 2017-07-14
     * 修改成点击红包的逻辑和邀请逻辑
     */
    private void onClickRedPackage() {
//        if (!ModuleMgr.getCenterMgr().getMyInfo().isB()) {
        if (true) {
            onClickLookAtHer();
            return;
        }
        getChatInstance().chatViewLayout.onClickRedPackage(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Statistics.userBehavior(SendPoint.page_chat_redpackage, tuid);
                closeAllInput();
                if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                    //如果是男用户弹出红包弹窗
                    if (isUnlocked == -1) {
                        //没有设置是否解锁聊天用户不做任何处理
                        return;
                    }
                    Context context = getContext();
                    if (context instanceof FragmentActivity) {
                        long whisperId = getChatInstance().chatAdapter.getLWhisperId();
                        UserInfoLightweight info = getChatInstance().chatAdapter.getUserInfo(whisperId);

//                        ABTestBottomDialog dialog = new ABTestBottomDialog((FragmentActivity) context);
//                        dialog.showRedPackage(tuid, info == null ? "" : String.valueOf(info.getChannel_uid()), isUnlocked == 1);
                        if (!ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
                            UIShow.showGoodsVipBottomDlg(getContext(), GoodsConstant.DLG_VIP_CHOOSE_B);
                        } else {
                            UIShow.showPrivateChatAct(context, tuid, null);
                        }
                    }
                } else {
                    //女用户邀请
                    VideoVerifyBean bean = ModuleMgr.getCommonMgr().getVideoVerify();
                    if ((!bean.getBooleanVideochat() && !bean.getBooleanAudiochat())) {
                        PToast.showShort("请在设置中开启视频、语音通话");
                        return;
                    }
                    long whisperId = getChatInstance().chatAdapter.getLWhisperId();
                    UserInfoLightweight info = getChatInstance().chatAdapter.getUserInfo(whisperId);
                    VideoAudioChatHelper.getInstance().inviteVAChat((Activity) getContext(),
                            whisperId,
                            AgoraConstant.RTC_CHAT_VIDEO,
                            true,
                            Constant.APPEAR_TYPE_NO, info == null ? "" : String.valueOf(info.getChannel_uid()),
                            true,
                            SourcePoint.getInstance().getSource());

                }
            }
        });
    }

    /**
     * 清空输入框内容。
     */
    public void clearInputEdit() {
        chatTextEdit.setText("");
    }

    public void setBg(int bgColor, int bgLineColor) {
//        this.bg.setBackgroundResource(bgColor);
        this.bgline.setBackgroundResource(bgLineColor);
    }

    /**
     * 设置输入内容。
     *
     * @param text
     */
    public void setInputText(String text) {
        chatTextEdit.setText(text);
        chatTextEdit.setSelection(text.length());
        chatTextEdit.requestFocus();
    }

    public EditText getChatTextEdit() {
        return chatTextEdit;
    }

    /**
     * 是否可以看信
     */
    public void showChat(boolean isCanChat) {
        if (!isCanChat) {//不能回复信息
            input_monthly.setVisibility(View.VISIBLE);
//            bg.setVisibility(View.GONE);
        } else {//能回复信息
            input_monthly.setVisibility(View.GONE);
//            bg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 如果是小秘书都不显示
     */
    public void showInputGONE() {
        input_monthly.setVisibility(View.GONE);
//        bg.setVisibility(View.GONE);
    }

    public void attach() {
        MsgMgr.getInstance().attach(this);
    }

    public void detach() {
        MsgMgr.getInstance().detach(this);
    }

    @Override
    public void onMessage(String key, Object value) {
        if (MsgType.MT_TASK_TO_JUMP.equalsIgnoreCase(key)) {
            onClickChatExtend();
        }
    }
}