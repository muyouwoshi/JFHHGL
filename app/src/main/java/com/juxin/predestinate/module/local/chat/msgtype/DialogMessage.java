package com.juxin.predestinate.module.local.chat.msgtype;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 弹框消息类型
 *
 * @author Mr.Huang
 * @date 2017-07-18
 */
public class DialogMessage extends BaseMessage {

    public static final String TYPE_FREEKEY_RECVED = "freekey_recved";
    public static final String FREEVIDEOCARD_RECVED = "freevideocard_recved";

    private String dialogType;//弹框类型，在上面用用常量定义
    private DialogData data;//携带的相关数据

    @Override
    public BaseMessage parseJson(String jsonStr) {
        super.parseJson(jsonStr);
        JSONObject object = JSON.parseObject(jsonStr);
        this.dialogType = object.getString("dialog_tp");
        if (TYPE_FREEKEY_RECVED.equals(this.dialogType)) {
            FreeKeyDialogData fdd = new FreeKeyDialogData();
            object = object.getJSONObject("dialog_data");
            fdd.keyCount = object.getIntValue("key_count");
            data = fdd;
        } else if (FREEVIDEOCARD_RECVED.equals(this.dialogType)) {
            object = object.getJSONObject("dialog_data");
            FreeVideoCard fvd = new FreeVideoCard();
            fvd.count = object.getIntValue("count");
            fvd.videochat_len = object.getIntValue("videochat_len");
            data = fvd;
        }
        return this;
    }

    public String getDialogType() {
        return dialogType;
    }

    public DialogData getDialogData() {
        return data;
    }

    /**
     * 免费钥匙具体类
     */
    public static class FreeKeyDialogData extends DialogData {
        public int keyCount;
    }

    public static class FreeVideoCard extends DialogData{
//        {"count":1,"videochat_len":10}
        public int count;
        public int videochat_len;
    }

    /**
     * 抽象弹框数据类
     */
    public static abstract class DialogData {

    }
}
