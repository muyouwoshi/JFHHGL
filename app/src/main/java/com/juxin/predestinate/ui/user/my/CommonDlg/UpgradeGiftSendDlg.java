package com.juxin.predestinate.ui.user.my.CommonDlg;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.juxin.library.image.ImageLoader;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.bean.my.SendGiftInfo;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.juxin.predestinate.R.id.info;
import static com.juxin.predestinate.R.id.to_upgrade_gift_tv_ok;

/**
 * 创建日期：2017/7/11
 * 描述:送礼弹框
 * 作者:zm
 */
public class UpgradeGiftSendDlg extends BaseDialogFragment implements View.OnClickListener {

    public static final int BANNERETUPGRADEB = 1;                     //爵位升级弹框
    public static final int PROMISEGIFT = 2;                          //男送定情礼物弹框
    public static final int ADDCLOSEFRIEND = 3;                       //女加密友索要礼物
    public static final int CLOSEFRIENDUPGRADE = 4;                   //密友升级弹框

    private int giftType = BANNERETUPGRADEB;                          //设置默认值 BANNERETUPGRADEB
    private TextView tvTitle, tvTip;
    private ImageView imgGiftOne, imgGiftTwo, imgGiftThree;             //图片
    private TextView tvNeedstoneOne, tvNeedstoneTwo, tvNeedstoneThree;  //价格
    private TextView tvGiftnameOne, tvGiftnameTwo, tvGiftnameThree;     //礼物名称
    private LinearLayout llGiftOne, llGiftTwo, llGiftThree;
    private TextView tvCancel, tvOk;
    private Context context;

    private List<GiftsList.GiftInfo> gifts;
    private GiftsList.GiftInfo mGiftInfo;                               //选中的礼物信息
    private List<GiftsList.GiftInfo> selectGifts = new ArrayList<>();                       //多选的礼物消息
    private BaseMessage message;
    private RequestComplete complete;

