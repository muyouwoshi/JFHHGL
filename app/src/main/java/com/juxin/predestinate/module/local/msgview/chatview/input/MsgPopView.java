package com.juxin.predestinate.module.local.msgview.chatview.input;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.mail.chat.PrivateChatAct;

/**
 * 创建日期：2017/5/25
 * 描述:
 * 作者:lc
 */
public class MsgPopView {
    private int mPopW;
    private int mPopH;
    private String mUrl;

    private Context mContext;
    private TextView tv_add_emoji;
    private PopupWindow mPopupWindow;

    public MsgPopView(Context context, String url) {
        this.mContext = context;
        this.mUrl = url;
        View view = LayoutInflater.from(context).inflate(R.layout.f1_view_msg_pop, null);
        tv_add_emoji = (TextView) view.findViewById(R.id.tv_add_emoji);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 必须有不然点击外部无响应
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mPopW = view.getMeasuredWidth();
        mPopH = view.getMeasuredHeight();

        tv_add_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgMgr.getInstance().sendMsg(MsgType.MT_ADD_CUSTOM_SMILE, mUrl);
                mPopupWindow.dismiss();
            }
        });
    }

    public void show(View view) {
        try {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            // 50 + 34 + 30 标题、（关注TA、查看手机）、滚动条
            int topH = PSP.getInstance().getInt(Constant.PRIVATE_CHAT_TOP_H, UIUtil.dip2px(mContext, 114));
            int x = (location[0] + view.getWidth() / 2) - mPopW / 2;
            int y = location[1] - mPopH;
            y = y < topH ? topH : y;
            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
