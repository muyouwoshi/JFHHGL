package com.juxin.predestinate.bean.db;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.juxin.predestinate.bean.db.utils.CloseUtil;
import com.juxin.predestinate.bean.db.utils.CursorUtil;
import com.juxin.predestinate.module.local.chat.ChatMark;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.util.ByteUtil;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * FMark 处理
 * Created by Kind on 2017/3/28.
 */
public class DBCenterFMark {

    private BriteDatabase mDatabase;
    private Handler handler;

    public DBCenterFMark(BriteDatabase database, Handler handler) {
        this.mDatabase = database;
        this.handler = handler;
    }

    public void insertFMark(final ChatMark chatMark, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                DBCenter.makeDBCallback(callback, insertFMarkMsg(chatMark.getUserID(), chatMark.getType(),
                        chatMark.getNum(), chatMark.getContent(), chatMark.getTime()));
            }
        });
    }

    /**
     * 插入
     *
     * @param userid
     * @param type
     * @param num
     * @param content
     * @return
     */
    private long insertFMarkMsg(long userid, int type, int num, String content, long time) {
        try {
            ContentValues values = new ContentValues();
            values.put(FMark.COLUMN_USERID, userid);
            if (type != -1)
                values.put(FMark.COLUMN_TYPE, type);

            if (num != -1)
                values.put(FMark.COLUMN_NUM, num);

            if (content != null)
                values.put(FMark.COLUMN_CONTENT, ByteUtil.toBytesUTF(content));

            if (time != -1)
                values.put(FMark.COLUMN_TIME, time);

            long ret = mDatabase.insert(FMark.FMARK_TABLE, values);
            return ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageConstant.ERROR;
        }
    }

    /**
     * 删除
     *
     * @param userid
     * @param type
     */
    public void deleteFMark(final long userid, final int type, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                long ret = mDatabase.delete(FMark.FMARK_TABLE, FMark.COLUMN_USERID + " = ? AND " +
                        FMark.COLUMN_TYPE + " = ?", String.valueOf(userid), String.valueOf(type));
                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }


    /**
     * @param userID
     * @param type
     * @return
     */
    public Observable<ChatMark> queryFMark(long userID, int type) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(FMark.FMARK_TABLE)
                .append(" WHERE ")
                .append(FMark.COLUMN_USERID + " = ")
                .append(userID)
                .append(" AND ")
                .append(FMark.COLUMN_TYPE + " = ")
                .append(type);
        return mDatabase.createQuery(FMark.FMARK_TABLE, sql.toString())
                .map(new Function<SqlBrite.Query, ChatMark>() {
                    @Override
                    public ChatMark apply(@NonNull SqlBrite.Query query) throws Exception {
                        return convert(query);
                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private ChatMark convert(SqlBrite.Query query) {
        Cursor cursor = null;
        try {
            cursor = query.run();
            while (cursor != null && cursor.moveToNext()) {
                Bundle bundle = new Bundle();
                bundle.putLong(FMark._ID, CursorUtil.getLong(cursor, FMark._ID));
                bundle.putLong(FMark.COLUMN_USERID, CursorUtil.getLong(cursor, FMark.COLUMN_USERID));
                bundle.putInt(FMark.COLUMN_TYPE, CursorUtil.getInt(cursor, FMark.COLUMN_TYPE));
                bundle.putInt(FMark.COLUMN_NUM, CursorUtil.getInt(cursor, FMark.COLUMN_NUM));
                bundle.putString(FMark.COLUMN_CONTENT, CursorUtil.getBlobToString(cursor, FMark.COLUMN_CONTENT));
                bundle.putLong(FMark.COLUMN_TIME, CursorUtil.getLong(cursor, FMark.COLUMN_TIME));
                return new ChatMark(bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(cursor);
        }
        return null;
    }
}