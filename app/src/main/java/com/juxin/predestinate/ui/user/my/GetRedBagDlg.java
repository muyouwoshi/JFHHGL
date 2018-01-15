package com.juxin.predestinate.ui.user.my;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.ui.user.util.CenterConstant;

/**
 * 创建日期：2017/10/11
 * 描述:恭喜你获得红包弹框
 *
 * @author :lc
 */
public class GetRedBagDlg extends BaseDialogFragment implements View.OnClickListener {

    private int redType;
    private TextView tvFirst, tvSecond;

    public GetRedBagDlg() {
        settWindowAnimations(R.style.AnimScaleInScaleOutOverShoot);
        setGravity(Gravity.CENTER);
        setDialogSizeRatio(1, 0);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.redbag_get_dlg);
        View view = getContentView();
        initView(view);
        PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.GET_RED_BAG_TIP), true);
        return view;
    }

    /**
     * 红包类型
     *
     * @param redType 2 视频聊天， 3 礼物， 4 充值 （注：无1 服务器规定）
     */
    public void setData(int redType) {
        this.redType = redType;
    }

    private void initView(View view) {
        tvFirst = (TextView) view.findViewById(R.id.tv_first);
        tvSecond = (TextView) view.findViewById(R.id.tv_second);
        ImageView ivClose = (ImageView) view.findViewById(R.id.iv_close);
        ImageView ivPutBag = (ImageView) view.findViewById(R.id.iv_put_bag);
        ivClose.setOnClickListener(this);
        ivPutBag.setOnClickListener(this);
        setTitle();
    }

    private void setTitle() {
        String firstStr = getString(R.string.gift_random_redbag);
        String secondStr = getString(R.string.gift_random_redbag);
        switch (redType) {
            case CenterConstant.TYPE_VIDEO:
                firstStr = getString(R.string.gift_red_video_top);
                secondStr = getString(R.string.gift_red_video_bot);
                break;
            case CenterConstant.TYPE_GIFT:
                firstStr = getString(R.string.gift_red_gift_top);
                secondStr = getString(R.string.gift_red_gift_bot);
                break;
            case CenterConstant.TYPE_RECHARGE:
                firstStr = getString(R.string.gift_red_recharge_top);
                secondStr = getString(R.string.gift_red_recharge_bot);
                break;
            case CenterConstant.TYPE_SHARE:
                firstStr = getString(R.string.gift_red_share_top);
                secondStr = getString(R.string.gift_red_share_bot);
                break;
            default:
                break;
        }
        tvFirst.setText(firstStr);
        tvSecond.setText(secondStr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
            case R.id.iv_put_bag:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        //1.点击 dlg 内部两个按钮（关闭和放入) 或者 dlg 外部
        //2.点击 手机物理返回键
        MsgMgr.getInstance().sendMsg(MsgType.MT_GET_BAG_TIP, "");
        Statistics.userBehavior(SendPoint.alert_redpackage_backpack);
    }
}
