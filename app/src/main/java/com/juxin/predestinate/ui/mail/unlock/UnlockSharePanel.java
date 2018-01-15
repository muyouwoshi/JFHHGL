package com.juxin.predestinate.ui.mail.unlock;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.baseui.WebActivity;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.ShareUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.live.bean.ShareMatrial;
import com.juxin.predestinate.ui.share.QQShareCallBack;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.tangzi.sharelibrary.utils.ShareUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：聊天解锁分享解锁的Panel
 * 创建时间：2017/11/1 9:48
 * 修改时间：2017/11/1 9:48
 * Created by zhoujie on 2017/11/1
 * 修改备注：
 */

public class UnlockSharePanel extends BasePanel implements View.OnClickListener {
    private QQShareCallBack mShareCallBack;
    private long toUid;
    private String remindStr, shareURL, shareTitle, shareContent, shareContentImageUri;
    private ShareMatrial shareMatrial;
    private ShareChanelData shareChanelData;
    private UnlockMsgDlg.OnUnlockDlgMsg unlockDlgMsg;

    private TextView wxShareText, wxCricleShareText, qqShareText, qqZoneShareText, awardTextOne, awardTextTwo, remindtv;
    private LinearLayout btn_wxfirend, btn_wx_quan, btn_qq_firend, btn_qq_space, btn_wxfirend_big,
            btn_wx_quan_big, btn_no, btn_qq_firend_big, btn_qq_space_big;


    public UnlockSharePanel(Context context, UnlockMsgDlg.OnUnlockDlgMsg onUnlockDlgMsg) {
        super(context);
        setContentView(R.layout.spread_share_chat_unlock_dialog_share_item);
        unlockDlgMsg = onUnlockDlgMsg;
        initView();
        initData();
        showShare(0, 0, 0, 0);
    }

    public void setToUid(Long toUid) {
        this.toUid = toUid;
    }

