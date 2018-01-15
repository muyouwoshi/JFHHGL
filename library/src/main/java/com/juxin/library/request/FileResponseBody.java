package com.juxin.library.request;

import com.juxin.library.observe.RxBus;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

/**
 * 文件下载ResponseBody
 * Created by zrp on 2016/11/22.
 */
public class FileResponseBody extends ResponseBody {

    Response originalResponse;
    String url;

    public FileResponseBody(Response originalResponse, String url) {
        this.originalResponse = originalResponse;
        this.url = url;
    }

    @Override
    public MediaType contentType() {
        return originalResponse.body().contentType();
    }

    @Override
    public long contentLength() {
        return originalResponse.body().contentLength();
    }

    @Override
    public BufferedSource source() {
        return Okio.buffer(new ForwardingSource(originalResponse.body().source()) {
            long bytesReaded = 0;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                bytesReaded += bytesRead == -1 ? 0 : bytesRead;
                RxBus.getInstance().post(new FileLoadingBean(contentLength(), bytesReaded, url));
                return bytesRead;
            }
        });
    }
}
