package com.juxin.predestinate.ui.live.view;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.UIUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by terry on 2017/7/12.
 */

public class GestureLayout extends FrameLayout {

    private final int ANIMATION_DURATION = 300; //切换动画时间
    private final int SCROLL_HEIGHT = 90*3; //滑动距离临界点
    private final float DAMPING = 0.55f;    //滑动阻尼系数
    private int mLastMotionY;               //上次滑动的Y坐标
    private int mScreenHeight;
    private boolean isHashPrevious = true;
    private boolean isHashNext = true;
    private boolean isEnableTouch = false; //默认禁止切换
    private boolean isAnim = false;
    private boolean isRefreshLiveRoom = false;
    private int mClickAreaY = 0,mDisableClickY = 0;
    
    private View mHeaderView;
    private View mFootView;

    private GestureLayout.PullStateE mPullStateE = GestureLayout.PullStateE.PULL_STATE_NONE; //代表手指滑动开始时候的意向

    public GestureLayout(Context context) {
        super(context);
        init();
    }

    public GestureLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GestureLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScreenHeight = ModuleMgr.getAppMgr().getScreenHeight();
        mDisableClickY = mScreenHeight -  UIUtil.dp2px(42);
    }

    //设置是否可以滑动
    public void setIsEnableTouch(boolean touch){
        isEnableTouch = touch;
//        Log.d("zt","setIsEnableTouch------"+touch);
    }

    //设置可点击区域
    public void setInputHeight(int kHeight){
        mClickAreaY = mScreenHeight - kHeight;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        hiddenHeaderView();
        hiddenFootView();
    }


    private void hiddenFootView() {
//        mFootView = LayoutInflater.from(getContext()).inflate(R.layout.l1_live_room_fher_view, null);
//        View upView =  mFootView.findViewById(R.id.live_up_layout);
//        upView.setVisibility(VISIBLE);
//
//        LayoutParams footParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenHeight);
//        footParams.topMargin = mScreenHeight;
//        footParams.gravity = Gravity.CENTER_HORIZONTAL;
//        addView(mFootView, footParams);
//
//        mFootView.setVisibility(INVISIBLE);
    }

    private void hiddenHeaderView() {
//        mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.l1_live_room_fher_view, null);
//        View downView =  mHeaderView.findViewById(R.id.live_down_layout);
//        downView.setVisibility(VISIBLE);
//
//        LayoutParams headerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mScreenHeight);
//        headerParams.topMargin = -mScreenHeight;
//        headerParams.gravity = Gravity.CENTER_HORIZONTAL;
//        addView(mHeaderView,headerParams);

    }

    int mLastMotionX = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d("zt","dispatchTouchEvent------"+isEnableTouch);
        int y = (int) event.getRawY();
        int x = (int) event.getRawX();
        if (!isEnableTouch || isAnim){

            if (event.getAction() == MotionEvent.ACTION_UP && pullToNextI!=null && y<= mDisableClickY &&(y<=mClickAreaY || mClickAreaY == 0)){

                pullToNextI.onUp(mClickAreaY);
            }

            return  super.dispatchTouchEvent(event);
        }
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                 mLastMotionY = (int) event.getRawY();
//                 mLastMotionX = (int) event.getRawX();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int deltaY = y - mLastMotionY;
//                int deltaX = x - mLastMotionX;
////                Log.d("zt","ACTION_MOVE ===="+deltaY+"==="+mPullStateE+"---"+y+"---"+mLastMotionY);
//                if (mPullStateE == PullStateE.PULL_STATE_NONE && Math.abs(deltaY) > Math.abs(deltaX)){//只有无状态时才判断当前手指走向
//                    if (deltaY > 0){
//                        mPullStateE = PullStateE.PULL_STATE_DOWN;
//                    }else if (deltaY<0){
//                        mPullStateE = PullStateE.PULL_STATE_UP;
//                    }else{ //不做处理，原来是什么状态还是什么状态
//
//                    }
//                }
//
//                if (mPullStateE == PullStateE.PULL_STATE_DOWN ){
//                    if (deltaY != 0){
//                        changingViewPosition(mHeaderView,deltaY);
//                    }
//                }else if (mPullStateE == PullStateE.PULL_STATE_UP){
//                    if (deltaY != 0){
//                        if (mFootView.getVisibility() !=VISIBLE){
//                            mFootView.setVisibility(VISIBLE);
//                        }
//                        changingViewPosition(mFootView,deltaY);
//                    }
//                }
//                mLastMotionY = y;
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                if (mPullStateE == PullStateE.PULL_STATE_UP ){
//                    int height = mScreenHeight-mFootView.getTop();
//                    if (height > SCROLL_HEIGHT) {
//                        isRefreshLiveRoom = true;
//                        moveTo(mFootView.getTop(),0,  ANIMATION_DURATION);
//                    }else{
//                        moveTo(mFootView.getTop(),mScreenHeight, ANIMATION_DURATION);
//                    }
//                } else if (mPullStateE == PullStateE.PULL_STATE_DOWN) {
//
//                    int height = mHeaderView.getBottom();
//                    if (height > SCROLL_HEIGHT) {
//                        isRefreshLiveRoom = true;
//                        moveTo(height,mScreenHeight, ANIMATION_DURATION);
//                    }else{
//                        moveTo(height,0,  ANIMATION_DURATION);
//                    }
//
//                }
//
//                break;
//        }
        return super.dispatchTouchEvent(event);
    }


    public void moveTo(final int height,final int end,int duration) {
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(height,end);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim = false;
                if (mPullStateE == PullStateE.PULL_STATE_DOWN){
                    if (end == mScreenHeight){
                        Flowable.timer(200, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(@NonNull Long aLong) throws Exception {

                                        if (isRefreshLiveRoom && isHashPrevious && pullToNextI!=null) {
                                            pullToNextI.previous();
                                        }
                                        resetView();
                                    }
                                });

                    }else{
                        resetView();
                    }
                }else if (mPullStateE == PullStateE.PULL_STATE_UP){
                    if (end == 0){
                        Flowable.timer(200, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(@NonNull Long aLong) throws Exception {

                                        if (isRefreshLiveRoom && isHashNext && pullToNextI!=null) {
                                            pullToNextI.next();
                                        }
                                        resetView();
                                    }
                                });
                    }else {
                        resetView();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mPullStateE = GestureLayout.PullStateE.PULL_STATE_NONE;
                mLastMotionY = 0;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {


            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void onAnimationUpdate(android.animation.ValueAnimator valueAnimator) {

                int temp = (int) valueAnimator.getAnimatedValue();
                setViewHeight(temp);
            }
        });
        animator.start();
    }

    int lastBottom = 0;
    /**
     * 设置view的height
     *
     * @param height ，为0时，说明view 刚好完全隐藏；
     */
    private void setViewHeight(int height) {
        View view = null;
        if (mPullStateE == PullStateE.PULL_STATE_DOWN ){
            view = mHeaderView;
            if (lastBottom == 0){
                lastBottom = height;
            }
            mHeaderView.layout(mHeaderView.getLeft(),mHeaderView.getTop() - (lastBottom-height),mHeaderView.getRight(),height);
            lastBottom = mHeaderView.getBottom();
        }else if (mPullStateE == PullStateE.PULL_STATE_UP){
            view = mFootView;
            if (lastBottom == 0){
                lastBottom = height;
            }
            mFootView.layout(mFootView.getLeft(),height,mFootView.getRight(),mFootView.getBottom()+(height-lastBottom));
            lastBottom = mFootView.getTop();
        }

//        if (view != null){
//            Log.d("zt","setViewHeight===="+mPullStateE+"======"+height+"==="+view.getBottom()+"==="+view.getTop()+"=="+(view.getBottom() - view.getTop())+"==="+view.getHeight());
//
//        }
    }

    /**
     * 重置当前view的layout
     */
    private void resetView() {
        if (mPullStateE == PullStateE.PULL_STATE_DOWN){
            LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
            params.height = mScreenHeight;
            params.topMargin = -params.height;
            mHeaderView.setLayoutParams(params);

        }else if (mPullStateE == PullStateE.PULL_STATE_UP){
            LayoutParams params = (LayoutParams) mFootView.getLayoutParams();
            params.height = mScreenHeight ;
            params.topMargin = params.height;
            mFootView.setLayoutParams(params);
            mFootView.setVisibility(INVISIBLE);
        }
        mPullStateE = GestureLayout.PullStateE.PULL_STATE_NONE;
        mLastMotionY = 0;
        lastBottom = 0;
        isRefreshLiveRoom = false;
    }

    /**
     * 修改Header view top margin的值
     *
     * @param deltaY
     */
    private void changingViewPosition(View view,int deltaY) {


        if (view != null){

            int dy = (int) (deltaY * DAMPING);

            view.layout(view.getLeft(),view.getTop()+ dy,view.getRight(),view.getBottom()+ dy);
        }
    }

    private PullToNextI pullToNextI;

    public void setPullToNextI(PullToNextI pullToNextI) {
        this.pullToNextI = pullToNextI;
    }

    public interface PullToNextI {
         void previous();

         void next();

         void onUp(int clickY);
    }


    enum PullStateE {

        PULL_STATE_NONE,
        PULL_STATE_DOWN,
        PULL_STATE_UP
    }

    public void setHashPrevious(boolean isHashPrevious) {
        this.isHashPrevious = isHashPrevious;
    }

    public void setHashNext(boolean isHashNext) {
        this.isHashNext = isHashNext;
    }
}
