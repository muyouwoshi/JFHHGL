package com.juxin.predestinate.ui.user.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.log.PLogger;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.socket.IMProxy;
import com.juxin.predestinate.module.logic.socket.KeepAliveSocket;
import com.juxin.predestinate.module.logic.socket.TCPConstant;
import com.juxin.predestinate.module.util.UIShow;

import java.util.HashMap;

/**
 * 自定义长连接状态变更提示view
 * Created by Administrator on 2017/7/25.
 */

@SuppressLint("ViewConstructor")
public class OffNetPanel extends LinearLayout implements View.OnClickListener, PObserver {

    private TextView netStatus;
    private RelativeLayout netStatusLayout;
    private ImageView goNoNetDes;
    private Context mContext;
    private ViewGroup mViewGroup;

    public OffNetPanel(Context context, ViewGroup viewGroup) {
        super(context);
        mContext = context;
        mViewGroup = viewGroup;

        initParent(viewGroup);
        initView();

        MsgMgr.getInstance().attach(this, true);
        getSocketStatus();
    }

    private void initParent(ViewGroup viewGroup) {
        viewGroup.setVisibility(View.GONE);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        addView(View.inflate(mContext, R.layout.offnetpanel, null));
    }

    private void initView() {
        netStatusLayout = (RelativeLayout) findViewById(R.id.netStatusLayout);
        netStatusLayout.setOnClickListener(this);
        netStatus = (TextView) findViewById(R.id.netStatus);
        goNoNetDes = (ImageView) findViewById(R.id.go_no_net_des);
        goNoNetDes.setVisibility(View.GONE);
    }


    /**
     * 状态回调
     * @param key   事件key
     * @param value 事件传递值
     */
    @Override
    public void onMessage(String key, Object value) {
        if (MsgType.MT_App_IMStatus.equals(key)) {
            HashMap<String, Object> type = ((HashMap<String, Object>) value);
            setData(type);
        }
    }

    /*
    socket状态  CONNECTED已连接上 DISCONNECT  断开,CONNECTING连接中
     */
    public enum SocketStatus {
        CONNECTED, DISCONNECT, CONNECTING
    }

    private SocketStatus mStatus = SocketStatus.CONNECTED;

    /**
     * 根据当前长连接状态刷新界面展示
     *
     * @param status 当前长连接状态
     */
    private void setNetStatus(SocketStatus status) {
        mStatus = status;
        if (status == SocketStatus.CONNECTED) {
            mViewGroup.setVisibility(View.GONE);
            netStatusLayout.setVisibility(View.GONE);
            goNoNetDes.setVisibility(View.GONE);
            mHandler.removeCallbacks(runnable);
        } else if (status == SocketStatus.CONNECTING) {
            mViewGroup.setVisibility(View.VISIBLE);
            netStatusLayout.setVisibility(View.VISIBLE);
            goNoNetDes.setVisibility(View.GONE);
            netStatus.setText(mContext.getString(R.string.net_status_connecting));
        } else if (status == SocketStatus.DISCONNECT) {
            mViewGroup.setVisibility(View.VISIBLE);
            netStatusLayout.setVisibility(View.VISIBLE);
            goNoNetDes.setVisibility(View.VISIBLE);
            netStatus.setText(mContext.getString(R.string.net_status_no_des));
            mHandler.removeCallbacks(runnable);
            mHandler.postDelayed(runnable, 5 * 1000);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            PLogger.i("OffNetPanel长连接状态获取轮循中...");
            getSocketStatus();
            mHandler.postDelayed(this, 5 * 1000);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * 初始化时从长连接模块获取当前的连接状态，防止监听先行初始化
     */
    private void getSocketStatus() {
        int socketStatus = IMProxy.getInstance().getSocketStatus();
        if (socketStatus == KeepAliveSocket.SocketState.CONNECTED_SUCCESS.ordinal()) {
            setNetStatus(SocketStatus.CONNECTED);
        } else if (socketStatus == KeepAliveSocket.SocketState.CONNECTING.ordinal()) {
            setNetStatus(SocketStatus.CONNECTING);
        } else {
            setNetStatus(SocketStatus.DISCONNECT);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.netStatusLayout:
                if (mStatus == SocketStatus.DISCONNECT) {
                    UIShow.showOffNetDesAct(mContext);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 根据长连接变更消息监听刷新当前界面展示状态
     *
     * @param type 长连接变更消息参数
     */
    private void setData(HashMap<String, Object> type) {
        if (type.containsKey("type")) {
            int typeint = (int) type.get("type");
            if (!NetworkUtils.isConnected(getContext())) {
                setNetStatus(SocketStatus.DISCONNECT);
            } else {
                if (typeint == TCPConstant.SOCKET_STATUS_Login_Success) {
                    setNetStatus(SocketStatus.CONNECTED);
                } else {
                    setNetStatus(SocketStatus.CONNECTING);
                }
            }
        }
    }

    /**
     * 释放资源
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MsgMgr.getInstance().detach(this);
        mHandler.removeCallbacks(runnable);
    }
}
