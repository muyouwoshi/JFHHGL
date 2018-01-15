package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.PrivateMessage;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 通用私密布局
 */
public class ChatPanelPrivateCommon extends BasePanel implements View.OnClickListener {

    private ImageView imgPic;
    private TextView tvTitle, tvLook;

    private PrivateMessage msg;
    private int type;           //私密类型
    ChatAdapter.ChatInstance chatInstance;
    private UserInfoLightweight infoLightweight;

    public ChatPanelPrivateCommon(Context context, ChatAdapter.ChatInstance chatInstance, UserInfoLightweight infoLightweight) {
        super(context);
        setContentView(R.layout.f1_chat_item_panel_private_common);
        this.chatInstance = chatInstance;
        this.infoLightweight = infoLightweight;

        initView();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_invite_tv_title);
        tvLook = (TextView) findViewById(R.id.tv_invite_tv_look);
        imgPic = (ImageView) findViewById(R.id.iv_invite_img);

        findViewById(R.id.rl_container).setOnClickListener(this);
    }

    public void reSetData(PrivateMessage msg) {
        //int type,giftId; //1消息、2语音、3视频、4图片
        if (msg == null) return;
        this.msg = msg;
        if (infoLightweight != null)
            msg.setAvatar(infoLightweight.getAvatar());
        BaseMessage.BaseMessageType messageType = BaseMessage.BaseMessageType.valueOf(msg.getType());
        if (messageType == BaseMessage.BaseMessageType.privatePhoto) {
            imgPic.setBackgroundResource(R.drawable.f1_th04);
            tvTitle.setText(getContext().getString(R.string.send_private, getContext().getString(R.string.photo)));
            tvLook.setText(R.string.immediately_check);
            type = 4;
        } else if (messageType == BaseMessage.BaseMessageType.privateVideo) {
            imgPic.setBackgroundResource(R.drawable.f1_th02);
            tvTitle.setText(getContext().getString(R.string.send_private, getContext().getString(R.string.video)));
            tvLook.setText(R.string.play_immediately);
            type = 3;
        } else if (messageType == BaseMessage.BaseMessageType.privateVoice) {
            if (!TextUtils.isEmpty(msg.getVoiceUrl())) { //私密语音
                //私密语音
                imgPic.setBackgroundResource(R.drawable.f1_th01);
                tvTitle.setText(getContext().getString(R.string.send_private, getContext().getString(R.string.audio)));
                tvLook.setText(R.string.open_to_hear);
                type = 2;
            } else {  //私密消息
                imgPic.setBackgroundResource(R.drawable.f1_th03);
                tvTitle.setText(getContext().getString(R.string.send_private, getContext().getString(R.string.message)));
                tvLook.setText(R.string.immediately_check);
                type = 1;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_container:
                if (msg == null) break;
                StatisticsMessage.chatViewPrivate(msg.getLWhisperID(), type);

                //打开打赏弹框
                UIShow.showLookPrivateDlg(App.activity, msg, type);
                break;
            default:
                break;
        }
    }
}
