package com.juxin.predestinate.module.logic.media;

import android.text.TextUtils;

import com.juxin.library.observe.ModuleBase;
import com.juxin.library.utils.FileUtil;
import com.juxin.predestinate.bean.file.UpLoadResult;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 媒体文件管理类
 * Created by Su on 2017/4/12.
 */
public class MediaMgr implements ModuleBase {

    private ArrayList<String> mediaUrls = new ArrayList<>();  // 维护多文件上传地址
    private ArrayList<String> mediaFullUrls = new ArrayList<>();  // 维护多文件全称上传地址

    @Override
    public void init() {
    }

    @Override
    public void release() {
    }

    // ================================== 老缘分吧 =====================================

    /**
     * 所有 文件,图片,语音上传等
     *
     * @param uploadType 上传类型
     * @param url        文件地址
     */
    public void sendHttpFile(String uploadType, String url, RequestComplete complete) {
        sendHttpFile(uploadType, url, null, complete);
    }

    /**
     * 上传文件
     *
     * @param uploadType 头像avatar，大头像avatar_big, 相册photo, 语音voice, 自定义表情face
     *                   头像认证videochat，视频video
     * @param url        文件地址
     * @param url2       视频缩略图地址
     */
    private void sendHttpFile(String uploadType, final String url, String url2, final RequestComplete complete) {
        if (TextUtils.isEmpty(url)) return;
        File file = new File(url);
        if (!file.exists()) return;

        Map<String, File> uploadFile = new HashMap<>();
        uploadFile.put("file_name", file);

        Map<String, Object> postParams = new HashMap<>();
        postParams.put("type", uploadType);
        postParams.put("uid", App.uid);

        ModuleMgr.getHttpMgr().uploadFile(UrlParam.uploadFile, postParams, uploadFile, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (complete != null)
                    complete.onRequestComplete(response);
            }
        });
    }

    /**
     * 多文件上传
     *
     * @param uploadType 上传类型
     * @param index      上传指定位置文件
     * @param complete   完成回调
     * @param url        文件地址
     */
    public void sendHttpMultiFiles(final String uploadType, int index, final OnMultiFilesUploadComplete complete, final String... url) {
        if (mediaUrls == null) mediaUrls = new ArrayList<>();
        if (mediaFullUrls == null) mediaFullUrls = new ArrayList<>();
        if (index >= url.length) {
            complete.onUploadComplete(mediaUrls);
            if(complete instanceof OnMFUFullComplete) {
                ((OnMFUFullComplete)complete).onFullComplete(mediaFullUrls);
            }
            mediaUrls.clear();
            mediaFullUrls.clear();
            return;
        }

        final int tempIndex = index + 1;
        if (FileUtil.isExist(url[index])) {  // 当文件存在时，才进行上传任务
            sendHttpFile(uploadType, url[index], new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    if (response.isOk()) {
                        UpLoadResult upLoadResult = (UpLoadResult) response.getBaseData();
                        String pic = upLoadResult.getFile_http_path();
                        if (!TextUtils.isEmpty(pic)) {
                            String post_url = ModuleMgr.getCenterMgr().getInterceptUrl(pic);
                            mediaFullUrls.add(pic);
                            mediaUrls.add(post_url);
                        }
                        sendHttpMultiFiles(uploadType, tempIndex, complete, url);
                    } else {
                        sendHttpMultiFiles(uploadType, tempIndex, complete, url);
                    }
                }
            });
        } else {
            sendHttpMultiFiles(uploadType, tempIndex, complete, url);  // 跳过不存在文件继续上传
        }
    }

    /**
     * 多文件上传结果回调
     */
    public interface OnMultiFilesUploadComplete {
        /**
         * @param mediaUrls 上传成功文件地址
         */
        void onUploadComplete(ArrayList<String> mediaUrls);
    }

    /**
     * 多文件上传结果回调 完整路径
     */
    public interface OnMFUFullComplete extends OnMultiFilesUploadComplete {
        void onFullComplete(ArrayList<String> mediaFullUrls);
    }


    // ================================ 小友上传文件 ====================================

    //    /**
