package com.juxin.predestinate.ui.user.my.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.AddredTotalInfo;
import com.juxin.predestinate.bean.my.RedbagList;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.ui.user.my.RedBagRecordPanel;
import com.juxin.predestinate.ui.user.my.RedBoxRecordAct;


/**
 * 红包列表
 * Created by zm on 2017/4/13.
 */
public class RedBagTabAdapter extends ExBaseAdapter<RedbagList.RedbagInfo> implements RequestComplete {

    private int mPosition = -1;
    private Context mContext;
    private RedBagRecordPanel mRedBagRecordPanel;

    public RedBagTabAdapter(Context context, RedBagRecordPanel mRedBagRecordPanel) {
        super(context, null);
        this.mContext = context;
        this.mRedBagRecordPanel = mRedBagRecordPanel;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyViewHolder vh;
        if (convertView == null) {
            convertView = inflate(R.layout.f1_wode_withdraw_item);
            vh = new MyViewHolder(convertView);

            convertView.setTag(vh);
        } else {
            vh = (MyViewHolder) convertView.getTag();
        }

        final RedbagList.RedbagInfo info = getItem(position);
        String time = info.getCreate_time();//获取获得红包的时间
        if (!TextUtils.isEmpty(time)) {
            String[] tempArr = time.split(" ");
            vh.tvData.setText(tempArr[0] + " " + tempArr[1].substring(0, 5));
        }
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
        //路径 服务器新增字段：type_text
        //红包类型 1 水果游戏红包(旧) 2 打擂奖励/排行奖励 3 聊天红包 4 聊天排名奖励(旧) 5 礼物折现 6. 摇钱树红包 7.8 好友邀请 9.视频聊天 10.语音聊天
        // 11.视频任务补贴 12.语音任务补贴 13.礼物任务补贴 14.活跃奖金
        vh.tvPath.setText(info.getType_text());
        vh.tvMoney.setText(getContext().getString(R.string.detail_income, String.valueOf(info.getMoney())));
        //操作
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadingDialog.show((FragmentActivity) mContext);
                mPosition = position;
                ModuleMgr.getCommonMgr().reqAddredTotal(ModuleMgr.getCenterMgr().getMyInfo().getUid(), info.getMoney() * 100f, info.getId(), info.getType(), RedBagTabAdapter.this);
            }
        });

        return convertView;
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        LoadingDialog.closeLoadingDialog();
        AddredTotalInfo info = new AddredTotalInfo();
        info.parseJson(response.getResponseString());
        if (response.isOk()) {
            //更新可提现金额
            getList().remove(mPosition);
            RedbagList.oldDate = "";
            RedbagList.oldDate = null;
            for (int i = 0; i < getList().size(); i++) {
                getList().get(i).setCreate_time(getList().get(i).getCreate_time());
            }
            notifyDataSetChanged();
            mRedBagRecordPanel.handleData();
            ((RedBoxRecordAct) mContext).refreshView(info.getSum());
        }
        PToast.showShort(info.getMsg() + "");//展示提示
    }

    class MyViewHolder {
        TextView tvData, tvPath, tvMoney, tvMon;
        LinearLayout llTitle;
        View viewLine;

        public MyViewHolder(View convertView) {
            initView(convertView);
        }

        private void initView(View convertView) {
            tvData = (TextView) convertView.findViewById(R.id.withdraw_tv_date);
            tvPath = (TextView) convertView.findViewById(R.id.withdraw_tv_path);
            tvMoney = (TextView) convertView.findViewById(R.id.withdraw_tv_money);
            llTitle = (LinearLayout) convertView.findViewById(R.id.withdraw_item_ll_title);
            tvMon = (TextView) convertView.findViewById(R.id.withdraw_item_tv_mon);
            viewLine = convertView.findViewById(R.id.withdraw_item_view_line);
        }
    }
}