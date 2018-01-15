package com.juxin.predestinate.ui.user.paygoods.diamond;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweightList;
import com.juxin.predestinate.bean.config.base.Goods;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.baseui.custom.RadiationView;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;
import com.juxin.predestinate.ui.user.paygoods.GoodsListPanel;
import com.juxin.predestinate.ui.user.paygoods.GoodsPayTypePanel;

import java.util.ArrayList;
import java.util.List;

/**
 * 音视频聊天钻石不足充值页面
 * <p>
 * Created by Su on 2017/6/22.
 */
public class BottomChatDiamondDlg extends BaseDialogFragment implements View.OnClickListener {

    public static final int RTC_CHAT_VIDEO = 1;    // 视频通信
    public static final int RTC_CHAT_VOICE = 2;    // 音频通信

    private ImageView girlHead;
    private int type = 0;//0是2个头像 ，1是一个头像,2是在panel里一个头像
    private int goodsPanelType = GoodsConstant.DLG_DIAMOND_CHAT;
    private List<Goods> payGoods = new ArrayList<>();
    private GoodsListPanel goodsPanel; // 商品列表
    private GoodsPayTypePanel payTypePanel; // 支付方式

    private long otherID;
    private int chatType;
    private int price;

    private boolean isAloneInvite = false;  // 单邀
    private long videoID;
    private ImageView avatar;
    private ImageView avatar_2;
    private TextView username;
    private ImageView top01;
    private ImageView vip_icon;
    private TextView info;
    private ImageView iv_online_status;
    UserInfoLightweight userInfoLightweight;
    private ImageView top00;
    private boolean isShowPrice = false;

