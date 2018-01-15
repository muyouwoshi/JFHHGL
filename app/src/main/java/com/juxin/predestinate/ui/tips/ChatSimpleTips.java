package com.juxin.predestinate.ui.tips;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.log.PLogger;
import com.juxin.library.observe.Msg;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.tips.TipsBarMgr;
import com.juxin.predestinate.module.logic.tips.TipsBarType;
import com.juxin.predestinate.module.util.TimerUtil;

import org.json.JSONObject;

import java.util.Map;

import static com.juxin.library.observe.MsgType.MT_Inner_Suspension_Notice;

/**
 * 聊天界面友情提示
 * Created by zhang on 2016/11/25.
 */

public class ChatSimpleTips extends TipsBarBasePanel implements PObserver {

    private String TAG = "ChatSimpleTips";

    private TextView tipsTex;
    //提示内容
    private String tipsStr = "";

    private RelativeLayout y2_simple_tip_content;

    //提示条显示时间
    private int time = 3;
    //对方用户uid
    private long uid = 0;

    public ChatSimpleTips(Context context) {
        super(context);
    }

    @Override
    public void init(JSONObject jsonObject) {
        super.init(jsonObject);
        MsgMgr.getInstance().attach(this);
        setContentView(R.layout.y1_simple_tips);
        initView();
        getContentView().setTag("ChatSimpleTips");
    }

    private void initView() {
        y2_simple_tip_content = (RelativeLayout) findViewById(R.id.y2_simple_tip_content);
        y2_simple_tip_content.setVisibility(View.GONE);
        tipsTex = (TextView) findViewById(R.id.simple_tips_tev);
    }

    public void setTipsTex(String tex) {
        if (tipsTex != null) {
            tipsTex.setText(tex);
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        if (TextUtils.isEmpty(key)) {
            PLogger.d(TAG + "  onMessage key is null");
            return;
        }
        switch (key) {
            case MT_Inner_Suspension_Notice:
                Msg msg = (Msg) value;
                Map<String, Object> parms = (Map<String, Object>) msg.getData();
                if (parms.containsKey(TipsBarMgr.TipsMgrTag) && parms.get(TipsBarMgr.TipsMgrTag).equals(TipsBarMgr.TipsMgr_Order)) {
                    if (parms.containsKey(TipsBarMgr.TipsMgrType) && (TipsBarType) parms.get(TipsBarMgr.TipsMgrType) == TipsBarType.Show_Chart_Simple_tips) {
                        getJson((JSONObject) parms.get(TipsBarMgr.TipsMgrJson));
                        time = ((TipsBarType) parms.get(TipsBarMgr.TipsMgrType)).getTime();
                        onFriendlyReminder();
                    }
                }
                break;
        }
    }

    private void getJson(JSONObject jsonObject) {
        if (jsonObject != null) {
            if (!jsonObject.isNull("tipsTex")) {
                tipsStr = jsonObject.optString("tipsTex", "");
            }
            if (!jsonObject.isNull("uid")) {
                uid = jsonObject.optLong("uid", 0);
            }
        }
    }

    private void refreshTips() {
        y2_simple_tip_content.setVisibility(View.VISIBLE);
        setTipsTex(tipsStr);
        TimerUtil.beginTime(new TimerUtil.CallBack() {
            @Override
            public void call() {
                sendMsg(false);
            }
        }, time * 1000);
        sendMsg(true);
    }

    /**
     * 发送回返数据
     *
     * @param isShow
     */
    private void sendMsg(boolean isShow) {
        sendSuspensionNoticeMsg(TipsBarMgr.TipsMgr_Result, TipsBarType.Show_Chart_Simple_tips, isShow ? TipsBarMgr.TipsMgrIsShow_true : TipsBarMgr.TipsMgrIsShow_false);
    }

    /**
     * 聊天面板头部友情提示
     */
    private void onFriendlyReminder() {
        if (uid != 0) {
            //TODO 聊天面板头部友情提示
//            if (MailMsgID.getMailMsgID(uid) == null && uid != MailSpecialID.customerService.getSpecialID()) {
//                ModuleMgr.getChatListMgr().queryDMarkchatFriendlyReminder(uid, new ChatMsgInterface.DMarkListener() {
//                    @Override
//                    public void onDataListener(boolean ret, boolean isUsed) {
//                        Log.d("_test", "onFriendlyReminder onDataListener  ret == " + ret + " isUsed == " + isUsed);
//                        if (ret && isUsed) {
//                            handler.sendEmptyMessage(-100);
//                            ModuleMgr.getChatListMgr().insertDMarkchatFriendlyReminder(uid);
//                        } else {
//                            sendMsg(false);
//                        }
//                    }
//                });
//            } else {
//                sendMsg(false);
//            }
        } else {
            sendMsg(false);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -100:
                    refreshTips();
                    break;
            }
        }
    };

}