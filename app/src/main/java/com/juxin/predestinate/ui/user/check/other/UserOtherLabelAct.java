package com.juxin.predestinate.ui.user.check.other;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.flow.TagAdapter;
import com.juxin.predestinate.module.logic.baseui.flow.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 他人标签
 * Created by Su on 2017/4/13.
 */

public class UserOtherLabelAct extends BaseActivity implements View.OnClickListener {
    private List<String> labels = new ArrayList<>(); // 标签
    private TagFlowLayout mFlowLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_user_other_label_act);

        initTitle();
        initView();
    }

    private void initTitle() {
        setBackView();
        setTitle(getString(R.string.user_other_label));
        setTitleRight(getString(R.string.save), this);
    }

    private void initView() {
        mFlowLayout = (TagFlowLayout) findViewById(R.id.flowlayout);

        initLabels();
        initTagFlowLayout();
    }

    /**
     * 印象标签
     */
    private void initLabels() {
        mFlowLayout.removeAllViews();
        if (labels != null) labels.clear();
        //labels.addAll(ModuleMgr.getCenterMgr().getMyInfo().getImpressions());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.base_title_right_txt:  // 保存

                labels.add("ksjinj");
                mAdapter.notifyDataChanged();

                break;
        }
    }


    private TagAdapter<String> mAdapter;

    private void initTagFlowLayout() {
        mFlowLayout.setAdapter(mAdapter = new TagAdapter<String>(labels) {
            @Override
            public View getView(com.juxin.predestinate.module.logic.baseui.flow.FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(UserOtherLabelAct.this).inflate(R.layout.p1_flow_tv,
                        mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });

        mFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, com.juxin.predestinate.module.logic.baseui.flow.FlowLayout parent) {
                Log.d("TagFlowLayout===11=", labels.get(position));
                return true;
            }
        });

        mFlowLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                Log.d("TagFlowLayout===22=", selectPosSet.toString());
            }
        });
    }
}
