package com.juxin.predestinate.ui.live;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.live.bean.LiveOtherGirl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结束直播 chengxiaobo on 2017/7/14 .
 */
public class GirlLiveEndActivity extends BaseActivity {

    private ImageView ivBackgroud; //直播结束背景图
    private ImageView ivClose;//关闭按钮
    private ImageView ivGirlHeadPic;//头像
    private TextView tvGirlName;//用户名称

    private ImageView ivFollow; //直播结束背景图
    private TextView tvTime;//直播时长
    private TextView tvGiftCount;//礼物个数
    private LinearLayout llBack;//返回首页
    private LinearLayout llOtherLive;//热门主播
    private RelativeLayout rlOtherLive1;//热门主播1
    private RelativeLayout rlOtherLive2;//热门主播1
    private RelativeLayout rlOtherLive3;//热门主播1
    private RelativeLayout[] rlArr;

    private String name; //用户名称
    private String url; //头像地址
    private boolean isGirl;//是不是主播
    private boolean isFriend;//是不是好友
    private String liveBg;//live背景图
    private long time;//时间戳
    private long charm;//当场魅力值
    private String uid; //uid
    private String roomid; //roomid
    private String channel_uid; // 渠道id

    private List<LiveOtherGirl> mList = null; //推荐其他主播列表


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_girl_live_end);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        url = intent.getStringExtra("url");
        isGirl = intent.getBooleanExtra("isGirl", true);
        isFriend = intent.getBooleanExtra("isFriend", true);
        liveBg = intent.getStringExtra("liveBg");
        time = intent.getLongExtra("time", 0);
        charm = intent.getLongExtra("charm", 0);
        uid = intent.getStringExtra("uid");
        roomid = intent.getStringExtra("roomid");
        channel_uid = intent.getStringExtra("channel_uid");

        if (isGirl) {

            /**{
             "live_second"                   :    0,    //直播时长秒数
             "meilizhi"                      :    0,    //魅力值(实际上是钻石的总收入)
             }
             */
            final Map<String, Object> params = new HashMap<>();
            params.put("live_second", time);
            params.put("meilizhi", charm);
            Statistics.userBehavior(SendPoint.page_live_gameover, params);
        }


        initView();

        getOtherLive(); //其他热门主播列表
    }

    /**
     * 不用沉浸式状态栏
     */
    @Override
    public boolean isFullScreen() {
        return true;
    }

    private void initView() {
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivBackgroud = (ImageView) findViewById(R.id.iv_girl_live_bg);
        ivGirlHeadPic = (ImageView) findViewById(R.id.iv_girl_head_pic);
        tvGirlName = (TextView) findViewById(R.id.tv_girl_name);

        ivFollow = (ImageView) findViewById(R.id.iv_follow);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvGiftCount = (TextView) findViewById(R.id.tv_gift_count);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GirlLiveEndActivity.this.finish();
            }
        });
        llOtherLive = (LinearLayout) findViewById(R.id.ll_other_live);
        rlOtherLive1 = (RelativeLayout) findViewById(R.id.rl_other_live_1);
        rlOtherLive2 = (RelativeLayout) findViewById(R.id.rl_other_live_2);
        rlOtherLive3 = (RelativeLayout) findViewById(R.id.rl_other_live_3);

        rlArr = new RelativeLayout[]{rlOtherLive1, rlOtherLive2, rlOtherLive3};

        tvGirlName.setText(name);
        ImageLoader.loadCircleAvatar(this, url, ivGirlHeadPic);
        ImageLoader.loadBlur(this, liveBg, ivBackgroud, 15);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GirlLiveEndActivity.this.finish();
            }
        });

        if (isGirl) {
            ivFollow.setVisibility(View.GONE);
        } else {
            ivFollow.setVisibility(isFriend ? View.GONE : View.VISIBLE);

            ivFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    postPPCSource(uid, channel_uid);

                    Statistics.userBehavior(SendPoint.page_live_gameover_mkfriend);
                    SourcePoint.getInstance().lockSource(GirlLiveEndActivity.this, "plusfirend_sendgift");
                    RequestComplete complete = new RequestComplete() {
                        @Override
                        public void onRequestComplete(HttpResponse response) {

                            if (response != null) {
                                if (response.isOk()) {
                                    ivFollow.setVisibility(View.GONE);
                                    PToast.showShort("添加好友成功");
                                }
                            }
                        }
                    };
                    UIShow.showAddFriendGiftDialog(GirlLiveEndActivity.this, uid, roomid, "", complete);
                }
            });
        }

        long t = time / 60;
        int second = (int) (time % 60);
        int hour = (int) (t / 60);
        int minute = (int) (t % 60);

        tvTime.setText(String.format("%02d:%02d:%02d", hour, minute, second));
        tvGiftCount.setText(charm + "");
    }

    /**
     * 获取推荐的主播
     */
    private void getOtherLive() {

        final HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("platform", "2");//android为2

        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.OtherPopularList, postParams, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                LoadingDialog.closeLoadingDialog();
                if (response.isOk()) {
                    try {
                        JSONObject resJo = response.getResponseJson().getJSONObject("res");
                        if (resJo != null) {
                            JSONArray jsonArray = resJo.optJSONArray("list");
                            mList = new ArrayList<LiveOtherGirl>();

                            if (jsonArray != null) {

                                for (int i = 0; i < jsonArray.length(); ++i) {
                                    LiveOtherGirl bean = new LiveOtherGirl();
                                    bean.parseJson(jsonArray.getJSONObject(i));
                                    mList.add(bean);
                                }
                                setOtherLiveView();
                            }
                        }
                    } catch (org.json.JSONException e) {

                    }
                    return;
                }
                PToast.showShort(response.getMsg());
            }
        });
    }

    /**
     * 获取完数据，设置View
     */
    private void setOtherLiveView() {

        if (mList != null && mList.size() > 0) {
            llOtherLive.setVisibility(View.VISIBLE);
        }

        if (mList.size() == 1) {
            rlOtherLive2.setVisibility(View.INVISIBLE);
            rlOtherLive3.setVisibility(View.INVISIBLE);
        }
        if (mList.size() == 2) {
            rlOtherLive3.setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < mList.size(); ++i) {
            setOtherGirlView(mList.get(i), rlArr[i]);
        }
    }

    /**
     * 设置view的点击事件
     */
    private void setOtherGirlView(LiveOtherGirl liveOtherGirl, RelativeLayout rlOtherLive) {

        ImageView imageView = (ImageView) rlOtherLive.findViewById(R.id.iv_other_live);
        TextView tv = (TextView) rlOtherLive.findViewById(R.id.tv_nikename);
        tv.setText(liveOtherGirl.getNickname());
        ImageLoader.loadRoundCenterCrop(this, liveOtherGirl.getPic(), imageView, UIUtil.dp2px(2.5f),
                R.drawable.live_end_live_zhanwei, R.drawable.live_end_live_zhanwei);

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }

    private void postPPCSource(String uid, String channel_uid) {
        if (uid.matches("[0-9]+") && channel_uid.matches("[0-9]+")) {
            SourcePoint.getInstance().lockPPCSource(Long.parseLong(uid), Integer.parseInt(channel_uid));
        }
    }
}
