package com.juxin.predestinate.ui.user.my.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.util.UIUtil;

/**
 * Created by zm on 2016/12/8
 */
public class ContourTextView extends TextView {

    private TextView borderText = null;///用于描边的TextView

    public ContourTextView(Context context) {
        this(context, null);
    }

    public ContourTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContourTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        borderText = new TextView(context, attrs, defStyleAttr);
        initView();
    }

    //初始化
    public void initView() {
        TextPaint tp1 = borderText.getPaint();
        tp1.setStrokeWidth(UIUtil.dp2px(1)); //设置描边宽度
        tp1.setStyle(Paint.Style.STROKE); //对文字只描边
        borderText.setTextColor(getResources().getColor(R.color.theme_color_red)); //设置描边颜色
        borderText.setGravity(getGravity());
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        borderText.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence tt = borderText.getText();

        //两个TextView上的文字必须一致
        if (tt == null || !tt.equals(this.getText())) {
            borderText.setText(getText());
            this.postInvalidate();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        borderText.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        borderText.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        borderText.draw(canvas);
        super.onDraw(canvas);
    }
}
