package com.juxin.predestinate.bean.db.cache;

/**
 * http缓存
 * Created by Kind on 2017/3/28.
 */

public class FHttpCache {

    public static final String FHTTPCACHE_TABLE = "fhttpcache";

    public static final String _ID = "_id";
    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_CONTENT = "content";//json

    public static String getCreateTable() {
        return "CREATE TABLE IF NOT EXISTS " + FHTTPCACHE_TABLE + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_KEY + " TEXT NOT NULL UNIQUE," +
                COLUMN_CONTENT + " BLOB, folder TEXT, field1 TEXT, field2 TEXT, field3 TEXT, field4 TEXT, field5 TEXT)";
    }
}
