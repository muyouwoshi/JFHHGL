package com.juxin.predestinate.ui.user.my.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.view.roadlights.LMarqueeFactory;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.bean.my.GiftMessageList;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.bean.my.MarqueeMessageList;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 跑马灯item项
 * Created by zm on 2017/1/8
 */
public class GiftMessageInfoView extends LMarqueeFactory<RelativeLayout, MarqueeMessageList.MarqueeMessageInfo> {

    private Context mContext;

    public GiftMessageInfoView(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    @Override
    public RelativeLayout generateMarqueeItemView(MarqueeMessageList.MarqueeMessageInfo data, RelativeLayout convertView) {
        MyViewHolder vh;
        if (convertView == null){
            convertView = (RelativeLayout) inflate(R.layout.f2_marquee_panel_two);
            vh = new MyViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh = (MyViewHolder) convertView.getTag();
        }

        if (data == null) {
            return convertView;
        }

        if (data.getType() == 1) { //礼物信息
            vh.imgGiftNum.setVisibility(View.VISIBLE);
            vh.llHead.setVisibility(View.VISIBLE);
            vh.llTwo.setVisibility(View.VISIBLE);
            vh.llHeadTwo.setVisibility(View.GONE);
            vh.llThree.setVisibility(View.GONE);
            ImageLoader.loadCircleAvatar(mContext, data.getFrom_avatar(), vh.imgHead);
            vh.tvName.setText(data.getFrom_name());
            vh.tvCongratulation.setText(Html.fromHtml(mContext.getString(R.string.send_name, data.getTo_name())));
            GiftsList.GiftInfo info = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo(data.getGift_id());
            if (info != null) {
                ImageLoader.loadFitCenter(mContext, info.getPic(), vh.imgLevel);
            }
            vh.imgGiftNum.setImageIds(convertData(data.getCount() + ""));
        } else if (data.getType() == 2) { //爵位信息
            vh.imgGiftNum.setVisibility(View.GONE);
            vh.llHeadTwo.setVisibility(View.VISIBLE);
            vh.llThree.setVisibility(View.VISIBLE);
            vh.llHead.setVisibility(View.GONE);
            vh.llTwo.setVisibility(View.GONE);
            ImageLoader.loadCircleAvatar(mContext, data.getAvatar(), vh.imgHeadTwo);
            NobilityList.Nobility nobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(data.getLevel(), data.getGender());
            vh.tvNameTwo.setText(mContext.getString(R.string.marquee_upgrade, data.getNickname()));
            ImageLoader.loadFitCenter(mContext, nobility.getTitle_badge(), vh.imgIcon);
            vh.tvGiftName.setText(nobility.getStar_name() + nobility.getTitle_name());

        }
        return convertView;
    }

    class MyViewHolder {

        ImageView imgHead;
        ImageView imgHeadTwo;
        TextView tvName;
        TextView tvCongratulation;
        ImageView imgLevel;
        TextView tvGiftName;
        MultiImageView imgGiftNum;
        LinearLayout llThree;
        LinearLayout llTwo;
        TextView tvNameTwo;
        RelativeLayout rlName;
        ImageView imgIcon;
        LinearLayout llGift;
        LinearLayout llHead;
        LinearLayout llHeadTwo;

        public MyViewHolder(View convertView) {
            initView(convertView);
        }

        private void initView(View marqueePanel) {
            marqueePanel.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtil.dip2px(mContext, 52)));
            imgHead = (ImageView) marqueePanel.findViewById(R.id.marquee_panel_two_head);
            imgHeadTwo = (ImageView) marqueePanel.findViewById(R.id.marquee_panel_two_head_two);
            tvName = (TextView) marqueePanel.findViewById(R.id.marquee_panel_tv_from_name);
            tvCongratulation = (TextView) marqueePanel.findViewById(R.id.marquee_panel_tv_to_name);
            imgLevel = (ImageView) marqueePanel.findViewById(R.id.marquee_panel_img_gift_icon);
            tvGiftName = (TextView) marqueePanel.findViewById(R.id.marquee_panel_tv_gift_name);
            imgGiftNum = (MultiImageView) marqueePanel.findViewById(R.id.marquee_panel_img_gift_num);
            llThree = (LinearLayout) marqueePanel.findViewById(R.id.marquee_panel_three_ll);
            llTwo = (LinearLayout) marqueePanel.findViewById(R.id.marquee_panel_two_ll);
            tvNameTwo = (TextView) marqueePanel.findViewById(R.id.marquee_panel_tv_name);
            rlName = (RelativeLayout) marqueePanel.findViewById(R.id.marquee_panel_rl_name);
            imgIcon = (ImageView) marqueePanel.findViewById(R.id.marquee_panel_img_icon);
            llGift = (LinearLayout) marqueePanel.findViewById(R.id.marquee_panel_ll_gift);
            llHead = (LinearLayout) marqueePanel.findViewById(R.id.marquee_panel_two_ll_head);
            llHeadTwo = (LinearLayout) marqueePanel.findViewById(R.id.marquee_panel_two_ll_head_tw);
        }
    }

    private List<Integer> convertData(String num) {
        int len = num.length();
        List<Integer> imgs = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            imgs.add(getImg(Integer.valueOf(num.charAt(i)) - 48));
        }
        return imgs;
    }

    private int getImg(int num) {
        switch (num) {
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