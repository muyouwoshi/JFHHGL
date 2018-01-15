package com.juxin.predestinate.module.local.statistics;

/**
 * 用户行为统计项发送点，[统计项参数文档](http://test.game.xiaoyouapp.cn:20080/juxin-data/juxin-data-doc/src/master/flume/userbehavior-event-all.md)
 * Created by ZRP on 2017/5/24.
 */
public enum SendPoint {

    // -------软件升级统计项--------
    page_upgrade_box_pageview,      // 自动升级弹框->弹窗打开事件
    page_upgrade_box_cancel,        // 自动升级弹框->取消
    page_upgrade_box_confirm,       // 自动升级弹框->立即更新
    page_upgrade_minibox_pageview,  // 自动升级弹框(迷你)->弹窗打开事件
    page_upgrade_minibox_confirm,   // 自动升级弹框(迷你)->立即更新(当前默认下载回来的是全量包)

    // -------首页tab--------
    menu_faxian,        // 发现
    menu_xiaoxi,        // 消息
    menu_fengyunbang,   // 风云榜
    menu_guangchang,    // 广场
    menu_me,            // 我的

    // --------------发现---------------
    menu_faxian_tuijian,                        //发现->推荐(普通点击)
    menu_faxian_hot,                            //发现->热门(普通点击)
    menu_faxian_tuijian_downrefresh,            //发现->下拉刷新(传递用户列表)
    menu_faxian_tuijian_uprefresh,              //发现->推荐->上拉刷新(传递用户列表,从下往上拉)
    menu_faxian_tuijian_viewuserinfo,           //发现->查看用户资料
    menu_faxian_tuijian_more_viewall,           //发现->更多->发现->更多->查看全部(普通点击)
    menu_faxian_tuijian_more_viewfujin,         //发现->更多->发现->更多->只看附近的人(普通点击)
    menu_faxian_tuijian_more_cancel,            //发现->更多->发现->更多->取消(普通点击)
    menu_faxian_tuijian_fujin_sayhello,         //发现->热门->只看附近的人->打招呼
    menu_faxian_tuijian_fujin_batchsayhello,    //发现->热门->只看附近的人->群打招呼
    menu_faxian_hot_viewuserinfo,               //发现->热门->查看用户资料(外层传递touid)
    menu_faxian_hot_picturenum,                 //发现->热门->图片数量按钮
    menu_faxian_hot_btnvideo,                   //发现->热门->发视频按钮
    menu_faxian_hot_btnvoice,                   //发现->热门->发语音按钮
    menu_faxian_hot_btnsendmessage,             //发现->热门->发私信
    menu_faxian_hot_btngirl,                    //发现->热门->发礼物
    menu_faxian_hot_btngirl_zongsong,           //发现->热门->发礼物->赠送
    menu_faxian_hot_btngirl_zongsong_ljcz,      //发现->热门->发礼物->赠送->立即充值
    menu_faxian_hot_btngirl_pay,                //发现->热门->发礼物->充值(普通点击,外层传递touid)
    menu_faxian_hot_slideremove,                //发现->热门->发礼物->滑动移除一个用户(普通点击,外层传递touid)
    menu_faxian_banner,                         //发现->点击banner区域
    menu_faxian_tuijian_click_head,             //发现->推荐->点击用户头像

    // --------------消息---------------
    menu_xiaoxi_myfriend,                   // 消息->我的好友
    menu_xiaoxi_myfriend_lahei,             // 消息->我的好友->黑名单
    menu_xiaoxi_myfriend_lahei_viewuserinfo,// 消息->我的好友->黑名单->查看用户资料
    menu_xiaoxi_myfriend_lahei_remove,      // 消息->我的好友->黑名单->移出黑名单
    menu_xiaoxi_myfriend_chatframe,         // 消息->我的好友->打开聊天框(需要传递to_uid)
    menu_xiaoxi_sgzw,                       // 消息->谁关注我
    menu_xiaoxi_chatframe,                  // 消息->打开聊天框(需要传递to_uid)
    menu_xiaoxi_deluser,                    // 消息->删除用户
    menu_xiaoxi_sgzw_wgz,                   // 消息->谁关注我->我关注的
    menu_xiaoxi_sgzw_wgz_cancelfollow,      // 消息->谁关注我->我关注的->取消关注
    menu_xiaoxi_sgzw_wgz_viewuserinfo,      // 消息->谁关注我->我关注的->查看用户资料
    menu_xiaoxi_sgzw_gzw,                   // 消息->谁关注我->关注我的
    menu_xiaoxi_sgzw_gzw_vippay,            // 消息->谁关注我->关注我的->升级VIP会员,查看关注用户资料(男用户)
    menu_xiaoxi_sgzw_gzw_viewuserinfo,      // 消息->谁关注我->关注我的->查看用户资料
    menu_xiaoxi_sgzw_gzw_cancelfollow,      // 消息->谁关注我->关注我的->取消关注
    menu_xiaoxi_sgzw_gzw_followit,          // 消息->谁关注我->关注我的->关注TA
    menu_xiaoxi_mymoney,                    // 消息->我要赚钱(普通点击,女用户)
    page_chatframe_msg_addlabourunion,      // 聊天框->消息面板->加入公会(女用户,普通点击,touid传男用户UID)

