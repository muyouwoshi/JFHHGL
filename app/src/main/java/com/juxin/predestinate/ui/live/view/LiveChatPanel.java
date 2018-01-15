package com.juxin.predestinate.ui.live.view;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.utils.InputUtils;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by terry on 2017/7/14.
 */
public class LiveChatPanel extends BasePanel implements View.OnClickListener {

    private TextView mMsgTv, mBarrageTv, mBroadcastTv, mTipsTv;
    private EditText mLiveChatEText;
    private Button mSendBtn;
    private List<TextView> mItemViews;

    private String roomId;
    private int mSignleCard ,mServiceCard;
    private final int BARRAGE_LIMIT_COUNT = 20;
    private final int MSG_LIMIT_COUNT = 40;

    private ChatListener mListener;

    public LiveChatPanel(Context context) {
        super(context);

        setContentView(R.layout.l1_live_chat_panel);
        init();
    }

    private void init() {
        mMsgTv = (TextView) findViewById(R.id.live_chat_msg_tv);
        mBarrageTv = (TextView) findViewById(R.id.live_chat_barrage_tv);
        mBroadcastTv = (TextView) findViewById(R.id.live_chat_broadcast_tv);
        mTipsTv = (TextView) findViewById(R.id.live_chat_tips_tv);
        mLiveChatEText = (EditText) findViewById(R.id.live_chat_edittext);
        mSendBtn = (Button) findViewById(R.id.live_chat_send_btn);

        mItemViews = new ArrayList<>();
        mItemViews.add(mMsgTv);
        mItemViews.add(mBarrageTv);
        mItemViews.add(mBroadcastTv);

        mMsgTv.setOnClickListener(this);
        mBarrageTv.setOnClickListener(this);
        mBroadcastTv.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        mLiveChatEText.addTextChangedListener(textWatcher);
        mLiveChatEText.setOnEditorActionListener(onEditorActionListener);

    }


    public void setRoomId(String roomid,int signleCard,int serviceCard) {
        this.roomId = roomid;
        this.mSignleCard = signleCard;
        this.mServiceCard = serviceCard;
        mMsgTv.setSelected(true);
        mLiveChatEText.setText("");
        mLiveChatEText.setHint("举手打字，以表尊敬");
        mLiveChatEText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(mMsgTv.isSelected() ? MSG_LIMIT_COUNT : BARRAGE_LIMIT_COUNT)});
    }

    /**
     *  点击充值回调接口
     */
    public void setChatListener(ChatListener listener) {
        mListener = listener;
    }


    public void focus() {
        mLiveChatEText.setFocusable(true);
        mLiveChatEText.setFocusableInTouchMode(true);
        mLiveChatEText.requestFocus();
        InputUtils.forceOpen(context);
    }

    private String getSignleHint(){
       return mSignleCard > 0 ? "免费弹幕剩余"+mSignleCard+"条":"发送弹幕，10钻石/条";
    }

    private String getServiceHint(){
        return mServiceCard > 0 ? "免费广播剩余"+mServiceCard+"条":"发送广播，880钻石/条";
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.live_chat_send_btn) {
            sendChatData();
        } else if (id == R.id.live_chat_msg_tv) {
            onClickChatItemView(v, "举手打字，以表尊敬");
        } else if (id == R.id.live_chat_barrage_tv) {
            onClickChatItemView(v, getSignleHint());
        } else if (id == R.id.live_chat_broadcast_tv) {
            onClickChatItemView(v, getServiceHint());
        }
    }

    private void onClickChatItemView(View view, String hint) {
        mLiveChatEText.setText("");
        mLiveChatEText.setHint(hint);
        for (TextView tv : mItemViews) {
            tv.setSelected(false);
        }
        view.setSelected(true);
        mLiveChatEText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(mMsgTv.isSelected() ? MSG_LIMIT_COUNT : BARRAGE_LIMIT_COUNT)});
        if (mMsgTv.isSelected()) {
            mTipsTv.setText( "0/" + MSG_LIMIT_COUNT);
        } else {
            mTipsTv.setText( "0/" + BARRAGE_LIMIT_COUNT);
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            mSendBtn.setEnabled(!TextUtils.isEmpty(mLiveChatEText.getText().toString()));
            mSendBtn.setTextColor(mSendBtn.isEnabled() ? Color.parseColor("#ffffff") : Color.parseColor("#999999"));

            if (mMsgTv.isSelected()) {
                mTipsTv.setText(mLiveChatEText.getText().toString().length() + "/" + MSG_LIMIT_COUNT);
            } else {
                mTipsTv.setText(mLiveChatEText.getText().toString().length() + "/" + BARRAGE_LIMIT_COUNT);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (event == null) {
                return false;
            }
            if (actionId == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                sendChatData();
                return true;
            }
            return false;
        }
    };

    private void sendChatData(){
        if (!TextUtils.isEmpty(mLiveChatEText.getText().toString())) {

            if (mBarrageTv.isSelected()) {
                Statistics.userBehavior(SendPoint.page_live_send_danmu);
                if (mSignleCard<=0 && ModuleMgr.getCenterMgr().getMyInfo().getDiamand()<10) {
                    if (mListener != null) mListener.needDiamand();
                    InputUtils.forceClose(mLiveChatEText);
                    UIShow.showGoodsDiamondDialog(getContext(),0,Constant.OPEN_FROM_LIVE, Long.parseLong(roomId), "");
                    return;
                }
                sendChat("txt",mLiveChatEText.getText().toString(),UrlParam.chatSendBarrage);
            } else if (mBroadcastTv.isSelected()) {
                if (mServiceCard <=0 && ModuleMgr.getCenterMgr().getMyInfo().getDiamand() < 880) {
                    InputUtils.forceClose(mLiveChatEText);
                    UIShow.showGoodsDiamondDialog(getContext(),0,Constant.OPEN_FROM_LIVE, Long.parseLong(roomId), "");
                    return;
                }
                sendChat("text",mLiveChatEText.getText().toString(),UrlParam.BroadCast);
            } else {
                sendChat("txt",mLiveChatEText.getText().toString(), UrlParam.chatSend);
            }
        }
    }

    /**
     *
     * @param msg   聊天消息
     * @param url   发送url
     */
    private void sendChat(String params,String msg,UrlParam url) {
        mLiveChatEText.setText("");
        HashMap<String, Object> postParam = new HashMap<>();
        postParam.put("room_id", roomId);//房间ID
        postParam.put(params, msg);//聊天内容
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(url, postParam, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (!response.isOk()) {
                    PToast.showShort(response.getMsg());
                } else {
                    try {
                        JSONObject resJo = response.getResponseJson().getJSONObject("res");
                        if (resJo != null) {
                            if (resJo.has("gem_total")){
                                //更新本地钻石
                                ModuleMgr.getCenterMgr().getMyInfo().setDiamand(resJo.optInt("gem_total"));
                            }
                            if (resJo.has("single_card")){
                                mSignleCard = resJo.optInt("single_card");
                                mLiveChatEText.setHint(getSignleHint());
                            }else if (resJo.has("service_card")){
                                mServiceCard = resJo.optInt("service_card");
                                mLiveChatEText.setHint(getServiceHint());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public interface ChatListener {
        void needDiamand();
    }
}
