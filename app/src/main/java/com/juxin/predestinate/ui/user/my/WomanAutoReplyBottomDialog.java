package com.juxin.predestinate.ui.user.my;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.FileUtil;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.ui.user.my.view.VoiceView;

import org.json.JSONObject;

/**
 * Created by duanzheng on 2017/7/10.
 */

@SuppressLint("ValidFragment")
public class WomanAutoReplyBottomDialog extends BaseDialogFragment implements View.OnClickListener, RecordVoicePanel.OnRecordVoiceCallBack {

    private ImageView btn_input_change;
    private EditText tv_edit;
    private RelativeLayout rlMsg;
    private Button btn_voice;
    //private LinearLayout ll_voice_main;
    private RecordVoicePanel recordPanel;
    private VoiceView mVoiceView;
    public final static int TXT = 0;
    public final static int VOICE = 1;
    private TextView askfor_tv_num;
    private boolean isEdit = true;
    private long timeCount;
    private String sVoiceUrl;
    private int voice_length;
    private LinearLayout root;
    private RelativeLayout voice_layout;
    private FragmentActivity activity;
    private LoadingDialog loadingDialog;
    InputMethodManager imm;
    private LinearLayout ll_voice;
    private LinearLayout askfor_voice_layout;
    public int mType = SayHelloAutoReplyPanel.AutoReply;
    private TextView des;
    private TextView say_hello_txt;
    private ImageView say_hello_ico;
    private ImageView auto_reply_ico;
    private TextView auto_reply_txt;
    private LinearLayout select_say_hello;
    private LinearLayout select_auto_reply;

    @IntDef({TXT, VOICE})
    public @interface Type {
    }

