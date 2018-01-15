package com.juxin.predestinate.module.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.juxin.library.dir.DirType;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.request.DownloadListener;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.logic.request.RequestParam;
import com.juxin.predestinate.ui.live.bean.ShareMatrial;
import com.juxin.predestinate.ui.share.QQShareActivity;
import com.juxin.predestinate.ui.share.QQShareCallBack;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.juxin.predestinate.wxapi.WXEntryActivity;
import com.tangzi.sharelibrary.utils.ShareUtils;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.tauth.Tencent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述：
 * 创建时间：2017/11/7 11:16
 * 修改时间：2017/11/7 11:16
 * Created by zhoujie on 2017/11/7
 * 修改备注：
 */

public class ShareUtil {
    private static volatile ShareUtil INSTANCE;

    private volatile MyShareCallBack mShareCallBack;

    //3:微信好友
    public static final int CHANNEL_WX_FRIEND = 3;
    //4:朋友圈
    public static final int CHANNEL_WX_FRIEND_CRICLE = 4;
    //1:QQ好友
    public static final int CHANNEL_QQ_FRIEND = 1;
    //2:QQ空间
    public static final int CHANNEL_QQ_ZONE = 2;
    //5：二维码
    public static final int CHANNEL_QR_CODE = 5;
    //6：复制链接，
    public static final int CHANNEL_COPY_LINK = 6;
    //99：未知分享
    public static final int CHANNEL_UNDEF = 99;

    //获取数据的状态
    public static final int GET_DATA_OK = 1;
    public static final int GET_DATA_FAILED = 2;
    public static final int GET_DATA_PARSE_ERROR = 3;

    //微信分享判断使用
    public final static int FUNCTION_NORMAL_SHARE = 0x00;
    public final static int FUNCTION_UNLOCK_SHARE = 0x01;
    public final static int FUNCTION_REDBAG_SHARE = 0x02;
    public final static int FUNCTION_LIVE_SHARE = 0x03;
    /**
     * 具体的分享功能
     */
    public static int shareFunction = FUNCTION_NORMAL_SHARE;

    //scontent 分享种类， 0 ：用户信息分享，1：直播分享
    public final static int SCONTENT_USER_SHARE = 0;
    public final static int SCONTENT_LIVE_SHARE = 1;
    public static int scontent;

    //opt      0：默认， 1：分享解锁， 2：红包分享
    public final static int OPT_DEFAULT = 0;
    public final static int OPT_UNLOCK_SHARE = 1;
    public final static int OPT_LIVE_SHARE = 2;
    public static int opt;

    public static int channel;


    //1:图片  2: 文字
    public final static int SHARE_TYPE_PIC = 1;
    public final static int SHARE_TYPE_WEBLINK = 2;
    private int shareType;


    private ShareUtil() {

    }

