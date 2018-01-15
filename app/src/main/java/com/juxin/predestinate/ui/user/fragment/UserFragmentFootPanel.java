package com.juxin.predestinate.ui.user.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.juxin.library.log.PSP;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.WebActivity;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.FinalKey;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.logic.model.impl.UnreadMgrImpl;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.WebUtil;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewHolder;
import com.juxin.predestinate.ui.user.fragment.bean.UserAuth;
import com.juxin.predestinate.ui.user.util.CenterConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人中心的list布局panel
 */
public class UserFragmentFootPanel extends BasePanel implements BaseRecyclerViewHolder.OnItemClickListener {

    private RecyclerView recyclerView;
    private UserAuthAdapter userAuthAdapter;
    private boolean isB;
    int[] ids = new int[]{
            CenterItemID.I_CENTER_ITEM_LIVE_STAT,
            CenterItemID.i_Center_item_Promotion,
            CenterItemID.i_Center_item_0,
            CenterItemID.i_Center_item_1,
            CenterItemID.i_Center_item_2,
            CenterItemID.i_Center_item_3,
            CenterItemID.i_Center_item_4,
            CenterItemID.i_Center_item_5,
            CenterItemID.i_Center_item_6,
            CenterItemID.i_Center_item_7,
            CenterItemID.i_Center_item_8,
            CenterItemID.i_Center_item_9,};

    public UserFragmentFootPanel(Context context) {
        super(context);
        setContentView(R.layout.p1_user_fragment_footer);
        isB = ModuleMgr.getCenterMgr().getMyInfo().isB();
        initData();
    }

    // 初始化条目数据
    private void initData() {
        List<UserAuth> userAuthList = new ArrayList<>();
        Resources resources = getContext().getResources();
        recyclerView = (RecyclerView) findViewById(R.id.user_fragment_footer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false); // 嵌套ScrollView滑动惯性消失
        String[] names = resources.getStringArray(R.array.user_authority_name);
        int[] levels = resources.getIntArray(R.array.user_authority_level);
        int[] icons = new int[]{
                R.drawable.live_ic_auth_live,
                R.drawable.spread_user_promotion,
                R.drawable.f1_icon_mainpage,
                R.drawable.f1_user_wallet_ico,
                R.drawable.f1_user_ycoin_ico,
                R.drawable.f1_user_diamonds_ico,
                R.drawable.f1_icon_vip,
                R.drawable.f1_auth_ico,
                R.drawable.f1_user_xiangce_ico,
                R.drawable.f1_auto_replay_ico,
                R.drawable.f2_catch_girl_ico,
                R.drawable.f1_user_info_ico};



        for (int i = 0; i < names.length; i++) {
            UserAuth userAuth = new UserAuth(ids[i], icons[i], levels[i], names[i], isShow(ids[i]));
            userAuthList.add(userAuth);
        }
        userAuthAdapter = new UserAuthAdapter(getContext());
        userAuthAdapter.setList(userAuthList);
        recyclerView.setAdapter(userAuthAdapter);
        userAuthAdapter.setOnItemClickListener(this);
    }

    /**
     * 刷新list
     */
    public void refreshView() {
        userAuthAdapter.notifyDataSetChanged();
    }

    /**
     * 判断指定条目显隐状态
     */
    private boolean isShow(int id) {
        UserDetail detail = ModuleMgr.getCenterMgr().getMyInfo();
        if (id == CenterItemID.i_Center_item_2) {// AB环境隐藏Y币栏
            return false;
        }
        if (!detail.isMan() && id == CenterItemID.i_Center_item_4) {// 女性用户隐藏：VIP充值
            return false;
        }
        if (detail.isMan() && id == CenterItemID.i_Center_item_7) { // 男性用户隐藏：设置自动回复
            return false;
        }
        //男性用户隐藏直播统计
        if (detail.isMan() && id == CenterItemID.I_CENTER_ITEM_LIVE_STAT) {
            return false;
        }
        if (id == CenterItemID.i_Center_item_8) {// 个人中心暂时隐藏女神追捕
            return false;
        }
        return true;
    }

    @Override
    public void onItemClick(View convertView, int position) {
        switch (ids[position]) {
            case CenterItemID.i_Center_item_0: // 我的主页
                UIShow.showCheckOwnInfoAct(getContext());
                Statistics.userBehavior(SendPoint.menu_me_myhome);
                break;

            case CenterItemID.i_Center_item_1: // 我的钱包
                UIShow.showRedBoxRecordAct(getContext());
                Statistics.userBehavior(SendPoint.menu_me_money);
                break;

            case CenterItemID.i_Center_item_2:// 我的Y币
                SourcePoint.getInstance().lockSource((Activity) getContext(), "myy");
                UIShow.showBuyCoinActivity(getContext());
                Statistics.userBehavior(SendPoint.menu_me_y);
                break;

            case CenterItemID.i_Center_item_3:// 我的钻石
                SourcePoint.getInstance().lockSource("me_mygem_pay");
                UIShow.showMyDiamondsAct(getContext());
                Statistics.userBehavior(SendPoint.menu_me_gem);
                break;

            case CenterItemID.i_Center_item_4:// VIP充值
                if (!ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                    return;
                }
                SourcePoint.getInstance().lockSource((Activity) getContext(), "myvip");
                UIShow.showOpenVipActivity(getContext());
                Statistics.userBehavior(SendPoint.menu_me_vippay);
                break;

            case CenterItemID.i_Center_item_5:// 我的认证
                UIShow.showMyAuthenticationAct((FragmentActivity) getContext(), 103);
                Statistics.userBehavior(SendPoint.menu_me_meauth);
                break;

            case CenterItemID.i_Center_item_6:// 我的相册
                UIShow.showUserPhotoAct(getContext());
                StatisticsUser.centerAlbum();
                break;

            case CenterItemID.i_Center_item_7:// 女性自动回复设置
                PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(FinalKey.ISSHOWAUTOREPLYPOINT_MYHOME), false);
                ModuleMgr.getUnreadMgr().resetUnreadByKey(UnreadMgrImpl.WOMAN_AUTOREPLY);
                userAuthAdapter.notifyDataSetChanged();

                UIShow.showWomanAutoReply(getContext());
                Statistics.userBehavior(SendPoint.menu_me_autoreply);
                break;

            case CenterItemID.i_Center_item_8:// 女神追捕
                UIShow.showCatchGoddessInfoAct(getContext());
                break;

            case CenterItemID.i_Center_item_9:// 个人资料
                UIShow.showUserEditInfoAct(getContext());
                Statistics.userBehavior(SendPoint.menu_me_profile);
                break;

            case CenterItemID.i_Center_item_Promotion: //分享赚钱
                PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.TEMP_SHARE_TYPE), CenterConstant.SHARE_TYPE_ACTIVE);
                UIShow.showWebActivity(context, WebActivity.NORMAL_RIGHT, Hosts.H5_SHARE_EXTENSION, null);
                StatisticsShareUnlock.onMenu_me_sharetomakemoney();
                break;
            //直播统计
            case CenterItemID.I_CENTER_ITEM_LIVE_STAT:
                UIShow.showWebActivity(getContext(), WebUtil.jointUrl(Hosts.H5_APP_LIVE_STAT));
                break;
            default:
                break;
        }
    }
}
