package com.juxin.predestinate.ui.user.fragment;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.local.album.ImgSelectUtil;
import com.juxin.predestinate.module.local.album.help.ImgConstant;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.auth.MyAuthenticationAct;
import com.juxin.predestinate.ui.user.util.CenterConstant;

/**
 * 个人中心条目头部
 */
class UserFragmentHeadPanel extends BasePanel implements View.OnClickListener, ImgSelectUtil.OnChooseCompleteListener {

    private LinearLayout user_tips;
    private ImageView user_head, user_head_status;

    private IncomePanel incomePanel;
    private UserFragmentPeeragePanel peeragePanel;
    private UserFragmentProgressPanel progressPanel;

    private UserDetail myInfo;

    UserFragmentHeadPanel(Context context) {
        super(context!=null?context: App.getActivity());
        setContentView(R.layout.p1_user_fragment_header);
        initView();
        refreshView();
    }

    private void initView() {
        user_head = (ImageView) findViewById(R.id.user_head);
        user_head_status = (ImageView) findViewById(R.id.user_head_status);
        user_tips = (LinearLayout) findViewById(R.id.tips_verify_mobile);

        user_head.setOnClickListener(this);
        user_tips.setOnClickListener(this);

        LinearLayout progress_container = (LinearLayout) findViewById(R.id.progress_container);
        progressPanel = new UserFragmentProgressPanel(getContext(), progress_container);
        progress_container.addView(progressPanel.getContentView());

        LinearLayout peerage_container = (LinearLayout) findViewById(R.id.peerage_container);
        peeragePanel = new UserFragmentPeeragePanel(getContext(), ModuleMgr.getCenterMgr().getMyInfo(), true, peerage_container);
        peerage_container.addView(peeragePanel.getContentView());

        LinearLayout function_container = (LinearLayout) findViewById(R.id.function_container);
        if (!ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            incomePanel = new IncomePanel(getContext(), ModuleMgr.getCenterMgr().getMyInfo(), function_container);
            function_container.addView(incomePanel.getContentView());
        }
    }

    /**
     * 界面刷新
     */
    public void refreshView() {
        myInfo = ModuleMgr.getCenterMgr().getMyInfo();
        if (myInfo.isVerifyCellphone()) {
            user_tips.setVisibility(View.GONE);
        } else {
            user_tips.setVisibility(View.VISIBLE);
        }
        refreshHeader();
        progressPanel.refreshView();
        peeragePanel.refreshView();
    }

    private void refreshHeader() {
        ImageLoader.loadRoundAvatar(getContext(), myInfo.getAvatar(), user_head);
        switch (myInfo.getAvatar_status()) {
            case CenterConstant.USER_AVATAR_UNCHECKING:  // 审核中
                user_head_status.setVisibility(View.VISIBLE);
                ImageLoader.loadRoundAvatar(getContext(), R.drawable.f2_user_avatar_checking, user_head_status);
                break;

            case CenterConstant.USER_AVATAR_NO_PASS:   // 未通过
                user_head_status.setVisibility(View.VISIBLE);
                ImageLoader.loadRoundAvatar(getContext(), R.drawable.f2_user_avatar_notpass, user_head_status);
                break;

            default:
                user_head_status.setVisibility(View.GONE); // 其他状态默认通过
                break;
        }
    }

    /**
     * 女号刷新收入（四项）/ 等级 / 进度
     */
    void refreshIncome() {
        if (incomePanel == null) return;
        incomePanel.refreshView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_head://上传头像
                if (PermissionsUtil.hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ImgSelectUtil.getInstance().cropType(getContext(), ImgConstant.CROP_RECTANGLE, this);
                    Statistics.userBehavior(SendPoint.menu_me_avatar);
                } else {
                    PermissionsUtil.requestPermission((BaseActivity) context, new PermissionListener() {
                        @Override
                        public void permissionGranted(@NonNull String[] permissions) {
                            ImgSelectUtil.getInstance().cropType(getContext(), ImgConstant.CROP_RECTANGLE, UserFragmentHeadPanel.this);
                            Statistics.userBehavior(SendPoint.menu_me_avatar);
                        }

                        @Override
                        public void permissionDenied(@NonNull String[] permissions) {
                            PToast.showShort("请打开存储权限");
                        }
                    }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }


                break;
            case R.id.tips_verify_mobile:
                Statistics.userBehavior(SendPoint.menu_me_top_ljbd);
                UIShow.showPhoneVerifyAct((FragmentActivity) getContext(),
                        MyAuthenticationAct.AUTHENTICSTION_REQUESTCODE); //跳手机认证页面
                break;
            default:
                break;
        }
    }

    //处理其他界面返回结果
    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == 101) {
            refreshView();
        }
    }

    @Override
    public void onComplete(final String... path) {
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