    // --------------风云榜---------------
    menu_fengyunbang_bz,                    //菜单->风云榜->本周(普通点击)
    menu_fengyunbang_sz,                    //菜单->风云榜->上周(普通点击)

    // --------------我的---------------
    menu_me_todaymoney,                     // 我的->今日收益->立即查看(女用户)
    menu_me_vippay,                         // 立即开通VIP
    menu_me_meauth,                         // 我的认证
    menu_me_meauth_videoauth,               // 视频认证
    menu_me_meauth_id,                      // 身份认证
    menu_me_meauth_id_submit,               // 提交按钮(上传资料信息)
    menu_me_meauth_videoauth_capturepicture,// 拍摄照片按钮
    menu_me_meauth_videoauth_capturevideo,  // 拍摄视频按钮
    menu_me_meauth_telauth,                 // 手机认证
    menu_me_meauth_telauth_btnverifycode,   // 获取验证码
    menu_me_meauth_telauth_btnverify,       // 立即验证按钮
    menu_me_top_ljbd,                       // 个人中心->立即绑定(普通点击,顶部黄色提示条链接)
    menu_me_avatar,                         // 上传头像
    menu_me_sgzw,                           // 谁关注我
    menu_me_money,                          // 我的钱包
    menu_me_money_withdraw,                 // 立即提现
    menu_me_money_explain,                  // 提现说明
    menu_me_money_onekey,                   // 一键放入钱袋
    menu_me_redpackage,                     // 我要赚红包
    menu_me_redpackage_sylw,                // 我要赚红包->索要礼物(普通点击)
    menu_me_redpackage_sylw_send,           // 我要赚红包->索要礼物->发送按钮
    menu_me_redpackage_sylw_voice,          // 我要赚红包->索要礼物->按住说话按钮(普通点击)
    menu_me_redpackage_dzp,                 // 我的->我要赚红包->开心大转盘(普通点击)
    menu_me_y,                              // 我的Y币
    menu_me_gem,                            // 我的钻石
    menu_me_gem_explain,                    // 钻石说明
    menu_me_gem_btnpay,                     // 点击钻石支付按钮
    menu_me_gift,                           // 我的礼物
    menu_me_myhome,                         // 我的主页
    menu_me_profile,                        // 个人资料
    menu_me_album,                          // 我的相册
    menu_me_setting,                        // 设置中心
    menu_me_setting_enablevideo,            // 开启视频通话
    menu_me_setting_enablevoice,            // 开启语音通话
    menu_me_setting_newmsgalert,            // 新消息提醒
    menu_me_setting_shockalert,             // 震动提醒
    menu_me_setting_soundalert,             // 声音提醒
    menu_me_setting_exitmsgalert,           // 退出消息提醒
    menu_me_setting_modifypassword,         // 修改密码
    menu_me_setting_feedback,               // 意见反馈
    menu_me_setting_checkupdates,           // 检查更新
    menu_me_setting_huodong,                // 活动相关
    menu_me_setting_about,                  // 关于
    menu_me_setting_about_kefuservice,      // 关于-客服服务
    menu_me_setting_clearcache,             // 清除缓存
    menu_me_setting_signout,                // 退出按钮

    // --------------封停弹窗---------------
    page_stopframe_close,           // 封停弹窗->关闭(普通点击,传递当前用户UID哦!)

    // --------------聊天框---------------
    chatframe_tool_btnsend,         // 发送按钮
    chatframe_tool_btnvoice,        // 语音说话按钮
    chatframe_tool_btngift,         // 礼物按钮
    chatframe_tool_face,            // 表情按钮
    chatframe_tool_prcture,         // 聊天框->工具栏->图片按钮(普通点击)
    chatframe_tool_video,           // 聊天框->工具栏->视频聊天按钮(普通点击)
    chatframe_tool_voice,           // 聊天框->工具栏->语音聊天按钮(普通点击)

