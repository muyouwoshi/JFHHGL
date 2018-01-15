package com.juxin.predestinate.ui.user.my.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zm on 2017/7/21.
 */

public class MultiImageView extends ImageView {

    private List<Integer> imageIds;
    private List<Bitmap> bitmapList = new ArrayList<>();
    private int desWidth = 0;
    private int widthFrist = 10;
    private int widthTwo = 10;
    private int heightFrist = 10;
    private int heightTwo = 14;
    private Bitmap bmp;

    public MultiImageView(Context context) {
        this(context, null);
    }

    public MultiImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bmp = ((BitmapDrawable) (getContext().getResources().getDrawable(R.drawable.f2_img_chenghao))).getBitmap();
    }

    public void setImageIds(List<Integer> imageIds) {
        this.imageIds = imageIds;
        bitmapList.clear();
        if (getLayoutParams() != null) {
            getLayoutParams().width = desWidth + UIUtil.dip2px(App.context, imageIds.size() * widthFrist + 12);
            getLayoutParams().height = UIUtil.dip2px(App.context, heightTwo + 4);
        }
        for (int i = 0; i < imageIds.size(); i++) {
            bitmapList.add(((BitmapDrawable) (getContext().getResources().getDrawable(imageIds.get(i)))).getBitmap());
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect girlDesRect;
        desWidth = 0;
        if (imageIds != null) {
            desWidth = UIUtil.dip2px(App.context, widthFrist);
            girlDesRect = new Rect(0, UIUtil.dip2px(App.context, (heightTwo - heightFrist) / 2), UIUtil.dip2px(App.context, widthFrist), UIUtil.dip2px(App.context, heightFrist) + UIUtil.dip2px(App.context, (heightTwo - heightFrist) / 2));
            canvas.drawBitmap(bmp, null, girlDesRect, null);
            for (int i = 0; i < imageIds.size() && i < bitmapList.size(); i++) {
                int oldWidth = desWidth;
                desWidth += UIUtil.dip2px(App.context, widthTwo);
                girlDesRect = new Rect(oldWidth, 0, desWidth, UIUtil.dip2px(App.context, heightTwo));
                canvas.drawBitmap(bitmapList.get(i), null, girlDesRect, null);
            }
        } else {
            super.onDraw(canvas);
        }
    }
}
