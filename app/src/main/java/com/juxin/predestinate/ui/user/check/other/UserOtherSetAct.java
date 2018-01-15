package com.juxin.predestinate.ui.user.check.other;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.Msg;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.center.user.others.UserBlack;
import com.juxin.predestinate.bean.db.DBCallback;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.PickerDialogUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.check.bean.VideoSetting;
import com.juxin.predestinate.ui.user.check.edit.EditKey;
import com.juxin.predestinate.ui.user.check.edit.custom.EditPopupWindow;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

import java.util.HashMap;
import java.util.Map;

/**
 * 他人： 资料设置
 * Created by Su on 2017/4/13.
 */
public class UserOtherSetAct extends BaseActivity implements RequestComplete {
    private TextView user_nick, user_id, user_remark;
    private ImageView user_head;
    private View edit_top;

    // 音视频配置
    private VideoSetting videoSetting;
    private SeekBar videoBar, voiceBar, shieldBar;
    private boolean videoBarStatus, voiceBarStatus, shieldBarStatus;

    private UserDetail userDetail; // 他人资料
    private String tempRemark;       // 临时备注名称
    private int channel = CenterConstant.USER_SET_FROM_CHECK;  // 默认个人主页跳转

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_user_other_set_act);
        setTitle(getString(R.string.user_other_info_set));
        setBackView();

        initData();
        initView();
    }

    private void initView() {
        initSeekBar();
        edit_top = findViewById(R.id.edit_top);
        user_head = (ImageView) findViewById(R.id.user_head);
        user_nick = (TextView) findViewById(R.id.user_nick);
        user_id = (TextView) findViewById(R.id.user_id);
        user_remark = (TextView) findViewById(R.id.user_remark);
        if (userDetail == null) return;

        user_remark.setOnClickListener(listener);
        findViewById(R.id.rl_clear).setOnClickListener(listener);
        findViewById(R.id.rl_complain).setOnClickListener(listener);
        findViewById(R.id.ll_edit).setOnClickListener(listener);

        ImageLoader.loadRoundAvatar(this, userDetail.getAvatar(), user_head);
        user_nick.setText(userDetail.getNickname());
        user_id.setText("ID: " + userDetail.getUid());
        user_remark.setText(userDetail.getRemark());
    }

    private void initData() {
        userDetail = getIntent().getParcelableExtra(CenterConstant.USER_CHECK_OTHER_KEY);
        channel = getIntent().getIntExtra(CenterConstant.USER_SET_KEY, CenterConstant.USER_SET_FROM_CHECK);
        if (userDetail == null) {
            PToast.showShort(getString(R.string.user_other_info_req_fail));
            return;
        }

        ModuleMgr.getCenterMgr().reqIsBlack(userDetail.getUid(), this);                  // 用户是否处于黑名单
        //ModuleMgr.getCenterMgr().reqGetOpposingVideoSetting(userProfile.getUid(), this);  // 请求用户接受音视频配置
    }

    private NoDoubleClickListener listener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.ll_edit:
                    Statistics.userBehavior(SendPoint.userinfo_face, userDetail.getUid());
                    if (channel == CenterConstant.USER_SET_FROM_CHAT) {
                        UIShow.showCheckOtherInfoAct(UserOtherSetAct.this, userDetail);
                        return;
                    }
                    finish();
                    break;

                case R.id.user_remark:
                    EditPopupWindow popupWindow = new EditPopupWindow(UserOtherSetAct.this, EditKey.s_key_remark_name, user_remark);
                    popupWindow.showPopupWindow(edit_top);
                    popupWindow.setEditPopupWindowListener(new EditPopupWindow.EditPopupWindowListener() {
                        @Override
                        public void editFinish(String text) {
                            tempRemark = text;
                            StatisticsUser.userRemark(userDetail.getUid(), text);
                            ModuleMgr.getCenterMgr().reqSetRemarkName(userDetail.getUid(), text, UserOtherSetAct.this);
                        }
                    });
                    break;

                case R.id.rl_clear:     // 清空聊天记录
                    Statistics.userBehavior(SendPoint.userinfo_more_setting_clear, userDetail.getUid());
                    clearRecord();
                    break;

                case R.id.rl_complain:  // 投诉，跳转举报
                    Statistics.userBehavior(SendPoint.userinfo_more_setting_jubao, userDetail.getUid());
                    UIShow.showDefriendAct(userDetail.getUid(), UserOtherSetAct.this);
                    break;
            }
        }
    };

    private void initSeekBar() {
        videoBar = (SeekBar) findViewById(R.id.sb_accept_video);
        voiceBar = (SeekBar) findViewById(R.id.sb_accept_voice);
        shieldBar = (SeekBar) findViewById(R.id.sb_msg_shield);

        videoBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (videoBarStatus) {
                        videoBarStatus = false;
                        videoBar.setProgress(0);
                    } else {
                        videoBarStatus = true;
                        videoBar.setProgress(100);
                    }
                }
                return true;
            }
        });

        voiceBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (voiceBarStatus) {
                        voiceBarStatus = false;
                        voiceBar.setProgress(0);
                    } else {
                        voiceBarStatus = true;
                        voiceBar.setProgress(100);
                    }
                }
                return true;
            }
        });

        shieldBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Statistics.userBehavior(SendPoint.userinfo_more_setting_shield, userDetail.getUid());
                    if (shieldBarStatus) {
                        shieldBarStatus = false;
                        shieldBar.setProgress(0);
                    } else {
                        shieldBarStatus = true;
                        shieldBar.setProgress(100);
                    }
                }
                return true;
            }
        });
    }

    /**
     * 初始化视频SeekBar
     */
    private void initVideoBar() {
        if (videoSetting.getAcceptvideo() == 1) {  // 接受
            videoBarStatus = true;
            videoBar.setProgress(100);
        } else {
            videoBarStatus = false;
            videoBar.setProgress(0);
        }
    }

    /**
     * 初始化音频SeekBar
     */
    private void initVoiceBar() {
        if (videoSetting.getAcceptvoice() == 1) {  // 接受
            voiceBarStatus = true;
            voiceBar.setProgress(100);
            return;
        }
        voiceBarStatus = false;
        voiceBar.setProgress(0);
    }

    /**
     * 初始化拉黑seekBar
     */
    private void initShieldBar(boolean isBlack) {
        if (isBlack) {  // 处于黑名单
            shieldBarStatus = true;
            shieldBar.setProgress(100);
            return;
        }
        shieldBarStatus = false;
        shieldBar.setProgress(0);
    }

    /**
     * 配置是否接受音视频
     */
    private void reqSetVideoSetting() {
        int videoSet = videoBarStatus ? 1 : 0;
        int voiceSet = voiceBarStatus ? 1 : 0;

        if (userDetail == null) return;

        if (videoSetting == null) {
            ModuleMgr.getCenterMgr().reqSetOpposingVideoSetting(userDetail.getUid(), videoSet, voiceSet, this);
            return;
        }

        if (videoSetting.getAcceptvideo() == videoSet
                && videoSetting.getAcceptvoice() == voiceSet)
            return;
        ModuleMgr.getCenterMgr().reqSetOpposingVideoSetting(userDetail.getUid(), videoSet, voiceSet, this);
    }

    /**
     * 拉黑、取消拉黑
     */
    private void reqAddOrRemoveBlack() {
        if (userDetail == null) return;
        if (shieldBarStatus) {
            ModuleMgr.getCenterMgr().reqAddBlack(userDetail.getUid(), this);
            return;
        }
        ModuleMgr.getCenterMgr().reqRemoveBlack(userDetail.getUid(), this);
        StatisticsMessage.blackRemove(userDetail.getUid());
    }

    /**
     * 清除聊天记录
     */
    private void clearRecord() {
        PickerDialogUtil.showSimpleAlertDialog(this, new SimpleTipDialog.ConfirmListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onSubmit() {
                ModuleMgr.getChatListMgr().deleteFmessage(userDetail.getUid(), new DBCallback() {
                    @Override
                    public void OnDBExecuted(long result) {
                        if (result != MessageConstant.ERROR) {
                            MsgMgr.getInstance().sendMsg(MsgType.MT_Chat_Clear_History, userDetail == null ? 0 : userDetail.getUid());
                            PToast.showShort(getString(R.string.user_other_set_chat_del_suc));
                            return;
                        }
                        PToast.showShort(getString(R.string.user_other_set_chat_del_fail));
                    }
                });

            }
        }, getString(R.string.user_other_set_chat_del), R.color.text_zhuyao_black, "");
    }

    private void setResult() {
        Intent data = new Intent();
        data.putExtra("remark", tempRemark);
        setResult(CenterConstant.USER_SET_RESULT_CODE, data);
    }

    /**
     * 刷新聊天列表
     */
    private void refreshChatList() {
        UserInfoLightweight temp = new UserInfoLightweight();
        Map<String, Object> params = new HashMap<>();
        params.put("avatar", userDetail.getAvatar());
        params.put("remark", tempRemark);
        params.put("nickname", userDetail.getNickname());
        params.put("group", userDetail.getGroup());
        params.put("top", userDetail.getTopN());

        temp.setTime(ModuleMgr.getAppMgr().getTime());
        temp.setUid(userDetail.getUid());
        temp.setInfoJson(JSON.toJSONString(params));

        ModuleMgr.getChatMgr().updateUserInfoLight(temp, null);
    }


    @Override
    public void onRequestComplete(HttpResponse response) {
        // 设置备注
        if (response.getUrlParam() == UrlParam.reqSetRemarkName) {
            if (!response.isOk()) {
                PToast.showShort(getString(R.string.user_info_set_fail));
                return;
            }
            Msg msg = new Msg();
            msg.setKey(String.valueOf(userDetail.getUid()));
            msg.setData(tempRemark);
            MsgMgr.getInstance().sendMsg(MsgType.MT_REMARK_INFORM, msg);//昵称修改完成通知
            setResult();
            refreshChatList();
            userDetail.setRemark(tempRemark);
            user_remark.setText(TextUtils.isEmpty(tempRemark) ? "" : tempRemark);
        }

        // 用户拉黑状态
        if (response.getUrlParam() == UrlParam.reqIsBlack) {
            if (!response.isOk()) return;

            UserBlack userBlack = (UserBlack) response.getBaseData();
            initShieldBar(userBlack.inBlack());
        }

        // 获取音视频配置
        if (response.getUrlParam() == UrlParam.reqGetOpposingVideoSetting) {
            if (!response.isOk()) {
                return;
            }
            videoSetting = (VideoSetting) response.getBaseData();
            if (videoSetting == null) return;
            initVideoBar();
            initVoiceBar();
        }else if (response.getUrlParam() == UrlParam.reqAddBlack) {
            if (response.isOk()) {
                sendIsBlackMsg(true);
                return;
            }
        }else if (response.getUrlParam() == UrlParam.reqRemoveBlack) {
            if (response.isOk()) {
                sendIsBlackMsg(false);
                return;
            }
        }
    }

    private void sendIsBlackMsg(boolean inBlack){
        if (userDetail != null){
            Msg msg = new Msg();
            msg.setKey(String.valueOf(userDetail.getUid()));
            msg.setData(inBlack);
            MsgMgr.getInstance().sendMsg(MsgType.MT_BLACK_STATUS, msg);//拉黑状态通知
        }
    }

    @Override
    protected void onDestroy() {
        //reqSetVideoSetting();
        reqAddOrRemoveBlack();
        super.onDestroy();
    }
}
