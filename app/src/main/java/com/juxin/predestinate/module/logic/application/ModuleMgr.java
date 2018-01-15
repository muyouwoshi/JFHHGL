package com.juxin.predestinate.module.logic.application;

import android.content.Context;
import android.os.Process;

import com.juxin.library.dir.DirType;
import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.ModuleBase;
import com.juxin.library.unread.UnreadMgr;
import com.juxin.predestinate.BuildConfig;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.center.CenterMgr;
import com.juxin.predestinate.module.local.chat.ChatListMgr;
import com.juxin.predestinate.module.local.chat.ChatMgr;
import com.juxin.predestinate.module.local.common.CommonMgr;
import com.juxin.predestinate.module.local.login.LoginMgr;
import com.juxin.predestinate.module.local.msgview.PhizMgr;
import com.juxin.predestinate.module.local.statistics.log.CrashHandler;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.FinalKey;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.logic.media.MediaMgr;
import com.juxin.predestinate.module.logic.model.impl.AppMgrImpl;
import com.juxin.predestinate.module.logic.model.impl.HttpMgrImpl;
import com.juxin.predestinate.module.logic.model.impl.UnreadMgrImpl;
import com.juxin.predestinate.module.logic.model.mgr.AppMgr;
import com.juxin.predestinate.module.logic.model.mgr.HttpMgr;
import com.juxin.predestinate.module.logic.notify.LockScreenMgr;
import com.juxin.predestinate.module.logic.notify.NotifyMgr;
import com.juxin.predestinate.module.logic.request.RequestHelper;
import com.juxin.predestinate.module.logic.tips.TipsBarMgr;
import com.juxin.predestinate.module.util.CheckDomainName;
import com.juxin.predestinate.ui.agora.RtcEngineMgr;
import com.networkbench.agent.impl.NBSAppAgent;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块初始化工具
 * Created by ZRP on 2016/12/27.
 */
public final class ModuleMgr {

    private static List<ModuleBase> selfModules = new ArrayList<>();

    /**
     * 调用所有逻辑模块
     */
    public static void releaseAll() {
        for (ModuleBase item : selfModules) {
            try {
                item.release();
            } catch (Throwable e) {
                PLogger.printThrowable(e);
            }
        }
        selfModules.clear();
        selfModules = null;
    }

    /**
     * 按等级初始化逻辑模块
     */
    public static void initModule(final Context context) {
        CrashHandler.getInstance().init();//初始化全局崩溃拦截
        PSP.getInstance().init(context);//初始化sharedPreferences存储，每个进程都初始化一次
        //初始化日志打印，每个进程都初始化一次
        PLogger.init(PSP.getInstance().getBoolean(FinalKey.GM_PRINT_DEBUG_LOG, BuildConfig.DEBUG));
        DirType.init(context, context.getString(R.string.app_storage_name));
        CheckDomainName.checkLocalDomainName();//检测本地是否存有域名文件

        String processName = ModuleMgr.getAppMgr().getProcessName(Process.myPid());
        String packageName = ModuleMgr.getAppMgr().getPackageName();
        PLogger.d("---processName--->" + processName);
        if (processName.equals(packageName)) {//主进程
            preInit(context);

            int runCount = PSP.getInstance().getInt(FinalKey.APP_RUN_COUNT, 0);
            PSP.getInstance().put(FinalKey.APP_RUN_COUNT, ++runCount);

            LockScreenMgr.getInstance().registerReceiver();//注册锁屏弹窗
            getLoginMgr().initCookie();
        }
        initTingYun(context);
    }

    /**
     * 初始化听云统计
     */
    private static void initTingYun(Context context) {
        // 初始化听云统计
        NBSAppAgent.setLicenseKey("4c371e9730a34f13b88176773ea6e382")
                .withLocationServiceEnabled(true)
                .startInApplication(context.getApplicationContext());
        NBSAppAgent.setUserCrashMessage("package", "dev");
        NBSAppAgent.setUserCrashMessage("hostType",
                PSP.getInstance().getInt(Hosts.SERVER_TYPE_KEY, 0) == 0 ? "formal" : "test");
    }

    /**
     * 预加载，在进程初始化的时候加载一次
     */
    private static void preInit(Context context) {
        PToast.init(context);                       //初始化toast提示
        RequestHelper.getInstance().init(context);  //初始化网络请求
        ImageLoader.init(context);                  //初始化图片加载

        initTBSX5(context);
        initUM(context);

        getAppMgr();
        getHttpMgr();
        getMediaMgr();

        getUnreadMgr();
        getChatListMgr();
        getChatMgr();

        getCommonMgr();
        getLoginMgr();
        getCenterMgr();

        getNotifyMgr();
        getTipsBarMgr();
    }

