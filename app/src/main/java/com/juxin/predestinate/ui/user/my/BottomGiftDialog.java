package com.juxin.predestinate.ui.user.my;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.bean.my.RedbagInfo;
import com.juxin.predestinate.bean.my.SendGiftResultInfo;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsDiscovery;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.JsonUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.module.util.my.GiftHelper;
import com.juxin.predestinate.ui.live.bean.SendGiftCallbackBean;
import com.juxin.predestinate.ui.live.callback.OnSendGiftCallbackListener;
import com.juxin.predestinate.ui.user.my.CommonDlg.GiftRedPackageDlg;
import com.juxin.predestinate.ui.user.my.CommonDlg.GiftRedPackageExplainDlg;
import com.juxin.predestinate.ui.user.my.CommonDlg.GiftRedPackageStatusDlg;
import com.juxin.predestinate.ui.user.my.adapter.GiftAdapter;
import com.juxin.predestinate.ui.user.my.adapter.GiftViewPagerAdapter;
import com.juxin.predestinate.ui.user.my.view.CustomViewPager;
import com.juxin.predestinate.ui.user.my.view.GiftPopView;
import com.juxin.predestinate.ui.user.my.view.PageIndicatorView;
import com.juxin.predestinate.ui.utils.CheckIntervalTimeUtil;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;
import com.third.wa5.sdk.common.utils.NetUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 送礼物弹框
 * Created by zm on 2017/3/.
 */
public class BottomGiftDialog extends BaseDialogFragment implements View.OnClickListener, RequestComplete, TextWatcher, ViewPager.OnPageChangeListener, GiftHelper.OnRequestGiftListCallback, GiftPopView.OnNumSelectedChangedListener {

    private TextView txvAllStone, txvPay, txvNeedStone, txvSend, txvLeft, txvRight, tvSendNum;
    private TextView txvSendNum;
    private RelativeLayout llSendNum;//礼物数量
    private LinearLayout llNum;
    private Button btnOpen;
    private PageIndicatorView pageIndicatorView;
    private List<GiftsList.GiftInfo> arrGifts = new ArrayList();
    private long uid;//收礼物的uid
    private String channel_uid;  // 统计用
    private int position = -1;
    private int pageCount;
    private List<GridView> mLists;
    private int curPage = 0;//当前页
    private CustomViewPager vpViews;
    private GiftViewPagerAdapter gvpAdapter;
    private int oldPosition = -1;
    private int sum = 0;
    private int onePageCount = 0;
    private GiftPopView gpvPop;
    private int num;
    private Context mContext;
    private boolean isDismiss;
    private CheckIntervalTimeUtil checkIntervalTimeUtil;

    private int fromTag = Constant.OPEN_FROM_INFO;
    private OnSendGiftCallbackListener complete; //live 直播间发礼物回调

