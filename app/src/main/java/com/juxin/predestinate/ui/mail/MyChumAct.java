package com.juxin.predestinate.ui.mail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.juxin.library.controls.xRecyclerView.ProgressStyle;
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
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.third.recyclerholder.CustomRecyclerView;
import com.juxin.predestinate.ui.user.my.view.DividerItemDecoration;

import java.util.List;

/**
 * 密友
 * Created by Kind on 2017/7/14.
 */

public class MyChumAct extends BaseActivity implements ExListView.IXListViewListener, PObserver, XRecyclerView.LoadingListener {

//    private CustomRecyclerView customRecyclerView;
//    private XRecyclerView xRecyclerView;
    private CustomStatusListView customStatusListView;
    private ExListView exListView;
    private MyChumAdapter myChumAdapter;

    private int page = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f2_mynobility_act);
        setBackView(R.id.base_title_back, "我的密友");
        setTitleRightImg(R.drawable.f2_icon_question, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statistics.userBehavior(SendPoint.menu_xiaoxi_miyou_explain);
                UIShow.showIntimateFriendExplain(MyChumAct.this);
            }
        });
        initView();
        MsgMgr.getInstance().attach(this);
    }

    private void initView() {
//        customRecyclerView = (CustomRecyclerView) findViewById(R.id.mynobility_listview);
//        xRecyclerView = customRecyclerView.getXRecyclerView();
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        xRecyclerView.setLayoutManager(layoutManager);
//
//        xRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
//        xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.SysProgress);
//        xRecyclerView.setArrowImageView(R.drawable.loading_01);//pull_heart_big
//        xRecyclerView.setArrowTextView(ModuleMgr.getCenterMgr().getMyInfo().isMan() ?
//                getResources().getString(R.string.xlistview_header_hint_loading_female) :
//                getResources().getString(R.string.xlistview_header_hint_loading_male));
//
//        xRecyclerView.setLoadingListener(this);
//        myChumAdapter = new MyChumAdapter(this, null);
//        xRecyclerView.setAdapter(myChumAdapter);
//        onData();




        customStatusListView = (CustomStatusListView) findViewById(R.id.mynobility_listview);
        exListView = customStatusListView.getExListView();
        exListView.setXListViewListener(this);
        exListView.setPullRefreshEnable(true);
        exListView.setPullLoadEnable(true);

        myChumAdapter = new MyChumAdapter(this, null);
        exListView.setAdapter(myChumAdapter);
        customStatusListView.showLoading();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        page = 1;
        onData();
    }

    @Override
    public void onLoadMore() {
        onData();
    }

//    private void onData() {
//        xRecyclerView.refreshComplete();
//        xRecyclerView.loadMoreComplete();
//        ModuleMgr.getCommonMgr().reqMyIntimateFriends(page, new RequestComplete() {
//            @Override
//            public void onRequestComplete(HttpResponse response) {
//                if (response.isOk()) {
//                    MyChumList myChumList = (MyChumList) response.getBaseData();
//                    List<MyChum> chumList = myChumList.getMyChumList();
//                    if (chumList != null && chumList.size() > 0) {
//                        if (page == 1) {
//                            myChumAdapter.setList(chumList);
//                        } else {
//                            myChumAdapter.addList(chumList);
//                        }
//
//                        page++;
//                        if (chumList.size() < 20) {
//                            xRecyclerView.setNoMore(false);
//                        }
//                        customRecyclerView.showXrecyclerView();
//                    } else {
//                        if (page == 1) {
//                            customRecyclerView.showNoData("暂无密友数据", "重试", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    customRecyclerView.showLoading();
//                                    onRefresh();
//                                }
//                            });
//                        } else {
//                            xRecyclerView.setNoMore(false);
//                        }
//                    }
//                } else {
//                    if (myChumAdapter.getList() != null && myChumAdapter.getList().size() > 0) {
//                        PToast.showShort("请求出错！");
//                    } else {
//                        customRecyclerView.showNoData("请求出错！", "重试", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                customRecyclerView.showLoading();
//                                onRefresh();
//                            }
//                        });
//                    }
//                }
//            }
//        });
//    }

    private void onData() {
        exListView.stopRefresh();
        exListView.stopLoadMore();
        ModuleMgr.getCommonMgr().reqMyIntimateFriends(page, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
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
                        }
                        customStatusListView.showExListView();
                    } else {
                        if (page == 1) {
                            customStatusListView.showNoData("暂无密友数据", "重试", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    customStatusListView.showLoading();
                                    onRefresh();
                                }
                            });
                        } else {
                            exListView.setPullLoadEnable(false);
                        }
                    }
                } else {
                    if (myChumAdapter.getList() != null && myChumAdapter.getList().size() > 0) {
                        PToast.showShort("请求出错！");
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
    public void onMessage(String key, Object value) {
        if (key == MsgType.MT_CHUM_TASK_FINISH) {
            ChumTaskMessage chumTaskMessage = (ChumTaskMessage) ((Msg) value).getData();
            if(chumTaskMessage == null || myChumAdapter == null) return;

            MyChum myChum = myChumAdapter.queryMyChum(chumTaskMessage.getLWhisperID());
            if(myChum == null ) return;
            MyChumTaskList.MyChumTask myChumTask = myChum.queryMyChumTask(chumTaskMessage.getTask_id());

            myChumTask.setTaskDone(1);//是否已完成 0未完成，1已完成，2已推送

            if(myChum.isUndoneTask()){
                myChum.setTodayTaskStatus(1);//是否完成今日任务 1 完成 0 未完成
            }
            myChumAdapter.notifyDataSetChanged();
        }
    }
}