    /**
     * 初始化腾讯X5内核
     *
     * @param context 保证context为applicationContext
     */
    private static void initTBSX5(Context context) {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                PLogger.d("Tencent tbs x5 is load " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(context, cb);
    }

    /**
     * 初始化友盟统计
     */
    private static void initUM(Context context) {
        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(context,
                Constant.UMENG_APPKEY, getAppMgr().getUMChannel());
        MobclickAgent.startWithConfigure(config);
    }

    /**
     * 将一个新模块添加的管理器中，并调用其init方法
     *
     * @param module 一个新的模块
     */
    private static void addModule(final ModuleBase module) {
        module.init();
        selfModules.add(module);
    }

    /**
     * 应用模块。
     */
    private static AppMgr appMgr = null;

    /**
     * 取得应用信息管理的实例的唯一方法，统一从此处获得，全局唯一
     *
     * @return 返回应用管理器的实例
     */
    public static AppMgr getAppMgr() {
        if (appMgr == null) {
            appMgr = new AppMgrImpl();
            addModule(appMgr);
        }
        return appMgr;
    }

    /**
     * 网络请求模块
     */
    private static HttpMgr httpMgr = null;

    /**
     * 取得应用信息管理的实例的唯一方法，统一从此处获得，全局唯一
     *
     * @return 返回应用管理器的实例
     */
    public static HttpMgr getHttpMgr() {
        if (httpMgr == null) {
            httpMgr = new HttpMgrImpl();
            addModule(httpMgr);
        }
        return httpMgr;
    }


    /**
     * 个人中心管理
     */
    private static CenterMgr centerMgr = null;

    public static CenterMgr getCenterMgr() {
        if (centerMgr == null) {
            centerMgr = new CenterMgr();
            addModule(centerMgr);
        }
        return centerMgr;
    }

    /**
     * 文件管理
     */
    private static MediaMgr mediaMgr = null;

    public static MediaMgr getMediaMgr() {
        if (mediaMgr == null) {
            mediaMgr = new MediaMgr();
            addModule(mediaMgr);
        }
        return mediaMgr;
    }

    /**
     * 登录管理
     */
    private static LoginMgr loginMgr = null;

    public static LoginMgr getLoginMgr() {
        if (loginMgr == null) {
            loginMgr = new LoginMgr();
            addModule(loginMgr);
        }
        return loginMgr;
    }


    /**
     * 通知栏管理类
     */
    private static TipsBarMgr tipsBarMgr = null;

    /**
     * 获取通知栏管理器 唯一实例
     *
     * @return
     */
    public static TipsBarMgr getTipsBarMgr() {
        if (tipsBarMgr == null) {
            tipsBarMgr = new TipsBarMgr();
            addModule(tipsBarMgr);
        }
        return tipsBarMgr;
    }


    /**
     * 通用
     */
    private static CommonMgr commonMgr = null;

    public static CommonMgr getCommonMgr() {
        if (commonMgr == null) {
            commonMgr = new CommonMgr();
            addModule(commonMgr);
        }
        return commonMgr;
    }

    /**
     * 通知信息管理
     */
    private static NotifyMgr notifyMgr = null;

    public static NotifyMgr getNotifyMgr() {
        if (notifyMgr == null) {
            notifyMgr = new NotifyMgr();
            addModule(notifyMgr);
        }
        return notifyMgr;
    }

    /**
     * 未读角标管理
     */
    private static UnreadMgrImpl unreadImpl = null;

    public static UnreadMgr getUnreadMgr() {
        if (unreadImpl == null) {
            unreadImpl = new UnreadMgrImpl();
            addModule(unreadImpl);
        }
        return unreadImpl.getUnreadMgr();
    }

    /**
     * 聊天消息管理
     */
    private static ChatMgr chatMgr = null;

    public static ChatMgr getChatMgr() {
        if (chatMgr == null) {
            chatMgr = new ChatMgr();
            addModule(chatMgr);
        }
        return chatMgr;
    }

    /**
     * 聊天listMg管理
     */
    private static ChatListMgr chatListMgr = null;

    public static ChatListMgr getChatListMgr() {
        if (chatListMgr == null) {
            chatListMgr = new ChatListMgr();
            addModule(chatListMgr);
        }
        return chatListMgr;
    }

    private static PhizMgr phizMgr = null;

    public static PhizMgr getPhizMgr() {
        if (phizMgr == null) {
            phizMgr = new PhizMgr();
            addModule(phizMgr);
        }
        return phizMgr;
    }

    /**
     * 音视频聊天管理类
     */
    private static RtcEngineMgr engineMgr = null;

    public static RtcEngineMgr getRtcEnginMgr() {
        if (engineMgr == null) {
            engineMgr = new RtcEngineMgr();
            addModule(engineMgr);
        }
        return engineMgr;
    }
}
