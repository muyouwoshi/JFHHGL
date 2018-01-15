package com.juxin.predestinate.ui.user.check;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.local.chat.MessageRet;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.socket.IMProxy;
import com.juxin.predestinate.module.logic.socket.NetData;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

/**
 * 创建日期：2017/7/24
 * 描述:部分信息panel
 * 作者:lc
 */
public class UserPartInfoPanel extends BasePanel implements IMProxy.SendCallBack {

    private int follow;
    private int channel;
    private int followType = 1;   // 关注、取消关注

    private TextView user_follow;
    private ImageView iv_follow;  // 关注星标

    private UserDetail userDetail; // 用户资料

    public UserPartInfoPanel(Context context, int channel, UserDetail userProfile) {
        super(context);
        setContentView(R.layout.p1_user_part_info);
        this.channel = channel;
        this.userDetail = userProfile;
        follow = userDetail.getFollowmecount();
        initView();
    }

    private void initView() {
        ImageView img_gender = (ImageView) findViewById(R.id.iv_sex);
        TextView user_age = (TextView) findViewById(R.id.tv_age);
        TextView user_height = (TextView) findViewById(R.id.tv_height);
        TextView user_distance = (TextView) findViewById(R.id.tv_distance);
        TextView user_online_time = (TextView) findViewById(R.id.tv_last_online);
        ImageView iv_rank = (ImageView) findViewById(R.id.iv_rank);
        ImageView iv_vip = (ImageView) findViewById(R.id.iv_vip);
        LinearLayout ll_guanzhu = (LinearLayout) findViewById(R.id.ll_guanzhu);
        user_follow = (TextView) findViewById(R.id.tv_guanzhu);
        iv_follow = (ImageView) findViewById(R.id.iv_guanzhu);

        if (userDetail == null) return;
//        if (channel != CenterConstant.USER_CHECK_INFO_OWN) {
//            ll_guanzhu.setOnClickListener(listener);
//            if (userDetail != null) {
//                if (userDetail.isFollow())
//                    iv_follow.setImageResource(R.drawable.f1_followed_star);
//            }
//        }
//         //男用户点击首页贵族进入TA人资料，关注按钮隐藏
//        if(ModuleMgr.getCenterMgr().getMyInfo().isMan() && (channel == CenterConstant.USER_CHECK_PEERAGE_OTHER || channel == CenterConstant.USER_CHECK_INFO_OWN)) {
//            ll_guanzhu.setVisibility(View.INVISIBLE);
//        }
        if (userDetail.isMan()) {
            img_gender.setImageResource(R.drawable.f1_sex_male_2);
            iv_rank.setImageResource(R.drawable.f1_top02);
        }
        user_age.setText(getContext().getString(R.string.user_info_age, userDetail.getAge()));
        user_height.setText(String.format("%scm", userDetail.getHeight()));
        user_online_time.setText(!TextUtils.isEmpty(userDetail.getOnline_text()) ? userDetail.getOnline_text() : getContext().getString(R.string.user_online));
        user_distance.setText(userDetail.getDistance() > 5 ? getContext().getString(R.string.user_info_distance_far) : getContext().getString(R.string.user_info_distance_near));
        user_follow.setText(getContext().getString(R.string.user_info_follow_count, follow));
        iv_vip.setVisibility(userDetail.isVip() ? View.VISIBLE : View.GONE);
        iv_rank.setVisibility(userDetail.getTopN() <= 0 ? View.GONE : View.VISIBLE);

        if (channel == CenterConstant.USER_CHECK_INFO_OWN) {//个人主页 字体颜色
            user_distance.setTextColor(ContextCompat.getColor(getContext(), R.color.text_ciyao_gray));
            user_online_time.setTextColor(ContextCompat.getColor(getContext(), R.color.text_ciyao_gray));
        }
    }

    private NoDoubleClickListener listener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.ll_guanzhu:       // 关注星标
                    Statistics.userBehavior(SendPoint.userinfo_btnfollow, userDetail.getUid());
                    handleFollow();
                    break;
            }
        }
    };

    // ---------------------------------------- 关注消息 --------------------------------------
    private void handleFollow() {
        if (channel == CenterConstant.USER_CHECK_INFO_OWN) return;
        followType = 1;
        if (userDetail.isFollow()) {  // 已关注
            followType = 2;
        }
        ModuleMgr.getChatMgr().sendAttentionMsg(userDetail.getUid(), "", userDetail.getKf_id(), followType, this);
    }

    private void handleFollowSuccess() {
        switch (followType) {
            case 1:
                follow += 1;
                PToast.showShort(getContext().getResources().getString(R.string.user_info_follow_suc));
                iv_follow.setImageResource(R.drawable.f1_followed_star);
                user_follow.setText(getContext().getString(R.string.user_info_follow_count, follow));
                if (userDetail != null) {
                    userDetail.setIsfollow(1);
                    userDetail.setFollowmecount(follow);
                }
                break;

            case 2:
                follow -= 1;
                PToast.showShort(getContext().getResources().getString(R.string.user_info_unfollow_suc));
                iv_follow.setImageResource(R.drawable.f1_follow_star);
                user_follow.setText(getContext().getString(R.string.user_info_follow_count, follow));
                if (userDetail != null) {
                    userDetail.setIsfollow(0);
                    userDetail.setFollowmecount(follow);
                }
                break;
        }
    }

    private void handleFollowFail() {
        String msg = "";
        switch (followType) {
            case 1:
                msg = getContext().getResources().getString(R.string.user_info_follow_fail);
                break;

            case 2:
                msg = getContext().getResources().getString(R.string.user_info_unfollow_fail);
                break;
        }
        PToast.showShort(msg);
    }

    /**
     * 发关注消息结果回调
     */
    @Override
    public void onResult(long msgId, boolean group, String groupId, long sender, String contents) {
        MessageRet messageRet = new MessageRet();
        messageRet.parseJson(contents);

        if (messageRet.getS() == 0) {
            handleFollowSuccess();
        } else {
            handleFollowFail();
        }
    }

    @Override
    public void onSendFailed(NetData data) {
        handleFollowFail();
    }
}
