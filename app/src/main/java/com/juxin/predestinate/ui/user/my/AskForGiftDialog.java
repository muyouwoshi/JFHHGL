package com.juxin.predestinate.ui.user.my;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.module.local.msgview.chatview.input.ChatMediaPlayer;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.my.GiftHelper;
import com.juxin.predestinate.ui.user.my.adapter.GiftGridviewAskForAdapter;
import com.juxin.predestinate.ui.user.my.adapter.GiftViewPagerAdapter;
import com.juxin.predestinate.ui.user.my.view.PageIndicatorView;
import com.juxin.predestinate.ui.user.my.view.VoiceView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 礼物
 *
 * @author zm
 */
public class AskForGiftDialog extends Dialog implements OnClickListener, GiftHelper.OnRequestGiftListCallback, RecordVoicePanel.OnRecordVoiceCallBack, ChatMediaPlayer.OnPlayListener {

    private GiftViewPagerAdapter gvpAdapter;
    private List<GridView> mLists;
    private ViewPager mViewPager;
    private int index = 0;
    private List<GiftsList.GiftInfo> mListGift;
    private Context mContext;
    private LinearLayout llMid;
    private List<ImageView> lv;
    private int pageCount;
    public GiftsList.GiftInfo selectGift;
    private ImageView btn_input_change;
    private EditText tv_edit;
    private Button btn_voice;
    private LinearLayout ll_voice, ll_voice_main;
    private boolean isEdit = true;
    private RecordVoicePanel recordPanel;
    private RelativeLayout rMain;
    private int voice_length;
    private String sVoiceUrl;
    private VoiceView mVoiceView;
    private long timeCount;
    private TextView tvNnm;
    private RelativeLayout rlMsg;
    private PageIndicatorView tv_pagesize;
    private int sum = 40;

    public AskForGiftDialog(Context context, List<GiftsList.GiftInfo> mListGift) {
        this(context, R.style.No_Background);
        mContext = context;
        this.mListGift = mListGift;
        initView();
        //初始化礼物列表
        initViewGrid();
    }

