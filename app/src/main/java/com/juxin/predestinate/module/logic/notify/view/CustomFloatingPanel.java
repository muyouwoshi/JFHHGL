package com.juxin.predestinate.module.logic.notify.view;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.ui.main.MainActivity;

/**
 * 私聊信息提示panel
 */
public class CustomFloatingPanel extends FloatingBasePanel {

    private ImageView user_img;
    private TextView user_name, user_content;
    private View floating_tip, btn_reply;

    public CustomFloatingPanel(Context context) {
        super(context);
        setContentView(R.layout.common_floating_layout);
    }

    @Override
    public void initView() {
        user_img = (ImageView) findViewById(R.id.user_img);
        user_name = (TextView) findViewById(R.id.user_name);
        user_content = (TextView) findViewById(R.id.user_content);

        floating_tip = findViewById(R.id.floating_tip);
        btn_reply = findViewById(R.id.btn_reply);

        findViewById(R.id.btn_ignore).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity)
                    ((MainActivity) context).closeFloatingMessage();
            }
        });
    }

    @Override
    public void reset() {
    }

    /**
     * 数据初始化
     *
     * @param name    用户名
     * @param content 消息内容
     * @param avatar  用户头像
     */
    public void init(final String name, final String content, final String avatar, final OnClickListener listener) {
        ImageLoader.loadAvatar(getContext(), avatar, user_img);
        user_name.setText(name);
        user_content.setText(Html.fromHtml(content));
        floating_tip.setOnClickListener(listener);
        btn_reply.setOnClickListener(listener);
    }
}
