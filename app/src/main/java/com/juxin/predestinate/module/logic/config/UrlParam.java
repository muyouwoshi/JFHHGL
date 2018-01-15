package com.juxin.predestinate.module.logic.config;

import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.hot.UserInfoHotList;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweightList;
import com.juxin.predestinate.bean.center.user.others.UserBlack;
import com.juxin.predestinate.bean.config.CheckDomainList;
import com.juxin.predestinate.bean.config.CommonConfig;
import com.juxin.predestinate.bean.config.VideoVerifyBean;
import com.juxin.predestinate.bean.file.UpLoadResult;
import com.juxin.predestinate.bean.my.ApplyLiveStatusBean;
import com.juxin.predestinate.bean.my.GiftMessageList;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.bean.my.MarqueeMessageList;
import com.juxin.predestinate.bean.my.RedOneKeyList;
import com.juxin.predestinate.bean.my.RedbagList;
import com.juxin.predestinate.bean.my.SendChumInvite;
import com.juxin.predestinate.bean.my.WomanAutoReplyList;
import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.bean.settting.ContactBean;
import com.juxin.predestinate.bean.settting.Setting;
import com.juxin.predestinate.bean.settting.ShareAccounts;
import com.juxin.predestinate.bean.start.LoginResult;
import com.juxin.predestinate.bean.start.OfflineMsg;
import com.juxin.predestinate.module.local.mail.MyChumList;
import com.juxin.predestinate.module.local.mail.MyChumTaskList;
import com.juxin.predestinate.ui.agora.act.bean.ChatStatus;
import com.juxin.predestinate.ui.agora.act.bean.Invited;
import com.juxin.predestinate.ui.agora.act.bean.RtcComment;
import com.juxin.predestinate.ui.user.check.bean.VideoConfig;
import com.juxin.predestinate.ui.user.check.bean.VideoSetting;
import com.juxin.predestinate.ui.user.my.adapter.Accept;

import java.util.Map;

/**
 * 管理常用的Url参数信息
 */
public enum UrlParam {

    reqRegister("pubtest/quickReg", null, false),//注册接口
    reqLogin("public/login", LoginResult.class, false),//普通登录接口
    reqNewLogin(Hosts.FATE_IT_GO, "x/userinfo/UserLogin", LoginResult.class, false),//2.2版登录接口
    forgotPassword("Public/forgotPassword"),//找回密码
    reqForgotsms("Public/forgotsms", false),//找回密码发送验证码
    getserviceqq("user/getserviceqq", ContactBean.class, true),//获取客服信息
    //================================ 配置项 ==================================
    CMDRequest(""),//cmd请求中默认拼接内容为空，通过resetHost方式进行使用
    checkUpdate("public/checkupNew", null, true),   //检查软件升级
    staticConfig(Hosts.FATE_CONFIG, "x/config/GetSet", CommonConfig.class, false),//检查服务器静态配置
    payStatistic("user/ppc", true),     //php旧版用户充值统计
    statistics(Hosts.FATE_IT_GO, "x/hdp/Action2", null, false),//大数据统计
    locationStatistics(Hosts.FATE_IT_GO, "xs/location/UpdateUserPosition", null, false),//位置信息统计
    reqSayHiList("pubtest/getSayHiUserNew", UserInfoLightweightList.class, true),//一键打招呼列表
    reqOfflineMsg(Hosts.FATE_IT_GO, "xs/message/OfflineMsgs", OfflineMsg.class, true), // 离线消息
    reqOfflineRecvedMsg(Hosts.FATE_IT_GO, "xs/message/OfflineRecved", null, true), // 离线送达消息

    sendMessage(Hosts.FATE_IT_GO, "xs/message/MessageSend", null, true),  //代替socket发送消息

