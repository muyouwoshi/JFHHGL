package com.juxin.predestinate.module.local.common;

import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.juxin.library.dir.DirType;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.ModuleBase;
import com.juxin.library.utils.EncryptUtil;
import com.juxin.library.utils.FileUtil;
import com.juxin.predestinate.BuildConfig;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.update.AppUpdate;
import com.juxin.predestinate.bean.center.update.UpgradeSource;
import com.juxin.predestinate.bean.center.user.detail.MyEarnInfo;
import com.juxin.predestinate.bean.config.CommonConfig;
import com.juxin.predestinate.bean.config.VideoVerifyBean;
import com.juxin.predestinate.bean.my.ApplyLiveStatusBean;
import com.juxin.predestinate.bean.my.BagList;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.bean.my.IdCardVerifyStatusInfo;
import com.juxin.predestinate.bean.my.OffMsgInfo;
import com.juxin.predestinate.bean.settting.ContactBean;
import com.juxin.predestinate.bean.settting.ShareAccounts;
import com.juxin.predestinate.bean.start.UP;
import com.juxin.predestinate.module.local.location.LocationMgr;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.FinalKey;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HTCallBack;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.logic.request.RequestParam;
import com.juxin.predestinate.module.util.CommonUtil;
import com.juxin.predestinate.module.util.JsonUtil;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.my.AttentionUtil;
import com.juxin.predestinate.module.util.my.GiftHelper;
import com.juxin.predestinate.ui.discover.MyFriendsPanel;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.tangzi.sharelibrary.utils.AppInfo;
import com.tangzi.sharelibrary.utils.ShareUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用，新版本
 * Created by Xy on 17/3/22.
 */
public class CommonMgr implements ModuleBase {

    private CommonConfig commonConfig;//服务器静态配置
    private GiftsList giftLists;//礼物信息
    private BagList bagList;
    private VideoVerifyBean videoVerify;//视频聊天配置
    private IdCardVerifyStatusInfo mIdCardVerifyStatusInfo;
    private ApplyLiveStatusBean applyLiveStatusBean;
    private ContactBean contactBean;//客服信息
    private MyEarnInfo myEarnInfo;

    @Override
    public void init() {
        Statistics.loopLocation();
        setMMoneyDlgShow(false);
    }

    @Override
    public void release() {
    }

    public ContactBean getContactBean() {
        return contactBean;
    }

    public void setContactBean(ContactBean contactBean) {
        this.contactBean = contactBean;
    }

    /**
     * 拼接用户uid参与的key值
     *
     * @param val 定义的字段值
     * @return 获取用户uid参与的key值
     */
    public String getPrivateKey(String val) {
        return (TextUtils.isEmpty(val) ? "uid" : val) + ModuleMgr.getCenterMgr().getMyInfo().getUid();
    }

    /**
     * 游戏交互-请求转发。接口回调data为String类型。[注意]：加密请求添加了新的header，若服务器无法处理，谨慎调用该方法
     *
     * @param requestType   请求方式（GET/POST）
     * @param isEnc         是否为加密请求
     * @param requestUrl    请求完整地址，不会进行再次拼接
     * @param isJsonRequest 是否为json格式请求
     * @param requestObject 请求体参数
     * @param complete      请求回调
     */
    public void CMDRequest(String requestType, boolean isEnc, String requestUrl, boolean isJsonRequest, Map<String, Object> requestObject, RequestComplete complete) {
        RequestParam requestParam = new RequestParam();
        if ("POST".equalsIgnoreCase(requestType)) {
            requestParam.setRequestType(RequestParam.RequestType.POST);
            if (requestObject != null) {
                requestParam.setPostParam(requestObject);
            }
        } else if ("GET".equalsIgnoreCase(requestType)) {
            requestParam.setRequestType(RequestParam.RequestType.GET);
            if (requestObject != null) {
                requestParam.setGetParam(requestObject);
            }
        }
        requestParam.setNeedEncrypt(isEnc);
        if (isEnc) {
            requestParam.setHeadParam(getEncHeaderMap());
        }
        requestParam.setJsonRequest(isJsonRequest);

        UrlParam urlParam = UrlParam.CMDRequest;
        urlParam.resetHost(requestUrl);
        requestParam.setUrlParam(urlParam);

        requestParam.setRequestCallback(complete);
        ModuleMgr.getHttpMgr().request(requestParam);
    }

    /**
     * @return 获取游戏调用时自定义的请求头：safe-request
     */
    public Map<String, String> getEncHeaderMap() {
        Map<String, String> headerParams = new HashMap<>();
        headerParams.put("Accept", "application/jx-json");
        headerParams.put("Content-Type", "application/jx-json");
        return headerParams;
    }

    /**
     * 请求一些在线配置信息
     */
    public void requestStaticConfig() {
        reqShareAccounts();
        requestConfig();
        getVerifyStatus(null);
        AttentionUtil.initUserDetails();
    }

