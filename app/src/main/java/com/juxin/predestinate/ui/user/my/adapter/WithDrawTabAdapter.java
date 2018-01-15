package com.juxin.predestinate.ui.user.my.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.WithdrawList;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIShow;


/**
 * 提现记录
 * Created by zm on 2017/4/13.
 */
public class WithDrawTabAdapter extends ExBaseAdapter<WithdrawList.WithdrawInfo> {

    private Context mContext;

    public WithDrawTabAdapter(Context context) {
        super(context, null);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyViewHolder vh;
        if (convertView == null) {
            convertView = inflate(R.layout.f1_withdraw_panel_item);
            vh = new MyViewHolder(convertView);

            convertView.setTag(vh);
        } else {
            vh = (MyViewHolder) convertView.getTag();
        }

        final WithdrawList.WithdrawInfo info = getList().get(position);
        vh.llError.setVisibility(View.GONE);
        if (info.isFrist() && !TextUtils.isEmpty(info.getDate())) {
            vh.llTitle.setVisibility(View.VISIBLE);
            vh.tvMon.setText(info.getDate());
        } else {
            vh.llTitle.setVisibility(View.GONE);
        }
        if (info.isLast() && position != getList().size() - 1) {
            vh.viewLine.setVisibility(View.GONE);
        } else {
            vh.viewLine.setVisibility(View.VISIBLE);
        }
        String time = info.getCreate_time();
        if (!TextUtils.isEmpty(time)) {//时间处理
            String[] tempArr = time.split(" ");
            vh.tvData.setText(tempArr[0] + " " + tempArr[1].substring(0, 5));
        }
        vh.tvData.setTextColor(mContext.getResources().getColor(R.color.color_D4D4D4));
        vh.tvMoney.setTextColor(mContext.getResources().getColor(R.color.black));
        vh.tvMoney.setText(getContext().getString(R.string.detail_income, String.valueOf(info.getMoney())));
        convertView.setOnClickListener(null);
        switch (info.getStatus()) {
            case 1:
                vh.tvStatus.setText(R.string.untreated);
                vh.tvStatus.setTextColor(mContext.getResources().getColor(R.color.color_dcb880));
                vh.tvStatus.setBackgroundResource(0);
                break;
            case 2:
                vh.tvStatus.setText(R.string.withdraw_succeed);
                vh.tvStatus.setTextColor(mContext.getResources().getColor(R.color.color_565d74));
                vh.tvStatus.setBackgroundResource(0);
                break;
            case 3:
                vh.tvStatus.setText(R.string.withdraw_failure);
                vh.tvMoney.setText(R.string.click_on_the_modify);
                vh.tvStatus.setTextColor(mContext.getResources().getColor(R.color.color_ed6363));
                vh.tvMoney.setTextColor(mContext.getResources().getColor(R.color.color_6ba8f4));
                vh.tvData.setTextColor(mContext.getResources().getColor(R.color.color_7c9cae));
                vh.tvData.setText("由于您的银行卡信息不正确，提现未成功");
                vh.tvStatus.setBackgroundResource(0);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //红包提现--银行卡界面
                        if (mContext != null) {
                            UIShow.showWithDrawApplyAct((int) info.getId(), info.getMoney(), true, info.getStatus(), (FragmentActivity) mContext);
                        }
                    }
                });
                break;
        }
        return convertView;
    }

    class MyViewHolder {

        TextView tvData, tvMoney, tvStatus, tvMon;
        LinearLayout llSuccess, llError, llTitle;
        View viewLine;

        public MyViewHolder(View convertView) {
            initView(convertView);
        }

        private void initView(View convertView) {
            tvData = (TextView) convertView.findViewById(R.id.withdraw_item_tv_date);
            tvMoney = (TextView) convertView.findViewById(R.id.withdraw_item_tv_money);
            tvStatus = (TextView) convertView.findViewById(R.id.withdraw_item_tv_status);
            llSuccess = (LinearLayout) convertView.findViewById(R.id.withdraw_item_ll_success);
            llError = (LinearLayout) convertView.findViewById(R.id.withdraw_item_ll_error);
            llTitle = (LinearLayout) convertView.findViewById(R.id.withdraw_item_ll_title);
            tvMon = (TextView) convertView.findViewById(R.id.withdraw_item_tv_mon);
            viewLine = convertView.findViewById(R.id.withdraw_item_view_line);
        }
    }
}