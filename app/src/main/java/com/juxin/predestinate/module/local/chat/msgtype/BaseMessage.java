package com.juxin.predestinate.module.local.chat.msgtype;

import android.os.Bundle;
import android.text.TextUtils;
import com.juxin.library.log.PLogger;
import com.juxin.library.utils.TypeConvertUtil;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.db.FLetter;
import com.juxin.predestinate.bean.db.FMessage;
import com.juxin.predestinate.module.local.chat.inter.IBaseMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.local.chat.utils.MsgIDUtils;
import com.juxin.predestinate.module.local.msgview.chatview.base.ChatPanelType;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.ui.mail.item.MailItemType;
import org.json.JSONObject;

/**
 * [消息类型处理](http://doc.dev.yuanfenba.net/pkg/yuanfen/common/msg_data/)
 * Created by Kind on 2017/3/17.
 */
public class BaseMessage extends BaseMessageUserInfo implements IBaseMessage, Cloneable {

    public enum BaseMessageType {

        common(CommonMessage.class, 2),//文本消息
        hi(CommonMessage.class, 3),//打招呼
        gift(GiftMessage.class, 10),//礼物消息
        hint(TextMessage.class, 14),//小提示消息   不显示
        html(TextMessage.class, 19),//html消息
        wantGift(GiftMessage.class, 20),//索要礼物消息
        video(VideoMessage.class, 24),//视频消息
        htmlText(TextMessage.class, 25),//HTML文本消息
        autoUpdateHtml(TextMessage.class, 28),//自动升级提示
        sysNotice(SysNoticeMessage.class, 29),//系统通知消息
        inviteVideoMass(InviteVideoMessage.class, 30),//女性对男性的语音（视频）邀请
        privatePhoto(PrivateMessage.class, 31),//女性群发的私密照片
        privateVideo(PrivateMessage.class, 32),//女性群发的私密视频
        privateVoice(PrivateMessage.class, 33),//女性群发的私密文字/语音
        RED_PACKAGE(RedPackageMessage.class, 34),//红包消息
        chumInvite(ChumInviteMessage.class, 35),//密友邀请相关消息,
        chumTask(ChumTaskMessage.class, 36),//密友任务
        smallIcon(SmallIconMessage.class, 37),//小图标消息
        giftRedPackage(GiftRedPackageMesaage.class, 38),//礼物红包版本，红包消息



        maxVersion(MaxVersionMessage.class, 1000000),//最大版本消息 1000000这个不要随便改
        ;

        public Class<? extends BaseMessage> msgClass = null;
        public int msgType;

        BaseMessageType(Class<? extends BaseMessage> msgClass, int msgType) {
            this.msgClass = msgClass;
            this.msgType = msgType;
        }

        public static BaseMessageType valueOf(int msgType) {
            if (MessageConstant.isMaxVersionMsg(msgType)) {
                return BaseMessageType.maxVersion;
            }

            for (BaseMessageType messageType : BaseMessageType.values()) {
                if (messageType.getMsgType() == msgType) {
                    return messageType;
                }
            }
            return null;
        }

        public int getMsgType() {
            return this.msgType;
        }
    }

    /**
     * 消息类型，特殊地方方便用
     */
    public static final int Follow_MsgType = 5;//关注
    public static final int System_MsgType = 7;//系统消息
    public static final int Gift_MsgType = 10; //礼物消息
    public static final int TalkRed_MsgType = 12;//聊天红包
    public static final int RedEnvelopesBalance_MsgType = 17;//红包余额变动消息
    public static final int video_MsgType = 24;//视频消息
    public static final int inviteVideoDelivery_MsgType = 30;//女性对男性的群发语音(视频)邀请
    public static final int chumInvite_MsgType = 35;//密友邀请相关消息(视频)邀请
    public static final int Recved_MsgType = 1001;//送达消息
    public static final int VideoInviteTomen_MsgType = 1002;//女性对男性的语音(视频)邀请送达男用户 此消息为群发视频/语音，送达人数对女用户的通知
    public static final int Alone_Invite_Video_MsgType = 1003;//女用户单独视频邀请
    public static final int DIALOG_TYPE = 1006;//弹框消息类型
    public static final int Intimate_Rem_MsgType = 1008;//解除密友消息通知
    public static final int Effect_Pop_MsgType = 1009;//全服特效弹屏消息
    public static final int MSG_TYPE_VIDEOCHAT_POP = 1010;//好友视频邀请弹窗消息（爵位版加的）
    public static final int LiveStartLive_MsgType = 1012;//开播提醒

