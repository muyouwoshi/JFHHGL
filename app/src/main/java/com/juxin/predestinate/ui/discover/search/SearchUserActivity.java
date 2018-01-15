package com.juxin.predestinate.ui.discover.search;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.utils.ViewUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.chat.MessageRet;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.logic.request.RequestParam;
import com.juxin.predestinate.module.logic.socket.IMProxy;
import com.juxin.predestinate.module.logic.socket.NetData;
import com.juxin.predestinate.module.util.UIShow;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Huang
 * @date 2017-07-26
 * 搜索用户界面
 */
public class SearchUserActivity extends BaseActivity {

    private SearchLabelView searchLabelView;
    private RecyclerView recyclerView;
    private Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abtest_search_user_activity);
        setBackView("搜索");
        searchLabelView = ViewUtils.findById(this, R.id.search_label);
        recyclerView = ViewUtils.findById(this, R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            int bottom = ViewUtils.dpToPx(1);
            ColorDrawable drawable = new ColorDrawable(0xffebebeb);

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = bottom;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                final int left = parent.getPaddingLeft();
                final int right = parent.getWidth() - parent.getPaddingRight();
                int count = parent.getChildCount();
                for (int i = 0; i < count; i++) {
                    final View child = parent.getChildAt(i);
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    final int top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
                    final int bottom = top + this.bottom;
                    drawable.setBounds(left, top, right, bottom);
                    drawable.draw(c);
                }
            }
        });
        searchLabelView.setCanSearch(true);
        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
        searchLabelView.setSearchLabelListener(new SearchLabelView.SearchLabelListener() {
            @Override
            public void onSearch(String text) {
                //点击软键盘上的搜索按钮
                doSearch(text);
            }
        });
    }

    private static final class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private JSONArray array = new JSONArray();

        public void setData(JSONArray array) {
            this.array.clear();
            this.array.addAll(array);
            this.notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.f1_discover_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            JSONObject object = this.array.getJSONObject(position);
            holder.setAvatar(object.getString("avatar"));
            holder.isVip(object);
            holder.setName(object.getString("nickname"));
            holder.setAge(object.getIntValue("age"));
            holder.setDistance(object.getString("distance"));
            holder.setHeight(object.getIntValue("height"));
            holder.setAVStatus(object.getInteger("video_busy"), object.getIntValue("video_available"), object.getIntValue("audio_available"));
            holder.bindListener(object, this);

            int KFId = object.getIntValue("kf_id");

            if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                if (ModuleMgr.getCenterMgr().isRobot(KFId) && !ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
                    holder.showSayHello(object, KFId, this);
                } else {
                    holder.showOnline(object);
                }
            } else {
                holder.showSayHello(object, KFId, this);
            }


            if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                if (ModuleMgr.getCenterMgr().isRobot(KFId) && !ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
                    holder.showSayHello(object, KFId, this);
                } else {
                    holder.showOnline(object);
                }
            } else {
                if (ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
                    holder.showOnline(object);
                } else {
                    holder.showSayHello(object, KFId, this);
                }
            }
        }

        @Override
        public int getItemCount() {
            return array.size();
        }

        public static final class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView discover_item_avatar, discover_item_ranking_state, discover_item_vip_state, discover_item_point;
            public TextView discover_item_name, discover_item_age, discover_item_height, discover_item_distance, discover_item_online_state, discover_item_call_tv;
            public View discover_item_calling_state, discover_item_video_state, discover_item_video, discover_item_sayhi, discover_item_call, discover_item_call_state;

            public ViewHolder(View itemView) {
                super(itemView);
                discover_item_avatar = ViewUtils.findById(itemView, R.id.discover_item_avatar);
                discover_item_ranking_state = ViewUtils.findById(itemView, R.id.discover_item_ranking_state);
                discover_item_vip_state = ViewUtils.findById(itemView, R.id.discover_item_vip_state);
                discover_item_point = ViewUtils.findById(itemView, R.id.discover_item_point);

                discover_item_name = ViewUtils.findById(itemView, R.id.discover_item_name);
                discover_item_age = ViewUtils.findById(itemView, R.id.discover_item_age);
                discover_item_height = ViewUtils.findById(itemView, R.id.discover_item_height);
                discover_item_distance = ViewUtils.findById(itemView, R.id.discover_item_distance);
                discover_item_online_state = ViewUtils.findById(itemView, R.id.discover_item_online_state);
                discover_item_call_tv = ViewUtils.findById(itemView, R.id.discover_item_call_tv);

                discover_item_calling_state = itemView.findViewById(R.id.discover_item_calling_state);
                discover_item_video_state = itemView.findViewById(R.id.discover_item_video_state);
                discover_item_video = itemView.findViewById(R.id.discover_item_video);
                discover_item_sayhi = itemView.findViewById(R.id.discover_item_sayhi);
                discover_item_call = itemView.findViewById(R.id.discover_item_call);
                discover_item_call_state = itemView.findViewById(R.id.discover_item_call_state);
            }

            public void setAVStatus(int video_busy, int video_available, int audio_available) {
                if (video_busy == 1) {
                    discover_item_calling_state.setVisibility(View.VISIBLE);
                    discover_item_call_state.setVisibility(View.GONE);
                    discover_item_video_state.setVisibility(View.GONE);
                } else {
                    if (video_available == 1 && audio_available == 1) {
                        //可语音可视频显示可视频
                        discover_item_calling_state.setVisibility(View.GONE);
                        discover_item_call_state.setVisibility(View.GONE);
                        discover_item_video_state.setVisibility(View.VISIBLE);
                        discover_item_video.setEnabled(true);
                    } else if (video_available == 1 && audio_available != 1) {
                        // 可视频不可语音显示可视频
                        discover_item_calling_state.setVisibility(View.GONE);
                        discover_item_call_state.setVisibility(View.GONE);
                        discover_item_video_state.setVisibility(View.VISIBLE);
                        discover_item_video.setEnabled(true);
                    } else if (video_available != 1 && audio_available != 1) {
                        //可语音不可视频显示可语音
                        discover_item_calling_state.setVisibility(View.GONE);
                        discover_item_call_state.setVisibility(View.VISIBLE);
                        discover_item_video_state.setVisibility(View.GONE);
                        discover_item_call.setEnabled(false);
                        discover_item_call_tv.setVisibility(View.VISIBLE);
                    } else {
                        //不可语音不可视频都不显示
                        discover_item_calling_state.setVisibility(View.GONE);
                        discover_item_call_state.setVisibility(View.GONE);
                        discover_item_video_state.setVisibility(View.GONE);
                        discover_item_call.setEnabled(video_available == 1);
                        discover_item_call_tv.setVisibility(View.GONE);
                    }
                }
            }

            public void setAvatar(String avatar) {
                if (avatar != null) {
                    ImageLoader.loadRoundAvatar(discover_item_avatar.getContext(), avatar, discover_item_avatar);
                } else {
                    discover_item_avatar.setImageResource(R.drawable.default_head);
                }
            }

            //设置距离
            public void setDistance(String distance) {
                if (distance != null) {
                    discover_item_distance.setText(distance);
                    discover_item_distance.setVisibility(View.VISIBLE);
                } else {
                    discover_item_distance.setVisibility(View.GONE);
                }
            }

            public void setHeight(int height) {
                if (height > 0) {
                    discover_item_height.setText(height + "cm");
                    discover_item_height.setVisibility(View.VISIBLE);
                } else {
                    discover_item_height.setVisibility(View.GONE);
                }
            }

            public void setAge(int age) {
                if (age > 0) {
                    discover_item_age.setText(age + "岁");
                    discover_item_age.setVisibility(View.VISIBLE);
                } else {
                    discover_item_age.setVisibility(View.GONE);
                }
            }

            public void setName(String name) {
                if (name != null) {
                    discover_item_name.setText(name);
                } else {
                    discover_item_name.setText("");
                }
            }

            public void isVip(JSONObject object) {
                if (object.getIntValue("gender") == 1) {
                    discover_item_vip_state.setVisibility(View.INVISIBLE);
                    return;
                }
                discover_item_vip_state.setVisibility(ModuleMgr.getCenterMgr().isVip(object.getIntValue("group")) ? View.VISIBLE : View.GONE);
            }

            public void bindListener(JSONObject object, Adapter adapter) {
                Object o = itemView.getTag(-1);//怕此view的tag会被用到 所以使用key来区分
                if (o == null) {
                    Click click = new Click();
                    itemView.setTag(-1, click);
                    itemView.setOnClickListener(click);

                    click = new Click();
                    discover_item_avatar.setTag(-1, click);
                    discover_item_avatar.setOnClickListener(click);

                    click = new Click();
                    discover_item_sayhi.setTag(-1, click);
                    discover_item_sayhi.setOnClickListener(click);
                }

                Click click = (Click) itemView.getTag(-1);
                ;
                click.setObject(object, adapter);

                click = (Click) discover_item_avatar.getTag(-1);
                click.setObject(object, adapter);

                click = (Click) discover_item_sayhi.getTag(-1);
                click.setObject(object, adapter);
            }

            /**
             * 显示打招呼
             */
            private void showSayHello(final JSONObject object, final int KFId, final Adapter adapter) {
                final boolean isSayHello = object.getBooleanValue("isSayHello");
                final int uid = object.getIntValue("uid");
                discover_item_online_state.setVisibility(View.GONE);
                discover_item_sayhi.setVisibility(View.VISIBLE);
                discover_item_sayhi.setEnabled(!isSayHello);
            }

            /**
             * 显示在线状态
             */
            private void showOnline(JSONObject object) {
                String isOnline = object.getString("is_online");
                String lastOnline = object.getString("last_online");
                if (lastOnline == null) {
                    lastOnline = "";
                }
                discover_item_sayhi.setVisibility(View.GONE);
                discover_item_online_state.setVisibility(View.VISIBLE);
                discover_item_online_state.setText("在线".equals(isOnline) ? isOnline : lastOnline);
            }
        }

        /**
         * Item相关点击事件
         */
        private static final class Click implements View.OnClickListener {

            private JSONObject object;
            private Adapter adapter;

            public void setObject(JSONObject object, Adapter adapter) {
                this.object = object;
                this.adapter = adapter;
            }

            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.discover_item_avatar) {
                    //点击头像v
                    UIShow.showCheckOtherInfoAct(v.getContext(), object.getIntValue("uid"));
                } else if (id == R.id.discover_item_sayhi) {
                    final Context context = v.getContext();
                    final boolean isSayHello = object.getBooleanValue("isSayHello");
                    if (isSayHello) {
                        return;
                    }
                    if (ModuleMgr.getCenterMgr().isCanSayHi(context)) {
                        int KFId = object.getIntValue("kf_id");
                        ModuleMgr.getChatMgr().sendSayHelloMsg(String.valueOf(object.getIntValue("uid")), context.getString(R.string.say_hello_txt), KFId, ModuleMgr.getCenterMgr().isRobot(KFId) ? Constant.SAY_HELLO_TYPE_ONLY : Constant.SAY_HELLO_TYPE_SIMPLE, new IMProxy.SendCallBack() {
                            @Override
                            public void onResult(long msgId, boolean group, String groupId, long sender, String contents) {
                                //170824 UPDATE START 打招呼提示使用消息体返回的错误信息
                                //PToast.showShort(context.getString(R.string.user_info_hi_suc));
                                //object.put("isSayHello", true);
                                //adapter.notifyDataSetChanged();

                                MessageRet messageRet = new MessageRet();
                                messageRet.parseJson(contents);

                                if (messageRet.getS() == 0) { // 成功
                                    PToast.showShort(context.getString(R.string.user_info_hi_suc));
                                    object.put("isSayHello", true);
                                    adapter.notifyDataSetChanged();
                                    MsgMgr.getInstance().sendMsg(MsgType.MT_Say_HI_Notice, object.getIntValue("uid"));
                                } else {
                                    if (!TextUtils.isEmpty(messageRet.getFailMsg())) {
                                        PToast.showShort(messageRet.getFailMsg());
                                    } else {
                                        PToast.showShort(context.getString(R.string.user_info_hi_fail));
                                    }
                                }
                                //170824 UPDATE END 打招呼提示使用消息体返回的错误信息
                            }

                            @Override
                            public void onSendFailed(NetData data) {
                                PToast.showShort(context.getString(R.string.user_info_hi_fail));
                            }
                        });
                    }
                } else {
                    //点击整个item
                    UIShow.showCheckOtherInfoAct(v.getContext(), object.getIntValue("uid"));
                }
            }
        }
    }

    /**
     * 搜索
     *
     * @param text 搜索内容
     */
    private void doSearch(final String text) {
        if (text == null || TextUtils.isEmpty(text.trim())) {
            PToast.showLong("请输入检索内容");
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("input", text);
        ModuleMgr.getHttpMgr().request(new RequestParam().setUrlParam(UrlParam.SEARCH_USERS).setPostParam(params)
                .setNeedEncrypt(true).setJsonRequest(true).setRequestCallback(new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        if (response.isOk()) {
                            org.json.JSONObject object = response.getResponseJson();
                            if (!object.isNull("res")) {
                                try {
                                    object = object.getJSONObject("res");
                                    if (object.isNull("list")) {
                                        PToast.showLong("未找到用户");
                                    } else {
                                        JSONArray array = JSON.parseArray(object.optString("list"));
                                        if (array.size() == 0) {
                                            PToast.showLong("未找到用户");
                                        }
                                        adapter.setData(array);
                                    }
                                    //搜索计数加1
                                    searchLabelView.makeSearchNum();
                                } catch (Exception e) {
                                    PToast.showLong("数据解析错误");
                                }
                            }
                        } else {
                            try {
                                org.json.JSONObject object = response.getResponseJson();
                                if ("4003".equals(object.getString("code"))) {
                                    PToast.showLong("请先充值vip");
                                } else {
                                    PToast.showLong("搜索失败");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                PToast.showLong("搜索失败");
                            }
                        }
                    }
                }));
    }
}
