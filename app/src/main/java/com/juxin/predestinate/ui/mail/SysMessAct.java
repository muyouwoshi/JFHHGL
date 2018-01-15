package com.juxin.predestinate.ui.mail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.chat.inter.ChatMsgInterface;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.SysNoticeMessage;
import com.juxin.predestinate.module.local.chat.utils.SortList;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 我的公告页面
 * Created by zm on 2017/6/14
 */
public class SysMessAct extends BaseActivity implements ExListView.IXListViewListener, ChatMsgInterface.ChatMsgListener {

    //控件
    private CustomStatusListView cslList;
    private ExListView listDatas;
    //数据
    private SysMessAdapter mSysMessAdapter;
    //变量
    private int page = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_sys_mess_act);
        initView();
    }

    private void initView() {
        setBackView(R.id.base_title_back);
        setTitle(getString(R.string.sys_mess_act));
        cslList = (CustomStatusListView) findViewById(R.id.sys_mess_list);
        listDatas = cslList.getExListView();
        mSysMessAdapter = new SysMessAdapter(this, null);
        listDatas.setAdapter(mSysMessAdapter);
        listDatas.addFooterView(LayoutInflater.from(this).inflate(R.layout.common_footer_distance, null));
        listDatas.setXListViewListener(this);
        initData();

        ModuleMgr.getChatMgr().attachChatListener(String.valueOf(MailSpecialID.customerService.getSpecialID()), this);
    }

    private void initData() {
        page = 0;
        showData(0);
        cslList.showLoading();
    }

    /**
     * 移动到聊天列表的最后一个。
     */
    public void moveToBottom() {
        if (mSysMessAdapter.getCount() > 0) listDatas.setSelection(mSysMessAdapter.getCount() - 1);
    }

    @Override
    public void onRefresh() {
        // 这里是加载更多信息的。
        showData(++page);
    }

    private void showData(int tmpPage) {
        ModuleMgr.getChatMgr().getSystemNotice(tmpPage)
                .observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BaseMessage>>() {
                    private Disposable sysDataDisposable;//系统消息查询回调

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        sysDataDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull final List<BaseMessage> baseMessages) {
                        sysDataDisposable.dispose();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listDatas.stopRefresh();
                                if (baseMessages.size() <= 0) {
                                    cslList.showNoData();
                                    return;
                                }

                                cslList.showExListView();
                                SortList.sortListView(baseMessages);//排序
                                if (baseMessages.size() < 20) {
                                    listDatas.setPullRefreshEnable(false);
                                }

                                List<SysNoticeMessage> listTemp = new ArrayList<>();
                                for (BaseMessage baseMessage : baseMessages) {
                                    if (baseMessage != null && baseMessage instanceof SysNoticeMessage) {
                                        listTemp.add((SysNoticeMessage) baseMessage);
                                    }
                                }
                                if (listTemp.size() <= 0) {
                                    cslList.showNoData();
                                    return;
                                }

                                if (mSysMessAdapter.getList() != null) {
                                    mSysMessAdapter.addList(listTemp);
                                } else {
                                    mSysMessAdapter.setList(listTemp);
                                }
                                listDatas.setSelection(baseMessages.size());
                                listDatas.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        listDatas.setSelection(baseMessages.size());//延迟定位
                                    }
                                }, 10);
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        listDatas.stopRefresh();
                    }

                    @Override
                    public void onComplete() {
                        listDatas.stopRefresh();
                    }
                });
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public void onChatUpdate(BaseMessage message) {
        if (message == null || !(message instanceof SysNoticeMessage)) return;

        SysNoticeMessage noticeMessage = (SysNoticeMessage) message;

        if (mSysMessAdapter.getList() != null) {
            mSysMessAdapter.getList().add(noticeMessage);
        } else {
            List<SysNoticeMessage> noticeMessages = new ArrayList<>();
            noticeMessages.add(noticeMessage);
            mSysMessAdapter.setList(noticeMessages);
        }

        if (!noticeMessage.isSender()) {
            ModuleMgr.getChatMgr().updateLocalReadStatus(noticeMessage.getChannelID(), noticeMessage.getWhisperID(), message.getMsgID());
        }
        moveToBottom();
    }

    @Override
    protected void onDestroy() {
        ModuleMgr.getChatMgr().detachChatListener(String.valueOf(MailSpecialID.customerService.getSpecialID()), this);
        super.onDestroy();
    }
}