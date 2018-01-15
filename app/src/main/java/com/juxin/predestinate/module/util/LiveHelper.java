package com.juxin.predestinate.module.util;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;

import com.juxin.library.dir.DirType;
import com.juxin.library.request.DownloadListener;
import com.juxin.library.utils.APKUtil;
import com.juxin.library.utils.FileUtil;
import com.juxin.library.utils.JniUtil;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.model.impl.HttpMgrImpl;
import com.juxin.predestinate.module.logic.model.mgr.HttpMgr;
import com.juxin.predestinate.ui.utils.DownloadPluginFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.juxin.predestinate.module.logic.application.App.context;

/**
 * 直播相关功能
 *
 * @author gwz
 */
public class LiveHelper {

    private static DownloadPluginFragment downloadPluginFragment = new DownloadPluginFragment();
    private static boolean isDownloading = false;

    /**
     * 打开直播间
     *
     * @param anchorId 主播UID
     * @param videoUrl 视频流地址
     * @param imgUrl   封面
     * @param downUrl  下载地址
     * @param pkg      直播app包名
     * @param cls      直播app入口
     */
    public static void openLiveRoom(String anchorId, String videoUrl, String imgUrl, String downUrl, String pkg, String cls) {
        if (!APKUtil.isAppInstalled(App.context, pkg)) {
            saveLiveInfo(anchorId, videoUrl, imgUrl, downUrl);
            return;
        }
        // 根据当前用户性别判断打开女性直播页面还是打开去直播页面
        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        ComponentName componetName = new ComponentName(pkg, cls);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        Bundle bundle = new Bundle();
        bundle.putString("type", "5");
        intent.putExtras(bundle);
        intent.putExtra("anchor_id", anchorId);
        intent.putExtra("video_url", videoUrl);
        intent.putExtra("image_url", imgUrl);
        intent.putExtra("uid", "yf" + userDetail.getUid());
        intent.putExtra("head_url", userDetail.getAvatar());
        intent.putExtra("sex", userDetail.getGender() == 2 ? "0" : "1");//隔壁APP 字符串："0"是女性，"1"是男性
        intent.putExtra("password", ModuleMgr.getLoginMgr().getAuth());
        intent.setComponent(componetName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 保存插件调用信息并进行插件下载
     *
     * @param anchorId 主播UID
     * @param videoUrl 视频流地址
     * @param imgUrl   封面图片
     * @param downUrl  插件下载地址
     */
    private static void saveLiveInfo(String anchorId, String videoUrl, String imgUrl, String downUrl) {
        File file = new File(DirType.getCacheDir() + "live.json");
        JSONObject jo = new JSONObject();
        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        try {
            jo.put("anchor_id", anchorId);
            jo.put("video_url", videoUrl);
            jo.put("image_url", imgUrl);
            jo.put("uid", "yf" + userDetail.getUid());
            jo.put("head_url", userDetail.getAvatar());
            jo.put("sex", userDetail.getGender() == 2 ? "0" : "1");//隔壁APP 字符串："0"是女性，"1"是男性
            jo.put("password", JniUtil.GetEncryptString(ModuleMgr.getLoginMgr().getUserList().get(0).getPw()));
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jo.toString().getBytes());
            fos.flush();
            fos.close();
            downLoadPlugin(downUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载插件
     *
     * @param downUrl
     */
    private static void downLoadPlugin(String downUrl) {
        if (!downloadPluginFragment.isAdded()) {
            downloadPluginFragment.setLiveStyle(true);
            downloadPluginFragment.show((FragmentActivity) App.getActivity());
        }
        if (isDownloading) return;

        isDownloading = true;
        HttpMgr httpMgr = new HttpMgrImpl();
        String apkFile = DirType.getApkDir() + FileUtil.getFileNameFromUrl(downUrl);//AppCfg.ASet.getVideo_chat_apk_url()
        httpMgr.download(downUrl, apkFile, new DownloadListener() {
            @Override
            public void onStart(String url, String filePath) {
                isDownloading = true;
            }

            @Override
            public void onProcess(String url, int process, long size) {
                if (downloadPluginFragment.isAdded() && downloadPluginFragment.isVisible())
                    downloadPluginFragment.updateProgress(process);
            }

            @Override
            public void onSuccess(String url, String filePath) {
                isDownloading = false;
                downloadPluginFragment.dismissAllowingStateLoss();
                APKUtil.installAPK(context, filePath);
            }

            @Override
            public void onFail(String url, Throwable throwable) {
                isDownloading = false;
                downloadPluginFragment.dismissAllowingStateLoss();
            }
        });
    }

    public static void readLiveInfo() {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "xiaou" + File.separator + "cache" + File.separator + "live.json";
        File file = new File(filePath);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[8192];
            int count = 0;
            StringBuilder sb = new StringBuilder();
            while ((count = fis.read(data)) != -1) {
                sb.append(new String(data, 0, count));
            }
            JSONObject jo = new JSONObject(sb.toString());
            long anchorId = jo.getLong("anchor_id");
            String imageUrl = jo.getString("image_url");
            String uid = jo.getString("uid");
            String headUrl = jo.getString("head_url");
            int sex = jo.getInt("sex");
            String pwd = jo.getString("password");
            System.out.println("read json:" + jo.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getIntentData(Intent intent) {
        long anchorId = intent.getLongExtra("anchor_id", -1);
        String imageUrl = intent.getStringExtra("image_url");
        String uid = intent.getStringExtra("uid");
        String headUrl = intent.getStringExtra("head_url");
        int sex = intent.getIntExtra("sex", 0);
        String pwd = intent.getStringExtra("password");
    }

}
