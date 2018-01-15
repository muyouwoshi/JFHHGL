package com.juxin.predestinate.ui.agora.act.comment;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.view.BasePanel;
import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.agora.act.bean.RtcComment;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;
import com.juxin.predestinate.ui.agora.view.RatingBarView;
import com.juxin.predestinate.ui.agora.view.ViewAutoWrap;

import java.util.List;

/**
 * 视频结束后评价
 * Created by Su on 2017/7/19.
 */
public class RtcCommentFootPanel extends BasePanel implements RequestComplete {

    private CustomFrameLayout customLayout;
    private RelativeLayout videoEarn;   // 女性：视频收入
    private RelativeLayout giftEarn;    // 女性：礼物收入
    private RelativeLayout rlProp;      // 男性：不满评价
    private RatingBarView barView;      // 星级评价
    private ViewAutoWrap autoWrap;      // 流布局
    private EditText tv_edit;
    private TextView tv_income_type;
    private TextView tvVideoEarn;
    private TextView tvGiftEarn;

    private List<String> list;
    private RtcComment comment;

    public RtcCommentFootPanel(Context context, RtcComment comment) {
        super(context);
        setContentView(R.layout.f1_chat_video_comment_foot_panel);
        this.comment = comment;
        initView();
    }

    private void initView() {
        customLayout = (CustomFrameLayout) findViewById(R.id.video_custom);

        // 男性
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            customLayout.showOfIndex(1);
            rlProp = (RelativeLayout) findViewById(R.id.rl_prop_reason);
            autoWrap = (ViewAutoWrap) findViewById(R.id.view_comment_content);
            barView = (RatingBarView) findViewById(R.id.custom_ratingbar);
            tv_edit = (EditText) findViewById(R.id.tv_edit);
            ModuleMgr.getCenterMgr().inputFilterSpace(tv_edit, 120);
            list = ModuleMgr.getCommonMgr().getCommonConfig().getVideo().getReport_reasons();

            if (comment != null && comment.getvType() == AgoraConstant.RTC_CHAT_VIDEO) {//男号 视频显示底部标签和原因，音频只显示星星
                rlProp.setVisibility(View.VISIBLE);
            }
            if (list != null && !list.isEmpty()) {
                autoWrap.init(list, getContext(), false);
            }

            barView.setSelectStar(3);//默认选中3星
            barView.setOnRatingListener(new RatingBarView.OnRatingListener() {
                @Override
                public void onRating(Object bindObject, int RatingScore) {
                    if (RatingScore < 4) {
                        list = ModuleMgr.getCommonMgr().getCommonConfig().getVideo().getReport_reasons();
                        autoWrap.removeAllViews();
                        autoWrap.init(list, getContext(), false);
                    } else {
                        list = ModuleMgr.getCommonMgr().getCommonConfig().getVideo().getPraise_reasons();
                        autoWrap.removeAllViews();
                        autoWrap.init(list, getContext(), false);
                    }
                }
            });
            return;
        }

        // 女性
        customLayout.showOfIndex(0);
        videoEarn = (RelativeLayout) findViewById(R.id.rl_video_earn);
        tv_income_type = (TextView) findViewById(R.id.tv_income_type);
        tvVideoEarn = (TextView) findViewById(R.id.video_earn_money);
        giftEarn = (RelativeLayout) findViewById(R.id.rl_gift_earn);
        tvGiftEarn = (TextView) findViewById(R.id.gift_earn_money);
        if (comment != null) {
            if (comment.getvType() == AgoraConstant.RTC_CHAT_VIDEO) {
                tv_income_type.setText(getContext().getString(R.string.chat_girl_video_earn));
            } else if (comment.getvType() == AgoraConstant.RTC_CHAT_VOICE) {
                tv_income_type.setText(getContext().getString(R.string.chat_girl_voice_earn));
            }
        }
    }

    public void refresh(RtcComment comment) {
        if (comment == null) {
            return;
        }
        this.comment = comment;

        // 男性用户
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            if (list == null || list.isEmpty()) {
                autoWrap.init(comment.getReasons(), getContext(), false);
            }
            return;
        }

        // 女性用户
        tvVideoEarn.setText(getContext().getString(R.string.chat_video_girl_earn, String.valueOf(comment.getVideoBonus() / 100f)));
        tvGiftEarn.setText(getContext().getString(R.string.chat_video_girl_earn, String.valueOf(comment.getGiftBonus() / 100f)));
    }

    public void onSubmit() {
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            if (comment == null) {
                PToast.showShort(getContext().getString(R.string.chat_video_error));
                return;
            }

            String edit = tv_edit.getText().toString();
            List<String> submit = autoWrap.getSelectedString();
            ModuleMgr.getRtcEnginMgr().reqAddFeedback(comment.getUid(), barView.getStarCount(), submit, edit, this);
            return;
        }
        finishActivity();
    }


    @Override
    public void onRequestComplete(HttpResponse response) {
        if (!response.isOk()) {
            PToast.showShort(getContext().getString(R.string.chat_video_submit_error));
            return;
        }
        PToast.showShort(getContext().getString(R.string.chat_video_submit_suc));
        finishActivity();
    }

    private void finishActivity() {
        if (comment != null) {
            UIShow.showPrivateChatAct(getContext(), comment.getUid(), comment.getNickName());
        }
        try {
            ((Activity) getContext()).finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
