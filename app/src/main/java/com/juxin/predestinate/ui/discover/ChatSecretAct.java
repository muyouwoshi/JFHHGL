package com.juxin.predestinate.ui.discover;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.db.DBCallback;
import com.juxin.predestinate.bean.db.utils.RxUtil;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;
import com.juxin.predestinate.module.logic.baseui.xlistview.XListView;
import com.juxin.predestinate.module.logic.swipemenu.SwipeListView;
import com.juxin.predestinate.module.logic.swipemenu.SwipeMenu;
import com.juxin.predestinate.module.logic.swipemenu.SwipeMenuCreator;
import com.juxin.predestinate.module.util.PickerDialogUtil;
import com.juxin.predestinate.module.util.TimerUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.mail.item.MailMsgID;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

import static com.juxin.predestinate.module.logic.application.App.getActivity;

/**
 * 私密消息
 * Created by Su on 2017/8/10.
 */
public class ChatSecretAct extends BaseActivity implements AdapterView.OnItemClickListener,
        XListView.IXListViewListener, SwipeListView.OnSwipeItemClickedListener,
        AbsListView.OnScrollListener, View.OnClickListener, PObserver {

    private CustomFrameLayout customFrameLayout;
    private SwipeListView exListView;

    private ChatPageAdapter adapter;

    private TextView mail_title_right_text;
    private View bottom_view;
    private Button del_btn, ignore_btn;
    private boolean isGone = false;//是否首面底部，默认是false
    private List<BaseMessage> delList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_chat_msg_list);
        initView();

        MsgMgr.getInstance().attach(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        setTitle(getString(R.string.chat_secret_title));
        setBackView();
        mail_title_right_text = setTitleRight(getString(R.string.edit), R.color.color_FE799A, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isGone) {
                    editContent();
                } else {
                    cancleEdit();
                }
            }
        });
        customFrameLayout = (CustomFrameLayout) findViewById(R.id.chat_list);
        customFrameLayout.setList(new int[]{R.id.chatinviteact_data, R.id.common_nodata});
        exListView = (SwipeListView) findViewById(R.id.chatinviteact_list);

        View mViewTop = LayoutInflater.from(this).inflate(R.layout.layout_margintop, null);
        View listview_footer = LayoutInflater.from(getActivity()).inflate(R.layout.common_footer_distance, null);
        exListView.setPullLoadEnable(false);
        exListView.setXListViewListener(this);
        exListView.addHeaderView(mViewTop);
        exListView.addFooterView(listview_footer);
        adapter = new ChatPageAdapter(this, null);
        exListView.setAdapter(adapter);
        exListView.setOnItemClickListener(this);

        exListView.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                menu.setTitle("删除");
                menu.setTitleSize(18);
                menu.setTitleColor(Color.WHITE);
                menu.setViewHeight(adapter.getItemHeight());
            }
        });

        bottom_view = findViewById(R.id.chatinviteact_bottom);
        del_btn = (Button) findViewById(R.id.chatinviteact_bottom_delete);
        ignore_btn = (Button) findViewById(R.id.chatinviteact_bottom_all_ignore);
        del_btn.setOnClickListener(this);
        ignore_btn.setOnClickListener(this);

        exListView.setSwipeItemClickedListener(this);
        exListView.setOnScrollListener(this);
    }

    private void initData() {
        adapter.setListNonotify(ModuleMgr.getChatListMgr().getPrivateUnreadList());
        PLogger.d("initData=" + ModuleMgr.getChatListMgr().getPrivateUnreadList().size());
        handlerNotify.removeMessages(1);
        handlerNotify.sendEmptyMessageDelayed(1, 500);
    }

    private final Handler handlerNotify = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    adapter.notifyDataSetChanged();
                    if (adapter.getList() != null && adapter.getList().size() > 0) {
                        showHasData();
                    } else {
                        showNoData();
                    }
                    break;
            }
        }
    };

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BaseMessage message = (BaseMessage) adapterView.getAdapter().getItem(i);
        if (message != null) {
            MailMsgID mailMsgID = MailMsgID.getMailMsgID(message.getLWhisperID());
            if (mailMsgID == null) {
                UIShow.showPrivateChatAct(this, message.getLWhisperID(), message.getName(), message.getKfID());
            }
        }
    }

    public void cancleEdit() {
        delList.clear();
        del_btn.setEnabled(false);
        exListView.smoothCloseChooseView();
    }

    public void editContent() {
        delList.clear();
        del_btn.setEnabled(false);
        if (adapter.getList().size() > 0) {
            exListView.smoothOpenChooseView();
        } else {
            onHidTitleLeft();
            PToast.showCenterShort("没有可编辑选项");
        }
    }

    /**
     * 显示有数据状态
     */
    public void showHasData() {
        exListView.stopRefresh();
        customFrameLayout.show(R.id.chatinviteact_data);
        showAllData();
    }

    private void onHidTitleLeft() {
        setTitleLeftContainerRemoveAll();
        setBackView();
    }

    private void onShowTitleLeft() {
        View title_left = LayoutInflater.from(getActivity()).inflate(R.layout.f1_mail_title_left, null);
        title_left.findViewById(R.id.mail_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAll();
            }
        });
        setBackViewGone();
        setTitleLeftContainer(title_left);
    }

    // 全选
    public void selectAll() {
        delList.clear();
        if (adapter == null) return;
        if (del_btn != null) {
            del_btn.setEnabled(true);
        }
        delList.addAll(adapter.getList());
        exListView.selectAllChooseView();
    }

    /**
     * 显示无数据状态
     */
    public void showNoData() {
        customFrameLayout.show(R.id.common_nodata);
    }

    @Override
    public void onSwipeChooseOpened() {
        bottom_view.setVisibility(View.VISIBLE);
        onShowTitleLeft();
        mail_title_right_text.setText("取消");
        isGone = true;
    }

    @Override
    public void onSwipeChooseClosed() {
        bottom_view.setVisibility(View.GONE);
        mail_title_right_text.setText("编辑");
        onHidTitleLeft();
        isGone = false;
    }

    @Override
    public void onSwipeChooseChecked(int position, boolean isChecked) {
        int size = adapter.getList().size();
        if (size <= 0 || position >= size) {
            return;
        }
        BaseMessage message = adapter.getItem(position);
        if (isChecked) {
            delList.add(message);
        } else {
            /**
             * 此处因为可能BaseMessage不是add时候的同一message所以，用WhisperID判断
             */
            long wid = message.getLWhisperID();
            int delListSize = delList.size();
            BaseMessage msg;
            for (int i = 0; i < delListSize; i++) {
                msg = delList.get(i);
                if (msg.getLWhisperID() == wid) {
                    delList.remove(msg);
                    break;
                }
            }
        }
    }

    @Override
    public void onSwipeMenuClick(int position, SwipeMenu swipeMenu, View contentView) {
        List<BaseMessage> baseMessageList = adapter.getList();
        final BaseMessage item = baseMessageList.get(position);
        if (item != null) {
            MailMsgID mailMsgID = MailMsgID.getMailMsgID(item.getLWhisperID());
            if (mailMsgID == null) {
                LoadingDialog.show(this, "删除中...");
                ModuleMgr.getChatListMgr().deleteMessage(item.getLWhisperID(), new DBCallback() {
                    @Override
                    public void OnDBExecuted(long result) {
                        LoadingDialog.closeLoadingDialog(1000);
                    }
                });
                TimerUtil.beginTime(new TimerUtil.CallBack() {
                    @Override
                    public void call() {
                        initData();
                    }
                }, 1000);
            }
        }
    }

    @Override
    public void onScrollStateChanged(final AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: {//停止滚动
                //设置为停止滚动
                showAllData();
                break;
            }
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING: {//滚动做出了抛的动作
                break;
            }
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: {//正在滚动
                break;
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    private void showAllData() {
        handlerStop.removeMessages(1);
        handlerStop.sendEmptyMessageDelayed(1, 500);
    }

    private final Handler handlerStop = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    detectInfo(exListView);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 检测个人资料
     *
     * @param view
     */
    private void detectInfo(AbsListView view) {
        if (adapter == null) return;

        final List<Long> stringList = new ArrayList<>();

        int firs = view.getFirstVisiblePosition();
        int last = view.getLastVisiblePosition();

        for (int i = firs; i < last; i++) {
            BaseMessage message = adapter.getItem(i);
            if (message == null || MailMsgID.getMailMsgID(message.getLWhisperID()) != null) {
                continue;
            }

            if (TextUtils.isEmpty(message.getName()) && TextUtils.isEmpty(message.getAvatar())) {
                stringList.add(message.getLWhisperID());
            }
        }

        if (stringList.size() > 0) {
            Observable<List<UserInfoLightweight>> observable = ModuleMgr.getChatMgr().getUserInfoList(stringList);
            observable.compose(RxUtil.<List<UserInfoLightweight>>applySchedulers(RxUtil.IO_ON_UI_TRANSFORMER))
                    .subscribe(new Observer<List<UserInfoLightweight>>() {
                        private Disposable queryDisposable;

                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            queryDisposable = d;
                        }

                        @Override
                        public void onNext(@NonNull List<UserInfoLightweight> userInfoLightweights) {
                            queryDisposable.dispose();
                            if (userInfoLightweights != null && userInfoLightweights.size() > 0) {
                                ModuleMgr.getChatMgr().updateUserInfoList(userInfoLightweights);
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

            TimerUtil.beginTime(new TimerUtil.CallBack() {
                @Override
                public void call() {
                    ModuleMgr.getChatMgr().getProFile(stringList);
                }
            }, 800);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatinviteact_bottom_delete:
                del_btn.setEnabled(false);
                LoadingDialog.show(this, "删除中...");
                ModuleMgr.getChatListMgr().deleteBatchMessage(delList, new DBCallback() {
                    @Override
                    public void OnDBExecuted(long result) {
                        LoadingDialog.closeLoadingDialog(1000);
                    }
                });
                delList.clear();
                onHidTitleLeft();
                exListView.smoothCloseChooseView();
                break;
            case R.id.chatinviteact_bottom_all_ignore:
                //忽略所有未读消息
                PickerDialogUtil.showSimpleAlertDialog(this, new SimpleTipDialog.ConfirmListener() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onSubmit() {
                        onHidTitleLeft();
                        LoadingDialog.show(ChatSecretAct.this, "忽略中...");
                        ModuleMgr.getChatListMgr().updateToBatchRead(ModuleMgr.getChatListMgr().getPrivateUnreadList(), new DBCallback() {
                            @Override
                            public void OnDBExecuted(long result) {
                                LoadingDialog.closeLoadingDialog(1000, new TimerUtil.CallBack() {
                                    @Override
                                    public void call() {
                                        PToast.showShort("忽略成功!");
                                    }
                                });
                            }
                        });
                        exListView.smoothCloseChooseView();
                    }
                }, "忽略未读消息,但消息不会删除.", "忽略消息");
                break;
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        switch (key) {
            case MsgType.MT_User_List_Msg_Change:
                initData();
                break;
            default:
                break;
        }
    }
}