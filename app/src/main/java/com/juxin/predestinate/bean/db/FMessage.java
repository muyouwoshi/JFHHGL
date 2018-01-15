package com.juxin.predestinate.bean.db;

/**
 * Created by Kind on 2017/3/22.
 */

public class FMessage {

    public static final String FMESSAGE_TABLE = "fmessage";

    public static final String _ID = "_id";
    public static final String COLUMN_CHANNELID = "channelID";// 频道ID
    public static final String COLUMN_WHISPERID = "whisperID";// 私聊ID
    public static final String COLUMN_SENDID = "sendID";// 发送ID
    public static final String COLUMN_MSGID = "msgID";// 服务器消息ID
    public static final String COLUMN_CMSGID = "cMsgID";// 客户端消息ID
    public static final String COLUMN_SPECIALMSGID = "specialMsgID";// 对于这条消息来说的特殊ID
    public static final String COLUMN_TYPE = "type";// 消息类型
    public static final String COLUMN_STATUS = "status";// 1.发送成功2.发送失败3.发送中 10.未读11.对方已读//12未审核通过//13本地已读
    public static final String COLUMN_FSTATUS = "fStatus";// 默认1 如果是已读就是0
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CONTENT = "content";//json

    public static String getCreateTable() {
        return "CREATE TABLE IF NOT EXISTS " + FMESSAGE_TABLE + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CHANNELID + " TEXT," +
                COLUMN_WHISPERID + " INTEGER," +
                COLUMN_SENDID + " INTEGER NOT NULL," +
                COLUMN_MSGID + " INTEGER UNIQUE," +
                COLUMN_CMSGID + " INTEGER UNIQUE," +
                COLUMN_SPECIALMSGID + " INTEGER UNIQUE," +
                COLUMN_TYPE + " TEXT," +
                COLUMN_STATUS + " INTEGER, " +
                COLUMN_FSTATUS + " INTEGER," +
                COLUMN_TIME + " TEXT," +
                COLUMN_CONTENT + " BLOB, folder TEXT, field1 TEXT, field2 TEXT, field3 TEXT, field4 TEXT, field5 TEXT)";
    }
}
