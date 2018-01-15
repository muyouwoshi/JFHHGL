package com.juxin.predestinate.ui.tips;

import android.content.Context;
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
 * 普通文字提示条
 * Created by zhang on 2016/8/30.
 */
public class SimpleTips extends TipsBarBasePanel implements PObserver {

    private String TAG = "SimpleTips";

    private TextView tipsTex;

    private String tipsStr = "";

    private RelativeLayout y2_simple_tip_content;

    private int time = 3;

    public SimpleTips(Context context) {
        super(context);
    }

    @Override
    public void init(JSONObject jsonObject) {
        super.init(jsonObject);
        MsgMgr.getInstance().attach(this);
        setContentView(R.layout.y1_simple_tips);
        initView();
        getContentView().setTag("SimpleTips");
        getJson(jsonObject);
    }

    private void initView() {
        y2_simple_tip_content = (RelativeLayout) findViewById(R.id.y2_simple_tip_content);
        y2_simple_tip_content.setVisibility(View.GONE);
        tipsTex = (TextView) findViewById(R.id.simple_tips_tev);
    }

    /**
     * 设置提示内容
     *
     * @param tex
     */
    public void setTipsTex(String tex) {
        if (tipsTex != null) {
            tipsTex.setText(tex);
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        if (TextUtils.isEmpty(key)) {
            PLogger.d(TAG + " onMessage key is null");
            return;
        }
        switch (key) {
            case MT_Inner_Suspension_Notice:
                Msg msg = (Msg) value;
                Map<String, Object> parms = (Map<String, Object>) msg.getData();
                if (parms.containsKey(TipsBarMgr.TipsMgrTag) && parms.get(TipsBarMgr.TipsMgrTag).equals(TipsBarMgr.TipsMgr_Order)) {
                    if (parms.containsKey(TipsBarMgr.TipsMgrType) && (TipsBarType) parms.get(TipsBarMgr.TipsMgrType) == TipsBarType.Show_Simple_tips) {
                        getJson((JSONObject) parms.get(TipsBarMgr.TipsMgrJson));
                        time = ((TipsBarType) parms.get(TipsBarMgr.TipsMgrType)).getTime();
                        refreshTips();
                    }
                }
                break;
        }
    }

    /**
     * 获取提示内容数据
     *
     * @param jsonObject
     */
    private void getJson(JSONObject jsonObject) {
        if (jsonObject != null) {
            if (!jsonObject.isNull("tipsTex")) {
                tipsStr = jsonObject.optString("tipsTex", "");
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
        sendSuspensionNoticeMsg(TipsBarMgr.TipsMgr_Result, TipsBarType.Show_Simple_tips,
                isShow ? TipsBarMgr.TipsMgrIsShow_true : TipsBarMgr.TipsMgrIsShow_false);
    }

}
