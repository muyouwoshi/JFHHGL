package com.juxin.predestinate.ui.agora.act.comment;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.ui.agora.act.bean.RtcComment;

/**
 * 视频结束后评价head
 * Created by Su on 2017/7/19.
 */

public class RtcCommentHeadPanel extends BasePanel {
    private TextView tvMoney, tv_relation;

    public RtcCommentHeadPanel(Context context) {
        super(context);
        setContentView(R.layout.f1_chat_video_comment_head_panel);
        initView();
    }

    private void initView() {
        ImageView ivMoney = (ImageView) findViewById(R.id.iv_money);
        tvMoney = (TextView) findViewById(R.id.tv_money);
        tv_relation = (TextView) findViewById(R.id.tv_relation);

        if (!ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            ivMoney.setImageResource(R.drawable.iv_money);
            tvMoney.setTextColor(getContext().getResources().getColor(R.color.theme_color_red));
            tv_relation.setTextColor(getContext().getResources().getColor(R.color.theme_color_red));
            tvMoney.setBackgroundResource(R.drawable.bg_comment_content_selected);
            tv_relation.setBackgroundResource(R.drawable.bg_comment_content_selected);
        }
    }

    public void refresh(RtcComment comment) {
        if (comment == null) return;
        TextView tvTime = (TextView) findViewById(R.id.tv_time);
        ImageView header = (ImageView) findViewById(R.id.img_header);
        TextView name = (TextView) findViewById(R.id.user_name);

        ImageLoader.loadAvatar(getContext(), comment.getAvatar(), header);
        String costDiamond = " " + (ModuleMgr.getCenterMgr().getMyInfo().isMan() ? "-" : "+") + " " + comment.getCost();
        name.setText(comment.getNickName());
        tvTime.setText(TimeUtil.formatTimeLong(comment.getDuration()));
        tvMoney.setText(ModuleMgr.getCenterMgr().getMyInfo().isMan() ?
                getContext().getString(R.string.chat_video_diamond_cost) + costDiamond :
                getContext().getString(R.string.chat_video_girl_earn, String.valueOf(comment.getTotalbonus() / 100f)));
        tv_relation.setText("亲密度:+" + comment.getExp() + "点");
    }
}
