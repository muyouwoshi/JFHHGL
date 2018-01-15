package com.juxin.predestinate.module.logic.baseui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.juxin.predestinate.R;

/**
 * 倒计时控件
 * Created by Su on 2017/3/21.
 */
public class CountDownTextView extends AppCompatTextView {
    private OnCountDownListener mListener;
    private CountDownTimer mTimer;
    private String mFormat;         // 自定义时间倒计时格式
    private long mCountDownTime;    // 倒计时间，单位毫秒
    private long mSecond;           // 剩余秒数
    private long mMinute;           // 剩余分钟
    private String exMinute;        // 展示分钟
    private String exSecond;        // 展示秒数

    public CountDownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCountDownTime(context, attrs);
    }

    public CountDownTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initCountDownTime(context, attrs);
    }

    public CountDownTextView(Context context) {
        super(context);
    }

    private void initCountDownTime(Context context, AttributeSet attrs) {
        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.CountDownView);
        mCountDownTime = (long) attribute.getFloat(R.styleable.CountDownView_countDownTime, 0);
        mFormat = attribute.getString(R.styleable.CountDownView_countDownFormat);
        if (TextUtils.isEmpty(mFormat)) {
            mFormat = getContext().getString(R.string.count_default_format);
        }
    }

    public void setCountDownTimes(long countDownTime, String cssResId) {
        if (!TextUtils.isEmpty(cssResId)) {
            this.mFormat = cssResId;
        }
        mCountDownTime = countDownTime;
    }

    public void setCountDownTimes(long countDownTime) {
        mCountDownTime = countDownTime;
    }

    public void start() {
        if (mCountDownTime < 0) mCountDownTime = 0;

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        int countDownInterval = 1000;
        mTimer = new CountDownTimer(mCountDownTime, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                mMinute = millisUntilFinished / (1000 * 60);
                mSecond = (millisUntilFinished % (1000 * 60)) / 1000;

                exFormat(); // 格式化展示时间
                CountDownTextView.this.setText(Html.fromHtml(String.format(mFormat, exMinute, exSecond)));

                if (mListener != null) {
                    mListener.onTick(mMinute, mSecond);
                }
            }

            @Override
            public void onFinish() {
                CountDownTextView.this.setText(Html.fromHtml(String.format(mFormat, "00", "00")));
                if (mListener != null)
                    mListener.onFinish();
            }
        };
        mTimer.start();
    }

    /**
     * 计时关闭
     */
    public void cancel() {
        if (mTimer != null)
            mTimer.cancel();
    }

    private void exFormat() {
        exMinute = String.valueOf(mMinute);
        exSecond = String.valueOf(mSecond);

        if (mMinute < 10)
            exMinute = "0" + mMinute;

        if (mSecond < 10)
            exSecond = "0" + mSecond;
    }

    public void setCountDownListener(OnCountDownListener listener) {
        this.mListener = listener;
    }

    public interface OnCountDownListener {
        void onTick(long min, long sec);   // 倒计时中

        void onFinish(); // 倒计时完成
    }
}
