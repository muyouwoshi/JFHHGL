package com.juxin.predestinate.ui.live.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.module.util.my.GiftHelper;
import com.juxin.predestinate.ui.live.callback.OnSendGiftCallbackListener;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播间添加好友送礼物弹框
 * Created by terry on 2017/7/19.
 */
public class AddFriendGiftDialog extends BaseDialogFragment implements View.OnClickListener,AppointGiftView.OnGiftItemClickListener, GiftHelper.OnRequestGiftListCallback{


    private List<GiftsList.GiftInfo> arrGifts = new ArrayList();
    private String room_id;//收礼物的roomid
    private String channel_uid;  // 统计用
    private long uid;//用户id
    private int fromTag = Constant.OPEN_FROM_INFO;
    private GiftsList.GiftInfo selectGift;//选中礼物

    private ImageView mCloseIv;
    private TextView mDiamondsTv;
    private TextView mRechargeTv;
    private TextView mSendTv;
    private ImageView mBannerIv;
    private FrameLayout mGiftContainer;
    private OnSendGiftCallbackListener requestComplete;//回调
    private RequestComplete requestComplete2;

    private int from=1;
    public static final int FROM_ZHIBO_ZHUBO =1;//给主播送礼物
    public static final int FROM_ZHIBO_USER =2; //给用户送礼物

    AppointGiftView giftView = null;

    public AddFriendGiftDialog() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, -2);
        setCancelable(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        from=getArguments().getInt("from",1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.l1_live_add_friend_gift_dialog);
        View contentView = getContentView();
        initGifts();
        reSetData();
        initView(contentView);
        return contentView;
    }

    //初始化UI
    private void initView(View contentView) {
        mCloseIv = (ImageView) contentView.findViewById(R.id.live_friend_dialog_close);
        mDiamondsTv = (TextView) contentView.findViewById(R.id.live_friend_dialog_diamonds_tv);
        mRechargeTv = (TextView) contentView.findViewById(R.id.live_friend_dialog_recharge_tv);
        mSendTv = (TextView) contentView.findViewById(R.id.live_friend_dialog_send_tv);
        mBannerIv = (ImageView) contentView.findViewById(R.id.live_friend_dialog_banner);
        mGiftContainer = (FrameLayout) contentView.findViewById(R.id.live_friend_dialog_gift);

        mRechargeTv.setOnClickListener(this);
        mSendTv.setOnClickListener(clickListener);
        mCloseIv.setOnClickListener(this);


        mDiamondsTv.setText(String.format("剩余：%1s钻石", ModuleMgr.getCenterMgr().getMyInfo().getDiamand()));

        int width = ModuleMgr.getAppMgr().getScreenWidth() - UIUtil.dp2px(17);
        int height = width  * 321 /1143;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
        mBannerIv.setLayoutParams(params);

        if (giftView!=null){
            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.gravity = Gravity.CENTER;
            mGiftContainer.addView(giftView,params1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.live_friend_dialog_recharge_tv://跳转充值页面
                Statistics.userBehavior(SendPoint.page_live_avatar_mkfriendadd_sendgirl_pay);
                dismiss();
                UIShow.showGoodsDiamondDialogAndTag(getContext(), getFromTag(), uid, channel_uid);
                break;
            case R.id.live_friend_dialog_close:
                dismiss();
                break;
        }
    }

    private NoDoubleClickListener clickListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.live_friend_dialog_send_tv://发送礼物按钮逻辑

                    if (selectGift == null) {//为选择礼物
                        PToast.showShort(getContext().getString(R.string.please_select_a_gift));
                        return;
                    }

                    if (selectGift.getMoney() > ModuleMgr.getCenterMgr().getMyInfo().getDiamand()) {
                        UIShow.showGoodsDiamondDialogAndTag(getContext(), getFromTag(), uid, channel_uid);
//                        PToast.showShort(getContext().getString(R.string.diamond_is_insufficient));
                        return;
                    }

                    if(from== FROM_ZHIBO_ZHUBO){

                        Statistics.userBehavior(SendPoint.page_live_avatar_mkfriendadd_sendgirl_zengsong);
                        // 发送礼物
                        ModuleMgr.getChatMgr().sendLiveGiftMsg(channel_uid, room_id, selectGift.getId(), 1,requestComplete,true);


                    }else if(from == FROM_ZHIBO_USER){ //roomId为用户id
                        ModuleMgr.getChatMgr().sendGiftMsg(channel_uid,room_id,selectGift.getId(),1,5,requestComplete2);
                    }
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    };


    public void setToId(long uid,String room_id, String channel_uid) {
        this.uid = uid;
        this.room_id = room_id;//设置接收礼物方的uid
        this.channel_uid = channel_uid;
    }

    //外部设置接口回调 live
    public void setRequestComplete(OnSendGiftCallbackListener requestComplete) {
        this.requestComplete = requestComplete;
    }

    public void setRequestComplete2(RequestComplete requestComplete2) {
        this.requestComplete2 = requestComplete2;
    }

    public void setGiftIds(Context context){
        giftView = new AppointGiftView(context);
        giftView.initGiftId(this);
    }

    /**
     * 初始化礼物列表
     */
    private void initGifts() {
        arrGifts = ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts();
        if (arrGifts.size() <= 0) {
            ModuleMgr.getCommonMgr().requestGiftList(this);
        }
    }

    /**
     * 将礼物的选择状态置为false
     */
    private void reSetData() {
        for (int j = 0; j < arrGifts.size(); j++) {
            arrGifts.get(j).setIsSelect(false);
        }
    }

    private CharSequence temp;
    private int selectionStart;
    private int selectionEnd;


    @Override
    public void onRequestGiftListCallback(boolean isOk) {
        if (isOk) {
            arrGifts = ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts();
//            initGridView();
        }
    }

    public int getFromTag() {
        return fromTag;
    }

    @Override
    public void onItemClick(GiftsList.GiftInfo info) {
        selectGift = info;
    }
}