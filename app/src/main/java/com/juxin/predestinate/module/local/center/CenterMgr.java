package com.juxin.predestinate.module.local.center;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.ModuleBase;
import com.juxin.library.observe.Msg;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.library.utils.EncryptUtil;
import com.juxin.library.utils.FileUtil;
import com.juxin.library.utils.StringUtils;
import com.juxin.library.utils.TypeConvertUtil;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.settting.Setting;
import com.juxin.predestinate.module.local.chat.OfflineMsg;
import com.juxin.predestinate.module.local.login.LoginMgr;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.InfoConfig;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.logic.request.RequestParam;
import com.juxin.predestinate.module.logic.socket.IMProxy;
import com.juxin.predestinate.module.logic.socket.TCPConstant;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.mail.unlock.UnlockComm;
import com.juxin.predestinate.ui.setting.UserModifyPwdAct;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.networkbench.agent.impl.NBSAppAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人中心管理类
 */
public class CenterMgr implements ModuleBase, PObserver {

    public static final String INFO_SAVE_KEY = "INFO_SAVE_KEY"; // 本地化个人资料key
    private static final String SETTING_SAVE_KEY = "SETTING_SAVE_KEY"; // 本地化设置key
    private volatile UserDetail userDetail = null;
    private Setting setting = null;
    private OfflineMsg offlineMsg = null;

    @Override
    public void init() {
        MsgMgr.getInstance().attach(this);
        if (offlineMsg == null) {
            offlineMsg = new OfflineMsg();
            offlineMsg.init();
        }
    }

    @Override
    public void release() {
        MsgMgr.getInstance().detach(this);
        if (offlineMsg != null) {
            offlineMsg.release();
            offlineMsg = null;
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        switch (key) {
            case MsgType.MT_App_Login:
                PLogger.d("---MT_App_Login--->" + value);
                if ((Boolean) value) {
                    NBSAppAgent.setUserCrashMessage("uid", String.valueOf(ModuleMgr.getLoginMgr().getUid()));
                    IMProxy.getInstance().connect();//登录成功之后连接socket
                    reqMyInfo();// 请求个人资料
//                    reqSetting();//请求设置信息
                } else {
                    IMProxy.getInstance().logout();//退出登录的时候退出socket
                    clearUserInfo();
                }
                break;

            case MsgType.MT_App_CoreService://socket已连接，登录
                IMProxy.getInstance().login();
                break;

            case MsgType.MT_App_IMStatus://socket登录成功后取离线消息
                HashMap<String, Object> data = (HashMap<String, Object>) value;
                int type = (int) data.get("type");
                if (type == TCPConstant.SOCKET_STATUS_Login_Success) {
                    PLogger.d("---offlineMessage--->SOCKET_STATUS_Login_Success");
                    if (offlineMsg != null) {
                        offlineMsg.getOfflineMsg();
                    }
                }
                break;

            case MsgType.MT_Update_MyInfo:
                reqMyInfo();
                break;
            case MsgType.MESSAGE_UNLOCK://周杰
                Msg msg = (Msg) value;
                if("1".equals(msg.getKey())){//解锁类型 1 分享解锁 2.钻石消费解锁 3.爵位解锁
                    MsgMgr.getInstance().delay(new Runnable() {
                        @Override
                        public void run() {
                            if(!getMyInfo().isMan()){
                                return;
                            }
                            if (UnlockComm.getJueweiOk()) {
                                return;
                            }
                            if((ModuleMgr.getCenterMgr().getMyInfo().isB() && ModuleMgr.getCenterMgr().getMyInfo().isVip())) {
                                return;
                            }
                            UIShow.showShareSuccessDlg((FragmentActivity) App.activity);
                        }
                    }, 500);
                }
                break;
            case MsgType.MT_Update_Diamond://钻石更新
                int diamondCost = (int) value;
                UnlockComm.showDiamondMessageOKDlg((FragmentActivity) App.activity);
                break;
            case MsgType.MT_PUNCH_REDENVELOPE://打卡红包通知UI
                ModuleMgr.getCommonMgr().setShowredCard();
                break;
            default:
                break;
        }
    }

    /**
     * 清除用户信息
     */
    public void clearUserInfo() {
        userDetail = null;
        setMyInfo("");
        putSettingPsp(null);
    }

    /**
     * 请求手机验证码
     *
     * @param mobile
     * @param complete
     */
    public void reqVerifyCodeEx(String mobile, RequestComplete complete) {
        HashMap<String, Object> getparam = new HashMap<>();
        getparam.put("cellPhone", mobile);
        getparam.put("sign", App.context.getResources().getString(R.string.app_name));
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqReqVerifyCode, getparam, complete);
    }


