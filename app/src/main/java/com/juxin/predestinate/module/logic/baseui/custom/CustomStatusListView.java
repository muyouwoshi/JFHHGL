package com.juxin.predestinate.module.logic.baseui.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;

/**
 * 可获取到普通的或者可下拉刷新上拉加载的两种ListView（同时只能获取其中一个）<br>
 * ListView状态有如下四种：showLoading()加载中、showNoData()无数据、showNetError()网络错误、
 * showPullListView()/showListView()有数据<br>
 * <p/>
 * 使用：可当作一个正常的listView进行使用
 * <pre>
 *     {@code <CustomStatusListView
 *      android:id="@+id/status_listview"
 *      android:layout_width="match_parent"
 *      android:layout_height="match_parent" />}
 * </pre>
 * 代码中：
 * <pre>
 *     {@code statusListView = (CustomStatusListView) findViewById(R.id.status_listview);
 *      ExListView history_list = statusListView.getExListView();
 *      history_list.setXListViewListener(this);
 *      statusListView.showLoading();//要写在获取listView之后，否则不会显示}
 * </pre>
 * 使用参见：MyConcern_MeAct.class及其布局
 *
 * @author ZRP
 */
public class CustomStatusListView extends LinearLayout {

    private CustomFrameLayout customFrameLayout;

    public CustomStatusListView(Context context) {
        this(context, null);
    }

    public CustomStatusListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomStatusListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.custom_status_listview, this);
        customFrameLayout = (CustomFrameLayout) findViewById(R.id.customFrameLayout);
        customFrameLayout.setList(new int[]{R.id.common_ex_listview,
                R.id.common_listviewLayout, R.id.common_nodata, R.id.common_loading, R.id.common_net_error});
    }

    /**
     * @return 获得下拉加载的listView
     */
    public ExListView getExListView() {
        return (ExListView) customFrameLayout.findViewById(R.id.common_ex_listview);
    }

    /**
     * @return 获得普通的listView
     */
    public ListView getListView() {
        return (ListView) customFrameLayout.findViewById(R.id.common_listview);
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

    /**
     * 显示网络错误
     *
     * @param btn_text 刷新按钮显示的文字
     * @param listener 刷新按钮的点击监听
     */
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

    /**
     * 显示无数据状态
     *
     * @param str 无数据提示文字
     */
    public void showNoData(String str) {
        showNoData(str, null, null);
    }

    /**
     * 显示无数据状态
     *
     * @param content  无数据提示文字
     * @param btn_txt  刷新按钮显示的文字
     * @param listener 刷新按钮的点击监听
     */
    public void showNoData(String content, String btn_txt, OnClickListener listener) {
        showNoData(-1, content, btn_txt, listener);
    }

    /**
     * 显示无数据状态
     *
     * @param imageRes 无数据页面图片资源
     * @param content  无数据提示文字
     * @param btn_txt  刷新按钮显示的文字
     * @param listener 刷新按钮的点击监听
     */
    public void showNoData(@DrawableRes int imageRes, String content, String btn_txt, OnClickListener listener) {
        ImageView nodata_img = (ImageView) findViewById(R.id.nodata_img);
        TextView nodata_txt = (TextView) findViewById(R.id.nodata_txt);
        TextView btn = (TextView) findViewById(R.id.nodata_btn);

        if (imageRes != -1) nodata_img.setImageResource(imageRes);
        if (content != null) nodata_txt.setText(content);
        if (btn_txt != null) {
            btn.setText(btn_txt);
            btn.setVisibility(VISIBLE);
            if (listener != null) btn.setOnClickListener(listener);
        }
        customFrameLayout.show(R.id.common_nodata);
    }

    /**
     * 显示ExListView
     */
    public void showExListView() {
        customFrameLayout.show(R.id.common_ex_listview);
    }

    /**
     * 显示list
     */
    public void showListView() {
        customFrameLayout.show(R.id.common_listviewLayout);
    }

    /**
     * 隐藏所有
     */
    public void GoneAll() {
        customFrameLayout.GoneAll();
    }
}