    @Override
    public BaseMessage parseJson(String jsonStr) {
        PLogger.d(jsonStr);
        this.setJsonStr(jsonStr);
        JSONObject object = getJsonObject(jsonStr);
        this.setType(object.optInt("mtp")); //消息类型
        this.setMsgDesc(object.optString("mct")); //消息内容
        this.setTime(object.optLong("mt")); //消息时间 int64
        this.setKnow(object.optInt("know"));
        return this;
    }

    @Override
    public String getJson(BaseMessage message) {
        return null;
    }

    /**
     * 是否为发送者
     */
    public boolean isSender() {
        return App.uid == getSendID();
    }

    private int displayWidth = DisplayWidth.getDisplayWidth();//消息显示的宽度，默认是80

    private long id;//自增ID
    private String channelID;// 频道ID 群ID
    private String whisperID;//私聊ID
    private long sendID;// 发送ID
    private long msgID = -1;//服务器消息ID
    private long cMsgID = -1;//客户端消息ID
    private long specialMsgID = -1;//特殊消息ID，对消息而言
    private long time;
    private String content;//具体内容
    private String jsonStr;//json串
    private int status;//1.发送成功2.发送失败3.发送中 10.未读11.已读//12未审核通过   私聊列表中是最后一条消息的状态
    private int fStatus = 1; // 给所有具有操作状态的消息用。1 表示可以操作；0 表示已经处理过
    private int type;//消息类型
    private int dataSource = 1;//数据来源 1.本地  2.网络  3.离线(默认是本地) 4.模拟消息
    private int version = 1;//版本
    private String msgDesc;//消息描述 mct
    private long know = MessageConstant.Know_stranger;//有know就不用ru了
    private boolean isSave;//是否保存

    //这几个字段 目前基本是给私聊用的
    private int num;//私聊列表专用字段
    private int Weight = MessageConstant.Small_Weight;//Item的权重
    private int MailItemStyle = MailItemType.Mail_Item_Ordinary.type;//私聊列表样式

    private int fPrivateNum;
    private int fGiftNum;

    public int getMailItemStyle() {
        return MailItemStyle;
    }

    public void setMailItemStyle(int mailItemStyle) {
        MailItemStyle = mailItemStyle;
    }

    /**
     * 初始化 MailItemStyle， Weight
     */
    public void initMailItemStyleWeight() {
        this.Weight = MessageConstant.Small_Weight;//Item的权重
        this.MailItemStyle = MailItemType.Mail_Item_Ordinary.type;//私聊列表样式
    }

    public int getfPrivateNum() {
        return fPrivateNum;
    }

    public void setfPrivateNum(int fPrivateNum) {
        this.fPrivateNum = fPrivateNum;
    }

    public int getfGiftNum() {
        return fGiftNum;
    }

    public void setfGiftNum(int fGiftNum) {
        this.fGiftNum = fGiftNum;
    }

    public int getWeight() {
        return Weight;
    }

    public void setWeight(int weight) {
        Weight = weight;
    }

    /***********************/
    private ChatPanelType msgPanelType;  // 消息面板用的：是否使用自定义消息面板。

    public int getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public String getWhisperID() {
        return whisperID;
    }

    //转成LONG型
    public long getLWhisperID() {
        return TypeConvertUtil.toLong(whisperID);
    }

    public void setWhisperID(String whisperID) {
        this.whisperID = whisperID;
    }

    public String getSSendID() {
        return String.valueOf(sendID);
    }

    public long getSendID() {
        return sendID;
    }

