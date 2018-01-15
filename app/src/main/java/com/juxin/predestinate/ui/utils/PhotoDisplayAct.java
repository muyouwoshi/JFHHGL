package com.juxin.predestinate.ui.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserPhoto;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;
import com.juxin.predestinate.module.logic.baseui.custom.TouchImageView;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.PickerDialogUtil;
import com.juxin.predestinate.module.util.TimerUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 像片放大页面
 *
 * @author
 */
public class PhotoDisplayAct extends BaseActivity implements OnClickListener, OnPageChangeListener {
    public final static int DISPLAY_TYPE_USER = 1; //看自己相册
    public final static int DISPLAY_TYPE_OTHER = 2; //看别人相册
    public final static int DISPLAY_TYPE_INFO_EDIT = 3;
    public final static int DISPLAY_TYPE_BIG_IMG = 4; //看大图

    private TextView btn_photo_display_del;
    private ViewPager vp_photo_display;
    private TextView btn_photo_display_title, tv_set_bg;

    private List<String> imageList;
    private List<UserPhoto> photoList;
    private int currentPosition;
    private int displayType;

    private PhotoDiaplayAdapter displayAdapter;
    private boolean isDele; //是否删除成功

    private RelativeLayout rl_title, rl_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_photo_display_act);
        initData();
        initView();
        initEvent();
    }

    @Override
    public int getStatusBarColor() {
        return 0xff000000;
    }

    @SuppressWarnings("unchecked")
    private void initData() {
        currentPosition = getIntent().getIntExtra("position", 0);
        displayType = getIntent().getIntExtra("type", 1);
        if (displayType == DISPLAY_TYPE_BIG_IMG) {
            imageList = (List<String>) getIntent().getSerializableExtra("list");
            if (imageList == null) {
                imageList = new ArrayList<>();
            }
            displayAdapter = new PhotoDiaplayAdapter(getSupportFragmentManager(), null, imageList);
        } else {
            photoList = (List<UserPhoto>) getIntent().getSerializableExtra("list");
            if (photoList == null) {
                photoList = new ArrayList<>();
            }
            displayAdapter = new PhotoDiaplayAdapter(getSupportFragmentManager(), photoList, null);
        }

        displayAdapter.setOnClickEvent(onClickEvent);

        setIsPhoto(photoList, imageList);

    }

    private TouchImageView.OnClickEvent onClickEvent = new TouchImageView.OnClickEvent() {
        @Override
        public void onClickCallBack(View view) {
            //onBackPressed();
        }
    };

    private void initView() {
        tv_set_bg = (TextView) findViewById(R.id.tv_set_bg);
        this.btn_photo_display_del = (TextView) findViewById(R.id.btn_photo_display_del);
        this.vp_photo_display = (ViewPager) findViewById(R.id.vp_photo_display);
        this.btn_photo_display_title = (TextView) findViewById(R.id.btn_photo_display_title);

        int imgSize;
        if (isPhoto()) {
            imgSize = photoList.size();
        } else {
            imgSize = imageList.size();
        }

        if (imgSize > 1) {
            btn_photo_display_title.setVisibility(View.VISIBLE);
            btn_photo_display_title.setText("照片  " + (currentPosition + 1) + "/" + imgSize);
        }

        this.vp_photo_display.setAdapter(displayAdapter);
        if (displayType != DISPLAY_TYPE_USER) {
            this.btn_photo_display_del.setVisibility(View.GONE);
            tv_set_bg.setVisibility(View.GONE);
        }
        if (getIntent().getBooleanExtra("isPre", false)) {//预览个人资料背景大图
            findViewById(R.id.rl_title).setVisibility(View.GONE);
        }
    }

    private void initEvent() {
        this.btn_photo_display_del.setOnClickListener(this);
        tv_set_bg.setOnClickListener(this);
        this.vp_photo_display.setOnPageChangeListener(this);
        findViewById(R.id.btn_title_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_title_back:
                finishCurrentActivity();
                break;
            case R.id.btn_photo_display_del:
                PickerDialogUtil.showSimpleAlertDialog(this, new SimpleTipDialog.ConfirmListener() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onSubmit() {
                        PLogger.d("photoList.get(currentPosition).getPic()=" + photoList.get(currentPosition).getPic());
                        LoadingDialog.show(PhotoDisplayAct.this, "正在删除，请稍候...");
                        ModuleMgr.getCenterMgr().deletePhoto((int) photoList.get(currentPosition).getAlbumid(), new RequestComplete() {
                            @Override
                            public void onRequestComplete(HttpResponse response) {
                                handleDelResult(response);
                            }
                        });

                    }
                }, "确定删除该张照片吗？", "删除照片");
                break;
            case R.id.tv_set_bg:
//                LoadingDialog.show(PhotoDisplayAct.this, "正在更换背景图，请稍候...");
//                ModuleMgr.getCenterMgr().updateMyInfo(new HashMap<String, Object>() {
//                    {
//                        put(EditKey.s_key_avatarBg, photoList.get(currentPosition).getPic());
//                    }
//                }, new HttpMgr.IReqComplete() {
//                    @Override
//                    public void onReqComplete(HttpResult result) {
//                        LoadingDialog.closeLoadingDialog();
//                        if (result.isOk()) {
//                            MMToast.showShort("更换背景图成功");
//                        } else {
//                            MMToast.showShort("更换背景图失败，请重试");
//                        }
//                    }
//                });
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int position) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int position) {
        long uid = PSP.getInstance().getLong(Constant.FLIP_ALBUM_UID, 0);
        if (uid != 0)
            StatisticsUser.userAlbumFlip(uid, photoList.get(position).getPic(), currentPosition > position);

        currentPosition = position;
        btn_photo_display_title.setText("照片  " + (position + 1) + "/" + photoList.size());
    }

    @Override
    public void onBackPressed() {
        if (displayType == DISPLAY_TYPE_BIG_IMG) {
            finish();
        } else {
            finishCurrentActivity();
        }
    }

    private void finishCurrentActivity() {
        Intent intent = new Intent();
        intent.putExtra("list", (Serializable) photoList);
        intent.putExtra("isDele", isDele);
        setResult(200, intent);
        finish();
    }

    private void handleDelResult(final HttpResponse response) {
        LoadingDialog.closeLoadingDialog(200, new TimerUtil.CallBack() {
            @Override
            public void call() {
                if (response.isOk()) {
                    PToast.showShort("删除成功");
                    isDele = true;
                    // 删除成功之后
                    photoList.remove(currentPosition);
                    if (currentPosition <= 0) {
                        currentPosition = 0;
                    } else {
                        currentPosition -= 1;
                    }
                    displayAdapter = new PhotoDiaplayAdapter(getSupportFragmentManager(), photoList, null);
                    displayAdapter.setOnClickEvent(onClickEvent);
                    vp_photo_display.setAdapter(displayAdapter);
                    vp_photo_display.setCurrentItem(currentPosition);
                    btn_photo_display_title.setText("照片  " + (currentPosition + 1) + "/" + photoList.size());

                    if (photoList != null && photoList.size() < 1) {
                        finishCurrentActivity();
                    }
                } else {
                    PToast.showShort("删除失败,请稍候再试");
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        vp_photo_display.setCurrentItem(currentPosition);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isDele) {
            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
        }
    }


    private boolean isPhoto = false;


    public boolean isPhoto() {
        return isPhoto;
    }

    private void setIsPhoto(List<UserPhoto> photoList, List<String> imgList) {
        if (photoList != null && imgList == null) {
            isPhoto = true;
        } else if (photoList == null && imgList != null) {
            isPhoto = false;
        } else {
            isPhoto = false;
        }
    }

}