    //============================== 设置页相关接口 =============================
    reqReqVerifyCode("Public/sendSMSM", false),//获取手机验证码
    mobileAuth("user/bindCellPhone", true),//手机认证
    modifyPassword("user/modifyPassword", null, true),//修改密码
    feedBack("user/feedback"),//意见反馈
    getSetting("s/uinfo/GetSetting", Setting.class, true),//获取设置信息
    updateSetting("s/uinfo/UpdateSetting", true),//设置信息修改
    //获取自己的音频、视频开关配置
    reqMyVideochatConfig(Hosts.FATE_IT_GO, "xs/message/MyVideochatConfig2", VideoVerifyBean.class, true),
    //音视频开关修改
    setVideochatConfig(Hosts.FATE_IT_GO, "xs/message/SetVideochatConfig", null, true),
    //上传视频认证配置
    addVideoVerify(Hosts.FATE_IT_GO, "xs/message/AddVideoVerify", null, true),

    // 用户身份认证提交
    userVerify("User/Verify", true),
    // 用户身份认证提交新
    userVerify1("User/Verify1", true),
    // 获取用户验证信息(密)
    getVerifyStatus("User/getVerifyStatus", true),

    //============================== 用户资料相关接口 =============================
    reqSetInfo("i/uinfo/SecSetInfo", true),                   // 用户设置更新
    reqMyInfo(Hosts.FATE_IT_GO, "xs/userinfo/MyDetail", UserDetail.class, true),         // 获取个人资料
    reqOtherInfo(Hosts.FATE_IT_GO, "xs/userinfo/OtherDetail", UserDetail.class, true), // 获取他人资料
    updateMyInfo("user/modifyUserData"),                      // 修改用户个人信息
    reqIsBlack(Hosts.FATE_IT_GO, "xs/userrelation/IsMyBlack", UserBlack.class, true), // 查询某用户是否处于黑名单中
    reqAddBlack(Hosts.FATE_IT_GO, "xs/userrelation/AddBlack", null, true),          // 拉黑某用户
    reqRemoveBlack(Hosts.FATE_IT_GO, "xs/userrelation/RemoveBlack", null, true),    // 拉黑列表移除某用户
    reqSetRemarkName(Hosts.FATE_IT_GO, "xs/userrelation/SetRemakName", null, true),             // 设置用户备注名
    reqVideoChatConfig(Hosts.FATE_IT_GO, "xs/message/GetVideochatConfig", VideoConfig.class, true), // 获取他人音视频开关配置
    reqGetOpposingVideoSetting(Hosts.FATE_IT_GO, "xs/userrelation/GetOpposingVideoSetting", VideoSetting.class, true), // 获取接受他人音视频配置
    reqSetOpposingVideoSetting(Hosts.FATE_IT_GO, "xs/userrelation/SetOpposingVideoSetting", null, true), // 设置接受他人音视频配置

    reqUserInfoSummary(Hosts.FATE_IT_GO, "xs/userinfo/UserInfoSummary", null, true),   //获取轻量级的用户信息

    reqUserHotList(Hosts.FATE_IT_GO, "xs/discovery/HotUsers", UserInfoHotList.class, true),   //获取用户信息热门列表

    // 接收聊天红包
    reqReceiveChatBag("gift/receiveChatred", true),
    // 私密视频相关
    reqSetPopnum("video2/setPopnum"),        // 增加私密视频人气值
    reqSetViewTime("video2/setviewtime"),    // 设置私密视频观看次数
    reqUnlockVideo("video2/unlockvideo"),    // 解锁视频

    //批量获取用户简略信息
    reqUserSimpleList("s/uinfo/USimple", UserInfoLightweightList.class, true),
    //获取昵称和头像的最近变更 list
    reqBasicUserInfoMsg("s/uinfo/NickChangedList", UserInfoLightweightList.class, true),

    // 上传头像
    uploadAvatar(Hosts.FATE_IT_HTTP_PIC, "index/uploadAvatar", null, false),

    // 上传相册
    uploadPhoto(Hosts.FATE_IT_HTTP_PIC, "index/uploadPhoto", null, true),

    // 删除照片
    deletePhoto("user/deletePic", false),

    // 上传文件
    uploadFile(Hosts.FATE_IT_HTTP_PIC, "index/upload", UpLoadResult.class, false),

