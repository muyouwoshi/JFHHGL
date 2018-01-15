package com.juxin.predestinate.bean.db;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;

import com.juxin.library.log.PSP;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage.BaseMessageType;
import com.juxin.predestinate.module.local.chat.msgtype.CommonMessage;
import com.juxin.predestinate.module.local.chat.msgtype.GiftMessage;
import com.juxin.predestinate.module.local.chat.msgtype.TextMessage;
import com.juxin.predestinate.module.local.chat.msgtype.VideoMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.QueryObservable;
import com.squareup.sqlbrite2.SqlBrite;

import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 旧数据库迁移模块
 * Created by siow on 2017/5/19.
 */
public class OldDBModule {
    private static final String MESSAGE_LIST_TABLE = "message_list";

    private static final int INDEX_COLUMN_LOGIN_ID = 1;
    private static final int INDEX_COLUMN_MSG_ID = 2;
    private static final int INDEX_COLUMN_OTHER_ID = 3;

    private static final int INDEX_COLUMN_CONTENT = 4;
    private static final int INDEX_COLUMN_TIME = 5;
    private static final int INDEX_COLUMN_RECEIVE_SEND_STATUS = 6;
    private static final int INDEX_COLUMN_STATUS = 7;
    private static final int INDEX_COLUMN_MSG_TYPE = 9;

    @Inject
    DBCenter dbCenter;

    @Singleton
    private BriteDatabase provideDB(Context context) {
        final SqlBrite.Builder builder = new SqlBrite.Builder();
        OldDBHelper oldDBHelper = new OldDBHelper(context);
        return builder.build().wrapDatabaseHelper(oldDBHelper, Schedulers.io());
    }

