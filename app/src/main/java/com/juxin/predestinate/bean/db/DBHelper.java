package com.juxin.predestinate.bean.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.juxin.predestinate.bean.db.cache.FHttpCache;
import com.juxin.predestinate.bean.db.cache.FProfileCache;

/**
 * Created by Kind on 2017/3/21.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    public DBHelper(Context context, String DBNAME) {
        super(context, DBNAME +  ".db", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FLetter.getCreateTable());
        db.execSQL(FMessage.getCreateTable());
        db.execSQL(FMark.getCreateTable());
        db.execSQL(FUnRead.getCreateTable());

        db.execSQL(FProfileCache.getCreateTable());
        db.execSQL(FHttpCache.getCreateTable());

      //  db.execSQL(FProfileCache.getIndex());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