    //============================== 用户资料模块相关接口 =============================
    //摇钱树红包列表
    reqTreeRedbagList("fruit/redbaglisttree", RedbagList.class, true),
    reqRedbagList("fruit/redbaglist", RedbagList.class, true),
    //客户端用户红包入袋fruit/addredonekey
    reqAddredTotal("fruit/addredtotalnew", false),
    // 红包记录--红包入袋 -- 一键入袋(24不能提现)
    reqAddredonekey("fruit/addredonekey", RedOneKeyList.class, true),
    // 客户端请求用户提现列表
    reqWithdrawlist("fruit/withdrawlistNew", true),
    // 红包记录--提现申请
    reqWithdraw("fruit/withdrawNew", true),
    // 新客户端提交提现修改
    reqWithdrawmodifyNew("fruit/withdrawmodifyNew", true),
    // 红包记录--提现申请获取地址
    reqWithdrawAddress("fruit/withdrawaddressNew", true),
    // 红包记录--提现申请修改地址
    reqWithdrawModify("fruit/withdrawmodifyNew", true),
    // 新版获取礼物列表
    getGiftLists(Hosts.FATE_CONFIG, "x/config/GetGifts", GiftsList.class, true),
    // 我的背包面板
    getMyBag(Hosts.FATE_IT_GO, "xs/hongbao/MyBag", null, true),
    // 索要礼物
    begGift("gift/begGift", true),
    //接收礼物
    receiveGift("gift/receiveGift", null, true),
    // 手机验证
    sendSMS("public/sendSMS", true),
    // 手机验证
    bindCellPhone("user/bindCellPhone", true),
    // 索要礼物群发
    qunFa(Hosts.FATE_IT_GO, "xs/discovery/Qunfa", null, true),
    // 索要礼物群发
    sendGift(Hosts.FATE_IT_GO, "xs/gift/SendGiftInstant", null, true),
    //女用户群发累计发送的用户数
    qunCount(Hosts.FATE_IT_GO, "xs/discovery/QunCount", null, true),
    // 我关注的列表
    getFollowing("MoneyTree/getFollowing", true),
    // 关注我的列表
    getFollowers("MoneyTree/getFollowers", true),
    // 上传身份证照片
    uploadIdCard("User/uploadIdCard", true),
    //获取二维码
    getQRCode("user/getShareQrcode", true),
    // 获取最近礼物列表
    lastGiftList("gift/lastgiftlist", GiftMessageList.class, false),
    // 通用跑马灯列表
    userBanner(Hosts.FATE_IT_GO, "xs/common/UserBanner", MarqueeMessageList.class, true),
    // 领取红包(领取自己的红包)
    achieveRewardHongbao(Hosts.FATE_IT_GO, "xs/hongbao/AchieveRewardHongbao", null, true),
    // 赠送红包
    bestowRewardHongbao(Hosts.FATE_IT_GO, "xs/hongbao/BestowRewardHongbao", null, true),
    // 接受赠送红包
    receiveRewardHongbao(Hosts.FATE_IT_GO, "xs/hongbao/ReceiveRewardHongbao", null, true),

    // 男用户发起密友申请
    startIntimateRequestM(Hosts.FATE_IT_GO, "xs/userrelation/StartIntimateRequestM", SendChumInvite.class, true),
    // 女用户发起密友申请
    startIntimateRequestW(Hosts.FATE_IT_GO, "xs/userrelation/StartIntimateRequestW", SendChumInvite.class, true),
    // 女用户拒绝男用户的密友申请
    rejectIntimateRequestW(Hosts.FATE_IT_GO, "xs/userrelation/RejectIntimateRequestW", null, true),
    // 女用户同意男用户的密友申请
    acceptIntimateRequstW(Hosts.FATE_IT_GO, "xs/userrelation/AcceptIntimateRequestW", null, true),
    // 解除密友关系
    cancelIntimateFriend(Hosts.FATE_IT_GO, "xs/userrelation/CancelIntimateFriend", null, true),
    // 我的密友任务信息
    getIntimateFriendTask(Hosts.FATE_IT_GO, "xs/userrelation/GetIntimateFriendTask", null, true),
    // 女性密友发送任务接口
    sendIntimateTask(Hosts.FATE_IT_GO, "xs/userrelation/SendIntimateTask", null, true),
    //分享CMD解析接口
    extractShareinfo(Hosts.FATE_IT_GO, "xs/common/ExtractShareinfo", null, true),

