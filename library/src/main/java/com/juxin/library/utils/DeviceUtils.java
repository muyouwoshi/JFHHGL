package com.juxin.library.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.juxin.library.log.PLogger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.regex.Pattern;

/**
 * 全局设备管理工具类
 * Created by ZRP on 2016/9/8.
 */
public class DeviceUtils {

    public static final String FILENAME_NOMEDIA = ".nomedia";

    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");

    private DeviceUtils() {
    }

    // -------------------------ROOT-----------------------------

    /**
     * @return 判断当前手机是否有ROOT权限
     */
    public static boolean isRooted() {
        return findBinary("su");
    }

    /**
     * 从指定的路径查找指定的二进制文件是否存在
     *
     * @param binaryName 二进制文件名称
     * @return 文件是否存在
     */
    private static boolean findBinary(String binaryName) {
        boolean found = false;
        String[] places = {"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/",
                "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};
        try {
            for (String where : places) {
                if (new File(where + binaryName).exists()) {
                    found = true;
                    break;
                }
            }
        } catch (Exception e) {
            // not catch
        }
        return found;
    }

    // -------------------------文件-----------------------------

    /**
     * Check if a filename is "safe" (no metacharacters or spaces).
     *
     * @param file The file to check
     */
    public static boolean isFilenameSafe(File file) {
        // Note, we check whether it matches what's known to be safe,
        // rather than what's known to be unsafe.  Non-ASCII, control
        // characters, etc. are all unsafe by default.
        return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
    }


    public static File getCacheDir(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File cacheDir = context.getExternalCacheDir();
            File noMedia = new File(cacheDir, FILENAME_NOMEDIA);
            if (!noMedia.exists()) {
                try {
                    noMedia.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return cacheDir;
        } else {
            return context.getCacheDir();
        }
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        PLogger.v("getRealPath() uri=" + uri + " isKitKat=" + isKitKat);

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return uri.getPath();
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = MediaStore.MediaColumns.DATA;
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    // -------------------------输入法-----------------------------

    /**
     * 隐藏输入法
     */
    public static void hideSoftKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 显示输入法
     */
    public static void showSoftKeyboard(Context context, EditText editText) {
        if (editText.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 如果输入法未打开，就打开输入法；如果输入法已打开，就关闭输入法
     */
    public static void toggleSoftInput(Context context, View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, 0);
        }
    }

    /**
     * Execute an {@link AsyncTask} on a thread pool.
     *
     * @param task Task to add.
     * @param args Optional arguments to pass to {@link AsyncTask#execute(Object[])}.
     * @param <T>  Task argument type.
     */
    @SuppressWarnings("unchecked")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static <T> void execute(AsyncTask<T, ?, ?> task, T... args) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            task.execute(args);
        } else {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args);
        }
    }

    /**
     * @return 设备是否有摄像头
     */
    public static boolean hasCamera(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    public static void mediaScan(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    // another media scan way
    public static void addToMediaStore(Context context, File file) {
        String[] path = new String[]{file.getPath()};
        MediaScannerConnection.scanFile(context, path, null, null);
    }

    public static boolean isMediaMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    // -------------------------应用前后台判断-----------------------------

    /**
     * 设置严苛模式，用于调试。添加自API11
     *
     * @param enable 打开/关闭严苛模式
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setStrictMode(boolean enable) {
        if (!enable) return;

        StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog();
        StrictMode.VmPolicy.Builder vmPolicyBuilder =
                new StrictMode.VmPolicy.Builder().detectAll().penaltyLog();
        StrictMode.setThreadPolicy(threadPolicyBuilder.build());
        StrictMode.setVmPolicy(vmPolicyBuilder.build());
    }

    // -------------------------电池信息-----------------------------

    /**
     * 获取当前电量百分比
     *
     * @param context 上下文
     * @return 电量百分比，max-100
     */
    public static int getBatteryPercentage(Context context) {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        float batteryPct = level / (float) scale;
        return (int) (batteryPct * 100);
    }

    /**
     * 获取电池信息
     *
     * @param context 上下文
     * @return 电池信息
     */
    public static String getBatteryInfo(Context context) {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        int status = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1) : -1;
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) : -1;
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        float batteryPct = level / (float) scale;

        return "Battery Info: isCharging=" + isCharging
                + " usbCharge=" + usbCharge + " acCharge=" + acCharge
                + " batteryPct=" + batteryPct + "%";
    }

    // -------------------------CPU占用信息-----------------------------

    /**
     * @return 获取手机当前CPU占用[所有应用cpu总占用百分比，max100]。todo 待优化
     */
    public static int getCpuUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (int) ((float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1)) * 100);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // -------------------------内存信息-----------------------------

    /**
     * 获取可用的内存大小(单位MB)
     *
     * @param context 上下文
     * @return 格式化的内存大小(单位MB)
     */
    public static int getAvailableMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        //mi.availMem; 当前系统的可用内存
        return (int) (mi.availMem / 1024 / 1024);// 将获取的内存大小转换为MB
    }

    /**
     * 获取内存总大小(单位MB)，兼容低版本手机获取
     *
     * @return 格式化的内存大小(单位MB)
     */
    public static int getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        int initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            arrayOfString = str2.split("\\s+");
//            for (String num : arrayOfString) {
//                Log.i(str2, num + "\t");
//            }
            initial_memory = Integer.valueOf(arrayOfString[1]) / 1024;// 获得系统总内存，单位是KB，除以1024转换为MB
            localBufferedReader.close();
        } catch (IOException | NumberFormatException e) {
        }
        return initial_memory;// Byte转换为KB或者MB，内存大小规格化
    }

    // -------------------------签名-----------------------------

    /**
     * @return 获取包体签名
     */
    @SuppressLint("PackageManagerGetSignatures")
    private static Signature getPackageSignature(Context context) {
        final PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (Exception ignored) {
        }

        Signature signature = null;
        if (info != null) {
            Signature[] signatures = info.signatures;
            if (signatures != null && signatures.length > 0) {
                signature = signatures[0];
            }
        }
        PLogger.v("getSignature() " + signature);
        return signature;
    }

    /**
     * @return 获取签名信息
     */
    public static String getSignatureInfo(Context context) {
        final Signature signature = getPackageSignature(context);
        if (signature == null) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        try {
            final byte[] signatureBytes = signature.toByteArray();
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            final InputStream is = new ByteArrayInputStream(signatureBytes);
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(is);
            final String chars = signature.toCharsString();
            builder.append("SignName:").append(cert.getSigAlgName()).append("\n");
            builder.append("Chars:").append(chars).append("\n");
            builder.append("SignNumber:").append(cert.getSerialNumber()).append("\n");
            builder.append("SubjectDN:").append(cert.getSubjectDN().getName()).append("\n");
            builder.append("IssuerDN:").append(cert.getIssuerDN().getName()).append("\n");
        } catch (Exception e) {
            PLogger.e("parseSignature() ex=" + e);
        }

        String text = builder.toString();
        PLogger.v(text);

        return text;
    }
}
