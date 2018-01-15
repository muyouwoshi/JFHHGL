package com.juxin.library.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.juxin.library.log.PLogger;

import java.io.File;
import java.util.List;

/**
 * APK操作相关的功能
 */
public class APKUtil {

    /**
     * 安装Apk包。
     *
     * @param context  上下文。
     * @param filePath Apk文件。
     * @return 无法正常启动安装则返回false。
     */
    public static boolean installAPK(Context context, String filePath) {
        File apkfile = new File(filePath);
        if (!apkfile.exists()) return false;

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }

        return false;
    }

    /**
     * 检测是否安装了App。
     *
     * @param packageName App包名。
     * @return 如果安装了则返回true。
     */
    public static boolean checkInstalled(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (packageInfo != null)
                return true;
        } catch (PackageManager.NameNotFoundException e) {
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 检测App是否需要升级。
     *
     * @param context     上下文。
     * @param packageName 包名。
     * @param newVerCode  新的版本号。
     * @return 0 最新版本；1 没有安装；2 升级
     */
    public static int checkUpdated(Context context, String packageName, int newVerCode) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);

            if (newVerCode > packageInfo.versionCode) {
                return 2;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return 1;
        } catch (Exception e) {
        }

        return 0;
    }

    /**
     * 打开应用
     *
     * @param context     上下文。
     * @param packageName 目标应用安装后的包名
     */
    public static boolean launchApp(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();

            // 获取目标应用安装包的Intent
            Intent intent = pm.getLaunchIntentForPackage(packageName);

            if (!(context instanceof Activity)) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }

        return false;
    }


    /**
     * 打开应用。
     *
     * @param context           上下文。
     * @param packageName       目标应用安装后的包名
     * @param activityClassName activity类名。
     */
    public static boolean launchApp(Context context, String packageName, String activityClassName) {
        try {
            Intent intent = new Intent();
            ComponentName cn = new ComponentName(packageName, activityClassName);
            intent.setComponent(cn);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }

        return false;
    }

    /**
     * 获取前台正在运行的app的pkgName
     *
     * @param context
     * @return
     */
    public static String getNowAppPkgName(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName cn = runningTaskInfos.get(0).topActivity;
            return cn.getPackageName();
        }
        return "";
    }

    /**
     * 判断自己在不在前台
     *
     * @param context
     * @return
     */
    public static boolean isRunningForegroundMe(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
            return true;
        }
        return false;
    }

    /**
     * 根据名名运行一个app
     *
     * @param context
     * @param sPkgName
     */
    public static void runApp(Context context, String sPkgName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(sPkgName);
        if (intent != null) {
            context.startActivity(intent);
        }
    }

    /**
     * 判断指定包名的APP是否已经安装
     *
     * @param context     上下文
     * @param packageName 需要检测的包名
     * @return 是否已经安装了指定包名的软件
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> infoList = packageManager.getInstalledPackages(0);
        for (int i = 0; i < infoList.size(); i++) {
            if (infoList.get(i).packageName.equalsIgnoreCase(packageName)) return true;
        }
        return false;
    }

    /**
     * 遍历本地已安装的所有软件，匹配与给定名称相同的软件[如果有多个同名软件，默认返回第一个查找到的包名]
     *
     * @param context Context
     * @param name    软件名称
     * @return 软件包名
     */
    public static String getPackageNameWithName(Context context, String name) {
        String packageName = "";
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            if (name.equals(appName)) {
                packageName = packageInfo.packageName;
                break;
            }
        }
        return packageName;
    }
}
