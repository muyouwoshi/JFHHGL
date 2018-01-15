package com.juxin.predestinate.bean.db;

/**
 * 未读消息
 */
public class FUnRead {

    public static final String FUNREAD_TABLE = "funread";

    public static final String _ID = "_id";
    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_CONTENT = "content";//json

    public static String getCreateTable() {
        return "CREATE TABLE IF NOT EXISTS " + FUNREAD_TABLE + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_KEY + " TEXT NOT NULL UNIQUE," +
                COLUMN_CONTENT + " BLOB, folder TEXT, field1 TEXT, field2 TEXT, field3 TEXT, field4 TEXT, field5 TEXT)";
    }
}
