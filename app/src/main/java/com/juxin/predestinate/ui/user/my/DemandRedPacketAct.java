package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.QunCountInfo;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.my.GiftHelper;

/**
 * 我的钱包页面
 * Created by zm on 2017/4/20
 */
public class DemandRedPacketAct extends BaseActivity implements View.OnClickListener, RequestComplete {

    private TextView tvSendNum;
    private AskForGiftDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_demand_red_packet_act);
        initView();
        initData();
    }

    private void initView() {
        setBackView(R.id.base_title_back);
        setTitle(getString(R.string.user_info_earn_redbag));
        findViewById(R.id.demand_red_packet_tv_askfor_gift_send).setOnClickListener(this);
        tvSendNum = (TextView) findViewById(R.id.demand_red_packet_tv_askfor_gift_send_num);
        findViewById(R.id.demand_red_packet_ll_askfor_gift_setting).setOnClickListener(this);
    }

    private void initData() {
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.qunCount, null, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.demand_red_packet_tv_askfor_gift_send://索要礼物弹框
                Statistics.userBehavior(SendPoint.menu_me_redpackage_sylw);

                dialog = null;
                if (ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts().size() > 0) {
                    dialog = new AskForGiftDialog(DemandRedPacketAct.this, ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts());
                    dialog.show();
                } else {
                    LoadingDialog.show(DemandRedPacketAct.this);
                    ModuleMgr.getCommonMgr().requestGiftList(new GiftHelper.OnRequestGiftListCallback() {
                        @Override
                        public void onRequestGiftListCallback(boolean isOk) {
                            LoadingDialog.closeLoadingDialog();
                            if (isOk) {
                                if (ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts().size() > 0 && dialog != null) {
                                    dialog = new AskForGiftDialog(DemandRedPacketAct.this, ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts());
                                    dialog.show();
                                }
                            } else {
                                PToast.showShort("数据加载失败，请检查您的网络");
                            }
                        }
                    });
                }
                break;
            case R.id.demand_red_packet_ll_askfor_gift_setting:
                UIShow.showRotaryActivity(this);
                Statistics.userBehavior(SendPoint.menu_me_redpackage_dzp);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        QunCountInfo info = new QunCountInfo();
        info.parseJson(response.getResponseString());
        if (info.isOk()) {
            tvSendNum.setText(info.getCount() + " 人");
            return;
        }
        PToast.showShort(getString(R.string.net_error));
    }
}