package com.juxin.predestinate.ui.pay;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.pay.goods.PayGood;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Kind on 2017/4/19.
 */
public class BasePayPannel extends BasePanel implements View.OnClickListener {

    private FragmentActivity activity;
    private PayGood payGood;

    public ImageView paylist_item_icon;
    public TextView paylist_item_title, paylist_item_remark;


    public BasePayPannel(FragmentActivity activity, PayGood payGood) {
        super(activity);
        this.activity = activity;
        this.payGood = payGood;
        setContentView(R.layout.y2_paylist_item);
        findViewById(R.id.paylist_item).setOnClickListener(this);
        paylist_item_icon = (ImageView) findViewById(R.id.paylist_item_icon);
        paylist_item_title = (TextView) findViewById(R.id.paylist_item_title);
        paylist_item_remark = (TextView) findViewById(R.id.paylist_item_remark);


    }

    /**
     * activity
     *
     * @return
     */
    public FragmentActivity getActivity() {
        return this.activity;
    }

    public PayGood getPayGood() {
        return payGood;
    }

    @Override
    public void onClick(View v) {
    }


    public void setTextTitle(String text) {
        paylist_item_title.setText(text);
    }

    public void setTextRemark(String text) {
        paylist_item_remark.setText(text);
    }

    public void setImageIcon(int resId) {
        paylist_item_icon.setImageResource(resId);
    }


    public static  String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String key = format.format(new Date());
        int i = new Random().nextInt(89999) + 10000;
        String str = key + i;
        return str;
    }
}
