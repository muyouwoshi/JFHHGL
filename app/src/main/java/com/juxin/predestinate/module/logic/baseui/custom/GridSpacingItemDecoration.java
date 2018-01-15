package com.juxin.predestinate.module.logic.baseui.custom;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 给RecyclerView布局管理器GridLayoutManager设置间距
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {


    private int spanCount;
    private int rowSpacing, columnSpacing;

    /**
     * @param spanCount 每行个数
     * @param rowSpacing 行间距
     * @param columnSpacing 列间距
     */
    public GridSpacingItemDecoration(int spanCount, int rowSpacing, int columnSpacing) {
        this.spanCount = spanCount;
        this.rowSpacing = rowSpacing;
        this.columnSpacing = columnSpacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //view的位置
        int position = parent.getChildAdapterPosition(view);
        //总行数
        int column = position % spanCount;
        //计算左边距
        outRect.left = column * rowSpacing / spanCount;
        //计算右边距
        outRect.right = rowSpacing - (column + 1) * rowSpacing / spanCount;
        if (position >= spanCount) {
            //设置行间距
            outRect.top = columnSpacing;
        }
    }


}
