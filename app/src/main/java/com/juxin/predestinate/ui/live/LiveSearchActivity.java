package com.juxin.predestinate.ui.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.ui.live.adapter.LiveSearchAdapter;
import com.juxin.predestinate.ui.live.bean.LiveSearchBean;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 直播模块用户搜索页面
 *
 * @author terry
 * @date 2017/10/27
 */
public class LiveSearchActivity extends BaseActivity {

    private TextView mCancelTv;
    private CustomStatusListView mCustomStatusListView;
    private ListView mListView;
    private EditText mSearchEdit;
    private LiveSearchAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.live_v2_search);

        initView();
        initStatus();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mCancelTv = findViewById(R.id.live_v2_search_cancel_tv);
        mSearchEdit = findViewById(R.id.common_search_edittext);
        mCustomStatusListView = findViewById(R.id.live_v2_search_listview);
        mListView = mCustomStatusListView.getListView();
        mAdapter = new LiveSearchAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    /**
     * 初始化状态
     */
    private void initStatus() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        mSearchEdit.setOnEditorActionListener(onEditorActionListener);

        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveSearchActivity.this.finish();
            }
        });
    }

    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (event == null) {
                return false;
            }
            if (actionId == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                searchData();
            }
            return false;
        }
    };

    private void searchData() {
        if (TextUtils.isEmpty(mSearchEdit.getText().toString().trim())) {
            PToast.showShort("请输入昵称或者ID");
            return;
        }
        mCustomStatusListView.showLoading();
        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("key", mSearchEdit.getText().toString().trim());

        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.SearchAnchor, postParams, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    JSONObject resJo = response.getResponseJsonRes();
                    if (resJo.optJSONArray("list") != null) {
                        List<LiveSearchBean> resList = JSON.parseArray(resJo.optJSONArray("list").toString(), LiveSearchBean.class);

                        if (resList == null || resList.isEmpty()) {
                            showEmpty();
                            return;
                        }
                        mAdapter.setDataList(resList);
                        mCustomStatusListView.showListView();
                    } else {
                        setDataError();
                    }
                    return;
                }
                setDataError();
                PToast.showShort(response.getMsg());
            }
        });
    }

    private void setDataError() {
        mCustomStatusListView.showNetError(this.getString(R.string.tip_click_refresh), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchData();
            }
        });
    }

    private void showEmpty() {
        mCustomStatusListView.showNoData("没有搜索到相关内容");
    }
}
