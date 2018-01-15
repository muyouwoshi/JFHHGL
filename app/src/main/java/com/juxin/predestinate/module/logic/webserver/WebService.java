package com.juxin.predestinate.module.logic.webserver;

import android.content.Context;
import android.webkit.MimeTypeMap;

import com.juxin.library.dir.DirType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * web轻量级服务
 * <p>
 * Created by Su on 2016/12/2.
 */

public class WebService extends NanoHTTPD {

    public enum FileLocation {
        FileInCard, FileInAssest
    }

    private FileLocation fileLocation;
    private Context context;
    private static WebService webService;

    public static WebService getInstance() {
        if (webService == null) {
            webService = new WebService(WebServiceConfig.port);
        }
        return webService;
    }

    public WebService(int port) {
        super(port);
    }

    public WebService(String hostname, int port) {
        super(hostname, port);
    }

    public void StartWebServiceForSDCard() {
        webService.setFileLocation(FileLocation.FileInCard);
        try {
            if (!isAlive()) {
                webService.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void StartWebServiceForAssets(Context context) {
        this.context = context;
        webService.setFileLocation(FileLocation.FileInAssest);
        try {
            webService.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {
        if (uri.equals("/") || uri.equals("")) {  // 无本地可加载文件
            String str = "<html><head><title>LocalHost Test</title></head><body>Hi Word! </body></html>";
            return new Response(Response.Status.OK, MIME_HTML, str);
        } else {
            String mimeType = getMimeTypeForFile(uri);
            try {
                InputStream filelocal = null;
                if (fileLocation == FileLocation.FileInCard) {
                    filelocal = new FileInputStream(DirType.getWebDir() + uri);
                } else if (fileLocation == FileLocation.FileInAssest) {
                    if (context == null) {
                        return new Response(Response.Status.CREATED, "text/*", "Content not init");
                    }
                    filelocal = context.getResources().getAssets().open(uri.substring(1));
                }

                if (filelocal == null) {
                    return new Response(Response.Status.CREATED, "text/*", "FileLocal not init success");
                }

                return new Response(Response.Status.OK, mimeType, filelocal);
            } catch (Exception e) {
                return new Response(Response.Status.INTERNAL_ERROR, "text/*", "Error: " + mimeType + "预加载错误");
            }
        }
    }


    public FileLocation getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(FileLocation fileLocation) {
        this.fileLocation = fileLocation;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void stop() {
        super.stop();
        webService = null;
    }

    // ************************************ mime type ****************************************************
    public static final String MIME_DEFAULT_BINARY = "application/octet-stream";

    private String getMimeTypeForFile(String uri) {
        int dot = uri.lastIndexOf('.');
        String mime = null;
        if (dot >= 0) {
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(uri.substring(dot + 1).toLowerCase());
        }
        return mime == null ? MIME_DEFAULT_BINARY : mime;
    }
}
