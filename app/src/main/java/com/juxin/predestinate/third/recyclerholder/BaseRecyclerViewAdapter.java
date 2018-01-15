package com.juxin.predestinate.third.recyclerholder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Kind on 2017/3/20.
 */

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext = null;
    private List<T> datas = null;
    private BaseRecyclerViewHolder.OnItemClickListener onItemClickListener;
    private BaseRecyclerViewHolder.OnItemLongClickListener onItemLongClickListener;


    public BaseRecyclerViewAdapter(Context context, List<T> datas) {
        this.mContext = context;
        this.datas = datas;
    }

    public Resources getResources() {
        return mContext.getResources();
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public int getListSize() {
        return datas == null ? 0 : datas.size();
    }

    public T getItem(int position) {
        if (datas == null) {
            return null;
        }

        if (position < 0 || position >= datas.size()) {
            return null;
        }

        return datas.get(position);
    }

    public T getItemByPosition(int position) {
        return this.getItem(position);
    }

    public void setList(List<T> mList) {
        this.datas = mList;
        notifyDataSetChanged();
    }

    public void addList(List<T> datas) {
        if (this.datas == null) {
            this.datas = datas;
        } else if (datas != null) {
            this.datas.addAll(datas);
        }
        notifyDataSetChanged();

        // notifyItemInserted(this.datas.size());
    }

    public void clear() {
        if (datas != null) datas.clear();
    }

    public void remove(Object o) {
        if (datas != null) datas.remove(o);

    }

    public List<T> getList() {
        return this.datas;
    }

    /**
     * Please return RecyclerView loading layout Id array
     * 请返回RecyclerView加载的布局Id数组
     *
     * @return 布局Id数组
     */
    public abstract int[] getItemLayouts();

    /**
     * butt joint the onBindViewHolder and
     * If you want to write logic in onBindViewHolder, you can write here
     * 对接了onBindViewHolder
     * onBindViewHolder里的逻辑写在这
     *
     * @param viewHolder viewHolder
     * @param position   position
     */
    public abstract void onBindRecycleViewHolder(BaseRecyclerViewHolder viewHolder, int position);

    /**
     * Please write judgment logic when more layout
     * and not write when single layout
     * 如果是多布局的话，请写判断逻辑
     * 单布局可以不写
     *
     * @param position Item position
     * @return 布局Id数组中的index
     */
    public abstract int getRecycleViewItemType(int position);


    /**
     * get the itemType by position
     * 根据position获取ItemType
     *
     * @param position Item position
     * @return 默认ItemType等于0
     */
    @Override
    public int getItemViewType(int position) {
        return this.getRecycleViewItemType(position);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            BaseRecyclerViewHolder easyRecyclerViewHolder = (BaseRecyclerViewHolder) holder;
            this.onBindRecycleViewHolder(easyRecyclerViewHolder, position);
            easyRecyclerViewHolder.setOnItemClickListener(this.onItemClickListener, position);
            easyRecyclerViewHolder.setOnItemLongClickListener(this.onItemLongClickListener,
                    position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType < 0) return null;
        if (this.getItemLayouts() == null) return null;
        int[] layoutIds = this.getItemLayouts();
        if (layoutIds.length < 1) return null;

        int itemLayoutId;
        if (layoutIds.length == 1) {
            itemLayoutId = layoutIds[0];
        } else {
            itemLayoutId = layoutIds[viewType];
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return new BaseRecyclerViewHolder(view);
    }

    /**
     * set the on item click listener
     * 设置点击事件
     *
     * @param onItemClickListener onItemClickListener
     */
    public void setOnItemClickListener(BaseRecyclerViewHolder.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    /**
     * set the on item long click listener
     * 设置长点击事件
     *
     * @param onItemLongClickListener onItemLongClickListener
     */
    public void setOnItemLongClickListener(BaseRecyclerViewHolder.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
}
