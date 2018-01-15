package com.juxin.predestinate.ui.live.util;

import android.content.Context;

/**
 * Created by terry on 2017/8/22.
 */
public class NavigationUtil {

    /**
     *
     * @param mContext
     * @return 获取navigation bar height
     */
    public static int getNavigationBarHeight(Context mContext){

        int resIdShow = mContext.getResources().getIdentifier("config_showNavigationBar","bool","android");
//        Log.d("zt","resIdShow: "+resIdShow);
        boolean isShowNavigation = false;
        if (resIdShow > 0){
            isShowNavigation = mContext.getResources().getBoolean(resIdShow);
        }

        if (isShowNavigation){
            int resId = mContext.getResources().getIdentifier("navigation_bar_height","dimen","android");
            int height =  0;
            if (resId > 0){
                height = mContext.getResources().getDimensionPixelSize(resId);
            }
            return height;
        }
        return  0;
    }
}
