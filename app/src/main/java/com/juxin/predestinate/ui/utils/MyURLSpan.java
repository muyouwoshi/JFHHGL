package com.juxin.predestinate.ui.utils;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

/**
 * 对TextView设置显示文字及html展示点击行为
 * Created by siow on 2017/5/8.
 */
public class MyURLSpan extends ClickableSpan {

    private CMDJumpUtil cmdJumpUtil;

    private MyURLSpan(Context mContext, String url, long otherID, String channel_uid) {
        cmdJumpUtil = new CMDJumpUtil(mContext, url, otherID, channel_uid);
    }

    public static void addClickToTextViewLink(Context mContext, TextView tv, String content) {
        addClickToTextViewLink(mContext, tv, content, -1, null);
    }

    /**
     * 拼接href，用的时候切记看清楚，别用错了
     *
     * @param mContext
     * @param tv
     * @param jumpContent 跳转内容
     * @param showContent 显示内容
     */
    public static void addClickToTextViewLink(Context mContext, TextView tv, String jumpContent, String showContent) {
        addClickToTextViewLink(mContext, tv, "<a href=\"" + jumpContent
                + "\">" + showContent + "</a>", -1, null);
    }

    /**
     * 设置TextView文字展示及其html格式内容点击行为
     *
     * @param mContext 上下文
     * @param tv       需要设置html点击效果展示的TextView
     * @param content  TextView展示的文字
     */
    public static void addClickToTextViewLink(Context mContext, TextView tv, String content, long otherID, String channel_uid) {
        tv.setText(Html.fromHtml(content + ""));
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence linkContent = tv.getText();
        if (linkContent instanceof Spannable) {
            int end = linkContent.length();
            Spannable sp = (Spannable) linkContent;
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(linkContent);
            for (URLSpan url : urls) {
                style.removeSpan(url);
                MyURLSpan myURLSpan = new MyURLSpan(mContext, url.getURL(), otherID, channel_uid);
                style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tv.setText(style);
        }
    }

    @Override
    public void onClick(View widget) {
        if (cmdJumpUtil != null) {
            cmdJumpUtil.onCMD();
        }
    }
}