    public UpgradeGiftSendDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(1, 0);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.p1_to_upgrade_the_title_a_gift_dlg);
        View view = getContentView();
        initView(view);
        return view;
    }

    private void initView(View view) {
        tvTitle = (TextView) view.findViewById(R.id.to_upgrade_gift_tv_title);
        tvTip = (TextView) view.findViewById(R.id.to_upgrade_gift_tv_tip);
        tvNeedstoneOne = (TextView) view.findViewById(R.id.to_upgrade_gift_tv_needstone_one);
        tvNeedstoneTwo = (TextView) view.findViewById(R.id.to_upgrade_gift_tv_needstone_two);
        tvNeedstoneThree = (TextView) view.findViewById(R.id.to_upgrade_gift_tv_needstone_three);
        tvGiftnameOne = (TextView) view.findViewById(R.id.to_upgrade_gift_tv_gifname_one);
        tvGiftnameTwo = (TextView) view.findViewById(R.id.to_upgrade_gift_tv_gifname_two);
        tvGiftnameThree = (TextView) view.findViewById(R.id.to_upgrade_gift_tv_gifname_three);
        llGiftOne = (LinearLayout) view.findViewById(R.id.to_upgrade_gift_ll_gift_one);
        llGiftTwo = (LinearLayout) view.findViewById(R.id.to_upgrade_gift_ll_gift_two);
        llGiftThree = (LinearLayout) view.findViewById(R.id.to_upgrade_gift_ll_gift_three);


        imgGiftOne = (ImageView) view.findViewById(R.id.to_upgrade_gift_img_one);
        imgGiftTwo = (ImageView) view.findViewById(R.id.to_upgrade_gift_img_two);
        imgGiftThree = (ImageView) view.findViewById(R.id.to_upgrade_gift_img_three);

        tvCancel = (TextView) view.findViewById(R.id.to_upgrade_gift_tv_cancle);
        tvOk = (TextView) view.findViewById(to_upgrade_gift_tv_ok);

        tvCancel.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        llGiftOne.setOnClickListener(this);
        llGiftTwo.setOnClickListener(this);
        llGiftThree.setOnClickListener(this);
        if (gifts.size() > 0) {
            mGiftInfo = gifts.get(0);
            selectGifts.add(mGiftInfo);
        }
        setGifts();
        if (giftType == BANNERETUPGRADEB) {
            int rank = ModuleMgr.getCenterMgr().getMyInfo().getNobilityInfo().getRank() + 1;
            NobilityList.Nobility nobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(rank, ModuleMgr.getCenterMgr().getMyInfo().getGender());
            tvTitle.setText(getString(R.string.por_una_cabeza, nobility.getStar_name() + nobility.getTitle_name()));
        } else if (giftType == PROMISEGIFT) {
            tvTitle.setText(R.string.gift_cards);
            tvTip.setText(R.string.chuan_private_privilege);
        } else if (giftType == ADDCLOSEFRIEND) {
            tvTitle.setText(R.string.add_friend_ask_gift);
            tvTip.setText(R.string.send_friend);
            tvOk.setText(R.string.ask_for);
        } else if (giftType == CLOSEFRIENDUPGRADE) {
            NobilityList.CloseFriend closeFriend = ModuleMgr.getCommonMgr().getCommonConfig().getNobilityList().queryCloseFriend(message.getNobility_rank());
            tvTitle.setText(getContext().getString(R.string.short_, message.getNobility_cmpprocess() + getContext().getString(R.string.close_upgrade) + closeFriend.getTitle()));
            tvTip.setText(R.string.giving_gifts_upgrade);
        }
    }

    /**
     * 初始化礼物列表
     */
    private void setGifts() {
        for (int i = 0; i < gifts.size(); i++) {
            GiftsList.GiftInfo info = gifts.get(i);
            if (info == null) {
                continue;
            }
            if (i == 0) {
                ImageLoader.loadFitCenter(context, info.getPic(), imgGiftOne);
                tvNeedstoneOne.setText(getString(R.string.goods_diamond_need, info.getMoney()));
                tvGiftnameOne.setText(info.getName() + "");
            } else if (i == 1) {
                ImageLoader.loadFitCenter(context, info.getPic(), imgGiftTwo);
                tvNeedstoneTwo.setText(getString(R.string.goods_diamond_need, info.getMoney()));
                tvGiftnameTwo.setText(info.getName() + "");
            } else if (i == 2) {
                ImageLoader.loadFitCenter(context, info.getPic(), imgGiftThree);
                tvNeedstoneThree.setText(getString(R.string.goods_diamond_need, info.getMoney()));
                tvGiftnameThree.setText(info.getName() + "");
            } else {
                return;
            }
        }
    }

    /**
     * 设置弹框样式和礼物数据
     *
     * @param giftType BANNERETUPGRADEB:爵位升级弹框  BANNERETUPGRADEB:爵位升级弹框 ADDCLOSEFRIEND：加密友索要礼物
     * @param gifts    礼物列表
     */
    public void setGiftTypeAndData(Context context, int giftType, List<GiftsList.GiftInfo> gifts, BaseMessage message, RequestComplete complete) {
        this.context = context;
        this.giftType = giftType;
        this.gifts = gifts;
        this.message = message;
        this.complete = complete;
    }

    private void reSetViewBg() {
        if (giftType != BANNERETUPGRADEB) {
            llGiftOne.setBackgroundResource(R.drawable.f1_gift_send_unselect_bg);
            llGiftTwo.setBackgroundResource(R.drawable.f1_gift_send_unselect_bg);
            llGiftThree.setBackgroundResource(R.drawable.f1_gift_send_unselect_bg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_upgrade_gift_tv_cancle: //取消
                if (giftType == BANNERETUPGRADEB) {
                    Statistics.userBehavior(SendPoint.alert_peerage_upgrade_cancel);
                } else if (giftType == CLOSEFRIENDUPGRADE) {
                    Statistics.userBehavior(SendPoint.page_chat_alert_qinmidu_cancel);
                }
                dismiss();
                break;
            case R.id.to_upgrade_gift_tv_ok: //赠送礼物或索要礼物逻辑
                if (mGiftInfo == null) {
                    return;
                }
                doAction();  //事件处理
                break;
            case R.id.to_upgrade_gift_ll_gift_one: //选中第一个礼物
                reSetViewBg();
                llGiftOne.setBackgroundResource(R.drawable.f1_gift_send_select_bg);
                if (gifts.size() > 0) {
                    mGiftInfo = gifts.get(0);
                }
                break;
            case R.id.to_upgrade_gift_ll_gift_two: //选中第二个礼物
                reSetViewBg();
                llGiftTwo.setBackgroundResource(R.drawable.f1_gift_send_select_bg);
                if (gifts.size() > 1) {
                    mGiftInfo = gifts.get(1);
                }
                break;
            case R.id.to_upgrade_gift_ll_gift_three: //选中第三个礼物
                reSetViewBg();
                llGiftThree.setBackgroundResource(R.drawable.f1_gift_send_select_bg);
                if (gifts.size() > 2) {
                    mGiftInfo = gifts.get(2);
                }
                break;
            default:
                break;
        }
        if (giftType == BANNERETUPGRADEB) {
            changeViewBg(v);
        }
    }

    private void changeViewBg(View v) {
        GiftsList.GiftInfo info = null;
        for (int i = 0; i < selectGifts.size(); i++) {
            if (mGiftInfo == selectGifts.get(i)) {
                info = mGiftInfo;
                if (selectGifts.size() > 1) {
                    selectGifts.remove(mGiftInfo);
                    v.setBackgroundResource(R.drawable.f1_gift_send_unselect_bg);
                }
                break;
            }
        }
        if (info == null) {
            selectGifts.add(mGiftInfo);
            v.setBackgroundResource(R.drawable.f1_gift_send_select_bg);
        }
    }

    private void doAction() {
        if (giftType != ADDCLOSEFRIEND && mGiftInfo.getMoney() > ModuleMgr.getCenterMgr().getMyInfo().getDiamand()) { //是否需要钻石以及钻石是否充足
            UIShow.showGoodsDiamondDialog(getContext(), mGiftInfo.getMoney() - ModuleMgr.getCenterMgr().getMyInfo().getDiamand(),
                    -1, message.getLWhisperID(), message.getChannelID());
            return;
        }
        if (giftType == BANNERETUPGRADEB) {
            List<SendGiftInfo> infos = new ArrayList<>();
            for (int i = 0; i < selectGifts.size(); i++) {  //发送多个礼物
                if (selectGifts.get(i) == null) {
                    continue;
                }
                infos.add(new SendGiftInfo(selectGifts.get(i).getId(), selectGifts.get(i).getMoney()));
                ModuleMgr.getChatMgr().sendGiftMsg(message.getChannelID(), message.getWhisperID(), selectGifts.get(i).getId(), 1, 1);
            }

            List<Map<String, Object>> users = new ArrayList<>();
            Map<String, Object> user;
            for (SendGiftInfo info : infos) {
                user = new HashMap<>();
                user.put("gift_id", info.getGift_id());
                user.put("gem_num", info.getGem_num());
                users.add(user);
            }
            Statistics.userBehavior(SendPoint.menu_faxian_tuijian_downrefresh.toString(), 0, JSON.toJSONString(users));



            dismiss();
        } else if (giftType == PROMISEGIFT || giftType == ADDCLOSEFRIEND) {
            Map<String, Object> params = new HashMap<>();
            params.put("gift_id", mGiftInfo.getId());
            params.put("gem_num", mGiftInfo.getMoney());
            if (giftType == PROMISEGIFT) {
                Statistics.userBehavior(SendPoint.alert_shoufuta_give, params);
            } else if (giftType == ADDCLOSEFRIEND) {
                Statistics.userBehavior(SendPoint.alert_jiamiyou_suoyao, params);
            }
            ModuleMgr.getChatMgr().sendChumInviteMsg(message.getWhisperID(), mGiftInfo.getId(), new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    if (complete != null) {
                        complete.onRequestComplete(response);
                    }
                    MsgMgr.getInstance().sendMsg(MsgType.MT_ADD_CHUM_INFORM, response.isOk());
                    dismissAllowingStateLoss();
                }
            });
        } else if (giftType == CLOSEFRIENDUPGRADE) {
            Map<String, Object> params = new HashMap<>();
            params.put("gift_id", mGiftInfo.getId());
            params.put("gem_num", mGiftInfo.getMoney());
            Statistics.userBehavior(SendPoint.page_chat_alert_qinmidu_give, params);
            ModuleMgr.getChatMgr().sendGiftMsg(message.getChannelID(), message.getWhisperID(), mGiftInfo.getId(), 1, 1);
            dismiss();
        }
    }
}