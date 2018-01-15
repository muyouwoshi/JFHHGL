package com.juxin.predestinate.ui.user.check;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.fragment.UserFragmentPeeragePanel;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

/**
 * 查看用户资料头部panel
 */
public class UserCheckInfoHeadPanel extends BasePanel {

    private final int channel;
    private ImageView iv_mask;
    private UserDetail userDetail; // 用户资料

    public UserCheckInfoHeadPanel(Context context, int channel, UserDetail userProfile) {
        super(context);
        setContentView(R.layout.p1_user_checkinfo_header);
        this.channel = channel;
        this.userDetail = userProfile;
        initData();
        initView();
    }

    private void initData() {
        if (channel == CenterConstant.USER_CHECK_INFO_OWN) {
            userDetail = ModuleMgr.getCenterMgr().getMyInfo();
            return;
        }
        if (userDetail == null) {
            PToast.showShort(getContext().getString(R.string.user_other_info_req_fail));
        }
    }

    private void initView() {
        ImageView iv_bg = (ImageView) findViewById(R.id.iv_bg); // 头部背景
        ImageView img_header = (ImageView) findViewById(R.id.img_header);
        iv_mask = (ImageView) findViewById(R.id.iv_mask);
        TextView user_id = (TextView) findViewById(R.id.user_id);
        LinearLayout ll_container_one = (LinearLayout) findViewById(R.id.ll_container_one);
        LinearLayout ll_container_two = (LinearLayout) findViewById(R.id.ll_container_two);
        UserFragmentPeeragePanel peeragePanel = new UserFragmentPeeragePanel(context, userDetail, false, ll_container_one);
        ll_container_one.addView(peeragePanel.getContentView());

        ll_container_two.setVisibility(View.VISIBLE);
        UserPartInfoPanel partInfoPanel = new UserPartInfoPanel(context, channel, userDetail);
        ll_container_two.addView(partInfoPanel.getContentView());

        img_header.setOnClickListener(listener);

        user_id.setText(String.format("ID:%s", userDetail.getUid()));
        ImageLoader.loadRoundAvatar(getContext(), userDetail.getAvatar(), img_header);
        if (TextUtils.isEmpty(userDetail.getAvatar())) {
            iv_mask.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_6F7485));
        } else {
            ImageLoader.loadBlur(getContext(), ImageLoader.checkOssAvatar(userDetail.getAvatar()), iv_bg, 8, 0);
        }
    }

    private NoDoubleClickListener listener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.img_header:
                    if (!ModuleMgr.getCenterMgr().getHasCheckAvatar()) {
                        if (ModuleMgr.getCenterMgr().uploadAvatarAct(getContext())) {
                            return;
                        }
                    }
                    if (TextUtils.isEmpty(userDetail.getAvatar())) return;
                    UIShow.showPhotoOfBigImg((FragmentActivity) App.getActivity(), userDetail.getAvatar());
                    break;

                default:
                    break;
            }
        }
    };
}
