package com.juxin.predestinate.ui.mail;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.local.chat.utils.SortList;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.mail.item.CustomMailItem;
import com.juxin.predestinate.ui.mail.item.MailItemType;
import com.juxin.predestinate.ui.mail.item.MailMsgID;

import java.util.ArrayList;
import java.util.List;

/**
 * 信箱
 * Created by Kind on 16/1/20.
 */
public class MailFragmentAdapter extends ExBaseAdapter<BaseMessage> {

    private boolean scrollState = false;

    public void setScrollState(boolean scrollState) {
        this.scrollState = scrollState;
    }

    public MailFragmentAdapter(Context context, List<BaseMessage> datas) {
        super(context, datas);
    }

    public int mailItemOrdinarySize() {
        return mailItemOrdinary().size();
    }

    private boolean isSetTitle = false;
    private boolean isSetTitle_two = false;
    private boolean isSetBottom = false;
    private boolean isSetBlankBar = false;

    public List<BaseMessage> mailItemOrdinary() {
        List<BaseMessage> messageList = new ArrayList<>();
        for (BaseMessage tmp : getList()) {
            if (MailItemType.Mail_Item_Title.type != tmp.getMailItemStyle() &&
                    MailItemType.Mail_Item_Title_TWO.type != tmp.getMailItemStyle() &&
                    MailItemType.Mail_Item_BlankBar.type != tmp.getMailItemStyle() &&
                    MailItemType.Mail_Item_Other.type != tmp.getMailItemStyle() &&
                    MailItemType.Mail_Item_Greet.type != tmp.getMailItemStyle() &&
                    MailItemType.Mail_Item_Bottom.type != tmp.getMailItemStyle()) {
                messageList.add(tmp);
            }
        }
        return messageList;
    }

    public BaseMessage mailItemOtherr() {
        BaseMessage message = new BaseMessage();
        for (BaseMessage tmp : getList()) {
            if (MailItemType.Mail_Item_Ordinary == MailItemType.getMailMsgType(tmp.getMailItemStyle())
                    || MailItemType.Mail_Item_Greet == MailItemType.getMailMsgType(tmp.getMailItemStyle())) {
                message = tmp;
                break;
            }
        }
        return message;
    }

    public void updateAllData() {
        List<BaseMessage> messageLists = ModuleMgr.getChatListMgr().getShowMsgList();
        PLogger.d("messageLists=多少人=" + messageLists.size());

        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        String yCoinUserid = userDetail.getyCoinUserid();
        boolean needTopUser = false;//是否置顶用户
        if (!(userDetail.isUnlock_vip() || userDetail.isVip()) && !"0".equals(yCoinUserid)) {
            needTopUser = true;
        }

        boolean isHaveService = false;
        for (BaseMessage temp : messageLists) {
            if (MailSpecialID.customerService.getSpecialID() == temp.getLWhisperID()) {
                temp.setMailItemStyle(MailItemType.Mail_Item_Other.type);
                temp.setWeight(MessageConstant.Max_Weight);
                isHaveService = true;
            }else if(MailSpecialID.shareMsg.getSpecialID() == temp.getLWhisperID()){
                temp.setMailItemStyle(MailItemType.Mail_Item_Greet.type);
                temp.setWeight(MessageConstant.Small_Weight);
            }else if (needTopUser && temp.getWhisperID().equals(yCoinUserid)) {
                temp.setWeight(MessageConstant.In_Weight);
            }
        }

        if (!isHaveService) {
            BaseMessage baseMessage = new BaseMessage();
            baseMessage.setWhisperID(String.valueOf(MailSpecialID.customerService.getSpecialID()));
            baseMessage.setMailItemStyle(MailItemType.Mail_Item_Other.type);
            baseMessage.setWeight(MessageConstant.Max_Weight);
         //   baseMessage.setName(MailSpecialID.customerService.getSpecialIDName());
            baseMessage.setLocalAvatar(R.drawable.f1_secretary_avatar);
            messageLists.add(baseMessage);
        }

        int geetNum = ModuleMgr.getChatListMgr().getGeetList().size();
        if (geetNum > 0) {
            BaseMessage baseMessage = new BaseMessage();
            baseMessage.setWhisperID(String.valueOf(MailMsgID.Greet_Msg.type));
            baseMessage.setMailItemStyle(MailItemType.Mail_Item_Greet.type);
            baseMessage.setNum(ModuleMgr.getChatListMgr().getGreetNum());
            baseMessage.setName("打招呼的人");
            baseMessage.setTime(ModuleMgr.getChatListMgr().getGeetListLastTime());
            baseMessage.setAboutme(geetNum > 0 ? "共有" + geetNum + "位打招呼的人" : "暂时还没有打招呼的人");
            baseMessage.setLocalAvatar(R.drawable.f1_hi_btn);
            messageLists.add(baseMessage);
        }

        setList(messageLists);
    }

    @Override
    public void notifyDataSetChanged() {
        SortList.sortWeightTimeListView(getList());
        super.notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return MailItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        BaseMessage message = getItem(position);
        if (message != null) {
            return message.getMailItemStyle();
        }
        return 0;
    }

