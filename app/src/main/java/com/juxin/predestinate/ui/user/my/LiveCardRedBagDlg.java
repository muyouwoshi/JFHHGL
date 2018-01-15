package com.juxin.predestinate.ui.user.my;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.ShareUtil;
import com.juxin.predestinate.module.util.TimerUtil;
import com.juxin.predestinate.ui.live.bean.ShareMatrial;
import com.juxin.predestinate.ui.mail.unlock.ShareChanelData;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.tangzi.sharelibrary.utils.ShareUtils;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.third.wa5.sdk.common.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2017/11/7
 * 描述:打卡红包弹框
 *
 * @author :zm
 */
public class LiveCardRedBagDlg extends BaseDialogFragment implements View.OnClickListener, ShareUtil.GetShareMatrialCallBack {

    private ImageView ivClose;
    private TextView tvState, tvSucceedMoney, tvFailure, tvInitOne, tvInitTwo, tvBottomSucceed, tvBottomInitGo, tvBottomSucceedOk, tvBottomInitBefor;
    private LinearLayout ll_succeed_money, llRandom;

    private int redType;
    public int id;
    /**
     * 分享渠道类型集合
     */
    private List<Integer> channels;
    private ShareChanelData shareChanelData;
    private Integer channelId[] = new Integer[]{ShareChanelData.CHANEL_WXQ, ShareChanelData.CHANEL_WX, ShareChanelData.CHANEL_QQK, ShareChanelData.CHANEL_QQ};
    private double money;

