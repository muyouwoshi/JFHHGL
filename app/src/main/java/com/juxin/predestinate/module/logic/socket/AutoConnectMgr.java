package com.juxin.predestinate.module.logic.socket;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.EncryptUtil;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.ServerTime;
import com.juxin.predestinate.module.util.TimerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 负责即时通讯的重连机制，同时提供对外的操作接口
 *
 * @author ZRP
 */
public class AutoConnectMgr implements KeepAliveSocket.SocketConnectionListener {
    /**
     * 连接服务器失败
     */
    private final int DISCONNECT_TYPE_CONNECT_FAILED_TO_SEVER = 1;
    /**
     * 发送数据超时
     */
    private final int DISCONNECT_TYPE_SEND_PACKET_TIMEOUT = 2;
    /**
     * 服务器主动断开连接
     */
    private final int DISCONNECT_TYPE_SEVER_DISCONNECTED = 3;
    /**
     * 连接线程池
     */
    private final Executor connectionExecutor = Executors.newSingleThreadExecutor();
    /**
     * 发送消息线程池
     */
    private final Executor sendExecutor = Executors.newCachedThreadPool();
    private Disposable disposable;

    private static class SingletonHolder {
        static AutoConnectMgr instance = new AutoConnectMgr();
    }

    public static AutoConnectMgr getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * KeepAliveSocket封装了连接服务器、接收、发送数据等功能。
     */
    private KeepAliveSocket socket = null;

    private ICSCallback iCSCallback = null;
    private volatile long uid = 0;//用户ID
    private String token;//用户token

    private AutoConnectMgr() {
        socket = new KeepAliveSocket(TCPConstant.HOST, TCPConstant.PORT);
        socket.setSocketStateListener(this);
    }

    /**
     * 设置回调，和App进行交互。
     *
     * @param iCSCallback 回调实例。
     */
    public void setCallback(ICSCallback iCSCallback) {
        this.iCSCallback = iCSCallback;
    }

    /**
     * 登录用的信息。
     * 自动重连时需要用到，需要保存到内存中。Service重启后，需要再次调用login。<br>
     * 如果token是null或者""，则将现有Socket断开。否则则重新连接。
     *
     * @param uid   用户uid。
     * @param token 登录用的cookie。
     */
    public void login(long uid, String token) {
        if (TextUtils.isEmpty(token)) {
            PLogger.d("---socket login--->token is empty,login return.");
            logout();
            return;
        }
        this.uid = uid;
        this.token = token;

        if (heartBeating) return;//如果是保持心跳连接状态，就不再次登录
        TimerUtil.resetIncreaseTime();//主动重连接时重置间隔时间
        PLogger.d("login: ------>发送连接服务器的消息");
        connect();
    }

    public KeepAliveSocket.SocketState getSocketStatus() {
        if (socket != null) {
            return socket.getSocketState();
        }
        return KeepAliveSocket.SocketState.INVALID;
    }