    public BaseMessage isContainMessage(long userID) {
        for (BaseMessage temp : getList()) {
            if (userID == temp.getLWhisperID()) {
                return temp;
            }
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = inflate(R.layout.p1_mail_fragment_item);
            vh.customMailItem = (CustomMailItem) convertView.findViewById(R.id.mail_item);
            vh.customMailItem.onCreateView();
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        BaseMessage msgData = getItem(position);
        if (msgData == null) return convertView;
        int viewType = getItemViewType(position);
        int aboveViewType = -1;
        if ((position - 1) >= 0) {
            aboveViewType = getItemViewType(position - 1);
        }

        MailItemType mailItemType = MailItemType.getMailMsgType(viewType);
        if (mailItemType == null) return convertView;
        switch (mailItemType) {
            case Mail_Item_Ordinary:
                vh.customMailItem.showItemLetter(msgData);
                break;
            case Mail_Item_Other:
            case Mail_Item_Greet:
                vh.customMailItem.showItemAct(msgData);
                break;
            case Mail_Item_Invite://邀请消息
            case Mail_Item_Gift://礼物
            case Mail_Item_Private://私密消息
            case Mail_Item_Stranger://陌生人
                vh.customMailItem.showItemSpecial(msgData);
                break;
            case Mail_Item_Title://头部显示
            case Mail_Item_Title_TWO://头部显示
                vh.customMailItem.showItemTitle(msgData);
                break;
            case Mail_Item_Bottom://底部，加载更多
                vh.customMailItem.showItemBottom(msgData);
                break;
            case Mail_Item_BlankBar:
                vh.customMailItem.showItemBlankBar(msgData);
                break;
            default:
                break;
        }
        if (mailItemType == MailItemType.Mail_Item_BlankBar && isSetBlankBar) {
            vh.customMailItem.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), UIUtil.dp2px(8));
            setItemHeight(UIUtil.dp2px(8));
            isSetBlankBar = true;
        } else if ((mailItemType == MailItemType.Mail_Item_Title) && !isSetTitle) {
            vh.customMailItem.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), UIUtil.dp2px(46));
            setItemHeight(UIUtil.dp2px(46));
            isSetTitle = true;
        } else if (mailItemType == MailItemType.Mail_Item_Title_TWO && !isSetTitle_two) {
            vh.customMailItem.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), UIUtil.dp2px(46));
            setItemHeight(UIUtil.dp2px(46));
            isSetTitle_two = true;
        } else if (mailItemType == MailItemType.Mail_Item_Bottom && !isSetBottom) {
            vh.customMailItem.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), UIUtil.dp2px(46));
            setItemHeight(UIUtil.dp2px(46));
            isSetBottom = true;
        } else {
            if (getItemHeight() == 0 || getItemHeight() == UIUtil.dp2px(8) || getItemHeight() == UIUtil.dp2px(46)) {
                int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                vh.customMailItem.measure(width, height);
                setItemHeight(vh.customMailItem.getMeasuredHeight());
            }
        }
        PLogger.d("viewType=" + viewType + ";;;aboveViewType=" + aboveViewType + ";;;start=" +
                mailItemOtherr().getLWhisperID() + ";;;ene====" + msgData.getLWhisperID() + ";;;setItemHeight=" + getItemHeight());

        if (viewType != aboveViewType) {
            switch (mailItemType) {
                case Mail_Item_Ordinary:
                    if (mailItemOtherr().getLWhisperID() == msgData.getLWhisperID()) {
                        PLogger.printObject("customMailItem===11111111");
                        vh.customMailItem.showLetterGap();
                    } else {
                        PLogger.printObject("customMailItem===22222222");
                        vh.customMailItem.hideLetterGap();
                    }
                    break;
                case Mail_Item_Greet:
                    if (mailItemOtherr().getLWhisperID() == msgData.getLWhisperID()) {
                        PLogger.printObject("customMailItem===33333333");
                        vh.customMailItem.showGreetGap();
                    } else {
                        PLogger.printObject("customMailItem===4444444");
                        vh.customMailItem.hideGreetGap();
                    }
                    break;
                case Mail_Item_Other:
                    vh.customMailItem.showGreetGap();
                    break;
                case Mail_Item_Title:
                case Mail_Item_Title_TWO:
                    vh.customMailItem.showTitleGap();
                    break;
                default:
                    break;
            }
        } else {
            switch (mailItemType) {
                case Mail_Item_Ordinary:
                    vh.customMailItem.hideLetterGap();
                    break;
                case Mail_Item_Greet:
                case Mail_Item_Other:
                    vh.customMailItem.hideGreetGap();
                    break;
                case Mail_Item_Title:
                case Mail_Item_Title_TWO:
                    vh.customMailItem.hideTitleGap();
                    break;
                default:
                    break;
            }
        }
        return convertView;
    }

    private class ViewHolder {
        CustomMailItem customMailItem;
    }

    private int itemHeight;

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public int getItemHeight() {
        return itemHeight;
    }
}