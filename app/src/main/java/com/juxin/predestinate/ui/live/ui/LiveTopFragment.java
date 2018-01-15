package com.juxin.predestinate.ui.live.ui;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PLogger;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.live.RoomPayInfo;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.WebPanel;
import com.juxin.predestinate.module.logic.invoke.Invoker;
import com.juxin.predestinate.module.logic.webserver.WebServiceConfig;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.module.util.WebUtil;
import com.juxin.predestinate.ui.live.bean.LiveHotOneState;
import com.juxin.predestinate.ui.live.callback.OnOpenNewGirlLiveListener;
import com.juxin.predestinate.ui.live.event.LiveCmdBusEvent;
import com.juxin.predestinate.ui.live.util.NavigationUtil;
import com.juxin.predestinate.ui.live.view.GestureLayout;
import com.juxin.predestinate.ui.live.view.LiveChatPanel;
import com.juxin.predestinate.ui.live.view.LiveUserRankView;
import com.juxin.predestinate.ui.live.view.StrokeTextView;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


/**
 * 直播间通用部分
 * 顶部、webview 、聊天
 * Created by terry on 2017/7/12.
 */
public abstract class LiveTopFragment extends BaseLiveFragment {

    public GestureLayout mUIRootView;          //ui最外层view
    public View mTopView, mSecondView;

    public View mNickContainer;                //昵称容器
    public ImageView mLiveAvatarIv;            //主播头像
    public TextView mOnLinePeopleTv;           //在线人数
    public TextView mIDTv;                     //直播间id
    public TextView mCharmTv;                  //魅力值
    public TextView mCharm1Tv;                 //魅力值单位
    public TextView mTimeTv;                   //当前时间 or 在线时长
    public FrameLayout mChatContainer;         //聊天面板容器 默认隐藏
    public LiveUserRankView mRecyclerView;     //观众列表
    public ImageView mCloseIv;                 //关闭按钮
    public TextView mNickTv; //主播昵称
    public RelativeLayout mLiveBottomView;//直播间底部
    public WebPanel mWebPanel;
    public View mRankingView;                  //距前名主播排行信息
    public StrokeTextView mRankingTv;                //当前排行
    public TextView mRankingCharmTv;           //距前名主播所差的魅力值
    public LiveChatPanel mChatPanel;            //聊天面板
    public LinearLayout web_container;
    public WebPanel webPanel;
    public LinearLayout mLlLiveMeili;//魅力值Linearlayout

    int lastHeight = 0;
    public String uid = "0", roomid = "";      //主播uid 房间id

    int mHotOneState = LiveHotOneState.HOT_ONE_END; //抢热一状态默认结束
    int mOnLinePeopleCount = 0;                 //在线人数

    /**
     * 设置主播id
     *
     * @param uid
     * @param roomid
     */
    public void setUid(String uid, String roomid) {
        this.uid = uid;
        this.roomid = roomid;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mUIRootView = (GestureLayout) inflater.inflate(R.layout.l1_live_top_container, container, false);
        mUIRootView.setClickable(true);
        mUIRootView.setPullToNextI(onPullToNextListener);
        mUIRootView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        return mUIRootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTopView = view.findViewById(R.id.live_top_view);
        mSecondView = view.findViewById(R.id.live_second_view);
        mNickContainer = view.findViewById(R.id.live_tcontainer_left_view);
        mCloseIv = (ImageView) view.findViewById(R.id.live_tcontainer_close);
        mLiveAvatarIv = (ImageView) view.findViewById(R.id.live_room_avatar_iv);
        mRecyclerView = (LiveUserRankView) view.findViewById(R.id.live_tcontainer_recyclerview);
        mChatContainer = (FrameLayout) view.findViewById(R.id.live_chat_container);
        mOnLinePeopleTv = (TextView) view.findViewById(R.id.live_tcontainer_online_number_tv);
        mIDTv = (TextView) view.findViewById(R.id.live_live_id_tv);
        mCharmTv = (TextView) view.findViewById(R.id.live_charm_tv);
        mCharm1Tv = (TextView) view.findViewById(R.id.live_charm1_tv);
        mTimeTv = (TextView) view.findViewById(R.id.live_time_tv);
        mNickTv = (TextView) view.findViewById(R.id.live_tcontainer_nick_tv);
        mLiveBottomView = (RelativeLayout) view.findViewById(R.id.live_bottom_view);
        mRankingView = view.findViewById(R.id.live_tcontainer_ranking_view);
        mRankingTv = (StrokeTextView) view.findViewById(R.id.live_tcontainer_ranking_tv);
        mRankingCharmTv = (TextView) view.findViewById(R.id.live_tcontainer_ranking_charm_tv);
        mChatPanel = new LiveChatPanel(getActivity());
        mLlLiveMeili = (LinearLayout) view.findViewById(R.id.live_meili_view);
        mLlLiveMeili.setOnClickListener(buttonListener);
        mChatPanel.setChatListener(new LiveChatPanel.ChatListener() {
            @Override
            public void needDiamand() {
                needDiamondSource();
            }
        });
        mChatContainer.addView(mChatPanel.getContentView());
        mTimeTv.setText(new SimpleDateFormat("yyyy.MM.dd").format(new Date()));
        mCloseIv.setOnClickListener(buttonListener);
        mLiveAvatarIv.setOnClickListener(buttonListener);
    }

