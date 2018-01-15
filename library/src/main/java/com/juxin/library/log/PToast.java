package com.juxin.library.log;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 全局Toast提示类
 * Created by ZRP on 2016/9/8.
 */
public class PToast {

    private static Context context;

    /**
     * 需要在application中进行初始化，以便程序中后续进行调用
     *
     * @param context applicationContext
     */
    public static void init(Context context) {
        PToast.context = context;
    }

    /**
     * 显示短时间的toast提示
     *
     * @param tip 提示文字
     */
    public static void showShort(String tip) {
        Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示短时间的toast提示
     *
     * @param tip 提示文字
     */
    public static void showShort(@StringRes int tip) {
        Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示长时间的toast提示
     *
     * @param tip 提示文字
     */
    public static void showLong(String tip) {
        Toast.makeText(context, tip, Toast.LENGTH_LONG).show();
    }

    /**
     * 显示长时间的toast提示
     *
     * @param tip 提示文字
     */
    public static void showLong(@StringRes int tip) {
        Toast.makeText(context, tip, Toast.LENGTH_LONG).show();
    }

    /**
     * 居中显示的toast
     *
     * @param tip 提示文字
     */
    public static void showCenterShort(String tip) {
        Toast toast = Toast.makeText(context, tip, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
