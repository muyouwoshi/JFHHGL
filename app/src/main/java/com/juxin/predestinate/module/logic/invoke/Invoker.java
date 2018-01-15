package com.juxin.predestinate.module.logic.invoke;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.juxin.library.dir.DirType;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.utils.BitmapUtil;
import com.juxin.library.utils.FileUtil;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.detail.UserInfo;
import com.juxin.predestinate.bean.my.MTitle;
import com.juxin.predestinate.bean.my.MultiTitle;
import com.juxin.predestinate.module.local.album.ImgSelectUtil;
import com.juxin.predestinate.module.local.location.LocationMgr;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsPay;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.baseui.WebActivity;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.FinalKey;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.logic.media.MediaMgr;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.BaseUtil;
import com.juxin.predestinate.module.util.ChineseFilter;
import com.juxin.predestinate.module.util.GamePlayer;
import com.juxin.predestinate.module.util.JsonUtil;
import com.juxin.predestinate.module.util.LiveHelper;
import com.juxin.predestinate.module.util.MediaNotifyUtils;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.WebAppDownloader;
import com.juxin.predestinate.module.util.WebUtil;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.live.event.LiveCmdBusEvent;
import com.juxin.predestinate.ui.main.MainActivity;
import com.juxin.predestinate.ui.user.my.RankAct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.juxin.predestinate.module.logic.application.App.context;
import static com.juxin.predestinate.module.logic.application.ModuleMgr.getCenterMgr;

/**
 * CMD操作统一处理
 * Created by ZRP on 2017/1/3.
 */
public class Invoker {

    // js cmd key
    public static final String JSCMD_refresh_web = "refresh_web";//主动调用刷新web页面
    public static final String JSCMD_ranking_btn_click = "change_ranking_list";// 风云榜按钮点击事件（本周上周切换）
    public static final String JSCMD_header_right_btn_click = "header_right_btn_click";// 导航条右侧按钮点击事件
    public static final String JSCMD_turntable_result = "turntable_result";// 转盘转动结果(同步他人客户端开始抽奖)
    public static final String JSCMD_chat_message = "chat_message";// 聊天信息
    public static final String JSCMD_gift_message = "gift_message";// 礼物信息
    public static final String JSCMD_turntable_activity = "turntable_activity";//启动关闭转盘
    public static final String JSCMD_close_game = "close_game";// 点击返回按钮 （webview上的返回按钮）
    public static final String JSCMD_keyboard_operation = "keyboard_operation";
    public static final String JSCMD_warning_message = "warning_message";//视频警告消息
    public static final String JSCMD_audience_leave = "audience_leave";//观众离开直播间
    public static final String JSCMD_open_share_guide_page = "open_share_guide_page";//调起h5引导弹出层

    private WebAppInterface appInterface = new WebAppInterface(context, null);
    private Object webView;

    /**
     * @return 获取持有的WebAppInterface实例
     */
    public WebAppInterface getWebAppInterface() {
        return appInterface;
    }

    //--------------------CMD处理start--------------------

    private static class SingletonHolder {
        public static Invoker instance = new Invoker();
    }

    public static Invoker getInstance() {
        return SingletonHolder.instance;
    }

    private Invoke invoke = new Invoke();

    /**
     * 根据command和data执行对应方法（处理在app内情况）
     *
     * @param appInterface WebAppInterface实例
     * @param cmd          CMD操作码
     * @param data         操作执行的json字符串数据
     */
    public void doInApp(WebAppInterface appInterface, final String cmd, final String data) {
        PLogger.d("---doInApp--->cmd：" + cmd + "，data：" + data +
                "，AppMgr.isForground()：" + App.isForeground());

        this.appInterface = (appInterface == null ? new WebAppInterface(context, null) : appInterface);
        webView = appInterface == null ? null : appInterface.getWebView();

        MsgMgr.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Method notifyCMDMethod = Invoke.class.getMethod(cmd, String.class);
                    notifyCMDMethod.invoke(invoke, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 本地调用执行JS逻辑
     *
     * @param cmd  JS cmd命令
     * @param data 传递给JS的数据
     */
    public void doInJS(String cmd, Map<String, Object> data) {
        Map<String, Object> cmdMap = new HashMap<>();
        cmdMap.put("jcmd", cmd);
        cmdMap.put("data", data == null ? new HashMap<>() : data);
        String url = "window.platform.appCommand('" + JSON.toJSONString(cmdMap) + "')";
        doInJS(url);
    }

    /**
     * 本地调用执行JS逻辑，默认进行字符串转码，以支持中文字符串的传输
     *
     * @param callbackName   回调方法名
     * @param callbackID     回调id
     * @param responseString 是否进行字符串转码
     */
    public void doInJS(String callbackName, String callbackID, String responseString) {
        doInJS(callbackName
                + "(\'" + callbackID + "\',\'"
                + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                ? responseString : URLEncoder.encode(responseString))
                + "\')");
    }

    /**
     * 本地调用执行JS逻辑
     *
     * @param loadUrl 调用执行的命令
     */
    public void doInJS(String loadUrl) {
        if (webView != null) {
            if (webView instanceof com.tencent.smtt.sdk.WebView) {
                PLogger.d("---tencent x5--->" + loadUrl);
                ((com.tencent.smtt.sdk.WebView) webView).evaluateJavascript(loadUrl, null);
            } else if (webView instanceof android.webkit.WebView) {
                PLogger.d("---webkit--->" + loadUrl);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    ((android.webkit.WebView) webView).evaluateJavascript(loadUrl, null);
                } else {
                    ((android.webkit.WebView) webView).loadUrl("javascript:" + loadUrl);
                }
            }
        }
    }

