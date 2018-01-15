package com.juxin.predestinate.ui.share;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.ShareUtil;
import com.tencent.connect.common.Constants;

/**
 * 类描述：
 * 创建时间：2017/11/13 10:39
 * 修改时间：2017/11/13 10:39
 * Created by zhoujie on 2017/11/13
 * 修改备注：
 */

public class QQShareActivity extends BaseActivity {
    public final static String DATA_SHARE_TYPE = "share_type";
    public final static String DATA_SHARE_IMAGE_URI = "share_image_url";
    public final static String DATA_SHARE_WEB_LINK = "share_web_link";
    public final static String DATA_TITLE = "share_title";
    public final static String DATA_CONTENT = "share_content";
    public final static String DATA_ICON_URI = "share_icon_uri";

    int shareType;
    String shareImageUri;
    String shareWebLink;
    String title;
    String content;
    String iconUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.transparent)));
        super.onCreate(savedInstanceState);
        ShareUtil.getInstance().share(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        PToast.showShort("未检测到分享结果");
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE) {
            ShareUtil.getInstance().onQQResult(requestCode, resultCode, data);
        }
        finish();
    }
}