    public void setSendID(long sendID) {
        this.sendID = sendID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        isSave = save;
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public JSONObject getJsonObj() {
        return getJsonObject(jsonStr);
    }

    public void setJsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getMsgID() {
        return msgID;
    }

    public void setMsgID(long msgID) {
        this.msgID = msgID;
    }

    public long getcMsgID() {
        return cMsgID;
    }

    public void setcMsgID(long cMsgID) {
        this.cMsgID = cMsgID;
    }

    public long getSpecialMsgID() {
        return specialMsgID;
    }

    public void setSpecialMsgID(long specialMsgID) {
        this.specialMsgID = specialMsgID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getfStatus() {
        return fStatus;
    }

    public void setfStatus(int fStatus) {
        this.fStatus = fStatus;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public static long getCMsgID() {
        return MsgIDUtils.getMsgIDUtils().getMsgID();
    }

    public long getCurrentTime() {
        return ModuleMgr.getAppMgr().getTime();
    }

    public long getConversionTime(String date) {//转换时间
        return TimeUtil.stringTDateToMillisecond(date);
    }

    public boolean isCustomMsgPanel() {
        return ChatPanelType.CPT_Custom == msgPanelType;
    }

    public void setMsgPanelType(ChatPanelType msgPanelType) {
        this.msgPanelType = msgPanelType;
    }

    public ChatPanelType getMsgPanelType() {
        return msgPanelType;
    }

    public int getDataSource() {
        return dataSource;
    }

    public void setDataSource(int dataSource) {
        this.dataSource = dataSource;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getMsgDesc() {
        return msgDesc;
    }

    public void setMsgDesc(String msgDesc) {
        this.msgDesc = msgDesc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getKnow() {
        return know;
    }

    public void setKnow(long know) {
        this.know = know;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BaseMessage o = null;
        try {
            o = (BaseMessage) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }

    public BaseMessage() {
        super();
    }

    public BaseMessage(String channelID, String whisperID) {
        super();
        this.setChannelID(channelID);
        this.setWhisperID(whisperID);
        this.setSendID(App.uid);
        this.setTime(getCurrentTime());
        this.setcMsgID(getCMsgID());
        this.setMsgID(getcMsgID());
        PLogger.d("getCMsgID()=" + getcMsgID() + "");
    }

    //fmessage
    public BaseMessage(Bundle bundle) {
        this.setId(bundle.getLong(FMessage._ID));
        this.setChannelID(bundle.getString(FMessage.COLUMN_CHANNELID));
        this.setWhisperID(bundle.getString(FMessage.COLUMN_WHISPERID));
        this.setSendID(bundle.getLong(FMessage.COLUMN_SENDID));
        this.setMsgID(bundle.getLong(FMessage.COLUMN_MSGID));
        this.setcMsgID(bundle.getLong(FMessage.COLUMN_CMSGID));
        this.setSpecialMsgID(bundle.getLong(FMessage.COLUMN_SPECIALMSGID));
        this.setType(bundle.getInt(FMessage.COLUMN_TYPE));
        this.setStatus(bundle.getInt(FMessage.COLUMN_STATUS));
        this.setfStatus(bundle.getInt(FMessage.COLUMN_FSTATUS));
        this.setTime(bundle.getLong(FMessage.COLUMN_TIME));
        this.setJsonStr(bundle.getString(FMessage.COLUMN_CONTENT));
    }

    //私聊列表
    public BaseMessage(Bundle bundle, boolean fletter) {
        this.setId(bundle.getLong(FLetter._ID));
        this.setWhisperID(bundle.getString(FLetter.COLUMN_USERID));
        this.setInfoJson(bundle.getString(FLetter.COLUMN_INFOJSON));
        parseInfoJson(getInfoJson());
        this.setType(bundle.getInt(FLetter.COLUMN_TYPE));
        this.setKfID(bundle.getInt(FLetter.COLUMN_KFID));
        this.setStatus(bundle.getInt(FLetter.COLUMN_STATUS));
        this.setcMsgID(bundle.getLong(FLetter.COLUMN_CMSGID));
        this.setKnow(bundle.getInt(FLetter.COLUMN_RU));
        this.setTime(bundle.getLong(FLetter.COLUMN_TIME));
        this.setJsonStr(bundle.getString(FLetter.COLUMN_CONTENT));
        this.setMsgID(bundle.getLong(FLetter.COLUMN_MSGID));
        this.setNum(bundle.getInt(FLetter.Num));

        this.setfPrivateNum(bundle.getInt(FLetter.FPRIVATENUM));
        this.setfGiftNum(bundle.getInt(FLetter.FGIFTNUM));
    }

    /**
     * 转换JSON 转子类的时候用
     *
     * @param jsonStr
     */
    public void convertJSON(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) return;
        this.setJsonStr(jsonStr);
    }

    /**
     * 私聊列表
     */
    public static BaseMessage parseToLetterMessage(Bundle bundle) {
        BaseMessage message = new BaseMessage();
        if (bundle == null) {
            return message;
        }
        BaseMessageType messageType = BaseMessage.BaseMessageType.valueOf(bundle.getInt(FLetter.COLUMN_TYPE));
        if (messageType == null) {
            message = new BaseMessage(bundle, true);
            return message;
        }
        switch (messageType) {
            case html:
            case hint:
            case htmlText:
            case autoUpdateHtml:
                message = new TextMessage(bundle, true);
                break;
            case hi:
            case common:
                message = new CommonMessage(bundle, true);
                break;
            case gift:
            case wantGift:
                message = new GiftMessage(bundle, true);
                break;
            case video:
                message = new VideoMessage(bundle, true);
                break;
            case sysNotice:
                message = new SysNoticeMessage(bundle, true);
                break;
            case inviteVideoMass:
                message = new InviteVideoMessage(bundle, true);
                break;
            case privatePhoto:
            case privateVideo:
            case privateVoice:
                message = new PrivateMessage(bundle, true);
                break;
            case chumInvite:
                message = new ChumInviteMessage(bundle, true);
                break;
            case chumTask:
                message = new ChumTaskMessage(bundle, true);
                break;
            case smallIcon:
                message = new SmallIconMessage(bundle, true);
                break;
            case giftRedPackage:
                message = new GiftRedPackageMesaage(bundle, true);
                break;
            default:
                message = new BaseMessage(bundle, true);
                break;
        }
        return message;
    }

    //内容表
    public static BaseMessage parseToBaseMessage(Bundle bundle) {
        BaseMessage message = new BaseMessage();
        if (bundle == null) {
            return message;
        }

        BaseMessageType messageType = BaseMessage.BaseMessageType.valueOf(bundle.getInt(FMessage.COLUMN_TYPE));
        if (messageType == null) {
            message = new BaseMessage(bundle);
            return message;
        }
        switch (messageType) {
            case html:
            case hint:
            case htmlText:
            case autoUpdateHtml:
                message = new TextMessage(bundle);
                break;
            case hi:
            case common:
                message = new CommonMessage(bundle);
                break;
            case gift:
            case wantGift:
                message = new GiftMessage(bundle);
                break;
            case video:
                message = new VideoMessage(bundle);
                break;
            case sysNotice:
                message = new SysNoticeMessage(bundle);
                break;
            case inviteVideoMass:
                message = new InviteVideoMessage(bundle);
                break;
            case privatePhoto:
            case privateVideo:
            case privateVoice:
                message = new PrivateMessage(bundle);
                break;
            case chumInvite:
                message = new ChumInviteMessage(bundle);
                break;
            case chumTask:
                message = new ChumTaskMessage(bundle);
                break;
            case smallIcon:
                message = new SmallIconMessage(bundle);
                break;
            case giftRedPackage:
                message = new GiftRedPackageMesaage(bundle);
                break;
            default:
                message = new BaseMessage(bundle);
                break;
        }
        return message;
    }

    /**
     * 列表显示转换
     *
     * @param msg
     * @return
     */
    public static String getContent(BaseMessage msg) {
        String result = "";
        BaseMessageType messageType = BaseMessage.BaseMessageType.valueOf(msg.getType());
        if (messageType == null) {
            return result;
        }
        switch (messageType) {
            case hi:
                String content = msg.getMsgDesc();
                if (TextUtils.isEmpty(content)) {
                    result = "[打招呼]";
                } else {
                    result = content.replaceAll("小友", App.context.getString(R.string.app_name));
                }
                break;
            case common:
                result = msg.getMsgDesc();
                if (TextUtils.isEmpty(result)) {
                    CommonMessage commonMessage = (CommonMessage) msg;

                    String videoUrl = commonMessage.getVideoUrl();
                    String localVideoUrl = commonMessage.getLocalVideoUrl();
                    String voiceUrl = commonMessage.getVoiceUrl();
                    String localVoiceUrl = commonMessage.getLocalVoiceUrl();
                    String img = commonMessage.getImg();
                    String localImg = commonMessage.getLocalImg();
                    if (!TextUtils.isEmpty(videoUrl) || !TextUtils.isEmpty(localVideoUrl)) {//视频
                        result = "[视频]";
                    } else if (!TextUtils.isEmpty(voiceUrl) || !TextUtils.isEmpty(localVoiceUrl)) {//语音
                        result = "[语音]";
                    } else if (!TextUtils.isEmpty(img) || !TextUtils.isEmpty(localImg)) {//图片
                        result = "[图片]";
                    }
                }
                break;
            case video: {
                VideoMessage videoMessage = (VideoMessage) msg;
                boolean sender = videoMessage.getStatus() == MessageConstant.OK_STATUS ||
                        videoMessage.getStatus() == MessageConstant.FAIL_STATUS || videoMessage.getStatus() == MessageConstant.SENDING_STATUS;
                result = VideoMessage.transLastStatusText(videoMessage.getEmLastStatus(),
                        TimeUtil.getFormatTimeChatTip(TimeUtil.onPad(videoMessage.getTime())), sender);
                break;
            }
            case hint:
            case html://html消息
            case htmlText:
                result = msg.getMsgDesc();
                break;
            case gift:
            case wantGift:
                result = "[礼物]";
                break;
            case autoUpdateHtml:
                result = "[系统消息]";
                break;
            case sysNotice:
                result = "[活动通知] " + msg.getMsgDesc();
                break;
            case inviteVideoMass:
                InviteVideoMessage inviteMessage = (InviteVideoMessage) msg;
                if (inviteMessage.getMedia_tp() == 1) {
                    result = "[视频邀请]";
                } else {
                    result = "[语音邀请]";
                }
                break;
            case maxVersion:
                result = "[你的版本过低，无法接收此类消息]";
                break;
            case privatePhoto:
                result = "[私密图片]";
                break;
            case privateVideo:
                result = "[私密视频]";
                break;
            case privateVoice:
                PrivateMessage message = (PrivateMessage) msg;
                if (TextUtils.isEmpty(message.getVoiceUrl())) {
                    result = "[私密消息]";
                } else {
                    result = "[私密语音]";
                }
                break;
            case RED_PACKAGE:
                result = "[红包]";
                break;
            case chumInvite:
                result = "[好友邀请]";
                break;
            case chumTask:
                result = "[好友任务]";
                break;
            case smallIcon:
                if (!TextUtils.isEmpty(msg.getMsgDesc())) {
                    result = msg.getMsgDesc();
                } else {
                    result = "[小图标消息]";
                }
                break;
            case giftRedPackage:
                long tid = msg.getJsonObj().optLong("tid");
                if(App.uid != tid){//发送方
                    result = "[您送给TA一个红包]";
                }else {
                    if (!TextUtils.isEmpty(msg.getMsgDesc())) {
                        result = "["+msg.getMsgDesc()+"]";
                    } else {
                        result = "[TA送您一个红包]";
                    }
                }
                break;
            default:
                result = msg.getMsgDesc();
                break;
        }
        return result;
    }
}