    /**
     * 用户端重写该方法，主播端不需要处理
     * @param uid
     * @param roomid
     */
    public void onOpenNewGirlLive(String uid,String roomid,OnOpenNewGirlLiveListener listener){

    }

    /**
     * 接收cmd 供Invoker调用
     */
    public void onReceiveCMD(LiveCmdBusEvent liveBusEvent){
        if (liveBusEvent == null || getActivity()==null || getActivity().isFinishing()){
            return;
        }
        if (liveBusEvent.type == LiveCmdBusEvent.REFRESH_CHARM) {
            //刷新魅力值
            showFormatCharm(liveBusEvent.charm);
        } else if (liveBusEvent.type == LiveCmdBusEvent.GIRL_END_LIVE) {
            //直播结束
            if (getActivity() != null && !getActivity().isFinishing()) {
                roomEndCmd(liveBusEvent);
            }
        } else if (liveBusEvent.type == LiveCmdBusEvent.RANKING) {
            //刷新主播排行
            mRankingView.setVisibility(View.VISIBLE);
            mRankingTv.setText(String.valueOf(liveBusEvent.ranking));
            if (liveBusEvent.ranking == 1) {
                mRankingCharmTv.setText("当前排名第1");
            } else {
                showFormatRankCharm(liveBusEvent.need_charm);
            }
        } else if (liveBusEvent.type == LiveCmdBusEvent.ROOM_CONTROL) {
            //房间控制
            roomControlCmd(liveBusEvent);
        } else if (liveBusEvent.type == LiveCmdBusEvent.OPEN_USER_CARD) {
            //打开个人资料
            openUserDetailCard(String.valueOf(liveBusEvent.uid),liveBusEvent.message);
        } else if (liveBusEvent.type == LiveCmdBusEvent.USER_ENTER) {
            //用户进入
            mOnLinePeopleCount++;
            int count = mRecyclerView.appendData(liveBusEvent);
            if (mOnLinePeopleCount < count){
                mOnLinePeopleCount = count;
            }
            mOnLinePeopleTv.setText(mOnLinePeopleCount + "人");
        } else if (liveBusEvent.type == LiveCmdBusEvent.USER_LEAVE) {
            //用户离开
            if (mOnLinePeopleCount > 0) {
                mOnLinePeopleCount--;
            }

            int count = mRecyclerView.userLeave(liveBusEvent);
            if (mOnLinePeopleCount < count){
                mOnLinePeopleCount = count;
            }
            mOnLinePeopleTv.setText(mOnLinePeopleCount + "人");
        } else if (liveBusEvent.type == LiveCmdBusEvent.SYNC_USER_CONSUME) {
            //刷新用户本场消费
            mRecyclerView.appendData(liveBusEvent);
        } else if (liveBusEvent.type == LiveCmdBusEvent.ACTIVITY_ONE_HOT) {
            //抢热一活动
            mHotOneState = liveBusEvent.activity_state;
            if (liveBusEvent.activity_state == LiveHotOneState.HOT_ONE_START) {
                setHotNumberOneViewsStatus(View.GONE);
            } else if (liveBusEvent.activity_state == LiveHotOneState.HOT_ONE_END) {
                setHotNumberOneViewsStatus(View.VISIBLE);
            }
        }
    }

