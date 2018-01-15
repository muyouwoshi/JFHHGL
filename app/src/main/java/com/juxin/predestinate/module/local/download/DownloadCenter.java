package com.juxin.predestinate.module.local.download;

import com.juxin.library.log.PSP;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 新的下载中心
 *
 * @author Mr.Huang
 * @date 2017/11/9
 */
public class DownloadCenter {

    private static volatile DownloadCenter singleton;
    private Thread[] threads = new Thread[3];
    private Vector<DownloadInfo> infos = new Vector<>();
    private Map<DownloadInfo, List<DownloadListener>> listenerMap = new WeakHashMap<>();
    private AtomicInteger ai = new AtomicInteger(0);

    public static DownloadCenter getInstance() {
        if (singleton == null) {
            synchronized (DownloadCenter.class) {
                if (singleton == null) {
                    singleton = new DownloadCenter();
                }
            }
        }
        return singleton;
    }

    public DownloadCenter addDownload(DownloadInfo info, DownloadListener listener) {
        if (!infos.contains(info)) {
            infos.add(info);
        }
        addDownloadListener(info, listener);
        return this;
    }

    /**
     * 紧急下载
     *
     * @param info
     * @param listener
     */
    public void startUrgentDownload(final DownloadInfo info, final DownloadListener listener) {
        for (int i = 0; i < threads.length; i++) {
            Thread thread = threads[i];
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
        }
        addDownloadListener(info, listener);
        Thread thread = new Thread() {
            @Override
            public void run() {
                ai.incrementAndGet();
                download(info);
                listenerMap.remove(info);
                if (ai.decrementAndGet() == 0) {
                    DownloadCenter.this.start();
                }
            }
        };
        thread.start();
    }

    public void start() {
        for (int i = 0; i < threads.length; i++) {
            Thread thread = threads[i];
            if (thread == null || !thread.isAlive()) {
                thread = new Thread(new DownloadTask());
                threads[i] = thread;
                thread.start();
            }
        }
    }

    public void removeListener(DownloadInfo info, DownloadListener listener) {
        if (listener == null) {
            return;
        }
        List<DownloadListener> listeners = listenerMap.get(info);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    private class DownloadTask implements Runnable {

        @Override
        public void run() {
            while (infos.size() > 0) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                DownloadInfo info = infos.remove(0);
                save(info);
                download(info);
            }
        }
    }

    private void download(DownloadInfo info) {
        HttpURLConnection conn = null;
        RandomAccessFile file = null;
        InputStream stream = null;
        try {
            onDownload(info, DownloadInfo.STATUS_START);
            URL url = new URL(info.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setConnectTimeout(3000);
            File source = new File(info.getPath());
            if (source.exists() && source.length() > 0) {
                info.setLength(source.length());
                conn.addRequestProperty("Range", "bytes=" + source.length() + "-");
            } else {
                conn.addRequestProperty("Range", "bytes=0-");
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == 206) {
                info.setTotal(conn.getContentLength() + info.getLength());
                file = new RandomAccessFile(info.getPath(), "rwd");
                file.seek(file.length());
                stream = conn.getInputStream();
                byte[] bytes = new byte[2048];
                int len;
                info.setStatus(DownloadInfo.STATUS_DOWNLOADING);
                save(info);
                while ((len = stream.read(bytes)) > 0) {
                    if (info.isStop()) {
                        break;
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        infos.add(info);
                        break;
                    }
                    file.write(bytes, 0, len);
                    info.appendLength(len);
                    onDownload(info, DownloadInfo.STATUS_DOWNLOADING);
                    if (info.isStop()) {
                        break;
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        infos.add(info);
                        break;
                    }
                }
            }
            if (info.isStop() || Thread.currentThread().isInterrupted()) {
                onDownload(info, DownloadInfo.STATUS_STOP);
                info.setStatus(DownloadInfo.STATUS_STOP);
            } else {
                onDownload(info, DownloadInfo.STATUS_SUCCESS);
                info.setStatus(DownloadInfo.STATUS_SUCCESS);
            }
            save(info);
            listenerMap.remove(info);
        } catch (Exception e) {
            onDownload(info, DownloadInfo.STATUS_FAIL);
            info.setStatus(DownloadInfo.STATUS_FAIL);
            save(info);
            listenerMap.remove(info);
        } finally {
            close(file);
            close(stream);
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void addDownloadListener(DownloadInfo info, DownloadListener listener) {
        if (listener == null) {
            return;
        }
        List<DownloadListener> listeners = listenerMap.get(info);
        if (listeners == null) {
            listeners = new ArrayList<>();
            listeners.add(listener);
            listenerMap.put(info, listeners);
        } else if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    private void onDownload(DownloadInfo info, int status) {
        List<DownloadListener> listeners = listenerMap.get(info);
        if (listeners != null) {
            for (DownloadListener listener : listeners) {
                listener.onDownload(info, status);
            }
        }
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void save(DownloadInfo info) {
        PSP.getInstance().remove(info.getUrl());
        PSP.getInstance().put(info.getUrl(), info.toJSONString());
    }
}