package com.juxin.predestinate.module.local.login;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.ModuleBase;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.utils.EncryptUtil;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.start.LoginResult;
import com.juxin.predestinate.bean.start.RegResult;
import com.juxin.predestinate.bean.start.UP;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.FinalKey;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.logic.request.RequestParam;
import com.juxin.predestinate.module.util.BaseUtil;
import com.juxin.predestinate.module.util.JsonUtil;
import com.juxin.predestinate.module.util.NotificationsUtils;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.networkbench.agent.impl.NBSAppAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 登录逻辑处理
 * Created by ZRP on 2016/9/19.
 */
public class LoginMgr implements ModuleBase {

    private final static String LOGINMGR_UID = "LOGINMGR_UID";          // 保存当前登录用户账号信息 uid, pw
    private final static String LOGINMGR_COOKIE = "LOGINMGR_COOKIE";    // 保存当前登录用户cookie
    private final static String LOGINMGR_AUTH = "LOGINMGR_AUTH";        // 保存当前登录用户的密码md5

    @Override
    public void init() {
    }

    @Override
    public void release() {
    }

    /**
     * @return 判断Cookie是否存在
     */
    public boolean checkAuthIsExist() {
        return !TextUtils.isEmpty(getCookie());
    }

    /**
     * @return 获取cookie
     */
    public String getCookie() {
        return PSP.getInstance().getString(LOGINMGR_COOKIE, "");
    }

    // ************************************ 登录用户信息列表存储 *****************************

    private List<UP> userList;  // 维护登录过的用户数据列表，防止多次进行SP读写操作

    /**
     * 清空本地Cookie
     */
    public void clearCookie() {
        setCookie("");
    }

    /**
     * 保存cookie
     */
    public void setCookie(String cookie) {
        PLogger.d("yao=" + cookie);
        PSP.getInstance().put(LOGINMGR_COOKIE, cookie);
    }

    /**
     * 获取用户列表
     */
    public List<UP> getUserList() {
        if (userList == null) {
            userList = getUserJson();
        }
        return userList;
    }

    /**
     * @return 获取已登录过的所有用户ID和密码
     */
    private List<UP> getUserJson() {
        List<UP> upList = new ArrayList<>();
        try {
            String key = PSP.getInstance().getString(FinalKey.LOGIN_USER_KEY, null);
            if (!TextUtils.isEmpty(key)) {
                JSONObject jsonObject = new JSONObject(key);
                JSONArray jsonArray = jsonObject.optJSONArray("user");
                UP up;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.optJSONObject(i);
                    up = new UP();
                    up.setUid(Long.valueOf(EncryptUtil.decryptDES(object.optString("sUid"), FinalKey.UP_DES_KEY)));
                    up.setPw(EncryptUtil.decryptDES(object.optString("sPw"), FinalKey.UP_DES_KEY));
                    up.setTm(object.optLong("tm"));
                    upList.add(up);
                }
            }
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
        return upList;
    }

    /**
     * 登录后存储账号密码到list配置
     */
    public void putUserInfo(long uid, String pwd) {
        List<UP> sourceList = getUserJson();
        List<UP> reSortList = new ArrayList<>();
        boolean uidRepeat = false;
        UP up;

        // 将以存储的用户数据复制到另一个list中，并保证当前登录用户处于列表首位
        for (int i = 0; i < sourceList.size(); i++) {
            up = sourceList.get(i);
            if (up != null && up.getUid() != 0) {
                if (up.getUid() == uid) {//如果已登录过该uid，更新并添加到列表首位
                    up.setPw(pwd);
                    up.setTm(TimeUtil.getTimeInMillis());
                    reSortList.add(0, up);
                    uidRepeat = true;
                } else {
                    reSortList.add(up);
                }
            }
        }
        if (!uidRepeat) {//用户为首次登录
            reSortList.add(0, new UP(uid, pwd, TimeUtil.getTimeInMillis()));
        }
        putUserJson(reSortList);
    }