    public BottomChatDiamondDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, -1);
        setCanceledOnTouchOutside(true);
    }

    /**
     * @param otherId       对方ID
     * @param chatType      视频，语音邀请
     * @param price         通信价格
     * @param isAloneInvite 是否是单邀
     * @param videoID       vc_id
     */
    public void setInfo(long otherId, int chatType, int price, boolean isAloneInvite, long videoID, int type, int goodsPanelType, boolean isShowPrice) {
        this.otherID = otherId;
        this.chatType = chatType;
        this.price = price;
        this.type = type;
        this.goodsPanelType = goodsPanelType;
        this.isAloneInvite = isAloneInvite;
        this.videoID = videoID;
        this.isShowPrice = isShowPrice;
    }

    public void UserInfoLightweight(UserInfoLightweight userInfoLightweight) {
        this.userInfoLightweight = userInfoLightweight;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_bottom_chat_diamond_dialog);
        View contentView = getContentView();
        payGoods.clear();
        List<Goods> goods = ModuleMgr.getCommonMgr().getCommonConfig().getPay().getDiamondList();
        if (goods != null) {
            int size = goods.size();
            for (int i = 0; i < size; i++) {
                if (i >= 2) {
                    break;
                }
                payGoods.add(goods.get(i));
            }
        }
        initView(contentView);
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAloneInvite) {
            ModuleMgr.getCenterMgr().reqMyInfo(new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    if (response.isOk()) {
                        UserDetail userDetail = (UserDetail) response.getBaseData();
                        if (userDetail.getDiamand() >= price) {
                            isAloneInvite = false;
                            //跳转视频
                            VideoAudioChatHelper.getInstance().openInvitedActivity((Activity) App.getActivity(),
                                    videoID, otherID, chatType, price);
                        }
                        return;
                    }
                    isAloneInvite = false;
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
        if (isAloneInvite) {
            ModuleMgr.getRtcEnginMgr().reqRejectVideoChat(videoID, null);
        }
    }

    private void reqOtherInfo() {
        List<Long> uids = new ArrayList<>();
        uids.add(otherID);
        ModuleMgr.getCommonMgr().reqUserInfoSummary(uids, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (!response.isOk()) {
                    return;
                }
                UserInfoLightweightList infoLightweightList = new UserInfoLightweightList();
                infoLightweightList.parseJsonSummary(response.getResponseJson());
                if (infoLightweightList.getUserInfos() != null && infoLightweightList.getUserInfos().size() > 0) {//数据大于1条
                    UserInfoLightweight info = infoLightweightList.getUserInfos().get(0);
                    if (type == 0) {
                        ImageLoader.loadCircleAvatar(getContext(), info.getAvatar(), girlHead);
                    } else if (type == 1) {
                        ImageLoader.loadCircleAvatar(getContext(), info.getAvatar(), avatar);
                    }
                }
            }
        });
    }

    private void initView(View contentView) {
        initRadiationView();
        contentView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        contentView.findViewById(R.id.btn_recharge).setOnClickListener(this);

        TextView diamond = (TextView) findViewById(R.id.diamond_remain);
        ImageView myHead = (ImageView) findViewById(R.id.iv_my_head);
        girlHead = (ImageView) findViewById(R.id.iv_girl_head);
        TextView chatPrice = (TextView) findViewById(R.id.chat_price);

        LinearLayout recharge_popping = (LinearLayout) findViewById(R.id.recharge_popping);
        RelativeLayout ll_head = (RelativeLayout) findViewById(R.id.ll_head);
        final RelativeLayout avatar_layout = (RelativeLayout) findViewById(R.id.avatar_layout);
        avatar = (ImageView) findViewById(R.id.avatar);//显示露出一半的头像
        LinearLayout header_root = (LinearLayout) findViewById(R.id.header_root);

        //在panel里显示一个头像
        avatar_2 = (ImageView) findViewById(R.id.avatar_2);
        username = (TextView) findViewById(R.id.username);
        top01 = (ImageView) findViewById(R.id.top01);
        top00 = (ImageView) findViewById(R.id.top00);
        vip_icon = (ImageView) findViewById(R.id.vip_icon);
        info = (TextView) findViewById(R.id.info);
        iv_online_status = (ImageView) findViewById(R.id.iv_online_status);


        chatPrice.setText(getString(R.string.invite_video_price, price));
        diamond.setText(String.valueOf(ModuleMgr.getCenterMgr().getMyInfo().getDiamand()));
        ImageLoader.loadCircleAvatar(getContext(), ModuleMgr.getCenterMgr().getMyInfo().getAvatar(), myHead);
        ImageLoader.loadCircleAvatar(getContext(), "", girlHead);
        reqOtherInfo();

        if (chatType == RTC_CHAT_VOICE) {
            ImageView chatImg = (ImageView) findViewById(R.id.chat_img);
            TextView chatText = (TextView) findViewById(R.id.chat_text);
            chatImg.setImageResource(R.drawable.f1_chat_voice_img);
            chatText.setText(getString(R.string.invite_voice_tips));
        }

        // 商品
        LinearLayout container = (LinearLayout) contentView.findViewById(R.id.pay_type_container);
        goodsPanel = new GoodsListPanel(getContext(), goodsPanelType);
        container.addView(goodsPanel.getContentView());
        goodsPanel.refresh(payGoods);

        // 支付方式
        payTypePanel = new GoodsPayTypePanel(getContext(), GoodsConstant.PAY_TYPE_NEW);
        container.addView(payTypePanel.getContentView());


        if (type == 0) {
            recharge_popping.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            ll_head.setVisibility(View.VISIBLE);
            header_root.setVisibility(View.GONE);
            avatar_layout.setVisibility(View.GONE);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) recharge_popping.getLayoutParams();
            lp.addRule(RelativeLayout.BELOW, ll_head.getId());
            recharge_popping.setLayoutParams(lp);

        } else if (type == 1) {
            recharge_popping.setBackgroundResource(R.drawable.index_yuliao_vip_head_bg);
            ImageLoader.loadCircleAvatar(getContext(), R.drawable.default_head, avatar);


            avatar_layout.bringToFront();
            avatar_layout.requestLayout();


            ll_head.setVisibility(View.GONE);
            header_root.setVisibility(View.GONE);
            avatar_layout.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) recharge_popping.getLayoutParams();
            lp.addRule(RelativeLayout.BELOW, avatar_layout.getId());
            recharge_popping.setLayoutParams(lp);

        } else {
            ImageLoader.loadCircleAvatar(getContext(), R.drawable.default_head, avatar);
            //recharge_popping.setBackgroundResource(R.drawable.white_shape_bg);
            ll_head.setVisibility(View.GONE);
            header_root.setVisibility(View.VISIBLE);
            avatar_layout.setVisibility(View.GONE);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) recharge_popping.getLayoutParams();
            lp.addRule(RelativeLayout.BELOW, header_root.getId());
            recharge_popping.setLayoutParams(lp);

            TextView call_type = (TextView) findViewById(R.id.call_type);

            if(AgoraConstant.RTC_CHAT_VIDEO  == this.chatType){
                call_type.setText("视频通话:");
            }else if(AgoraConstant.RTC_CHAT_VOICE  == this.chatType){
                call_type.setText("音频通话:");
            }

            if (userInfoLightweight != null) {
                if (isShowPrice) {
                    LinearLayout price_layout = (LinearLayout) findViewById(R.id.price_layout);
                    price_layout.setVisibility(View.VISIBLE);
                    TextView price = (TextView) findViewById(R.id.price);
                    price.setText(userInfoLightweight.getPrice() + "钻/分钟");
                }
                username.setText(userInfoLightweight.getNickname());
                ImageLoader.loadFitCenter(
                        getContext(), ModuleMgr.getCommonMgr().getCommonConfig()
                                .queryNobility(userInfoLightweight.getNobility_rank(), userInfoLightweight.getGender()).getTitle_icon(), top00);
                top01.setImageResource(userInfoLightweight.isMan() ? R.drawable.f1_top02 : R.drawable.f1_top01);
                vip_icon.setVisibility(userInfoLightweight.isVip() ? View.VISIBLE : View.GONE);
                if (userInfoLightweight.getDistance() != null && userInfoLightweight.getDistance().trim().length() > 0) {
                    BottomChatDiamondDlg.this.info.setText(userInfoLightweight.getAge() + "岁 • " + userInfoLightweight.getDistance());
                } else {
                    BottomChatDiamondDlg.this.info.setText(userInfoLightweight.getAge() + "岁");
                }
                iv_online_status.setImageResource(R.drawable.vip_dlg_title_bg_online);
                ImageLoader.loadCircleAvatar(getContext(), userInfoLightweight.getAvatar(), avatar_2);
            }

        }
    }

    private void initRadiationView() {
        final RadiationView rvFirst = (RadiationView) findViewById(R.id.rv_first);
        final RadiationView rvSec = (RadiationView) findViewById(R.id.rv_sec);
        rvFirst.setDpMultiple(UIUtil.toDpMultiple(getActivity()));
        rvSec.setDpMultiple(UIUtil.toDpMultiple(getActivity()));

        // 两个控件互相调起
        rvFirst.setRadiationListener(new RadiationView.RadiationListener() {
            @Override
            public void onArrival() {
                rvSec.startRadiate();   // rvSec开启
                if (rvSec.getVisibility() == View.GONE)
                    rvSec.setVisibility(View.VISIBLE);
            }
        });

        rvSec.setRadiationListener(new RadiationView.RadiationListener() {
            @Override
            public void onArrival() {
                rvFirst.startRadiate(); // rvFirst开启
            }
        });
        rvFirst.startRadiate();  // 启动其中之一
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_recharge:  // 充值
                if (payGoods.size() <= 0) return;

                Goods payGood = payGoods.get(goodsPanel.getPosition());
                StatisticsMessage.chatInviteGemPay(otherID, ModuleMgr.getCenterMgr().getMyInfo().getDiamand(),
                        payGood.getNum(), payTypePanel.getPayType());
                UIShow.showPayNoListAct(getActivity(), payGood.getPid(), payTypePanel.getPayType(), 0, "");
                break;

            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }
}
