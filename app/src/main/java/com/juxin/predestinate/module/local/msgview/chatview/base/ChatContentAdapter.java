package com.juxin.predestinate.module.local.msgview.chatview.base;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.CommonMessage;
import com.juxin.predestinate.module.local.chat.msgtype.GiftRedPackageMesaage;
import com.juxin.predestinate.module.local.chat.msgtype.PrivateMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.ChatMsgType;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kind on 2017/3/30.
 */
public class ChatContentAdapter extends ExBaseAdapter<BaseMessage> {

    private ChatAdapter.ChatInstance chatInstance = null;

    public ChatContentAdapter(Context context, List<BaseMessage> datas) {
        super(context, datas);
    }

    public ChatAdapter.ChatInstance getChatInstance() {
        return chatInstance;
    }

    public void setChatInstance(ChatAdapter.ChatInstance chatInstance) {
        this.chatInstance = chatInstance;
    }

    /**
     * 将更新的消息添加到消息列表中。如果已经存在的替换，新消息则添加的末尾。<br>
     * 注意区分发送的消息还是接收到的消息。
     *
     * @param message 需要更新的消息。
     */
    public void updateData(BaseMessage message) {
        if (message == null)
            return;

        List<BaseMessage> datas = getList();

        if (datas == null) {
            datas = new ArrayList<>();
            datas.add(message);
            setList(datas);
            return;
        }

        BaseMessage delVideoMsg = null;
        BaseMessage data;
        for (int i = 0; i < datas.size(); i++) {
            data = datas.get(i);
            try {
                if ((message.getType() == data.getType())
                        && (BaseMessage.BaseMessageType.video.getMsgType() == message.getType()
                        || BaseMessage.BaseMessageType.chumInvite.getMsgType() == message.getType()
                        || BaseMessage.BaseMessageType.chumTask.getMsgType() == message.getType())
                        && (message.getSpecialMsgID() == data.getSpecialMsgID())) {
                    delVideoMsg = data;
                } else if (message.getType() == data.getType() && data.getSpecialMsgID() == message.getSpecialMsgID()
                        && BaseMessage.BaseMessageType.giftRedPackage.getMsgType() == message.getType()) {
                    GiftRedPackageMesaage giftRedPackageMsg = (GiftRedPackageMesaage) message;
                    if (giftRedPackageMsg.getFrom_type() == 2 || giftRedPackageMsg.getFrom_type() == 3) {//2通知红包已收取3系统已自动帮你领取
                        data.setfStatus(0);
                        notifyDataSetChanged();
                        return;
                    }
                } else {
                    if (message.isSender()) {
                        if ((message.getcMsgID() > 0 && data.getcMsgID() == message.getcMsgID())) {
                            // 本地发送的消息更新
                            datas.set(i, message);
                            notifyDataSetChanged();
                            return;
                        }

                        if ((message.getMsgID() > 0 && data.getMsgID() == message.getMsgID())) {
                            datas.set(i, message);
                            notifyDataSetChanged();
                            return;
                        }
                    } else {
                        if (data.getMsgID() == message.getMsgID() && data.getcMsgID() == message.getcMsgID()) {
                            datas.set(i, message);
                            notifyDataSetChanged();
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (delVideoMsg != null) {
            datas.remove(delVideoMsg);
        }

        datas.add(message);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;

        if (convertView == null) {
            vh = new ViewHolder();
            convertView = inflate(R.layout.p1_chat_item);
            convertView.setTag(vh);

            vh.time = (TextView) convertView.findViewById(R.id.chat_item_time);

            ChatItemHolder cih;

            cih = (ChatItemHolder) vh.receiver;
            cih.parent = convertView.findViewById(R.id.chat_item_left);
            cih.head = (ImageView) cih.parent.findViewById(R.id.chat_item_head);
            cih.nobility = (ImageView) cih.parent.findViewById(R.id.chat_item_nobility);
            cih.content = (ViewGroup) cih.parent.findViewById(R.id.chat_item_content);
            cih.status = (TextView) cih.parent.findViewById(R.id.chat_item_status);
            cih.statusImg = (ImageView) cih.parent.findViewById(R.id.chat_item_status_img);
            cih.statusImgError = (ImageView) cih.parent.findViewById(R.id.chat_item_status_img_error);
            cih.tvContent = (TextView) cih.parent.findViewById(R.id.chat_item_tv_content);
            cih.statusProgress = (ProgressBar) cih.parent.findViewById(R.id.chat_item_status_progress);
            cih.statusError = (ImageView) cih.parent.findViewById(R.id.chat_item_status_error);
            cih.downprogress = (ImageView) cih.parent.findViewById(R.id.chat_item_down_progress);

            cih = (ChatItemHolder) vh.sender;
            cih.parent = convertView.findViewById(R.id.chat_item_right);
            cih.head = (ImageView) cih.parent.findViewById(R.id.chat_item_head);
            cih.nobility = (ImageView) cih.parent.findViewById(R.id.chat_item_nobility);
            cih.content = (ViewGroup) cih.parent.findViewById(R.id.chat_item_content);
            cih.status = (TextView) cih.parent.findViewById(R.id.chat_item_status);
            cih.statusImg = (ImageView) cih.parent.findViewById(R.id.chat_item_status_img);
            cih.statusImgError = (ImageView) cih.parent.findViewById(R.id.chat_item_status_img_error);
            cih.tvContent = (TextView) cih.parent.findViewById(R.id.chat_item_tv_content);
            cih.statusProgress = (ProgressBar) cih.parent.findViewById(R.id.chat_item_status_progress);
            cih.statusError = (ImageView) cih.parent.findViewById(R.id.chat_item_status_error);
            cih.downprogress = (ImageView) cih.parent.findViewById(R.id.chat_item_down_progress);

            ChatViewHolder cvh = vh.custom;
            cvh.parent = convertView.findViewById(R.id.chat_item_custom);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        BaseMessage msgData = getItem(position);
        vh.reset(ChatAdapter.isSender(msgData.getSendID()), msgData, getItem(position - 1));
        return convertView;
    }

    /**
     * 左右的布局是不一样的，因此都要准备好各自的布局。
     */
    private class ViewHolder {
        public TextView time;

        public ChatViewHolder receiver = new ChatItemHolder();
        public ChatViewHolder sender = new ChatItemHolder();
        public ChatViewHolder custom = new ChatCustomHolder();

        public void reset(boolean sender, BaseMessage msgData, BaseMessage preMsgData) {
            if (msgData == null) {
                return;
            }

            String tipTime = "";
            if (preMsgData != null) {
                long tmp = TimeUtil.onPad(msgData.getTime());
                long temp = TimeUtil.onPad(preMsgData.getTime());
                if (tmp - temp > Constant.CHAT_SHOW_TIP_TIME_Interval) {
                    tipTime = TimeUtil.getFormatTimeChatTip(tmp);
                }
            }

            if (TextUtils.isEmpty(tipTime)) {
                time.setVisibility(View.GONE);
            } else {
                time.setVisibility(View.VISIBLE);
                time.setText(tipTime);
            }

            // 提高效率
            if (msgData.isCustomMsgPanel()) {
                this.receiver.parent.setVisibility(View.GONE);
                this.sender.parent.setVisibility(View.GONE);
                this.custom.parent.setVisibility(View.VISIBLE);

                this.custom.reset(msgData, sender);
            } else {
                this.custom.parent.setVisibility(View.GONE);
                if (sender) {
                    this.receiver.parent.setVisibility(View.GONE);
                    this.sender.parent.setVisibility(View.VISIBLE);
                    this.sender.reset(msgData, sender);
                } else {
                    this.sender.parent.setVisibility(View.GONE);
                    this.receiver.parent.setVisibility(View.VISIBLE);
                    this.receiver.reset(msgData, sender);
                }
            }
        }
    }

    public abstract class ChatViewHolder {
        public View parent;

        public ChatPanel chatpanel;
        public BaseMessage msg;
        public Map<String, ChatPanel> msgPanel = new HashMap<String, ChatPanel>();

        public void reset(BaseMessage msgData, boolean sender) {
            msg = msgData;

            if (chatpanel != null) {
                if (!ChatMsgType.isMatchPanel(chatpanel, msg.getType())) {
                    chatpanel.setVisibility(View.GONE);
                    chatpanel = msgPanel.get(ChatMsgType.getPanelClassName(msg.getType()));

                    if (chatpanel != null) {
                        chatpanel.setVisibility(View.VISIBLE);
                    }
                }
            }

            ChatPanel chatpanelTemp = getChatInstance().chatAdapter.getItemPanel(chatpanel, msgData, chatInstance, sender);
            if (chatpanelTemp != null && this instanceof ChatItemHolder) {
                chatpanelTemp.setInit(msgData);
            }

            if (chatpanelTemp != chatpanel && chatpanelTemp != null) {
                chatpanel = chatpanelTemp;

                msgPanel.put(ChatMsgType.getPanelClassName(msg.getType()), chatpanelTemp);

                if (this instanceof ChatItemHolder) {
                    chatpanelTemp.setChatItemHolder((ChatItemHolder) this);
                }

                adjustMargin(msgData, sender);
                init(sender);
            }
        }

        public abstract void init(boolean sender);

        public void adjustMargin(BaseMessage msgData, boolean sender) {
            ViewGroup layoutVG;
            if (sender) {
                layoutVG = (ViewGroup) parent.findViewById(R.id.chat_item_right_layout);

                if (layoutVG != null) {
                    ViewGroup.LayoutParams layoutvgLp = layoutVG.getLayoutParams();
                    layoutvgLp.width = UIUtil.dp2px(msgData.getDisplayWidth());
                    layoutVG.setLayoutParams(layoutvgLp);
                }
            } else {
                layoutVG = (ViewGroup) parent.findViewById(R.id.chat_item_left_layout);

                if (layoutVG != null) {
                    RelativeLayout.LayoutParams layoutVG_LP = (RelativeLayout.LayoutParams) layoutVG.getLayoutParams();
                    layoutVG_LP.setMargins(0, 0, UIUtil.dp2px(msgData.getDisplayWidth()), 0);
                    layoutVG.setLayoutParams(layoutVG_LP);
                }
            }
        }

        /**
         * 是否显示背景边框
         *
         * @param sender
         * @param viewGroup
         */
        public void showLayout(boolean sender, ViewGroup viewGroup) {
            if (chatpanel == null || viewGroup == null)
                return;

            if (chatpanel.isShowParentLayout()) {
                if (sender) {
                    viewGroup.setBackgroundResource(R.drawable.y1_talk_box_24x24_me);
                } else {
                    viewGroup.setBackgroundResource(R.drawable.y1_talk_box_24x24);
                }
            } else {
                viewGroup.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        }
    }

    public class ChatItemHolder extends ChatViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public ImageView head, nobility;
        public ViewGroup content;
        public TextView status, tvContent;
        public ImageView statusImg, statusError;
        private ProgressBar statusProgress;
        private ImageView statusImgError, downprogress;

        @Override
        public void init(boolean sender) {
            showLayout(sender, content);
            content.addView(chatpanel.getContentView());

            // 注册事件
            head.setOnClickListener(this);
            status.setOnClickListener(this);
            statusError.setOnClickListener(this);

            content.setOnClickListener(this);
            content.setOnLongClickListener(this);
        }

        @Override
        public void reset(BaseMessage msgData, boolean sender) {
            super.reset(msgData, sender);
            UserInfoLightweight infoLightweight = getChatInstance().chatAdapter.getUserInfo(msg.getSendID());

            if (msgData.getLWhisperID() == MailSpecialID.customerService.getSpecialID() && !msgData.isSender()) {
                ImageLoader.loadCircleAvatar(getContext(), (infoLightweight != null &&
                        !TextUtils.isEmpty(infoLightweight.getAvatar())) ? infoLightweight.getAvatar() : R.drawable.f1_secretary_avatar, head);
            } else {
                ImageLoader.loadCircleAvatar(getContext(), (infoLightweight != null) ? infoLightweight.getAvatar() : "", head);
            }

            if (infoLightweight != null && !MailSpecialID.getMailSpecialID(msgData.getLWhisperID())) {
                NobilityList.Nobility tmpNobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(infoLightweight.getNobility_rank(), infoLightweight.getGender());
                String titleIcon = tmpNobility.getTitle_icon();
                if (!TextUtils.isEmpty(titleIcon)) {
                    ImageLoader.loadCenterCrop(getContext(), titleIcon, nobility);
                }
            }

            if (chatpanel != null) {
                updateStatus(ChatMsgType.getMsgType(msg.getType()), sender);
                chatpanel.reset(msg, infoLightweight);

                showLayout(sender, content);
            } else {
                updateStatus(null, sender);
            }
            if (ChatMsgType.getMsgType(msg.getType()) == ChatMsgType.CMT_2) {//设置音频下载状态
                if (msg != null && msg instanceof CommonMessage) {

                    CommonMessage commonMessage = (CommonMessage) msg;
                    if (commonMessage.getVoiceDownStatus() == 1) {
                        downprogress.setVisibility(View.VISIBLE);
                        downprogress.clearAnimation();

                        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        LinearInterpolator lin = new LinearInterpolator();
                        rotate.setInterpolator(lin);
                        rotate.setDuration(1500);//设置动画持续时间
                        rotate.setRepeatCount(-1);//设置重复次数
                        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
                        downprogress.startAnimation(rotate);
                    } else {
                        downprogress.clearAnimation();
                        downprogress.setVisibility(View.GONE);
                    }
                }
            } else {
                downprogress.setVisibility(View.GONE);
            }
        }

        private void updateStatus(ChatMsgType msgType, boolean sender) {
            if (!sender) {
                status.setVisibility(View.GONE);
                statusProgress.setVisibility(View.GONE);
                statusError.setVisibility(View.GONE);
                if (statusImgError != null)
                    statusImgError.setVisibility(View.GONE);
                if (tvContent != null)
                    tvContent.setVisibility(View.GONE);

                if (MessageConstant.isMaxVersionMsg(msg.getType()) && statusImgError != null) {
                    statusImgError.setVisibility(View.VISIBLE);
                    statusImgError.setBackgroundResource(R.drawable.p1_msg_status_tip);
                }

                if (ChatMsgType.CMT_33 == msgType && msg != null) {
                    PrivateMessage message = (PrivateMessage) msg;
                    if (!TextUtils.isEmpty(message.getVoiceUrl())) {
                        tvContent.setVisibility(View.VISIBLE);
                        tvContent.setText("" + message.getVoiceLen() + "''");
                    }
                }

                if (ChatMsgType.CMT_2 == msgType && msg.getfStatus() == 1) {
                    CommonMessage message = (CommonMessage) msg;
                    if (!TextUtils.isEmpty(message.getVoiceUrl()) && message.getVoiceLen() > 0) {
                        statusImg.setVisibility(View.VISIBLE);
                        return;
                    }
                }

                statusImg.setVisibility(View.GONE);
                return;
            }

            statusImg.setVisibility(View.GONE);
            statusError.setVisibility(View.GONE);
            if (msg.getStatus() != MessageConstant.SENDING_STATUS) {
                statusProgress.setVisibility(View.VISIBLE);
                status.setVisibility(View.INVISIBLE);
            }

            switch (msg.getStatus()) {
                case 1: // 发送成功
                    status.setText("已发送");
                    break;

                case 2: // 发送失败
                    status.setText("失败");
                    break;

                case 5: // 送达
                    status.setText("送达");
                    break;

                case 11: // 已读
                    status.setText("已读");
                    break;

                default: // "未知状态" + msg.getStatus()
                    status.setVisibility(View.GONE);
                    break;
            }

            if (msg.getStatus() == MessageConstant.SENDING_STATUS) {//发送中,
                long time = msg.getCurrentTime() - msg.getTime();
                if (time >= 90000) {
                    statusProgress.setVisibility(View.GONE);
                    status.setVisibility(View.GONE);
                    statusError.setVisibility(View.VISIBLE);
                } else {
                    statusProgress.setVisibility(View.VISIBLE);
                    status.setVisibility(View.GONE);
                    statusError.setVisibility(View.GONE);
                }
            } else if (msg.getStatus() == MessageConstant.FAIL_STATUS ||
                    msg.getStatus() == MessageConstant.BLACKLIST_STATUS) {//发送失败
                statusProgress.setVisibility(View.GONE);
                status.setVisibility(View.GONE);
                statusError.setVisibility(View.VISIBLE);
            } else if (msg.getStatus() == MessageConstant.OK_STATUS
                    || msg.getStatus() == MessageConstant.READ_STATUS ||
                    msg.getStatus() == MessageConstant.DELIVERY_STATUS) {//状态
                statusProgress.setVisibility(View.GONE);
                status.setVisibility(View.VISIBLE);
                statusError.setVisibility(View.GONE);
            } else {
                statusProgress.setVisibility(View.GONE);
                status.setVisibility(View.GONE);
                statusError.setVisibility(View.GONE);
            }

            BaseMessage.BaseMessageType messageType = BaseMessage.BaseMessageType.valueOf(msg.getType());
            if (messageType != BaseMessage.BaseMessageType.common && messageType != BaseMessage.BaseMessageType.gift) {
                status.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.chat_item_head:
                    if (chatpanel != null) {
                        chatpanel.onClickHead(msg);
                    }
                    break;

                case R.id.chat_item_status:
                    if (chatpanel != null) {
                        chatpanel.onClickStatus(msg);
                    }
                    break;

                case R.id.chat_item_content:
                    if (chatpanel != null) {
                        chatpanel.onClickContent(msg, false);
                    }
                    break;
                case R.id.chat_item_status_error:
                    if (chatpanel != null) {
                        chatpanel.onClickErrorResend(msg);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.chat_item_head:
                    if (chatpanel != null) {
                        chatpanel.onClickHead(msg);
                    }
                    break;

                case R.id.chat_item_status:
                    if (chatpanel != null) {
                        chatpanel.onClickStatus(msg);
                    }
                    break;

                case R.id.chat_item_content:
                    if (chatpanel != null) {
                        chatpanel.onClickContent(msg, true);
                        return true;
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    public class ChatCustomHolder extends ChatViewHolder {
        @Override
        public void init(boolean sender) {
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).addView(chatpanel.getContentView());
            }
        }

        @Override
        public void reset(BaseMessage msgData, boolean sender) {
            super.reset(msgData, sender);

            if (chatpanel != null) {
                chatpanel.reset(msgData, null);
            }
        }
    }
}
