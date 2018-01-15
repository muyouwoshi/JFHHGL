package com.juxin.predestinate.ui.tips;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.juxin.library.log.PLogger;
import com.juxin.library.observe.Msg;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.PObserver;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.tips.TipsBarMgr;
import com.juxin.predestinate.module.logic.tips.TipsBarType;
import com.juxin.predestinate.module.util.UIShow;

import org.json.JSONObject;

import java.util.Map;

import static com.juxin.library.observe.MsgType.MT_Inner_Suspension_Notice;

/**
 * 网络变换提示条
 * Created by zhang on 2016/8/30.
 */
public class NetErrorTips extends TipsBarBasePanel implements View.OnClickListener, PObserver {
    
    private View net_error_panel;

    public NetErrorTips(Context context) {
        super(context);
    }

    @Override
    public void init(JSONObject jsonObject) {
        super.init(jsonObject);
        MsgMgr.getInstance().attach(this);
        setContentView(R.layout.y1_net_error_tips);
        initView();
        getContentView().setTag("NetErrorTips");
    }

    private void initView() {
        net_error_panel = findViewById(R.id.net_error_panel);
        net_error_panel.setOnClickListener(this);
        refreshNetErrorTips(NetworkUtils.isConnected(getContext()));
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -10:
                    setViewState((Boolean) msg.obj);
                    break;
                case -11:
                    sendResultMsg();
                    break;
                default:
                    setViewState(true);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.net_error_panel:
                UIShow.showNetworkSettings(getContext());
                break;
        }
    }

    /**
     * 刷新显示
     *
     * @param isConnect 是否连接成功
     */
    public void refreshNetErrorTips(boolean isConnect) {
        Message message = new Message();
        message.what = -10;
        message.obj = isConnect;
        handler.sendMessage(message);
    }

    /**
     * 设置界面显示状态
     *
     * @param isConnect
     */
    private void setViewState(boolean isConnect) {
        if (null == net_error_panel) {
            return;
        }
        Log.d("_test", "NetErrorTips isConnect == " + isConnect + " state == " + NetworkUtils.isConnected(getContext()));
        if (isConnect) {
            net_error_panel.setVisibility(View.GONE);
        } else {
            net_error_panel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        if (TextUtils.isEmpty(key)) {
            PLogger.d("");
            return;
        }
        switch (key) {
            case MT_Inner_Suspension_Notice:
                Msg msg = (Msg) value;
                Map<String, Object> parms = (Map<String, Object>) msg.getData();
                if (parms.containsKey(TipsBarMgr.TipsMgrTag) && parms.get(TipsBarMgr.TipsMgrTag).equals(TipsBarMgr.TipsMgr_Order)) {
                    if (parms.containsKey(TipsBarMgr.TipsMgrType) && (TipsBarType) parms.get(TipsBarMgr.TipsMgrType) == TipsBarType.Show_Network_Status_Change) {
                        refreshNetErrorTips(NetworkUtils.isConnected(getContext()));
                        handler.sendEmptyMessage(-11);
                    }
                }
                break;
        }
    }

    /**
     * 发送回返状态
     */
    private void sendResultMsg() {
        sendSuspensionNoticeMsg(TipsBarMgr.TipsMgr_Result, TipsBarType.Show_Network_Status_Change,
                !NetworkUtils.isConnected(getContext()) ? TipsBarMgr.TipsMgrIsShow_true : TipsBarMgr.TipsMgrIsShow_false);
    }

}
