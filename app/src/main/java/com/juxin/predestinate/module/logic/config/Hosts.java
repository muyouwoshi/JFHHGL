package com.juxin.predestinate.module.logic.config;

import com.juxin.library.log.PSP;
import com.juxin.predestinate.module.util.WebAppDownloader;
import com.juxin.predestinate.module.util.WebUtil;

/**
 * 地址配置
 *
 * @author ZRP
 * @date 2017/5/15
 */
public class Hosts {

    // --------------------逻辑服务器地址 start-----------------------

    public static String NO_HOST = "no_host";

    public static final String SERVER_TYPE_KEY = "SERVER_TYPE_KEY";//正式服测试服切换存储key
    // 0-正式服，1-测试服，对应以上几个host-array的position
    public static int SERVER_TYPE = PSP.getInstance().getInt(SERVER_TYPE_KEY, 1);

    public static final String[] TCP_HOST = {"sc.app.xiaoyo.net", "test.push.xiaoyouqipai.cn"};
    public static final String[] TCP_PORT = {"8823", "8823"};
    public static final String[] PHP_HOST = {"http://api2.app.xiaoyo.net/", "http://test.logic.ph2.xiaoyouqipai.cn/"};
    public static final String[] GO_HOST = {"http://g.api.xiaoyo.net/", "http://test.logic.go.xiaoyouqipai.cn/"};
    public static final String[] CONFIG_HOST = {"http://config.go.xiaoyo.net/", "http://test.logic.cfg.xiaoyouqipai.cn/"};
    public static final String[] UPLOAD_HOST = {"http://upload.img.xiaoyo.net/", "http://test.upload.img.xiaoyouqipai.cn/"};
    public static final String[] PAY_HOST = {"http://p.app.xiaoyo.net/", "http://test.logic.pay.xiaoyouqipai.cn/"};
    public static final String[] LIVE_HOST = {"http://livesvr.xiaoyo.net/", "http://test.live.http.xiaoyouqipai.cn/"};//live.http.liuzhiai.com
    public static final String[] MATERIAL_HOST = {"http://yfbapi.tangzisoftware.com/", "http://tbk.tangzisoftware.com/"};
    private static final String[] CUP_HOST = {"http://pay.app.mumu123.cn/"};

    public static String FATE_IT_TCP = TCP_HOST[SERVER_TYPE];         //socket地址
    public static int FATE_IT_TCP_PORT = Integer.valueOf(TCP_PORT[SERVER_TYPE]);//socket端口
    public static String FATE_IT_HTTP = PHP_HOST[SERVER_TYPE];        //php地址
    public static String FATE_IT_GO = GO_HOST[SERVER_TYPE];           //go地址
    public static String FATE_CONFIG = CONFIG_HOST[SERVER_TYPE];      //在线配置host地址
    public static String FATE_IT_HTTP_PIC = UPLOAD_HOST[SERVER_TYPE]; //图片地址
    public static String FATE_IT_PROTOCOL = PAY_HOST[SERVER_TYPE];    //支付地址
    public static String FATE_IT_LIVE = LIVE_HOST[SERVER_TYPE];       //直播服务地址
    public static String FATE_IT_MATERIAL = MATERIAL_HOST[SERVER_TYPE]; //分享素材
    public static final String FATE_IT_CUP_HTTP = CUP_HOST[0];        //银联地址

    public static final String[] DOMAINNAME_BACKUP_TEST = new String[]{"http://domain.13.xiaoyouapp.cn/", "http://domain.13.xiaoyouapp.cn/", "http://domain.13.xiaoyouapp.cn/", "http://domain.13.xiaoyouapp.cn/"};//灾备域名
    public static final String[] DOMAINNAME_BACKUP_ONLINE = new String[]{"http://zhaoaiba.cn/", "http://zheiyuan.com/", "http://kouyin8.com/", "http://123.57.47.76/"};//批量查询有用域名，这个功能只能在线测试，不管测试包还是线上包，都用的这个地址
    public static final String[][] DOMAINNAME_BACKUP = {DOMAINNAME_BACKUP_ONLINE, DOMAINNAME_BACKUP_TEST};
    public static final String[] DOMAINTEST_ONLINE_ARR = DOMAINNAME_BACKUP[SERVER_TYPE];

    public static String HOST_URL = FATE_IT_HTTP;                     //默认host地址
    // -------------------逻辑服务器地址 end------------------------

    // ---------------------H5路径 start--------------------

    public static String WEB_APP_ROOT = new String[]{"https://page.xiaoyaoai.cn/xiaoyou-b/",
            "https://ow.xiaoyouapp.cn/yfb_pages/xiaoyou-b/"}[SERVER_TYPE];

