package com.juxin.predestinate.bean.db;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.juxin.predestinate.bean.db.utils.CloseUtil;
import com.juxin.predestinate.bean.db.utils.CursorUtil;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.VideoMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.util.ByteUtil;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * fmessage 处理
 * Created by Kind on 2017/3/28.
 */
public class DBCenterFMessage {
    private BriteDatabase mDatabase;
    private Handler handler;

    public DBCenterFMessage(BriteDatabase database, Handler handler) {
        this.mDatabase = database;
        this.handler = handler;
    }

    public long storageDataVideo(BaseMessage message) {
        return storageDataVideo(message, true);
    }
    /**
     * 存储消息
     * @param message
     * @param isSave  tmpMsg == null && !isSave直接返上扔，就不保存了，
     * @return
     */
    public long storageDataVideo(BaseMessage message, boolean isSave) {
        BaseMessage tmpMsg = isExist(message.getSpecialMsgID());
        if(tmpMsg == null && !isSave){
            return MessageConstant.ERROR;
        }

        if (tmpMsg == null) {//没有数据
            return insertOneMsg(message);
        }

        if (deleteMsgID(message.getSpecialMsgID()) != MessageConstant.ERROR) {
            message.setTime(tmpMsg.getTime());
            return insertOneMsg(message);
        }
        return MessageConstant.OK;
    }

