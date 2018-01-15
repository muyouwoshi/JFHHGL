package com.juxin.predestinate.module.logic.baseui.picker.common.popup;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.util.UIUtil;


/**
 * 带确定及取消按钮的
 */
public abstract class ConfirmPopup<V extends View> extends BottomPopup<View> {

    protected int topLineColor = 0XFFE5E5E5;
    protected boolean cancelVisible = true;
    protected CharSequence cancelText = "";
    protected CharSequence submitText = "";
    protected CharSequence titleText = "";
    protected int cancelTextColor = 0XFF333333;
    protected int submitTextColor = 0XFFFC615C;
    protected int titleTextColor = 0XFF333333;

    /**
     * Instantiates a new Confirm popup.
     *
     * @param activity the activity
     */
    public ConfirmPopup(FragmentActivity activity) {
        super(activity);
        cancelText = activity.getString(android.R.string.cancel);
        submitText = activity.getString(android.R.string.ok);
    }

    /**
     * Sets top line color.
     *
     * @param topLineColor the top line color
     */
    public void setTopLineColor(@ColorInt int topLineColor) {
        this.topLineColor = topLineColor;
    }

    /**
     * Sets cancel visible.
     *
     * @param cancelVisible the cancel visible
     */
    public void setCancelVisible(boolean cancelVisible) {
        this.cancelVisible = cancelVisible;
    }

    /**
     * Sets cancel text.
     *
     * @param cancelText the cancel text
     */
    public void setCancelText(CharSequence cancelText) {
        this.cancelText = cancelText;
    }

    /**
     * Sets cancel text.
     *
     * @param textRes the text res
     */
    public void setCancelText(@StringRes int textRes) {
        this.cancelText = activity.getString(textRes);
    }

    /**
     * Sets submit text.
     *
     * @param submitText the submit text
     */
    public void setSubmitText(CharSequence submitText) {
        this.submitText = submitText;
    }

    /**
     * Sets submit text.
     *
     * @param textRes the text res
     */
    public void setSubmitText(@StringRes int textRes) {
        this.submitText = activity.getString(textRes);
    }

    /**
     * Sets title text.
     *
     * @param titleText the title text
     */
    public void setTitleText(CharSequence titleText) {
        this.titleText = titleText;
    }

    /**
     * Sets title text.
     *
     * @param textRes the text res
     */
    public void setTitleText(@StringRes int textRes) {
        this.titleText = activity.getString(textRes);
    }

    /**
     * Sets cancel text color.
     *
     * @param cancelTextColor the cancel text color
     */
    public void setCancelTextColor(@ColorInt int cancelTextColor) {
        this.cancelTextColor = cancelTextColor;
    }

    /**
     * Sets submit text color.
     *
     * @param submitTextColor the submit text color
     */
    public void setSubmitTextColor(@ColorInt int submitTextColor) {
        this.submitTextColor = submitTextColor;
    }

    /**
     * Sets title text color.
     *
     * @param titleTextColor the title text color
     */
    public void setTitleTextColor(@ColorInt int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    /**
     * @see #makeHeaderView()
     * @see #makeCenterView()
     * @see #makeFooterView()
     */
    @Override
    protected final View makeContentView() {
        LinearLayout rootLayout = new LinearLayout(activity);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        rootLayout.setBackgroundResource(R.drawable.dialog_baseconfirm_bg);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setGravity(Gravity.CENTER);
        rootLayout.setPadding(0, 0, 0, 0);
        rootLayout.setClipToPadding(false);

        //添加头部
        View headerView = makeHeaderView();
        if (!TextUtils.isEmpty(titleText) && headerView != null) {
            rootLayout.addView(headerView);

            //添加头部分割线
            View headerLine = new View(activity);
            headerLine.setLayoutParams(new LinearLayout.LayoutParams(100, 5));
            headerLine.setBackgroundColor(topLineColor);
            rootLayout.addView(headerLine);
        }

        //添加中部内容
        rootLayout.addView(makeCenterView(), new LinearLayout.LayoutParams(MATCH_PARENT, 0, 1.0f));

        //添加脚部
        View footerView = makeFooterView();
        if (footerView != null) {
            //添加脚部分割线
            View footerLine = new View(activity);
            footerLine.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 1));
            footerLine.setBackgroundColor(activity.getResources().getColor(R.color.fengexian_gray));
            rootLayout.addView(footerLine);

            rootLayout.addView(footerView);
        }