    public static String H5_RANKING = WEB_APP_ROOT + "pages/windRanking/windRanking.html";  // 排行榜
    public static String H5_ACTION = WEB_APP_ROOT + "pages/setting/activity.html";          // 活动相关
    public static String H5_PREPAID = WEB_APP_ROOT + "pages/vipAndKey/vipAndKey.html";      // VIP和购买钥匙页面
    public static String H5_PREPAID_Y = WEB_APP_ROOT + "pages/prepaid/prepaid.html";        // VIP和购买Y币页面
    public static String H5_GIFT = WEB_APP_ROOT + "pages/myGift/myGift.html";               // 我的礼物
    public static String H5_BILL = WEB_APP_ROOT + "pages/setting/tollCollection.html";      // 话费领取
    public static String H5_ROTARY = WEB_APP_ROOT + "pages/turntable/turntable.html";       // 大转盘设置
    public static String H5_EARN_RED_BAG = WEB_APP_ROOT + "pages/earnRedBag/earn-red-bag.html";    //我要赚红包
    public static String H5_SHARE_PROMOTION_MONEY = WEB_APP_ROOT + "pages/share/shareQrcode.html";    //我的推广—去推广赚钱
    public static String H5_SHARE_WANTMONEY = WEB_APP_ROOT + "pages/share/share2.html";    //我要赚钱—分享赚钱

    public static String H5_EARN_MONEY = WEB_APP_ROOT + "pages/earnMoney/earnMoney.html";            //我要赚钱
    public static String H5_VIDEO_INVITE = WEB_APP_ROOT + "pages/activeVideoInvite/videoInvite.html";//主动视频邀请
    public static String H5_ASK_FOR_GIFT = WEB_APP_ROOT + "pages/askForGift/askForGift.html";        //索要礼物
    public static String H5_INCOME_DETAIL = WEB_APP_ROOT + "pages/incomeDetail/income-detail.html";  //收入明细
    public static String H5_MASS_VOICE = WEB_APP_ROOT + "pages/message/massVoice.html";              //群发语音
    public static String H5_MY_LEVEL = WEB_APP_ROOT + "pages/myLevel/myLevel.html";                  //我的等级
    public static String H5_LIVE_LIST = WEB_APP_ROOT + "pages/square/video-square.html";             //直播列表
    public static String H5_LITE_LIVE_VIEW = WEB_APP_ROOT + "pages/liteView/lite-view-new.html";      //Lite版直播间
    public static String H5_TITLE_PRIVILEGE = WEB_APP_ROOT + "pages/knight/knightPrivilege.html";    //爵位特权
    public static String H5_INTIMATE_FRIEND_EXPLAIN = WEB_APP_ROOT + "pages/intimateFriend/intimateFriend.html";    //密友说明
    public static String H5_DUKE_RANKING = WEB_APP_ROOT + "pages/DukeRanking/DukeRanking.html";      // 爵位排行榜
    public static String H5_ANCHOR_AGREEMENT = WEB_APP_ROOT + "pages/copyright/AnchorAgreement.html";

    public static String H5_APP_ABOUT = WEB_APP_ROOT + "pages/copyright/about.html";                  //关于页面
    public static String H5_APP_ANCHOR_MGR = WEB_APP_ROOT + "pages/copyright/AnchorManagement.html";  //主播管理规范
    public static String H5_APP_PLATFORN_MGR = WEB_APP_ROOT + "pages/copyright/platformSpecific.html";//平台规范
    public static String H5_APP_PRIVACY_POLICY = WEB_APP_ROOT + "pages/copyright/privacyPolicy.html"; // 隐私政策
    public static String H5_APP_REGISTER_AGREE = WEB_APP_ROOT + "pages/copyright/registerAgree.html"; // 用户注册协议
    public static String H5_APP_TIPS = WEB_APP_ROOT + "pages/copyright/tips.html";                    // 交友注意事项
    public static String H5_APP_LIVE_STAT = WEB_APP_ROOT + "pages/liveCount/liveCount.html";

    // ----------------------音视频相关H5------------------------

    public static String H5_VIDEO_ROOT = new String[]{"https://page.xiaoyaoai.cn/VideoWeb/",
            "https://ow.xiaoyouapp.cn/yfb_pages/VideoWeb/"}[SERVER_TYPE];

    // ----------------------捕女神游戏------------------------

    public static String H5_GAME_LOLITA_ROOT = new String[]{"https://page.xiaoyaoai.cn/catch_girl/",
            "https://ow.xiaoyouapp.cn/yfb_pages/catch_girl/"}[SERVER_TYPE];
    public static String H5_GAME_CATCH_LOLITA = H5_GAME_LOLITA_ROOT + "index.html";

    // ----------------------分享---------------------------

    public static String H5_SHARE_ROOT = new String[]{"https://page.xiaoyaoai.cn/share/",
            "https://ow.xiaoyouapp.cn/yfb_pages/share/"}[SERVER_TYPE];
    public static String H5_SHARE_EXTENSION = H5_SHARE_ROOT + "page/extension.html";
    public static String H5_SHARE_REQ = H5_SHARE_ROOT + "page/issueList.html";
    public static String H5_SHARE_TESKS = H5_SHARE_ROOT + "page/shareTasks.html";
    public static String H5_SHARE_CODE = H5_SHARE_ROOT + "page/selectModel.html";
    public static String H5_SHARE_TOPLIST = H5_SHARE_ROOT + "page/topList.html";
    public static String H5_SHARE_PROMOTION = H5_SHARE_ROOT + "page/popularize.html";    //我的推广

