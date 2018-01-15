package com.juxin.predestinate.module.local.chat;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Message;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.observe.ModuleBase;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.library.unread.UnreadMgr;
import com.juxin.predestinate.bean.db.AppComponent;
import com.juxin.predestinate.bean.db.AppModule;
import com.juxin.predestinate.bean.db.DBCallback;
import com.juxin.predestinate.bean.db.DBCenter;
import com.juxin.predestinate.bean.db.DBModule;
import com.juxin.predestinate.bean.db.DaggerAppComponent;
import com.juxin.predestinate.bean.db.OldDBModule;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.local.chat.utils.SortList;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.model.impl.UnreadMgrImpl;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.ui.mail.item.MailItemType;
import com.juxin.predestinate.ui.mail.item.MailMsgID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 聊天列表数据处理中心
 * Created by Kind on 2017/4/13.
 */
public class ChatListMgr implements ModuleBase, PObserver {

    private int unreadNum = 0;

    private List<BaseMessage> showMsgList = new ArrayList<>(); //所有的消息列表

    private List<BaseMessage> msgList = new ArrayList<>(); //私聊列表
    private List<BaseMessage> greetList = new ArrayList<>(); //招呼的人
    private List<BaseMessage> strangerList = new ArrayList<>(); //陌生人

    private List<BaseMessage> inviteList = new ArrayList<>(); //语音视频邀请
    private List<BaseMessage> giftList = new ArrayList<>(); //礼物未领取
    private List<BaseMessage> privateUnreadList = new ArrayList<>(); //私密未读消息

    private boolean inviteUnfoldedList = false;//邀请开关
    private boolean privateUnfoldedList = false;//私有
    private boolean giftUnfoldedList = false;//礼物
    private boolean strangerUnfoldedList = false;//陌生人