    public WomanAutoReplyBottomDialog(FragmentActivity activity,@SayHelloAutoReplyPanel.Type int type) {
        this.activity = activity;
        mType=type;
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, -2);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_woman_autoreply_bottom_dlg);
        View contentView = getContentView();
        initView(contentView);
        initData();
        return contentView;
    }

    private void initData() {
        root.setOnClickListener(this);
        btn_input_change.setOnClickListener(this);
        tv_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_edit.removeTextChangedListener(this);
                if (tv_edit.getText() != null && tv_edit.getText().toString() != null) {
                    int length = tv_edit.getText().toString().length();
                    askfor_tv_num.setText(length + "/" + 30);
                }
                tv_edit.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initEdit();
        btn_voice.setOnTouchListener(onTouchListener);
        recordPanel = new RecordVoicePanel(getContext());
        voice_layout.addView(recordPanel.getContentView());
        recordPanel.setVisibility(View.GONE);
        recordPanel.setOnRecordVoiceCallBack(this);
        sayHello_autoReply(mType);
        select_say_hello.setOnClickListener(this);
        select_auto_reply.setOnClickListener(this);
    }

    //点击切换时 选择 自动回复还是自动打招呼
    public void sayHello_autoReply(@SayHelloAutoReplyPanel.Type int type) {
        if (mType == SayHelloAutoReplyPanel.AutoReply) {
            des.setText("设置您自动回复语音或文字");
            auto_reply_ico.setSelected(true);
            auto_reply_txt.setTextColor(ContextCompat.getColor(getContext(), R.color.color_fd7f9e));
            say_hello_txt.setTextColor(ContextCompat.getColor(getContext(), R.color.tip_light_gray));
            say_hello_ico.setSelected(false);
        } else {
            des.setText("设置您的打招呼语音或文字");
            auto_reply_ico.setSelected(false);
            auto_reply_txt.setTextColor(ContextCompat.getColor(getContext(), R.color.tip_light_gray));
            say_hello_txt.setTextColor(ContextCompat.getColor(getContext(), R.color.color_fd7f9e));
            say_hello_ico.setSelected(true);
        }
    }

    private void initView(View contentView) {
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        loadingDialog = new LoadingDialog(activity);
        ll_voice = (LinearLayout) findViewById(R.id.askfor_voice);
        askfor_voice_layout = (LinearLayout) findViewById(R.id.askfor_voice_layout);
        root = (LinearLayout) contentView.findViewById(R.id.root);
        btn_input_change = (ImageView) contentView.findViewById(R.id.askfor_btn_voice);
        contentView.findViewById(R.id.close_iv).setOnClickListener(this);
        askfor_tv_num = (TextView) contentView.findViewById(R.id.askfor_tv_num);
        tv_edit = (EditText) contentView.findViewById(R.id.askfor_editmsg);
        rlMsg = (RelativeLayout) contentView.findViewById(R.id.askfor_rl_msg);
        btn_voice = (Button) contentView.findViewById(R.id.askfor_btn_record);
        voice_layout = (RelativeLayout) contentView.findViewById(R.id.voice_layout);
        contentView.findViewById(R.id.content).setOnClickListener(this);
        contentView.findViewById(R.id.send).setOnClickListener(this);
        root.post(new Runnable() {
            @Override
            public void run() {
                imm.showSoftInput(tv_edit, InputMethodManager.SHOW_FORCED);
            }
        });
        des = (TextView) contentView.findViewById(R.id.des);

        say_hello_txt = (TextView) contentView.findViewById(R.id.say_hello_txt);
        say_hello_ico = (ImageView) contentView.findViewById(R.id.say_hello_ico);

        auto_reply_ico = (ImageView) contentView.findViewById(R.id.auto_reply_ico);
        auto_reply_txt = (TextView) contentView.findViewById(R.id.auto_reply_txt);


        select_say_hello = (LinearLayout) contentView.findViewById(R.id.select_say_hello);
        select_auto_reply = (LinearLayout) contentView.findViewById(R.id.select_auto_reply);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_iv:
            case R.id.root:
                dismiss();
                break;
            case R.id.askfor_btn_voice:
                changeEdit();
                break;
            case R.id.content:

                break;
            case R.id.send:
                send();
                break;
            case R.id.select_say_hello:
                mType = SayHelloAutoReplyPanel.SayHello;
                sayHello_autoReply(mType);
                break;
            case R.id.select_auto_reply:
                mType = SayHelloAutoReplyPanel.AutoReply;
                sayHello_autoReply(mType);
                break;
        }
    }


    private void send() {
        if (isEdit) {//文字
            if (tv_edit.getText() == null || (tv_edit.getText() != null && (TextUtils.isEmpty(tv_edit.getText().toString()) || TextUtils.isEmpty(tv_edit.getText().toString().trim())))) {
                PToast.showShort("请输入文字");
                return;
            }

            sendMessage(tv_edit.getText().toString(), "", 0);
        } else {
            if (TextUtils.isEmpty(sVoiceUrl) || (!TextUtils.isEmpty(sVoiceUrl) && !FileUtil.isExist(sVoiceUrl))) {
                PToast.showShort("请录入语音");
                return;
            }
            loadingDialog.show();
            ModuleMgr.getMediaMgr().sendHttpFile(Constant.UPLOAD_TYPE_VOICE, sVoiceUrl, new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    try {
                        String str = response.getResponseString();
                        JSONObject jso = new JSONObject(str);
                        if ("ok".equals(jso.optString("status")) && jso.optJSONObject("res") != null) {
                            String sVoiceUrl = jso.optJSONObject("res").optString("file_http_path");
                            sendMessage("", sVoiceUrl, voice_length);
                        } else {
                            loadingDialog.dismiss();
                            PToast.showLong(jso.optString("msg"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 提交
     * @param txt
     * @param sVoiceUrl
     * @param voice_length
     */
    public void sendMessage(String txt, final String sVoiceUrl, final int voice_length) {
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }

        ModuleMgr.getCommonMgr().addWomanAutoReply(txt, sVoiceUrl, voice_length, 0, mType, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                loadingDialog.dismiss();
                try {
                    String str = response.getResponseString();
                    JSONObject jso = new JSONObject(str);
                    if ("ok".equals(jso.optString("status")) && jso.optJSONObject("res") != null) {
                        int arid = jso.optJSONObject("res").optInt("arid");
                        if (arid > 0) {
                            if (mSendCallBack != null) {
                                if (isEdit) {//文字
                                    mSendCallBack.send(arid, tv_edit.getText().toString(), 0, TXT, mType);
                                } else {
                                    if (!TextUtils.isEmpty(WomanAutoReplyBottomDialog.this.sVoiceUrl) && FileUtil.isExist(WomanAutoReplyBottomDialog.this.sVoiceUrl)) {
                                        FileUtil.deleteFile(WomanAutoReplyBottomDialog.this.sVoiceUrl);
                                    }
                                    mSendCallBack.send(arid, sVoiceUrl, voice_length, VOICE, mType);
                                }
                            }
                            dismiss();
                        } else {
                            PToast.showLong(jso.optString("msg"));
                        }
                        //sendMessage("",sVoiceUrl,  voice_length);
                    } else {
                        PToast.showLong(jso.optString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void dismiss() {
        if (mVoiceView != null) {
            mVoiceView.close();
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {//延时 方式dialog无法收回
                //execute the task
                imm.hideSoftInputFromWindow(tv_edit.getWindowToken(), 0); //强制隐藏键盘
                WomanAutoReplyBottomDialog.super.dismiss();
            }
        }, 200);
    }

    private void changeEdit() {
        if (isEdit) {//语音
            isEdit = false;
            btn_input_change.setImageResource(R.drawable.p1_chat_e01);
            tv_edit.setVisibility(View.GONE);
            rlMsg.setVisibility(View.GONE);
            btn_voice.setVisibility(View.VISIBLE);
            sVoiceUrl = "";
            voice_length = 0;
            ll_voice.setVisibility(View.GONE);
            imm.hideSoftInputFromWindow(tv_edit.getWindowToken(), 0); //强制隐藏键盘
        } else {
            ll_voice.setVisibility(View.GONE);
            imm.showSoftInput(tv_edit, InputMethodManager.SHOW_FORCED);
            isEdit = true;
            initEdit();
        }
    }

    private void initEdit() {
        btn_input_change.setImageResource(R.drawable.p1_chat_a01);
        tv_edit.setVisibility(View.VISIBLE);
        rlMsg.setVisibility(View.VISIBLE);
        btn_voice.setVisibility(View.GONE);

    }


    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (timeCount == 0 || System.currentTimeMillis() - timeCount > 500) {
                        recordPanel.onTouch(action, 0f);
                    } else {
                        PLogger.d("---ChatInputPanel--->点击间隔<500ms，过于频繁");
                    }

                    btn_voice.setText(R.string.loosen_the_end);
                    btn_voice.setPressed(true);
                    recordPanel.setVisibility(View.VISIBLE);
                    break;

                case MotionEvent.ACTION_MOVE:
                    recordPanel.onTouch(action, event.getY());
                    break;

                case MotionEvent.ACTION_UP:

                    timeCount = System.currentTimeMillis();

                    btn_voice.setText(R.string.hold_to_talk);
                    recordPanel.onTouch(action, event.getY());
                    btn_voice.setPressed(false);
                    recordPanel.setVisibility(View.GONE);
                    break;

                default:
                    recordPanel.onTouch(action, 0f);
                    timeCount = System.currentTimeMillis();
                    btn_voice.setText(R.string.hold_to_talk);
                    btn_voice.setPressed(false);
                    return false;
            }
            return true;
        }
    };

    @Override
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        super.setOnDismissListener(onDismissListener);

    }

    @Override
    public void onRecordVoiceCallBack(String url, int length) {

        if (!TextUtils.isEmpty(sVoiceUrl) && FileUtil.isExist(sVoiceUrl)) {
            FileUtil.deleteFile(sVoiceUrl);
        }

        sVoiceUrl = url;
        voice_length = length;

        btn_voice.setVisibility(View.GONE);
        ll_voice.setVisibility(View.VISIBLE);
        if (mVoiceView == null) {
            mVoiceView = new VoiceView(getContext());
            askfor_voice_layout.addView(mVoiceView);
        }
        mVoiceView.setData(sVoiceUrl, String.valueOf(voice_length) + getString(R.string.second));
    }

    public SendCallBack mSendCallBack;

    public void setSendCallBack(SendCallBack sendcallback) {
        mSendCallBack = sendcallback;
    }

    public interface SendCallBack {
        public void send(int arid, String content, int length, @Type int typeVideoVoice, @SayHelloAutoReplyPanel.Type int comeType);
    }

}
