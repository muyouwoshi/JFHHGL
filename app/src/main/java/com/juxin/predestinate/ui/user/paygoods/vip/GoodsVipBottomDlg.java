package com.juxin.predestinate.ui.user.paygoods.vip;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.Goods;
import com.juxin.predestinate.bean.settting.ContactBean;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;
import com.juxin.predestinate.ui.user.paygoods.GoodsListPanel;
import com.juxin.predestinate.ui.user.paygoods.GoodsPayTypePanel;

import java.util.List;

/**
 * VIP底部充值弹框
 * Created by Su on 2017/8/22.
 */
public class GoodsVipBottomDlg extends BaseDialogFragment implements View.OnClickListener {

    private int type;   // vip支付类型
    private int chatType= AgoraConstant.RTC_CHAT_VIDEO;
    private TextView vip_tips;
    private String avaterUrl, nickname, distanceict;
    private int age;
    private boolean Vip, isMan, isOnline;
    private String vipTips;
    private GoodsListPanel goodsPanel; // 商品列表
    private GoodsPayTypePanel payTypePanel; // 支付方式
    private RelativeLayout content;
    private ImageView btn_cancel;
    private int price=0;//音视频通话价格

    // vip购买在线配置
    private List<Goods> goodsList = ModuleMgr.getCenterMgr().getMyInfo().isB() ?
            ModuleMgr.getCommonMgr().getCommonConfig().getPay().getBBuyVipList() :
            ModuleMgr.getCommonMgr().getCommonConfig().getPay().getBuyVipList();