    private boolean isMsgChange = false;
    private Thread notifyThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    stepHandler.sendEmptyMessage(1);
                    Thread.sleep(1500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    @Inject
    DBCenter dbCenter;
    SpecialMsgMgr specialMsgMgr = new SpecialMsgMgr();

    @Override
    public void init() {
        MsgMgr.getInstance().attach(this);
        specialMsgMgr.init();
        notifyThread.start();
    }

    @Override
    public void release() {
        MsgMgr.getInstance().detach(this);
        specialMsgMgr.release();
    }

    public int getUnreadNumber() {
        return unreadNum;
    }

    /**
     * 打招呼的人未读
     *
     * @return
     */
    public int getGreetNum() {
        int tmpNum = 0;
        synchronized (greetList) {
            for (BaseMessage temp : greetList) {
                tmpNum += temp.getNum();
            }
        }
        return tmpNum;
    }

    /**
     * 陌生人未读
     *
     * @return
     */
    public int getStrangerNum() {
        int tmpNum = 0;
        synchronized (strangerList) {
            for (BaseMessage temp : strangerList) {
                tmpNum += temp.getNum();
            }
        }
        return tmpNum;
    }

    /**
     * 转换消息角标个数
     *
     * @param unreadNum
     * @return
     */
    public String getUnreadNum(int unreadNum) {
        return unreadNum <= 9 ? String.valueOf(unreadNum) : "9+";
    }

    public String getUnreadTotalNum(int unreadNum) {
        return unreadNum <= 99 ? String.valueOf(unreadNum) : "99+";
    }

    public int getTaskNum() {
        return specialMsgMgr.getTaskNum();
    }

    public List<BaseMessage> getMsgList() {
        List<BaseMessage> tempList = new ArrayList<>();
        synchronized (msgList) {
            tempList.addAll(msgList);
            return tempList;
        }
    }

    public List<BaseMessage> getShowMsgList() {
        List<BaseMessage> tempList = new ArrayList<>();
        synchronized (showMsgList) {
            tempList.addAll(showMsgList);
            return tempList;
        }
    }

    public boolean isContain(long userID) {
        synchronized (msgList) {
            for (BaseMessage temp : msgList) {
                if (userID == temp.getLWhisperID()) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 获取角标数
     *
     * @param userID
     * @return
     */
    public int getNoReadNum(long userID) {
        List<BaseMessage> tempList = new ArrayList<>();
        tempList.addAll(msgList);
        for (BaseMessage temp : tempList) {
            if (userID == temp.getLWhisperID()) {
                return temp.getNum();
            }
        }
        return 0;
    }

    /**
     * 打招呼人列表
     *
     * @return
     */
    public List<BaseMessage> getGeetList() {
        List<BaseMessage> tempList = new ArrayList<>();
        synchronized (greetList) {
            tempList.addAll(greetList);
            return tempList;
        }
    }

    /**
     * 陌生人列表
     *
     * @return
     */
    public List<BaseMessage> getStrangerList() {
        List<BaseMessage> tempList = new ArrayList<>();
        synchronized (strangerList) {
            tempList.addAll(strangerList);
            return tempList;
        }
    }

    /**
     * 语音视频邀请
     *
     * @return
     */
    public List<BaseMessage> getInviteList() {
        List<BaseMessage> tempList = new ArrayList<>();
        synchronized (inviteList) {
            tempList.addAll(inviteList);
            return tempList;
        }
    }

    /**
     * 礼物
     *
     * @return
     */
    public List<BaseMessage> getGiftList() {
        List<BaseMessage> tempList = new ArrayList<>();
        synchronized (giftList) {
            tempList.addAll(giftList);
            return tempList;
        }
    }

    /**
     * 礼物是否包含
     *
     * @return
     */
    public boolean isContainGiftList(long userID) {
        synchronized (giftList) {
            for (BaseMessage temp : giftList) {
                if (userID == temp.getLWhisperID()) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 私密未读消息
     *
     * @return
     */
    public List<BaseMessage> getPrivateUnreadList() {
        List<BaseMessage> tempList = new ArrayList<>();
        synchronized (privateUnreadList) {
            tempList.addAll(privateUnreadList);
            return tempList;
        }
    }

    public long getGeetListLastTime() {
        long tm = 0;
        for (BaseMessage temp : greetList) {
            if (temp.getTime() > tm) {
                tm = temp.getTime();
            }
        }
        return tm;
    }

    /**
     * 关注
     *
     * @return
     */
    public int getFollowNum() {
        return ModuleMgr.getUnreadMgr().getUnreadNumByKey(UnreadMgrImpl.FOLLOW_ME);
    }

    public void updateFollow() {
        ModuleMgr.getUnreadMgr().resetUnreadByKey(UnreadMgrImpl.FOLLOW_ME);
    }

    private synchronized void updateListMsg(List<BaseMessage> messages) {
        unreadNum = 0;
        msgList.clear();
        greetList.clear();
        strangerList.clear();
        inviteList.clear();
        giftList.clear();
        privateUnreadList.clear();

        if (messages != null && messages.size() > 0) {
            for (BaseMessage tmp : messages) {
                if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                    if (MailSpecialID.getMailSpecialID(tmp.getLWhisperID()) || tmp.getType() == 10) {
                        msgList.add(tmp);
                    } else if (tmp.getType() == 30) {//邀请
                        tmp.setWeight(MessageConstant.Invite_Ordinary_Weight);
                        tmp.setMailItemStyle(MailItemType.Mail_Item_Invite.type);
                        inviteList.add(tmp);
                    } else if (tmp.getType() == 31 || tmp.getType() == 32 || tmp.getType() == 20) {//私密
                        tmp.setWeight(MessageConstant.Private_Ordinary_Weight);
                        tmp.setMailItemStyle(MailItemType.Mail_Item_Private.type);
                        privateUnreadList.add(tmp);
                    } else if (tmp.getKnow() >= MessageConstant.Know_Chat_Person) {
                        msgList.add(tmp);
                    } else {
                        tmp.setMailItemStyle(MailItemType.Mail_Item_Greet.type);
                        greetList.add(tmp);
                    }
                } else {
                    if (tmp.getfGiftNum() > 0) {
                        try {
                            BaseMessage temp = (BaseMessage) tmp.clone();
                            temp.setWeight(MessageConstant.Gift_Ordinary_Weight);
                            temp.setMailItemStyle(MailItemType.Mail_Item_Gift.type);
                            giftList.add(temp);
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (MailSpecialID.getMailSpecialID(tmp.getLWhisperID())) {
                        msgList.add(tmp);
                    } else if (tmp.getKnow() >= MessageConstant.Know_Friends || MailSpecialID.getMailSpecialID(tmp.getLWhisperID())) {
                        tmp.initMailItemStyleWeight();
                        msgList.add(tmp);
                    } else if (tmp.getKnow() >= MessageConstant.Know_Chat_Person) {//有过聊天的的人
                        tmp.setWeight(MessageConstant.Stranger_Ordinary_Weight);
                        tmp.setMailItemStyle(MailItemType.Mail_Item_Stranger.type);
                        strangerList.add(tmp);
                    } else {
                        tmp.setMailItemStyle(MailItemType.Mail_Item_Greet.type);
                        greetList.add(tmp);
                    }
                }
                unreadNum += tmp.getNum();
            }
            onStitchingMsg(-1);
        } else {
            showMsgList.clear();
            isMsgChange = true;
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler stepHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (isMsgChange) {
                        MsgMgr.getInstance().sendMsg(MsgType.MT_User_List_Msg_Change, null);
                        isMsgChange = false;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 拼接消息
     */
    private void onStitchingMsg(int type) {
        if (MessageConstant.Chat_Mail_Invite == type) {//邀请消息
            if (inviteUnfoldedList) {
                inviteUnfoldedList = false;
            } else {
                inviteUnfoldedList = true;
            }
        } else if (MessageConstant.Chat_Mail_Private == type) {
            if (privateUnfoldedList) {
                privateUnfoldedList = false;
            } else {
                privateUnfoldedList = true;
            }
        } else if (MessageConstant.Chat_Mail_Gift == type) {
            if (giftUnfoldedList) {
                giftUnfoldedList = false;
            } else {
                giftUnfoldedList = true;
            }
        } else if (MessageConstant.Chat_Mail_Stranger == type) {
            if (strangerUnfoldedList) {
                strangerUnfoldedList = false;
            } else {
                strangerUnfoldedList = true;
            }
        }

        showMsgList.clear();
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {//男
            List<BaseMessage> inviteMsgs = getInviteMsgs(inviteUnfoldedList ? 10 : 1);//默认是一个展示一个用户，点开的时候展示10个
            if (inviteMsgs.size() > 0) {
                showMsgList.addAll(inviteMsgs);//邀请消息
            }

            List<BaseMessage> privateTempMsg = getPrivateMsgs(privateUnfoldedList ? 10 : 1);
            if (privateTempMsg.size() > 0) {
                showMsgList.addAll(privateTempMsg);
            }
            showMsgList.addAll(strangerList);
        } else {
            List<BaseMessage> giftMsgs = getGiftMsgs(giftUnfoldedList ? 10 : 1);
            if (giftMsgs.size() > 0) {
                showMsgList.addAll(giftMsgs);//礼物未读消息
            }

            List<BaseMessage> strangerMsgs = getStrangerMsg(strangerUnfoldedList ? 10 : 1);
            if (strangerMsgs.size() > 0) {
                showMsgList.addAll(strangerMsgs);//陌生人
            }
        }

        showMsgList.addAll(msgList);
        if (type == -1) {
            isMsgChange = true;
        } else {
            MsgMgr.getInstance().sendMsg(MsgType.MT_User_List_Msg_Change, null);
        }
    }

    //是否能聊天
    private String getIsTodayChatKey() {//是否显示问题反馈第一句KEY
        return "isTodayChat" + App.uid;
    }

    public void setTodayChatShow() {//隐藏，显示私聊列表
        PSP.getInstance().put(getIsTodayChatKey(), TimeUtil.getCurrentData());
    }

    /**
     * 当前时否可以聊天
     *
     * @return true是可以发信的，false不可以发信
     */
    public boolean getTodayChatShow() {
        String currentData = TimeUtil.getCurrentData();
        String isTodayChat = PSP.getInstance().getString(getIsTodayChatKey(), "");
        return isTodayChat.equals("") || !isTodayChat.equals(currentData);
    }

    /**
     * 批量删除消息
     *
     * @param messageList
     */
    public void deleteBatchMessage(final List<BaseMessage> messageList, DBCallback callback) {
        List<Long> idList = new ArrayList<Long>();

        for (BaseMessage temp : messageList) {
            idList.add(temp.getLWhisperID());
        }

        dbCenter.deleteMessageList(idList, callback);
    }

    public void deleteMessage(long userID, final DBCallback callback) {
        dbCenter.deleteMessage(userID, callback);
    }

    /**
     * 删除聊天记录
     *
     * @param userID
     * @return
     */
    public void deleteFmessage(final long userID, final DBCallback dbCallback) {
        dbCenter.getCenterFLetter().updateContent(String.valueOf(userID), new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (result != MessageConstant.OK) {
                    if (dbCallback != null) {
                        dbCallback.OnDBExecuted(result);
                    }
                    return;
                }

                dbCenter.getCenterFMessage().delete(userID, new DBCallback() {
                    @Override
                    public void OnDBExecuted(long result) {
                        if (dbCallback != null) {
                            dbCallback.OnDBExecuted(result);
                        }
                    }
                });
            }
        });
    }

    /**
     * 插入
     *
     * @param chatMark
     * @param callback
     */
    public void insertFMark(final ChatMark chatMark, final DBCallback callback) {
        if (dbCenter == null) return;
        dbCenter.getCenterFMark().insertFMark(chatMark, callback);
    }

    public void deleteFMark(long userID, int type, final DBCallback callback) {
        if (dbCenter == null) return;
        dbCenter.getCenterFMark().deleteFMark(userID, type, callback);
    }

    public Observable<ChatMark> queryFMark(long userID, int type) {
        if (dbCenter == null) return null;
        return dbCenter.getCenterFMark().queryFMark(userID, type);
    }

    /**
     * 更新已读
     */
    public void updateToReadAll(final DBCallback dbCallback) {
        if (dbCenter == null) return;
        dbCenter.updateToReadAll(new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (dbCallback != null) {
                    dbCallback.OnDBExecuted(result);
                }
                getWhisperListUnSubscribe();
            }
        });
    }

    public void updateToBatchRead(List<BaseMessage> greetList, final DBCallback dbCallback) {
        if (greetList == null || greetList.size() <= 0 || dbCenter == null) {
            return;
        }
        dbCenter.getCenterFMessage().updateToRead(greetList, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (dbCallback != null) {
                    dbCallback.OnDBExecuted(result);
                }
                getWhisperListUnSubscribe();
            }
        });
    }

    /**
     * 更新私聊列表状态
     *
     * @param userID
     * @return
     */
    public void updateToReadPrivate(long userID) {
        dbCenter.getCenterFLetter().updateReadStatus(userID, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (result != MessageConstant.OK) {
                    return;
                }
                getWhisperListUnSubscribe();
            }
        });
    }

    /**
     * 获取消息列表，初始化时进行调用，不取消订阅，外部请勿使用
     */
    public void getWhisperList() {
        Observable<List<BaseMessage>> observable = dbCenter.getCenterFLetter().queryLetterList()
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        //此处使用简化的调用方式，如果需要详细的监听回调，需要subscribe一个Observer
        observable.subscribe(new Consumer<List<BaseMessage>>() {
            @Override
            public void accept(List<BaseMessage> baseMessages) throws Exception {
                updateListMsg(baseMessages);
            }
        });
    }

    /**
     * 获取消息列表，外部使用，查询完成后取消订阅
     */
    public void getWhisperListUnSubscribe() {
        if (dbCenter == null || dbCenter.getCenterFLetter() == null) return;

        dbCenter.getCenterFLetter().queryLetterList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // 此处使用简化的调用方式，如果需要详细的监听回调，需要subscribe一个Observer
                .subscribe(new Observer<List<BaseMessage>>() {
                    private Disposable whisperListDisposable;

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        whisperListDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull List<BaseMessage> baseMessages) {
                        whisperListDisposable.dispose();
                        updateListMsg(baseMessages);
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
     * 获取邀请消息前二条消息
     *
     * @param pos 需要的个数
     * @return
     */
    private List<BaseMessage> getInviteMsgs(int pos) {
        List<BaseMessage> baseMessages = new ArrayList<>();
        if (inviteList.size() <= 0) return baseMessages;
        SortList.sortWeightTimeListView(inviteList);

        BaseMessage temp = new BaseMessage();
        temp.setWhisperID(String.valueOf(MailMsgID.Invite_Msg.type));
        temp.setWeight(MessageConstant.Invite_Max_Weight);
        temp.setMailItemStyle(MailItemType.Mail_Item_Title.type);
        temp.setName("视频/语音邀请");
        temp.setAboutme("（" + getUnreadTotalNum(inviteList.size()) + "位美女邀请）");
        baseMessages.add(temp);

        for (int i = 0; i < inviteList.size(); i++) {
            if (i >= pos) break;
            baseMessages.add(inviteList.get(i));
        }

        if (pos == 10 && inviteList.size() > 10) {
            baseMessages.add(getMailItemBottom(MailMsgID.Invite_More_Msg.type, MessageConstant.Invite_Ordinary_Weight));
        }
        return baseMessages;
    }

    /**
     * 礼物
     *
     * @param pos
     * @return
     */
    private List<BaseMessage> getGiftMsgs(int pos) {
        List<BaseMessage> baseMessages = new ArrayList<>();
        if (giftList.size() <= 0) return baseMessages;
        SortList.sortWeightTimeListView(giftList);

        BaseMessage temp = new BaseMessage();
        temp.setWhisperID(String.valueOf(MailMsgID.Gift_Msg.type));
        temp.setWeight(MessageConstant.Gift_Max_Weight);
        temp.setMailItemStyle(MailItemType.Mail_Item_Title.type);
        temp.setName("哇哦，有帅哥送礼物啦！");
        // temp.setAboutme(getUnreadTotalNum(giftList.size()) + "位帅哥赠送礼物...");
        baseMessages.add(temp);

        for (int i = 0; i < giftList.size(); i++) {
            if (i >= pos) break;
            baseMessages.add(giftList.get(i));
        }

        if (pos == 10 && giftList.size() > 10) {
            baseMessages.add(getMailItemBottom(MailMsgID.Gift_More_Msg.type, MessageConstant.Gift_Ordinary_Weight));
        }
        return baseMessages;
    }

    /**
     * 获取邀请消息
     *
     * @param pos 需要的个数
     * @return
     */
    private List<BaseMessage> getPrivateMsgs(int pos) {
        List<BaseMessage> baseMessages = new ArrayList<>();
        if (privateUnreadList.size() <= 0) return baseMessages;
        SortList.sortWeightTimeListView(privateUnreadList);

        BaseMessage temp = new BaseMessage();
        temp.setWhisperID(String.valueOf(MailMsgID.Private_Msg.type));
        temp.setWeight(MessageConstant.Private_Max_Weight);
        temp.setMailItemStyle(MailItemType.Mail_Item_Title_TWO.type);
        temp.setName("私密消息");
        temp.setAboutme("（" + privateUnreadList.size() + "位美女私密消息）");
        baseMessages.add(temp);

        for (int i = 0; i < privateUnreadList.size(); i++) {
            if (i >= pos) break;
            baseMessages.add(privateUnreadList.get(i));
        }

        if (pos == 10 && privateUnreadList.size() > 10) {
            baseMessages.add(getMailItemBottom(MailMsgID.Private_More_Msg.type, MessageConstant.Private_Ordinary_Weight));
        }

        return baseMessages;
    }

    /**
     * 陌生人消息
     *
     * @param pos
     * @return
     */
    private List<BaseMessage> getStrangerMsg(int pos) {
        List<BaseMessage> baseMessages = new ArrayList<>();
        if (strangerList.size() <= 0) return baseMessages;
        SortList.sortWeightTimeListView(strangerList);

        BaseMessage temp = new BaseMessage();
        temp.setWhisperID(String.valueOf(MailMsgID.Stranger_Msg.type));
        temp.setWeight(MessageConstant.Stranger_Max_Weight);
        temp.setMailItemStyle(MailItemType.Mail_Item_Title_TWO.type);
        temp.setName("陌生人消息");
        temp.setAboutme("（" + getStrangerNum() + "条未读）");
        baseMessages.add(temp);

        for (int i = 0; i < strangerList.size(); i++) {
            if (i >= pos) break;
            baseMessages.add(strangerList.get(i));
        }

        if (pos == 10 && strangerList.size() > 10) {
            baseMessages.add(getMailItemBottom(MailMsgID.Stranger_More_Msg.type, MessageConstant.Stranger_Ordinary_Weight));
        }
        return baseMessages;
    }

    /**
     * 底部条
     *
     * @param uid
     * @param weight
     * @return
     */
    private BaseMessage getMailItemBottom(long uid, int weight) {
        BaseMessage temp = new BaseMessage();
        temp.setWhisperID(String.valueOf(uid));
        temp.setWeight(weight);
        temp.setMailItemStyle(MailItemType.Mail_Item_Bottom.type);

        return temp;
    }

    @Override
    public void onMessage(String key, Object value) {
        switch (key) {
            case MsgType.MT_Unread_change:
                if (App.uid <= 0) return;
                Map<String, Object> msgMap = (Map<String, Object>) value;
                String Msg_Name_Key = (String) msgMap.get(UnreadMgr.Msg_Name_Key);
                if (Msg_Name_Key.equals(UnreadMgrImpl.FOLLOW_ME)) {
                    ModuleMgr.getCenterMgr().reqMyInfo(new RequestComplete() {
                        @Override
                        public void onRequestComplete(HttpResponse response) {
                            getWhisperListUnSubscribe();
                        }
                    });
                }
                break;
            case MsgType.MT_App_Login:
                PLogger.d("---ChatList_MT_App_Login--->" + value);
                if ((Boolean) value) {//登录成功
                    login();
                } else {
                    logout();
                }
                break;
            case MsgType.MT_Chat_Mail_More: {
                onStitchingMsg((Integer) value);
                break;
            }
            case MsgType.MESSAGE_UNLOCK://解锁类型 1 分享解锁 2.钻石消费解锁 3.爵位解锁
                //Msg msg = (Msg) value;
                ModuleMgr.getChatMgr().sendMsg();
                break;
            default:
                break;
        }
    }

    private void login() {
        if (App.uid > 0) {
            initAppComponent();
            ModuleMgr.getChatMgr().inject();
            //升级数据库
            new OldDBModule().updateDB(App.uid);
            getWhisperList();
            ModuleMgr.getChatMgr().deleteMessageKFIDHour(48);
            specialMsgMgr.getIntimateTaskCnt();
        }
    }

    private void logout() {
        mAppComponent = null;
        msgList.clear();
        greetList.clear();
        unreadNum = 0;

        showMsgList.clear();
        strangerList.clear();
        inviteList.clear();
        privateUnreadList.clear();

        inviteUnfoldedList = false;
        privateUnfoldedList = false;
        giftUnfoldedList = false;
        strangerUnfoldedList = false;
    }

    /**
     * AppComponent
     */
    private AppComponent mAppComponent;

    /**
     * @return 获取dagger2管理的全局实例
     */
    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    /**
     * DB初始化
     */
    private void initAppComponent() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule((Application) App.getContext()))
                .dBModule(new DBModule(App.uid))
                .build();
        mAppComponent.inject(this);
        MsgMgr.getInstance().sendMsg(MsgType.MT_DB_Init_Ok, null);
    }

    /**
     * 处理特殊消息
     * 例如 系统消息，心动消息
     *
     * @param message
     */
    public void setSpecialMsg(BaseMessage message) {
        specialMsgMgr.setSpecialMsg(message);
    }

    public SpecialMsgMgr getSpecialMsgMgr() {
        return specialMsgMgr;
    }
}