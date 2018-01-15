package com.juxin.predestinate.ui.mail;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.db.DBCallback;
import com.juxin.predestinate.bean.db.utils.RxUtil;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseFragment;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.logic.swipemenu.SwipeListView;
import com.juxin.predestinate.module.logic.swipemenu.SwipeMenu;
import com.juxin.predestinate.module.logic.swipemenu.SwipeMenuCreator;
import com.juxin.predestinate.module.util.PickerDialogUtil;
import com.juxin.predestinate.module.util.TimerUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.mail.item.MailItemType;
import com.juxin.predestinate.ui.mail.item.MailMsgID;
import com.juxin.predestinate.ui.main.MainActivity;
import com.juxin.predestinate.ui.user.util.OffNetPanel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 消息
 *
 * @author Kind
 * @date 2017/3/20
 */
public class MailFragment extends BaseFragment implements AdapterView.OnItemClickListener,
        SwipeListView.OnSwipeItemClickedListener, PObserver, View.OnClickListener, AbsListView.OnScrollListener {

    private MailFragmentAdapter mailFragmentAdapter;
    private SwipeListView listMail;
    private View mail_bottom;
    private Button mail_delete, mail_all_ignore;

    private boolean isGone = false;//是否首面底部，默认是false
    private List<BaseMessage> mailDelInfoList = new ArrayList<>();
    private boolean isShow = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.mail_fragment);
        setTitle(getResources().getString(R.string.main_btn_mail));
        onTitleRight();
        initView();
        initListenerAndRequest();

        return getContentView();
    }

    private void initListenerAndRequest() {
        MsgMgr.getInstance().attach(this);
        // 消息要分类，通过性别分类，如果没有个人资料就去请求
        if (ModuleMgr.getCenterMgr().getMyInfo().getUid() < 0) {
            LoadingDialog.show(getActivity(), "加载中...");
            ModuleMgr.getCenterMgr().reqMyInfo(new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    LoadingDialog.closeLoadingDialog();
                    if (response.isOk()) {
                        ModuleMgr.getChatListMgr().getWhisperListUnSubscribe();
                    }
                }
            });
        } else {
            ModuleMgr.getChatListMgr().getWhisperListUnSubscribe();
        }
    }

    private void onTitleRight() {
        setTitleRightImgGone();
        setTitleRight(getString(R.string.edit), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isGone) {
                    editContent();
                } else {
                    cancleEdit();
                }
            }
        });
    }

    private void ondisplayTitleLeft() {
        View title_left = LayoutInflater.from(getActivity()).inflate(R.layout.f1_mail_title_left, null);
        title_left.findViewById(R.id.mail_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAll();
            }
        });
        setTitleLeftContainer(title_left);
    }

    private void onhideTitleLeft() {
        setTitleLeftContainerRemoveAll();
    }

    private void initView() {
        RelativeLayout netStatusLayout = (RelativeLayout) findViewById(R.id.netStatusLayout);
        OffNetPanel offNetPanel = new OffNetPanel(getActivity(), netStatusLayout);
        netStatusLayout.addView(offNetPanel);

        listMail = (SwipeListView) findViewById(R.id.mail_list);
        //设置不可以滑动的
        listMail.addSpecilList(MailItemType.Mail_Item_Title.type);
        listMail.addSpecilList(MailItemType.Mail_Item_Title_TWO.type);
        listMail.addSpecilList(MailItemType.Mail_Item_BlankBar.type);
        listMail.addSpecilList(MailItemType.Mail_Item_Bottom.type);

        mail_bottom = findViewById(R.id.mail_bottom);
        mail_delete = (Button) findViewById(R.id.mail_delete);
        mail_all_ignore = (Button) findViewById(R.id.mail_all_ignore);
        mail_delete.setOnClickListener(this);
        mail_all_ignore.setOnClickListener(this);

        View listview_footer = LayoutInflater.from(getActivity()).inflate(R.layout.common_footer_distance, null);
        listMail.addFooterView(listview_footer);
        listview_footer.setOnClickListener(null);
        mailFragmentAdapter = new MailFragmentAdapter(getContext(), null);
        listMail.setAdapter(mailFragmentAdapter);
        mailFragmentAdapter.updateAllData();
        showAllData();

        listMail.setPullLoadEnable(false);
        listMail.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                if (menu.getViewType() == MailItemType.Mail_Item_Ordinary.type ||
                        menu.getViewType() == MailItemType.Mail_Item_Private.type ||
                        menu.getViewType() == MailItemType.Mail_Item_Gift.type ||
                        menu.getViewType() == MailItemType.Mail_Item_Invite.type ||
                        //  menu.getViewType() == MailItemType.Mail_Item_Other.type ||
                        menu.getViewType() == MailItemType.Mail_Item_Stranger.type) {//如果是私聊，私密，陌生人消息都显示删除
                    menu.setTitle("删除");
                } else {
                    menu.setTitle("忽略");
                }
                menu.setTitleSize(18);
                menu.setTitleColor(Color.WHITE);
                menu.setViewHeight(mailFragmentAdapter.getItemHeight());
            }
        });

        listMail.setPullLoadEnable(false);
        listMail.setPullRefreshEnable(false);
        listMail.setOnItemClickListener(this);
        listMail.setSwipeItemClickedListener(this);
        listMail.setOnScrollListener(this);
    }

    @Override
    public void onSwipeChooseOpened() {
        ((MainActivity) getActivity()).onGoneBottom(false);
        mail_bottom.setVisibility(View.VISIBLE);
        getTitleRightText().setText(getString(R.string.cancel));
        isGone = true;
        ondisplayTitleLeft();
    }

    @Override
    public void onSwipeChooseClosed() {
        ((MainActivity) getActivity()).onGoneBottom(true);
        mail_bottom.setVisibility(View.GONE);
        getTitleRightText().setText(getString(R.string.edit));
        isGone = false;
        onhideTitleLeft();
    }

    @Override
    public void onSwipeChooseChecked(int position, boolean isChecked) {
        int size = mailFragmentAdapter.getList().size();
        if (size <= 0 || position >= size) {
            return;
        }
        BaseMessage message = mailFragmentAdapter.getItem(position);
        if (isChecked) {
            mailDelInfoList.add(message);
        } else {
            // 此处因为可能BaseMessage不是add时候的同一message所以，用WhisperID判断
            long wid = message.getLWhisperID();
            int delListSize = mailDelInfoList.size();
            BaseMessage msg;
            for (int i = 0; i < delListSize; i++) {
                msg = mailDelInfoList.get(i);
                if (msg.getLWhisperID() == wid) {
                    mailDelInfoList.remove(msg);
                    break;
                }
            }
        }
        mail_delete.setEnabled(mailDelInfoList.size() > 0);
    }

    @Override
    public void onSwipeMenuClick(int position, SwipeMenu swipeMenu, View contentView) {
        List<BaseMessage> baseMessageList = mailFragmentAdapter.getList();
        final BaseMessage item = baseMessageList.get(position);
        if (item != null) {
            MailMsgID mailMsgID = MailMsgID.getMailMsgID(item.getLWhisperID());
            if (mailMsgID != null) {
                switch (mailMsgID) {
                    case Follow_Msg:
                        ModuleMgr.getChatListMgr().updateFollow();
                        break;
                    case MyFriend_Msg:
                        break;

                    case Chum_Msg:

                        break;
                    case Greet_Msg:
                        List<BaseMessage> tmpList = ModuleMgr.getChatListMgr().getGeetList();
                        if (tmpList.size() <= 0) {
                            PToast.showCenterShort("没有打招呼的人！");
                            return;
                        }
                        LoadingDialog.show(getActivity(), getString(R.string.ignore));
                        ModuleMgr.getChatListMgr().updateToBatchRead(tmpList, new DBCallback() {
                            @Override
                            public void OnDBExecuted(long result) {
                                LoadingDialog.closeLoadingDialog(1000);
                            }
                        });
                        break;
                    case Private_Msg://私聊消息
                    case Stranger_Msg://陌生人消息
                        deleteMsg(item.getLWhisperID());
                        break;
                    default:
                        break;
                }
            } else {
                if (MailSpecialID.customerService.getSpecialID() == item.getLWhisperID()) {
                    BaseMessage baseMessage = mailFragmentAdapter.isContainMessage(MailSpecialID.customerService.getSpecialID());
                    if (baseMessage.getNum() <= 0) {
                        PToast.showCenterShort("没有可忽略的人！");
                        return;
                    }
                    List<BaseMessage> tmpList = new ArrayList<>();
                    tmpList.add(baseMessage);
                    LoadingDialog.show(getActivity(), getString(R.string.ignore));
                    ModuleMgr.getChatListMgr().updateToBatchRead(tmpList, new DBCallback() {
                        @Override
                        public void OnDBExecuted(long result) {
                            LoadingDialog.closeLoadingDialog(1000);
                        }
                    });
                } else {
                    deleteMsg(item.getLWhisperID());
                }
            }
        }
    }

    private void deleteMsg(long uid) {
        LoadingDialog.show(getActivity(), getString(R.string.deleted));
        ModuleMgr.getChatListMgr().deleteMessage(uid, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                LoadingDialog.closeLoadingDialog(1000);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BaseMessage message = (BaseMessage) parent.getAdapter().getItem(position);
        if (message != null) {
            MailMsgID mailMsgID = MailMsgID.getMailMsgID(message.getLWhisperID());
            if (mailMsgID != null) {
                switch (mailMsgID) {
                    case Follow_Msg:
                        UIShow.showMyAttentionAct(getContext());
                        break;
                    case Chum_Msg:
                        UIShow.showMyChumActAct(getContext());
                        Statistics.userBehavior(SendPoint.menu_xiaoxi_miyou);
                        break;
                    case MyFriend_Msg://我的好友
                        UIShow.showMyFriendsAct(getActivity());
                        StatisticsMessage.friendClick();
                        break;
                    case Greet_Msg://打招呼的人
                        UIShow.showSayHelloUserAct(getActivity());
                        break;
                    default:
                        break;
                }
            } else {
                if (MailSpecialID.systemMsg.getSpecialID() == message.getLWhisperID()) {//公告
                    UIShow.showSysMessActivity(getActivity());
                    return;
                }
                UIShow.showPrivateChatAct(getActivity(), message.getLWhisperID(), message.getName(), message.getKfID());
                StatisticsMessage.openChat(message.getLWhisperID(), ModuleMgr.getChatListMgr().getNoReadNum(message.getLWhisperID()));
            }
        }
    }

    // 全选
    public void selectAll() {
        mailDelInfoList.clear();
        if (mailFragmentAdapter != null && mailFragmentAdapter.mailItemOrdinarySize() > 0) {
            if (mail_delete != null) {
                mail_delete.setEnabled(true);
            }
            mailDelInfoList.addAll(mailFragmentAdapter.mailItemOrdinary());
            listMail.selectAllChooseView();
        }
    }

    public void cancleEdit() {
        mailDelInfoList.clear();
        mail_delete.setEnabled(false);
        listMail.smoothCloseChooseView();
    }

    public void editContent() {
        mailDelInfoList.clear();
        mail_delete.setEnabled(false);
        if (mailFragmentAdapter.mailItemOrdinarySize() > 0) {
            listMail.smoothOpenChooseView();
        } else {
            setTitleLeftContainerRemoveAll();
            PToast.showCenterShort("没有可编辑选项");
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        if (mailFragmentAdapter == null) {
            return;
        }
        switch (key) {
            case MsgType.MT_User_List_Msg_Change:
            case MsgType.MT_Stranger_New:
            case MsgType.MT_Friend_Num_Notice:
                if (isHidden() || !isShow) {
                    return;
                }
                mailFragmentAdapter.updateAllData();
                showAllData();

                //在编辑中保留之前已选择的选项
                if (isGone && mailDelInfoList.size() > 0) {
                    listMail.clearAllChooseChecked();
                    for (BaseMessage msg : mailDelInfoList) {
                        for (int i = 0; i < mailFragmentAdapter.getCount(); i++) {
                            BaseMessage baseMessage = mailFragmentAdapter.getItem(i);
                            if (msg.getLWhisperID() == baseMessage.getLWhisperID()) {
                                listMail.selectChooseView(i, true);
                                break;
                            }
                        }
                    }
                    mailFragmentAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mail_delete:
                mail_delete.setEnabled(false);
                LoadingDialog.show(getActivity(), getString(R.string.deleted));
                ModuleMgr.getChatListMgr().deleteBatchMessage(mailDelInfoList, new DBCallback() {
                    @Override
                    public void OnDBExecuted(long result) {
                        LoadingDialog.closeLoadingDialog(1000);
                    }
                });
                setTitleLeftContainerRemoveAll();
                listMail.smoothCloseChooseView();
                mailDelInfoList.clear();
                StatisticsMessage.deleteUser(mailDelInfoList);
                break;
            case R.id.mail_all_ignore:
                //忽略所有未读消息
                PickerDialogUtil.showSimpleAlertDialog(getActivity(), new SimpleTipDialog.ConfirmListener() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onSubmit() {
                        setTitleLeftContainerRemoveAll();
                        LoadingDialog.show(getActivity(), getString(R.string.ignore));
                        ModuleMgr.getChatListMgr().updateToReadAll(new DBCallback() {
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
                        listMail.smoothCloseChooseView();

                    }
                }, "忽略未读消息,但消息不会删除.", "忽略消息");
                break;
            default:
                break;
        }
    }

    /**
     * 检测个人资料
     *
     * @param view
     */
    private void detectInfo(AbsListView view) {
        final List<Long> stringList = new ArrayList<>();

        int firs = view.getFirstVisiblePosition();
        int last = view.getLastVisiblePosition();

        for (int i = firs; i < last; i++) {
            BaseMessage message = mailFragmentAdapter.getItem(i);
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
                        public void onNext(@NonNull List<UserInfoLightweight> lightweights) {
                            queryDisposable.dispose();
                            if (lightweights != null && lightweights.size() > 0) {
                                ModuleMgr.getChatMgr().updateUserInfoList(lightweights);
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
    public void onScrollStateChanged(final AbsListView view, int scrollState) {
        if (mailFragmentAdapter == null) {
            return;
        }
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: {//停止滚动
                //设置为停止滚动
                mailFragmentAdapter.setScrollState(false);
                showAllData();
                break;
            }
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING: {//滚动做出了抛的动作
                //设置为正在滚动
                mailFragmentAdapter.setScrollState(true);
                break;
            }
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: {//正在滚动
                //设置为正在滚动
                mailFragmentAdapter.setScrollState(true);
                break;
            }
            default:
                break;
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
                    detectInfo(listMail);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isHidden()) {
            mailFragmentAdapter.updateAllData();
            showAllData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isShow = true;
        mailFragmentAdapter.updateAllData();
        showAllData();
        if (mailFragmentAdapter != null && mailFragmentAdapter.getList() != null && mailFragmentAdapter.getList().size() != 0) {
            if (listMail != null) {
                listMail.setmTouchPosition(-2);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MsgMgr.getInstance().detach(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        isShow = false;
    }
}