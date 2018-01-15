package com.juxin.predestinate.ui.live.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.live.bean.LiveUserDetail;
import com.juxin.predestinate.ui.live.bean.SendGiftCallbackBean;
import com.juxin.predestinate.ui.live.callback.OnSendGiftCallbackListener;

import java.util.HashMap;
import java.util.Map;

/**
 * <使用/> <br/>
 * new LiveBaseDialog().setInformation.setConfirmText().setCancelText()
 * .setOnItemClickListener()
 * .showDialog();
 * <p>
 * Created by chengxiaobo on 2017/9/11.
 */

public class LiveUserCardDialog extends BaseDialogFragment {

    private ImageView mIvHead;//头像
    private LinearLayout mLLreport;//举报
    private ImageView mIvClose;//关闭
    private TextView mTvNickname;//昵称
    private ImageView mIvSex;//性别
    private TextView mTvID;//ID

    private LinearLayout mllDengji;//等级Linearlayout
    private ImageView mIvJueWei;//爵位
    private ImageView mIvVIP;//VIP
    private ImageView mIvZuanShi;//钻石

    private LinearLayout mllGift;//送礼物和时长Linearlayout
    private TextView mTVGiftCount;//本场送礼
    private TextView mTvOnlineTime;//本场时长

    private LinearLayout mllSetting;//设置
    private RelativeLayout vlbAddFriend;//添加好友
    private RelativeLayout vlbIsFriend;//已经是好友
    private RelativeLayout vlbGoOut;//踢出
    private RelativeLayout vlbShutUp;//禁言
    private RelativeLayout vlbSettingAdministrator;//设为场控
    private RelativeLayout vlbCancelAdministrator;//取消场控
    private View viewLine; //灰线

    private LiveUserDetail userDetail;//用户信息
    private DissmissAddFirend mDissmissAddFirend;

    public static final int JINYAN = 1;
    public static final int TIANJIACHANGKONG = 2;
    public static final int TICHU = 3;
    public static final int QUXIAOCHANGKONG = 4;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDetail = (LiveUserDetail) getArguments().getSerializable("liveUserDetail");

