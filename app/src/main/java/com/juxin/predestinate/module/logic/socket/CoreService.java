package com.juxin.predestinate.module.logic.socket;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.module.util.ExTimerUtil;
import com.juxin.predestinate.module.util.TimerUtil;

/**
 * socket长连接服务
 * Created by ZRP on 2017/3/10.
 */
public class CoreService extends Service {

    private CoreServiceBinder binder = null;
    private NetWorkChangeReceiver netWorkChangeReceiver = null;

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new CoreServiceBinder();

        Intent intent = new Intent(this, CoreService.class);
        intent.putExtra("timer", "60");
        ExTimerUtil.startRepeatTimer(this, intent, TCPConstant.Heartbeat_Time);//每隔60s发送一次心跳消息
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if ("60".equals(intent.getStringExtra("timer"))) {//心跳
                AutoConnectMgr.getInstance().heartbeat();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private class CoreServiceBinder extends ICoreService.Stub {

        @Override
        public void setCallback(ICSCallback iCSCallback) throws RemoteException {
            AutoConnectMgr.getInstance().setCallback(iCSCallback);
        }

        @Override
        public void login(long uid, String token) throws RemoteException {
            AutoConnectMgr.getInstance().login(uid, token);

            if (netWorkChangeReceiver == null) {
                netWorkChangeReceiver = new NetWorkChangeReceiver();
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netWorkChangeReceiver, intentFilter);
        }

        @Override
        public void logout() throws RemoteException {
            AutoConnectMgr.getInstance().logout();

            if (netWorkChangeReceiver != null) {
                unregisterReceiver(netWorkChangeReceiver);
            }
        }

        @Override
        public void setLocationGPS(double longitude, double latitude) throws RemoteException {

        }

        @Override
        public void setSystemInfo(int systemInfo) throws RemoteException {

        }

        @Override
        public void sendMsg(NetData data) throws RemoteException {
            AutoConnectMgr.getInstance().send(data);
        }

        @Override
        public int getSocketStatus() throws RemoteException {
            return AutoConnectMgr.getInstance().getSocketStatus().ordinal();
        }

        @Override
        public void reSetHostPort(String hosts, int port) throws RemoteException {
            AutoConnectMgr.getInstance().reSetHostPort(hosts, port);
        }
    }

    private class NetWorkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtils.isConnected(context)) {
                TimerUtil.resetIncreaseTime();
                AutoConnectMgr.getInstance().reConnect();
                MsgMgr.getInstance().sendMsg(MsgType.MT_Net_Change, "");

            }
        }
    }
}
