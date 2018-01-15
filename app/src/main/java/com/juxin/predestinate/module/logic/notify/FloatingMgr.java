package com.juxin.predestinate.module.logic.notify;

import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.intercept.InterceptTouchLinearLayout;
import com.juxin.predestinate.module.logic.notify.view.FloatingBasePanel;
import com.juxin.predestinate.module.util.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 软件内通知，实现悬浮窗功能
 */
public class FloatingMgr implements Handler.Callback {

    private static FloatingMgr ourInstance = new FloatingMgr();

    public static FloatingMgr getInstance() {
        return ourInstance;
    }

    private FloatingMgr() {
        Looper looper;

        if ((looper = Looper.myLooper()) != null) {
            handler = new Handler(looper, this);
        } else if ((looper = Looper.getMainLooper()) != null) {
            handler = new Handler(looper, this);
        } else {
            handler = null;
        }
    }

    private boolean canNotify = true;

    /**
     * 设置是否允许弹出floating提示，特殊情况使用
     *
     * @param canNotify 是否允许
     */
    public void setCanNotify(boolean canNotify) {
        this.canNotify = canNotify;
    }

    /**
     * 对外功能。
     */
    private ArrayList<FloatingBasePanel> autoPanels = new ArrayList<FloatingBasePanel>();
    private ArrayList<FloatingBasePanel> fixedPanels = new ArrayList<FloatingBasePanel>();

    /**
     * 添加一个弹窗。
     *
     * @param floatingBasePanel 弹窗面板。
     * @return 添加成功返回true。
     */
    public boolean addPanel(FloatingBasePanel floatingBasePanel) {
        if (!canNotify) return false;
        if (floatingBasePanel == null) return false;

        synchronized (this) {
            if (!ModuleMgr.getAppMgr().isForeground()) return false;//如果应用在后台，返回false

            getWindowManager();
            if (floatingBasePanel.isFixed()) {
                fixedPanels.add(floatingBasePanel);
            } else {
                autoPanels.add(floatingBasePanel);
            }
            addView(floatingBasePanel);
        }
        return true;
    }

    /**
     * 对外接口，移除一个已有的弹窗。
     *
     * @param floatingBasePanel 弹窗面板。
     */
    public void removePanel(FloatingBasePanel floatingBasePanel) {
        if (floatingBasePanel == null) return;

        synchronized (this) {
            if (floatingBasePanel.isFixed()) {
                fixedPanels.remove(floatingBasePanel);
            } else {
                autoPanels.remove(floatingBasePanel);
            }

            removeView(floatingBasePanel);
        }
    }

    public void removeAllPanel() {
        if (layoutParams == null) return;

        synchronized (this) {
            handler.removeMessages(FLOATING_Countdown_Id);
            handler.removeMessages(FLOATING_Remove_Id);
            handler.removeMessages(FLOATING_Remove_View_Id);
            handler.removeMessages(FLOATING_Remove_Ani_Id);

            fixedPanels.clear();
            autoPanels.clear();

            fixedViewGroup.removeAllViews();
            autoViewGroup.removeAllViews();

            removeView();
        }
    }

    /**
     * 功能实现。
     */
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private boolean isAddView = false;
    private View parentView = null;
    private ViewGroup fixedViewGroup = null;
    private ViewGroup autoViewGroup = null;
    private FloatingBasePanel lastBasePanel = null;

    private android.os.Handler handler = null;
    private static final int FLOATING_Countdown_Id = 1;
    private static final int FLOATING_Remove_Id = 2;
    private static final int FLOATING_Remove_View_Id = 3;
    private static final int FLOATING_Remove_Ani_Id = 4;
    private static final long FLOATING_Countdown = 5 * 1000;

    public void init() {
    }

