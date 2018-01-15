package com.juxin.predestinate.ui.friend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.controls.xRecyclerView.XRecyclerView;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.Msg;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.chat.msgtype.ChumTaskMessage;
import com.juxin.predestinate.module.local.mail.MyChum;
import com.juxin.predestinate.module.local.mail.MyChumList;
import com.juxin.predestinate.module.local.mail.MyChumTaskList;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseFragment;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.request.HTCallBack;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.mail.MyChumAdapter;
import com.juxin.predestinate.ui.utils.CheckIntervalTimeUtil;

import java.util.List;

/**
 * 好友fragment
 * Created by zm on 2017/9/4.
 */
public class FriendFragment extends BaseFragment implements ExListView.IXListViewListener, PObserver, XRecyclerView.LoadingListener {

    private CustomStatusListView customStatusListView;
    private ExListView exListView;
    private MyChumAdapter myChumAdapter;
    private LinearLayout llExplain, llGo;
    private TextView tvTaskOne, tvTaskTwo;

    private int page = 1;
    private HTCallBack htCallBack;
    private CheckIntervalTimeUtil checkIntervalTimeUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f2_mynobility_act);
        setTitle(getResources().getString(R.string.main_btn_friends));
        setTitleLeftImg(R.drawable.f2_icon_shuoming, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statistics.userBehavior(SendPoint.menu_haoyou_question);
                UIShow.showIntimateFriendExplain(getContext());
            }
        });
        setTitleRight(getResources().getString(R.string.my_friend_righ_title), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIShow.showMyDefriends(getContext());
            }
        });
        MsgMgr.getInstance().attach(this);
        initView();
        return getContentView();
    }

    private void initView() {
        customStatusListView = (CustomStatusListView) findViewById(R.id.mynobility_listview);
        exListView = customStatusListView.getExListView();
        llExplain = (LinearLayout) findViewById(R.id.mynobility_ll_explain);
        tvTaskOne = (TextView) findViewById(R.id.mynobility_tv_task_one);
        tvTaskTwo = (TextView) findViewById(R.id.mynobility_tv_task_two);
        llGo = (LinearLayout) findViewById(R.id.mynobility_ll_go);
        exListView.setXListViewListener(this);
        exListView.setPullRefreshEnable(true);
        exListView.setPullLoadEnable(true);

        View mViewTop = LayoutInflater.from(getContext()).inflate(R.layout.layout_margintop_banner, null);
        myChumAdapter = new MyChumAdapter(getContext(), null);
        myChumAdapter.attach();
        exListView.addHeaderView(mViewTop);
        exListView.setAdapter(myChumAdapter);
        customStatusListView.showLoading();
        llGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statistics.userBehavior(SendPoint.menu_haoyou_rule);
                UIShow.showIntimateFriendExplain(getContext());
            }
        });
        checkIntervalTimeUtil = new CheckIntervalTimeUtil();
    }

    @Override
    public void onResume() {
        super.onResume();
        reqData();
    }

    private void reqData() {
        if (checkIntervalTimeUtil == null) {
            checkIntervalTimeUtil = new CheckIntervalTimeUtil();
        }
        if (checkIntervalTimeUtil.check(Constant.CHAT_FRIEND_LIST_REFRESH)) {
            onRefresh();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            reqData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MsgMgr.getInstance().detach(this);
        if (myChumAdapter != null) {
            myChumAdapter.detach();
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        if (key == MsgType.MT_CHUM_TASK_FINISH) {
            ChumTaskMessage chumTaskMessage = (ChumTaskMessage) ((Msg) value).getData();
            if (chumTaskMessage == null || myChumAdapter == null) {
                return;
            }

            MyChum myChum = myChumAdapter.queryMyChum(chumTaskMessage.getLWhisperID());
            if (myChum == null) {
                return;
            }
            MyChumTaskList.MyChumTask myChumTask = myChum.queryMyChumTask(chumTaskMessage.getTask_id());

            myChumTask.setTaskDone(1);//是否已完成 0未完成，1已完成，2已推送

            if (myChum.isUndoneTask()) {
                myChum.setTodayTaskStatus(1);//是否完成今日任务 1 完成 0 未完成
            }
            myChumAdapter.notifyDataSetChanged();
        } else if (key == MsgType.MT_BLACK_STATUS) {
            Msg msg = (Msg) value;
            if (msg.getData() instanceof Boolean) {
                for (int i = 0; i < myChumAdapter.getList().size(); i++) {
                    if (myChumAdapter.getList().get(i).getUid() == Long.valueOf(msg.getKey()) && ((Boolean) msg.getData())) {
                        myChumAdapter.getList().remove(i);
                        myChumAdapter.notifyDataSetChanged();
                        break;
                    } else if (myChumAdapter.getList().get(i).getUid() == Long.valueOf(msg.getKey()) && !((Boolean) msg.getData()) || page == 2) {
                        onRefresh();
                    }
                }
            }
        }
    }

    private void onData() {
        exListView.stopRefresh();
        exListView.stopLoadMore();
        htCallBack = ModuleMgr.getCommonMgr().reqMyIntimateFriends(page, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isCache()) {
                    if (page != 1) {
                        return;
                    }
                    customStatusListView.setVisibility(View.VISIBLE);
                    llExplain.setVisibility(View.GONE);
                    MyChumList myChumList = (MyChumList) response.getBaseData();
                    List<MyChum> chumList = myChumList.getMyChumList();
                    if (chumList != null && chumList.size() > 0) {
                        myChumAdapter.setList(chumList);
                        if (chumList.size() < 20) {
                            exListView.setPullLoadEnable(false);
                        }
                        customStatusListView.showExListView();
                    } else {
                        customStatusListView.setVisibility(View.GONE);
                        llExplain.setVisibility(View.VISIBLE);
                        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                            tvTaskOne.setText(R.string.send_girl_gift);
                            tvTaskTwo.setText(R.string.with_girl_voice);
                        } else {
                            tvTaskOne.setText(R.string.receive_he_gift);
                            tvTaskTwo.setText(R.string.with_he_voice);
                        }
                    }
                    return;
                }

                customStatusListView.setVisibility(View.VISIBLE);
                llExplain.setVisibility(View.GONE);
                if (response.isOk()) {
                    MyChumList myChumList = (MyChumList) response.getBaseData();
                    List<MyChum> chumList = myChumList.getMyChumList();
                    if (chumList != null && chumList.size() > 0) {
                        if (page == 1) {
                            myChumAdapter.setList(chumList);
                        } else {
                            myChumAdapter.addList(chumList);
                        }

                        page++;
                        if (chumList.size() < 20) {
                            exListView.setPullLoadEnable(false);
                        } else {
                            exListView.setPullLoadEnable(true);
                        }
                        customStatusListView.showExListView();
                    } else {
                        if (page == 1) {
                            customStatusListView.setVisibility(View.GONE);
                            llExplain.setVisibility(View.VISIBLE);
                            if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                                tvTaskOne.setText(R.string.send_girl_gift);
                                tvTaskTwo.setText(R.string.with_girl_voice);
                            } else {
                                tvTaskOne.setText(R.string.receive_he_gift);
                                tvTaskTwo.setText(R.string.with_he_voice);
                            }
                        } else {
                            exListView.setPullLoadEnable(false);
                        }
                    }
                } else {
                    if (myChumAdapter.getList() != null && myChumAdapter.getList().size() > 0) {
                        if (TextUtils.isEmpty(response.getResponseString())){
                            PToast.showShort("请求出错！");
                        }
                    } else {
                        customStatusListView.showNoData("请求出错！", "重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                customStatusListView.showLoading();
                                onRefresh();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        if (htCallBack != null) {
            htCallBack.cancel();
        }
        page = 1;
        onData();
    }

    @Override
    public void onLoadMore() {
        onData();
    }
}