        return rootLayout;
    }

    private int location = Gravity.CENTER; // Dialog标题显示显示位置

    public void setTitleLocation(int location) {
        this.location = location;
    }

    /**
     * Make header view view.
     *
     * @return the view
     */
    @Nullable
    protected View makeHeaderView() {
        //创建标题
        TextView titleView = new TextView(activity);
        titleView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, UIUtil.dip2px(activity,40)));
        titleView.setGravity(location);
        if (!TextUtils.isEmpty(titleText)) {
            titleView.setText(titleText);
        }
        titleView.setTextSize(15);
        titleView.setTextColor(titleTextColor);

        return titleView;
    }

    /**
     * Init center view v.
     *
     * @return the v
     */
    @NonNull
    protected abstract V makeCenterView();

    /**
     * Make footer view view.
     *
     * @return the view
     */
    @Nullable
    protected View makeFooterView() {
        LinearLayout footerButtonLayout = new LinearLayout(activity);
        footerButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
        footerButtonLayout.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, UIUtil.dip2px(activity,45)));
        footerButtonLayout.setGravity(Gravity.CENTER_VERTICAL);

        //创建取消按钮
        TextView cancelButton = new TextView(activity);
        cancelButton.setVisibility(cancelVisible ? View.VISIBLE : View.GONE);
        LinearLayout.LayoutParams cancelButtonLayoutParams = new LinearLayout.LayoutParams(0, MATCH_PARENT);
        cancelButtonLayoutParams.weight = 1;
        cancelButton.setLayoutParams(cancelButtonLayoutParams);
        cancelButton.setBackgroundResource(R.drawable.dialog_baseconfirm_left_bg);
        cancelButton.setGravity(Gravity.CENTER);
        if (!TextUtils.isEmpty(cancelText)) {
            cancelButton.setText(cancelText);
        }
        cancelButton.setTextSize(15);
        cancelButton.setTextColor(cancelTextColor);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                onCancel();
            }
        });
        footerButtonLayout.addView(cancelButton);

        //创建中间的分割线
        if (cancelVisible) {
            View line = new View(activity);
            line.setBackgroundColor(activity.getResources().getColor(R.color.fengexian_gray));
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(1, MATCH_PARENT);
            line.setLayoutParams(lineParams);
            footerButtonLayout.addView(line);
        }

        //创建确定按钮
        TextView submitButton = new TextView(activity);
        LinearLayout.LayoutParams submitButtonLayoutParams = new LinearLayout.LayoutParams(0, MATCH_PARENT);
        submitButtonLayoutParams.weight = 1;
        submitButton.setLayoutParams(submitButtonLayoutParams);
        submitButton.setBackgroundResource(cancelVisible ?
                R.drawable.dialog_baseconfirm_right_bg : R.drawable.dialog_baseconfirm_button_bg);
        submitButton.setGravity(Gravity.CENTER);
        if (!TextUtils.isEmpty(submitText)) {
            submitButton.setText(submitText);
        }
        submitButton.setTextSize(15);
        submitButton.setTextColor(submitTextColor);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });
        footerButtonLayout.addView(submitButton);

        return footerButtonLayout;
    }

    /**
     * On submit.
     */
    protected void onSubmit() {
        dismiss();
    }

    /**
     * On cancel.
     */
    protected void onCancel() {

    }

}
