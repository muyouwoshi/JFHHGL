package com.juxin.predestinate.ui.mail.unlock;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.bean.my.NobilityLeftGifts;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IQQ on 2017-11-01.
 */

public class UnlockJueweiPanel extends BasePanel implements View.OnClickListener, RequestComplete {

    private TextView tvDesc, tvDifference, tvNeedstoneOne, tvNeedstoneTwo, tvNeedstoneThree, tvGifnameOne,
            tvGifnameTwo, tvGifnameThree, tvExperienceOne, tvExperienceTwo, tvExperienceThree, tvSend,
            tvPrivilegeOne, tvPrivilegeTwo;
    private ImageView imgGiftOne, imgGiftTwo, imgGiftThree;
    private LinearLayout llGiftOne, llGiftTwo, llGiftThree;

    private List<GiftsList.GiftInfo> gifts;
    private GiftsList.GiftInfo mGiftInfo;                               //选中的礼物信息
    private UnlockMsgDlg.OnUnlockDlgMsg msg;
    private NobilityList.Nobility nobility;
    private int cmpProcess;

    private long whisperID;
    private boolean isCanClick = true;

    public UnlockJueweiPanel(Context context, UnlockMsgDlg.OnUnlockDlgMsg msg) {
        super(context);
        this.msg = msg;
        setContentView(R.layout.spread__knighthood_panel);
        initData();
        initView();
        initGifts();
    }

    public void setData(Long toUid) {
        whisperID = toUid;
    }

    private void initData() {
        nobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(
                ModuleMgr.getCommonMgr().getCommonConfig().getCommon().getFree_speak_level(), ModuleMgr.getCenterMgr().getMyInfo().getGender());
    }

    private void initGifts() {
        ModuleMgr.getCommonMgr().getNobilityLeftGifts(this);
    }

