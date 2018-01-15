package com.juxin.predestinate.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.viewanimator.ViewAnimator;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.library.unread.BadgeView;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.StackNode;
import com.juxin.predestinate.bean.center.update.UpgradeSource;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.InviteVideoMessage;
import com.juxin.predestinate.module.local.login.PromoCode;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.BaseFragment;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.FinalKey;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.logic.model.impl.UnreadMgrImpl;
import com.juxin.predestinate.module.logic.notify.CallingNotificationWindow;
import com.juxin.predestinate.module.logic.notify.view.CustomFloatingPanel;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.logic.socket.IMProxy;
import com.juxin.predestinate.module.util.GamePlayer;
import com.juxin.predestinate.module.util.JsonUtil;
import com.juxin.predestinate.module.util.ShareUtil;
import com.juxin.predestinate.module.util.TimerUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.discover.DiscoverMFragment;
import com.juxin.predestinate.ui.friend.FriendFragment;
import com.juxin.predestinate.ui.live.ui.LiveMainFragment;
import com.juxin.predestinate.ui.mail.MailFragment;
import com.juxin.predestinate.ui.share.EarnMoneyQQShareCallBack;
import com.juxin.predestinate.ui.user.auth.MyAuthenticationAct;
import com.juxin.predestinate.ui.user.fragment.UserFragment;
import com.juxin.predestinate.ui.user.my.LiveCardRedBagDlg;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.juxin.predestinate.ui.web.WebFragment;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

import org.json.JSONObject;

