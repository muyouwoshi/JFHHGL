package com.juxin.predestinate.bean.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.TextUtils;

import com.juxin.predestinate.module.logic.application.App;

import java.io.File;

/**
 * Created by siow on 2017/5/19.
 */

public class OldDBHelper extends SQLiteOpenHelper {
    private static final String UPDATE_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "xiaou" + File.separator;
    private static final String DBNAME = "weshot.db";
    private static final int VERSION = 9;

    public static String getDBPath(){
        String path = App.context.getDatabasePath(DBNAME).getPath();
        if (new File(path).exists())
            return path;
        path = UPDATE_CACHE_PATH + DBNAME + ".jpg";
        if (new File(path).exists())
            return path;
        return null;
    }

    public static boolean isExitsDB(){
        return !TextUtils.isEmpty(getDBPath());
    }

    public OldDBHelper(Context context) {
        super(context, getDBPath(), null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
