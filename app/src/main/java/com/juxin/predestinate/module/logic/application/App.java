package com.juxin.predestinate.module.logic.application;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDexApplication;

import com.alibaba.sdk.android.httpdns.HttpDnsService;
import com.juxin.library.utils.HttpDnsInstance;
import com.juxin.predestinate.module.local.statistics.Source;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.ui.discover.ChatFragment;
import com.juxin.predestinate.ui.discover.DiscoverFragment;
import com.juxin.predestinate.ui.discover.DiscoverMFragment;
import com.juxin.predestinate.ui.friend.FriendFragment;
import com.juxin.predestinate.ui.live.bean.LiveStatusEnum;
import com.juxin.predestinate.ui.mail.FriendTaskDlg;
import com.juxin.predestinate.ui.mail.MailFragment;
import com.juxin.predestinate.ui.mail.chat.PrivateChatAct;
import com.juxin.predestinate.ui.main.MainActivity;
import com.juxin.predestinate.ui.setting.SettingAct;
import com.juxin.predestinate.ui.user.check.UserCheckInfoAct;
import com.juxin.predestinate.ui.user.fragment.UserFragment;
import com.juxin.predestinate.ui.user.my.EarnMoneyAct;

/**
 * Application
 * Created by @author ZRP on 2016/9/8.
 */
public class App extends MultiDexApplication {

    public static Context context;
    public static Activity activity;

    /**
     * 登录用户的uid（供运行时程序内调用）
     * //全局uid，避免重复从本地获取
     */
    public static long uid = 0;

    /**
     * 发送需要登录信息的Http请求使用。
     */
    public static String cookie = "";

    /**
     * 用户是否已经登录。该值暂时无效
     */
    public static boolean isLogin = false;

    /**
     * 是否锁屏中
     */
    public static volatile AppKeyguard isKeyguard = AppKeyguard.KG_INIT;

    /**
     * 用来互斥处理视频 、直播
     * 直播、视频状态
     */
    public static LiveStatusEnum liveStatus = LiveStatusEnum.LIVING_IDLE;

    private static PActivityLifecycleCallbacks lifecycleCallbacks;
    public static long t;
    public static HttpDnsService httpdns;

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreate() {
        super.onCreate();

        t = System.currentTimeMillis();
        context = getApplicationContext();

        lifecycleCallbacks = new PActivityLifecycleCallbacks();
        registerActivityLifecycleCallbacks(lifecycleCallbacks);

        ModuleMgr.initModule(context);
        //只能保证一个实例
        httpdns = HttpDnsInstance.getInstance(context).getHttpDNS();

        SourcePoint.getInstance().register(new SourcePoint.StackRegister() {
            @Override
            public void register(SourcePoint.Register register) {
                register.register("faxian_yuliao_listitem", MainActivity.class, DiscoverMFragment.class, ChatFragment.class);
                register.register("faxian_tuijian_userinfo", MainActivity.class, DiscoverMFragment.class, DiscoverFragment.class, UserCheckInfoAct.class);
                register.register("faxian_tuijian_userinfo_chatframe", MainActivity.class, DiscoverMFragment.class, DiscoverFragment.class, UserCheckInfoAct.class, PrivateChatAct.class);
                register.register("xiaoxi_listitem", MailFragment.class);
                register.register("xiaoxi_chatframe", MailFragment.class, PrivateChatAct.class);
                register.register("xiaoxi_chatframe_userinfo", MailFragment.class, PrivateChatAct.class, UserCheckInfoAct.class);
                register.register("haoyou_chatframe", MainActivity.class, FriendFragment.class, PrivateChatAct.class);
                register.register("me", MainActivity.class, UserFragment.class);
                register.register("me_setting", MainActivity.class, UserFragment.class, SettingAct.class);
//                register.register("me_mygem",MainActivity.class, UserFragment.class, MyDiamondsAct.class);
                register.register("me_money", MainActivity.class, UserFragment.class, EarnMoneyAct.class);
                register.register("tip_chatframe", Source.TIP.class, PrivateChatAct.class);
                register.register("haoyou_chatframe", MainActivity.class, FriendTaskDlg.class, PrivateChatAct.class);
            }
        });
    }

    /**
     * @return 获取进程生命周期回调
     */
    public static PActivityLifecycleCallbacks getLifecycleCallbacks() {
        return lifecycleCallbacks;
    }

    /**
     * @return 获取当前展示的activity对象，如果activity为null则返回applicationContext
     */
    public static Context getActivity() {
        return activity == null ? context : activity;
    }

    public static Context getContext() {
        return context;
    }

    /**
     * @return 判断最后的Activity是否属于前台显示
     */
    public static boolean isForeground() {
        return lifecycleCallbacks.isForeground();
    }

    /**
     * 应用锁屏状态
     */
    public enum AppKeyguard {
        //初始化
        KG_INIT,
        //锁屏中
        KG_SCREEN_OFF,
        //开屏中
        KG_SCREEN_ON
    }

    // -----------------------------------------------------

//    private static CacheComponent cacheComponent;
//
//    public static CacheComponent getCacheComponent() {
//        return cacheComponent;
//    }
//
//    /**
//     * 缓存初始化
//     */
//    private void initAppComponent() {
//        cacheComponent = DaggerCacheComponent.builder()
//                .cacheModule(new CacheModule((Application) getContext()))
//                .dBCache(new DBCache())
//                .build();
//    }
}