    public static ShareUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (ShareUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ShareUtil();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * @param uid      是 string 用户的uid
     * @param callBack 获取通道回调
     * @group 是 int 1,其他分享 2,分享解锁 3,直播分享
     */
    public static void getShareChannels(String uid, String group, final GetChannelCallBack callBack) {
        final ArrayList<Integer> channels = new ArrayList<>();
        /**
         * uid 是 string 用户的uid
         * group 是 int 1,其他分享 2,分享解锁
         */
        HashMap<String, Object> postParam = new HashMap<>();
        postParam.put("uid", uid);
        postParam.put("group", group);

        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.reqGetShareChannel, postParam, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    try {
                        if (response.getResponseJson().has("res") && !response.getResponseJson().isNull("res")) {
                            JSONObject resJo = response.getResponseJson().getJSONObject("res");
                            JSONArray array = resJo.optJSONArray("counts");

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject jsonObject = array.getJSONObject(i);

                                int channel = jsonObject.getInt("channel");
                                int rest_time = jsonObject.getInt("rest_time");
                                if (channel <= 6 && rest_time > 0) {

                                    channels.add(channel);

                                }

                            }
                            if (callBack != null) {
                                callBack.channelBack(channels, GET_DATA_OK, "");
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (callBack != null) {
                            callBack.channelBack(channels, GET_DATA_PARSE_ERROR, "解析异常");
                        }
                    }

                    return;
                }
                if (callBack != null) {
                    callBack.channelBack(channels, GET_DATA_FAILED, response.getMsg());
                }
            }
        });
    }

    /**
     * 分享赚钱成功的回调
     *
     * @param scontent 分享种类， 0 ：用户信息分享，1：直播分享
     * @param opt      0：默认， 1：分享解锁， 2：红包分享
     * @param channel  分享方式：1:微信， 2:朋友圈， 3:QQ， 4:QQ空间
     * @param callBack 分享成功回调
     */
    public static void shareCodeCallBack(int scontent, int opt, final int channel, final ShareCodeCallBack callBack) {
        HashMap<String, Object> postParam = new HashMap<>();
        postParam.put("scontent", ShareUtil.scontent);
        postParam.put("opt", ShareUtil.opt);
        postParam.put("stype", ShareUtil.channel);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.shareCodeCallback2, postParam, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    JSONObject resJo = response.getResponseJson();
                    if (("ok").equals(resJo.optString("status"))) {
                        if (resJo.has("res") && resJo.opt("res") != null) {
                            JSONObject object = resJo.optJSONObject("res");
                            if (callBack != null) {
                                callBack.onShareCodeCallBackSuccess(object.optInt("pkg"), object.optInt("amount"), channel);
                            }
                            return;
                        }
                        if (callBack != null) {
                            callBack.onShareCodeCallBackFailed(response);
                        }
                    } else {
                        if (callBack != null) {
                            callBack.onShareCodeCallBackFailed(response);
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取分享素材
     */
    public static void getShareMatriData(GetShareMatrialCallBack callBack) {
        getShareMatriData("0", "0", "", "", "", callBack);
    }

    /**
     * 获取分享素材
     */
    public static void getShareMatriData(String otherId, String earn_money, final GetShareMatrialCallBack callBack) {
        getShareMatriData(otherId, earn_money, "", "", "", callBack);
    }

    /**
     * 获取分享素材
     *
     * @param earn_money
     * @param pic
     */
    public static void getShareMatriData(String otherUid, String earn_money, String share_code_type, String share_content_type, String pic, final GetShareMatrialCallBack callBack) {

        /**
         * self_uid //自己的uid
         share_code_type //分享链接类型 1分享用户 2分享直播  3解锁分享
         周隆 获取格式化分享的模板内容接口需要的参数
         share_content_type //分享类型 主页分享:"usermain".解锁分享:"unlock" ,主动推广分享:"active", 赚钱分享:"earn",直播分享:"live"
         pkg_name //包名
         ver //版本号
         earn_money //[opt]赚到的钱数，分为单位
         cover //[opt]直播封面url
         */
        //下载素材
        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        Map<String, Object> getParam = new HashMap<>();
        getParam.put("self_uid", userDetail.getUid());
        getParam.put("share_uid", otherUid);
        getParam.put("share_code_type", share_code_type);
        getParam.put("share_content_type", PSP.getInstance().getString(ModuleMgr.getCommonMgr().getPrivateKey(Constant.TEMP_SHARE_TYPE), CenterConstant.SHARE_TYPE_ACTIVE));
        getParam.put("pkg_name", ModuleMgr.getAppMgr().getPackageName());
        getParam.put("ver", ModuleMgr.getAppMgr().getVerCode());
        getParam.put("earn_money", earn_money);
        getParam.put("cover", pic);

        ModuleMgr.getHttpMgr().request(new RequestParam().setUrlParam(UrlParam.reqShareUrlV2).setGetParam(getParam)
                .setNeedEncrypt(false).setRequestCallback(new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {

                        List<ShareMatrial> list = null;
                        if (response.isOk()) {
                            try {
                                JSONObject resJo = response.getResponseJson().getJSONObject("res");
                                JSONArray arr = resJo.optJSONArray("shareMatrialList");
                                if (arr != null) {
                                    list = new ArrayList<ShareMatrial>();

                                    for (int i = 0; i < arr.length(); i++) {

                                        JSONObject jsonObject = arr.getJSONObject(i);
                                        ShareMatrial bean = new ShareMatrial();
                                        bean.parseJson(jsonObject);
                                        list.add(bean);
                                    }

                                    if (callBack != null) {
                                        callBack.shareMatrialCallBack(list, GET_DATA_OK, "");
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();

                                if (callBack != null) {
                                    callBack.shareMatrialCallBack(list, GET_DATA_PARSE_ERROR, "解析异常");
                                }
                            }
                            return;
                        }

                        if (callBack != null) {
                            callBack.shareMatrialCallBack(list, GET_DATA_FAILED, response.getMsg());
                        }

                    }
                }));
    }

    private static boolean isCanShare = true;    //目前只用于打卡红包解锁
    private static Calendar lastCallTime = null; //目前只用于打卡红包解锁

    /**
     * 设置是否可以分享
     *
     * @param isCanShare
     */
    public static void setIsCanShare(boolean isCanShare) {
        ShareUtil.isCanShare = isCanShare;
        if (!isCanShare) {
            lastCallTime = Calendar.getInstance();
        }
    }

    /**
     * ShareUtil.shareGapTimeOut(false, {@link Constant#SHARE_GAP_TIME});
     *
     * @param tm 单位毫秒
     * @return
     */
    public static boolean shareGapTimeOut(long tm) {
        boolean bool = false;
        if (isCanShare) {
            bool = true;
        } else {
            Calendar currCalendar = Calendar.getInstance();
            if (lastCallTime != null) {
                if (currCalendar.getTime().getTime() - lastCallTime.getTime().getTime() > tm) {
                    bool = true;
                }
            } else {
                bool = true;
            }
            if (bool) {
                lastCallTime = Calendar.getInstance();
            }
        }
        return bool;
    }

    public void setQQContext(QQShareActivity qqShareActivity) {

    }

    public interface GetChannelCallBack {
        /**
         * 获取通道的分享渠道，无可用渠道或分享不成功时反回size为0的list
         *
         * @param channels 可分享的渠道f
         */
        void channelBack(ArrayList<Integer> channels, int flag, String msg);
    }

    public interface GetShareMatrialCallBack {
        /**
         * 获取分享素材
         */
        void shareMatrialCallBack(List<ShareMatrial> list, int flag, String msg);
    }

    public interface ShareCodeCallBack {
        /**
         * 获取通道的分享渠道，无可用渠道或分享不成功时反回size为0的list
         *
         * @param pkg     //0：未产生红包， 1 红包放入背包，2：红包放入钱包
         * @param channel 分享的渠道
         * @amount
         */
        void onShareCodeCallBackSuccess(int pkg, float amount, int channel);

        /**
         * 获取通道的分享渠道，无可用渠道或分享不成功时反回size为0的list
         *
         * @param response 所有返回信息
         */
        void onShareCodeCallBackFailed(HttpResponse response);
    }

    /**
     * 保存网络图片到本地
     */
    public static void saveImageToLocal(String imageUrl, SaveImageToLocalCallBack callBack) {
        String filePath = DirType.getCacheDir() + System.nanoTime() + ".jpg";
        ModuleMgr.getHttpMgr().download(imageUrl, filePath, callBack);
    }

    /**
     * 保存网络图片到本地的回调
     */
    public static abstract class SaveImageToLocalCallBack implements DownloadListener {
        @Override
        public void onStart(String url, String filePath) {

        }

        @Override
        public void onProcess(String url, int process, long size) {

        }

        @Override
        public void onFail(String url, Throwable throwable) {
            PToast.showShort("下载素材失败，请重新获取");
        }
    }

    /**
     * @param shareFunction {@linkplain ShareUtil#FUNCTION_REDBAG_SHARE 红包分享}  {@linkplain ShareUtil#FUNCTION_UNLOCK_SHARE 解锁聊天}
     * @param channel       分享渠道
     * @param preViewImgUrl 图片Url
     * @param activity      activity
     * @param qqCallBack    qq分享回调
     */
    public static void shareImage(final int shareFunction, final int channel, final String preViewImgUrl, final Activity activity, final ShareUtils.CallBack qqCallBack) {
        saveImageToLocal(preViewImgUrl, new SaveImageToLocalCallBack() {
            @Override
            public void onSuccess(String url, String filePath) {
                switch (channel) {
                    case ShareUtil.CHANNEL_QQ_FRIEND:
                        ShareUtils.shareImageToQQ(activity, filePath, qqCallBack);
                        break;
                    case ShareUtil.CHANNEL_QQ_ZONE:
                        ArrayList<String> imageList = new ArrayList<>();
                        imageList.add(filePath);
                        ShareUtils.uploadImageToQQZone(activity, "", imageList, qqCallBack);
                        break;
                    case ShareUtil.CHANNEL_WX_FRIEND:
                        ShareUtil.shareFunction = shareFunction;
                        ShareUtil.channel = ShareUtil.CHANNEL_WX_FRIEND;
                        ShareUtils.sendImageToWechat(filePath, ShareUtils.WECHAT_FRIEND);
                        break;
                    case ShareUtil.CHANNEL_WX_FRIEND_CRICLE:
                        ShareUtil.shareFunction = shareFunction;
                        ShareUtil.channel = ShareUtil.CHANNEL_WX_FRIEND_CRICLE;
                        ShareUtils.sendImageToWechat(filePath, ShareUtils.WECHAT_FRIEND_CIRCLE);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * @param activity activity
     */
    public void shareWebLink(Activity activity, String londingPageUrl, String title, String content, String iconUri, QQShareCallBack callBack) {
        switch (channel) {
            //分享给微信好友
            case ShareUtil.CHANNEL_WX_FRIEND:
                ShareUtils.sendWebpageToWechat(londingPageUrl, title, content, iconUri, ShareUtils.WECHAT_FRIEND);
                break;
            //分享到朋友圈
            case ShareUtil.CHANNEL_WX_FRIEND_CRICLE:
                ShareUtils.sendWebpageToWechat(londingPageUrl, title, content, iconUri, ShareUtils.WECHAT_FRIEND_CIRCLE);
                break;

            //分享给QQ好友
            case ShareUtil.CHANNEL_QQ_FRIEND:
                ShareUtils.shareToQQ(activity, title, content, londingPageUrl, iconUri, callBack);
                break;

            //分享到QQ空间
            case ShareUtil.CHANNEL_QQ_ZONE:
                ArrayList<String> imageList = new ArrayList<>();
                imageList.add(iconUri);
                ShareUtils.shareToQQZone(activity, title, content, londingPageUrl, imageList, callBack);
                break;
            default:
                break;
        }
    }

    public void share(Activity activity, ShareMatrial shareData, int channel, int opt, int scontent, ShareCodeCallBack callBack) {
        if (isNullParam(shareData)) {
            return;
        }
        mShareCallBack = new MyShareCallBack(callBack);
        mShareCallBack.setChannel(channel);
        mShareCallBack.setOpt(opt);
        mShareCallBack.setScontent(scontent);
        mShareCallBack.setData(shareData);

        if (channel == CHANNEL_QQ_FRIEND || channel == CHANNEL_QQ_ZONE) {
            UIShow.showQQShareActivity(activity);
            return;
        }

        share(activity);
    }

    public void share(Activity activity) {
        if (mShareCallBack != null) {
            ShareMatrial data = mShareCallBack.getData();
            switch (data.getMatrialType()) {
                case SHARE_TYPE_PIC:
                    shareImage(0, mShareCallBack.getChannel(), data.getPreview_img(), activity, mShareCallBack);
                    break;
                case SHARE_TYPE_WEBLINK:
                    shareWebLink(activity, data.getLandingPageUrl(), data.getShareContentTitle(), data.getShareContentSubTitle(), data.getShareContentIcon(), mShareCallBack);
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isNullParam(ShareMatrial shareData) {
        if (shareData == null) {
            return true;
        }
        if (shareData.getMatrialType() == SHARE_TYPE_PIC) {
            return TextUtils.isEmpty(shareData.getPreview_img());
        } else if (shareData.getMatrialType() == SHARE_TYPE_WEBLINK) {
            boolean isNull = TextUtils.isEmpty(shareData.getLandingPageUrl())
                    || TextUtils.isEmpty(shareData.getShareContentTitle())
                    || TextUtils.isEmpty(shareData.getShareContentSubTitle())
                    || TextUtils.isEmpty(shareData.getShareContentIcon());
            return isNull;
        }
        return true;
    }

    public void onQQResult(final int requestCode, final int resultCode, final Intent data) {
        MsgMgr.getInstance().delay(new Runnable() {
            @Override
            public void run() {
                if (App.getActivity() instanceof QQShareActivity) {
                    onQQResult(requestCode, resultCode, data);
                    return;
                }
                if (mShareCallBack != null) {
                    Tencent.onActivityResultData(requestCode, resultCode, data, mShareCallBack);
                }
            }
        }, 200);
    }

    public void onWXResult(final int errCode){
        MsgMgr.getInstance().delay(new Runnable() {
            @Override
            public void run() {
                if (App.getActivity().getClass().getSimpleName().equals(WXEntryActivity.class.getSimpleName())) {
                    onWXResult(errCode);
                    return;
                }
                if (mShareCallBack != null) {
                    switch (errCode) {
                        case BaseResp.ErrCode.ERR_OK:
                            mShareCallBack.onSuccess(null);
                            break;
                        case BaseResp.ErrCode.ERR_USER_CANCEL:
                            mShareCallBack.onCancel();
                            break;
                        case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        case BaseResp.ErrCode.ERR_UNSUPPORT:
                            mShareCallBack.onError(null);
                        default:
                            break;
                    }
                }
            }
        }, 200);
    }

    class MyShareCallBack extends QQShareCallBack {
        private ShareMatrial shareData;

        public MyShareCallBack(ShareCodeCallBack callBack) {
            super(callBack);
        }

        public void setData(ShareMatrial shareData) {
            this.shareData = shareData;
        }

        public ShareMatrial getData() {
            return shareData;
        }
    }
}
