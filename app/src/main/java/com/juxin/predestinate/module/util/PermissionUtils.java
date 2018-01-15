package com.juxin.predestinate.module.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;

/**
 * 权限管理
 * <p>关于android权限的几点说明：
 * <ol>
 * <li>只有android.M以上的程序才拥有运行时权限</li>
 * <li>android.M以下的手机运行时权限获取一直为false，但是自身权限获取一直为true</li>
 * </ol>
 * <p>以上内容见<a href="http://stackoverflow.com/questions/33407250/checkselfpermission-method-is-not-working-in-targetsdkversion-22">stackOverflow</>
 * <p>所以我们无法在23以下的手机上主动请求运行时权限，只能通过模拟的方式去主动展示预处理弹框。<p>
 * Created by ZRP on 2016/11/9.
 */
public class PermissionUtils {

    /**
     * @return 获取当前app的targetSdkVersion
     */
    public static int getTargetVersion(Context context) {
        int targetSdkVersion = 0;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return targetSdkVersion;
    }

    public static boolean isHasPermission(Context context, String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            if (getTargetVersion(context) >= 23) {//代码兼容
                result = ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
                PLogger.d("---PermissionUtils--->Build.VERSION.SDK_INT >= 23，targetSdkVersion >= 23");
            } else {//targetVersion<23的时候权限获取依旧遵循原有的方式，默认无权限
                PLogger.d("---PermissionUtils--->Build.VERSION.SDK_INT >= 23，targetSdkVersion < 23");
                result = PSP.getInstance().getBoolean(permission, false);
            }
        } else {
            PLogger.d("---PermissionUtils--->Build.VERSION.SDK_INT < 23");
            result = PSP.getInstance().getBoolean(permission, false);
        }
        PLogger.d("---PermissionUtils--->" + permission + "：" + result);
        return result;
    }

    /**
     * 请求权限
     *
     * @param activity    FragmentActivity实例，用于调起dialog
     * @param permission  当前要请求的权限类型，如：Manifest.permission.READ_CONTACTS
     * @param requestCode 权限请求码，用于回调监听
     * @param runnable    点击确定按钮之后执行的runnable
     */
    public static void getPermission(FragmentActivity activity, String permission, int requestCode, Runnable runnable) {
        if (!isHasPermission(activity, permission)) {
            PSP.getInstance().put(permission, true);
            if (Build.VERSION.SDK_INT >= 23 && getTargetVersion(activity) >= 23) {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            } else {
                if (runnable != null) {
                    runnable.run();
                }
            }
        } else {
            PLogger.d("---PermissionUtils--->已有权限，或不需要展示权限弹框");
        }
    }

    /**
     * 显示权限未打开时提示
     *
     * @param activity 弹框提示的activity
     * @param type     权限类型
     */
    public static void showErrorTip(FragmentActivity activity, String type) {
        PickerDialogUtil.showSimpleTipDialogExt(activity, new SimpleTipDialog.ConfirmListener() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onSubmit() {
                        //TODO 打开系统设置，权限修改页面
                    }
                }, "有可能第三方软件关闭了权限，请在设置-应用程序页面允许该软件“" + type + "”的访问权限",
                type + "授权未打开", "", App.getContext().getString(R.string.defriend_sure_dialog_surebtn),
                false, R.color.text_ciyao_gray);
    }

    /**
     * 请求相机权限
     *
     * @param activity 上下文
     * @param runnable 点击确定按钮之后执行的runnable
     */
    public static void requestCamera(FragmentActivity activity, Runnable runnable) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            //用户以前请求过权限但是被拒绝了
            showErrorTip(activity, "相机");
        } else {
            getPermission(activity, Manifest.permission.CAMERA, 1, runnable);
        }
    }

    /**
     * 请求录音权限
     *
     * @param activity 上下文
     * @param runnable 点击确定按钮之后执行的runnable
     */
    public static void requestAudio(FragmentActivity activity, Runnable runnable) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            showErrorTip(activity, "麦克风");
        } else {
            getPermission(activity, Manifest.permission.RECORD_AUDIO, 2, runnable);
        }
    }

    /**
     * 请求音视频需要的权限
     *
     * @param fragmentActivity FragmentActivity实例
     */
    public static void requestPermission(FragmentActivity fragmentActivity) {
        // 实际请求权限，暂时置空。后续如想批量请求权限可添加至此
    }
}