    public BottomGiftDialog() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, -2);
        setCancelable(true);
    }

    public void setCtx(Context context) {
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.p1_bottom_gif_dialog);
        View contentView = getContentView();
        onePageCount = ((ModuleMgr.getAppMgr().getScreenWidth() - UIUtil.dp2px(17)) / UIUtil.dp2px(95)) * 2;//根据屏幕宽度计算列数
        initView(contentView);
        initGifts();
        reSetData();
        initGridView();
        return contentView;
    }

    //初始化UI
    private void initView(final View contentView) {
        checkIntervalTimeUtil = new CheckIntervalTimeUtil();
        checkIntervalTimeUtil.check(Constant.DLG_TIME);
        txvAllStone = (TextView) contentView.findViewById(R.id.bottom_gif_txv_allstone);
        txvNeedStone = (TextView) contentView.findViewById(R.id.bottom_gif_txv_needstone);
        txvSendNum = (TextView) contentView.findViewById(R.id.bottom_gif_txv_sendnum);
        llSendNum = (RelativeLayout) contentView.findViewById(R.id.bottom_gif_ll_sendnum);
        txvLeft = (TextView) contentView.findViewById(R.id.bottom_gif_txv_left);
        txvRight = (TextView) contentView.findViewById(R.id.bottom_gif_txv_right);
        pageIndicatorView = (PageIndicatorView) contentView.findViewById(R.id.bottom_gif_rlv_gif_indicator);
        vpViews = (CustomViewPager) contentView.findViewById(R.id.bottom_gift_vp_view);
        gpvPop = (GiftPopView) contentView.findViewById(R.id.bottom_gif_gpv);
        llNum = (LinearLayout) contentView.findViewById(R.id.bottom_gif_ll_num);
        btnOpen = (Button) contentView.findViewById(R.id.bottom_gif_btn_redbag);
        tvSendNum = (TextView) contentView.findViewById(R.id.bottom_gif_tv_sendnum);
        vpViews.setRow(2);

        //设置dot的size和背景
        pageIndicatorView.setSelectDot(12, 8, R.drawable.f1_dot_select);//选中
        pageIndicatorView.setUnselectDot(8, 8, R.drawable.f1_dot_unselect);//未选中

        findViewById(R.id.bottom_gif_view_blank).setOnClickListener(this);
        findViewById(R.id.bottom_gif_rl_top).setOnClickListener(this);
        contentView.findViewById(R.id.bottom_gif_txv_pay).setOnClickListener(this);
        contentView.findViewById(R.id.bottom_gif_txv_send).setOnClickListener(clickListener);
        txvLeft.setText("<");
        txvRight.setText(">");
        txvAllStone.setText(ModuleMgr.getCenterMgr().getMyInfo().getDiamand() + "");
        txvSendNum.addTextChangedListener(this);
        btnOpen.setOnClickListener(clickListener);
        txvSendNum.setOnClickListener(this);
        gpvPop.setOnNumSelectedChanged(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_gif_view_blank://点击空白消失
                dismiss();
                break;
            case R.id.bottom_gif_txv_pay://跳转充值页面
                if (getFromTag() == Constant.OPEN_FROM_HOT) { //在热门界面打开
                    StatisticsDiscovery.onClickGiftPay(uid);
                } else if (getFromTag() == Constant.OPEN_FROM_CHAT_FRAME) {
                    Statistics.userBehavior(SendPoint.chatframe_tool_gift_pay, uid);
                } else if (getFromTag() == Constant.OPEN_FROM_LIVE) {
                    Statistics.userBehavior(SendPoint.page_live_girl_pay);
                }
                dismiss();
                //如果没有钻石跳转充值弹窗，有砖石跳转到我的钻石界面
                if (ModuleMgr.getCenterMgr().getMyInfo().getDiamand() <= 0) {
                    UIShow.showGoodsDiamondDialogAndTag(getContext(), getFromTag(), uid, channel_uid);
                } else {
                    UIShow.showMyDiamondsAct(getContext());
                }

                break;
            case R.id.bottom_gif_txv_sendnum:
                gpvPop.setVisibility(View.VISIBLE);
                break;
            case R.id.bottom_gif_rl_top:
                gpvPop.setVisibility(View.GONE);
                break;
        }
    }

    private NoDoubleClickListener clickListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            if (position == -1) {//为选择礼物
                PToast.showShort(getContext().getString(R.string.please_select_a_gift));
                return;
            }
            GiftsList.GiftInfo info = arrGifts.get(position);
            switch (v.getId()) {
                case R.id.bottom_gif_btn_redbag://放入钱包
                    if (info != null && info.getType() == 1 && info.getCount() == 0){
                        GiftRedPackageExplainDlg dlg = new GiftRedPackageExplainDlg();
                        dlg.showDialog((FragmentActivity) App.getActivity());
                        return;
                    }
                    dismiss();
                    ModuleMgr.getCommonMgr().achieveRewardHongbao(new RequestComplete() {
                        @Override
                        public void onRequestComplete(HttpResponse response) {
                            if (response.isOk()) {
                                RedbagInfo info = new RedbagInfo();
                                info.parseJson(response.getResponseString());
                                GiftRedPackageDlg giftRedPackageDlg = new GiftRedPackageDlg(info.getRed_amount(), true, true);
                                giftRedPackageDlg.showDialog((FragmentActivity) App.getActivity());
                            } else {
                                PToast.showShort(response.getMsg());
                            }
                        }
                    });
                    break;
                case R.id.bottom_gif_txv_send://发送礼物按钮逻辑
                    if (info != null && info.getType() == 1 && info.getCount() == 0){
                        GiftRedPackageExplainDlg dlg = new GiftRedPackageExplainDlg();
                        dlg.showDialog((FragmentActivity) App.getActivity());
                        return;
                    }
                    int needStone = Integer.valueOf(txvNeedStone.getText().toString());
                    if (needStone > ModuleMgr.getCenterMgr().getMyInfo().getDiamand()) {
                        dismiss();
                        UIShow.showGoodsDiamondDialog(getContext(), needStone - ModuleMgr.getCenterMgr().getMyInfo().getDiamand(),
                                getFromTag(), uid, channel_uid);
                        return;
                    }
                    if (position == -1) {//为选择礼物
                        PToast.showShort(getContext().getString(R.string.please_select_a_gift));
                        return;
                    }

                    if (complete == null) {
                        if (arrGifts.get(position).getType() == 1) {
                            if (!NetUtil.getInstance().isNetConnect(getContext())) {
                                PToast.showShort(getContext().getString(R.string.tip_net_error));
                                return;
                            }
                            ModuleMgr.getChatMgr().sendRedbagMsg(uid + "", null);
                        } else {
                            if (num <= 0) return;
                            StatisticsMessage.chatGiveGift(uid, arrGifts.get(position).getId(), arrGifts.get(position).getMoney());

                            //统计
                            if (getFromTag() == Constant.OPEN_FROM_HOT) {
                                StatisticsDiscovery.onGiveGift(uid, arrGifts.get(position).getId(), arrGifts.get(position).getMoney());
                            }
                            ModuleMgr.getChatMgr().sendGiftMsg("", uid + "", arrGifts.get(position).getId(), num, 1, arrGifts.get(position).getGiftfrom());
                            if (getFromTag() == Constant.OPEN_FROM_INFO) {
                                UIShow.showPrivateChatAct(getContext(), uid, null);
                            }
                        }
                    } else {
                        //直播间发送礼物
                        ModuleMgr.getChatMgr().sendLiveGiftMsg(null, String.valueOf(uid), arrGifts.get(position).getId(), num, complete, false);
                        Statistics.userBehavior(SendPoint.page_live_girl_send);
                        SendGiftCallbackBean bean = new SendGiftCallbackBean();
                        bean.setRoom_id(String.valueOf(uid));
                        bean.setGift_count(num);
                        bean.setGift_id(arrGifts.get(position).getId());
                        complete.onSendGiftCallback(true, bean);
                    }

                    dismiss();
                    break;
                default:
                    break;
            }
        }
    };


    public void setToId(long to_id, String channel_uid) {
        this.uid = to_id;//设置接收礼物方的uid
        this.channel_uid = channel_uid;
    }

    /**
     * 初始化礼物列表
     */
    private void initGifts() {

        if (getFromTag() == Constant.OPEN_FROM_LIVE) {
            arrGifts = ModuleMgr.getCommonMgr().getCommonConfig().getGift().getLiveGifts();
        } else {
            arrGifts = ModuleMgr.getCommonMgr().getCommonConfig().getGift().getChatGifts();
            if (arrGifts.size() > 0) {
                arrGifts.addAll(0, ModuleMgr.getCommonMgr().getBagList().getBagGifts());
            }
        }

        if (arrGifts.size() <= 0) {
            ModuleMgr.getCommonMgr().requestGiftList(this);
        }
    }

    private void initData() {
        //设置ViewPager监听
        vpViews.addOnPageChangeListener(this);
        gvpAdapter = new GiftViewPagerAdapter(getContext(), mLists);
        vpViews.setAdapter(gvpAdapter);
        pageIndicatorView.initIndicator(pageCount);
        if (pageCount > 1)
            pageIndicatorView.setSelectedPage(0);
    }

    private void initGridView() {
        pageCount = (int) Math.ceil(arrGifts.size() / (float) onePageCount);//计算总页数
        //分页数据处理
        mLists = new ArrayList<>();
        for (int i = 0; i < pageCount; i++) {
            final GridView gv = new GridView(getContext());
            final GiftAdapter gvGift = new GiftAdapter(getContext(), arrGifts, i, onePageCount);
            gv.setAdapter(gvGift);
            gv.setGravity(Gravity.CENTER);
            gv.setClickable(true);
            gv.setFocusable(false);
            gv.setNumColumns(onePageCount / 2);
            gv.setSelector(R.color.transparent);
            gv.setFadingEdgeLength(0);
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int select = curPage * onePageCount + position;
                    if (select < arrGifts.size()) {
                        reSetData();
                        arrGifts.get(select).setIsSelect(true);//设置为选中状态
                        if (oldPosition == select) {
                            sum++;
                        } else {
                            sum = 1;
                        }
                        oldPosition = select;//记录前一个position的位置
                        BottomGiftDialog.this.position = oldPosition;
                        onSelectNumChanged(sum, sum * arrGifts.get(select).getMoney(), select, arrGifts.get(select).getType(), arrGifts.get(select).getCount());
                        for (int i = 0; i < mLists.size(); i++) {
                            ((GiftAdapter) mLists.get(i).getAdapter()).notifyDataSetChanged();
                        }
                        GiftsList.GiftInfo info = arrGifts.get(select);
                        if (info != null && info.getType() == 1 && info.getCount() == 0){
                            GiftRedPackageExplainDlg dlg = new GiftRedPackageExplainDlg();
                            dlg.showDialog((FragmentActivity) App.getActivity());
                        }
                    }
                }
            });
            mLists.add(gv);
        }
        initData();
    }

    /**
     * 将礼物的选择状态置为false
     */
    private void reSetData() {
        for (int j = 0; j < arrGifts.size(); j++) {
            arrGifts.get(j).setIsSelect(false);
        }
    }

    /**
     * 选择礼物时用于更新界面的方法
     *
     * @param num      礼物数量
     * @param sum      礼物所需的钻石数量
     * @param position 被选中礼物的位置
     * @param type     礼物类型
     * @param position 礼物总数
     */
    public void onSelectNumChanged(int num, int sum, int position, int type, int count) {
        if (position == -1){
            PToast.showShort(getContext().getString(R.string.please_select_a_gift));
            gpvPop.setVisibility(View.GONE);
            return;
        }
        this.position = position;
        this.num = num;
        GiftsList.GiftInfo info = arrGifts.get(position);
        if (info == null) return;
        txvSendNum.setText(num + "");
        if (type == 1) {
            llNum.setVisibility(View.GONE);
            btnOpen.setVisibility(View.VISIBLE);
            tvSendNum.setVisibility(View.GONE);
            llSendNum.setVisibility(View.GONE);
        } else {
            llNum.setVisibility(View.VISIBLE);
            btnOpen.setVisibility(View.GONE);
            tvSendNum.setVisibility(View.VISIBLE);
            llSendNum.setVisibility(View.VISIBLE);
//        txvSendNum.setSelection(txvSendNum.length());
            txvNeedStone.setText(sum + "");
            gpvPop.setVisibility(View.GONE);
            if (info.getGiftfrom() == 1 && num > count) {
                txvSendNum.setText(count + "");
                this.num = count;
                PToast.showShort("您未拥有该数量的礼物");
            }
        }
    }

    @Override
    public void onNumSelectedChanged(int num) {
        if (position == -1){
            PToast.showShort(getContext().getString(R.string.please_select_a_gift));
            gpvPop.setVisibility(View.GONE);
            return;
        }
        GiftsList.GiftInfo info = arrGifts.get(position);
        if (info == null) return;
        this.num = num;
        txvSendNum.setText(num + "");
        if (info.getType() == 1) {
            llNum.setVisibility(View.GONE);
            btnOpen.setVisibility(View.VISIBLE);
        } else {
            llNum.setVisibility(View.VISIBLE);
            btnOpen.setVisibility(View.GONE);
            if (position > -1) {
                int sum = num * arrGifts.get(position).getMoney();
                txvNeedStone.setText(sum + "");
            }
            if (info.getGiftfrom() == 1 && num >= info.getCount()) {
                txvSendNum.setText(info.getCount() + "");
                this.num = info.getCount();
                PToast.showShort("您未拥有该数量的礼物");
            }
        }
        gpvPop.setVisibility(View.GONE);
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        SendGiftResultInfo info = new SendGiftResultInfo();
        info.parseJson(response.getResponseString());
        ModuleMgr.getCenterMgr().getMyInfo().setDiamand(info.getDiamand());
        ModuleMgr.getChatMgr().sendGiftMsg(null, uid + "", arrGifts.get(position).getId(), num, 0);
        PToast.showShort(info.getMsg() + "");
    }

    private CharSequence temp;
    private int selectionStart;
    private int selectionEnd;

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        temp = charSequence;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        try {
            selectionStart = txvSendNum.getSelectionStart();
            selectionEnd = txvSendNum.getSelectionEnd();
            if (position > -1) {
                int need = Integer.valueOf(editable.toString());
                sum = need;
                txvNeedStone.setText(need * arrGifts.get(position).getMoney() + "");
            }
            if (temp.length() > 4) {
                editable.delete(selectionStart - 1, selectionEnd);
                int tempSelection = selectionEnd;
                txvSendNum.setText(editable);
//                txvSendNum.setSelection(tempSelection);//设置光标在最后
            }
        } catch (Exception e) {
            txvNeedStone.setText(0 + "");
            PLogger.e("BottomGiftDialog---------" + e.toString());
        }
    }

    public boolean isDismiss() {
        if (checkIntervalTimeUtil != null && checkIntervalTimeUtil.check(Constant.DLG_TIME)) {
            isDismiss = true;
        }
        return isDismiss;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        isDismiss = true;
        super.onDismiss(dialog);
    }

    @Override
    public void dismissAllowingStateLoss() {
        isDismiss = true;
        super.dismissAllowingStateLoss();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        curPage = position;
        pageIndicatorView.setSelectedPage(curPage);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onRequestGiftListCallback(boolean isOk) {
        if (isOk) {
            if (getFromTag() == Constant.OPEN_FROM_LIVE) {
                arrGifts = ModuleMgr.getCommonMgr().getCommonConfig().getGift().getLiveGifts();
            } else {
                arrGifts = ModuleMgr.getCommonMgr().getCommonConfig().getGift().getChatGifts();
            }
            initGridView();
        }
    }

    public int getFromTag() {
        return fromTag;
    }

    public void setFromTag(int fromTag) {
        this.fromTag = fromTag;
    }

    public void setComplete(OnSendGiftCallbackListener complete) {
        this.complete = complete;
    }
}