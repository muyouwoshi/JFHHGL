package com.juxin.predestinate.ui.pay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.local.pay.goods.PayGood;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;

import java.util.List;

/**
 * 支付选择方式
 *
 * @author Kind
 * @date 2017/4/19
 * Edited by siow
 */

public class PayListAct extends BaseActivity implements View.OnClickListener {
    /**
     * 发起支付来源
     */
    private String source;
    private PayGood payGood;

    private TextView payListHelpTxt;
    private boolean helpTxt = true;

    PayCompleteCheckMgr payCompleteCheckMgr = new PayCompleteCheckMgr(this, false);
    private boolean pauseFlag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_paylistact);
        setBackView(R.id.base_title_back, "支付订单");
        initView();
    }

    private void initView() {
        payGood = (PayGood) getIntent().getSerializableExtra("payGood");
        if (payGood == null) {
            PToast.showShort(R.string.request_error);
            this.finish();
            return;
        }

        TextView payListTitle = (TextView) findViewById(R.id.paylist_title);

        payListTitle.setText(Html.fromHtml("订单信息：" + payGood.getPay_name() + " " + "<font color='#fd6c8e'>" + payGood.getPay_money() + "</font>" + " 元"));

        LinearLayout payListView = (LinearLayout) findViewById(R.id.paytype_list);
        BasePayPannel payAlipayPannel = new PayAlipayPannel(this, payGood);
        BasePayPannel payWXPannel = new PayWXPannel(this, payGood);

        List<String> payList = ModuleMgr.getCommonMgr().getCommonConfig().getPay().getPayTypes();
        if (null != payList && payList.size() > 0) {
            for (String temp : payList) {
                if (GoodsConstant.PAY_TYPE_WECHAT_NAME.equals(temp)) {
                    // 微信支付
                    payListView.addView(payWXPannel.getContentView());
                } else if (GoodsConstant.PAY_TYPE_ALIPAY_NAME.equals(temp)) {
                    // 支付宝支付
                    payListView.addView(payAlipayPannel.getContentView());
                }
            }
        } else {
            payListView.addView(payAlipayPannel.getContentView());
            payListView.addView(payWXPannel.getContentView());
        }

        findViewById(R.id.paylist_qq).setOnClickListener(this);
        payListHelpTxt = (TextView) findViewById(R.id.paylist_help_txt);
        findViewById(R.id.paylist_help).setOnClickListener(this);
    }

    private void lockSource() {
        String goodName = payGood.getPay_name();
        source = SourcePoint.getInstance().getSource(goodName);
        if (goodName.contains("VIP")) {
            if(!source.equals("menu_me_vipmark")){
                SourcePoint.getInstance().lockSource(source + "_vip_gopay");
            }
        } else if (goodName.contains("Y币")) {
            SourcePoint.getInstance().lockSource(source + "_y_gopay");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.paylist_qq: {
                UIShow.showPrivateChatAct(this, MailSpecialID.customerService.getSpecialID(), "");
                break;
            }
            case R.id.paylist_help: {
                if (helpTxt) {
                    payListHelpTxt.setVisibility(View.VISIBLE);
                    helpTxt = false;
                } else {
                    payListHelpTxt.setVisibility(View.GONE);
                    helpTxt = true;
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pauseFlag) {
            pauseFlag = false;
            payCompleteCheckMgr.startTimer();
        }
        //通知刷个人资料
        MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
        lockSource();
    }

    @Override
    protected void onPause() {
        unlockSource();
        super.onPause();
        pauseFlag = true;
        payCompleteCheckMgr.cancelTimer();
    }

    /**
     * 恢复source
     */
    private void unlockSource() {
        SourcePoint.getInstance().lockSource(source);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        payCompleteCheckMgr.cancelTimer();
    }
}
