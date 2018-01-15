package com.juxin.predestinate.ui.agora.act.comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.ui.agora.act.bean.RtcComment;

/**
 * 视频结束后评价，结算
 * Created by chengxiaobo on 2017/7/17.
 */
public class RtcCommentActivity extends BaseActivity implements View.OnClickListener {

    private RtcCommentHeadPanel headPanel;
    private RtcCommentFootPanel footPanel;
    private RtcComment comment;

    private Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_chat_video_comment);
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        comment = intent.getParcelableExtra("RtcComment");
        if (comment != null) {
            headPanel.refresh(comment);
            footPanel.refresh(comment);
        }
    }

    private void initView() {
        comment = getIntent().getParcelableExtra("RtcComment");

        setBackView(ModuleMgr.getCenterMgr().getMyInfo().isMan() ? getString(R.string.chat_video_comment_man) :
                getString(R.string.chat_video_comment_female));
        submit = (Button) findViewById(R.id.btn_submit);
        submit.setOnClickListener(this);

        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        headPanel = new RtcCommentHeadPanel(this);
        container.addView(headPanel.getContentView());
        footPanel = new RtcCommentFootPanel(this, comment);
        container.addView(footPanel.getContentView());

        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            submit.setVisibility(View.GONE);
            setTitleRight(getString(R.string.chat_video_submit), this);
        }

        if (comment != null) {
            headPanel.refresh(comment);
            footPanel.refresh(comment);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
            case R.id.base_title_right_txt:
                footPanel.onSubmit();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        //刷新首页数据
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan())
            MsgMgr.getInstance().sendMsg(MsgType.MT_REFRESH_CHAT, true);
        super.onDestroy();
    }
}
