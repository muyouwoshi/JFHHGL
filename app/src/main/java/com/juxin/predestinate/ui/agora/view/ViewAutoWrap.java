package com.juxin.predestinate.ui.agora.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.predestinate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 流式布局：可设置单选，多选
 * <p>
 * Created by cheng on 2017/7/18.
 */
public class ViewAutoWrap extends ViewGroup {
    private int wSpacing = 10;
    private int hSpacing = 10;

    private List<String> list;

    private TextView mLastSelectedTextView;
    private boolean mIsSingleSelected;

    public ViewAutoWrap(Context context) {
        super(context);
    }

    public ViewAutoWrap(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(List<String> list, Context context, boolean isSingleSelected) {
        if(list == null) return;
        this.list = list;
        mIsSingleSelected = isSingleSelected;

        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        float density = dm.density;

        wSpacing = (int) (density * 10);
        hSpacing = (int) (density * 10);

        int padding = (int) (density * 13);
        int paddingTop = (int) (density * 8);

        for (int i = 0; i < list.size(); i++) {
            final TextView textView = new TextView(context);
            textView.setPadding(padding, 0, padding, 0);
            textView.setHeight((int) (density * 23));
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundResource(R.drawable.bg_comment_content);
            textView.setText(list.get(i));
            textView.setTextColor(Color.parseColor("#c9c9c9"));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);

            textView.setTag(false);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView textView1 = (TextView) view;
                    boolean isChecked = (boolean) view.getTag();
                    if (mIsSingleSelected) {

                        if (mLastSelectedTextView != null) {
                            mLastSelectedTextView.setBackgroundResource(R.drawable.bg_comment_content);
                            mLastSelectedTextView.setTextColor(Color.parseColor("#c9c9c9"));
                            mLastSelectedTextView.setTag(false);
                        }

                        textView1.setBackgroundResource(R.drawable.bg_comment_content_selected);
                        textView1.setTextColor(Color.parseColor("#fd7294"));
                        textView1.setTag(true);
                        mLastSelectedTextView = textView1;

                    } else {
                        if (isChecked) {
                            textView1.setBackgroundResource(R.drawable.bg_comment_content);
                            textView1.setTextColor(Color.parseColor("#c9c9c9"));
                        } else {
                            textView1.setBackgroundResource(R.drawable.bg_comment_content_selected);
                            textView1.setTextColor(Color.parseColor("#fd7294"));
                        }
                        textView1.setTag(!isChecked);
                    }
                }
            });
            this.addView(textView);
        }
    }

    public List<String> getSelectedString() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < getChildCount(); i++) {
            TextView tv = (TextView) getChildAt(i);
            if ((Boolean) tv.getTag()) {
                list.add(tv.getText().toString());
            }
        }
        return list;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();
        int x = 0;
        int y = 0;
        int viewHeight = 0;
        for (int index = 0; index < childCount; index++) {
            final View child = getChildAt(index);
            if (child.getVisibility() != View.GONE) {
                child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                // 此处增加onlayout中的换行判断，用于计算所需的高度
                int width = child.getMeasuredWidth();
                int height = viewHeight = child.getMeasuredHeight();
                x += width + wSpacing;
                if (x - wSpacing > maxWidth) {
                    x = width + wSpacing;
                    y = y + height + hSpacing;
                }
            }
        }
        // 设置容器所需的宽度和高度
        setMeasuredDimension(maxWidth, y + viewHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        int maxWidth = r - l;
        int x = 0;
        int y = 0;
        int viewHeight = 0;
        for (int i = 0; i < childCount; i++) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int width = child.getMeasuredWidth();
                int height = viewHeight = child.getMeasuredHeight();
                x += width + wSpacing;
                if (x - wSpacing > maxWidth) {
                    x = width + wSpacing;
                    y = y + height + hSpacing;
                }
                child.layout(x - width - wSpacing, y, x - wSpacing, y + viewHeight);
//                child.measure(width, height);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }
}
