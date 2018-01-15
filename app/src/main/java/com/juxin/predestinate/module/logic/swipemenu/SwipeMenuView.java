package com.juxin.predestinate.module.logic.swipemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.juxin.predestinate.R;

public class SwipeMenuView extends LinearLayout implements OnClickListener {
    private int position;

    private SwipeMenu mSwipeMenu;
    private View mContentView;
    private OnSwipeMenuViewClickListener mSwipeMenuViewClickListener;


    public SwipeMenuView(SwipeMenu swipeMenu) {
        this(swipeMenu.getContext());
        this.mSwipeMenu = swipeMenu;
        init();
    }

    private SwipeMenuView(Context context) {
        this(context, null);
    }

    public SwipeMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        LayoutParams params = new LayoutParams(dp2px(70), this.mSwipeMenu.getViewHeight());
        params.gravity = Gravity.BOTTOM;
        LinearLayout parent = new LinearLayout(getContext());
        parent.setGravity(Gravity.CENTER);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setBackgroundColor(getResources().getColor(R.color.theme_color_red));
        parent.setLayoutParams(params);
        parent.setOnClickListener(this);
        addView(parent);

        parent.addView(createTitle());
    }

    private TextView createTitle() {
        TextView tv = new TextView(getContext());
        tv.setText(mSwipeMenu.getTitle());
        tv.setTextSize(mSwipeMenu.getTitleSize());
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(mSwipeMenu.getTitleColor());
        return tv;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onClick(View v) {
        if (mSwipeMenuViewClickListener != null) {
            mSwipeMenuViewClickListener.onSwipeMenuViewClick(position, mSwipeMenu, mContentView);
        }
    }

    public interface OnSwipeMenuViewClickListener {
        void onSwipeMenuViewClick(int position, SwipeMenu swipeMenu, View contentView);
    }

    public void setSwipeMenuViewClickListener(OnSwipeMenuViewClickListener swipeMenuViewClickListener) {
        this.mSwipeMenuViewClickListener = swipeMenuViewClickListener;
    }

    public void setContentView(View contentView) {
        this.mContentView = contentView;
    }
}