//     * 所有 文件,图片,语音上传等
//     *
//     * @param sourceType 上传类型
//     * @param url        文件地址
//     */
//    public void sendHttpFile(int sourceType, String url, RequestComplete complete) {
//        sendHttpFile(sourceType, url, null, complete);
//    }
//
//    /**
//     * 身份认证： 图片，视频(暂时保留)
//     */
//    public void sendHttpFile(String url, RequestComplete complete) {
//        sendHttpFile(Constant.INT_CHAT_PIC, url, null, complete);
//    }
//
//    /**
//     * 给视频专用
//     */
//    public void sendHttpFileVideo(int sourceType, String url, String url2, RequestComplete complete) {
//        sendHttpFile(sourceType, url, url2, complete);
//    }
//
//    /**
//     * 多文件上传
//     *
//     * @param sourceType 上传类型
//     * @param index      上传指定位置文件
//     * @param complete   完成回调
//     * @param url        文件地址
//     */
//    public void sendHttpMultiFiles(final int sourceType, int index, final OnMultiFilesUploadComplete complete, final String... url) {
//        if (mediaUrls == null) mediaUrls = new ArrayList<>();
//        if (index >= url.length) {
//            complete.onUploadComplete(mediaUrls);
//            mediaUrls.clear();
//            return;
//        }
//
//        final int tempIndex = index + 1;
//        if (FileUtil.isExist(url[index])) {  // 当文件存在时，才进行上传任务
//            sendHttpFile(sourceType, url[index], new RequestComplete() {
//                @Override
//                public void onRequestComplete(HttpResponse response) {
//                    if (response.isOk()) {
//                        UpLoadResult upLoadResult = (UpLoadResult) response.getBaseData();
//                        String pic = upLoadResult.getHttpPathPic();
//                        if (!TextUtils.isEmpty(pic)) {
//                            String post_url = ModuleMgr.getCenterMgr().getInterceptUrl(pic);
//                            mediaUrls.add(post_url);
//                        }
//                        sendHttpMultiFiles(sourceType, tempIndex, complete, url);
//                    } else {
//                        sendHttpMultiFiles(sourceType, tempIndex, complete, url);
//                    }
//                }
//            });
//        } else {
//            sendHttpMultiFiles(sourceType, tempIndex, complete, url);  // 跳过不存在文件继续上传
//        }
//    }
//
//    /**
//     * 多文件上传结果回调
//     */
//    public interface OnMultiFilesUploadComplete {
//        /**
//         * @param mediaUrls 上传成功文件地址
//         */
//        void onUploadComplete(ArrayList<String> mediaUrls);
//    }
//
//    /**
//     * 上传文件
//     *
//     * @param sourceType 文件来源： 头像、聊天图片、集赞图片的区分，用于服务器判断长/短存储
//     * @param url        文件地址
//     * @param url2       视频缩略图地址
//     */
//    private void sendHttpFile(int sourceType, String url, String url2, RequestComplete complete) {
//        Map<String, File> uploadFile = new HashMap<>();
//        Map<String, Object> postParams = new HashMap<>();
//        if (TextUtils.isEmpty(url)) return;
//        postParams.put("type", sourceType);
//        switch (sourceType) {
//            case Constant.INT_AVATAR:
//            case Constant.INT_ALBUM:
//            case Constant.INT_CHAT_PIC:
//            case Constant.INT_PRAISE_PIC: {//图片
//                uploadImage(UrlParam.uploadFile, url, postParams, complete);
//                break;
//            }
//            case Constant.INT_CHAT_VOICE:
//            case Constant.INT_PRAISE_VOICE:
//            case Constant.INT_GAME_VOICE: {//语音
//                uploadFile.put("voice", new File(url));
//                ModuleMgr.getHttpMgr().uploadFile(UrlParam.uploadFile, postParams, uploadFile, complete);
//                break;
//            }
//            case Constant.INT_CHAT_VIDEO: {//视频
//                if (TextUtils.isEmpty(url2)) return;
//                uploadFile.put("video", new File(url));
//                uploadFile.put("thumb", new File(url2));
//                ModuleMgr.getHttpMgr().uploadFile(UrlParam.uploadFile, postParams, uploadFile, complete);
//                break;
//            }
//        }
//    }
//
//    /**
//     * 上传图片，内部处理图片压缩并删除临时压缩文件操作
//     */
//    private void uploadImage(UrlParam urlParam, final String uri, Map<String, Object> post_param, final RequestComplete logicCallBack) {
//        Map<String, File> uploadFile = new HashMap<>();
//        final String path = BitmapUtil.getSmallBitmapAndSave(uri, DirType.getUploadDir());
//        if (TextUtils.isEmpty(path)) {
//            PLogger.d("图片地址无效");
//            return;
//        }
//        uploadFile.put("pic", new File(path));
//        ModuleMgr.getHttpMgr().uploadFile(urlParam, post_param, uploadFile, new RequestComplete() {
//            @Override
//            public void onRequestComplete(HttpResponse response) {
//                if (logicCallBack != null) {
//                    logicCallBack.onRequestComplete(response);
//                }
//                FileUtil.deleteFile(path);// 删除缩略图
//            }
//        });
//    }
}
