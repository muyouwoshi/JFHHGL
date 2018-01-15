package com.juxin.predestinate.ui.discover.search;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.InputUtils;
import com.juxin.library.utils.ViewUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.StackNode;
import com.juxin.predestinate.module.local.statistics.StatisticsManager;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;

/**
 * @author Mr.Huang
 * @date 2017-07-26
 * 搜索用户的头部View
 */
public class SearchLabelView extends FrameLayout {

    public static final String ACTION_UPDATE_SEARCH_NUM = "com.juxin.yfb.SearchLabelView.ACTION_UPDATE_SEARCH_NUM";

    /**
     * 每天能搜索的次数
     */
    private static final int CAN_SEARCH_SIZE = 5;

    private EditText etSearch;
    private TextView tvSearch, tvSize;
    private SearchLabelListener searchLabelListener;
    private long uid;

    public SearchLabelView(@NonNull Context context) {
        super(context);
        initView(context, null);
    }

    public SearchLabelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SearchLabelView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SearchLabelView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setSize(CAN_SEARCH_SIZE - getSearchSize());
        }
    };

    private void initView(Context context, AttributeSet attrs) {
        uid = ModuleMgr.getCenterMgr().getMyInfo().getUid();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter(ACTION_UPDATE_SEARCH_NUM);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        etSearch = ViewUtils.findById(this, R.id.et_search);
        tvSize = ViewUtils.findById(this, R.id.tv_size);
        tvSearch = ViewUtils.findById(this, R.id.tv_search);
        tvSize.setText(Html.fromHtml(getHtmlInfo(CAN_SEARCH_SIZE - getSearchSize())));
        etSearch.setVisibility(GONE);
        tvSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击去往搜索页面
                //如果还有其他地方进入搜索也 将这个操作放到UIShow.java中公用
                //暂时这么写

                Activity activity = App.activity;
                if (activity == null) {
                    return;
                }
//                if (!ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
//                    //不是vip弹提示
//                    UIShow.showGoodsVipBottomDlg(activity, GoodsConstant.DLG_VIP_SEARCH_B);
//                    return;
//                }
                if (!checkCanSearch()) {//如果不能搜索
                    PToast.showLong("搜索每天只能使用5次哦！");
                    return;
                }
                Intent intent = new Intent(activity, SearchUserActivity.class);
                StackNode.appendIntent(activity, intent);
                activity.startActivity(intent);
            }
        });
    }

    /**
     * 设置是否可以搜索
     * 用于切换textview 和 edittext的状态
     * textview只能点击
     * editext 是可以输入
     * 以为首页搜索和搜索页搜索相同，但交互方式不同
     *
     * @param canSearch true可以
     */
    public void setCanSearch(boolean canSearch) {
        if (canSearch) {
            if (tvSearch != null) {
                tvSearch.setVisibility(GONE);
            }
            if (etSearch != null) {
                etSearch.setVisibility(VISIBLE);
                etSearch.post(new Runnable() {
                    @Override
                    public void run() {
                        InputUtils.forceOpen(getContext());
                    }
                });
            }
            etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        InputUtils.HideKeyboard(etSearch);
                        StatisticsManager.menu_faxian_sousuo(etSearch.getText().toString(), getSearchSize());
                        if (!checkCanSearch()) {//如果不能搜索
                            PToast.showLong("搜索每天只能使用5次哦！");
                            return false;
                        }
                        if (searchLabelListener != null) {
                            searchLabelListener.onSearch(etSearch.getText().toString());
                        }
                        return true;
                    }
                    return false;
                }
            });
        } else {
            etSearch.setVisibility(GONE);
            tvSearch.setVisibility(VISIBLE);
        }

    }

    private String getHtmlInfo(int size) {
        return "<font color=\"#F1C98C\">今日剩余:" + size + "</font>";
    }

    /**
     * 设置剩余次数
     *
     * @param size
     */
    public void setSize(int size) {
        if (tvSize == null) {
            return;
        }
        tvSize.setText(Html.fromHtml(getHtmlInfo(size)));
    }

    /**
     * 设置软键盘搜索监听
     *
     * @param searchLabelListener
     */
    public void setSearchLabelListener(SearchLabelListener searchLabelListener) {
        this.searchLabelListener = searchLabelListener;
    }

    /**
     * 软键盘搜索监听类
     */
    public interface SearchLabelListener {
        /**
         * 点击搜索回调方法
         *
         * @param text 输入框的内容
         */
        void onSearch(String text);
    }

    /**
     * 检查是否可以搜索 每天有5次搜索
     *
     * @return
     */
    public boolean checkCanSearch() {
        return getSearchSize() < CAN_SEARCH_SIZE;
    }

    /**
     * 获取搜索次数
     *
     * @return
     */
    public int getSearchSize() {
        String date = TimeUtil.getCurrentData() + uid;
        return PSP.getInstance().getInt(date, 0);
    }

    /**
     * 记录搜索次数
     */
    public void makeSearchNum() {
        String date = TimeUtil.getCurrentData() + uid;
        int num = PSP.getInstance().getInt(date, 0) + 1;
        PSP.getInstance().put(date, num);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(ACTION_UPDATE_SEARCH_NUM));
    }
}