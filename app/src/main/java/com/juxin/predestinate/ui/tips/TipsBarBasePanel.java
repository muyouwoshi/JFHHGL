package com.juxin.predestinate.ui.tips;

import android.content.Context;

import com.juxin.library.observe.Msg;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.module.logic.tips.TipsBarMgr;
import com.juxin.predestinate.module.logic.tips.TipsBarType;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 小标题黄色基类
 * Created by Kind on 2016/11/16.
 */

public abstract class TipsBarBasePanel extends BasePanel {

    private JSONObject jsonObject;

    /**
     * 初始化
     */
    public void init(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public TipsBarBasePanel(Context context) {
        super(context);
    }

    public JSONObject getJsonObject() {
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * 发送消息
     *
     * @param tipsMgrTag    返回类型
     * @param tipsMgrType   返回提示条的类型
     * @param tipsMgrIsShow 是否显示
     */
    public void sendSuspensionNoticeMsg(String tipsMgrTag, TipsBarType tipsMgrType, String tipsMgrIsShow) {
        Map<String, Object> parms = new HashMap<>();
        parms.put(TipsBarMgr.TipsMgrTag, tipsMgrTag);
        parms.put(TipsBarMgr.TipsMgrType, tipsMgrType);
        parms.put(TipsBarMgr.TipsMgrIsShow, tipsMgrIsShow);
        Msg msg = new Msg();
        msg.setData(parms);
        MsgMgr.getInstance().sendMsg(MsgType.MT_APP_Suspension_Notice, msg);
    }
}