    // --------------聊天框(2.2 消息面板新增消息类型)---------------
    page_chatframe_msg_invite_rejected,         // 聊天框->消息->音视频邀请->拒绝接收
    page_chatframe_msg_invite_accept,           // 聊天框->消息->音视频邀请->立即接通
    page_chatframe_msg_invitetimeout_gocall,    // 聊天框->消息->邀请已过有效时间->立即回拨
    page_chatframe_inviteta_video,              // 聊天框->邀请TA->邀请视频(普通点击,女用户)
    page_chatframe_inviteta_voice,              // 聊天框->邀请TA->邀请语音(普通点击,女用户)
    page_chatframe_inviteta_cancel,             // 聊天框->邀请TA->取消(普通点击,女用户)

    // --------------聊天框(2.2 Y币余额浮动提示)---------------
    page_chatframe_gopayy,                  // 聊天框->立即购买Y币(传递当前Y币余额)
    page_chatframe_gopayclose,              // 聊天框->立即购买Y币->'X'关闭符号(普通点击)
    page_chatframe_closemoneyprompt_cancel, // 聊天框->关闭余额提示->取消(普通点击)
    page_chatframe_closemoneyprompt_confirm,// 聊天框->关闭余额提示->确认关闭(普通点击)

    // --------------聊天框(礼物和充值相关)统计项---------------
    chatframe_tool_gift_pay,                //礼物框充值钻石链接
    chatframe_tool_gift_give,               //赠送按钮
    chatframe_tool_gift_give_btnljcz,       //聊天框->送礼物弹窗->充值->立即充值
    chatframe_nav_y_ypay_btnqrzf,           //聊天框->导航栏->Y币->Y币充值确认支付按钮
    chatframe_nav_tel_vippay_btnqrzf,       //聊天框->导航栏->查看手机->vip开通确认支付按钮
    chatframe_nav_weixin_vippay_btnqrzf,    //聊天框->导航栏->查看微信->vip开通确认支付按钮

    // --------------充值页面统计项（H5）---------------
    pay_y_btnljzf,  // 立即支付按钮:y币
    pay_vip_btnljzf,// 立即支付按钮:vip
    pay_zixun,      // 充值咨询按钮

    // --------------用户资料---------------
    userinfo_btnsendmessage,        // 发信按钮
    userinfo_btnsayhello,           // 打招呼按钮
    userinfo_btnfollow,             // 关注按钮
    userinfo_btngirl,               // 礼物按钮
    userinfo_btnvideo,              // 发视频
    userinfo_btnvoice,              // 发语音
    userinfo_album,                 // 相册按钮(传递to_uid,第几张照片,照片index)
    userinfo_navalbum_leftflip,     // 左翻相册
    userinfo_navalbum_rightflip,    // 右翻相册

    userinfo_more_setting_remark,           //用户资料->更多->备注名(传递最新备注信息)
    userinfo_more_setting_clear,            //用户资料->更多->清空聊天记录(普通点击,外层传递touid)
    userinfo_more_setting_shield,           //用户资料->更多->屏蔽(普通点击,外层传递touid)
    userinfo_more_setting_jubao,            //用户资料->更多->举报(普通点击,外层传递touid)
    userinfo_more_setting_jubao_submit,     //用户资料->更多->举报->提交按钮(上传举报资料)
    userinfo_face,                          //用户资料->点击用户头像(普通点击,外层传递touid)

    // --------------视频语音框--------------
    video_close,                // 视频挂断(传递to_uid)
    voice_close,                // 语音挂断(传递to_uid)
    voice_tool_chat,            //视频语音框->工具栏->聊天
    voice_tool_voice_enable,    //视频语音框->工具栏->开启语音
    voice_tool_voice_disable,   //视频语音框->工具栏->关闭语音
    voice_tool_dzp,             //视频语音框->工具栏->大转盘
    voice_tool_dzp_loop,        //视频语音框->工具栏->大转盘->转
    voice_tool_gift,            //视频语音框->工具栏->礼物
    voice_tool_gift_give,       //视频语音框->工具栏->礼物->赠送
    voice_tool_ps,              //视频语音框->工具栏->美颜
    voice_tool_avatar,          //视频语音框->工具栏->头像
    voice_tool_closeall,        //视频语音框->工具栏->关闭结束聊天
    voice_tool_closevideo,      //视频语音框->工具栏->关闭自己摄像头
    voice_tool_enablevideo,     //视频语音框->工具栏->开启自己摄像头
    voice_tool_switch,          //视频语音框->工具栏->切换
    voice_tool_send,            //视频语音框->工具栏->发送消息

