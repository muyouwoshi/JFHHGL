package com.juxin.predestinate.module.util;

import com.juxin.predestinate.module.logic.application.App;

/**
 * 通用工具类
 * Created by siow on 2017/9/8.
 */
public class CommonUtil {

    /**
     * 判断是否为纯净版的apk包[现阶段小友为纯净包，判断其包名]
     *
     * @return 是否为纯净版的apk包
     */
    public static boolean isPurePackage() {
        return "com.xiaoyou.friends".equals(App.context.getPackageName());
    }
}