    public AskForGiftDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    protected AskForGiftDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    private void initView() {
        setContentView(R.layout.f1_askfor_gift_dlg);
        rMain = (RelativeLayout) findViewById(R.id.rl_ask_main);
        mViewPager = (ViewPager) findViewById(R.id.vp_gift_main);
        llMid = (LinearLayout) findViewById(R.id.ll_gift_main_mid);
        tv_pagesize = (PageIndicatorView) findViewById(R.id.tv_gift_pagesize);
        findViewById(R.id.tv_gift_main_send).setOnClickListener(this);

        btn_input_change = (ImageView) findViewById(R.id.askfor_btn_voice);
        btn_voice = (Button) findViewById(R.id.askfor_btn_record);
        tv_edit = (EditText) findViewById(R.id.askfor_editmsg);
        tvNnm = (TextView) findViewById(R.id.askfor_tv_num);
        rlMsg = (RelativeLayout) findViewById(R.id.askfor_rl_msg);
        ll_voice = (LinearLayout) findViewById(R.id.askfor_voice);
        ll_voice_main = (LinearLayout) findViewById(R.id.askfor_voice_parent);

        recordPanel = new RecordVoicePanel(mContext);
        rMain.addView(recordPanel.getContentView());
        recordPanel.setVisibility(View.GONE);
        recordPanel.setOnRecordVoiceCallBack(this);
        //设置dot的size和背景
        tv_pagesize.setSelectDot(8, 8, R.drawable.f1_ask_dot_select);//选中
        tv_pagesize.setUnselectDot(8, 8, R.drawable.f1_ask_dot_unselect);//未选中

        initEdit();
        btn_input_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEdit();
            }
        });

        btn_voice.setOnTouchListener(onTouchListener);

        tv_edit.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int number = sum - s.length();
                tvNnm.setText(s.length() + "/" + sum);
                selectionStart = tv_edit.getSelectionStart();
                selectionEnd = tv_edit.getSelectionEnd();
                int leng = temp.length();
                if (temp.length() > sum) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    tv_edit.setText(s);
                    tv_edit.setSelection(s.length());//设置光标在最后
                }
            }
        });
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    Statistics.userBehavior(SendPoint.menu_me_redpackage_sylw_voice);

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

    private void switchVoice() {
        tv_edit.setVisibility(View.GONE);
        rlMsg.setVisibility(View.GONE);
        btn_voice.setVisibility(View.GONE);
        ll_voice_main.setVisibility(View.VISIBLE);
        if (mVoiceView == null) {
            mVoiceView = new VoiceView(mContext);
            ll_voice.addView(mVoiceView);
        }
        mVoiceView.setData(sVoiceUrl, String.valueOf(voice_length) + mContext.getString(R.string.second));
    }

    private void initEdit() {
        btn_input_change.setImageResource(R.drawable.p1_chat_a01);
        tv_edit.setVisibility(View.VISIBLE);
        rlMsg.setVisibility(View.VISIBLE);
        btn_voice.setVisibility(View.GONE);
        ll_voice_main.setVisibility(View.GONE);
    }

    private void changeEdit() {
        if (isEdit) {
            isEdit = false;
            btn_input_change.setImageResource(R.drawable.p1_chat_e01);
            tv_edit.setVisibility(View.GONE);
            rlMsg.setVisibility(View.GONE);
            btn_voice.setVisibility(View.VISIBLE);
            ll_voice_main.setVisibility(View.GONE);
            sVoiceUrl = "";
            voice_length = 0;
        } else {
            isEdit = true;
            initEdit();
        }
    }

    private void initDot(int select) {
        if (true) return;
        if (pageCount == 0)
            return;
        if (null == lv) {
            lv = new ArrayList<>();
            for (int i = 0; i < pageCount; i++) {
                ImageView iv = new ImageView(mContext);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(12, 12);
                layoutParams.setMargins(10, 0, 10, 0);
                iv.setLayoutParams(layoutParams);
                iv.setBackgroundResource(R.drawable.f1_dot_normal);
                lv.add(iv);
                llMid.addView(iv);
            }
        }
        for (int i = 0; i < lv.size(); i++) {
            lv.get(i).setBackgroundResource(R.drawable.f1_dot_normal);
        }
        lv.get(select).setBackgroundResource(R.drawable.f1_dot_focused);
    }

    public void initSelect() {
        if (null != mListGift) {
            for (int i = 0; i < mListGift.size(); i++) {
                if (mListGift.get(i) != null) {
                    mListGift.get(i).setIsSelect(false);
                }
            }
            if (mListGift.get(0) != null) {
                mListGift.get(0).setIsSelect(true);
                selectGift = mListGift.get(0);
            }
            if (null != mLists) {
                if (null != mLists.get(index)) {
                    GiftGridviewAskForAdapter gvGift = (GiftGridviewAskForAdapter) mLists.get(index).getAdapter();
                    gvGift.notifyDataSetChanged();
                }
            }
        }
    }

    private void initData() {
        mViewPager.addOnPageChangeListener(new MyOnPageChanger());
        gvpAdapter = new GiftViewPagerAdapter(mContext, mLists);
        mViewPager.setAdapter(gvpAdapter);
        tv_pagesize.initIndicator(pageCount);
        if (pageCount > 1) {
            tv_pagesize.setVisibility(View.VISIBLE);
            tv_pagesize.setSelectedPage(0);
        } else {
            tv_pagesize.setVisibility(View.GONE);
        }
    }

    private void initGridView() {
        if (null == mListGift) return;

        pageCount = (int) Math.ceil(mListGift.size() / 3.0f);
        mLists = new ArrayList<>();
        for (int i = 0; i < pageCount; i++) {
            GridView gv = new GridView(mContext);
            final GiftGridviewAskForAdapter gvGift = new GiftGridviewAskForAdapter(mContext, mListGift, i);
            gv.setAdapter(gvGift);
            gv.setGravity(Gravity.CENTER);
            gv.setClickable(true);
            gv.setFocusable(false);
            gv.setNumColumns(3);
            gv.setSelector(R.color.transparent);
            gv.setFadingEdgeLength(0);
            gv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int select = index * 3 + position;
                    for (int j = 0; j < mListGift.size(); j++) {
                        if (mListGift.get(j) != null) {
                            mListGift.get(j).setIsSelect(false);
                        }
                    }
                    if (mListGift.get(select) != null) {
                        mListGift.get(select).setIsSelect(true);
                    }
                    selectGift = mListGift.get(select);
                    gvGift.notifyDataSetChanged();
                }
            });
            mLists.add(gv);
        }
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_gift_main_send:
                if (null == selectGift) {
                    PToast.showShort(mContext.getString(R.string.please_select_a_gift));
                } else {
                    if (isEdit && TextUtils.isEmpty(tv_edit.getText().toString().trim())) {
                        PToast.showShort(mContext.getString(R.string.please_input_you_want_to_say));
                    } else if (!isEdit && TextUtils.isEmpty(sVoiceUrl)) {
                        PToast.showShort(mContext.getString(R.string.please_input_want_to_say));
                    } else
                        onSend();
                }
                break;
        }
    }

    private void initViewGrid() {
        if (null != mListGift) {
            if (mListGift.size() > 0) {
                initGridView();
                initDot(0);
                initSelect();
            } else {
                dismiss();
            }
        }
    }

    @Override
    public void onRequestGiftListCallback(boolean isOk) {
        if (isOk) {
            mListGift = ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts();
            initGridView();
        }
    }

    @Override
    public void onRecordVoiceCallBack(String url, int length) {
        sVoiceUrl = url;
        voice_length = length;
        ModuleMgr.getMediaMgr().sendHttpFile(Constant.UPLOAD_TYPE_VOICE, url, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                try {
                    String str = response.getResponseString();
                    JSONObject jso = new JSONObject(str);
                    if ("ok".equals(jso.optString("status")) && jso.optJSONObject("res") != null) {
                        sVoiceUrl = jso.optJSONObject("res").optString("file_http_path");
                        switchVoice();
                    } else {
                        PToast.showLong("语音发送失败!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onStart(String filePath) {
    }

    @Override
    public void onStop(String filePath) {
    }

    class MyOnPageChanger implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            index = position;
            initDot(position);
            ((GiftGridviewAskForAdapter) mLists.get(index).getAdapter()).notifyDataSetChanged();

            tv_pagesize.setSelectedPage(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public void onSend() {
        StatisticsUser.userAskForGift(isEdit ? tv_edit.getText().toString()
                : sVoiceUrl, selectGift.getId());

        Map<String, Object> requestObject = new HashMap<>();
        requestObject.put("gift_id", selectGift.getId());
        if (isEdit) {
            requestObject.put("content", tv_edit.getText().toString());
        } else {
            requestObject.put("voice_url", sVoiceUrl.toLowerCase().replace(".amr", ""));
            requestObject.put("voice_len", voice_length);
        }
        // 发送请求
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.qunFa, requestObject, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    PToast.showShort(mContext.getString(R.string.send_suceed));
                    dismiss();
                    return;
                }
                PToast.showShort(response.getMsg());
            }
        });
        dismiss();
    }
}
