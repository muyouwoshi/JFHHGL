package com.juxin.predestinate.ui.user.my.CommonDlg;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.bean.my.BigGiftInfo;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.module.util.my.DlgMessageHelper;
import com.juxin.predestinate.ui.user.my.view.MultiImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * 创建日期：2017/7/17
 * 描述:密友升级弹框
 * 作者:zm
 */
public class BigGiftDlg extends BaseDialogFragment {

    private TextView tvTitle, tvLevel, tvBottomTitle, tvBottomDiscount, tvTopLevel;
    private LinearLayout llcontainer;

    private String name; //昵称
    private int level;   //等级


    private ImageView imgFromHead, imgToHead, imgGiftIcon;
    private TextView tvGiftName, tvFromName, tvToName;
    private MultiImageView imgGiftNum;

    private BigGiftInfo info;

    public BigGiftDlg() {
        settWindowAnimations(R.style.dialog);
        setGravity(Gravity.CENTER);
        setDialogSizeRatio(1, 0);
        setDimAmount(0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        window.setAttributes(windowParams);
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f2_big_gift_dlg);
        View view = getContentView();
        initView(view);
        return view;
    }

    public void setData(BigGiftInfo info) {
        this.info = info;
    }

    private void initView(View view) {
        imgFromHead = (ImageView) view.findViewById(R.id.big_gift_img_from_head);
        imgToHead = (ImageView) view.findViewById(R.id.big_gift_img_to_head);
        imgGiftIcon = (ImageView) view.findViewById(R.id.big_gift_img_gift_icon);
        tvGiftName = (TextView) view.findViewById(R.id.big_gift_tv_name);
        imgGiftNum = (MultiImageView) view.findViewById(R.id.big_gift__img_gift_num);
        tvFromName = (TextView) view.findViewById(R.id.big_gift_tv_from_name);
        tvToName = (TextView) view.findViewById(R.id.big_gift_tv_to_name);

        if (info != null){
            ImageLoader.loadCircleAvatar(getContext(),info.getFrom_avatar(),imgFromHead);
            ImageLoader.loadCircleAvatar(getContext(),info.getTo_avatar(),imgToHead);
            tvFromName.setText(info.getFrom_name());
            tvToName.setText(info.getTo_name());
            GiftsList.GiftInfo giftInfo = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo(info.getGift_id());
            if (giftInfo != null ){
                ImageLoader.loadFitCenter(getContext(),giftInfo.getPic(),imgGiftIcon);
                tvGiftName.setText(giftInfo.getName());
                imgGiftNum.setImageIds(convertData(info.getCount() + ""));
            }
        }

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissAllowingStateLoss();
                DlgMessageHelper.getInstance().HandleDlgMsgs();
            }
        }, 3000);
    }

    @Override
    public void showDialog(FragmentActivity context) {
        super.showDialog(context);
    }

    private List<Integer> convertData(String num) {
        int len = num.length();
        List<Integer> imgs = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            imgs.add(getImg(Integer.valueOf(num.charAt(i)) - 48));
        }
        return imgs;
    }

    private int getImg(int num){
        switch (num){
            case 0:
                return R.drawable.f2_img_no0;
            case 1:
                return R.drawable.f2_img_no1;
            case 2:
                return R.drawable.f2_img_no2;
            case 3:
                return R.drawable.f2_img_no3;
            case 4:
                return R.drawable.f2_img_no4;
            case 5:
                return R.drawable.f2_img_no5;
            case 6:
                return R.drawable.f2_img_no6;
            case 7:
                return R.drawable.f2_img_no7;
            case 8:
                return R.drawable.f2_img_no8;
            case 9:
                return R.drawable.f2_img_no9;
            default:
                return 0;
        }
    }
}
