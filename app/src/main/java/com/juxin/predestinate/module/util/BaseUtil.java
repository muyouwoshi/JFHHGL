package com.juxin.predestinate.module.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.notify.view.UserMailNotifyAct;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseUtil {

    public static String substring(String str, int toCount) {
        int reInt = 0;
        String reStr = "";
        if (str == null)
            return "";
        char[] tempChar = str.toCharArray();
        for (int kk = 0; (kk < tempChar.length && toCount > reInt); kk++) {
            String s1 = String.valueOf(tempChar[kk]);
            byte[] b = s1.getBytes();
            if (reInt + b.length >= toCount)
                break;
            reInt += b.length;
            reStr += tempChar[kk];
        }

        byte[] tempbufer = reStr.getBytes();
        int liCount = tempbufer.length;
        if (liCount >= toCount) {
            reStr = reStr.substring(0, toCount - 1);
        }

        return reStr;
    }

    // 字节流到bitmap
    public static Bitmap bytes2Bimap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        try {
            Bitmap.Config localConfig = Bitmap.Config.RGB_565;
            Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2, localConfig);
            ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
            localBitmap.copyPixelsFromBuffer(localByteBuffer);
            return localBitmap;
        } catch (OutOfMemoryError localOutOfMemoryError) {
            while (true) {
                localOutOfMemoryError.printStackTrace();
                System.gc();
            }
        }
    }

    public static Bitmap file2Bitmap(String paramString) {
        try {
            Bitmap localBitmap = BitmapFactory.decodeFile(paramString);
            return localBitmap;
        } catch (OutOfMemoryError localOutOfMemoryError) {
            while (true) {
                localOutOfMemoryError.printStackTrace();
                System.gc();
            }
        }
    }

    /**
     * 比较两个字节数组的内容是否相等
     *
     * @param b1 字节数组1
     * @param b2 字节数组2
     * @return true表示相等
     */
    public static boolean isByteArrayEqual(byte[] b1, byte[] b2) {
        if (b1.length != b2.length)
            return false;

        for (int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i])
                return false;
        }
        return true;
    }

    /**
     * 把字符串转换成int
     *
     * @param s          字符串
     * @param faultValue 如果转换失败，返回这个值
     * @return 如果转换失败，返回faultValue，成功返回转换后的值
     */
    public static int getInt(String s, int faultValue) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return faultValue;
        }
    }


    /**
     * 把字符串转换成long
     *
     * @param s          字符串
     * @param faultValue 如果转换失败，返回这个值
     * @return 如果转换失败，返回faultValue，成功返回转换后的值
     */
    public static long getLong(String s, long faultValue) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return faultValue;
        }
    }

    /**
     * 把字符串转换成long
     *
     * @param s          字符串
     * @param faultValue 如果转换失败，返回这个值
     * @return 如果转换失败，返回faultValue，成功返回转换后的值
     */
    public static long getLong(String s, int radix, long faultValue) {
        try {
            return Long.parseLong(s, radix);
        } catch (NumberFormatException e) {
            return faultValue;
        }
    }

    /**
     * 把字符串转换成int
     *
     * @param s          字符串
     * @param radix      基数
     * @param faultValue 如果转换失败，返回这个值
     * @return 如果转换失败，返回faultValue，成功返回转换后的值
     */
    public static int getInt(String s, int radix, int faultValue) {
        try {
            return Integer.parseInt(s, radix);
        } catch (NumberFormatException e) {
            return faultValue;
        }
    }

    /**
     * 检查字符串是否是整数格式
     *
     * @param s 字符串
     * @return true表示可以解析成整数
     */
    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 限制editText字数限制
     *
     * @param limit
     * @param edtView
     */
    public static void limitLettersOfEditText(int limit, EditText edtView) {
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(limit);
        edtView.setFilters(fArray);
    }

    public static String toMd5(String string) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(string.getBytes());
            return toHexString(algorithm.digest(), "");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toMd5(byte[] bytes) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(bytes);
            return toHexString(algorithm.digest(), "");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHexString(byte[] bytes, String separator) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex).append(separator);
        }
        return hexString.toString();
    }

    /**
     * 复制文件
     *
     * @param strSourceFile
     * @param strTargetFile
     */
    public static void copyFile(String strSourceFile, String strTargetFile) {

        File fileSource = new File(strSourceFile);
        File fileTarget = new File(strTargetFile);

        String[] dir = strTargetFile.split("/");// 用"//"把str分割成一个字符串数组
        String path = null;
        for (int i = 0; i < dir.length; i++) {
            if (i == (dir.length - 1)) {
                break;
            }
            if (path == null) {
                path = dir[i];
            } else {
                path = path + "//" + dir[i];
            }
        }
        if (path != null) {
            File file = new File(path);
            if (!file.exists()) {
                try {
                    // 按照指定的路径创建文件夹
                    file.mkdirs();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }

        copyFile(fileSource, fileTarget);
    }

    /**
     * 复制文件
     *
     * @param sourceFile
     * @param targetFile
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File targetFile) {

        try {
            // 新建文件输入流并对它进行缓冲
            FileInputStream input = new FileInputStream(sourceFile);
            BufferedInputStream inBuff = new BufferedInputStream(input);

            // 新建文件输出流并对它进行缓冲
            FileOutputStream output = new FileOutputStream(targetFile);
            BufferedOutputStream outBuff = new BufferedOutputStream(output);

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();

            // 关闭流
            inBuff.close();
            outBuff.close();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);// 获取年份
        int month = calendar.get(Calendar.MONTH);// 获取月份
        int day = calendar.get(Calendar.DATE);// 获取日
        String time = String.valueOf(year) + String.valueOf(month + 1) + String.valueOf(day);
        return time;
    }

    /**
     * 截取部分字符串
     *
     * @param strSource 来源
     * @param strFrom   要替换的东西
     * @param strTo     替换的东西
     * @return
     */
    public static String replace(String strSource, String strFrom, String strTo) {
        if (strSource == null) {
            return null;
        }
        int i = 0;
        if ((i = strSource.indexOf(strFrom, i)) >= 0) {
            char[] cSrc = strSource.toCharArray();
            char[] cTo = strTo.toCharArray();
            int len = strFrom.length();
            StringBuffer buf = new StringBuffer(cSrc.length);
            buf.append(cSrc, 0, i).append(cTo);
            i += len;
            int j = i;
            while ((i = strSource.indexOf(strFrom, i)) > 0) {
                buf.append(cSrc, j, i - j).append(cTo);
                i += len;
                j = i;
            }
            buf.append(cSrc, j, cSrc.length - j);
            return buf.toString();
        }
        return strSource;
    }

    /**
     * 用正则表达式
     * 是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 判断某个服务是否启动
     *
     * @param context
     * @param className
     * @return boolean
     */
    public static boolean serviceIsStart(Context context, String className) {
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> mServiceList = mActivityManager
                .getRunningServices(30);
        for (int i = 0; i < mServiceList.size(); i++) {
            if (className.equals(mServiceList.get(i).service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //过滤无效Mac字符
    public static String TrimMACStr(String MAC) {
        if (TextUtils.isEmpty(MAC))
            return "";
        int chr;
        StringBuilder strbd = new StringBuilder();

        for (int i = 0; i < MAC.length(); i++) {
            chr = MAC.charAt(i);
            // 0-9,:,A-F,a-f
            if (((chr >= 48 && chr <= 58) || (chr >= 65 && chr <= 70) || (chr >= 97 && chr <= 102))) {
                strbd.append((char) chr);
            }
        }

        return strbd.toString();
    }


    private static Intent getBrowserApp(Context context, String url) {
        String default_browser = "android.intent.category.DEFAULT";
        String browsable = "android.intent.category.BROWSABLE";
        String view = "android.intent.action.VIEW";

        Intent intent = new Intent();
        intent.setAction(view);
        intent.addCategory(default_browser);
        intent.addCategory(browsable);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        // 找出手机当前安装的所有浏览器程序
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);

        if (resolveInfoList.size() > 0) {
            ActivityInfo activityInfo = resolveInfoList.get(0).activityInfo;
            String packageName = activityInfo.packageName;
            String className = activityInfo.name;
            ComponentName comp = new ComponentName(packageName, className);
            intent.setComponent(comp);
            return intent;
        } else {
            return null;
        }
    }

    public static void addShortCutToURL(Context context, String url, String sIcon, String sIconName) {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, sIconName);
        // 不允许重复创建
        shortcut.putExtra("duplicate", false);
        Intent intent = getBrowserApp(context, url);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 快捷方式的图标
        Bitmap bitmap = BitmapFactory.decodeFile(sIcon);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
        // 发出广播
        context.sendBroadcast(shortcut);
    }

    public static void addShortcutToAPkPub(Context context, String sApkLocal, String sIcon, String sIconName) {

        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, sIconName);
        // 不允许重复创建
        shortcut.putExtra("duplicate", false);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(sApkLocal)), "application/vnd.android.package-archive");

        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 快捷方式的图标
        Bitmap bitmap = BitmapFactory.decodeFile(sIcon);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
        // 发出广播
        context.sendBroadcast(shortcut);
    }

    //判断点击区域是否在可视范围
    public static boolean isOutOfBounds(Activity context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        final View decorView = context.getWindow().getDecorView();
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop)) || (y > (decorView.getHeight() + slop));
    }

    /**
     * 判断应用是否在前台，考虑到应用在后台但是弹框展示在前面的情况，其余普通判断不适用
     *
     * @return 在前台：true
     */
    public static boolean isRunningForegroundMe(Context context) {
        return isTopProcess(context,
                context.getPackageName(),
                "com.juxin.predestinate.assist"
        ) || BaseUtil.isTopActivity(App.context,
                UserMailNotifyAct.class.getName(),
                "com.juxin.predestinate.assist.ui.video.RtcVideoActivity",
                "com.juxin.predestinate.assist.ui.voice.RtcVoiceActivity",
                "com.juxin.predestinate.assist.ui.RtcInitActivity",
                "com.juxin.predestinate.assist.ui.RtcGroupInitAct");
    }

    /**
     * 判断指定包名的进程是否在顶部
     *
     * @param sPkgNames applicationId
     * @return true-顶部进程
     */
    public static boolean isTopProcess(Context context, String... sPkgNames) {
        String packageName = "";
        ActivityManager manager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        if (Build.VERSION.SDK_INT >= 21) {
            List<ActivityManager.RunningAppProcessInfo> pis = manager.getRunningAppProcesses();
            ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(0);
            if (topAppProcess != null && topAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                packageName = topAppProcess.processName;
            }
        } else {
            List localList = manager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo) localList.get(0);
            packageName = localRunningTaskInfo.topActivity.getPackageName();
        }

        if (!TextUtils.isEmpty(packageName)) {
            for (String sPkgName : sPkgNames) {
                if (packageName.equals(sPkgName)) return true;
            }
        }
        return false;
    }

    /**
     * 检测某ActivityUpdate是否在当前Task的栈顶
     */
    public static boolean isTopActivity(Context context, String... actNames) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
        String cmpNameTemp = null;

        if (null != runningTasks) {
            cmpNameTemp = runningTasks.get(0).topActivity.getClassName();
        }
        if (null == cmpNameTemp) return false;

        for (String actName : actNames) {
            if (cmpNameTemp.equals(actName)) return true;
        }
        return false;
    }

    public static boolean isScreenLock(Context context) {
        try {
            KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            return mKeyguardManager.inKeyguardRestrictedInputMode();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @return 获取当前cpu型号
     */
    public static String getMachineCpu() {
        String jvmName = System.getProperty("java.vm.name", "").toLowerCase();
        String osName = System.getProperty("os.name", "").toLowerCase();
        String osArch = System.getProperty("os.arch", "").toLowerCase();
        if (jvmName.startsWith("dalvik") && osName.startsWith("linux")) {
            osName = "android";
        } else if (jvmName.startsWith("robovm") && osName.startsWith("darwin")) {
            osName = "ios";
            osArch = "arm";
        } else if (osName.startsWith("mac os x")) {
            osName = "macosx";
        } else {
            int spaceIndex = osName.indexOf(' ');
            if (spaceIndex > 0) {
                osName = osName.substring(0, spaceIndex);
            }
        }
        if (osArch.equals("i386") || osArch.equals("i486") || osArch.equals("i586") || osArch.equals("i686")) {
            osArch = "x86";
        } else if (osArch.equals("amd64") || osArch.equals("x86-64") || osArch.equals("x64")) {
            osArch = "x86_64";
        } else if (osArch.startsWith("aarch64") || osArch.startsWith("armv8") || osArch.startsWith("arm64")) {
            osArch = "arm64";
        } else if (osArch.startsWith("arm")) {
            osArch = "arm";
        }
        return osName + "-" + osArch;
    }

    /**
     * 获取资源ID
     * @param name 资源名
     * @return
     */
    public static int getResId(String name) {
        return  App.context.getResources().getIdentifier(name, "raw", App.context.getPackageName());
    }
}