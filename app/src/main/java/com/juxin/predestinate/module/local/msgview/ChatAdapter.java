package com.juxin.predestinate.module.local.msgview;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.ListView;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.library.utils.TypeConvertUtil;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.my.ChatInfo;
import com.juxin.predestinate.module.local.chat.inter.ChatMsgInterface;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.SystemMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.local.chat.utils.SortList;
import com.juxin.predestinate.module.local.msgview.chatview.ChatInterface;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;
import com.juxin.predestinate.module.local.msgview.chatview.base.ChatContentAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.input.ChatExtendPanel;
import com.juxin.predestinate.module.local.msgview.chatview.input.ChatInputPanel;
import com.juxin.predestinate.module.local.msgview.chatview.input.ChatMediaPlayer;
import com.juxin.predestinate.module.local.msgview.chatview.input.ChatRecordPanel;
import com.juxin.predestinate.module.local.msgview.chatview.input.ChatSmilePanel;
import com.juxin.predestinate.module.local.msgview.chatview.input.CommonGridBtnPanel;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 私聊页面消息处理及界面展示adapter
 * Created by Kind on 2017/3/30.
 */
public class ChatAdapter implements ChatMsgInterface.ChatMsgListener, ExListView.IXListViewListener,
        ChatInterface.OnClickChatItemListener, PObserver {

    private Map<Long, UserInfoLightweight> userInfos = new HashMap<>();

    private List<BaseMessage> unHandledMess = new ArrayList();//未读消息列表

    /**
     * 频道Id（群聊的），私聊时聊天对象的uid；群聊时是群聊Id。
     */
    private String channelId = "";

    /**
     * 频道Id（私聊的），私聊时聊天对象的uid；群聊时是群聊Id。
     */
    private String whisperId = "";

    /**
     * 是否显示扩展功能按钮。
     */
    private boolean showExtend = true;

    /**
     * 提供给业务层使用的回调。
     */
    private ChatInterface.OnClickChatItemListener onClickChatItemListener = null;

    /**
     * 当用户信息变化时回调。
     */
    private ChatInterface.OnUserInfoListener onUserInfoListener = null;

    /**
     * 是否正在输入
     */
    private boolean isTyping = true;

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    /**
     * 聊天窗口信息--整合接口
     */
    private ChatInfo chatInfo = new ChatInfo();

    /**
     * 判断消息发出者是否是当前用户自己。
     *
     * @param uid 需要判断的用户uid。
     * @return 相同返回true。
     */
    public static boolean isSender(long uid) {
        return App.uid == uid;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getWhisperId() {
        return whisperId;
    }

    public long getLWhisperId() {
        return TypeConvertUtil.toLong(whisperId);
    }

    public boolean isShowExtend() {
        return showExtend;
    }

    public void setShowExtend(boolean showExtend) {
        this.showExtend = showExtend;
    }

    public void setWhisperId(long whisperID) {
        try {
            this.channelId = null;
            this.whisperId = String.valueOf(whisperID);
            getUserInfo(getLWhisperId());

            chatInstance.chatInputPanel.showSendBtn();
            page = 0;
            PSP.getInstance().put("whisperId", whisperId);

            attach();
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }

    public ChatInterface.OnClickChatItemListener getOnClickChatItemListener() {
        return onClickChatItemListener;
    }

    public void setOnClickChatItemListener(ChatInterface.OnClickChatItemListener onClickChatItemListener) {
        this.onClickChatItemListener = onClickChatItemListener;
    }

    public void setUserChatRecordPanel(ChatRecordPanel chatRecordPanel) {
        chatInstance.chatRecordPanelUser = chatRecordPanel;
    }

    public ChatInfo getChatInfo() {
        return chatInfo;
    }

    public void setChatInfo(ChatInfo chatInfo) {
        this.chatInfo = chatInfo;
    }

    /**
     * 设置输入框的背景色。
     *
     * @param bgColor     背景色。
     * @param bgLineColor 最上一条线颜色。
     */
    public void setInputPanelBg(int bgColor, int bgLineColor) {
        getChatInstance().chatInputPanel.setBg(bgColor, bgLineColor);
    }

    /**
     * 添加一个消息到聊天消息列表中。
     *
     * @param msg 消息。
     */
    public void addMessage(BaseMessage msg) {
        try {
            getChatInstance().chatContentAdapter.updateData(msg);
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        switch (key) {
            case MsgType.MT_Private_Msg_Ulock:
                if (getChatInstance() != null && getChatInstance().chatContentAdapter != null) {
                    getChatInstance().chatContentAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    /**
     * ChatView相关的逻辑对象的实例。
     */
    public static class ChatInstance {
        public Context context = null;
        public ChatAdapter chatAdapter = null;
        public ChatInputPanel chatInputPanel = null;
        public ChatExtendPanel chatExtendPanel = null;
        public ChatSmilePanel chatSmilePanel = null;

        public ChatViewLayout chatViewLayout = null;
        public ChatContentAdapter chatContentAdapter = null;
        public ChatRecordPanel chatRecordPanel = null;
        public ChatRecordPanel chatRecordPanelUser = null;
        public ExListView chatListView = null;
    }

    /**
     * ChatInstance实例。
     */
    private ChatInstance chatInstance = null;

    /**
     * 获取ChatInstance实例。
     *
     * @return 实例。
     */
    public ChatInstance getChatInstance() {
        return chatInstance;
    }

    /**
     * 设置ChatInstance实例。
     *
     * @param chatInstance 实例。
     */
    public void setChatInstance(ChatInstance chatInstance) {
        this.chatInstance = chatInstance;
    }

    /**
     * 根据用户Uid获取用户信息，如果没有获取用户数据。
     *
     * @param uid 用户uid。
     * @return 返回用户信息，如果没有则返回null。
     */
    public synchronized UserInfoLightweight getUserInfo(long uid) {
        UserInfoLightweight userInfo = userInfos.get(uid);
        if (userInfo == null && uid == App.uid) {
            UserDetail temp = ModuleMgr.getCenterMgr().getMyInfo();
            UserInfoLightweight infoLightweight = new UserInfoLightweight(temp);
            userInfos.put(uid, infoLightweight);
            return infoLightweight;
        }
        if (userInfo == null || (TextUtils.isEmpty(userInfo.getNickname()) && TextUtils.isEmpty(userInfo.getAvatar()))) {
            PLogger.d("------>User's userInfo or nickname or avatar is empty, Re-qurey or re-request user info.");
            ModuleMgr.getChatMgr().getUserInfoLightweight(uid, new ChatMsgInterface.InfoComplete() {
                @Override
                public void onReqComplete(boolean ret, UserInfoLightweight infoLightweight) {
                    if (ret){
                        addUserInfo(infoLightweight);
                    }
                }
            });
        }
        return userInfo;
    }

    /**
     * 添加一个用户信息到用户列表。如果当前没有对应用户信息，则刷新数据。
     *
     * @param userInfo 用户信息。
     */
    public synchronized void addUserInfo(final UserInfoLightweight userInfo) {
        if (userInfo == null)
            return;
        UserInfoLightweight temp = userInfos.get(userInfo.getUid());
        if (temp == null) {
            PLogger.printObject(userInfo);
            userInfos.put(userInfo.getUid(), userInfo);

            MsgMgr.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (msgPanelPair != null) {
                        addFixedPanel(msgPanelPair.first, msgPanelPair.second);
                    }

                    getChatInstance().chatContentAdapter.notifyDataSetChanged();

                    if (onUserInfoListener != null) {
                        onUserInfoListener.onComplete(userInfo);
                    }
                }
            });
        }
    }

    private boolean isMachine = false;

    public void onDataUpdate() {
        isMachine = true;
        if (chatInstance.chatContentAdapter == null)
            return;
        if (chatInstance.chatContentAdapter.getList() == null)
            return;
        List<BaseMessage> datas = new ArrayList<>();
        datas.addAll(chatInstance.chatContentAdapter.getList());
        if (datas.size() <= 0)
            return;
        BaseMessage mess = null;
        for (int i = datas.size() - 1; i > 0 && i < datas.size(); i--) {
            mess = datas.get(i);
            if (mess == null) {
                continue;
            }
            BaseMessage.BaseMessageType messageType = BaseMessage.BaseMessageType.valueOf(mess.getType());
            if (messageType != BaseMessage.BaseMessageType.hint && messageType != BaseMessage.BaseMessageType.html) {
                break;
            }
        }

        if (mess != null && !mess.isSender() && datas.size() > 0) {
            for (int i = datas.size() - 1; i >= 0 && i < datas.size(); i--) {
                BaseMessage message = datas.get(i);
                if (message == null)
                    continue;
                if (message.isSender() && message.getStatus() == MessageConstant.OK_STATUS) {
                    ModuleMgr.getChatMgr().updateDeliveryStatus(message.getMsgID(), null);
                    message.setStatus(MessageConstant.READ_STATUS);
                    ModuleMgr.getChatMgr().updateOtherSideRead(null, message.getWhisperID(), message.getSSendID());
                }
            }
            chatInstance.chatContentAdapter.notifyDataSetChanged();
        }
    }

    public ChatInterface.OnUserInfoListener getOnUserInfoListener() {
        return onUserInfoListener;
    }

    public void setOnUserInfoListener(ChatInterface.OnUserInfoListener onUserInfoListener) {
        this.onUserInfoListener = onUserInfoListener;
    }

    public void closeAllInput() {
        getChatInstance().chatInputPanel.closeAllInput();
    }

    /**
     * 设置新的扩展信息。
     *
     * @param chatExtendDatas 扩展功能表。
     */
    public void setChatExtendDatas(List<CommonGridBtnPanel.BTN_KEY> chatExtendDatas) {
        chatInstance.chatExtendPanel.setChatExtendDatas(chatExtendDatas);
    }

    /**
     * 设置群聊的基本按钮。
     */
    public void setGroupChatExtendDatas() {
        ArrayList<CommonGridBtnPanel.BTN_KEY> chatExtendDatas = new ArrayList<CommonGridBtnPanel.BTN_KEY>();
        chatExtendDatas.add(CommonGridBtnPanel.BTN_KEY.IMG);
        chatInstance.chatExtendPanel.setChatExtendDatas(chatExtendDatas);
    }

    /**
     * 设置私聊的基本按钮。
     */
    public void setWhisperChatExtendDatas() {
        ArrayList<CommonGridBtnPanel.BTN_KEY> chatExtendDatas = new ArrayList<CommonGridBtnPanel.BTN_KEY>();

        chatExtendDatas.add(CommonGridBtnPanel.BTN_KEY.IMG);
        chatInstance.chatExtendPanel.setChatExtendDatas(chatExtendDatas);
    }

    /**
     * ListView是否已到底部，非精准判断
     *
     * @param listView
     * @return
     */
    public static boolean isListViewReachBottomEdge(final ListView listView) {
        //忽略新到来的一条消息，和最后一条消息的高度
        return listView.getLastVisiblePosition() >= (listView.getCount() - 3);
    }

    /**
     * 移动到聊天列表的最后一个。
     */
    public void moveToBottom() {
        if (getChatInstance().chatListView == null) {
            return;
        }
        getChatInstance().chatListView.post(new Runnable() {
            @Override
            public void run() {
                getChatInstance().chatListView.setSelection(ListView.FOCUS_DOWN);
                chatInstance.chatInputPanel.getChatTextEdit().requestFocus();
            }
        });
    }

    /**
     * 打开表情面板的指定表情。
     */
    public void showSpecifiedExpression(String smilePackageName) {
        getChatInstance().chatInputPanel.showChatExpression();
        getChatInstance().chatSmilePanel.selectSmile(smilePackageName);
    }

    /**
     * 设置输入内容。
     *
     * @param text
     */
    public void addInputText(String text) {
        if (text == null) {
            text = "";
        }
        chatInstance.chatInputPanel.setInputText(text);
    }

    /**
     * 注册到消息管理模块，接收消息的更新。
     */
    private void attach() {
        MsgMgr.getInstance().attach(this);
        PLogger.printObject(this);
        ModuleMgr.getChatMgr().attachChatListener(TextUtils.isEmpty(channelId) ? whisperId : channelId, this);

        //chatViewLayout注册监听
        if (getChatInstance() != null && getChatInstance().chatViewLayout != null) {
            getChatInstance().chatInputPanel.attach();
        }
        ModuleMgr.getChatMgr().getRecentlyChat(channelId, whisperId, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BaseMessage>>() {
                    private Disposable recentlyDisposable;// 最近消息数据库查询监听

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        recentlyDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<BaseMessage> baseMessages) {
                        recentlyDisposable.dispose();

                        PLogger.printObject(baseMessages);
                        SortList.sortListView(baseMessages);//排序
                        List<BaseMessage> listTemp = new ArrayList<>();

                        if (baseMessages.size() < 20) {
                            chatInstance.chatListView.setPullRefreshEnable(false);
                        }
                        if (baseMessages.size() > 0) {
                            for (BaseMessage baseMessage : baseMessages) {
                                if (isShowMsg(baseMessage)) {
                                    listTemp.add(baseMessage);
                                }
                            }
                        }

                        chatInstance.chatContentAdapter.setList(listTemp);
                        moveToBottom();
                        checkUnHandledMess(listTemp);
                        if (listTemp.size() <= 0) {  //更新已读
                            return;
                        }
                        BaseMessage message = listTemp.get(listTemp.size() - 1);
                        if (message != null && !message.isSender() && !TextUtils.isEmpty(message.getWhisperID())) {
                            ModuleMgr.getChatListMgr().updateToReadPrivate(message.getLWhisperID());
                            ModuleMgr.getChatMgr().updateOtherSideRead(null, message.getWhisperID(), message.getSSendID());
                            handReadMsg();
                            chatInstance.chatContentAdapter.setList(listTemp);
                            moveToBottom();
                        }
                        if (isMachine) onDataUpdate();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 反注册消息模块，解除绑定。
     */
    public void detach() {
        MsgMgr.getInstance().detach(this);
        ModuleMgr.getChatMgr().detachChatListener(this);
        ChatMediaPlayer.getInstance().stopPlayVoice();

        //关闭页面的时候查看是否下在输入中
        if (getChatInstance() != null && getChatInstance().chatInputPanel != null) {
            getChatInstance().chatInputPanel.sendSystemMsgCancelInput();
            getChatInstance().chatInputPanel.detach();//chatViewLayout取消监听
        }
        //关闭页面的时候detach表情面板的监听
        if (getChatInstance() != null && getChatInstance().chatSmilePanel != null) {
            getChatInstance().chatSmilePanel.detach();
        }
        PSP.getInstance().put("whisperId", "-1");
    }

    public void clearHistory() {
        chatInstance.chatContentAdapter.setList(new ArrayList<BaseMessage>());
        unHandledMess.clear();
    }

    public void checkUnHandledMess(List<BaseMessage> listTemp) {
        for (int i = 0; i < listTemp.size(); i++) {
            BaseMessage message = listTemp.get(i);
            if (message == null)
                continue;
            if (message.getStatus() == MessageConstant.FAIL_STATUS ||
                    message.getStatus() == MessageConstant.OK_STATUS ||
                    message.getStatus() == MessageConstant.DELIVERY_STATUS) {
                unHandledMess.add(message);
            }
        }
    }

    @Override
    public void onChatUpdate(BaseMessage message) {
        if (message != null) {
            boolean isUpdate = true;
            PLogger.printObject(message.getJsonStr());
            if (message.getTime() == 0) {
                message.setTime(ModuleMgr.getAppMgr().getTime());
            }

            boolean show = isShowMsg(message);
            if (show) {
                chatInstance.chatContentAdapter.updateData(message);
                if (isListViewReachBottomEdge(getChatInstance().chatListView))
                    moveToBottom();
                if (isMachine)
                    onDataUpdate();

                if (!message.isSender()) {//发送已读消息
                    ModuleMgr.getChatMgr().sendMailReadedMsg(message.getChannelID(), Long.valueOf(whisperId));
                } else {
                    addUnHandledMsg(message);
                }
            } else {
                ChatMsgType msgType = ChatMsgType.getMsgType(message.getType());
                switch (msgType) {
                    case CMT_7:
                        isUpdate = false;
                        List<BaseMessage> messData = chatInstance.chatContentAdapter.getList();
                        if (messData == null || message == null)
                            break;
                        handReadMsg();
                        chatInstance.chatContentAdapter.setList(messData);
                        break;
                    case CMT_1001:
                        List<BaseMessage> msgData = chatInstance.chatContentAdapter.getList();
                        if (msgData == null || message == null)
                            break;
                        SystemMessage msg = new SystemMessage();
                        msg.parseJson(message.getJsonStr());
                        handRecvedMsg(msg.getMsgID());
                        chatInstance.chatContentAdapter.setList(msgData);
                        break;
                    case CMT_1008:
                        upDataChumState();
                        break;
                    default:
                        break;
                }
            }

            /**
             * 本地模拟消息
             * 接收的网络消息
             */
            if ((message.getcMsgID() == 0 && isUpdate) || message.getDataSource() == MessageConstant.FOUR
                    || (message.getDataSource() == MessageConstant.TWO && show)) {
                ModuleMgr.getChatMgr().updateLocalReadStatus(channelId, whisperId, message.getMsgID());
            }
        }
    }

    private void addUnHandledMsg(BaseMessage message) {
        if (message == null)
            return;
        unHandledMess.remove(message);
        unHandledMess.add(message);
    }

    private void handRecvedMsg(Long msgId) {
        for (int i = 0; i < unHandledMess.size(); i++) {
            BaseMessage message = unHandledMess.get(i);
            if (message == null)
                continue;
            if (message.getMsgID() == msgId) {
                message.setStatus(MessageConstant.DELIVERY_STATUS);
                break;
            }
        }
    }

    private void handReadMsg() {
        for (int i = 0; i < unHandledMess.size(); i++) {
            BaseMessage message = unHandledMess.get(i);
            if (message == null)
                continue;
            if (message.getStatus() == MessageConstant.DELIVERY_STATUS) {
                message.setStatus(MessageConstant.READ_STATUS);
                unHandledMess.remove(i);
                i--;
            }
        }
    }

    public boolean isShowMsg(BaseMessage baseMsg) {
        if (baseMsg == null) {
            return false;
        }

        ChatMsgType chatMsgType = ChatMsgType.getMsgType(baseMsg.getType());
        baseMsg.setMsgPanelType(chatMsgType.getMsgPanelType());


        switch (chatMsgType.getMsgPanelType()) {
            case CPT_Fixed:
                addFixedPanel(baseMsg, getItemPanel(null, baseMsg, chatInstance, baseMsg.isSender()));
                return false;
            default:
                break;
        }

        return chatMsgType.getPanelClass() != null;
    }

    public ChatPanel getItemPanel(ChatPanel itemPanel, BaseMessage baseMsg, ChatInstance chatInstance, boolean sender) {
        if (baseMsg == null) {
            return null;
        }

        ChatPanel chatPanel;
        if (itemPanel != null) {
            return itemPanel;
        }

        Class<? extends ChatPanel> panelClass = ChatMsgType.getPanelClass(baseMsg.getType());
        if (panelClass == null) {
            return null;
        }

        try {
            Constructor c = panelClass.getDeclaredConstructor(Context.class, ChatInstance.class, boolean.class);
            c.setAccessible(true);//增加可访问设置，让其忽略访问控制符
            chatPanel = (ChatPanel) c.newInstance(App.context, chatInstance, sender);
            chatPanel.initView();
        } catch (Exception e) {
            e.printStackTrace();
            chatPanel = null;
        }

        return chatPanel;
    }

    private Pair<BaseMessage, ChatPanel> msgPanelPair = null;

    /**
     * 右上角添加固定提示。
     *
     * @param chatPanel 需要显示的面板，必须有效，不能为null。
     */
    public synchronized void addFixedPanel(BaseMessage baseMessage, final ChatPanel chatPanel) {
        if (chatPanel == null) {
            return;
        }

        msgPanelPair = new Pair<>(baseMessage, chatPanel);
        UserInfoLightweight infoLightweight = getUserInfo(baseMessage.getSendID());

        if (chatPanel.reset(baseMessage, infoLightweight)) {
            msgPanelPair = null;
            chatInstance.chatViewLayout.addFixedPanel(chatPanel);
        }
    }

    @Override
    public boolean onClickHead(BaseMessage msgData) {
        if (onClickChatItemListener != null) {
            return onClickChatItemListener.onClickHead(msgData);
        }

        return false;
    }

    @Override
    public boolean onClickStatus(BaseMessage msgData) {
        if (onClickChatItemListener != null) {
            return onClickChatItemListener.onClickStatus(msgData);
        }

        return false;
    }

    @Override
    public boolean onClickContent(BaseMessage msgData, boolean longClick) {
        if (onClickChatItemListener != null) {
            return onClickChatItemListener.onClickContent(msgData, longClick);
        }

        return false;
    }

    @Override
    public boolean onClickErrorResend(BaseMessage msgData) {
        if (onClickChatItemListener != null) {
            return onClickChatItemListener.onClickErrorResend(msgData);
        }

        return false;
    }

    private int page = 0;

    @Override
    public void onRefresh() {
        // 这里是加载更多信息的。
        ModuleMgr.getChatMgr().getHistoryChat(channelId, whisperId, ++page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BaseMessage>>() {
                    private Disposable historyDisposable;// 历史消息数据库查询监听

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        historyDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<BaseMessage> baseMessages) {
                        historyDisposable.dispose();

                        SortList.sortListView(baseMessages);//排序
                        PLogger.printObject(baseMessages);
                        chatInstance.chatListView.stopRefresh();
                        if (baseMessages.size() > 0) {
                            if (baseMessages.size() < 20) {
                                chatInstance.chatListView.setPullRefreshEnable(false);
                            }

                            List<BaseMessage> listTemp = new ArrayList<>();
                            for (BaseMessage baseMessage : baseMessages) {
                                if (isShowMsg(baseMessage)) {
                                    listTemp.add(baseMessage);
                                }
                            }

                            if (chatInstance.chatContentAdapter.getList() != null) {
                                listTemp.addAll(chatInstance.chatContentAdapter.getList());
                            }

                            chatInstance.chatContentAdapter.setList(listTemp);
                            chatInstance.chatListView.setSelection(baseMessages.size());
                            checkUnHandledMess(listTemp);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    //更改密友任务的状态
    private void upDataChumState() {
        if (chatInstance.chatContentAdapter == null)
            return;
        if (chatInstance.chatContentAdapter.getList() == null)
            return;
        for (int i = 0; i < chatInstance.chatContentAdapter.getList().size(); i++) {
            BaseMessage message = chatInstance.chatContentAdapter.getList().get(i);
            if (message != null) {
                ChatMsgType msgType = ChatMsgType.getMsgType(message.getType());
                switch (msgType) {
                    case CMT_36:
                        message.setfStatus(0);
                        break;
                    default:
                        break;
                }
            }
        }
        chatInstance.chatContentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMore() {
    }

    /**
     * @author Mr.Huang
     * @date 2017-08-01
     * 显示解锁后获得红包奖励的提示，5秒后消失
     */
    public void showGetRedPackageTip() {
        getChatInstance().chatViewLayout.showGetRedPackageTip();
    }

    /**
     * @param tuid
     * @author Mr.Huang
     * @date 2017-07-17
     * 设置当前聊天对象ID
     */
    public void setTUID(long tuid) {
        chatInstance.chatInputPanel.setTUID(tuid);
    }

    /**
     * 是否可以聊天
     */
    public void showIsCanChat(boolean isCanChat) {
        chatInstance.chatInputPanel.showChat(isCanChat);
    }

    /**
     * 如果是小秘书都不显示
     */
    public void showInputGONE() {
        chatInstance.chatInputPanel.showInputGONE();
    }

    /**
     * @author Mr.Huang
     * @date 2017-07-17
     * 设置解锁状态
     */
    public void setUnlocked(boolean isUnlocked) {
        chatInstance.chatInputPanel.setUnlocked(isUnlocked);
    }
}
