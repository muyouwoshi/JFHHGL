package com.juxin.predestinate.ui.live.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.predestinate.R;

/**
 * 提示是否在直播的小图标控件
 * Created by Administrator on 2017/7/21.
 */
public class LiveStateView extends LinearLayout {
    private boolean isLiving;
    private ImageView infoImage;
    private TextView infoText;
    private TextView introduceText;
    private String introduceStr;

    public LiveStateView(Context context) {
        this(context, null);
    }

    public LiveStateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initState();

    }

    private void initState() {
        switchState(isLiving);
        setIntroduceText(introduceStr);
    }

    private void initView(Context context) {
        inflate(context, R.layout.live_state_view, this);
        infoImage = (ImageView) findViewById(R.id.live_info_image);
        infoText = (TextView) findViewById(R.id.live_info_text);
        introduceText = (TextView) findViewById(R.id.live_info_introduce);
    }

    /**
     * 设置直播状态
     * @param isLiving  true 正在直播 false直播结束
     *                   true 显示 false不显示
     */
    public void setLiveState(boolean isLiving) {
        this.isLiving = isLiving;
        switchState(isLiving);
    }

    private void switchState(boolean isLiving) {
        if (infoImage == null) return;

        AnimationDrawable animationDrawable = (AnimationDrawable) ((ImageView) findViewById(R.id.live_info_image)).getDrawable();
        if (isLiving) {
            animationDrawable.start();
            infoText.setText(getContext().getString(R.string.living));
            this.setVisibility(VISIBLE);
            return;
        }
        this.setVisibility(View.GONE);
        animationDrawable.stop();
        infoText.setText("直播结束");
    }

    /**
     * 设置直播介绍（标题）
     * @param string
     */
    public void setIntroduceText(String string){
        introduceStr = string;
        if(introduceText != null) introduceText.setText(string);
    }
}
