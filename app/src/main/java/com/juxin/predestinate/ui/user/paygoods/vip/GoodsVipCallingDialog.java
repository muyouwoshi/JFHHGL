package com.juxin.predestinate.ui.user.paygoods.vip;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.ViewUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.config.base.Goods;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;
import com.juxin.predestinate.ui.user.paygoods.GoodsListPanel;
import com.juxin.predestinate.ui.user.paygoods.GoodsPayTypePanel;

import java.util.List;

/**
 * @author Mr.Huang
 * @date 2017/9/15
 * 专门为首页推送的视频消息做的
 */
public class GoodsVipCallingDialog extends BaseDialogFragment implements View.OnClickListener {

    private GoodsListPanel goodsPanel; // 商品列表
    private GoodsPayTypePanel payTypePanel; // 支付方式
    private TextView tv_age_km, tv_name, tv_price, tv_tip;
    private ImageView iv_avatar, iv_level, iv_vip, iv_charm, iv_online_status;
    private View layout_tip;
    private String videoPriceText = "<font color=\"#E0BA7E\">视频通话：</font>%d钻/分钟";
    private String tipString = "对方是%s，开通vip会员才有视频通话的权限";
    private String distance = "";

    private UserDetail user;
    private long uid;

    // vip购买在线配置
    private List<Goods> goodsList = ModuleMgr.getCenterMgr().getMyInfo().isB() ?
            ModuleMgr.getCommonMgr().getCommonConfig().getPay().getBBuyVipList() :
            ModuleMgr.getCommonMgr().getCommonConfig().getPay().getBuyVipList();

    public GoodsVipCallingDialog() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, -1);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_recharge) {
            //当配置信息中的数据为null或者数量为0的话给个提示，防止报错奔溃
            if(goodsList == null || goodsList.isEmpty()){
                PToast.showLong("无法获取商品信息！");
                return;
            }
            Goods payGood = goodsList.get(goodsPanel.getPosition());
            UIShow.showPayNoListAct(getActivity(), payGood.getPid(), payTypePanel.getPayType(), 0, "");
            StatisticsUser.abTestVipPay(payGood.getPid(), payGood.getCost(), payTypePanel.getPayType());
            dismiss();
        } else if (id == R.id.tv_look_vip) {
            UIShow.showOpenVipActivity(getContext());
        }
    }

    public void setUserInfo(UserDetail user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.peerage_dialog_pay_vip, container, false);
        initViews(view);
        LinearLayout layout_goods_container = ViewUtils.findById(view, R.id.layout_goods_container);
        LinearLayout layout_pay_type_container = ViewUtils.findById(view, R.id.layout_pay_type_container);
        goodsPanel = new GoodsListPanel(getContext(), GoodsConstant.DLG_VIP_VIDEO_NEW);
        layout_goods_container.addView(goodsPanel.getContentView());
        goodsPanel.refresh(goodsList);
        payTypePanel = new GoodsPayTypePanel(getContext(), GoodsConstant.PAY_TYPE_NEW);
        layout_pay_type_container.addView(payTypePanel.getContentView());
        view.findViewById(R.id.btn_recharge).setOnClickListener(this);
        view.findViewById(R.id.tv_look_vip).setOnClickListener(this);
        initData();
        if (user == null && uid > 0) {
            ModuleMgr.getCenterMgr().reqOtherInfo(uid, new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    if (response.isOk()) {
                        UserDetail detail = new UserDetail();
                        detail.parseJson(response.getResponseString());
                        detail.getChatInfo().parseJsonOtherInfo();
                        setUserInfo(detail);
                        initData();
                    } else {
                        PToast.showLong("获取用户信息失败！");
                    }
                }
            });
        }
        return view;
    }

    private void initData() {
        if (user == null) {
            ImageLoader.loadCircleAvatar(getContext(), R.drawable.default_head, iv_avatar);
            ImageLoader.loadFitCenter(getContext(), 11, iv_level);
            tv_tip.setText("你非VIP会员，不可以接听高爵位女性视频");
            iv_vip.setVisibility(View.GONE);
            iv_charm.setVisibility(View.GONE);
            tv_price.setText(Html.fromHtml(String.format(videoPriceText, 50)));
            return;
        }
        ImageLoader.loadCircleAvatar(getContext(), user.getAvatar(), iv_avatar);
        tv_name.setText(user.getNickname());
        if (user.getNobilityInfo().getRank() > 0) {
            NobilityList.Nobility nobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(user.getNobilityInfo().getRank(), user.getGender());
            ImageLoader.loadFitCenter(getContext(), nobility.getTitle_icon(), iv_level);
            tv_tip.setText(String.format(tipString, nobility.getStar_name() + nobility.getTitle_name()));
            layout_tip.setVisibility(View.VISIBLE);
        } else {
            iv_level.setVisibility(View.GONE);
            layout_tip.setVisibility(View.GONE);
        }
        if (user.isVip()) {
            iv_vip.setVisibility(View.VISIBLE);
        } else {
            iv_vip.setVisibility(View.GONE);
        }
        iv_charm.setImageResource(user.isMan() ? R.drawable.f1_top02 : R.drawable.f1_top01);
        tv_age_km.setText(String.valueOf(user.getAge()));
        tv_age_km.append("岁");
        //没有距离
        if (user.getDistance() > 0) {
            tv_age_km.append(" • ");
            tv_age_km.append(String.valueOf(user.getDistance()));
            tv_age_km.append("km");
        } else if (!TextUtils.isEmpty(distance)) {
            tv_age_km.append(" • ");
            tv_age_km.append(distance);
        }
        if (user.isOnline()) {
            iv_online_status.setVisibility(View.VISIBLE);
        } else {
            iv_online_status.setVisibility(View.GONE);
        }
        tv_price.setText(Html.fromHtml(String.format(videoPriceText, user.getChatInfo().getVideoPrice())));
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    private void initViews(View view) {

        layout_tip = view.findViewById(R.id.layout_tip);

        tv_age_km = ViewUtils.findById(view, R.id.tv_age_km);
        tv_name = ViewUtils.findById(view, R.id.tv_name);
        tv_price = ViewUtils.findById(view, R.id.tv_price);
        tv_tip = ViewUtils.findById(view, R.id.tv_tip);

        Drawable drawable = getContext().getResources().getDrawable(R.drawable.peerage_dialog_tip_icon);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2, drawable.getMinimumHeight() / 2);
        tv_tip.setCompoundDrawables(drawable, null, null, null);
        tv_tip.setCompoundDrawablePadding(ViewUtils.dpToPx(2));

        iv_avatar = ViewUtils.findById(view, R.id.iv_avatar);
        iv_level = ViewUtils.findById(view, R.id.iv_level);
        iv_vip = ViewUtils.findById(view, R.id.iv_vip);
        iv_charm = ViewUtils.findById(view, R.id.iv_charm);
        iv_online_status = ViewUtils.findById(view, R.id.iv_online_status);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFFFF4E9);
        gd.setCornerRadius(ViewUtils.dpToPx(3));
        View layout_tip = view.findViewById(R.id.layout_tip);
        layout_tip.setBackgroundDrawable(gd);
    }

    @Override
    public void show(FragmentManager manager, String tag) {

    }

    public void show(FragmentManager manager) {
        if (user != null) {
            super.show(manager, "");
        }
    }

    public void show(FragmentManager manager, long uid) {
        this.user = null;
        this.uid = uid;
        super.show(manager, "");
    }
}
