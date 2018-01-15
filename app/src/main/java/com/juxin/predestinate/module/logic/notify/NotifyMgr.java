package com.juxin.predestinate.module.logic.notify;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.observe.ModuleBase;
import com.juxin.library.observe.MsgMgr;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.inter.ChatMsgInterface;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.BaseUtil;
import com.juxin.predestinate.module.util.JsonUtil;
import com.juxin.predestinate.module.util.MediaNotifyUtils;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.mail.chat.PrivateChatAct;
import com.juxin.predestinate.ui.main.MainActivity;
import com.juxin.predestinate.ui.utils.CheckIntervalTimeUtil;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 消息通知管理manager
 */
public class NotifyMgr implements ModuleBase, ChatMsgInterface.ChatMsgListener {

    private Executor notifyExecutor = Executors.newSingleThreadExecutor();
    private CheckIntervalTimeUtil interval = new CheckIntervalTimeUtil();

    @Override
    public void init() {
        ModuleMgr.getChatMgr().attachChatListener(this);
    }

    @Override
    public void release() {
        ModuleMgr.getChatMgr().detachChatListener(this);
    }

    /**
     * @return 判断是否在睡眠模式的区间范围
     */
    public static Boolean isInSleep() {
        if (PSP.getInstance().getBoolean(Constant.SETTING_SLEEP_MESSAGE, Constant.SETTING_SLEEP_MESSAGE_DEFAULT)) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            return hour < 8 || hour >= 22;
        }
        return false;
    }

    //------------------新消息通知start--------------------

    @Override
    public void onChatUpdate(final BaseMessage message) {
        if (message == null) return;
        PLogger.d("---onChatUpdate--->sendId：" + message.getSSendID()
                + "，message：" + message.getJsonStr());
        if (message.getSendID() == App.uid) return;
        notifyExecutor.execute(new Runnable() {
            @Override
            public void run() {
                showNotify(message);
            }
        });
    }

    //进行悬浮窗通知的消息类型
    private final int NOTIFY_COMMON = BaseMessage.BaseMessageType.common.getMsgType();    //文本消息:2
    private final int NOTIFY_GIFT = BaseMessage.BaseMessageType.gift.getMsgType();        //礼物消息:10
    private final int NOTIFY_VIDEO = BaseMessage.BaseMessageType.video.getMsgType();      //音视频消息:24
    private final int NOTIFY_UPDATE = BaseMessage.BaseMessageType.autoUpdateHtml.getMsgType();//自动升级提示
    private final int NOTIFY_INVITEVIDEO = BaseMessage.BaseMessageType.inviteVideoMass.getMsgType();//女性对男性的语音（视频）邀请

    /**
     * 进行聊天消息通知
     *
     * @param message 消息内容
     */
    private void showNotify(BaseMessage message) {
        if (!PSP.getInstance().getBoolean(Constant.SETTING_MESSAGE, Constant.SETTING_MESSAGE_DEFAULT)) {
            return;
        }
        JSONObject jsonObject = JsonUtil.getJsonObject(message.getJsonStr());
        int type = jsonObject.optInt("mtp");
        if (type != NOTIFY_COMMON && type != NOTIFY_GIFT
                && type != NOTIFY_UPDATE && type != NOTIFY_INVITEVIDEO) return;

        if (type == NOTIFY_INVITEVIDEO && checkInterval()) return;

        show(message, getContent(type, jsonObject));
    }

    /**
     * 弹框限制
     */
    private boolean checkInterval() {
        if (interval == null) interval = new CheckIntervalTimeUtil();

        return !interval.check(Constant.FOUR_HOUR_TIME);
    }

    /**
     * 根据消息类型展示提示内容
     *
     * @param type 消息类型
     * @return 提示内容
     */
    private String getContent(int type, JSONObject jsonObject) {
        if (type == NOTIFY_GIFT) return App.context.getString(R.string.notify_gift);
        if (type == NOTIFY_UPDATE) return App.context.getString(R.string.notify_system);

        if (type == NOTIFY_VIDEO && jsonObject.optInt("vc_tp") == 1) {
            return App.context.getString(R.string.notify_video, jsonObject.optInt("media_tp") == 1 ?
                    App.context.getString(R.string.video) : App.context.getString(R.string.audio));
        }

        if (type == NOTIFY_INVITEVIDEO) {
            if (jsonObject.optInt("media_tp") == 1) {
                return App.context.getString(R.string.notify_invite_video);
            } else {
                return App.context.getString(R.string.notify_invite_voice);
            }
        }

        String content = App.context.getString(R.string.notify_normal);
        if (jsonObject.has("voice")) {
            content = App.context.getString(R.string.notify_media, App.context.getString(R.string.audio));
        } else if (jsonObject.has("video")) {
            content = App.context.getString(R.string.notify_media, App.context.getString(R.string.video));
        } else if (jsonObject.has("image")) {
            content = App.context.getString(R.string.notify_media, App.context.getString(R.string.photo));
        }
        return content;
    }

    /**
     * 进行消息提示
     *
     * @param baseMessage 消息内容
     * @param content     消息提示内容
     */
    public void show(final BaseMessage baseMessage, final String content) {
        // 请求用户资料
        ModuleMgr.getChatMgr().getUserInfoLightweight(baseMessage.getSendID(), new ChatMsgInterface.InfoComplete() {
            @Override
            public void onReqComplete(boolean ret, final UserInfoLightweight simpleData) {
                if (!ret || simpleData == null) {
                    PLogger.d("------>个人资料查询失败, uid: " + baseMessage.getSendID());
                    return;
                }
                PLogger.d("---文本消息提示--->type：" + baseMessage.getType() + "，content：" + content + "，simpleData：" + String.valueOf(simpleData));
                if (simpleData.getUid() == MailSpecialID.customerService.getSpecialID()) {
                    simpleData.setNickname(MailSpecialID.customerService.getSpecialIDName());
                }
                MsgMgr.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewPrivacy(simpleData, baseMessage, content);
                    }
                });
            }
        });
    }

    /**
     * 界面展示
     *
     * @param simpleData  数据库查询出的个人资料数据
     * @param baseMessage 消息类型处理
     * @param content     消息提示内容
     */
    private void viewPrivacy(final UserInfoLightweight simpleData, final BaseMessage baseMessage, final String content) {
        //锁屏状态，锁屏弹窗
        if (BaseUtil.isScreenLock(App.context) && (App.isKeyguard == App.AppKeyguard.KG_INIT || App.isKeyguard == App.AppKeyguard.KG_SCREEN_OFF)) {
            LockScreenMgr.getInstance().setChatData(simpleData, baseMessage, content);
            popupActivity();
            noticeRemind(baseMessage.getType());
            return;
        }

        //解锁状态
        if (App.isForeground()) {//在前台，应用内悬浮窗
            if (App.getActivity() instanceof MainActivity) {
                //@author Mr.Huang
                //@updateDate 17年9月1号晚上
                //首页不在弹出此消息框
//                ((MainActivity) App.getActivity()).showFloatingMessage(simpleData, baseMessage, content);
            }
        } else {//在后台，桌面悬浮窗
            if (App.isForeground()
                    || !LockScreenMgr.getInstance().isTip()) {
                //应用在前台/(应用在退出状态且应用设置为退出不提示)：不进行应用外弹框
                PLogger.d("------>应用外弹框，return");
                return;
            }
            UIShow.showUserMailNotifyAct(baseMessage.getType(), simpleData, content);
            noticeRemind(baseMessage.getType());
        }
    }

    private long notifyTime = -1;//控制消息提示时间，3s之后执行一次提示

    /**
     * 进行新消息提示
     *
     * @param messageType 消息类型
     */
    public void noticeRemind(int messageType) {
        boolean instanceOfChat = App.getActivity() instanceof PrivateChatAct;
        if (!instanceOfChat && messageType != NOTIFY_VIDEO
                && (System.currentTimeMillis() - notifyTime > 3 * 1000)) {
            notifyTime = System.currentTimeMillis();
            playSound();
            vibrator();
        }
    }

    //------------------新消息通知end--------------------

    /**
     * 震动
     */
    public void vibrator() {
        if (PSP.getInstance().getBoolean(Constant.SETTING_VIBRATION, Constant.SETTING_VIBRATION_DEFAULT)) {
            MediaNotifyUtils.vibrator();
        }
    }

    /**
     * 响铃
     */
    public void playSound() {
        if (PSP.getInstance().getBoolean(Constant.SETTING_VOICE, Constant.SETTING_VOICE_DEFAULT)) {
            MediaNotifyUtils.playSound();
        }
    }

    /**
     * 弹窗
     */
    public void popupActivity() {
        LockScreenMgr.getInstance().popupActivity(/*!isInSleep()*/true);
    }
}
