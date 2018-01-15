package com.juxin.predestinate.ui.discover.task;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PSP;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 赚钱秘籍提示（仅女性用户提示）
 * Created by zhang on 2017/6/19.
 */
@Deprecated
public class MakeMoneyTipDialog extends BaseDialogFragment implements View.OnClickListener {

    private ImageView tip_img;
    private Button suer_btn;
    private ImageView cancel_btn;
    private TextView people_num_tv;

    private UserDetail userDetail;

    private String People_Key = "MakeMoney_Tip_Num_Key";

    private boolean isUserAuth; //是否通过认证 指全部认证 （手机、身份证、视频）
    private SpannableString msp;

    public MakeMoneyTipDialog() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(0, 0);
        setCancelable(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_make_money_dialog);
        initData();
        initView();
        return getContentView();
    }

    private void initData() {
        userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        isUserAuth = userDetail.isVerifyAll();
    }

    private void initView() {
        tip_img = (ImageView) findViewById(R.id.make_money_img_tip);
        suer_btn = (Button) findViewById(R.id.make_money_sure_btn);
        cancel_btn = (ImageView) findViewById(R.id.make_money_cancel_btn);
        people_num_tv = (TextView) findViewById(R.id.make_money_people_num);

        ImageLoader.loadRoundTop(getActivity(), R.drawable.f1_actimg, tip_img, 30, R.drawable.default_pic, R.drawable.default_pic);


        int peopleNum = getNum(People_Key, 999999);
        String tvStr = String.format(getString(R.string.make_money_num_tip), peopleNum);
        msp = new SpannableString(tvStr);
        msp.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.txt_auth_status_error)),
                2, String.valueOf(peopleNum).length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        people_num_tv.setText(msp);
        suer_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.make_money_sure_btn:
                Statistics.userBehavior(SendPoint.page_loginpopup_money_goview);
                UIShow.showEarnMoney(getActivity());
                dismiss();
                break;
            case R.id.make_money_cancel_btn:
                Statistics.userBehavior(SendPoint.page_loginpopup_money_close);
                closeDialog();
                break;
        }
    }

    private void closeDialog() {
        if (isUserAuth) {
            dismiss();
        } else {
            UIShow.showUserAuthDlg(getActivity(), Constant.OPEN_FROM_HOME);
            dismiss();
        }
    }

    /**
     * @param key
     * @param size
     * @return
     */
    private int getNum(String key, int size) {
        int tmp = PSP.getInstance().getInt(key, 0);
        int num = 0;
        if (0 != tmp) {
            try {
                return tmp + (int) (Math.random() * 50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        num = (int) (Math.random() * size) + 1000;
        PSP.getInstance().put(key, num);
        return num;
    }

    /**
     * @author Mr.Huang
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        UIShow.showWebPushDialog(getActivity());//内部根据在线配置判断是否展示活动推送弹窗
    }
}
