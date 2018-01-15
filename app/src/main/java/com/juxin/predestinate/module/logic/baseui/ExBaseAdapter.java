package com.juxin.predestinate.module.logic.baseui;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 通用adapter基类，继承自BaseAdapter，内部实现了BaseAdapter的部分共用方法
 * Created by Kind on 16/2/26.
 */
public abstract class ExBaseAdapter<T> extends BaseAdapter {

    private Context mContext = null;
    private List<T> datas = null;

    public ExBaseAdapter(Context context, List<T> datas) {
        this.mContext = context;
        this.datas = datas;
    }

    public void setList(List<T> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void setListNonotify(List<T> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return datas;
    }

    public void addList(List<T> datas) {
        if (this.datas == null) {
            this.datas = datas;
        } else if (datas != null) {
            this.datas.addAll(datas);
        }
        notifyDataSetChanged();
    }

    public void addListFirst(List<T> datas) {
        if (this.datas == null) {
            this.datas = datas;
        } else if (datas != null) {
            this.datas.addAll(0, datas);
        }
        notifyDataSetChanged();
    }

    public void setList(List<T> datas, boolean nextPage) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public View inflate(int resource) {
        return LayoutInflater.from(mContext).inflate(resource, null);
    }

    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        return LayoutInflater.from(mContext).inflate(resource, root, attachToRoot);
    }


    public Resources getResources() {
        return mContext.getResources();
    }

    public Context getContext() {
        return mContext;
    }

    public boolean isEmpty() {
        return datas == null ? true : datas.isEmpty();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public T getItem(int position) {
        if (datas == null) {
            return null;
        }

        if (position < 0 || position >= datas.size()) {
            return null;
        }

        return datas.get(position);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

}

