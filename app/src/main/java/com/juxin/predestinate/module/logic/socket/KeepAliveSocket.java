package com.juxin.predestinate.module.logic.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.juxin.library.log.PLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.SocketFactory;

/**
 * socket通信内核
 * Created by sks on 2017/4/20.
 */
public class KeepAliveSocket {

    private final int SOCKET_PACKET_QUEUE_SIZE = 500;
    private Socket socket;
    //    private Condition socketConnectCondition;
    private volatile SocketState state = SocketState.INVALID;
    private SocketAddress address;
    private Executor singleDispatchExecutor = Executors.newSingleThreadExecutor();
    private Executor singleSendErrorExecutor = Executors.newSingleThreadExecutor();

    private InputStream input;
    private OutputStream output;
    private PacketWriter packetWriter;
    private PackerReader packerReader;
    private String host;
    private int port;

    private boolean wantClose = false;

    private SocketConnectionListener listener;

    private void setState(SocketState newState) {
        if (state == newState) {
            return;
        }

        state = newState;

        switch (newState) {
            case CONNECTING:
                PLogger.d("Socket connecting");
                if (listener != null) {
                    listener.onSocketConnecting();
                }
                break;
            case CONNECTED_SUCCESS:
                PLogger.d("Socket connect success");
                if (listener != null) {
                    listener.onSocketConnected();
                }
                break;
            case CONNECTED_FAILED:
                PLogger.d("Socket connect fail");
                if (listener != null) {
                    listener.onSocketConnectError();
                }
                break;
            case DISCONNECTING:
                PLogger.d("Socket disconnecting");
                break;
            case DISCONNECT_ERROR:
                PLogger.d("Socket disconnect error");
                if (listener != null) {
                    listener.onSocketDisconnectByError();
                }
                break;
            case DISCONNECT_NORMAL:
                PLogger.d("Socket disconnect normal");
                if (listener != null) {
                    listener.onSocketDisconnectNormally();
                }
                break;
            default:
                break;
        }
    }

    public KeepAliveSocket(String host, int port) {
        this.host = host;
        this.port = port;
//        socketConnectCondition = socketStateLock.newCondition();
    }

    public void setSocketStateListener(SocketConnectionListener listener) {
        this.listener = listener;
    }

    public void connect() {
//        checkAndWaitForConnect();
        if (state == SocketState.CONNECTING || state == SocketState.CONNECTED_SUCCESS) {
            PLogger.d("Socket already connected");
            return;
        }

        setState(SocketState.CONNECTING);

        try {
            address = new InetSocketAddress(host, port);
//            socketStateLock.lock();
            socket = SocketFactory.getDefault().createSocket();
            socket.connect(address, TCPConstant.SOCKET_CONNECT_TIMEOUT);
            input = socket.getInputStream();
            output = socket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
            setState(SocketState.CONNECTED_FAILED);
            return;
        }

        if (packerReader == null) {
            packerReader = new PackerReader();
            packerReader.init(this);
        }

        if (packetWriter == null) {
            packetWriter = new PacketWriter();
            packetWriter.init(this);
        }

        setState(SocketState.CONNECTED_SUCCESS);
    }


    public void sendPacket(NetData data) {
        if (packetWriter == null) {
            PLogger.d("not connected or writer not ready?");
            if (listener != null) {
                listener.onSendPacketError(state, data);
            }
            return;
        }
        packetWriter.sendPacket(data);
    }

    //ture正常关闭，false非正常关闭
    public void disconnect(boolean instant) {
        wantClose = instant;

        if (state != SocketState.CONNECTING && state != SocketState.CONNECTED_SUCCESS) {
            return;
        }

        setState(SocketState.DISCONNECTING);

        PLogger.d("Socket disconnect by instant " + instant + " start");
        if (packetWriter != null) {
            packetWriter.shutdown(instant);
        }

        if (packerReader != null) {
            packerReader.shutdown(instant);
        }
    }

