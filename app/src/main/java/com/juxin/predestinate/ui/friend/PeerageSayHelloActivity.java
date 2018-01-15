package com.juxin.predestinate.ui.friend;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PSP;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.utils.ViewUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweightList;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.custom.GridSpacingItemDecoration;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.UIShow;

/**
 * @author Mr.Huang
 * @date 2017/9/12
 */
public class PeerageSayHelloActivity extends BaseActivity {

    public static final String EXTRA_DATA = "data";
    private RecyclerView recycler_view;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peerage_say_hellp_activity);
        setTitle("用户推荐");
        String data = getIntent().getStringExtra(EXTRA_DATA);
        if (data == null) {
            finish();
            return;
        }
        final JSONArray array;
        try {
            array = JSON.parseObject(data).getJSONObject("res").getJSONArray("list");
            UserInfoLightweightList list = new UserInfoLightweightList();
            list.parseJsonSayhi(data);
            if (array == null || array.isEmpty()) {
                finish();
                return;
            }
        } catch (Exception e) {
            finish();
            return;
        }
        int pading = ViewUtils.dpToPx(20);
        recycler_view = ViewUtils.findById(this, R.id.recycler_view);
        recycler_view.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        recycler_view.addItemDecoration(new GridSpacingItemDecoration(3, pading, pading));
        adapter = new Adapter(array);
        recycler_view.setAdapter(adapter);
        View view = findViewById(R.id.btn_say_hello);
        final JSONArray finalArray = array;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long[] uids = new long[finalArray.size()];
                for (int i = 0; i < finalArray.size(); i++) {
                    uids[i] = finalArray.getJSONObject(i).getLongValue("uid");
                }
                StatisticsUser.dailyOneKeySayHiByABTest(uids);
                ModuleMgr.getChatMgr().sendSayHelloMsg(uids, null);
                ModuleMgr.getCommonMgr().saveDateState(ModuleMgr.getCommonMgr().getSayHelloKey());
                MsgMgr.getInstance().sendMsg(MsgType.MT_Say_Hello_Notice, finalArray);
                MsgMgr.getInstance().sendMsg(MsgType.MT_SURE_USER, true);
                MsgMgr.getInstance().sendMsg(MsgType.MT_CLOSE_SAY_HELLO, true);
                finish();
            }
        });
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.WHITE);
        gd.setCornerRadius(ViewUtils.dpToPx(6));
        gd.setStroke(ViewUtils.dpToPx(0.5f), 0xffdddddd);
        view.setBackgroundDrawable(gd);
    }

    @Override
    public void onBackPressed() {
        // 空实现，不响应返回键点击
    }

    private static final class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private JSONArray array;

        private Adapter(JSONArray array) {
            this.array = array;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.av_chat_fragment_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.update(array.getJSONObject(position));
        }

        @Override
        public int getItemCount() {
            return array.size();
        }
    }


    private static final class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivHead, ivMedia;
        private TextView tvName, tvAgeKM;

        public ViewHolder(View view) {
            super(view);
            this.ivHead = ViewUtils.findById(view, R.id.iv_head);
            this.ivMedia = ViewUtils.findById(view, R.id.iv_media);
            this.tvName = ViewUtils.findById(view, R.id.tv_name);
            this.tvAgeKM = ViewUtils.findById(view, R.id.tv_age_km);
        }

        private void update(JSONObject object) {
            String avatar = object.getString("avatar");
            ImageLoader.loadRoundAvatar(ivHead.getContext(), ImageLoader.checkOssImageUrl(avatar, 256), ivHead);
            ivMedia.setVisibility(View.GONE);
            String nickName = object.getString("nickname");
            tvName.setText(nickName != null ? nickName : "");
            int age = object.getIntValue("age");
            String distance = object.getString("distance");
            tvAgeKM.setText(age + "岁 • " + (distance != null ? distance : ""));
        }
    }
}