    // --------------语音/视频邀请等待页--------------
    page_invitewait_close,          // 语音视频邀请等待页->挂断按钮(传递touid哦!)
    page_invitewait_accept,         // 语音视频邀请等待页->接受(男性,传递touid哦!)
    page_invitewait_rejected,       // 语音视频邀请等待页->拒绝按钮(男性,传递touid哦!)
    page_invitewait_gempay_gopay,   // 语音视频邀请等待页->钻石不足充值->立即支付(to_uid等于邀请发起人)
    page_invitewait_gempay_cancel,  // 语音视频邀请等待页->钻石不足充值->取消(to_uid等于邀请发起人)

    // --------------欢迎页---------------
    welcome_login,  //登录按钮
    welcome_regist, //注册按钮

    // --------------登录页---------------
    login_btnlogin,                     //登录按钮
    login_freereg,                      //免费注册按钮
    login_findpassword,                 //用户登录->忘记密码(普通点击)
    login_findpassword_confirm,         //用户登录->忘记密码->确定(上传表单信息)
    login_findpassword_getverifycode,   //用户登录->忘记密码->获取验证码(上传表单信息)

    // --------------注册页---------------
    regist_btnreg,                      //注册按钮
    regist_success,                     //完善个人资料->完成(上传资料信息)
    regist_uploadface_choosepicture,    //上传头像->相册选取(普通点击)

    // --------------登录后引导---------------
    login_guide_onekeysayhello,     // 一键打招呼(男用户)
    login_guide_moneyhelp,          // 如何赚钱(女用户)
    // 以下为2.3女性任务版新添加的统计项
    page_loginpopup_money_close,    // 登录后弹窗->赚钱秘籍->关闭(普通点击)
    page_loginpopup_money_goview,   // 登录后弹窗->赚钱秘籍->立即查看秘籍(普通点击)
    page_loginpopup_auth_go,        // 登录后弹窗->资料认证->去认证(普通点击)
    page_loginpopup_auth_cancel,    // 登录后弹窗->资料认证->取消(普通点击)

    // =============================2.3女性任务版模块化功能 start=============================

    // --------------我要赚钱，部分统计由h5进行添加，详情见大数据文档--------------
    page_mymoney_labourunion_sumbit,    // 我要赚钱->加入公会弹框->提交
    page_mymoney_labourunion_canel,     // 我要赚钱->加入公会弹框->取消(普通点击)
    page_mymoney_auth_go,               // 我要赚钱->解锁赚钱任务完成相关认证->去认证(普通点击)
    page_mymoney_auth_canel,            // 我要赚钱->解锁赚钱任务完成相关认证->取消(普通点击)

    // --------------主动视频邀请，部分统计由h5进行添加，详情见大数据文档--------------
    page_invitevideo_batchsendvideo_prompt_goenable,//群发视频->未开启视频提示->去开启(普通点击)
    page_invitevideo_batchsendvideo_prompt_cancel,  // 群发视频->未开启视频提示->取消(普通点击)

    // --------------群发语音(女用户发送)，部分统计由h5进行添加，详情见大数据文档--------------
    page_invitevoice_batchsendvoice_prompt_goenable,// 群发语音->未开启语音提示->去开启(普通点击)
    page_invitevoice_batchsendvoice_prompt_cancel,  // 群发语音->未开启语音提示->取消(普通点击)

    // --------------索要礼物(女用户发送)，部分统计由h5进行添加，详情见大数据文档--------------
    page_askforgift_img_prompt_send,    // 索要礼物->私密照片->发送私房照->发送
    page_askforgift_video_prompt_send,  // 索要礼物->私密视频->发送私密视频->发送

