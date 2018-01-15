package com.juxin.predestinate.module.local.album;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.juxin.library.log.PToast;
import com.juxin.library.dir.DirUtils;
import com.juxin.predestinate.bean.StackNode;
import com.juxin.predestinate.module.local.album.activity.TransferActivity;
import com.juxin.predestinate.module.local.album.help.AlbumHelper;
import com.juxin.predestinate.module.local.album.help.ImgConstant;
import com.juxin.predestinate.module.logic.application.App;

import java.util.List;

/**
 * 选取相片工具类  供外部模块调用相机使用
 * Created by Kind on 16/3/1.
 * ImgSelectUtil.getInstance().pickPhoto(getContext(), 1, new ImgSelectUtil.OnChooseCompleteListener() {
 *
 * @Override public void onComplete(String... path) {
 * MMLog.e("path==", path[0]+"");
 * }
 * });
 * <p>
 * ImgSelectUtil.getInstance().pickRadioPhoto(getContext(), new ImgSelectUtil.OnChooseCompleteListener() {
 * @Override public void onComplete(String... path) {
 * MMLog.e("path==", path[0]+"");
 * }
 * });
 */
public class ImgSelectUtil {
    private static ImgSelectUtil mInstance = null;
    private OnChooseCompleteListener onChooseCompleteListener; // 图片选择完成后回调接口

    public static ImgSelectUtil getInstance() {
        if (mInstance == null) {
            mInstance = new ImgSelectUtil();
        }
        return mInstance;
    }

    /**
     * 单选图片  有裁切
     */
    public void pickPhoto(Context context, OnChooseCompleteListener chooseCompleteListener) {
        pickPhoto(context, 1, chooseCompleteListener);
    }

    /**
     * 多选图片  有裁剪(目前可裁切均为单选，暂不支持多选裁切)
     *
     * @param totalNum 多选图片数量
     */
    public void pickPhoto(Context context, int totalNum, OnChooseCompleteListener chooseCompleteListener) {
        this.onChooseCompleteListener = chooseCompleteListener;
        Intent intent = new Intent(context, TransferActivity.class);
        StackNode.appendIntent(context, intent);
        intent.putExtra(ImgConstant.STR_NUMS, totalNum);
        AlbumHelper.getInstance().setIsCrop(true);
        context.startActivity(intent);
    }

    /**
     * 裁剪图片
     * @param clipType  类型(1 圆形可能还有漏掉的地方，请用pickPhoto方法；2 矩形)
     */
    public void cropType(Context context, int clipType, OnChooseCompleteListener chooseCompleteListener) {
        this.onChooseCompleteListener = chooseCompleteListener;
        Intent intent = new Intent(context, TransferActivity.class);
        intent.putExtra(ImgConstant.STR_NUMS, 1);
        intent.putExtra(ImgConstant.CROP_TYPE, clipType);
        AlbumHelper.getInstance().setIsCrop(true);
        context.startActivity(intent);
    }

    /**
     * 单选图片  无裁切
     */
    public void pickPhotoGallery(Context context, OnChooseCompleteListener chooseCompleteListener) {
        pickPhotoGallery(context, 1, chooseCompleteListener);
    }

    /**
     * 多选图片  无裁剪
     *
     * @param totalNum 多选图片数量
     */
    public void pickPhotoGallery(Context context, int totalNum, OnChooseCompleteListener chooseCompleteListener) {
        this.onChooseCompleteListener = chooseCompleteListener;
        Intent intent = new Intent(context, TransferActivity.class);
        intent.putExtra(ImgConstant.STR_NUMS, totalNum);
        AlbumHelper.getInstance().setIsCrop(false);
        context.startActivity(intent);
    }

    public void handlePickPhotoResult(List<String> data) {
        if (onChooseCompleteListener != null) {
            String[] strs = new String[data.size()];
            data.toArray(strs);
            onChooseCompleteListener.onComplete(strs);
        }
    }

    /**
     * 调用系统相机拍照
     * @param  frontCamera 是否默认前置摄像头
     */
    public void takePhoto(Activity activity, boolean frontCamera) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                if (!AlbumHelper.getInstance().hasCamera(App.getActivity())) {
                    PToast.showShort("启动相机失败!");
                    return;
                }
                // 创建保存图片文件
                AlbumHelper.getInstance().setPhotoUri(Uri.fromFile(DirUtils.createPictureFile("take")));
                // 准备intent，并 指定 新 照片 的文件名（photoUri）
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, AlbumHelper.getInstance().getPhotoUri());
                if (frontCamera)
                    intent.putExtra("camerasensortype", 2); // 调用前置摄像头
                // 启动拍照的窗体。并注册 回调处理。
                activity.startActivityForResult(intent, ImgConstant.CODE_FOR_TAKE_PIC);
            } catch (Exception e) {
                PToast.showShort("启动相机失败!");
                e.printStackTrace();
            } catch (Error e) {
                PToast.showShort("启动相机失败!");
            }
        } else {
            PToast.showShort("存储卡不可用!");
        }
    }


    /**
     * 调用TuSDK相机拍照
     */
//    public void takePhoto(final Activity activity) {
//        //初始化滤镜管理器
//        TuSdk.messageHub().setStatus(activity, R.string.lsq_initing);
//        TuSdk.checkFilterManager(new FilterManager.FilterManagerDelegate() {
//            @Override
//            public void onFilterManagerInited(FilterManager manager) {
//                TuSdk.messageHub().showSuccess(activity, R.string.lsq_inited);
//                new CameraComponentSample().showSample(activity);
//            }
//        });
//    }

    /**
     * 相册选择照片完成监听接口
     */
    public interface OnChooseCompleteListener {
        void onComplete(String... path);
    }
}