    private void check2Close() {
        if (packerReader != null) {
            return;
        }

        if (packetWriter != null) {
            return;
        }

        PLogger.d("Socket close");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (wantClose) {
            setState(SocketState.DISCONNECT_NORMAL);
        } else {
            setState(SocketState.DISCONNECT_ERROR);
        }
    }

    private void onReadClose() {
        PLogger.d("Socket reader close");
        packerReader = null;
        check2Close();
    }

    private void onWriterClose() {
        PLogger.d("Socket writer close");
        packetWriter = null;
        check2Close();
    }

    private void onIOError() {
        disconnect(false);
    }

    private final int READER_CLOSE = 1;
    private final int WRITER_CLOSE = 2;
    private final int IO_ERROR = 3;

    private class SocketIoHandler extends Handler {
        private KeepAliveSocket client;

        public SocketIoHandler(KeepAliveSocket client) {
            this.client = client;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case READER_CLOSE:
                    client.onReadClose();
                    break;
                case WRITER_CLOSE:
                    client.onWriterClose();
                    break;
                case IO_ERROR:
                    client.onIOError();
                default:
                    break;
            }
        }
    }

    private class SubTread extends Thread {
        public SocketIoHandler handler;
        private KeepAliveSocket client;
        private ThreadRunner runner;

        public SubTread(KeepAliveSocket client, ThreadRunner runner) {
            this.client = client;
            this.runner = runner;
        }

        @Override
        public void run() {
            Looper.prepare();
            handler = new SocketIoHandler(client);

            super.run();
            runner.run();

            Looper.loop();
        }
    }

    private interface ThreadRunner {
        public void run();
    }


    private class PacketWriter implements ThreadRunner {
        private final ArrayBlockingQueue<NetData> queue = new ArrayBlockingQueue(SOCKET_PACKET_QUEUE_SIZE, true);
        private volatile boolean endWithException = false;

        private boolean isShutDown = false;

        private SubTread writerThread;

        public PacketWriter() {
//            ownerHandler = handler;
//            shutDownCondition = shutDownLock.newCondition();
        }

        @Override
        public void run() {
            writePacket();
        }

        public void init(KeepAliveSocket client) {
            endWithException = false;

            writerThread = new SubTread(client, this);
            writerThread.start();
            PLogger.d("Socket send packet thread init and start");
        }

        protected void sendPacket(final NetData data) {
            try {
                queue.put(data);
            } catch (Exception e) {
                if (listener != null) {
                    singleSendErrorExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSendPacketError(state, data);
                        }
                    });
                }
                PLogger.d("Socket send packet add to queue error:" + e.getMessage());
            }
        }

        private void writePacket() {
            NetData packet = null;
            //loop
            try {
                while (!done()) {
                    packet = queue.take();
                    if (packet == null) {
                        Thread.sleep(100);
                        continue;
                    }
                    PLogger.d("Socket send packet :" + packet.toString());

                    byte[] datas = packet.getBytes();

                    output.write(datas);
                    output.flush();
                    packet = null;
                }
            } catch (InterruptedException e) {
                PLogger.d("Socket send packet ,take packet from queue interrupt");
            } catch (IOException e) {
                final NetData errorPacket = packet;
                PLogger.d("Socket send packet error:" + e.getMessage() + ", packet:" + packet != null ? packet.toString() : "none");
                if (listener != null && errorPacket != null) {
                    singleSendErrorExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSendPacketError(state, errorPacket);
                        }
                    });
                }
                endWithException = true;
            }

            //fail left msg
            do {
                packet = queue.poll();
                if (packet == null) {
                    break;
                }

                PLogger.d("fail send left packet :" + packet.toString());
                final NetData errorPacket = packet;
                if (listener != null && errorPacket != null) {
                    singleSendErrorExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSendPacketError(state, errorPacket);
                        }
                    });
                }
            } while (true);

            //注意消息顺序
            if (endWithException) {
                writerThread.handler.sendEmptyMessage(IO_ERROR);
            }
            writerThread.handler.sendEmptyMessage(WRITER_CLOSE);


            PLogger.d("Socket send packet thread shutdown done ,instant with exception:" + endWithException);
        }

        public boolean isEndWithException() {
            return endWithException;
        }

        private boolean done() {
            return isShutDown;
        }

        private void shutdown(boolean instant) {
            if (isShutDown) {
                return;
            }
            PLogger.d("Socket send packet thread shutdown instant:" + instant + " start");
            isShutDown = true;
            writerThread.interrupt();
            PLogger.d("Socket send packet thread shutdown instant:" + instant + " end");
        }
    }

    public void  reSetHostPort(String hosts,int port){
        PLogger.d("reSetHostPort:"+hosts+"-->"+port);
        TCPConstant.HOST=hosts;
        TCPConstant.PORT=port;
        this.host=hosts;
        this.port=port;
    }

    private class PackerReader implements ThreadRunner {
        private final ArrayBlockingQueue<NetData> queue = new ArrayBlockingQueue(SOCKET_PACKET_QUEUE_SIZE, true);

        private volatile boolean endWithException = false;

        private boolean isShutDown = false;

        private SubTread readThread;

        public PackerReader() {
//            shutDownCondition = shutDownLock.newCondition();
//            ownerHandler = handler;
        }

        @Override
        public void run() {
            readPacket();
        }

        protected void init(KeepAliveSocket client) {
            isShutDown = false;
            endWithException = false;

            readThread = new SubTread(client, this);
            readThread.start();
            PLogger.d("Socket read packet thread init and start");
        }

        private void readPacket() {
            try {
                while (!done()) {
                    PLogger.d("Socket read packet thread start read");
                    NetData data = NetData.parseNetData(input);
                    if (data != null) {
                        PLogger.d("Socket read packet dispatch packet:" + data.toString());
                        dispatchPacket(data);
                    } else {
                        PLogger.d("Socket read packet dispatch packet null");
                    }
                    Thread.sleep(15);
                }
            } catch (IOException e) {
                if (!isShutDown) {
                    endWithException = true;
                    PLogger.d("Socket read packet error:" + e.getMessage());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //注意消息顺序
            if (endWithException) {
                readThread.handler.sendEmptyMessage(IO_ERROR);
            }
            readThread.handler.sendEmptyMessage(READER_CLOSE);

            PLogger.d("Socket read packet thread shutdown done with exception:" + endWithException);
        }

        public boolean isEndWithException() {
            return endWithException;
        }

        private boolean done() {
            return isShutDown;
        }

        private void shutdown(boolean instant) {
            if (isShutDown) {
                return;
            }
            PLogger.d("Socket read packet thread shutdown instant:" + instant + " start");
//            shutDownLock.lock();
            isShutDown = true;
            try {
                PLogger.d("Socket read packet thread shutdown wait for done start");
                input.close();
                readThread.interrupt();
                PLogger.d("Socket read packet thread shutdown wait for done end");

            } catch (IOException e) {
                e.printStackTrace();
            }
            PLogger.d("Socket read packet thread shutdown instant:" + instant + " end");
//            shutDownLock.unlock();

        }
    }

    private void dispatchPacket(final NetData data) {
        if (listener != null) {
            singleDispatchExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    listener.onReceivePacket(data);
                }
            });
        }
    }

    public SocketState getSocketState() {
        return state;
    }

    public enum SocketState {
        INVALID,
        CONNECTING,
        CONNECTED_SUCCESS,
        CONNECTED_FAILED,
        DISCONNECTING,
        DISCONNECT_NORMAL,
        DISCONNECT_ERROR
    }

    public interface SocketConnectionListener {
        void onSocketConnected();

        void onSocketConnecting();

        void onSocketConnectError();

        void onSendPacketError(SocketState state, NetData failedData);

        void onSocketDisconnectByError();

        void onReceivePacket(NetData data);

        void onSocketDisconnectNormally();
    }


}
