package com.juxin.predestinate.ui.live.ui;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.live.bean.LiveConsumeList;
import com.juxin.predestinate.ui.live.bean.LiveUserDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * 直播间 消费列表 fragment
 * <p>
 * Created by chengxiaobo on 2017/10/24.
 */

public class LiveConsumeListFragment extends Fragment {

    private String mType = "";
    private String mRoomid = "";

    private ListView mLv; //listView
    private ImageView mIvHeadBg;//头像的背景
    private ImageView mIvHead;//头像
    private ImageView mIvJueWei;//爵位
    private ImageView mIvSex;//性别
    private TextView mTvZuanshi;//钻石
    private TextView mTvNikeName;//昵称
    private RelativeLayout mRlNoNetWork;//无网络连接
    private ImageView mIvRefresh;//重新加载
    private View mTop;//顶部

    private List<LiveConsumeList> mList = null;
    private LiveConsumeList mFirstPerson = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mType = getArguments().getString("type");
        mRoomid = getArguments().getString("roomid");

        PLogger.e("========onCreate=============");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.live_consume_list_fragment, container, false);

        initView(view);
        initViewState();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 初始View
     *
     * @param view
     */
    private void initView(View view) {

        mLv = (ListView) view.findViewById(R.id.lv_consume);

        mTop = view.findViewById(R.id.layout_consume_top);
        mIvHeadBg = (ImageView) mTop.findViewById(R.id.iv_head_bg);
        mIvHead = (ImageView) mTop.findViewById(R.id.iv_head);
        mIvJueWei = (ImageView) mTop.findViewById(R.id.iv_juewei);
        mIvSex = (ImageView) mTop.findViewById(R.id.iv_sex);
        mTvZuanshi = (TextView) mTop.findViewById(R.id.tv_consume);
        mTvNikeName = (TextView) mTop.findViewById(R.id.tv_nikename);
        mRlNoNetWork = (RelativeLayout) view.findViewById(R.id.rl_no_net);
        mIvRefresh = (ImageView) view.findViewById(R.id.iv_refresh);

    }

    /**
     * 初始化View的状态
     */
    private void initViewState() {

        mIvJueWei.setVisibility(View.GONE);
        mIvSex.setVisibility(View.GONE);
        mIvHead.setVisibility(View.GONE);
        mIvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getData();
            }
        });
    }

    /**
     * 下载完数据，设置View
     */
    private void setData() {

        mRlNoNetWork.setVisibility(View.GONE);
        mLv.setVisibility(View.VISIBLE);
        mTop.setVisibility(View.VISIBLE);

        if (mList == null) {

            mRlNoNetWork.setVisibility(View.VISIBLE);
            mLv.setVisibility(View.GONE);
            mTop.setVisibility(View.GONE);

        } else if (mList.size() == 0) {

            mIvJueWei.setVisibility(View.GONE);
            mIvSex.setVisibility(View.GONE);
            mIvHead.setVisibility(View.GONE);
            mTvNikeName.setText("虚位以待");
            mTvZuanshi.setText("0");
            mLv.setVisibility(View.GONE);
            mIvHeadBg.setBackgroundResource(R.drawable.live_consume_no_no1);
        } else {
            mFirstPerson = mList.get(0);
            mList.remove(0);
            //设置第一项内容
            setFirstPersonData();

            if (mList.size() == 0) {
                mLv.setVisibility(View.GONE);
            } else {
                mLv.setAdapter(new MyAdapter());
            }
        }
    }

    /**
     * 设置第一个人的信息
     */
    private void setFirstPersonData() {

        mIvHeadBg.setBackgroundResource(R.drawable.live_consume_no1);
        mIvJueWei.setVisibility(View.VISIBLE);
        mIvSex.setVisibility(View.VISIBLE);
        mIvHead.setVisibility(View.VISIBLE);
        mTvNikeName.setText(mFirstPerson.getNickname());
        mTvZuanshi.setText(mFirstPerson.getConsume() + "");
        //用户头像
        if (!"".equals(mFirstPerson.getAvatar())) {
            ImageLoader.loadCircleAvatar(App.activity, mFirstPerson.getAvatar(), mIvHead);
        }
        //性别
        if (mFirstPerson.getGender() == LiveUserDetail.SEX_MAN) {
            mIvSex.setImageResource(R.drawable.icon_boy);
        } else if (mFirstPerson.getGender() == LiveUserDetail.SEX_WOMAN) {
            mIvSex.setImageResource(R.drawable.icon_girl);
        }

        //爵位
        NobilityList.Nobility tmpNobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(mFirstPerson.getNobilitylevel(), mFirstPerson.getGender());
        String icon = tmpNobility.getTitle_icon();

        if (icon == null || "".equals(icon)) {
            mIvJueWei.setVisibility(View.GONE);
        } else {
            ImageLoader.loadFitCenter(App.activity, icon, mIvJueWei);
            mIvJueWei.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 去下载数据
     */
    private void getData() {

        /**
         {
         "type":1,
         "roomid":1000001
         }
         */

        LoadingDialog.show((FragmentActivity) App.activity);
        final HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("platform", "2");//android为2
        postParams.put("con_type", mType);//1- 今日榜，  2 - 七日榜， 3 - 总榜
        postParams.put("roomid", mRoomid);//房间号

        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.ContributionList, postParams, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                LoadingDialog.closeLoadingDialog();
                if (response.isOk()) {
                    //解析数据
                    parseData(response);
                    //设置数据
                    setData();
                    return;
                }
                setNoDataView(); //无网络的情况
                PToast.showShort(response.getMsg());
            }
        });
    }

    /**
     * 解析数据
     *
     * @param response
     */
    private void parseData(HttpResponse response) {
        try {

            JSONObject resJo = response.getResponseJson().getJSONObject("res");
            if (resJo != null) {
                JSONArray jsonArray = resJo.optJSONArray("list");
                if (jsonArray != null) {

                    mList = new ArrayList<LiveConsumeList>();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        LiveConsumeList bean = new LiveConsumeList();
                        bean.parseJson(jsonArray.getJSONObject(i));
                        mList.add(bean);
                    }
                    //排序
                    Collections.sort(mList, new Comparator<LiveConsumeList>() {

                        @Override
                        public int compare(LiveConsumeList o1, LiveConsumeList o2) {
                            if (o1.getConsume() > o2.getConsume()){
                                return -1;
                            }else if (o1.getConsume() == o2.getConsume()){
                                return 0;
                            }else{
                                return 1;
                            }
                        }
                    });
                }

            }

        } catch (JSONException e) {
            mList = null;
        }
    }

    /**
     * viewpager选中该 fragment以后，判断有没有数据，如果没有数据从网络下载，否则，显示已经下载数据
     */
    public void updateData() {

        PLogger.e("========updateData=============");

        if (mList == null) {
            //从网络下载数据
            getData();
        }
    }

    /**
     * 解析异常以及服务的返回的错误，则显示网络失败页面
     */
    private void setNoDataView() {

        mRlNoNetWork.setVisibility(View.VISIBLE);
        mLv.setVisibility(View.GONE);
        mTop.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            MyHolder myHolder = new MyHolder();

            if (convertView == null) {

                convertView = LayoutInflater.from(App.activity).inflate(R.layout.live_consume_list_item, parent, false);

                myHolder.ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
                myHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nikename);
                myHolder.tvNum = (TextView) convertView.findViewById(R.id.tv_num);
                myHolder.ivSex = (ImageView) convertView.findViewById(R.id.iv_sex);
                myHolder.ivJuewei = (ImageView) convertView.findViewById(R.id.iv_juewei);
                myHolder.tvZuanshi = (TextView) convertView.findViewById(R.id.tv_consume);

                convertView.setTag(myHolder);

            } else {

                myHolder = (MyHolder) convertView.getTag();
            }

            final LiveConsumeList bean = mList.get(position);

            //用户头像
            if (!"".equals(bean.getAvatar())) {
                ImageLoader.loadRoundAvatar(App.activity, bean.getAvatar(), myHolder.ivHead, UIUtil.dp2px(2.5f));
            }

            //昵称
            myHolder.tvNickName.setText(bean.getNickname());

            //爵位
            NobilityList.Nobility tmpNobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(bean.getNobilitylevel(), bean.getGender());
            String icon = tmpNobility.getTitle_icon();

            if (icon == null || "".equals(icon)) {
                myHolder.ivJuewei.setVisibility(View.GONE);
            } else {
                ImageLoader.loadFitCenter(App.activity, icon, myHolder.ivJuewei);
                myHolder.ivJuewei.setVisibility(View.VISIBLE);
            }
            //排名
            myHolder.tvNum.setText((position + 2) + "");

            //性别
            if (bean.getGender() == LiveUserDetail.SEX_MAN) {
                myHolder.ivSex.setImageResource(R.drawable.icon_boy);
            } else if (bean.getGender() == LiveUserDetail.SEX_WOMAN) {
                myHolder.ivSex.setImageResource(R.drawable.icon_girl);
            }

            //钻石
            myHolder.tvZuanshi.setText(bean.getConsume() + "");

            return convertView;
        }
    }

    class MyHolder {

        ImageView ivHead;
        TextView tvNickName;
        TextView tvNum;
        ImageView ivSex;
        ImageView ivJuewei;
        TextView tvZuanshi;

    }

}
