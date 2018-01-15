package com.juxin.predestinate.ui.user.my;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.TypeConvertUtil;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.bean.my.SendGiftResultInfo;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.my.GiftHelper;


public class DiamondOpenVideoDlg extends Dialog implements View.OnClickListener, RequestComplete, GiftHelper.OnRequestGiftListCallback {
    private String otherId;
    private String channel_uid; // 统计用
    private Context mContext;
    private TextView tv_diamonds, tv_gift_name;
    private ImageView iv_pic;
    private String giftPic;
    private int giftDiamonds;
    private int gifId;
    private GiftsList.GiftInfo giftBean;

    public DiamondOpenVideoDlg(Context context, int giftid, String OtherId, String channel_uid) {
        super(context, R.style.dialog);
        mContext = context;
        this.gifId = giftid;
        initView(context);
        giftBean = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo(giftid);
        if (giftBean == null) {
            ModuleMgr.getCommonMgr().requestGiftList(this);
        }
        otherId = OtherId;
        this.channel_uid = channel_uid;
        initData();
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.f1_dlg_open_video, null);
        setContentView(view);
        view.findViewById(R.id.btn_open_video_ok).setOnClickListener(this);

        tv_diamonds = (TextView) findViewById(R.id.tv_open_video_need_diamonds);
        tv_gift_name = (TextView) findViewById(R.id.tv_open_video_gift_name);
        iv_pic = (ImageView) findViewById(R.id.iv_open_video_pic);
        findViewById(R.id.tv_open_video_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setCanceledOnTouchOutside(true);
    }

    private void initData() {
        giftBean = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo(gifId);
        giftNotFind();
        giftPic = giftBean.getPic();
        giftDiamonds = giftBean.getMoney();
        tv_diamonds.setText(giftDiamonds + mContext.getString(R.string.a_diamond));
        tv_gift_name.setText(giftBean.getName());
        ImageLoader.loadAvatar(mContext, giftPic, iv_pic);
    }

    private void giftNotFind() {
        if (null == giftBean) {
            PToast.showShort(mContext.getString(R.string.gift_not_find));
            dismiss();
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_video_ok:
                int dec = ModuleMgr.getCenterMgr().getMyInfo().getDiamand() - giftBean.getMoney();
                if (dec >= 0) {//赠送礼物
                    ModuleMgr.getChatMgr().sendGiftMsg("", otherId, giftBean.getId(), 1, 3);
                } else {
                    UIShow.showGoodsDiamondDialog(mContext, Math.abs(dec), -1, TypeConvertUtil.toLong(otherId), channel_uid);
                }
                dismiss();
                break;
        }
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        SendGiftResultInfo info = new SendGiftResultInfo();
        info.parseJson(response.getResponseString());
        ModuleMgr.getCenterMgr().getMyInfo().setDiamand(info.getDiamand());
        ModuleMgr.getChatMgr().sendGiftMsg(null, otherId + "", gifId, 1, 0);
        PToast.showShort(info.getMsg() + "");
    }

    @Override
    public void onRequestGiftListCallback(boolean isOk) {
        if (isOk) {
            initData();
            return;
        }
        giftNotFind();
    }
}
