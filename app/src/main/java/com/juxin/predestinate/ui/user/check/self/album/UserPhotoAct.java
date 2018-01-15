package com.juxin.predestinate.ui.user.check.self.album;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.detail.UserPhoto;
import com.juxin.predestinate.module.local.album.ImgSelectUtil;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的相册
 * Created by Su on 2017/5/10.
 */
public class UserPhotoAct extends BaseActivity implements ImgSelectUtil.OnChooseCompleteListener, AdapterView.OnItemClickListener, PObserver {

    private GridView grid_photo;
    private AlbumAdapter albumAdapter;

    private UserDetail userDetail;
    private List<UserPhoto> userPhotoList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_user_info_photo_act);
        setBackView(getString(R.string.user_info_photo));

        initView();
        MsgMgr.getInstance().attach(this);
    }

    private void initView() {
        userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        grid_photo = (GridView) findViewById(R.id.grid_photo);

        // 计算GridView的横向间隔的宽度
        int horizontalSpacing = ModuleMgr.getAppMgr().getScreenWidth() / 50;
        grid_photo.setHorizontalSpacing(horizontalSpacing);
        // 计算GridView的每一列的宽度
        int columnWidth = (ModuleMgr.getAppMgr().getScreenWidth() - 5 * horizontalSpacing) / 4;
        grid_photo.setColumnWidth(columnWidth);

        userPhotoList.add(new UserPhoto(AlbumAdapter.PHOTO_STATUS_NORMAL));
        userPhotoList.addAll(userDetail.getUserPhotos());
        albumAdapter = new AlbumAdapter(this, userPhotoList, columnWidth);
        grid_photo.setAdapter(albumAdapter);
        grid_photo.setOnItemClickListener(this);
    }

    private void refreshView() {
        userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        if (userPhotoList == null) userPhotoList = new ArrayList<>();

        userPhotoList.clear();
        userPhotoList.add(new UserPhoto(AlbumAdapter.PHOTO_STATUS_NORMAL));
        userPhotoList.addAll(userDetail.getUserPhotos());
        albumAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            ImgSelectUtil.getInstance().pickPhotoGallery(UserPhotoAct.this, this);
        } else {
            UIShow.showPhotoOfSelf(this, (Serializable) userDetail.getUserPhotos(), position - 1);
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        switch (key) {
            case MsgType.MT_MyInfo_Change:
                refreshView();
                break;
        }
    }

    @Override
    public void onComplete(String... path) {
        if (path == null || path.length == 0 || TextUtils.isEmpty(path[0])) {
            return;
        }
        LoadingDialog.show(this, "正在上传到相册");
        ModuleMgr.getCenterMgr().uploadPhoto(path[0], new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    LoadingDialog.closeLoadingDialog();
                    MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MsgMgr.getInstance().detach(this);
    }
}
