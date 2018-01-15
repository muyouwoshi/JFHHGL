package com.juxin.library.controls.smarttablayout;

import android.view.View;

/**
 * Created by Kind on 16/10/14.
 */

public class PagerItem {

    private int id;             //标识
    private String title;
    private View view;

    public PagerItem() {
        super();
    }

    public PagerItem(String title, View view) {
        this.title = title;
        this.view = view;
    }

    public PagerItem(int id, String title, View view) {
        this.id = id;
        this.title = title;
        this.view = view;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
