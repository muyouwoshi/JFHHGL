package com.juxin.predestinate.bean.db.cache;

/**
 * 个人资料
 * Created by Kind on 2017/3/23.
 */
public class FProfileCache {
    public static final String FPROFILE_TABLE = "fprofile";

    public static final String _ID = "_id";
    public static final String COLUMN_USERID = "userID";// 用户ID
    public static final String COLUMN_INFOJSON = "infoJSON";//好友信息json
    public static final String COLUMN_TIME = "time";

    public static final String FPROFILE_TABLE_INDEX = "FPROFILE_TABLE_INDEX";

    public static String getCreateTable() {
        return new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(FPROFILE_TABLE).append("(")
                .append(_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
                .append(COLUMN_USERID).append(" TEXT NOT NULL UNIQUE,")
                .append(COLUMN_INFOJSON).append(" BLOB,")
                .append(COLUMN_TIME).append(" TEXT, field1 TEXT, field2 TEXT, field3 TEXT, field4 TEXT, field5 TEXT)").toString();
    }

    public static String getIndex() {
        return "CREATE UNIQUE INDEX '" + FPROFILE_TABLE_INDEX + "' ON '" + FPROFILE_TABLE + "'('" + COLUMN_USERID + "' ASC, '" + COLUMN_TIME + "' ASC)";
    }

}