    /**
     * 页面onResume的时候重新赋值WindowManager，在BaseExActivity的onResume调用
     *
     * @param windowManager 窗体引用
     */
    public void onResume(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public WindowManager getWindowManager() {
        if (layoutParams == null) {
            layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
            layoutParams.gravity = Gravity.LEFT | Gravity.TOP;

            layoutParams.y = ModuleMgr.getAppMgr().getStatusBarHeight() +
                    App.getContext().getResources().getDimensionPixelSize(R.dimen.title_footer_height);

            parentView = LayoutInflater.from(App.context).inflate(R.layout.common_floating_window, null);
            fixedViewGroup = (ViewGroup) parentView.findViewById(R.id.floating_fixed_layout);
            autoViewGroup = (ViewGroup) parentView.findViewById(R.id.floating_auto_layout);
        }
        return windowManager;
    }

    /**
     * 页面onPause的时候清空本地持有的windowManager，在BaseExActivity的onResume调用
     *
     * @param windowManager 窗体引用
     */
    public void onPause(WindowManager windowManager) {
        removeAllPanel();

        layoutParams = null;
        this.windowManager = null;
    }

    /**
     * 添加panel到悬浮窗中。
     *
     * @param floatingBasePanel 添加的悬浮窗面板
     */
    private void addView(FloatingBasePanel floatingBasePanel) {
        lastBasePanel = floatingBasePanel;
        ViewGroup tempViewGroup = null;

        if (lastBasePanel.isFixed()) {
            tempViewGroup = fixedViewGroup;
        } else {
            if (handler != null) {
                handler.removeMessages(FLOATING_Countdown_Id);
                handler.sendEmptyMessageDelayed(FLOATING_Countdown_Id, FLOATING_Countdown);
            }
            tempViewGroup = autoViewGroup;
        }

        View view = lastBasePanel.getContentView();
        InterceptTouchLinearLayout layout = createLayout(view);
        layout.addView(view, params);
        tempViewGroup.addView(layout);
        Animation animation = AnimationUtils.loadAnimation(App.context, R.anim.floating_top_in);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        layout.startAnimation(animation);

        addView();
        removeAutoMorePanel();
    }

    private HashMap<View, InterceptTouchLinearLayout> views = new HashMap<View, InterceptTouchLinearLayout>();
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    private InterceptTouchLinearLayout createLayout(View view) {
        InterceptTouchLinearLayout linearLayout = new InterceptTouchLinearLayout(view.getContext());
        views.put(view, linearLayout);
        return linearLayout;
    }

    /**
     * 从悬浮窗中移除panel。
     *
     * @param floatingBasePanel 添加的悬浮窗面板
     */
    private void removeView(FloatingBasePanel floatingBasePanel) {
        View view = floatingBasePanel.getContentView();
        InterceptTouchLinearLayout layout = views.remove(view);
        if (layout == null) return;

        Animation animation = AnimationUtils.loadAnimation(App.context, R.anim.floating_top_out);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setFillAfter(true);
        layout.setInterceptTouchEvent(true);
        layout.startAnimation(animation);

        if (handler != null) {
            Message msg;
            if (floatingBasePanel.isFixed()) {
                msg = handler.obtainMessage(FLOATING_Remove_View_Id, 1, 0, layout);
            } else {
                msg = handler.obtainMessage(FLOATING_Remove_View_Id, 2, 0, layout);
            }
            handler.sendMessageDelayed(msg, 500);
        }
    }

    /**
     * 从悬浮窗中移除View。
     *
     * @param layout
     */
    private void removeView(InterceptTouchLinearLayout layout, boolean fixed) {
        layout.removeAllViews();

        if (fixed) {
            fixedViewGroup.removeView(layout);
        } else {
            autoViewGroup.removeView(layout);
        }

        if (!isHaveTip()) {
            removeView();
        }
    }

    private void removeAutoMorePanel() {
        if (autoPanels.size() <= 1) {
            return;
        }

        removePanel(autoPanels.get(0));

        removeAutoMorePanel();
    }


    /**
     * 是否还有需要提示的。
     *
     * @return true有需要提示的。
     */
    private boolean isHaveTip() {
        return !(fixedViewGroup.getChildCount() == 0 && autoViewGroup.getChildCount() == 0);

    }

    /**
     * 添加view到window中。
     */
    private void addView() {
        if (!isHaveTip()) return;//如果没有需要提示的条目，就不进行显示

        if (!isAddView && windowManager != null && layoutParams != null) {
            UIUtil.addView(windowManager, parentView, layoutParams);
            isAddView = true;
        }
    }

    /**
     * 将view从window中移除。
     */
    private void removeView() {
        if (isAddView && windowManager != null) {
            UIUtil.removeView(windowManager, parentView);
            isAddView = false;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case FLOATING_Countdown_Id:
                if (handler != null) {
                    handler.sendEmptyMessage(FLOATING_Remove_Id);
                }
                break;

            case FLOATING_Remove_Id:
                removePanel(lastBasePanel);
                break;

            case FLOATING_Remove_View_Id:
                if (msg.arg1 == 1) {
                    removeView((InterceptTouchLinearLayout) msg.obj, true);
                } else {
                    removeView((InterceptTouchLinearLayout) msg.obj, false);
                }
                break;

            case FLOATING_Remove_Ani_Id:
//                 msg.obj
                break;

            default:
                break;
        }
        return true;
    }
}
