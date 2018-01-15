package com.juxin.predestinate.module.logic.baseui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.predestinate.R;

/**
 * 应用内Fragment基类
 * Created by ZRP on 2016/12/2.
 */
public class BaseFragment extends Fragment {

    public BaseFragment() {
        super();
    }

    private LayoutInflater inflater;
    private ViewGroup container;
    private View contentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 设置内容组件
     *
     * @param resourcesId
     */
    public View setContentView(int resourcesId) {
        contentView = inflater.inflate(resourcesId, container, false);
        return contentView;
    }

    /**
     * 获取内容组件
     *
     * @return
     */
    public View getContentView() {
        return this.contentView;
    }

    /**
     * 查找组件
     *
     * @param id
     * @return
     */
    public View findViewById(int id) {
        return contentView.findViewById(id);
    }

    /**
     * 设置标题
     *
     * @param txt
     */
    public void setTitle(String txt) {
        TextView textView = (TextView) contentView.findViewById(R.id.base_title_title);
        textView.setText(txt);
    }

    /**
     * 设置标题
     *
     * @param txt
     */
    public void setTitle(String txt, int color) {
        TextView textView = (TextView) this.findViewById(R.id.base_title_title);
        textView.setVisibility(View.VISIBLE);
        textView.setText(txt);
        textView.setTextColor(color);
    }

    /**
     * 设置标题背景颜色
     *
     * @param color 标题颜色
     */
    public void setTitleBackground(int color) {
        View titleBg = contentView.findViewById(R.id.base_title);
        titleBg.setBackgroundResource(color);
    }

    /**
     * 设置标题 带角标的标题
     *
     * @param txt    标题内容
     * @param number 角标显示数量
     */
    public void setTitleWithBadge(String txt, String number) {
        TextView textView = (TextView) this.findViewById(R.id.base_title_title);
        TextView badge = (TextView) this.findViewById(R.id.base_title_badge_view);
        textView.setVisibility(View.VISIBLE);
        badge.setVisibility(View.VISIBLE);
        textView.setText(txt);
        badge.setText(number);
    }

    protected void hideTitle(){
        findViewById(R.id.base_title).setVisibility(View.GONE);
    }

    /**
     * 添加标题栏左边布局的填充view
     *
     * @param container 填充的view
     */
    public void setTitleLeftContainer(View container) {
        LinearLayout title_left_container = (LinearLayout) contentView.findViewById(R.id.base_title_left_container);
        title_left_container.setVisibility(View.VISIBLE);
        title_left_container.removeAllViews();
        title_left_container.addView(container);
    }

    public void setTitleLeftContainerRemoveAll() {
        LinearLayout title_left_container = (LinearLayout) contentView.findViewById(R.id.base_title_left_container);
        title_left_container.setVisibility(View.GONE);
        title_left_container.removeAllViews();
    }

    public void setTitleRightContainer(View container) {
        LinearLayout title_left_container = (LinearLayout) contentView.findViewById(R.id.base_title_right_container);
        title_left_container.setVisibility(View.VISIBLE);
        title_left_container.removeAllViews();
        title_left_container.addView(container);
    }

    public void setTitleRightImgGone() {
        this.findViewById(R.id.base_title_right_img_container).setVisibility(View.GONE);
    }

    public void setTitleRightImgVisible() {
        this.findViewById(R.id.base_title_right_img_container).setVisibility(View.VISIBLE);
    }

    /**
     * 添加title中标题中间的填充view
     *
     * @param container 填充的view
     */
    public void setTitleCenterContainer(View container) {
        LinearLayout title_center_container = (LinearLayout) this.findViewById(R.id.base_title_center_container);
        title_center_container.removeAllViews();
        title_center_container.addView(container);
    }

    /**
     * 设置标题右侧文字
     *
     * @param txt      标题右侧显示文字
     * @param listener 文字点击事件回调
     */
    public void setTitleRight(String txt, View.OnClickListener listener) {
        setTitleRight(txt, -1, listener);
    }