    /**
     * 退出登录，即断开即时通讯的连接。同时清除登录用的token。
     *
     * @see #login(long, String) login(long uid, String token)
     */
    public void logout() {
        PLogger.d("logout: ------>与服务器断开连接");
        this.uid = 0;
        this.token = "";

        heartBeating = false;
        loopHeartbeatStatus();
        heartbeatSend = 0;
        heartbeatResend = 0;
        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                socket.disconnect(true);
            }
        });
        this.token = null;
    }

    public void send(final NetData packet) {
        sendExecutor.execute(new Runnable() {
            @Override
            public void run() {
                socket.sendPacket(packet);
            }
        });
    }

    public void reSetHostPort(String hosts, int port) {
        if (socket != null) {
            isFirstTry = true;
            socket.reSetHostPort(hosts, port);
        }
    }

    // =================以下是关于内部自动连接维护的功能=================
    /**
     * 重连递增时间，每次2秒
     */
    private int incrementTime = 0;

    /**
     * @return 请求发送的延时时长
     */
    private int getIncrementTime() {
        incrementTime = TimerUtil.increaseTime(TCPConstant.SOCKET_AUTO_CONNECT_Increment_Time, 60 * 1000, false);
        PLogger.d("socket reconnect delayed time：" + incrementTime);
        return incrementTime;
    }

    /**
     * 以获取到的地址和秘钥登录及时通讯服务器
     */
    protected void connect() {
        if (uid == 0) return;
        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                socket.connect();
            }
        });
        PLogger.d("connect: ------>socket开始连接，hostIP：" + TCPConstant.HOST + ":" + TCPConstant.PORT);
    }

    /**
     * 重连，每次重连进行延时，防止刷服务器
     */
    protected void reConnect() {
        if (uid == 0) return;
        if (disposable != null && !disposable.isDisposed()) {
            return;
        }
        Observable<Long> timer = Observable.timer(getIncrementTimeForReDomainName(), TimeUnit.MILLISECONDS);
        disposable = timer.subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                disposable.dispose();
                disposable = null;
                connect();
            }
        });
    }

    private boolean isFirstTry = true;

    /*
    socket第一次断开时不切换域名，第二次时有网的情况下切换域名
     */
    private int getIncrementTimeForReDomainName() {
        int time = getIncrementTime();
        //int time =10*1000;
        if (isFirstTry && NetworkUtils.isConnected(App.getContext())) {
            isFirstTry = false;
        } else {
            if (time > 5 * 1000 && App.getContext() != null && NetworkUtils.isConnected(App.getContext())) {
                changeDomainName();
            }
        }
        return time;
    }

    private void changeDomainName() {
        try {
            if (iCSCallback != null) {
                iCSCallback.changeDomainName();
            }
        } catch (DeadObjectException de) {//如果服务挂了，就缓存当前消息，并重启ChatService
//            CoreService.startChatService(App.context);
            PLogger.d("---AutoConnectMgr--->DeadObjectException");
        } catch (RemoteException e) {
            PLogger.d("---AutoConnectMgr--->RemoteException");
        }
    }

    private boolean heartBeating = false;   //是否需要发送心跳：长连接断线状态为false
    private int heartbeatSend = 0;          //本地发送心跳次数，当数值为2，再次发送心跳的时候重置为0
    private int heartbeatResend = 0;        //心跳回送次数

    /**
     * 发送心跳消息
     */
    public void heartbeat() {
        PLogger.d("---heartbeat--->heartBeating：" +
                heartBeating + "，heartbeatSend：" + heartbeatSend + "，heartbeatResend：" +
                heartbeatResend + "，packet loss：" + (heartbeatSend - heartbeatResend));
        if (!heartBeating) return;

        if (socket == null) {
            heartBeating = false;
            loopHeartbeatStatus();
        } else {
            //回送次数与发送次数误差校准，允许1次丢包。如果两次心跳均未收到回送，就重连socket
            if (heartbeatSend - heartbeatResend > 1) {
                heartBeating = false;
                loopHeartbeatStatus();
                heartbeatSend = 0;
                heartbeatResend = 0;

                onStatusChange(TCPConstant.SOCKET_STATUS_Disconnect, "心跳回送失败，socket重新进行连接");
                connect();
            } else {
                socket.sendPacket(getRevertHeartbeat());//心跳时发送的是回送类型的心跳消息
            }
        }
    }

    private int lastRandomNum = 0;//心跳旧随机数
    private int newRandomNum = 0;//心跳新随机数

    /**
     * 获取心跳消息数据
     *
     * @return NetData socket发送消息
     */
    private NetData getHeartbeat(int heartbeatType) {
        NetData heartbeatData;
        if (heartbeatType == TCPConstant.MSG_ID_HEART_BEAT3) {
            heartbeatData = new NetData(uid, heartbeatType, new byte[]{PSP.getInstance().getBoolean(Constant.APP_IS_FOREGROUND, false) ? (byte) 1 : (byte) 2});
        } else {
            if (lastRandomNum == 0) {
                newRandomNum = PSP.getInstance().getInt("Net_Random_Last", new Random(System.currentTimeMillis()).nextInt());
                lastRandomNum = newRandomNum;
            } else {
                lastRandomNum = newRandomNum;
                newRandomNum = new Random(System.currentTimeMillis()).nextInt();
                PSP.getInstance().put("Net_Random_Last", newRandomNum);
            }
            heartbeatData = new NetData(uid, heartbeatType, lastRandomNum, newRandomNum);
        }
        PLogger.d("getHeartbeat: ---心跳--->类型：" + heartbeatType + "/" + heartbeatData.toString());
        return heartbeatData;
    }

    /**
     * 获取心回送跳消息数据
     *
     * @return NetData socket发送消息
     */
    private NetData getRevertHeartbeat() {
        if (heartbeatSend == 2) {//重置心跳发送与回送次数统计
            heartbeatSend = 0;
            heartbeatResend = 0;
        }
        heartbeatSend++;//叠加心跳发送次数
        return getHeartbeat(TCPConstant.MSG_ID_HEART_BEAT3);
    }

    /**
     * 获取登录信息
     *
     * @return NetData socket发送消息
     */
    private NetData getLoginData() {
        long curTime = ServerTime.getServerTime() / 1000;
        // 注意：md字段md5加密字符串为string的拼接
        String md = EncryptUtil.md5(String.valueOf(uid) + String.valueOf(curTime) + token).toUpperCase();
        Map<String, Object> loginMap = new HashMap<>();
        loginMap.put("fid", uid);
        loginMap.put("mt", curTime);
        loginMap.put("md", md);
        loginMap.put("xt", 0);//android传0，或者不传。暂时不区分系统
        loginMap.put("ms", Constant.MS_TYPE);
        loginMap.put("imei", ModuleMgr.getAppMgr().getDeviceID());//客户端机器码 安卓imei,IOS为用户码（注册时提交未IMEI里的字段）
        loginMap.put("pkg_name", ModuleMgr.getAppMgr().getPackageName());//客户端包名，主要针对IOS（2017-06-20）
        loginMap.put("ontop", PSP.getInstance().getBoolean(Constant.APP_IS_FOREGROUND, false) ? 1 : 2);//是否前端在线 (1为前端在线 2为非前端在线)
        loginMap.put("lite", Constant.PACKAGE_TYPE);//[opt]是否全量包  1为轻量包 2为全量包

        NetData data = new NetData(uid, TCPConstant.MSG_ID_Login, JSON.toJSONString(loginMap));
        PLogger.d("getLoginData: ---socket登录消息--->" + data.toString());
        return data;
    }

    /**
     * @param msgType 消息类型：从消息头中获取
     * @param msgId   消息id：从消息头中获取
     * @return 回送消息体结构
     */
    private NetData getLoopbackData(int msgType, long msgId) {
        Map<String, Object> loopbackMap = new HashMap<>();
        loopbackMap.put("s", 0);
        loopbackMap.put("d", msgId);
        return new NetData(uid, msgType, JSON.toJSONString(loopbackMap));
    }

    /**
     * 应用退出登录
     *
     * @param reason 退出原因：1[异地登陆踢下线]，2[密码验证失败，用户不存在等]，3[账号被封消息]
     */
    private void accountInvalid(int reason) {
        accountInvalid(reason, null);
    }

    private void accountInvalid(int reason, String content) {
        try {
            if (iCSCallback != null) iCSCallback.accountInvalid(reason, content);
        } catch (RemoteException e) {
            PLogger.printThrowable(e);
        }
    }

    /**
     * 回送心跳状态变更
     */
    private void loopHeartbeatStatus() {
        try {
            if (iCSCallback != null) iCSCallback.heartbeatStatus(heartBeating);
        } catch (RemoteException e) {
            PLogger.printThrowable(e);
        }
    }

    public void onDisconnect(int type) {
        //打印日志
        String disconnectType = "";
        switch (type) {
            case DISCONNECT_TYPE_CONNECT_FAILED_TO_SEVER:
                disconnectType = "连接服务器失败";
                break;
            case DISCONNECT_TYPE_SEND_PACKET_TIMEOUT:
                disconnectType = "发送数据超时";
                break;
            case DISCONNECT_TYPE_SEVER_DISCONNECTED:
                disconnectType = "服务器主动关闭";
                break;
        }
        PLogger.d("socket断开连接：" + disconnectType);

        //暂停心跳，开始断线重连
        heartBeating = false;
        loopHeartbeatStatus();
        heartbeatSend = 0;
        heartbeatResend = 0;

        onStatusChange(TCPConstant.SOCKET_STATUS_Disconnect, "socket断开服务器连接");
        reConnect();
    }

    /**
     * 将即时通讯中收到消息通过ICSCallback抛出。
     */
    private void onMessage(NetData data, long msgId) {
//        PLogger.i("onMessage:---->msgId:" + msgId + ",sender:" + data.getUid() + ",content:" + data.getContent());
        try {
            if (iCSCallback != null) {
                iCSCallback.onMessage(data);
            }
        } catch (DeadObjectException de) {//如果服务挂了，就缓存当前消息，并重启ChatService
//            CoreService.startChatService(App.context);
            PLogger.d("---AutoConnectMgr--->DeadObjectException");
        } catch (RemoteException e) {
            PLogger.d("---AutoConnectMgr--->RemoteException");
        }
    }

    /**
     * 将即时通讯中的状态变化发送到App中。
     *
     * @param type 类型。
     * @param msg  消息内容。
     */
    private void onStatusChange(final int type, final String msg) {
        PLogger.d(msg);

        try {
            if (iCSCallback != null) {
                iCSCallback.onStatusChange(type, msg);
            }
        } catch (DeadObjectException de) {//如果服务挂了，就重启聊天service
//            CoreService.startChatService(App.context);
            PLogger.d("---AutoConnectMgr--->DeadObjectException");
        } catch (RemoteException e) {
            PLogger.d("---AutoConnectMgr--->RemoteException");
        }
    }

    @Override
    public void onSocketConnected() {
        onStatusChange(TCPConstant.SOCKET_STATUS_Connected, "socket连接服务器成功");
        TimerUtil.resetIncreaseTime();

        if (uid == 0) {
            socket.disconnect(true);
            return;
        }

        socket.sendPacket(getHeartbeat(TCPConstant.MSG_ID_Heartbeat));//登录时发送的是普通心跳消息
        socket.sendPacket(getLoginData());
    }

    @Override
    public void onSocketConnecting() {
        onStatusChange(TCPConstant.SOCKET_STATUS_ConnectIng, "socket正在连接");
    }

    @Override
    public void onSocketConnectError() {
        onDisconnect(DISCONNECT_TYPE_CONNECT_FAILED_TO_SEVER);
    }

    @Override
    public void onSendPacketError(KeepAliveSocket.SocketState state, NetData failedData) {
        PLogger.d("onSendPacketError:---->SocketState:" + state.name() + ",sender:" + failedData.getUid() + ",content:" + failedData.getContent());
        try {
            if (iCSCallback != null) {
                iCSCallback.onSendMsgError(failedData);
            }
        } catch (DeadObjectException de) {//如果服务挂了，就缓存当前消息，并重启ChatService
//            CoreService.startChatService(App.context);
            PLogger.d("---AutoConnectMgr--->DeadObjectException");
        } catch (RemoteException e) {
            PLogger.d("---AutoConnectMgr--->RemoteException");
        }
    }

    @Override
    public void onSocketDisconnectByError() {
        onDisconnect(DISCONNECT_TYPE_SEND_PACKET_TIMEOUT);
    }

    @Override
    public void onReceivePacket(NetData data) {
        String content = data.getContent();
        PLogger.d("---socket消息头：" + data.getHeaderString() + "\n---socket消息体：" + content);

        if (data.getMsgType() == TCPConstant.MSG_ID_HEART_BEAT3) {//心跳回送消息
            heartbeatResend++;
        }

        if (!TextUtils.isEmpty(content)) {
            try {
                JSONObject contentObject = new JSONObject(content);
                if (contentObject.has("mt")) {
                    ServerTime.setServerTime(contentObject.optLong("mt"));//保存服务器时间戳
                }
                //socket登录处理
                if (contentObject.has("s") && heartBeating == false) {
                    int status = contentObject.optInt("s");//登录状态，0表示成功，其他见下面详情
                    if (status == 0) {//登录成功
                        heartBeating = true;
                        loopHeartbeatStatus();
                        incrementTime = 0;
                        onStatusChange(TCPConstant.SOCKET_STATUS_Login_Success, "socket用户登录成功：" + content);
                    } else {//登录失败，默认为与服务器断开连接，重新连接
                        onStatusChange(TCPConstant.SOCKET_STATUS_Login_Fail, "socket用户登录失败：" + content);
                        switch (status) {
                            case -1://json解析错误
                                PLogger.d("socket登录---->json解析错误");
                                break;
                            case -2://时间戳不对,需要读取返回的mt 字段来重新登录
                                PLogger.d("socket登录---->时间戳不对");
                                reConnect();
                                break;
                            case -3://密码验证失败
                                PLogger.d("socket登录---->密码验证失败");
                                PToast.showLong("密码验证失败，请重新登录");
                                accountInvalid(2);
                                break;
                            case -4://用户已封号
                                PLogger.d("socket登录---->用户已封号");
                                PToast.showLong("您因特殊原因已被系统封号，如有疑问，请联系客服");
                                accountInvalid(2);
                                break;
                            case -5://用户不存在
                                PLogger.d("socket登录---->用户不存在");
                                accountInvalid(2);
                                break;
                        }
                    }
                    return;//如果含s字段，为登录返回消息，处理完成之后直接return，不抛出消息
                }

                if (data.getMsgType() == TCPConstant.MSG_ID_KICK_Offline) {//帐号异地登陆消息或切换服务器消息
                    String imei = contentObject.optString("imei");
                    if (!TextUtils.isEmpty(imei) && imei.equals(ModuleMgr.getAppMgr().getDeviceID())) {
                        PLogger.d("---AutoConnectMgr--->本机超时或者其他错误，不出异地提示");
                    } else {
                        accountInvalid(1);
                    }
                    return;
                }

                if (data.getMsgType() == TCPConstant.MSG_ID_Account_Closed) {//账号被封消息
                    // String unban_tm = contentObject.optString("unban_tm");
                    accountInvalid(3, content);
                    return;
                }

                //消息重要字段解析
                long msgId = contentObject.optLong("d", -1);//消息id
                long sender = contentObject.optLong("fid", -1);//发送者id

//DELETE START 170629 解决App主进程被终止，Service做了消息回执导致主进程丢消息，也收不到离线
//                //消息接收反馈
//                if (data.getMsgType() >= TCPConstant.MSG_ID_SERVER_PUSH_START_INDEX
//                        && data.getMsgType() < TCPConstant.MSG_ID_SERVER_PUSH_END_INDEX) {
//                    socket.sendPacket(getLoopbackData(TCPConstant.MSG_ID_PUSH_MESSAGE, msgId));
//                }
//DELETE END 170629

                //抛出消息
                onMessage(data, msgId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSocketDisconnectNormally() {

    }
}