    /**
     * 升级数据库
     *
     * @param uid
     */
    public void updateDB(long uid) {
        if (PSP.getInstance().getBoolean("updateDB_" + uid, false))
            return;
        PSP.getInstance().put("updateDB_" + uid, true);

        if (!OldDBHelper.isExitsDB())
            return;
        // 注入dagger组件
        ModuleMgr.getChatListMgr().getAppComponent().inject(this);
        BriteDatabase db = provideDB(App.context);
        QueryObservable query = db.createQuery(MESSAGE_LIST_TABLE, "SELECT * FROM " + MESSAGE_LIST_TABLE +
                " WHERE msg_type <> 0 and other_id <> 9999 and login_id=" + uid);
        query.subscribe(new Observer<SqlBrite.Query>() {
            private Disposable queryDisposable;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                queryDisposable = d;
            }

            @Override
            public void onNext(@NonNull SqlBrite.Query query) {
                queryDisposable.dispose();
                convertRecord(query);
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
     * 数据库记录转换
     *
     * @param query
     */
    private void convertRecord(SqlBrite.Query query) {
        Cursor cursor = null;
        try {
            cursor = query.run();
            if (cursor == null)
                return;
//            int c1 = cursor.getCount();
//            int c2 = 0;
            while (cursor.moveToNext()) {
                try {
                    int type = cursor.getInt(INDEX_COLUMN_MSG_TYPE);
                    //发送端：0正在发送1送达2失败3已读4警告图标5无标记状态
                    int msg_status = cursor.getInt(INDEX_COLUMN_STATUS);
                    //0接收  1发送
                    int receive_send_status = cursor.getInt(INDEX_COLUMN_RECEIVE_SEND_STATUS);
                    long msgID = cursor.getLong(INDEX_COLUMN_MSG_ID);
                    long login_id = Long.parseLong(cursor.getString(INDEX_COLUMN_LOGIN_ID));
                    long other_id = Long.parseLong(cursor.getString(INDEX_COLUMN_OTHER_ID));
                    long time = Long.parseLong(cursor.getString(INDEX_COLUMN_TIME));
                    String content = cursor.getString(INDEX_COLUMN_CONTENT);

                    BaseMessageType messageType = BaseMessageType.valueOf(type);
                    if (messageType == null)
                        continue;

                    BaseMessage message = messageType.msgClass.newInstance();
                    //从content解析出数据存放到bundle
                    Bundle bundle = getBundleFromContent(messageType, content);
                    //填充bundle里的数据到message
                    setBundleToMessage(messageType, message, bundle);

                    //视频、语音的真正消息ID需特殊处理，不是从数据库里取出来的
                    if (messageType == BaseMessageType.video)
                        msgID = bundle.getLong("d");

                    //0接收  1发送
                    if (receive_send_status == 0) {
                        message.setMsgID(msgID);
                        message.setStatus(MessageConstant.READ_STATUS);
                    } else {
                        //发送端：0正在发送1送达2失败3已读4警告图标5无标记状态
                        int new_status = 0;
                        switch (msg_status) {
                            case 0:
                            case 2:
                                new_status = MessageConstant.FAIL_STATUS;
                                break;
                            case 1:
                            case 5:
                                new_status = MessageConstant.OK_STATUS;
                                break;
                            case 3:
                                new_status = MessageConstant.READ_STATUS;
                                break;
                            case 4:
                                new_status = MessageConstant.BLACKLIST_STATUS;
                                break;
                            default:
                                break;
                        }
                        message.setcMsgID(msgID);
                        message.setStatus(new_status);
                    }

                    message.setSendID(receive_send_status == 0 ? other_id : login_id);
                    message.setWhisperID(other_id + "");
                    message.setType(type);
                    message.setTime(time);
                    message.setDataSource(MessageConstant.ONE);
                    message.setChannelID(null);

                    String jsonStr = message.getJson(message);
                    if (receive_send_status == 0) {
                        JSONObject json = new JSONObject(message.getJson(message));
                        json.put("fid", other_id);
                        jsonStr = json.toString();
                    }
                    message.setJsonStr(jsonStr);
                    dbCenter.insertMsg(message, null);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
//            Log.d("aaa", "c1:" + c1 + ", c2:" + c2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private void setBundleToMessage(BaseMessageType messageType, BaseMessage message, Bundle bundle) {
        switch (messageType) {
            case hi:
            case common:
                CommonMessage commonMessage = (CommonMessage) message;
                commonMessage.setMsgDesc(bundle.getString("msg")); //消息内容
                //图片
                commonMessage.setImg(bundle.getString("imageUrl"));
                //语音
                commonMessage.setVoiceUrl(bundle.getString("voiceUrl"));
                commonMessage.setVoiceLen(bundle.getInt("voiceLen"));
                commonMessage.setVoiceUserid(bundle.getLong("voiceUid"));
                //视频
                commonMessage.setVideoUrl(bundle.getString("videoUrl"));
                commonMessage.setVideoLen(bundle.getInt("videoLen"));
                commonMessage.setVideoThumb(bundle.getString("videoThumb"));
                commonMessage.setVideoWidth(bundle.getInt("videoThumbWidth"));
                commonMessage.setVideoHeight(bundle.getInt("videoThumbHeight"));
                break;
            case hint:
            case html:
            case htmlText:
            case autoUpdateHtml:
                TextMessage textMessage = (TextMessage) message;
                textMessage.setHtm(bundle.getString("htm"));
                textMessage.setMsgDesc(bundle.getString("msg"));
                break;
            case gift:
            case wantGift:
                GiftMessage giftMessage = (GiftMessage) message;
                giftMessage.setGiftID(bundle.getInt("gift_id"));
                giftMessage.setGiftCount(bundle.getInt("count"));
                giftMessage.setGiftLogID(bundle.getLong("gift_log_id"));
                giftMessage.setfStatus(bundle.getBoolean("giftReceived") ? 0 : 1);
                break;
            case video:
                VideoMessage videoMessage = (VideoMessage) message;
                videoMessage.setVideoID((int) bundle.getLong("vc_id"));
                videoMessage.setSpecialMsgID(bundle.getLong("vc_id"));
                videoMessage.setVideoTp(bundle.getInt("vc_tp"));
                videoMessage.setVideoMediaTp(bundle.getInt("media_tp"));
                videoMessage.setVideoVcEscCode(bundle.getInt("vc_esc_code"));
                videoMessage.setVideoVcTalkTime(bundle.getInt("vc_talk_time"));
                videoMessage.setVc_channel_key(bundle.getString("vc_channel_key"));
                break;
            default:
                message.setMsgDesc(bundle.getString("msg"));
                break;
        }
    }

    private Bundle getBundleFromContent(BaseMessageType messageType, String content) {
        Bundle bundle = new Bundle();
        try {
            if (TextUtils.isEmpty(content) ||
                    (content.charAt(0) != '{') ||
                    (content.charAt(content.length() - 1) != '}')) {
                bundle.putString("msg", content);
                return bundle;
            }

            JSONObject msgJSO = new JSONObject(JSONTokener(content));
            bundle.putString("msg", msgJSO.optString("msg"));
            switch (messageType) {
                case common:
                case hi:
                    if (!msgJSO.isNull("voice")) {
                        String voiceUrl = msgJSO.optJSONObject("voice").optString("url");
                        if (!TextUtils.isEmpty(voiceUrl) && !voiceUrl.endsWith(".amr"))
                            voiceUrl = voiceUrl + ".amr";
                        bundle.putString("voiceUrl", voiceUrl);
                        bundle.putInt("voiceLen", Integer.parseInt(msgJSO.optJSONObject("voice").optString("len")));
                        bundle.putLong("voiceUid", Long.parseLong(msgJSO.optJSONObject("voice").optString("voice_userid")));
                        //bundle.putString("voiceMod", msgJSO.optJSONObject("voice").optString("mod"));
                    } else if (!msgJSO.isNull("video")) {
                        bundle.putString("videoUrl", msgJSO.optJSONObject("video").optString("url"));
                        bundle.putInt("videoLen", Integer.parseInt(msgJSO.optJSONObject("video").optString("len")));
                        bundle.putString("videoThumb", msgJSO.optJSONObject("video").optString("thumb"));
                        bundle.putInt("videoThumbWidth", msgJSO.optJSONObject("video").optInt("width"));
                        bundle.putInt("videoThumbHeight", msgJSO.optJSONObject("video").optInt("height"));
                    } else if (!msgJSO.isNull("image")) {
                        bundle.putString("imageUrl", msgJSO.optString("image"));
                    }
                    break;
                case gift:
                    if (msgJSO.isNull("gift")) {
                        JSONObject jsc = msgJSO.optJSONObject("gift");
                        bundle.putInt("gift_id", jsc.optInt("gift_id"));
                        bundle.putInt("count", jsc.optInt("count"));
                        bundle.putLong("gift_log_id", jsc.optLong("gift_log_id"));
                        bundle.putBoolean("giftReceived", jsc.optBoolean("received"));
                        bundle.putString("giftUrl", msgJSO.optString("gift_url"));
                        bundle.putString("giftInfo", msgJSO.optString("info"));
                    }
                    break;
                case wantGift:
                    if (!msgJSO.isNull("give_gift")) {
                        JSONObject jsc = msgJSO.optJSONObject("give_gift");
                        bundle.putInt("gift_id", jsc.optInt("gift_id"));
                    }
                    break;
                case hint:
                    break;
                case html:
                case htmlText:
                case autoUpdateHtml:
                    if (!msgJSO.isNull("htm"))
                        bundle.putString("htm", msgJSO.optString("htm"));
                    break;
                case video:
                    bundle.putString("tid", msgJSO.optString("tid"));// 发给谁的Id
                    bundle.putString("fid", msgJSO.optString("fid")); // 发消息人的Id
                    bundle.putLong("d", Long.parseLong(msgJSO.optString("d"))); // 消息ID
                    bundle.putString("mt", msgJSO.optString("mt")); // 消息时间（时间戳）
                    bundle.putLong("vc_id", msgJSO.optLong("vc_id"));//视频聊天ID，一次视频聊天过程的唯一标识
                    bundle.putInt("vc_tp", msgJSO.optInt("vc_tp"));//请求类型，1邀请加入聊天，2同意加入 3拒绝加入 4挂断（挂断可能会收到不止一次）
                    bundle.putInt("media_tp", msgJSO.optInt("media_tp"));//[opt]媒体类型只在vc_tp=1邀请加入聊天时会包含此字段， 1视频, 2语音
                    bundle.putInt("vc_esc_code", msgJSO.optInt("vc_esc_code"));//[opt]拒绝或取消 只在vc_tp=3 时生效 1未接通，对方无应答 2接收方拒绝 3发送方取消
                    bundle.putInt("vc_talk_time", msgJSO.optInt("vc_talk_time"));//[opt]聊天耗时 单位秒 只在  vc_tp=4挂断时有效
                    bundle.putString("vc_channel_key", msgJSO.optString("vc_channel_key"));//[opt]视频聊天要用的的channel_key 只在  vc_tp=2同意时有效
                    bundle.putInt("receive_send_status", msgJSO.optInt("receive_send_status"));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bundle;
    }

    // 如果是4.0以下版本调用这个方法BOM
    private String JSONTokener(String in) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < 14) {// 14为4.0
            // consume an optional byte order mark (BOM) if it exists
            if (in != null && in.startsWith("\ufeff")) {
                in = in.substring(1);
            }
            return in;
        }
        return in;
    }
}