    /**
     * 手机认证
     *
     * @param mobile   手机号
     * @param code     验证码
     * @param complete
     */
    public void mobileAuthEx(String mobile, String code, RequestComplete complete) {
        HashMap<String, Object> getParams = new HashMap<>();
        getParams.put("cellPhone", mobile);
        getParams.put("verifyCode", code);
        ModuleMgr.getHttpMgr().request(new RequestParam().setUrlParam(UrlParam.mobileAuth).setGetParam(getParams)
                .setNeedEncrypt(true).setRequestCallback(complete));
    }

    /**
     * 修改密码
     */
    public void modifyPassword(final Context context, String oldpwd, final String newpwd) {
        HashMap<String, Object> postParam = new HashMap<>();
        postParam.put("oldpassword", oldpwd);
        postParam.put("newpassword", newpwd);
        ModuleMgr.getHttpMgr().request(new RequestParam().setUrlParam(UrlParam.modifyPassword).setPostParam(postParam)
                .setJsonRequest(false).setRequestCallback(new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        if (response.isOk()) {
                            PToast.showShort(context.getResources().getString(R.string.toast_update_ok));
                            LoginMgr loginMgr = ModuleMgr.getLoginMgr();
                            long uid = loginMgr.getUid();
                            loginMgr.putUserInfo(uid, newpwd);
                            ((UserModifyPwdAct) context).exitApp();
                            return;
                        }
                        PToast.showShort(response.getMsg());
                    }
                })
        );
    }

    /**
     * 意见反馈
     *
     * @param contract 联系方式
     * @param content  意见
     */
    public void feedBack(final FragmentActivity activity, String contract, String content) {
        HashMap<String, Object> postParam = new HashMap<>();
        postParam.put("user_client_type", Constant.PLATFORM_TYPE);
        postParam.put("contract", contract);
        postParam.put("content", content);
        ModuleMgr.getHttpMgr().request(new RequestParam().setUrlParam(UrlParam.feedBack).setPostParam(postParam)
                .setJsonRequest(false).setRequestCallback(new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.getResponseString());
                            if (!"true".equals(jsonObject.optString("item"))) {
                                PToast.showShort(activity.getResources().getString(R.string.toast_commit_suggest_error));
                            } else {
                                PToast.showLong(activity.getResources().getString(R.string.toast_commit_suggest_ok));
                                activity.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }));
    }

    /**
     * 昵称：个人资料限制昵称最大字数
     * 过滤空格 禁止注册的时候 昵称带有空格
     *
     * @param limit : 限制字数个数
     */
    public void inputFilterSpace(final EditText edit, final int limit) {
        edit.addTextChangedListener(new TextWatcher() {
            int cou = 0;
            int selectionEnd = 0;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cou = before + count;
                String editable = edit.getText().toString();
                String str = StringUtils.noSpace(editable);
                if (!editable.equals(str)) {
                    edit.setText(str);
                }
                edit.setSelection(edit.length());
                cou = edit.length();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (cou > limit) {
                    selectionEnd = edit.getSelectionEnd();
                    s.delete(limit, selectionEnd);
                    if (!TextUtils.isEmpty(s)) {
                        edit.setText(s.toString());
                    }
                }
            }
        });
    }

    /**
     * 获取我的个人资料
     */
    public UserDetail getMyInfo() {
        if (userDetail == null) {
            synchronized (UserDetail.class) {
                if (userDetail == null) {
                    userDetail = new UserDetail();
                    String result = PSP.getInstance().getString(INFO_SAVE_KEY, "");
                    if (!TextUtils.isEmpty(result)) {
                        userDetail.parseJson(result);
                    }
                }
            }
        }
        return userDetail;
    }

    public void reqMyInfo() {
        reqMyInfo(null);
    }

    /**
     * 获取自己的个人资料
     *
     * @updateAuthor Mr.Huang
     * @updateDate 2017-07-18
     * 对调下顺序
     * 先处理数据在回调接口
     */
    public void reqMyInfo(final RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqMyInfo, null, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    userDetail = (UserDetail) response.getBaseData();
                    setMyInfo(response.getResponseString());// 保存到SP
                    MsgMgr.getInstance().sendMsg(MsgType.MT_MyInfo_Change, null);
                }
                if (complete != null) {
                    complete.onRequestComplete(response);
                }
            }
        });
    }

    /**
     * 修改个人信息
     */
    public void updateMyInfo(final HashMap<String, Object> params, final RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqPostNoCacheNoEncHttp(UrlParam.updateMyInfo, params, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (complete != null) {
                    complete.onRequestComplete(response);
                }
                if (response.isOk()) {
                    reqMyInfo();
                }
                PToast.showShort(response.getMsg());
            }
        });
    }

    /**
     * 上传头像
     *
     * @param url      图片本地地址
     * @param complete 上传完成回调
     */
    public void uploadAvatar(final String url, final RequestComplete complete) {
        if (FileUtil.isExist(url)) {
            Map<String, File> fileParams = new HashMap<>();
            fileParams.put("avatar", new File(url));

            long uid = ModuleMgr.getLoginMgr().getUserList().get(0).getUid();
            String password = ModuleMgr.getLoginMgr().getUserList().get(0).getPw().trim();

            Map<String, Object> postParams = new HashMap<>();
            postParams.put("uid", uid);
            postParams.put("code", EncryptUtil.md5(uid + EncryptUtil.md5(password)));

            ModuleMgr.getHttpMgr().uploadFile(UrlParam.uploadAvatar, postParams, fileParams, new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    if (complete != null) {
                        complete.onRequestComplete(response);
                    }
                    FileUtil.deleteFile(url);
                }
            });

        } else {
            LoadingDialog.closeLoadingDialog();
            PToast.showShort("图片地址无效");
        }
    }

    /**
     * 上传相册
     */
    public void uploadPhoto(final String url, final RequestComplete complete) {
        if (FileUtil.isExist(url)) {
            Map<String, File> fileParams = new HashMap<>();
            fileParams.put("userfile", new File(url));

            long uid = ModuleMgr.getLoginMgr().getUserList().get(0).getUid();
            String password = ModuleMgr.getLoginMgr().getUserList().get(0).getPw().trim();

            Map<String, Object> postParams = new HashMap<>();
            postParams.put("uid", uid);
            postParams.put("code", EncryptUtil.md5(uid + EncryptUtil.md5(password)));

            ModuleMgr.getHttpMgr().uploadFile(UrlParam.uploadPhoto, postParams, fileParams, complete);

        } else {
            LoadingDialog.closeLoadingDialog();
            PToast.showShort("图片地址无效");
        }
    }

    /**
     * 删除相片
     */
    public void deletePhoto(int albumId, RequestComplete complete) {
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("id", albumId);

        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.deletePhoto, getParams, complete);
    }

    // ------------------------- 他人 ----------------------

    /**
     * 获取他人用户详细信息
     */
    public void reqOtherInfo(final long uid, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("hisuid", uid);

        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqOtherInfo, postParams, complete);
    }

    /**
     * 获取他人用户详细信息里
     * 女用户是否开启座驾
     */
    public void reqBeautyCar(final long uid, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("uid", uid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqBeautyCar, postParams, complete);
    }

    /**
     * 批量获取用户简略信息
     */
    public void reqUserSimpleList(final long[] uidList, RequestComplete complete) {
        HashMap<String, Object> post_param = new HashMap<>();
        post_param.put("uidlist", uidList);
        ModuleMgr.getHttpMgr().reqPostAndCacheHttp(UrlParam.reqUserSimpleList, post_param, complete);
    }

    /**
     * 获取他人音视频开关配置
     */
    public void reqVideoChatConfig(long uid, RequestComplete complete) {
        HashMap<String, Object> post_param = new HashMap<>();
        post_param.put("uid", uid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqVideoChatConfig, post_param, complete);
    }

    /**
     * 获取设置的是否接受他人音视频配置
     */
    public void reqGetOpposingVideoSetting(long uid, RequestComplete complete) {
        HashMap<String, Object> post_param = new HashMap<>();
        post_param.put("hisuid", uid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqGetOpposingVideoSetting, post_param, complete);
    }

    /**
     * 设置是否接受他人音视频
     *
     * @param acceptvideo 接受对方视频 0：不接受  1：接受
     * @param acceptvoice 接受对方语音 0：不接受  1：接受
     */
    public void reqSetOpposingVideoSetting(long uid, int acceptvideo, int acceptvoice, RequestComplete complete) {
        HashMap<String, Object> post_param = new HashMap<>();
        post_param.put("hisuid", uid);
        post_param.put("acceptvideo", acceptvideo);
        post_param.put("acceptvoice", acceptvoice);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqSetOpposingVideoSetting, post_param, complete);
    }

    /**
     * 设置他人备注名
     *
     * @param toUid      对方UID
     * @param remarkName 备注名称
     */
    public void reqSetRemarkName(long toUid, String remarkName, RequestComplete complete) {
        HashMap<String, Object> post_param = new HashMap<>();
        post_param.put("hisuid", toUid);
        post_param.put("remarkname", remarkName);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqSetRemarkName, post_param, complete);
    }

    /**
     * 用户是否处于黑名单
     */
    public void reqIsBlack(long uid, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("tuid", uid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqIsBlack, postParams, complete);
    }

    /**
     * 拉黑用户
     */
    public void reqAddBlack(long uid, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("tuid", uid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqAddBlack, postParams, complete);
    }

    /**
     * 移除拉黑用户
     */
    public void reqRemoveBlack(long uid, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("tuid", uid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqRemoveBlack, postParams, complete);
    }

    /**
     * 私密视频： 增加人气值
     */
    public void reqSetPopnum(long uid, RequestComplete complete) {
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("uid", uid);
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.reqSetPopnum, getParams, complete);
    }

    /**
     * 私密视频： 解锁视频
     *
     * @param uid 用户id
     * @param vid 视频id
     */
    public void reqUnlockVideo(long uid, long vid, RequestComplete complete) {
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("uid", uid);
        getParams.put("vid", vid);
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.reqUnlockVideo, getParams, complete);
    }

    /**
     * 私密视频： 设置视频观看次数
     */
    public void reqSetViewTime(long uid, long vid, RequestComplete complete) {
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("uid", uid);
        getParams.put("vid", vid);
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.reqSetViewTime, getParams, complete);
    }


    /**
     * 保存个人信息Json串到SP
     */
    private void setMyInfo(String resultStr) {
        PSP.getInstance().put(INFO_SAVE_KEY, resultStr);
    }

    /**
     * 图片上传时截取的字符串
     * <p>
     * 短存储： oss
     * 长存储： jxfile
     */
    public String getInterceptUrl(String picUrl) {
        if (TextUtils.isEmpty(picUrl)) {
            return "";
        }
        String tag = Constant.STR_SHORT_TAG;
        if (picUrl.contains(Constant.STR_LONG_TAG)) {
            tag = Constant.STR_LONG_TAG;
        }
        return StringUtils.getAfterWithFlag(picUrl, tag);
    }

    /**
     * 是否是机器人
     *
     * @param kf_id 不为0就是机器人
     * @return true 是机器人，false不是机器人
     */
    public boolean isRobot(int kf_id) {
        return kf_id != 0;
    }

    /**
     * 是否是包月vip用户
     *
     * @param group 1 普通用户  2,3包月用户
     * @return true 是vip
     */
    public boolean isVip(int group) {
        return group == 2 || group == 3;
    }

    /**
     * 是否可以打招呼
     * 头像未审核通过不准打招呼
     *
     * @param context
     * @return true可以打招呼 false不可以打招呼
     */
    public boolean isCanSayHi(Context context) {
        if (getMyInfo().getAvatar_status() == CenterConstant.USER_AVATAR_NO_PASS) {
            PToast.showShort(context.getString(R.string.say_hi_avatar_fail));
            return false;
        } else {
            return true;
        }
    }


    /**
     * 是否可以进行群打招呼
     *
     * @param context
     * @return
     */
    public boolean isCanGroupSayHi(Context context) {
        //头像状态是否是未通过审核状态
        if (getMyInfo().getAvatar_status() == CenterConstant.USER_AVATAR_NO_PASS) {
            PToast.showShort(context.getString(R.string.say_hi_avatar_fail));
            return false;
        }
        PLogger.d("isCanGroupSayHi------ key = " + getGroupSayHiDayKey() + " bool = " + ModuleMgr.getCommonMgr().checkDate(getGroupSayHiDayKey()));
        //判断是否达到第二天
        if (ModuleMgr.getCommonMgr().checkDate(getGroupSayHiDayKey())) {
            //判断群打招呼次数
            int num = PSP.getInstance().getInt(getGroupSayHiNumKey(), 0);
            PLogger.d("isCanGroupSayHi ----- num == " + num);
            if (num > 2) { //如果达到第三次重置 是否达到第二天的状态 并清除打招呼次数
                ModuleMgr.getCommonMgr().saveDateState(getGroupSayHiDayKey());
                PSP.getInstance().put(getGroupSayHiNumKey(), 0);
                PToast.showShort(context.getString(R.string.say_hi_group_num_state));
                return false;
            } else { //如果没有达到第三次 则更新群打招呼次数
                PSP.getInstance().put(getGroupSayHiNumKey(), num + 1);
                return true;
            }
        } else { //群打招呼用完了 第二天的状态已经被重置 直接返回false
            PToast.showShort(context.getString(R.string.say_hi_group_num_state));
            return false;
        }
    }

    /**
     * 群打招呼是否达到第二天状态key
     *
     * @return
     */
    public String getGroupSayHiDayKey() {
        return "GroupSayHiDayKey" + getMyInfo().getUid();
    }

    /**
     * 群打招呼次数key
     *
     * @return
     */
    private String getGroupSayHiNumKey() {
        return "GroupSayHiNumKey" + getMyInfo().getUid();
    }



    /*设置信息*/

    /**
     * 获取系统设置信息
     */
    public void reqSetting() {
        ModuleMgr.getHttpMgr().reqGetAndCacheHttp(UrlParam.getSetting, null, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    setting = (Setting) response.getBaseData();
                    try {
                        JSONObject json = new JSONObject(response.getResponseString());
                        putSettingPsp(json.optString("res"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 更新系统设置信息
     */
    public void updateSetting(HashMap<String, Object> post_param) {
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.updateSetting, post_param, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    PToast.showShort(App.context.getResources().getString(R.string.toast_update_ok));
                } else {
                    PToast.showShort(response.getMsg());
                }
            }
        });
    }

    /**
     * 获取我的设置信息
     */
    public Setting getSetting() {
        if (setting == null) {
            setting = new Setting();
            String result = PSP.getInstance().getString(SETTING_SAVE_KEY, "");
            if (!TextUtils.isEmpty(result)) {
                setting.parseJson(result);
            }
        }
        return setting;
    }

    /**
     * 保存设置信息Json串到SP
     */
    private void putSettingPsp(String resultStr) {
        PSP.getInstance().put(SETTING_SAVE_KEY, resultStr);
    }

    // -------------------------充值页面逻辑 start---------------------------

    private static final String CHARGE_NUM_COIN = "CHARGE_NUM_COIN"; //Y币充值人数随机数存储key
    private static final String CHARGE_NUM_VIP = "CHARGE_NUM_VIP";   //VIP币充值人数随机数存储key

    /**
     * 获取进入H5充值页面需要传递的参数map
     *
     * @param type 1 表示购买y币 2 表示购买vip
     * @return 拼接完成的参数map
     */
    public HashMap<String, Object> getChargeH5Params(int type) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("ycoin_person", getActiveUserNum(CHARGE_NUM_COIN));
        params.put("vip_person", getActiveUserNum(CHARGE_NUM_VIP));
        params.put("ycoin_surplus", getMyInfo().getYcoin());
        params.put("type", type);
        return params;
    }

    /**
     * 获取充值人数随机数
     *
     * @param numKey Y币/VIP key：FinalKey#CHARGE_NUM_COIN/FinalKey#CHARGE_NUM_VIP
     * @return 根据本地初始化存储的数据累加的随机数
     */
    private int getActiveUserNum(String numKey) {
        int random = PSP.getInstance().getInt(numKey, (int) ((Math.random() * 9 + 1) * 100000))
                + (int) (Math.random() * 9 + 1);
        PSP.getInstance().put(numKey, random);
        return random;
    }
    // -------------------------充值页面逻辑 end---------------------------

    /**
     * 根据日期获取星座
     */
    private List<String> starList;
    private List<String> matchTable; // 配表

    public String getStar(int month, int day) {
        if (null == starList) {
            starList = InfoConfig.getInstance().getConstellation().getShow();
        }
        if (null == matchTable) {
            matchTable = InfoConfig.getInstance().getConstellation().getSubmit();
        }
        return day < TypeConvertUtil.toInt(matchTable.get(month - 1)) ? starList.get(month - 1) : starList.get(month);
    }

    private static boolean hasCheckAvatar = false; // 是否已经进过上传头像页

    public boolean getHasCheckAvatar() {
        return hasCheckAvatar;
    }

    /**
     * 上传头像页面
     */
    public boolean uploadAvatarAct(Context context) {
        hasCheckAvatar = true;
        boolean upload = !checkUserIsUploadAvatar();

        if (upload) {
            int avatar_status = ModuleMgr.getCenterMgr().getMyInfo().getAvatar_status();
            if (avatar_status == CenterConstant.USER_AVATAR_NO_PASS) {
                UIShow.showUploadAvatarAct(context, true);//更新
            }
            if (avatar_status == CenterConstant.USER_AVATAR_NO_UPLOAD) {
                UIShow.showUploadAvatarAct(context, false);
            }
        }

        return upload;
    }

    /**
     * 判断了是否上传了用户头像
     */
    private boolean checkUserIsUploadAvatar() {
        int avatar_status = getMyInfo().getAvatar_status();
        // 判断用户头像是否正常(未上传／未通过时跳转上传头像)
        return !(avatar_status == CenterConstant.USER_AVATAR_NO_PASS || avatar_status == CenterConstant.USER_AVATAR_NO_UPLOAD);
    }
}