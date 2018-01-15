package com.juxin.predestinate.ui.user.my;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.WomanAutoReplyList;
import com.juxin.predestinate.module.local.msgview.chatview.input.ChatMediaPlayer;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by duanzheng on 2017/7/26.
 */

public class SayHelloAutoReplyPanel extends BasePanel implements View.OnClickListener {
    public final static int AutoReply=1;//自动回复
    public final static int SayHello=2;//打招呼
    public int mType=AutoReply;
    private RelativeLayout empty_layout;
    private int selectIndex = -1;
    private ImageView select_Switch_iv;
    private WomanAutoReplyList.AutoReply select_AutoReply;
    private SeekBar sb_auto_invite_voice;
    private SeekBar sb_auto_invite_video;
    private TextView addItem;
    private LinearLayout common_net_error;
    private LinearLayout common_loading;
    private LinearLayout common_nodata;
    private ScrollView root;
    private TextView error_btn;
    public LinearLayout list;
    public WomanAutoReplyList listObj;
    private String playVoice = "";
    private boolean isClickSelectStatus = true;
    private TextView no_data_des;
    private LinearLayout autoVideoVoice;
    private TextView des;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                if(context instanceof WomanAutoReplyAct){
                    ((WomanAutoReplyAct)context).showAddDlg(mType);
                }

