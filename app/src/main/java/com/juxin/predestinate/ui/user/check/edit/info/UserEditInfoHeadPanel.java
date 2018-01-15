package com.juxin.predestinate.ui.user.check.edit.info;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.local.album.ImgSelectUtil;
import com.juxin.predestinate.module.local.album.help.ImgConstant;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.ChineseFilter;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

/**
 * 编辑页面头部
 * Created by Su on 2017/5/3.
 */
public class UserEditInfoHeadPanel extends BasePanel implements ImgSelectUtil.OnChooseCompleteListener {

    private UserDetail userDetail;

    private ImageView img_header;
    public ImageView iv_bg, iv_mask;
    private TextView user_id, tv_invite_code, tv_copy, user_gender, user_age, user_height, user_province;

    public UserEditInfoHeadPanel(Context context) {
        super(context);
        setContentView(R.layout.f1_user_edit_info_head_panel);
        initView();
        initData();
    }

    public void initView() {
        iv_bg = (ImageView) findViewById(R.id.iv_bg);
        img_header = (ImageView) findViewById(R.id.img_header);
        iv_mask = (ImageView) findViewById(R.id.iv_mask);
        user_id = (TextView) findViewById(R.id.user_id);
        tv_invite_code = (TextView) findViewById(R.id.tv_invite_code);
        tv_copy = (TextView) findViewById(R.id.tv_copy);
        user_gender = (TextView) findViewById(R.id.user_gender);
        user_age = (TextView) findViewById(R.id.user_age);
        user_height = (TextView) findViewById(R.id.user_height);
        user_province = (TextView) findViewById(R.id.user_province);

        img_header.setOnClickListener(listener);
        tv_copy.setOnClickListener(listener);
    }

    public void initData() {
        userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        user_id.setText(String.format("ID: %s", userDetail.getUid()));
        user_gender.setText(userDetail.isMan() ? "男" : "女");
        user_age.setText(getContext().getString(R.string.user_info_age, userDetail.getAge()));
        user_height.setText(String.format("%scm", userDetail.getHeight()));
        user_province.setText(userDetail.getProvince());
        tv_invite_code.setText(userDetail.getShareCode());
        ImageLoader.loadRoundAvatar(getContext(), userDetail.getAvatar(), img_header);
        if (TextUtils.isEmpty(userDetail.getAvatar())) {
            iv_mask.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_6F7485));
        } else {
            ImageLoader.loadBlur(getContext(), userDetail.getAvatar(), iv_bg);
        }
    }

    public void refreshView() {
        userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        ImageLoader.loadRoundAvatar(getContext(), userDetail.getAvatar(), img_header);
    }

    private NoDoubleClickListener listener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.img_header:
                    ImgSelectUtil.getInstance().cropType(getContext(), ImgConstant.CROP_RECTANGLE, UserEditInfoHeadPanel.this);
                    break;

                case R.id.tv_copy:
                    ChineseFilter.copyString(getContext(), ModuleMgr.getCenterMgr().getMyInfo().getShareCode());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onComplete(String... path) {
        if (path == null || path.length == 0 || TextUtils.isEmpty(path[0])) {
            return;
        }
        LoadingDialog.show((FragmentActivity) getContext(), "正在上传头像");
        ModuleMgr.getCenterMgr().uploadAvatar(path[0], new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    LoadingDialog.closeLoadingDialog();
                    MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
                }
            }
        });
    }
}