    public GoodsVipBottomDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, -1);
        setCanceledOnTouchOutside(true);
    }

    public void setInfo(int type, String avater, String nickname, int age, String distanceict,
                        boolean Vip, boolean isMan, boolean isOnline, String vipTips) {
        this.type = type;
        this.avaterUrl = avater;
        this.nickname = nickname;
        this.age = age;
        this.distanceict = distanceict;
        this.Vip = Vip;
        this.isMan = isMan;
        this.isOnline = isOnline;
        this.vipTips = vipTips;
    }

    public void setInfo(int type, String avater, String nickname, int age, String distanceict,
                        boolean Vip, boolean isMan, boolean isOnline, String vipTips,int chatType,int price) {
        this.type = type;
        this.avaterUrl = avater;
        this.nickname = nickname;
        this.age = age;
        this.distanceict = distanceict;
        this.Vip = Vip;
        this.isMan = isMan;
        this.isOnline = isOnline;
        this.vipTips = vipTips;
        this.chatType=chatType;
        this.price=price;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_bottom_vip_dlg);
        initView();
        return getContentView();
    }

    private void initView() {
        btn_cancel = (ImageView) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        findViewById(R.id.btn_recharge).setOnClickListener(this);
        content = (RelativeLayout) findViewById(R.id.content);

        vip_tips = (TextView) findViewById(R.id.vip_tips);
        TextView tele = (TextView) findViewById(R.id.tv_kefu);
        ContactBean contactBean = ModuleMgr.getCommonMgr().getContactBean();
        if (contactBean != null && !TextUtils.isEmpty(contactBean.getTel())) {
            tele.setVisibility(View.VISIBLE);
            tele.setText(getString(R.string.goods_vip_context, contactBean.getTel()));
        }
        initText();
        TextView tv_look_vip = (TextView) findViewById(R.id.tv_look_vip);
        tv_look_vip.setOnClickListener(this);

        if (!TextUtils.isEmpty(vipTips)) {
            vip_tips.setText(vipTips);
        }
        // 商品
        LinearLayout container = (LinearLayout) findViewById(R.id.goods_container);
        goodsPanel = new GoodsListPanel(getContext(), GoodsConstant.DLG_VIP_VIDEO_NEW);
        container.addView(goodsPanel.getContentView());
        goodsPanel.refresh(goodsList);

        // 支付方式
        LinearLayout payContainer = (LinearLayout) findViewById(R.id.pay_type_container);
        payTypePanel = new GoodsPayTypePanel(getContext(), GoodsConstant.PAY_TYPE_NEW);
        payContainer.addView(payTypePanel.getContentView());
        if (type == GoodsConstant.DLG_VIP_INDEX_YULIAO || type == GoodsConstant.DLG_VIP_INDEX_YULIAO_AVATER) {//动态添加,解决先设置9图后在更换背景高度错误的bug
            ImageView avatar = null;
            if (type == GoodsConstant.DLG_VIP_INDEX_YULIAO) {
                content.setBackgroundResource(R.drawable.white_shape_bg);
                avatar = (ImageView) findViewById(R.id.avatar_2);
                TextView username = (TextView) findViewById(R.id.username);
                ImageView top01 = (ImageView) findViewById(R.id.top01);
                ImageView vip_icon = (ImageView) findViewById(R.id.vip_icon);
                TextView info = (TextView) findViewById(R.id.info);
                ImageView iv_online_status = (ImageView) findViewById(R.id.iv_online_status);
                username.setText(this.nickname);
                top01.setImageResource(this.isMan ? R.drawable.f1_top02 : R.drawable.f1_top01);
                vip_icon.setVisibility(Vip ? View.VISIBLE : View.GONE);

                if (distanceict == null || distanceict.trim().length() == 0) {
                    info.setText(age + "岁");
                } else {
                    info.setText(age + "岁 • " + distanceict);
                }

                iv_online_status.setImageResource(R.drawable.vip_dlg_title_bg_online);
            } else {
                RelativeLayout avatar_layout = (RelativeLayout) findViewById(R.id.avatar_layout);
                avatar_layout.setVisibility(View.VISIBLE);
                avatar_layout.bringToFront();
                avatar_layout.requestLayout();
                content.setBackgroundResource(R.drawable.index_yuliao_vip_head_bg);
                avatar = (ImageView) findViewById(R.id.avatar);
            }

            ImageLoader.loadCircleAvatar(getContext(), R.drawable.default_head, avatar);
            if (!TextUtils.isEmpty(avaterUrl)) {
                ImageLoader.loadCircleAvatar(getContext(), avaterUrl, avatar);
            }
        } else {
            content.setBackgroundResource(R.drawable.f1_vip_head_bg);
        }
    }


    private void initText() {
        switch (type) {
            case GoodsConstant.DLG_VIP_AV_CHAT:
                vip_tips.setText(getString(R.string.goods_vip_av_chat));
                break;

            case GoodsConstant.DLG_VIP_SEARCH_B:
                vip_tips.setText(getString(R.string.goods_vip_search_b));
                break;

            case GoodsConstant.DLG_VIP_CHOOSE_B:
                vip_tips.setText(getString(R.string.goods_vip_choose_b));
                break;

            case GoodsConstant.DLG_VIP_CHECK_ALBUM:
                vip_tips.setText(getString(R.string.goods_vip_check_album));
                break;
            case GoodsConstant.DLG_VIP_INDEX_YULIAO:
                btn_cancel.setVisibility(View.GONE);
                vip_tips.setText(TextUtils.isEmpty(vipTips) ? getString(R.string.goods_vip_index_yuliao) : vipTips);
                vip_tips.setTextColor(ContextCompat.getColor(getContext(), R.color.text_zhuyao_black));
                findViewById(R.id.header).setVisibility(View.VISIBLE);
                TextView call_type = (TextView) findViewById(R.id.call_type);
                if(AgoraConstant.RTC_CHAT_VIDEO  == this.chatType){
                    call_type.setText("视频通话:");
                }else if(AgoraConstant.RTC_CHAT_VOICE  == this.chatType){
                    call_type.setText("音频通话:");
                }
                TextView price_tv = (TextView) findViewById(R.id.price);
                price_tv.setText(price+ "钻/分钟");

                break;
            case GoodsConstant.DLG_VIP_INDEX_YULIAO_AVATER:
                vip_tips.setText(getString(R.string.goods_vip_index_yuliao));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_recharge:  // 充值
                //当配置信息中的数据为null或者数量为0的话给个提示，防止报错奔溃
                if(goodsList == null || goodsList.isEmpty()){
                    PToast.showLong("无法获取商品信息！");
                    return;
                }
                Goods payGood = goodsList.get(goodsPanel.getPosition());
                UIShow.showPayNoListAct(getActivity(), payGood.getPid(), payTypePanel.getPayType(), 0, "");
                StatisticsUser.abTestVipPay(payGood.getPid(), payGood.getCost(), payTypePanel.getPayType());
                dismiss();
                break;

            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.tv_look_vip:
                UIShow.showOpenVipActivity(getContext());
                break;
        }
    }
}