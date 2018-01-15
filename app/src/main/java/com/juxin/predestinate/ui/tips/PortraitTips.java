package com.juxin.predestinate.ui.tips;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.juxin.library.log.PLogger;
import com.juxin.library.observe.Msg;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.tips.TipsBarMgr;
import com.juxin.predestinate.module.logic.tips.TipsBarType;

import org.json.JSONObject;

import java.util.Map;

import static com.juxin.library.observe.MsgType.MT_Inner_Suspension_Notice;

/**
 * 头像更新提示
 * Created by zhang on 2016/8/30.
 */
public class PortraitTips extends TipsBarBasePanel implements View.OnClickListener, PObserver {

    private String TAG = "PortraitTips";

    private TextView update_portrait_content;
    private View update_portrait_tips;

    private int time = 3;

    public PortraitTips(Context context) {
        super(context);
    }

    @Override
    public void init(JSONObject jsonObject) {
        super.init(jsonObject);
        setContentView(R.layout.y1_update_portrait_tips);
        initView();
        getContentView().setTag("PortraitTips");
    }

    private void initView() {
        MsgMgr.getInstance().attach(this);
        update_portrait_tips = findViewById(R.id.update_portrait_tips);
        update_portrait_content = (TextView) findViewById(R.id.update_portrait_content);
        update_portrait_tips.setOnClickListener(this);
        update_portrait_tips.setVisibility(View.GONE);
        setTipsContent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update_portrait_tips:
//                UIShow.showNoHeadAct(getContext());
                break;
        }
    }

    /**
     * 提示条内容
     */
    public void setTipsContent() {
        //TODO 提示条内容
//        if (update_portrait_content != null) {
//            if (ModuleMgr.getCenterMgr().isMan()) {
//                update_portrait_content.setText(getContext().getResources().getString(R.string.portrait_tips_man));
//            } else {
//                update_portrait_content.setText(getContext().getResources().getString(R.string.portrait_tips_women));
//            }
//        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -10:
                    update_portrait_tips.setVisibility(View.GONE);
                    break;
                case -11:
                    setTipsContent();
                    update_portrait_tips.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };


    public void refreshView() {
        handler.sendEmptyMessage(-11);
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
                    if (parms.containsKey(TipsBarMgr.TipsMgrType)) {
                        TipsBarType type = (TipsBarType) parms.get(TipsBarMgr.TipsMgrType);
                        Log.d("_test", " panel name == " + type.getBaseViewPanel().getName() + " this name = " + getClass().getName());
                        if (type.getBaseViewPanel().getName().equals(this.getClass().getName())) {
                            time = type.getTime();
                            //显示时间是-1 是常驻提示条 否则是悬浮提示条
                            if (time == -1) {
                                getPortraitState();
                            } else {
                                showFloatTips();
                            }
                        }
                    }
                }
                break;
        }
    }

    /**
     * 发送显示悬浮提示条指令
     * 如果头像没通过则显示提示条 否则不显示
     */
    private void showFloatTips() {
        //TODO 发送显示悬浮提示条指令
      /*  if (!ModuleMgr.getCenterMgr().isAvatarPass()) {
            sendResultMsg(true);
            refreshView();
            TimerUtil.beginTime(new TimerUtil.CallBack() {
                @Override
                public void call() {
                    sendResultMsg(false);
                }
            }, time * 1000);
        } else {
            handler.sendEmptyMessage(-10);
            sendResultMsg(false);
        }*/

    }

    /**
     * 发送显示常驻提示条指令
     * 如果头像没通过则显示提示条 否则不显示
     */
    private void getPortraitState() {
        //TODO 发送显示常驻提示条指令
        /*if (!ModuleMgr.getCenterMgr().isAvatarPass()) {
            refreshView();
            sendResultMsg(true);
        } else {
            handler.sendEmptyMessage(-10);
            sendResultMsg(false);
        }*/
    }

    /**
     * 发送回返管理中心数据
     *
     * @param isShow
     */
    private void sendResultMsg(boolean isShow) {
        sendSuspensionNoticeMsg(TipsBarMgr.TipsMgr_Result,
                time == -1 ? TipsBarType.Show_Update_Portrait : TipsBarType.Show_Float_Update_Portrait,
                isShow ? TipsBarMgr.TipsMgrIsShow_true : TipsBarMgr.TipsMgrIsShow_false);
    }

}
