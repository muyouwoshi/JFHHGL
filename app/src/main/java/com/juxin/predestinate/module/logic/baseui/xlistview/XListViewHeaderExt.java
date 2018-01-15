package com.juxin.predestinate.module.logic.baseui.xlistview;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

/**
 * 自定风的下拉刷新头部
 */
public class XListViewHeaderExt extends LinearLayout {

    private LinearLayout mContainer;
    private TextView mHintTextView;
    private FrameLayout mHeartContainer;
    private FrameLayout mHeartContainerExt;
    private RelativeLayout mContent;
    private String mNormalStr, mRefeshingStr;

    private int mState = STATE_NORMAL;

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;

    private float mContainerHeight = dp2px(getResources(), 100);
    private float mContainerWidth = dp2px(getResources(), 90);
    private float mContainerBoundary = dp2px(getResources(), 50);

    private int header_hintType = 0;     //下拉显示文字方式

    /**
     * 设置下拉刷新文字
     *
     * @param normalStr
     * @param refreshingStr
     */
    public void setHeaderStr(String normalStr, String refreshingStr) {
        this.mNormalStr = normalStr;
        if (mHintTextView != null)
            mHintTextView.setText(normalStr);
        this.mRefeshingStr = refreshingStr;
    }

    public void setHeaderHintType(int type) {
        this.header_hintType = type;
        if (header_hintType == 1) {
            mHintTextView.setText(R.string.xlistview_header_hint_normal_a);
        }
    }

    public XListViewHeaderExt(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public XListViewHeaderExt(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        // 初始情况，设置下拉刷新view高度为0
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.xlistview_header_ext, null);
        addView(mContainer, lp);
        setGravity(Gravity.BOTTOM);

        mHintTextView = (TextView) findViewById(R.id.xlistview_header_hint_textview);
        mHeartContainer = (FrameLayout) findViewById(R.id.xlistview_header_heart_container);
        mHeartContainerExt = (FrameLayout) findViewById(R.id.xlistview_header_heart_ext);
        mContent = (RelativeLayout) findViewById(R.id.xlistview_header_content);
    }

    public void setState(int state) {
//        if (state == mState) return;

        if (state == STATE_REFRESHING) {
            mHeartContainer.setVisibility(View.INVISIBLE);
            mHeartContainerExt.setVisibility(View.VISIBLE);
        } else {
            mHeartContainer.setVisibility(View.VISIBLE);
            mHeartContainerExt.setVisibility(View.INVISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                if (mNormalStr != null) {
                    mHintTextView.setText(mNormalStr);
                } else {
                    if (header_hintType == 1) {
                        mHintTextView.setText(R.string.xlistview_header_hint_normal_a);
                    } else {
                        mHintTextView.setText(R.string.xlistview_header_hint_normal);
                    }
                }
                break;
            case STATE_READY:
                break;
            case STATE_REFRESHING:
                if (mRefeshingStr != null) {
                    mHintTextView.setText(mRefeshingStr);
                } else {
                    if (header_hintType == 1) {
                        mHintTextView.setText(R.string.xlistview_header_hint_loading_a);
                    } else {
                        int gender = ModuleMgr.getCenterMgr().getMyInfo().getGender();
                        if (gender == 2) {
                            mHintTextView.setText(R.string.xlistview_header_hint_loading_female);
                        } else {
                            mHintTextView.setText(R.string.xlistview_header_hint_loading_male);
                        }
                    }
                }
                break;
            default:
        }

        mState = state;
    }

    public void setVisiableHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);

        height = (int) (height / 1.5);
        RelativeLayout.LayoutParams rrlp = (RelativeLayout.LayoutParams) mHeartContainer.getLayoutParams();
        if (height <= mContainerBoundary && height > 0) {
            rrlp.width = (int) (mContainerWidth - height);
            rrlp.height = (int) (mContainerHeight - height);
        } else if (height > mContainerBoundary) {
            rrlp.width = (int) (mContainerWidth - mContainerBoundary);
            rrlp.height = (int) (mContainerHeight - mContainerBoundary);
        }
        mHeartContainer.setLayoutParams(rrlp);
    }

    public int getVisiableHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.height;
    }

    public float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public RelativeLayout getContent() {
        return mContent;
    }
}
