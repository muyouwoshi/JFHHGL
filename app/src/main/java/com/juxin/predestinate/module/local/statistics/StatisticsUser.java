package com.juxin.predestinate.module.local.statistics;

import com.juxin.predestinate.bean.center.update.AppUpdate;
import com.juxin.predestinate.bean.center.update.UpgradeSource;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人中心、个人资料统计项
 * Created by ZRP on 2017/6/10.
 */
public class StatisticsUser {

    // ------------------------用户升级统计--------------------------

    /**
     * 软件升级统计
     *
     * @param sendPoint     page_upgrade_box_pageview/page_upgrade_box_cancel/page_upgrade_box_confirm
     * @param appUpdate     软件升级请求返回结构体
     * @param upgradeSource 升级弹窗来源
     */
    public static void upgradeDialog(SendPoint sendPoint, AppUpdate appUpdate, UpgradeSource upgradeSource) {
        if (appUpdate == null) return;

        Map<String, Object> params = new HashMap<>();
        params.put("target_title", appUpdate.getTitle());       //升级标题,显示软件版本
        params.put("target_summary", appUpdate.getSummary());   //升级描述信息
        params.put("download_url", appUpdate.getUrl());         //升级版本下载地址
        params.put("target_package_name", appUpdate.getPackage_name());//升级客户端包名
        params.put("target_build_ver", appUpdate.getVersion()); //升级小版本号
        params.put("target_force", appUpdate.getForce());       //强制升级|1强制|2可选
        params.put("source", upgradeSource.toString());         //升级弹窗来源

        Statistics.userBehavior(sendPoint, params);
    }

    /**
     * 轻量升级全量弹窗
     *
     * @param sendPoint     page_upgrade_minibox_pageview/page_upgrade_minibox_confirm
     * @param download_url  全量包下载地址
     * @param upgradeSource 升级弹窗来源
     */
    public static void upgradeLiteDialog(SendPoint sendPoint, String download_url, UpgradeSource upgradeSource) {
        Map<String, Object> params = new HashMap<>();
//        params.put("app_id", Constant.LITE_UPDATE_APPID);
        params.put("download_url", download_url);
        params.put("source", upgradeSource.toString());

        Statistics.userBehavior(sendPoint, params);
    }

    // ------------------------用户资料统计--------------------------

    /**
     * 用户资料->照片(传递to_uid,第几张照片,照片ID,是否成功查看)
     *
     * @param to_uid        被查看相册的用户uid
     * @param picture       查看的照片
     * @param picture_index 被查看的照片位置,从左往右排序,从1开始
     * @param success       是否成功打开照片
     */
    public static void userAlbum(long to_uid, String picture, int picture_index, boolean success) {
        Map<String, Object> params = new HashMap<>();
        params.put("picture", picture);
        params.put("picture_index", picture_index);
        params.put("success", success);
        Statistics.userBehavior(SendPoint.userinfo_album, to_uid, params);
    }

    /**
     * 用户资料->照片->翻相册
     *
     * @param to_uid  被查看相册的用户uid
     * @param picture 查看的照片
     * @param isRight 是否为向右滑动相册
     */
    public static void userAlbumFlip(long to_uid, String picture, boolean isRight) {
        Map<String, Object> params = new HashMap<>();
        params.put("picture", picture);
        Statistics.userBehavior(isRight ? SendPoint.userinfo_navalbum_rightflip
                : SendPoint.userinfo_navalbum_leftflip, to_uid, params);
    }

    /**
     * 用户资料->更多->备注名
     *
     * @param to_uid 被备注的用户uid
     * @param remark 备注名
     */
    public static void userRemark(long to_uid, String remark) {
        Map<String, Object> params = new HashMap<>();
        params.put("remark", remark);
        Statistics.userBehavior(SendPoint.userinfo_more_setting_remark, to_uid, params);
    }

