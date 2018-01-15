package com.juxin.predestinate.ui.user.my.CommonDlg;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.bean.db.DBCallback;
import com.juxin.predestinate.module.local.chat.ChatMark;
import com.juxin.predestinate.module.local.mail.MyChumTaskList;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.user.my.adapter.FriendTaskAdapter;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 私聊页密友任务下拉panel
 * Created by zm on 2017/7/17
 */
public class CloseFriendTaskView extends LinearLayout implements PObserver {

    private final int LEVEL_FRIST = 0;   // 第一级
    private final int LEVEL_TWO = 1;     // 第二级
    private final int LEVEL_THREE = 2;   // 第三级
    private int initLevel_Frist;         //一级初始位置
    private int initLevel_Three;         //三级初始位置

    private ListView lvTask;
    private LinearLayout llLevelTop,llLevelDetails;
    private RelativeLayout llAll;
    private TextView tvLevelTop;
    private TextView tvLevelName, tvVoiceNum, tvGiftNum;
    private ImageView imgTaskNum;
    private View llContainerTop;


    private int mLastY = -1; // 保存 y
    private int mLastX = -1; // 保存 x
    private MotionEvent mLastMoveEvent;
    private VelocityTracker mVelocityTracker;
    private float mMaxVelocity;
    private boolean isIntercepted = false;
    private Context mContext;
    private int state = LEVEL_FRIST; //当前状态
    private int stateLevel;
    private MyChumTaskList mFriendTaskInfo;
    private FriendTaskAdapter mFriendTaskAdapter;
    private long tuid;
    private String channel_uid;
    private int taskCount;
    private boolean isClickOut = false;

    private final int SCROLL_DURATION = 300; // scroll back duration

    public CloseFriendTaskView(Context context) {
        this(context, null);
    }

    public CloseFriendTaskView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CloseFriendTaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.f1_level_of_intimacy_panel, this);
        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        initView();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (llLevelDetails != null && (initLevel_Three < llLevelDetails.getMeasuredHeight() || initLevel_Three == 0)) {
            initLevel_Three = llLevelDetails.getMeasuredHeight() > 0 ? llLevelDetails.getMeasuredHeight() : 1;
            if (llLevelDetails.getMeasuredHeight() > 0){
                llLevelTop.setVisibility(VISIBLE);
                llLevelDetails.setVisibility(VISIBLE);
                llLevelDetails.setTranslationY(-llLevelDetails.getMeasuredHeight() + initLevel_Frist);
                state = LEVEL_TWO;
            }
        }
    }

    private void initView() {

        llLevelTop = (LinearLayout) findViewById(R.id.level_of_intimacy_ll_level_top);
        tvLevelTop = (TextView) findViewById(R.id.level_of_intimacy_tv_level_top);
        llLevelDetails = (LinearLayout) findViewById(R.id.chat_level_ll_details);
        tvLevelName = (TextView) findViewById(R.id.chat_level_tv_level_name);
        tvVoiceNum = (TextView) findViewById(R.id.chat_level_tv_voice_num);
        tvGiftNum = (TextView) findViewById(R.id.chat_level_tv_gift_num);
        lvTask = (ListView) findViewById(R.id.level_of_intimacy_lv_task);
        llAll = (RelativeLayout) findViewById(R.id.level_of_intimacy_ll_all);
        imgTaskNum = (ImageView) findViewById(R.id.level_of_intimacy_img_task_num);
        llContainerTop = findViewById(R.id.level_of_intimacy_ll_container_top);


        lvTask.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return CloseFriendTaskView.this.onTouchEvent(event);
            }
        });
        llLevelTop.setOnClickListener(clickListener);
        MsgMgr.getInstance().attach(this);

        llLevelDetails.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (state == LEVEL_THREE){
                    return true;
                }
                return false;
            }
        });


