package com.juxin.predestinate.third.recyclerholder;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.controls.xRecyclerView.XRecyclerView;
import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.third.pagerecyeler.PageRecyclerView;

/**
 * 页面四种状态： {@link #showLoading()}加载中，{@link #showNetError()}网络错误、
 * {@link #showNoData()}无数据、{@link #showXrecyclerView()}可刷新RecycleView、
 * {@link #showRecyclerView()}普通RecycleView
 * <p>
 * Created by Kind on 2017/3/21.
 */

public class CustomRecyclerView extends LinearLayout {

    private Context context;
    private CustomFrameLayout customFrameLayout;

    public CustomRecyclerView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_status_recyclerview, this);
        customFrameLayout = (CustomFrameLayout) view.findViewById(R.id.customFrameLayout);
        customFrameLayout.setList(new int[]{R.id.common_recyclerView, R.id.common_xrecyclerView, R.id.common_pagerecylerview,
                R.id.common_nodata, R.id.common_loading, R.id.common_net_error});
    }

    /**
     * @return 获得下拉加载的RecyclerView
     */
    public XRecyclerView getXRecyclerView() {
        return (XRecyclerView) customFrameLayout.findViewById(R.id.common_xrecyclerView);
    }

    /**
     * @return 获得普通RecyclerView
     */
    public RecyclerView getRecyclerView() {
        return (RecyclerView) customFrameLayout.findViewById(R.id.common_recyclerView);
    }

    /**
     * 获得翻页的recycleView (配合PageIndicatorView使用)
     *
     * @return
     */
    public PageRecyclerView getPageRecyclerView() {
        return (PageRecyclerView) customFrameLayout.findViewById(R.id.common_pagerecylerview);
    }

    /**
     * 显示loading
     */
    public void showLoading() {
        customFrameLayout.show(R.id.common_loading);
    }

    /**
     * 显示网络错误
     */
    public void showNetError() {
        customFrameLayout.show(R.id.common_net_error);
    }

    public void showNetError(String btn_text, OnClickListener listener) {
        customFrameLayout.show(R.id.common_net_error);
        TextView btn = (TextView) findViewById(R.id.error_btn);
        btn.setVisibility(VISIBLE);
        btn.setText(btn_text);
        btn.setOnClickListener(listener);
    }

    /**
     * 显示无数据状态
     */
    public void showNoData() {
        customFrameLayout.show(R.id.common_nodata);
    }

    public void showNoData(String str) {
        customFrameLayout.show(R.id.common_nodata);
        TextView nodata_txt = (TextView) findViewById(R.id.nodata_txt);
        nodata_txt.setText(str);
    }

    public void showNoData(String content, String btn_txt, OnClickListener listener) {
        customFrameLayout.show(R.id.common_nodata);
        TextView nodata_txt = (TextView) findViewById(R.id.nodata_txt);
        nodata_txt.setText(content);
        TextView btn = (TextView) findViewById(R.id.nodata_btn);
        btn.setText(btn_txt);
        btn.setVisibility(VISIBLE);
        btn.setOnClickListener(listener);
    }

    /**
     * 显示XrecyclerView
     */
    public void showXrecyclerView() {
        customFrameLayout.show(R.id.common_xrecyclerView);
    }

    /**
     * RecyclerView
     */
    public void showRecyclerView() {
        customFrameLayout.show(R.id.common_recyclerView);
    }

    /**
     * 显示XrecyclerView
     */
    public void showPageRecyclerView() {
        customFrameLayout.show(R.id.common_pagerecylerview);
    }

    /**
     * 隐藏所有
     */
    public void GoneAll() {
        customFrameLayout.GoneAll();
    }
}
