package com.juxin.predestinate.module.local.album.crop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.juxin.library.observe.MsgMgr;
import com.juxin.library.dir.DirUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.album.help.AlbumHelper;
import com.juxin.predestinate.module.local.album.help.ImgConstant;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * 裁切图片
 * Created by Kind on 16/5/6.
 */
public class CropImageAct extends BaseActivity implements View.OnClickListener {

    private Bitmap newbitmap;
    private ClipViewLayout clipViewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setCanNotify(false);//设置该页面不弹出悬浮窗消息通知
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_album_clip_act);
        Intent intent = getIntent();
        String image_path = intent.getStringExtra("data");

        findViewById(R.id.btn_clip_cancel).setOnClickListener(this);
        findViewById(R.id.btn_clip_submit).setOnClickListener(this);
        clipViewLayout = (ClipViewLayout) findViewById(R.id.clipViewLayout);

        if(intent.hasExtra(ImgConstant.CROP_TYPE)) {
            int cropType = intent.getIntExtra(ImgConstant.CROP_TYPE, 0);
            if(cropType == ImgConstant.CROP_RECTANGLE) {//矩形  xml 里默认是圆形
                ClipView clipView = clipViewLayout.getClipView();
                if(clipView != null) {
                    clipView.setClipType(ClipView.ClipType.RECTANGLE);
                }
            }
        }
        clipViewLayout.setImageSrc(Uri.parse(image_path));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clip_cancel: { // 取消
                finish();
                break;
            }

            case R.id.btn_clip_submit: { // 确认
                submit(getCropPath());
                break;
            }
        }
    }

    private void submit(String path) {
        final ArrayList<String> tmp = new ArrayList<>();
        if (!TextUtils.isEmpty(path)) {
            tmp.add(path);
        } else {
            tmp.add("");
        }
        MsgMgr.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlbumHelper.getInstance().showTransferAct(CropImageAct.this, tmp);
            }
        });
    }

    /**
     * 获取裁切图片路径
     */
    private String getCropPath() {
        Bitmap zoomedCropBitmap = clipViewLayout.clip();
        if (zoomedCropBitmap == null) {
            return null;
        }

        Uri mSaveUri = Uri.fromFile(DirUtils.createPictureFile("crop"));
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(mSaveUri);
                if (outputStream != null) {
                    zoomedCropBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                }
            } catch (IOException ex) {
                Log.e("android", "Cannot open file: " + mSaveUri, ex);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return mSaveUri.getPath();
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (newbitmap != null && !newbitmap.isRecycled()) {
            newbitmap.recycle();
            newbitmap = null;
        }
    }
}