    // --------------索要礼物-聊天框页(男用户收到)--------------
    page_chatframe_msg_privateimg_goview,               // 聊天框->消息->私密照片->立即查看(普通点击,touid等于女用户UID)
    page_chatframe_msg_privatevideo_goview,             // 聊天框->消息->私密视频->立即查看
    page_chatframe_msg_privatevideo_dashang_payview,    // 聊天框->消息->私密视频->查看私密视频需打赏->打赏查看
    page_chatframe_msg_privatevideo_mood_submit,        // 聊天框->消息->私密视频->本次感受->确定
    page_chatframe_msg_privatemsg_goview,               // 聊天框->消息->私密消息->立即查看
    page_chatframe_msg_privatemsg_dashang_pay,          // 聊天框->消息->私密消息->打赏TA->确认打赏
    page_chatframe_msg_privatevoice_golisten,           // 聊天框->消息->私密语音->打开听听
    page_chatframe_msg_privatevoice_dashang_pay,        // 聊天框->消息->私密语音->打赏TA->确认打赏
    // =============================2.3女性任务版模块化功能 end=============================

    // =============================女性自动回复版模块化功能 start=============================
    menu_me_autoreply,          // 我的->自动回复设置(普通点击)
    page_setautoreply_add,      // 设置自动回复页面->立即添加(右上角的'+'和中间的按钮都是这个)(普通点击)
    page_setautoreply_video,    // 设置自动回复页面->自动邀请对方视频(默认开启)
    page_setautoreply_vioce,    // 设置自动回复页面->自动邀请对方语音
    page_setautoreply_alert_set_confirm,    // 设置您的自动回复语音或文字->确定
    page_peopleofsayhi_autoreply,           // 打招呼的人页面->自动回复(普通点击)
    // =============================女性自动回复版模块化功能 end=============================

    // =============================AB测试版模块化功能 start=============================
    // ----------------弹窗-----------------
    alert_keys_receive,         // 获得1把钥匙弹窗->立即领取(普通点击)
    alert_vip_pay,              // 开通VIP弹窗->确认支付
    alert_y_pay,                // 充值y币弹窗->确认支付(A版,B版无y币)
    alert_gem_pay,              // 充值钻石弹窗->立即支付
    alert_unlock_confirm,       // 聊天页->解锁聊天->确认解锁
    alert_buykeys_confirm,      // 购买钥匙弹窗->确定
    alert_videocard_use,        // 视频卡弹窗->立即使用
    alert_videocard_donotuse,   // 视频卡弹窗->知道了,暂时不使用
    alert_push_refuse,          // A版视频内推->拒绝按钮
    alert_push_accept,          // A版视频内推->接受按钮

    // ----------------聊天页-----------------
    page_chat_unlock,           // 聊天页->解锁与此美女聊天，需1把钥匙，可得红包(普通点击)
    page_chat_redpackage,       // 聊天页->红包(普通点击)
    page_chat_redpackage_alert_unlock,  // 聊天页->红包->马上解锁(普通点击)
    page_chat_redpackage_open,          // 聊天页->红包->开
    page_chat_redpackage_open_receive,  // 聊天页->红包->开->领取
    page_chat_redpackage_open_give,     // 聊天页->红包->开->赠送
    page_chat_msg_redpackage,           // 聊天页->查看红包(普通点击)
    page_chat_alert_redpackage_receive, // 聊天页->恭喜获得红包弹窗->领取
    page_chat_alert_redpackage_close,   // 聊天页->恭喜获得红包弹窗->关闭

    // ----------------个人中心-----------------
    menu_me_mykeys,             // 我的->我的钥匙(普通点击)

    // ----------------发现-----------------
    menu_faxian_filter,                 // 发现->筛选(右上角)(普通点击)
    menu_faxian_personfilter,           // 发现->个性化筛选(普通点击)
    menu_faxian_alert_select_sayhello,  // 发现->筛选->打招呼
    menu_faxian_alert_sayhello_cancel,  // 发现->筛选->打招呼->取消
    menu_faxian_alert_sayhello_confirm, // 发现->筛选->打招呼->确定
    menu_faxian_sousuo,                 // 发现->搜索
    menu_faxian_yuliao,                 // 发现->语聊(普通点击)
    menu_faxian_yuliao_first,           // 发现->语聊->页面第一次加载
    menu_faxian_yuliao_flush,           // 发现->语聊->刷新
    menu_faxian_tuijian_first,          // 发现->推荐->页面第一次加载
    menu_faxian_tuijian_flush,          // 发现->推荐->刷新
    menu_faxian_yuliao_click_head,      // 发现->语聊->点击用户头像

    // =============================AB测试版模块化功能 end=============================

