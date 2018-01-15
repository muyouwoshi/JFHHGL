package com.juxin.library.request;

import com.juxin.library.log.PLogger;
import com.juxin.library.observe.RxBus;
import com.juxin.library.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * retrofit文件下载回调处理，实现内容与{@link FileObserver}保持一致
 *
 * @author zrp
 * @date 2016/11/22
 */
public class FileCallback implements Callback<ResponseBody> {

    /**
     * 订阅下载进度
     */
    private CompositeDisposable rxDisposable = new CompositeDisposable();
    /**
     * 文件下载地址
     */
    private String url;
    /**
     * 目标文件存储的文件夹路径
     */
    private String filePath;
    /**
     * 下载监听
     */
    private DownloadListener listener;
    /**
     * 当前的下载进度
     */
    private int downloadProcess;

    public FileCallback(String url, String filePath, DownloadListener listener) {
        this.url = url;
        this.filePath = filePath;
        this.listener = listener;

        if (listener != null) {
            listener.onStart(url, filePath);
        }
        subscribeLoadProgress();// 订阅下载进度
    }

    @Override
    public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            // 切换到工作线程，防止大文件下载时OOM。须保证所有listener回调均在主线程
            rxDisposable.add(Flowable.create(new FlowableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull FlowableEmitter<Boolean> e) throws Exception {
                    e.onNext(saveFile(response.body()));
                    e.onComplete();
                }
            }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean isDownloadSuccess) throws Exception {
                    if (listener != null) {
                        if (isDownloadSuccess) {
                            listener.onSuccess(url, filePath);
                        } else {
                            listener.onFail(url, new Throwable("IO exception."));
                        }
                    }
                    unSubscribe();//文件处理完成且回调成功之后解绑
                }
            }));
        } else if (listener != null) {
            listener.onFail(url, new Throwable("Net or server error."));
            unSubscribe();//下载出错直接解绑
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        PLogger.printThrowable(t);
        if (listener != null) {
            listener.onFail(url, t);
        }
    }

    /**
     * 保存文件[先临时命名，下载完成之后重命名，防止下载失败之后文件被占用]
     *
     * @param responseBody 下载流
     * @return 是否保存成功
     */
    private boolean saveFile(ResponseBody responseBody) {
        String tempPath = FileUtil.getFilePath(filePath) + File.separator + System.nanoTime();
        boolean isWriteSuccess = true;

        File file = null;
        InputStream in = null;
        FileOutputStream out = null;
        byte[] buf = new byte[2048];
        int len;
        try {
            file = new File(tempPath);
            if (file.exists()) {
                file.delete();
            }

            in = responseBody.byteStream();
            out = new FileOutputStream(file);

            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            isWriteSuccess = false;
            PLogger.printThrowable(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    PLogger.printThrowable(e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    PLogger.printThrowable(e);
                }
            }
        }
        return file != null && isWriteSuccess && file.renameTo(new File(filePath));
    }

    /**
     * 订阅文件下载进度
     */
    private void subscribeLoadProgress() {
        downloadProcess = 0;
        rxDisposable.add(RxBus.getInstance().toFlowable(FileLoadingBean.class)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FileLoadingBean>() {
                    @Override
                    public void accept(FileLoadingBean fileLoadingBean) throws Exception {
                        if (url == null || fileLoadingBean == null || !url.equals(fileLoadingBean.getUrl())) {
                            return;
                        }
                        if (fileLoadingBean.getTotal() == 0) {
                            return;
                        }

                        int process = (int) ((100 * fileLoadingBean.getProgress()) / fileLoadingBean.getTotal());
                        if (process > downloadProcess) {
                            downloadProcess = process;
                            if (listener != null) {
                                // 下载中
                                listener.onProcess(url, process, fileLoadingBean.getTotal());
                            }
                        }
                    }
                }));
    }

    /**
     * 取消订阅，防止内存泄漏
     */
    private void unSubscribe() {
        if (!rxDisposable.isDisposed()) {
            rxDisposable.dispose();
        }
    }
}
