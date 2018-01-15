package com.juxin.predestinate.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.juxin.library.dir.DirType;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.update.UpgradeSource;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.config.VideoVerifyBean;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
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
import com.juxin.predestinate.ui.live.LiveStartLiveTipActivity;
import com.juxin.predestinate.ui.live.bean.LiveStartLiveTipDetailList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 设置页面
 *
 * @author xy
 */
public class SettingAct extends BaseActivity implements OnClickListener {

    private TextView setting_clear_cache_tv, setting_account, setting_update_version, setting_action;
    private ToggleButton setting_message_iv, setting_vibration_iv, setting_voice_iv, setting_quit_message_iv, settingVideoIv, settingAudioIv;
    private View setting_line;

    /**
     * 消息提示状态及音视频开关状态
     */
    private Boolean messageStatus, vibrationStatus, voiceStatus, quitMessageStatus, videoStatus, audioStatus;
    /**
     * 用户个人资料
     */
    private UserDetail myInfo;
    /**
     * 用户音视频开关状态
     */
    private VideoVerifyBean videoVerifyBean;

    private LiveStartLiveTipDetailList mLiveStartLiveTipDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_settings_layout);
        setBackView(getResources().getString(R.string.title_set));
        myInfo = ModuleMgr.getCenterMgr().getMyInfo();

        initView();
        initPreference();
        initLiveStartLiveTipData();
    }

    /**
     * 查看是否有 好友开播列表，有的话，开播提醒入口显示，否则隐藏
     */
    private void initLiveStartLiveTipData() {

        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        // 性别 1男2女
        if (userDetail.getGender() == 2) {
            return;
        }

        /**
         * "page": 1
         "limit": 10
         */
        final HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("platform", "2");//android为2
        postParams.put("page", 1);
        postParams.put("limit", LiveStartLiveTipActivity.LIMIT);

        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.GetLiveNotifyList, postParams, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    try {
                            JSONObject resJo = response.getResponseJson().getJSONObject("res");
                            if (resJo != null) {
                                mLiveStartLiveTipDetailList = new LiveStartLiveTipDetailList();
                                mLiveStartLiveTipDetailList.parseJson(resJo);

                                if(mLiveStartLiveTipDetailList.getList()!=null&&mLiveStartLiveTipDetailList.getList().size()>0){
                                    findViewById(R.id.setting_start_live_tip).setVisibility(View.VISIBLE);
                                    findViewById(R.id.view_line).setVisibility(View.VISIBLE);
                                }
                            }

                            return;

                    } catch (JSONException e) {
                        mLiveStartLiveTipDetailList = null;
                    }
                }

                PLogger.d(response.getMsg());
                PToast.showShort(response.getMsg());
            }
        });
    }

    private void initView() {
        findViewById(R.id.setting_modifypwd).setOnClickListener(this);
        findViewById(R.id.setting_message).setOnClickListener(this);
        findViewById(R.id.setting_vibration).setOnClickListener(this);
        findViewById(R.id.setting_voice).setOnClickListener(this);
        findViewById(R.id.setting_feedback).setOnClickListener(this);
        findViewById(R.id.setting_clear_cache).setOnClickListener(this);
        findViewById(R.id.setting_logoff).setOnClickListener(this);
        findViewById(R.id.setting_quit_message).setOnClickListener(this);
        findViewById(R.id.setting_video_switch).setOnClickListener(this);
        findViewById(R.id.setting_audio_switch).setOnClickListener(this);
        findViewById(R.id.setting_about).setOnClickListener(this);
        findViewById(R.id.setting_ll_update).setOnClickListener(this);
        findViewById(R.id.setting_start_live_tip).setOnClickListener(this);

        setting_action = (TextView) findViewById(R.id.setting_action);
        setting_action.setOnClickListener(this);

        setting_clear_cache_tv = (TextView) findViewById(R.id.setting_clear_cache_tv);
        setting_account = (TextView) findViewById(R.id.setting_account);
        setting_update_version = (TextView) findViewById(R.id.setting_update_version);
        setting_line = findViewById(R.id.setting_line);

        setting_message_iv = (ToggleButton) findViewById(R.id.setting_message_iv);
        setting_vibration_iv = (ToggleButton) findViewById(R.id.setting_vibration_iv);
        setting_voice_iv = (ToggleButton) findViewById(R.id.setting_voice_iv);
        setting_quit_message_iv = (ToggleButton) findViewById(R.id.setting_quit_message_iv);
        settingVideoIv = (ToggleButton) findViewById(R.id.setting_video_switch_iv);
        settingAudioIv = (ToggleButton) findViewById(R.id.setting_audio_switch_iv);

        if (myInfo.getUid() != 0) {
            setting_account.setText(String.valueOf(myInfo.getUid()));
            setting_account.setVisibility(View.VISIBLE);
        }
        setting_update_version.setText(getString(R.string.setting_update_version,ModuleMgr.getAppMgr().getVerName()));
        //男用户屏蔽音视频开启按钮
        if (myInfo.isMan()) {
            findViewById(R.id.ll_video_audio).setVisibility(View.GONE);
        }

        setting_action.setVisibility(View.GONE);
        setting_line.setVisibility(View.GONE);
//        if (myInfo.isB()){
//        }else {
//            setting_action.setVisibility(View.VISIBLE);
//            setting_line.setVisibility(View.VISIBLE);
//        }
        setting_clear_cache_tv.setText(DirType.getCacheSize(this));
    }

    private void initPreference() {
        videoVerifyBean = ModuleMgr.getCommonMgr().getVideoVerify();

        // 新消息提示
        messageStatus = PSP.getInstance().getBoolean(Constant.SETTING_MESSAGE, Constant.SETTING_MESSAGE_DEFAULT);
        setting_message_iv.setBackgroundResource(messageStatus ? R.drawable.f1_setting_ok : R.drawable.f1_setting_no);

        // 消息震动提示
        vibrationStatus = PSP.getInstance().getBoolean(Constant.SETTING_VIBRATION, Constant.SETTING_VIBRATION_DEFAULT);
        setting_vibration_iv.setBackgroundResource(vibrationStatus ? R.drawable.f1_setting_ok : R.drawable.f1_setting_no);

        // 消息声音提示
        voiceStatus = PSP.getInstance().getBoolean(Constant.SETTING_VOICE, Constant.SETTING_VOICE_DEFAULT);
        setting_voice_iv.setBackgroundResource(voiceStatus ? R.drawable.f1_setting_ok : R.drawable.f1_setting_no);

        // 退出消息提示
        quitMessageStatus = PSP.getInstance().getBoolean(Constant.SETTING_QUIT_MESSAGE, Constant.SETTING_QUIT_MESSAGE_DEFAULT);
        setting_quit_message_iv.setBackgroundResource(quitMessageStatus ? R.drawable.f1_setting_ok : R.drawable.f1_setting_no);

        setVideoAndAudioStatus(videoVerifyBean.getBooleanVideochat(), videoVerifyBean.getBooleanAudiochat());
    }

    /**
     * 更新视频及音频开关状态
     *
     * @param videoStatus 当前视频状态
     * @param audioStatus 当前音频状态
     */
    private void setVideoAndAudioStatus(boolean videoStatus, boolean audioStatus) {
        this.videoStatus = videoStatus;
        this.audioStatus = audioStatus;
        settingVideoIv.setBackgroundResource(videoStatus ? R.drawable.f1_setting_ok : R.drawable.f1_setting_no);
        settingAudioIv.setBackgroundResource(audioStatus ? R.drawable.f1_setting_ok : R.drawable.f1_setting_no);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 消息
            case R.id.setting_message:
                Statistics.userBehavior(SendPoint.menu_me_setting_newmsgalert);

                if (messageStatus) {
                    messageStatus = false;
                    PSP.getInstance().put(Constant.SETTING_MESSAGE, messageStatus);
                    setting_message_iv.setBackgroundResource(R.drawable.f1_setting_no);
                } else {
                    messageStatus = true;
                    PSP.getInstance().put(Constant.SETTING_MESSAGE, messageStatus);
                    setting_message_iv.setBackgroundResource(R.drawable.f1_setting_ok);
                }
                break;

            // 震动
            case R.id.setting_vibration:
                Statistics.userBehavior(SendPoint.menu_me_setting_shockalert);

                if (vibrationStatus) {
                    vibrationStatus = false;
                    PSP.getInstance().put(Constant.SETTING_VIBRATION, vibrationStatus);
                    setting_vibration_iv.setBackgroundResource(R.drawable.f1_setting_no);
                } else {
                    vibrationStatus = true;
                    PSP.getInstance().put(Constant.SETTING_VIBRATION, vibrationStatus);
                    setting_vibration_iv.setBackgroundResource(R.drawable.f1_setting_ok);
                }
                break;

            // 声音
            case R.id.setting_voice:
                Statistics.userBehavior(SendPoint.menu_me_setting_soundalert);

                if (voiceStatus) {
                    voiceStatus = false;
                    PSP.getInstance().put(Constant.SETTING_VOICE, voiceStatus);
                    setting_voice_iv.setBackgroundResource(R.drawable.f1_setting_no);
                } else {
                    voiceStatus = true;
                    PSP.getInstance().put(Constant.SETTING_VOICE, voiceStatus);
                    setting_voice_iv.setBackgroundResource(R.drawable.f1_setting_ok);
                }
                break;

            // 退出消息
            case R.id.setting_quit_message:
                Statistics.userBehavior(SendPoint.menu_me_setting_exitmsgalert);
                if (quitMessageStatus) {
                    quitMessageStatus = false;
                    PSP.getInstance().put(Constant.SETTING_QUIT_MESSAGE, quitMessageStatus);
                    setting_quit_message_iv.setBackgroundResource(R.drawable.f1_setting_no);
                } else {
                    quitMessageStatus = true;
                    PSP.getInstance().put(Constant.SETTING_QUIT_MESSAGE, quitMessageStatus);
                    setting_quit_message_iv.setBackgroundResource(R.drawable.f1_setting_ok);
                }
                break;

            // 修改密码
            case R.id.setting_modifypwd:
                Statistics.userBehavior(SendPoint.menu_me_setting_modifypassword);
                UIShow.showModifyAct(this);
                break;

            // 意见反馈
            case R.id.setting_feedback:
                Statistics.userBehavior(SendPoint.menu_me_setting_feedback);
                UIShow.showSuggestAct(this);
                break;

            // 软件更新
            case R.id.setting_ll_update:
                Statistics.userBehavior(SendPoint.menu_me_setting_checkupdates);
                ModuleMgr.getCommonMgr().checkUpdate(this, UpgradeSource.Setting, true);//检查应用升级
                break;

            // 活动相关
            case R.id.setting_action:
                Statistics.userBehavior(SendPoint.menu_me_setting_huodong);
                UIShow.showActionActivity(this);
                break;

            // 清除缓存
            case R.id.setting_clear_cache:
                Statistics.userBehavior(SendPoint.menu_me_setting_clearcache);
                clearAppCache();
                break;

            // 退出登录
            case R.id.setting_logoff:
                Statistics.userBehavior(SendPoint.menu_me_setting_signout);
                PickerDialogUtil.showSimpleTipDialog(this, new SimpleTipDialog.ConfirmListener() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onSubmit() {
                        exitLogin();
                    }
                }, getResources().getString(R.string.dal_exit_content), getResources().getString(R.string.dal_exit_title), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), true);
                break;

            //视频通话开关
            case R.id.setting_video_switch:
                Statistics.userBehavior(SendPoint.menu_me_setting_enablevideo);
                SourcePoint.getInstance().lockSource(this, "enablevideo");
                if (validChange()) {
                    final boolean statusTmp = !videoStatus;
                    ModuleMgr.getCommonMgr().setVideochatConfig(statusTmp, videoVerifyBean.getBooleanAudiochat(), new RequestComplete() {
                        @Override
                        public void onRequestComplete(HttpResponse response) {
                            // 服务器返回成功的话再重置状态
                            setVideoAndAudioStatus(response.isOk() ? statusTmp : videoStatus, audioStatus);
                        }
                    });
                }
                break;

            //语音通话开关
            case R.id.setting_audio_switch: {
                Statistics.userBehavior(SendPoint.menu_me_setting_enablevoice);
                SourcePoint.getInstance().lockSource(this, "enablevoice");
                if (validChange()) {
                    final boolean statusTmp = !audioStatus;
                    ModuleMgr.getCommonMgr().setVideochatConfig(videoStatus, statusTmp, new RequestComplete() {
                        @Override
                        public void onRequestComplete(HttpResponse response) {
                            // 服务器返回成功的话再重置状态
                            setVideoAndAudioStatus(videoStatus, response.isOk() ? statusTmp : audioStatus);
                        }
                    });
                }
                break;
            }

            // 跳转关于页面
            case R.id.setting_about:
                Statistics.userBehavior(SendPoint.menu_me_setting_about);
                UIShow.showAboutAct(this);
                break;
            //开播提醒
            case R.id.setting_start_live_tip:
                UIShow.showLiveStartTipAct(this, mLiveStartLiveTipDetailList);
                break;

            default:
                break;
        }
    }

    /**
     * 切换音视频开关配置
     */
    public boolean validChange() {
        //开启音、视频通话时，男性用户判断是否VIP
        if (myInfo.getGender() == 1 && !myInfo.isVip()) {

            PickerDialogUtil.showSimpleTipDialogExt(SettingAct.this, new SimpleTipDialog.ConfirmListener() {
                @Override
                public void onCancel() {
                }

                @Override
                public void onSubmit() {
                    UIShow.showOpenVipActivity(SettingAct.this);
                }
            }, getResources().getString(R.string.dal_vip_content), "", getResources().getString(R.string.cancel), getResources().getString(R.string.dal_vip_open), true, R.color.text_zhuyao_black);
            return false;
        }
        //开启音、视频通话时，女性用户判断是否视频认证
        if (myInfo.getGender() == 2) {
            if (videoVerifyBean.getStatus() == 0 || videoVerifyBean.getStatus() == 2) {
                PickerDialogUtil.showSimpleTipDialogExt(SettingAct.this, new SimpleTipDialog.ConfirmListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onSubmit() {
                        UIShow.showMyAuthenticationVideoAct(SettingAct.this, 0);
                    }
                }, getResources().getString(R.string.dal_auth_content), "", getResources().getString(R.string.cancel), getResources().getString(R.string.dal_auth_open), true, R.color.text_zhuyao_black);
                return false;
            } else if (videoVerifyBean.getStatus() == 1) {
                PToast.showShort(getResources().getString(R.string.toast_under_review));
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 200) {
            exitLogin();
        }
    }

    /**
     * 清除当前登录的用户信息并退出
     */
    public void exitLogin() {
        ModuleMgr.getLoginMgr().logout();
        ModuleMgr.getCenterMgr().clearUserInfo();
        ModuleMgr.getCommonMgr().clearIdCardVerifyStatusInfo();

        setResult(200);
        finish();
    }

    /**
     * 清除软件缓存
     */
    public void clearAppCache() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    PToast.showLong(getResources().getString(R.string.toast_clearcache_ok));
                    setting_clear_cache_tv.setText("0KB");
                } else {
                    PToast.showLong(getResources().getString(R.string.toast_clearcache_error));
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    ModuleMgr.getCommonMgr().clearCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }
}