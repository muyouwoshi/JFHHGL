package com.juxin.predestinate.ui.user.my.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.GiftsNumInfo;
import com.juxin.predestinate.ui.user.my.adapter.GiftNumAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zm on 2016/12/8
 */
public class GiftPopView extends LinearLayout {

    private Context mContext;
    private RecyclerView rlvList;
    private List<GiftsNumInfo> arrGiftNumInfos = new ArrayList<>();
    private GiftNumAdapter mGiftNumAdapter;
    private int viewWidth = 0;
    private int viewHeight = 0;
    private OnNumSelectedChangedListener mListener;

    private int[] nums = new int[]{1314, 520, 188, 66, 30, 10, 1};
    private String[] morals = new String[]{"一生一世", "我爱你", "要抱抱", "一切顺利", "想你", "十全十美", "一心一意"};

    public GiftPopView(Context context) {
        this(context, null);
    }

    public GiftPopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GiftPopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView(context);
        this.setVisibility(View.INVISIBLE);
    }

    //初始化
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.f1_bottom_gift_popup_view, this);
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rlvList = (RecyclerView) findViewById(R.id.gift_pupup_rlv_list);
        rlvList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rlvList.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL_LIST, R.drawable.p1_decoration_px1));
    }

    private void initGiftsNum() {
        for (int i = 0; i < morals.length; i++) {
            GiftsNumInfo info = new GiftsNumInfo();
            info.setNum(nums[i]);
            info.setMoral(morals[i]);
            arrGiftNumInfos.add(info);
        }
        mGiftNumAdapter.setList(arrGiftNumInfos);
    }

    private int getViewWidth() {
        return viewWidth;
    }

    private int getViewHeight() {
        return viewHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (viewWidth == 0 && viewHeight == 0 && getMeasuredHeight() > 0 && getMeasuredWidth() > 0) {
            viewWidth = getMeasuredWidth() * 479 / 531;
            viewHeight = (getMeasuredHeight() * 840 / 907) / nums.length;
            mGiftNumAdapter = new GiftNumAdapter(getContext(), null, viewWidth, viewHeight);
            rlvList.setAdapter(mGiftNumAdapter);
            initGiftsNum();
            mGiftNumAdapter.setOnNumSelectedChanged(mListener);
        }
    }

    public void setOnNumSelectedChanged(OnNumSelectedChangedListener mListener) {
        this.mListener = mListener;
        if (mGiftNumAdapter != null)
            mGiftNumAdapter.setOnNumSelectedChanged(mListener);
    }

    public interface OnNumSelectedChangedListener {
        void onNumSelectedChanged(int num);
    }
}
