package com.juxin.library.log;

import android.content.Context;
import android.support.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

/**
 * tray初始化类
 * Created by ZRP on 2017/6/16.
 */
public class PTrayPreferences extends TrayPreferences {

    private static final int VERSION = 1;

    public PTrayPreferences(@NonNull Context context, @NonNull String module) {
        super(context, module, VERSION);
    }
}
