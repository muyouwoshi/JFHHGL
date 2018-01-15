package com.juxin.predestinate.ui.live.view;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.UIUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by terry on 2017/9/12.
 *
 * 撩
 */

public class LiveLiaoPanel extends BasePanel implements View.OnClickListener{

    TextView itemTv1;
    TextView itemTv2;
    TextView itemTv3;
    TextView mLiaoTv;
    TextView mDoubleLiaoTipsTv;
    boolean isOpen = false,isDoubleLiao = false;
    String roomId,liaoTxt="";
    int sendCount = 0;

    public LiveLiaoPanel(@NonNull Context context) {
        super(context!=null?context: App.getActivity());
        this.setContentView(R.layout.live_liao_view);
        init();
    }

    public void setRoomId(String roomId){
        this.roomId = roomId;
        onTouchScreen();
    }

    private void init(){

        itemTv1 = (TextView) findViewById(R.id.live_liao_item1_tv);
        itemTv2 = (TextView) findViewById(R.id.live_liao_item2_tv);
        itemTv3 = (TextView) findViewById(R.id.live_liao_item3_tv);

        mLiaoTv = (TextView) findViewById(R.id.live_liao_tv);
        mDoubleLiaoTipsTv = (TextView) findViewById(R.id.live_lianliao_tips_tv);

        itemTv1.setOnClickListener(this);
        itemTv2.setOnClickListener(this);
        itemTv3.setOnClickListener(this);
        mLiaoTv.setOnClickListener(this);
    }

    /**
     * 触摸屏幕 关闭撩
     */
    public void onTouchScreen(){
        if (isOpen){
            closeLiaoViews();
            isOpen = false;
        }
    }

    @Override
    public void onClick(View v) {
        if ( v == mLiaoTv ){
            if (mDoubleLiaoTipsTv.getVisibility() == View.VISIBLE){
                mDoubleLiaoTipsTv.setVisibility(View.GONE);
            }
            if (isOpen){
                closeLiaoViews();
            }else{
                getFlirtSetting();
                showLiaoViews();
            }
            isOpen = !isOpen;
        }else if (v == itemTv1 || v == itemTv2 || v == itemTv3){
            isOpen = false;
            isDoubleLiao = true;
            closeLiaoViews();
            if (TextUtils.isEmpty(mLiaoTv.getText().toString())){
                mDoubleLiaoTipsTv.setVisibility(View.VISIBLE);
            }
            liaoTxt = ((TextView)v).getText().toString();
            Integer tag = Integer.parseInt((String)v.getTag());
            Map<String,Object> map = new HashMap<>();
            map.put("index",tag-1);
            map.put("content",liaoTxt);
            Statistics.userBehavior(SendPoint.page_live_liao_click,map);

            if (sendCount>0){
                if (10 > ModuleMgr.getCenterMgr().getMyInfo().getDiamand()) {
                    UIShow.showGoodsDiamondDialog(getContext(), 10 - ModuleMgr.getCenterMgr().getMyInfo().getDiamand(),
                            Constant.OPEN_FROM_LIVE, Long.parseLong(roomId), "");
                }else{
                    handleDoubleLiao(liaoTxt,(String)v.getTag());
                }
            }else{
                handleDoubleLiao(liaoTxt,(String)v.getTag());
            }
        }
    }

    /**
     * 处理连撩
     * @param txt
     */
    private void handleDoubleLiao(String txt,String txtid){
        sendCount ++;
        sendFlirtInfo(txt,txtid);
        mLiaoTv.setText("119s");
        mLiaoTv.setBackgroundResource(R.drawable.live_user_liao_ing);
        if (countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer.start();
        }
    }

    /**
     *  倒计时
     */
    private CountDownTimer countDownTimer = new CountDownTimer(120*1000,1000){
        @Override
        public void onTick(long millisUntilFinished) {
            mLiaoTv.setText(millisUntilFinished/1000+"s");
            if (millisUntilFinished <= 114*1000){
                mDoubleLiaoTipsTv.setVisibility(View.GONE);
            }
        }

        @Override
        public void onFinish() {
            sendCount = 0;
            isDoubleLiao =false;
            mLiaoTv.setText("");
            mLiaoTv.setBackgroundResource(R.drawable.live_user_liao_normal);
        }
    };

