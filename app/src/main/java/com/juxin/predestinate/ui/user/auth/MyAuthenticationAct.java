package com.juxin.predestinate.ui.user.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.config.VideoVerifyBean;
import com.juxin.predestinate.bean.my.ApplyLiveStatusBean;
import com.juxin.predestinate.bean.my.IdCardVerifyStatusInfo;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.PickerDialogUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.live.ui.LiveAuthDialog;
import com.juxin.predestinate.ui.live.util.DialogUtil;
import com.juxin.predestinate.ui.setting.SettingAct;

/**
 * 我的认证
 *
 * @author xy
 */
public class MyAuthenticationAct extends BaseActivity implements View.OnClickListener, RequestComplete {

    public static final int AUTHENTICSTION_REQUESTCODE = 103;
    private final int authForVodeo = 104;
    private final int authIDCard = 105;

    private LinearLayout ll_auth_hot_tips;
    private ImageView iv_auth_video_ok, iv_auth_id_ok, ivAuthLiveOk;
    private TextView tv_txt_auth_phone, tv_txt_auth_video, tv_txt_auth_id, tv_auth_top_left, tv_auth_top_right, tvTxtAuthLive;
    private UserDetail userDetail;
    private VideoVerifyBean videoVerifyBean;
    private IdCardVerifyStatusInfo mIdCardVerifyStatusInfo;
    private ApplyLiveStatusBean applyLiveStatusBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_authentication_act);
        setBackView(getResources().getString(R.string.title_auth_man));
        initView();
        initObj();
        initConfig();

        //以下必须在 initView() 后面
        initTopTxt();
        phoneShow();
        initVideoAuth();
        initIdCardAuth();
        hotTipsIsShow();
        initLiveAuth();
    }

    private synchronized void initObj() {
        userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        videoVerifyBean = ModuleMgr.getCommonMgr().getVideoVerify();
        mIdCardVerifyStatusInfo = ModuleMgr.getCommonMgr().getIdCardVerifyStatusInfo();
        applyLiveStatusBean = ModuleMgr.getCommonMgr().getApplyLiveStatusBean();
        //don't show live auth,if user is man
        if(userDetail.isMan()){
            findViewById(R.id.rl_auth_live).setVisibility(View.GONE);
        }
    }

    private void initView() {
        findViewById(R.id.rl_auth_phone).setOnClickListener(this);
        findViewById(R.id.rl_auth_video).setOnClickListener(this);
        findViewById(R.id.rl_auth_id).setOnClickListener(this);
        findViewById(R.id.rl_auth_live).setOnClickListener(this);
        tv_auth_top_left = (TextView) findViewById(R.id.tv_auth_top_left);
        tv_auth_top_right = (TextView) findViewById(R.id.tv_auth_top_right);
        ll_auth_hot_tips = (LinearLayout) findViewById(R.id.ll_auth_hot_tips);
        tv_txt_auth_phone = (TextView) findViewById(R.id.txt_auth_phone);
        tv_txt_auth_video = (TextView) findViewById(R.id.txt_auth_video);
        iv_auth_video_ok = (ImageView) findViewById(R.id.iv_auth_video_ok);
        tv_txt_auth_id = (TextView) findViewById(R.id.txt_auth_id);
        iv_auth_id_ok = (ImageView) findViewById(R.id.iv_auth_id_ok);
        ivAuthLiveOk = (ImageView) findViewById(R.id.iv_auth_live_ok);
        tvTxtAuthLive = (TextView) findViewById(R.id.txt_auth_live);
    }

    private synchronized void initIdCardAuth() {
        tv_txt_auth_id.setTextColor(ContextCompat.getColor(this, R.color.txt_auth_status_isnull));

        switch (mIdCardVerifyStatusInfo.getStatus()) {
            case 0:
                tv_txt_auth_id.setText(getResources().getString(R.string.auth_real_name));
                tv_txt_auth_id.setTextColor(ContextCompat.getColor(this, R.color.theme_color_red));
                break;
            case 1:
                tv_txt_auth_id.setText(getResources().getString(R.string.txt_authstatus_authing));
                tv_txt_auth_id.setTextColor(ContextCompat.getColor(this, R.color.txt_auth_status_ing));
                break;
            case 2:
                tv_txt_auth_id.setText(getResources().getString(R.string.center_phone_has_verify));
                iv_auth_id_ok.setVisibility(View.VISIBLE);
                tv_txt_auth_id.setTextColor(ContextCompat.getColor(this, R.color.txt_auth_status_complete));
                break;
            case 3:
            case 4:
                tv_txt_auth_id.setText(getResources().getString(R.string.txt_authstatus_autherror));
                tv_txt_auth_id.setTextColor(ContextCompat.getColor(this, R.color.txt_auth_status_error));
                break;
            default:
                break;
        }
    }

    private synchronized void initVideoAuth() {
        tv_txt_auth_video.setTextColor(ContextCompat.getColor(this, R.color.txt_auth_status_isnull));

        switch (videoVerifyBean.getStatus()) {
            case 0:
                tv_txt_auth_video.setText(getResources().getString(R.string.title_videoauth));
                tv_txt_auth_video.setTextColor(ContextCompat.getColor(this, R.color.theme_color_red));
                break;
            case 1:
                tv_txt_auth_video.setText(getResources().getString(R.string.txt_authstatus_authing));
                tv_txt_auth_video.setTextColor(ContextCompat.getColor(this, R.color.txt_auth_status_ing));
                break;
            case 2:
                tv_txt_auth_video.setText(getResources().getString(R.string.txt_authstatus_autherror));
                tv_txt_auth_video.setTextColor(ContextCompat.getColor(this, R.color.txt_auth_status_error));
                break;
            case 3:
                tv_txt_auth_video.setText(getResources().getString(R.string.center_phone_has_verify));
                tv_txt_auth_video.setTextColor(ContextCompat.getColor(this, R.color.txt_auth_status_complete));
                iv_auth_video_ok.setVisibility(View.VISIBLE);
                ModuleMgr.getCommonMgr().setVideochatConfig(true, true, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //手机认证页
            case R.id.rl_auth_phone:
                if (!ModuleMgr.getCenterMgr().getMyInfo().isVerifyCellphone()) {
                    UIShow.showPhoneVerifyAct(MyAuthenticationAct.this, AUTHENTICSTION_REQUESTCODE);
                } else {//手机认证完成页
                    UIShow.showPhoneVerifyCompleteAct(MyAuthenticationAct.this, AUTHENTICSTION_REQUESTCODE);
                }
                Statistics.userBehavior(SendPoint.menu_me_meauth_telauth);
                break;
            //自拍认证
            case R.id.rl_auth_video:
                UIShow.showMyAuthenticationVideoAct(this, authForVodeo);
                Statistics.userBehavior(SendPoint.menu_me_meauth_videoauth);
                break;
            //身份认证
            case R.id.rl_auth_id:
                Statistics.userBehavior(SendPoint.menu_me_meauth_id);
                UIShow.showIDCardAuthenticationAct(this, authIDCard);
                break;
            //申请开播认证
            case R.id.rl_auth_live:
                if(applyLiveStatusBean.status != 0){
                    return;
                }

                DialogUtil.ShowAuthDialog(MyAuthenticationAct.this, new LiveAuthDialog.Click(){
                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void confirm() {
                        applyLive();
                    }
                });

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initObj();
        hotTipsIsShow();
        if (requestCode == AUTHENTICSTION_REQUESTCODE) {
            phoneShow();
            if (resultCode == Constant.EXITLOGIN_RESULTCODE) {
                setResult(Constant.EXITLOGIN_RESULTCODE);
                back();
            }
        } else if (requestCode == authForVodeo) {
            initVideoAuth();
        } else if (requestCode == authIDCard) {
            if (data != null && data.getIntExtra(IDCardAuthenticationSucceedAct.IDCARDBACK, 0) == 1) {
                this.finish();
            }
        }
    }

    private void initConfig() {
        //已完成认证，不再请求
        if (videoVerifyBean.getStatus() != 3) {// 3 已认证
            ModuleMgr.getCommonMgr().requestVideochatConfig(this);
        }
        if (mIdCardVerifyStatusInfo.getStatus() != 2) {// 2 已认证
            ModuleMgr.getCommonMgr().getVerifyStatus(this);
        }
        //非认证状态请求接口
        if(applyLiveStatusBean.status != 3){
            ModuleMgr.getCommonMgr().getApplyLiveStatus(this);
        }
    }

    /**
     * 男、女顶部分别显示的内容
     */
    private void initTopTxt() {
        if (userDetail.isMan()) {
            ll_auth_hot_tips.setVisibility(View.GONE);
            tv_auth_top_left.setText(getResources().getString(R.string.auth_step));
            tv_auth_top_right.setVisibility(View.INVISIBLE);
        } else {
            ll_auth_hot_tips.setVisibility(View.VISIBLE);
            tv_auth_top_left.setText(getResources().getString(R.string.auth_info));
            tv_auth_top_right.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 手机已认证显示
     */
    private void phoneShow() {
        try {
            if (userDetail != null && userDetail.isVerifyCellphone()) {
                String mobile = userDetail.getMobile();
                if (!TextUtils.isEmpty(mobile)) {
                    tv_txt_auth_phone.setText(mobile.substring(0, 3) + "****" + mobile.substring(7, mobile.length()));
                    tv_txt_auth_phone.setTextColor(ContextCompat.getColor(this, R.color.gray_text));
                }
            } else {
                tv_txt_auth_phone.setText(getResources().getString(R.string.auth_bind_phone));
                tv_txt_auth_phone.setTextColor(ContextCompat.getColor(this, R.color.theme_color_red));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否显示女号提示
     */
    private void hotTipsIsShow() {
        if (userDetail.isMan()) {
            return;
        }
        if (userDetail.isVerifyCellphone() && videoVerifyBean.getStatus() == 3 && mIdCardVerifyStatusInfo.getStatus() == 2) {
            ll_auth_hot_tips.setVisibility(View.GONE);
        } else {
            ll_auth_hot_tips.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        initObj();
        initVideoAuth();
        initIdCardAuth();
        initLiveAuth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ModuleMgr.getCenterMgr().getMyInfo().isVerifyAll()) {
            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null); //更新个人资料
        }
    }

    /**
     * 初始化直播认证
     */
    private void initLiveAuth(){
        tvTxtAuthLive.setTextColor(ContextCompat.getColor(this, R.color.txt_auth_status_isnull));
        switch (applyLiveStatusBean.status){
            //未认证
            case 0:
                tvTxtAuthLive.setText(getResources().getString(R.string.title_auth_live));
                tvTxtAuthLive.setTextColor(ContextCompat.getColor(this, R.color.theme_color_red));
                break;
            case 1:
                tvTxtAuthLive.setText(getResources().getString(R.string.txt_authstatus_authing));
                tvTxtAuthLive.setTextColor(ContextCompat.getColor(this, R.color.txt_auth_status_ing));
                break;
            case 2:
                tvTxtAuthLive.setText(getResources().getString(R.string.txt_authstatus_autherror));
                tvTxtAuthLive.setTextColor(ContextCompat.getColor(this, R.color.txt_auth_status_error));
                break;
            case 3:
                tvTxtAuthLive.setText(getResources().getString(R.string.center_phone_has_verify));
                tvTxtAuthLive.setTextColor(ContextCompat.getColor(this, R.color.txt_auth_status_complete));
                ivAuthLiveOk.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }

    /**
     * 申请直播认证
     */
    private void applyLive(){
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.applyLive, null, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if(response.isOk()){
                    applyLiveStatusBean.status = 1;
                    initLiveAuth();
                    return;
                }
                PToast.showShort(response.getMsg());
            }
        });
    }
}
