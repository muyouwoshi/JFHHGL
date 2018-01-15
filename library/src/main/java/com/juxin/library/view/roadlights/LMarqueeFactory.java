package com.juxin.library.view.roadlights;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kind on 2017/1/6.
 */

public abstract class LMarqueeFactory<T extends View, E> {
    protected Context mContext;
    protected OnItemClickListener onItemClickListener;
    protected List<T> mViews;
    protected List<E> datas;
    private boolean isOnItemClickRegistered;
    private LMarqueeView lMarqueeView;
    private int viewCount = 2;
    private int currentDataIndex = 2;
    private int currentViewIndex = 0;

    public LMarqueeFactory(Context mContext) {
        this.mContext = mContext;
    }

    public abstract T generateMarqueeItemView(E data, T convertView);

    public void setData(List<E> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        this.datas = datas;
        mViews = new ArrayList<>();
        for (int i = 0; i < viewCount && i < datas.size(); i++) {
            E data = datas.get(i);
            T mView = generateMarqueeItemView(data, null);
            mViews.add(mView);
        }
        registerOnItemClick();
        if (lMarqueeView != null) {
            lMarqueeView.setMarqueeFactory(this);
        }
    }

    public View inflate(int resource) {
        return LayoutInflater.from(mContext).inflate(resource, null);
    }

    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        return LayoutInflater.from(mContext).inflate(resource, root, attachToRoot);
    }

    public void setOnItemClickListener(OnItemClickListener<T, E> mOnItemClickListener) {
        this.onItemClickListener = mOnItemClickListener;
        registerOnItemClick();
    }

    public List<T> getMarqueeViews() {
        return mViews;
    }

    public void setItemData() {
        if (currentDataIndex >= datas.size()) {
            currentDataIndex = 0;
        }

        if (currentViewIndex >= mViews.size() || currentViewIndex < 0) {
            currentViewIndex = 0;
        }
        if (currentViewIndex >= mViews.size()) {
            mViews.add(generateMarqueeItemView(datas.get(currentDataIndex++), null));
        }

        if (datas.size() > 0) {
            generateMarqueeItemView(datas.get(currentDataIndex++), mViews.get(currentViewIndex++));
        }
    }

    private void registerOnItemClick() {
        if (!isOnItemClickRegistered && onItemClickListener != null && datas != null) {
            for (int i = 0; i < datas.size(); i++) {
                T mView = mViews.get(i);
                E data = datas.get(i);
                mView.setTag(new ViewHolder(mView, data, i));
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onItemClickListener((ViewHolder<T, E>) view.getTag());
                    }
                });
            }
            isOnItemClickRegistered = true;
        }
    }

    public interface OnItemClickListener<V extends View, E> {
        void onItemClickListener(ViewHolder<V, E> holder);
    }

    public static class ViewHolder<V extends View, P> {
        public V mView;
        public P data;
        public int position;

        public ViewHolder(V mView, P data, int position) {
            this.mView = mView;
            this.data = data;
            this.position = position;
        }
    }

    public void setAttachedToMarqueeView(LMarqueeView marqueeView) {
        this.lMarqueeView = marqueeView;
    }
}