        setDialogWidthSizeRatio(0.95);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.live_user_card_dialog);
        setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

        initView();
        initViewState();

        return getContentView();
    }

    private void initView() {

        mIvHead = (ImageView) findViewById(R.id.iv_head);
        mLLreport = (LinearLayout) findViewById(R.id.ll_report);
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mTvNickname = (TextView) findViewById(R.id.tv_nikename);
        mIvSex = (ImageView) findViewById(R.id.iv_sex);
        mTvID = (TextView) findViewById(R.id.tv_id);

        mllDengji = (LinearLayout) findViewById(R.id.ll_dengji);
        mIvJueWei = (ImageView) findViewById(R.id.iv_juewei);
        mIvVIP = (ImageView) findViewById(R.id.iv_vip);
        mIvZuanShi = (ImageView) findViewById(R.id.iv_zuanshi);

        mllGift = (LinearLayout) findViewById(R.id.ll_gift);
        mTVGiftCount = (TextView) findViewById(R.id.tv_gift_count);
        mTvOnlineTime = (TextView) findViewById(R.id.tv_online_time);

        mllSetting = (LinearLayout) findViewById(R.id.ll_setting2);
        vlbAddFriend = (RelativeLayout) findViewById(R.id.vlb_addfriend);
        vlbIsFriend = (RelativeLayout) findViewById(R.id.vlb_isfriend);
        vlbGoOut = (RelativeLayout) findViewById(R.id.vlb_go_out);
        vlbShutUp = (RelativeLayout) findViewById(R.id.vlb_no_say);
        vlbSettingAdministrator = (RelativeLayout) findViewById(R.id.vlb_setting_administrator);
        vlbCancelAdministrator = (RelativeLayout) findViewById(R.id.vlb_cancel_administrator);
        viewLine=findViewById(R.id.view_line);
    }

    private void initViewState() {

        if (userDetail != null) {

            dealTotal(); //所有情况都会有的情况

            //自己看自己的卡片。 return,否则有举报
            //同性(无加好友和已经是好友) 异性（有加好友或者已加好友）
            //主播 （本场时长，踢出，禁言，设置场控，取消场控）
            //场控（点击非主播，踢出，禁言）

            //1.自己查看自己的资料
            if (userDetail.getUserId().equals(userDetail.getBeiUserId())) {

                mLLreport.setVisibility(View.GONE);
                mIvZuanShi.setVisibility(View.GONE);
                viewLine.setVisibility(View.GONE);
                return;
            } else {
                mLLreport.setVisibility(View.VISIBLE);
            }

            //2.同性(无加好友和已经是好友) 异性（有加好友或者已加好友）
            if (userDetail.getUserSex() != userDetail.getBeiGender()) { //异性有添加好友，同性无
                if (userDetail.isfriend()) {
                    vlbIsFriend.setVisibility(View.VISIBLE);
                    vlbAddFriend.setVisibility(View.GONE);
                } else {
                    vlbAddFriend.setVisibility(View.VISIBLE);
                    vlbIsFriend.setVisibility(View.GONE);
                }

            } else {
                vlbAddFriend.setVisibility(View.GONE);
                vlbIsFriend.setVisibility(View.GONE);
            }

            //3.主播 （本场时长，踢出，禁言，设置场控，取消场控）
            if (userDetail.getUserId().equals(userDetail.getGirlUid())) { //主播查看其它用户

                mllGift.setVisibility(View.VISIBLE);
                mTVGiftCount.setText(userDetail.getConsume()+" 元");

                long t = userDetail.getTime() / 60;
                int second = (int) (userDetail.getTime() % 60);
                int hour = (int) (t / 60);
                int minute = (int) (t % 60);
                mTvOnlineTime.setText(String.format("%02d:%02d:%02d", hour, minute, second));

                vlbGoOut.setVisibility(View.VISIBLE);
                vlbShutUp.setVisibility(View.VISIBLE);
                if (userDetail.isBeiAdmin()) {
                    vlbSettingAdministrator.setVisibility(View.GONE);
                    vlbCancelAdministrator.setVisibility(View.VISIBLE);
                } else {
                    vlbSettingAdministrator.setVisibility(View.VISIBLE);
                    vlbCancelAdministrator.setVisibility(View.GONE);
                }

                //钻石
                if (userDetail.getDiamond() > 0) {
                    mIvZuanShi.setVisibility(View.VISIBLE);
                } else {
                    mIvZuanShi.setVisibility(View.GONE);
                }
            }
            //4.场控（点击非主播，踢出，禁言）
            else if (userDetail.isAdmin() && !userDetail.getBeiUserId().equals(userDetail.getGirlUid())
                    &&!userDetail.isBeiAdmin()) {//场控查看非主播非场控
                vlbGoOut.setVisibility(View.VISIBLE);
                vlbShutUp.setVisibility(View.VISIBLE);
                vlbSettingAdministrator.setVisibility(View.GONE);
                vlbCancelAdministrator.setVisibility(View.GONE);
                mIvZuanShi.setVisibility(View.GONE);
            }

            //5.整理
            if (vlbIsFriend.getVisibility() == View.GONE &&
                    vlbAddFriend.getVisibility() == View.GONE &&
                    vlbGoOut.getVisibility() == View.GONE &&
                    vlbShutUp.getVisibility() == View.GONE &&
                    vlbCancelAdministrator.getVisibility() == View.GONE &&
                    vlbSettingAdministrator.getVisibility() == View.GONE) {

                mllSetting.setVisibility(View.GONE);
                viewLine.setVisibility(View.GONE);

            } else {
                mllSetting.setVisibility(View.VISIBLE);
                viewLine.setVisibility(View.VISIBLE);
                for(int i=0;i<mllSetting.getChildCount();i++){
                    View view=mllSetting.getChildAt(i);
                    if(view.getVisibility()==View.VISIBLE){
                        ((ViewLiveButton)view).dissmissLine();
                    }
                }

            }
        }
    }

    /**
     * 处理统一的消息
     */
    private void dealTotal() {

        if (userDetail != null) {

            //用户头像
            if (!"".equals(userDetail.getAvatar())) {
                ImageLoader.loadCircleAvatar(getActivity(), userDetail.getAvatar(), mIvHead);
            }
            //昵称
            mTvNickname.setText(userDetail.getNickname());
            mTvID.setText("ID：" + userDetail.getBeiUserId());

            //性别
            if (userDetail.getBeiGender() == LiveUserDetail.SEX_MAN) {
                mIvSex.setImageResource(R.drawable.icon_boy);
            } else if (userDetail.getBeiGender() == LiveUserDetail.SEX_WOMAN) {
                mIvSex.setImageResource(R.drawable.icon_girl);
            }
            //爵位等级
//            if ("".equals(userDetail.getNobility_icon())) {
//                mIvJueWei.setVisibility(View.GONE);
//            } else {
//                ImageLoader.loadFitCenter(getActivity(), userDetail.getNobility_icon(), mIvJueWei);
//                mIvJueWei.setVisibility(View.VISIBLE);
//            }

            NobilityList.Nobility tmpNobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(userDetail.getNobility_level(),userDetail.getBeiGender());
            String icon=tmpNobility.getTitle_icon();

            if ("".equals(icon)) {
                mIvJueWei.setVisibility(View.GONE);
            } else {
                ImageLoader.loadFitCenter(getActivity(), icon, mIvJueWei);
                mIvJueWei.setVisibility(View.VISIBLE);
            }
            //vip
            if (userDetail.getVip() == LiveUserDetail.VIP_NO) {
                mIvVIP.setVisibility(View.GONE);

            } else if (userDetail.getVip() == LiveUserDetail.VIP_YES) {
                mIvVIP.setVisibility(View.VISIBLE);
            }


            mLLreport.setVisibility(View.GONE);
            mllGift.setVisibility(View.GONE);
            mllSetting.setVisibility(View.GONE);
            vlbIsFriend.setVisibility(View.GONE);
            vlbAddFriend.setVisibility(View.GONE);
            vlbGoOut.setVisibility(View.GONE);
            vlbShutUp.setVisibility(View.GONE);
            vlbSettingAdministrator.setVisibility(View.GONE);
            vlbCancelAdministrator.setVisibility(View.GONE);
            mIvZuanShi.setVisibility(View.GONE);

            //关闭按钮
            mIvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LiveUserCardDialog.this.dismiss();
                }
            });

            //举报
            mLLreport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    UIShow.showLiveReportDialog(getActivity(), userDetail.getUserId(), userDetail.getBeiUserId(),new LiveReportDialog.OnItemClickListener(){
                        @Override
                        public void onReportSuc() {

                            LiveUserCardDialog.this.dismiss();
                        }
                    });
                }
            });

            //添加好友
            vlbAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(userDetail.getBeiUserId().equals(userDetail.getGirlUid())){//添加主播为好友

                        addFriendToGirl();
                    }else{ //添加用户为好友

                        addFriendToUser();
                    }
                }
            });
            //踢出
            vlbGoOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Map<String, Object> params = new HashMap<>();
                    params.put("uid", userDetail.getBeiUserId());

                    Statistics.userBehavior(SendPoint.page_live_usermanage_tichu,params);

                    UIShow.showLiveBaseDialog(getActivity(), "用户 " + userDetail.getNickname() + " 将被您踢出直播间！", "确定踢出", "再考虑一下", new LiveBaseDialog.OnItemClickListener() {
                        @Override
                        public void onConfirm() {
                            dealOperator(TICHU);
                            Statistics.userBehavior(SendPoint.page_live_alert_tichu_confirm,params);
                        }

                        @Override
                        public void onCancel() {
                            Statistics.userBehavior(SendPoint.page_live_alert_tichu_cancel);
                        }
                    });
                }
            });
            //禁言
            vlbShutUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Map<String, Object> params = new HashMap<>();
                    params.put("uid", userDetail.getBeiUserId());

                    Statistics.userBehavior(SendPoint.page_live_usermanage_jinyan,params);

                    UIShow.showLiveBaseDialog(getActivity(), "用户 " + userDetail.getNickname() + " 将被您直播禁言！", "确定禁言", "再考虑一下", new LiveBaseDialog.OnItemClickListener() {
                        @Override
                        public void onConfirm() {
                            dealOperator(JINYAN);
                            Statistics.userBehavior(SendPoint.page_live_alert_jinyan_confirm,params);
                        }

                        @Override
                        public void onCancel() {
                            Statistics.userBehavior(SendPoint.page_live_alert_jinyan_cancel);
                        }
                    });
                }
            });
            //设为场控
            vlbSettingAdministrator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Map<String, Object> params = new HashMap<>();
                    params.put("uid", userDetail.getBeiUserId());
                    dealOperator(TIANJIACHANGKONG);
                    Statistics.userBehavior(SendPoint.page_live_seting_manage_changkong,params);
                }
            });
            //取消场控
            vlbCancelAdministrator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dealOperator(QUXIAOCHANGKONG);
                }
            });
        }
    }

    /**
     * 添加好友(用户)
     */
    private void addFriendToUser() {

        RequestComplete complete=new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {

                if(response!=null){
                    if (response.isOk()){
                        PToast.showShort("添加好友成功");
                        LiveUserCardDialog.this.dismiss();
                    }
                }
            }
        };
        UIShow.showAddFriendGiftDialog(getActivity(),userDetail.getUserId(),userDetail.getBeiUserId(), "", complete);
    }

    /**
     * 添加好友(主播)
     */
    private void addFriendToGirl(){
        OnSendGiftCallbackListener addFriendComplete = new OnSendGiftCallbackListener() {
            @Override
            public void onSendGiftCallback(boolean success, SendGiftCallbackBean bean) {
                if (success) {
                    PToast.showShort("添加好友成功");

                    if(mDissmissAddFirend!=null){
                        mDissmissAddFirend.dissmissAddFriend();
                    }

                    LiveUserCardDialog.this.dismiss();

                }
            }
        };
        UIShow.showAddFriendGiftDialog(getActivity(), userDetail.getBeiUserId(), userDetail.getRoomId(), "", addFriendComplete);
    }

    /**
     * 参数 {
     * "roomid":110874891 //房间
     * "op":1,        // 0-正常，1-禁言，2-添加场控 3-踢出 4- 取消场控
     * "bid":1001,    //被禁言，踢出，设置为场控，取消场控 的用户，
     * <p>
     * }
     *
     * @param operator
     */
    private void dealOperator(final int operator) {

        LoadingDialog.show(getActivity());
        final HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("platform", "2");//android为2
        postParams.put("roomid", userDetail.getRoomId());//房间
        postParams.put("op", operator);//操作
        postParams.put("bid", userDetail.getBeiUserId());//类型
        if(operator==TICHU||operator==JINYAN){
            postParams.put("txt", userDetail.getCmdMessage().trim());//cmd传过来的message
        }

        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.liveAdminOpt, postParams, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                LoadingDialog.closeLoadingDialog();
                if (response.isOk()) {
                    String tip = userDetail.getNickname();
                    switch (operator) {

                        case TIANJIACHANGKONG:
                            tip = tip + "已被设为场控";
                            break;
                        case QUXIAOCHANGKONG:
                            tip = tip + "已被取消场控";
                            break;
                        case TICHU:
                            tip = tip + "已被踢出直播间";
                            break;
                        case JINYAN:
                            tip = tip + "已被禁言";
                            break;
                    }
                    PToast.showShort(tip);
                    LiveUserCardDialog.this.dismiss();
                    return;
                }
                PToast.showShort(response.getMsg());
            }
        });
    }

    public void setmDissmissAddFirend(DissmissAddFirend mDissmissAddFirend) {
        this.mDissmissAddFirend = mDissmissAddFirend;
    }

    public interface  DissmissAddFirend {
        void dissmissAddFriend();
    }
}
