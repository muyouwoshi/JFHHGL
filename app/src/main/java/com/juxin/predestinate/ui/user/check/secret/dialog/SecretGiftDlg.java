package com.juxin.predestinate.ui.user.check.secret.dialog;

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
import com.juxin.predestinate.bean.center.user.detail.UserVideo;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.util.CenterConstant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 查看视频、相册赠送礼物弹框
 * Created by Su on 2017/5/17.
 */
public class SecretGiftDlg extends BaseActivity implements View.OnClickListener {

    private TextView tv_gift, tv_gift_diamonds;
    private ImageView iv_gift;

    private UserDetail userDetail;
    private UserVideo userVideo;
    private GiftsList.GiftInfo giftInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_user_secret_gift_dlg);

        initView();
    }

    private void initView() {
        userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        userVideo = getIntent().getParcelableExtra(CenterConstant.USER_CHECK_VIDEO_KEY);
        GiftsList giftsList = ModuleMgr.getCommonMgr().getGiftLists();
        giftInfo = giftsList.getGiftInfo(userVideo.getGiftid());

        tv_gift = (TextView) findViewById(R.id.tv_gift_count);
        iv_gift = (ImageView) findViewById(R.id.iv_gift_pic);
        tv_gift_diamonds = (TextView) findViewById(R.id.tv_gift_diamonds);
        findViewById(R.id.tv_send).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);

        if (giftInfo != null) {
            ImageLoader.loadCenterCrop(this, giftInfo.getPic(), iv_gift);
            tv_gift.setText(userVideo.getGiftnum() + giftInfo.getName());
        }
        tv_gift_diamonds.setText(String.valueOf(userVideo.getCost()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_send:
                // 钻石数量充足则请求解锁接口，不足弹充值弹框
                if (userDetail.getDiamand() >= userVideo.getCost()) {
                    unlock();
                    return;
                }
                UIShow.showSecretDiamondDlg(this);
                break;

            case R.id.btn_close:
                finish();
                break;
        }
    }

    /**
     * 解锁视频
     */
    private void unlock() {
        LoadingDialog.show(this, getString(R.string.user_secret_media_unlock));
        ModuleMgr.getCenterMgr().reqUnlockVideo(userVideo.getUid(), userVideo.getId(), new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                LoadingDialog.closeLoadingDialog();
                if (response.isOk()) {
                    PToast.showShort(getString(R.string.user_secret_media_lock_suc));
                    try {
                        JSONObject jsonObject = new JSONObject(response.getResponseString());
                        ModuleMgr.getCenterMgr().getMyInfo().setDiamand(jsonObject.optInt("diamand"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    userVideo.setCanView();
                    Intent intent = new Intent();
                    intent.putExtra(CenterConstant.USER_CHECK_VIDEO_KEY, userVideo);
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                }
                PToast.showShort(getString(R.string.user_secret_media_lock_fail));
                finish();
            }
        });
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }
}
