package com.juxin.predestinate.bean.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Handler;
import android.text.TextUtils;

import com.juxin.predestinate.bean.db.utils.CloseUtil;
import com.juxin.predestinate.bean.db.utils.CursorUtil;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.util.ByteUtil;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * funread 处理
 * Created by Kind on 2017/3/28.
 */
public class DBCenterFUnRead {

    private BriteDatabase mDatabase;
    private Handler handler;

    public DBCenterFUnRead(BriteDatabase database, Handler handler) {
        this.mDatabase = database;
        this.handler = handler;
    }

    public long storageData(final String key, final String content, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                long ret;
                if (!isExist(key)) {//没有数据
                    ret = insertUnRead(key, content);
                } else {
                    ret = updateUnRead(key, content);
                }

                DBCenter.makeDBCallback(callback, ret);
            }
        });
        return MessageConstant.OK;
    }

    /**
     * 多条消息插入
     *
     * @param stringMap
     */
    public void insertLetter(final Map<String, String> stringMap) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                BriteDatabase.Transaction transaction = mDatabase.newTransaction();
                try {
                    for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                        insertUnRead(entry.getKey(), entry.getValue());
                    }

                    transaction.markSuccessful();
                } finally {
                    transaction.end();
                }
            }
        });
    }

    /**
     * 单条插入
     *
     * @param key
     * @param content
     * @return
     */
    private long insertUnRead(String key, String content) {
        try {
            final ContentValues values = new ContentValues();
            values.put(FUnRead.COLUMN_KEY, key);

            if (!TextUtils.isEmpty(content))
                values.put(FUnRead.COLUMN_CONTENT, ByteUtil.toBytesUTF(content));

            return mDatabase.insert(FUnRead.FUNREAD_TABLE, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MessageConstant.ERROR;
    }

    /**
     * 更新
     *
     * @param key
     * @param content
     * @return
     */
    private int updateUnRead(String key, String content) {
        try {
            final ContentValues values = new ContentValues();
            values.put(FUnRead.COLUMN_CONTENT, ByteUtil.toBytesUTF(content));

            mDatabase.update(FUnRead.FUNREAD_TABLE, values, FUnRead.COLUMN_KEY + " = ? ", key);
        } catch (Exception e) {
            e.printStackTrace();
            return MessageConstant.ERROR;
        }
        return MessageConstant.OK;
    }

    /**
     * 是否存在
     *
     * @param key
     * @return
     */
    private boolean isExist(String key) {
        Cursor cursor = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM ").append(FUnRead.FUNREAD_TABLE)
                    .append(" WHERE ")
                    .append(FUnRead.COLUMN_KEY + " = ?");
            cursor = mDatabase.query(sql.toString(), key);
            if (cursor != null && cursor.moveToFirst()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(cursor);
        }
        return false;
    }

    /**
     * 查询所有
     *
     * @return
     */
    public Observable<Map<String, String>> queryUnReadList() {
        String sqlStr = "SELECT * FROM " + FUnRead.FUNREAD_TABLE;
        return mDatabase.createQuery(FUnRead.FUNREAD_TABLE, sqlStr).map(new Function<SqlBrite.Query, Map<String, String>>() {
            @Override
            public Map<String, String> apply(@NonNull SqlBrite.Query query) throws Exception {
                Map<String, String> map = new HashMap<>();
                Cursor cursor = query.run();
                if (null == cursor) {
                    return map;
                }
                try {
                    while (cursor.moveToNext()) {
                        map.put(CursorUtil.getString(cursor, FUnRead.COLUMN_KEY), CursorUtil.getBlobToString(cursor, FLetter.COLUMN_CONTENT));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    CloseUtil.close(cursor);
                }
                return map;
            }
        });
    }

    public Observable<String> queryUnRead(String key) {
        String sql = "SELECT * FROM " + FUnRead.FUNREAD_TABLE +
                " WHERE " +
                FUnRead.COLUMN_KEY + " = " +
                key;

        return mDatabase.createQuery(FUnRead.FUNREAD_TABLE, sql).map(new Function<SqlBrite.Query, String>() {
            @Override
            public String apply(@NonNull SqlBrite.Query query) throws Exception {
                Cursor cursor = null;
                try {
                    cursor = query.run();
                    if (cursor != null && cursor.moveToFirst()) {
                        return CursorUtil.getBlobToString(cursor, FUnRead.COLUMN_CONTENT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    CloseUtil.close(cursor);
                }
                return null;
            }
        });
    }

    /**
     * 删除
     *
     * @param key
     * @return
     */
    public void delete(final String key, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                long ret = mDatabase.delete(FUnRead.FUNREAD_TABLE, FUnRead.COLUMN_KEY + " = ? ", key);
                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }
}