    private void initView() {
        tvDesc = (TextView) findViewById(R.id.spread_panel_kh_desc_tv);
        tvDifference = (TextView) findViewById(R.id.spread_panel_kh_tv_difference);
        imgGiftOne = (ImageView) findViewById(R.id.spread_panel_kh_gift_img_one);
        imgGiftTwo = (ImageView) findViewById(R.id.spread_panel_kh_gift_img_two);
        imgGiftThree = (ImageView) findViewById(R.id.spread_panel_kh_gift_img_three);
        tvNeedstoneOne = (TextView) findViewById(R.id.spread_panel_kh_gift_tv_needstone_one);
        tvNeedstoneTwo = (TextView) findViewById(R.id.spread_panel_kh_gift_tv_needstone_two);
        tvNeedstoneThree = (TextView) findViewById(R.id.spread_panel_kh_gift_tv_needstone_three);
        tvGifnameOne = (TextView) findViewById(R.id.spread_panel_kh_gift_tv_gifname_one);
        tvGifnameTwo = (TextView) findViewById(R.id.spread_panel_kh_gift_tv_gifname_two);
        tvGifnameThree = (TextView) findViewById(R.id.spread_panel_kh_gift_tv_gifname_three);
        tvExperienceOne = (TextView) findViewById(R.id.spread_panel_kh_tv_experience_one);
        tvExperienceTwo = (TextView) findViewById(R.id.spread_panel_kh_tv_experience_two);
        tvExperienceThree = (TextView) findViewById(R.id.spread_panel_kh_tv_experience_three);
        tvSend = (TextView) findViewById(R.id.spread_panel_kh_tv_send);
        tvPrivilegeOne = (TextView) findViewById(R.id.spread_panel_kh_tv_privilege_one);
        tvPrivilegeTwo = (TextView) findViewById(R.id.spread_panel_kh_tv_privilege_two);
        llGiftOne = (LinearLayout) findViewById(R.id.spread_panel_gift_ll_gift_one);
        llGiftTwo = (LinearLayout) findViewById(R.id.spread_panel_gift_ll_gift_two);
        llGiftThree = (LinearLayout) findViewById(R.id.spread_panel_gift_ll_gift_three);
        cmpProcess = nobility != null ? (nobility.getUpgrade_condition() - ModuleMgr.getCenterMgr().getMyInfo().getNobilityInfo().getCmpprocess()) : 0;
        tvDesc.setText(Html.fromHtml(getContext().getResources().getString(R.string.the_message_for_free, nobility != null ? nobility.getTitle_name() : "")));
        tvDifference.setText(Html.fromHtml(getContext().getResources().getString(R.string.how_much_is_poor, nobility != null ? nobility.getTitle_name() : "",
                cmpProcess == 0 ? 1 : cmpProcess)));
        tvPrivilegeOne.setText(Html.fromHtml(getContext().getResources().getString(R.string.privilege_one)));
        tvPrivilegeTwo.setText(Html.fromHtml(getContext().getResources().getString(R.string.privilege_two)));
        llGiftOne.setOnClickListener(this);
        llGiftTwo.setOnClickListener(this);
        llGiftThree.setOnClickListener(this);
        tvSend.setOnClickListener(this);
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
                tvNeedstoneOne.setText(getContext().getString(R.string.goods_diamond_need, info.getMoney()));
                tvGifnameOne.setText(info.getName() + "");
                tvExperienceOne.setText(getContext().getString(R.string.goods_diamond_spread, info.getMoney()));
            } else if (i == 1) {
                ImageLoader.loadFitCenter(context, info.getPic(), imgGiftTwo);
                tvNeedstoneTwo.setText(getContext().getString(R.string.goods_diamond_need, info.getMoney()));
                tvGifnameTwo.setText(info.getName() + "");
                tvExperienceTwo.setText(getContext().getString(R.string.goods_diamond_spread, info.getMoney()));
            } else if (i == 2) {
                ImageLoader.loadFitCenter(context, info.getPic(), imgGiftThree);
                tvNeedstoneThree.setText(getContext().getString(R.string.goods_diamond_need, info.getMoney()));
                tvGifnameThree.setText(info.getName() + "");
                tvExperienceThree.setText(getContext().getString(R.string.goods_diamond_spread, info.getMoney()));
            } else {
                return;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mGiftInfo == null) {
            initGifts();
            return;
        }
        switch (v.getId()) {
            case R.id.spread_panel_kh_tv_send: //赠送礼物或索要礼物逻辑
                if (mGiftInfo == null) {
                    return;
                }
                doAction();  //事件处理
                break;
            case R.id.spread_panel_gift_ll_gift_one: //选中第一个礼物
                reSetViewBg();
                llGiftOne.setBackgroundResource(R.drawable.spread_gift_send_select_bg);
                if (gifts.size() > 0) {
                    mGiftInfo = gifts.get(0);
                }
                break;
            case R.id.spread_panel_gift_ll_gift_two: //选中第二个礼物
                reSetViewBg();
                llGiftTwo.setBackgroundResource(R.drawable.spread_gift_send_select_bg);
                if (gifts.size() > 1) {
                    mGiftInfo = gifts.get(1);
                }
                break;
            case R.id.spread_panel_gift_ll_gift_three: //选中第三个礼物
                reSetViewBg();
                llGiftThree.setBackgroundResource(R.drawable.spread_gift_send_select_bg);
                if (gifts.size() > 2) {
                    mGiftInfo = gifts.get(2);
                }
                break;
            default:
                break;
        }
    }

    private void reSetViewBg() {
        llGiftOne.setBackgroundResource(R.drawable.spread_gift_send_unselect_bg);
        llGiftTwo.setBackgroundResource(R.drawable.spread_gift_send_unselect_bg);
        llGiftThree.setBackgroundResource(R.drawable.spread_gift_send_unselect_bg);
    }

    private void doAction() {
        if (mGiftInfo.getMoney() > ModuleMgr.getCenterMgr().getMyInfo().getDiamand()) { //是否需要钻石以及钻石是否充足
            SourcePoint.getInstance().lockSource((Activity) getContext(), "unlockchat_gempay");
            UIShow.showGoodsDiamondDialog(getContext(), mGiftInfo.getMoney() - ModuleMgr.getCenterMgr().getMyInfo().getDiamand(),
                    -1, whisperID, "");
            return;
        }
        if (!isCanClick) {
            isCanClick = false;
            return;
        }
        SourcePoint.getInstance().lockSource((Activity) getContext(), "unlockchat");
        StatisticsShareUnlock.onAlert_unlockchat_give(cmpProcess, mGiftInfo.getId(), mGiftInfo.getMoney());
        ModuleMgr.getChatMgr().sendGiftMsg("", String.valueOf(whisperID), mGiftInfo.getId(), 1, 8, 2, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                isCanClick = true;
                if (response.isOk()) {
                    UnlockComm.sendUnlockMessage("3", mGiftInfo.getId());
                }
                if (null != msg) {
                    msg.onDismiss();
                }
            }
        });
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        if (response.isOk()) {
            NobilityLeftGifts giftsList = new NobilityLeftGifts();
            giftsList.parseJson(response.getResponseString());
            gifts = giftsList.getGifts();
            setGiftsData();
        } else {
            getDataErr();
        }
    }

    private void setGiftsData() {
        if (gifts != null && !gifts.isEmpty()) {
            mGiftInfo = gifts.get(0);
            if (cmpProcess == 0) {
                tvDifference.setText(Html.fromHtml(getContext().getResources().getString(R.string.how_much_is_poor, nobility != null ? nobility.getTitle_name() : "",
                        gifts.get(0).getMoney())));
            }
            setGifts();
        } else {
            getDataErr();
        }
    }

    //获取数据失败添加默认礼物id
    private void getDataErr() {
        List<Integer> listIds = new ArrayList<>();
        listIds.add(25);
        listIds.add(6);
        listIds.add(35);
        gifts = ModuleMgr.getCommonMgr().getCommonConfig().getGift().getGiftsFromIds(listIds);
        setGiftsData();
    }

}
