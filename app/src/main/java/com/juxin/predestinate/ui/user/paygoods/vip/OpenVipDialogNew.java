package com.juxin.predestinate.ui.user.paygoods.vip;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.config.base.Goods;
import com.juxin.predestinate.module.local.chat.MessageRet;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.socket.IMProxy;
import com.juxin.predestinate.module.logic.socket.NetData;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;
import com.juxin.predestinate.ui.user.paygoods.GoodsListPanel;
import com.juxin.predestinate.ui.user.paygoods.GoodsPayTypePanel;

import java.util.List;

/**
 * Created by zm on 2017/9/15.
 * 开通vip
 */
public class OpenVipDialogNew extends BaseDialogFragment implements View.OnClickListener {

    private LinearLayout pay_goods_layout, pay_type_layout;
    private GoodsListPanel goodsPanel;
    private GoodsPayTypePanel payTypePanel;

    private LinearLayout root, content;
    private TextView send, sayHello, payCheck;
    private ImageView imgCancel;
    private UserInfoLightweight userDetail;
    private String mDesc;
    private boolean mIsShowSayHello = true;
    private String mSendTxt = "";

    // vip购买在线配置
    private List<Goods> goodsList = ModuleMgr.getCenterMgr().getMyInfo().isB() ?
            ModuleMgr.getCommonMgr().getCommonConfig().getPay().getBBuyVipList() :
            ModuleMgr.getCommonMgr().getCommonConfig().getPay().getBuyVipList();
    private TextView desc_tv;

    public OpenVipDialogNew() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, -1);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f2_openvip_bottom_dlg);
        View contentView = getContentView();
        initView(contentView);
        initData();
        return contentView;
    }

    private void initView(View contentView) {
        send = (TextView) contentView.findViewById(R.id.send);

        pay_goods_layout = (LinearLayout) contentView.findViewById(R.id.pay_goods);
        pay_type_layout = (LinearLayout) contentView.findViewById(R.id.pay_type);
        root = (LinearLayout) contentView.findViewById(R.id.root);
        content = (LinearLayout) contentView.findViewById(R.id.content);
        sayHello = (TextView) contentView.findViewById(R.id.say_hello);
        payCheck = (TextView) contentView.findViewById(R.id.vip_check);
        imgCancel = (ImageView) contentView.findViewById(R.id.btn_cancel);
        desc_tv = (TextView) contentView.findViewById(R.id.desc_tv);
    }

    public void setData(UserInfoLightweight userDetail, String desc, boolean isShowSayHello, String sendTxt) {
        this.userDetail = userDetail;
        this.mDesc = desc;
        this.mIsShowSayHello = isShowSayHello;
        this.mSendTxt = sendTxt;
    }


    private void initData() {
        root.setOnClickListener(this);
        content.setOnClickListener(this);
        sayHello.setOnClickListener(this);
        payCheck.setOnClickListener(this);
        imgCancel.setOnClickListener(this);

        goodsPanel = new GoodsListPanel(getContext(), GoodsConstant.DLG_VIP_VIDEO_NEW);
        pay_goods_layout.addView(goodsPanel.getContentView());
        goodsPanel.refresh(goodsList);

        payTypePanel = new GoodsPayTypePanel(getContext(), GoodsConstant.PAY_TYPE_NEW);
        pay_type_layout.addView(payTypePanel.getContentView());

        send.setOnClickListener(this);

        if (!TextUtils.isEmpty(mDesc)) {
            desc_tv.setText(mDesc);
        }
        if (!TextUtils.isEmpty(mSendTxt)) {
            send.setText(mSendTxt);
        }
        if (mIsShowSayHello) {
            sayHello.setVisibility(View.VISIBLE);
        } else {
            sayHello.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                //当配置信息中的数据为null或者数量为0的话给个提示，防止报错奔溃
                if(goodsList == null || goodsList.isEmpty()){
                    PToast.showLong("无法获取商品信息！");
                    return;
                }
                dismiss();
                Goods payGood = goodsList.get(goodsPanel.getPosition());
                UIShow.showPayNoListAct(getActivity(), payGood.getPid(), payTypePanel.getPayType(), 0, "");
                StatisticsUser.abTestVipPay(payGood.getPid(), payGood.getCost(), payTypePanel.getPayType());
                break;
            case R.id.content:
                break;
            case R.id.say_hello:
                sayHello();
                break;
            case R.id.vip_check:
                UIShow.showOpenVipActivity(getContext());
                break;
            case R.id.btn_cancel:
            case R.id.root:
                dismiss();
                break;
        }
    }

    private void sayHello() {
        if (userDetail == null) {
            return;
        }
        if (userDetail.isSayHello()) {
            PToast.showShort(getString(R.string.user_info_hi_repeat));
            return;
        }

        ModuleMgr.getChatMgr().sendSayHelloMsg(String.valueOf(userDetail.getUid()),
                getString(R.string.say_hello_txt),
                userDetail.getKf_id(),
                ModuleMgr.getCenterMgr().isRobot(userDetail.getKf_id()) ?
                        Constant.SAY_HELLO_TYPE_ONLY : Constant.SAY_HELLO_TYPE_SIMPLE, new IMProxy.SendCallBack() {
                    @Override
                    public void onResult(long msgId, boolean group, String groupId, long sender, String contents) {
                        MessageRet messageRet = new MessageRet();
                        messageRet.parseJson(contents);

                        if (messageRet.getS() == 0) { // 成功
                            PToast.showShort(getString(R.string.user_info_hi_suc));
                            MsgMgr.getInstance().sendMsg(MsgType.MT_Say_HI_Notice, userDetail.getUid());
                            userDetail.setSayHello(true);
                        } else {
                            //170824 UPDATE START 打招呼提示使用消息体返回的错误信息
                            //PToast.showShort(getString(R.string.user_info_hi_fail));

                            if (!TextUtils.isEmpty(messageRet.getFailMsg())) {
                                PToast.showShort(messageRet.getFailMsg());
                            } else {
                                PToast.showShort(getString(R.string.user_info_hi_fail));
                            }
                            //170824 UPDATE END 打招呼提示使用消息体返回的错误信息
                        }
                        dismissAllowingStateLoss();
                    }

                    @Override
                    public void onSendFailed(NetData data) {
                        PToast.showShort(getString(R.string.user_info_hi_fail));
                    }
                });
    }
}
