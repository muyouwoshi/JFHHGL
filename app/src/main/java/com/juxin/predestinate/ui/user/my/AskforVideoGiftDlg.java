package com.juxin.predestinate.ui.user.my;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.dir.DirType;
import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.BitmapUtil;
import com.juxin.library.utils.DeviceUtils;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.library.utils.TimeBaseUtil;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.file.UpLoadResult;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.module.util.my.GiftHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2017/6/27
 * 描述:视频索要礼物弹框
 * 作者:lc
 */
public class AskforVideoGiftDlg extends BaseDialogFragment implements View.OnClickListener, GiftHelper.OnRequestGiftListCallback {

    private int reqCode = 1;
    private long duration;
    private String videoPath, duration2, scalePicPath, scaleUrl;

    // 界面上展示的几个礼物信息，使用SparseArray保证顺序
    private SparseArray<GiftsList.GiftInfo> displayGifts = new SparseArray<>();
    private GiftsList.GiftInfo selectGiftInfo;

    private Context context;
    private FrameLayout fl_add_video;
    private RelativeLayout rl_add_video_over;
    private LinearLayout ll_gift1, ll_gift2, ll_gift3;
    private ImageView iv_add_video, iv_add_video_over, iv_add_video_bg, iv_gift1, iv_gift2, iv_gift3;
    private TextView tv_add_video_over_time, tv_gift_price1, tv_gift_price2, tv_gift_price3, tv_gift_name1, tv_gift_name2, tv_gift_name3, tv_send;

    private MediaMetadataRetriever mmr;
    private List<GiftsList.GiftInfo> arrGifts = new ArrayList();

