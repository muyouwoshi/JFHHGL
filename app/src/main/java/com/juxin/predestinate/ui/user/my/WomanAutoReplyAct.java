package com.juxin.predestinate.ui.user.my;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.juxin.library.controls.smarttablayout.PagerItem;
import com.juxin.library.controls.smarttablayout.SmartTabLayout;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.WomanAutoReplyList;
import com.juxin.predestinate.module.local.msgview.chatview.input.ChatMediaPlayer;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.ui.user.my.adapter.ViewGroupPagerAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by duanzheng on 2017/7/10.
 */

public class WomanAutoReplyAct extends BaseActivity  {


    private int mType=SayHelloAutoReplyPanel.AutoReply;
    private List<PagerItem> listViews;//pagerItem集合
    private List<BasePanel> panls = new ArrayList<>(); // Tab页面列表
    private SmartTabLayout stlTitles;
    private ViewPager vpViewChange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_woman_autoreply);


        initView();
        initData();
    }

    //添加两个panel
    private void initViewsList() {
        panls.add(new SayHelloAutoReplyPanel(this,SayHelloAutoReplyPanel.AutoReply));
        panls.add(new SayHelloAutoReplyPanel(this,SayHelloAutoReplyPanel.SayHello));
        listViews = new ArrayList<>();
        listViews.add(new PagerItem("自动回复", panls.get(0).getContentView()));
        listViews.add(new PagerItem("主动打招呼", panls.get(1).getContentView()));
    }

    private void initView() {
        stlTitles = (SmartTabLayout) findViewById(R.id.my_attention_stl_titles);
        vpViewChange = (ViewPager) findViewById(R.id.my_attention_vPager);
        initViewsList();
        initViewPager();
        ((LinearLayout) stlTitles.getTabStrip()).setGravity(Gravity.CENTER_HORIZONTAL);//标题居中
        stlTitles.setCustomTabView(R.layout.f1_custom_table_view, R.id.tv_left_tab);//设置自定义标题
        stlTitles.setViewPager(vpViewChange);//设置viewpager

        stlTitles.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                SayHelloAutoReplyPanel pan = getPanel(mType);
                if(pan!=null){
                    pan.closePlayVoice();
                }
                mType=position==0?SayHelloAutoReplyPanel.AutoReply:SayHelloAutoReplyPanel.SayHello;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        mType=getIntent().getIntExtra("type",SayHelloAutoReplyPanel.AutoReply);
        setTitle(getString(R.string.auto_reply_title));
        setBackView();
        setTitleRightImg(R.drawable.woman_autoreply_add_item_ico, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDlg(mType);
            }
        });
    }

    private void initViewPager() {
        vpViewChange.setAdapter(new ViewGroupPagerAdapter(listViews));
    }


    /**
     * 展示添加自动回复弹框
     */
    public void showAddDlg(int mType) {
        Statistics.userBehavior(SendPoint.page_setautoreply_add);
        SayHelloAutoReplyPanel pan = getPanel(mType);
        if(pan==null){
            PToast.showShort("发生了错误,请稍后再试！");
            return;
        }
        /*if (pan.list.getChildCount() >= 5) {
            if(mType==SayHelloAutoReplyPanel.SayHello){
                PToast.showShort(getString(R.string.say_hello_exceed_count));
            }else{
                PToast.showShort(getString(R.string.auto_reply_exceed_count));
            }
            return;
        } else {*/
            WomanAutoReplyBottomDialog dlg = new WomanAutoReplyBottomDialog(this,mType);
            dlg.showDialog(WomanAutoReplyAct.this);
            pan.closePlayVoice();
            dlg.setSendCallBack(new WomanAutoReplyBottomDialog.SendCallBack() {
                @Override
                public void send(int arid, String content, int length, @WomanAutoReplyBottomDialog.Type int type, @SayHelloAutoReplyPanel.Type int mType) {
                    StatisticsUser.setAutoReplyContent(type == WomanAutoReplyBottomDialog.VOICE ? 0 : 1, content);

                    WomanAutoReplyList.AutoReply autoReply = new WomanAutoReplyList.AutoReply();
                    if (type == WomanAutoReplyBottomDialog.TXT) {
                        autoReply.setText(content);
                    } else {
                        autoReply.setTimespan(String.valueOf(length));
                        autoReply.setSpeech_url(content);
                    }
                    autoReply.setArid(arid);
                    SayHelloAutoReplyPanel pan = getPanel(mType);
                    if (pan.listObj == null) {
                        pan.listObj = new WomanAutoReplyList();
                    }
                    if (pan.listObj.getAuto_reply() == null) {
                        pan.listObj.setAuto_reply(new ArrayList<WomanAutoReplyList.AutoReply>());
                    }
                    pan. listObj.getAuto_reply().add(autoReply);

                    pan.addItemView();
                }
            });
        //}
    }

    //获取需要添加的panel容器
    private SayHelloAutoReplyPanel getPanel(int mType) {
        return (SayHelloAutoReplyPanel)( mType==SayHelloAutoReplyPanel.AutoReply?panls.get(0):panls.get(1));
    }

    @Override
    protected void onDestroy() {
        SayHelloAutoReplyPanel pan = getPanel(mType);
        if (pan != null) {//停止正在播放的音频
            pan.closePlayVoice();
        }
        super.onDestroy();
    }

    public interface CallBack {
        public void call(boolean isSuccess);
    }

}
