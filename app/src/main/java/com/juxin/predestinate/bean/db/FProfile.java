package com.juxin.predestinate.bean.db;

/**
 * Created by Kind on 2017/3/23.
 */
public class FProfile {
    public static final String FPROFILE_TABLE = "fprofile";

    private static final String _ID = "_id";
    private static final String COLUMN_USERID = "userID";// 用户ID
    private static final String COLUMN_INFOJSON = "infoJson";//好友信息json
    private static final String COLUMN_TIME = "time";

    public static String getCreateTable() {
        return new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(FPROFILE_TABLE).append("(")
                .append(_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
                .append(COLUMN_USERID).append(" TEXT NOT NULL UNIQUE,")
                .append(COLUMN_INFOJSON).append(" BLOB,")
                .append(COLUMN_TIME).append(" TEXT, field1 TEXT, field2 TEXT, field3 TEXT, field4 TEXT, field5 TEXT)").toString();
    }

}