                break;
            case R.id.error_btn:
                reqData();
                break;
        }
    }

    public @interface Type{}

    public SayHelloAutoReplyPanel(Context context,@Type int type) {
        super(context);
        mType=type;
        setContentView(R.layout.sayhello_autoreply_panel);
        initView();
        initData();
        reqData();
    }

    private void initData() {
        error_btn.setOnClickListener(this);
        addItem.setOnClickListener(this);
        sb_auto_invite_video.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (listObj != null && listObj.getAuto_av_invite() != null) {
                        setWomanAutoReplyAvStatus(!listObj.getAuto_av_invite().isVideo(), listObj.getAuto_av_invite().isAudio());
                        StatisticsUser.setAutoReply(1, listObj.getAuto_av_invite().isVideo() ? 0 : 1);
                    }
                }
                return true;
            }
        });
        sb_auto_invite_voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (listObj != null && listObj.getAuto_av_invite() != null) {
                        setWomanAutoReplyAvStatus(listObj.getAuto_av_invite().isVideo(), !listObj.getAuto_av_invite().isAudio());
                        StatisticsUser.setAutoReply(2, listObj.getAuto_av_invite().isAudio() ? 0 : 1);
                    }
                }
                return true;
            }
        });

        if(mType == AutoReply){
            no_data_des.setText(context.getString(R.string.auto_reply_no_data));
            des.setText(getContext().getString(R.string.auto_reply_des));
            autoVideoVoice.setVisibility(View.VISIBLE);
        }else{
            des.setText(getContext().getString(R.string.auto_reply_des_2));
            no_data_des.setText(context.getString(R.string.say_hello_no_data));
            autoVideoVoice.setVisibility(View.GONE);
        }
        setViewStatus(false, true, false, false);
        reqData();
    }

    /**
     * 设置状态 错误 无数据或者已经拉倒数据
     * @param net_error
     * @param loading
     * @param nodata
     * @param root
     */
    private void setViewStatus(boolean net_error, boolean loading, boolean nodata, boolean root) {
        common_net_error.setVisibility(net_error ? View.VISIBLE : View.GONE);
        common_loading.setVisibility(loading ? View.VISIBLE : View.GONE);
        common_nodata.setVisibility(nodata ? View.VISIBLE : View.GONE);
        this.root.setVisibility(root ? View.VISIBLE : View.GONE);
    }

    private void reqData() {
        LoadingDialog.show((BaseActivity)context);
        ModuleMgr.getCommonMgr().getWomanAutoReplyList(mType, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                LoadingDialog.closeLoadingDialog(300);

                listObj = (WomanAutoReplyList) response.getBaseData();
                if (response.isOk()) {
                    setViewStatus(false, false, false, true);
                    addItemView();
                    if (listObj != null && listObj.getAuto_av_invite() != null) {
                        setAvStatus(listObj.getAuto_av_invite().isVideo(), listObj.getAuto_av_invite().isAudio());
                    }
                } else {
                    if (NetworkUtils.isConnected(context) ){
                        setViewStatus(false, false, true, false);
                    } else {
                        setViewStatus(true, false, false, false);
                    }
                }
            }
        });
    }


    private void initView() {
        root = (ScrollView) findViewById(R.id.root);
        des = (TextView) findViewById(R.id.des);
        common_net_error = (LinearLayout) findViewById(R.id.common_net_error);
        common_loading = (LinearLayout) findViewById(R.id.common_loading);
        common_nodata = (LinearLayout) findViewById(R.id.common_nodata);
        no_data_des = (TextView) findViewById(R.id.no_data_des);
        error_btn = (TextView) findViewById(R.id.error_btn);
        empty_layout = (RelativeLayout) findViewById(R.id.empty_layout);
        empty_layout.setVisibility(View.GONE);
        addItem = (TextView) findViewById(R.id.add);
        list = (LinearLayout) findViewById(R.id.list);
        sb_auto_invite_voice = (SeekBar) findViewById(R.id.sb_auto_invite_voice);
        sb_auto_invite_video = (SeekBar) findViewById(R.id.sb_auto_invite_video);
        autoVideoVoice = (LinearLayout) findViewById(R.id.autoVideoVoice);
    }


    /**
     * 设置自动邀请对方开关
     *
     * @param videoSelected 视频开关
     * @param voiceSelected 音频开关
     */
    private void setWomanAutoReplyAvStatus(final boolean videoSelected, final boolean voiceSelected) {
        ModuleMgr.getCommonMgr().setWomanAutoReplyAvStatus(videoSelected, voiceSelected, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                try {
                    String str = response.getResponseString();
                    JSONObject jso = new JSONObject(str);
                    if ("ok".equals(jso.optString("status")) && jso.optJSONObject("res") != null && jso.optJSONObject("res").optInt("ref") == 1) {
                        setAvStatus(videoSelected, voiceSelected);
                    } else {
                        PToast.showShort("设置失败！");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 设置音视频自动回复状态
     *
     * @param videoSelected 视频开关
     * @param voiceSelected 音频开关
     */
    public void setAvStatus(final boolean videoSelected, final boolean voiceSelected) {
        if (!videoSelected) {
            sb_auto_invite_video.setProgress(0);
        } else {
            sb_auto_invite_video.setProgress(100);
        }
        listObj.getAuto_av_invite().setVideo(videoSelected);


        if (!voiceSelected) {
            sb_auto_invite_voice.setProgress(0);
        } else {
            sb_auto_invite_voice.setProgress(100);
        }
        listObj.getAuto_av_invite().setAudio(voiceSelected);
    }

    /**
     * 设置自动回复条目
     *
     * @param index     自动回复条目postion
     * @param view      选中状态view
     * @param autoReply 自动回复内容实体
     * @param status    选中状态
     * @param callBack  操作成功回调
     */
    private void setWomanAutoReplyItem(final int index, final ImageView view, final WomanAutoReplyList.AutoReply autoReply, final boolean status, final WomanAutoReplyAct.CallBack callBack) {
        isClickSelectStatus = false;
        ModuleMgr.getCommonMgr().setWomanAutoReplyItem(autoReply.getArid(), status, mType, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                isClickSelectStatus = true;
                try {
                    String str = response.getResponseString();
                    JSONObject jso = new JSONObject(str);
                    if ("ok".equals(jso.optString("status")) && jso.optJSONObject("res") != null && jso.optJSONObject("res").optInt("ref") == 1) {
                        view.setSelected(status);
                        if (listObj != null && listObj.getAuto_reply() != null && listObj.getAuto_reply().size() > 0 && listObj.getAuto_reply().get(index) != null) {
                            listObj.getAuto_reply().get(index).setSelected(status);
                        }
                        if (status) {
                            selectIndex = index;
                            select_Switch_iv = view;
                            select_AutoReply = autoReply;

                        } else {
                            selectIndex = -1;
                            select_Switch_iv = null;
                            select_AutoReply = null;
                        }

                        if (callBack != null) {
                            callBack.call(true);
                        }
                    } else {
                        PToast.showLong("操作失败!");
                        if (callBack != null) {
                            callBack.call(false);
                        }
                    }
                } catch (Exception e) {
                    PToast.showLong("操作失败!");
                    if (callBack != null) {
                        callBack.call(false);
                    }
                    e.printStackTrace();

                }
            }
        });
    }

    /**
     * 添加view
     * @param i
     * @param autoReply
     */
    private void addItem(final int i, final WomanAutoReplyList.AutoReply autoReply) {
        View item = View.inflate(context, R.layout.f1_woman_autoreply_item, null);
        TextView txt_tv = (TextView) item.findViewById(R.id.txt_tv);
        TextView time = (TextView) item.findViewById(R.id.time);
        final ImageView switch_iv = (ImageView) item.findViewById(R.id.switch_iv);
        TextView status = (TextView) item.findViewById(R.id.status);
        LinearLayout voice_layout = (LinearLayout) item.findViewById(R.id.voice_layout);
        final LinearLayout voice_layout2 = (LinearLayout) item.findViewById(R.id.voice_layout2);

        final ImageView chat_item_voice_right_img = (ImageView) item.findViewById(R.id.chat_item_voice_right_img);
        TextView del_btn = (TextView) item.findViewById(R.id.del_btn);
        if (autoReply.getStatus() == 1) {
            status.setVisibility(View.GONE);
            switch_iv.setVisibility(View.VISIBLE);
        } else {
            status.setVisibility(View.VISIBLE);
            switch_iv.setVisibility(View.GONE);
            if (autoReply.getStatus() == 0) {
                status.setText("审核中");
                status.setTextColor(ContextCompat.getColor(context, R.color.color_FF9900));
                status.setBackgroundResource(R.drawable.bg_colorff9900_shape);
                status.setPadding(
                        (int) context. getResources().getDimension(R.dimen.px15_dp),
                        (int) context. getResources().getDimension(R.dimen.px10_dp),
                        (int) context. getResources().getDimension(R.dimen.px15_dp),
                        (int) context. getResources().getDimension(R.dimen.px10_dp)
                );
            } else {
                status.setText("审核失败");
                status.setTextColor(ContextCompat.getColor(context, R.color.color_F0426B));
                status.setBackgroundResource(R.drawable.bg_openvip_bottom_shape_selected);
                status.setPadding(
                        (int) context. getResources().getDimension(R.dimen.px15_dp),
                        (int) context. getResources().getDimension(R.dimen.px10_dp),
                        (int) context. getResources().getDimension(R.dimen.px15_dp),
                        (int) context. getResources().getDimension(R.dimen.px10_dp)
                );
            }

        }

        switch_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!isClickSelectStatus) {
                    PToast.showShort("请稍后点击");
                    return;
                }
                if (switch_iv.isSelected()) {
                    setWomanAutoReplyItem(i, switch_iv, autoReply, false, null);
                } else {
                    if (selectIndex >= 0 && select_Switch_iv != null && select_AutoReply != null) {
                        setWomanAutoReplyItem(selectIndex, select_Switch_iv, select_AutoReply, false, new WomanAutoReplyAct.CallBack() {

                            @Override
                            public void call(boolean isSuccess) {
                                if (isSuccess) {
                                    setWomanAutoReplyItem(i, switch_iv, autoReply, true, null);
                                }
                            }
                        });
                    } else {
                        setWomanAutoReplyItem(i, switch_iv, autoReply, true, null);
                    }
                }

            }
        });

        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(autoReply.getText()) && !TextUtils.isEmpty(autoReply.getSpeech_url()) && ChatMediaPlayer.getInstance().isPlayingVoice(autoReply.getSpeech_url())) {
                    ChatMediaPlayer.getInstance().stopPlayVoice();
                }
                ModuleMgr.getCommonMgr().delWomanAutoReply(autoReply.getArid(), mType, new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        try {
                            String str = response.getResponseString();
                            JSONObject jso = new JSONObject(str);
                            if ("ok".equals(jso.optString("status")) && jso.optJSONObject("res") != null && jso.optJSONObject("res").optInt("ref") == 1) {
                                if (listObj != null && listObj.getAuto_reply() != null && listObj.getAuto_reply().size() > 0) {
                                    if (listObj.getAuto_reply().get(i).isSelected()) {
                                        select_AutoReply = null;
                                        select_Switch_iv = null;
                                        selectIndex = -1;
                                    }
                                    listObj.getAuto_reply().remove(i);
                                }
                                addItemView();
                            } else {
                                PToast.showLong("删除失败!");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        if (autoReply.isSelected()) {
            if (select_Switch_iv != null && selectIndex != -1) {
                select_Switch_iv.setSelected(false);
                selectIndex = -1;
                select_Switch_iv = null;
            }
            selectIndex = i;
            select_Switch_iv = switch_iv;
            select_AutoReply = autoReply;
            switch_iv.setSelected(true);
        }
        if (TextUtils.isEmpty(autoReply.getText()) && !TextUtils.isEmpty(autoReply.getSpeech_url())) {
            txt_tv.setVisibility(View.GONE);
            voice_layout.setVisibility(View.VISIBLE);
            time.setText(autoReply.getTimespan() + "''");
            final AnimationDrawable[] voiceAnimation = new AnimationDrawable[1];
            voice_layout2.post(new Runnable() {
                @Override
                public void run() {
                    int voice_layout_width = voice_layout2.getWidth();
                    LinearLayout.LayoutParams voice_layout_lp = (LinearLayout.LayoutParams) voice_layout2.getLayoutParams();
                    voice_layout_lp.width = voice_layout_width + (Integer.valueOf(autoReply.getTimespan()) * 3);
                    voice_layout2.setLayoutParams(voice_layout_lp);
                }
            });

            voice_layout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ChatMediaPlayer.getInstance().togglePlayVoice(autoReply.getSpeech_url(), new ChatMediaPlayer.OnPlayListener() {
                        @Override
                        public void onStart(String filePath) {
                            playVoice = filePath;
                            chat_item_voice_right_img.setImageResource(R.drawable.woman_autoreply_voice_from_icon);
                            voiceAnimation[0] = (AnimationDrawable) chat_item_voice_right_img.getDrawable();
                            voiceAnimation[0].start();
                        }

                        @Override
                        public void onStop(String filePath) {
                            playVoice = "";
                            if (voiceAnimation[0] != null) {
                                voiceAnimation[0].stop();
                            }
                            chat_item_voice_right_img.setImageResource(R.drawable.woman_auto_reply_voiceico);
                        }
                    });
                }
            });
        } else {
            txt_tv.setVisibility(View.VISIBLE);
            txt_tv.setText(autoReply.getText());
            voice_layout.setVisibility(View.GONE);
        }
        list.addView(item);
        if (list.getChildCount() > 0 && empty_layout.getVisibility() == View.VISIBLE) {
            empty_layout.setVisibility(View.GONE);
        }
    }



    public void addItemView() {
        list.removeAllViews();
        if (listObj != null && listObj.getAuto_reply() != null && listObj.getAuto_reply().size() > 0) {
            for (int i = 0; i < listObj.getAuto_reply().size(); i++) {
                addItem(i, listObj.getAuto_reply().get(i));
            }
        }
        if (list.getChildCount() <= 0 && empty_layout.getVisibility() == View.GONE) {
            empty_layout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 回调监听添加自动回复或者打招呼是否成功
     */
    public interface CallBack {
        public void call(boolean isSuccess);
    }


    /**
     * 关闭正在播放的音频
     */
    public void closePlayVoice() {
        if (!TextUtils.isEmpty(playVoice)) {
            if (ChatMediaPlayer.getInstance().isPlayingVoice(playVoice)) {
                ChatMediaPlayer.getInstance().stopPlayVoice();
                playVoice = null;
            }
        }
    }
}