    //================= 发现 ===========
    //举报
    complainBlack(Hosts.FATE_IT_GO, "xs/userrelation/ComplainBlack", null, true),
    //获取发现列表
    getMainPage(Hosts.FATE_IT_GO, "xs/discovery/MainPage", UserInfoLightweightList.class, true),
    //获取附近的人列表
    getNearUsers2(Hosts.FATE_IT_GO, "xs/discovery/NearUsers2", UserInfoLightweightList.class, true),
    //获取我的好友列表
    getMyFriends(Hosts.FATE_IT_GO, "xs/userrelation/GiftFriends", UserInfoLightweightList.class, true),
    //新版 获取我的好友列表，应服务器要求暂时废弃2017/7/17

    //视频好友
    getVideoFriends(Hosts.FATE_IT_GO, "xs/userrelation/VideoFriends", UserInfoLightweightList.class, true),

    //解锁好友
    getUnlockFriends(Hosts.FATE_IT_GO, "xs/userrelation/UnlockFriends", UserInfoLightweightList.class, true),

    //获取黑名单列表
    getMyDefriends(Hosts.FATE_IT_GO, "xs/userrelation/BlackList", UserInfoLightweightList.class, true),

    getVideoChatUsers(Hosts.FATE_IT_GO, "xs/discovery/VideochatUsers", UserInfoLightweightList.class, true), // 获取首页语聊推荐用户

    //============ 支付 =============
    reqCommodityList("user/payListNode"),  // 商品列表

    reqWX(Hosts.FATE_IT_PROTOCOL, "user/wxAllPayencry", null, true),  //微信支付

    reqAlipay(Hosts.FATE_IT_PROTOCOL, "user/alipayencry", null, true),  //银联支付

    reqPhoneCard(Hosts.FATE_IT_CUP_HTTP, "user/card", null, true),  //手机卡

    reqSearchPhoneCard(Hosts.FATE_IT_CUP_HTTP, "user/checkSZPay", null, true),  //手机卡查询

    reqAliWapPay(Hosts.FATE_IT_CUP_HTTP, "user/aliWapPay", null, true),   //支付宝wap充值

    reqCustomFace(Hosts.FATE_IT_GO, "xs/message/GetCustomFace", null, true),   //获取自定义表情列表

    delCustomFace(Hosts.FATE_IT_GO, "xs/message/DelCustomFace", null, true),   //删除自定义表情

    addCustomFace(Hosts.FATE_IT_GO, "xs/message/AddCustomFace", null, true),   //添加自定义表情

    getUserNetInfo(Hosts.FATE_IT_GO, "xs/userinfo/GetUserNetInfo", null, true),  //获取用户设备网络信息

    /**
     * [提交用户设备网络信息](http://doc.dev.yuanfenba.net/pkg/yuanfen/yfb_service/modules/userinfo/#Uinfo.XS_UpdateNetInfo)
     */
    updateNetInfo(Hosts.FATE_IT_GO, "xs/userinfo/UpdateNetInfo", null, true),

    reqChatInfo(Hosts.FATE_IT_GO, "xs/userinfo/ChatInfo", null, true),  //聊天窗口信息

    refPayComplete(Hosts.FATE_IT_GO, "xs/userinfo/RefPayComplete", null, true), //刷新充值是否成功

    reqSerectComment(Hosts.FATE_IT_GO, "xs/discovery/SerectComment", null, true),  //私密视频/图片的评价

    reqQunPhotos(Hosts.FATE_IT_GO, "xs/discovery/QunPhotos", null, true),  //女用户群发私密照片索要礼物

    reqQunVideo(Hosts.FATE_IT_GO, "xs/discovery/QunVideo", null, true),  //女用户群发私密视频索要礼物

    getNobilityLeftGifts(Hosts.FATE_IT_GO, "xs/gift/NobilityLeftGifts", null, true),// 爵位升级礼物

