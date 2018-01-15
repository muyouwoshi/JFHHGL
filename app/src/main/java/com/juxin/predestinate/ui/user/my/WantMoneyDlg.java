package com.juxin.predestinate.ui.user.my;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.util.UIShow;


public class WantMoneyDlg extends Dialog {
    private TextView tv_tips;
    private String MoneyPeople = "DlgMoneyPeople";
    private String MoneyCount = "DlgMoneyCount";
    private Context context;

    protected WantMoneyDlg(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public WantMoneyDlg(Context context) {
        this(context, R.style.dialog);
    }

    public WantMoneyDlg(Context context, int theme) {
        super(context, theme);
        this.context = context;
        initView(context);
    }


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

    private void initView(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.p1_dlg_askfor_money, null);
        setContentView(view);

        int people = getNum(MoneyPeople, 999999);
        int money = getNum(MoneyCount, 9999);

        tv_tips = (TextView) view.findViewById(R.id.dlg_wangmoney_tv_tips);
        tv_tips.setText(Html.fromHtml("已有<font color='#fff400'>" + people + "</font>人，赚到<font color='#fff400'>" + money + "</font>元现金"));
        findViewById(R.id.dlg_wangmoney_tv_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statistics.userBehavior(SendPoint.login_guide_moneyhelp);
                UIShow.showEarnRedBagAct(context);
                dismiss();
            }
        });

        findViewById(R.id.iv_dlg_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
