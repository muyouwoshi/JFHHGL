package com.juxin.predestinate.ui.discover;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.discover.DiscoverSelect;
import com.juxin.predestinate.module.local.statistics.StatisticsDiscovery;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现筛选
 * Created by zhang on 2017/4/28.
 */

public class DiscoverSelectDialog extends BaseDialogFragment implements View.OnClickListener, DisCoverSelectAdapter.OnSelect {

    private ListView listView;

    private View btn_cancle;

    private DisCoverSelectAdapter adapter;

    private boolean isNear = false;

    private OnDialogItemClick onDialogItemClick;

    private List<DiscoverSelect> data = new ArrayList<>();

    public DiscoverSelectDialog() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(1, 0);
        setCancelable(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_discover_select_dialog);
        initView();
        initData();
        return getContentView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.discover_select_list);
        btn_cancle = findViewById(R.id.discover_select_cancle);
        btn_cancle.setOnClickListener(this);
    }

    private void initData() {
        String[] selectData = getResources().getStringArray(R.array.discover_select_item);
        for (int i = 0; i < selectData.length; i++) {
            DiscoverSelect select = new DiscoverSelect();
            select.setSelectType(selectData[i]);
            if (i == 0) {
                select.setSelect(!isNear);
            } else {
                select.setSelect(isNear);
            }
            data.add(select);
        }
        adapter = new DisCoverSelectAdapter(getActivity(), data, this);
        listView.setAdapter(adapter);
    }

    public void setOnItemClick(final OnDialogItemClick onItemClick) {
        onDialogItemClick = onItemClick;
    }

    public void setNear(boolean isNear) {
        this.isNear = isNear;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.discover_select_cancle:
                dismiss();
                //统计
                StatisticsDiscovery.onClickCancel();
                break;
        }
    }

    @Override
    public void onSelectChange(int position, DiscoverSelect select) {
        if (onDialogItemClick != null) {
            onDialogItemClick.onDialogItemCilck(position);
        }
    }

    public interface OnDialogItemClick {
        /**
         * @param
         */
        void onDialogItemCilck(int position);
    }

}