    reqSerectUnlock(Hosts.FATE_IT_GO, "xs/discovery/SerectUnlock", null, true),  //私密视频/语音/图片/聊天的解锁通知

    reqAddWomanAutoReply(Hosts.FATE_IT_GO, "xs/discovery/FemaleAutoReplyIncr", null, true),//添加女性自动回复

    reqWomanAutoReplyList(Hosts.FATE_IT_GO, "xs/discovery/FemaleAutoReplyGets", WomanAutoReplyList.class, true),//女性自动回复列表

    reqWomanAutoReplySetSelect(Hosts.FATE_IT_GO, "xs/discovery/FemaleAutoReplySelectedSet", null, true),//设置女用户自动回复的选中项

    reqWomanAutoReplySetAVStatus(Hosts.FATE_IT_GO, "xs/discovery/FemaleAvAutoInviteSet", null, true),//设置女用户音视频自动邀请状态

    reqWomanAutoReplyDel(Hosts.FATE_IT_GO, "xs/discovery/FemaleAutoReplyDel", null, true),//删除女性自动回复条目

    joinGuild("user/addGroup", null, true),  //加入公会

    // 音视频聊天
    reqInviteChat(Hosts.FATE_IT_GO, "xs/message/InviteVideoChat", Invited.class, true),      // 邀请对方
    reqAcceptChat(Hosts.FATE_IT_GO, "xs/message/AcceptVideoChat", Accept.class, true), // 接受通信邀请
    reqRejectChat(Hosts.FATE_IT_GO, "xs/message/RejectVideoChat", null, true),// 拒绝通信邀请
    reqStopChat(Hosts.FATE_IT_GO, "xs/message/StopVideoChat", null, true),// 挂断聊天
    reqCheckChat(Hosts.FATE_IT_GO, "xs/message/CheckVideoChat", ChatStatus.class, true),// 聊天中，客户端刷新聊天状态（轮询接口）
    reqCheckPornography(Hosts.FATE_IT_GO, "xs/message/CheckPornography", null, true), // 视频聊天鉴黄接口
    reqTimeoutChat(Hosts.FATE_IT_GO, "xs/message/SetTimeoutVideoChat", null, true),// 60s超时挂断请求接口

    // 女性版：单邀，群邀
    reqVCGroupCancel(Hosts.FATE_IT_GO, "xs/message/VCGroupCancel", null, true),// 群发邀请： 取消群发邀请状态
    girlSingleInviteVa(Hosts.FATE_IT_GO, "xs/message/InviteVideoChatW", Invited.class, true),//女性单邀音视频
    girlGroupInviteVa(Hosts.FATE_IT_GO, "xs/message/VCGroupInvite", null, true),  //女性群邀音视频
    reqGirlAcceptChat(Hosts.FATE_IT_GO, "xs/message/VCGroupAccept", Accept.class, false),   // 接受女性通信邀请

    reqTitleUsers(Hosts.FATE_IT_GO, "xs/discovery/TitleUsers", null, true),  //爵位用户推荐

    reqMyEarningsInfo(Hosts.FATE_IT_GO, "xs/userinfo/MyEarningsInfo", null, true),  //女号挣钱信息

    reqAcceptIntimateRequstM(Hosts.FATE_IT_GO, "xs/userrelation/AcceptIntimateRequestM", null, true),  //男用户同意女用户的密友申请

    reqRejectIntimateFriendM(Hosts.FATE_IT_GO, "xs/userrelation/RejectIntimateFriendM", null, true),  //RejectIntimateFriendM

    reqMyIntimateFriends(Hosts.FATE_IT_GO, "xs/userrelation/MyIntimateFriends", MyChumList.class, true),  //我的密友列表

    reqGetIntimateFriendTask(Hosts.FATE_IT_GO, "xs/userrelation/GetIntimateFriendTask", MyChumTaskList.class, true),  //我的密友任务信息

    reqGetFeedbackInfo(Hosts.FATE_IT_GO, "xs/message/GetFeedbackInfo", RtcComment.class, true),//获取视频评价信息

    reqGetSettlement(Hosts.FATE_IT_GO, "xs/message/GetSettlement", RtcComment.class, true),//获取视频结算信息

