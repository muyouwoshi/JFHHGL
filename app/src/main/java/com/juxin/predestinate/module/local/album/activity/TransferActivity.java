package com.juxin.predestinate.module.local.album.activity;

import android.content.Intent;
import android.os.Bundle;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.StackNode;
import com.juxin.predestinate.module.local.album.help.AlbumHelper;
import com.juxin.predestinate.module.local.album.help.ImgConstant;
import com.juxin.predestinate.module.local.album.ImgSelectUtil;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;

import java.util.List;

/**
 * 拍照回调中转ACT
 */
public class TransferActivity extends BaseActivity {

    private int CROP_TYPE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setCanNotify(false);//设置该页面不弹出悬浮窗消息通知
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_album_transfer_act);
        Intent intent = getIntent();
        int extra_nums = intent.getIntExtra(ImgConstant.STR_NUMS, 4);
        if(intent.hasExtra(ImgConstant.CROP_TYPE)) {
            CROP_TYPE = intent.getIntExtra(ImgConstant.CROP_TYPE, 0);
        }
        newTakePhoto(extra_nums);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        List<String> data = intent.getStringArrayListExtra(ImgConstant.STR_CURRENT_PIC);
        handleResult(data);
        finish();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if (resultCode == RESULT_OK && intent != null) {
//            List<String> data = (List<String>) intent.getSerializableExtra("data");
//            handleResult(data);
//        }
//        finish();
//    }

    // 调用本地相机
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK && intent != null) {
            List<String> data = (List<String>) intent.getSerializableExtra("data");

            if (AlbumHelper.getInstance().isCrop()) {
                AlbumHelper.getInstance().skipToCrop2(TransferActivity.this, data.get(0), CROP_TYPE);
                return;
            }
            handleResult(data);
        }
        finish();
    }

    /**
     * 打开新照片选择界面
     */
    private void newTakePhoto(int totalNum) {
        Intent intent = new Intent(this, PickOrTakeImageActivity.class);
        intent.putExtra(ImgConstant.STR_NUMS, totalNum);
        intent.putExtra(ImgConstant.CROP_TYPE, CROP_TYPE);
        StackNode.appendIntent(this, intent);
        startActivityForResult(intent, 100);
    }

    /**
     * 处理返回结果
     */
    private void handleResult(List<String> data) {
        if (data == null) {
            PToast.showShort("选择图片失败，无法上传");
            return;
        }
        ImgSelectUtil.getInstance().handlePickPhotoResult(data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AlbumHelper.getInstance().release();
    }
}