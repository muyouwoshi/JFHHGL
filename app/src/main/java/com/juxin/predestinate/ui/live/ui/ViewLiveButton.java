package com.juxin.predestinate.ui.live.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.predestinate.R;

/**
 * 用户资料底部 icon+文字
 * <p>
 * Created by chengxiaobo on 2017/9/11.
 */

public class ViewLiveButton extends RelativeLayout {

    private String mText = "";
    private Drawable mDrawable = null;
    private int mColor = 0xfafafa;
    private boolean mShowline = true;

    public ViewLiveButton(Context context) {
        super(context);

        initView(context);
    }

    public ViewLiveButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
        initView(context);
    }

    public ViewLiveButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
        initView(context);
    }

    /**
     * 初始化
     */
    private void init(Context context, AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LiveUserCardIcon);
        mText = ta.getString(R.styleable.LiveUserCardIcon_textString);
        mDrawable = ta.getDrawable(R.styleable.LiveUserCardIcon_icon);
        mColor = ta.getColor(R.styleable.LiveUserCardIcon_textColor, 0xfafafa);
        mShowline = ta.getBoolean(R.styleable.LiveUserCardIcon_showLine, true);
        ta.recycle();

    }

    /**
     * 初始化View
     */
    private void initView(Context context) {

        LayoutInflater.from(context).inflate(R.layout.live_user_card_icon, this);

        ImageView imageView = (ImageView) findViewById(R.id.iv_icon);
        if (mDrawable != null) {
            imageView.setImageDrawable(mDrawable);
        }

        TextView textView = (TextView) findViewById(R.id.tv_text);
        textView.setTextColor(mColor);
        textView.setText(mText);

        View viewline = findViewById(R.id.view_line);
        if (mShowline) {
            viewline.setVisibility(View.VISIBLE);
        } else {
            viewline.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏刻痕线
     */
    public void dissmissLine() {

        View viewline = findViewById(R.id.view_line);
        viewline.setVisibility(View.GONE);
    }
}
