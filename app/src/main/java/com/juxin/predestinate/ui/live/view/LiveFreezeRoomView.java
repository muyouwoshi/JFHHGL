package com.juxin.predestinate.ui.live.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;

/**
 * Created by Administrator on 2017/7/24.
 */

public class LiveFreezeRoomView extends LinearLayout {
    private ImageView mBackground ;  //背景图片
    private OnClickListener mListener; //热门跳转按钮的监听
    private TextView mTextButton;    //热门跳转按钮
    private String url;              //背景图片地址
    public LiveFreezeRoomView(Context context) {
        this(context,null);
    }

    public LiveFreezeRoomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initState();
    }

    protected void initState() {
//        url = "http://img4.imgtn.bdimg.com/it/u=1761350384,135247328&fm=26&gp=0.jpg";
        setButtonClickListener(mListener);
        setBackgroundImage(url);
    }

    protected void initView(Context context) {
        inflate(context, R.layout.live_room_freeze,this);
        mBackground = (ImageView) findViewById(R.id.live_room_load_bg);
        mTextButton = (TextView) findViewById(R.id.goto_hot_button);
    }

    /**
     * 设置跳转到热门的按钮的监听
     * @param listener OnClick 监听器
     */
    public void setButtonClickListener(OnClickListener listener){
        mListener = listener;
        if(mTextButton != null) mTextButton.setOnClickListener(mListener);
    }

    /**
     * 设置背景图片
     * @param URL 图片地址
     */
    public void setBackgroundImage(String URL) {
        if (TextUtils.isEmpty(URL)){
            ImageLoader.loadBlur(getContext(), R.drawable.bg_user_nav,mBackground,8, R.drawable.bg_user_nav);
            return;
        }
        url = URL;
        if(mBackground!=null){
            ImageLoader.loadBlur(getContext(),url,mBackground,8, R.drawable.bg_user_nav);
        }
    }
}