/**
 * 应用主界面activity
 *
 * @author ZRP
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, PObserver {

    private FragmentManager fragmentManager;
    private DiscoverMFragment discoverMFragment;
    private MailFragment mailFragment;
    private FriendFragment friendFragment;
    private UserFragment userFragment;
    private LiveMainFragment liveFragment;

    /**
     * 记录当前的fragment
     */
    private BaseFragment current;
    private View[] views;

    private CustomFloatingPanel floatingPanel;
    private ViewGroup floating_message_container;
    private View layout_main_bottom;
    private BadgeView mail_num, user_num;

    /**
     * 邀请弹框 @author Mr.Huang
     */
    private CallingNotificationWindow window;
    private boolean activityVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);

        Hosts.initWebAppUrl();
        initViews();
        initData();
        initFragment();
        initListenerAndRequest();
    }

    private void initData() {
        // 请求当前用户音视频配置
        ModuleMgr.getCommonMgr().requestVideochatConfig(null);
        // 检查应用升级[其余首页弹窗逻辑都在该方法内部实现]
        ModuleMgr.getCommonMgr().checkUpdate(this, UpgradeSource.Main, false);
        // 获取客服联系信息
        ModuleMgr.getCommonMgr().getCustomerserviceContact();
        // 请求礼物配表
        ModuleMgr.getCommonMgr().requestGiftList(null);
        //请求用户直播认证状态
        ModuleMgr.getCommonMgr().getApplyLiveStatus(null);

        // 女性用户第一次进入应用的时候设置个人中心自动回复“NEW”角标提醒
        if (!ModuleMgr.getCenterMgr().getMyInfo().isMan()
                && PSP.getInstance().getBoolean(
                ModuleMgr.getCommonMgr().getPrivateKey(FinalKey.ISSHOWAUTOREPLYPOINT_MYHOME), true)) {
            ModuleMgr.getUnreadMgr().addNumUnread(UnreadMgrImpl.WOMAN_AUTOREPLY);
        }
        updateNetInfo();
        GamePlayer.getInstance().initLoadSound();
    }

    /**
     * 设置用户设备网络信息
     */
    private void updateNetInfo() {
        ModuleMgr.getCommonMgr().updateNetInfo(NetworkUtils.getNetWorkType(App.getContext()), ModuleMgr.getAppMgr().getPhoneMode());
    }

    /**
     * 初始化监听与请求
     */
    private void initListenerAndRequest() {
        MsgMgr.getInstance().attach(this);
        onMsgNum(ModuleMgr.getChatListMgr().getUnreadNumber());
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        discoverMFragment = new DiscoverMFragment();
        mailFragment = new MailFragment();
        friendFragment = new FriendFragment();
        userFragment = new UserFragment();
//        liveFragment = new WebFragment(getString(R.string.main_btn_live), Hosts.H5_LIVE_LIST);
        liveFragment = new LiveMainFragment();
        switchContent(discoverMFragment);
    }

    private void initViews() {
        View discoveryLayout = findViewById(R.id.discovery_layout);
        View mailLayout = findViewById(R.id.mail_layout);
        View friendLayout = findViewById(R.id.friend_layout);
        View plazaLayout = findViewById(R.id.plaza_layout);
        View userLayout = findViewById(R.id.user_layout);

        views = new View[]{discoveryLayout, mailLayout, friendLayout, plazaLayout, userLayout};

        discoveryLayout.setOnClickListener(this);
        mailLayout.setOnClickListener(this);
        friendLayout.setOnClickListener(this);
        plazaLayout.setOnClickListener(this);
        userLayout.setOnClickListener(this);

        floating_message_container = findViewById(R.id.floating_message_container);
        floatingPanel = new CustomFloatingPanel(this);
        floatingPanel.initView();

        mail_num = findViewById(R.id.mail_number);
        user_num = findViewById(R.id.user_number);
        layout_main_bottom = findViewById(R.id.layout_main_bottom);

        window = new CallingNotificationWindow(this);
    }

    /**
     * 切换当前显示的fragment
     */
    private void switchContent(BaseFragment fragment) {
        tabSwitchStatus(fragment);

        if (current != fragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (current != null) {
                transaction.hide(current);
            }
            //先判断是否被add过
            if (!fragment.isAdded()) {
                transaction.add(R.id.content, fragment).commitAllowingStateLoss();
            } else {
                // 隐藏当前的fragment，显示下一个
                transaction.show(fragment).commitAllowingStateLoss();
            }
            fragmentManager.executePendingTransactions();
            current = fragment;
        }
    }

    /**
     * fragment切换的时候的状态判断以及请求
     *
     * @param fragment 切换的fragment
     */
    private void tabSwitchStatus(BaseFragment fragment) {
        if (fragment == discoverMFragment) {
            tabSwitchHandler.sendEmptyMessage(R.id.discovery_layout);
        } else if (fragment == mailFragment) {
            tabSwitchHandler.sendEmptyMessage(R.id.mail_layout);
        } else if (fragment == friendFragment) {
            tabSwitchHandler.sendEmptyMessage(R.id.friend_layout);
        } else if (fragment == liveFragment) {
            tabSwitchHandler.sendEmptyMessage(R.id.plaza_layout);
        } else if (fragment == userFragment) {
            tabSwitchHandler.sendEmptyMessage(R.id.user_layout);
        }
    }

    private Handler tabSwitchHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            for (View view : views) {
                // 底部tab选中效果
                view.setSelected(msg.what == view.getId());
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discovery_layout:
                Statistics.userBehavior(SendPoint.menu_faxian);
                discoverMFragment.onRefresh();
                switchContent(discoverMFragment);
                break;
            case R.id.mail_layout:
                Statistics.userBehavior(SendPoint.menu_xiaoxi);
                switchContent(mailFragment);
                if (isFloatShowing) {
                    closeFloatingMessage();
                }
                break;
            case R.id.friend_layout:
                Statistics.userBehavior(SendPoint.menu_haoyou);
                switchContent(friendFragment);
                break;
            case R.id.plaza_layout:
                Statistics.userBehavior(SendPoint.menu_zhibo);
                liveFragment.refresh();
                switchContent(liveFragment);
                break;
            case R.id.user_layout:
                Statistics.userBehavior(SendPoint.menu_me);
                switchContent(userFragment);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 200) {
            UIShow.showNavUserAct(this);
            finish();
        }
        if (requestCode == MyAuthenticationAct.AUTHENTICSTION_REQUESTCODE && resultCode == 200) {//手机绑定成功,跳转到登录页
            UIShow.showUserLoginExtAct(this);
            finish();
        }

        if (requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE) {
            int shareChannel = requestCode == Constants.REQUEST_QQ_SHARE ? ShareUtil.CHANNEL_QQ_FRIEND : ShareUtil.CHANNEL_QQ_ZONE;
            LiveCardRedBagDlg.ShareCallback callBack = new LiveCardRedBagDlg.ShareCallback(shareChannel);
            Tencent.onActivityResultData(requestCode, resultCode, data, callBack);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        simulateExit(intent);
        changeTab(intent.getIntExtra(FinalKey.HOME_TAB_TYPE, FinalKey.MAIN_TAB_1), intent);
    }

    /**
     * 模拟软件退出，特殊情况下使用，防止以System.exit(0)方式退出在某些机型上会再次重启
     *
     * @param intent 跳转Intent
     */
    private void simulateExit(Intent intent) {
        if (intent != null && intent.hasExtra(FinalKey.SIMULATE_EXIT_KEY)) {
            finish();

            Process.killProcess(Process.myPid());
            System.exit(0);
            System.gc();
        }
    }

    /**
     * 切换首页tab
     *
     * @param tab_type tab类型
     * @param intent   跳转intent，默认传null
     */
    public void changeTab(int tab_type, Intent intent) {
        PLogger.d("---changeTab--->tab_type：" + tab_type);

        if (FinalKey.MAIN_TAB_1 == tab_type) {
            //跳转到发现tab
            switchContent(discoverMFragment);
        } else if (FinalKey.MAIN_TAB_2 == tab_type) {
            //跳转到直播tab
            switchContent(liveFragment);
        } else if (FinalKey.MAIN_TAB_3 == tab_type) {
            //跳转到消息tab
            switchContent(mailFragment);
        } else if (FinalKey.MAIN_TAB_4 == tab_type) {
            //跳转到好友tab
            switchContent(friendFragment);
        } else if (FinalKey.MAIN_TAB_5 == tab_type) {
            //跳转到我的tab
            switchContent(userFragment);
        }
    }

    /**
     * 用于判断双击退出时间间隔
     */
    private long firstExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            long doubleExitTime = System.currentTimeMillis();
            if (doubleExitTime - firstExitTime < 2000) {
                //假退出，只关闭当前页面
                Statistics.shutDown();
                finish();
            } else {
                firstExitTime = doubleExitTime;
                PToast.showShort(getResources().getString(R.string.tip_quit));
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void onGoneBottom(boolean isGone) {
        layout_main_bottom.setVisibility(isGone ? View.VISIBLE : View.GONE);
    }

    private void onMsgNum(int num) {
        mail_num.setText(ModuleMgr.getChatListMgr().getUnreadTotalNum(num));
        mail_num.setVisibility(num > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onMessage(String key, final Object value) {
        switch (key) {
            case MsgType.MT_User_List_Msg_Change:
                TimerUtil.beginTime(new TimerUtil.CallBack() {
                    @Override
                    public void call() {
                        onMsgNum(ModuleMgr.getChatListMgr().getUnreadNumber());
                    }
                }, 200);
                break;

            case MsgType.MT_Unread_change:
                ModuleMgr.getUnreadMgr().registerBadge(user_num, true, UnreadMgrImpl.CENTER);
                break;

            case MsgType.MT_Net_Change:
                updateNetInfo();
                break;

            case MsgType.MT_DIAMAND_CONSUME:
                //首次爵位系统提示(监听钻石变化)
                UIShow.showAccumulatedSystemDlg();
                break;

            case MsgType.MT_REFRESH_CHAT:
                // 弹出对方忙线中...弹框，刷新语聊数据
                if (discoverMFragment != null) {
                    discoverMFragment.onRefresh();
                }
                break;

            case MsgType.MT_CLOSE_SAY_HELLO:
                break;

            case MsgType.MT_SURE_USER:
                MsgMgr.getInstance().delay(new Runnable() {
                    @Override
                    public void run() {
                        UIShow.showSureUserDlg(MainActivity.this, true);
                    }
                }, 500);
                break;
            case MsgType.MT_PUNCH_REDENVELOPE:
                if (discoverMFragment != null) {
                    discoverMFragment.setRedCardGone();
                }
                break;
            case MsgType.MT_SHARE_SUCCEED:
                ModuleMgr.getCommonMgr().shareCodeCallBack2(0, 2, (int) value, new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        if (response.isOk()) {
                            MsgMgr.getInstance().sendMsg(MsgType.MT_PUNCH_REDENVELOPE, value);
                            JSONObject jsonObject = JsonUtil.getJsonObject(response.getResponseString());
                            jsonObject = jsonObject.optJSONObject("res");
                            if (jsonObject != null) {
                                int pkg = jsonObject.optInt("pkg");
                                double amount = jsonObject.optInt("amount");
                                if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                                    PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.GET_RED_BAG_TIP), true);
                                    UIShow.showCardRedBagDlg(MainActivity.this, CenterConstant.TYPE_SUCCEED, amount / 100.0);
                                } else {
                                    UIShow.showCardRedBagDlg(MainActivity.this, CenterConstant.TYPE_SUCCEED_WOMAN, amount / 100.0);
                                }
                            }
                        } else {
                            UIShow.showCardRedBagDlg(MainActivity.this, CenterConstant.TYPE_FAILURE, 0);
                        }
                    }
                });
                break;
            case MsgType.MT_SHARE_FAILURE:
                UIShow.showCardRedBagDlg(MainActivity.this, CenterConstant.TYPE_FAILURE, 0);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ModuleMgr.getUnreadMgr().registerBadge(user_num, true, UnreadMgrImpl.CENTER);
        IMProxy.getInstance().connect();
        if (window != null) {
            window.onResume();
        }
        //PromoCode.initPromoUser(this, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MsgMgr.getInstance().detach(this);
    }

    // --------------------------消息提示处理-----------------------------------------

    private boolean isFloatShowing = false;
    private Handler floatHandler = new Handler();
    private Runnable floatRunnable = new Runnable() {
        @Override
        public void run() {
            floating_message_container.removeAllViews();
            isFloatShowing = false;
        }
    };

    /**
     * 展示首页消息悬浮提示
     *
     * @param simpleData  简略个人资料
     * @param baseMessage 消息体
     * @param content     消息提示内容
     */
    public void showFloatingMessage(final UserInfoLightweight simpleData, final BaseMessage baseMessage, String content) {
        synchronized (this) {
            if (current == mailFragment || current == userFragment) {
                return;
            }
            ModuleMgr.getNotifyMgr().noticeRemind(baseMessage.getType());
            floatHandler.removeCallbacks(floatRunnable);
            floatHandler.postDelayed(floatRunnable, 5 * 1000);
            floatingPanel.init(TextUtils.isEmpty(simpleData.getNickname()) ? String.valueOf(simpleData.getUid()) : simpleData.getNickname(),
                    content, simpleData.getAvatar(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UIShow.showPrivateChatAct(App.getActivity(), baseMessage.getLWhisperID(), simpleData.getNickname());
                            closeFloatingMessage();
                        }
                    });
            if (!isFloatShowing) {
                floating_message_container.removeAllViews();
                floating_message_container.addView(floatingPanel.getContentView());
                floatingPanel.getContentView().setTranslationY(-100f);
                ViewAnimator
                        .animate(floatingPanel.getContentView())
                        .translationY(-100, 0)
                        .decelerate()
                        .duration(1000)
                        .start();
                isFloatShowing = true;
            }
        }
    }

    /**
     * 移除首页消息悬浮提示
     */
    public void closeFloatingMessage() {
        floatHandler.removeCallbacks(floatRunnable);
        floatRunnable.run();
    }

    public FriendFragment getFriendFragment() {
        return friendFragment;
    }

    /**
     * 循环检查Activity显示计数
     *
     * @author Mr.Huang
     */
    private int checkCount;

    @Override
    protected void onStop() {
        super.onStop();
        if (window != null) {
            window.stopPlaySound();
        }
    }

    /**
     * 妹的！onStart, onResume都不是Activity真正visible的时间点，真正的visible时间点是下面函数被执行时。
     *
     * @param hasFocus
     * @author Mr.Huang
     * @date 2017-09-21
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        activityVisible = hasFocus;
    }

    /**
     * 显示邀请弹框
     *
     * @author Mr.Huang
     */
    public void showWindow(final UserDetail detail, final InviteVideoMessage message) {
        if (detail == null || message == null) {
            return;
        }
        if (window != null && layout_main_bottom != null) {
            if (activityVisible && !isFinishing()) {
                window.setData(detail, message);
                window.showAsDropDown(layout_main_bottom);
            } else {
                if (checkCount > 10) {
                    //如果循环检查10次还不能显示就算了
                    checkCount = 0;
                    return;
                }
                checkCount++;
                layout_main_bottom.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showWindow(detail, message);
                    }
                }, 500);
            }
        }
    }

    @Override
    public StackNode getStackNode() {
        StackNode node = super.getStackNode();
        removePrevios(node);

        if (current instanceof DiscoverMFragment) {
            StackNode newNode = new StackNode();
            newNode.stack = ((DiscoverMFragment) current).getChildClass().getName();
            newNode.previous = node;
            return newNode;
        }

        return node;
    }

    /**
     * 移除MainActivity 前面的节点，始终以MainActivity为根节点，防止从通知消息进入，Activity的调用信息混乱
     *
     * @param node
     */
    private void removePrevios(StackNode node) {
        if (node == null) {
            return;
        }
        if (node.stack.equals(MainActivity.class.getName())) {
            node.previous = null;
        }
        removePrevios(node.previous);
    }
}
