package com.juxin.predestinate.module.logic.baseui;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.juxin.predestinate.R;

/**
 * @author Mr.Huang
 * @date 2017-07-28
 * 此类实为了增加沉浸式状态栏
 */
public abstract class AbstractActivity extends FragmentActivity {

    private OnGlobalLayoutListener onGlobalLayoutListener;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBarStyle();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setStatusBarStyle();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setStatusBarStyle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyContentChildObserver();
    }

    /**
     * 沉浸式状态栏
     * 设置状态栏颜色
     */
    private void setStatusBarStyle() {
        if (isFullScreen()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            int statusColor = getStatusBarColor();
            boolean isTransparent = isStatusBarTransparent();
            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);//获取Activity根布局
            if (isTransparent && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.0以后的系统状态栏全透明设置，需要注意的是此此设置将导致输入法无法顶起输入的控件，这个设置类似于设置全屏
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);//5.0以后系统提供了设置状态栏颜色值，这里为了统一使用透明色。
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//设置成沉浸式
            }
            if (mContentView != null && mContentView.getChildCount() > 0) {
                final View mContentChild = mContentView.getChildAt(0);
                boolean isFitsSystemWindows = isFitsSystemWindows();
                mContentChild.setFitsSystemWindows(isFitsSystemWindows);
                StatusBarDrawable statusBarDrawable = new StatusBarDrawable(statusColor, mContentChild.getBackground(), getStatusBarHeight());
                statusBarDrawable.setFitsSystemWindows(isFitsSystemWindows);
                mContentChild.setBackground(statusBarDrawable);
                if (!isFitsSystemWindows && isAdjustResize()) {
                    onGlobalLayoutListener = new OnGlobalLayoutListener(mContentChild);
                    mContentChild.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
                }
            }
        }
    }

    public boolean isFullScreen() {
        return false;
    }

    public boolean isFitsSystemWindows() {
        return true;
    }

    public boolean isStatusBarTransparent() {
        return getResources().getBoolean(R.bool.android_status_bar_transparent);
    }

    public boolean isAdjustResize() {
        return false;
    }

    public void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            if (mContentView != null && mContentView.getChildCount() > 0) {
                View mContentChild = mContentView.getChildAt(0);
                if (mContentChild != null && mContentChild.getBackground() != null) {
                    Drawable drawable = mContentChild.getBackground();
                    if (drawable instanceof StatusBarDrawable) {
                        ((StatusBarDrawable) drawable).setColor(color);
                    }
                }
            }
        }
    }

    public int getStatusBarColor() {
        return getResources().getColor(R.color.color_6F7485);//状态栏颜色,修改的话需要覆盖status_bar_color颜色值熟悉;
    }

    private void destroyContentChildObserver() {
        if (onGlobalLayoutListener == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            if (mContentView != null && mContentView.getChildCount() > 0) {
                View mContentChild = mContentView.getChildAt(0);
                mContentChild.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
            }
        }
    }

    private final class OnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        private int usableHeightPrevious;
        private Rect r = new Rect();
        private View mContentChild;

        private OnGlobalLayoutListener(View contentChild) {
            this.mContentChild = contentChild;
        }

        @Override
        public void onGlobalLayout() {
            mContentChild.getWindowVisibleDisplayFrame(r);
            ViewGroup.LayoutParams frameLayoutParams = mContentChild.getLayoutParams();
            int usableHeightNow = (r.bottom - r.top) + getStatusBarHeight();
            if (usableHeightNow != usableHeightPrevious) {
                int usableHeightSansKeyboard = mContentChild.getRootView().getHeight();
                int heightDifference = usableHeightSansKeyboard - usableHeightNow;
                if (heightDifference > (usableHeightSansKeyboard / 4)) {
                    frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                } else {
                    if (!checkDeviceHasNavigationBar()) {
                        frameLayoutParams.height = usableHeightSansKeyboard;
                    } else {
                        frameLayoutParams.height = usableHeightSansKeyboard - getNavigationBarHeight();
                    }
                }
                mContentChild.requestLayout();
                usableHeightPrevious = usableHeightNow;
            }
        }
    }

    public int getStatusBarHeight() {
        Resources resources = getApplicationContext().getResources();
        try {
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getNavigationBarHeight() {
        Resources resources = getApplicationContext().getResources();
        try {
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean checkDeviceHasNavigationBar() {
        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        boolean hasMenuKey = ViewConfiguration.get(getApplicationContext()).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey && !hasBackKey) {
            // 做任何你需要做的,这个设备有一个导航栏
            return true;
        }
        return false;
    }

    public boolean isEmpty(CharSequence str){
        return TextUtils.isEmpty(str);
    }
}
