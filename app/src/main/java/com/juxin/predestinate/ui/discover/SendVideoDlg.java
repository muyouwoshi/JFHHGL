package com.juxin.predestinate.ui.discover;

import android.os.Bundle;
import android.support.annotation.IntDef;
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
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.config.base.Goods;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;
import com.juxin.predestinate.ui.user.paygoods.GoodsListPanel;
import com.juxin.predestinate.ui.user.paygoods.GoodsPayTypePanel;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送视频语音提示弹窗
 * Created by Administrator on 2017/8/31.
 */
public class SendVideoDlg extends BaseDialogFragment implements View.OnClickListener {

    private TextView send;
    private UserInfoLightweight userInfoLightweight;
    public final static int VIDEO = 0;//视频
    public final static int VOICE = 1;//语音
    public int mType = VIDEO;

    public final static int viewDefault = 0;
    public final static int viewSwitchVipDiamonds = 1;
    public final static int viewVideoSend = 2;
    public int mViewType = viewDefault;
    private List<Goods> payGoods = new ArrayList<>();
    private TextView videoCardNum;
    private ImageView videoCardPay_select;
    private ImageView diamonds_select;
    private TextView diamondsPay_desc;
    private TextView diamondsPay_title;
    private LinearLayout remainderDiamonds;
    public int SendType = 0;//mViewType=viewSwitchVipDiamonds时才有此值  0是发送，1是支付
    private GoodsListPanel goodsPanel;
    private GoodsPayTypePanel payTypePanel;

    private BackCall mBackCall;
    private int mPrice;
    public int mVideoCardNum = 0;
    private int mDiamondsNum = 0;
    private RelativeLayout diamondsPay;
    private LinearLayout container;
    private RelativeLayout videoCardPay;

    @IntDef({VIDEO, VOICE})
    public @interface Type {
    }

    @IntDef({viewDefault, viewSwitchVipDiamonds, viewVideoSend})
    public @interface viewType {
    }