    // --------------密友--------------
    // --------------用户主页--------------
    page_home_shoufuta,          // 女用户主页—>收服她(普通点击)
    alert_shoufuta_give,          // 收服她->赠送
    alert_shoufuta_give_ok,          // 收服她->赠送->知道了(普通点击)
    page_home_jiamiyou,          // 男用户主页->加密友(普通点击)
    alert_jiamiyou_suoyao,          // 男用户主页->加密友->索要
    alert_jiamiyou_suoyao_ok,          // 加密友->索要->知道了(普通点击)

    //------------------资料页----------------------
    page_info_juewei,                   //用户资料页->爵位(普通点击)
    page_info_shouhu,                   //用户资料页->守护(普通点击)
    page_info_zuojia,                   //用户资料页->座驾(普通点击)

    // --------------消息--------------
    menu_xiaoxi_miyou, //消息->我的密友(普通点击)
    menu_xiaoxi_miyou_task, //消息->我的密友->任务(普通点击)
    menu_xiaoxi_miyou_alert_yes, //消息->我的密友->解除密友关系(解除密友关系/解除关系)
    menu_xiaoxi_miyou_alert_no, //消息->我的密友->解除密友关系(解除密友关系/考虑一下)
    menu_xiaoxi_miyou_explain, //消息->我的密友->密友说明(普通点击)
    menu_xiaoxi_miyou_task_todo, //消息->我的密友->任务->去完成
    menu_xiaoxi_miyou_task_sendtask, //消息->我的密友->任务->发送任务

    // --------------聊天--------------
    page_chat_expansion, //聊天页->亲密度列表展开(普通点击)
    page_chat_close, //聊天页->亲密度列表收起(普通点击)
    page_chat_alert_qinmidu_cancel, //聊天页->亲密度礼物弹窗->取消
    page_chat_alert_qinmidu_give, //聊天页->亲密度礼物弹窗->赠送
    page_chat_shoufuta, //聊天页->收服她
    page_chat_jiamiyou, //聊天页->加密友
    page_chat_msg_miyou_accepther, //聊天页->我想和你成为密友->接受她(普通点击)
    page_chat_msg_miyou_refuseher, //聊天页->我想和你成为密友-拒绝她(普通点击)
    page_chat_expansion_task, //聊天页->亲密度列表展开->任务(普通点击)
    page_chat_click_head,     //聊天页->点击用户头像

    // --------------发现--------------
    menu_faxian_peerage, //发现->贵族(普通点击)
    menu_faxian_zhuinvshen,

    // --------------有关爵位升级的弹窗--------------
    alert_peerage_first_viewprivileges, //首次钻石消费进入爵位弹窗->查看特权(普通点击)
    alert_peerage_upgrade_cancel, //即将升爵位弹窗->取消(普通点击)
    alert_peerage_upgrade_give, //即将升爵位弹窗->赠送

    menu_faxian_rank, //发现->左上角"榜"(普通点击)

    // --------------魅力榜 土豪榜页面--------------
    page_rank_meili_thisweek, //排行榜页面->魅力榜->本周排行
    page_rank_meili_lastweek, //排行榜页面->魅力榜->上周排行
    page_rank_tuhao_thisweek, //排行榜页面->土豪榜->本周排行
    page_rank_tuhao_lastweek, //排行榜页面->土豪榜->上周排行

    // --------------好友 原来通过消息进入我的好友全部删除，但是删掉的功能找不到去哪里了
    menu_haoyou,                //好友(普通点击)
    menu_haoyou_question,       //好友->左上角问号(普通点击)
    menu_haoyou_rule,           //好友->好友规则说明(普通点击)
    menu_haoyou_blacklist,      //好友->黑名单
    menu_haoyou_click,          //好友->点击好友(普通点击)
    alert_haoyou_click_task,    //好友弹窗->点击右侧的去视频或者去语音或者去聊天按钮(普通点击)
    alert_haoyou_click_chat,    //好友弹窗->点击最下面的聊天大按钮(普通点击)
    alert_haoyou_busy_random,   //友好->对方忙线中弹窗->随机挑一位
    alert_haoyou_busy_close,    //友好->对方忙线中弹窗->关闭(普通点击)
    // -------------- end 密友--------------

