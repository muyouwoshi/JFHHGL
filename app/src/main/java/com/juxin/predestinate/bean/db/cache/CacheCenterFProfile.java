package com.juxin.predestinate.bean.db.cache;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;

import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweightList;
import com.juxin.predestinate.bean.db.FMessage;
import com.juxin.predestinate.bean.db.utils.CloseUtil;
import com.juxin.predestinate.bean.db.utils.CursorUtil;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * FProfileCache 处理
 * Created by Kind on 2017/3/28.
 */
public class CacheCenterFProfile {

    private BriteDatabase mDatabase;

    public CacheCenterFProfile(BriteDatabase database) {
        this.mDatabase = database;
    }

    public Observable<UserInfoLightweightList> queryBySqlFProfile(String sql) {
        return mDatabase.createQuery(FProfileCache.FPROFILE_TABLE, sql)
                .map(new Function<SqlBrite.Query, UserInfoLightweightList>() {
                    @Override
                    public UserInfoLightweightList apply(@NonNull SqlBrite.Query query) throws Exception {
                        return convert(query.run());
                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private UserInfoLightweightList convert(Cursor cursor) {
        UserInfoLightweightList lightweightList = new UserInfoLightweightList();
        if (null == cursor) {
            return lightweightList;
        }
        try {
            while (cursor.moveToNext()) {
                lightweightList.getUserInfos().add(new UserInfoLightweight(
                        CursorUtil.getLong(cursor, FProfileCache.COLUMN_USERID),
                        CursorUtil.getString(cursor, FProfileCache.COLUMN_INFOJSON),
                        CursorUtil.getLong(cursor, FProfileCache.COLUMN_TIME)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(cursor);
        }
        return lightweightList;
    }

    /**
     * 多条数据更新或插入
     *
     * @param list
     */
    public void updateMsg(List<UserInfoLightweight> list) {
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            for (final UserInfoLightweight item : list) {
                Observable<Boolean> observable = isExistProfile(item.getUid());
                observable.subscribe(new Observer<Boolean>() {
                    private Disposable queryDisposable;

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        queryDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        queryDisposable.dispose();
                        updateMsg(aBoolean, item);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    private Observable<Boolean> queryBySqlFmessage(String sql) {
        return mDatabase.createQuery(FMessage.FMESSAGE_TABLE, sql)
                .map(new Function<SqlBrite.Query, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull SqlBrite.Query query) throws Exception {
                        Cursor cursor = query.run();
                        if (cursor != null && cursor.moveToFirst()) {
                            CloseUtil.close(cursor);
                            return true;
                        } else {
                            CloseUtil.close(cursor);
                            return false;
                        }
                    }
                });
    }


    private Observable<Boolean> isExistProfile(long userid) {
        String sql = "SELECT * FROM " +
                FProfileCache.FPROFILE_TABLE +
                " WHERE " +
                FProfileCache.COLUMN_USERID + " = " +
                userid;

        return mDatabase.createQuery(FProfileCache.FPROFILE_TABLE, sql)
                .map(new Function<SqlBrite.Query, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull SqlBrite.Query query) throws Exception {
                        Cursor cursor = query.run();
                        if (cursor != null && cursor.moveToFirst()) {
                            CloseUtil.close(cursor);
                            return true;
                        } else {
                            CloseUtil.close(cursor);
                            return false;
                        }
                    }
                });
    }

    private long updateMsg(Boolean b, UserInfoLightweight lightweight) {
        try {
            final ContentValues values = new ContentValues();
            values.put(FProfileCache.COLUMN_USERID, lightweight.getUid());

            if (lightweight.getInfoJson() != null)
                values.put(FProfileCache.COLUMN_INFOJSON, lightweight.getInfoJson());
            if (lightweight.getTime() != -1)
                values.put(FProfileCache.COLUMN_TIME, lightweight.getTime());

            if (!b) {
                return mDatabase.insert(FProfileCache.FPROFILE_TABLE, values);
            } else {
//                return mDatabase.update(FProfileCache.FPROFILE_TABLE, values, FProfileCache.COLUMN_USERID + " = ? ", lightweight.getSUid());
                return mDatabase.update(FProfileCache.FPROFILE_TABLE, values, FProfileCache.COLUMN_USERID + " = ? ", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MessageConstant.ERROR;
    }

    /**
     * 删除
     *
     * @param userid 用户ID
     * @return
     */
    public int delete(long userid) {
        return mDatabase.delete(FProfileCache.FPROFILE_TABLE, FProfileCache.COLUMN_USERID + " = ? ", String.valueOf(userid));
    }
}
