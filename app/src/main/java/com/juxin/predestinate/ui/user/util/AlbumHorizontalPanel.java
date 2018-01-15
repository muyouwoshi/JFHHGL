package com.juxin.predestinate.ui.user.util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;

import com.juxin.library.log.PSP;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.detail.UserPhoto;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.custom.HorizontalListView;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.PickerDialogUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.check.UserCheckInfoAct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 水平查看相册/视频panel
 *
 * @author Su
 * @date 2017/3/23
 */
public class AlbumHorizontalPanel extends BasePanel implements AdapterView.OnItemClickListener {

    public static final int EX_HORIZONTAL_ALBUM = 1;  // 展示照片
    public static final int EX_HORIZONTAL_VIDEO = 2;  // 展示视频
    private int channel = CenterConstant.USER_CHECK_INFO_OWN; // 默认查看自己

    private int showType;               // 展示类型：相册，视频，礼物
    private UserDetail userDetail;      // 用户数据

    private HorizontalListView horiListView;  // 水平列表
    private HorizontalAdapter albumAdapter;   // 相册适配器
    private VideoHoriAdapter videoAdapter;    // 视频适配器

    /**
     * @param channel    查看方：自己，他人
     * @param showType   展示类型：相册，视频，礼物
     * @param userDetail 数据列表
     */
    public AlbumHorizontalPanel(Context context, int channel, int showType, UserDetail userDetail) {
        super(context);
        setContentView(R.layout.p1_album_horizontal_panel);
        this.channel = channel;
        this.showType = showType;
        this.userDetail = userDetail;

        initView();
    }

    private void initView() {
        int horizontalSpacing = ModuleMgr.getAppMgr().getScreenWidth() / 50;
        int columnWidth = (ModuleMgr.getAppMgr().getScreenWidth() - 8 * horizontalSpacing) / 4;
        horiListView = (HorizontalListView) findViewById(R.id.list_horizontal);
        horiListView.setItemMargin(horizontalSpacing);

        // 相册列表
        if (showType == EX_HORIZONTAL_ALBUM) {
            /**
             * @author Mr.Huang
             * @date 2017-09-06
             * @action update
             * 传入判断自己相册参数 channel
             */
            albumAdapter = new HorizontalAdapter(getContext(), this.channel, columnWidth, userDetail.getUserPhotos());
            albumAdapter.checkVip();//先检查下vip, 为了设置adapter中的isVip字段
            horiListView.setAdapter(albumAdapter);
        }

        // 视频列表
        if (showType == EX_HORIZONTAL_VIDEO) {
            videoAdapter = new VideoHoriAdapter(getContext(), columnWidth, userDetail.getUserVideos());
            horiListView.setAdapter(videoAdapter);
        }
        horiListView.setOnItemClickListener(this);
    }

    public void refresh(UserDetail userDetail) {
        if (showType == EX_HORIZONTAL_ALBUM) {
            albumAdapter.setList(userDetail.getUserPhotos());
            return;
        }
        videoAdapter.setList(userDetail.getUserVideos());
    }

    /**
     * 开通Vip提示
     */
    private void showVipTips() {
        PickerDialogUtil.showSimpleTipDialogExt((FragmentActivity) getContext(), new SimpleTipDialog.ConfirmListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onSubmit() {
                UIShow.showOpenVipActivity(getContext());
            }
        }, getContext().getString(R.string.goods_vip_check_other_album), "", "取消", "去开通", true, R.color.text_zhuyao_black);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SourcePoint.getInstance().lockSource((Activity) getContext(), "picture");
        switch (showType) {
            case EX_HORIZONTAL_ALBUM:
                UserDetail info = ModuleMgr.getCenterMgr().getMyInfo();
                PSP.getInstance().put(Constant.FLIP_ALBUM_UID, info.getUid());
                StatisticsUser.userAlbum(userDetail.getUid(), userDetail.getUserPhotos().get(position).getPic(),
                        position + 1, !info.isMan() || info.isVip());

                if (channel == CenterConstant.USER_CHECK_INFO_OWN) {
                    UIShow.showPhotoOfSelf((FragmentActivity) getContext(), (Serializable) userDetail.getUserPhotos(), position);
                    return;
                }

                // 女用户
                if (!info.isMan()) {
                    if (!ModuleMgr.getCenterMgr().getHasCheckAvatar()) {
                        if (ModuleMgr.getCenterMgr().uploadAvatarAct(getContext())) {
                            return;
                        }
                    }
                    UIShow.showPhotoOfOther((FragmentActivity) getContext(), (Serializable) userDetail.getUserPhotos(), position);
                    return;
                }

                // 男用户，先判断vip
                //@author Mr.Huang
                if (position != 0 && !info.isVip()) {//如果点击的不是第一个 并且是不是vip
                    UserInfoLightweight userInfoLightweight = new UserInfoLightweight(userDetail);
                    UIShow.showOpenVipDialogNew(getContext(), userInfoLightweight, "查看相册,您需要开通VIP会员才可查看私密相册。", false, "开通VIP");
                    return;
                }

                if (!ModuleMgr.getCenterMgr().getHasCheckAvatar()) {
                    if (ModuleMgr.getCenterMgr().uploadAvatarAct(getContext())) {
                        return;
                    }
                }
                if (info.isVip()) {
                    UIShow.showPhotoOfOther((FragmentActivity) getContext(), (Serializable) userDetail.getUserPhotos(), position);
                } else if (position == 0) {
                    List<UserPhoto> list = new ArrayList<>(1);
                    list.add(userDetail.getUserPhotos().get(0));
                    UIShow.showPhotoOfOther((FragmentActivity) getContext(), (Serializable) list, position);
                }
                break;

            case EX_HORIZONTAL_VIDEO:
                UIShow.showUserSecretAct((FragmentActivity) getContext(), channel, userDetail, UserCheckInfoAct.REQUEST_CODE_UNLOCK_VIDEO);
                break;

            default:
                break;
        }
    }
}