    reqAddFeedback(Hosts.FATE_IT_GO, "xs/message/AddFeedback", null, true),//提交视频评价

    getIntimateTaskCnt(Hosts.FATE_IT_GO, "xs/userrelation/GetIntimateTaskCnt", null, true),//获取今天未完成密友任务的好友数

    qunSayHelloCount(Hosts.FATE_IT_GO, "xs/discovery/BQunGetCount", null, true),  //群打招呼筛选 获取人数

    qunSayHello(Hosts.FATE_IT_GO, "xs/discovery/BQunSend", null, true),  //群打招呼

    reqBeautyCar(Hosts.FATE_IT_GO, "xs/userinfo/BeautyCar", null, true),//女神座驾信息

    /**
     * [首页活动banner配置](http://doc.13.xiaoyouapp.cn/pkg/configure/modules/config/#ConfigModule.X_GetBanner)
     */
    reqBanner(Hosts.FATE_CONFIG, "x/config/GetBanner", null, false),

    reqVCRandUser(Hosts.FATE_IT_GO, "xs/message/VCRandUser", null, true),//同随机的一个女用户视频

    /**
     * [搜索用户](http://doc.dev.yuanfenba.net/pkg/yuanfen/yfb_service/modules/discovery/#DiscoveryModule.XS_SearchUsers)
     */
    SEARCH_USERS(Hosts.FATE_IT_GO, "xs/discovery/SearchUsers", null, true),

    /**
     * [获取一键打招呼用户信息](http://doc.dev.yuanfenba.net/pkg/yuanfen/yfb_service/modules/message/#MessageModule.XS_GetOnehelloUsers)
     */
    GET_ONE_HELLO_USERS(Hosts.FATE_IT_GO, "xs/message/GetOnehelloUsers", null, true),

    /**
     * [一键打招呼，动作](http://doc.dev.yuanfenba.net/pkg/yuanfen/yfb_service/modules/message/#MessageModule.XS_SayHello2)
     */
    SAY_HELLO_PEERAGE(Hosts.FATE_IT_GO, "xs/message/SayHello2", null, true),

    /**
     * [B版本获取视频卡数量](http://doc.dev.yuanfenba.net/pkg/yuanfen/yfb_service/modules/userinfo/#Uinfo.XS_GetVideoCardCount)
     */
    GET_VIDEOCARDCOUNT(Hosts.FATE_IT_GO, "xs/userinfo/GetVideoCardCount", null, false),

    //检测域名
    checkDomainName_0(Hosts.DOMAINTEST_ONLINE_ARR[0], "x/domain/ServerInfo", CheckDomainList.class, false),
    checkDomainName_1(Hosts.DOMAINTEST_ONLINE_ARR[1], "x/domain/ServerInfo", CheckDomainList.class, false),
    checkDomainName_2(Hosts.DOMAINTEST_ONLINE_ARR[2], "x/domain/ServerInfo", CheckDomainList.class, false),
    checkDomainName_3(Hosts.DOMAINTEST_ONLINE_ARR[3], "x/domain/ServerInfo", CheckDomainList.class, false),

