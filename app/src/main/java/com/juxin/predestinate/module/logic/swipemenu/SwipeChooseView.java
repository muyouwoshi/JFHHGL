package com.juxin.predestinate.module.logic.swipemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.juxin.predestinate.R;

public class SwipeChooseView extends LinearLayout implements View.OnClickListener {
	private ImageView mImageView;

	private boolean mIsChecked = false;
	private int position;

	private OnChooseCheckedChangeListener mCheckedChangeListener;

	public SwipeChooseView(Context context) {
		this(context, null);
	}

	public SwipeChooseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void init() {
		setBackgroundColor(getContext().getResources().getColor(R.color.bg_color));
		LayoutParams params = new LayoutParams(dp2px(35), LayoutParams.MATCH_PARENT);
		LinearLayout parent = new LinearLayout(getContext());
		parent.setGravity(Gravity.CENTER);
		parent.setOrientation(LinearLayout.VERTICAL);
		parent.setOnClickListener(this);
		parent.setLayoutParams(params);
		addView(parent);

		parent.addView(createCheckBox());
	}

	private View createCheckBox() {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mImageView = new ImageView(getContext());
		mImageView.setBackgroundResource(R.drawable.btn_radio_mail_choose_seletor);
		mImageView.setLayoutParams(params);
		return mImageView;
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public void onClick(View v) {
		mIsChecked = !mIsChecked;
		mImageView.setEnabled(mIsChecked);
		if (mCheckedChangeListener != null) {
			mCheckedChangeListener.onChooseCheckedChanged(position, mIsChecked);
		}
	}

	public void setChecked(boolean isChecked){
		mIsChecked = isChecked;
		mImageView.setEnabled(mIsChecked);
	}

	public interface OnChooseCheckedChangeListener {
		void onChooseCheckedChanged(int position, boolean isChecked);
	}

	public void setCheckedChangeListener(OnChooseCheckedChangeListener checkedChangeListener) {
		this.mCheckedChangeListener = checkedChangeListener;
	}
}
