package com.juxin.predestinate.bean.db;

/**
 * 消息列表
 * Created by Kind on 2017/3/28.
 */

public class FLetter {

    public static final String FLETTER_TABLE = "fletter";

    public static final String _ID = "_id";
    public static final String COLUMN_USERID = "userID";
    public static final String COLUMN_INFOJSON = "infoJson";
    public static final String COLUMN_TYPE = "type";// 消息类型
    public static final String COLUMN_KFID = "kfID";
    public static final String COLUMN_STATUS = "status";//默认插入是1
    public static final String COLUMN_CMSGID = "cMsgID";// 客户端消息ID
    public static final String COLUMN_RU = "ru";//如果为1则为熟人消息，否则为0
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CONTENT = "content";//json
    public static final String COLUMN_MSGID = "folder";//msgid

    public static final String Num = "num";
    public static final String FPRIVATENUM = "fPrivateNum";//私有消息
    public static final String FCONTENT = "fContent";//邀请消息
    public static final String FGIFTNUM = "fGiftNum";//礼物


    public static String getCreateTable() {
        return "CREATE TABLE IF NOT EXISTS " + FLETTER_TABLE + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USERID + " TEXT NOT NULL UNIQUE," +
                COLUMN_INFOJSON + " BLOB," +
                COLUMN_TYPE + " TEXT," +
                COLUMN_KFID + " INTEGER," +
                COLUMN_STATUS + " INTEGER," +
                COLUMN_CMSGID + " INTEGER," +
                COLUMN_RU + " INTEGER," +
                COLUMN_TIME + " TEXT," +
                COLUMN_CONTENT + " BLOB, " +
                COLUMN_MSGID + " TEXT, field1 TEXT, field2 TEXT, field3 TEXT, field4 TEXT, field5 TEXT)";
    }
}