//        llAll.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (state == LEVEL_THREE){
//                    hideTask();
//                }
//            }
//        });
    }

    public void setData(final MyChumTaskList info, final long tuid, String channel_uid) {
        this.mFriendTaskInfo = info;
        this.tuid = tuid;
        this.channel_uid = channel_uid;
        this.taskCount = info.getMyChumTasks().size();

        if (info == null){
            this.setVisibility(INVISIBLE);
            return;
        }

        NobilityList.CloseFriend friend = ModuleMgr.getCommonMgr().getCommonConfig().getNobilityList().queryCloseFriend(info.getLevel() + 1);
        tvLevelTop.setText("Lv."+info.getLevel());
        tvLevelName.setText("Lv."+info.getLevel()+" "+friend.getTitle());
        tvVoiceNum.setText(((int)Math.floor(info.getVideoVoiceSec()/60))+"");
        tvGiftNum.setText(info.getGiftCnt()+"");


        if (mFriendTaskAdapter == null) {
            mFriendTaskAdapter = new FriendTaskAdapter(getContext(), this, info.getMyChumTasks(), tuid, channel_uid, info);
        }
        mFriendTaskAdapter.setLevel(info.getLevel());
        mFriendTaskAdapter.setList(info.getMyChumTasks());
        lvTask.setAdapter(mFriendTaskAdapter);

        Observable<ChatMark> chatMarkObservable = ModuleMgr.getChatListMgr().queryFMark(tuid, ChatMark.CHATMARK_TYPE_TASK);
        if(chatMarkObservable == null) return;
        chatMarkObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                // 此处使用简化的调用方式，如果需要详细的监听回调，需要subscribe一个Observer
                .subscribe(new Observer<ChatMark>() {
                    private Disposable whisperListDisposable;

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        whisperListDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull ChatMark chatMark) {
                        whisperListDisposable.dispose();
                        String data = chatMark.getContent();
                        if(data != null && data.equalsIgnoreCase(TimeUtil.getCurrentData())){//已存在
                            imgTaskNum.setVisibility(GONE);
                        }else{
                            if (info == null){
                                CloseFriendTaskView.this.setVisibility(INVISIBLE);
                                return;
                            }
                            if (info.getTodayTaskStatus() == 1){
                                imgTaskNum.setVisibility(GONE);
                            }else {
                                imgTaskNum.setVisibility(VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        acquireVelocityTracker(ev);
        int deltaY = 0;
        int deltaX = 0;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) ev.getRawY();
                mLastX = (int) ev.getRawX();
                isClickOut = false;
                if (isTouchMoveView(ev)) {
                    return true;
                }else if (state == LEVEL_THREE){
                    isClickOut = true;
                    stateLevel = LEVEL_TWO;
                    if (!isTouchMoveView(ev)){
                        hideTask();
                        return false;
                    }
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                if (!isClickOut){
                    if (taskCount <= 0) {
                        return true;
                    }
                    mLastMoveEvent = ev;
                    int currentY = (int) ev.getRawY();
                    int currentX = (int) ev.getRawX();
                    deltaY = currentY - mLastY;
                    deltaX = currentX - mLastX;
                    mLastY = currentY;
                    mLastX = currentX;
                    if (!isIntercepted) {
                        isIntercepted = true;
                    }
                    if (isIntercepted) {
                        moveView(deltaX, deltaY);
                        invalidate();
                        sendCancelEvent();
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!isClickOut){
                    isClickOut = false;
                    if (taskCount <= 0) {
                        return true;
                    }
                    isIntercepted = false;
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    scrollToFinal();

                    releaseVelocityTracker();
                }else {
                    isClickOut = false;
                    if (state == LEVEL_THREE){
                        hideTask();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * @param event 向VelocityTracker添加MotionEvent
     * @see VelocityTracker#obtain()
     * @see VelocityTracker#addMovement(MotionEvent)
     */
    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 释放VelocityTracker
     *
     * @see VelocityTracker#clear()
     * @see VelocityTracker#recycle()
     */
    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 是否摸到了某个view
     *
     * @param ev
     * @return
     */
    private boolean isTouchMoveView(MotionEvent ev) {
        View downView = getDownView();
        if (downView != null && downView.getVisibility() == VISIBLE) {
            Rect bounds = new Rect();
            downView.getGlobalVisibleRect(bounds);
            int x = (int) ev.getRawX();
            int y = (int) ev.getRawY();
            if (bounds.contains(x, y)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public View getView() {
        if (state == LEVEL_FRIST) {
            return llLevelDetails;
        } else if (state == LEVEL_TWO) {
            return llLevelDetails;
        } else if (state == LEVEL_THREE) {
            return llLevelDetails;
        }
        return null;
    }

    public View getDownView() {
        if (state == LEVEL_FRIST) {
            return null;
        } else if (stateLevel == LEVEL_TWO){
            stateLevel = LEVEL_FRIST;
            return llContainerTop;
        }else if (state == LEVEL_TWO) {
            return null;
        } else if (state == LEVEL_THREE) {
            return llLevelDetails;
        }
        return null;
    }

    public int getState() {
        return state;
    }

    private void moveView(int deltaX, int deltaY) {
        View moveView = getView();
        if (moveView != null) {
            float desY = moveView.getTranslationY() + deltaY;
            if (moveView.getTranslationY() == initLevel_Frist){
                desY = moveView.getTranslationY() - 1;
            }else if (moveView.getTranslationY() == -moveView.getMeasuredHeight() + initLevel_Frist){
                desY = moveView.getTranslationY() + 1;
            }
            if (desY > -moveView.getMeasuredHeight() + initLevel_Frist + UIUtil.dip2px(getContext(), 4)) {
                llLevelTop.setVisibility(INVISIBLE);
            } else {
                llLevelTop.setVisibility(VISIBLE);
            }
            if (desY < -moveView.getMeasuredHeight() + initLevel_Frist) {
                moveView.setTranslationY(-moveView.getMeasuredHeight() + initLevel_Frist);
                llLevelTop.setVisibility(VISIBLE);
                return;
            } else if (desY > initLevel_Frist) {
                moveView.setTranslationY(initLevel_Frist);
                return;
            }else {
                moveView.setTranslationY(desY);
            }
        }
    }

    private void scrollToFinal() {
        ObjectAnimator animator = null;
        View moveView = getView();
        float currY = Math.abs(-moveView.getMeasuredHeight() - moveView.getTranslationY() + initLevel_Frist);
        if (currY < moveView.getMeasuredHeight() / 2) {  //拉下任务列表
            animator = ObjectAnimator.ofFloat(moveView, "translationY", moveView.getTranslationY(), -moveView.getMeasuredHeight() + initLevel_Frist);
            llLevelTop.setVisibility(VISIBLE);
            state = LEVEL_TWO;
        } else if (currY >= moveView.getMeasuredHeight() / 2) {  //拉下任务列表
            animator = ObjectAnimator.ofFloat(moveView, "translationY", moveView.getTranslationY(), initLevel_Frist);
            llLevelTop.setVisibility(INVISIBLE);
            state = LEVEL_THREE;
        }

        if (animator != null) {
            animator.setDuration(SCROLL_DURATION);
            animator.start();
        }
    }

    private boolean mHasSendCancelEvent = false;

    private void sendCancelEvent() {
        if (!mHasSendCancelEvent) {
            mHasSendCancelEvent = true;
            MotionEvent last = mLastMoveEvent;
            MotionEvent e = MotionEvent.obtain(
                    last.getDownTime(),
                    last.getEventTime()
                            + ViewConfiguration.getLongPressTimeout(),
                    MotionEvent.ACTION_CANCEL, last.getX(), last.getY(),
                    last.getMetaState());
            dispatchTouchEventSupper(e);
        }
    }

    public boolean dispatchTouchEventSupper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    private NoDoubleClickListener clickListener = new NoDoubleClickListener() {

        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.level_of_intimacy_ll_level_top:
                    ObjectAnimator animator = null;
                    if (state != LEVEL_THREE) {
                        Statistics.userBehavior(SendPoint.page_chat_expansion);
                        InputMethodManager imm =  (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(imm != null && getContext() instanceof Activity) {
                            imm.hideSoftInputFromWindow(((Activity)getContext()).getWindow().getDecorView().getWindowToken(), 0);
                        }
                        llLevelDetails.setVisibility(VISIBLE);
                        animator = ObjectAnimator.ofFloat(llLevelDetails, "translationY", llLevelDetails.getTranslationY(), initLevel_Frist);
                        llLevelTop.setVisibility(INVISIBLE);
                        state = LEVEL_THREE;

                        ModuleMgr.getChatListMgr().deleteFMark(tuid, ChatMark.CHATMARK_TYPE_TASK, new DBCallback() {
                            @Override
                            public void OnDBExecuted(long result) {
//                                if (result != MessageConstant.ERROR) {
//
//                                    return;
//                                }
                                ChatMark mark = new ChatMark();
                                mark.setNum(1);
                                mark.setType(ChatMark.CHATMARK_TYPE_TASK);
                                mark.setUserID(tuid);
                                mark.setContent(TimeUtil.getCurrentData());
                                ModuleMgr.getChatListMgr().insertFMark(mark,null);
//                                PToast.showShort(getString(R.string.user_other_set_chat_del_fail));
                            }
                        });

                        imgTaskNum.setVisibility(GONE);
                    } else if (state == LEVEL_THREE) {
                        Statistics.userBehavior(SendPoint.page_chat_close);
                        animator = ObjectAnimator.ofFloat(llLevelDetails, "translationY", llLevelDetails.getTranslationY(), -llLevelDetails.getMeasuredHeight() + initLevel_Frist);
                        llLevelTop.setVisibility(VISIBLE);
                        state = LEVEL_TWO;
                    }
                    if (animator != null) {
                        animator.setDuration(SCROLL_DURATION);
                        animator.start();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 隐藏任务列表
     */
    public void hideTask(){
        Statistics.userBehavior(SendPoint.page_chat_close);
        ObjectAnimator animator = ObjectAnimator.ofFloat(llLevelDetails, "translationY", llLevelDetails.getTranslationY(), -llLevelDetails.getMeasuredHeight() + initLevel_Frist);
        llLevelTop.setVisibility(VISIBLE);
        state = LEVEL_TWO;
        animator.setDuration(SCROLL_DURATION);
        animator.start();
    }

    public boolean isShowTask(){
        return state == LEVEL_THREE;
    }

    @Override
    protected void onDetachedFromWindow() {
        MsgMgr.getInstance().detach(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void onMessage(String key, Object value) {
        switch (key) {
            case MsgType.MT_DIAMAND_CHANGE://  接收钻石变更通知
                if (value instanceof Integer && (int) value > 0) {
                }
                break;
            default:
                break;
        }
    }
}