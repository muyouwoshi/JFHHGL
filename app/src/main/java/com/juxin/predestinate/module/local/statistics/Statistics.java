package com.juxin.predestinate.module.local.statistics;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Process;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.juxin.library.dir.SDCardUtil;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.utils.DeviceUtils;
import com.juxin.library.utils.EncryptUtil;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.live.RoomPayInfo;
import com.juxin.predestinate.module.local.location.LocationMgr;
import com.juxin.predestinate.module.local.statistics.log.AppStatus;
import com.juxin.predestinate.module.local.statistics.log.LogModule;
import com.juxin.predestinate.module.local.statistics.log.LogType;
import com.juxin.predestinate.module.local.statistics.log.StatisticSend;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 各种统计管理类
 *
 * @author ZRP
 * @date 2017/4/13
 */
public class Statistics {

    // ------------------------------旧版本统计项-----------------------------------

    /**
     * 支付统计
     *
     * @param uid   交互对方uid
     * @param tplId 交互对方channel_uid
     */
    public static void payStatistic(String uid, String tplId) {
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(tplId)) {
            return;
        }

        PLogger.d("------>" + uid + "/" + tplId);
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("id", uid);
        getParams.put("tplId", tplId);
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.payStatistic, getParams, null);
    }

    // ------------------------应用内日志统计分析-------------------------

    /**
     * 日志统计分析
     *
     * @param logModule  日志标签，当前统计的是哪个模块
     * @param logTitle   异常信息描述
     * @param logContent [日志内容]json格式日志内容
     */
    public static void DEBUG(LogModule logModule, String logTitle, String logContent) {
        log(LogType.DEBUG, logModule, logTitle, logContent);
    }

    /**
     * 崩溃日志统计
     *
     * @param logTitle   异常信息描述
     * @param logContent [日志内容]异常代码堆栈,越详细越好
     */
    public static void CRASH(String logTitle, String logContent) {
        log(LogType.CRASH, LogModule.CRASH, logTitle, logContent);
    }

    /**
     * 日志统计分析
     *
     * @param logType    日志级别
     * @param logModule  日志标签
     * @param logTitle   异常信息描述
     * @param logContent [日志内容]异常代码堆栈,越详细越好
     */
    public static void log(LogType logType, LogModule logModule, String logTitle, String logContent) {
        if (TextUtils.isEmpty(logTitle)) {
            return;
        }
        Map<String, Object> singleMap = getCommonParams();

        // sd卡可用内存（MB）
        List<Integer> sdcardAvailableAll = SDCardUtil.getSdcardAvailableAll(App.getContext());
        singleMap.put("sd1storage_free", sdcardAvailableAll.isEmpty() ? 0 : sdcardAvailableAll.get(0));
        singleMap.put("sd2storage_free", sdcardAvailableAll.size() < 2 ? 0 : sdcardAvailableAll.get(1));
        // sd卡已用内存（MB）
        List<Integer> sdcardUsageAll = SDCardUtil.getSdcardUsageAll(App.getContext());
        singleMap.put("sd1storage_use", sdcardUsageAll.isEmpty() ? 0 : sdcardUsageAll.get(0));
        singleMap.put("sd2storage_use", sdcardUsageAll.size() < 2 ? 0 : sdcardUsageAll.get(1));

        // CPU使用百分比
        singleMap.put("cpu", DeviceUtils.getCpuUsage());
        // 内存可用（MB）
        int availableMemory = DeviceUtils.getAvailableMemory(App.getContext());
        singleMap.put("memory_free", availableMemory);
        // 内存已用（MB）
        singleMap.put("memory_use", DeviceUtils.getTotalMemory() - availableMemory);
        //电量剩余百分比
        singleMap.put("power", DeviceUtils.getBatteryPercentage(App.getContext()));
        //网络类型|WIFI|GPRS|3G|4G|2G(自己的标记内容)
        singleMap.put("network", NetworkUtils.getNetworkTypeName(App.getContext()));
        singleMap.put("root", DeviceUtils.isRooted());//是否ROOT或越狱

        //日志级别|INFO|DEBUG|WARN|ERROR|FATAL|CRASH(自己的标记内容)
        singleMap.put("log_level", logType.toString());
        singleMap.put("log_class", "Log");//日志打印类
        singleMap.put("log_process", ModuleMgr.getAppMgr().getProcessName(Process.myPid())
                + "/" + Thread.currentThread().getName());//当前进程和线程信息
        //日志标签,如(VIDEO,CHAT,LOGIN,PROFILE)(自己的标记内容)
        singleMap.put("log_tag", logModule.toString());
        singleMap.put("log_title", logTitle);//异常信息描述
        singleMap.put("log_stack", logContent);//异常代码堆栈,越详细越好

        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("topic", StatisticPoint.ClientLog);//统计项名称
        postParams.put("message", singleMap);//统计项数据内容
        sendStatistics(postParams, StatisticSend.CACHE);
    }

    // ---------------------------新版本统计-----------------------------

    private static final String BEHAVIOR_CACHE_KEY = "BEHAVIOR_CACHE_KEY";//用户行为缓存key
    private static final String BEHAVIOR_IMMEDIATELY_KEY = "BEHAVIOR_IMMEDIATELY_KEY";//立即发送消息类型发送失败时的缓存key
    private static final String BEHAVIOR_SESSION_KEY = "BEHAVIOR_SESSION_KEY";//用户行为session time校验存储key
    private static final String BEHAVIOR_SESSION_ID_KEY = "BEHAVIOR_SESSION_ID_KEY";//用户行为sessionId存储key
    private static final String BEHAVIOR_ACCOUNT_KEY = "BEHAVIOR_ACCOUNT_KEY";//是否切换帐号登录存储key，存储用户uid
    private static final long BEHAVIOR_SESSION_TIME = 30 * 60 * 1000;//session有效时间：30分钟

    /**
     * 客户端统计项
     */
    private enum StatisticPoint {
        Startup,                // APP启动日志:用户每启动一次APP,就发送一次最新的数据
        Shutdown,               // APP关闭事件日志
        UserBehavior,           // 客户端用户行为日志
        AppStatusChange,        // APP运行状态变更(进入前台/进入后台/启动/关闭)
        RoomPay,                // 直播间充值
        ClientLog,              // 客户端运行日志，包括崩溃及其他信息
    }

    /**
     * 每隔5min发送一次定位信息
     */
    public static void loopLocation() {
        MsgMgr.getInstance().delay(new Runnable() {
            @Override
            public void run() {
                location();
                loopLocation();
            }
        }, Constant.CHAT_RESEND_TIME, false);
    }

    /**
     * 向大数据发送位置统计
     */
    public static void location() {
        if (ModuleMgr.getLoginMgr().getUid() == 0) {
            return;
        }

        LocationMgr.PointD pointD = LocationMgr.getInstance().getPointD();
        String province = pointD.province;

        Map<String, Object> requestObject = new HashMap<>();
        requestObject.put("longitude", pointD.longitude);
        requestObject.put("latitude", pointD.latitude);
        requestObject.put("country_code", pointD.countryCode);
        requestObject.put("province", province);
        requestObject.put("city_code", pointD.city);
        requestObject.put("district", pointD.district);
        requestObject.put("street_number", pointD.streetNum);
        requestObject.put("building_id", pointD.buildID);

        PLogger.d("---location--->" + JSON.toJSONString(requestObject));
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.locationStatistics, requestObject, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                PLogger.d("---locationStatistics--->" + response.getResponseString());
            }
        });
    }

    /**
     * @return 获取大数据统计时通用的一些app参数
     */
    private static Map<String, Object> getCommonParams() {
        Map<String, Object> commonParams = new HashMap<>();

        UserDetail myInfo = ModuleMgr.getCenterMgr().getMyInfo();
        commonParams.put("uid", myInfo.getUid());//用户UID,未注册用户或新设备时返回0
        commonParams.put("time", ModuleMgr.getAppMgr().getSecondTime());//发送时间戳
        commonParams.put("gender", myInfo.getGender());//用户性别
        commonParams.put("client_type", Constant.PLATFORM_TYPE);//客户端类型
        commonParams.put("version", String.valueOf(Constant.SUB_VERSION));//客户端标记号
        commonParams.put("build_ver", ModuleMgr.getAppMgr().getVerCode());//客户端打包版本号
        // 渠道编号<主渠道>_<子渠道>,当uid=0时,取APP包内的渠道号
        commonParams.put("channel_id", myInfo.getUid() == 0
                ? (ModuleMgr.getAppMgr().getMainChannelID() + "_" + ModuleMgr.getAppMgr().getSubChannelID())
                : (myInfo.getChannel_uid() + "_" + myInfo.getChannel_sid()));
        commonParams.put("package_name", ModuleMgr.getAppMgr().getPackageName());//客户端包名
        commonParams.put("sign_name", EncryptUtil.sha1(ModuleMgr.getAppMgr().getSignature()));//客户端签名
        commonParams.put("device_id", ModuleMgr.getAppMgr().getDeviceID());//设备标识符,获取失败返回空字符串
        commonParams.put("client_ip", NetworkUtils.getIpAddressString());//客户端IP
        commonParams.put("device_model", android.os.Build.MODEL);//手机型号
        commonParams.put("device_os_version", android.os.Build.DISPLAY);//手机操作系统版本
        commonParams.put("screen_width", String.valueOf(ModuleMgr.getAppMgr().getScreenWidth()));//屏幕宽度
        commonParams.put("screen_height", String.valueOf(ModuleMgr.getAppMgr().getScreenHeight()));//屏幕高度
        commonParams.put("tracker_code", EncryptUtil.md5(ModuleMgr.getAppMgr().getDeviceID()));//追踪码MD5,访客(包含游客)唯一标识且终生唯一
        commonParams.put("session_id", EncryptUtil.md5(getSessionId()));//会话标识MD5,30分钟无操作失效

        return commonParams;
    }

    /**
     * APP启动日志:用户每启动一次APP,就发送一次最新的数据
     */
    public static void startUp() {
        Map<String, Object> singleMap = getCommonParams();

        singleMap.put("uid_env", ModuleMgr.getCenterMgr().getMyInfo().isB() ? 1 : 0);//用户当前环境|0A环境|1B环境
        // 是否是APP启动后发送的第1条Startup数据,统计APP启动次数字段
        singleMap.put("first_start", isSwitchAccount());
        // [opt]是否全量包  1为轻量包 2为全量包
        singleMap.put("package_type", Constant.PACKAGE_TYPE);
        // 2017.9.15 每次startUp都发送app_list数据
        singleMap.put("app_list", getInstalledApkList());//手机上已安装的APP软件列表

        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("topic", StatisticPoint.Startup);//统计项名称
        postParams.put("message", singleMap);//统计项数据内容
        sendStatistics(postParams, StatisticSend.IMMEDIATELY);

        // startUp的同时发送AppStatusChange#START
        appStatusChange(AppStatus.START);
    }

    /**
     * @return 用户切换帐号登录-false，同一帐号登录-true
     */
    private static boolean isSwitchAccount() {
        long account = PSP.getInstance().getLong(BEHAVIOR_ACCOUNT_KEY, -1);
        if (account == -1 || ModuleMgr.getLoginMgr().getUid() == account) {
            PSP.getInstance().put(BEHAVIOR_ACCOUNT_KEY, ModuleMgr.getLoginMgr().getUid());
            return false;
        }
        return true;
    }

    /**
     * @return 获取用户安装的软件列表
     */
    private static List<AppInfo> getInstalledApkList() {
        List<AppInfo> installedApkList = new LinkedList<>();
        Context context = App.getContext();
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            AppInfo appInfo = new AppInfo();
            PackageInfo packageInfo = packages.get(i);
            appInfo.setName(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
            appInfo.setSys_app((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            appInfo.setPackage_name(packageInfo.packageName);
            appInfo.setVersion_name(packageInfo.versionName);
            appInfo.setFirst_install_time(packageInfo.firstInstallTime);
            appInfo.setLast_update_time(packageInfo.lastUpdateTime);
            installedApkList.add(appInfo);
        }
        return installedApkList;
    }

    /**
     * APP关闭事件日志
     */
    public static void shutDown() {
        Map<String, Object> singleMap = new HashMap<>();
        singleMap.put("uid", ModuleMgr.getLoginMgr().getUid());//用户UID,获取失败返回0
        singleMap.put("time", ModuleMgr.getAppMgr().getSecondTime());//发送时间戳

        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("topic", StatisticPoint.Shutdown);//统计项名称
        postParams.put("message", singleMap);//统计项数据内容
        sendStatistics(postParams);

        // shutDown的同时发送AppStatusChange#CLOSE
        appStatusChange(AppStatus.CLOSE);
    }

    /**
     * APP运行状态变更统计(进入前台/进入后台/启动/关闭)
     *
     * @param appStatus APP运行状态
     */
    public static void appStatusChange(AppStatus appStatus) {
        Map<String, Object> singleMap = getCommonParams();
        singleMap.put("status", appStatus.getStatus());//运行状态

        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("topic", StatisticPoint.AppStatusChange);//统计项名称
        postParams.put("message", singleMap);//统计项数据内容
        sendStatistics(postParams);
    }

    /**
     * 直播间充值统计
     *
     * @param info 直播间充值信息
     */
    public static void roomPay(RoomPayInfo info) {
        if (info == null) {
            return;
        }
        Map<String, Object> singleMap = getCommonParams();
        singleMap.put("room_uid", info.room_uid);
        singleMap.put("room_id", info.room_id);
        singleMap.put("room_live_id", info.room_live_id);
        singleMap.put("room_live_time", info.room_live_time);
        singleMap.put("pay_type", info.pay_type);
        singleMap.put("product_id", info.product_id);
        singleMap.put("gem_num", info.gem_num);
        singleMap.put("price", info.price);
        singleMap.put("source", info.source);

        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("topic", StatisticPoint.RoomPay);//统计项名称
        postParams.put("message", singleMap);//统计项数据内容
        sendStatistics(postParams);
    }

    /**
     * 用户行为统计
     *
     * @param sendPoint 统计点
     */
    public static void userBehavior(SendPoint sendPoint) {
        userBehavior(sendPoint, 0);
    }

    /**
     * 用户行为统计
     *
     * @param sendPoint 统计点
     * @param to_uid    与其产生交互的用户uid
     */
    public static void userBehavior(SendPoint sendPoint, long to_uid) {
        userBehavior(sendPoint, to_uid, null);
    }

    /**
     * 用户行为统计
     *
     * @param sendPoint 统计点
     * @param fixParams 行为修正参考参数，map格式，内部拼接成json格式字符串
     */
    public static void userBehavior(SendPoint sendPoint, Map<String, Object> fixParams) {
        userBehavior(sendPoint, 0, fixParams);
    }

    /**
     * 用户行为统计
     *
     * @param sendPoint 统计点
     * @param to_uid    与其产生交互的用户uid
     * @param fixParams 行为修正参考参数，map格式，内部拼接成json格式字符串
     */
    public static void userBehavior(SendPoint sendPoint, long to_uid, Map<String, Object> fixParams) {
        userBehavior(sendPoint.toString(), to_uid, fixParams == null ? "{}" : JSON.toJSONString(fixParams));
    }

    /**
     * 用户行为统计
     *
     * @param sendPoint 统计点
     * @param to_uid    与其产生交互的用户uid
     * @param fixParams 行为修正参考参数，拼接成json格式字符串，如{"to_uid":80429386,"index":3}
     */
    public static void userBehavior(String sendPoint, long to_uid, String fixParams) {
        if (TextUtils.isEmpty(sendPoint)) {
            return;
        }

        Map<String, Object> singleMap = getCommonParams();
        singleMap.put("to_uid", to_uid);//与谁交互UID(可选)

        singleMap.put("uid_env", ModuleMgr.getCenterMgr().getMyInfo().isB() ? 1 : 0);//用户当前环境|0A环境|1B环境
        LinkedList<String> activities = App.getLifecycleCallbacks().getActivities();
        singleMap.put("page", App.getActivity().getClass().getSimpleName());//当前页面，栈顶activity
        singleMap.put("referer", activities.size() > 1 ?
                activities.get(activities.size() - 2) : "");//来源页面(可选)，栈中第二个activity
        singleMap.put("event_type", sendPoint);//事件类型
        singleMap.put("event_data", TextUtils.isEmpty(fixParams) ? "{}" : fixParams);//事件数据(可选,有数据需提供数据说明文档)

        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("topic", StatisticPoint.UserBehavior);//统计项名称
        postParams.put("message", singleMap);//统计项数据内容
        sendStatistics(postParams);
    }

    /**
     * @return 获取用户sessionId，该id本地使用时间戳进行标识
     */
    private static String getSessionId() {
        long sessionTime = PSP.getInstance().getLong(BEHAVIOR_SESSION_KEY, -1);
        String sessionId = PSP.getInstance().getString(BEHAVIOR_SESSION_ID_KEY, "");
        long currentTimeMillis = System.currentTimeMillis();
        if (sessionTime == -1 || TextUtils.isEmpty(sessionId) || currentTimeMillis - sessionTime > BEHAVIOR_SESSION_TIME) {
            PSP.getInstance().put(BEHAVIOR_SESSION_KEY, currentTimeMillis);
            PSP.getInstance().put(BEHAVIOR_SESSION_ID_KEY, String.valueOf(currentTimeMillis));
            return String.valueOf(currentTimeMillis);
        }
        PSP.getInstance().put(BEHAVIOR_SESSION_KEY, currentTimeMillis);
        return sessionId;
    }

    /**
     * 发送统计内容，默认缓存10条之后再发送
     *
     * @param postParams 提交参数map
     */
    private static void sendStatistics(HashMap<String, Object> postParams) {
        sendStatistics(postParams, StatisticSend.NORMAL);
    }

    /**
     * 发送统计内容
     *
     * @param postParams    提交参数map
     * @param statisticSend 统计发送策略
     */
    private static void sendStatistics(HashMap<String, Object> postParams, StatisticSend statisticSend) {
        final boolean isSendImmediately = (StatisticSend.IMMEDIATELY == statisticSend);
        if (postParams == null) {
            postParams = new HashMap<>();
        }
        LinkedList<HashMap<String, Object>> cachedList = null;
        try {
            cachedList = JSON.parseObject(
                    PSP.getInstance().getString(isSendImmediately ? BEHAVIOR_IMMEDIATELY_KEY : BEHAVIOR_CACHE_KEY, "[]"),
                    new TypeReference<LinkedList<HashMap<String, Object>>>() {
                    });
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
        if (cachedList == null) {
            cachedList = new LinkedList<>();
        }
        if ((!isSendImmediately && cachedList.size() < 10) || StatisticSend.CACHE == statisticSend) {
            cachedList.add(postParams);
            PSP.getInstance().put(BEHAVIOR_CACHE_KEY, JSON.toJSONString(cachedList));
            return;
        }
        if (NetworkUtils.isConnected(App.context)) {
            cachedList.add(postParams);

            LinkedList<Map<String, Object>> statisticsList = new LinkedList<>();
            Map<String, Object> batchMap = new HashMap<>();
            for (HashMap<String, Object> maps : cachedList) {
                Map<String, Object> headBodyMap = new HashMap<>();
                headBodyMap.put("headers", new HashMap<>());
                headBodyMap.put("body", JSON.toJSONString(maps));
                statisticsList.add(headBodyMap);
            }
            batchMap.put("data", statisticsList);
            final LinkedList<HashMap<String, Object>> tempList = cachedList;

            PLogger.d("---Statistics--->" + JSON.toJSONString(batchMap));
            ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.statistics, batchMap, new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    if (response.isOk()) {
                        PSP.getInstance().remove(isSendImmediately ? BEHAVIOR_IMMEDIATELY_KEY : BEHAVIOR_CACHE_KEY);
                    } else {
                        PSP.getInstance().put(isSendImmediately ? BEHAVIOR_IMMEDIATELY_KEY : BEHAVIOR_CACHE_KEY,
                                JSON.toJSONString(tempList));
                    }
                }
            });
        } else {
            cachedList.add(postParams);
            PSP.getInstance().put(isSendImmediately ? BEHAVIOR_IMMEDIATELY_KEY : BEHAVIOR_CACHE_KEY,
                    JSON.toJSONString(cachedList));
        }
    }
}