    /**
     * 发弹幕 充值统计
     */
    public abstract void needDiamondSource();

    /**
     * 接收cmd 之后 打开个人卡片
     * @param uid
     */
    public abstract void openUserDetailCard(String uid,String message);

    /**
     *
     * @param wsUrl       websocket 长连接地址
     * @param charm       魅力值
     * @param userTotal   在线人数
     * @param nickName    昵称
     * @param avatar      头像
     */
    public void initHeaderViewData(String wsUrl,String charm,int userTotal,String nickName,String avatar){
        initWebSetting(wsUrl);
        showFormatCharm(charm);
        mOnLinePeopleTv.setText(userTotal + "人");
        mIDTv.setText("ID：" + roomid);
        mNickTv.setText(nickName);
        ImageLoader.loadCircleAvatar(getActivity(), avatar, mLiveAvatarIv);
    }

    public void initWebSetting(String ws_url) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("roomID", roomid);
        params.put("token", ModuleMgr.getLoginMgr().getCookie().replace("auth=", ""));
        params.put("wsUrl",ws_url);

        mWebPanel.loadUrl(WebUtil.jointUrl(WebServiceConfig.liveRoomWeb.getUrl(), params));
        mWebPanel.setWebViewTransparent();
        mWebPanel.hideScrollBar();
    }

    /**
     * 格式化魅力值
     *
     * @param charm
     */
    public void showFormatCharm(String charm) {
        if (!TextUtils.isEmpty(charm)) {
            if (Long.parseLong(charm) > 10 * 10000) {
                DecimalFormat di = new DecimalFormat("#.0");
                mCharmTv.setText(di.format(Long.parseLong(charm) / 10000f));
                mCharm1Tv.setText("万");
            } else {
                mCharmTv.setText(charm);
                mCharm1Tv.setText("");
            }
        }
    }

    /**
     * 格式化距上名主播魅力值
     *
     * @param charm
     */
    public void showFormatRankCharm(String charm) {
        if (!TextUtils.isEmpty(charm)) {
            String formatStr;
            if (Long.parseLong(charm) > 10 * 10000) {
                DecimalFormat di = new DecimalFormat("#.0");
                formatStr = String.format("距上名差%1s万魅力", di.format(Long.parseLong(charm) / 10000f));

            } else {
                formatStr = String.format("距上名差%1s魅力", charm);
            }
            SpannableString spannableString = new SpannableString(formatStr);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#fe5a75")), 4, formatStr.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mRankingCharmTv.setText(spannableString);
        }
    }

    /**
     * 跳转到直播结束页面
     * @param nickName  昵称
     * @param avatar 头像
     * @param time 直播时长
     * @param charm 魅力值
     *
     *  UIShow.showLiveStop
     */
    public abstract void goToLiveStop(String nickName,String avatar,long time, long charm);

    /**
     * 房间结束
     *
     * @param liveBusEvent
     */
    public void roomEndCmd(LiveCmdBusEvent liveBusEvent) {

        //0 正常结束， 1 管理员停止， 2 封禁直播间
        int type = liveBusEvent.room_control_type;
        if (type == 0 || type == 1) {
            //TODO  主播是否接收 接收重写GirlLiveFragment.goToLiveStop
            goToLiveStop(liveBusEvent.nickName, liveBusEvent.avatar,liveBusEvent.liveTime, Long.valueOf(liveBusEvent.charm));

        } else if (type == 2) {
//            ((BaseLiveAct) getActivity()).showRoomFreezeView("");
        }
    }

    /**
     * 房间控制
     * 用户端需要处理
     * @param liveBusEvent
     */
    public abstract void roomControlCmd(LiveCmdBusEvent liveBusEvent);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mWebPanel != null){
            mWebPanel.onDestroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebPanel != null) {
            mWebPanel.getWebView().onResume();
            mWebPanel.getWebView().resumeTimers();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWebPanel != null) {
            mWebPanel.getWebView().onPause();
            mWebPanel.getWebView().pauseTimers();
        }
    }

    /**
     *  房间页面按钮监听事件 关闭头像 加好友
     */
    NoDoubleClickListener buttonListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            if (v.getId() == R.id.live_tcontainer_close) {
                Statistics.userBehavior(SendPoint.page_live_close);
//                ((BaseLiveAct) getActivity()).liveClose();
            } else if (v.getId() == R.id.live_room_avatar_iv) {
                SourcePoint.getInstance().lockSource(getActivity(), "faceplusfirend_sendgift");
                openUserDetailCard(uid,null);
                Statistics.userBehavior(SendPoint.page_live_avatar);
            } else if (v.getId() == R.id.live_meili_view) {
                UIShow.showLiveConsumeListDialog(LiveTopFragment.this.getActivity(), roomid);
            }
        }
    };

    /**
     * 需要 room_uid、room_id、room_live_id、room_live_time
     * @return RoomPayInfo
     */
    public abstract RoomPayInfo getRoomPayInfo();

    /**
     * 抢热一活动控制按钮可见状态
     *
     * 子类处理个性view 可见状态
     */
    public void setHotNumberOneViewsStatus(int visibleStatus) {
        //正在输入中 不做任何处理
        if (lastHeight > 0) {
            return;
        }
        mCloseIv.setVisibility(View.VISIBLE);
    }

    /**
     * 重置底部view可见状态
     * 子类处理个性view 可见状态
     */
    public void resetBottomViewStatus(int visibleStatus) {
        mChatContainer.setVisibility(visibleStatus == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    /**
     *    滑出键盘views进行上下偏移
     */
    public void setViewTranslationY(int heightDifference) {
        mLiveBottomView.setTranslationY(heightDifference);
        mTopView.setTranslationY(heightDifference);
        mSecondView.setTranslationY(heightDifference);
    }

    /**
     * 监听根布局的layout变化
     */
    public ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (getActivity() != null && !getActivity().isFinishing() && mChatContainer.getVisibility() == View.VISIBLE) {
                Rect r = new Rect();
                //获取当前界面可视部分
                App.activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight = App.activity.getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom;
                if (lastHeight > 0 && heightDifference > 0) {  //当聊天框显示时 如果第一次计算 则过滤
                    return;
                }
                int navigationBarHeight = NavigationUtil.getNavigationBarHeight(getActivity());
                PLogger.d("zt===heightDifference " + heightDifference + " navigationBarHeight " + navigationBarHeight + " lastHeight " + lastHeight);

                //前面的条件---带有navigation bar || 后面的条件 不带navigation bar
                if ((heightDifference == navigationBarHeight && lastHeight > heightDifference) || (heightDifference == 0 && lastHeight > 0)) {//键盘收起
                    Invoker.getInstance().keyboardOpt(false, 0);
                    setViewTranslationY(0);
                    if (mHotOneState == LiveHotOneState.HOT_ONE_START) {
                        lastHeight = 0;//要提前 改变lastheight
                        setHotNumberOneViewsStatus(View.GONE); //在抢热一活动中 隐藏各个按钮 除了礼物和关闭
                        mChatContainer.setVisibility(View.GONE);
                    }else {
                        resetBottomViewStatus(View.VISIBLE);
                    }
                    mUIRootView.setInputHeight(0);
                } else if (heightDifference > 0 && navigationBarHeight == 0) {//不含navigation bar
                    Invoker.getInstance().keyboardOpt(true, heightDifference);
                    setViewTranslationY(-heightDifference);
                    mUIRootView.setInputHeight(Math.abs(heightDifference) + UIUtil.dp2px(78)); //键盘高度 + 底部输入框高度
                } else if (navigationBarHeight != 0 && heightDifference > navigationBarHeight) {
                    if (heightDifference > lastHeight && lastHeight != 0) { //navigation bar隐藏
                        Invoker.getInstance().keyboardOpt(true, heightDifference - navigationBarHeight);
                        setViewTranslationY(-heightDifference + navigationBarHeight);
                        mUIRootView.setInputHeight(Math.abs(heightDifference) + UIUtil.dp2px(78)); //键盘高度 + 底部输入框高度
                    } else {
                        Invoker.getInstance().keyboardOpt(true, heightDifference);
                        setViewTranslationY(-heightDifference);
                        mUIRootView.setInputHeight(Math.abs(heightDifference) + navigationBarHeight + UIUtil.dp2px(78)); //键盘高度 + 底部输入框高度
                    }
                }
                lastHeight = heightDifference;
            }
        }
    };
}