    /**
     * 请求服务器在线配置
     */
    private void requestConfig() {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("platform", Constant.PLATFORM_TYPE);
        requestParams.put("ver", Constant.SUB_VERSION);
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.staticConfig, requestParams, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    commonConfig = (CommonConfig) response.getBaseData();
                }
            }
        });
    }

    /**
     * 修改判断，有些机型在某个页面闪退后会重新启动这个闪退的页面，在这个过程中单例的对象会被重置清空
     * 为此修改下逻辑，在对象为null的时候使用本地的配置。
     *
     * @return 获取服务器静态配置对象
     * @updateAuthor Mr.Huang
     * @updateDate 2017-09-21 09:30
     */
    public CommonConfig getCommonConfig() {
        if (commonConfig == null) {
            commonConfig = new CommonConfig();
            String pspServerString = PSP.getInstance().getString(FinalKey.SERVER_STATIC_CONFIG, "");
            commonConfig.parseJson(TextUtils.isEmpty(pspServerString) ?
                    FileUtil.getFromAssets(App.getContext(), CommonConfig.STATIC_CONFIG_FILE) : pspServerString);
        }
        return commonConfig;
    }

    /**
     * 请求分享帐号信息并初始化分享sdk
     */
    private void reqShareAccounts() {
        Map<String, Object> params = new HashMap<>();
        params.put("pack_name", ModuleMgr.getAppMgr().getPackageName());
        params.put("app_id", ModuleMgr.getAppMgr().getLiteAppId());
        ModuleMgr.getHttpMgr().reqPostAndCacheHttp(UrlParam.reqShareAccounts, params, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                // 带缓存的请求，不管成功失败都进行sdk的初始化，当接口未返回帐号信息时使用打包编译时注入的帐号
                initShareLib((ShareAccounts) response.getBaseData());
            }
        });
    }

    /**
     * 初始化分享sdk[provide by tangjianbo]
     */
    private void initShareLib(ShareAccounts shareAccounts) {
        try {
            AppInfo qqAppInfo = new AppInfo(shareAccounts.getQqAppId(), shareAccounts.getQqAppKey(), BuildConfig.APPLICATION_ID);
            AppInfo wechatAppInfo = new AppInfo(shareAccounts.getWxAppId(), shareAccounts.getWxAppKey(), BuildConfig.APPLICATION_ID);
            ShareUtils.init(App.getContext().getApplicationContext(), qqAppInfo, wechatAppInfo);
        } catch (Exception e) {
            PLogger.printThrowable(e);
            PToast.showShort(App.getContext().getString(R.string.share_init_error));
        }
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        DirType.clearCache(App.getContext());
        //删除48小时后的真人聊天消息数据库缓存
        ModuleMgr.getChatMgr().deleteMessageHour(48);
    }

    /**
     * 获取离线消息: 客户端每次最多只处理1000条离线消息
     */
    public void reqOfflineMsg(RequestComplete complete) {
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("count", 1000);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqOfflineMsg, getParams, complete);
    }

    /**
     * 发送离线送达消息
     */
    public void reqOfflineRecvedMsg(List<OffMsgInfo> msgs, RequestComplete complete) {
        Map<String, Object> params = new HashMap<>();
        params.put("list", msgs);// msgs

        ModuleMgr.getHttpMgr().reqPostJsonNoCacheHttp(UrlParam.reqOfflineRecvedMsg, JSON.toJSONString(params), complete);
    }

    /**
     * 获取自己的音频、视频开关配置
     */
    public void requestVideochatConfig(final RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqMyVideochatConfig, null, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    videoVerify = (VideoVerifyBean) response.getBaseData();
                }
                if (complete != null) {
                    complete.onRequestComplete(response);
                }
            }
        });
    }

    /**
     * 修改自己的音频、视频开关配置
     */
    public void setVideochatConfig(boolean videoStatus, boolean audioStatus, final RequestComplete complete) {
        HashMap<String, Object> post_param = new HashMap<>();
        post_param.put("videochat", videoStatus ? 1 : 0);
        post_param.put("audiochat", audioStatus ? 1 : 0);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.setVideochatConfig, post_param, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    requestVideochatConfig(complete);
                    return;
                }
                PToast.showShort(response.getMsg());
            }
        });
    }

    /**
     * 上传视频认证配置
     */
    public void addVideoVerify(String imgUrl, String videoUrl, RequestComplete complete) {
        HashMap<String, Object> post_param = new HashMap<>();
        post_param.put("imgurl", imgUrl);
        post_param.put("videourl", videoUrl);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.addVideoVerify, post_param, complete);
    }

    /**
     * 获取客服信息
     */
    public void getCustomerserviceContact() {
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.getserviceqq, null, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    contactBean = (ContactBean) response.getBaseData();
                }
            }
        });
    }

    /**
     * 请求礼物列表
     */
    public void requestGiftList(final GiftHelper.OnRequestGiftListCallback callback) {
        ModuleMgr.getHttpMgr().reqGetAndCacheHttp(UrlParam.getGiftLists, null, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                PLogger.d("---GiftList--->isCache：" + response.isCache() + "，" + response.getResponseString());
                // 不管是否缓存，只要返回ok，就进行解析赋值
                if (response.isOk()) {
//                    giftLists = new GiftsList();
//                    giftLists.parseJson(response.getResponseString());
                    giftLists = (GiftsList) response.getBaseData();
                    if (callback != null) {
                        callback.onRequestGiftListCallback(response.isOk());
                    }
                } else {
                    PLogger.e("NOTICE: Gift list request fail.");
                }
            }
        });
    }

    /**
     * 请求背包列表
     */
    public HTCallBack requestMyBag(final RequestComplete complete) {
        return ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.getMyBag, null, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                PLogger.d("---BagList--->isCache：" + response.isCache() + "，" + response.getResponseString());
                // 不管是否缓存，只要返回ok，就进行解析赋值
                if (response.isOk()) {
                    if (bagList == null) {
                        bagList = new BagList();
                    }
                    bagList.parseJson(response.getResponseString());
                } else {
                    PLogger.e("NOTICE: Bag list request fail.");
                }
                if (complete != null) {
                    complete.onRequestComplete(response);
                }
            }
        });
    }

    /**
     * @return 获取礼品信息
     */
    public GiftsList getGiftLists() {
        if (giftLists == null) {
            giftLists = new GiftsList();
        }
        return giftLists;
    }

    /**
     * @return 获取背包信息
     */
    public BagList getBagList() {
        if (bagList == null) {
            bagList = new BagList();
        }
        return bagList;
    }

    /**
     * @return 返回认证消息对象
     */
    public IdCardVerifyStatusInfo getIdCardVerifyStatusInfo() {
        if (mIdCardVerifyStatusInfo == null) {
            mIdCardVerifyStatusInfo = new IdCardVerifyStatusInfo();
        }
        return mIdCardVerifyStatusInfo;
    }

    public void clearIdCardVerifyStatusInfo() {
        if (mIdCardVerifyStatusInfo == null) {
            return;
        }
        mIdCardVerifyStatusInfo = null;
    }

    public VideoVerifyBean getVideoVerify() {
        return videoVerify != null ? videoVerify : new VideoVerifyBean();
    }

    public void setVideoVerify(VideoVerifyBean videoVerify) {
        this.videoVerify = videoVerify;
    }

    /**
     * 判断是否到达第二天,并存储
     *
     * @return true 达到第二天 并存储
     */
    public boolean checkDateAndSave(String key) {
        return checkDate(key, true);
    }

    /**
     * 判断是否到达第二天，对判断结果不进行存储
     *
     * @return true 达到第二天
     */
    public boolean checkDate(String key) {
        return checkDate(key, false);
    }

    /**
     * 判断是否到达第二天并进行时间存储
     *
     * @param key    进行比对的SP存储key
     * @param isSave 是否进行存储
     * @return true 达到第二天
     */
    public boolean checkDate(String key, boolean isSave) {
        String recommendDate = PSP.getInstance().getString(key, "-1");
        if (!recommendDate.equals(TimeUtil.getCurrentData())) {
            if (isSave) {
                PSP.getInstance().put(key, TimeUtil.getCurrentData() + "");
            }
            return true;
        }
        return false;
    }

    /**
     * 保存达到第二天的状态
     *
     * @param key
     */
    public void saveDateState(String key) {
        PSP.getInstance().put(key, TimeUtil.getCurrentData() + "");
    }

    // ---------------------------- 软件升级 start ------------------------------

    /**
     * 软件升级账号密码迁移临时文件路径
     */
    private static final String UPDATE_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "xiaou" + File.separator;

    /**
     * 检查应用升级
     *
     * @param activity  FragmentActivity实例
     * @param isShowTip 是否展示界面提示
     */
    public void checkUpdate(final FragmentActivity activity, final UpgradeSource upgradeSource, final boolean isShowTip) {
        if (isShowTip) {
            LoadingDialog.show(activity, "检测中请等待...");
        }
        HashMap<String, Object> getParams = new HashMap<>();
        getParams.put("c_uid", ModuleMgr.getAppMgr().getMainChannelID());// 渠道ID
        getParams.put("c_sid", ModuleMgr.getAppMgr().getSubChannelID());// 子渠道
        getParams.put("platform", "android");
        getParams.put("v", ModuleMgr.getAppMgr().getVerCode());
        getParams.put("app_key", EncryptUtil.sha1(ModuleMgr.getAppMgr().getSignature()));
        getParams.put("package_name", ModuleMgr.getAppMgr().getPackageName());
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.checkUpdate, getParams, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (isShowTip) {
                    LoadingDialog.closeLoadingDialog(300);
                }
                AppUpdate appUpdate = new AppUpdate();
                appUpdate.parseJson(response.getResponseString());
                UIShow.showUpdateDialog(activity, appUpdate, upgradeSource, isShowTip);
            }
        });
    }

    /**
     * 将用户信息写入文件，跨软件升级使用
     *
     * @param packageName 需要升级到的包名
     * @param versionCode 需要升级到的软件版本号
     */
    public void updateSaveUP(String packageName, int versionCode) {
        Map<String, Object> upMap = new HashMap<>();
        upMap.put("packageName", packageName);
        upMap.put("versionCode", versionCode);
        upMap.put("user", PSP.getInstance().getString(FinalKey.LOGIN_USER_KEY, null));

        // 如果文件夹创建失败，直接跳出
        if (!DirType.isFolderExists(UPDATE_CACHE_PATH)) {
            return;
        }

        // 文件写入
        File file = new File(UPDATE_CACHE_PATH + "user_cache");
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(file));//根据文件创建文件的输出流
            out.write(JSON.toJSONString(upMap));//向文件写入内容
            out.flush();
        } catch (Exception e) {
            PLogger.printThrowable(e);
        } finally {
            try {
                if (out != null) {
                    out.close();// 关闭输出流
                }
            } catch (Exception e) {
                PLogger.printThrowable(e);
            }
        }
    }

    /**
     * 获取已登录过的所有用户ID和密码
     */
    public void updateUsers() {
        String key = FileUtil.fileRead(UPDATE_CACHE_PATH + "user_cache");
        if (!TextUtils.isEmpty(key)) {
            JSONObject cacheObject = JsonUtil.getJsonObject(key);
            String packageName = cacheObject.optString("packageName");
            int versionCode = cacheObject.optInt("versionCode");
            // 如果当前包名或版本号不等于目标包名或版本号，才进行处理
            if (!ModuleMgr.getAppMgr().getPackageName().equalsIgnoreCase(packageName) ||
                    ModuleMgr.getAppMgr().getVerCode() != versionCode) {
                return;
            }

            // 读取旧版本用户数据
            JSONObject userObject = JsonUtil.getJsonObject(cacheObject.optString("user"));
            List<UP> upList = new ArrayList<>();
            UP up;
            if (userObject != null) {
                JSONArray jsonArray = userObject.optJSONArray("user");
                for (int i = 0; jsonArray != null && i < jsonArray.length(); i++) {
                    JSONObject upObject = JsonUtil.getJsonObject(jsonArray.optString(i));
                    try {
                        up = new UP(Long.parseLong(EncryptUtil.decryptDES(upObject.optString("sUid"), FinalKey.UP_DES_KEY)),
                                EncryptUtil.decryptDES(upObject.optString("sPw"), FinalKey.UP_DES_KEY));
                        upList.add(up);
                    } catch (Exception e) {
                        PLogger.printThrowable(e);
                    }
                }
            }
            ModuleMgr.getLoginMgr().putUserJson(upList);
            FileUtil.deleteFile(UPDATE_CACHE_PATH + "user_cache");
        }
    }
    // ---------------------------- 软件升级 end ------------------------------

    /**
     * 爵位版获取一键打招呼展示的用户信息
     *
     * @param complete 请求回调
     */
    public void getSayHiList(RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.GET_ONE_HELLO_USERS, null, complete);
    }

    /**
     * 一键打招呼的状态Key
     *
     * @return
     */
    public String getSayHelloKey() {
        return "Say_Hello_" + ModuleMgr.getCenterMgr().getMyInfo().getUid();
    }

    /**
     * @param activity FragmentActivity实例
     * @updateAuthor Mr.Huang
     * @date 2017-07-11
     * 显示一键打招呼对话框
     */
    public void showSayHelloDialog(final FragmentActivity activity) {
        if (checkDate(getSayHelloKey()) && ModuleMgr.getCenterMgr().getMyInfo().isMan() && !ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
            getSayHiList(new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    // 2017/9/16 AB环境均弹出此一键打招呼页面
                    if (response.isOk()) {
                        UIShow.showSayHelloWithPeerage(activity, response.getResponseString());
                    }
                }
            });
        }
        UIShow.showSureUserDlg(activity);
    }

    //============================== 小友模块相关接口 =============================

    /**
     * 获取最近礼物列表
     *
     * @param complete 请求完成后回调
     */
    public void lastGiftList(RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqGetAndCacheHttp(UrlParam.lastGiftList, null, complete);
    }

    /**
     * 通用跑马灯列表
     *
     * @param complete 请求完成后回调
     */
    public void userBanner(int ver, RequestComplete complete) {
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("ver", ver);
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.userBanner, getParams, complete);
    }

    /**
     * 接受视频聊天请求
     *
     * @param invite_id 视频聊天id
     */
    public void reqAcceptVideoChat(long invite_id, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("invite_id", invite_id);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqAcceptChat, postParams, complete);
    }

    /**
     * 拒绝视频聊天请求
     *
     * @param vcid 视频聊天id
     */
    public void reqRejectVideoChat(long vcid, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("vc_id", vcid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqRejectChat, postParams, complete);
    }

    /**
     * 上传身份证照片
     *
     * @param url      图片本地地址
     * @param complete 上传完成回调
     */
    public void uploadIdCard(final String url, final RequestComplete complete) {
        if (FileUtil.isExist(url)) {
            Map<String, File> fileParams = new HashMap<>();
            fileParams.put("userfile", new File(url));

            long uid = ModuleMgr.getLoginMgr().getUserList().get(0).getUid();
            String password = ModuleMgr.getLoginMgr().getUserList().get(0).getPw().trim();

            Map<String, Object> postParams = new HashMap<>();
            postParams.put("uid", uid);
            postParams.put("code", EncryptUtil.md5(uid + EncryptUtil.md5(password)));

            ModuleMgr.getHttpMgr().uploadFile(UrlParam.uploadIdCard, null, fileParams, new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    if (complete != null) {
                        complete.onRequestComplete(response);
                    }
                }
            });

        } else {
            LoadingDialog.closeLoadingDialog();
            PToast.showShort("图片地址无效");
        }
    }

    /**
     * 我关注的列表
     *
     * @param complete 请求完成后回调
     */
    public void getFollowing(RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.getFollowing, null, complete);
    }

    /**
     * 关注我的列表
     *
     * @param complete 请求完成后回调
     */
    public void getFollowers(RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.getFollowers, null, complete);
    }

    /**
     * 手机验证
     *
     * @param phoneNum 手机号
     * @param complete 请求完成后回调
     */
    public void bindCellPhone(String code, String phoneNum, RequestComplete complete) {
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("cellPhone", phoneNum);
        getParams.put("verifyCode", code);
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.bindCellPhone, getParams, complete);
    }

    /**
     * 用户身份认证提交
     *
     * @param id_num       身份证号码
     * @param accountname  真实姓名
     * @param accountnum   银行账号/支付宝账户
     * @param bank         银行
     * @param subbank      支行
     * @param id_front_img 身份证正面照URL
     * @param id_back_img  身份证背面照URL
     * @param face_img     手持身份证照URL
     * @param paytype      支付类型 1银行 2支付宝 (默认支付宝)
     * @param complete     请求完成后回调
     */
    public void userVerify(String id_num, String accountname, String accountnum, String bank, String subbank, String id_front_img,
                           String id_back_img, String face_img, int paytype, RequestComplete complete) {
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("id_num", id_num);
        getParams.put("accountname", accountname);
        getParams.put("accountnum", accountnum);
        getParams.put("bank", bank);
        getParams.put("subbank", subbank);
        getParams.put("id_front_img", id_front_img);
        getParams.put("id_back_img", id_back_img);
        getParams.put("face_img", face_img);
        getParams.put("paytype", paytype);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.userVerify, getParams, complete);
    }

    /**
     * 用户身份认证提交
     *
     * @param id_num       身份证号码
     * @param accountname  真实姓名
     * @param id_front_img 身份证正面照URL
     * @param id_back_img  身份证背面照URL
     * @param face_img     手持身份证照URL
     * @param complete     请求完成后回调
     */
    public void userVerify1(String id_num, String accountname, String id_front_img,
                            String id_back_img, String face_img, RequestComplete complete) {
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("id_num", id_num);
        getParams.put("accountname", accountname);
        getParams.put("id_front_img", id_front_img);
        getParams.put("id_back_img", id_back_img);
        getParams.put("face_img", face_img);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.userVerify1, getParams, complete);
    }

    /**
     * 获取用户验证信息(密)
     *
     * @param complete 请求完成后回调
     */
    public void getVerifyStatus(final RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.getVerifyStatus, null, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    mIdCardVerifyStatusInfo = new IdCardVerifyStatusInfo();
                    mIdCardVerifyStatusInfo.parseJson(response.getResponseString());
                }
                if (complete != null) {
                    complete.onRequestComplete(response);
                }
            }
        });
    }

    /**
     * 手机验证
     *
     * @param phoneNum 手机号
     * @param complete 请求完成后回调
     */
    public void sendSMS(String phoneNum, RequestComplete complete) {
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("cellPhone", phoneNum);
        getParams.put("type", "1");
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.sendSMS, getParams, complete);
    }

    /**
     * 接收聊天红包
     *
     * @param red_log_id 聊天红包流水号
     */
    public void receiveChatRedBag(int red_log_id) {
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("rid", red_log_id);
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.reqReceiveChatBag, getParams, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                PToast.showShort(TextUtils.isEmpty(response.getMsg()) ?
                        App.getContext().getString(R.string.received_error) : response.getMsg());
            }
        });
    }

    /**
     * 领取红包(领取自己的红包)
     *
     * @param complete 请求完成后回调
     */
    public void achieveRewardHongbao(RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.achieveRewardHongbao, null, complete);
    }

    /**
     * 赠送红包
     *
     * @param tuid     对方uid
     * @param complete 请求完成后回调
     */
    public void bestowRewardHongbao(long tuid, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("tuid", tuid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.bestowRewardHongbao, postParams, complete);
    }

    /**
     * 接受赠送红包
     *
     * @param id       红包id
     * @param complete 请求完成后回调
     */
    public void receiveRewardHongbao(long id, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("id", id);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.receiveRewardHongbao, postParams, complete);
    }

    /**
     * 接收礼物
     *
     * @param rid      礼物红包ID
     * @param gname    礼物名称
     * @param gid      礼物ID
     * @param complete 请求完成后回调
     */
    public void receiveGift(long rid, String gname, int gid, RequestComplete complete) {
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("rid", rid);
        getParams.put("gname", gname);
        getParams.put("gid", gid);
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.receiveGift, getParams, complete);
    }

    /**
     * 送礼物
     *
     * @param touid    赠送对象UId
     * @param giftid   礼物Id
     * @param giftnum  礼物数量（不填为1）
     * @param gtype    礼物来源类型 1 聊天列表 2 旧版索要 3 新版索要 4私密视频，5音视频插件，6解锁礼物(2.3版) （不填为1）8解锁聊天送礼
     * @param complete 请求完成后回调
     */
    public void sendGift(String touid, String giftid, int giftnum, int gtype, String source, int giftfrom, RequestComplete complete) {
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("touid", touid);
        getParams.put("gift_id", giftid);
        getParams.put("giftnum", giftnum);
        getParams.put("ftype", gtype);
        getParams.put("source", source);
        getParams.put("receive", (gtype == 5 || gtype == 6) ? 1 : 0);//是否自动接收 1是 0 否
        getParams.put("giftfrom", giftfrom);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.sendGift, getParams, complete);
    }

    /**
     * 摇钱树红包列表
     *
     * @param complete 请求完成后回调
     */
    public void getTreeRedbagList(RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<String, Object>();
        postParams.put("uid", App.uid);
        ModuleMgr.getHttpMgr().reqPostNoCacheNoEncHttp(UrlParam.reqTreeRedbagList, postParams, complete);
    }

    /**
     * 客户端获得用户红包列表
     *
     * @param complete 请求完成后回调
     */
    public void getRedbagList(RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqRedbagList, null, complete);
    }

    /**
     * 客户端用户红包入袋
     *
     * @param uid      用户Id
     * @param money    红包金额(分)
     * @param redbagid 红包ID
     * @param type     红包类型 1,2或为空为水果红包和水果排行红包 3为聊天红包 4聊天排名红包 5礼物红包
     * @param complete 请求完成后回调
     */
    public void reqAddredTotal(long uid, double money, long redbagid, int type, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("uid", uid);
        postParams.put("money", money);
        postParams.put("redbagid", redbagid);
        postParams.put("type", type);
        ModuleMgr.getHttpMgr().reqPostNoCacheNoEncHttp(UrlParam.reqAddredTotal, postParams, complete);
    }

    /**
     * 红包记录--红包入袋 -- 一键入袋(24不能提现)
     *
     * @param uid      用户Id
     * @param complete 请求完成后回调
     */
    public void reqAddredonekey(long uid, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("uid", uid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqAddredonekey, postParams, complete);
    }

    /**
     * 客户端请求用户提现列表
     *
     * @param complete 请求完成后回调
     */
    public void reqWithdrawlist(RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqWithdrawlist, null, complete);
    }

    /**
     * 红包记录--提现申请
     *
     * @param money       提现金额(分)
     * @param paytype     支付方式(1或为空 银行卡, 2 支付宝)
     * @param accountname 帐户姓名
     * @param accountnum  银行卡号/支付宝账号
     * @param bank        开户行/支付类型
     * @param subbank     开户支行
     * @param complete    请求完成后回调
     */
    public void reqWithdraw(String money, int paytype, String accountname, String accountnum, String bank, String subbank, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("money", (Float.parseFloat(money) * 100) + "");
        postParams.put("paytype", paytype);
        postParams.put("accountname", accountname);
        postParams.put("accountnum", accountnum);
        postParams.put("bank", bank);
        postParams.put("subbank", subbank);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqWithdraw, postParams, complete);
    }

    /**
     * 新客户端提交提现修改
     *
     * @param id          提现记录ID
     * @param paytype     支付方式(1或为空 银行卡, 2 支付宝)
     * @param accountname 帐户姓名
     * @param accountnum  银行卡号/支付宝账号
     * @param bank        开户行/支付类型
     * @param subbank     开户支行
     * @param complete    请求完成后回调
     */
    public void reqWithdrawmodifyNew(int id, int paytype, String accountname, String accountnum, String bank, String subbank, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("id", id);
        postParams.put("paytype", paytype);
        postParams.put("accountname", accountname);
        postParams.put("accountnum", accountnum);
        postParams.put("bank", bank);
        postParams.put("subbank", subbank);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqWithdrawmodifyNew, postParams, complete);
    }

    /**
     * 红包记录--提现申请获取地址
     *
     * @param complete 请求完成后回调
     */
    public void reqWithdrawAddress(RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqWithdrawAddress, null, complete);
    }

    /**
     * 生成订单
     *
     * @param orderID
     * @param complete
     */
    public void reqGenerateOrders(int orderID, RequestComplete complete) {
        HashMap<String, Object> getParms = new HashMap<>();
        getParms.put("pid", orderID);
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.reqCommodityList, getParms, complete);
    }

    /**
     * 微信支付方式
     *
     * @param complete
     */
    public void reqWXMethod(String name, int payID, int payMoney, int payCType, String source, RequestComplete complete) {
        HashMap<String, Object> postParms = new HashMap<>();
        postParms.put("subject", name);
        postParms.put("body", name);
        postParms.put("productid", payID);
        postParms.put("total_fee", payMoney);
        postParms.put("source", source);

        //  postParms.put("total_fee", "0.01");// 钱
        if (payCType > -1) {
            postParms.put("payCType", payCType);
        }
        ModuleMgr.getHttpMgr().reqPostNoCacheNoEncHttp(UrlParam.reqWX, postParms, complete);
    }

    /**
     * 银联或支付宝
     *
     * @param urlParam
     * @param out_trade_no
     * @param name
     * @param payID
     * @param payMoney
     * @param complete
     */
    public void reqCUPOrAlipayMethod(UrlParam urlParam, String out_trade_no, String name, int payID, int payMoney, String source, RequestComplete complete) {
        HashMap<String, Object> postParms = new HashMap<>();
        postParms.put("out_trade_no", out_trade_no);// 订单号
        postParms.put("subject", name);// 标题
        postParms.put("body", "android-" + name);
        postParms.put("productid", payID);
        postParms.put("total_fee", payMoney);
        postParms.put("payCType", 1000);
        postParms.put("user_client_type", 2);
        postParms.put("source", source);
        ModuleMgr.getHttpMgr().reqPostNoCacheNoEncHttp(urlParam, postParms, complete);
    }

    /**
     * 刷新充值是否成功
     */
    public void reqPayComplete(String order_no, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("order_no", order_no);

        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.refPayComplete, postParams, complete);
    }

    /**
     * 男用户发起密友申请
     *
     * @param tuid    对方uid
     * @param gift_id 礼物id
     */
    public void startIntimateRequestM(long tuid, int gift_id, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("tuid", tuid);
        postParams.put("gift_id", gift_id);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(ModuleMgr.getCenterMgr().getMyInfo().isMan() ? UrlParam.startIntimateRequestM : UrlParam.startIntimateRequestW, postParams, complete);
    }

    /**
     * 女用户拒绝男或男用户拒绝女用户的密友申请
     *
     * @param req_id 好友请求ID
     */
    public void rejectIntimateRequestW(long req_id, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("req_id", req_id);
        boolean isMan = ModuleMgr.getCenterMgr().getMyInfo().isMan();
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(isMan ? UrlParam.reqRejectIntimateFriendM : UrlParam.rejectIntimateRequestW, postParams, complete);
    }

    /**
     * 女用户同意男或男用户同意女用户的密友申请
     *
     * @param req_id 好友请求ID
     */
    public void acceptIntimateRequstW(long req_id, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("req_id", req_id);
        boolean isMan = ModuleMgr.getCenterMgr().getMyInfo().isMan();
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(isMan ? UrlParam.reqAcceptIntimateRequstM : UrlParam.acceptIntimateRequstW, postParams, complete);
    }

    /**
     * 解除密友关系
     *
     * @param tuid 对方uid
     */
    public void cancelIntimateFriend(long tuid, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("tuid", tuid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.cancelIntimateFriend, postParams, complete);
    }

    /**
     * 我的密友任务信息
     *
     * @param tuid 对方uid
     */
    public void getIntimateFriendTask(long tuid, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("tuid", tuid);
        ModuleMgr.getHttpMgr().reqPostAndCacheHttp(UrlParam.getIntimateFriendTask, postParams, complete);
    }

    /**
     * 女性密友发送任务接口
     *
     * @param tuid    对方uid
     * @param task_id 任务ID
     */
    public void sendIntimateTask(long tuid, long task_id, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("tuid", tuid);
        postParams.put("task_id", task_id);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.sendIntimateTask, postParams, complete);
    }

    /**
     * 解析cme
     *
     * @param cmd 要解析的cmd
     */
    public void extractShareinfo(String cmd, RequestComplete requestComplete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("str", cmd);
        String json = JSON.toJSONString(postParams);
        ModuleMgr.getHttpMgr().reqPostJsonNoCacheHttp(UrlParam.extractShareinfo, json, requestComplete);
    }

    //================ 发现 start =========================\\

    /**
     * 举报
     *
     * @param tuid     被举报人uid
     * @param content
     * @param detail
     * @param complete
     */
    public void complainBlack(long tuid, String content, String detail, RequestComplete complete) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("tuid", tuid);
        parms.put("content", content);
        parms.put("detail", detail);

        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.complainBlack, parms, complete);
    }

    /**
     * "page":1,
     * "limit":10,
     * "reload":1,  //是否刷新缓存 1为刷新缓存 0为向下翻页
     * "x":104.55,  //[opt] 用户位置
     * "y":40.11	//[opt] 用户位置
     * "ver":5		//[opt]大版本号
     * "isnear":1	//[opt]是否搜索附近的人
     *
     * @param page
     * @param reload
     * @param complete
     */
    public void getMainPage(int page, int reload, RequestComplete complete) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("page", page);
        parms.put("reload", reload);
        parms.put("limit", 10);
        parms.put("x", LocationMgr.getInstance().getPointD().longitude);  //经度
        parms.put("y", LocationMgr.getInstance().getPointD().latitude); //纬度
        parms.put("ver", Constant.SUB_VERSION);
        parms.put("isnear", 1);

        ModuleMgr.getHttpMgr().reqPostAndCacheHttp(UrlParam.getMainPage, parms, complete);
    }

    /**
     * 爵位用户推荐
     *
     * @param page     //当前页
     * @param reload   //是否刷新缓存 1为刷新缓存 0为向下翻页
     * @param complete //[opt]大版本号
     */
    public void getTitleUsers(int page, int reload, RequestComplete complete) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("page", page);
        parms.put("reload", reload);
        parms.put("limit", 10);
        parms.put("ver", Constant.SUB_VERSION);
        ModuleMgr.getHttpMgr().reqPostAndCacheHttp(UrlParam.reqTitleUsers, parms, complete);
    }

    /**
     * "x":104.55,  //用户位置
     * "y":40.11	// 用户位置
     * "ver":5		//[opt]大版本号
     *
     * @param complete
     */
    public void getNearUsers2(RequestComplete complete) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("x", LocationMgr.getInstance().getPointD().longitude);  //经度
        parms.put("y", LocationMgr.getInstance().getPointD().latitude); //纬度
        parms.put("ver", Constant.SUB_VERSION);

        ModuleMgr.getHttpMgr().reqPostAndCacheHttp(UrlParam.getNearUsers2, parms, complete);
    }

    /**
     * 获取语聊用户推荐
     */
    public void getVideoChatUsers(RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.getVideoChatUsers, null, complete);
    }

    /**
     * 获取我的好友列表，视频好友，解锁好友
     *
     * @param page
     * @param complete
     */
    public void getMyFriends(int page, RequestComplete complete, int type) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("page", page);
        parms.put("limit", 10);
        UrlParam url = UrlParam.getMyFriends;
        if (type == MyFriendsPanel.UnlockFriends) {
            url = UrlParam.getUnlockFriends;
        } else if (type == MyFriendsPanel.VideoVoiceFriends) {
            url = UrlParam.getVideoFriends;
        }
        PLogger.i("UrlParam:" + url);
        ModuleMgr.getHttpMgr().reqPostAndCacheHttp(url, parms, complete);
    }

    /**
     * 获取黑名单列表
     *
     * @param complete
     */
    public void getMyDefriends(RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqPostAndCacheHttp(UrlParam.getMyDefriends, null, complete);
    }

    //================ 发现 end =========================\\

    /**
     * 获取自定义表情列表
     *
     * @param complete
     */
    public void reqCustomFace(RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqPostAndCacheHttp(UrlParam.reqCustomFace, null, complete);
    }

    /**
     * add自定义表情
     *
     * @param url
     * @param complete
     */
    public void addCustomFace(String url, RequestComplete complete) {
        HashMap<String, Object> postParms = new HashMap<>();
        postParms.put("url", url);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.addCustomFace, postParms, complete);
    }

    /**
     * del自定义表情
     *
     * @param url
     * @param complete
     */
    public void delCustomFace(String url, RequestComplete complete) {
        HashMap<String, Object> postParms = new HashMap<>();
        postParms.put("url", url);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.delCustomFace, postParms, complete);
    }

    public void reqUserInfoSummary(List<Long> uids, RequestComplete complete) {
        HashMap<String, Object> postParms = new HashMap<>();
        postParms.put("uids", uids.toArray(new Long[uids.size()]));
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqUserInfoSummary, postParms, complete);
    }

    /**
     * 获取用户热门列表
     *
     * @param page
     * @param reload   是否刷新缓存 1为刷新缓存 0为向下翻页
     * @param complete
     */
    public void reqUserInfoHotList(int page, boolean reload, RequestComplete complete) {
        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("page", page);
        postParams.put("limit", 10);
        postParams.put("reload", reload ? 1 : 0);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqUserHotList, postParams, complete);
    }

    /**
     * 提交当前用户设备网络信息[接口只返回status字段，因此不关心返回值]
     *
     * @param net_tp     网络类型，用户上网方式（2017-06-20）Wifi 1 4G 2 3G/2G 3 其它 4
     * @param phone_info 用户设备描述
     */
    public void updateNetInfo(int net_tp, String phone_info) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("net_tp", net_tp);
        parms.put("phone_info", phone_info);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.updateNetInfo, parms, null);
    }

    /**
     * 聊天窗口信息--整合接口
     *
     * @param tuid     对方uid
     * @param complete 回调
     */
    public void reqChatInfo(long tuid, RequestComplete complete) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("tuid", tuid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqChatInfo, parms, complete);
    }

    /**
     * 私密视频/图片的评价
     *
     * @param qun_id
     * @param tuid
     * @param stat     //评价结果1喜欢2不喜欢  true=1
     * @param complete
     */
    public void reqSerectComment(long qun_id, long tuid, boolean stat, RequestComplete complete) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("qun_id", qun_id);
        parms.put("tuid", tuid);
        parms.put("stat", stat ? 1 : 2);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqSerectComment, parms, complete);
    }

    /**
     * 私密视频/语音/图片/聊天的解锁通知
     *
     * @param qun_id   群发id
     * @param tuid     对方uid
     * @param msgId    被查看的消息id
     * @param complete 回调
     */
    public void reqSerectUnlock(long qun_id, long tuid, long msgId, RequestComplete complete) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("qun_id", qun_id);
        parms.put("tuid", tuid);
        parms.put("msg_id", msgId);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqSerectUnlock, parms, complete);
    }

    /**
     * 群发索要图片礼物
     *
     * @param selectGiftId 礼物id
     * @param content      说的话
     * @param mediaUrls    图片地址
     * @param complete     回调
     */
    public void reqQunPhotos(int selectGiftId, String content, ArrayList<String> mediaUrls, RequestComplete complete) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("gift_id", selectGiftId);
        parms.put("info", content);
        parms.put("photos", mediaUrls.toArray(new String[mediaUrls.size()]));
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqQunPhotos, parms, complete);
    }

    /**
     * 群发索要视频礼物
     *
     * @param selectGiftId 礼物id
     * @param videoUrl     视频url
     * @param duration     视频时长 秒
     * @param scaleUrl     缩略图url
     * @param complete     回调
     */
    public void reqQunVideo(int selectGiftId, String videoUrl, long duration, String scaleUrl, RequestComplete complete) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("gift_id", selectGiftId);
        parms.put("info", "");
        parms.put("video_url", videoUrl);
        parms.put("video_len", duration);
        parms.put("thumb", scaleUrl);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqQunVideo, parms, complete);
    }

    /**
     * 爵位升级礼物
     */
    public void getNobilityLeftGifts(RequestComplete complete) {
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.getNobilityLeftGifts, null, complete);
    }

    /**
     * 添加女性自动回复
     *
     * @param complete
     */
    public void addWomanAutoReply(String text, String speech_url, int timespan, int selected, int auto_type, RequestComplete complete) {
        Map<String, Object> parms = new HashMap<>();
        if (!TextUtils.isEmpty(text)) {//文字
            parms.put("text", text);
        } else {//语音
            parms.put("speech_url", speech_url);
            parms.put("timespan", timespan);
        }
        parms.put("selected", selected);
        parms.put("auto_type", auto_type);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqAddWomanAutoReply, parms, complete);
    }

    /**
     * 获取女性自动回复列表
     *
     * @param complete
     */
    public void getWomanAutoReplyList(int auto_type, RequestComplete complete) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("auto_type", auto_type);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqWomanAutoReplyList, parms, complete);
    }

    /**
     * 设置选中的自动回复条目
     */
    public void setWomanAutoReplyItem(int arid, boolean selected, int auto_type, RequestComplete complete) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("selected", selected ? 1 : 0);
        parms.put("arid", arid);
        parms.put("auto_type", auto_type);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqWomanAutoReplySetSelect, parms, complete);
    }

    /**
     * 设置自动邀请音视频状态
     *
     * @param
     * @param
     * @param complete
     */
    public void setWomanAutoReplyAvStatus(boolean videoSelected, boolean voiceSelected, RequestComplete complete) {
        Map<String, Object> parms = new HashMap<>();

        parms.put("video", videoSelected ? 1 : 0);
        parms.put("audio", voiceSelected ? 1 : 0);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqWomanAutoReplySetAVStatus, parms, complete);
    }

    /**
     * 删除自动回复
     *
     * @param arid
     * @param complete
     */
    public void delWomanAutoReply(int arid, int auto_type, RequestComplete complete) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("arid", arid);
        parms.put("auto_type", auto_type);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqWomanAutoReplyDel, parms, complete);
    }

    /**
     * 群打招呼 和数量估算
     *
     * @param area       地区
     * @param yan        颜值
     * @param see        看相册选项
     * @param isGetCount 是否为群打招呼获取人数
     * @param complete
     */
    public void qunSayHelloCount(int area, int yan, int see, boolean isGetCount, RequestComplete complete) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("area", area);
        parms.put("yan", yan);
        parms.put("see", see);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(isGetCount ? UrlParam.qunSayHelloCount : UrlParam.qunSayHello, parms, complete);
    }

    public MyEarnInfo getMyEarnInfo() {
        return myEarnInfo != null ? myEarnInfo : new MyEarnInfo();
    }

    /**
     * 女号收益信息
     */
    public void reqMyEarningsInfo(final RequestComplete complete) {
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            return;
        }
        Map<String, Object> parms = new HashMap<>();
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqMyEarningsInfo, parms, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                myEarnInfo = new MyEarnInfo();
                myEarnInfo.parseJson(response.getResponseString());
                complete.onRequestComplete(response);
            }
        });
    }

    /**
     * 我的密友列表
     *
     * @param page
     * @param complete
     */
    public HTCallBack reqMyIntimateFriends(int page, RequestComplete complete) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("page", page);
        parms.put("limit", 20);
        return ModuleMgr.getHttpMgr().reqPostAndCacheHttp(UrlParam.reqMyIntimateFriends, parms, complete);
    }

    /**
     * 我的密友任务信息
     *
     * @param tuid
     * @param complete
     */
    public void reqGetIntimateFriendTask(long tuid, RequestComplete complete) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("tuid", tuid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqGetIntimateFriendTask, parms, complete);
    }

    /**
     * 获取今天未完成密友任务的好友数
     *
     * @param complete
     */
    public void getIntimateTaskCnt(RequestComplete complete) {
        Map<String, Object> parms = new HashMap<>();
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.getIntimateTaskCnt, parms, complete);
    }

    /**
     * 请求可用的域名灾备地址
     *
     * @param position 当前访问的灾备域名列表的索引
     * @param complete 请求回调
     */
    public void checkEffectiveDomainArr(int position, RequestComplete complete) {
        UrlParam urlParam = UrlParam.CMDRequest;
        switch (position) {
            case 0:
                urlParam = UrlParam.checkDomainName_0;
                break;
            case 1:
                urlParam = UrlParam.checkDomainName_1;
                break;
            case 2:
                urlParam = UrlParam.checkDomainName_2;
                break;
            case 3:
                urlParam = UrlParam.checkDomainName_3;
                break;

            default:
                break;
        }
        HashMap<String, Object> params = new HashMap<>();
        // 如果是纯净版传2，其他马甲包传1[2017/11/4]
        params.put("mode", CommonUtil.isPurePackage() ? 2 : 1);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(urlParam, params, complete);
    }

    /**
     * 首页banner
     */
    public void reqBanner(RequestComplete complete) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("platform", Constant.PLATFORM_TYPE);
        params.put("ver", Constant.SUB_VERSION);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqBanner, params, complete);
    }

    /**
     * 获取用户推广二维码(密)
     *
     * @param type     分享链接方式 1分享用户 2分享直播
     * @param uid      要分享的用户uid(由于直播间id等同uid，所以用这个字段就可以)
     * @param complete 回调
     */
    public void reqShareQrCode(int type, long uid, RequestComplete complete) {
        Map<String, Object> postParam = new HashMap<>();
        postParam.put("type", type);
        postParam.put("share_uid", uid);
        PLogger.d("shareUid=" + uid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqShareQrCode, postParam, complete);
    }

    /**
     * 获取素材接口
     *
     * @param gender   自身性别
     * @param toGender 对方性别
     * @param complete 回调
     */
    public void reqShareUrl(int gender, int toGender, RequestComplete complete) {
        Map<String, Object> getParam = new HashMap<>();
        getParam.put("self_gender", gender);
        getParam.put("target_gender", toGender);
        ModuleMgr.getHttpMgr().request(new RequestParam().setUrlParam(UrlParam.reqShareUrl).setGetParam(getParam)
                .setNeedEncrypt(false).setRequestCallback(complete));
    }

    /**
     * 获取可用分享渠道接口
     *
     * @param uid      用户UID
     * @param type     1,其他分享 2,分享解锁
     * @param complete 回调
     */
    public void reqGetShareChannel(long uid, int type, RequestComplete complete) {
        Map<String, Object> getParam = new HashMap<>();
        getParam.put("uid", uid);
        getParam.put("group", type);
        ModuleMgr.getHttpMgr().request(new RequestParam().setUrlParam(UrlParam.reqGetShareChannel).setGetParam(getParam)
                .setNeedEncrypt(false).setRequestCallback(complete));
    }

    /**
     * 格式化分享的模板内容
     *
     * @param shareUid  //[opt]被分享人uid
     * @param earnMoney //[opt]赚到的钱数，分为单位
     */
    public void reqFormatShareContent(long shareUid, String earnMoney, RequestComplete complete) {
        Map<String, Object> postParam = new HashMap<>();
        //分享类型 主页分享:"usermain".解锁分享:"unlock" ,主动推广分享:"active", 赚钱分享:"earn"
        postParam.put("type", PSP.getInstance().getString(ModuleMgr.getCommonMgr().getPrivateKey(Constant.TEMP_SHARE_TYPE), CenterConstant.SHARE_TYPE_ACTIVE));
        postParam.put("pkg_name", ModuleMgr.getAppMgr().getPackageName());//包名
        postParam.put("ver", ModuleMgr.getAppMgr().getVerCode());//版本号
        postParam.put("share_uid", shareUid);
        postParam.put("earn_money", TextUtils.isEmpty(earnMoney) ? 0 : (Float.parseFloat(earnMoney) * 100));
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqFormatShareContent, postParam, complete);
    }

    /**
     * 分享赚钱成功的回调
     *
     * @param scontent 分享种类， 0 ：用户信息分享，1：直播分享
     * @param opt      0：默认， 1：分享解锁， 2：红包分享
     * @param channel  分享方式：1:微信， 2:朋友圈， 3:QQ， 4:QQ空间
     * @param complete 分享成功回调
     */
    public void shareCodeCallBack2(int scontent, int opt, final int channel, RequestComplete complete) {
        HashMap<String, Object> postParam = new HashMap<>();
        postParam.put("scontent", scontent);
        postParam.put("opt", opt);
        postParam.put("stype", channel);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.shareCodeCallback2, postParam, complete);
    }

    /**
     * 同随机的一个女用户视频
     *
     * @param vtype    期望 聊天媒体类型 1视频, 2语音
     * @param price    期望价格
     * @param usecard  是否使用视频卡
     * @param complete 回调
     */
    public void reqVCRandUser(int vtype, int price, int usecard, RequestComplete complete) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("vtype", vtype);
        parms.put("price", price);
        parms.put("usecard", usecard);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqVCRandUser, parms, complete);
    }

    /**
     * 直播间 送礼物
     *
     * @param roomid   赠送房间id
     * @param giftid   礼物Id
     * @param giftnum  礼物数量（不填为1）
     * @param complete 请求完成后回调
     */
    public void sendLiveGift(String roomid, String giftid, int giftnum, String source, RequestComplete complete) {
        Map<String, Object> getParams = new HashMap<>();
        getParams.put("room_id", roomid);
        getParams.put("present_id", giftid);
        getParams.put("present_num", giftnum);
        getParams.put("source", source);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.chatSendPresent, getParams, complete);
    }

    /**
     * 获取客服信息
     *
     * @param uid             被分享用户的uid
     * @param requestComplete
     */
    public void getQRCode(long uid, RequestComplete requestComplete) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("uid", uid);
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.getQRCode, parms, requestComplete);
    }


    /**
     * 获取要找的人的uid
     */
    public long getPromotionuid() {
        if (TextUtils.isEmpty(PSP.getInstance().getString(CenterConstant.USER_SHARE_UID, null))) {
            return 0;
        } else {
            return Long.valueOf(PSP.getInstance().getString(CenterConstant.USER_SHARE_UID, null));
        }
    }

    /**
     * 重置要找的人的 uid
     */
    public void reSetPromotionuid() {
        PSP.getInstance().put(CenterConstant.USER_SHARE_UID, "");
    }


    /**
     * 设置我要赚钱界面是否显示过的标记 用于判断要找的人是否在我要赚钱dlg没弹出的时候是否显示
     */
    private boolean isMMoneyDlgShow = false;

    public boolean isMMoneyDlgShow() {
        return isMMoneyDlgShow;
    }

    public void setMMoneyDlgShow(boolean MMoneyDlgShow) {
        isMMoneyDlgShow = MMoneyDlgShow;
    }

    public ApplyLiveStatusBean getApplyLiveStatusBean() {
        return applyLiveStatusBean == null ? new ApplyLiveStatusBean() : applyLiveStatusBean;
    }

    /**
     * 网络获取申请直播状态
     *
     * @param requestComplete 请求回调
     */
    public void getApplyLiveStatus(final RequestComplete requestComplete) {
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.getApplyLiveStatus, null, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    applyLiveStatusBean = (ApplyLiveStatusBean) response.getBaseData();
                }
                if (requestComplete != null) {
                    requestComplete.onRequestComplete(response);
                }
            }
        });
    }


    /***********设置首页立即抢红包****************/
    private String getIsShowredCard() {
        return ModuleMgr.getCommonMgr().getPrivateKey(Constant.MESSAGE_ISSHOWREDCARD);
    }

    public void setShowredCard() {//+ "-" +
        PSP.getInstance().put(getIsShowredCard(), TimeUtil.getCurrentData() + false);
    }

    public boolean getTodayChatShow() {
        String showRedCard = PSP.getInstance().getString(getIsShowredCard(), "");
        if (TextUtils.isEmpty(showRedCard)) {
            return true;
        }

        String currentData = TimeUtil.getCurrentData() + "false";
        return !currentData.equals(showRedCard);
    }
    /***********设置首页立即抢红包****************/
}