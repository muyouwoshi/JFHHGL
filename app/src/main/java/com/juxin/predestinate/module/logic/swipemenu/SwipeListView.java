package com.juxin.predestinate.module.logic.swipemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.module.logic.baseui.xlistview.XListView;

import java.util.ArrayList;

public class SwipeListView extends XListView {
    private static final int DefVelue = -10;
    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_X = 1;
    private static final int TOUCH_STATE_Y = 2;

    private int MAX_X = 3;
    private int MAX_Y = 5;

    private SwipeLayout mTouchView;
    private int mTouchPosition;
    private int mTouchState = TOUCH_STATE_NONE;

    private SwipeMenuCreator mMenuCreator;
    private SwipeAdapter mSwipeAdapter;

    private float mDownX;
    private float mDownY;

    private boolean isOpenChooseView = false;
    private OnSwipeItemClickedListener mSwipeItemClickedListener;

    public SwipeListView(Context context) {
        this(context, null);
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        MAX_X = dp2px(MAX_X);
        MAX_Y = dp2px(MAX_Y);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mSwipeAdapter = new SwipeAdapter(getContext(), adapter) {
            @Override
            public void createMenu(SwipeMenu swipeMenu) {
                if (mMenuCreator != null) {
                    mMenuCreator.create(swipeMenu);
                }
            }

            @Override
            public void onChooseCheckedChanged(int position, boolean isChecked) {
                super.onChooseCheckedChanged(position, isChecked);
                if (mSwipeItemClickedListener != null) {
                    mSwipeItemClickedListener.onSwipeChooseChecked(position, isChecked);
                }
            }

            @Override
            public void onSwipeMenuViewClick(int position, SwipeMenu swipeMenu, View contentView) {
                if (mTouchView != null) {
                    mTouchView.closeMenu();
                }
                if (mSwipeItemClickedListener != null) {
                    mSwipeItemClickedListener.onSwipeMenuClick(position, swipeMenu, contentView);
                }
            }
        };
        super.setAdapter(mSwipeAdapter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        touchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

        int type = getAdapter().getItemViewType(touchPosition);
        PLogger.d("SwipeListView onTouchEvent ----->getItemViewType position=" + touchPosition + "  getItemViewType = " + type);
        if (getSpecilList() != null && getSpecilList().contains(type)) {
            return super.onTouchEvent(ev);
        }

        if (action != MotionEvent.ACTION_DOWN && mTouchView == null) {
            if (!isOpenChooseView) {
                return super.onTouchEvent(ev);
            }
            float dy = Math.abs(ev.getY() - mDownY);
            if (action == MotionEvent.ACTION_MOVE) {
                float dx = Math.abs(ev.getX() - mDownX);
                if (dx > MAX_X && dx > dy) {
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                }
            }
            if (action == MotionEvent.ACTION_UP) {
                if (dy > MAX_X) {
                    mTouchState = TOUCH_STATE_Y;
                } else {
                    smoothCloseChooseView();
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                }
            }
            return super.onTouchEvent(ev);
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int oldPos = mTouchPosition;
                mDownX = ev.getX();
                mDownY = ev.getY();
                mTouchState = TOUCH_STATE_NONE;

                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

                PLogger.d("MailFragment onTouchEvent --- oldPos = " + oldPos + " ----  ev.x = " + ev.getX() + "  -- ev.y = " + ev.getY() + " -- mTouchPosition = " + mTouchPosition);
                if (oldPos == mTouchPosition && mTouchView != null && mTouchView.isMenuOpen()) {
                    mTouchState = TOUCH_STATE_X;
                    mTouchView.onSwipeTouchEvent(ev);
                    return true;
                }
                PLogger.d("MailFragment onTouchEvent ----  childAt == " + (mTouchPosition - getFirstVisiblePosition()));
                View view = getChildAt(mTouchPosition - getFirstVisiblePosition());
                PLogger.d("MailFragment onTouchEvent ----  childAt View == " + (view == null) + "--- mTouchView = " + (mTouchView == null));
                if (mTouchView != null && mTouchView.isMenuOpen()) {
                    mTouchView.smoothCloseMenu();
                    mTouchView = null;
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                }
                if (view instanceof SwipeLayout) {
                    mTouchView = (SwipeLayout) view;
                }
                if (mTouchView != null && mTouchView.isChooseOpen()) {
                    mTouchView = null;
                    return super.onTouchEvent(ev);
                }
                if (mTouchView != null && isOpenChooseView) {
                    mTouchView = null;
                    return super.onTouchEvent(ev);
                }
                if (mTouchView != null) {
                    mTouchView.onSwipeTouchEvent(ev);
                }

                PLogger.d("MailFragment onTouchEvent --- mTouchView = " + (mTouchView == null));

                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(ev.getX() - mDownX);
                float dy = Math.abs(ev.getY() - mDownY);
                if (mTouchState == TOUCH_STATE_X) {
                    if (mTouchView != null) {
                        mTouchView.onSwipeTouchEvent(ev);
                    }
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                } else if (mTouchState == TOUCH_STATE_NONE) {
                    if (dy > MAX_Y) {
                        mTouchState = TOUCH_STATE_Y;
                    } else if (dx > MAX_X) {
                        mTouchState = TOUCH_STATE_X;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_X && mTouchView != null) {
                    mTouchView.onSwipeTouchEvent(ev);
                    if (!mTouchView.isMenuOpen()) {
                        mTouchPosition = -1;
                        mTouchView = null;
                    }
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setMenuCreator(SwipeMenuCreator menuCreator) {
        this.mMenuCreator = menuCreator;
    }

    public void smoothOpenChooseView() {
        if (mTouchView != null && mTouchView.isMenuOpen()) {
            mTouchView.smoothCloseMenu();
            mTouchView = null;
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof SwipeLayout) {
                SwipeLayout swipeLayout = (SwipeLayout) view;
                swipeLayout.smoothOpenChoose();
            }
            if (mSwipeItemClickedListener != null) {
                mSwipeItemClickedListener.onSwipeChooseOpened();
            }
            isOpenChooseView = true;
        }
    }

    public void smoothCloseChooseView() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof SwipeLayout) {
                SwipeLayout swipeLayout = (SwipeLayout) view;
                swipeLayout.smoothCloseChoose();
                SwipeChooseView swipeChooseView = (SwipeChooseView) swipeLayout.getChooseView();
                swipeChooseView.setChecked(false);
            }
            if (mSwipeItemClickedListener != null) {
                mSwipeItemClickedListener.onSwipeChooseClosed();
            }
            isOpenChooseView = false;
        }
        mSwipeAdapter.clearAllChooseChecked();
    }

    public void selectAllChooseView() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof SwipeLayout) {
                SwipeLayout swipeLayout = (SwipeLayout) view;
                SwipeChooseView swipeChooseView = (SwipeChooseView) swipeLayout.getChooseView();
                swipeChooseView.setChecked(true);
            }
        }
        mSwipeAdapter.selectAllChooseView();
    }

    public void clearAllChooseChecked(){
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof SwipeLayout) {
                SwipeLayout swipeLayout = (SwipeLayout) view;
                SwipeChooseView swipeChooseView = (SwipeChooseView) swipeLayout.getChooseView();
                swipeChooseView.setChecked(false);
            }
        }
        mSwipeAdapter.clearAllChooseChecked();
    }

    public void selectChooseView(int position, boolean isCheck) {
        mSwipeAdapter.selectChooseView(position, isCheck);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    public boolean isOpenChooseView() {
        return isOpenChooseView;
    }

    public interface OnSwipeItemClickedListener {
        void onSwipeChooseOpened();

        void onSwipeChooseClosed();

        void onSwipeChooseChecked(int position, boolean isChecked);

        void onSwipeMenuClick(int position, SwipeMenu swipeMenu, View contentView);
    }

    public void setSwipeItemClickedListener(OnSwipeItemClickedListener swipeItemClickedListener) {
        this.mSwipeItemClickedListener = swipeItemClickedListener;
    }

    public int getmTouchPosition() {
        return mTouchPosition;
    }

    public void setmTouchPosition(int mTouchPosition) {
        this.mTouchPosition = mTouchPosition;
        mTouchView = null;
        mTouchState = TOUCH_STATE_NONE;
    }


    private int touchPosition = DefVelue;

    private ArrayList<Integer> specilList = null;

    public ArrayList<Integer> getSpecilList() {
        return specilList;
    }

    public void setSpecilList(ArrayList<Integer> specilList) {
        this.specilList = specilList;
    }

    public void addSpecilList(int specilPos) {
        if(specilList == null){
            specilList = new ArrayList();
        }
        specilList.add(specilPos);
    }
}