    getEnterLiveUrl(Hosts.FATE_IT_LIVE, "xs/live/EnterLive", null, true),//获取直播播放流地址
    chatSend(Hosts.FATE_IT_LIVE, "xs/chat/Send", null, true),  //发送普通聊天消息
    chatSendBarrage(Hosts.FATE_IT_LIVE, "xs/chat/SendBarrage", null, true),//发送弹幕消息
    chatSendPresent(Hosts.FATE_IT_LIVE, "xs/chat/SendPresent", null, true),//发送礼物聊天消息
    getLivingList(Hosts.FATE_IT_LIVE, "xs/live/LivingList", null, true),//获取正在直播的主播列表
    roomConsumerList(Hosts.FATE_IT_LIVE, "xs/live/RoomConsumerList", null, true),//获取消费人员列表
    getFlirtSetting(Hosts.FATE_IT_LIVE, "xs/interact/GetFlirtSetting", null, true),//获取撩选项
    updateFlirtSetting(Hosts.FATE_IT_LIVE, "xs/interact/UpdateFlirtSetting", null, true),//更新撩选项
    flirtInformation(Hosts.FATE_IT_LIVE, "xs/interact/FlirtInformation", null, true),//撩的
    report(Hosts.FATE_IT_LIVE, "xs/chat/Report", null, true),//举报
    ContributionList(Hosts.FATE_IT_LIVE, "xs/live/ContributionList", null, true),//直播榜单
    GetLiveNotifyList(Hosts.FATE_IT_GO, "xs/kugetlive/GetLiveNotifyList", null, true),//直播提醒列表
    SetLiveNotifyStatus(Hosts.FATE_IT_GO, "xs/kugetlive/SetLiveNotifyStatus", null, true),//更新直播提醒状态
    OtherPopularList(Hosts.FATE_IT_LIVE, "xs/live/OtherPopularList", null, true),//其他热门主播列表
    liveAdminOpt(Hosts.FATE_IT_LIVE, "xs/chat/AdminOpt", null, true),//管理员操作，禁言，踢出，设置场控，取消场控
    liveUserDetail(Hosts.FATE_IT_LIVE, "xs/uinfo/UDetail", null, true),//用户资料
    liveBatchAuth(Hosts.FATE_IT_LIVE, "xs/batch/BatchAuth", null, true),//限流认证
    BroadCast(Hosts.FATE_IT_LIVE,"xs/chat/BroadCast",null,true),//广播发言
    /**直播认证*/
    applyLive(Hosts.FATE_IT_GO, "xs/kugetlive/ApplyLive", null, true),
    /**获取直播认证状态*/
    getApplyLiveStatus(Hosts.FATE_IT_GO, "xs/kugetlive/GetApplyLiveStatus", ApplyLiveStatusBean.class, true),
    SearchAnchor(Hosts.FATE_IT_LIVE,"xs/uinfo/SearchAnchor",null,true),
    /**获取直播标签列表*/
    getLiveTagList(Hosts.FATE_IT_LIVE, "xs/live/LiveTagList", null, true),
    /**根据标签获取直播列表*/
    getLiveListByTag(Hosts.FATE_IT_LIVE, "xs/live/TagLiveList", null, true),
    /**签约推荐主播*/
    getRecommendLiveList(Hosts.FATE_IT_LIVE, "xs/live/RecommendedAnchorList", null, true),
    /**签约推荐更多*/
    getRecommendLiveMoreList(Hosts.FATE_IT_LIVE, "xs/live/MoreRecommendedAnchorList", null, true),
    getNearbyLiveList(Hosts.FATE_IT_LIVE, "xs/live/NearbyLiveList", null ,true),
    getLiveBanner(Hosts.FATE_IT_LIVE, "xs/live/LiveBanner", null, true),
    reqShareQrCode(Hosts.FATE_IT_GO, "xs/common/GetShareCode", null, true),//获取用户推广二维码(密)
    reqShareUrl(Hosts.FATE_IT_MATERIAL, "YFBclient/api/share_matrial/getShareMatrial.json", null, true),// 获取素材接口
    reqShareUrlV2(Hosts.FATE_IT_MATERIAL, "YFBclient/api/share_matrial/getShareDataV2.json", null, true),// 获取素材接口版本2
    reqFormatShareContent(Hosts.FATE_IT_GO, "xs/common/FormatShareContent", null, true),//格式化分享的模板内容
    reqGetShareChannel(Hosts.FATE_IT_MATERIAL, "YFBclient/api/share/getShareChannel.json", null, true),//获取可用分享渠道接口

