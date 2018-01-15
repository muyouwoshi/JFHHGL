package com.juxin.predestinate.ui.share;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.ChineseFilter;
import com.juxin.predestinate.module.util.ShareUtil;
import com.juxin.predestinate.ui.mail.unlock.UnlockComm;
import com.tangzi.sharelibrary.utils.ShareUtils;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 类描述：分享弹窗 微信的回调在{@link com.juxin.predestinate.wxapi.WXEntryActivity} <b/>
 * QQ的回调需要在弹窗的Activity中onActivityResult（）方法中处理<b/>
 *
 * @see com.juxin.predestinate.ui.user.my.ShareCodeAct#onActivityResult(int, int, Intent)
 * 创建时间：2017/10/27 11:29
 * 修改时间：2017/10/27 11:29
 * Created by zhoujie on 2017/10/27
 * 修改备注：
 */
public class ShareDialog extends BaseDialogFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private EarnMoneyQQShareCallBack callBack;
    private OnWebShareListener mShareListener;
    /**
     * 分享渠道类型集合 如{@link ShareUtil#CHANNEL_WX_FRIEND}{@link ShareUtil#CHANNEL_WX_FRIEND_CRICLE}{@link ShareUtil#CHANNEL_QQ_FRIEND}
     */
    List<Integer> channels;
    /**
     * 展示分享的渠道的GridView
     */
    GridView channelsView;
    /**
     * 标题
     */
    private String shareTitle = "";
    /**
     * 内容简介
     */
    private String shareContent = "";
    /**
     * 本地图片路径，或者网络图片路径
     */
    private String shareImageUri = "";
    /**
     * 分享链接
     */
    private String shareURL = "";
    /**
     * 分享出去的网页连接消息显示的小图片的本地路径或者网络路径
     */
    private String shareContentImageUri = "";
    /**
     * 分享码
     */
    private String shareCode;
    /**
     * 被分享人
     */
    private int uid;
    /**
     * 模版id
     */
    private int mould;

    public ShareDialog() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(0.9, 0);
        callBack = new EarnMoneyQQShareCallBack();
    }

    public void setSharedFilePath(String filePath) {
        if (filePath != null) {
            shareImageUri = filePath;
        }
    }

    public void setShareLink(String shareLink) {
        if (shareLink != null) {
            shareURL = shareLink;
        }
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public void setShareContentImageUri(String shareContentImageUri) {
        this.shareContentImageUri = shareContentImageUri;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    public void setMould(int mould) {
        this.mould = mould;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setShareChannels(List<Integer> channels) {
        this.channels = channels;
        initData();
    }

    public void setShareListener(OnWebShareListener mShareListener) {
        this.mShareListener = mShareListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.spread_share_dialog);
        initView();
        initData();
        return getContentView();
    }

    private void initView() {
        findViewById(R.id.close_iv).setOnClickListener(this);
        channelsView = (GridView) findViewById(R.id.channels_grid_view);
    }

    private void initData() {
        if (channels != null && channelsView != null) {
            int channelsCount = channels.size();
            if (channelsCount < 4) {
                channelsView.setNumColumns(channelsCount);
            } else if (channelsCount % 3 == 0 && channelsCount % 4 != 0) {
                channelsView.setNumColumns(3);
            }
            channelsView.setAdapter(new ShareDialogChannelsAdapter(getContext(), channels));
            channelsView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_iv:
                if (mShareListener == null) {
                    StatisticsShareUnlock.onAlert_sharecard_close();
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * 检查本地保存的图片是否存在
     *
     * @return true存在，false不存在
     */
    protected boolean checkFileExist() {
        if (new File(shareImageUri).exists()) {
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int channelType = channels.get(position);
        callBack.setChannel(channelType);
        startShare(channelType);
        switch (channels.get(position)) {
            //显示图片保存的路径
            case ShareUtil.CHANNEL_QR_CODE:
                if (!checkFileExist()) {
                    PToast.showShort(getString(R.string.spread_share_save_error));
                    return;
                }
                PToast.showShort(getString(R.string.spread_share_save_path) + shareImageUri);
                dismiss();
                break;

            //分享给微信好友
            case ShareUtil.CHANNEL_WX_FRIEND:
                ShareUtil.channel = ShareUtil.CHANNEL_WX_FRIEND;
                // shareImageUrl分享图片, 支持网络图片，本地图片，res图片，asset图片
                // ShareUtils.WECHAT_FRIEND 分享给好友
                // ShareUtils.WECHAT_FRIEND_CIRCLE 分享给朋友圈
                // ShareUtils.WECHAT_FAVORITE 添加到微信收藏
                if (mShareListener != null) {
                    mShareListener.sendToWechatFriend();
                    dismiss();
                    return;
                }

                // shareImageUrl分享图片, 支持网络图片，本地图片，res图片，asset图片
                // ShareUtils.WECHAT_FRIEND 分享给好友
                // ShareUtils.WECHAT_FRIEND_CIRCLE 分享给朋友圈
                // ShareUtils.WECHAT_FAVORITE 添加到微信收藏
                ShareUtils.sendWebpageToWechat(shareURL, shareTitle, shareContent, shareContentImageUri, ShareUtils.WECHAT_FRIEND);
                dismiss();
                break;

            //分享到朋友圈
            case ShareUtil.CHANNEL_WX_FRIEND_CRICLE:
                ShareUtil.channel = ShareUtil.CHANNEL_WX_FRIEND_CRICLE;
                if (mShareListener != null) {
                    mShareListener.sendToWechatFriendCricle();
                    dismiss();
                    return;
                }
                if (!checkFileExist()) {
                    PToast.showShort(getString(R.string.spread_share_save_error));
                    return;
                }
                ShareUtils.sendImageToWechat(shareImageUri, ShareUtils.WECHAT_FRIEND_CIRCLE);
                dismiss();
                break;

            //分享给QQ好友
            case ShareUtil.CHANNEL_QQ_FRIEND:
                ShareUtil.channel = ShareUtil.CHANNEL_QQ_FRIEND;
                if (mShareListener != null) {
                    mShareListener.sendToQQFriend();
                    dismiss();
                    return;
                }
                ShareUtils.shareToQQ(getActivity(), shareTitle, shareContent, shareURL, shareContentImageUri, callBack);
                dismiss();
                break;

            //分享到QQ空间
            case ShareUtil.CHANNEL_QQ_ZONE:
                ShareUtil.channel = ShareUtil.CHANNEL_QQ_ZONE;
                if (mShareListener != null) {
                    mShareListener.sendToQQZone();
                    dismiss();
                    return;
                }

                if (!checkFileExist()) {
                    PToast.showShort(getString(R.string.spread_share_save_error));
                    return;
                }
                ArrayList<String> imageList = new ArrayList<>();
                imageList.add(shareImageUri);
                ShareUtils.uploadImageToQQZone(getActivity(), shareContent, imageList, callBack);
                dismiss();
                break;

            //复制链接
            case ShareUtil.CHANNEL_COPY_LINK:
                ChineseFilter.copyString(getActivity(), shareURL);
                dismiss();
                break;

            default:
                break;
        }
        dismiss();
    }

    private void startShare(int channeType) {
        if (mShareListener == null) {
            String source = SourcePoint.getInstance().getShareSource();
            StatisticsShareUnlock.onAlert_sharecard_click(source, channeType, mould, shareURL);
        }
        HashMap<String, Object> postParam = new HashMap<>();
        postParam.put("stype", channeType);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.startShare, postParam, new RequestComplete() {

            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    JSONObject resJo = response.getResponseJson();
                    if (("ok").equals(resJo.optString("status"))) {
                        long time = resJo.optLong("tm");
                        PLogger.d("" + time);
                    }
                }
            }
        });
    }

    public interface OnWebShareListener {
        void sendToWechatFriend();

        void sendToWechatFriendCricle();

        void sendToQQFriend();

        void sendToQQZone();
    }

}