    /**
     * 显示撩选项
     */
    private void showLiaoViews(){
//        int duration = 400;
//        float distance = UIUtil.dip2px(context,50);
//        float angle1 = (float)(140f*Math.PI/180);
//        float angle2 = (float)(90f*Math.PI/180);
//        float angle3 = (float)(40f*Math.PI/180);
//
//        Interpolator interpolator = getInterpolator(0.2f, 1f, 0.2f, 1f);
//        float translationX_icon1 = (float) (distance * Math.cos(angle1));
//        float translationY_icon1 = -(float) (distance * Math.sin(angle1));
//        float translationX_icon2 = (float) (distance * Math.cos(angle2));
//        float translationY_icon2 = -(float) (distance * Math.sin(angle2));
//        float translationX_icon3 = (float) (distance * Math.cos(angle3));
//        float translationY_icon3 = -(float) (distance * Math.sin(angle3));
//
//        startAnim(itemTv1,translationX_icon1,translationY_icon1,1,duration,interpolator);
//        startAnim(itemTv2,translationX_icon2,translationY_icon2,1,duration,interpolator);
//        startAnim(itemTv3,translationX_icon3,translationY_icon3,1,duration,interpolator);

        int duration = 400;

        Interpolator interpolator = getInterpolator(0.2f, 1f, 0.2f, 1f);
        float translationX_icon1 = 0;
        float translationY_icon1 = -(float) UIUtil.dip2px(context,130);
        float translationX_icon2 = 0;
        float translationY_icon2 = -(float) UIUtil.dip2px(context,90);;
        float translationX_icon3 = 0;
        float translationY_icon3 = -(float) UIUtil.dip2px(context,48);;

        startAnim(itemTv1,translationX_icon1,translationY_icon1,1,duration,interpolator);
        startAnim(itemTv2,translationX_icon2,translationY_icon2,1,duration,interpolator);
        startAnim(itemTv3,translationX_icon3,translationY_icon3,1,duration,interpolator);
    }

    /**
     * 关闭撩选项
     */
    private void closeLiaoViews(){
        int duration = 400;

        Interpolator interpolator = getInterpolator(0.2f, 1f, 0.2f, 1f);

        startAnim(itemTv1,0,0,0,duration,interpolator);
        startAnim(itemTv2,0,0,0,duration,interpolator);
        startAnim(itemTv3,0,0,0,duration,interpolator);
    }

    private void startAnim(final TextView view, float translationX, float translationY, final int alpha, int duration, Interpolator interpolator){
        view.animate().alpha(alpha).translationX(translationX).translationY(translationY)
                .setDuration(duration).setInterpolator(interpolator).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (alpha == 1){
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (alpha == 0){
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Interpolator getInterpolator(float x1, float x2, float y1, float y2) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 21) {
            return new PathInterpolator(x1, x2, y1, y2);
        } else {
            return new LinearInterpolator();
        }
    }

    private void getFlirtSetting(){
        HashMap<String,Object> postParam = new HashMap<>();
        postParam.put("roomid",roomId);//房间ID
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp( UrlParam.getFlirtSetting, postParam, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if(!response.isOk()){
                    PToast.showShort(response.getMsg());
                }else{
                    try {
                        JSONObject resJo = response.getResponseJson().getJSONObject("res");
                        if (resJo!=null ){
                            itemTv1.setText(resJo.optString("flirt_one"));
                            itemTv2.setText(resJo.optString("flirt_two"));
                            itemTv3.setText(resJo.optString("flirt_three"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendFlirtInfo(String text,String txtid){
        HashMap<String,Object> postParam = new HashMap<>();
        postParam.put("anthor_id",roomId);
        postParam.put("txt",text);
        postParam.put("diamond", ModuleMgr.getCenterMgr().getMyInfo().getDiamand());
        postParam.put("txt_id",txtid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp( UrlParam.flirtInformation, postParam, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if(!response.isOk()){
                    PToast.showShort(response.getMsg());
                }else{
                    try {
                        JSONObject resJo = response.getResponseJson().getJSONObject("res");
                        if (resJo!=null ){
                            int diamond = resJo.optInt("diamond");
                            if (diamond >=0){
                                ModuleMgr.getCenterMgr().getMyInfo().setDiamand(diamond);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
