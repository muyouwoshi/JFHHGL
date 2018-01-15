package com.juxin.predestinate.bean.db.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Handler;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.db.DBCallback;
import com.juxin.predestinate.bean.db.DBCenter;
import com.juxin.predestinate.bean.db.utils.CloseUtil;
import com.juxin.predestinate.bean.db.utils.CursorUtil;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.util.ByteUtil;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 数据库缓存处理类
 * Created by Kind on 2017/4/7.
 */
public class DBCacheCenter {

    private BriteDatabase mDatabase;
    private Handler handler;

    public DBCacheCenter(BriteDatabase database, Handler handler) {
        this.mDatabase = database;
        this.handler = handler;
    }

    /******************** FProfileCache **************************/

    /**
     * 批量更新个人资料
     *
     * @param lightweights
     * @return
     */
    public void storageProfileData(final List<UserInfoLightweight> lightweights, final DBCallback callback) {
        if (lightweights == null || lightweights.size() <= 0) {
            DBCenter.makeDBCallback(callback, MessageConstant.ERROR);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                BriteDatabase.Transaction transaction = mDatabase.newTransaction();
                try {
                    for (UserInfoLightweight temp : lightweights) {
                        storageOneData(temp, isInfoExist(temp.getUid()));
                    }
                    transaction.markSuccessful();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    transaction.end();
                }

                DBCenter.makeDBCallback(callback, MessageConstant.OK);
            }
        });
    }


    /**
     * 是否存在
     *
     * @param userID
     * @return
     */
    private boolean isInfoExist(long userID) {
        Cursor cursor = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM ").append(FProfileCache.FPROFILE_TABLE)
                    .append(" WHERE ")
                    .append(FProfileCache.COLUMN_USERID + " = ?");
            cursor = mDatabase.query(sql.toString(), String.valueOf(userID));
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

    private long storageOneData(UserInfoLightweight lightweight, boolean aBoolean) {
        long ret = -1;
        try {
            long uid = lightweight.getUid();
            PLogger.printObject("storageProfileData==" + aBoolean);
            final ContentValues values = new ContentValues();
            values.put(FProfileCache.COLUMN_USERID, uid);
            values.put(FProfileCache.COLUMN_TIME, lightweight.getTime());

            if (lightweight.getInfoJson() != null)
                values.put(FProfileCache.COLUMN_INFOJSON, ByteUtil.toBytesUTF(lightweight.getInfoJson()));

            if (aBoolean) {
                ret = mDatabase.update(FProfileCache.FPROFILE_TABLE, values, FProfileCache.COLUMN_USERID + " = ? ", String.valueOf(uid));
            } else {
                ret = mDatabase.insert(FProfileCache.FPROFILE_TABLE, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret = -1;
        }

        long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;

        return result;
    }

    /**
     * 插入或更新内容
     *
     * @param lightweight
     * @return
     */
    public void storageProfileData(final UserInfoLightweight lightweight, final DBCallback callback) {
        PLogger.d("storageProfileData==" + "111111");

        handler.post(new Runnable() {
            @Override
            public void run() {
                final long uid = lightweight.getUid();
                Observable<Boolean> observable = isProfileExist(uid);
                observable.subscribe(new Observer<Boolean>() {
                    private Disposable queryDisposable;

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        queryDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        queryDisposable.dispose();
                        PLogger.d("storageProfileData==" + aBoolean);
                        long ret = storageOneData(lightweight, aBoolean);
                        DBCenter.makeDBCallback(callback, ret);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
            }
        });
    }

    /**
     * 是否存在
     *
     * @param userID
     * @return
     */
    public Observable<Boolean> isProfileExist(long userID) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(FProfileCache.FPROFILE_TABLE)
                .append(" WHERE ")
                .append(FProfileCache.COLUMN_USERID + " = ")
                .append(userID);

        return mDatabase.createQuery(FProfileCache.FPROFILE_TABLE, sql.toString())
                .map(new Function<SqlBrite.Query, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull SqlBrite.Query query) throws Exception {
                        Cursor cursor = null;
                        try {
                            cursor = query.run();
                            if (cursor != null && cursor.moveToFirst()) {
                                cursor.close();
                                return true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            CloseUtil.close(cursor);
                        }
                        return false;
                    }
                });
    }


    public Observable<UserInfoLightweight> queryProfile(long userID) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(FProfileCache.FPROFILE_TABLE)
                .append(" WHERE ").append(FProfileCache.COLUMN_USERID + " = ").append(userID);

        return mDatabase.createQuery(FProfileCache.FPROFILE_TABLE, sql.toString())
                .map(new Function<SqlBrite.Query, UserInfoLightweight>() {
                    @Override
                    public UserInfoLightweight apply(@NonNull SqlBrite.Query query) throws Exception {
                        UserInfoLightweight lightweight = new UserInfoLightweight();
                        Cursor cursor = query.run();
                        try {
                            if (cursor == null || !cursor.moveToNext()) {
                                return lightweight;
                            }

                            lightweight.parseUserInfoLightweight(
                                    CursorUtil.getLong(cursor, FProfileCache.COLUMN_USERID),
                                    CursorUtil.getBlobToString(cursor, FProfileCache.COLUMN_INFOJSON),
                                    CursorUtil.getLong(cursor, FProfileCache.COLUMN_TIME)
                            );
                            return lightweight;
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            CloseUtil.close(cursor);
                        }
                        return lightweight;
                    }
                });
    }

    public Observable<List<UserInfoLightweight>> queryProfile(List<Long> userIDs) {
        if (userIDs.size() < 1) return null;

        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(FProfileCache.FPROFILE_TABLE)
                .append(" WHERE ");
        if (userIDs.size() == 1) {
            sql.append(FProfileCache.COLUMN_USERID + " = ").append(userIDs.get(0));
        } else {
            sql.append(FProfileCache.COLUMN_USERID + " in ").append("( ");
            for (int i = 0; i < userIDs.size(); i++) {
                sql.append(userIDs.get(i));
                if ((i + 1) < userIDs.size()) {
                    sql.append(",");
                }
            }
            sql.append(" )");
        }

        PLogger.printObject("sql.to=" + sql.toString());
        return mDatabase.createQuery(FProfileCache.FPROFILE_TABLE, sql.toString())
                .map(new Function<SqlBrite.Query, List<UserInfoLightweight>>() {
                    @Override
                    public List<UserInfoLightweight> apply(@NonNull SqlBrite.Query query) throws Exception {
                        Cursor cursor = query.run();
                        if (null == cursor) return null;

                        List<UserInfoLightweight> infoLightweights = new ArrayList<UserInfoLightweight>();
                        try {
                            while (cursor.moveToNext()) {
                                UserInfoLightweight temp = new UserInfoLightweight();
                                temp.parseUserInfoLightweight(
                                        CursorUtil.getLong(cursor, FProfileCache.COLUMN_USERID),
                                        CursorUtil.getBlobToString(cursor, FProfileCache.COLUMN_INFOJSON),
                                        CursorUtil.getLong(cursor, FProfileCache.COLUMN_TIME)
                                );
                                infoLightweights.add(temp);
                            }
                            return infoLightweights;
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            CloseUtil.close(cursor);
                        }
                        return infoLightweights;
                    }
                });
    }

    /******************** FHttpCache **************************/

    /**
     * 插入或更新内容
     *
     * @param key
     * @param content
     * @return
     */
//    public long storageDataCache(final String key, final String content, DBCallback callback) {
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                boolean ret = isExist(key);
//                final ContentValues values = new ContentValues();
//                if (!ret) {
//                    values.put(FHttpCache.COLUMN_KEY, key);
//
//                    if (TextUtils.isEmpty(content)) {
//                        values.put(FHttpCache.COLUMN_KEY, ByteUtil.toBytesUTF(content));
//                    }
//
//
//                    mDatabase.insert(FHttpCache.FHTTPCACHE_TABLE, values);
//                } else {
//
//                    values.put(FHttpCache.COLUMN_KEY, ByteUtil.toBytesUTF(content));
//                    mDatabase.update(FHttpCache.FHTTPCACHE_TABLE, values, FHttpCache.COLUMN_KEY + " = ? ", key);
//                }
//                try {
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }

    /**
     * 是否存在
     *
     * @param key
     * @return
     */
    private boolean isExist(String key) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(FHttpCache.FHTTPCACHE_TABLE)
                .append(" WHERE ")
                .append(FHttpCache.COLUMN_KEY + " = ?");
        Cursor cursor = mDatabase.query(sql.toString(), key);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            if (cursor != null) cursor.close();
            return false;
        }
    }

    public Observable<String> queryHttpCache(String key) {
        String sql = "SELECT * FROM " + FHttpCache.FHTTPCACHE_TABLE +
                " WHERE " +
                FHttpCache.COLUMN_KEY + " = " +
                key;

        return mDatabase.createQuery(FHttpCache.FHTTPCACHE_TABLE, sql)
                .map(new Function<SqlBrite.Query, String>() {
                    @Override
                    public String apply(@NonNull SqlBrite.Query query) throws Exception {
                        String str = "";
                        Cursor cursor = query.run();
                        if (null == cursor) {
                            return str;
                        }
                        try {
                            while (cursor.moveToNext()) {
                                str = CursorUtil.getBlobToString(cursor, FHttpCache.COLUMN_CONTENT);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            CloseUtil.close(cursor);
                        }
                        return str;
                    }
                }).unsubscribeOn(Schedulers.io());
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
                long ret = mDatabase.delete(FHttpCache.FHTTPCACHE_TABLE, FHttpCache.COLUMN_KEY + " = ? ", key);
                long result = ret >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, result);
            }
        });
    }

    /**
     * 删除所有
     *
     * @return
     */
    public void deleteAll(final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                long ret = mDatabase.delete(FHttpCache.FHTTPCACHE_TABLE, "", "");
                DBCenter.makeDBCallback(callback, ret);
            }
        });
    }
}
