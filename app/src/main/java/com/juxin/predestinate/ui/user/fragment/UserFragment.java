package com.juxin.predestinate.ui.user.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseFragment;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

/**
 * 我的
 * Created by @author Kind on 2017/3/20.
 */
public class UserFragment extends BaseFragment implements PObserver, RequestComplete {

    private TextView title_nickname;
    private UserFragmentHeadPanel headPanel;
    private UserFragmentFootPanel footPanel;

    LinearLayout container;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.user_fragment);
        addTitle();
        initTitle();
        initView();
        return getContentView();
    }

    /**
     * UI优化 为减少（首页-我的）UI层次，个人中心顶部View（base_title） 也通过inflate后加载到同一层容器（container） 周敏
     */
    private void addTitle() {
        container = (LinearLayout) findViewById(R.id.container);
        View titleView = LayoutInflater.from(getContext()).inflate(R.layout.base_title, null);
        container.addView(titleView);
    }

    private void initView() {
        headPanel = new UserFragmentHeadPanel(getActivity());
        footPanel = new UserFragmentFootPanel(getActivity());
        container.addView(headPanel.getContentView());
        container.addView(footPanel.getContentView());

        View empty_footer = LayoutInflater.from(getContext()).inflate(R.layout.p1_user_fragment_empty_footer, null);
        container.addView(empty_footer);

        MsgMgr.getInstance().attach(this);
    }

    private void initTitle() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.f2_two_vertical_tv, null);
        title_nickname = (TextView) view.findViewById(R.id.tv_top);
        title_nickname.setText(ModuleMgr.getCenterMgr().getMyInfo().getNickname());
        ((TextView) view.findViewById(R.id.tv_bottom)).setText(String.format("ID: %s", ModuleMgr.getCenterMgr().getMyInfo().getUid()));
        setTitleCenterContainer(view);
        setTitleBackground(R.color.color_6F7485);
        setTitleRightImg(R.drawable.f1_user_set_ico, 3, listener);
        setIsBottomLineShow(false);
    }

    private void refreshView() {
        if (title_nickname != null) {
            title_nickname.setText(ModuleMgr.getCenterMgr().getMyInfo().getNickname());
        }
        if (headPanel != null && footPanel != null) {
            headPanel.refreshView();
            footPanel.refreshView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            ModuleMgr.getCommonMgr().reqMyEarningsInfo(this);
        }
        MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null); //更新个人资料
        if (footPanel != null) {
            footPanel.refreshView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MsgMgr.getInstance().detach(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        headPanel.onActivityResult(requestCode, resultCode);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ModuleMgr.getCenterMgr().reqMyInfo();
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        switch (key) {
            case MsgType.MT_MyInfo_Change:
                refreshView();
                break;
            default:
                break;
        }
    }

    private NoDoubleClickListener listener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                // 标题右侧按钮
                case R.id.base_title_right_img_container:
                    UIShow.showUserSetAct((Activity) getContext(), 100);
                    Statistics.userBehavior(SendPoint.menu_me_setting);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onRequestComplete(HttpResponse response) {
        if (headPanel != null) {
            headPanel.refreshIncome();
        }

    }
}
