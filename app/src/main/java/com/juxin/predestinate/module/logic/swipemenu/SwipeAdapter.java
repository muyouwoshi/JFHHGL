package com.juxin.predestinate.module.logic.swipemenu;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;
import com.juxin.predestinate.ui.mail.item.MailItemType;

public class SwipeAdapter implements WrapperListAdapter, SwipeChooseView.OnChooseCheckedChangeListener,
        SwipeMenuView.OnSwipeMenuViewClickListener {
    private Context mContext;
    private ListAdapter mAdapter;

    private boolean[] mChooseCheckedArr;

    public SwipeAdapter(Context context, ListAdapter adapter) {
        mContext = context;
        mAdapter = adapter;
    }

    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public int getCount() {
        if (mChooseCheckedArr == null || mChooseCheckedArr.length != mAdapter.getCount()) {
            mChooseCheckedArr = new boolean[mAdapter.getCount()];
        }
        return mAdapter.getCount();
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SwipeListView swipeListView = (SwipeListView) parent;
        boolean isOpenChooseView = swipeListView.isOpenChooseView();
        boolean isItemViewType = mAdapter.getItemViewType(position) != MailItemType.Mail_Item_Title.type
                && mAdapter.getItemViewType(position) != MailItemType.Mail_Item_Title_TWO.type
                && mAdapter.getItemViewType(position) != MailItemType.Mail_Item_BlankBar.type
                && mAdapter.getItemViewType(position) != MailItemType.Mail_Item_Bottom.type
                && mAdapter.getItemViewType(position) != MailItemType.Mail_Item_Other.type
                && mAdapter.getItemViewType(position) != MailItemType.Mail_Item_Greet.type;

        SwipeLayout swipeLayout;
        if (convertView == null) {
            View contentView = mAdapter.getView(position, convertView, parent);
            // 创建MenuView
            SwipeMenu swipeMenu = new SwipeMenu();
            swipeMenu.setContext(mContext);
            swipeMenu.setViewType(mAdapter.getItemViewType(position));
            createMenu(swipeMenu);
            swipeLayout = new SwipeLayout(contentView, new SwipeMenuView(swipeMenu),
                    new SwipeChooseView(mContext), (isOpenChooseView && isItemViewType));
            swipeLayout.setIsShowChooseView(isItemViewType);
        } else {
            swipeLayout = (SwipeLayout) convertView;
            //170825 UPDATE START 修复当改变item大小后，整个item大小异常的问题
            //mAdapter.getView(position, swipeLayout.getContentView(), parent);

            View adapterView = mAdapter.getView(position, swipeLayout.getContentView(), parent);
            swipeLayout.changeContentView(adapterView);
            swipeLayout.measure(adapterView.getMeasuredWidthAndState(), adapterView.getMeasuredHeightAndState());
            //170825 UPDATE END 修复当改变item大小后，整个item大小异常的问题
            swipeLayout.setIsShowChooseView(isItemViewType);
            // 判断是否需要打开ChooseView
            if (isOpenChooseView ) {
                swipeLayout.openChoose();
            } else {
                swipeLayout.closeChoose();
            }
        }

        SwipeChooseView chooseView = (SwipeChooseView) swipeLayout.getChooseView();
        chooseView.setChecked(mChooseCheckedArr[position]);
        chooseView.setPosition(position);
        chooseView.setCheckedChangeListener(this);
        SwipeMenuView menuView = (SwipeMenuView) swipeLayout.getMenuView();
        menuView.setPosition(position);
        menuView.setSwipeMenuViewClickListener(this);
        menuView.setContentView(swipeLayout.getContentView());
        return swipeLayout;
    }

    public void createMenu(SwipeMenu swipeMenu) {}

    @Override
    public boolean areAllItemsEnabled() {
        return mAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return mAdapter.isEnabled(position);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean hasStableIds() {
        return mAdapter.hasStableIds();
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return mAdapter.isEmpty();
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

    @Override
    public void onChooseCheckedChanged(int position, boolean isChecked) {
        mChooseCheckedArr[position] = isChecked;
    }

    public void clearAllChooseChecked() {
        for (int i = 0; i < mChooseCheckedArr.length; i++) {
            mChooseCheckedArr[i] = false;
        }
    }

    public void selectAllChooseView() {
        for (int i = 0; i < mChooseCheckedArr.length; i++) {
            mChooseCheckedArr[i] = true;
        }
    }

    public void selectChooseView(int position, boolean isCheck) {
        if(mChooseCheckedArr == null || position >= mChooseCheckedArr.length || position < 0) return;
        mChooseCheckedArr[position] = isCheck;
    }

    @Override
    public void onSwipeMenuViewClick(int position, SwipeMenu swipeMenu, View contentView) {

    }
}
