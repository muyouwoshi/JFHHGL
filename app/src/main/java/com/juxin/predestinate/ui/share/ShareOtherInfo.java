package com.juxin.predestinate.ui.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.my.ShareTypeData;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.ShareUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

import org.json.JSONObject;

/**
 * 它人信息分享
 * Created by IQQ on 2017/9/19
 */
public class ShareOtherInfo extends BaseActivity implements ScreenShot.SaveCallBack {

    private ImageView iv_Avatar, iv_avatar_other, iv_juewei, iv_meili, iv_QRCode;
    private TextView tv_nickname, tv_nickname_other, tv_age_dis;
    private View view;
    private UserDetail otherInfo;

    private long iUid;
    private String sAvatar, sNickname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spread_share);
        initView();
        initData();
    }

    //初始化数据
    private void initView() {
        setBackView(R.id.base_title_back);
        setTitle(getString(R.string.share_act_title));

        //View.OnClickListener 替换成  NoDoubleClickListener 防止多次点击，生成多次图片
        setTitleRightImg(R.drawable.spread_share_btn_black, new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (otherInfo != null) {
                    ShareUtil.shareFunction = ShareUtil.FUNCTION_NORMAL_SHARE;
                    ShareUtil.scontent = ShareUtil.SCONTENT_USER_SHARE;
                    ShareUtil.opt = ShareUtil.OPT_DEFAULT;
                    ShareTypeData shareTypeData = new ShareTypeData();
                    shareTypeData.setShareType(CenterConstant.SHARE_TYPE_FIRST);
                    shareTypeData.setOtherId(otherInfo.getUid());
                    shareTypeData.setOtherName(otherInfo.getNickname());
                    shareTypeData.setOtherAvatar(otherInfo.getAvatar());
                    shareTypeData.setShareCode(otherInfo.getShareCode());
                    SourcePoint.getInstance().lockShareSource(getString(R.string.share_act_title));
                    ScreenShot.saveShareImg(ShareOtherInfo.this, view, shareTypeData, null);
                }
            }
        });
        iv_Avatar = (ImageView) findViewById(R.id.share_img_avatar);
        tv_nickname = (TextView) findViewById(R.id.share_tv_nickname);
        view = findViewById(R.id.share_view);

        tv_nickname_other = (TextView) findViewById(R.id.share_tv_nickname_other);
        iv_avatar_other = (ImageView) findViewById(R.id.share_img_avatar_other);
        iv_meili = (ImageView) findViewById(R.id.share_img_meili);
        iv_QRCode = (ImageView) findViewById(R.id.share_img_qr_code);
        iv_juewei = (ImageView) findViewById(R.id.iv_share_juewei);
        tv_age_dis = (TextView) findViewById(R.id.share_tv_age_dis);
    }

    private void initData() {
        sNickname = ModuleMgr.getCenterMgr().getMyInfo().getNickname();
        sAvatar = ModuleMgr.getCenterMgr().getMyInfo().getAvatar();

        ImageLoader.loadAvatar(this, sAvatar, iv_Avatar);
        tv_nickname.setText(sNickname);

        otherInfo = getIntent().getParcelableExtra(CenterConstant.USER_CHECK_OTHER_KEY);
        if (otherInfo == null)
            return;
        iUid = otherInfo.getUid();
        //载入要分享的用户信息
        ImageLoader.loadCircleAvatar(this, otherInfo.getAvatar(), iv_avatar_other, 8, getResources().getColor(R.color.pink));
        tv_nickname_other.setText(otherInfo.getNickname());

        if (otherInfo.getNobilityInfo().getRank() > CenterConstant.TITLE_LEVEL_LOW) {//爵位和对应背景图
            iv_juewei.setVisibility(View.VISIBLE);
            ImageLoader.loadFitCenter(this, ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(otherInfo.getNobilityInfo().getRank(),
                    otherInfo.getGender()).getTitle_icon(), iv_juewei);
        } else {
            iv_juewei.setVisibility(View.GONE);
        }

        if (otherInfo.getTop() != 0) {
            iv_meili.setVisibility(View.VISIBLE);
            if (otherInfo.isMan()) {
                iv_meili.setImageResource(R.drawable.f1_top02);
            } else {
                iv_meili.setImageResource(R.drawable.f1_top01);
            }
        } else {
            iv_meili.setVisibility(View.GONE);
        }

        String dis = otherInfo.getDistance() > 5 ? getString(R.string.user_info_distance_far) : getString(R.string.user_info_distance_near);
        if (otherInfo.getAge() != 0 && otherInfo.getDistance() != 0) {
            tv_age_dis.setVisibility(View.VISIBLE);
            tv_age_dis.setText(otherInfo.getAge() + "岁" + "  -  " + dis);
        } else if (otherInfo.getAge() == 0 && otherInfo.getDistance() != 0) {
            tv_age_dis.setVisibility(View.VISIBLE);
            tv_age_dis.setText(dis);
        } else if (otherInfo.getAge() != 0 && otherInfo.getDistance() == 0) {
            tv_age_dis.setVisibility(View.VISIBLE);
            tv_age_dis.setText(otherInfo.getAge() + "岁");
        } else {
            tv_age_dis.setVisibility(View.GONE);
        }


        ModuleMgr.getCommonMgr().getQRCode(iUid, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    JSONObject jsonObject = response.getResponseJson();
                    jsonObject = jsonObject.optJSONObject("data");
                    if (jsonObject != null) {
                        String url = jsonObject.optString("url");
                        ImageLoader.loadFitCenter(ShareOtherInfo.this, ImageLoader.getGlideUrl(url), iv_QRCode);
                    }
                }
            }
        });
    }

    @Override
    public void shareSuccess() {
        PToast.showShort(getString(R.string.share_act_success));
        //启动微信
        UIShow.startWexin();
    }

    @Override
    public void shareFail() {
        PToast.showShort(getString(R.string.share_act_fail));
    }

    /**
     * 调用ShareDialog分享，需要在此接收QQ分享的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE) {
            EarnMoneyQQShareCallBack callBack = new EarnMoneyQQShareCallBack();
            callBack.setChannel(requestCode == Constants.REQUEST_QQ_SHARE ? ShareUtil.CHANNEL_QQ_FRIEND : ShareUtil.CHANNEL_QQ_ZONE);
            Tencent.onActivityResultData(requestCode, resultCode, data, callBack);
        }
    }
}