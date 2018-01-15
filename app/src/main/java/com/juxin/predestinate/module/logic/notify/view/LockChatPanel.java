package com.juxin.predestinate.module.logic.notify.view;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.utils.InputUtils;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.statistics.Source;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.invoke.Invoker;
import com.juxin.predestinate.module.logic.notify.LockScreenMgr;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 锁屏弹窗：聊天，一次只显示与一位用户的聊天信息
 */
public class LockChatPanel extends BasePanel implements OnClickListener {

    private View ll_reply_container;
    private TextView wake_lock_nickname, wake_lock_message;
    private EditText wake_lock_content;

    private UserInfoLightweight userInfo;
    private BaseMessage baseMessage;

    public LockChatPanel(Context context, UserInfoLightweight userInfo, BaseMessage baseMessage, String content) {
        super(context);
        setContentView(R.layout.common_wake_lockact);
        initView();

        refresh(userInfo, baseMessage, content);
    }

    private void initView() {
        ll_reply_container = findViewById(R.id.ll_reply_container);
        wake_lock_nickname = (TextView) findViewById(R.id.wake_lock_nickname);
        wake_lock_message = (TextView) findViewById(R.id.wake_lock_message);
        wake_lock_content = (EditText) findViewById(R.id.wake_lock_content);

        findViewById(R.id.wake_lock_send).setOnClickListener(this);
        findViewById(R.id.wake_lock_off).setOnClickListener(this);
        wake_lock_message.setOnClickListener(this);
        wake_lock_nickname.setOnClickListener(this);
    }

    /**
     * 刷新锁屏聊天信息
     *
     * @param userInfo    轻量级用户资料
     * @param baseMessage 预先传递消息内容，以处理特殊的情况
     * @param content     聊天内容
     */
    public void refresh(UserInfoLightweight userInfo, BaseMessage baseMessage, String content) {
        this.userInfo = userInfo;
        this.baseMessage = baseMessage;

        wake_lock_nickname.setText(TextUtils.isEmpty(userInfo.getNickname()) ?
                String.valueOf(userInfo.getUid()) : userInfo.getNickname());
        wake_lock_message.setText(Html.fromHtml(content));

        // 现阶段设计锁屏弹窗无回复逻辑
//        ll_reply_container.setVisibility(BaseMessage.BaseMessageType.video.getMsgType() == baseMessage.getType()
//                ? View.GONE : View.VISIBLE);
//        wake_lock_content.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wake_lock_off:
                LockScreenMgr.getInstance().closeLockNotify();
                break;
            case R.id.wake_lock_send:
                if (TextUtils.isEmpty(wake_lock_content.getText().toString())) {
                    PToast.showShort(getContext().getString(R.string.message_empty_warn));
                } else {
                    InputUtils.forceClose(wake_lock_content);
                    LockScreenMgr.getInstance().closeLockNotify();
                    Invoker.getInstance().doInApp(null, "cmd_open_chat",
                            "{\"uid\":" + userInfo.getUid() + ",\"reply\":" +
                                    wake_lock_content.getText().toString() + "}");
                }
                break;
            case R.id.wake_lock_message:
            case R.id.wake_lock_nickname:
                LockScreenMgr.getInstance().closeLockNotify();
                // 音视频消息不用打开私聊页面，先进行处理
                if (BaseMessage.BaseMessageType.video.getMsgType() != baseMessage.getType()) {
                    if(App.getActivity() instanceof BaseActivity) ((BaseActivity) App.getActivity()).appendStackNode(Source.TIP.class.getName());
                    UIShow.showMainWithBackMessage(getContext());
                    Invoker.getInstance().doInApp(null, "cmd_open_chat", "{\"uid\":" + userInfo.getUid() + "}");
                }
                break;
            default:
                break;
        }
    }
}
