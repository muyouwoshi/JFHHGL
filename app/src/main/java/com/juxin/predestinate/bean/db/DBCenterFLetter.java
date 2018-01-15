package com.juxin.predestinate.bean.db;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.db.utils.CloseUtil;
import com.juxin.predestinate.bean.db.utils.CursorUtil;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.ByteUtil;
import com.juxin.predestinate.module.util.TimeUtil;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * fletter 处理
 * Created by Kind on 2017/3/28.
 */
public class DBCenterFLetter {

    private BriteDatabase mDatabase;
    private Handler handler;

    public DBCenterFLetter(BriteDatabase database, Handler handler) {
        this.mDatabase = database;
        this.handler = handler;
    }

    public long storageData(BaseMessage message) {
        long ret = MessageConstant.OK;
        BaseMessage temp = isExist(message.getWhisperID());
        if (temp == null) {//没有数据
            ret = insertLetter(message);
        } else if ((message.getType() == temp.getType()) && (message.getType() == BaseMessage.BaseMessageType.video.getMsgType()
                || message.getType() == BaseMessage.BaseMessageType.chumInvite.getMsgType()
                || message.getType() == BaseMessage.BaseMessageType.chumTask.getMsgType())) {
            ret = updateOneLetter(message, temp);
        } else if (!message.isSender() || (message.getcMsgID() >= temp.getcMsgID()) || (message.getKnow() > temp.getKnow())) {
            ret = updateOneLetter(message, temp);
        }
        return ret;
    }