    /**
     * 文件下载完成之后同步本地地址
     */
    public static void initWebAppUrl() {
        // 应用内h5文件
        String LOCAL_WEB_APP_ROOT = "file://" +
                PSP.getInstance().getString(WebAppDownloader.KEY_WEB_APP_ROOT, WEB_APP_ROOT) + "/";

        H5_RANKING = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/windRanking/windRanking.html");  // 排行榜
        H5_ACTION = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/setting/activity.html");          // 活动相关
        H5_PREPAID = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/vipAndKey/vipAndKey.html");      // VIP和购买钥匙页面
        H5_PREPAID_Y = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/prepaid/prepaid.html");        // VIP和购买Y币页面
        H5_GIFT = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/myGift/myGift.html");               // 我的礼物
        H5_BILL = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/setting/tollCollection.html");      // 话费领取
        H5_ROTARY = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/turntable/turntable.html");       // 大转盘设置
        H5_EARN_RED_BAG = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/earnRedBag/earn-red-bag.html");    //我要赚红包
        H5_SHARE_PROMOTION_MONEY = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/share/shareQrcode.html"); //我的推广—去推广赚钱
        H5_SHARE_WANTMONEY = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/share/share2.html");           //我要赚钱—分享赚钱

        H5_EARN_MONEY = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/earnMoney/earnMoney.html");            //我要赚钱
        H5_VIDEO_INVITE = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/activeVideoInvite/videoInvite.html");//主动视频邀请
        H5_ASK_FOR_GIFT = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/askForGift/askForGift.html");        //索要礼物
        H5_INCOME_DETAIL = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/incomeDetail/income-detail.html");  //收入明细
        H5_MASS_VOICE = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/message/massVoice.html");              //群发语音
        H5_MY_LEVEL = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/myLevel/myLevel.html");                  //我的等级
        H5_LIVE_LIST = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/square/video-square.html");             //直播列表
        H5_TITLE_PRIVILEGE = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/knight/knightPrivilege.html");    //爵位特权
        H5_INTIMATE_FRIEND_EXPLAIN = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/intimateFriend/intimateFriend.html");    //密友说明
        H5_DUKE_RANKING = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/DukeRanking/DukeRanking.html");      // 爵位排行榜

        H5_APP_ABOUT = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/copyright/about.html");                  //关于页面
        H5_APP_ANCHOR_MGR = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/copyright/AnchorManagement.html");  //主播管理规范
        H5_APP_PLATFORN_MGR = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/copyright/platformSpecific.html");//平台规范
        H5_APP_PRIVACY_POLICY = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/copyright/privacyPolicy.html"); // 隐私政策
        H5_APP_REGISTER_AGREE = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/copyright/registerAgree.html"); // 用户注册协议
        H5_APP_TIPS = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/copyright/tips.html");                    // 交友注意事项
        H5_APP_LIVE_STAT = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/liveCount/liveCount.html");
        H5_ANCHOR_AGREEMENT = WebUtil.getHtmlPath(WEB_APP_ROOT, LOCAL_WEB_APP_ROOT, "pages/copyright/AnchorAgreement.html");

        // 游戏模块h5文件
        String LOCAL_GAME_LOLITA_ROOT = "file://" +
                PSP.getInstance().getString(WebAppDownloader.KEY_WEB_GAME_LOLITA_ROOT, H5_GAME_LOLITA_ROOT) + "/";
        H5_GAME_CATCH_LOLITA = WebUtil.getHtmlPath(H5_GAME_LOLITA_ROOT, LOCAL_GAME_LOLITA_ROOT, "index.html");

        // ----------------------分享---------------------------
        String LOCAL_SHARE_ROOT = "file://" +
                PSP.getInstance().getString(WebAppDownloader.KEY_WEB_SHARE_ROOT, H5_SHARE_ROOT) + "/";
        H5_SHARE_EXTENSION = WebUtil.getHtmlPath(H5_SHARE_ROOT, LOCAL_SHARE_ROOT, "page/extension.html");
        H5_SHARE_REQ = WebUtil.getHtmlPath(H5_SHARE_ROOT, LOCAL_SHARE_ROOT, "page/issueList.html");
        H5_SHARE_TESKS = WebUtil.getHtmlPath(H5_SHARE_ROOT, LOCAL_SHARE_ROOT, "page/shareTasks.html");
        H5_SHARE_CODE = WebUtil.getHtmlPath(H5_SHARE_ROOT, LOCAL_SHARE_ROOT, "page/selectModel.html");
        H5_SHARE_TOPLIST = WebUtil.getHtmlPath(H5_SHARE_ROOT, LOCAL_SHARE_ROOT, "page/topList.html");
        H5_SHARE_PROMOTION = WebUtil.getHtmlPath(H5_SHARE_ROOT, LOCAL_SHARE_ROOT, "page/popularize.html");//我的推广
    }
    // ---------------------H5路径 finish--------------------

    // -----------------------备份地址 start---------------------------

    // 广场本地备份地址
    public static final String LOCAL_SQUARE_URL = "https://page.xiaoyaoai.cn/app/pages/square/square.html";
    // -----------------------备份地址 end---------------------------
}
