package com.juxin.predestinate.ui.discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.utils.FileUtil;
import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.local.album.ImgSelectUtil;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.UIUtil;

/**
 * 上传头像界面
 *
 * @author zhang
 * @date 2017/6/17
 */
public class UserAvatarUploadAct extends BaseActivity implements View.OnClickListener, ImgSelectUtil.OnChooseCompleteListener {

    private int intentType = -1;
    /**
     * 是否是重新上传头像
     */
    private boolean isResetAvatar = false;

    private Button btn_pick_photo;
    private Button btn_take_photo;
    private ImageView imgBtn_upload_head;
    private TextView txt_usernohead_info;

    private CustomFrameLayout frameLayout;
    private ImageView img_upload_logo;
    private ImageButton imgBtn_noheard_ts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_user_avatar_upload);
        initType();
        initTitle();
        initView();
        initEvent();
    }

    private void initType() {
        intentType = getIntent().getIntExtra("type", -1);
        isResetAvatar = getIntent().getBooleanExtra("isResetAvatar", false);
    }

    private void initTitle() {
        setTitle(isResetAvatar ? getString(R.string.re_upload_avatar_title) : getString(R.string.upload_avatar_title));
        setTitleRight(getString(R.string.re_upload_avatar_right_title), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToOtherAct();
            }
        });
    }

    private void initView() {
        btn_pick_photo = (Button) findViewById(R.id.btn_pick_photo_nohead);
        btn_take_photo = (Button) findViewById(R.id.btn_take_photo_nohead);
        imgBtn_upload_head = (ImageView) findViewById(R.id.imgBtn_noheard_head);
        imgBtn_noheard_ts = (ImageButton) findViewById(R.id.imgBtn_noheard_ts);
        txt_usernohead_info = (TextView) findViewById(R.id.txt_usernohead_info);

        frameLayout = (CustomFrameLayout) findViewById(R.id.img_upload_reason);
        img_upload_logo = (ImageView) findViewById(R.id.img_upload_logo);
        frameLayout.setList(new int[]{R.id.img_upload_text, R.id.img_upload_logo});

        if (isResetAvatar) {
            showImgUploadText();
        } else {
            showImgUploadLogo();
        }
    }

    private void showImgUploadText() {
        imgBtn_noheard_ts.setVisibility(View.VISIBLE);
        frameLayout.show(R.id.img_upload_text);
        String avatar = ModuleMgr.getCenterMgr().getMyInfo().getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            ImageLoader.loadCircleAvatar(this, avatar, imgBtn_upload_head, UIUtil.dip2px(this, 4));
        }
        txt_usernohead_info.setText(getString(R.string.re_upload_avatar_def));
    }

    private void showImgUploadLogo() {
        imgBtn_noheard_ts.setVisibility(View.GONE);
        imgBtn_upload_head.setImageResource(R.drawable.btn_upload_head_selector);
        frameLayout.show(R.id.img_upload_logo);
        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        if (userDetail.isMan()) {
            img_upload_logo.setImageResource(R.drawable.ic_upload_head_logo_female);
        } else {
            img_upload_logo.setImageResource(R.drawable.ic_upload_head_logo_male);
        }

        imgBtn_upload_head.setOnClickListener(this);
    }

    private void initEvent() {
        btn_take_photo.setOnClickListener(this);
        btn_pick_photo.setOnClickListener(this);
    }

    private void redirectToOtherAct() {
        if (intentType == 0) {
            UIShow.showMainClearTask(this);
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_take_photo_nohead:
                ImgSelectUtil.getInstance().pickPhoto(this, this);
                break;
            case R.id.btn_pick_photo_nohead:
                ImgSelectUtil.getInstance().pickPhoto(this, this);
                break;
            case R.id.imgBtn_noheard_head:
                ImgSelectUtil.getInstance().pickPhoto(this, this);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        redirectToOtherAct();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void clearUserAvatarFile(String headPicPath) {
        FileUtil.deleteFile(headPicPath);
    }

    @Override
    public void onComplete(final String... path) {
        if (path == null || path.length == 0 || TextUtils.isEmpty(path[0])) {
            PToast.showShort(getString(R.string.re_upload_avatar_selet_fail));
            return;
        }
        PLogger.d("path=== " + path[0]);
        LoadingDialog.show(this, "正在上传头像");
        ModuleMgr.getCenterMgr().uploadAvatar(path[0], new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    LoadingDialog.closeLoadingDialog();
                    MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
                    clearUserAvatarFile(path[0]);
                    redirectToOtherAct();
                } else {
                    PToast.showShort(getString(R.string.re_upload_avatar_fail));
                }
            }
        });
    }
}
