package com.juxin.predestinate.ui.tips;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.juxin.library.log.PLogger;
import com.juxin.library.observe.Msg;
import com.juxin.library.observe.PObserver;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.util.UIShow;

import static com.juxin.library.observe.MsgType.MT_Network_Status_Change;

/**
 * 网络连接状态提示条
 * Created by zhang on 2016/7/12.
 */
public class NetErrorTipPanel extends BasePanel implements View.OnClickListener, PObserver {

    private String TAG = "NetErrorTipPanel";

    private View net_error_panel, net_error_top_view;
    private boolean isShowTopView = true;
    private NetStateChange stateChange;

    public NetErrorTipPanel(Context context) {
        super(context);
        setContentView(R.layout.y1_net_error_tips);
        initView();
    }

    /**
     * 网络连接状态提示条
     *
     * @param context
     * @param isShowTopView 是否显示 topView
     */
    public NetErrorTipPanel(Context context, boolean isShowTopView) {
        super(context);
        setContentView(R.layout.y1_net_error_tips);
        setIsShowTopView(isShowTopView);
        initView();
    }

    /**
     * 网络连接状态提示条
     *
     * @param context
     * @param isShowTopView 是否显示顶部空白View
     * @param stateChange   网络状态变化回调
     */
    public NetErrorTipPanel(Context context, boolean isShowTopView, NetStateChange stateChange) {
        super(context);
        setContentView(R.layout.y1_net_error_tips);
        setIsShowTopView(isShowTopView);
        setStateChange(stateChange);
        initView();
    }


    private void initView() {
        net_error_panel = findViewById(R.id.net_error_panel);
        net_error_top_view = findViewById(R.id.net_error_top_view);
        net_error_panel.setOnClickListener(this);
        if (NetworkUtils.isConnected(getContext())) {
            refreshNetErrorTips(true);
        } else {
            refreshNetErrorTips(false);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -10:
                    setViewState(true);
                    break;
                case -11:
                    setViewState(false);
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
        if (isConnect) {
            handler.sendEmptyMessage(-10);
        } else {
            handler.sendEmptyMessage(-11);
        }
    }

    private void setViewState(boolean isConnect) {
        if (null == net_error_panel) {
            return;
        }
        Log.d("_test", "isConnect == " + isConnect);
        if (isConnect) {
            net_error_panel.setVisibility(View.GONE);
            net_error_top_view.setVisibility(isShowTopView ? View.VISIBLE : View.GONE);
        } else {
            net_error_panel.setVisibility(View.VISIBLE);
            net_error_top_view.setVisibility(isShowTopView ? View.GONE : View.GONE);
        }
    }

    public void setIsShowTopView(boolean isShow) {
        this.isShowTopView = isShow;
    }

    public NetStateChange getStateChange() {
        return stateChange;
    }

    public void setStateChange(NetStateChange stateChange) {
        this.stateChange = stateChange;
    }

    @Override
    public void onMessage(String key, Object value) {
        if (TextUtils.isEmpty(key)) {
            PLogger.d(TAG + "onMessage key is null");
            return;
        }
        switch (key) {
            case MT_Network_Status_Change:
                Msg msg = (Msg) value;
                boolean isConnect = (boolean) (msg.getData());
                refreshNetErrorTips(isConnect);
                if (null != getStateChange()) {
                    getStateChange().onNetStateChange(isConnect);
                }
                break;
        }
    }


    /**
     * 返回是否连接网络
     */
    public interface NetStateChange {
        /**
         * 网络状态变化回调
         *
         * @param isConnect 是否连接 true 链接 false 断开
         */
        void onNetStateChange(boolean isConnect);
    }
}