    /**
     * 设置webView，每次webView页面可见的时候重设
     *
     * @param webView WebView实例
     */
    public void setWebView(Object webView) {
        this.webView = webView;
        PLogger.d("------>" + String.valueOf(webView));
    }

    //--------------------CMD处理end--------------------

    public class Invoke {

        // 显示加载loading
        public void show_data_loading(String data) {
            PLogger.d("show_data_loading: ------>" + data);
            Activity act = appInterface.getAct();
            LoadingDialog.show((FragmentActivity) (act == null ? App.getActivity() : act));
        }

        // 关闭加载loading
        public void hide_data_loading(String data) {
            PLogger.d("hide_data_loading: ------>" + data);
            LoadingDialog.closeLoadingDialog(500);
        }

        // 私聊指令
        public void cmd_open_chat(String data) {
            PLogger.d("cmd_open_chat: ------>" + data);
            Activity act = appInterface.getAct();
            JSONObject dataObject = JsonUtil.getJsonObject(data);
            UIShow.showPrivateChatAct(act == null ? App.getActivity() : act,
                    dataObject.optLong("uid"), dataObject.optString("nickname"));
        }

        // 打开游戏页面
        public void open_game_web(String data) {
            PLogger.d("---open_game_web--->" + data);
            Activity act = appInterface.getAct();
            JSONObject dataObject = JsonUtil.getJsonObject(data);
            int type = dataObject.optInt("type");//打开方式（1为侧滑页面，2为全屏页面，全屏时显示loading条）
            String url = dataObject.optString("url");//游戏url
            String title = dataObject.optString("title");//标题
            String gameName = dataObject.optString("gameName");//游戏名称
            SourcePoint.getInstance().lockSource("faxian_grabgoddess").clearPPCSource();//添加游戏页面的统计来源,清除ppc统计（H5加）
            UIShow.showWebActivity(act == null ? App.getActivity() : act, type, url, gameName);
        }

