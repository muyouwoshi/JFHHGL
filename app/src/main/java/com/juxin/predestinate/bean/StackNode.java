package com.juxin.predestinate.bean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;

/**
 * @author Mr.Huang
 * @date 2017/9/8
 * 用于记录Activity路径栈
 * <b>需要注意的是一个Activity中必须只能显示一个Fragment</b>
 */
public class StackNode implements Parcelable {

    public static final String EXTRA_KEY = "stack";
    public String stack;
    public StackNode previous;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.stack);
        dest.writeParcelable(this.previous, flags);
    }

    public StackNode() {
    }

    protected StackNode(Parcel in) {
        this.stack = in.readString();
        this.previous = in.readParcelable(StackNode.class.getClassLoader());
    }

    public static final Parcelable.Creator<StackNode> CREATOR = new Parcelable.Creator<StackNode>() {
        @Override
        public StackNode createFromParcel(Parcel source) {
            return new StackNode(source);
        }

        @Override
        public StackNode[] newArray(int size) {
            return new StackNode[size];
        }
    };

    public static void appendIntent(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            intent.putExtra(StackNode.EXTRA_KEY, activity.getStackNode());
        } else {
            Activity activity = App.activity;
            if (activity != null && activity instanceof BaseActivity) {
                intent.putExtra(StackNode.EXTRA_KEY, ((BaseActivity) activity).getStackNode());
            }
        }
    }

    public static StackNode getStackNode(FragmentActivity fragmentActivity) {
        StackNode node = null;
        if (fragmentActivity instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) fragmentActivity;
            node = activity.getStackNode();
        } else {
            Activity activity = App.activity;
            if (activity != null && activity instanceof BaseActivity) {
                node = ((BaseActivity) activity).getStackNode();
            }
        }
        return node;
    }

    public static String getName(Object o) {
        if (o != null) {
            return o.getClass().getName();
        }
        return null;
    }

    public static String getName(Class clazz) {
        if (clazz != null) {
            return clazz.getName();
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof StackNode) {
            return this.stack.equals(((StackNode) obj).stack);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        printNode(sb, this);
        return sb.toString();
    }

    private static void printNode(StringBuilder sb, StackNode node) {
        sb.append("-> [").append(node.stack != null ? node.stack : "NULL").append("] ");
        if (node.previous != null) {
            printNode(sb, node.previous);
        }
    }

    public boolean withNode(Class node){
        return withNode(node.getName());
    }

    private boolean withNode(String name){
        if(this.stack .equals( name)) return true;
        if(this.previous == null) return false;
            return previous.withNode(name);
    }
}