    public AskforVideoGiftDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(1, 0);
        setCancelable(true);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_askfor_video_gift_dlg);
        View view = getContentView();
        initView(view);
        initGifts();
        setGiftView();
        mmr = new MediaMetadataRetriever();
        return view;
    }

    private void initView(View view) {
        fl_add_video = (FrameLayout) view.findViewById(R.id.fl_add_video);
        rl_add_video_over = (RelativeLayout) view.findViewById(R.id.rl_add_video_over);
        iv_add_video = (ImageView) view.findViewById(R.id.iv_add_video);
        tv_add_video_over_time = (TextView) view.findViewById(R.id.tv_add_video_over_time);
        iv_add_video_over = (ImageView) view.findViewById(R.id.iv_add_video_over);
        iv_add_video_bg = (ImageView) view.findViewById(R.id.iv_add_video_bg);
        ll_gift1 = (LinearLayout) view.findViewById(R.id.ll_gift1);
        ll_gift2 = (LinearLayout) view.findViewById(R.id.ll_gift2);
        ll_gift3 = (LinearLayout) view.findViewById(R.id.ll_gift3);
        iv_gift1 = (ImageView) view.findViewById(R.id.iv_gift1);
        iv_gift2 = (ImageView) view.findViewById(R.id.iv_gift2);
        iv_gift3 = (ImageView) view.findViewById(R.id.iv_gift3);
        tv_gift_price1 = (TextView) view.findViewById(R.id.tv_gift_price1);
        tv_gift_price2 = (TextView) view.findViewById(R.id.tv_gift_price2);
        tv_gift_price3 = (TextView) view.findViewById(R.id.tv_gift_price3);
        tv_gift_name1 = (TextView) view.findViewById(R.id.tv_gift_name1);
        tv_gift_name2 = (TextView) view.findViewById(R.id.tv_gift_name2);
        tv_gift_name3 = (TextView) view.findViewById(R.id.tv_gift_name3);
        tv_send = (TextView) view.findViewById(R.id.tv_send);

        fl_add_video.setOnClickListener(this);
        ll_gift1.setOnClickListener(this);
        ll_gift2.setOnClickListener(this);
        ll_gift3.setOnClickListener(this);
        tv_send.setOnClickListener(this);

        ll_gift1.setSelected(true);
    }

    /**
     * 初始化礼物列表
     */
    private void initGifts() {
        arrGifts = ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts();
        if (arrGifts.size() > 0) return;
        ModuleMgr.getCommonMgr().requestGiftList(this);
    }

    public void setGiftView() {
        try {
            List<Integer> listVideoIds = ModuleMgr.getCommonMgr().getCommonConfig().getGift().getUnlock_video_ids();
            for (int i = 0; i < listVideoIds.size(); i++) {
                GiftsList.GiftInfo info = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo(listVideoIds.get(i));
                displayGifts.put(i, info);
                switch (i) {
                    case 0:
                        tv_gift_name1.setText(info.getName());
                        tv_gift_price1.setText(getString(R.string.goods_diamond_need, info.getMoney()));
                        ImageLoader.loadCenterCrop(context, info.getPic(), iv_gift1);
                        break;

                    case 1:
                        tv_gift_name2.setText(info.getName());
                        tv_gift_price2.setText(getString(R.string.goods_diamond_need, info.getMoney()));
                        ImageLoader.loadCenterCrop(context, info.getPic(), iv_gift2);
                        break;

                    case 2:
                        tv_gift_name3.setText(info.getName());
                        tv_gift_price3.setText(getString(R.string.goods_diamond_need, info.getMoney()));
                        ImageLoader.loadCenterCrop(context, info.getPic(), iv_gift3);
                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_gift1:
                selectGiftInfo = displayGifts.get(0);
                changeCheck(true, false, false);
                break;

            case R.id.ll_gift2:
                selectGiftInfo = displayGifts.get(1);
                changeCheck(false, true, false);
                break;

            case R.id.ll_gift3:
                selectGiftInfo = displayGifts.get(2);
                changeCheck(false, false, true);
                break;

            case R.id.fl_add_video:
                selectVideo();
                break;

            case R.id.tv_send:
                if (NetworkUtils.isNotConnected(context)) {
                    PToast.showShort(getString(R.string.net_error_check_your_net));
                    return;
                }
                if (TextUtils.isEmpty(videoPath) || TextUtils.isEmpty(scalePicPath)) {
                    PToast.showShort(getString(R.string.private_video_upload));
                    return;
                }
                if (selectGiftInfo == null) selectGiftInfo = displayGifts.get(0);
                uploadScalePic();
                break;
            default:
                break;
        }
    }

    private void changeCheck(boolean b1, boolean b2, boolean b3) {
        ll_gift1.setSelected(b1);
        ll_gift2.setSelected(b2);
        ll_gift3.setSelected(b3);
    }

    private void selectVideo() {
        if (getContext() == null) return;
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, reqCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Cursor cursor = null;
        Bitmap bmp;
        try {
            if (reqCode != requestCode) return;

            Uri uri = data.getData();
            String[] projection = {
                    MediaStore.Video.Media.DISPLAY_NAME,//视频在sd卡中的名称
                    MediaStore.Video.Media.DURATION,//视频时长
                    MediaStore.Video.Media.SIZE,//视频文件的大小
                    MediaStore.Video.Media.DATA,//视频的绝对路径
                    MediaStore.Video.Media.ARTIST//艺术家
            };
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                videoPath = DeviceUtils.getPath(context, uri);                                                             // 文件路径
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));      // 文件名称
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));                  // 文件大小
//                Long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));          // 文件时长 获取的是0
                PLogger.d("path=" + videoPath + ",name=" + name + ",size=" + size + ",duration=" + duration);

                if (TextUtils.isEmpty(videoPath) || size == 0) {
                    PToast.showShort(getString(R.string.private_video_sel_fail));
                    return;
                }
                if (size > (10 * 1024 * 1024)) {//视频大小不能大于10M
                    PToast.showShort(getString(R.string.private_video_size));
                    return;
                }
                mmr.setDataSource(videoPath);
                duration2 = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);// 播放时长单位为毫秒
                PLogger.d("path duration=" + duration2);

                if (!TextUtils.isEmpty(duration2)) {
                    duration = Long.parseLong(duration2) / 1000;
                    tv_add_video_over_time.setText(TimeBaseUtil.getDuration(duration2));
                }

                bmp = BitmapUtil.toRoundCorner(BitmapUtil.zoomBitmap(mmr.getFrameAtTime(), UIUtil.dip2px(context, 70), UIUtil.dip2px(context, 70)), UIUtil.dip2px(context, 8));
                scalePicPath = BitmapUtil.saveBitmap(bmp, DirType.getImageDir() + "/videotmp.jpg");
                iv_add_video.setImageBitmap(bmp);
                iv_add_video_bg.setVisibility(View.VISIBLE);
                rl_add_video_over.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) cursor.close();
        }
    }

    @Override
    public void onRequestGiftListCallback(boolean isOk) {
        if (isOk) {
            arrGifts = ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts();
        }
    }

    /**
     * 上传缩略图
     */
    private void uploadScalePic() {
        ModuleMgr.getMediaMgr().sendHttpFile(Constant.UPLOAD_TYPE_PHOTO, scalePicPath, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                try {
                    if (!response.isOk()) {
                        PToast.showShort(getString(R.string.private_video_upload_fail));
                        return;
                    }
                    UpLoadResult upLoadResult = (UpLoadResult) response.getBaseData();
                    scaleUrl = upLoadResult.getFile_http_path();
                    if (TextUtils.isEmpty(scaleUrl)) {
                        PToast.showShort(getString(R.string.private_video_upload_fail));
                        return;
                    }
                    uploadVideo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 上传视频
     */
    private void uploadVideo() {
        LoadingDialog.show((FragmentActivity) context, getString(R.string.private_video_uploading));
        ModuleMgr.getMediaMgr().sendHttpFile(Constant.UPLOAD_TYPE_VIDEO, videoPath, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                try {
                    LoadingDialog.closeLoadingDialog();
                    JSONObject jsonObject = new JSONObject(response.getResponseString());
                    String status = jsonObject.optString("status");
                    JSONObject resJob = jsonObject.optJSONObject("res");
                    if (!"ok".equals(status) || resJob == null) {
                        PToast.showShort(getString(R.string.private_video_upload_fail));
                        return;
                    }
                    if (!resJob.isNull("file_http_path")) {
                        groupSend(resJob.optString("file_http_path"));
                    }
                } catch (JSONException e) {
                    PToast.showShort(getString(R.string.private_video_upload_fail));
                }
            }
        });
    }

    /**
     * 群发视频索要礼物消息
     */
    public void groupSend(String videoUrl) {
        if (selectGiftInfo == null) {
            PToast.showShort(getString(R.string.gift_not_find));
            dismissAllowingStateLoss();
            return;
        }
        StatisticsUser.userVideoAskGift(selectGiftInfo.getMoney(), selectGiftInfo.getId(), videoUrl);
        ModuleMgr.getCommonMgr().reqQunVideo(selectGiftInfo.getId(), videoUrl, duration, scaleUrl, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                try {
                    if (response.isOk()) {
                        if (context == null) return;
                        PToast.showShort(context.getString(R.string.send_suceed));
                        return;
                    }
                    PToast.showShort(response.getMsg());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dismissAllowingStateLoss();
    }
}

