package com.juxin.predestinate.module.util.my;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.BoringLayout;
import android.util.Log;

import com.juxin.library.log.PSP;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.bean.StackNode;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.bean.my.SendGiftResultInfo;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.mail.chat.PrivateChatAct;
import com.juxin.predestinate.ui.user.check.UserCheckInfoAct;
import com.juxin.predestinate.ui.user.my.CommonDlg.UpgradeGiftSendDlg;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by zm on 2017/5/9.
 */

public class DlgMessageHelper {

    private Handler handDelayed = new Handler(); //用于延时操作
    private List<BaseMessage> dlgMsgs = new ArrayList<>();
    private Boolean isCanShowDlag = true;

    private static DlgMessageHelper instance = new DlgMessageHelper();

    public static DlgMessageHelper getInstance() {
        return instance;
    }

    public void addDlgMsgs(final BaseMessage message) {
        if (message != null) {
            BaseMessage msg = new BaseMessage();
            if (message.getType() == BaseMessage.Effect_Pop_MsgType) {
                msg.setWeight(MessageConstant.Max_Weight);
            } else if (message.getType() == BaseMessage.DIALOG_TYPE) {
                JSONObject jsonObject = message.getJsonObj();
                String dialog_tp = jsonObject.optString("dialog_tp");//弹窗类型nobility_hello欢迎进入爵位系统nobility_left爵位一步之遥nobility_up爵位升级
                if (!jsonObject.has("dialog_data")) { //无消息结构体则返回
                    return;
                }
                if ("nobility_up".equalsIgnoreCase(dialog_tp)) { //爵位升级
                    msg.setWeight(MessageConstant.Invite_Ordinary_Weight);
                } else if ("intimate_lvup".equalsIgnoreCase(dialog_tp)) { //intimate_lvup 密友等级提升
                    msg.setWeight(MessageConstant.Private_Ordinary_Weight);
                } else if ("intimate_left".equalsIgnoreCase(dialog_tp)) { //密友还差100弹窗
                    msg.setWeight(MessageConstant.Gift_Ordinary_Weight);
                } else if ("nobility_left".equalsIgnoreCase(dialog_tp)) { //爵位一步之遥
                    msg.setWeight(MessageConstant.Gift_Ordinary_Weight);
                }else if ("nobility_hello".equalsIgnoreCase(dialog_tp)) { //欢迎进入爵位系统
                    msg.setWeight(MessageConstant.Stranger_Ordinary_Weight);
                }
            }

            msg.setJsonStr(message.getJsonStr());
            msg.setWhisperID(message.getWhisperID());
            msg.setType(message.getType());
            msg.setChannelID(message.getChannelID());
            sortMess(msg);
        }
        long time = PSP.getInstance().getLong("dlgTime",0);

        if (dlgMsgs.size() == 1 || (ModuleMgr.getAppMgr().getTime() - time) > 1000){
            handDelayed.postDelayed(runnable, 1000);
        }
        PSP.getInstance().put("dlgTime",ModuleMgr.getAppMgr().getTime());
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long time = PSP.getInstance().getLong("dlgTime",0);
            HandleDlgMsgs();
        }
    };

    //弹框消息排序
    private void sortMess(BaseMessage message) {
        if (dlgMsgs.size() == 0) {
            dlgMsgs.add(message);
        } else {
            for (int i = dlgMsgs.size() - 1; i >= 0; i--) {
                BaseMessage msg = dlgMsgs.get(dlgMsgs.size() - 1);
                if (message.getWeight() > msg.getWeight()) {
                    dlgMsgs.add(i, message);
                    if (message.getWeight() == MessageConstant.Stranger_Ordinary_Weight && msg.getWeight() == MessageConstant.Gift_Ordinary_Weight){
                        dlgMsgs.remove(message);
                    }else if (message.getWeight() == MessageConstant.Gift_Ordinary_Weight && msg.getWeight() == MessageConstant.Stranger_Ordinary_Weight){
                        dlgMsgs.remove(msg);
                    }
                    return;
                } else if (message.getWeight() < msg.getWeight()) {
                    if ((i +1) <= dlgMsgs.size() -1){
                        dlgMsgs.add( i+1 , message);
                    }else {
                        dlgMsgs.add(message);
                    }
                    if (message.getWeight() == MessageConstant.Stranger_Ordinary_Weight && msg.getWeight() == MessageConstant.Gift_Ordinary_Weight){
                        dlgMsgs.remove(message);
                    }else if (message.getWeight() == MessageConstant.Gift_Ordinary_Weight && msg.getWeight() == MessageConstant.Stranger_Ordinary_Weight){
                        dlgMsgs.remove(msg);
                    }
                    return;
                } else if (message.getWeight() == msg.getWeight()) {
                    if (message.getWeight() == MessageConstant.Gift_Ordinary_Weight) {
                        dlgMsgs.add(i, message);
                        int index = i + 1;
                        dlgMsgs.remove(index < (dlgMsgs.size() - 1) ? index : (dlgMsgs.size() - 1));
                    } else {
                        dlgMsgs.add(i, message);
                    }
                    return;
                }
            }
        }
    }

    public List<BaseMessage> getDlgMsgs() {
        return dlgMsgs;
    }

    public void setDlgMsgs(List<BaseMessage> dlgMsgs) {
        this.dlgMsgs = dlgMsgs;
    }

    private boolean isInChatActivity(){
        StackNode nod = StackNode.getStackNode((FragmentActivity) App.activity);
        if (nod != null && PrivateChatAct.class.getName().equalsIgnoreCase(nod.stack)){
            return true;
        }
        return false;
    }

    private boolean isInInfoAct(){
        StackNode nod = StackNode.getStackNode((FragmentActivity) App.activity);
        if (nod != null && UserCheckInfoAct.class.getName().equalsIgnoreCase(nod.stack)){
            return true;
        }
        return false;
    }

    public void HandleDlgMsgs() {
        while (dlgMsgs.size() > 0) {
            BaseMessage message = dlgMsgs.get(0);
            dlgMsgs.remove(0);
            if (message != null) {
                if (message.getType() == BaseMessage.Effect_Pop_MsgType) {//大礼物弹框
                    ModuleMgr.getChatListMgr().getSpecialMsgMgr().setEffectPopMsg(message);
                    return;
                } else if (message.getType() == BaseMessage.DIALOG_TYPE) {
                    JSONObject jsonObject = message.getJsonObj();
                    String dialog_tp = jsonObject.optString("dialog_tp");//弹窗类型nobility_hello欢迎进入爵位系统nobility_left爵位一步之遥nobility_up爵位升级
                    if ("nobility_hello".equalsIgnoreCase(dialog_tp)) { //欢迎进入爵位系统
                        if (isInChatActivity()){
                            ModuleMgr.getChatListMgr().getSpecialMsgMgr().setPopups(message);
                        }
                        continue;
                    }else if ("nobility_up".equalsIgnoreCase(dialog_tp)) { //爵位升级
                        if (isInChatActivity() || isInInfoAct()){
                            ModuleMgr.getChatListMgr().getSpecialMsgMgr().setPopups(message);
                        }else {
                            continue;
                        }
                        return;
                    } else if ("intimate_lvup".equalsIgnoreCase(dialog_tp)) { //intimate_lvup 密友等级提升
                        if (isInChatActivity() || isInInfoAct()){
                            ModuleMgr.getChatListMgr().getSpecialMsgMgr().setPopups(message);
                        }else {
                            continue;
                        }
                        return;
                    } else if ("intimate_left".equalsIgnoreCase(dialog_tp)) { //密友还差100弹窗
                        if (isInChatActivity()){
                            ModuleMgr.getChatListMgr().getSpecialMsgMgr().setPopups(message);
                        }
                        dlgMsgs.clear();
                        return;
                    } else if ("nobility_left".equalsIgnoreCase(dialog_tp)) { //爵位一步之遥
                        if (isInChatActivity()){
                            ModuleMgr.getChatListMgr().getSpecialMsgMgr().setPopups(message);
                        }
                        dlgMsgs.clear();
                        return;
                    }else if ("pack_recved".equalsIgnoreCase(dialog_tp)) { //礼包购买成功
                        ModuleMgr.getChatListMgr().getSpecialMsgMgr().setPopups(message);
                        dlgMsgs.clear();
                        return;
                    } else if ("red_recved".equalsIgnoreCase(dialog_tp)) { //收到红包弹窗
                        ModuleMgr.getChatListMgr().getSpecialMsgMgr().setPopups(message);
                        dlgMsgs.clear();
                        return;
                    }
                }
            }
        }
    }
}
