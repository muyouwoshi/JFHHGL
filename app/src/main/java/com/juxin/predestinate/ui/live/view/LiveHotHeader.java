package com.juxin.predestinate.ui.live.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.discover.BannerConfig;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.discover.banner.BannerHelper;
import com.juxin.predestinate.ui.live.bean.LiveRoomBean;
import com.juxin.predestinate.ui.live.ui.LiveRecommendAct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 热门直播列表的HeaderView
 *
 * @author gwz
 */
public class LiveHotHeader extends BasePanel implements View.OnClickListener, RequestComplete {

    private TextView tvBigPic, tvSmallPic;
    private boolean isSmall = true, isBannerClose;
    private BigSmallSwitchListener listener;
    private BannerHelper bannerHelper;
    private View rlRecommend, ivBannerClose, bannerContainer;

    public LiveHotHeader(Context context) {
        super(context);
        setContentView(R.layout.view_header_live_hot);
        initView();
        refreshData();
    }

    private void initView() {
        tvBigPic = (TextView) findViewById(R.id.tv_big_pic);
        tvSmallPic = (TextView) findViewById(R.id.tv_small_pic);
        rlRecommend = findViewById(R.id.rl_recommend);
        ivBannerClose = findViewById(R.id.iv_banner_close);
        bannerContainer = findViewById(R.id.banner_container);
        tvBigPic.setOnClickListener(this);
        tvSmallPic.setOnClickListener(this);
        findViewById(R.id.tv_more).setOnClickListener(this);
        ivBannerClose.setOnClickListener(this);
        bannerHelper = new BannerHelper(getContext(), getContentView(), null);
        bannerHelper.showBanner();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_more:
                getContext().startActivity(new Intent(getContext(), LiveRecommendAct.class));
                break;
            case R.id.tv_big_pic:
                switchPic(false);
                break;
            case R.id.tv_small_pic:
                switchPic(true);
                break;
            case R.id.iv_banner_close:
                isBannerClose = true;
                findViewById(R.id.fl_banner).setVisibility(View.GONE);
                ivBannerClose.setVisibility(View.GONE);
                break;
            default:
        }
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        if (response.getUrlParam() == UrlParam.getRecommendLiveList) {
            if (response.isOk()) {
                parseRecommendData(response.getResponseString());
            }
        } else if (response.getUrlParam() == UrlParam.getLiveBanner) {
            if (response.isOk()) {
                parseBannerData(response.getResponseString());
            }
        }
    }

    private void switchPic(boolean small) {
        if (isSmall == small) {
            return;
        }
        isSmall = !isSmall;
        if (isSmall) {
            tvSmallPic.setTextColor(getContext().getResources().getColor(R.color.color_333333));
            Drawable smallPicSel = getContext().getResources().getDrawable(R.drawable.ic_live_small_pic_sel);
            smallPicSel.setBounds(0, 0, smallPicSel.getMinimumWidth(), smallPicSel.getMinimumHeight());
            tvSmallPic.setCompoundDrawables(smallPicSel, null, null, null);
            tvBigPic.setTextColor(getContext().getResources().getColor(R.color.color_999999));
            Drawable bigPicNor = getContext().getResources().getDrawable(R.drawable.ic_live_big_pic_nor);
            bigPicNor.setBounds(0, 0, bigPicNor.getMinimumWidth(), bigPicNor.getMinimumHeight());
            tvBigPic.setCompoundDrawables(bigPicNor, null, null, null);
        } else {
            tvBigPic.setTextColor(getContext().getResources().getColor(R.color.color_333333));
            Drawable bigPicSel = getContext().getResources().getDrawable(R.drawable.ic_live_big_pic_sel);
            bigPicSel.setBounds(0, 0, bigPicSel.getMinimumWidth(), bigPicSel.getMinimumHeight());
            tvBigPic.setCompoundDrawables(bigPicSel, null, null, null);
            tvSmallPic.setTextColor(getContext().getResources().getColor(R.color.color_999999));
            Drawable smallPicNor = getContext().getResources().getDrawable(R.drawable.ic_live_small_pic_nor);
            smallPicNor.setBounds(0, 0, smallPicNor.getMinimumWidth(), smallPicNor.getMinimumHeight());
            tvSmallPic.setCompoundDrawables(smallPicNor, null, null, null);
        }
        if (listener != null) {
            listener.onSwitch(isSmall);
        }
    }

    /**
     * 获取Banner数据
     */
    private void getBannerData() {
        if (isBannerClose) {
            return;
        }
        HashMap<String, Object> params = new HashMap<>(1);
        params.put("version", 1);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.getLiveBanner, params, this);
    }

    /**
     * 获取签约推荐数据
     */
    private void getRecommendData() {
        ModuleMgr.getHttpMgr().reqGetNoCacheHttp(UrlParam.getRecommendLiveList, null, this);
    }

    public void refreshData() {
        getBannerData();
        getRecommendData();
    }

    public void setListener(BigSmallSwitchListener listener) {
        this.listener = listener;
    }

    public interface BigSmallSwitchListener {
        void onSwitch(boolean isSmall);
    }

    private void parseBannerData(String strJson) {
        try {
            ArrayList<BannerConfig.Banner> bannerList = new ArrayList<>();
            JSONObject resJo = new JSONObject(strJson).getJSONObject("res");
            JSONArray bannerJa = resJo.getJSONArray("banner_list");
            for (int i = 0; i < bannerJa.length(); i++) {
                BannerConfig.Banner banner = new BannerConfig.Banner();
                banner.parseJson(bannerJa.getString(i));
                bannerList.add(banner);
            }
            bannerContainer.setVisibility(bannerList.isEmpty() ? View.GONE : View.VISIBLE);
            bannerHelper.updData(bannerList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseRecommendData(String strJson) {
        try {
            JSONObject resJo = new JSONObject(strJson).getJSONObject("res");
            JSONArray liveJa = resJo.getJSONArray("list");
            int cnt = liveJa.length() > 3 ? 3 : liveJa.length();
            if (cnt == 0) {
                rlRecommend.setVisibility(View.GONE);
                return;
            }
            for (int i = 0; i < cnt; i++) {
                LiveRoomBean liveRoomBean = new LiveRoomBean();
                JSONObject liveJo = liveJa.getJSONObject(i);
                setRecommendData(i, liveRoomBean.parseJson(liveJo));
            }
        } catch (JSONException e) {
            rlRecommend.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void setRecommendData(int pos, LiveRoomBean liveRoomBean) {
        View itemView = null;
        switch (pos) {
            case 0:
                itemView = findViewById(R.id.item_rec1);
                break;
            case 1:
                itemView = findViewById(R.id.item_rec2);
                break;
            case 2:
                itemView = findViewById(R.id.item_rec3);
                break;
            default:
        }
        ImageView ivPic = itemView.findViewById(R.id.iv_pic);
        ImageLoader.loadRoundAvatar(ivPic.getContext(), ImageLoader.checkOssImageUrl(liveRoomBean.pic, 256), ivPic);
        TextView tvName = itemView.findViewById(R.id.tv_name);
        tvName.setText(liveRoomBean.nickname);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //用Lite，Whole分支代码
            }
        });
    }
}
