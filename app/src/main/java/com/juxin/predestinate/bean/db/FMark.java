package com.juxin.predestinate.bean.db;

/**
 * 标记专用
 * Created by Kind on 2017/3/28.
 */

public class FMark {
    public static final String FMARK_TABLE = "fmark";

    public static final String _ID = "_id";
    public static final String COLUMN_USERID = "userID";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_NUM = "num"; //数量，目前是1
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_TIME = "time";

    public static String getCreateTable() {
        return new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(FMARK_TABLE).append("(")
                .append(_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
                .append(COLUMN_USERID).append(" TEXT NOT NULL UNIQUE,")
                .append(COLUMN_TYPE).append(" INTEGER NOT NULL,")
                .append(COLUMN_NUM).append(" INTEGER,")
                .append(COLUMN_CONTENT).append(" TEXT,")
                .append(COLUMN_TIME).append(" INTEGER, field1 TEXT, field2 TEXT, field3 TEXT, field4 TEXT, field5 TEXT)").toString();



    }

}