    /**
     * 多条消息插入
     *
     * @param list
     */
    public void insertLetterBatch(final List<BaseMessage> list, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                long ret = MessageConstant.OK;
                BriteDatabase.Transaction transaction = mDatabase.newTransaction();
                try {
                    for (BaseMessage item : list) {
                        ret = insertLetter(item);
                        if (ret != MessageConstant.OK) {
                            break;
                        }
                    }
                    transaction.markSuccessful();
                } finally {
                    transaction.end();
                }

                DBCenter.makeDBCallback(callback, ret);
            }
        });
    }

    /**
     * 单条消息插入
     *
     * @param baseMessage
     * @return
     */
    private long insertLetter(final BaseMessage baseMessage) {
        if (baseMessage == null) {
            return MessageConstant.ERROR;
        }

        try {
            final ContentValues values = new ContentValues();
            values.put(FLetter.COLUMN_USERID, baseMessage.getWhisperID());

            if (TextUtils.isEmpty(baseMessage.getInfoJson()))
                values.put(FLetter.COLUMN_INFOJSON, ByteUtil.toBytesUTF(baseMessage.getInfoJson()));

            if (baseMessage.getKfID() != -1)
                values.put(FLetter.COLUMN_KFID, baseMessage.getKfID());

            if (baseMessage.getKnow() != -1)
                values.put(FLetter.COLUMN_RU, baseMessage.getKnow());

            if (baseMessage.getcMsgID() != -1)
                values.put(FLetter.COLUMN_CMSGID, baseMessage.getcMsgID());

            values.put(FLetter.COLUMN_TYPE, baseMessage.getType());
            values.put(FLetter.COLUMN_STATUS, baseMessage.getStatus());
            values.put(FLetter.COLUMN_TIME, baseMessage.getTime());
            values.put(FLetter.COLUMN_CONTENT, ByteUtil.toBytesUTF(baseMessage.getJsonStr()));

            if (baseMessage.getMsgID() != -1)
                values.put(FLetter.COLUMN_MSGID, baseMessage.getMsgID());

            return mDatabase.insert(FLetter.FLETTER_TABLE, values) >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageConstant.ERROR;
        }
    }

    /**
     * @param baseMessage 当前要存储的消息
     * @param previousMsg 上一个消息
     * @return
     */
    private int updateOneLetter(BaseMessage baseMessage, BaseMessage previousMsg) {
        if (baseMessage == null) {
            return MessageConstant.ERROR;
        }

        try {
            final ContentValues values = new ContentValues();
            if (baseMessage.getInfoJson() != null)
                values.put(FLetter.COLUMN_INFOJSON, ByteUtil.toBytesUTF(baseMessage.getInfoJson()));

            if (baseMessage.getType() != -1)
                values.put(FLetter.COLUMN_TYPE, baseMessage.getType());

            if (baseMessage.getStatus() != -1)
                values.put(FLetter.COLUMN_STATUS, baseMessage.getStatus());

            long cMsgID = baseMessage.getcMsgID();
            if (cMsgID != -1)
                values.put(FLetter.COLUMN_CMSGID, cMsgID);

            if (previousMsg == null || (baseMessage.getKnow() > previousMsg.getKnow()))
                values.put(FLetter.COLUMN_RU, baseMessage.getKnow());

            if (baseMessage.getTime() != -1)
                values.put(FLetter.COLUMN_TIME, baseMessage.getTime());

            if (baseMessage.getMsgID() != -1)
                values.put(FLetter.COLUMN_MSGID, baseMessage.getMsgID());

            values.put(FLetter.COLUMN_CONTENT, ByteUtil.toBytesUTF(baseMessage.getJsonStr()));

            long ret = mDatabase.update(FLetter.FLETTER_TABLE, values, FLetter.COLUMN_USERID + " = ? ", baseMessage.getWhisperID());
            return ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageConstant.ERROR;
        }
    }

    public void updateLetter(final BaseMessage baseMessage, final BaseMessage tmpMsg, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                DBCenter.makeDBCallback(callback, updateOneLetter(baseMessage, tmpMsg));
            }
        });
    }

    public boolean updateUserInfoLightList(final List<UserInfoLightweight> lightweights) {
        if (lightweights == null || lightweights.size() <= 0) {
            return false;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                BriteDatabase.Transaction transaction = mDatabase.newTransaction();
                try {
                    for (UserInfoLightweight temp : lightweights) {
                        updateOneUserInfoLight(temp);
                    }
                    transaction.markSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    transaction.end();
                }
            }
        });
        return true;
    }

    private long updateOneUserInfoLight(UserInfoLightweight lightweight) {
        try {
            if (isExist(String.valueOf(lightweight.getUid())) == null)
                return MessageConstant.ERROR;//没有数据

            final ContentValues values = new ContentValues();
            if (lightweight.getInfoJson() != null)
                values.put(FLetter.COLUMN_INFOJSON, ByteUtil.toBytesUTF(lightweight.getInfoJson()));

            mDatabase.update(FLetter.FLETTER_TABLE, values, FLetter.COLUMN_USERID + " = ? ", String.valueOf(lightweight.getUid()));
        } catch (Exception e) {
            e.printStackTrace();
            return MessageConstant.ERROR;
        }
        return MessageConstant.OK;
    }

    /**
     * 更新个人资料
     *
     * @param lightweight
     * @return
     */
    public void updateUserInfoLight(final UserInfoLightweight lightweight, final DBCallback callback) {
        if (lightweight == null) {
            DBCenter.makeDBCallback(callback, MessageConstant.ERROR);
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                long ret = updateOneUserInfoLight(lightweight);
                DBCenter.makeDBCallback(callback, ret);
            }
        });
    }

    private BaseMessage isExist(String userid) {
        Cursor cursor = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM ").append(FLetter.FLETTER_TABLE)
                    .append(" WHERE ").append(FLetter.COLUMN_USERID + " = ?");
            cursor = mDatabase.query(sql.toString(), userid);
            if (cursor != null && cursor.moveToFirst()) {
                BaseMessage message = new BaseMessage();
                message.setWhisperID(CursorUtil.getString(cursor, FLetter.COLUMN_USERID));
                message.setInfoJson(CursorUtil.getBlobToString(cursor, FLetter.COLUMN_INFOJSON));
                message.setType(CursorUtil.getInt(cursor, FLetter.COLUMN_TYPE));
                message.setKfID(CursorUtil.getInt(cursor, FLetter.COLUMN_KFID));
                message.setStatus(CursorUtil.getInt(cursor, FLetter.COLUMN_STATUS));
                message.setcMsgID(CursorUtil.getLong(cursor, FLetter.COLUMN_CMSGID));
                message.setKnow(CursorUtil.getInt(cursor, FLetter.COLUMN_RU));
                message.setTime(CursorUtil.getLong(cursor, FLetter.COLUMN_TIME));
                message.setJsonStr(CursorUtil.getBlobToString(cursor, FLetter.COLUMN_CONTENT));
                message.setMsgID(CursorUtil.getLong(cursor, FLetter.COLUMN_MSGID));
                return message;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(cursor);
        }
        return null;
    }

    public Observable<BaseMessage> isHaveMsg(String userid) {
        String sql = "SELECT * FROM " + FLetter.FLETTER_TABLE + " WHERE " +
                FLetter.COLUMN_USERID + " = " + userid;

        return mDatabase.createQuery(FLetter.COLUMN_USERID, sql)
                .map(new Function<SqlBrite.Query, BaseMessage>() {
                    @Override
                    public BaseMessage apply(@NonNull SqlBrite.Query query) throws Exception {
                        BaseMessage message = new BaseMessage();
                        Cursor cursor = null;
                        try {
                            cursor = query.run();
                            if (cursor != null && cursor.moveToFirst()) {
                                message.setWhisperID(CursorUtil.getString(cursor, FLetter.COLUMN_USERID));
                                message.setInfoJson(CursorUtil.getBlobToString(cursor, FLetter.COLUMN_INFOJSON));
                                message.setType(CursorUtil.getInt(cursor, FLetter.COLUMN_TYPE));
                                message.setKfID(CursorUtil.getInt(cursor, FLetter.COLUMN_KFID));
                                message.setStatus(CursorUtil.getInt(cursor, FLetter.COLUMN_STATUS));
                                message.setcMsgID(CursorUtil.getLong(cursor, FLetter.COLUMN_CMSGID));
                                message.setKnow(CursorUtil.getInt(cursor, FLetter.COLUMN_RU));
                                message.setTime(CursorUtil.getLong(cursor, FLetter.COLUMN_TIME));
                                message.setJsonStr(CursorUtil.getBlobToString(cursor, FLetter.COLUMN_CONTENT));
                                message.setMsgID(CursorUtil.getLong(cursor, FLetter.COLUMN_MSGID));
                                return message;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            CloseUtil.close(cursor);
                        }
                        return message;
                    }
                });
    }

    /**
     * 聊天记录
     *
     * @return
     */
    public Observable<List<BaseMessage>> queryLetterList() {
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            String sql = "select f._id, f.userID, f.infoJson, f.type, f.kfID, f.status, f.cMsgID, f.ru," +
                    " f.time, f.content, f.folder, m.whisperID, m.num, m1.num1 fPrivateNum from "
                    + FLetter.FLETTER_TABLE + " f left join (select whisperID,count(*)" +
                    " num from " + FMessage.FMESSAGE_TABLE + " where status = 10 group by whisperID)" +
                    " m on f.userID = m.whisperID left join (select whisperID,count(*) num1 from "
                    + FMessage.FMESSAGE_TABLE + " where fstatus = 1 and (type = 31 or type = 32 or type = 20)" +
                    " group by whisperID) m1 on f.userID = m1.whisperID";
            return queryBySqlFletter(sql, true);
        } else {
            String sql = "select f._id, f.userID, f.infoJson, f.type, f.kfID, f.status, f.cMsgID, f.ru," +
                    " f.time, f.content, f.folder, m.whisperID, m.num, m1.num fGiftNum from " + FLetter.FLETTER_TABLE
                    + " f left join (select whisperID,count(*) num from " + FMessage.FMESSAGE_TABLE +
                    " where status = 10 group by whisperID) m on f.userID = m.whisperID left join" +
                    " (select whisperID,count(*) num from " + FMessage.FMESSAGE_TABLE + " where time > " +
                    TimeUtil.getCurrentDataPoints(3) + " and whisperID == sendID  and fstatus = 1 and" +
                    " type = 10 group by whisperID) m1 on f.userID = m1.whisperID";
            return queryBySqlFletter(sql, false);
        }
    }

    /**
     * 查询 列表
     *
     * @param sql
     * @return
     */
    public Observable<List<BaseMessage>> queryBySqlFletter(String sql, final boolean isMan) {
        return mDatabase.createQuery(FLetter.FLETTER_TABLE, sql)
                .map(new Function<SqlBrite.Query, List<BaseMessage>>() {
                    @Override
                    public List<BaseMessage> apply(@NonNull SqlBrite.Query query) throws Exception {
                        return convert(query, isMan);
                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private List<BaseMessage> convert(SqlBrite.Query query, boolean isMan) {
        Cursor cursor = null;
        ArrayList<BaseMessage> result = new ArrayList<>();
        try {
            cursor = query.run();
            while (cursor != null && cursor.moveToNext()) {
                Bundle bundle = new Bundle();
                bundle.putLong(FLetter._ID, CursorUtil.getLong(cursor, FMessage._ID));
                bundle.putString(FLetter.COLUMN_USERID, CursorUtil.getString(cursor, FLetter.COLUMN_USERID));
                bundle.putString(FLetter.COLUMN_INFOJSON, CursorUtil.getBlobToString(cursor, FLetter.COLUMN_INFOJSON));
                bundle.putInt(FLetter.COLUMN_TYPE, CursorUtil.getInt(cursor, FLetter.COLUMN_TYPE));
                bundle.putInt(FLetter.COLUMN_KFID, CursorUtil.getInt(cursor, FLetter.COLUMN_KFID));
                bundle.putInt(FLetter.COLUMN_STATUS, CursorUtil.getInt(cursor, FLetter.COLUMN_STATUS));
                bundle.putLong(FLetter.COLUMN_CMSGID, CursorUtil.getLong(cursor, FLetter.COLUMN_CMSGID));
                bundle.putInt(FLetter.COLUMN_RU, CursorUtil.getInt(cursor, FLetter.COLUMN_RU));
                bundle.putLong(FLetter.COLUMN_TIME, CursorUtil.getLong(cursor, FLetter.COLUMN_TIME));
                bundle.putString(FLetter.COLUMN_CONTENT, CursorUtil.getBlobToString(cursor, FLetter.COLUMN_CONTENT));
                bundle.putLong(FLetter.COLUMN_MSGID, CursorUtil.getLong(cursor, FLetter.COLUMN_MSGID));
                bundle.putInt(FLetter.Num, CursorUtil.getInt(cursor, FLetter.Num));

                if (isMan) {
                    bundle.putInt(FLetter.FPRIVATENUM, CursorUtil.getInt(cursor, FLetter.FPRIVATENUM));
                } else {
                    bundle.putInt(FLetter.FGIFTNUM, CursorUtil.getInt(cursor, FLetter.FGIFTNUM));
                }
                result.add(BaseMessage.parseToLetterMessage(bundle));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(cursor);
        }
        return result;
    }

    /**
     * 删除
     *
     * @param whisperID 私聊ID
     * @return
     */
    public void delete(final long whisperID, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                long result = delete(whisperID);
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }

    private int delete(long whisperID) {
        long ret = mDatabase.delete(FLetter.FLETTER_TABLE, FLetter.COLUMN_USERID + " = ? ", String.valueOf(whisperID));
        return ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
    }

    public void deleteList(final List<Long> list, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (long temp : list) {
                    delete(temp);
                }

                DBCenter.makeDBCallback(callback, MessageConstant.OK);
            }
        });
    }

    /**
     * 更新成内容
     *
     * @param userid
     * @return
     */
    public void updateContent(final String userid, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(FLetter.COLUMN_CONTENT, new byte[0]);
                values.put(FLetter.COLUMN_TYPE, 0);
                values.put(FLetter.COLUMN_TIME, 0);
                values.put(FLetter.COLUMN_STATUS, 0);
                values.put(FLetter.COLUMN_CMSGID, 0);
                values.put(FLetter.COLUMN_MSGID, 0);
                long ret = mDatabase.update(FLetter.FLETTER_TABLE, values, FLetter.COLUMN_USERID + " = ? ", userid);
                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }

    //修改为已读
    public void updateReadStatus(long userID, DBCallback callback) {
        updateStatus(userID, MessageConstant.READ_STATUS, MessageConstant.DELIVERY_STATUS, callback);
    }

    //修改为送达
    public void updateDeliveryStatus(final long msgID, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(FLetter.COLUMN_STATUS, String.valueOf(MessageConstant.DELIVERY_STATUS));
                long ret = mDatabase.update(FLetter.FLETTER_TABLE, values,
                        FLetter.COLUMN_MSGID + " = ? AND " + FLetter.COLUMN_TYPE + " != ? AND ("
                                + FMessage.COLUMN_STATUS + " = ? OR " + FMessage.COLUMN_STATUS + " = ?)",
                        String.valueOf(msgID), String.valueOf(BaseMessage.video_MsgType),
                        String.valueOf(MessageConstant.OK_STATUS), String.valueOf(MessageConstant.SENDING_STATUS));

                DBCenter.makeDBCallback(callback, (ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR));
            }
        });
    }

    /**
     * @param userID
     * @param modifyStatus   要修改为的状态
     * @param judgmentStatus 判断的状态
     * @param callback
     */
    public void updateStatus(final long userID, final int modifyStatus, final int judgmentStatus, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(FLetter.COLUMN_STATUS, String.valueOf(modifyStatus));
                long ret = mDatabase.update(FLetter.FLETTER_TABLE, values,
                        FLetter.COLUMN_USERID + " = ? AND " + FLetter.COLUMN_STATUS + " = ? AND " + FLetter.COLUMN_TYPE + " != ?",
                        String.valueOf(userID), String.valueOf(judgmentStatus), String.valueOf(BaseMessage.video_MsgType));

                DBCenter.makeDBCallback(callback, (ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR));
            }
        });
    }

    public void updateStatusFail(final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(FLetter.COLUMN_STATUS, String.valueOf(MessageConstant.FAIL_STATUS));
                long ret = mDatabase.update(FLetter.FLETTER_TABLE, values, FLetter.COLUMN_STATUS + " = ?", String.valueOf(MessageConstant.SENDING_STATUS));
                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }

    public void updateMsgStatus(final BaseMessage message, final DBCallback callback) {
        final String userID = message.getWhisperID();
        handler.post(new Runnable() {
            @Override
            public void run() {
                BaseMessage temp = isExist(message.getWhisperID());
                if (temp == null) {//没有数据
                    DBCenter.makeDBCallback(callback, MessageConstant.ERROR);
                    return;
                }

                long ret = MessageConstant.OK;
                if (BaseMessage.BaseMessageType.video.getMsgType() == message.getType()
                        && BaseMessage.BaseMessageType.video.getMsgType() == temp.getType()) {
                    ret = updateStatus(userID, message.getStatus(), message.getMsgID());
                } else if (!message.isSender() || (message.getcMsgID() >= temp.getcMsgID())) {
                    ret = updateStatus(userID, message.getStatus(), message.getMsgID());
                }

                DBCenter.makeDBCallback(callback, ret);
            }
        });
    }

    /**
     * 发送成功或失败更新状态
     *
     * @param userID
     * @param status
     * @return
     */
    private long updateStatus(String userID, int status, long msgID) {
        ContentValues values = new ContentValues();
        values.put(FLetter.COLUMN_STATUS, String.valueOf(status));
        if (msgID != -1) values.put(FLetter.COLUMN_MSGID, msgID);

        long ret = mDatabase.update(FLetter.FLETTER_TABLE, values, FLetter.COLUMN_USERID + " = ?", userID);
        return ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
    }

    public Observable<List<BaseMessage>> deleteCommon(long delTime) {
        final StringBuilder sql = new StringBuilder("SELECT * FROM ").append(FLetter.FLETTER_TABLE)
                .append(" WHERE ")
                .append(FLetter.COLUMN_TIME + " < ")
                .append(delTime);

        return mDatabase.createQuery(FLetter.FLETTER_TABLE, sql.toString())
                .map(new Function<SqlBrite.Query, List<BaseMessage>>() {
                    @Override
                    public List<BaseMessage> apply(@NonNull SqlBrite.Query query) throws Exception {
                        return convertFletter(query);
                    }
                });
    }

    public Observable<List<BaseMessage>> deleteKFID() {
        String sql = "SELECT * FROM " + FLetter.FLETTER_TABLE +
                " WHERE " +
                FLetter.COLUMN_KFID + " = " +
                MessageConstant.KF_ID +
                " AND " +
                FLetter.COLUMN_USERID + " <> " +
                MailSpecialID.customerService.getSpecialID();

        return mDatabase.createQuery(FLetter.FLETTER_TABLE, sql)
                .map(new Function<SqlBrite.Query, List<BaseMessage>>() {
                    @Override
                    public List<BaseMessage> apply(@NonNull SqlBrite.Query query) throws Exception {
                        return convertFletter(query);
                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private List<BaseMessage> convertFletter(SqlBrite.Query query) {
        Cursor cursor = null;
        ArrayList<BaseMessage> result = new ArrayList<>();
        try {
            cursor = query.run();
            while (cursor != null && cursor.moveToNext()) {
                BaseMessage message = new BaseMessage();
                message.setWhisperID(CursorUtil.getString(cursor, FLetter.COLUMN_USERID));
                message.setKfID(CursorUtil.getInt(cursor, FLetter.COLUMN_KFID));
                message.setTime(CursorUtil.getLong(cursor, FLetter.COLUMN_TIME));
                result.add(message);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(cursor);
        }
        return result;
    }
}