    /**
     * 登录后存储账号密码list到本地Json
     *
     * @return true：存储成功
     */
    public boolean putUserJson(List<UP> upList) {
        userList = null;  // 更新本地userList
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            JSONObject tmpJson;
            for (UP tmp : upList) {
                tmpJson = new JSONObject();
                tmpJson.put("sUid", EncryptUtil.encryptDES(String.valueOf(tmp.getUid()), FinalKey.UP_DES_KEY));
                tmpJson.put("sPw", EncryptUtil.encryptDES(String.valueOf(tmp.getPw()), FinalKey.UP_DES_KEY));
                tmpJson.put("tm", tmp.getTm());
                jsonArray.put(tmpJson);
            }
            jsonObject.put("user", jsonArray);
            PSP.getInstance().put(FinalKey.LOGIN_USER_KEY, jsonObject.toString());
            return true;
        } catch (Exception e) {
            PLogger.printThrowable(e);
            return false;
        }
    }

    /**
     * 移除登录用户
     *
     * @return 被移除的用户
     */
    public UP removeLoginUser(int position) {
        UP user = getUserList().remove(position);
        if (getUserList().size() >= 0) {
            putUserJson(getUserList());
        }
        return user;
    }

    /**
     * @return 获取cookie + vercode
     */
    public String getCookieVerCode() {
        String cookie = getCookie();
        if (!TextUtils.isEmpty(cookie)) {
            return cookie + ";" + "v=" + ModuleMgr.getAppMgr().getVerCode();
        } else {
            clearCookie();
            return "v=" + ModuleMgr.getAppMgr().getVerCode();
        }
    }

    /**
     * 保存登录信息
     *
     * @param uid
     * @param password
     * @param isUserLogin
     */
    private void putAllLoginInfo(long uid, String password, boolean isUserLogin) {
        setUid(uid + "");
        PSP.getInstance().put(LOGINMGR_AUTH, EncryptUtil.md5(password));
        putUserInfo(uid, password); //保存登录账户到list配置
        setLoginInfo(uid, isUserLogin);  //设置登录状态
    }

    /**
     * 设置登录信息，并发送登录信息。
     *
     * @param uid
     * @param isUserLogin 　进行登录请求或处于登录状态
     * @return
     */
    private boolean setLoginInfo(long uid, boolean isUserLogin) {
        App.cookie = getCookie();
        changeIsLogin(isUserLogin && (TextUtils.isEmpty(App.cookie) || uid == 0) ? 0 : uid);
        MsgMgr.getInstance().sendMsg(MsgType.MT_App_Login, App.isLogin);
        return App.isLogin;
    }

    private void changeIsLogin(long uid) {
        App.uid = uid;
        App.isLogin = uid != 0;
    }

    /**
     * 退出登录，并清空用户登录信息
     */
    public void logout() {
        NotificationsUtils.cancelAll();//如果还有通知栏提示，在退出帐号的时候全部清掉
        setUid("");//清空uid
        clearCookie();//在setLoginInfo方法之前执行
        setLoginInfo(0, true);
    }

    /**
     * 保存单独的 uid
     *
     * @param uid
     */
    public void setUid(String uid) {
        NBSAppAgent.setUserIdentifier(uid);
        PSP.getInstance().put(LOGINMGR_UID, uid);
    }

    public long getUid() {
        String uid = PSP.getInstance().getString(LOGINMGR_UID, "");
        if (!TextUtils.isEmpty(uid)) {
            return BaseUtil.getLong(uid, 0);
        }
        return 0L;
    }

    /**
     * @return 获取保存的密码md5值
     */
    public String getAuth() {
        return PSP.getInstance().getString(LOGINMGR_AUTH, "");
    }

    /**
     * 初始化登陆信息
     */
    public void initCookie() {
        long uid = getUid();
        if (uid == 0) {
            clearCookie();
            return;
        }
        setLoginInfo(uid, true);
    }

    // ---------------------------登录注册相关接口---------------------------------

    /**
     * 账号注册
     */
    public void onRegister(final Activity context, String nickname, int age, final int gender) {
        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("flag", Constant.REG_FLAG);
        postParams.put("user_client_type", Constant.PLATFORM_TYPE);
        postParams.put("s_uid", ModuleMgr.getAppMgr().getMainChannelID());
        postParams.put("s_sid", ModuleMgr.getAppMgr().getSubChannelID());
        postParams.put("ie", ModuleMgr.getAppMgr().getIMEI());
        postParams.put("is", ModuleMgr.getAppMgr().getIMSI());
        postParams.put("mc", ModuleMgr.getAppMgr().getMAC());
        postParams.put("simoperator", ModuleMgr.getAppMgr().getSimOperator());
        postParams.put("ms", Constant.MS_TYPE);
        postParams.put("ver", Constant.SUB_VERSION);
        postParams.put("app_key", EncryptUtil.sha1(ModuleMgr.getAppMgr().getSignature()));
        postParams.put("pkgname", ModuleMgr.getAppMgr().getPackageName());
        postParams.put("age", age);
        postParams.put("gender", gender);
        postParams.put("nickname", nickname);
        postParams.put("reg_source", PromoCode.getCode(context)); //新增加注册来源参数 20170913 by IQQ
        postParams.put("r", new Random().nextLong());
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqRegister, postParams, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                LoadingDialog.closeLoadingDialog(300);
                JSONObject jsonObject = JsonUtil.getJsonObject(response.getResponseString());
                if (!"success".equals(jsonObject.optString("respCode"))) {
                    if (!NetworkUtils.isConnected(context)) {
                        PToast.showShort(context.getString(R.string.net_error_retry));
                    } else {
                        PToast.showShort(TextUtils.isEmpty(jsonObject.optString("msg")) ? context.getString(R.string.toast_reg_error) : jsonObject.optString("msg"));
                    }
                    return;
                }
                RegResult result = new RegResult();
                result.parseJson(jsonObject.toString());
                putAllLoginInfo(Long.parseLong(result.getUsername()), result.getPassword(), false);
                ModuleMgr.getCenterMgr().getMyInfo().setGender(gender);
                UIShow.showUserInfoCompleteAct(context, gender);
            }
        });
    }

    /**
     * 登录
     */
    public void onLogin(final Activity context, final long uid, final String pwd) {
        HashMap<String, Object> userAccount = new HashMap<>();
        userAccount.put("username", uid);
        userAccount.put("password", pwd);
        userAccount.put("platform", "android");
        userAccount.put("ver", Constant.SUB_VERSION);
        userAccount.put("ms", Constant.MS_TYPE);
        userAccount.put("app_key", EncryptUtil.sha1(ModuleMgr.getAppMgr().getSignature()));
        userAccount.put("pkgname", ModuleMgr.getAppMgr().getPackageName());
        userAccount.put("ie", ModuleMgr.getAppMgr().getIMEI());
        userAccount.put("is", ModuleMgr.getAppMgr().getIMSI());
        LoadingDialog.show((FragmentActivity) context, context.getString(R.string.tip_loading_login));
        ModuleMgr.getHttpMgr().reqPost(UrlParam.reqNewLogin, null, null, userAccount,
                RequestParam.CacheType.CT_Cache_No, true, true, new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        LoadingDialog.closeLoadingDialog(500);
                        if (!response.isOk()) {
                            PToast.showShort(context.getString(R.string.toast_login_iserror));
                            return;
                        }
                        // 临时资料设置
                        LoginResult result = (LoginResult) response.getBaseData();
                        ModuleMgr.getLoginMgr().setCookie("auth=" + result.getToken());
                        if (result.getLoginstatus() != 0 && result.getFailCode() != 2) {
                            PToast.showShort(result.getMsg());
                            clearCookie();
                            return;
                        }
                        if (result.getLoginstatus() != 0 && result.getFailCode() == 2) {
                            UIShow.showBottomBannedDlg(context, false, result.getExpire());
                            clearCookie();
                            return;
                        }
                        result.setUserInfo();
                        putAllLoginInfo(result.getUid(), pwd, true);// Cookie 在http响应头中返回
                        if (!result.isValidDetailInfo()) {
                            PToast.showLong(context.getString(R.string.toast_userdetail_isnull));
                            UIShow.showUserInfoCompleteAct(context, result.getGender());
                            return;
                        }

                        //@updateAuthor Mr.Huang
                        //@updateDate 2017-08-15
                        //在成功登陆后主动获取一次个人信息，因为登陆后需要这个
                        ModuleMgr.getCenterMgr().reqMyInfo(new RequestComplete() {
                            @Override
                            public void onRequestComplete(HttpResponse response) {
                                UIShow.showMainClearTask(context);
                                Statistics.startUp();//登录成功之后再次发送startUp统计
                            }
                        });
                    }
                });
    }

    /**
     * 重置密码获取验证码
     *
     * @param phone
     * @param complete
     */
    public void reqForgotsms(String phone, RequestComplete complete) {
        HashMap<String, Object> post_param = new HashMap<>();
        post_param.put("phone", phone);
        post_param.put("sign", App.context.getString(R.string.app_name));
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqForgotsms, post_param, complete);
    }

    /**
     * 重置密码
     *
     * @param phone    手机号
     * @param code     验证码
     * @param pwd      密码
     * @param complete
     */
    public void forgotPassword(String phone, String code, String pwd, RequestComplete complete) {
        HashMap<String, Object> post_param = new HashMap<>();
        post_param.put("phone", phone);
        post_param.put("sign", App.context.getString(R.string.app_name));
        post_param.put("code", code);
        post_param.put("password", pwd);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.forgotPassword, post_param, complete);
    }
}