    private void initData() {
        LoadingDialog.show((FragmentActivity) getContext());
        ModuleMgr.getCommonMgr().reqGetShareChannel(ModuleMgr.getCenterMgr().getMyInfo().uid, 2, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                LoadingDialog.closeLoadingDialog();
                if (response.isOk()) {
                    shareChanelData = new ShareChanelData();
                    shareChanelData.parseJson(response.getResponseString());
                    setData();
                }
            }
        });
        PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.TEMP_SHARE_TYPE), CenterConstant.SHARE_TYPE_UNLOCK);
        ShareUtil.getShareMatriData(String.valueOf(toUid), "", "3", CenterConstant.SHARE_TYPE_UNLOCK, "", new ShareUtil.GetShareMatrialCallBack() {
            @Override
            public void shareMatrialCallBack(List<ShareMatrial> list, int flag, String msg) {
                if (list != null && !list.isEmpty()) {
                    shareMatrial = list.get(0);
                }
            }
        });

        mShareCallBack = new UnlockShareQQShareCallBack();
    }

    private void initView() {
        remindtv = (TextView) findViewById(R.id.remind_tv);
        awardTextOne = (TextView) findViewById(R.id.share_award_tv_one);
        awardTextTwo = (TextView) findViewById(R.id.share_award_tv_two);
        remindStr = getContext().getString(R.string.share_chat_unlock_dialog_share_item_remind);

        findViewById(R.id.weixinhaoy).setOnClickListener(this);
        findViewById(R.id.pengyouquan).setOnClickListener(this);
        findViewById(R.id.qq).setOnClickListener(this);
        findViewById(R.id.qqzone).setOnClickListener(this);

        wxShareText = (TextView) findViewById(R.id.weixin_share_count_tv);
        wxCricleShareText = (TextView) findViewById(R.id.penyouquan_share_count_tv);
        qqShareText = (TextView) findViewById(R.id.qq_share_count_tv);
        qqZoneShareText = (TextView) findViewById(R.id.qqzone_share_count_tv);
        btn_wxfirend = (LinearLayout) findViewById(R.id.spread_share_weixinfirend);
        btn_wxfirend_big = (LinearLayout) findViewById(R.id.spread_share_weixinfirend_big);
        btn_wx_quan = (LinearLayout) findViewById(R.id.spread_share_weixinpengyouquan);
        btn_wx_quan_big = (LinearLayout) findViewById(R.id.spread_share_weixinpengyouquan_big);
        btn_qq_firend = (LinearLayout) findViewById(R.id.spread_share_qqfirend);
        btn_qq_space = (LinearLayout) findViewById(R.id.spread_share_qqspace);
        btn_no = (LinearLayout) findViewById(R.id.spread_share_no);
        btn_qq_firend_big = (LinearLayout) findViewById(R.id.live_share_qq_firend_big);
        btn_qq_space_big = (LinearLayout) findViewById(R.id.live_share_qq_space_big);

        btn_wx_quan_big.setOnClickListener(this);
        btn_wxfirend_big.setOnClickListener(this);
        btn_qq_firend_big.setOnClickListener(this);
        btn_qq_space_big.setOnClickListener(this);
        btn_no.setOnClickListener(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            remindtv.setText(Html.fromHtml(remindStr, Html.FROM_HTML_MODE_LEGACY));
        } else {
            remindtv.setText(Html.fromHtml(remindStr));
        }
    }

    private void setData() {
        try {
            if (shareChanelData == null) {
                return;
            }
            showShare(shareChanelData.qq(), shareChanelData.qqK(), shareChanelData.wxFirend(), shareChanelData.wxCircle());

            if (wxShareText != null) {
                wxShareText.setText(shareChanelData.getChanelData(ShareChanelData.CHANEL_WX).getText());
            }
            if (wxCricleShareText != null) {
                wxCricleShareText.setText(shareChanelData.getChanelData(ShareChanelData.CHANEL_WXQ).getText());
            }
            if (qqShareText != null) {
                qqShareText.setText(shareChanelData.getChanelData(ShareChanelData.CHANEL_QQ).getText());
            }
            if (qqZoneShareText != null) {
                qqZoneShareText.setText(shareChanelData.getChanelData(ShareChanelData.CHANEL_QQK).getText());
            }

            String textOne = "1、" + shareChanelData.getContentList().get(0);
            String textTwo = "2、" + shareChanelData.getContentList().get(1);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                remindtv.setText(Html.fromHtml(remindStr, Html.FROM_HTML_MODE_LEGACY));
                awardTextOne.setText(Html.fromHtml(textOne, Html.FROM_HTML_MODE_LEGACY));
                awardTextTwo.setText(Html.fromHtml(textTwo, Html.FROM_HTML_MODE_LEGACY));
            } else {
                remindtv.setText(Html.fromHtml(remindStr));
                awardTextOne.setText(Html.fromHtml(textOne));
                awardTextTwo.setText(Html.fromHtml(textTwo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化界面，取值1，0
     *
     * @param QQ
     * @param QQSpace
     * @param WX
     * @param WXSpace
     */
    private void showShare(int QQ, int QQSpace, int WX, int WXSpace) {
        btn_wxfirend.setVisibility(View.GONE);
        btn_wxfirend_big.setVisibility(View.GONE);
        btn_wx_quan.setVisibility(View.GONE);
        btn_wx_quan_big.setVisibility(View.GONE);
        btn_qq_firend.setVisibility(View.GONE);
        btn_qq_space.setVisibility(View.GONE);
        btn_qq_firend_big.setVisibility(View.GONE);
        btn_qq_space_big.setVisibility(View.GONE);
        btn_no.setVisibility(View.GONE);

        if ((QQ + QQSpace + WX + WXSpace) == 0) {
            btn_no.setVisibility(View.VISIBLE);
        }

        if ((QQ + QQSpace + WX + WXSpace) == 1) {
            if (QQ == 1) {
                btn_qq_firend_big.setVisibility(View.VISIBLE);
                return;
            }
            if (QQSpace == 1) {
                btn_qq_space_big.setVisibility(View.VISIBLE);
                return;
            }
            if (WX == 1) {
                btn_wxfirend_big.setVisibility(View.VISIBLE);
                return;
            }
            if (WXSpace == 1) {
                btn_wx_quan_big.setVisibility(View.VISIBLE);
                return;
            }
        }

        if (QQ == 1) {
            btn_qq_firend.setVisibility(View.VISIBLE);
        }
        if (QQSpace == 1) {
            btn_qq_space.setVisibility(View.VISIBLE);
        }
        if (WX == 1) {
            btn_wxfirend.setVisibility(View.VISIBLE);
        }
        if (WXSpace == 1) {
            btn_wx_quan.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onClick(View v) {
        ShareUtil.shareFunction = ShareUtil.FUNCTION_UNLOCK_SHARE;
        ShareUtil.scontent = ShareUtil.SCONTENT_USER_SHARE;
        ShareUtil.opt = ShareUtil.OPT_UNLOCK_SHARE;
        StatisticsShareUnlock.onAlert_unlockchat_share();
        switch (v.getId()) {
            case R.id.qq:
            case R.id.live_share_qq_firend_big:
                if (matrialIsNull()) {
                    PToast.showShort(context.getString(R.string.share_link_fail));
                    return;
                }
                if(shareMatrial.getMatrialType() == ShareMatrial.SHARE_TYPE_PIC) {
                    ShareUtil.shareImage(ShareUtil.FUNCTION_UNLOCK_SHARE, ShareUtil.CHANNEL_QQ_FRIEND, shareMatrial.getPreview_img(), (Activity) getContext(), new UnlockShareQQShareCallBack());
                } else {
                    ShareUtils.shareToQQ((Activity) context, shareTitle, shareContent, shareURL, shareContentImageUri, new UnlockShareQQShareCallBack());
                }
                unlockDlgMsg.onDismiss();
                break;

            case R.id.qqzone:
            case R.id.live_share_qq_space_big:
                if (matrialIsNull()) {
                    PToast.showShort(context.getString(R.string.share_link_fail));
                    return;
                }
                if(shareMatrial.getMatrialType() == ShareMatrial.SHARE_TYPE_PIC) {
                    ShareUtil.shareImage(ShareUtil.FUNCTION_UNLOCK_SHARE, ShareUtil.CHANNEL_QQ_ZONE, shareMatrial.getPreview_img(), (Activity) getContext(), new UnlockShareQQShareCallBack());
                } else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(shareContentImageUri);
                    ShareUtils.shareToQQZone((Activity) context, shareTitle, shareContent, shareURL, list, new UnlockShareQQShareCallBack());
                }
                unlockDlgMsg.onDismiss();
                break;

            case R.id.spread_share_no:
                PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.TEMP_SHARE_TYPE), CenterConstant.SHARE_TYPE_UNLOCK);
                UIShow.showWebActivity(context, WebActivity.NORMAL_RIGHT, Hosts.H5_SHARE_EXTENSION, null);
                unlockDlgMsg.onDismiss();
                break;

            case R.id.weixinhaoy:
            case R.id.spread_share_weixinfirend_big:
                if (matrialIsNull()) {
                    PToast.showShort(context.getString(R.string.share_link_fail));
                    return;
                }
                if(shareMatrial.getMatrialType() == ShareMatrial.SHARE_TYPE_PIC) {
                    ShareUtil.shareImage(ShareUtil.FUNCTION_UNLOCK_SHARE, ShareUtil.CHANNEL_WX_FRIEND, shareMatrial.getPreview_img(), (Activity) getContext(), null);
                } else {
                    ShareUtil.shareFunction = ShareUtil.FUNCTION_UNLOCK_SHARE;
                    ShareUtil.scontent = 0;
                    ShareUtil.opt = 1;
                    ShareUtil.channel = ShareUtil.CHANNEL_WX_FRIEND;
                    ShareUtils.sendWebpageToWechat(shareURL, shareTitle, shareContent, shareContentImageUri, ShareUtils.WECHAT_FRIEND);
                }
                unlockDlgMsg.onDismiss();
                break;

            case R.id.pengyouquan:
            case R.id.spread_share_weixinpengyouquan_big:
                if (matrialIsNull()) {
                    PToast.showShort(context.getString(R.string.share_link_fail));
                    return;
                }
                if(shareMatrial.getMatrialType() == ShareMatrial.SHARE_TYPE_PIC) {
                    ShareUtil.shareImage(ShareUtil.FUNCTION_UNLOCK_SHARE, ShareUtil.CHANNEL_WX_FRIEND_CRICLE, shareMatrial.getPreview_img(), (Activity) getContext(), null);
                } else {
                    ShareUtil.shareFunction = ShareUtil.FUNCTION_UNLOCK_SHARE;
                    ShareUtil.scontent = 0;
                    ShareUtil.opt = 1;
                    ShareUtil.channel = ShareUtil.CHANNEL_WX_FRIEND_CRICLE;
                    ShareUtils.sendWebpageToWechat(shareURL, shareTitle, shareContent, shareContentImageUri, ShareUtils.WECHAT_FRIEND_CIRCLE);
                }
                unlockDlgMsg.onDismiss();
                break;
            default:
                break;
        }
    }

    private boolean matrialIsNull() {
        if (null == shareMatrial) {
            return true;
        }
        shareURL = shareMatrial.getLandingPageUrl();
        shareTitle = shareMatrial.getShareContentTitle();
        shareContent = shareMatrial.getShareContentSubTitle();
        shareContentImageUri = shareMatrial.getShareContentIcon();
        return false;
    }
}
