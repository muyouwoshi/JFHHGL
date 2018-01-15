package com.juxin.predestinate.ui.user.fragment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.library.unread.BadgeView;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.FinalKey;
import com.juxin.predestinate.module.logic.model.impl.UnreadMgrImpl;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewAdapter;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewHolder;
import com.juxin.predestinate.ui.user.fragment.bean.UserAuth;

import java.util.List;

/**
 * 个人中心布局adapter
 * Created by Su on 2017/3/28.
 */

public class UserAuthAdapter extends BaseRecyclerViewAdapter {

    public UserAuthAdapter(Context context) {
        super(context, null);
    }

    @Override
    public int[] getItemLayouts() {
        return new int[]{R.layout.p1_user_fragment_list_item};
    }

    @Override
    public void onBindRecycleViewHolder(BaseRecyclerViewHolder viewHolder, int position) {
        UserAuth userAuth = (UserAuth) getItem(position);

        View item_view = viewHolder.findViewById(R.id.item_view);
        ImageView item_icon = viewHolder.findViewById(R.id.item_icon);
        TextView item_text = viewHolder.findViewById(R.id.item_text);
        TextView item_hint = viewHolder.findViewById(R.id.item_hint);
        BadgeView item_badge = viewHolder.findViewById(R.id.item_badge);
        View divider_line = viewHolder.findViewById(R.id.divider_line);
        View divider_view = viewHolder.findViewById(R.id.divider_view);
        ImageView item_hint_img = viewHolder.findViewById(R.id.item_hint_img);

        //区块划分
        int select = getListItemSelection(userAuth, position);
        if (select == 1) {
            divider_line.setVisibility(View.GONE);
            divider_view.setVisibility(View.VISIBLE);
        } else if (select == -1) {
            divider_line.setVisibility(View.VISIBLE);
            divider_view.setVisibility(View.GONE);
        } else {
            divider_line.setVisibility(View.GONE);
            divider_view.setVisibility(View.GONE);
        }

        //角标提示
        item_hint.setVisibility(View.GONE);

        if (!userAuth.isShow()) {
            divider_line.setVisibility(View.GONE);
            item_view.setVisibility(View.GONE);
            return;
        }
        item_view.setVisibility(View.VISIBLE);
        item_icon.setBackgroundResource(userAuth.getRes());
        item_text.setText(userAuth.getName());

        switch (userAuth.getId()) { // 对应arrays.xml 里的 user_authority_name
            case CenterItemID.i_Center_item_1: // 我的钱包
                String num = String.valueOf(ModuleMgr.getCenterMgr().getMyInfo().getRedbagsum() / 100f);
                item_hint.setVisibility(View.VISIBLE);
                item_hint.setText(App.context.getString(R.string.user_info_redbag_num, num));
                avoidDislocation(item_hint);
                ModuleMgr.getUnreadMgr().registerBadge(item_badge, true, UnreadMgrImpl.MY_WALLET);
                break;

            case CenterItemID.i_Center_item_2: // 我的Y币
                item_hint.setVisibility(View.VISIBLE);
                UserDetail detail = ModuleMgr.getCenterMgr().getMyInfo();
                if (detail.isB()) {
//                    item_hint.setText(String.valueOf(detail.getKeyCount()));
                } else {
                    if (detail.getYcoin() < 100000) {
                        item_hint.setText(String.valueOf(detail.getYcoin()));
                    } else {
                        item_hint.setText("无限");
                    }
                }
                avoidDislocation(item_hint);
                break;

            case CenterItemID.i_Center_item_3: // 我的钻石
                item_hint.setVisibility(View.VISIBLE);
                item_hint.setText(String.valueOf(ModuleMgr.getCenterMgr().getMyInfo().getDiamand()));
                avoidDislocation(item_hint);
                break;

            case CenterItemID.i_Center_item_5: // 我的认证
                item_hint.setVisibility(View.VISIBLE);
                item_hint.setText(String.valueOf(ModuleMgr.getCenterMgr().getMyInfo().isVerifyAll() ? "已认证" : ""));
                avoidDislocation(item_hint);
                break;

            case CenterItemID.i_Center_item_7: // 女性自动回复
                if (PSP.getInstance().getBoolean(
                        ModuleMgr.getCommonMgr().getPrivateKey(FinalKey.ISSHOWAUTOREPLYPOINT_MYHOME), true)
                        && !ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                    item_hint.setVisibility(View.VISIBLE);
                    item_hint.setText("NEW");
                    item_hint.setTextSize(9);
                    item_hint.setTextColor(ContextCompat.getColor(item_hint.getContext(), R.color.white));
                    item_hint.setBackgroundResource(R.drawable.bg_my_autoreply);

                    RelativeLayout.LayoutParams item_hint_lp = (RelativeLayout.LayoutParams) item_hint.getLayoutParams();
                    item_hint_lp.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    item_hint_lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    item_hint.setLayoutParams(item_hint_lp);

                    item_hint.setPadding((int) item_hint.getResources().getDimension(R.dimen.px15_dp),
                            (int) item_hint.getResources().getDimension(R.dimen.px10_dp),
                            (int) item_hint.getResources().getDimension(R.dimen.px15_dp),
                            (int) item_hint.getResources().getDimension(R.dimen.px10_dp));
                } else {
                    item_hint.setVisibility(View.GONE);
                }
                break;

            case CenterItemID.i_Center_item_Promotion:
                item_hint_img.setVisibility(View.VISIBLE);
                item_hint_img.setBackgroundResource(R.drawable.spread_user_promotion_hot);
                break;

            case CenterItemID.i_Center_item_4: // VIP充值
            case CenterItemID.i_Center_item_6: // 我的相册
            case CenterItemID.i_Center_item_8: // 女神追捕
            case CenterItemID.i_Center_item_9: // 个人资料
                break;
            default:
                break;
        }
    }

    @Override
    public int getRecycleViewItemType(int position) {
        return 0;
    }

    /**
     * 处理当前item的level
     */
    private int getListItemSelection(UserAuth userAuth, int position) {
        if (position + 1 < getItemCount()) {
            if (userAuth.getLevel() != ((UserAuth) getItem(position + 1)).getLevel()) {
                return 1;//不同区块
            }
        } else {
            return 0;//集合末尾
        }
        return -1;//相同区块
    }

    private void avoidDislocation(TextView item_hint) {
        item_hint.setTextSize(12);
        item_hint.setPadding(0,0,0,0);
        item_hint.setBackgroundResource(0);
        item_hint.setTextColor(ContextCompat.getColor(item_hint.getContext(), R.color.color_cccccc));
    }
}
