package com.juxin.predestinate.ui.discover.task;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.invoke.Invoker;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 加入公会弹窗
 *
 * @author steve
 */

public class JoinGuildDialog extends BaseDialogFragment implements View.OnClickListener {
    private EditText etGuildId;
    private TextView tvGuildTitle;


    public JoinGuildDialog() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(1, 0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_join_guild_dialog);
        initView();
        setTitle();
        return getContentView();
    }

    private void initView() {
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_sure).setOnClickListener(this);
        etGuildId = (EditText) findViewById(R.id.edit_guild_id);
        tvGuildTitle = (TextView) findViewById(R.id.join_guild_title);
    }

    private void setTitle() {
        String strTitle = getString(R.string.join_guild_dialog_title);
        SpannableString ss = new SpannableString(strTitle);
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#f4a84e")), strTitle.length() - 4, strTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvGuildTitle.setText(ss);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sure:
                String guildId = etGuildId.getText().toString().trim();
                StatisticsUser.userAddUnionSubmit(guildId);
                executeJoinGuildTask(guildId);
                break;
            case R.id.btn_cancel:
                Statistics.userBehavior(SendPoint.page_mymoney_labourunion_canel);
                dismiss();
                //getActivity().finish();
                break;
        }
    }

    private void executeJoinGuildTask(String guildId) {
        if (TextUtils.isEmpty(guildId)) {
            PToast.showShort(getString(R.string.join_guild_dialog_input_hint));
            return;
        }
        HashMap<String, Object> postParam = new HashMap<>();
        postParam.put("gid", guildId);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.joinGuild, postParam, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                JSONObject jo = response.getResponseJson();
                if (response.isOk()) {
                    Invoker.getInstance().doInJS(Invoker.JSCMD_refresh_web, null);
                    ModuleMgr.getCenterMgr().getMyInfo().setJoinGuild(true);
                    ModuleMgr.getCenterMgr().reqMyInfo();
                    dismiss();
                    return;
                }
                PToast.showShort(TextUtils.isEmpty(jo.optString("msg")) ? "数据异常" : jo.optString("msg"));
            }
        });
    }

}