    //---------------直播------------------
    menu_zhibo,
    //直播间页面
    page_live_avatar,//直播间->点击主播头像(普通点击)
    page_live_avatar_mkfriendadd,//视频语音框->主播头像右侧->加好友(普通点击,左上角头像)
    page_live_avatar_mkfriendadd_sendgirl_pay,//视频语音框->主播头像右侧->加好友->充值(普通点击)
    page_live_avatar_mkfriendadd_sendgirl_zengsong,//视频语音框->主播头像右侧->加好友->赠送
    page_live_pubchat,//直播间->公聊按钮(普通点击)
    page_live_sendmsg,//直播间->私信按钮(普通点击)
    page_live_shared,//直播间->分享按钮(普通点击)
    page_live_girl,//直播间->礼物按钮(普通点击)
    page_live_girl_pay,//直播间->礼物按钮->充值按钮(普通点击)
    page_live_close,//直播间->关闭按钮(普通点击)
    page_live_sendmsg_btnsend,//直播间->私信按钮->发送按钮
    page_live_usermanage_tichu,//直播间->用户管理->踢出
    page_live_alert_tichu_confirm,//直播间->用户管理->踢出->警示弹窗->确认踢出
    page_live_alert_tichu_cancel,//直播间->用户管理->踢出->警示弹窗->考虑一下
    page_live_usermanage_jinyan,//直播间->用户管理->禁言
    page_live_alert_jinyan_confirm,//直播间->用户管理-禁言->警示弹窗->确认禁言
    page_live_alert_jinyan_cancel,//直播间->用户管理->禁言->警示弹窗->考虑一下
    page_live_usermanage_lahei,//直播间->用户管理->拉黑
    page_live_alert_lahei_confirm,//直播间->用户管理-拉黑->警示弹窗->确认拉黑
    page_live_alert_lahei_cancel,//直播间->用户管理->拉黑->警示弹窗->考虑一下
    page_live_usermanage_changkong,//直播间->用户管理->场控
    page_live_alert_changkong_confirm,//直播间->用户管理-->警示弹窗->确认场控
    page_live_alert_changkong_cancel,//直播间->用户管理->场控->警示弹窗->考虑一下
    page_live_seting,//直播间->设置(普通点击,主播设置按钮)
    page_live_seting_meiyan,//直播间->设置->美颜(普通点击)
    page_live_seting_qianhou,//直播间->设置->前后(普通点击)
    page_live_seting_jingxiang,//直播间->设置->镜像(普通点击)
    page_live_seting_dengguang,//直播间->设置->灯光(普通点击)
    page_live_seting_yinyue,//直播间->设置->音乐(普通点击)
    page_live_seting_manage,//直播间->设置->管理(普通点击)
    page_live_seting_manage_downrefresh,//直播间->设置->管理->下拉刷新(普通点击)
    page_live_seting_manage_tichu,//直播间->设置->管理->剔除(普通点击,传递touid)
    page_live_seting_manage_jinyan,//直播间->设置->管理->禁言(普通点击,传递touid)
    page_live_seting_manage_lahei,//直播间->设置->管理->拉黑(普通点击,传递touid)
    page_live_seting_manage_changkong,//直播间->设置->管理->场控(普通点击)
    page_live_chatframe_close,//直播间->私信聊天框->关闭(普通点击)
    page_live_chatframe_success,//直播间->私信聊天框->完成(普通点击,发送按钮,传递touid哦)
    page_live_chatframe_back,//直播间->私信聊天框->返回(普通点击)
    page_live_chatframe_voice,//直播间->私信聊天框->语音按钮(普通点击)
    page_live_chatframe_face,//直播间->私信聊天框->表情按钮(普通点击)
    page_live_chatlist_close,//直播间->私信列表->关闭按钮(普通点击)
    page_live_chatlist_viewmsg,//直播间->私信列表->查看消息(普通点击)
    page_live_chatlist_face,//直播间->私信列表->表情按钮(普通点击)
    page_live_chatlist_voice,//直播间->私信列表->语音按钮(普通点击)
    page_live_chatlist_success,//直播间->私信列表->完成(普通点击,发送按钮)
    page_live_girl_send,//直播间->礼物按钮->发送按钮
    page_live_firstbroadcast_start,//直播间->首次直播->开始直播(普通点击)
    page_live_gameover,//直播间->直播结束(页面打开之后)
    page_live_gameover_mkfriend,//直播间->直播结束->加好友(页面打开之后)(看直播的)(普通点击)
    page_live_gameover_viewmoney,//直播间->直播结束->查看收益(普通点击)
    page_live_gameover_adspace_click,//直播间->直播结束->广告位点击
    page_live_liao_click,//直播间->撩->点击任意一个弹出来的按钮
    page_live_send_danmu,//直播间->发弹幕
    menu_zhibo_firstload,
    menu_zhibo_flush,
    alert_gotoliveroom_close,//这是您要找的直播间吗？->右上‘x’关闭
    alert_gotoliveroom_go,//这是您要找的直播间吗？->进入直播间

