package com.juxin.predestinate.ui.user.my.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.juxin.predestinate.module.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zm on 2017/5/10.
 * <p>
 * 页码指示器类，获得此类实例后，可通过{@link PageIndicatorView#initIndicator(int)}方法初始化指示器
 * 参考调用顺序 setUnselectDot，setSelectDot，initIndicator
 * pageCount 发生变化时需重新调用initIndicator方法
 */

public class PageIndicatorView extends LinearLayout {

    private Context mContext = null;
    private int unSelectDotWidth = 8;//dp
    private int unSelectDotHeight = 8;//dp
    private int selectDotWidth = 12;//dp
    private int selectDotHeight = 8;//dp
    private int margin = 6;//dp
    private int pageCount = -1;//页面个数
    private int unSelectResource = android.R.drawable.presence_invisible;
    private int selectResource = android.R.drawable.presence_online;
    private View selectDot;
    private List<View> indicatorViews = new ArrayList<>(); // 存放指示器


    public PageIndicatorView(Context context) {
        this(context, null);
    }

    public PageIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        initSize();
    }

    //初始化指示器
    private void initDot(){
        if (pageCount > 1){
            this.removeAllViews();
            if (selectDot ==null){
                selectDot = new View(mContext);
            }
            LayoutParams params = new LayoutParams(selectDotWidth,selectDotHeight);
            params.leftMargin = margin;
            params.rightMargin = margin;
            selectDot.setLayoutParams(params);
            selectDot.setBackgroundResource(selectResource);
            this.addView(selectDot);
            pageCount--;
            pageCount = pageCount - indicatorViews.size();
            for (int i = 0 ;i < pageCount;i++){
                View view = new View(mContext);
                indicatorViews.add(view);
            }
            if (pageCount < 0 ){
                pageCount = -pageCount;//负数转正
                for (int i = 0 ;i < pageCount;i++){
                    indicatorViews.remove(0);//移除多余的view
                }
            }
            int size = indicatorViews.size();
            for (int i = 0 ;i < size;i++){
                LayoutParams param = new LayoutParams(unSelectDotWidth,unSelectDotHeight);
                param.leftMargin = margin;
                param.rightMargin = margin;
                indicatorViews.get(i).setLayoutParams(param);
                indicatorViews.get(i).setBackgroundResource(unSelectResource);
                this.addView(indicatorViews.get(i));
            }
        }
    }

    /**
     * 设置未选中dot(单位dp)
     *
     * @param unSelectDotWidth  宽度
     * @param unSelectDotHeight 高度
     * @param unSelectResource  背景
     */
    public void setUnselectDot(int unSelectDotWidth,int unSelectDotHeight,int unSelectResource){
        this.unSelectDotWidth = unSelectDotWidth;
        this.unSelectDotHeight = unSelectDotHeight;
        this.unSelectResource = unSelectResource;
    }

    /**
     * 设置选中的dot(单位dp)
     *
     * @param selectDotWidth   宽度
     * @param selectDotHeight  高度
     * @param selectResource   背景
     */
    public void setSelectDot(int selectDotWidth,int selectDotHeight,int selectResource){
        this.selectDotWidth = selectDotWidth;
        this.selectDotHeight = selectDotHeight;
        this.selectResource = selectResource;
    }

    /**
     * 设置间隔（单位dp）默认间隔为6dp
     */
    public void setMargin(int margin){
        this.margin = margin;
    }

    //初始化指示器尺寸
    private void initSize(){
        unSelectDotWidth = UIUtil.dip2px(mContext, unSelectDotWidth);
        unSelectDotHeight = UIUtil.dip2px(mContext, unSelectDotHeight);
        selectDotWidth = UIUtil.dip2px(mContext, selectDotWidth);
        selectDotHeight = UIUtil.dip2px(mContext, selectDotHeight);
        margin = UIUtil.dip2px(mContext, margin)/2;
        initDot();
    }

    /**
     * 初始化指示器，默认选中第一页
     *
     * @param count 指示器数量，即页数
     */
    public void initIndicator(int count) {
        pageCount = count;
        initSize();
    }

    /**
     * 设置选中页
     *
     * @param selected 页下标，从0开始
     */
    public void setSelectedPage(int selected) {
        if (selected >= 0 && selected < indicatorViews.size()+1){
            this.removeView(selectDot);
            this.addView(selectDot,selected);
        }
    }
}