package com.juxin.predestinate.ui.discover;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsDiscovery;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;

import org.json.JSONObject;

import java.util.Random;

/**
 * 筛选打招呼对象弹框
 * Created by duanzheng on 2017/7/13.
 */

public class GroupSayHelloDialog extends BaseDialogFragment implements View.OnClickListener {

    private LinearLayout region_flowlayout;
    private LinearLayout facescore_flowlayout;
    private LinearLayout video_album_flowlayout;
    private String[] region_arr = new String[]{"同城", "全国"};
    private String[] facescore_arr = new String[]{"90分", "80分", "70分", "全部"};
    private String[] video_album_arr = new String[]{"有视频", "有相册", "视频&相册"};
    private int region_flowlayout_select_index = 1;
    private int facescore_flowlayout_select_index = 1;
    private int video_album_flowlayout_select_index = 1;
    private TextView send;
    private int count = 0;

    private TextView all_count_tv;
    private LinearLayout sayHello;
    private LinearLayout sayHelloProgress;
    private LinearLayout sayHelloOk;
    private ProgressBar all_progress;
    private ValueAnimator anim;
    private TextView cancal;
    private TextView all_count_ok;
    private TextView sayHelloProgress_tv;
    private TextView ok;
    private TextView progress_count_all;
    private LinearLayout root;
    private LinearLayout sayHelloLayout;