    public SendVideoDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, -1);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.index_sendvideo_bottom_dlg);
        View contentView = getContentView();
        initView(contentView);
        return contentView;
    }

    private void initView(View contentView) {
        findViewById(R.id.price_layout).setVisibility(View.GONE);
        send = (TextView) contentView.findViewById(R.id.send);
        send.setOnClickListener(this);

        TextView price = (TextView) contentView.findViewById(R.id.price_tv);
        TextView diamond = (TextView) contentView.findViewById(R.id.diamand);
        LinearLayout default_view = (LinearLayout) contentView.findViewById(R.id.default_view);
        TextView username = (TextView) contentView.findViewById(R.id.username);
        ImageView top00 = (ImageView) contentView.findViewById(R.id.top00);
        ImageView top01 = (ImageView) contentView.findViewById(R.id.top01);
        ImageView vip_icon = (ImageView) contentView.findViewById(R.id.vip_icon);
        TextView info = (TextView) contentView.findViewById(R.id.info);
        ImageView iv_online_status = (ImageView) contentView.findViewById(R.id.iv_online_status);
        ImageView avatar_2 = (ImageView) findViewById(R.id.avatar_2);
        avatar_2.setOnClickListener(this);
        price.setText((mType == VIDEO ? "视频通话:" : "音频通话:") + mPrice + "钻石/分钟");
        diamond.setText(String.valueOf(ModuleMgr.getCenterMgr().getMyInfo().getDiamand()));

        if (userInfoLightweight != null) {
            username.setText(userInfoLightweight.getNickname());
            ImageLoader.loadFitCenter(
                    getContext(), ModuleMgr.getCommonMgr().getCommonConfig()
                            .queryNobility(userInfoLightweight.getNobility_rank(),
                                    userInfoLightweight.getGender()).getTitle_icon(), top00);
            top01.setImageResource(userInfoLightweight.isMan() ? R.drawable.f1_top02 : R.drawable.f1_top01);
            vip_icon.setVisibility(userInfoLightweight.isVip() ? View.VISIBLE : View.GONE);
            info.setText(userInfoLightweight.getAge() + "岁 • " + userInfoLightweight.getDistance());
            iv_online_status.setImageResource(R.drawable.vip_dlg_title_bg_online);
            ImageLoader.loadCircleAvatar(getContext(), userInfoLightweight.getAvatar(), avatar_2);
        }

        container = (LinearLayout) contentView.findViewById(R.id.pay_type_container);
        LinearLayout videoCardDiamand = (LinearLayout) contentView.findViewById(R.id.videoCardDiamand);
        TextView dsec = (TextView) contentView.findViewById(R.id.dsec);

        if (mViewType == viewSwitchVipDiamonds) {
            videoCardDiamand.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);

            videoCardPay = (RelativeLayout) contentView.findViewById(R.id.videoCardPay);
            videoCardPay.setOnClickListener(this);
            videoCardNum = (TextView) contentView.findViewById(R.id.videoCardNum);
            videoCardNum.setText("免费视频卡(" + mVideoCardNum + "张)");

            diamondsPay = (RelativeLayout) contentView.findViewById(R.id.diamondsPay);
            diamondsPay.setOnClickListener(this);

            videoCardPay_select = (ImageView) contentView.findViewById(R.id.videoCardPay_select);
            diamonds_select = (ImageView) contentView.findViewById(R.id.diamonds_select);
            diamonds_select.setVisibility(View.GONE);
            diamondsPay_desc = (TextView) contentView.findViewById(R.id.diamondsPay_desc);
            diamondsPay_desc.setText(mDiamondsNum + "钻/分钟");
            diamondsPay_title = (TextView) contentView.findViewById(R.id.diamondsPay_title);

            remainderDiamonds = (LinearLayout) contentView.findViewById(R.id.remainderDiamonds);
            remainderDiamonds.setVisibility(View.GONE);
            videoCardPay.setSelected(true);
            diamondsPay.setSelected(false);
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

            TextView tv = new TextView(getContext());
            tv.setText("钻石不足，购买钻石可与她视频");
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.color_333333));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            lp.topMargin = (int) getResources().getDimension(R.dimen.px30_dp);
            tv.setLayoutParams(lp);
            container.addView(tv);

            goodsPanel = new GoodsListPanel(getContext(), GoodsConstant.DLG_DIAMOND_YULIAO);
            container.addView(goodsPanel.getContentView());
            goodsPanel.refresh(payGoods);

            // 支付方式
            payTypePanel = new GoodsPayTypePanel(getContext(), GoodsConstant.PAY_TYPE_NEW);
            container.addView(payTypePanel.getContentView());
        } else if (mViewType == viewVideoSend) {
            default_view.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
            videoCardDiamand.setVisibility(View.GONE);
            dsec.setText("发送邀请与对方视频");
            price.setText("(" + mPrice + "钻/分钟)");
            if (mType == VIDEO) {
                send.setText("发送视频");
            } else {
                send.setText("发送语音");
            }
        } else {
            send.setText("发送");
            videoCardDiamand.setVisibility(View.GONE);
            default_view.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);

    }

    public void setCallAndPrice(BackCall call, int price, @Type int type, UserInfoLightweight userInfoLightweight) {
        mBackCall = call;
        mPrice = price;
        this.mType = type;
        this.userInfoLightweight = userInfoLightweight;
    }

    public void viewSwitchVipDiamonds(int videoCardNum, int diamonds) {
        this.mViewType = viewSwitchVipDiamonds;
        this.mVideoCardNum = videoCardNum;
        this.mDiamondsNum = diamonds;
    }

    public void setViewType(@viewType int type) {
        this.mViewType = type;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                if (mViewType == viewDefault) {
                    if (mBackCall != null) {
                        mBackCall.call();
                    }
                } else {
                    if (SendType == 0) {
                        if (mBackCall != null) {
                            mBackCall.call();
                        }
                    } else {
                        if (mDiamondsNum > ModuleMgr.getCenterMgr().getMyInfo().getDiamand()) {
                            dismiss();
                            if (payGoods.size() <= 0) return;

                            Goods payGood = payGoods.get(goodsPanel.getPosition());

                            UIShow.showPayNoListAct(getActivity(), payGood.getPid(), payTypePanel.getPayType(), 0, "");
                        } else {
                            if (mBackCall != null) {
                                mBackCall.call();
                            }
                        }
                    }
                }
                break;

            case R.id.avatar_2:
                if (mViewType == viewSwitchVipDiamonds) {
                    UIShow.showCheckOtherInfoAct(getContext(), userInfoLightweight.getUid());
                }
                break;

            case R.id.videoCardPay://视频卡
                SendType = 0;
                videoCardPay.setSelected(true);
                diamondsPay.setSelected(false);
                videoCardNum.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FD7194));
                diamondsPay_title.setTextColor(ContextCompat.getColor(getContext(), R.color.text_zhuyao_black));
                diamondsPay_desc.setTextColor(ContextCompat.getColor(getContext(), R.color.text_ciyao_gray));
                diamonds_select.setVisibility(View.GONE);
                videoCardPay_select.setVisibility(View.VISIBLE);
                remainderDiamonds.setVisibility(View.GONE);
                container.setVisibility(View.GONE);
                send.setText("发送视频");
                break;

            case R.id.diamondsPay://钻石
                SendType = 1;
                videoCardPay.setSelected(false);
                diamondsPay.setSelected(true);
                videoCardNum.setTextColor(ContextCompat.getColor(getContext(), R.color.text_zhuyao_black));
                diamondsPay_title.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FD7194));
                diamondsPay_desc.setTextColor(ContextCompat.getColor(getContext(), R.color.color_FD7194));
                diamonds_select.setVisibility(View.VISIBLE);
                videoCardPay_select.setVisibility(View.GONE);
                remainderDiamonds.setVisibility(View.VISIBLE);

                if (mDiamondsNum > ModuleMgr.getCenterMgr().getMyInfo().getDiamand()) {
                    container.setVisibility(View.VISIBLE);
                    send.setText("购买钻石");
                } else {
                    container.setVisibility(View.GONE);
                    send.setText("发送视频");
                }
                break;
        }
    }

    public interface BackCall {
        public void call();
    }
}