    /**
     * 设置标题右侧文字
     *
     * @param txt      标题右侧显示文字
     * @param color    文字颜色资源id
     * @param listener 文字点击事件回调
     */
    public void setTitleRight(String txt, int color, View.OnClickListener listener) {
        this.findViewById(R.id.base_title_right_img_container).setVisibility(View.GONE);
        TextView textView = (TextView) this.findViewById(R.id.base_title_right_txt);
        textView.setVisibility(View.VISIBLE);
        if (color != -1) textView.setTextColor(getResources().getColor(color));
        textView.setText(txt);
        textView.setOnClickListener(listener);
        titleRightText = textView;
    }

    /**
     * 设置标题右侧图片
     */
    public void setTitleRightImg(int resId, View.OnClickListener listener) {
        setTitleRightImg(resId, null, listener);
    }

    public void setTitleRightImg(int resId, String title, View.OnClickListener listener) {
        this.findViewById(R.id.base_title_right_txt).setVisibility(View.GONE);
        View view = this.findViewById(R.id.base_title_right_img_container);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(listener);
        ImageView imageView = (ImageView) view.findViewById(R.id.base_title_right_img);
        imageView.setImageResource(resId);
        TextView txt = (TextView) findViewById(R.id.base_title_img_right_txt);
        if (!TextUtils.isEmpty(title)) {
            txt.setVisibility(View.VISIBLE);
            txt.setText(title);
        } else {
            txt.setVisibility(View.GONE);
        }
    }

    public void setTitleRightImg(int resId, int padding, View.OnClickListener listener) {
        this.findViewById(R.id.base_title_right_txt).setVisibility(View.GONE);
        View view = this.findViewById(R.id.base_title_right_img_container);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(listener);
        ImageView imageView = (ImageView) view.findViewById(R.id.base_title_right_img);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setImageResource(resId);
    }

    private TextView titleLeftText, titleRightText;

    /**
     * @return 获取左侧textview，需先set，再get，确保其不为空
     */
    public TextView getTitleLeftText() {
        return titleLeftText;
    }

    /**
     * @return 获取右侧textview，需先set，再get，确保其不为空
     */
    public TextView getTitleRightText() {
        return titleRightText;
    }

    /**
     * 设置标题左侧文字
     *
     * @param txt
     */
    public void setTitleLeft(String txt, View.OnClickListener listener) {
        TextView textView = (TextView) contentView.findViewById(R.id.base_title_left_txt);
        textView.setVisibility(View.VISIBLE);
        textView.setText(txt);
        textView.setOnClickListener(listener);
        titleLeftText = textView;
    }

    /**
     * 设置标题左侧文字
     *
     * @param txt
     */
    public void setTitleLeft(String txt, int color, View.OnClickListener listener) {
        TextView textView = (TextView) contentView.findViewById(R.id.base_title_left_txt);
        textView.setVisibility(View.VISIBLE);
        textView.setTextColor(color);
        textView.setText(txt);
        textView.setOnClickListener(listener);
        titleLeftText = textView;
    }

    /**
     * 设置标题左侧图片
     */
    public void setTitleLeftImg(int resId, View.OnClickListener listener) {
        LinearLayout titleLeftView = (LinearLayout) contentView.findViewById(R.id.base_title_left_view);
        ImageView imageView = (ImageView) contentView.findViewById(R.id.base_title_left_img);
        titleLeftView.setVisibility(View.VISIBLE);
        imageView.setImageResource(resId);
        imageView.setOnClickListener(listener);
    }

    /**
     * 设置是否显示标题下方横线(默认显示)
     *
     * @param isShow
     */
    public void setIsBottomLineShow(Boolean isShow) {
        View view = contentView.findViewById(R.id.base_title_bottom_line);
        if (isShow){
            view.setVisibility(View.VISIBLE);
        }else {
            view.setVisibility(View.GONE);
        }
    }


    public boolean isEmpty(CharSequence str){
        return TextUtils.isEmpty(str);
    }

}