    public GroupSayHelloDialog() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, -1);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_groupsayhello_bottom_dlg);
        View contentView = getContentView();
        initView(contentView);
        initData();
        return contentView;
    }

    private void initView(View contentView) {
        root = (LinearLayout) contentView.findViewById(R.id.root);

        region_flowlayout = (LinearLayout) contentView.findViewById(R.id.region_flowlayout);
        facescore_flowlayout = (LinearLayout) contentView.findViewById(R.id.facescore_flowlayout);
        video_album_flowlayout = (LinearLayout) contentView.findViewById(R.id.video_Album_flowlayout);

        send = (TextView) contentView.findViewById(R.id.send);
        all_count_tv = (TextView) contentView.findViewById(R.id.all_count_tv);

        sayHello = (LinearLayout) contentView.findViewById(R.id.sayHello);
        sayHelloProgress = (LinearLayout) contentView.findViewById(R.id.sayHelloProgress);
        sayHelloOk = (LinearLayout) contentView.findViewById(R.id.sayHelloOk);
        all_progress = (ProgressBar) contentView.findViewById(R.id.all_progress);
        cancal = (TextView) contentView.findViewById(R.id.cancal);
        all_count_ok = (TextView) contentView.findViewById(R.id.all_count_ok);

        sayHelloProgress_tv = (TextView) contentView.findViewById(R.id.sayHelloProgress_tv);
        ok = (TextView) contentView.findViewById(R.id.ok);
        progress_count_all = (TextView) contentView.findViewById(R.id.progress_count_all);

        sayHelloLayout = (LinearLayout) contentView.findViewById(R.id.sayHelloLayout);

    }

    private void initData() {
        //loadingDialog = new LoadingDialog(getActivity());
        send.setOnClickListener(this);
        cancal.setOnClickListener(this);
        ok.setOnClickListener(this);
        root.setOnClickListener(this);
        sayHelloLayout.setOnClickListener(this);
        getData(true);
        region_add_item();
        facescore_add_item();
        video_album_add_item();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        count = 0;
    }

    private void getData(final boolean isGetCount) {
        ModuleMgr.getCommonMgr().qunSayHelloCount(region_flowlayout_select_index, facescore_flowlayout_select_index, video_album_flowlayout_select_index, isGetCount, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                try {
                    String str = response.getResponseString();
                    JSONObject jso = new JSONObject(str);
                    if ( "ok".equals(jso.optString("status")) && jso.optJSONObject("res") != null) {
                        if (isGetCount) {
                            count = jso.optJSONObject("res").optInt("count");
                            all_count_tv.setText(count + "");
                        } else {
                            setCancelable(false);
                            all_progress.setMax(count);
                            progress_count_all.setText(count + "人");
                            sayHello.setVisibility(View.GONE);
                            sayHelloProgress.setVisibility(View.VISIBLE);
                            anim = ValueAnimator.ofFloat(0f, 100f);
                            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    float fraction = animation.getAnimatedFraction();
                                    int progress = (int) (fraction * count);
                                    all_progress.setProgress(progress);
                                    if (progress == count) {
                                        setCancelable(true);
                                        sayHelloProgress.setVisibility(View.GONE);
                                        sayHelloOk.setVisibility(View.VISIBLE);
                                        all_count_ok.setText(count + "");
                                    } else {
                                        sayHelloProgress_tv.setText(progress + "");
                                    }
                                }
                            });
                            anim.setInterpolator(new AccelerateInterpolator());
                            int random = new Random().nextInt(5) % (5 - 3 + 1) + 3;
                            anim.setDuration(random * 1000);
                            anim.start();
                        }
                    } else {
                        PToast.showLong(jso.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void region_add_item() {
        for (int i = 0; i < region_arr.length; i++) {
            final TextView tv = (TextView) View.inflate(getContext(), R.layout.groupsayhellodlg_item, null);
            tv.setText(region_arr[i]);
            if (i == region_flowlayout_select_index - 1) {
                tv.setSelected(true);
            } else {
                tv.setSelected(false);
            }
            final int finalI = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    region_flowlayout.getChildAt(region_flowlayout_select_index - 1).setSelected(false);
                    region_flowlayout_select_index = finalI + 1;
                    tv.setSelected(true);
                    getData(true);
                }
            });
            if (i < region_arr.length - 1) {
                LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tvLp.rightMargin = (int) getResources().getDimension(R.dimen.px18_dp);
                tv.setLayoutParams(tvLp);
            }
            region_flowlayout.addView(tv);
        }
    }

    private void facescore_add_item() {
        for (int i = 0; i < facescore_arr.length; i++) {
            final TextView tv = (TextView) View.inflate(getContext(), R.layout.groupsayhellodlg_item, null);
            tv.setText(facescore_arr[i]);
            if (i == facescore_flowlayout_select_index - 1) {
                tv.setSelected(true);
            } else {
                tv.setSelected(false);
            }
            final int finalI = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    facescore_flowlayout.getChildAt(facescore_flowlayout_select_index - 1).setSelected(false);
                    facescore_flowlayout_select_index = finalI + 1;
                    tv.setSelected(true);
                    getData(true);
                }
            });
            if (i < facescore_arr.length - 1) {
                LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tvLp.rightMargin = (int) getResources().getDimension(R.dimen.px18_dp);
                tv.setLayoutParams(tvLp);
            }
            facescore_flowlayout.addView(tv);
        }
    }

    private void video_album_add_item() {
        for (int i = 0; i < video_album_arr.length; i++) {
            final TextView tv = (TextView) View.inflate(getContext(), R.layout.groupsayhellodlg_item, null);
            tv.setText(video_album_arr[i]);
            if (i == video_album_flowlayout_select_index - 1) {
                tv.setSelected(true);
            } else {
                tv.setSelected(false);
            }
            final int finalI = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    video_album_flowlayout.getChildAt(video_album_flowlayout_select_index - 1).setSelected(false);
                    video_album_flowlayout_select_index = finalI + 1;
                    tv.setSelected(true);
                    getData(true);
                }
            });
            if (i < video_album_arr.length - 1) {
                LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tvLp.rightMargin = (int) getResources().getDimension(R.dimen.px18_dp);
                tv.setLayoutParams(tvLp);
            }
            video_album_flowlayout.addView(tv);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root:
                if(sayHelloProgress.getVisibility()==View.GONE){
                    dismiss();
                }

                break;

            case R.id.sayHelloLayout:

                break;
            case R.id.ok:
                Statistics.userBehavior(SendPoint.menu_faxian_alert_sayhello_confirm);
                dismiss();
                break;
            case R.id.send:
                // go数据提交index从1开始，大数据统计index从0开始
                StatisticsDiscovery.abTestSelectSayHi(region_flowlayout_select_index - 1, facescore_flowlayout_select_index - 1,
                        video_album_flowlayout_select_index - 1, count);

                //if (ModuleMgr.getCenterMgr().isCanGroupSayHi(getActivity())) {
//                if (ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
                if (!NetworkUtils.isConnected(getActivity())) {
                    PToast.showShort("网络异常,请稍后再试");
                    return;
                }
                getData(false);
//                } else {
//                    dismiss();
//                    UIShow.showOpenVipDialogNew(getContext(), null, "您非VIP用户，不可以解锁一键打招呼功能", false, "确认支付");
//                }
                //}

                //if(ModuleMgr.getCenterMgr().getMyInfo().isVip()){
                //}
                break;
            case R.id.cancal:
                setCancelable(true);
                Statistics.userBehavior(SendPoint.menu_faxian_alert_sayhello_cancel);
                if (anim != null && (anim.isStarted() || anim.isRunning())) {
                    anim.cancel();
                }

                int count = all_progress.getProgress();
                sayHelloProgress.setVisibility(View.GONE);
                sayHelloOk.setVisibility(View.VISIBLE);
                all_count_ok.setText(count + "");

                //dismiss();
                break;
        }
    }
}
