package com.juxin.predestinate.module.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Think on 2015/10/20.
 */
public class ApkUnit {
    //从一个apk文件中读取包名
    public static String GetPackagenameFromFile(Context context, String sFile) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(sFile, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return info.packageName;
        }
        return null;
    }

    //运行Apk
    public static void ExecApkFile(Context context, String sFile) {
        try {
            if ((context == null) || TextUtils.isEmpty(sFile))
                return;
            File file = new File(sFile);
            ExecApkFile(context, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //运行Apk
    public static void ExecApkFile(Context context, File aFile) {
        try {

            if ((context == null) || (aFile == null))
                return;
            if (!aFile.exists() || !aFile.isFile() || (aFile.length() == 0))
                return;

//            if (getAppIsInstall(context, GetPackagenameFromFile(context, aFile.getPath()))) {
//                return;
//            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(aFile),
                    "application/vnd.android.package-archive");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //判断APP是否安装过
    public static boolean getAppIsInstall(Context context, String sPkgName) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(sPkgName))
                return true;
        }
        return false;
    }

    //获取apk versionCode
    public static int getInstallAppVer(Context context, String pkgName) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(pkgName))
                return pinfo.get(i).versionCode;
        }
        return -1;
    }

    /*
    * App 现在的运行状态 0，没运行，1前台，2后台
     */
    public static int GetAppRunStatus(Context context, String sPkgName) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if ((sPkgName + "").equals("")) {
            sPkgName = context.getPackageName();
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(sPkgName)) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return 2;
                } else {
                    return 1;
                }
            }

        }
        return 0;
    }

    //根据名名运行一个app
    public static void RunApp(Context context, String sPkgName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(sPkgName);
        if (intent != null) {
            context.startActivity(intent);
        }
    }

    //获取前台正在运行的app的名字
    public static String GetNowAppName(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName cn = runningTaskInfos.get(0).topActivity;
            String sPkgName = cn.getPackageName();
            List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
            for (PackageInfo pkInfo : packages) {
                if ((pkInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    if (pkInfo.packageName.equals(sPkgName)) {
                        String Tmp = pkInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                        return Tmp;
                    }
                }
            }
        }
        return "";
    }

    //获取前台正在运行的app的pkgName
    public static String GetNowAppPkgName(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName cn = runningTaskInfos.get(0).topActivity;
            String sPkgName = cn.getPackageName();
            return sPkgName;
        }
        return "";
    }

    //判断自己在不在前台
    public static boolean IsRunningForegroundMe(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
            return true;
        }
        return false;
    }

    public static boolean FileExists(String sFile) {
        return new File(sFile).exists();
    }

    public static boolean FileDelete(String sFile) {
        try {
            File f = new File(sFile);
            if (f.exists()) {
                return f.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getFilenameFromUrl(String url) {
        String result = "";
        try {
            URL u = new URL(url);
            result = u.getFile();

            int iP = result.indexOf("?");
            if (iP >= 0) {
                iP = iP >= 0 ? iP : result.length();
                result = result.substring(0, iP);
            }

            iP = result.lastIndexOf("/");
            if (iP >= 0) {
                iP = iP >= 0 ? iP : 0;
                result = result.substring(iP + 1, result.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static HashMap<String, String> mapUrlDown = new HashMap<String, String>();

//    public static boolean downApp(Context context, final String url) {
//        return downApp(context, url, null);
//    }

//    public static boolean downApp(final Context context, final String url, final String sCallback) {
//        String aApkFile = mapUrlDown.get(url);
//
//        if (!TextUtils.isEmpty(aApkFile)) {
//            if (aApkFile.endsWith(".tmp")) {
//               PToast.showShort("下载中，请稍侯...");
//                return true;
//            }
//            if (aApkFile.endsWith(".apk")) {
//                if (FileExists(aApkFile)) {
//                    ApkUnit.ExecApkFile(App.context, aApkFile);
//                    return true;
//                } else {
//                    mapUrlDown.remove(url);
//                }
//            }
//        }
//
//        aApkFile = getFilenameFromUrl(url);
//
//        if (TextUtils.isEmpty(aApkFile) || !aApkFile.endsWith(".apk"))
//            aApkFile = Long.toString(new Date().getTime());
//
//        if (!aApkFile.endsWith(".apk"))
//            aApkFile = aApkFile + ".apk";
//
//        aApkFile = Common.getCahceDir("apk") + aApkFile;
//
//        mapUrlDown.put(url, aApkFile + ".tmp");
//        PToast.showShort("下载中，请稍侯...");
//
//        FileDelete(aApkFile);
//        FileDelete(aApkFile + ".tmp");
//
//        final SessionTaskDownload download = new SessionTaskDownload();
//        download.setDowloadInfo(url, aApkFile, "", new NetInterface.OnTransmissionListener() {
//            final ProgressDialog dialog = new ProgressDialog(context);
//
//            @Override
//            public void onProcess(String s, int i, long l) {
////                T.showShort(AppCtx.context, "已下载" + i + "%");
//                dialog.setProgress(i);
//            }
//
//            @Override
//            public void onStart(String s, String s1) {
//                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//                lp.alpha = 0.6f;
//                dialog.getWindow().setAttributes(lp);
//                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
//                dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
//                dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
//                dialog.setMessage("下载进度");
//                dialog.setMax(100);
//                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//                        download.cancel(true);
//                    }
//                });
//                dialog.show();
//            }
//
//            @Override
//            public void onStop(String s, String s1, int i) {
//                try {
//                    dialog.dismiss();
//
//                    if (TextUtils.isEmpty(s1) || !FileExists(s1)) {
//                        mapUrlDown.remove(s);
//                        FileDelete(s1);
//                        PToast.showShort( "下载失败");
//                        return;
//                    }
//                    mapUrlDown.put(s, s1);
//                    if (!TextUtils.isEmpty(sCallback)) {
//                        SessionTaskDownload download = new SessionTaskDownload();
//                        download.setDowloadInfo(sCallback, null, null, null, false);
//                        download.execute();
//                    }
//                    ApkUnit.ExecApkFile(App.context, s1);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, true);
//        download.execute();
//
//        return true;
//    }
}