    /**
     * [获取分享帐号信息](http://doc.13.xiaoyouapp.cn/pkg/configure/modules/share/#ShareModule.X_GetShareAccounts)
     */
    reqShareAccounts(Hosts.FATE_CONFIG, "x/share/GetShareAccounts", ShareAccounts.class, false),
    /**
     * [点击分享](http://doc.dev.yuanfenba.net/pkg/yuanfen/yfb_service/modules/hongbao/#HongbaoService.XS_StartShare)
     */
    startShare(Hosts.FATE_IT_GO, "xs/hongbao/StartShare", null, true),
    /**
     * [能够分享渠道列表](http://doc.dev.yuanfenba.net/pkg/yuanfen/yfb_service/modules/hongbao/#HongbaoService.XS_GetShareChannel)
     */
    getShareChannel(Hosts.FATE_IT_GO, "xs/hongbao/GetShareChannel", null, true),
    /**
     * [分享赚钱二维码的回调](http://doc.dev.yuanfenba.net/pkg/yuanfen/yfb_service/modules/hongbao/#HongbaoService.XS_ShareCodeCallback)
     */
    shareCodeCallback(Hosts.FATE_IT_GO, "xs/hongbao/ShareCodeCallback", null, true),
    shareCodeCallback2(Hosts.FATE_IT_GO, "xs/hongbao/ShareCodeCallback2", null, true),
    //获取各个渠道，分享人数
    getShareCount(Hosts.FATE_IT_GO, "xs/hongbao/GetShareCount", null, true),
    // 最后一个，占位
    LastUrlParam("");

    // -------------------------------内部处理逻辑----------------------------------------

    private String host = Hosts.HOST_URL;    //请求host
    private String spliceUrl = null;            //接口url，与host拼接得到完整url
    private boolean needLogin = false;          //请求是否需要登录才会发送
    private Class<? extends BaseData> parseClass = null;//请求返回体解析类

    // --------------构造方法 start--------------

    /**
     * host+接口url+解析bean
     */
    UrlParam(final String host, final String spliceUrl, final Class<? extends BaseData> parseClass, final boolean needLogin) {
        this.host = host;
        this.spliceUrl = spliceUrl;
        this.parseClass = parseClass;
        this.needLogin = needLogin;
    }

    /**
     * 接口url+解析bean+是否需要登录
     */
    UrlParam(final String spliceUrl, final Class<? extends BaseData> parseClass, final boolean needLogin) {
        this(Hosts.HOST_URL, spliceUrl, parseClass, needLogin);
    }

    /**
     * 接口url+解析bean
     */
    UrlParam(final String spliceUrl, final Class<? extends BaseData> parseClass) {
        this(spliceUrl, parseClass, false);
    }

    /**
     * 接口url+是否需要登录
     */
    UrlParam(final String spliceUrl, final boolean needLogin) {
        this(spliceUrl, null, needLogin);
    }

    /**
     * 接口url
     */
    UrlParam(final String spliceUrl) {
        this(spliceUrl, null);
    }

    // --------------构造方法 end--------------

    /**
     * 重设请求host
     *
     * @param host host地址
     * @return UrlParam
     */
    public UrlParam resetHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * 重设接口url，特殊情况使用
     *
     * @param spliceUrl 接口url
     */
    public void resetSpliceUrl(String spliceUrl) {
        this.spliceUrl = spliceUrl;
    }

    /**
     * 是否需要登录才能发送的请求
     *
     * @return boolean值
     */
    public boolean isNeedLogin() {
        return needLogin;
    }

    /**
     * 获取完整Url
     *
     * @return url
     */
    public String getFinalUrl() {
        if (host.equals(Hosts.NO_HOST)) {
            return spliceUrl;
        }
        return host + spliceUrl;
    }

    /**
     * 获取一个实现了BaseData接口的实例
     *
     * @return 有Class的newInstance生成的实例
     */
    public BaseData getBaseData() {
        BaseData baseData = null;
        try {
            if (parseClass != null) {
                baseData = parseClass.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseData;
    }

    /**
     * 获取完整拼接参数的Url，用于缓存url等
     *
     * @param param 需要传送的参数
     * @return spliceUrl
     */
    public String getEntireUrl(Map<String, Object> param) {
        String url = this.spliceUrl;
        if (param != null) {
            for (Object o : param.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                Object key = entry.getKey();
                Object val = entry.getValue();
                url = url.replaceAll("\\{" + key.toString() + "\\}", val.toString());
            }
        }
        if (!host.equals(Hosts.NO_HOST)) {
            url = host + url;
        }
        return url;
    }
}