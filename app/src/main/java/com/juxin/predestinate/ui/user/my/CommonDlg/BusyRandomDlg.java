package com.juxin.predestinate.ui.user.my.CommonDlg;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.BusyRandom;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.main.MainActivity;

/**
 * 创建日期：2017/9/12
 * 描述:女号忙线中...弹框
 * 作者:lc
 */
public class BusyRandomDlg extends BaseDialogFragment implements View.OnClickListener, RequestComplete {
    private String source;   //视屏发起来源
    private Context context;
    private TextView tv_sure, tv_close;

    private int vtype, price;
    private boolean usecard;

    public BusyRandomDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(1, 0);
        setCancelable(true);
    }

    /**
     * 同随机的一个女用户视频
     *
     * @param vtype   期望 聊天媒体类型 1视频, 2语音
     * @param price   期望价格
     * @param usecard 是否使用视频卡 1:true 0:false
     */
    public void setData(Context context, int vtype, int price, boolean usecard,String source) {
        this.context = context;
        this.vtype = vtype;
        this.price = price;
        this.usecard = usecard;
        this.source = source;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f2_friend_random_dlg);
        View view = getContentView();
        isRefreshMData();
        initView(view);
        return view;
    }

    private void initView(View view) {
        tv_sure = (TextView) view.findViewById(R.id.tv_sure);
        tv_close = (TextView) view.findViewById(R.id.tv_close);
        tv_sure.setOnClickListener(this);
        tv_close.setOnClickListener(this);
    }

    /**
     * 是否刷新语聊数据
     */
    private void isRefreshMData() {
        if (App.getActivity() instanceof MainActivity) {
            MsgMgr.getInstance().sendMsg(MsgType.MT_REFRESH_CHAT, true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure:
                PSP.getInstance().put("ISINVITE", true);
                ModuleMgr.getCommonMgr().reqVCRandUser(vtype, price, usecard ? 1 : 0, this);
                break;

            case R.id.tv_close:
                Statistics.userBehavior(SendPoint.alert_haoyou_busy_close);
                this.dismiss();
                break;

            default:
                break;
        }
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        try {
            if (response.isOk()) {
                dismissAllowingStateLoss();
                final BusyRandom busyRandom = new BusyRandom();
                busyRandom.parseJson(response.getResponseString());
                Statistics.userBehavior(SendPoint.alert_haoyou_busy_random, busyRandom.getTo_uid());//统计
                //参照ChatFragment   回应邀请
                int show = PSP.getInstance().getInt(ModuleMgr.getCommonMgr().getPrivateKey(Constant.APPEAR_FOREVER_TYPE), 0);
                VideoAudioChatHelper.getInstance().acceptInviteVAChat(busyRandom.getVc_id(), usecard, show, false,source, new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        if (response.isOk()) {
                            return;
                        }
                    }
                });
            } else {
                PToast.showShort("对不起，您的缘分还没到");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