    /**
     * 多条消息插入
     *
     * @param list
     */
    public void insertMsgList(final List<BaseMessage> list, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                BriteDatabase.Transaction transaction = mDatabase.newTransaction();
                try {
                    for (BaseMessage item : list) {
                        insertOneMsg(item);
                    }
                    transaction.markSuccessful();
                } finally {
                    transaction.end();
                }

                DBCenter.makeDBCallback(callback, MessageConstant.OK);
            }
        });
    }

    public long insertOneMsg(BaseMessage baseMessage) {
        if (baseMessage == null) {
            return MessageConstant.ERROR;
        }

        long ret = -1;
        try {
            final ContentValues values = new ContentValues();
            if (baseMessage.getChannelID() != null)
                values.put(FMessage.COLUMN_CHANNELID, baseMessage.getChannelID());

            if (baseMessage.getWhisperID() != null)
                values.put(FMessage.COLUMN_WHISPERID, baseMessage.getWhisperID());

            if (baseMessage.getMsgID() != -1)
                values.put(FMessage.COLUMN_MSGID, baseMessage.getMsgID());

            if (baseMessage.getcMsgID() != -1)
                values.put(FMessage.COLUMN_CMSGID, baseMessage.getcMsgID());

            if (baseMessage.getSpecialMsgID() != -1)
                values.put(FMessage.COLUMN_SPECIALMSGID, baseMessage.getSpecialMsgID());

            values.put(FMessage.COLUMN_SENDID, baseMessage.getSendID());
            values.put(FMessage.COLUMN_TYPE, baseMessage.getType());
            values.put(FMessage.COLUMN_STATUS, baseMessage.getStatus());// 1.发送成功2.发送失败3.发送中 10.未读11.已读
            values.put(FMessage.COLUMN_FSTATUS, 1);// 默认为1插入的时候
            values.put(FMessage.COLUMN_TIME, baseMessage.getTime());

            values.put(FMessage.COLUMN_CONTENT, ByteUtil.toBytesUTF(baseMessage.getJsonStr()));
            ret = mDatabase.insert(FMessage.FMESSAGE_TABLE, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
    }

    /**
     * 单条消息插入
     *
     * @param baseMessage
     * @return
     */
    public void insertMsg(final BaseMessage baseMessage, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                DBCenter.makeDBCallback(callback, insertOneMsg(baseMessage));
            }
        });
    }

    /**
     * 更新fmessage
     *
     * @param baseMessage
     * @return
     */
    public void updateMsg(final BaseMessage baseMessage, final DBCallback callback) {
        if (baseMessage == null) {
            DBCenter.makeDBCallback(callback, MessageConstant.ERROR);
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                long ret = -1;
                try {
                    String channelID = baseMessage.getChannelID();
                    String whisperID = baseMessage.getWhisperID();
                    long cMsgID = baseMessage.getcMsgID();

                    String sql;
                    String[] str;
                    if (!TextUtils.isEmpty(channelID) && !TextUtils.isEmpty(whisperID)) {
                        sql = FMessage.COLUMN_CHANNELID + " = ? AND " + FMessage.COLUMN_WHISPERID + " = ? AND " + FMessage.COLUMN_CMSGID + " = ?";
                        str = new String[]{channelID, whisperID, String.valueOf(cMsgID)};
                    } else if (!TextUtils.isEmpty(channelID)) {
                        sql = FMessage.COLUMN_CHANNELID + " = ? AND " + FMessage.COLUMN_CMSGID + " = ?";
                        str = new String[]{channelID, String.valueOf(cMsgID)};
                    } else {
                        sql = FMessage.COLUMN_WHISPERID + " = ? AND " + FMessage.COLUMN_CMSGID + " = ?";
                        str = new String[]{whisperID, String.valueOf(cMsgID)};
                    }

                    ContentValues values = new ContentValues();
                    if (baseMessage.getMsgID() != -1)
                        values.put(FMessage.COLUMN_MSGID, baseMessage.getMsgID());

                    if (baseMessage.getStatus() != -1)
                        values.put(FMessage.COLUMN_STATUS, baseMessage.getStatus());

                    if (baseMessage.getTime() != -1)
                        values.put(FMessage.COLUMN_TIME, baseMessage.getTime());

                    if (baseMessage.getSpecialMsgID() != -1)
                        values.put(FMessage.COLUMN_SPECIALMSGID, baseMessage.getSpecialMsgID());

                    if (baseMessage.getJsonStr() != null)
                        values.put(FMessage.COLUMN_CONTENT, ByteUtil.toBytesUTF(baseMessage.getJsonStr()));

                    ret = mDatabase.update(FMessage.FMESSAGE_TABLE, values, sql, str);

                } catch (Exception e) {
                    e.printStackTrace();
                    ret = -1;
                }

                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }

    public void updateMsgIDMsg(final BaseMessage baseMessage, final DBCallback callback) {
        if (baseMessage == null) {
            DBCenter.makeDBCallback(callback, MessageConstant.ERROR);
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                long ret = -1;
                try {
                    String whisperID = baseMessage.getWhisperID();
                    long msgID = baseMessage.getMsgID();

                    ContentValues values = new ContentValues();
                    if (baseMessage.getTime() != -1)
                        values.put(FMessage.COLUMN_TIME, baseMessage.getTime());

                    if (baseMessage.getSpecialMsgID() != -1)
                        values.put(FMessage.COLUMN_SPECIALMSGID, baseMessage.getSpecialMsgID());

                    if (baseMessage.getJsonStr() != null)
                        values.put(FMessage.COLUMN_CONTENT, ByteUtil.toBytesUTF(baseMessage.getJsonStr()));

                    ret = mDatabase.update(FMessage.FMESSAGE_TABLE, values,
                            FMessage.COLUMN_WHISPERID + " = ? AND " + FMessage.COLUMN_MSGID + " = ?", whisperID, String.valueOf(msgID));

                } catch (Exception e) {
                    e.printStackTrace();
                    ret = -1;
                }

                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }

    public void updateMsgFStatus(final long msgID, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(FMessage.COLUMN_FSTATUS, String.valueOf(MessageConstant.NumDefault));
                long ret = mDatabase.update(FMessage.FMESSAGE_TABLE, values, FMessage.COLUMN_MSGID + " = ?", String.valueOf(msgID));
                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }

    /**
     * 修改特殊消息ID
     * @param specialMsgID
     * @param callback
     */
    public void updateMsgSpecialMsgID(final int type, final long specialMsgID, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(FMessage.COLUMN_FSTATUS, String.valueOf(MessageConstant.NumDefault));

                String sql = FMessage.COLUMN_TYPE + " = ? AND " +  FMessage.COLUMN_SPECIALMSGID + " = ? AND " + FMessage.COLUMN_FSTATUS + " = ?";
                long ret = mDatabase.update(FMessage.FMESSAGE_TABLE, values, sql, String.valueOf(type), String.valueOf(specialMsgID) , "1");
                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }

    /**
     * 更新某个类型所的有的消息FStatus=1
     * @param uid
     * @param type
     * @param callback
     */
    public void updateTypeFStatus(final long uid, final int type, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(FMessage.COLUMN_FSTATUS, String.valueOf(MessageConstant.NumDefault));

                String sql = FMessage.COLUMN_WHISPERID + " = ? AND " + FMessage.COLUMN_TYPE + " = ? AND " + FMessage.COLUMN_FSTATUS + " = ?";
                long ret = mDatabase.update(FMessage.FMESSAGE_TABLE, values, sql,  String.valueOf(uid), String.valueOf(type), "1");
                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }

    /**
     * 更新某个类型所的有的消息FStatus=1
     * @param uid
     * @param type
     * @param callback
     */
    public void update(final long uid, final int type, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(FMessage.COLUMN_FSTATUS, String.valueOf(MessageConstant.NumDefault));

                String sql = FMessage.COLUMN_WHISPERID + " = ? AND " + FMessage.COLUMN_TYPE + " = ? AND " + FMessage.COLUMN_FSTATUS + " = ?";
                long ret = mDatabase.update(FMessage.FMESSAGE_TABLE, values, sql,  String.valueOf(uid), String.valueOf(type), "1");
                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }

    public void updateToReadAll(final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(FMessage.COLUMN_STATUS, String.valueOf(MessageConstant.READ_STATUS));
                long ret = mDatabase.update(FMessage.FMESSAGE_TABLE, values, FMessage.COLUMN_STATUS + " = ?", String.valueOf(MessageConstant.UNREAD_STATUS));
                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }

    public void updateDeliveryStatus(final long msgID, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(FMessage.COLUMN_STATUS, MessageConstant.DELIVERY_STATUS);

                String sql = FMessage.COLUMN_MSGID + " = ? AND (" + FMessage.COLUMN_STATUS + " = ? OR " + FMessage.COLUMN_STATUS + " = ?)";

                long ret = mDatabase.update(FMessage.FMESSAGE_TABLE, values, sql,
                        String.valueOf(msgID), String.valueOf(MessageConstant.OK_STATUS), String.valueOf(MessageConstant.SENDING_STATUS));
                DBCenter.makeDBCallback(callback, (ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR));
            }
        });
    }

    public void updateToRead(final List<BaseMessage> list, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                BriteDatabase.Transaction transaction = mDatabase.newTransaction();
                try {
                    for (BaseMessage temp : list) {
                        updateOneToRead(temp.getLWhisperID());
                    }
                    transaction.markSuccessful();
                } finally {
                    transaction.end();
                }

                DBCenter.makeDBCallback(callback, MessageConstant.OK);
            }
        });
    }

    public void updateStatusFail(final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(FMessage.COLUMN_STATUS, String.valueOf(MessageConstant.FAIL_STATUS));
                long ret = mDatabase.update(FMessage.FMESSAGE_TABLE, values, FMessage.COLUMN_STATUS + " = ?", String.valueOf(MessageConstant.SENDING_STATUS));
                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }

    private long updateOneToRead(long userID) {
        ContentValues values = new ContentValues();
        values.put(FMessage.COLUMN_STATUS, String.valueOf(MessageConstant.READ_STATUS));
        return mDatabase.update(FMessage.FMESSAGE_TABLE, values, FMessage.COLUMN_WHISPERID + " = ?", String.valueOf(userID));
    }

    /**
     * 更新已读消息
     *
     * @param channelID
     * @param userID
     * @return
     */
    public void updateToRead(final String channelID, final String userID, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                long ret = -1;
                try {
                    String sql;
                    String[] str;
                    if (!TextUtils.isEmpty(channelID) && !TextUtils.isEmpty(userID)) {
                        sql = FMessage.COLUMN_CHANNELID + " = ? AND " + FMessage.COLUMN_WHISPERID + " = ? AND " + FMessage.COLUMN_STATUS + " = ?";
                        str = new String[]{channelID, userID, String.valueOf(MessageConstant.UNREAD_STATUS)};
                    } else if (!TextUtils.isEmpty(channelID)) {
                        sql = FMessage.COLUMN_CHANNELID + " = ? AND " + FMessage.COLUMN_STATUS + " = ?";
                        str = new String[]{channelID, String.valueOf(MessageConstant.UNREAD_STATUS)};
                    } else {
                        sql = FMessage.COLUMN_WHISPERID + " = ? AND " + FMessage.COLUMN_STATUS + " = ?";
                        str = new String[]{userID, String.valueOf(MessageConstant.UNREAD_STATUS)};
                    }

                    ContentValues values = new ContentValues();
                    values.put(FMessage.COLUMN_STATUS, String.valueOf(MessageConstant.READ_STATUS));
                    ret = mDatabase.update(FMessage.FMESSAGE_TABLE, values, sql, str);
                } catch (Exception e) {
                    e.printStackTrace();
                    ret = -1;
                }
                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;

                DBCenter.makeDBCallback(callback, result);
            }
        });
    }

    /**
     * 对方已读
     *
     * @param channelID
     * @param userID
     * @param sendID
     * @return
     */
    public void updateOtherSideRead(final String channelID, final String userID,
                                    @NonNull final String sendID, final long msgID, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {

                StringBuilder sql = new StringBuilder();
                List<String> stringList = new ArrayList<>();

                if (!TextUtils.isEmpty(channelID) && !TextUtils.isEmpty(userID)) {
                    sql.append(FMessage.COLUMN_CHANNELID + " = ? AND ")
                            .append(FMessage.COLUMN_WHISPERID + " = ? AND ")
                            .append(FMessage.COLUMN_SENDID + " = ? AND ")
                            .append(FMessage.COLUMN_STATUS + " = ?");
                    stringList.add(channelID);
                    stringList.add(userID);
                    stringList.add(sendID);
                    stringList.add(String.valueOf(MessageConstant.DELIVERY_STATUS));
                } else if (!TextUtils.isEmpty(channelID)) {
                    sql.append(FMessage.COLUMN_CHANNELID + " = ? AND ")
                            .append(FMessage.COLUMN_SENDID + " = ? AND ")
                            .append(FMessage.COLUMN_STATUS + " = ?");
                    stringList.add(channelID);
                    stringList.add(sendID);
                    stringList.add(String.valueOf(MessageConstant.DELIVERY_STATUS));
                } else {
                    sql.append(FMessage.COLUMN_WHISPERID + " = ? AND ")
                            .append(FMessage.COLUMN_SENDID + " = ? AND ")
                            .append(FMessage.COLUMN_STATUS + " = ?");
                    stringList.add(userID);
                    stringList.add(sendID);
                    stringList.add(String.valueOf(MessageConstant.DELIVERY_STATUS));
                }

                if (msgID != MessageConstant.NumNo) {
                    sql.append(" AND " + FMessage.COLUMN_MSGID + " < ?");
                    stringList.add(String.valueOf(msgID));
                }

                String[] strs = new String[stringList.size()];
                stringList.toArray(strs);

                ContentValues values = new ContentValues();
                values.put(FMessage.COLUMN_STATUS, String.valueOf(MessageConstant.READ_STATUS));
                long ret = mDatabase.update(FMessage.FMESSAGE_TABLE, values, sql.toString(), strs);
                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }


    private BaseMessage isExist(long specialMsgID) {
        Cursor cursor = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM ").append(FMessage.FMESSAGE_TABLE)
                    .append(" WHERE ")
                    .append(FMessage.COLUMN_SPECIALMSGID + " = ?");
            cursor = mDatabase.query(sql.toString(), String.valueOf(specialMsgID));
            if (cursor != null && cursor.moveToFirst()) {
                Bundle bundle = new Bundle();
                bundle.putLong(FMessage._ID, CursorUtil.getLong(cursor, FMessage._ID));
                bundle.putString(FMessage.COLUMN_CHANNELID, CursorUtil.getString(cursor, FMessage.COLUMN_CHANNELID));
                bundle.putString(FMessage.COLUMN_WHISPERID, CursorUtil.getString(cursor, FMessage.COLUMN_WHISPERID));
                bundle.putLong(FMessage.COLUMN_SENDID, CursorUtil.getLong(cursor, FMessage.COLUMN_SENDID));
                bundle.putLong(FMessage.COLUMN_MSGID, CursorUtil.getLong(cursor, FMessage.COLUMN_MSGID));
                bundle.putLong(FMessage.COLUMN_CMSGID, CursorUtil.getLong(cursor, FMessage.COLUMN_CMSGID));
                bundle.putLong(FMessage.COLUMN_SPECIALMSGID, CursorUtil.getLong(cursor, FMessage.COLUMN_SPECIALMSGID));
                bundle.putInt(FMessage.COLUMN_TYPE, CursorUtil.getInt(cursor, FMessage.COLUMN_TYPE));
                bundle.putInt(FMessage.COLUMN_STATUS, CursorUtil.getInt(cursor, FMessage.COLUMN_STATUS));
                bundle.putInt(FMessage.COLUMN_FSTATUS, CursorUtil.getInt(cursor, FMessage.COLUMN_FSTATUS));
                bundle.putLong(FMessage.COLUMN_TIME, CursorUtil.getLong(cursor, FMessage.COLUMN_TIME));
                bundle.putString(FMessage.COLUMN_CONTENT, CursorUtil.getBlobToString(cursor, FMessage.COLUMN_CONTENT));
                return BaseMessage.parseToBaseMessage(bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(cursor);
        }
        return null;
    }

    public Observable<List<BaseMessage>> queryMsgList(String channelID, String whisperID, int type, int start, int offset) {
        if (TextUtils.isEmpty(channelID) && TextUtils.isEmpty(whisperID)) {
            return null;
        }
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(FMessage.FMESSAGE_TABLE)
                .append(" WHERE ");
        if (!TextUtils.isEmpty(channelID)) {
            sql.append(FMessage.COLUMN_CHANNELID + " = ")
                    .append(channelID)
                    .append(" AND ");
        }

        sql.append(FMessage.COLUMN_WHISPERID + " = ")
                .append(whisperID)
                .append(" AND ")
                .append(FMessage.COLUMN_TYPE + " = ")
                .append(type)
                .append(" ORDER BY ")
                .append(FMessage._ID)
                .append(" DESC")
                .append(" LIMIT ").append((start * offset)).append(",").append(offset);
        return queryBySqlFmessage(sql.toString());
    }


    /**
     * 聊天记录
     *
     * @param channelID
     * @param whisperID
     * @param start
     * @param offset
     * @return
     */
    public Observable<List<BaseMessage>> queryMsgList(String channelID, String whisperID, int start, int offset) {
        if (TextUtils.isEmpty(channelID) && TextUtils.isEmpty(whisperID)) {
            return null;
        }
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(FMessage.FMESSAGE_TABLE)
                .append(" WHERE ");
        if (!TextUtils.isEmpty(channelID)) {
            sql.append(FMessage.COLUMN_CHANNELID + " = ")
                    .append(channelID)
                    .append(" AND ");
        }

        sql.append(FMessage.COLUMN_WHISPERID + " = ")
                .append(whisperID)
                .append(" ORDER BY ")
                .append(FMessage._ID)
                .append(" DESC")
                .append(" LIMIT ").append((start * offset)).append(",").append(offset);
        return queryBySqlFmessage(sql.toString());
    }

    /**
     * 查询
     *
     * @param sql
     * @return
     */
    public Observable<List<BaseMessage>> queryBySqlFmessage(String sql) {
        return mDatabase.createQuery(FMessage.FMESSAGE_TABLE, sql)
                .map(new Function<SqlBrite.Query, List<BaseMessage>>() {
                    @Override
                    public List<BaseMessage> apply(@NonNull SqlBrite.Query query) throws Exception {
                        return convert(query);
                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private List<BaseMessage> convert(SqlBrite.Query query) {
        Cursor cursor = null;
        ArrayList<BaseMessage> result = new ArrayList<>();
        try {
            cursor = query.run();
            while (cursor != null && cursor.moveToNext()) {
                Bundle bundle = new Bundle();
                bundle.putLong(FMessage._ID, CursorUtil.getLong(cursor, FMessage._ID));
                bundle.putString(FMessage.COLUMN_CHANNELID, CursorUtil.getString(cursor, FMessage.COLUMN_CHANNELID));
                bundle.putString(FMessage.COLUMN_WHISPERID, CursorUtil.getString(cursor, FMessage.COLUMN_WHISPERID));
                bundle.putLong(FMessage.COLUMN_SENDID, CursorUtil.getLong(cursor, FMessage.COLUMN_SENDID));
                bundle.putLong(FMessage.COLUMN_MSGID, CursorUtil.getLong(cursor, FMessage.COLUMN_MSGID));
                bundle.putLong(FMessage.COLUMN_CMSGID, CursorUtil.getLong(cursor, FMessage.COLUMN_CMSGID));
                bundle.putLong(FMessage.COLUMN_SPECIALMSGID, CursorUtil.getLong(cursor, FMessage.COLUMN_SPECIALMSGID));
                bundle.putInt(FMessage.COLUMN_TYPE, CursorUtil.getInt(cursor, FMessage.COLUMN_TYPE));
                bundle.putInt(FMessage.COLUMN_STATUS, CursorUtil.getInt(cursor, FMessage.COLUMN_STATUS));
                bundle.putInt(FMessage.COLUMN_FSTATUS, CursorUtil.getInt(cursor, FMessage.COLUMN_FSTATUS));
                bundle.putLong(FMessage.COLUMN_TIME, CursorUtil.getLong(cursor, FMessage.COLUMN_TIME));
                bundle.putString(FMessage.COLUMN_CONTENT, CursorUtil.getBlobToString(cursor, FMessage.COLUMN_CONTENT));

                result.add(BaseMessage.parseToBaseMessage(bundle));
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
                DBCenter.makeDBCallback(callback, delete(whisperID));
            }
        });
    }

    private int delete(long whisperID) {
        long ret = mDatabase.delete(FMessage.FMESSAGE_TABLE, FMessage.COLUMN_WHISPERID + " = ? ", String.valueOf(whisperID));
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

    private long deleteMsgID(long vcID) {
        long ret = mDatabase.delete(FMessage.FMESSAGE_TABLE, FMessage.COLUMN_SPECIALMSGID + " = ? ", String.valueOf(vcID));
        return ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
    }

    public void delete(final long whisperID, final long time, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                long ret = mDatabase.delete(FMessage.FMESSAGE_TABLE, FMessage.COLUMN_WHISPERID + " = ? AND " +
                        FMessage.COLUMN_TIME + " = ? ", String.valueOf(whisperID), String.valueOf(time));
                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }
}
