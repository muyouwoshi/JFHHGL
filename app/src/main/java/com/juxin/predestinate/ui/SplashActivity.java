package com.juxin.predestinate.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.juxin.library.observe.MsgMgr;
import com.juxin.predestinate.module.local.location.LocationMgr;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.CheckDomainName;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.start.NavUserAct;

/**
 * 闪屏页面
 *
 * @author ZRP
 * @date 2016/12/27
 */
public class SplashActivity extends BaseActivity {

    private final static long delayTime = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isCanBack(false);
        setCanNotify(false);//设置该页面不弹出悬浮窗消息通知
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        initData();

        int delay = (int) (delayTime - (System.currentTimeMillis() - App.t));
        MsgMgr.getInstance().delay(new Runnable() {
            @Override
            public void run() {
                skipLogic();
            }
        }, delay);

        // 子线程处理数据统计
        MsgMgr.getInstance().runOnChildThread(new Runnable() {
            @Override
            public void run() {
                Statistics.startUp();
            }
        });
    }

    private void initData() {
        LocationMgr.getInstance().start();//启动定位
        ModuleMgr.getCommonMgr().updateUsers();//软件升级U-P读取
        ModuleMgr.getCommonMgr().requestStaticConfig();//请求一些在线配置信息
        CheckDomainName.getDomainNameList();//域名灾备
    }

    /**
     * Intent跳转
     */
    private void skipLogic() {
        if (ModuleMgr.getLoginMgr().checkAuthIsExist()) {
            UIShow.showMainClearTask(this);
        } else {
            UIShow.show(this, NavUserAct.class);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        // 空实现，不响应返回键点击
    }
}