    /**
     * 用户资料->更多->举报->提交按钮
     *
     * @param to_uid  被举报的用户uid
     * @param type    举报类型
     * @param content 举报内容
     */
    public static void userReport(long to_uid, String type, String content) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("content", content);
        Statistics.userBehavior(SendPoint.userinfo_more_setting_jubao_submit, to_uid, params);
    }

    /**
     * 手机验证统计
     *
     * @param phone    手机号输入框内容
     * @param code     验证码输入框内容
     * @param isCommit 是否是立即验证提交按钮
     */
    public static void userPhoneVerify(String phone, String code, boolean isCommit) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("tel", phone);//手机号文本框输入的内容
        params.put("verifycode", code);//短信验证码文本框的内容
        Statistics.userBehavior(isCommit ? SendPoint.menu_me_meauth_telauth_btnverify
                : SendPoint.menu_me_meauth_telauth_btnverifycode, params);
    }

    /**
     * 索要礼物统计
     *
     * @param msg    索要礼物文字内容或语音连接
     * @param giftId 礼物id
     */
    public static void userAskForGift(String msg, int giftId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("msg", msg);
        params.put("giftId", giftId);
        Statistics.userBehavior(SendPoint.menu_me_redpackage_sylw_send, params);
    }

    /**
     * 我的->我的钻石->点击钻石支付按钮
     *
     * @param gem_num 钻石数量按钮,只传钻石数量即可
     */
    public static void centerGemPay(int gem_num) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("gem_num", gem_num);
        Statistics.userBehavior(SendPoint.menu_me_gem_btnpay, params);
    }

    /**
     * 我的->我的相册(上传照片数量)
     */
    public static void centerAlbum() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("picture_number", ModuleMgr.getCenterMgr().getMyInfo().getUserPhotos().size());
        Statistics.userBehavior(SendPoint.menu_me_album, params);
    }

    /**
     * 我的认证->身份认证->提交按钮(上传资料信息)
     *
     * @param name                  //姓名
     * @param id_number             身份证号
     * @param pay_type              支付方式(zhifubao/bank)
     * @param bank_kaihuhang        开户行
     * @param bank_zhihang          开户支行
     * @param bank_cardid           银行卡号
     * @param zhifubao_account      支付宝账号
     * @param pic_idnumber_positive 身份证正面图片URL
     * @param pic_idnumber_contrary 身份证正反面图片URL
     * @param pic_idnumberinhand    手持身份证照片
     */
    public static void meauthIdSubmit(String name, String id_number, int pay_type, String bank_kaihuhang, String bank_zhihang, String bank_cardid, String zhifubao_account, String pic_idnumber_positive, String pic_idnumber_contrary, String pic_idnumberinhand) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("id_number", id_number);
        params.put("pay_type", pay_type);
        params.put("bank_kaihuhang", bank_kaihuhang);
        params.put("bank_zhihang", bank_zhihang);
        params.put("bank_cardid", bank_cardid);
        params.put("zhifubao_account", zhifubao_account);
        params.put("pic_idnumber_positive", pic_idnumber_positive);
        params.put("pic_idnumber_contrary", pic_idnumber_contrary);
        params.put("pic_idnumberinhand", pic_idnumberinhand);
        Statistics.userBehavior(SendPoint.menu_me_meauth_id_submit, params);
    }

    /**
     * 登录页->登录按钮(无需检测登录成功状态,点击按钮一次上报一次日志)
     *
     * @param username 用户账号,取文本框输入内容
     * @param password 账号密码,取文本框输入内容
     */
    public static void userLogin(String username, String password) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("msg", "");//登录状态消息(可选)
        Statistics.userBehavior(SendPoint.login_btnlogin, params);
    }

    /**
     * 用户登录->忘记密码->确定(上传表单信息)
     *
     * @param tel          手机号码文本框
     * @param verify_code  验证码文本框
     * @param new_password 新密码文本框
     */
    public static void findPWConfirm(String tel, String verify_code, String new_password) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("tel", tel);
        params.put("verify_code", verify_code);
        params.put("new_password", new_password);
        Statistics.userBehavior(SendPoint.login_findpassword_confirm, params);
    }

    /**
     * 用户登录->忘记密码->获取验证码(上传表单信息)
     *
     * @param tel          手机号码文本框
     * @param verify_code  验证码文本框
     * @param new_password 新密码文本框
     */
    public static void findPWGetCode(String tel, String verify_code, String new_password) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("tel", tel);
        params.put("verify_code", verify_code);
        params.put("new_password", new_password);
        Statistics.userBehavior(SendPoint.login_findpassword_getverifycode, params);
    }

    /**
     * 用户注册->注册按钮(无需检测注册成功状态,点击按钮记录一次日志)
     *
     * @param nick   昵称
     * @param age    年龄
     * @param gender 性别
     */
    public static void userRegister(String nick, int age, int gender) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("nick", nick);
        params.put("age", age);
        params.put("sex", gender == 1 ? "男" : "女");
        params.put("msg", "");//登录状态消息(可选)
        Statistics.userBehavior(SendPoint.regist_btnreg, params);
    }

    /**
     * 完善个人资料->完成(上传资料信息)
     *
     * @param avatar     头像照片url
     * @param profession 职业
     * @param education  学历
     * @param height     身高
     * @param marriage   婚姻
     */
    public static void registerSuccess(String avatar, String profession, String education, int height, String marriage) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("avatar", avatar);
        params.put("profession", profession);
        params.put("education", education);
        params.put("height", height);
        params.put("marriage", marriage);
        Statistics.userBehavior(SendPoint.regist_success, params);
    }

    // 登录后引导->一键打招呼,登录后引导(男用户)
    public static void dailyOneKeySayHi(List<UserInfoLightweight> data) {
        List<Long> uids = new ArrayList<>();
        for (UserInfoLightweight lightInfo : data) {
            uids.add(lightInfo.getUid());
        }
        Map<String, Object> params = new HashMap<>();
        params.put("to_uid", uids.toArray(new Long[uids.size()]));
        Statistics.userBehavior(SendPoint.login_guide_onekeysayhello, params);
    }

    public static void dailyOneKeySayHiByABTest(long[] ids) {
        Map<String, Object> params = new HashMap<>();
        params.put("to_uid", ids);
        Statistics.userBehavior(SendPoint.login_guide_onekeysayhello, params);
    }

    // --------------------------缘分吧2.3女性任务版新增统计--------------------------------

    /**
     * 我要赚钱->加入公会弹框->提交
     *
     * @param group_id 公会ID,用户在文本框中输入的内容
     */
    public static void userAddUnionSubmit(String group_id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("group_id", group_id);
        Statistics.userBehavior(SendPoint.page_mymoney_labourunion_sumbit, params);
    }

    /**
     * 我的->今日收益->立即查看(女用户)
     *
     * @param money_num 今日收益元
     */
    public static void userTodayMoney(double money_num) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("money_num", money_num);
        Statistics.userBehavior(SendPoint.menu_me_todaymoney, params);
    }

    /**
     * 认证弹窗
     *
     * @param fromType 弹框来源：Constant#OPEN_FROM_HOME/OPEN_FROM_CMD
     * @param isCancel 是否为点击取消按钮
     */
    public static void userAuthDialog(int fromType, boolean isCancel) {
        SendPoint sendPoint = SendPoint.page_loginpopup_auth_cancel;
        if (fromType == Constant.OPEN_FROM_HOME) {
            sendPoint = isCancel ? SendPoint.page_loginpopup_auth_cancel : SendPoint.page_loginpopup_auth_go;
        } else if (fromType == Constant.OPEN_FROM_CMD) {
            sendPoint = isCancel ? SendPoint.page_mymoney_auth_canel : SendPoint.page_mymoney_auth_go;
        }
        Statistics.userBehavior(sendPoint);
    }

    /**
     * 未开启视频弹框
     *
     * @param openType 弹框来源：VideoAudioChatHelper#TYPE_VIDEO_CHAT/TYPE_AUDIO_CHAT
     * @param isCancel 是否为点击取消按钮
     */
    public static void userOpenAVDialog(int openType, boolean isCancel) {
        SendPoint sendPoint = SendPoint.page_loginpopup_auth_cancel;
        if (openType == AgoraConstant.RTC_CHAT_VIDEO) {
            sendPoint = isCancel ? SendPoint.page_invitevideo_batchsendvideo_prompt_cancel
                    : SendPoint.page_invitevideo_batchsendvideo_prompt_goenable;
        } else if (openType == AgoraConstant.RTC_CHAT_VOICE) {
            sendPoint = isCancel ? SendPoint.page_invitevoice_batchsendvoice_prompt_cancel
                    : SendPoint.page_invitevoice_batchsendvoice_prompt_goenable;
        }
        Statistics.userBehavior(sendPoint);
    }

    /**
     * 索要礼物->私密照片->发送私房照->发送
     *
     * @param text    文本消息
     * @param gem_num 礼物的钻石数量
     * @param girl_id 礼物ID
     * @param images  私密图片url
     */
    public static void userPicAskGift(String text, int gem_num, int girl_id, List<String> images) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("text", text);
        params.put("gem_num", gem_num);
        params.put("girl_id", girl_id);
        params.put("img", images);
        Statistics.userBehavior(SendPoint.page_askforgift_img_prompt_send, params);
    }

    /**
     * 索要礼物->私密视频->发送私密视频->发送
     *
     * @param gem_num 礼物的钻石数量
     * @param girl_id 礼物ID
     * @param video   视频地址
     */
    public static void userVideoAskGift(int gem_num, int girl_id, String video) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("gem_num", gem_num);
        params.put("girl_id", girl_id);
        params.put("video_url", new String[]{video});
        Statistics.userBehavior(SendPoint.page_askforgift_img_prompt_send, params);
    }

    // --------------------------缘分吧女性自动回复版新增统计--------------------------------

    /**
     * 女性设置自动回复页面，开关点击状态
     *
     * @param type   1视频，2音频
     * @param status 0代表关闭|1代表开启
     */
    public static void setAutoReply(int type, int status) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("status", status);
        Statistics.userBehavior(type == 1 ? SendPoint.page_setautoreply_video
                : SendPoint.page_setautoreply_vioce, params);
    }

    /**
     * 女性设置自动回复页面，添加自动回复按钮确定
     *
     * @param type    0代表语音|1代表文字
     * @param content 文字或语音内容
     */
    public static void setAutoReplyContent(int type, String content) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("content", content);
        Statistics.userBehavior(SendPoint.page_setautoreply_alert_set_confirm, params);
    }

    // --------------------------缘分吧AB测试版新增统计--------------------------------

    /**
     * 开通VIP弹窗->确认支付
     *
     * @param product_id //VIPId
     * @param price      //本次充值金额(人民币)
     * @param pay_type   //支付类型/wechat/alipay
     */
    public static void abTestVipPay(int product_id, float price, int pay_type) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("product_id", product_id);
        params.put("price", price);
        params.put("pay_type", StatisticsMessage.getPayType(pay_type));
        params.put("env", ModuleMgr.getCenterMgr().getMyInfo().isB() ? 1 : 0);
        Statistics.userBehavior(SendPoint.alert_vip_pay, params);
    }
}
