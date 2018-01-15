package com.juxin.predestinate.module.local.msgview.chatview;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.base.ChatContentAdapter;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;
import com.juxin.predestinate.module.util.PickerDialogUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.util.CenterConstant;

/**
 * 只处理聊天内容部分的信息
 * <p>
 * Created by Kind on 2017/3/30.
 */
public abstract class ChatPanel extends ChatBasePanel implements ChatInterface.OnClickChatItemListener {

    private boolean isSender = true;
    protected ChatContentAdapter.ChatItemHolder chatItemHolder = null;

    protected ChatPanel(Context context, ChatAdapter.ChatInstance chatInstance, int layoutId, boolean sender) {
        super(context, chatInstance);

        this.isSender = sender;
        setContentView(layoutId);
    }

    /**
     * 难道整个的Item。
     *
     * @param chatItemHolder
     */
    public void setChatItemHolder(ChatContentAdapter.ChatItemHolder chatItemHolder) {
        this.chatItemHolder = chatItemHolder;
    }

    /**
     * 主动初始化 目前只有语音在用,做自动播的,别地方没有特殊需求不要用
     */
    public void setInit(BaseMessage msgData) {
    }

    /**
     * 判断当前Panel是显示在左侧还是右侧。
     *
     * @return 左侧为true。
     */
    public boolean isSender() {
        return isSender;
    }

    /**
     * 设置Panel显示位置。
     *
     * @param sender 显示位置。
     */
    public void setSender(boolean sender) {
        this.isSender = sender;
    }


    /**
     * 初始化所有需要显示的View。
     */
    public abstract void initView();

    /**
     * 用消息数据重新初始化显示View。
     *
     * @param msgData         最新的消息数据。
     * @param infoLightweight 对应用户的基本信息。
     * @return 如果返回false，表示界面没有完全的被初始化。
     */
    public abstract boolean reset(BaseMessage msgData, UserInfoLightweight infoLightweight);

    /**
     * 当头像被点击的时候调用。
     *
     * @param msgData 消息。
     */
    @Override
    public boolean onClickHead(BaseMessage msgData) {
        if (msgData == null) {
            return true;
        }

        if (getChatInstance().chatAdapter.onClickHead(msgData)) {
            return true;
        }

        if (ChatAdapter.isSender(msgData.getSendID())) {
            //   UIShow.showMyInfoActivity(getChatInstance().context);
            return true;
        }

        if (MailSpecialID.customerService.getSpecialID() != msgData.getLWhisperID()) {
            UIShow.showCheckOtherInfoAct(getChatInstance().context, msgData.getLWhisperID());
        }
        return true;
    }

    /**
     * 当发送状态被点击的时候掉调用。
     *
     * @param msgData 消息。
     */
    @Override
    public boolean onClickStatus(BaseMessage msgData) {
        if (msgData == null) {
            return true;
        }

        if (getChatInstance().chatAdapter.onClickStatus(msgData)) {
            return true;
        }

        return true;
    }

    /**
     * 当整个消息内容被点击时调用。
     *
     * @param msgData 消息。
     */
    @Override
    public boolean onClickContent(final BaseMessage msgData, boolean longClick) {
        if (msgData == null) {
            return true;
        }

        if (getChatInstance().chatAdapter.onClickContent(msgData, longClick)) {
            return true;
        }

        return true;
    }

    /**
     * 消息重发
     *
     * @param msgData
     * @return
     */
    @Override
    public boolean onClickErrorResend(BaseMessage msgData) {
        if (msgData == null) {
            return true;
        }

        if (getChatInstance().chatAdapter.onClickErrorResend(msgData)) {
            return true;
        }

        return true;
    }

    private boolean isShowParentBg = true;

    /**
     * 设置是否显示父布局的背景
     *
     * @param isShowParentBg 是否显示父布局的背景
     */
    public void setShowParentLayout(boolean isShowParentBg) {
        this.isShowParentBg = isShowParentBg;
    }

    /**
     * @return 是否显示parent的背景
     */
    public boolean isShowParentLayout() {
        return isShowParentBg;
    }

    /**
     * 判断Tag是否一致。
     *
     * @param view
     * @param tag
     * @return
     */
    public boolean isEqualsTag(View view, String tag) {
        if (view == null || TextUtils.isEmpty(tag)) {
            return false;
        }

        return tag.equals(view.getTag());
    }

    protected void setDialog(final BaseMessage msgData, final SimpleTipDialog.ConfirmListener listener) {
        PickerDialogUtil.showSimpleTipDialog((FragmentActivity) App.getActivity(),
                new SimpleTipDialog.ConfirmListener() {
                    @Override
                    public void onCancel() {
                        if (listener != null) {
                            listener.onCancel();
                        }
                    }

                    @Override
                    public void onSubmit() {
                        ModuleMgr.getChatMgr().resendMsg(msgData);
                        if (listener != null) {
                            listener.onSubmit();
                        }
                    }
                }, "消息发送失败，重新发送该条消息", "重新发送", true);
    }


}