        // 关闭loading页面
        public void hide_loading(String data) {
            PLogger.d("---hide_loading--->" + data);
            try {
                Activity act = appInterface.getAct();
                if (act != null && act instanceof WebActivity) {
                    ((WebActivity) act).hideLoading();
                }
                LoadingDialog.closeLoadingDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 播放音效，url为音频相对地址
        public void play_sound(String data) {
            PLogger.d("---play_sound--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);
            // 1 url xxx.mp3；2 is_long：是否是长音频：0-不是，1-是；3 is_loop 是否循环播放   true循环播放 fasle播放一次
            String name = dataObject.optString("url");
            name = name.substring(0, name.indexOf("."));
            GamePlayer.getInstance().playRawSound(BaseUtil.getResId(name), dataObject.optBoolean("is_loop"));
        }

        //停止播放所有背景音效
        public void stop_all_sounds(String data) {
            PLogger.d("---stop_all_sounds--->" + data);
            GamePlayer.getInstance().stop();
        }

        // 结束当前游戏页面
        public void do_finish(String data) {
            PLogger.d("---do_finish--->" + data);
            // [注意]：当前游戏页面必须是继承自BaseActivity.class
            Activity act = appInterface.getAct();
            if (act != null) act.finish();
        }

        // 获取当前用户的uid和auth
        public void get_app_data(String data) {
            PLogger.d("---get_app_data--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);

            UserInfo userInfo = getCenterMgr().getMyInfo();
            Map<String, Object> responseObject = new HashMap<>();
            responseObject.put("uid", userInfo.getUid());
            responseObject.put("auth", ModuleMgr.getLoginMgr().getCookie());
            responseObject.put("user_type", ModuleMgr.getCenterMgr().getMyInfo().isB() ? 3 : 1);//钥匙环境type=3，其他为1
            responseObject.put("gender", userInfo.getGender());
            responseObject.put("is_vip", userInfo.isVip());
            responseObject.put("version", 2);//1为之前的缘分吧版本（礼物版），2为红包来了之后的缘分吧版本

            doInJS(dataObject.optString("callbackName"), dataObject.optString("callbackID"), JSON.toJSONString(responseObject));
        }

        // 根据uid获取用户信息
        public void get_user_info(String data) {
            PLogger.d("---get_user_info--->" + data);
            final JSONObject dataObject = JsonUtil.getJsonObject(data);
            getCenterMgr().reqOtherInfo(dataObject.optLong("uid"), new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    if (!response.isOk()) return;

                    UserDetail userDetail = (UserDetail) response.getBaseData();
                    Map<String, Object> responseObject = new HashMap<>();
                    responseObject.put("uid", userDetail.getUid());
                    responseObject.put("avatar", userDetail.getAvatar());
                    responseObject.put("avatar_status", userDetail.getAvatar_status());
                    responseObject.put("nickname", userDetail.getNickname());
                    responseObject.put("gender", userDetail.getGender());
                    responseObject.put("is_vip", userDetail.isVip());

                    doInJS(dataObject.optString("callbackName"), dataObject.optString("callbackID"), JSON.toJSONString(responseObject));
                }
            });
        }

        // 获得用户自己的数据 （包含用户的账户余额等数据）
        public void get_user_detail(String data) {
            PLogger.d("---get_user_detail--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);

            UserDetail userInfo = ModuleMgr.getCenterMgr().getMyInfo();
            Map<String, Object> responseObject = new HashMap<>();
            responseObject.put("uid", userInfo.getUid());
            responseObject.put("auth", ModuleMgr.getLoginMgr().getCookie());
            responseObject.put("avatar", userInfo.getAvatar());
            responseObject.put("avatar_status", userInfo.getAvatar_status());
            responseObject.put("nickname", userInfo.getNickname());
            responseObject.put("gender", userInfo.getGender());
            responseObject.put("is_vip", userInfo.isVip());
            //responseObject.put("money", userInfo.getMoney());
            responseObject.put("diamond_count", userInfo.getDiamand());
            responseObject.put("longitude", LocationMgr.getInstance().getPointD().longitude);
            responseObject.put("latitude", LocationMgr.getInstance().getPointD().latitude);
            responseObject.put("video_price", userInfo.getChatInfo().getVideoPrice());
            responseObject.put("voice_price", userInfo.getChatInfo().getAudioPrice());
            //此处实际上为versionName，h5字段定义偏差
            responseObject.put("version_code", ModuleMgr.getAppMgr().getVerName());
            responseObject.put("app_version", Constant.SUB_VERSION);
            responseObject.put("is_b", ModuleMgr.getCenterMgr().getMyInfo().isB());//是否为钥匙B版
            responseObject.put("height", ModuleMgr.getCenterMgr().getMyInfo().getHeight());
            responseObject.put("age", ModuleMgr.getCenterMgr().getMyInfo().getAge());

            Map<String, Object> responseObject2 = new HashMap<>();
            responseObject2.put("rank", userInfo.getNobilityInfo().getRank());
            responseObject2.put("cmpprocess", userInfo.getNobilityInfo().getCmpprocess());
            responseObject.put("nobility", responseObject2);
            doInJS(dataObject.optString("callbackName"), dataObject.optString("callbackID"), JSON.toJSONString(responseObject));
        }

        // 打开本地H5页面，只对应yfb资源
        public void open_local_page(String data) {
            PLogger.d("---open_local_page--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);
            Activity act = appInterface.getAct();

            String sp_key = WebAppDownloader.KEY_WEB_APP_ROOT;
            String web_root = Hosts.WEB_APP_ROOT;
            switch (dataObject.optString("module")) {
                case "webapp":
                    sp_key = WebAppDownloader.KEY_WEB_APP_ROOT;
                    web_root = Hosts.WEB_APP_ROOT;
                    break;
                case "video":
                    sp_key = WebAppDownloader.KEY_WEB_VIDEO_ROOT;
                    web_root = Hosts.H5_VIDEO_ROOT;
                    break;
                case "game_catch_lolita":
                    sp_key = WebAppDownloader.KEY_WEB_GAME_LOLITA_ROOT;
                    web_root = Hosts.H5_GAME_LOLITA_ROOT;
                    break;
                case "share":
                    sp_key = WebAppDownloader.KEY_WEB_SHARE_ROOT;
                    web_root = Hosts.H5_SHARE_ROOT;
                    break;
                default:
                    break;
            }
            // 本地h5资源解压路径
            String LOCAL_WEB_APP_ROOT = "file://" + PSP.getInstance().getString(sp_key, web_root) + "/";
            // 对本地h5资源进行兼容处理，防止文件夹被删之后无法读取跳转
            String url = WebUtil.getHtmlPath(web_root, LOCAL_WEB_APP_ROOT, dataObject.optString("url"));
            UIShow.showWebActivity(act == null ? App.getActivity() : act,
                    dataObject.optInt("type"), WebUtil.jointUrl(url), "");
        }

        /**
         * 打开多标签页
         */
        public void open_multi_title_page(String data) {
            PLogger.d("---open_multi_title_page--->" + data);
            MultiTitle multiTitle = new MultiTitle();
            multiTitle.parseJson(data);
            Activity act = appInterface.getAct();
            if(multiTitle.getMultiTitle() == null || multiTitle.getMultiTitle().isEmpty()) {
                return;
            }
            ArrayList<MTitle> mTitles = multiTitle.getMultiTitle();
            String sp_key = WebAppDownloader.KEY_WEB_APP_ROOT;
            String web_root = Hosts.WEB_APP_ROOT;
            switch (multiTitle.getModule()) {
                case "webapp":
                    sp_key = WebAppDownloader.KEY_WEB_APP_ROOT;
                    web_root = Hosts.WEB_APP_ROOT;
                    break;
                case "video":
                    sp_key = WebAppDownloader.KEY_WEB_VIDEO_ROOT;
                    web_root = Hosts.H5_VIDEO_ROOT;
                    break;
                case "game_catch_lolita":
                    sp_key = WebAppDownloader.KEY_WEB_GAME_LOLITA_ROOT;
                    web_root = Hosts.H5_GAME_LOLITA_ROOT;
                    break;
                case "share":
                    sp_key = WebAppDownloader.KEY_WEB_SHARE_ROOT;
                    web_root = Hosts.H5_SHARE_ROOT;
                    break;
                default:
                    break;
            }
            // 本地h5资源解压路径
            String LOCAL_WEB_APP_ROOT = "file://" + PSP.getInstance().getString(sp_key, web_root) + "/";
            // 对本地h5资源进行兼容处理，防止文件夹被删之后无法读取跳转
            String url = WebUtil.getHtmlPath(web_root, LOCAL_WEB_APP_ROOT, mTitles.get(0).getUrl());
            UIShow.showMultiTitle(act == null ? App.getActivity() : act, url, multiTitle);
        }

        //分享给美女、帅哥
        public void share_to_people(String data) {
            PLogger.d("---share_to_people--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);
            Activity act = appInterface.getAct();
            UIShow.showShareCodeAct(act == null ? App.getActivity() : act, dataObject.optInt("gender"));
        }

        // 弹出模态对话框
        public void show_dialog(String data) {
            PLogger.d("---show_dialog--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);

            Activity act = appInterface.getAct();
            Activity context = (act == null ? (Activity) App.getActivity() : act);
            //展示游戏弹框
            switch (dataObject.optInt("code")) {
                case 101://弹出灵气不足弹框
                    break;
                case 104://弹出分享恢复灵气弹框
                    break;
                case 105://弹出添加好友分享弹框
                    break;
                case 106://分享获得哈士奇
                    break;
                case 201://弹出购买VIP弹框
                    break;
                case 202://获得斗牛犬，开通VIP弹框样式
                    break;
                case 301://弹出开通vip服务弹框：提示是高级场需要开通vip，下面只去掉赠送体力提示
                    break;
                case 401://弹出购买y币弹框
                    break;
            }
        }

        // 跳转到跟指定uid用户私聊界面
        public void jump_to_chat(String data) {
            PLogger.d("---jump_to_chat--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);

            Activity act = appInterface.getAct();
            UIShow.showPrivateChatAct(act == null ? context : act, dataObject.optLong("target_uid"), "");
        }

        // 侧滑出app页面
        public void jump_to_app(String data) {
            PLogger.d("---jump_to_app--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);

            //跳转到应用内页面
            Activity act = appInterface.getAct();
            Activity context = (act == null ? (Activity) App.getActivity() : act);
            int code = dataObject.optInt("code");
            switch (code) {
                case 501://跳转到话费领取页面
                    UIShow.showBillCollectionActivity(context);
                    break;
                case 502://跳转到Y币购买页面
                    if (ModuleMgr.getCenterMgr().getMyInfo().isB()) {
                        //跳转购买钥匙
                        UIShow.showBuyKeyActivity(context);
                        StatisticsPay.TJ_CMDKey();
                    } else {
                        UIShow.showBuyCoinActivity(context);
                    }
                    break;
                case 503://跳转到设置-活动相关页面
                    UIShow.showActionActivity(context);
                    break;
                case 504://跳转主动视频邀请页面
                case 505://主动语音邀请页面
                case 506://动索要礼物 图片视频要礼物界面
                    go2EarnMoney(code, context);
                    break;
                case 507://设置大转盘
                    UIShow.showRotaryActivity(context);
                    break;
                case 508://群发视频
                    VideoAudioChatHelper.getInstance().girlGroupInvite(context, AgoraConstant.RTC_CHAT_VIDEO);
                    break;
                case 509://群发语音
                    VideoAudioChatHelper.getInstance().girlGroupInvite(context, AgoraConstant.RTC_CHAT_VOICE);
                    break;
                case 510://等级详情
                    UIShow.showMyLevel(context);
                    break;
                case 511://关于页面
                    UIShow.showAboutAct(context);
                    break;
                case 512://缘分吧主播管理规范
                    UIShow.showAnchorManage(context);
                    break;
                case 513://缘分吧平台规范
                    UIShow.showPlatformSpecific(context);
                    break;
                case 514://隐私政策
                    UIShow.showPrivacyPolicy(context);
                    break;
                case 515://用户注册协议
                    UIShow.showRegisterAgree(context);
                    break;
                case 516://交友注意事项
                    UIShow.showAppTips(context);
                    break;
                case 524://钻石充值页面
                    UIShow.showMyDiamondsAct(context);
                    break;
                default:
                    break;
            }
        }

        //自动回复或主动招呼，已添加的语句数量为=0时，显示 去设置按钮 跳转到 添加自动回复页面
        public void auto_reply(String s) {
            Activity act = appInterface.getAct();
            Activity context = (act == null ? (Activity) App.getActivity() : act);
            UIShow.showWomanAutoReply(context);
        }

        // 我要赚钱页面cmd跳转
        private void go2EarnMoney(int code, Activity context) {
            if (!validation()) return;
            switch (code) {
                case 504://跳转主动视频邀请页面
                    UIShow.showMassVideo(context);
                    break;
                case 505://主动语音邀请页面
                    UIShow.showMassVoice(context);
                    break;
                case 506://动索要礼物 图片视频要礼物界面
                    UIShow.showAskGift(context);
                    break;
            }
        }

        private boolean validation() {
            Activity act = appInterface.getAct();

            UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
            //如果未认证通过，弹框提示
            if (!userDetail.isVerifyAll()) {
                UIShow.showUserAuthDlg((FragmentActivity) (act == null ? App.getActivity() : act), Constant.OPEN_FROM_CMD);
                return false;
            }
            //如果没有加入公会，弹加入公会
            if (!userDetail.isJoinGuild()) {
                UIShow.showJoinGuildDlg((FragmentActivity) (act == null ? App.getActivity() : act));
                return false;
            }
            return true;
        }

        //进入直播间
        public void enter_yfb_video(String data) {
            PLogger.d("---enter_yfb_video--->" + data);
        }

        //打开开始直播
        public void open_video(String data) {
            PLogger.d("---open_video--->" + data);
        }

        //用户进入直播间
        public void user_enter(String data){
            PLogger.d("---user_enter--->" + data);
            userEnterOrLeave(data, LiveCmdBusEvent.USER_ENTER);
        }

        //用户离开直播间
        public void user_leave(String data){
            PLogger.d("---user_leave--->" + data);
            userEnterOrLeave(data, LiveCmdBusEvent.USER_LEAVE);
        }

        //用户本场贡献收益
        public void user_consume(String data){
            PLogger.d("---user_consume--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);
            long uid = dataObject.optLong("uid");
            String avatar = dataObject.optString("avatar");
            int nobilitylevel = dataObject.optInt("nobilityLevel");
            int gender = dataObject.optInt("gender");
            int consume = dataObject.optInt("consume");

            LiveCmdBusEvent cmdEvent =  new LiveCmdBusEvent.Builder().uid(uid).avtar(avatar).nobilityLevel(nobilitylevel).gender(gender).
                    consume(consume).type(LiveCmdBusEvent.SYNC_USER_CONSUME).build();
            sendCmdToLive(cmdEvent);
        }

        //直播间管理
        //{
        //    uid: 123， 操作者
        //    target_uid: 111 被操作者
        //    type: 1 //1 禁言,2 拉黑,3 踢出
        //}
        public void user_room_control(String data){
            PLogger.d("---user_room_control--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);

            long uid = dataObject.optLong("uid");
            long tid = dataObject.optLong("target_uid");
            int type = dataObject.optInt("type");
            LiveCmdBusEvent cmdEvent =  new LiveCmdBusEvent.Builder().uid(uid).target_uid(tid).
                    room_control_type(type).type(LiveCmdBusEvent.ROOM_CONTROL).build();
            sendCmdToLive(cmdEvent);

        }

        private void userEnterOrLeave(String strJson,int type){
            JSONObject dataObject = JsonUtil.getJsonObject(strJson);
            long uid = dataObject.optLong("uid");
            String avatar = dataObject.optString("avatar");

            LiveCmdBusEvent.Builder cmdEvent =  new LiveCmdBusEvent.Builder();
            if (dataObject.has("nobilityLevel")){
                cmdEvent.nobilityLevel(dataObject.optInt("nobilityLevel"));
            }
            if (dataObject.has("gender")){
                cmdEvent.gender(dataObject.optInt("gender"));
            }
            if (dataObject.has("consume")){
                cmdEvent.consume(dataObject.optInt("consume"));
            }
            cmdEvent.uid(uid).avtar(avatar).type(type);
            sendCmdToLive(cmdEvent.build());
        }
        //直播间刷新魅力值
        public void sync_charm_value(String data){
            PLogger.d("---sync_charm_value--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);
            LiveCmdBusEvent cmdEvent = new LiveCmdBusEvent.Builder()
                    .charm(dataObject.optString("value")).type(LiveCmdBusEvent.REFRESH_CHARM).build();
            sendCmdToLive(cmdEvent);
        }

        //直播间刷新排行榜排名
        public void sync_ranking_info(String data){
            PLogger.d("---sync_ranking_info--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);
            LiveCmdBusEvent cmdEvent = new LiveCmdBusEvent.Builder().ranking(dataObject.optInt("ranking"))
                    .need_charm(dataObject.optString("need_charm")).type(LiveCmdBusEvent.RANKING).build();
            sendCmdToLive(cmdEvent);
        }

        //主播结束直播通知
        public void live_room_end(String data){
            PLogger.d("---live_room_end--->" + data);

            JSONObject dataObject = JsonUtil.getJsonObject(data);
            long uid = dataObject.optLong("uid");
            String avatar = dataObject.optString("avatar");
            String nickname = dataObject.optString("nickname");
            String charm = dataObject.optString("charm");
            long livetime = dataObject.optLong("liveTime");
            int type = dataObject.optInt("type");

            LiveCmdBusEvent cmdEvent =  new LiveCmdBusEvent.Builder()
                    .uid(uid).avtar(avatar).nickName(nickname).charm(charm).liveTime(livetime)
                    .type(LiveCmdBusEvent.GIRL_END_LIVE).room_control_type(type).build();
            sendCmdToLive(cmdEvent);
        }

        //webview 手势事件 暂时去掉此功能
        public void webview_touch_activity(String data){
            PLogger.d("---webview_touch_activity--->" + data);
//            JSONObject dataObject = JsonUtil.getJsonObject(data);
//            boolean isTouch = dataObject.optBoolean("activity");
//            LiveCmdBusEvent cmdEvent =  new LiveCmdBusEvent.Builder().activity(isTouch)
//                    .type(LiveCmdBusEvent.WEBVIEW_TOUCH_ACTIVITY).build();
//            RxBus.getInstance().post(cmdEvent);
        }

        //打开个人资料
        public void user_message(String data){
            PLogger.d("---user_message--->" + data);
            try {
                JSONObject obj = new JSONObject(data);
                long uid = obj.optLong("uid");
                String message=obj.optString("message");
                LiveCmdBusEvent cmdBusEvent = new LiveCmdBusEvent.Builder().uid(uid).message(message).type(LiveCmdBusEvent.OPEN_USER_CARD).build();
                sendCmdToLive(cmdBusEvent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //抢热一活动
        public void activity_hot_number_one(String data){
            PLogger.d("---activity_hot_number_one--->" + data);
            try {
                JSONObject obj = new JSONObject(data);
                int state = obj.optInt("activity_state");
                LiveCmdBusEvent cmdBusEvent = new LiveCmdBusEvent.Builder().activity_state(state).type(LiveCmdBusEvent.ACTIVITY_ONE_HOT).build();
                sendCmdToLive(cmdBusEvent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //切换到某个直播间
        public void enter_other_video_room(String data){
            PLogger.d("---enter_other_video_room--->" + data);
            try {
                JSONObject obj = new JSONObject(data);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 获取webview的宽高
        public void get_view_size(String data) {
            PLogger.d("---get_view_size--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);

            Map<String, Object> responseObject = new HashMap<>();
            responseObject.put("width", ModuleMgr.getAppMgr().getScreenWidth());
            responseObject.put("height",ModuleMgr.getAppMgr().getScreenHeight());

            doInJS(dataObject.optString("callbackName"), dataObject.optString("callbackID"), JSON.toJSONString(responseObject));
        }

        //直播间 发cmd
        private void sendCmdToLive(LiveCmdBusEvent cmdBusEvent){
            Activity act = appInterface.getAct();
//            if (act!=null && act instanceof H5LivePlayAct){
//                ((H5LivePlayAct) act).onReceiveCMD(cmdBusEvent);
//            }
        }
        // 索要礼物
        public void ask_for_gift(String data) {
            PLogger.d("---ask_for_gift--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);

            Activity act = appInterface.getAct();
            Activity context = (act == null ? (Activity) App.getActivity() : act);

            //photo 图片索要礼物 video 视频索要礼物 ‘normal’ 普通索要（直接索要礼物 缘分吧2.2）
            switch (dataObject.optString("type")) {
                case "normal":
                    UIShow.showNormalAskGiftDlg(context);
                    break;
                case "photo":
                    UIShow.showAskforPicGiftDlg(context);
                    break;
                case "video":
                    UIShow.showAskforVideoGiftDlg(context);
                    break;
                default:
                    break;
            }
        }

        // 改变页面标题
        public void change_title(String data) {
            PLogger.d("---change_title--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);
            Activity act = appInterface.getAct();
            if (act != null && act instanceof BaseActivity) {
                try {// catch页面中可能未include base_title的情况
                    ((BaseActivity) act).setTitle(dataObject.optString("title"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 改变页面标题 爵位版（点击或滑动切换页面）
        public void change_ranking_titles(String data) {
            Activity act = appInterface.getAct();
            if (act != null && act instanceof RankAct) {
                JSONObject dataObject = JsonUtil.getJsonObject(data);
                ((RankAct) act).changeTitles(dataObject.optInt("index"));
            }
        }

        // 屏幕震动
        public void play_shock(String data) {
            PLogger.d("---play_shock--->" + data);
            MediaNotifyUtils.vibrator();
        }

        // 加密网络请求
        public void safe_request(String data) {
            PLogger.d("---safe_request--->" + data);
            cmdRequest(JsonUtil.getJsonObject(data), true);
        }

        // 普通网络请求
        public void normal_request(String data) {
            PLogger.d("---normal_request--->" + data);
            cmdRequest(JsonUtil.getJsonObject(data), false);
        }

        // 切换到主tab页
        public void enter_tab(String data) {
            PLogger.d("---enter_tab--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);
            // tab页面索引
            int index = 1;
            switch (dataObject.optInt("index")) {
                case 1:
                    index = FinalKey.MAIN_TAB_1;
                    break;
                case 2:
                    index = FinalKey.MAIN_TAB_2;
                    break;
                case 3:
                    index = FinalKey.MAIN_TAB_3;
                    break;
                case 4:
                    index = FinalKey.MAIN_TAB_4;
                    break;
                case 5:
                    index = FinalKey.MAIN_TAB_5;
                    break;
                default:
                    break;
            }
            Activity act = appInterface.getAct();
            if (act != null && act instanceof MainActivity) {
                ((MainActivity) act).changeTab(index, null);
            } else {
                UIShow.showMainWithTabData(act, index, null);
            }
        }

        // 获取设备信息
        public void getdevicemodel(String data) {
            PLogger.d("---getdevicemodel--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);

            Map<String, Object> responseObject = new HashMap<>();
            responseObject.put("devicemodel", Build.MODEL);//解密后的服务端返回（安卓注意大小写）

            doInJS(dataObject.optString("callbackName"), dataObject.optString("callbackID"), JSON.toJSONString(responseObject));
        }

        // 进入别人的个人中心页面
        public void jump_to_userinfo(String data) {
            PLogger.d("---jump_to_userinfo--->" + data);
            final JSONObject dataObject = JsonUtil.getJsonObject(data);
            final Activity act = appInterface.getAct();
            UIShow.showCheckOtherInfoAct(act == null ? App.getActivity() : act, dataObject.optLong("target_uid"));
        }

        // 吐司提示
        public void show_toast(String data) {
            PLogger.d("---show_toast--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);
            PToast.showShort(dataObject.optString("content"));
        }

        // 开启支付页面
        public void start_pay(String data) {
            PLogger.d("---start_pay--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);
            Activity act = appInterface.getAct();
            UIShow.showPayListAct((FragmentActivity) (act == null ? App.getActivity() : act), dataObject.optInt("pay_id"));
        }

        // 获取认证状态
        public void get_identify_status(String data) {
            PLogger.d("---get_identify_status--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);

            UserInfo userInfo = getCenterMgr().getMyInfo();
            Map<String, Object> responseObject = new HashMap<>();
//            responseObject.put("mobile_auth_status", userInfo.getMobileAuthStatus());
//            responseObject.put("idcard_auth_status", userInfo.getIdcard_auth_status());
//            responseObject.put("bank_auth_status", userInfo.getBankAuthStatus());
//            responseObject.put("video_auth_status", userInfo.getVideoAuthStatus());

            doInJS(dataObject.optString("callbackName"), dataObject.optString("callbackID"), JSON.toJSONString(responseObject));
        }

        // 刷新个人详情
        public void refresh_userdetail(String data) {
            PLogger.d("---refresh_userdetail--->" + data);
            getCenterMgr().reqMyInfo();
        }

        // 照片选取：打开相册或相机，选取或拍摄完成之后回调图片的base64字符串给js(先进行图片质量压缩)
        public void get_image_data(String data) {
            PLogger.d("---get_image_data--->" + data);
            final JSONObject dataObject = JsonUtil.getJsonObject(data);
            Activity act = appInterface.getAct();
            ImgSelectUtil.getInstance().pickPhotoGallery(act == null ? context : act, new ImgSelectUtil.OnChooseCompleteListener() {
                @Override
                public void onComplete(String... path) {
                    if (path == null || path.length == 0 || TextUtils.isEmpty(path[0])) return;
                    PLogger.d("------>" + path[0]);
                    // 将选取的图片进行质量压缩并将二进制流转换为base64字符串
                    Map<String, Object> responseObject = new HashMap<>();
                    responseObject.put("imageData", BitmapUtil.imagePathToBase64(path[0]));//base64格式字符串
                    doInJS(dataObject.optString("callbackName"), dataObject.optString("callbackID"), JSON.toJSONString(responseObject));
                }
            });
        }

        // 图片数据转换成url：将图片上传服务器，并回调图片服务器地址给js
        public void image_data_to_url(String data) {
            PLogger.d("---image_data_to_url--->" + data);
            final JSONObject dataObject = JsonUtil.getJsonObject(data);
            final List<String> files = new LinkedList<>();
            JSONArray imageDataList = JsonUtil.getJsonArray(dataObject.optString("imageDataList"));
            for (int i = 0; i < imageDataList.length(); i++) {
                files.add(BitmapUtil.saveBitmap(BitmapUtil.decodeBase64(imageDataList.optString(i)),
                        DirType.getUploadDir() + System.currentTimeMillis() + "_" + i + ".jpg"));//将base64的数据保存成jpg文件
            }
            // 图片上传，上传完成之后删除本地存储的缓存文件
            int type = dataObject.optInt("type");
            ModuleMgr.getMediaMgr().sendHttpMultiFiles(Constant.UPLOAD_TYPE_PHOTO, 0, new MediaMgr.OnMultiFilesUploadComplete() {
                @Override
                public void onUploadComplete(ArrayList<String> mediaUrls) {
                    Map<String, Object> responseObject = new HashMap<>();
                    responseObject.put("urlList", mediaUrls.toArray());
                    doInJS(dataObject.optString("callbackName"), dataObject.optString("callbackID"), JSON.toJSONString(responseObject));
                    for (String s : files) FileUtil.deleteFile(s);
                }
            }, (String[]) files.toArray(new String[files.size()]));
        }

        // 联系qq客服
        public void open_qq_service(String data) {
            PLogger.d("---open_qq_service--->" + data);
            Activity act = appInterface.getAct();
            UIShow.showQQService(act == null ? context : act);
        }

        // 获取用户绑定手机号
        public void get_phone_number(String data) {
            PLogger.d("---get_phone_number--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);

            UserInfo userInfo = getCenterMgr().getMyInfo();
            Map<String, Object> responseObject = new HashMap<>();
            //字符串 没有绑定 返回值空字符，绑定的返回手机号
            responseObject.put("num", userInfo.isVerifyCellphone() ? userInfo.getMobile() : "");
            doInJS(dataObject.optString("callbackName"), dataObject.optString("callbackID"), JSON.toJSONString(responseObject));
        }

        // 获取服务器请求url
        public void get_agent_url(String data) {
            PLogger.d("---get_agent_url--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);

            String url = Hosts.HOST_URL;
            switch (dataObject.optString("type")) {
                case "php":
                    url = Hosts.FATE_IT_HTTP;
                    break;
                case "go":
                    url = Hosts.FATE_IT_GO;
                    break;
                case "pay":
                    url = Hosts.FATE_IT_PROTOCOL;
                    break;
                case "image":
                    url = Hosts.FATE_IT_HTTP_PIC;
                    break;
                case "goLive":
                    url = Hosts.FATE_IT_LIVE;
                default:
                    break;
            }
            Map<String, Object> responseObject = new HashMap<>();
            responseObject.put("url", url);
            doInJS(dataObject.optString("callbackName"), dataObject.optString("callbackID"), JSON.toJSONString(responseObject));
        }

        // H5页面用户行为统计
        public void user_behavior(String data) {
            PLogger.d("---user_behavior--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);

            Statistics.userBehavior(dataObject.optString("event_type"),
                    dataObject.optLong("to_uid"), dataObject.optString("event_data"));
        }

        // 跳转小秘书聊天页
        public void open_small_secretary(String data) {
            PLogger.d("---open_small_secretary--->" + data);

            long uid = MailSpecialID.customerService.getSpecialID();
            if(!TextUtils.isEmpty(data)){
                JSONObject dataObject = JsonUtil.getJsonObject(data);
                long tmpUID = dataObject.optLong("uid");
                if(tmpUID > 0){
                    uid = tmpUID;
                }
            }

            Activity act = appInterface.getAct();
            UIShow.showPrivateChatAct(act == null ? context : act, uid, "");
        }

        // H5页面用户行为统计
        public void open_live_view(String data) {
            PLogger.d("---open_live_view--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);
            LiveHelper.openLiveRoom(dataObject.optString("anchor_id"),
                    dataObject.optString("video_url"), dataObject.optString("image_url"),
                    dataObject.optString("download_url"), dataObject.optString("package_name"),
                    dataObject.optString("entrance"));
        }

        /**
         * 跳转查看特权
         */
        public void view_title_right(String data) {
            UIShow.showTitlePrivilegeAct(App.getActivity());
        }

        // ------------------------------游戏用cmd---------------------------------

        // 获取当前视频通话时长
        public void get_video_time(String data) {
            PLogger.d("---get_video_time--->" + data);
            JSONObject dataObject = JsonUtil.getJsonObject(data);
            Map<String, Object> responseObject = new HashMap<>();
            responseObject.put("timespan", 0);//TODO 当前通话时长(秒数)
            doInJS(dataObject.optString("callbackName"), dataObject.optString("callbackID"), JSON.toJSONString(responseObject));
        }

        // 转盘开始转动
        public void turntable_start_rotate(String data) {
            PLogger.d("---turntable_start_rotate--->" + data);
        }

        // 转盘停止转动
        public void turntable_stop_rotate(String data) {
            PLogger.d("---turntable_stop_rotate--->" + data);
        }

        //打开我的钱包
        public void open_wallet(String data) {
            PLogger.d("---open_wallet--->" + data);
            Activity act = appInterface.getAct();
            UIShow.showRedBoxRecordAct(act == null ? App.getActivity() : act);
        }

        //视频聊天消费钻石提示
        public void video_spend_diamond(String data) {
            PLogger.d("---open_wallet--->" + data);

            JSONObject dataObject = JsonUtil.getJsonObject(data);
            dataObject.optString("diamond");
        }

    }

    /**
     * 游戏交互-请求转发，判断是否为go服务器接口进行url-hash加密，
     *
     * @param dataObject    JS传递的JSONObject
     * @param isSafeRequest 是否是加密请求
     */
    private void cmdRequest(final JSONObject dataObject, boolean isSafeRequest) {
        JSONObject bodyObject = JsonUtil.getJsonObject(dataObject.optString("body"));
        String url = dataObject.optString("url");
        ModuleMgr.getCommonMgr().CMDRequest(dataObject.optString("method"), isSafeRequest, url,
                !"form".equalsIgnoreCase(dataObject.optString("dataType")),
                ChineseFilter.JSONObjectToMap(bodyObject), new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        PLogger.d("---cmdRequest--->" + response.getResponseString());
                        doInJS(dataObject.optString("callbackName"), dataObject.optString("callbackID"), response.getResponseString());
                    }
                });
    }

    /**
     * JSCMD 观众离开直播间
     * @param roomId 直播间ID
     */
    public void audienceLeave(String roomId){
        Map<String, Object> data = new HashMap<>();
        data.put("uid", ModuleMgr.getCenterMgr().getMyInfo().getUid());
        data.put("room_id", roomId);
        doInJS(Invoker.JSCMD_audience_leave, data);
    }

    /**
     * JSCMD 键盘弹起事件
     * @param isOpen 键盘是否打开
     * @param height 键盘高度
     */
    public void keyboardOpt(boolean isOpen,int height){
        Map<String, Object> data = new HashMap<>();
        data.put("type", isOpen ? 1 : 0);
        data.put("height", height);
        doInJS(Invoker.JSCMD_keyboard_operation, data);
    }

    /**
     * 视频中警告消息
     */
    public void warningMessageJSCMD(final long fid, final long tid, final String mct) {
        App.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> data = new HashMap<>();
                data.put("fid", fid);
                data.put("tid", tid);
                data.put("mct", ChineseFilter.toUnicode(mct));
                doInJS(Invoker.JSCMD_warning_message, data);
            }
        });
    }

    /**
     * JSCMD: 发送接收礼物
     *
     * @param url  礼物icon
     * @param id   礼物id
     * @param num  数量
     * @param type 1 表示送出 0 收到
     */
    public void giftMessageJSCMD(final String url, final int id, final int num, final int type) {
        App.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> data = new HashMap<>();
                data.put("giftUrl", url);
                data.put("gift_id", id);
                data.put("num", num);
                data.put("isSend", type);
                doInJS(Invoker.JSCMD_gift_message, data);
            }
        });
    }

}