    public LiveCardRedBagDlg() {
        settWindowAnimations(R.style.AnimScaleInScaleOutOverShoot);
        setGravity(Gravity.CENTER);
        setDialogSizeRatio(1, 0);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.live_share_redbag_dlg);
        View view = getContentView();
        initView(view);
        return view;
    }

    /**
     * 红包类型
     *
     * @param redType 1 初始化， 2 分享成功， 3 分享失败
     */
    public void setData(int redType, double money) {
        this.redType = redType;
        this.money = money;
    }

    private void initView(View view) {
        ivClose = view.findViewById(R.id.live_share_iv_close);
        tvState = view.findViewById(R.id.live_share_tv_state);
        tvSucceedMoney = view.findViewById(R.id.live_share_tv_succeed_money);
        tvFailure = view.findViewById(R.id.live_share_tv_failure);
        tvInitOne = view.findViewById(R.id.live_share_tv_init_one);
        tvInitTwo = view.findViewById(R.id.live_share_tv_init_two);
        tvBottomInitGo = view.findViewById(R.id.live_share_tv_bottom_init_go);
        tvBottomSucceed = view.findViewById(R.id.live_share_tv_bottom_succeed);
        tvBottomSucceedOk = view.findViewById(R.id.live_share_tv_bottom_succeed_ok);
        tvBottomInitBefor = view.findViewById(R.id.live_share_tv_bottom_init_befor);
        ll_succeed_money = view.findViewById(R.id.live_share_ll_succeed_money);
        llRandom = view.findViewById(R.id.live_share_ll_random);

        ivClose.setOnClickListener(this);
        tvBottomInitGo.setOnClickListener(this);
        tvBottomSucceedOk.setOnClickListener(this);
        setTitle();
    }

    private void viewGone() {
        llRandom.setVisibility(View.GONE);
        tvFailure.setVisibility(View.GONE);
        tvInitOne.setVisibility(View.GONE);
        tvInitTwo.setVisibility(View.GONE);
        tvBottomSucceed.setVisibility(View.GONE);
        tvBottomInitGo.setVisibility(View.GONE);
        tvBottomSucceedOk.setVisibility(View.GONE);
        tvBottomInitBefor.setVisibility(View.GONE);
        ll_succeed_money.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void setTitle() {
        viewGone();
        switch (redType) {
            case CenterConstant.TYPE_INIT:
                tvInitOne.setVisibility(View.VISIBLE);
                tvInitTwo.setVisibility(View.VISIBLE);
                tvBottomInitGo.setVisibility(View.VISIBLE);
                tvBottomInitBefor.setVisibility(View.VISIBLE);
                tvBottomInitGo.setText(R.string.go_share);
                tvState.setText(R.string.live_redbag_init);
                tvBottomInitBefor.setText(R.string.redbag_limited);
                break;
            case CenterConstant.TYPE_SUCCEED_WOMAN:
                ll_succeed_money.setVisibility(View.VISIBLE);
                tvBottomSucceed.setVisibility(View.VISIBLE);
                tvBottomSucceedOk.setVisibility(View.VISIBLE);
                tvState.setText(R.string.live_redbag_succeed);
                tvSucceedMoney.setText(money + "");
                break;
            case CenterConstant.TYPE_FAILURE:
                tvFailure.setVisibility(View.VISIBLE);
                tvBottomInitGo.setVisibility(View.VISIBLE);
                tvBottomInitBefor.setVisibility(View.VISIBLE);
                tvBottomInitGo.setText(R.string.re_share);
                tvState.setText(R.string.live_redbag_failure);
                tvBottomInitBefor.setText(R.string.redbag_limited);
                break;
            case CenterConstant.TYPE_SUCCEED:
                llRandom.setVisibility(View.VISIBLE);
                tvBottomInitGo.setVisibility(View.VISIBLE);
                tvBottomInitBefor.setVisibility(View.VISIBLE);
                tvBottomInitGo.setText(R.string.into_backpack);
                tvState.setText(R.string.live_get_redbag);
                tvBottomInitBefor.setText(R.string.share_get_redbag);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_share_iv_close:
            case R.id.live_share_tv_bottom_succeed_ok:
                StatisticsShareUnlock.onAlert_shareresult_ok();
                dismissAllowingStateLoss();
                break;
            case R.id.live_share_tv_bottom_init_go:
                if (!NetUtil.getInstance().isNetConnect(getContext())) {
                    PToast.showShort(getContext().getString(R.string.tip_net_error));
                    return;
                }
                switch (redType) {
                    case CenterConstant.TYPE_INIT:
                    case CenterConstant.TYPE_FAILURE:
                        if (redType == CenterConstant.TYPE_INIT) {
                            StatisticsShareUnlock.onAlert_dayredpacketcard_share();
                        } else if (redType == CenterConstant.TYPE_FAILURE) {
                            StatisticsShareUnlock.onAlert_shareresult_reshare();
                        }
                        LoadingDialog.show(getActivity());
                        ModuleMgr.getCommonMgr().reqGetShareChannel(ModuleMgr.getCenterMgr().getMyInfo().uid, 1, new RequestComplete() {
                            @Override
                            public void onRequestComplete(HttpResponse response) {
                                if (response.isOk()) {
                                    shareChanelData = new ShareChanelData();
                                    shareChanelData.parseJson(response.getResponseString());
                                    if (shareChanelData != null) {
                                        for (int i = 0; i < channelId.length; i++) {
                                            ShareChanelData chanelData = shareChanelData.getChanelData(channelId[i]);
                                            if (chanelData.getChannel() != 0) {
                                                PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.TEMP_SHARE_TYPE), CenterConstant.SHARE_TYPE_DAKA);
                                                ShareUtil.getShareMatriData(null, null, "1", CenterConstant.SHARE_TYPE_DAKA, null, LiveCardRedBagDlg.this);
                                                id = channelId[i];
                                                break;
                                            }
                                        }
                                        if (id == 0) {
                                            LoadingDialog.closeLoadingDialog();
                                        }
                                    } else {
                                        LoadingDialog.closeLoadingDialog();
                                        PToast.showShort("没有可分享的渠道");
                                    }
                                } else {
                                    LoadingDialog.closeLoadingDialog();
                                    PToast.showShort(response.getMsg());
                                }
                            }
                        });
                        break;
                    default:
                        break;
                }
                dismissAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        ShareUtil.setIsCanShare(true);
        if (redType == CenterConstant.TYPE_INIT) {
            StatisticsShareUnlock.onAlert_dayredpacketcard_close();
        }
        super.onDismiss(dialog);
    }

    @Override
    public void shareMatrialCallBack(List<ShareMatrial> list, int flag, String msg) {
        LoadingDialog.closeLoadingDialog();
        if (list != null && !list.isEmpty()) {
            ShareUtil.channel = id;
            ShareMatrial bean = list.get(0);
//            bean.setMatrialType(1);
            if (bean.getMatrialType() == 2) { //分享链接
                switch (id) {
                    //分享给微信好友
                    case ShareUtil.CHANNEL_WX_FRIEND:
                        ShareUtil.shareFunction = ShareUtil.FUNCTION_REDBAG_SHARE;
                        ShareUtils.sendWebpageToWechat(bean.getLandingPageUrl(), bean.getShareContentTitle(), bean.getShareContentSubTitle(), bean.getShareContentIcon(), ShareUtils.WECHAT_FRIEND);
                        break;
                    //分享到朋友圈
                    case ShareUtil.CHANNEL_WX_FRIEND_CRICLE:
                        ShareUtil.shareFunction = ShareUtil.FUNCTION_REDBAG_SHARE;
                        ShareUtils.sendWebpageToWechat(bean.getLandingPageUrl(), bean.getShareContentTitle(), bean.getShareContentSubTitle(), bean.getShareContentIcon(), ShareUtils.WECHAT_FRIEND_CIRCLE);
                        break;

                    //分享给QQ好友
                    case ShareUtil.CHANNEL_QQ_FRIEND:
                        ShareUtils.shareToQQ(App.activity, bean.getShareContentTitle(), bean.getShareContentSubTitle(), bean.getLandingPageUrl(), bean.getShareContentIcon(), new ShareCallback(id));
                        break;

                    //分享到QQ空间
                    case ShareUtil.CHANNEL_QQ_ZONE:
                        ArrayList<String> imageList = new ArrayList<>();
                        imageList.add(bean.getShareContentIcon());
                        ShareUtils.shareToQQZone(App.activity, bean.getShareContentTitle(), bean.getShareContentSubTitle(), bean.getLandingPageUrl(), imageList, new ShareCallback(id));
                        break;
                }
            } else if (bean.getMatrialType() == 1) { //分享图片
                ShareUtil.shareImage(ShareUtil.FUNCTION_REDBAG_SHARE, id, bean.getPreview_img(), App.activity, new ShareCallback(id));
            }
        }
    }

    public static class ShareCallback implements ShareUtils.CallBack, IUiListener {
        private int channel;

        public ShareCallback(int shareChannel) {
            channel = shareChannel;
        }

        @Override
        public void onComplete(Object o) {
            onSuccess(o);
        }

        @Override
        public void onError(UiError uiError) {
            ShareUtil.setIsCanShare(false);
//            error();
        }

        @Override
        public void onSuccess(Object o) {
            ShareUtil.setIsCanShare(false);
            MsgMgr.getInstance().sendMsg(MsgType.MT_SHARE_SUCCEED, channel);
        }

        @Override
        public void onCancel() {
            error();
        }

        private void error() {
            ShareUtil.setIsCanShare(false);
            MsgMgr.getInstance().sendMsg(MsgType.MT_SHARE_FAILURE, 0);
        }
    }
}
