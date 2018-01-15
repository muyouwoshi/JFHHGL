package com.juxin.predestinate.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import com.juxin.library.dir.DirType;
import com.juxin.library.log.PSP;
import com.juxin.library.utils.TimeBaseUtil;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Think on 2017/5/2.
 */

public class Video {

    public static String getVideoThumb(String path) {
        String newPath = getVideoPath();
        copyFile(path, newPath);
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(newPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        String pic = getPicPath();
        File file = new File(pic);
        if (file.exists())
            file.delete();
        pic = getSavePicPath();
        file = new File(pic);
        if (file.exists())
            file.delete();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            return null;
        }
        return file.getAbsolutePath();
    }

    public static String getPicPath() {
        return PSP.getInstance().getString("VideoThumbUrl" + ModuleMgr.getCenterMgr().getMyInfo().getUid(), "0");
    }

    public static String getSavePicPath() {
        String strUrl = DirType.getImageDir() + TimeBaseUtil.getFormatTimeFileName() + ".jpg";
        PSP.getInstance().put("VideoThumbUrl" + ModuleMgr.getCenterMgr().getMyInfo().getUid(), strUrl);
        return strUrl;
    }

    public static String getVideoPath() {
        return DirType.getVideoDir() + ModuleMgr.getCenterMgr().getMyInfo().getUid() + ".mp4";
    }

    public static boolean isHaveVideo() {
        return new File(getVideoPath()).exists();
    }

    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File file = new File(newPath);
            if (file.exists())
                file.delete();
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }

    public static void videoPlay(Context context) {
        Uri uri = Uri.parse(getVideoPath());
        //调用系统自带的播放器
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "video/mp4");
        context.startActivity(intent);
    }
}