    // --------------推广版本统计--------------

    page_info_share,            //个人资料页->右上角分享按钮(普通点击)
    page_share_share_save,      //推广赚钱页面->分享->保存到手机(普通点击)(安卓)
    page_share_share_close,     //推广赚钱页面->分享->关闭(普通点击)(安卓)
    menu_me_share,              //我的->推广赚钱

    // --------------红包版统计--------------

    page_xiaomishu_huodong,            //小秘书页面->右上角活动按钮(普通点击)
    page_xiaomishu_click,            //小秘书页面->点击活动
    alert_redpackage_backpack,            //获得红包弹窗->放入背包
    page_chat_click_readpackage,            //聊天页面->点击查看红包

    menu_me_vipmark,            //我的->VIP(普通点击)(vip-爵位-守护-座驾这一行)

    // --------------聊天框解锁与推广分享统计--------------

    alert_unlockchat_share,             //解锁聊天弹窗->点击微信按钮或其他按钮(分享解锁)(会跳到二维码分享名片页面向下看)
    alert_unlockchatsuccess_ok,         //解锁成功->好的
    alert_unlockchat_paysend,           //解锁聊天弹窗->付费发送(钻石解锁)
    alert_sendsuccess_ok,               //消息发送成功->知道了
    alert_unlockchat_give,              //解锁聊天弹窗->立即赠送(爵位解锁)
    page_qrcodeshare_share,             //右上角分享图标(选择了什么模板,在分享赚钱2.0中alert_sharecard_click/分享名片弹窗->点击任意类型分享按钮/传递)
    page_sharetomakemoney_gotoshare,    //去分享赚钱(普通点击)(右边是赚钱小秘书按钮)
    page_sharetomakemoney_xiaomishu,    //赚钱小秘书(普通点击)(左边是去分享赚钱按钮)
    page_sharetomakemoney_profitdetail, //右上角收益明细(普通点击)
    alert_sharecard_close,              //分享名片弹窗->右上‘x’关闭
    alert_sharecard_click,              //分享名片弹窗->点击任意类型分享按钮
    alert_share,                        //只要弹出来就算(类似pageview,然后是用户选择关闭还是点击立即赚钱)
    alert_share_close,                  //分享就能赚钱弹窗(一键打招呼关闭后那个弹窗)->右上‘x’关闭
    alert_share_makemoney,              //分享就能赚钱弹窗(一键打招呼关闭后那个弹窗)->立即赚钱
    alert_sharereward_close,            //分享奖励弹窗->右上‘x’关闭
    alert_gotouser_close,               //这是您要找的人吗？->右上‘x’关闭
    alert_gotouser_go,                  //这是您要找的人吗？->聊天
    menu_me_sharetomakemoney,           //我的->分享赚钱(普通点击)
    menu_me_homepage_share,             //我的->主页->分享(普通点击)(跟推广版里page_info_share/个人资料页->右上角分享按钮区分一下,从推广版里剥离出来)
    menu_me_myfans,                     //我的->我的粉丝
    menu_me_novicetutorial,             //我的->新手教程
    page_sharetomakemoney_myrank,       //我的排名
    page_sharetomakemoney_moneyschool,  //赚钱学堂
    page_sharetomakemoney_questionclick,//直接点击问题
    page_sharetomakemoney_morequestion, //更多问题
    page_moneyschool_questionclick,     //点击问题
    page_sharingtasks_sharewoman,       //分享给美女(普通点击)
    page_sharingtasks_shareman,         //分享给帅哥(普通点击)
    page_live_share,                    //分享按钮(普通点击)
    float_button_redpacketcard_close,   //红包卡悬浮按钮->关闭
    float_button_redpacketcard_click,   //红包卡悬浮按钮->点击
    alert_dayredpacketcard_close,       //每日打卡红包弹窗->关闭
    alert_dayredpacketcard_share,       //每日打卡红包弹窗->去分享并领取(走分享流程)
    alert_shareresult_ok,               //分享结果弹窗->知道了(分享成功)
    alert_shareresult_reshare,          //分享结果弹窗->重新分享(分享失败)
    page_Withdrawsuccess_share,         //分享完成提现 提现成功页面
}
