package com.juxin.predestinate.bean.db;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.BuildConfig;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;

/**
 * 基于Dagger2的数据库全局管理类
 * Created by Kind on 2017/3/27.
 */
@Module
public class DBModule {

    private long uid = 0;

    public DBModule(long uid) {
        this.uid = uid;
    }

    @Provides
    @Singleton
    SQLiteOpenHelper provideOpenHelper(Application application) {
        return new DBHelper(application, String.valueOf(uid));
    }

    @SuppressLint("CheckResult")
    @Provides
    @Singleton
    SqlBrite provideSqlBrite() {
        final SqlBrite.Builder builder = new SqlBrite.Builder();
        if (BuildConfig.DEBUG) {
            //noinspection CheckResult
            builder.logger(new SqlBrite.Logger() {
                @Override
                public void log(String message) {
                    if (!TextUtils.isEmpty(message)) {
                        PLogger.d("DBModule=" + message);
                    }
                }
            });
        }
        return builder.build();
    }

    @Provides
    @Singleton
    BriteDatabase provideDatabase(SqlBrite sqlBrite, SQLiteOpenHelper helper) {
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
        db.setLoggingEnabled(BuildConfig.DEBUG);
        return db;
    }

    @Provides
    @Singleton
    DBCenter provideConsumerDao(BriteDatabase briteDatabase) {
        return new DBCenter(briteDatabase);
    }
}
